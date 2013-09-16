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
package com.healthcit.analytics.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.TableCell;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.Value;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.healthcit.analytics.utils.CAHopeDataSourceUtils;
import com.healthcit.analytics.utils.DateUtils;

public class DataTableMapper {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger( DataTableMapper.class );
	private static final String ROWS = "rows";
	private static final String VALUE = "value";
	private static final String KEY = "key";
	private static final String QUESTIONS="questions";
	private static final String QUESTIONTEXT = "questionText";
	private static final String QUESTIONID = "questionId";
	private static final String ANSWERVALUES = "answerValues";
	private static final String ANSVALUEVAL = "ansValue";
	private static final String ANSVALUETEXT = "ansText";
	private static final String FORMNAME = "formName";
	private static final String FORMID = "formId";
	private static final String OWNERID = "ownerId";
	
	private static final String FORMNAMECOLUMN = "formName";
	private static final String FORMIDCOLUMN = "formId";
	private static final String OWNERIDCOLUMN = "ownerId";
	private static final String QUESTIONTEXTCOLUMN = "questionText";
	private static final String QUESTIONIDCOLUMN = "questionIdColumn";
	private static final String ANSWERCOLUMN = "answer";
	
	private static final String TEXT = "TEXT";
	private static final String NUMBER = "NUMBER";
	private static final String DATE = "DATE";
	
	/**
	 * Returns a JSONArray that represents a collection of rows from a CouchDB resultset of documents
	 */
	public static JSONArray getRows( JSONObject resultSet )
	{
		JSONArray rows = ( JSONArray ) CAHopeDataSourceUtils.getJSONValue( resultSet, ROWS );
		
		if ( rows == null ) rows = new JSONArray();
				
		return rows;
	}
	
	/**
	 * Returns a JSONObject that represents the actual document in a CouchDB resultset row
	 * (i.e. the value of the "value" key)
	 */
	private static JSONObject getDocument( JSONObject row )
	{
		JSONObject document = ( JSONObject )getValue( row );
		
		return document;
	}
	
	/**
	 * Returns a JSONObject that represents the value of the "value" key in a CouchDB resultset row
	 * @param row
	 * @return
	 */
	public static Object getValue(JSONObject row)
	{
		Object value = CAHopeDataSourceUtils.getJSONValue( (JSONObject)row, VALUE );
		
		return value;
	}
	
	/**
	 * Returns a JSONObject that represents the value of the "key" key in a CouchDB resultset row
	 */
	public static Object getKey(JSONObject row)
	{
		Object key = CAHopeDataSourceUtils.getJSONValue( (JSONObject)row, KEY );
		
		return key;
	}
	
	/**
	 * Returns a JSONObject that represents the set of questions in a CouchDB caCure document
	 * (i.e. the value of the "questions" key)
	 */
	private static JSONObject getQuestions( JSONObject document )
	{
		JSONObject questions = ( JSONObject ) CAHopeDataSourceUtils.getJSONValue( document, QUESTIONS );
		
		return questions;
	}
	
	/**
	 * Returns a String that represents the formId of a CouchDB caCure document
	 * (i.e. the value of the "formId" key)
	 */
	private static String getFormId( JSONObject document )
	{
		String formId = ( String ) CAHopeDataSourceUtils.getJSONValue( document, FORMID );
		
		return formId;
	}
	
	/**
	 * Returns a String that represents the form name of a CouchDB caCure document
	 * (i.e. the value of the "formName" key)
	 */
	private static String getFormName( JSONObject document )
	{
		String formName = ( String ) CAHopeDataSourceUtils.getJSONValue( document, FORMNAME );
		
		return formName;
	}
	
	/**
	 * Returns a String that represents the owner ID of a CouchDB caCure document
	 * (i.e. the value of the "ownerId" key)
	 */
	private static String getOwnerId( JSONObject document )
	{
		String ownerId = ( String ) CAHopeDataSourceUtils.getJSONValue( document, OWNERID );
		
		return ownerId;
	}
	
	/**
	 * Returns a String that represents the question text of a CouchDB caCure question
	 * (i.e. the value of the "questionText" key)
	 */
	private static String getQuestionText( JSONObject question )
	{
		String questionText = ( String )CAHopeDataSourceUtils.getJSONValue( question, QUESTIONTEXT );
		
		return questionText;
	}
	
