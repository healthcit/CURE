/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.businessdelegates.ModuleDeploymentManager;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.utils.IOUtils;

/**
 * This interceptor will check if there are any modules whose redeployment process has not yet been completed
 * (via recalculation of form skips).
 * If so, then an error will be returned describing the issue
 * and requesting administrative action to complete the redeployment process.
 * 
 * @author oawofolu
 *
 */
public class ModuleDeploymentInterceptor extends AbstractCureInterceptor {
	
	private static Logger log = LoggerFactory.getLogger( ModuleDeploymentInterceptor.class );
		
	@Autowired ModuleDeploymentManager moduleDeploymentManager;

	@Override
	public boolean invokePreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
	throws Exception 
	{
		log.debug("In preHandle...");
		
		// If any modules exist that are not fully processed
		// i.e. modules whose status = DEPLOYMENT_LOCKED,
		// then display an error message.
		// An administrator must update the modules via a link to 
		// /admin/updateFormSkipData.form
		// before this handler can become accessible again.
		if ( moduleDeploymentManager.checkIfDeploymentLockedModulesExist() )
		{
			String errorMessage = "Module deployment error";
			
			JSONObject errorInfo = new JSONObject();		
			
			errorInfo.put( Constants.RESPONSE_STATUS, Constants.RESPONSE_STATUS_ERROR);
			
			errorInfo.put( Constants.RESPONSE_STATUS_DETAILS,  errorMessage );		
			
			log.error( errorInfo.toString() );		
			
			IOUtils.sendResults(response, Constants.CONTENT_TYPE_JSON, errorInfo );
			
			return false;
		}
		
		// Else proceed as usual
		return true;
	}
	
	
}
