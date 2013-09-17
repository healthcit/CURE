#!/usr/bin/env python
# -*- coding: ISO-8859-1 -*-
# dvhcalc.py
"""Calculate dose volume histogram (DVH) from DICOM RT Structure / Dose data."""
# Copyright (c) 2011 Aditya Panchal
# Copyright (c) 2010 Roy Keyes
# This file is part of dicompyler, released under a BSD license.
#    See the file license.txt included with this distribution, also
#    available at http://code.google.com/p/dicompyler/

#from dicompyler import dicomparser

import logging
logger = logging.getLogger('dicompyler.dvhlog')
import numpy as np
import os
import numpy.ma as ma
import matplotlib.nxutils as nx
import dvhdoses
from dvhdata import DVH as dvd
import dvhdata as dvh_data
import simplejson
import dicomparser
#import nror
def get_dvh(structure, dose, limit=None, callback=None):
    """Get a calculated cumulative DVH along with the associated parameters."""

    # Get the differential DVH
    hist = calculate_dvh(structure, dose, limit, callback)
    # Convert the differential DVH into a cumulative DVH
    dvh = get_cdvh(hist)

    dvhdata = {}
    dvhdata['data'] = dvh
    dvhdata['bins'] = len(dvh)
    dvhdata['type'] = 'CUMULATIVE'
    dvhdata['doseunits'] = 'GY'
    dvhdata['volumeunits'] = 'CM3'
    dvhdata['scaling'] = 1
    # save the min dose as -1 so we can calculate it later
    dvhdata['min'] = -1
    # save the max dose as -1 so we can calculate it later
    dvhdata['max'] = -1
    # save the mean dose as -1 so we can calculate it later
    dvhdata['mean'] = -1
    return dvhdata

def calculate_dvh(structure, dose, limit=None, callback=None):
    """Calculate the differential DVH for the given structure and dose grid."""

    sPlanes = structure['planes']
    #print 'Calculating DVH of', structure['id'], structure['name']

    # Get the dose to pixel LUT
    doselut = dose.GetPatientToPixelLUT()

    # Generate a 2d mesh grid to create a polygon mask in dose coordinates
    # Code taken from Stack Overflow Answer from Joe Kington:
    # http://stackoverflow.com/questions/3654289/scipy-create-2d-polygon-mask/3655582
    # Create vertex coordinates for each grid cell
    x, y = np.meshgrid(np.array(doselut[0]), np.array(doselut[1]))
    x, y = x.flatten(), y.flatten()
    dosegridpoints = np.vstack((x,y)).T

    # Get the dose and image data information
    dd = dose.GetDoseData()
    id = dose.GetImageData()

    # Create an empty array of bins to store the histogram in cGy
    # only if the structure has contour data
    if len(sPlanes):
        maxdose = int(dd['dosemax'] * dd['dosegridscaling'] * 100)
        # Remove values above the limit (cGy) if specified
        if not (limit == None):
            if (limit < maxdose):
                maxdose = limit
        hist = np.zeros(maxdose)
    else:
        hist = np.array([0])
    volume = 0

    plane = 0
    # Iterate over each plane in the structure
    for z, sPlane in sPlanes.iteritems():

        # Get the contours with calculated areas and the largest contour index
        contours, largestIndex = calculate_contour_areas(sPlane)

        # Get the dose plane for the current structure plane
        doseplane = dose.GetDoseGrid(z)

        # If there is no dose for the current plane, go to the next plane
        if not len(doseplane):
            break

        # Calculate the histogram for each contour
        for i, contour in enumerate(contours):
            m = get_contour_mask(doselut, dosegridpoints, contour['data'])
            h, vol = calculate_contour_dvh(m, doseplane, maxdose,
                                           dd, id, structure)
            # If this is the largest contour, just add to the total histogram
            if (i == largestIndex):
                hist += h
                volume += vol
            # Otherwise, determine whether to add or subtract histogram
            # depending if the contour is within the largest contour or not
            else:
                contour['inside'] = False
                for point in contour['data']:
                    if nx.pnpoly(point[0], point[1],
                                 np.array(contours[largestIndex]['data'])):
                        contour['inside'] = True
                        # Assume if one point is inside, all will be inside
                        break
                # If the contour is inside, subtract it from the total histogram
                if contour['inside']:
                    hist -= h
                    volume -= vol
                # Otherwise it is outside, so add it to the total histogram
                else:
                    hist += h
                    volume += vol
        plane += 1
        if not (callback == None):
            callback(plane, len(sPlanes))
    # Volume units are given in cm^3
    volume = volume/1000
    # Rescale the histogram to reflect the total volume
    hist = hist*volume/sum(hist)
    # Remove the bins above the max dose for the structure
    hist = np.trim_zeros(hist, trim='b')

    return hist

