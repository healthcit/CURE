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
package com.healthcit.analytics.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.healthcit.analytics.dao.ReportTemplateDao;
import com.healthcit.analytics.dao.rowmapper.ReportTemplateRowMapper;
import com.healthcit.analytics.model.ReportTemplate;
import com.healthcit.analytics.utils.Constants;

@SuppressWarnings("unused")
public class JdbcReportTemplateDAO implements MessageSourceAware, ReportTemplateDao {
	private MessageSource messageSource;
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void setMessageSource(MessageSource messageSource) 
	{
		this.messageSource = messageSource;
	}

    public void setDataSource(DataSource dataSource) 
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public ReportTemplate getReportTemplateById(Long id) 
	{
		String sql = messageSource.getMessage(Constants.GET_REPORT_BY_ID_SQL, null, Locale.getDefault());
		
		List<ReportTemplate> results = jdbcTemplate.query( sql, new Object[]{id}, new ReportTemplateRowMapper());
		
		return ( results.isEmpty() ? null : results.get( 0 ) );
		
	}

	@Override
	public List<ReportTemplate> getReportTemplatesByTitle(String title) 
	{
		String sql = messageSource.getMessage(Constants.GET_REPORT_BY_TITLE_SQL, null, Locale.getDefault());
		
		List<ReportTemplate> results = jdbcTemplate.query( sql, new Object[]{StringUtils.lowerCase( title )}, new ReportTemplateRowMapper());
		
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportTemplate> findAllReportTemplates(Long userId) 
	{
		String sql = messageSource.getMessage(Constants.GET_REPORTS_SQL, null, Locale.getDefault());
		List<ReportTemplate> results = jdbcTemplate.query( sql, new ReportTemplateRowMapper(), new Object[]{userId});
		return results;
	}

	@Override
	public void saveReportTemplate(ReportTemplate template) 
	{
		String sql = messageSource.getMessage(Constants.SAVE_NEW_REPORT_SQL, null, Locale.getDefault());
		long id = jdbcTemplate.queryForLong( sql,  template.getTitle(), template.getReport(), template.getOwnerId(), template.isShared());
		template.setId(id);
	}

	@Override
	public int updateReportTemplateById(ReportTemplate template) 
	{
		String sql = messageSource.getMessage(Constants.UPDATE_REPORT_BY_ID_SQL, null, Locale.getDefault());
		
		int numUpdatedRows = jdbcTemplate.update( sql, new Object[]{ template.getTitle(), template.getReport(), template.isShared(), template.getOwnerId(), template.getId() } );
		
		return numUpdatedRows;
	}

	@Override
	public int updateReportTemplateByTitle(ReportTemplate template) 
	{
		String sql = messageSource.getMessage(Constants.UPDATE_REPORT_BY_TITLE_SQL, null, Locale.getDefault());
		
		int numUpdatedRows = jdbcTemplate.update( sql, new Object[]{ template.getReport(), template.isShared(), template.getOwnerId(), template.getTitle() } );
		
		return numUpdatedRows;
	}

	@Override
	public int deleteReportTemplateById(Long id) 
	{
		String sql = messageSource.getMessage(Constants.DELETE_REPORT_BY_ID_SQL, null, Locale.getDefault());
		
		int numUpdatedRows = jdbcTemplate.update( sql, new Object[]{ id } );
		
		return numUpdatedRows;
	}
	
}
