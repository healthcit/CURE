/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtils {
	
	@SuppressWarnings("unchecked")
	/**
	 * Adds an object to a collection.
	 * @return the collection with the object added.
	 */
	public static Collection addObject(Collection coll, Object obj) {
		org.apache.commons.collections.CollectionUtils.addIgnoreNull(coll, obj);
		return coll;
	}
	
	public static <P extends Collection<BigInteger>> List<Long> convertAllElementsToLong(P bigIntegers) {
		if(bigIntegers == null) {
			return null;
		}
		try {
			ArrayList<Long> results = new ArrayList<Long>();
			for (BigInteger bigInteger : bigIntegers) {
				results.add(bigInteger.longValue());
			}
			return results;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
