/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;


public class IOUtils {

  private static Logger log = Logger.getLogger(IOUtils.class);

  private static SSLSocketFactory socketFactory;


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

	private static InputStream getInputStream( HttpServletRequest request ) {
		InputStream ins = null;
		try {
			ins = request.getInputStream();
		} catch( IOException ioe ){ log.error( "Could not get inputstream from request" );}
		return ins;
	}

	public static String read(HttpServletRequest request)
	{
		String content = null;

		try {
			InputStream ins = getInputStream( request );
			if ( ins != null ) content = read( ins );
		}catch( Exception e){
			log.debug( e.getMessage() );
		}

		return content;
	}

	/**
   * @param pageName
   * @return
   */
	public static String getFullURL(String pageName) {
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest rq = wctx.getHttpServletRequest();
		StringBuilder url = new StringBuilder();
		url.append(rq.getScheme()).append("://").append(rq.getServerName()).append(":").append(rq.getServerPort())
			.append(rq.getContextPath()).append(pageName);
		return url.toString();
	}

	/**
	 * @param str String
	 * @return queryString with "+" instead of white spaces
	 */
	public static String convertStringToStringQuery(String str) {
		String regex = "[\\s]+";
		String[] spl = str.trim().split(regex);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < spl.length; i++) {
			sb.append(spl[i]);
			if (i < spl.length - 1) {
				sb.append("+");
			}
		}
		return sb.toString();
	}

}
