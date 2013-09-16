/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.model.breadcrumb;

import com.healthcit.cacure.utils.Constants;

/**
 * Breadcrumb for displaying link to Manage Library page.
 *
 */
public class ManageLibraryBreadCrumb extends HomeBreadCrumb {
	
	@Override
	public Link getLink() {
		Link link = super.getLink();
		Link currentLink = new Link("Manage Library", Constants.LIBRARY_MANAGE_URI, this);
		this.addLastChild(link, currentLink);
		return link;
	}
}
