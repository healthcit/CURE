/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.propertyeditors;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.models.SecurityQuestion;

public class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {
	
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger( CustomPropertyEditorRegistrar.class );
	
	@Autowired
	private SecurityQuestionPropertyEditor securityQuestionPropertyEditor;
	
	@Override
	public void registerCustomEditors( PropertyEditorRegistry registry ) {
		// Register custom Property Editor for SecurityQuestion 
		registry.registerCustomEditor( SecurityQuestion.class, securityQuestionPropertyEditor );
		
		// ... register other property editors as required ...
	}

}
