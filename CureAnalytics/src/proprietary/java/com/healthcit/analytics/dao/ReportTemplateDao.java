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
package com.healthcit.analytics.dao;

import java.util.List;

import com.healthcit.analytics.model.ReportTemplate;

public interface ReportTemplateDao {
	public List<ReportTemplate> findAllReportTemplates(Long userId);
	public ReportTemplate getReportTemplateById(Long id);
	public List<ReportTemplate> getReportTemplatesByTitle(String title);
	public void saveReportTemplate(ReportTemplate template);
	public int updateReportTemplateById(ReportTemplate template);
	public int updateReportTemplateByTitle(ReportTemplate template);
	public int deleteReportTemplateById(Long id);
}
