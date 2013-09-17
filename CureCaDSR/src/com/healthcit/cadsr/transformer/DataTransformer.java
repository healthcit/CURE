/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cadsr.transformer;
import gov.nih.nci.cadsr.domain.AdministeredComponent;
import gov.nih.nci.cadsr.domain.DataElement;
import gov.nih.nci.cadsr.domain.Question;
import gov.nih.nci.objectCart.client.ObjectCartClient;
import gov.nih.nci.objectCart.client.ObjectCartException;
import gov.nih.nci.objectCart.domain.CartObject;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.healthcit.cadsr.transformer.impl.CDEDataTransformer;
import com.healthcit.cadsr.transformer.impl.FormDataTransformer;

/**
 * Handles the extraction and transformation of data from the caDSR repository into FormBuilder-compatible entities.
 * @author oawofolu
 *
 */
public abstract class DataTransformer {
	
	public static Logger log = Logger.getLogger( DataTransformer.class );
	private static String referenceDocAlias = "rdc";
	private static String docTextProperty = "rdc.doctext";
	public static final String CADSR_QUESTION_DELETED_INDICATOR_PROPERTY = "deletedIndicator";
	public static final String CADSR_QUESTION_LATEST_VERSION_PROPERTY = "latestVersionIndicator";
	public static final String CADSR_QUESTION_LONG_NAME_PROPERTY = "longName";
	public static final String CADSR_REFERENCE_DOCUMENT_COLLECTION_PROPERTY = "referenceDocumentCollection";
	public static final String CADSR_VALUE_DOMAIN_PERMISSIBLE_VALUES_PROPERTY = "valueDomainPermissibleValueCollection";
	public static final String CADSR_VALUE_DOMAIN_PROPERTY = "valueDomain";
	public static final String CADSR_PREFFERD_QUESTION_TEXT = "Preferred Question Text";
	public static final String CADSR_CDE_CART_TYPE = "cdeCart";
	public static final String CADSR_FORM_CART_TYPE="formCart";
	public static final String SPLITTER = ",";
	private static enum CADSRSearchType { SINGLEIDSEARCH, MULTIIDSEARCH, TEXTSEARCH, SINGLEPUBLICIDSEARCH, CDECARTUSERSEARCH, FORMCARTUSERSEARCH };
	private static ObjectCartClient CADSR_OBJECT_CART_CLIENT = null;
	public static ApplicationService appService;
	
	// Initialize ApplicationService
	static 
	{
		try {
			 appService = ApplicationServiceProvider.getApplicationServiceFromUrl("http://cadsrapi.nci.nih.gov/cadsrapi40/");
		} catch ( Exception ex ) {
			log.error( "Could not instantiate the application service...");
			ex.printStackTrace();
		}
	}
	
	// Static factory instance method for DataTransformer class
	public static DataTransformer getInstance( String searchType )
	{
		// Initialize the DataTransformer
		switch( CADSRSearchType.valueOf( searchType ))
		{
			case FORMCARTUSERSEARCH: 
			{
				return FormDataTransformer.getInstance();
			}
			default:
			{
				return CDEDataTransformer.getInstance();
			}
		}
	}
			
