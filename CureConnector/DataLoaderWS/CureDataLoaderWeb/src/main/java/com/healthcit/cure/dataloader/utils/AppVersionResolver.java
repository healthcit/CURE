/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cure.dataloader.utils;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

public class AppVersionResolver implements ServletContextAware
{

	private final String APP_VERSION_ATTR = "appVersion";
	private String appVersion;
	private ServletContext servletContext;
	
	public void setAppVersion(String appVersion) 
	{
		this.appVersion = appVersion;
		if (servletContext != null)
			servletContext.setAttribute(APP_VERSION_ATTR, appVersion);
	}

	public String getAppVersion() {
		return appVersion;
	}

	@Override
	public void setServletContext(ServletContext sc) {
		if (appVersion != null)
		{
			sc.setAttribute(APP_VERSION_ATTR, appVersion);
		}
		else
		{
			servletContext = sc;
		}		
	}
}
