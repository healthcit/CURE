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
package com.healthcit.analytics.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;


import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.AbstractColumn;
import com.google.visualization.datasource.query.Query;
import com.google.visualization.datasource.query.QuerySelection;
import com.google.visualization.datasource.query.SimpleColumn;
import com.healthcit.analytics.dto.DataTableMapper;
import com.healthcit.analytics.exceptions.QueryInvalidException;

public class CAHopeDataSourceUtils {
	
	private static final String DEFAULT_COLUMN_PREFIX = "Column";
	private static final String SPLITTER = ",";
	private static final String TYPE = "type";
	private static final String NAME = "name";
	
	/**
	   * Returns a list of required columns based on the query and the actual
	   * columns.
	   *
	   * @param query The user selection query.
	   * @param availableColumns The list of possible columns.
	   *
	   * @return A List of required columns for the requested data table.
	   */
	  public static List<ColumnDescription> getRequiredColumns(Query query, JSONObject resultSet, List<Map<String, Object>> columnDataArray ) {
		
		ColumnDescription[] columns = generateColumnDescriptions( getColumns( query, resultSet, columnDataArray ), columnDataArray );
		
		return Arrays.asList( columns );
		
	  }
	  
	  /**
	   * Generates a list of AbstractColumn columns associated with this query
	   */
	  private static List<AbstractColumn> getColumns( Query query, JSONObject resultSet, List<Map<String, Object>> columnDataArray ) {

		  QuerySelection querySelection = new QuerySelection();
		  
		  List<String> columnIds = new LinkedList<String>();
		  
		  // If no SELECT statement was associated with this query,
		  // then generate a QuerySelection consisting of columns
		  // Column1, Column2, Column3 etc.
		  // based on the total possible number of columns 
		  // associated with the query
		  if ( query.getSelection() == null ) 
		  {
			  // get the number of columns associated with the resultSet
			  int numColumns = getNumberOfColumns( resultSet );
			  
			  // based on the number of columns, create a collection of column ids
			  for ( int index=1; index<=numColumns; ++index ) {
				  
				  String columnId = DEFAULT_COLUMN_PREFIX + index;
				  
				  columnIds.add( columnId );
			  }
		  }
		  // Otherwise, the columns must come from a SELECT statement.
		  // Since the order of the columns received on the server is not guaranteed
		  // to match the original order of the columns (specified on the browser side);
		  // we reset the QuerySelection with the correct ordering,
		  // which has been ensured in the columnDataArray object.
		  else
		  {
			  columnIds = Arrays.asList( ( String[] )getFullColumnNameList(columnDataArray) );			  
		  }
		  
		  // generate a new set of columns, and 
		  // update the QuerySelection with the new set of columns
		  for ( String columnId : columnIds )
		  {
			  SimpleColumn column = new SimpleColumn( StringUtils.trim(columnId) );
			  
			  querySelection.addColumn( column ); 
		  }
		  
		  query.setSelection( querySelection );
		  
		  // return the columns associated with the query
		  return query.getSelection().getColumns();
	  }
	  
	  /**
	   * Validates a Query
	   */
	  public static void validateQuery( Query query, HttpServletRequest request ) 
	  throws QueryInvalidException
	  {
		  // if the user specified a SELECT statement,
		  // then the request parameter "orderedColumnNames" is required
		  if ( query.getSelection() != null && StringUtils.isBlank( request.getParameter( Constants.ORDERED_COLUMN_NAMES )) ) {
			  throw new QueryInvalidException("ERROR: The request parameter \"orderedColumnNames\" is required when you specify a SELECT in the query.");
		  }
		  
	  }
	  	  
	  /**
	   * Transforms a list of AbstractColumn objects into ColumnDescription entities
	   */
	  private static ColumnDescription[] generateColumnDescriptions( List<AbstractColumn> columns, List<Map<String, Object>> columnDataArray ) {
		  
		  List<ColumnDescription> list = new LinkedList<ColumnDescription>();
		  
		  // get the full list of column data types
		  ValueType[] dataTypes = getFullColumnDataTypeList(columnDataArray);
		  		  		  
		  for ( int i = 0; i < columns.size(); ++i ) 
		  {
			AbstractColumn column = columns.get( i );
					
			// get the column's data type
			//TODO: The datatypes for each column should be dynamically determined.
			//( Currently, the datatypes have been hardcoded as "text" fields,
			// and in some cases they have been hardcoded as "numeric" fields 
			// on the client side.)
			ValueType columnType = dataTypes[ i ];		
			  			  
			ColumnDescription columnDescription = 
				new ColumnDescription( column.getId(), columnType, StringUtils.capitalize( column.getId() ) );
			
			list.add( columnDescription );
		  }
		  
		  return list.toArray( new ColumnDescription[ list.size() ] );
	  }
		
	  public static Object getJSONValue( JSONObject json, Object key )
	  {
		if ( json.containsKey( key ))
			return json.get( key );
		else
			return null;
	  }

