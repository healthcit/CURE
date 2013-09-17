/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cadsr.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.healthcit.cadsr.transformer.DataTransformer;
import com.healthcit.cadsr.utils.CADSRUtils;


public class CADSRServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1923421303064502780L;
	private static String CADSR_SEARCH_CRITERIA_PARAM = "crit";
	private static String CADSR_SEARCH_CRITERIA_PARAM_TYPE = "critType";
	private static int CADSR_SEARCH_FAILED_HTTPCODE = 599;
	private static String ENCODING_TYPE = "UTF-8";
	private static Logger log = Logger.getLogger( CADSRServlet.class );
	private Map<String, String> resultsMap = new HashMap<String, String>();

	/**
	 * Constructor of the object.
	 */
	public CADSRServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println( "In CADSR Servlet...");
		String xml = null;
		
		try 
		{
			// Get the search criteria object
			log.debug( request.getParameter( CADSR_SEARCH_CRITERIA_PARAM ) );
			log.debug( request.getParameter( CADSR_SEARCH_CRITERIA_PARAM_TYPE ) );
			String searchCriteria = URLDecoder.decode( request.getParameter( CADSR_SEARCH_CRITERIA_PARAM  ), ENCODING_TYPE );
			String searchType     = request.getParameter( CADSR_SEARCH_CRITERIA_PARAM_TYPE );
			
			// Get the search results and serialize the results
			String key = searchCriteria + "|||" + searchType;
			if(resultsMap.containsKey(key)) {
				xml = resultsMap.get(key); 
			} else {
				DataTransformer dataTransformer = DataTransformer.getInstance( searchType );
				@SuppressWarnings("rawtypes")
				List results = dataTransformer.search( searchCriteria, searchType );
				xml = CADSRUtils.getXML( dataTransformer.getProcessedEntities( results, searchType ) );
				
				// Cacheing
				boolean isCacheable = !CollectionUtils.isEmpty(results);
				if ( isCacheable ) resultsMap.put(key, xml);
			}
		} 
		catch ( Exception ex )
		{
			ex.printStackTrace();	
			// Set the error code
			response.setStatus( CADSR_SEARCH_FAILED_HTTPCODE );
		}
		
		// Generate the response
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println( xml == null ? "" : xml );
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
	}

}