def calculate_contour_areas(plane):
    """Calculate the area of each contour for the given plane.
       Additionally calculate and return the largest contour index."""

    # Calculate the area for each contour in the current plane
    contours = []
    largest = 0
    largestIndex = 0
    for c, contour in enumerate(plane):
        # Create arrays for the x,y coordinate pair for the triangulation
        x = []
        y = []
        for point in contour['contourData']:
            x.append(point[0])
            y.append(point[1])

        cArea = 0
        # Calculate the area based on the Surveyor's formula
        for i in range(0, len(x)-1):
            cArea = cArea + x[i]*y[i+1] - x[i+1]*y[i]
        cArea = abs(cArea / 2)
        # Remove the z coordinate from the xyz point tuple
        data = map(lambda x: x[0:2], contour['contourData'])
        # Add the contour area and points to the list of contours
        contours.append({'area':cArea, 'data':data})

        # Determine which contour is the largest
        if (cArea > largest):
            largest = cArea
            largestIndex = c

    return contours, largestIndex

def get_contour_mask(doselut, dosegridpoints, contour):
    """Get the mask for the contour with respect to the dose plane."""

    grid = nx.points_inside_poly(dosegridpoints, contour)
    grid = grid.reshape((len(doselut[1]), len(doselut[0])))

    return grid

def calculate_contour_dvh(mask, doseplane, maxdose, dd, id, structure):
    """Calculate the differential DVH for the given contour and dose plane."""

    # Multiply the structure mask by the dose plane to get the dose mask
    mask = ma.array(doseplane * dd['dosegridscaling'] * 100, mask=~mask)
    # Calculate the differential dvh
    hist, edges = np.histogram(mask.compressed(),
                               bins=maxdose,
                               range=(0,maxdose))

    # Calculate the volume for the contour for the given dose plane
    vol = sum(hist) * ((id['pixelspacing'][0]) *
                      (id['pixelspacing'][1]) *
                      (structure['thickness']))
    return hist, vol

def get_cdvh(ddvh):
    """Calculate the cumulative DVH from a differential DVH array."""

    # cDVH(x) is Sum (Integral) of dDVH with x as lower limit
    cdvh = []
    j = 0
    jmax = len(ddvh)
    while j < jmax:
        cdvh += [np.sum(ddvh[j:])]
        j += 1
    cdvh = np.array(cdvh)
    return cdvh

############################# Test DVH Calculation #############################
def CalculateDoseStatistics(dvh, rxdose):
    """Calculate the dose statistics for the given DVH and rx dose."""

    sfdict = {'min':dvhdoses.get_dvh_min,
              'mean':dvhdoses.get_dvh_mean,
              'max':dvhdoses.get_dvh_max}

    for stat, func in sfdict.iteritems():
        # Only calculate stat if the stat was not calculated previously (-1)
        if dvh[stat] == -1:
            dvh[stat] = 100*func(dvh['data']*dvh['scaling'])/rxdose

    return dvh


