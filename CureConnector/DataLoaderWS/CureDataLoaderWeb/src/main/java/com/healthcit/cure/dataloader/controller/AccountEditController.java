/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cure.dataloader.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.healthcit.cure.dataloader.beans.UserCredentials;
import com.healthcit.cure.dataloader.businessdelegates.UserCredentialsManager;




@Controller
@RequestMapping(value="/account.edit")
public class AccountEditController {
	
	private static final Logger log = LoggerFactory.getLogger( AccountEditController.class );

	@Autowired
	UserCredentialsManager userCredentialsManager;
	public static final String COMMAND_NAME = "accountDetails";
	
	@ModelAttribute(COMMAND_NAME)
	public UserCredentials createCommand(@RequestParam(value = "account_id", required = false) String accountId)
	{
		UserCredentials credentials;
		if(accountId==null)
		{
			credentials = new UserCredentials();
		}
		else
		{
			credentials= userCredentialsManager.getAccount(accountId);
		}
		//return new UserCredentials();
		return credentials;
	
	}
	
	
	@RequestMapping(method = RequestMethod.POST, params="create")  
	public View CreateNewCredentials (@ModelAttribute(COMMAND_NAME) UserCredentials accountDetails)
	{
		log.info("In POST");
		log.info("AccountDetails: " + accountDetails);
		userCredentialsManager.createAccount(accountDetails);
		return new RedirectView ("account.list", true);
	}
	
	@RequestMapping(method = RequestMethod.POST, params="disable")  
	public View disableAccount (@ModelAttribute(COMMAND_NAME) UserCredentials accountDetails)
	{
		String accountId = accountDetails.getAccountId();
		log.info("In POST");
		log.info("AccountDetails: " + accountDetails);
		userCredentialsManager.disableAccount(accountId);
		return new RedirectView ("account.list", true);
	}
	
	@RequestMapping(method = RequestMethod.POST, params="enable")  
	public View enableAccount (@ModelAttribute(COMMAND_NAME) UserCredentials accountDetails)
	{
		String accountId = accountDetails.getAccountId();
		log.info("In POST");
		log.info("AccountDetails: " + accountDetails);
		userCredentialsManager.enableAccount(accountId);
		return new RedirectView ("account.list", true);
	}
	
	@RequestMapping(method = RequestMethod.POST, params="delete")  
	public View deleteAccount (@ModelAttribute(COMMAND_NAME) UserCredentials accountDetails)
	{
		String accountId = accountDetails.getAccountId();
		log.info("In POST");
		log.info("AccountDetails: " + accountDetails);
		userCredentialsManager.deleteAccount(accountId);
		return new RedirectView ("account.list", true);
	}
	
	
	@RequestMapping(method = RequestMethod.GET)  
	public String displayCredentials (@ModelAttribute(COMMAND_NAME) UserCredentials accountDetails)
	{
		log.info("In GET");
		
		return ("accountCreate");
		
		
		
	}
	
//	@RequestMapping(value="/find")  
//	public String findCredentials (@ModelAttribute(COMMAND_NAME) UserCredentials accountDetails)
//	{
//		log.info("In Find");
////		UserCredentials userCredentials = userCredentialsManager.getAccount(accountId);
//		return ("accountCreate");
		
		
		
//	}
}
