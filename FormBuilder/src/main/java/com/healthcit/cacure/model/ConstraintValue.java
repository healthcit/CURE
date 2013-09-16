/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.model;

public class ConstraintValue {
	private String name;
	private String value;
	private String displayName;
	
	public ConstraintValue()
	{
		name = new String();
		value = new String();
	}
	public ConstraintValue(String name, String value, String displayName)
	{
	   this.name = name;
	   this.value = value;
	   this.displayName = displayName;
	}
	public ConstraintValue(String name, String value)
	{
	   this.name = name;
	   this.value = value;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public String getDisplayName()
	{
		return displayName;
	}

}
