/*******************************************************************************
 * Copyright (c) 2013 HealthCare It, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-Clause license
 * which accompanies this distribution, and is available at
 * http://directory.fsf.org/wiki/License:BSD_3Clause
 * 
 * Contributors:
 *     HealthCare It, Inc - initial API and implementation
 ******************************************************************************/
package com.healthcit.analytics.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

public class DateUtils {

	public static String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";
	
	public static String GMT = "GMT";
	
	public static Date getDateValue( String str )
	{
		if ( str == null ) return null;
		
		Date dateValue = null;
		
		try
		{
			dateValue = new SimpleDateFormat( DEFAULT_DATE_FORMAT ).parse( str );
		}
		catch( ParseException ex )
		{
		}
		
		return dateValue;
	}
	
	@SuppressWarnings("deprecation")
	public static GregorianCalendar getGregorianCalendar( String str )
	{
		GregorianCalendar calendar = null;
		
		if ( str == null ) return null;
		
		Date date = getDateValue( str );
		
		if ( date != null )
		{		
			calendar = new GregorianCalendar( date.getYear() + 1900, date.getMonth(), date.getDate() );
		
			calendar.setTimeZone( TimeZone.getTimeZone( GMT ));
		}
		
		return calendar;
	}
	
	public static String formatDate( Date date )
	{
		return formatDate( date, DEFAULT_DATE_FORMAT );
	}
	
	public static String formatDate( Date date, String format )
	{
		return new SimpleDateFormat( format ).format( date );
	}
}