	/**
	 * Copies all non-proxied properties from the source to the target.
	 * @param target
	 * @param source
	 * @return
	 * 
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public Object copyTransferrableProperties(AdministeredComponent target, AdministeredComponent source) {
		try {
			Iterator<String> propertyIterator = PropertyUtils.describe( target ).keySet().iterator();
			Map              sourceValuesMap  = PropertyUtils.describe( source );
			while ( propertyIterator.hasNext() ) {
				String property = propertyIterator.next();
				Object value    = sourceValuesMap.get( property );
				if ( PropertyUtils.isWriteable( target, property ) && 
				     Hibernate.isInitialized( value ) && 
				     !(isCADSREntity( value )) &&
				     !(value instanceof Collection)) {
					PropertyUtils.setProperty( target, property, value );
				}
			}			
			return target;
		} catch (Exception ex){
			log.error( "Error while copying properties: "+ ex.getMessage() );
		}
		return target;
	}
	
	/**
	 * Queries the caDSR repository
	 * @return
	 */
	@SuppressWarnings({"rawtypes"})
	public List search( String str, String searchType ) {
		List results = null;
		DataTransformer dataTransformer = getInstance(searchType);
		try {
			CADSRSearchType st = CADSRSearchType.valueOf( searchType );
			
			// Option: Search by Single ID
			if ( st.equals( CADSRSearchType.SINGLEIDSEARCH ) ) 
			{ 
				DataElement searchQuestion  = new DataElement();
				searchQuestion.setId(str);
				results = appService.search( DataElement.class, searchQuestion );
			}
			
			// Option: Search by Text
			if ( st.equals( CADSRSearchType.TEXTSEARCH )) 
			{ 
				if ( str != null && str.trim().length() > 0 ) {
					// Set up the search criteria
					DetachedCriteria searchCriteria = DetachedCriteria.forClass( DataElement.class );
					searchCriteria.add         			( Restrictions.eq    ( CADSR_QUESTION_DELETED_INDICATOR_PROPERTY, "No" ));
					searchCriteria.add         			( Restrictions.eq    ( CADSR_QUESTION_LATEST_VERSION_PROPERTY, "Yes" ));
					searchCriteria.add         			( Restrictions.ilike ( CADSR_QUESTION_LONG_NAME_PROPERTY, str, MatchMode.ANYWHERE ));
					searchCriteria.createAlias 			( CADSR_REFERENCE_DOCUMENT_COLLECTION_PROPERTY, referenceDocAlias );
					searchCriteria.add         			( Restrictions.ilike ( docTextProperty, str, MatchMode.ANYWHERE ));
					searchCriteria.add         			( Restrictions.eq    ( "rdc.type", CADSR_PREFFERD_QUESTION_TEXT ) );
					searchCriteria.setResultTransformer ( CriteriaSpecification.DISTINCT_ROOT_ENTITY );
					results = appService.query( searchCriteria );
				}
			}
			
			// Option: Search by Multiple IDs
			if ( st.equals( CADSRSearchType.MULTIIDSEARCH ) ) 
			{ 
				String[] array = str.split( SPLITTER );
				
				StringBuffer hqlQuery = new StringBuffer("from DataElement where id in ");
				String[] symbols = new String[]{"(?", ",?", ")"};
				for ( int i = 0; i <= array.length; ++i ) {
					if ( i == 0 ) hqlQuery.append( symbols[0] );
					else if ( i < array.length ) hqlQuery.append( symbols[1] );
					else hqlQuery.append( symbols[2] );
				}
				
				results = appService.query(new HQLCriteria( hqlQuery.toString(), Arrays.asList(array)));
			}
			
			// Option: Search by Single Public ID
			if ( st.equals( CADSRSearchType.SINGLEPUBLICIDSEARCH ) ) 
			{ 
				DataElement searchQuestion  = new DataElement();
				searchQuestion.setPublicID(new Long(str));
				results = appService.search( DataElement.class, searchQuestion );
			}
			
			// Option: Search by CDE Cart User ID
			if ( st.equals( CADSRSearchType.CDECARTUSERSEARCH ) ) 
			{ 				
				results = dataTransformer.searchCartObjects(  str );
			}
			
			// Option: Search by Form Cart User ID
			if ( st.equals( CADSRSearchType.FORMCARTUSERSEARCH ) )
			{
				results = dataTransformer.searchCartObjects(  str );
			}
			
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
		return results;
	}
	
	/**
	 * Checks if this entity is a CADSR entity.
	 * @param entity
	 * @return
	 */
	protected boolean isCADSREntity( Object entity ){
		return entity == null ? false : 
			entity.getClass().getPackage().getName().equalsIgnoreCase(Question.class.getPackage().getName());
	}
	
	/**
	 * Returns the Object Cart service used to query the FormCart Object repository.
	 * @return
	 * @throws ObjectCartException
	 */
	protected ObjectCartClient getObjectCartClientService() throws ObjectCartException {
		if ( CADSR_OBJECT_CART_CLIENT == null ) CADSR_OBJECT_CART_CLIENT = new ObjectCartClient();		
		return CADSR_OBJECT_CART_CLIENT;
	}
	
	/**
	 * Given a user and cart type, returns a collection of CartObjects from the FormCart repository.
	 * @param user
	 * @param type
	 * @return
	 */
	protected Collection<CartObject> getCartObjectCollectionByUserAndType( String user, String type ) {
		Collection<CartObject> collection = null;
		try{
			collection = getObjectCartClientService().retrieveCart( user, type ).getCartObjectCollection();
		}catch(ObjectCartException oce){
			log.error("Could not retrieve cart with user " + user + " and type " + type + ": ");
			log.error( oce.getMessage() + " : " + oce.getCause().getMessage() );
		}
		return collection;
	}
	/**
	 *  Transforms CADSR entities into the appropriate CURE FormBuilder entities
	 * @param sourceList
	 * @param searchType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public abstract List getProcessedEntities( List<? extends AdministeredComponent> sourceList, String searchType );
	/**
	 * Queries the caDSR repository for CartObjects
	 * @throws ApplicationException 
	 */
	@SuppressWarnings("rawtypes")
	public abstract List searchCartObjects( String str ) throws ApplicationException;
}
