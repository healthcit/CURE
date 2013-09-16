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
package com.healthcit.analytics.businessdelegate;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.healthcit.analytics.dao.ReportTemplateDao;
import com.healthcit.analytics.model.ReportTemplate;

public class ReportTemplateManager {
	@Autowired
	private ReportTemplateDao reportTemplateDao;
		
	@Transactional(readOnly = true, propagation=Propagation.REQUIRED)
	public JSONArray getAllReports(Long userId)
	{
		List<ReportTemplate> templates = reportTemplateDao.findAllReportTemplates(userId);
		
		JSONArray jsonArray = new JSONArray();
		
		for ( ReportTemplate template : templates )
		{
			jsonArray.add( template.toJSON() );
		}
		
		return jsonArray;
	}
	
	@Transactional(readOnly = true, propagation=Propagation.REQUIRED)
	public JSONObject getReportById( Long id )
	{
		ReportTemplate reportTemplate = reportTemplateDao.getReportTemplateById(id);
		
		return JSONObject.fromObject(reportTemplate);
	}
	
	@Transactional(readOnly = true, propagation=Propagation.REQUIRED)
	private ReportTemplate getReportByTitle(String title, long userId,
			boolean shared) {
		ReportTemplate reportTemplate = null;
		List<ReportTemplate> reportTemplates = reportTemplateDao
				.getReportTemplatesByTitle(title);
		for (ReportTemplate template : reportTemplates) {
			if ((template.isShared() && shared) || (!template.isShared() && !shared && userId == template.getOwnerId())) {
				reportTemplate = template;
				break;
			}
		}
		return reportTemplate;
	}
	
	@Transactional(readOnly = true, propagation=Propagation.REQUIRED)
	public boolean checkIfReportTitleExists( String title, Long userId, Boolean shared )
	{
		return getReportByTitle( StringUtils.lowerCase( title ) , userId, shared) != null;
	}
	
	@Transactional(readOnly = false, propagation=Propagation.REQUIRED)
	public Long saveOrUpdate( ReportTemplate reportTemplate)
	{
		if ( reportTemplate.isEmpty() ) return null;
		
		int numRows = 0;
		ReportTemplate template = this.getReportByTitle(reportTemplate.getTitle(), reportTemplate.getOwnerId(), reportTemplate.isShared());
		if (template != null) {
			if (reportTemplate.getId() == null) {
				reportTemplate.setId(template.getId());
				//numRows = reportTemplateDao
				//		.updateReportTemplateByTitle(reportTemplate);
			} //else {
				numRows = reportTemplateDao
						.updateReportTemplateById(reportTemplate);
			//}
		}
		
		if ( numRows == 0 )
		{
			reportTemplateDao.saveReportTemplate( reportTemplate );			
		}		
		
		return reportTemplate.getId();
		
	}
	
	public boolean delete( Long reportTemplateId ) 
	{
		if ( reportTemplateId == null ) return false;
		
		int numDeleted = reportTemplateDao.deleteReportTemplateById( reportTemplateId );
		
		return ( numDeleted != 0 );
	}
	
}
