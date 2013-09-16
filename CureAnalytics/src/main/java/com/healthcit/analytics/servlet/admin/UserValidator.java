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
package com.healthcit.analytics.servlet.admin;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.healthcit.analytics.service.UserManager;
import com.healthcit.analytics.web.model.UserModel;

/**
 * Validator for {@link UserModel} validation.
 * 
 * @author Stanislav Sedavnikh
 *
 */
@Component("userValidator")
public class UserValidator implements Validator {
	
	@Autowired
	private UserManager userManager;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return UserModel.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if(target instanceof UserModel){
			UserModel user = (UserModel)target;
			
			//Check is user name already exists
			if(!errors.hasFieldErrors("username") && user.getId() == null){
				if(this.userManager.userExists(user.getUsername())){
					errors.rejectValue("username", "error.username.exists", "User with the same name already exists. ");
				}
			}
			
			if(StringUtils.isNotBlank(user.getPassword())){
				if(user.getPassword().length() < 6){
					errors.rejectValue("password", "error.user.pass", "At least 6 symbols required. ");
				} else if(!user.getPassword().equals(user.getConfirmPassword())) {
					errors.rejectValue("password", "error.user.pass.confirm", "Password confirmation doesn't match.");
				}
			} else if(user.getId() == null){
				errors.rejectValue("password", "error.user.pass.empty", "Password can not be empty.");
			}
		}
	}

}
