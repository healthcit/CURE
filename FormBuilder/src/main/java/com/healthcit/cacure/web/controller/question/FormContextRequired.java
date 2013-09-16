/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.web.controller.question;

import com.healthcit.cacure.model.BaseForm;

public interface FormContextRequired
{
	public static final String FORM_ID_NAME = "formId";

	public void setFormId(Long formId);
	public void unsetFormId();
	public BaseForm getFormContext();
}
