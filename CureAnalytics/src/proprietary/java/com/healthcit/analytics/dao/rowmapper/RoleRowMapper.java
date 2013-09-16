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

import org.springframework.jdbc.core.RowMapper;

import com.healthcit.analytics.model.Role;

/**
 * <code>RowMapper</code> implementation for building <code>Role</code> objects.
 * 
 * @author Stanilsav Sedavnikh
 *
 */
public class RoleRowMapper implements RowMapper<Role>{

	@Override
	public Role mapRow(ResultSet rs, int index) throws SQLException {
		Long id = rs.getLong("id");
		String name = rs.getString("name");
		String displayName = rs.getString("display_name");
		return new Role(id, name, displayName);
	}

}
