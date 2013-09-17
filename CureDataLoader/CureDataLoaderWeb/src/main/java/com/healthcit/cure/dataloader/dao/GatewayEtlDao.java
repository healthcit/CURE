package com.healthcit.cure.dataloader.dao;

import java.io.File;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.healthcit.cure.dataloader.utils.IOUtils;

public class GatewayEtlDao {
	
	private static Logger logger = LoggerFactory.getLogger( GatewayEtlDao.class );
	
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	public void executeSql(String sqlFileName)
	throws Exception
	{
		File sqlFile = new File( sqlFileName );
		
		String sql = IOUtils.readFileContent( sqlFile );
		
		logger.debug( "SQL to execute: " + StringUtils.defaultIfEmpty( sql, "" ) );
		
		jdbcTemplate.execute(sql);
	}
}
