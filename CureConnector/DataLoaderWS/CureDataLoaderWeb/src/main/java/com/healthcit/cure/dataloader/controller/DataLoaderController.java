/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cure.dataloader.controller;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.healthcit.cure.dataloader.beans.UserCredentials;
import com.healthcit.cure.dataloader.businessdelegates.UserCredentialsManager;
import com.healthcit.cure.dataloader.utils.CureDataLoaderUtils;
import com.healthcit.cure.utils.Constants;

@Controller
@RequestMapping(value="/api")
public class DataLoaderController {

	private static final Logger log = LoggerFactory.getLogger( DataLoaderController.class );
	
	public static String FACILITY_CODE = "facilityCode";
	public static String NOTIFICATION_EMAIL = "notificationEmail";
	public static String DICOM_JSON_FILE_NAME = "data.dicom"; 	
	public static String DICOM = "DICOM";
	public static String BASH = "bash";
	
	@Value("${cure.dataFileDestination}")
	private String path;
	
	@Value("${cure.pentahoHome}")
	private String pentahoHome;
	
	@Value("${cure.workingDirectory}")
    private String workingDirectory;
	
	@Value("classpath:invoke_pentaho.sh")
    private Resource pentahoScript;
	
	@Value("classpath:dicompyler/dvh_dicom.py")
    private Resource dvhDicomScript;
	
	
	
	@Autowired
	UserCredentialsManager userCredentialsManager;
//	@Value("${cure.dataUnzipFileDestination}")
//	String unzipPath; 
	
	
	@RequestMapping(value="hello")
	public void getHello( HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		log.debug("HELLO");
		ServletOutputStream sos = response.getOutputStream();
		sos.print("hello");
		sos.close();
	}
	
