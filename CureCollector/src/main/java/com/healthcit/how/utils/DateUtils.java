/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtils 
{
	private static DatatypeFactory dataTypeFactory = null;

	public static XMLGregorianCalendar getXMLGregorianCalendar( Date date )
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime( date );
		return getDatatypeFactory().newXMLGregorianCalendar( cal );
	}
	
	public static DatatypeFactory getDatatypeFactory()
	{
		if ( dataTypeFactory == null )
		{
			try
			{			
				dataTypeFactory = DatatypeFactory.newInstance();
			}
			
			catch( DatatypeConfigurationException ex )
			{
			}
		}
		return dataTypeFactory;
	}
}
