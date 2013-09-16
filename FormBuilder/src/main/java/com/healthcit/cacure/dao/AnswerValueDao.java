/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnswerValueDao {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(FormElementDao.class);
	@PersistenceContext
	protected EntityManager em;

	public boolean isValidAnswerValue(String permAnswerValueId)
	{
	    String queryStr = "	select 1 from answer_value where permanent_id = ?1";

		Query query = em.createNativeQuery(queryStr);
	    query.setParameter(1, permAnswerValueId);
	    
	    @SuppressWarnings("rawtypes")
		List results = query.getResultList();

	    if(results.size() > 0){
	    	return true;
	    }
	    return false;
	}
}
