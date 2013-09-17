/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cadsr.transformer.impl;

import gov.nih.nci.cadsr.domain.AdministeredComponent;
import gov.nih.nci.cadsr.domain.DataElement;
import gov.nih.nci.cadsr.domain.Form;
import gov.nih.nci.objectCart.domain.CartObject;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

import com.healthcit.cadsr.transformer.DataTransformer;
import com.healthcit.cadsr.utils.CADSRUtils;

/**
 * Instance of DataTransformer used to process Forms from CADSR.
 * @author oawofolu
 *
 */
public class FormDataTransformer extends DataTransformer{
	
	private static FormDataTransformer dataTransformer;
	
	private FormDataTransformer(){}
	
	public static FormDataTransformer getInstance()
	{
		if ( dataTransformer == null ) dataTransformer = new FormDataTransformer();
		return dataTransformer;
	}
	
	/**
	 * Transforms CADSR entities into the appropriate CURE FormBuilder entities
	 * @param sourceList
	 * @param searchType
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public List getProcessedEntities( List<? extends AdministeredComponent> sourceList, String searchType ){
		List transformedList = (List)CollectionUtils.collect( 
				sourceList, 
				new Transformer(){
					public Object transform( Object source ) {
						Form target = new Form();
						copyTransferrableProperties( target, ( AdministeredComponent ) source );
						return target;
					}});
		// Clear out the sourceList; this is done to deallocate memory resources as the sourceList could be very large
		CADSRUtils.clearList( sourceList );
		
		return transformedList;
	}

	/**
	 * Queries the caDSR repository for CartObjects
	 * @throws ApplicationException 
	 */
	@SuppressWarnings("rawtypes")
	public List searchCartObjects( String str ) throws ApplicationException
	{
		List results = null; 
		
		Collection<CartObject> searchCartObjectResults = getCartObjectCollectionByUserAndType(  str, CADSR_FORM_CART_TYPE );
		if ( CollectionUtils.isEmpty( searchCartObjectResults ) )
		{
			results = new ArrayList<DataElement>();
		}
		else
		{
			ArrayList<String> ids = new ArrayList<String>( searchCartObjectResults.size() );				
			for ( CartObject searchCartObjectResult : searchCartObjectResults)
			{
				ids.add( searchCartObjectResult.getNativeId() );
			}				
			StringBuffer hqlQuery = new StringBuffer("from Form where id in (").append(StringUtils.repeat("?,", ids.size()-1)).append("?)");
			results = appService.query(new HQLCriteria( hqlQuery.toString(), ids));
		}
		
		return results;
	}

}