def FindAndParseFiles(path):
        """Thread to start the directory search."""

        # we are assuming that we will only get data for a single patient
        #patients = {}
        patient = {}
        # Check if the path is valid
        if os.path.isdir(path):

            files = []
            for root, dirs, filenames in os.walk(path):
                files += map(lambda f:os.path.join(root, f), filenames)
                #if (self.import_search_subfolders == False):
                #    break
            for n in range(len(files)):

                if (os.path.isfile(files[n])):
                    try:
                        logger.debug("Reading: %s", files[n])
                        dp = dicomparser.DicomParser(filename=files[n])
                    except (AttributeError, EOFError, IOError, KeyError):
                        pass
                        logger.info("%s is not a valid DICOM file.", files[n])
                    else:
                        #patient = dp.GetDemographics()
                        #h = hashlib.sha1(patient['id']).hexdigest()
                        #if not patients.has_key(h):
                        #    patients[h] = {}
                        #    patients[h]['demographics'] = patient
                        #    if not patients[h].has_key('studies'):
                        #        patients[h]['studies'] = {}
                        #        patients[h]['series'] = {}
                        
                        ## Create each Study but don't create one for RT Dose
                        ## since some vendors use incorrect StudyInstanceUIDs
                        #if not (dp.GetSOPClassUID() == 'rtdose'):
                        #    stinfo = dp.GetStudyInfo()
                        #    if not patients[h]['studies'].has_key(stinfo['id']):
                        #        patients[h]['studies'][stinfo['id']] = stinfo
                        ## Create each Series of images
                        #if (('ImageOrientationPatient' in dp.ds) and \
                        #    not (dp.GetSOPClassUID() == 'rtdose')):
                        #    seinfo = dp.GetSeriesInfo()
                        #    seinfo['numimages'] = 0
                        #    seinfo['modality'] = dp.ds.SOPClassUID.name
                        #    if not patients[h]['series'].has_key(seinfo['id']):
                        #        patients[h]['series'][seinfo['id']] = seinfo
                        #    if not patients[h].has_key('images'):
                        #        patients[h]['images'] = {}
                        #    image = {}
                        #    image['id'] = dp.GetSOPInstanceUID()
                        #    image['filename'] = files[n]
                        #    image['series'] = seinfo['id']
                        #    image['referenceframe'] = dp.GetFrameofReferenceUID()
                        #    patients[h]['series'][seinfo['id']]['numimages'] = \
                        #        patients[h]['series'][seinfo['id']]['numimages'] + 1 
                        #    patients[h]['images'][image['id']] = image
                        ## Create each RT Structure Set
                        if dp.ds.Modality in ['RTSTRUCT']:
                            #if not patients[h].has_key('structures'):
                            #    patients[h]['structures'] = {}
                            #structure = dp.GetStructureInfo()
                            #structure['id'] = dp.GetSOPInstanceUID()
                            #structure['filename'] = files[n]
                            #structure['series'] = dp.GetReferencedSeries()
                            #structure['referenceframe'] = dp.GetFrameofReferenceUID()
                            patient['structures']=dp.GetStructures()
                        # Create each RT Plan
                        elif dp.ds.Modality in ['RTPLAN']:
                            #if not patients[h].has_key('plans'):
                            #    patients[h]['plans'] = {}
                            #plan = dp.GetPlan()
                            #plan['id'] = dp.GetSOPInstanceUID()
                            #plan['filename'] = files[n]
                            #plan['series'] = dp.ds.SeriesInstanceUID
                            #plan['referenceframe'] = dp.GetFrameofReferenceUID()
                            #plan['rtss'] = dp.GetReferencedStructureSet()
                            #patients[h]['plans'][plan['id']] = plan
                            patient['plan'] = dp.GetPlan()
                            patient['patientDemographics'] = dp.GetDemographics()
                        # Create each RT Dose
                        elif dp.ds.Modality in ['RTDOSE']:
                            #if not patients[h].has_key('doses'):
                            #    patients[h]['doses'] = {}
                            #dose = {}
                            #dose['id'] = dp.GetSOPInstanceUID()
                            #dose['filename'] = files[n]
                            #dose['referenceframe'] = dp.GetFrameofReferenceUID()
                            #dose['hasdvh'] = dp.HasDVHs()
                            #dose['rtss'] = dp.GetReferencedStructureSet()
                            #dose['rtplan'] = dp.GetReferencedRTPlan()
                            #patients[h]['doses'][dose['id']] = dose
                            patient['dvhs'] = dp.GetDVHs()
                            patient['dose'] = dp
                        # Otherwise it is a currently unsupported file
                        else:
                            logger.info("%s is a %s file and is not " + \
                                "currently supported.",
                                files[n], dp.ds.SOPClassUID.name)

        # if the path is not valid, display an error message
        else:
            print "ERROR in DICOM processing"
            #wx.CallAfter(progressFunc, 0, 0, 'Select a valid location.')
            #dlg = wx.MessageDialog(
            #    parent,
            #    "The DICOM import location does not exist. Please select a valid location.",
            #    "Invalid DICOM Import Location", wx.OK|wx.ICON_ERROR)
            #dlg.ShowModal()
        return patient 

