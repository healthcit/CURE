/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.test;

import java.lang.annotation.*;

/**
 * Annotation which indicates that a test class or test method should load a
 * data set, using dbunit behind the scenes, before executing the test.
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DataSet {

  String value() default "";

  String setupOperation() default "CLEAN_INSERT";

  String teardownOperation() default "NONE";
}
