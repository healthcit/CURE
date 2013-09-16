/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;

public class NumberUtils {

	public static Long parseLong( String str ) {
		Long number = null;
		
		try {
			number = org.apache.commons.lang.math.NumberUtils.createLong( str );
		}catch ( NumberFormatException nfe ){ }
		
		return number;
	}
	
	public static byte[] UUIDtoByteArray( UUID uuid ) {
		if ( uuid == null ) return null;
		
		byte[] mostSignificantBits  = BigInteger.valueOf( uuid.getMostSignificantBits() ).toByteArray();
		byte[] leastSignificantBits = BigInteger.valueOf( uuid.getLeastSignificantBits() ).toByteArray();
		
		byte[] newArray = new byte[ mostSignificantBits.length + leastSignificantBits.length ];
		
		System.arraycopy( mostSignificantBits, 0, newArray, 0, mostSignificantBits.length );
		System.arraycopy( leastSignificantBits, 0, newArray, mostSignificantBits.length, leastSignificantBits.length );
		
		return newArray;
	}
	
	public static UUID byteArrayToUUID( byte[] byteArray ) {
		if ( byteArray == null ) return null;
		
		byte[] mostSignificantBits = ArrayUtils.subarray(byteArray, 0, (int)Math.floor( byteArray.length/2 ) );
		byte[] leastSignificantBits = ArrayUtils.subarray(byteArray, (int)Math.ceil( byteArray.length/2 ), byteArray.length );
		
		long mostSignificantLong = new BigInteger( mostSignificantBits ).longValue();
		long leastSignificantLong = new BigInteger( leastSignificantBits ).longValue();
		
		return new UUID( mostSignificantLong, leastSignificantLong );
	}
	
	public static Long zeroIfNull( Long number ) {
		return ( number == null ? new Long( 0 ) : number );
	}
	
	public static Long defaultIfNull( Long number, Long defaultNumber ) {
		return ( number == null ? defaultNumber : number );
	}
	
	public static boolean containsNumber(Collection<Number> collection, Number number)
	{
		Iterator<Number> iterator = collection.iterator();
		while ( iterator.hasNext() )
		{
			Number comparable = iterator.next();
			if ( comparable == null ) return comparable == number;
			if ( comparable.doubleValue() == number.doubleValue() ) return true;
		}
		return false;
	}
}
