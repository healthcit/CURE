/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.interceptors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.healthcit.how.utils.URLUtils;

/**
 * Base interceptor which may be used to configure Spring MVC interception for specific URL paths.
 * "includeControllerPaths" specify the URL paths that should be intercepted, while
 * "excludeControllerPaths" specify the URL paths that should NOT be intercepted.
 * @author oawofolu
 *
 */
public abstract class AbstractCureInterceptor extends HandlerInterceptorAdapter {
	
	private AntPathMatcher matcher = new AntPathMatcher();

	private List<String> includeControllerPaths = new ArrayList<String>();
	
	private List<String> excludeControllerPaths = new ArrayList<String>();
	
	public List<String> getExcludeControllerPaths() {
		return excludeControllerPaths;
	}

	public void setExcludeControllerPaths(List<String> controllerPaths) {
		this.excludeControllerPaths = controllerPaths;
	}

	public List<String> getIncludeControllerPaths() {
		return includeControllerPaths;
	}

	public void setIncludeControllerPaths(List<String> includeControllerPaths) {
		this.includeControllerPaths = includeControllerPaths;
	}
	
	protected boolean isInterceptable( HttpServletRequest request )
	{		
		String path = URLUtils.getPathInfo( request );		
		for ( String controllerPath : includeControllerPaths )
		{
			if ( matcher.isPattern( controllerPath ) && matcher.match( controllerPath, path ) ) return true;
		}	
		for ( String controllerPath : excludeControllerPaths )
		{
			if ( matcher.isPattern( controllerPath ) && matcher.match( controllerPath, path ) ) return false;
		}	
		return false;
	}
	
	@Override
	public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
	throws Exception 
	{
		if ( isInterceptable(request) )
		{
			return invokePreHandle(request, response, handler);
		}
		else return true;
	}
	
	protected abstract boolean invokePreHandle( HttpServletRequest request, HttpServletResponse response, Object handler )
	throws Exception;

}
