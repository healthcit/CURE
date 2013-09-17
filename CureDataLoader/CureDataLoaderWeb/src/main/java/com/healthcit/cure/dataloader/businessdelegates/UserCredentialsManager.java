/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cure.dataloader.businessdelegates;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cure.dataloader.beans.UserCredentials;
import com.healthcit.cure.dataloader.dao.UserCredentialsDao;

public class UserCredentialsManager {
	
	@Autowired
	UserCredentialsDao userCredentialsDao;
	
	
	public boolean isValidUser(UserCredentials userCredentials)
	{
		int count = userCredentialsDao.verifyAccount(userCredentials);
		return (count>0) ? true: false;
	}

	public List<UserCredentials> getAllAccounts()
	{
		return userCredentialsDao.getAllAccounts();
	}
	
	
	public UserCredentials getAccount(String accountId)
	{
		return userCredentialsDao.getAccount(accountId);
	}
	
	public void createAccount(UserCredentials credentials)
	{
		userCredentialsDao.createAccount(credentials);
	}
	
	public void disableAccount(String accountId)
	{
		userCredentialsDao.updateEnabled(accountId, false);
	}
	
	public void enableAccount(String accountId)
	{
		userCredentialsDao.updateEnabled(accountId, true);
	}
	public void deleteAccount(String accountId)
	{
		userCredentialsDao.delete(accountId);
	}
}