	public static boolean isArrayOrCollection( Object object )
	{
		if ( object == null ) return false;
		
		return ( object.getClass().isArray() || object instanceof Collection );
	}
	
	
	/**
	 * Returns a JSONArray that contains some metadata about the columns.
	 * Currrently includes information about the column name and data type.
	 */
	public static List<Map<String, Object>> getColumnData( HttpServletRequest request )
	{		
		// if there is no "orderedColumnNames" request parameter then return null
		if ( StringUtils.isBlank( Constants.ORDERED_COLUMN_NAMES ) ) return null;
		
		// else, set up a JSONArray containing metadata about the columns
//		JSONArray columnDataArray = new JSONArray();
		ArrayList<Map<String, Object>> columnDataArray = new ArrayList<Map<String, Object>>();
				
		// get the full list of columns in the request
		String[] allColumns = request.getParameter( Constants.ORDERED_COLUMN_NAMES ).split( SPLITTER );
				
		// get the list of numeric columns in the request
		String[] numericColumns = StringUtils.split( request.getParameter( Constants.NUMERIC_DATA_TYPE ));
		
		// get the list of boolean columns in the request
		String[] booleanColumns = StringUtils.split( request.getParameter( Constants.BOOLEAN_DATA_TYPE )) ;
		
		// get the list of date columns in the request
		String[] dateColumns = StringUtils.split( request.getParameter( Constants.DATE_DATA_TYPE ));
		
		// associate column data types with each column
		for ( String column : allColumns )
		{
			// get the column data type
			ValueType columnDataType = getColumnDataType( column, numericColumns, booleanColumns, dateColumns );
			
			// create a JSONObject representing metadata for this column
//			JSONObject columnMetaData = new JSONObject();
//			
//			columnMetaData.put( NAME, column );
//			
//			columnMetaData.put( TYPE, columnDataType );
//			
//			columnDataArray.add( columnMetaData );
			Map<String, Object> columnMetaData = new HashMap<String, Object>();
			columnMetaData.put(NAME, column);
			columnMetaData.put(TYPE, columnDataType);
			columnDataArray.add(columnMetaData);
		}
		
		// return the JSON object
		return columnDataArray;
	}
	
	/**
	 * Returns an ordered, full list of names from the column metadata generated by a user's query.
	 * @param columnDataArray
	 * @return
	 */
	public static String[] getFullColumnNameList( List<Map<String, Object>> columnDataArray )
	{
		Object[] array = extractFromJSONColumnData( columnDataArray, NAME );
		
		String[] names = Arrays.copyOf( array, array.length, String[].class );
		
		return names;
	}
	
	/**
	 *  Returns an ordered, full list of data types from the column metadata generated by a user's query.
	 * @param columnDataArray
	 * @return
	 */
	public static ValueType[] getFullColumnDataTypeList( List<Map<String, Object>> columnDataArray )
	{
		Object[] array = extractFromJSONColumnData( columnDataArray, TYPE );
		
		ValueType[] types = Arrays.copyOf( array, array.length, ValueType[].class );
		
		return types;
	}
	
	/**
	 * Returns a filtered list of values from the given JSON column metadata
	 * which match the given key.
	 * @param columnDataArray
	 * @param key
	 * @return
	 */
	private static Object[] extractFromJSONColumnData( List<Map<String, Object>> columnDataArray, String key )
	{
		if ( columnDataArray == null ) return null;
		
		Object[] extractedData = new Object[ columnDataArray.size() ];
		
		for ( int i = 0; i < columnDataArray.size(); ++i  )
		{
			Map<String, Object> map = columnDataArray.get( i );
			
			extractedData[ i ] = map.get( key );
		}
		
		return extractedData;
	}
	
	private static int getNumberOfColumns( JSONObject resultSet ) {
		
		int numColumns = 0;
		
		// get the resultSet rows
		JSONArray rows = DataTableMapper.getRows( resultSet );
		
		if ( rows != null && !rows.isEmpty() ) {
			
			// get the first row
			JSONObject row = ( JSONObject )rows.get( 0 );
			
			// get the first row's key
			Object key = DataTableMapper.getKey( row );
			
			// get the first row's value
			Object value = DataTableMapper.getValue( row );
			
			// Add the number of columns associated with the key
			numColumns += DataTableMapper.getTotalNumberOfColumns( key );
			
			// Add the number of columns associated with the value
			numColumns += DataTableMapper.getTotalNumberOfColumns( value );
		}	
		
		return numColumns;
	}
	
	private static ValueType getColumnDataType ( String column, String[] numericColumns, String[] booleanColumns, String[] dateColumns )
	{
		// the column data type
		ValueType columnDataType = null; 
		
		// if this column was included in the request parameters as a numeric then declare it as such
		if ( ArrayUtils.contains( numericColumns, column ) ) columnDataType = ValueType.NUMBER;
		
		// else, if this column was included in the request parameters as a boolean then declare it as such
		else if ( ArrayUtils.contains( booleanColumns, column ) ) columnDataType = ValueType.BOOLEAN;
		
		// else, if this column was included in the request parameters as a date then declare it as such
		else if ( ArrayUtils.contains( dateColumns, column ) ) columnDataType = ValueType.DATE;
		
		// else, declare it as a TEXT field (the default)
		else columnDataType = ValueType.TEXT;
		
		return columnDataType;
	}

}
