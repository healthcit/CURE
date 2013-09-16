/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.data.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public class CouchJSONConverter
{
	public enum OutputFormat {XML, JSON}
	protected OutputFormat outputFormat = OutputFormat.JSON;
	protected OutputStream os;
	protected InputStream is;
	protected static Map<String, String> elementNameMap = new HashMap<String, String>();
	static{
		elementNameMap.put("questions", "question");
		elementNameMap.put("simple_tables", "simple_table");
		elementNameMap.put("complex_tables", "complex_table");
		
	}
	public CouchJSONConverter()
	{
		
	}
	public CouchJSONConverter(OutputFormat of)
	{
		outputFormat = of;
	}
	
	public void setOutputStream(OutputStream os)
	{
		this.os = os;
	}
	
	public void setInputStream(InputStream is)
	{
		this.is = is;
	}
	public void convert() throws Exception
	{
		if(OutputFormat.JSON.equals(outputFormat))
		{
			writeJSON();
		}
		else if (OutputFormat.XML.equals(outputFormat))
		{
			writeXML();
		}
	}
	
	protected void writeJSON()throws JsonParseException, IOException
	{
		JsonFactory jsonF = new JsonFactory();
		JsonParser jp = null;
		JsonGenerator jg = null;
		try
		{
			jp = jsonF.createJsonParser(is);
			
			jg = jsonF.createJsonGenerator(os);
			jg.useDefaultPrettyPrinter(); // enable indentation just to make debug/testing easier
			
			if (jp.nextToken() != JsonToken.START_OBJECT) {
			    throw new IOException("Expected data to start with an Object");
			}
			while ((jp.nextToken() != null))
			{
				String fieldName = jp.getCurrentName();
				   // Let's move to value
				jp.nextToken();//value or start_object or start_array
				
				if ("rows".equals(fieldName)) {
					jg.writeStartObject();
					jp.nextToken();//start object, start array was taken care by prev nextToken() call
					String currentEntityId = null;
					while(jp.nextToken() != JsonToken.END_OBJECT) //reads the key
					{
						String keyName = jp.getCurrentName();
						jp.nextToken();//value or start_object or start_array
						if("key".equals(keyName))
						{
							String entityId = jp.getText();
							if(currentEntityId != null && !currentEntityId.equals(entityId))
							{
								currentEntityId = entityId;
								jg.writeEndArray();//Close array for prev entity
								jg.writeFieldName(entityId);
								jg.writeStartArray();//start array for this entity
							}
							else if(currentEntityId == null)
							{
								currentEntityId = entityId;
								jg.writeFieldName(entityId);
								jg.writeStartArray();
							}
						}
						else if("value".equals(keyName))
						{
							Map<String, Object> row = new LinkedHashMap<String, Object>(); 
							readJsonObject(jp, row);
							JSONObject json = JSONObject.fromObject(row);
							jg.writeRaw(json.toString()+ "\r\n");
							jp.nextToken(); //move it off the END_OBJECT
							jp.nextToken(); //move it off the END_OBJECT
						}
					}
					//Close array for the last entityId
					jg.writeEndArray();
					jg.writeEndObject();
				}
				
	
			}
		}
		finally
		{
			jp.close();
			jg.close();
		}
	}
	protected void writeXML() throws XMLStreamException, JsonParseException, IOException
	{
		XMLOutputFactory factory      = XMLOutputFactory.newInstance();
		JsonFactory jsonF = new JsonFactory();
		XMLStreamWriter writer = null;
		JsonParser jp = null;
		try {
			jp = jsonF.createJsonParser(is);
			writer =  factory.createXMLStreamWriter(os);
			
			
			if (jp.nextToken() != JsonToken.START_OBJECT) {
			    throw new IOException("Expected data to start with an Object");
			}
			writer.writeStartDocument();
			while ((jp.nextToken() != null))
			{
				String fieldName = jp.getCurrentName();
				   // Let's move to value
				jp.nextToken();//value or start_object or start_array
				
				if ("rows".equals(fieldName))
				{
					writer.writeStartElement("entities");
					jp.nextToken();//start object, start array was taken care by prev nextToken() call
					String currentEntityId = null;
					while(jp.nextToken() != JsonToken.END_OBJECT) //reads the key
					{
						String keyName = jp.getCurrentName();
						jp.nextToken();//value or start_object or start_array
						if("key".equals(keyName))
						{
							String entityId = jp.getText();
							if(currentEntityId != null && !currentEntityId.equals(entityId))
							{
								currentEntityId = entityId;
								writer.writeEndElement();
								writer.writeStartElement("entity");
								writer.writeAttribute("id", entityId);
							}
							else if(currentEntityId == null)
							{
								currentEntityId = entityId;
								writer.writeStartElement("entity");
								writer.writeAttribute("id", entityId);
							}
						}
						else if("value".equals(keyName))
						{
							Map<String, Object> row = new LinkedHashMap<String, Object>(); 
							readJsonObject(jp, row);
							writeXMLObject(writer, row, "document", "entity");
							jp.nextToken(); //move it off the END_OBJECT
							jp.nextToken(); //move it off the END_OBJECT
						}
					}
					//Close array for the last entityId
					writer.writeEndElement();
					writer.writeEndElement();
				}
				
	
			}
//		    writer.writeEndElement();
		    writer.writeEndDocument();
		 }
		 finally
		 {
			 writer.flush();
			 writer.close();
		 }

	}
	
//	private void writeDocument (XMLStreamWriter writer, Map<String, Object> row) throws XMLStreamException
//	{
//		writer.writeStartElement("document");
//		Set<String> attributes = row.keySet();
//		for(String attribute: attributes)
//		{
//			Object value = row.get(attribute);
//			if (value instanceof String)
//			{
//				writer.writeAttribute(attribute, (String)value);
//			}
//			else
//			{
//				
//			}
//		}
//		writer.writeEndElement();
//	}
//	
	private void writeXMLObject(XMLStreamWriter writer, Map<String, Object> row, String name, String parentName) throws XMLStreamException
	{
		Map<String, Object> children = new LinkedHashMap<String, Object>();
		if(elementNameMap.containsKey(parentName))
		{
			name = elementNameMap.get(parentName);
		}
		writer.writeStartElement(name);
		Set<String> attributes = row.keySet();
		for(String attribute: attributes)
		{
			Object value = row.get(attribute);
			
			if (value instanceof String)
			{
				writer.writeAttribute(attribute, (String)value);
			}
			else
			{
				children.put(attribute, value);
			}
			
		}
		// go over the child elements
		for(String key: children.keySet())
		{
			Object value = children.get(key);
			if(value instanceof Map<?, ?>)
			{
				writeXMLObject(writer, (Map<String, Object>)value, key, name);
			}
			else if(value instanceof List<?>)
			{
				
				for(Map<String, Object> o: (List<Map<String, Object>>)value)
				{
					writeXMLObject(writer, o, key, name);
				}
				
			}
		}
		writer.writeEndElement();
	}
	private void readJsonObject(JsonParser jp, Map<String, Object> row)throws JsonParseException, IOException
	{
		while(jp.nextToken()!= JsonToken.END_OBJECT)
		{
			String fieldName = jp.getCurrentName();
			jp.nextToken();
			if(jp.getCurrentToken() == JsonToken.START_OBJECT)
			{
				Map<String, Object> object = new LinkedHashMap<String, Object>();
				readJsonObject(jp, object);
				row.put(fieldName, object);
			}
			else if(jp.getCurrentToken() == JsonToken.START_ARRAY)
			{
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				readJsonArray(jp, list);
				row.put(fieldName, list);
			}
			else
			{
				row.put(fieldName, jp.getText());
			}
		}
	}
	
	private void readJsonArray(JsonParser jp, List<Map<String, Object>> array)throws JsonParseException, IOException
	{
		while(jp.nextToken()!= JsonToken.END_ARRAY)
		{
			Map<String, Object> object = new LinkedHashMap<String, Object>();
			readJsonObject(jp, object);
			array.add(object);

		}
		
	}
}