	/**
	 * Returns a String that represents the question ID of a CouchDB caCure question
	 * (i.e. the value of the "questionId" key)
	 */
	private static String getQuestionId( JSONObject question )
	{
		String questionId = ( String )CAHopeDataSourceUtils.getJSONValue( question, QUESTIONID );
		
		return questionId;
	}
	
	/**
	 * Returns a JSONArray that represents the answervalues that are associated with a CouchDB caCure question
	 * (i.e. the value of the "answerValues" key)
	 */
	private static JSONArray getAnswerValues( JSONObject question )
	{
		JSONArray answerValues = ( JSONArray ) CAHopeDataSourceUtils.getJSONValue( (JSONObject)question, ANSWERVALUES );
		
		return answerValues;
	}
	
	/**
	 * Returns a String that represents the textual value of a given answer value.
	 * If the value of the "ansText" key is not blank, then it returns the "ansText";
	 * else it returns the value of the "ansVal" key.
	 */
	private static String getAnswerValueText( JSONObject answerValue )
	{
		String text = StringUtils.defaultIfEmpty( 
				( String )CAHopeDataSourceUtils.getJSONValue((JSONObject)answerValue, ANSVALUETEXT), 
				( String )CAHopeDataSourceUtils.getJSONValue((JSONObject)answerValue, ANSVALUEVAL));
		
		return text;
	}
	
	/**
	 * Takes a CouchDB query resultset and converts it into
	 * an collection of TableRows which will represent
	 * the rows of a DataTable.
	 */
	public static List<TableRow> convertCouchDBResultSetToList( JSONObject resultSet, List<Map<String, Object>> columnDataArray, boolean emitsDocuments ) {
		
		// get the full list of query columns
		String[] queryColumns = CAHopeDataSourceUtils.getFullColumnNameList( columnDataArray );
		
		// get the full list of query column types
		ValueType[] queryColumnTypes = CAHopeDataSourceUtils.getFullColumnDataTypeList( columnDataArray );
		
		if ( ! emitsDocuments ) // if the CouchDB resultSet does not consist of CouchDB documents
		{
			return generateNonDocumentBasedResultSet(resultSet, queryColumns, queryColumnTypes);
		}
		
		else
		{
			return generateDocumentBasedResultSet(resultSet, queryColumns);
		}		
	}
	
	private static List<TableRow> generateNonDocumentBasedResultSet( JSONObject resultSet, String[] queryColumns, ValueType[] queryColumnTypes )
	{
		List<TableRow> dataTableRows = new ArrayList<TableRow>();
						
		// Get array of rows
		JSONArray rows = getRows( resultSet );
		
		// Get the number of columns in the query
		int numQueryColumns = queryColumns.length;
		
		for ( Object row : rows )
		{
			// Get the emitted key
			Object key = getKey( (JSONObject)row );
			
			// Get the emitted value
			Object value = getValue( (JSONObject)row );
			
			TableRow tableRow = new TableRow();
			
			// Make the key the first cell(/s) of the table row (if required, as indicated by the query columns)
			addCellContent( tableRow, key, numQueryColumns, queryColumnTypes );
			
			// Next add the value(s) to the table row (if required, as indicated by the query columns)
			addCellContent( tableRow, value, numQueryColumns, queryColumnTypes );			
			
			// Then add the tableRow to the collection of rows
			dataTableRows.add( tableRow );
		}
		
		return dataTableRows;
	}
	
