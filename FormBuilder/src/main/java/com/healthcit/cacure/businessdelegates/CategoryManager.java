/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.businessdelegates;

import java.util.List;

//import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.dao.CategoryDao;
import com.healthcit.cacure.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryManager {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CategoryManager.class);
	
	@Autowired
	private CategoryDao categoryDao;
	
	public Category getCategoryById(Long id) {
		return categoryDao.getById(id);
	}
	
	public List<Category> getAllCategories() {
		return categoryDao.list();
	}
	
	public List<Category> getLibraryQuestionsCategories() {
		return categoryDao.getLibraryQuestionsCategories();
	}

	public Category saveCategory(Category entity) {
		return categoryDao.save(entity);
	}

	public List<Category> getCategoriesByName(String name)
	{
		return categoryDao.getCategoriesByName(name);
	}
}
