/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean containsOnly( Collection collection, Object object)
	{
		if ( collection == null ) return false;
		Set duplicatesRemoved = new HashSet(collection);
		return duplicatesRemoved.size() == 1 && duplicatesRemoved.contains( object );
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Collection removeDuplicates( Collection collection )
	{
		if ( collection == null ) return collection;
		Set duplicatesRemoved = new HashSet(collection);
		return duplicatesRemoved;
	}

}
