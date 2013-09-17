package com.healthcit.cure.dataloader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthcit.cure.utils.Constants;

public class IOUtils 
{
	
	private static Logger log = LoggerFactory.getLogger( IOUtils.class );

	public static String readFileContent(File file) throws FileNotFoundException, IOException
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
	
	public static File createFile( File file, String fileContent ) throws FileNotFoundException 
	{
		PrintWriter writer = new PrintWriter( file.getAbsolutePath() );
		writer.println( fileContent );
		writer.close();
		return file;
	}
	
	public static File createDirectory( String parentDirectory, String fileOrDirectoryName )
	{
		File file = null;
		if ( new File( parentDirectory ).exists() ) {
			if ( !(file = new File( parentDirectory, fileOrDirectoryName )).mkdir() ) file = null;
			
		}
		return file;
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
	
	public static void sendOkResponse(HttpServletResponse response, String mimeType)
	{
		// mimeType = application/json
		if ( StringUtils.equals( mimeType, Constants.CONTENT_TYPE_JSON) )
		{
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.RESPONSE_STATUS_OK);
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}
		
		// ... other mime types ...
		
		else // mime type not recognized
		{
			sendProcessingError(response, 
								"Mime type " + StringUtils.defaultIfEmpty(mimeType, "null") + " not recognized",
								null);
		}
	}
	
	public static void sendOkResponseAsJSONP(String callback, HttpServletResponse response, String mimeType)
	{
		JSONObject statusInfo = new JSONObject();				
		statusInfo.put( Constants.RESPONSE_STATUS, Constants.RESPONSE_STATUS_OK);
		sendResults(response, Constants.CONTENT_TYPE_JSON,  callback + "(" + statusInfo + ")");
	}
	
	public static void sendProcessingError(HttpServletResponse response, String errorMessage, Exception e)
	{
		if ( e != null ) log.error(e.getMessage(), e);
		JSONObject errorInfo = new JSONObject();		
		
		errorInfo.put( Constants.RESPONSE_STATUS, Constants.RESPONSE_STATUS_ERROR);
		errorInfo.put( Constants.RESPONSE_STATUS_DETAILS,  errorMessage);
		IOUtils.sendResults(response, Constants.CONTENT_TYPE_JSON,  errorInfo);
	}
}
