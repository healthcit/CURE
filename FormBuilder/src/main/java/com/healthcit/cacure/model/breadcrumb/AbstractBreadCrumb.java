/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.model.breadcrumb;

/**
 * Common class for breadcrumbs
 *
 */
public abstract class AbstractBreadCrumb implements BreadCrumb{

	/**
	 * Adds <code>childLink</code> as a last child in <code>parentLink</code> chain.
	 * 
	 * @param parentLink - link the child must be added to
	 * @param childLink - child link to add
	 */
	protected void addLastChild(Link parentLink, Link childLink)
	{
		while(parentLink.getChildLink() != null)
		{
			parentLink = parentLink.getChildLink();
		}
		parentLink.setChildLink(childLink);
	}
	
	protected Link getLastChild(Link parentLink)
	{
		while(parentLink.getChildLink() != null)
		{
			parentLink = parentLink.getChildLink();
		}
		return parentLink;
	}
}
