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


public class Constants {

	public static final String ORDERED_COLUMN_NAMES = "orderedColumnNames";
	public static final String DOCUMENT = "document";
	public static final String NUMERIC_DATA_TYPE = "numericDataType";
	public static final String BOOLEAN_DATA_TYPE = "booleanDataType";
	public static final String DATE_DATA_TYPE = "dateDataType";
	
	// Properties file keys
	public static final String SAVE_NEW_REPORT_SQL = "saveNewReportTemplateSql";
	public static final String UPDATE_REPORT_BY_ID_SQL = "updateReportTemplateByIdSql";
	public static final String UPDATE_REPORT_BY_TITLE_SQL = "updateReportTemplateByTitleSql";
	public static final String GET_REPORTS_SQL = "getAllReportTemplatesSql";
	public static final String GET_REPORT_BY_ID_SQL = "getReportTemplateByIdSql";
	public static final String GET_REPORT_BY_TITLE_SQL = "getReportTemplateByTitleSql";
	public static final String DELETE_REPORT_BY_ID_SQL = "deleteReportTemplateByIdSql";
	
	// HTTP Response-Related keys
	public static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";
	public static final String JSON_CONTENT_TYPE = "application/json";
	public static final String ZIP_CONTENT_TYPE = "application/octet-stream";
	public static final String PLAIN_TEXT = "plain/text";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String PRAGMA_HEADER = "Pragma";
	public static final String CACHE_CONTROL_HEADER = "Cache-Control";
	public static final String EXPIRES_HEADER = "Expires";
	public static final String NO_CACHE = "no-cache";

}
