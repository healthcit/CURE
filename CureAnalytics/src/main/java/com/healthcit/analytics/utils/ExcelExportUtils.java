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

import java.io.OutputStream;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.converter.json.excel.Json2Excel;

public class ExcelExportUtils {
	
	private static final String COLUMNS_JSON_KEY = "cols";
	private static final String LABEL_JSON_KEY = "label";
	private static final String ROWS_JSON_KEY = "rows";
	private static final String ROWCELL_JSON_KEY = "c";
	private static final String ROWCELLVALUE_JSON_KEY = "v";
	
	
	@SuppressWarnings("unchecked")
	public static void streamVisualizationDataAsExcelFormat( OutputStream out, JSONObject data ) 
	{
		// Get the array of columns
		JSONArray jsonColumnArray = ( JSONArray ) data.get( COLUMNS_JSON_KEY );
		
		String[] excelColumnArray = new String[ jsonColumnArray.size() ];
		
		for ( int index = 0; index < excelColumnArray.length; ++index )
		{
			excelColumnArray[ index ] = ( String )(( JSONObject )jsonColumnArray.get( index )).get( LABEL_JSON_KEY );
		}
		
		// Get the array of rows
		JSONArray jsonRowArray = ( JSONArray ) data.get( ROWS_JSON_KEY ); 
		
		JSONArray excelRowArray = new JSONArray();
		
		Iterator<JSONObject> jsonRowIterator = jsonRowArray.iterator();
		
		while ( jsonRowIterator.hasNext() )
		{
			JSONArray rowCell = ( JSONArray ) jsonRowIterator.next().get( ROWCELL_JSON_KEY );
			
			JSONObject excelRowObj = new JSONObject();
			
			for ( int index = 0; index < rowCell.size(); ++index )
			{
				
				excelRowObj.put( excelColumnArray[ index ], ((JSONObject) rowCell.get( index )).get( ROWCELLVALUE_JSON_KEY ));
			}

			excelRowArray.add( excelRowObj );
		}
		
		// build the Excel outputstream
		Json2Excel.build( out, excelRowArray.toJSONString(), excelColumnArray );
	}
	
	public static JSONObject getParamAsJsonObject( String jsonParam )
	{
		System.out.println( jsonParam );
				
		return getAsJsonObject( jsonParam );
	}
	
	public static JSONObject getAsJsonObject( String jsonString ) 
	{
		JSONObject obj = null;
		
		try
		{
			obj = ( JSONObject ) JSONValue.parseWithException( jsonString );
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		
		return obj;
	}

	public static JSONArray getAsJsonArray( String jsonString ) 
	{
		JSONArray arr = null;
		
		try
		{
			arr = ( JSONArray ) JSONValue.parseWithException( jsonString );
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		
		return arr;
	}
}
