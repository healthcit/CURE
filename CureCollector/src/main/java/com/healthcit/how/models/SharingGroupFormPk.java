/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

public class SharingGroupFormPk  implements java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2139327938464401263L;
	private static final int HASH_CODE_BASE = 17286229; // some random number

	protected String form;
	protected String sharingGroup;

	public SharingGroupFormPk(){}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof SharingGroupFormPk)
		{
			SharingGroupFormPk fk2 = (SharingGroupFormPk)obj;
			boolean equals = areStringsEqual(this.form, fk2.form);

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
		if (this.form != null)
			hashCode |= this.form.hashCode();
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

	public void setForm(String form)
	{
		this.form = form;
	}
	public void setSharingGroup(String sharingGroup)
	{
		this.sharingGroup = sharingGroup;
	}
}
