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
package com.healthcit.analytics.businessdelegates;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.query.Query;
import com.healthcit.analytics.dao.CouchDBDaoUtils;
import com.healthcit.analytics.dto.DataTableMapper;
import com.healthcit.analytics.utils.CAHopeDataSourceUtils;
import com.healthcit.cacure.dao.CouchDBDao;


public class DataTableManager {
		
	private static Logger log = LoggerFactory.getLogger( DataTableManager.class );
	
	/**
	 * Returns a Google Visualization-based DataTable used by the DataSourceServlet 
	 * to feed the AJAX visualizations in the front-end.
	 * @param query - The query object received from the client side
	 * @param datasourceUrl - the URL of the datasource to be queried
	 * @param columnDataArray - An array of useful metadata information about the columns
	 * @param emitsDocuments - Whether or not this query will emit entire CouchDB documents
	 * @return The DataTable
	 */
	public static DataTable getDataTable( Query query, HttpServletRequest request, List<Map<String, Object>> columnDataArray, boolean emitsDocuments )
	throws DataSourceException
	{
		
		// Create a new DataTable object
		DataTable dataTable = new DataTable();
		CouchDBDao couchDao = CouchDBDaoUtils.getCouchDBDaoInstance();
		try
		{
			String viewName = CouchDBDaoUtils.getCouchViewName(request);
			String viewParams = CouchDBDaoUtils.constructParameterList(request);
			// Get data from CouchDB
			JSONObject couchDBData = ( JSONObject )couchDao.getDataForView(viewName, viewParams);
			
			// log.debug( "Data from CouchDB: " + couchDBData );			

			// Generate the column headers for the datatable				
			List<ColumnDescription> dataTableColumns = CAHopeDataSourceUtils.getRequiredColumns( query, couchDBData, columnDataArray );
			
			dataTable.addColumns( dataTableColumns );			
	
			// TODO: Populate the datatable with data
			// (This should be populated with CouchDB data)	
			List<TableRow> dataTableRows = DataTableMapper.convertCouchDBResultSetToList( couchDBData, columnDataArray, emitsDocuments );
			
			dataTable.addRows( dataTableRows );
			
			log.debug( "DataTable: " + dataTable.toString());
		}
		catch ( TypeMismatchException tex )
		{
			tex.printStackTrace();
			throw new DataSourceException(ReasonType.INTERNAL_ERROR, tex.toString() );
		}
		catch( URISyntaxException uex )
		{
			log.debug( uex.getMessage(), uex );
			throw new DataSourceException( ReasonType.INTERNAL_ERROR, uex.getMessage() );
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
			throw new DataSourceException(ReasonType.OTHER, ex.getMessage() );
		}
		
		//return the datatable
		return dataTable;
	}
}
