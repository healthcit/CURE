/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dao;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/app-config.xml",
		                            "file:src/main/webapp/WEB-INF/spring/dao-config.xml"
//		                            "file:/dev/Workspaces/HCIT/caCure/trunk/src/main/webapp/WEB-INF/spring/mvc-config.xml"
		                            })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class ModuleDaoTest
{

	@Autowired
	ModuleDao moduleDao;

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGetEntityForForm()
	{
		String formId = "1c396c68-3efd-42f5-82fc-209e073e4ece";
		List<String> results = moduleDao.getOwnerForForm(formId);
		assertNotNull(results);
	}
	
	@Test
	public void testBulkAddFormToEntity()
	{
		String formId = "a06ba57f-de45-4609-b627-724395584447";
		moduleDao.bulkAddFormToSharingGroup(formId);
		assert(true);
	}
	
	@Test
	public void testGetEntityForModule()
	{
		String moduleId = "9001";
		List<String> results = moduleDao.getOwnerForModule(moduleId);
		System.out.println ("the size of results " + results.size()) ;
		assertNotNull(results);
	}
	
	@Test
	public void testBulkAddModuleToEntity()
	{
		String moduleId = "9903";
		moduleDao.bulkAddModuleToSharingGroup(moduleId);
		assert(true);
	}

}
