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
package com.healthcit.analytics.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.healthcit.analytics.model.User;

/**
 * <code>RowMapper</code> implementation for building <code>User</code> objects.
 *
 * @author Stanislav Sedavnikh
 *
 */
public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int index) throws SQLException {
		Long id = rs.getLong("id");
		String userName = rs.getString("username");
		String password = rs.getString("password");
		Date creationDate = rs.getDate("created_date");
		String email = rs.getString("email_addr");
		return new User(id, userName, password, email, creationDate);
	}

}
