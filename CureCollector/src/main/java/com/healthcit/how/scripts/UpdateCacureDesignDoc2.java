/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.scripts;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.healthcit.cacure.dao.CouchDBDao;
import com.healthcit.how.scripts.utils.SystemUpdaterImpl;
import com.healthcit.how.utils.IOUtils;

@Component
public class UpdateCacureDesignDoc2  extends SystemUpdaterImpl{
	private Logger log = Logger.getLogger(UpdateCacureDesignDoc2.class);
	private CouchDBDao couchDbDao;	
	@Value("${couchDb.host}") String couchDbHost;
	@Value("${couchDb.port}") int couchDbPort;
	@Value("${couchDb.name}") String couchDbName;
	@Value("${couchDb.batch.size}") int couchDbBatchSize;
	@Value("${couchDb.bulk.batch.size}") int couchDbBulkBatchSize;
	@Value("${couchDb.design.doc}") String couchDbDesignDoc;
	@Value("${couchDb.name}") String cacureDbName;
	@Value("${couchDb.design.doc}") String cacureDesignDoc;
	@Value("${couchDb.admin.username}") String adminUserName;
	@Value("${couchDb.admin.password}") String adminPassword;

	@Override
	public void setUpDependencies() throws Exception {
		couchDbDao = new CouchDBDao();
		
		couchDbDao.setHost( couchDbHost );
		couchDbDao.setPort( couchDbPort );
		couchDbDao.setDbName( couchDbName );
		couchDbDao.setBatchSize( couchDbBatchSize );
		couchDbDao.setBulkBatchSize( couchDbBulkBatchSize );
		couchDbDao.setDesignDoc( couchDbDesignDoc );
		couchDbDao.setCacureDesignDoc( cacureDesignDoc );
		couchDbDao.setDbLoginUserName( adminUserName );
		couchDbDao.setDbLoginPassword( adminPassword );
	}

	@Override
	public void executeUpdate() throws Exception {
		String content = IOUtils.read( this.getClass().getResourceAsStream("UpdateCacureDesignDoc2.txt") );
					
		JSONObject body = JSONObject.fromObject(content);			
	
		String response = couchDbDao.updateDesignDoc( body, "caCURE" );
		
		log.info("Response: " + response );
		
		if ( !response.toLowerCase().contains("\"ok\"") ) 
		{
			throw new Exception( response );
		}
	}
}
