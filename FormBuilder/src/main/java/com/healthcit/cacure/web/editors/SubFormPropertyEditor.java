/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.web.editors;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.math.NumberUtils;

import com.healthcit.cacure.dao.FormDao;
import com.healthcit.cacure.dao.SkipPatternDao;
import com.healthcit.cacure.model.BaseForm;
import com.healthcit.cacure.model.BaseQuestion;
import com.healthcit.cacure.model.BaseSkipRule;
import com.healthcit.cacure.model.Description;
import com.healthcit.cacure.model.QuestionSkipRule;
import com.healthcit.cacure.model.QuestionnaireForm;
import com.healthcit.cacure.model.TableQuestion;



public class SubFormPropertyEditor extends PropertyEditorSupport 
{	
	
	private FormDao formDao;
	
	public SubFormPropertyEditor(FormDao formDao)
	{
		//System.out.println("in the subformeditor const");
		this.formDao = formDao;
	}
	


	@Override
	public String getAsText()
	{
		BaseForm form = (BaseForm) getValue();
		if(form == null){
			return null;
		}
		
		return form.getName();

	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException 
	{
		BaseForm form = null;
		if(! text.equals("0")){
			
		Long parent_id = Long.parseLong(text);		
		form = this.formDao.getById(parent_id);		
		setValue(form);
		
		}
		
		
	}



}
