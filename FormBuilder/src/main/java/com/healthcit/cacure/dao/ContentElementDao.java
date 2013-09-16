/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.dao;

import javax.persistence.Query;

import com.healthcit.cacure.model.ContentElement;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentElementDao extends FormElementDao
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ContentElementDao.class);

	/*
  @Override
  public ContentElement create(ContentElement entity) {
	  ContentElement result = super.create(entity);
	  reindexFormElementTextSearch(result);
	  return result;
  }

  @Override
  public ContentElement update(ContentElement entity) {
	  ContentElement result = super.update(entity);
    reindexFormElementTextSearch(result);
    return result;
  }
*/
	@Override
	public ContentElement getById(Long id)
	{
		Query query = em.createQuery("from FormElement fe where id = :Id and element_type='content'");
		query.setParameter("Id", id);
		return (ContentElement) query.getSingleResult();
	}  

}
