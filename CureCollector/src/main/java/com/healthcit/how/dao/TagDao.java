/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dao;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.healthcit.how.models.Tag;

public class TagDao extends BaseJpaDao<Tag, String> {

		private static final Logger logger = LoggerFactory.getLogger(TagDao.class);

		public 	TagDao()
		{
			super(Tag.class);
		}

}
