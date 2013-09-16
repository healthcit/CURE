/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.dao.SecurityQuestionDao;
import com.healthcit.how.models.SecurityQuestion;

/**
 * Business Delegate which handles the SecurityQuestion model.
 * @author Oawofolu
 *
 */
public class SecurityQuestionManager {
	
	@Autowired
	private SecurityQuestionDao securityQuestionDao;
	
	public List<SecurityQuestion> listQuestions(){
		return securityQuestionDao.getCachedQuestionList();
	}
	
	public SecurityQuestion getQuestion( Long id ) {
		return securityQuestionDao.getById( id );
	}

}
