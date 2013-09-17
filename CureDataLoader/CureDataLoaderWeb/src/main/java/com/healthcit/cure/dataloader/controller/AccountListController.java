/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cure.dataloader.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.healthcit.cure.dataloader.beans.UserCredentials;
import com.healthcit.cure.dataloader.businessdelegates.UserCredentialsManager;

@Controller
@RequestMapping(value="/account.list")
public class AccountListController {
	
	@Autowired
	UserCredentialsManager userCredentialsManager;
	
	@RequestMapping(method = RequestMethod.GET)
	public String listAccounts(HttpServletRequest request)
	{
		List<UserCredentials> credentials = userCredentialsManager.getAllAccounts();
		request.setAttribute("accountList", credentials);
		return "listAccounts";
	}

}
