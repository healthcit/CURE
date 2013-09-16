/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.businessdelegates;

import org.apache.commons.lang.StringUtils;

public class DuplicateResultBean {
	private final DuplicateResultType result;
	private final String[] shortNames;

	public DuplicateResultBean(DuplicateResultType result, String[] shortNames) {
		super();
		this.result = result;
		this.shortNames = shortNames;
	}

	public DuplicateResultType getResult() {
		return result;
	}

	public String[] getShortNames() {
		return shortNames;
	}

	@Override
	public String toString() {
		return "DuplicateResultBean [result=" + result + ", shortNames="
				+ StringUtils.join(shortNames, ",") + "]";
	}
}
