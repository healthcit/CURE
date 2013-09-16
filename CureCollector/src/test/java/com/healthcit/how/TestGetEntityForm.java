/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.healthcit.how.dao.EntityFormDao;
import com.healthcit.how.models.SharingGroupFormInstance;
import com.healthcit.how.models.QuestionnaireForm;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"file:src/main/webapp/WEB-INF/spring/app-config.xml",
		"file:src/main/webapp/WEB-INF/spring/dao-config.xml"
		                           })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class TestGetEntityForm {
	@Autowired
	EntityManagerFactory emf;


	EntityManager em;
	
	@Autowired
	EntityFormDao entityFormDao;
	
	@Before
    public void setUp() {
		em = emf.createEntityManager();
		TransactionSynchronizationManager.bindResource(emf , new EntityManagerHolder(em));
    }

    @After
    public void tearDown() throws Exception {
    	TransactionSynchronizationManager.unbindResourceIfPossible(emf);
    }
	
    @Test
    public void test()
    {
    	String formId="dfb1aca6-733e-4b7c-848c-ed2e6084dcc7";
    	String ownerId = "6a8a6834-aa4b-4e71-8974-ef3f8c80ab32";
    	List<SharingGroupFormInstance> entityForms = entityFormDao.getFormInstanceByIdAndOwner(formId, ownerId);
    	SharingGroupFormInstance entityForm = entityForms.get(0);
    	QuestionnaireForm form = entityForm.getForm();
    	String title = form.getName();
    	entityForms = form.getFormInstances();
    }
}
