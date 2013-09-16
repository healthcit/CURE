/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.api;

public class CureException extends Exception {

	//Parameterless Constructor
    public CureException() {}

    //Constructor that accepts a message
    public CureException(String message)
    {
       super(message);
    }
}
