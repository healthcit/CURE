package com.healthcit.cure.dataloader.utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

import com.healthcit.cure.dataloader.controller.DataLoaderController;
import com.healthcit.cure.utils.Constants;

public class CureDataLoaderUtils {

	private static final Logger log = LoggerFactory.getLogger( DataLoaderController.class );
	
    public static void copyInputStream(InputStream is, OutputStream os) throws IOException
	{
	    BufferedOutputStream output = new BufferedOutputStream(os);
	    byte[] buffer = new byte[1024];
	    int len;

	    while((len = is.read(buffer)) >= 0)
	       	output.write(buffer, 0, len);

	    output.flush();
	    is.close();
	    output.close();
	}
	 
	public static void sendProcessingError(HttpServletResponse response, String errorMessage, Exception e)
	{
		log.error(e.getMessage(), e);
		JSONObject errorInfo = new JSONObject();		
		
		errorInfo.put( Constants.RESPONSE_STATUS, Constants.RESPONSE_STATUS_ERROR);
		errorInfo.put( Constants.RESPONSE_STATUS_DETAILS,  errorMessage);
		sendResults(response, Constants.CONTENT_TYPE_JSON,  errorInfo);
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

}