def main():

    import string
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('-path', help='path to the files location')
    #parser.add_argument('-rtplan', help='plan file')
    #parser.add_argument('-rtdose', help='dose file')
    #parser.add_argument('-rtstruct', help='structure file')
    args = parser.parse_args()
    command_args = vars(args)

    path = command_args['path']
    #plan_file = command_args['rtplan']
    #dose_file = command_args['rtdose']
    #structure_file = command_args['rtstruct']

    rtdata = FindAndParseFiles(path)
    has_pylab = False
    try:
        import pylab as pl
    except ImportError:
        has_pylab = False

    data={}
    data['shortNames'] = {}
    # Read the example RT structure and RT dose files
    # The testdata was downloaded from the dicompyler website as testdata.zip
    #rtss = dicomparser.DicomParser(filename=structure_file)
    #rtdose = dicomparser.DicomParser(filename=dose_file)
    #rtplan = dicomparser.DicomParser(filename=plan_file)
    ##rtss = dicomparser.DicomParser(filename="/home/lkagan/dicom-files/nror_sample/RTSTRUCT20688.1.dcm")
    ##rtdose = dicomparser.DicomParser(filename="/home/lkagan/dicom-files/nror_sample/RTDOSE20688.4.dcm")
    ##rtplan = dicomparser.DicomParser(filename="/home/lkagan/dicom-files/nror_sample/RTPLAN20688.2.dcm")
    ##rtss = dicomparser.DicomParser(filename="../../dicom-files/testdata/rtss.dcm")
    ##rtdose = dicomparser.DicomParser(filename="../../dicom-files/testdata/rtdose.dcm")
    ##rtplan = dicomparser.DicomParser(filename="../../dicom-files/testdata/rtplan.dcm")
    #print '\nStructures and Dose parsed...'

    # Obtain the structures and DVHs from the DICOM data
    #structures = rtss.GetStructures()
    #dvhs = rtdose.GetDVHs()
    #plan = rtplan.GetPlan()
    #patientDemographics = rtplan.GetDemographics()
    structures = rtdata['structures']
    plan=rtdata['plan']
    dvhs= rtdata['dvhs']
    rtdose = rtdata['dose']
    patientDemographics = rtdata['patientDemographics'] 
    #structures = {}
    #plan= {}
    #dvhs={} 
    
    #print plan
    #print '\nRetrieved ' + str(len(structures)) + ' structures'

    # Generate the calculated DVHs
    calcdvhs = {}
    for key, structure in structures.iteritems():
        calcdvhs[key] = get_dvh(structure, rtdose)
        #dvd = DVH(calcdvhs[key])
        #if key in dvhs:
        #print "volume %:" + str(dvd(calcdvhs[key]).GetVolumeConstraint(1000))
        #volume = dvh_data.CalculateVolume(structure)
        volume = calcdvhs[key]['data'][0]
        dose70 = plan['rxdose'] * 0.70
        dose40 = plan['rxdose'] * 0.40
        if (structure['name'] == 'ctv'):
            data['patientId'] = str(patientDemographics['id'])
            data['shortNames']['ctv'] = str(volume)
            CalculateDoseStatistics(calcdvhs[key], plan['rxdose']) 
            data['shortNames']['CTVMDinose'] = str(calcdvhs[key]['min'])
            data['shortNames']['CTVMeanDose'] = str(calcdvhs[key]['mean'])
            data['shortNames']['CTVMaxDose'] = str(calcdvhs[key]['max'])
            #data['ctv_95_dose'] = dvd(calcdvhs[key]).GetDoseConstraint(volume * 0.95)
            #data['ctv_90_dose'] = dvd(calcdvhs[key]).GetDoseConstraint(volume * 0.9)
            data['shortNames']['CTVD95'] = str(dvd(calcdvhs[key]).GetDoseConstraint(95))
            data['shortNames']['CTVD90'] = str(dvd(calcdvhs[key]).GetDoseConstraint(90))
            #print data
        elif (structure['name'] == 'bladder'):
             data['shortNames']['BladderV70'] =  str(dvd(calcdvhs[key]).GetVolumeConstraintCC(dose70, volume))
             data['shortNames']['BladderV70cc'] = str(dvd(calcdvhs[key]).GetVolumeConstraintCC(dose70, volume))
             data['shortNames']['BladderV40cc'] = str(dvd(calcdvhs[key]).GetVolumeConstraintCC(dose40, volume))
             data['shortNames']['BladderV40'] =  str(dvd(calcdvhs[key]).GetVolumeConstraintCC(dose40, volume))
        elif (structure['name'] == 'rectum'):
             data['shortNames']['RectumV70cc'] = str(dvd(calcdvhs[key]).GetVolumeConstraintCC(dose70, volume))
             data['shortNames']['RectumV70'] = str(dvd(calcdvhs[key]).GetVolumeConstraintCC(dose70, volume))
             data['shortNames']['RectumV40cc'] = str(dvd(calcdvhs[key]).GetVolumeConstraintCC(dose40, volume))
             data['shortNames']['RectumV40'] =  str(dvd(calcdvhs[key]).GetVolumeConstraintCC(dose40, volume))
        #print "Volume: " + str(volume)
        #if (volume>0.0):
            #print "volume cc:" + str(dvd(calcdvhs[key]).GetVolumeConstraintCC(1000, volume))
            #print calcdvhs[key]
            #print calcdvhs[key]['data']
    print simplejson.dumps(data)
    #print '\nDVH calculated ...'
    # Compare the calculated and original DVH volume for each structure
    #print '\nStructure Name\t\t' + 'Original Volume\t\t' + \
    #      'Calculated Volume\t' + 'Percent Difference'
    #print '--------------\t\t' + '---------------\t\t' + \
    #      '-----------------\t' + '------------------'
    for key, structure in structures.iteritems():
        if (key in calcdvhs) and (len(calcdvhs[key]['data'])):
            #if key in dvhs:
            #    ovol = dvhs[key]['data'][0]
            cvol = calcdvhs[key]['data'][0]
            #print string.ljust(structure['name'], 18) + '\t' + \
            #      string.ljust(str(''), 18) + '\t' + \
            #      string.ljust(str(cvol), 18) + '\t'
                  #"%.3f" % float((100)*(cvol-ovol)/(ovol))

    # Plot the DVHs if pylab is available
    if has_pylab:
        for key, structure in structures.iteritems():
            if (key in calcdvhs) and (len(calcdvhs[key]['data'])):
                #if key in dvhs:
                pl.plot(calcdvhs[key]['data']*100/calcdvhs[key]['data'][0],
                        color=np.array(structure['color'], dtype=float)/255,
                        label=structure['name'], linestyle='dashed')
                #pl.plot(dvhs[key]['data']*100/dvhs[key]['data'][0],
                #        color=np.array(structure['color'], dtype=float)/255,
                #        label='Original '+structure['name'])
        pl.legend(loc=7, borderaxespad=-5)
        pl.setp(pl.gca().get_legend().get_texts(), fontsize='x-small')
        pl.show()

if __name__ == '__main__':
    main()
