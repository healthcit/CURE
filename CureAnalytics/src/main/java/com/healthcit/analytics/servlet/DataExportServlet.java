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
package com.healthcit.analytics.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthcit.analytics.businessdelegates.OwnerDataManager;
import com.healthcit.analytics.utils.Constants;
import com.healthcit.cacure.data.utils.CouchJSONConverter.OutputFormat;


public class DataExportServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(DataExportServlet.class);
	private static final long serialVersionUID = 1L;
	public static final Map<String, DownloadStatus> TOKENS_MAP = new HashMap<String, DownloadStatus>();
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String token = request.getParameter("token");
		TOKENS_MAP.put(token, DownloadStatus.STARTED);
		String keys = request.getParameter("keys");
		if(keys != null)
		{
			//JSONArray ownerIdsJSON = (JSONArray)JSONSerializer.toJSON(ownerIds ) ;
			String[] ownerIdsArray = StringUtils.split(keys, ",");
			
			response.setContentType(Constants.JSON_CONTENT_TYPE);	  
			response.addHeader(Constants.CONTENT_DISPOSITION, "attachment; filename=dataExport.zip");
		    response.addHeader(Constants.PRAGMA_HEADER, Constants.NO_CACHE);
		    
		    response.setHeader(Constants.CACHE_CONTROL_HEADER, Constants.NO_CACHE);
			ServletOutputStream os = response.getOutputStream();
			ZipOutputStream zos = new ZipOutputStream(os);
//			ZipEntry entry = new ZipEntry("dataExport."+OutputFormat.JSON.toString().toLowerCase());
			ZipEntry entry = new ZipEntry("dataExport."+OutputFormat.XML.toString().toLowerCase());
			zos.putNextEntry(entry);
//			OwnerDataManager ownerManager = new OwnerDataManager(OutputFormat.JSON);
			OwnerDataManager ownerManager = new OwnerDataManager(OutputFormat.XML);
			try
			{
				ownerManager.getEntitiesData(ownerIdsArray, zos);
				TOKENS_MAP.put(token, DownloadStatus.FINISHED);
			}
			catch(Exception e)
			{
				log.error(e.getMessage(), e);
				TOKENS_MAP.put(token, DownloadStatus.ERROR);
			}
			finally
			{
				zos.close();
//				os.flush();
			
//				os.close();
			}
		} else {
			TOKENS_MAP.put(token, DownloadStatus.ERROR);			
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType(Constants.PLAIN_TEXT);
		resp.addHeader(Constants.PRAGMA_HEADER, Constants.NO_CACHE);	    
		resp.setHeader(Constants.CACHE_CONTROL_HEADER, Constants.NO_CACHE);
		String token = req.getParameter("token");
		PrintWriter writer = resp.getWriter();
		try {
			if (token != null) {
				DownloadStatus status = TOKENS_MAP.get(token);
				if (status != null) {
					writer.print(status.name());
					if (DownloadStatus.FINISHED.equals(status)) {
						TOKENS_MAP.remove(token);
					}
				} else {
					writer.print(DownloadStatus.ERROR.name());
				}
			} else {
				writer.print(DownloadStatus.ERROR.name());
			}
		} finally {
			writer.close();
		}
	}
	
	private enum DownloadStatus{
		STARTED, FINISHED, ERROR;		
	};
}
