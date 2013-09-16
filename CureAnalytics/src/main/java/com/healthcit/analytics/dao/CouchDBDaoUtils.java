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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthcit.analytics.utils.PropertyUtils;
import com.healthcit.cacure.dao.CouchDBDao;


public class CouchDBDaoUtils {
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger( CouchDBDaoUtils.class );
	private static String COUCHDB_SERVER_HOST = PropertyUtils.getProperty( "couchDBServer" );
	private static String COUCHDB_SERVER_PORT = PropertyUtils.getProperty( "couchDBPort" );
	private static String COUCHDB_DBNAME = PropertyUtils.getProperty( "couchDBDatabaseName" );
	private static String COUCHDB_DESIGN_DOC = PropertyUtils.getProperty( "couchDBDesignDoc" );
	private static String COUCHDB_VIEW_NAME = PropertyUtils.getProperty( "couchDBViewName" );
	private static String COUCHDB_CACURE_DBNAME = PropertyUtils.getProperty( "couchDBCacureDatabaseName" );
	private static String COUCHDB_CACURE_DESIGNDOC = PropertyUtils.getProperty( "couchDBCacureDesignDoc" );
	private static final String VIEW_NAME = "viewName";
	private static final String[] STANDARD_COUCHDB_PARAM_NAMES = 
		new String[]{ "key", "startkey", "startkey_docid", "endkey", "endkey_docid",
		              "limit", "stale", "descending", "skip",
		               "group", "group_level", "reduce", "include_docs", "inclusive_end"};
//	private static String HTTP_PREFIX = "http";
	private static String UTF8 = "UTF8";
	private static CouchDBDao couchDao = null;
	
	
	public static CouchDBDao getCouchDBDaoInstance()
	{
		if ( couchDao == null )
		{
			couchDao = new CouchDBDao();
			couchDao.setSourceDbName(COUCHDB_DBNAME);
			couchDao.setDesignDoc(COUCHDB_DESIGN_DOC);
			couchDao.setHost(COUCHDB_SERVER_HOST);
			couchDao.setPort(Integer.parseInt(COUCHDB_SERVER_PORT));
			couchDao.setCacureSourceDbName(COUCHDB_CACURE_DBNAME);
			couchDao.setCacureDesignDoc(COUCHDB_CACURE_DESIGNDOC);
		}
		return couchDao;
	}
	
	public static String getCouchViewName(HttpServletRequest request)
	{
		String viewName = request.getParameter( VIEW_NAME );
		
		if ( viewName == null ) viewName = COUCHDB_VIEW_NAME;
		return viewName;
	}
	
	
	/**
	 * Constructs a URL-encoded String of request parameters from a set of parameter key/value pairs.
	 * @param paramKeys
	 * @param paramValues
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String constructParameterList( HttpServletRequest request )
	{
		Enumeration<String> requestParamNames = request.getParameterNames();
		
		List<String> names = new ArrayList<String>();
		
		List<String> values = new ArrayList<String>();
		
		while ( requestParamNames.hasMoreElements() )
		{
			String element = requestParamNames.nextElement();
			
			if ( ArrayUtils.contains( STANDARD_COUCHDB_PARAM_NAMES, element ) )
			{
				names.add( element );
				
				values.add( request.getParameter( element ));
			}
		}
		
		StringBuffer str = new StringBuffer();
		
		if ( names != null && names.size() == values.size() )
		{
			for ( int i = 0; i < names.size(); ++i )
			{
				if ( i > 0 ) str.append( "&" );
				
				String key = names.get( i );
				
				String value = values.get( i );
				
				str.append( key );
				
				str.append( "=" );
				
				try {
					str.append( URLEncoder.encode( value, UTF8 ) );
				} catch ( UnsupportedEncodingException e ) { }
			}
		}
		return str.toString();
	}
	
}
