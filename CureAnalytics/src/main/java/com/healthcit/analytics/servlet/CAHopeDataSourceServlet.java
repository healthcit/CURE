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
package com.healthcit.analytics.servlet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.visualization.datasource.Capabilities;
import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;
import com.healthcit.analytics.businessdelegates.DataTableManager;
import com.healthcit.analytics.utils.CAHopeDataSourceUtils;
import com.healthcit.analytics.utils.Constants;

/**
 * This servlet is a Google Visualization API-compatible datasource
 * which integrates with CouchDB and, hence, 
 * may be used to feed CouchDB data to Google Visualizations
 * in the caHope Analytics application.
 * 
 * (For more information on the Google Visualization Java API, see: 
 *  http://code.google.com/apis/visualization/documentation/dev/dsl_get_started.html)
 *  
 *  To access CouchDB data from the caHope Analytics application,
 *  an HTTP GET request is issued for this servlet 
 *  with request parameters
 *  that are compatible with the Google Visualizations API. 
 *  
 *  For example, the following URI
 *  returns a DataTable with the age, ethnicity, cancertype, drinking status and smoking status
 *  of the first 100 patients in the database:
 *  
 *  /<servlet_context>?tq=select patientid,age,ethnicity,cancertype,drinkstatus,smokestatus&start=1&end=100
 *  
 *  In addition, there are custom request parameters that have been added:
 *  -viewName: the CouchDB view used as the datasource (optional; defaults to GetDocByOwnerAndForm)
 *  -document: whether or not the CouchDB resultset consists of CouchDB documents (or customized objects emitted by the view)
 *  -orderedColumnNames: a list of columnNames. Required when a SELECT query is provided.
 *  (This might seem redundant, as the column names are already specified in the query
 *  either via the standard "tq" parameter or by setting the query in the javascript.
 *  However, it appears that the order of columns in the request is not preserved in the query object;
 *  they appear to be stored in a collection that does not preserve order.
 *  Adding this parameter helps to resolve this problem.)
 *  -the STANDARD CouchDB request parameters, such as key, include_docs, group, group_level, startkey, endkey, etc...
 *  
 *  For example, the following Javascript code will return a DataTable with columns formname,question and answer
 *  and CouchDB key=["760b0db5-39e8-4921-bec2-6e98bc6af587","d5fff3c4-dd06-34c1-9bec-0ca8b55d291e"]:
 *  
 *  var query = new google.visualization.Query('http://localhost:8080/<application context>/caHopeDS?key=["760b0db5-39e8-4921-bec2-6e98bc6af587","d5fff3c4-dd06-34c1-9bec-0ca8b55d291e"]&document=true&orderedColumnNames=formname,question,answer');
 *	query.setQuery('select formName,question,answer');					  
 *  query.send(handleQueryResponse);	
 * 
 * @author Oawofolu
 *
 */
public class CAHopeDataSourceServlet extends DataSourceServlet {

	private static final Logger log = LoggerFactory.getLogger( CAHopeDataSourceServlet.class );
	private static final long serialVersionUID = -9185119323890025699L;
	/**
	 * Generates the dataTable.
	 */
	@Override
	public DataTable generateDataTable(Query query, HttpServletRequest request)
	throws DataSourceException
	{
		log.debug( "In CAHopeDataSourceServlet" );
		
		// validate the query
		CAHopeDataSourceUtils.validateQuery( query, request );
		
		// determine whether or not the resultSet from CouchDB consists of documents
		// (if not, then the resultSet consists of a custom set of objects emitted by the CouchDB view)
		boolean emitsDocuments = StringUtils.equalsIgnoreCase( request.getParameter(Constants.DOCUMENT ), "true" );
		
		// get a mapping of columns to data types (and other metadata, as needed)
		List<Map<String, Object>> columnDataArray = CAHopeDataSourceUtils.getColumnData( request );
		
		// return the datatable
		DataTable table = DataTableManager.getDataTable(query, request, columnDataArray, emitsDocuments);
		
		return table;
	}

	/**
	 * Specifies the capabilities supported by this datasource.
	 */
	@Override
	public Capabilities getCapabilities() {
		return Capabilities.SELECT;
	}

	/**
	 * When isRestrictedAccessMode returns true, access to this webservice
	 * is limited to requests from the same domain.
	 * (This is a security measure which prevents cross domain scripting attacks.)
	 * However, we set this to false for testing purposes.
	 */
	@Override
	protected boolean isRestrictedAccessMode() {
		return false;
	}

}
