/*******************************************************************************
 * Copyright (c) 2013 HealthCare It, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-Clause license
 * which accompanies this distribution, and is available at
 * http://directory.fsf.org/wiki/License:BSD_3Clause
 * 
 * Contributors:
 *     HealthCare It, Inc - initial API and implementation
 ******************************************************************************/
package com.healthcit.analytics.dao;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class HttpClientWrapper extends DefaultHttpClient {

	   private static HttpClientWrapper httpClient = null;

	   protected HttpClientWrapper() {}
	   
	   protected HttpClientWrapper( ClientConnectionManager manager, HttpParams params )
	   {
		   super( manager, params );
	   }

	   /**
	    * @return The unique instance of this class.
	    */
	   public static synchronized HttpClientWrapper getHttpClient() {
	      if(httpClient == null) {
	    	  HttpParams params = new BasicHttpParams();
	    	  HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
	    	  HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

	    	  SchemeRegistry schemeRegistry = new SchemeRegistry();
	    	  schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    	  schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

	    	  ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
	    	  
	    	  httpClient = new HttpClientWrapper( cm, params );
	      }
	      return httpClient;
	   }

	   public static synchronized void shutDown(){
		   if(httpClient != null) {
			     // When HttpClient instance is no longer needed,
			     // shut down the connection manager to ensure
			     // immediate deallocation of all system resources
			   	 httpClient.getConnectionManager().shutdown();
		   }
	   }
}
