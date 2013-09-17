/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cadsr.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.healthcit.cadsr.transformer.DataTransformer;
import com.thoughtworks.xstream.XStream;

public class CADSRUtils {
	private static XStream xmlConverter = new XStream();
	
	public static String toXML( Object object ) {
		return object == null ? null : xmlConverter.toXML( object );
	}
	
	public static Object fromXML( String xml ) {
		return xml == null ? null : xmlConverter.fromXML( xml );
	}
	

	private static String read (InputStream is) throws IOException
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

	public static String read(HttpServletRequest request) 
	{
		String content = null;
		
		try {
			InputStream ins = CADSRUtils.getInputStream( request );
			if ( ins != null ) content = read( ins );
		}catch( Exception e){ 
			DataTransformer.log.error( e.getMessage() );
		}
		
		return content;
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	public
	static void clearList( Collection list ) {
		if ( list != null ) {
			list.removeAll(list);
		}
			
	}

	public static InputStream getInputStream( HttpServletRequest request ) {
		InputStream ins = null;
		try { 
			ins = request.getInputStream();
		} catch( IOException ioe ){ DataTransformer.log.error( "Could not get inputstream from request" );}
		return ins;
	}

	public static String getXML( Object object ) {
		return toXML( object );
	}
}
