/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.editors;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.healthcit.how.businessdelegates.TagManager;
import com.healthcit.how.models.Tag;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TagPropertyEditor  extends PropertyEditorSupport 
{

	TagManager tagManager;
	
	public TagPropertyEditor(TagManager tagManager)
	{
		this.tagManager = tagManager;
	}
	 public void setAsText(String id) throws IllegalArgumentException {
		Tag tag =  tagManager.getTag(id);
		if (tag == null)
		{
			tag = new Tag();
			tag.setId(id);
			tagManager.addNewTag(tag);
		}
		setValue(tag);
	 }
	    
	    @SuppressWarnings("unchecked")
		@Override
	    public String getAsText() {
	    	Tag tag = (Tag)getValue();
	    	String tagId = null;
	    	if(tag !=null)
	    	{
	    		tagId = tag.getId();
	    	}
	    	return tagId;
	    }

}
