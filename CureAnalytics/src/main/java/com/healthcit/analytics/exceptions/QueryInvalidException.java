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
package com.healthcit.analytics.exceptions;

import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;

/**
 * Custom exception thrown if a query is determined to be invalid.
 * @author Oawofolu
 *
 */
public class QueryInvalidException extends DataSourceException {

	private static final long serialVersionUID = 1589429989617261346L;
	
	public QueryInvalidException( String message ) {
		super( ReasonType.INTERNAL_ERROR, message );
	}

	public QueryInvalidException(ReasonType reasonType, String message) {
		super( reasonType, message );
	}

}
