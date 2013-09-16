/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
	
	Logger log = LoggerFactory.getLogger(UserService.class);

	
	private String authType;

	public String getAuthType() {
		
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}
	
	

}
