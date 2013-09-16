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
package com.healthcit.analytics.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.healthcit.analytics.model.ReportTemplate;

public class ReportTemplateRowMapper implements RowMapper<ReportTemplate> {

	private static final String ID_COLUMN = "id";
	private static final String TITLE_COLUMN = "title";
	private static final String REPORT_COLUMN = "report";
	private static final String TIMESTAMP_COLUMN = "timestamp";
	private static final String OWNER_ID_COLUMN = "owner_id";
	private static final String SHARED_COLUMN = "shared";
	private static final String USERNAME_COLUMN = "username";

	@Override
	public ReportTemplate mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		ReportTemplate template = new ReportTemplate(
				resultSet.getLong(ID_COLUMN),
				resultSet.getString(TITLE_COLUMN),
				resultSet.getString(REPORT_COLUMN),
				resultSet.getTimestamp(TIMESTAMP_COLUMN),
				resultSet.getLong(OWNER_ID_COLUMN),
				resultSet.getBoolean(SHARED_COLUMN));
		try{
			resultSet.findColumn(USERNAME_COLUMN);
			template.setOwnerName(resultSet.getString(USERNAME_COLUMN));
		} catch(SQLException ex){
			
		}
		
		return template;
	}

}
