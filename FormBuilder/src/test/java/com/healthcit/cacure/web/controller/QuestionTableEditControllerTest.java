/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.web.controller;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import com.healthcit.cacure.web.controller.question.TableElementEditController;


public class QuestionTableEditControllerTest {
	private TableElementEditController questionTableEditController;

	private static final String ATTR_VALIDATION_ERR = "validationErr";

	@Before
	public void setUp() {
		questionTableEditController = new TableElementEditController();
	}

	@Test
	public void testShowForms() {
		System.out.println("starting jason");
		JSONObject jsonDoc = new JSONObject();
		jsonDoc.put("firstName","John");
		jsonDoc.put("lastName","Doe");
		jsonDoc.put("age","23");

		System.out.println("jsonDoc: " + jsonDoc.toString());
	}


}


