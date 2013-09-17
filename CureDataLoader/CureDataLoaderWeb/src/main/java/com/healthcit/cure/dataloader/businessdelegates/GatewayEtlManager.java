package com.healthcit.cure.dataloader.businessdelegates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.healthcit.cure.dataloader.dao.GatewayEtlDao;

public class GatewayEtlManager {

	@Autowired
	GatewayEtlDao gatewayDao;
	
	@Value("${cure.etlSqlFile}")
	String etlSqlFile;
	
	public void updateEtlDB() throws Exception
	{
		gatewayDao.executeSql( etlSqlFile );
	}
	
}
