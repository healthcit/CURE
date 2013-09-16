/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.scripts.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import com.healthcit.how.utils.IOUtils;

/**
 * Central class which handles both database schema and Java-based system updates upon deployment.
 * 
 * Updates are tracked via a DB table "sys_variables". This table consists of a database version-tracking column ("schema_version")
 * and a system version-tracking column ("system_update_version"). These columns are numeric values which represent the current database 
 * and system update versions respectively. 
 * 
 * When the version number is 0, it implies that no scripts have been executed for this particular instance of the application.
 * Because of this, the default values of the columns has been set to 0, and updates are assumed to start from version 1. 
 * Upon completion of each update, the columns are incremented by 1.
 * 
 * The table must be generated manually. The script to generate this table is included under the  directory /src/main/db.
 * 
 * To perform database updates:
 * 
 * 1) Ensure that the "sys_variables" table has been generated correctly.
 * 
 * 2) Create a SQL script X.up.sql under the /src/main/db/uppers folder, where "X" is a number which represents the next DB version
 * (for example: 2.up.sql is the update script for DB version "2".)     
 * 
 * 3) Deploy the application.
 * 
 * To perform (java-based) system updates:
 * 
 * 1) Ensure that the "sys_variables" table has been generated correctly.
 * 
 * 2) Create a Java class which will execute the system update(s). It must meet the following requirements:
 * 		a) It must extend the class com.healthcit.how.scripts.utils.SystemUpdaterImpl.
 * 
 *  	b) It must be annotated as a @Component.
 *  
 *  	c) It must be created under the package com.healthcit.how.scripts.
 *  
 * 3) Create a new text file, X.up.txt, containing the name of the class created in Step 2.
 *    Create the file under the /src/main/sys/uppers folder, where "X" is a number representing the next DB version
 *    (for example: 2.up.txt is the update script for system update version "2".)     
 * 
 * 4) Deploy the application.
 * 	  
 * 
 * 
 * @author oawofolu
 *
 */

@Component
public class ApplicationUpdater implements ApplicationContextAware {

	private static Logger log = LoggerFactory.getLogger(ApplicationUpdater.class);
	private static final String PATH_TO_DB_UPPERS_FOLDER = "/db";
	private static final String PATH_TO_SYS_UPPERS_FOLDER = "/sys";
	private static final String JAVA_SCRIPTS_PACKAGE_NAME = "com.healthcit.how.scripts";
	@Autowired private DataSource dataSource;
	private ApplicationContext applicationContext;
	private Connection connection;
	private long dbVersion = -1;
	private long systemVersion = -1;
	private enum Script { DB, JAVA };

	@PostConstruct
	public void init() throws Exception 
	{
		preProcessUpdates();
		
		doDbUpdates();
		
		doJavaUpdates();
	}
	
	/**
	 * PRE-UPDATE PROCESSOR
	 */
	public void preProcessUpdates()
	{
		connection = DataSourceUtils.getConnection(dataSource);
		try {
			connection.setAutoCommit(false);
			
			Statement statement = connection.createStatement();
			
			ResultSet rs = statement.executeQuery("select schema_version, system_update_version from sys_variables limit 1;");
			
			rs.next();
			
			dbVersion = rs.getLong(1);
			
			systemVersion = rs.getLong(2);
					
		} catch( SQLException ex ) {
			ex.printStackTrace();
		}
		
		finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}
	
	/**
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * DB UPDATES
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */
		
