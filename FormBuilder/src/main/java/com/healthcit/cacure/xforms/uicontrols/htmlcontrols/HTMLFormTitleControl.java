/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.xforms.uicontrols.htmlcontrols;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.healthcit.cacure.xforms.XFormsConstants;
import com.healthcit.cacure.xforms.XFormsUtils;
import com.healthcit.cacure.xforms.uicontrols.XFormUIControl;

public class HTMLFormTitleControl extends XFormUIControl
{

	@Override
	public List<Element> getControlElements()
	{

		Element formTitle = new Element(XFormsConstants.OUTPUT_TAG, XFormsConstants.XFORMS_NAMESPACE);
		formTitle.setAttribute("ref", XFormsUtils.getFormNameXPath());
		formTitle.setAttribute("class", XFormsConstants.FORM_TITLE_CSS_CLASS);
		
		List<Element> eList = new ArrayList<Element>();
		eList.add(formTitle);
		return eList;
	}

}
