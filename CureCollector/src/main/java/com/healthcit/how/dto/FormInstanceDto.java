/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dto;

import java.io.Serializable;

public class FormInstanceDto implements Serializable{

	private static final long serialVersionUID = 8046572536083723319L;
	private String formId;
	private Long instanceId;
	
	public FormInstanceDto(String formId, Long instanceId){
		this.formId = formId;
		this.instanceId = instanceId;
	}
	
	
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public Long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}


	@Override
	public int hashCode() {
		int hashCode = ( this.formId == null ? 0 : this.formId.hashCode() );
		hashCode ^= ( this.instanceId == null ? 0 : this.instanceId.hashCode() );
		return hashCode;
	}


	@Override
	public boolean equals(Object obj) {
		if ( obj == null || ! (obj instanceof FormInstanceDto) ) return false;
		if ( obj == this ) return true;
		boolean equal = ( this.formId == null ? 
						this.formId == ((FormInstanceDto)obj).getFormId() : 
						this.formId.equals( ((FormInstanceDto)obj).getFormId() ))
		             && ( this.instanceId == null ? 
		            	this.instanceId == ((FormInstanceDto)obj).getInstanceId() : 
		            	this.instanceId.equals( ((FormInstanceDto)obj).getInstanceId() ));
		
		return equal;
	}
	
}
