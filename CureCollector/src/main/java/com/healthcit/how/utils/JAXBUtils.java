/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

public class JAXBUtils {

	private static JAXBContext moduleMetadataContext = null;
	
	private static JAXBContext xformsContext = null;
	
	private static JAXBContext gatewayContext = null;
		
	private static final Logger log = Logger.getLogger( JAXBUtils.class );
	
	
	

	public static JAXBContext getModuleMetadataContext() 
	{
		if ( moduleMetadataContext == null ) updateModuleMetadataContext();
		
		return moduleMetadataContext;
	}
	
	public static JAXBContext getXFormsContext() 
	{
		if ( xformsContext == null ) updateXFormsContext();
		
		return xformsContext;
	}
	
	public static JAXBContext getGatewayContext()
	{
		if ( gatewayContext == null ) updateGatewayContext();
		
		return gatewayContext;
	}
	
	public static void updateModuleMetadataContext() 
	{
		updateContext( Constants.MODULE_METADATA_CONTEXT_PATH );
	}
	
	public static void updateXFormsContext() 
	{
		updateContext( Constants.XFORMS_CONTEXT_PATH );
	}	
	
	public static void updateGatewayContext()
	{
		updateContext( Constants.GATEWAY_METADATA_CONTEXT_PATH );
	}
	
	
	
	
	private static void updateContext( String contextPath ) 
	{
		try 
		{
			JAXBContext context = JAXBContext.newInstance( contextPath );
			
			if ( Constants.MODULE_METADATA_CONTEXT_PATH.equals( contextPath ) )
			{
				moduleMetadataContext = context;
			}
			
			if ( Constants.XFORMS_CONTEXT_PATH.equals( contextPath ) )
			{
				xformsContext = context;
			}
			
			if ( Constants.GATEWAY_METADATA_CONTEXT_PATH.equals( contextPath ) )
			{
				gatewayContext = context;
			}
			
		} 
		catch (JAXBException e) 
		{
			log.error("ERROR: Could not create the JAXB Context for the context path '" + contextPath + "'");
			
			e.printStackTrace();
		}
	}
	
}
