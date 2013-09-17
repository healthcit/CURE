/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cure.dataloader.beans;

public class UserCredentials {
	
	private String accountId;
	private String token;
	private String description;
	private boolean enabled;
	
	public UserCredentials()
	{
		
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}
	public void setToken(String token)
	{
		this.token = token;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	
	public String getAccountId()
	{
		return this.accountId;
	}
	public String  getToken()
	{
		return this.token;
	}
	public String getDescription()
	{
		return this.description;
	}
	public boolean getEnabled()
	{
		return this.enabled;
	}
	
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	
	@Override
	public String toString()
	{
		return "AccountId: " + accountId + ", token: " + token+ ", description: " + description;
	}
}
