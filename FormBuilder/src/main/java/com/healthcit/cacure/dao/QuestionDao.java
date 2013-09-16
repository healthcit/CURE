/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.dao;

import javax.persistence.Query;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthcit.cacure.model.Question;

public class QuestionDao extends BaseQuestionDao
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(QuestionDao.class);

    @Override
	public Question getById(Long id)
        	{
    	Query query = em.createQuery("from Question q where id = :Id");
    	query.setParameter("Id", id);
    	return (Question) query.getSingleResult();
        	}
/*
  @Override
  public Question create(Question entity) {
	  Question result = super.create(entity);
	  return result;
  }

  @Override
  public Question update(Question entity) {
    Question result = super.update(entity);
    return result;
  }

*/

}
