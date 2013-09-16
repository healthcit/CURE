/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.businessdelegates.SecurityQuestionManager;
import com.healthcit.how.models.SecurityQuestion;
import com.healthcit.how.utils.NumberUtils;

public class SecurityQuestionPropertyEditor extends PropertyEditorSupport {
	@Autowired
	private SecurityQuestionManager securityQuestionManager;

	@Override
	public String getAsText() {
		SecurityQuestion securityQuestion = ( SecurityQuestion ) super.getValue();
		if ( securityQuestion == null ) 
			return null;
		return String.valueOf( securityQuestion.getId() );
	}

	@Override
	public void setAsText( String text )  {
		try{
		if ( StringUtils.isEmpty( text ) )
			super.setValue( null );
		else {
			Long primaryKey = NumberUtils.parseLong( text );
			if ( primaryKey == null )
				super.setValue( null );
			else {
				SecurityQuestion securityQuestion = securityQuestionManager.getQuestion( primaryKey );
				super.setValue( securityQuestion );
			}
		}
		} catch (  IllegalArgumentException ex ) {
			ex.printStackTrace();
		}
	}
	
}
