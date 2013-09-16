/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dao;

import javax.persistence.Query;

import com.healthcit.how.models.FormSkipAnswer;

public class FormSkipAnswerDao extends BaseJpaDao<FormSkipAnswer,String> {

	public FormSkipAnswerDao() {
		super(FormSkipAnswer.class);
	}
	
	public int deleteByTriggerFormInstance( String triggerFormId, String ownerId, Long triggerInstanceId )
	{
		String jpql = "delete from FormSkipAnswer f where f.triggerFormId = :formId and f.ownerId = :ownerId and f.triggerInstanceId = :instanceId";
		Query query = em.createQuery( jpql );
		query.setParameter("formId", triggerFormId);
		query.setParameter("ownerId", ownerId);
		query.setParameter("instanceId", triggerInstanceId);
		return query.executeUpdate();
	}

}
