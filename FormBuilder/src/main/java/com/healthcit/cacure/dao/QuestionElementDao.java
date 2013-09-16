/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.dao;

import javax.persistence.Query;

import com.healthcit.cacure.model.QuestionElement;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionElementDao extends FormElementDao
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(QuestionElementDao.class);

/*
  @Override
  public QuestionElement create(QuestionElement entity) {
	  QuestionElement result = super.create(entity);
//	  reindexFormElementTextSearch(result);
	  return result;
  }

  @Override
  public QuestionElement update(QuestionElement entity) {
	  QuestionElement result = super.update(entity);
//    reindexFormElementTextSearch(result);
    return result;
  }
  */
  @Override
  public QuestionElement getById(Long id)
  {
	  Query query = em.createQuery("from FormElement fe where id = :Id and element_type='question'");
	  query.setParameter("Id", id);
	  return (QuestionElement) query.getSingleResult();
  }  
  
}
