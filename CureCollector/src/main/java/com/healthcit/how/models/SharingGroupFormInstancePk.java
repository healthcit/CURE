/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import org.apache.commons.lang.StringUtils;

public class SharingGroupFormInstancePk  implements java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2139327938464401263L;
	private static final int HASH_CODE_BASE = 17286229; // some random number

	protected String form;
	protected String sharingGroup;
	protected Long instanceId;

	public SharingGroupFormInstancePk(){}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof SharingGroupFormInstancePk)
		{
			SharingGroupFormInstancePk fk2 = (SharingGroupFormInstancePk)obj;
			return StringUtils.equals(this.form, fk2.form)
				&& StringUtils.equals(this.sharingGroup, fk2.sharingGroup)
				&& this.instanceId == null ? this.instanceId == fk2.instanceId  : this.instanceId.equals(fk2.instanceId);
		}
		else return false;
	}

	@Override
	public int hashCode()
	{
		int hashCode = HASH_CODE_BASE;
		if (this.form != null)
			hashCode |= this.form.hashCode();
		if (this.sharingGroup != null)
			hashCode |= (this.sharingGroup.hashCode());
		if (this.instanceId != null)
			hashCode |= (this.instanceId.hashCode());

		return hashCode;
	}

	public void setForm(String form)
	{
		this.form = form;
	}
	public void setSharingGroup(String sharingGroup)
	{
		this.sharingGroup = sharingGroup;
	}
	public void setInstanceId(Long instance) 
	{
		this.instanceId = instance;
	}
	
}