	private static List<TableRow> generateDocumentBasedResultSet( JSONObject resultSet, String[] queryColumns )
	{
		List<TableRow> dataTableRows = new ArrayList<TableRow>();
		
		// Get array of rows
		JSONArray rows = getRows( resultSet );
		
		for ( Object row : rows )
		{
			// Get the actual document
			JSONObject document = getDocument( (JSONObject)row );
			
			// Get the questions associated with the document
			JSONObject questions = getQuestions( document );
			
			// Get the form name associated with the document
			String formName = getFormName( document );
			
			// Get the form ID associated with the document
			String formId = getFormId( document );
			
			// Get the owner ID associated with the document
			String ownerId = getOwnerId( document );
			
			for ( Object question : questions.values() )
			{
				
				// Get the questionText associated with each question
				String questionText = getQuestionText( (JSONObject)question );
				
				// Get the questionId associated with each question
				String questionId = getQuestionId( (JSONObject) question );
				
				// Get the answer values associated with each question
				JSONArray answerValues = getAnswerValues( (JSONObject) question );
				 
				for ( Object ans : answerValues )
				{
					String answer = getAnswerValueText( (JSONObject)ans );
					
					// Create a new TableRow with Form Name, Form ID, Owner ID, Question Text and Answer as its cells,
					// (depending on which columns were queried),
					// and add the new TableRow to the dataTableRows collection
					TableRow tableRow = new TableRow();
					
					if ( ArrayUtils.contains( queryColumns, ANSWERCOLUMN ) ) tableRow.addCell( answer );
					
					if ( ArrayUtils.contains( queryColumns, QUESTIONTEXTCOLUMN ) ) tableRow.addCell( questionText );
					
					if ( ArrayUtils.contains( queryColumns, QUESTIONIDCOLUMN ) ) tableRow.addCell( questionId );
					
					if ( ArrayUtils.contains( queryColumns, FORMNAMECOLUMN ) ) tableRow.addCell( formName );
					
					if ( ArrayUtils.contains( queryColumns, FORMIDCOLUMN ) ) tableRow.addCell( formId );
					
					if ( ArrayUtils.contains( queryColumns, OWNERIDCOLUMN ) ) tableRow.addCell( ownerId );
					
					dataTableRows.add( tableRow );					
				}			
			}
		}
		return dataTableRows;
	}
	
	private static boolean hasCompleteNumberOfColumns( TableRow row, int numQueryColumns )
	{
		List<TableCell> cells = row.getCells();
		return ( (cells == null ? 0 : cells.size()) == numQueryColumns );
	}
	
	public static int getTotalNumberOfColumns( Object cellContent ) 
	{
		if ( CAHopeDataSourceUtils.isArrayOrCollection( cellContent ) ) 
		{			
			return ( ( JSONArray ) cellContent ).size(); 
		}
		
		else 
		{
			return 1;
		}
	}
		
	private static void addCellContent( TableRow tableRow, Object cellContent, int numQueryColumns, ValueType[] queryColumnTypes )
	{
		if ( CAHopeDataSourceUtils.isArrayOrCollection( cellContent ) )
		{
			JSONArray array = ( JSONArray )cellContent;
			
			for ( int i=0; i<array.size(); ++i )
			{	
				if ( !hasCompleteNumberOfColumns(tableRow, numQueryColumns) )
				{
					Value cellValue = getCellValue( tableRow, array.get( i ), queryColumnTypes );
								
					tableRow.addCell( cellValue );
				}
			}
		}
		
		else
		{
			if ( !hasCompleteNumberOfColumns(tableRow, numQueryColumns) )
			{
				Value cellValue = getCellValue( tableRow, cellContent, queryColumnTypes );
				
				tableRow.addCell( cellValue );
			}
		}
	}
	
	/**
	 * Casts cell content to its appropriate data type, then adds it to the TableRow object.
	 * @param tableRow - The TableRow object to add to
	 * @param content - The cell content to add
	 * @param dataTypes - Ordered list of data types
	 * @return
	 */
	private static Value getCellValue( TableRow tableRow, Object content, ValueType[] dataTypes )
	{
		Value cellValue = null;
		
		int currentColumnIndex = ( tableRow.getCells() == null ? 0 : tableRow.getCells().size() );

		ValueType cellDataType = dataTypes[ currentColumnIndex ];
		
		// return a nullsafe cellValue (seems that without this, NullPointerExceptions were being generated)
		if ( content == null )
		{
			if ( cellDataType.name().equals( TEXT ) ) content = "";
			else if ( cellDataType.name().equals( NUMBER ) ) content = 0;
			else if ( cellDataType.name().equals( DATE ) ) content = "";
		}
		
		if ( dataTypes != null && dataTypes.length > currentColumnIndex )
		{	
			// convert the cell content to Strings
			content = content.toString();
			
			// convert the cell content from Strings to Numbers when applicable
			if ( cellDataType.name().equals( NUMBER ) ) content = NumberUtils.toDouble( ( String )content.toString() );
			
			// convert the cell content from Strings to Dates when applicable
			if ( cellDataType.name().equals( DATE ) ) 
			{					
				if ( StringUtils.isNotEmpty( ( String )content ) ) 
				{
					content = DateUtils.getGregorianCalendar( ( String )content );
				}				
			}
			
		}	

		// generate the cell value
		try { cellValue = cellDataType.createValue( content ); }
		
		catch( TypeMismatchException ex ){ }
		
		return cellValue;
	}
}