	public void doDbUpdates() throws Exception
	{
		if ( dbVersion < 0 ) 
			throw new Exception( 
					"ERROR while performing system updates: Could not obtain the current DB schema update version.\n" +
					"Please ensure that the sys_variables table exists and is populated appropriately.");
		
		connection = DataSourceUtils.getConnection(dataSource);
		
		connection.setAutoCommit(false);
		
		Statement statement = connection.createStatement();
		
		try 
		{
			for (long v = dbVersion + 1; checkExistsUpdateScript( v, Script.DB ); v++) 
			{
				String statements = getStatementsFor(v);
				
				log.info("Updating schema to " + v + " version...");
				
				statement = connection.createStatement();
				
				statement.execute(statements);
				
				statement.executeUpdate("update sys_variables set schema_version = " + v + ";");
				
				connection.commit();
				
				log.info("DB Update ----- OK");
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			
			connection.rollback();
			
			throw e;
		} finally 
		{
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	/**
	 * Returns sql statements from upper file for passed version.
	 * If correspondent upper file is not found it returns null;
	 * @param version
	 * @return
	 * @throws IOException
	 */
	private String getStatementsFor(final long version) throws IOException {
		return IOUtils.readClassPathResource(PATH_TO_DB_UPPERS_FOLDER + "/" + version + ".up.sql");
	}
	/**
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * END DB UPDATES
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */
	
	
	/**
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * JAVA UPDATES
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */
	public void doJavaUpdates() throws Exception
	{
		if ( systemVersion < 0 ) throw new Exception( "ERROR while performing system updates: Could not obtain the current system update version" );
		
		connection = DataSourceUtils.getConnection(dataSource);
		
		connection.setAutoCommit(false);
		
		Statement statement = connection.createStatement();
		
		try 
		{
			for (long v = systemVersion + 1; checkExistsUpdateScript( v, Script.JAVA ); v++) 
			{
				
				log.info("Updating system to " + v + " version...");
				
				invokeBeanFor(v);
				
				statement = connection.createStatement();
				
				statement.executeUpdate("update sys_variables set system_update_version = " + v + ";");
				
				connection.commit();
				
				log.info("System Update --- OK");
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			
			connection.rollback();
			
			throw e;
		} 
		finally 
		{
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}
	
	public void invokeBeanFor(final long version) throws Exception
	{
		String content = IOUtils.readClassPathResource(PATH_TO_SYS_UPPERS_FOLDER + "/" + version + ".up.txt");
				
		String[] beanNames = StringUtils.split( content );
		
		for ( String beanName : beanNames )
		{
			try
			{
				@SuppressWarnings("rawtypes")
				Class beanClass = Class.forName( JAVA_SCRIPTS_PACKAGE_NAME + "." + beanName );
				
				@SuppressWarnings("unchecked")
				SystemUpdaterImpl bean = ( SystemUpdaterImpl ) applicationContext.getBean( beanClass );
				
				bean.init();
			}
			
			catch( ClassNotFoundException ex )
			{
				log.error( "ERROR: No class named '" + beanName + "' was found in the " + JAVA_SCRIPTS_PACKAGE_NAME + " package");
				
				throw ex;
			}
			
			catch( NullPointerException ex )
			{
				log.error(" ERROR: No bean named '" + beanName + " exists in the Spring context" );
				
				throw new Exception(ex);
			}
			
			catch ( ClassCastException ex )
			{
				log.error( "ERROR: The bean '" + beanName + "' must extend the SystemUpdaterImpl class" );
				
				throw new Exception( ex );
			}
			
			catch( Exception ex )
			{
				log.error( "ERROR: The bean '" + beanName + "' could not be invoked successfully" );
				
				throw new Exception( ex );
			}
		}
	}
	
	private boolean checkExistsUpdateScript( final long version, Script type )
	{
		
		boolean exists = false;
		try
		{
			String resource = null;
			
			if ( type.equals( Script.DB ) ) resource = PATH_TO_DB_UPPERS_FOLDER + "/" + version + ".up.sql";
			
			else if ( type.equals( Script.JAVA ) ) resource = PATH_TO_SYS_UPPERS_FOLDER + "/" + version + ".up.txt";
			
			String content = IOUtils.readClassPathResource( resource );
			
			exists = StringUtils.isNotEmpty( content );
		}
		catch(IOException ex)
		{
		}
			
		return exists;
	}
	/**
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * END JAVA UPDATES
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */
		
	/**
	 * GETTERS AND SETTERS
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
