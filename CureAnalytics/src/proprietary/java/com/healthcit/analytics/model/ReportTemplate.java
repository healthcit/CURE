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
package com.healthcit.analytics.model;

import java.sql.Timestamp;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.healthcit.analytics.utils.JSONUtils;

public class ReportTemplate {
	private Long id;
	private String title;
	private String report;
	private Timestamp timestamp;
	private Long ownerId;
	private boolean shared;
	private String ownerName;
	
	private static final String JSON_ID_FIELD = "id";
	private static final String JSON_TITLE_FIELD = "title";
	private static final String JSON_REPORT_FIELD = "report";
	private static final String JSON_TIMESTAMP_FIELD = "timestamp";
	private static final String JSON_SHARED_FIELD = "shared";
	private static final String JSON_OWNER_ID = "ownerId";
	private static final String JSON_OWNER_NAME = "ownerName";
	
	/**
	 * Default constructor
	 */
	public ReportTemplate(){
	}
	
	/**
	 * Explicit constructor
	 * @return
	 */
	public ReportTemplate(Long id, String title, String report, Timestamp timestamp, Long ownerId, boolean shared){
		this.id = id;
		this.title = title;
		this.report = report;
		this.timestamp = timestamp;
		this.ownerId = ownerId;
		this.shared = shared;
	}
	
	/**
	 * Explicit constructor (parameter is a JSONObject)
	 * @return
	 */
	public ReportTemplate( JSONObject json, Long ownerId, boolean shared){
		if ( json != null )
		{
			Object id = JSONUtils.getValue( json, JSON_ID_FIELD );
			this.id = ( NumberUtils.isNumber(id + "") ? NumberUtils.createLong( id.toString() ) : null );
			this.title = ( String )JSONUtils.getValue( json, JSON_TITLE_FIELD );
			Object report = (( JSONObject )JSONUtils.getValue( json, JSON_REPORT_FIELD ));
			this.report = report == null ? null : report.toString();
			this.timestamp = ( Timestamp )JSONUtils.getValue( json, JSON_TIMESTAMP_FIELD );
			this.ownerId = ownerId;
			this.shared = shared;
		}
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public JSONObject getJSONReport() {
		return StringUtils.isEmpty(report) ? null : JSONObject.fromObject( report );
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public String getReport() {
		return report;
	}	
	public void setReport(String reportString) {
		this.report = reportString;
	}
	
	public boolean isEmpty(){
		return ( StringUtils.isEmpty( this.report ) );
	}
	
	public Long getOwnerId() {
		return this.ownerId;
	}
	
	public boolean isShared() {
		return shared;
	}
	
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public JSONObject toJSON(){
		
		JSONObject json = new JSONObject();
		
		json.put( JSON_ID_FIELD, this.id );
		
		json.put( JSON_TITLE_FIELD, this.title );
		
		json.put( JSON_REPORT_FIELD, getJSONReport() );
		
		json.put(JSON_SHARED_FIELD, this.shared);
		
		json.put(JSON_OWNER_ID, this.ownerId);
		
		if ( this.timestamp != null ) {
			json.put( JSON_TIMESTAMP_FIELD, this.timestamp.toString() );
		}
		
		if(this.ownerName != null){
			json.put(JSON_OWNER_NAME, this.ownerName);
		}
		
		return json;
	}
}
