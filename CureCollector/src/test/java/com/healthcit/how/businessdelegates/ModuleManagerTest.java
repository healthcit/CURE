/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.jpa.AbstractJpaTests;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.healthcit.how.dao.CoreEntityDao;
import com.healthcit.how.models.CoreEntity;
import com.healthcit.how.models.SharingGroupFormInstance;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"file:src/main/webapp/WEB-INF/spring/app-config.xml",
		"file:src/main/webapp/WEB-INF/spring/dao-config.xml"
//		"file:src/main/webapp/WEB-INF/spring/mvc-config.xml"
		                           })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class ModuleManagerTest {

	@Autowired
	ModuleManager moduleManager;

	@Autowired
	CoreEntityManager coreEntityManager;

	@Autowired
	EntityManagerFactory emf;

	@Autowired
	SharedEntityManagerBean em;

	protected Session session;


	@Before
    public void setUp() {
//		EntityManagerFactory emf = (EntityManagerFactory)context.getBean("entityManagerFactory");
		EntityManager em = emf.createEntityManager();
		TransactionSynchronizationManager.bindResource(emf , new EntityManagerHolder(em));
    }

    @After
    public void tearDown() throws Exception {
    	TransactionSynchronizationManager.unbindResourceIfPossible(emf);
//        TransactionSynchronizationManager.unbindResource(this.sessionFactory);
//        SessionFactoryUtils.releaseSession(this.session, this.sessionFactory);
    }

//	@Test
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void testAddFormsToEntity() throws Throwable
	{
		try
		{
			//Following core will add all existing entities to new modules and forms.
//			coreEntityManager.addCoreEntity("e8d02385-02c5-4109-b9b6-0549a5dd4c5c");
//			moduleManager.addModulesToEntity("e8d02385-02c5-4109-b9b6-0549a5dd4c5c");
			moduleManager.addFormsToEntityJPA("e8d02385-02c5-4109-b9b6-0549a5dd4c5c");

			assertTrue(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	@Test
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void testGetVisibleForms() throws Throwable
	{
		try
		{
			//Following core will add all existing entities to new modules and forms.
			LinkedList<QuestionnaireForm> forms = moduleManager.getVisibleFormsByForm("8b8bd0bf-b92c-49c6-bef3-9dde0af64764", "b87d7b9d-95a8-4b05-93fb-6ed759cb64c4");

			QuestionnaireForm submittedForm = forms.get(0);
			List<SharingGroupFormInstance> entityForms = submittedForm.getFormInstances();
			SharingGroupFormInstance form = entityForms.get(0);
			assertTrue(entityForms.size() == 1);
			assertTrue(submittedForm.getStatus() == FormStatus.SUBMITTED);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			throw e;
		}
	}


}