	@RequestMapping(value="/UploadFile")
	public String loadData(@RequestParam("file") MultipartFile file,
			@RequestParam("account_id") String  accountId,
			@RequestParam("token") String token,
			HttpServletRequest request, HttpServletResponse response)
	{
		log.debug("in the CureDataLoader controller");
		try
		{
			
			UserCredentials credentials = new UserCredentials();
			credentials.setAccountId(accountId);
			credentials.setToken(token);
			if(!userCredentialsManager.isValidUser(credentials))
			{
				CureDataLoaderUtils.sendProcessingError( response, "invalid user credentials", new Exception("invalid credentials") );
			}
			if(file != null)
			{
				File dstDir = new File(path);
				File unzipDir = unzipFile(file, dstDir);
				//needs to create the tree recursively
//				if(!dstDir.exists())
//				{
//					boolean status = dstDir.mkdir();
//					if(status)
//						log.debug("created directory " + dstDir.getAbsolutePath());
//					else
//						log.error("could not create directory " + dstDir.getAbsolutePath());
//				}
				
//				if(!unzipDir.exists())
//				{
//					boolean status = unzipDir.mkdir();
//					if(status)
//						log.debug("created directory " + unzipDir.getAbsolutePath());
//					else
//						log.error("could not create directory " + unzipDir.getAbsolutePath());
//				}
				
//				String fileUniqueId =  UUID.randomUUID().toString();
//				File unzipDir = new File(dstDir, fileUniqueId);
//				boolean status = unzipDir.mkdir();
//				if(status)
//					log.debug("created directory " + unzipDir.getAbsolutePath());
//				else
//					log.error("could not create directory " + unzipDir.getAbsolutePath());
//				
//				String originalFileName = file.getOriginalFilename();
//				int index = originalFileName.lastIndexOf('.');
//				String fileExtension = originalFileName.substring(index);
//				String fileName = fileUniqueId + fileExtension;
//				if(path!= null)
//				{
//					
//					File dataFile = new File(path, fileName);
//					file.transferTo(dataFile);
//					
//					ZipFile zipFile = new ZipFile(dataFile);
//				    Enumeration<? extends ZipEntry>  entries = zipFile.entries();
//
//				    while(entries.hasMoreElements()) {
//				        ZipEntry entry = (ZipEntry)entries.nextElement();
//				        if(entry.isDirectory()) {
//					         log.debug("Extracting directory: " + entry.getName());
//					         File dir = new File(unzipDir, entry.getName());
//					         dir.mkdir();
//					          continue;
//					    }
//				        File destFile = new File (unzipDir, entry.getName());
//				        CureDataLoaderUtils.copyInputStream(zipFile.getInputStream(entry), new FileOutputStream(destFile));
//				      }
//				      zipFile.close();
				      invokeEtlShellScript( unzipDir.getAbsolutePath() );
				      
				      
				      //Process proc = Runtime.getRuntime().exec("/home/tomcat/invoke_pentaho.sh " + unzipDir);
				      //Start
				      /*
				      StreamGobbler errorGobbler = new 
				                StreamGobbler(proc.getErrorStream(), "ERROR");            
				            
				            // any output?
				            StreamGobbler outputGobbler = new 
				                StreamGobbler(proc.getInputStream(), "OUTPUT", System.out);
				                
				            // kick them off
				            errorGobbler.start();
				            outputGobbler.start();
				                                    
				            // any error???
				            int exitVal = proc.waitFor();
				            System.out.println("ExitValue: " + exitVal);
				            //fos.flush();
				            //fos.close();
				       //End 
				        */     
			       // }
				}
				else
				{
					
				}
				JSONObject statusInfo = new JSONObject();				
				statusInfo.put( Constants.RESPONSE_STATUS, Constants.RESPONSE_STATUS_OK);
				CureDataLoaderUtils.sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		} 
		catch ( Exception ex )
		{
			CureDataLoaderUtils.sendProcessingError( response, ex.getMessage(),ex );
		}

		return null;
	}
	
	
	@RequestMapping(value="/ProcessDicom")
	public void  processDicom(@RequestParam("file") MultipartFile file,
			@RequestParam("account_id") String  accountId,
			@RequestParam("token") String token,
			@RequestParam("facilityId") String facilityId,
			@RequestParam("notificationEmail") String notificationEmail,
			HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			UserCredentials credentials = new UserCredentials();
			credentials.setAccountId(accountId);
			credentials.setToken(token);
			if(!userCredentialsManager.isValidUser(credentials))
			{
				CureDataLoaderUtils.sendProcessingError( response, "invalid user credentials", new Exception("invalid credentials") );
			}
			if(file != null)
			{
				File dstDir = new File(workingDirectory);
				File unzipDir = unzipFile(file, dstDir);
				
//				//needs to create the tree recursively
//				if(!dstDir.exists())
//				{
//					boolean status = dstDir.mkdir();
//					if(status)
//						log.debug("created directory " + dstDir.getAbsolutePath());
//					else
//						log.error("could not create directory " + dstDir.getAbsolutePath());
//				}
//				
//				String fileUniqueId =  UUID.randomUUID().toString();
//				File unzipDir = new File(dstDir, fileUniqueId);
//				boolean status = unzipDir.mkdir();
//				if(status)
//					log.debug("created directory " + unzipDir.getAbsolutePath());
//				else
//					log.error("could not create directory " + unzipDir.getAbsolutePath());
//				
//				String originalFileName = file.getOriginalFilename();
//				int index = originalFileName.lastIndexOf('.');
//				String fileExtension = originalFileName.substring(index);
//				String fileName = fileUniqueId + fileExtension;
//				if(path!= null)
//				{
//					
//					File dataFile = new File(path, fileName);
//					file.transferTo(dataFile);
//					
//					ZipFile zipFile = new ZipFile(dataFile);
//				    Enumeration<? extends ZipEntry>  entries = zipFile.entries();
//	
//				    while(entries.hasMoreElements()) {
//				        ZipEntry entry = (ZipEntry)entries.nextElement();
//				        if(entry.isDirectory()) {
//					         log.debug("Extracting directory: " + entry.getName());
//					         File dir = new File(unzipDir, entry.getName());
//					         dir.mkdir();
//					          continue;
//					    }
//				        File destFile = new File (unzipDir, entry.getName());
//				        CureDataLoaderUtils.copyInputStream(zipFile.getInputStream(entry), new FileOutputStream(destFile));	
//				      }
//				    	
//				      zipFile.close();
				
				//Invoke Python script
//			    List<String> processArgs = createShellScriptArguments(
//			    		"python",
//					    dvhDicomScript.getFile().getAbsolutePath(),
//					    "-path",
//					    unzipDir.getAbsolutePath());
				List<String> processArgs = new ArrayList<String>(4);
			    processArgs.add("python");
			    processArgs.add(dvhDicomScript.getFile().getAbsolutePath());
			    processArgs.add("-path");
			    processArgs.add(unzipDir.getAbsolutePath());
				ProcessBuilder processBuilder = new ProcessBuilder(processArgs);
				Process process = processBuilder.start();
				StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");      
						      
				String jsonTxt = IOUtils.toString(process.getInputStream());      
				errorGobbler.start();
				//outputGobbler.start();
						                                    
				// any error???
				int exitVal = process.waitFor();
				System.out.println("ExitValue: " + exitVal);
						            //fos.flush();
						            //fos.close();
						       //End 
						      
//						  System.out.println("DVH object is: " + jsonTxt);
				JSONObject dvh = (JSONObject)JSONSerializer.toJSON(jsonTxt);
				dvh.put(DataLoaderController.FACILITY_CODE, facilityId);
				dvh.put(DataLoaderController.NOTIFICATION_EMAIL, notificationEmail);
				System.out.println("DVH object is: " + dvh);
				loadDicomData(dvh);
						  
//			          }
				      
				      
				      //Process proc = Runtime.getRuntime().exec("/home/tomcat/invoke_pentaho.sh " + unzipDir);
				      //Start
				      
				      
				      
//				            // any output?
//				      StreamGobbler outputGobbler = new 
//				            StreamGobbler(process.getInputStream(), "OUTPUT");
				                
				            // kick them off
				      
//			        }
				}
				else
				{
					
				}
				JSONObject statusInfo = new JSONObject();				
				statusInfo.put( Constants.RESPONSE_STATUS, Constants.RESPONSE_STATUS_OK);
				CureDataLoaderUtils.sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}catch(Exception e)
		{
			CureDataLoaderUtils.sendProcessingError( response, e.getMessage(),e );
		}
		
	}
	
	public void loadDicomData( JSONObject dicomData )
		    throws Exception
    {
		// Perform initial validations
		performInitialDicomDataValidations(dicomData);
		
		
        // Get the notification email from the JSON object;
	    // throw an exception if a notification does not exist
		String notificationEmail = dicomData.getString  ( NOTIFICATION_EMAIL );
        
       
       // Generate file with JSON content at a random location
       String jsonFileParentFolder = UUID.randomUUID().toString();
       File jsonFileDirectory      = com.healthcit.cure.dataloader.utils.IOUtils.createDirectory( this.workingDirectory, jsonFileParentFolder );
       com.healthcit.cure.dataloader.utils.IOUtils.createFile( new File(jsonFileDirectory, DICOM_JSON_FILE_NAME), dicomData.toString() );
             
       
       // Invoke the ETL shell script with appropriate arguments
       invokeEtlShellScript( jsonFileDirectory.getAbsolutePath(),
		    		      DICOM,
		    		      notificationEmail );
    }
	
	private void performInitialDicomDataValidations( JSONObject dicomData )
	throws Exception
	{
		StringBuilder errorMessage = new StringBuilder();
		
		// Notification email is required
		if ( ! dicomData.containsKey( NOTIFICATION_EMAIL ) || StringUtils.isBlank( dicomData.getString( NOTIFICATION_EMAIL )))
		{
			errorMessage.append( "ERROR: Notification email is required" );
		}
		
		if ( errorMessage.length() > 0 ) throw new Exception ( errorMessage.toString() );
	}

	
    private File unzipFile(MultipartFile file, File dstDir) throws IOException
    {
    	//needs to create the tree recursively
		if(!dstDir.exists())
		{
			boolean status = dstDir.mkdir();
			if(status)
				log.debug("created directory " + dstDir.getAbsolutePath());
			else
				log.error("could not create directory " + dstDir.getAbsolutePath());
		}
		
		String fileUniqueId =  UUID.randomUUID().toString();
		File unzipDir = new File(dstDir, fileUniqueId);
		boolean status = unzipDir.mkdir();
		if(status)
			log.debug("created directory " + unzipDir.getAbsolutePath());
		else
			log.error("could not create directory " + unzipDir.getAbsolutePath());
		
		String originalFileName = file.getOriginalFilename();
		int index = originalFileName.lastIndexOf('.');
		String fileExtension = originalFileName.substring(index);
		String fileName = fileUniqueId + fileExtension;

			File dataFile = new File(path, fileName);
			file.transferTo(dataFile);
			
			ZipFile zipFile = new ZipFile(dataFile);
		    Enumeration<? extends ZipEntry>  entries = zipFile.entries();

		    while(entries.hasMoreElements()) {
		        ZipEntry entry = (ZipEntry)entries.nextElement();
		        if(entry.isDirectory()) {
			         log.debug("Extracting directory: " + entry.getName());
			         File dir = new File(unzipDir, entry.getName());
			         dir.mkdir();
			          continue;
			    }
		        File destFile = new File (unzipDir, entry.getName());
		        CureDataLoaderUtils.copyInputStream(zipFile.getInputStream(entry), new FileOutputStream(destFile));	
		      }
		    	
		      zipFile.close();
		
		return unzipDir;
    }
       
    private void invokeEtlShellScript( String ... arguments )
    throws Exception
    {
    	List<String> processArgs = new ArrayList<String>();
    	processArgs.add(BASH);
    	processArgs.add(pentahoScript.getFile().getAbsolutePath());
    	processArgs.add(pentahoHome);
    	processArgs.addAll( Arrays.asList( arguments ) );
	    ProcessBuilder processBuilder = new ProcessBuilder(processArgs);
	    log.debug("Script to exec: " + processArgs.toString());
	    processBuilder.start();
    }
    
