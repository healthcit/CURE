/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

public class SharingGroupModulePk implements java.io.Serializable {

	private static final long serialVersionUID = 2139327938464401264L;
	private static final int HASH_CODE_BASE = 17286227; // some random number
	
	private String module;
	private String sharingGroup;

	public SharingGroupModulePk(){}

	public SharingGroupModulePk(String module, String sharingGroup)
	{
		this.module = module;
		this.sharingGroup = sharingGroup;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getSharingGroup()
	{
		return sharingGroup;
	}
	public void setSharingGroup(String sharingGroup)
	{
		this.sharingGroup = sharingGroup;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof SharingGroupModulePk)
		{
			SharingGroupModulePk fk2 = (SharingGroupModulePk)obj;
			boolean equals = areStringsEqual(this.module, fk2.module);

			if (equals)
			{
				equals = areStringsEqual(this.sharingGroup, fk2.sharingGroup);
			}
			return equals;
		}
		else
			return false;
	}

	@Override
	public int hashCode()
	{
		int hashCode = HASH_CODE_BASE;
		if (this.module != null)
			hashCode |= this.module.hashCode();
		if (this.sharingGroup != null)
			hashCode |= (this.sharingGroup.hashCode());

		return hashCode;
	}

	private boolean areStringsEqual(String str1, String str2)
	{
		if (str1 != null)
			return str1.equals(str2);
		else if (str2 != null)
			return false; // null != ! null
		else
			return true; // null == null

	}
}
