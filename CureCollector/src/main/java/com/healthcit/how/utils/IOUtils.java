/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class IOUtils {

	/* Logger */
	private static final Logger log = Logger.getLogger(IOUtils.class );
	private static final String tempDirPrefix = "caCureTempDir";

	public static String read (InputStream is) throws IOException
	{
		String data = null;

		if (is != null)
		{
			// read into a buffer
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			data = sb.toString();
		}

		return data;
	}
	
	public static void writeToFile(File file, String content) throws IOException
	{
		OutputStream os = new FileOutputStream(file);
		os.write( content.getBytes() );
		os.close();
	}

	public static void unzipFile(String filename, String dest) {

	    Enumeration<? extends ZipEntry> entries;
	    ZipFile zipFile;

	    try {
	    	zipFile = new ZipFile(filename);
	    	entries = zipFile.entries();

	    	while(entries.hasMoreElements()) {

	    		ZipEntry entry = entries.nextElement();

	    		if(entry.isDirectory()) {
	    			// Assume directories are stored parents first then children.
	    			log.info("Extracting directory: " + entry.getName());
	    			// This is not robust, just for demonstration purposes.
	    			(new File(dest + File.separator + entry.getName())).mkdirs();

	    			continue;
	    		}

	    		log.info("Extracting file: " + entry.getName());
	    		copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(dest + File.separator + entry.getName())));
	    	}

	    	zipFile.close();

	    } catch (IOException ioe) {
	    	System.err.println("Unhandled exception:");
	    	ioe.printStackTrace();
	    }
	}

	private static void copyInputStream(InputStream in, OutputStream out)  throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}
		in.close();
		out.close();
	}

	public static void copyDirectory(File sourceDir, File destDir, String archiveFileName) throws IOException{
		log.info("Copying directory");

		File[] children = sourceDir.listFiles();
		log.info(destDir +" exists:"+destDir.exists());
		if(!destDir.exists()){
			destDir.mkdir();
			log.info("destination dir:"+destDir);
		}
		for(File sourceChild: children){
			String name = sourceChild.getName();
			log.info("Filename: " + name);
			File destChild = new File(destDir, name);
			if(sourceChild.isDirectory()){
				log.info("************** sourceChild Name: " + sourceChild.getName());
				log.info("************** destDir: " + destDir.getName());
				if(destDir.getName().indexOf(sourceChild.getName()) == -1) {
					copyDirectory(sourceChild, destChild, archiveFileName);
				}
			} else{
				log.info("************** archiveFileName: " + archiveFileName);
				if(archiveFileName.indexOf(sourceChild.getName()) == -1) {
					copyFile(sourceChild, destChild);
				}
			}
		}
	}

	private static void copyFile(File source, File dest) throws IOException{
		if(!dest.exists()){
			log.info("dest:"+dest);
			dest.createNewFile();
		}
		log.info("Writing file :"+source.getPath()+" to: "+dest.getPath());

		InputStream in = null;
		OutputStream out = null;
		try{
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int len;
			while((len=in.read(buf))>0){
				out.write(buf, 0, len);
			}
		} catch (Exception ex) {

		} finally {
			in.close();
			out.close();
		}
	}

	public static String readFileContent(File file)
	throws FileNotFoundException, IOException
	{
		BufferedReader buffer = new BufferedReader( new FileReader( file ));
		StringBuffer output = null;
		String s = null;
		while ( (s = buffer.readLine()) != null ) {
			if ( output == null ) output = new StringBuffer();
			output.append( s );
		}
		return output.toString();
	}

	public static File getTempDir(){

        final String baseTempPath = getTempDirBase();
		Random rand = new Random();

        File tempDir = null;

        while(true){
        	int randomInt = 1 + rand.nextInt();
        	tempDir = new File(baseTempPath + File.separator + tempDirPrefix + randomInt);

        	if (tempDir.exists() == false) {
	            tempDir.mkdir();
	            break;
	        }
        }
        return tempDir;
	}
	public static String getTempDirBase(){

        final String baseTempPath = System.getProperty("java.io.tmpdir");

        return baseTempPath;
	}

	public static String prettyPrintXML(String input) {
	    try {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}


	public static void write( FileWriter fw, String str ) {
		try {
			BufferedWriter out = new BufferedWriter( fw );
			out.write( str );
			out.close();
		} catch (Exception e) {
			log.debug("Could not write to file: ");
			e.printStackTrace();
		}
	}

	public static void cleanTempDir(File tempDir){
		tempDir.deleteOnExit();
	}

	public static Map<String, String> readMapping(InputStream is)throws IOException
	{
		Map<String, String>map = new HashMap<String, String>();
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(is));
		String oneLine;
		while((oneLine = fileReader.readLine()) != null)
		{
			if (oneLine.trim().length() == 0)
				continue; // skip empty line

			// split on =
			String[] keyVal = oneLine.split("=");
			map.put(keyVal[0], keyVal[1]);
		}
		fileReader.close();
		return map;

	}
	
	@Deprecated
	public static void sendResults(HttpServletResponse response, String mimeType, String responseSummary, Exception exception) throws IOException
	{
		PrintWriter out = null;

		try {
			if ( responseSummary == null ) responseSummary = "";
			out = response.getWriter();
			response.setContentType(mimeType);		
			if ( Constants.STATUS_FAIL.equals( responseSummary ) ) response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			if ( exception == null )
			{
				out.write(responseSummary);
			}
			else
			{
				response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
				JSONObject errorDetails = ExceptionUtils.getHttpResponseErrorDetails( responseSummary, exception );
				out.write( errorDetails.toString() );
			}
		} catch (IOException ex) {
			log.error(ex);
			throw ex;
		} finally {
			if(out != null) {
				out.close();
			}
		}
	}
	
	public static void sendResults(HttpServletResponse response, String mimeType, Object responseContent) 
	{
		PrintWriter out = null;

		try {
			out = response.getWriter();
			response.setContentType(mimeType);		
			out.write(responseContent.toString());
		} catch (IOException ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			if(out != null) {
				out.close();
			}
		}
	}
	
	public static void sendProcessingError(HttpServletResponse response, Exception e)
	{
		log.error(e.getMessage(), e);
		JSONObject errorInfo = new JSONObject();		
		
		errorInfo.put( Constants.RESPONSE_STATUS, Constants.RESPONSE_STATUS_ERROR);
		errorInfo.put( Constants.RESPONSE_STATUS_DETAILS,  "There has been an error processing the request");
		IOUtils.sendResults(response, Constants.CONTENT_TYPE_JSON,  errorInfo);
	}
	
	public static String readClassPathResource( String path ) throws IOException
	{
		InputStream resourceAsStream = IOUtils.class.getResourceAsStream(path);
		if(resourceAsStream == null) {
			return null;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
		StringBuffer sb = new StringBuffer();
		try {
			String line;
			while((line = br.readLine()) != null) {
				if(StringUtils.isNotBlank(line) && !line.trim().startsWith("--")) {
					sb.append(line);
					sb.append("\n");
				}
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}
}