//		BufferedInputStream input = new BufferedInputStream(is);
//		BufferedOutputStream output = new BufferedOutputStream(os);
//		
//		input.
//
//		try {
//			byte[] result = new byte[size];
//	        int totalBytesRead = 0;
//	        while(totalBytesRead < result.length){
//	          int bytesRemaining = result.length - totalBytesRead;
//	          //input.read() returns -1, 0, or more :
//	          int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
//	          if (bytesRead > 0){
//	            totalBytesRead = totalBytesRead + bytesRead;
//	          }
//	        }
//	        /*
//	         the above style is a bit tricky: it places bytes into the 'result' array; 
//	         'result' is an output parameter;
//	         the while loop usually has a single iteration only.
//	        */
//	        log("Num bytes read: " + totalBytesRead);
//	      }
//	      finally {
//	        log("Closing input stream.");
//	        input.close();
//	        output.close();
//	      }
    	

//	private void sendResults(HttpServletResponse response, String mimeType, JSONObject result)
//	{
//		sendResults(response, mimeType, result);
//	}
	
}



class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    OutputStream os;
    
    StreamGobbler(InputStream is, String type)
    {
        this(is, type, null);
    }
    StreamGobbler(InputStream is, String type, OutputStream redirect)
    {
        this.is = is;
        this.type = type;
        this.os = redirect;
    }
    
    public void run()
    {
        try
        {
            PrintWriter pw = null;
            if (os != null)
                pw = new PrintWriter(os);
                
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
            {
                if (pw != null)
                    pw.println(line);
                System.out.println(type + ">" + line);    
            }
            if (pw != null)
                pw.flush();
        } catch (IOException ioe)
        {
            ioe.printStackTrace();  
        }
    }
}