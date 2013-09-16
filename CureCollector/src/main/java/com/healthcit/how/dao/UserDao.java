/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dao;

import java.util.List;

import javax.persistence.Query;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.healthcit.how.models.User;

public class UserDao extends BaseJpaDao< User, Long > {
	
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger( UserDao.class );
	
	/* HQL Queries */
	private static final String FIND_EMAIL_HQL = "select u from User u where u.emailAddress = :email";
	private static final String CHECK_DUPLICATE_EMAIL_HQL = "select count(emailAddress) from User u where u.emailAddress = :email";
	private static final String CHECK_DUPLICATE_USERNAME_HQL = "select count(username) from User u where u.username = :username";
	private static final String FIND_ALL_EMAILS_HQL = "from User";
	private static final String FIND_USERNAME = "select u from User u where u.username = :username";

	public UserDao()
	{
		super(User.class);
	}
	

	
	public User findByName(String uName){
		Query query = this.em.createQuery(FIND_USERNAME);
		query.setParameter( "username", uName );
		@SuppressWarnings("rawtypes")
		List results = query.getResultList();
		if (results.size() == 1) {
        	User u = ( User ) results.get(0);
        	return u;
        }
		
		return null;	
	}
	
	public User findByEmail( String email ) {
		Query query = em.createQuery( FIND_EMAIL_HQL );
		query.setParameter( "email", email );
		User u = ( User ) query.getResultList().get( 0 );
		return u;
	}
	
	public boolean doesEmailExist( String email ) {
		Query query = em.createQuery( CHECK_DUPLICATE_EMAIL_HQL );
		query.setParameter( "email", email );
		Long count = ( Long ) query.getResultList().get( 0 );
		return count >= 1;
	}
	
	public boolean doesUsernameExist( String username ) {
		Query query = em.createQuery( CHECK_DUPLICATE_USERNAME_HQL );
		query.setParameter( "username", username );
		Long count = ( Long ) query.getResultList().get( 0 );
		return count >= 1;
	}
	
	public List<User> findAllUsers() {
		Query query = em.createQuery( FIND_ALL_EMAILS_HQL );
		@SuppressWarnings("unchecked") List<User> users = ( List<User> ) query.getResultList();
		return users;
	}
}
