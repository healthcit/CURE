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
package com.healthcit.analytics.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.healthcit.analytics.businessdelegate.ReportTemplateManager;
import com.healthcit.analytics.model.ReportTemplate;
import com.healthcit.analytics.model.User;

@Service
@RemoteProxy(name="reportTemplateService")
public class ReportTemplateService {
	
	private static Logger log = LoggerFactory.getLogger( ReportTemplateService.class );
	
	@Autowired
	private ReportTemplateManager manager;
	
	public ReportTemplateService(){}
	
	@RemoteMethod
	public String getAllReportTemplates()
	{
		Long userId = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
		JSONArray templates = manager.getAllReports(userId);
		log.debug( "Size is " + templates.size());		
		return /*manager.getAllReports().toString()*/ templates.toString();
	}

	@RemoteMethod
	public boolean checkIfReportTitleExists( String title, boolean shared )
	{
		Long userId = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
		boolean exists = manager.checkIfReportTitleExists( title, userId, shared );
		
		log.info(title + " exists: " + exists);
		
		return exists;
	}
	
	@RemoteMethod
	public Long saveOrUpdateReportTemplate( String templateString )
	{
		
		if ( StringUtils.isBlank( templateString )) return null;
		
		JSONObject template = ( JSONObject.fromObject( templateString ) );
		Long userId = null;
		Boolean sharedQuestion = (Boolean)template.get("sharedQuestion");
		userId = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
		Long templateId = manager.saveOrUpdate( new ReportTemplate( template, userId, sharedQuestion) );
		
		log.info( "Template Id : " + (templateId==null ? "blank" : templateId) );
		
		return templateId;
	}
	
	@RemoteMethod
	public String deleteReportTemplate( String templateId ) 
	{
		if ( ! NumberUtils.isNumber( templateId ) ) return null;
		
		Long id = NumberUtils.createLong( templateId );
		
		boolean isDeleted = manager.delete( id );
		
		log.info ( "Template Id " + templateId + " deleted: " + isDeleted );
		
		return ( isDeleted ? templateId : null );		
	}
	
}
