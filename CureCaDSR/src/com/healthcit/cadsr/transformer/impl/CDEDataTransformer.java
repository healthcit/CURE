/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cadsr.transformer.impl;

import gov.nih.nci.cadsr.domain.AdministeredComponent;
import gov.nih.nci.cadsr.domain.DataElement;
import gov.nih.nci.cadsr.domain.EnumeratedValueDomain;
import gov.nih.nci.cadsr.domain.PermissibleValue;
import gov.nih.nci.cadsr.domain.ReferenceDocument;
import gov.nih.nci.cadsr.domain.ValueDomain;
import gov.nih.nci.cadsr.domain.ValueDomainPermissibleValue;
import gov.nih.nci.cadsr.domain.ValueMeaning;
import gov.nih.nci.objectCart.domain.CartObject;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

import com.healthcit.cadsr.transformer.DataTransformer;
import com.healthcit.cadsr.utils.CADSRUtils;

/**
 * Instance of DataTransformer used to process CDE Data Elements from CADSR.
 * @author oawofolu
 *
 */
public class CDEDataTransformer extends DataTransformer {
	
	private static CDEDataTransformer dataTransformer;
	
	private CDEDataTransformer(){}
	
	public static CDEDataTransformer getInstance()
	{
		if ( dataTransformer == null ) dataTransformer = new CDEDataTransformer();
		return dataTransformer;
	}
		
	/**
	 * Transforms CADSR entities into the appropriate CURE FormBuilder entities
	 * @param sourceList
	 * @param searchType
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public List getProcessedEntities( List<? extends AdministeredComponent> sourceList, String searchType ){
		List transformedList = (List)CollectionUtils.collect( 
				sourceList, 
				new Transformer(){
					public Object transform( Object source ) {
						DataElement target = new DataElement();
						copyTransferrableProperties( target, ( DataElement ) source );
						return target;
					}});
		// Clear out the sourceList; this is done to deallocate memory resources as the sourceList could be very large
		CADSRUtils.clearList( sourceList );
		
		return transformedList;
	}
	
	/**
	 * Copies all non-proxied properties from the source to the target.
	 * @param target
	 * @param source
	 * @return
	 * 
	 */
	public Object copyTransferrableProperties(AdministeredComponent target, AdministeredComponent source) {
		try {
			super.copyTransferrableProperties(target, source);
			
			// Set the ValueDomain entity, ValueDomainPermissibleValues collections
			target = copyValueDomain( (DataElement)target, (( DataElement ) source).getValueDomain() );
			
			// Set the ReferenceDocument collection
			target = copyReferenceDocumentCollection( (DataElement)target, (( DataElement ) source).getReferenceDocumentCollection() );
			
			return target;
		} catch (Exception ex){
			log.error( "Error while copying properties: "+ ex.getMessage() );
		}
		return target;
	}
	
	/**
	 * Queries the caDSR repository for CartObjects
	 * @throws ApplicationException 
	 */
	@SuppressWarnings("rawtypes")
	public List searchCartObjects( String str ) throws ApplicationException
	{
		List results = null; 
		
		Collection<CartObject> searchCartObjectResults = getCartObjectCollectionByUserAndType(  str, CADSR_CDE_CART_TYPE );
		if ( CollectionUtils.isEmpty( searchCartObjectResults ) )
		{
			results = new ArrayList<DataElement>();
		}
		else
		{
			ArrayList<String> ids = new ArrayList<String>( searchCartObjectResults.size() );				
			for ( CartObject searchCartObjectResult : searchCartObjectResults)
			{
				ids.add( searchCartObjectResult.getNativeId() );
			}				
			StringBuffer hqlQuery = new StringBuffer("from DataElement where id in (").append(StringUtils.repeat("?,", ids.size()-1)).append("?)");
			results = appService.query(new HQLCriteria( hqlQuery.toString(), ids));
		}
		
		return results;
	}

	private DataElement copyValueDomain( DataElement target, ValueDomain sourceValueDomain  ) {
		ValueDomain targetValueDomain = sourceValueDomain instanceof EnumeratedValueDomain ? new EnumeratedValueDomain() : new ValueDomain();
		targetValueDomain.setLongName( sourceValueDomain.getLongName() );
		targetValueDomain.setDatatypeName( sourceValueDomain.getDatatypeName() );
		targetValueDomain.setLowValueNumber( sourceValueDomain.getLowValueNumber() );
		targetValueDomain.setHighValueNumber( sourceValueDomain.getHighValueNumber() );
		
		if ( sourceValueDomain instanceof EnumeratedValueDomain ) {
		(( EnumeratedValueDomain ) targetValueDomain).setValueDomainPermissibleValueCollection( new ArrayList<ValueDomainPermissibleValue>() );
			for ( ValueDomainPermissibleValue vdpv : (( EnumeratedValueDomain )sourceValueDomain ).getValueDomainPermissibleValueCollection()){
				ValueDomainPermissibleValue v = new ValueDomainPermissibleValue();
				PermissibleValue pv = new PermissibleValue();
				pv.setValue( vdpv.getPermissibleValue().getValue() );
				pv.setId( vdpv.getPermissibleValue().getId() );
				ValueMeaning vm = new ValueMeaning();
				vm.setPublicID( vdpv.getPermissibleValue().getValueMeaning().getPublicID() );
				pv.setValueMeaning( vm );
				v.setPermissibleValue( pv );
				((EnumeratedValueDomain)targetValueDomain).getValueDomainPermissibleValueCollection().add( v );
			}	
		}
		
		target.setValueDomain( targetValueDomain );
		return target;
	}
	
	private DataElement copyReferenceDocumentCollection( DataElement target, Collection<ReferenceDocument> referenceDocuments ) {
		target.setReferenceDocumentCollection( new ArrayList<ReferenceDocument>());
		for ( ReferenceDocument doc : referenceDocuments) {
			ReferenceDocument d = new ReferenceDocument();
			d.setDoctext( doc.getDoctext() );
			target.getReferenceDocumentCollection().add( d );
		}
		return target;
	}

}
