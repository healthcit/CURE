/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * Contains utility methods which may be used for Java reflective functions.
 * @author Oawofolu
 *
 */
public class ReflectionUtils {
	private static Logger log = Logger.getLogger( ReflectionUtils.class );
	/**
	 * Generates the Method object associated with the given method name.
	 * @param target
	 * @param methodName
	 * @param methodArgTypes
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Method getMethod ( Object target, String methodName, Class[] methodArgTypes ) {
		Method method = null;
		try {
			if ( target == null ) {
				log.error( "The method "+methodName+" cannot be invoked on a null target");
			}
			
			else if ( methodArgTypes == null ) {
				log.error( "The method "+methodName+" cannot be invoked with null method arguments");
			}
			
			else {
				method = target.getClass().getMethod( methodName, methodArgTypes );
			}
		}
		catch ( NoSuchMethodException nsme ) {
			log.error( "The method "+methodName+" does not exist in the class " + target.getClass() + ", or the argument types might be wrong" );
			log.error( nsme.getMessage() );
		} 
		return method;
		
	}
	
	/**
	 * Invokes the specified Method object on the target.
	 * @param target
	 * @param method
	 * @param methodArgs
	 */
	public static void invokeMethod( Object target, Method method, Object[] methodArgs ) {
		try{
			method.invoke( target, methodArgs );
		}catch (IllegalAccessException e) {
			log.error( e.getMessage() );
		} 
		catch (InvocationTargetException e) {
			log.error( e.getMessage() );
		}
		
	}
}
