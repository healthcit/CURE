/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.model;

import junit.framework.TestCase;

import com.healthcit.cacure.utils.GetterAndSetterTester;

public class QuestionTest extends TestCase {

    private GetterAndSetterTester tester;

    public void setUp(){
        tester = new GetterAndSetterTester();
    }

    /**
     * Test the getters and setters of a the given class.
     * Instantiation is left top the tester.
     *
     */
    public void testAllSettersAndGettersClass(){
        tester.testClass(Question.class);
    }
}
