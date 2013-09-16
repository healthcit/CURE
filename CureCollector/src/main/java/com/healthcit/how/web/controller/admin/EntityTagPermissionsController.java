/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.controller.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


import net.sf.json.JSONObject;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import com.healthcit.cacure.security.EntityTagPermissions;
import com.healthcit.cacure.security.FormTag;
import com.healthcit.cacure.security.FormTagPermission;
import com.healthcit.cacure.security.FormTagPermissions;
import com.healthcit.cacure.security.ObjectFactory;
import com.healthcit.cacure.security.Permissions;
import com.healthcit.cacure.security.Security;
import com.healthcit.how.api.AccessServices;
import com.healthcit.how.businessdelegates.EntityPermissionsManager;
import com.healthcit.how.utils.ExceptionUtils;

@Controller
public class EntityTagPermissionsController {

	/* Logger */
	private static final Logger log = LoggerFactory
			.getLogger(EntityTagPermissionsController.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AccessServices accessServices;

	@Autowired
	private EntityPermissionsManager entityPermissionsManager;

	private String saveResponse;

	@RequestMapping(value = "/admin/entityTagPermissions.form")
	public ModelAndView handleEntityTagPermissions(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView mav = new ModelAndView();
		return mav;
	}

	@RequestMapping(value = "/admin/entityTagPermissionsResult.form")
	public ModelAndView submit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// String groupId = request.getParameter("groups");
		String xml = "";

		String entityId = request.getParameter("entity");
		String groupId = request.getParameter("groups");
		
		/*
		 * tags, read, write, approve, submit are declared as String[] because the return 
		 * type from ServletRequest is String[]. The data type cannot be changed for better 
		 * efficiency in this case.
		 */

		String[] tags = request.getParameterValues("forms");

		String[] read = request.getParameterValues("read");

		String[] write = request.getParameterValues("write");

		String[] approve = request.getParameterValues("approve");

//		String[] submit = request.getParameterValues("submit");
		
		/*
		 * tags declared as String[] is changed to ArrayList??
		 * becos search cannot be performed on String[].
		 */
		Set<String> readSet = new TreeSet<String>();
		Set<String> writeSet = new TreeSet<String>();
		Set<String> approveSet = new TreeSet<String>();
//		Set<String> submitSet = new TreeSet<String>();
		if(read!=null)
		{
			for(String tagId: read)
			{
				readSet.add(tagId);
			}
		}
		if(write!=null)
		{
			for(String tagId: write)
			{
				writeSet.add(tagId);
			}
		}
//		if(submit!=null)
//		{
//			for(String tagId: submit)
//			{
//				submitSet.add(tagId);
//			}
//		}
		if(approve!=null)
		{
			for(String tagId: approve)
			{
				approveSet.add(tagId);
			}
		}
		
		if (tags != null) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
//			List<String> tagsList = new ArrayList(Arrays.asList(tags));
			HashMap<String, List<FormTagPermission>> permissionsMap = new HashMap<String, List<FormTagPermission>>();

			
			for (String tag: tags) {
				@SuppressWarnings("unchecked")
				List<FormTagPermission> formTagPermissionList = new ArrayList<FormTagPermission>();

				if (readSet.contains(tag)) {
					formTagPermissionList.add(FormTagPermission.valueOf("READ"));
				}
				if (writeSet.contains(tag)) {
					formTagPermissionList.add(FormTagPermission.valueOf("WRITE"));
				}
				if (approveSet.contains(tag)) {
					formTagPermissionList.add(FormTagPermission.valueOf("APPROVE"));
				}
//				if (submitSet.contains(tag)) {
//					formTagPermissionList.add(FormTagPermission.valueOf("SUBMIT"));
//				}
				permissionsMap.put(tag, formTagPermissionList);
			}

			xml = generateXml(entityId, permissionsMap);

			log.debug(xml);
		} else {

		}

		ModelAndView mav = new ModelAndView();

		String baseUrl = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath();
		StringBuilder url = new StringBuilder(baseUrl + "/api/" + entityId
				+ "/SavePermissions");
		saveResponse = null;
		try {

			saveResponse = restTemplate.postForObject(url.toString(), xml,
					String.class);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			mav.addObject("xml", e.toString());
			return mav;
		}

		mav.addObject("xml", saveResponse);

		return mav;
	}

	private String generateXml(String entityId, HashMap<String, List<FormTagPermission>> permissionsMap)
			throws JAXBException {

		try {
			JAXBContext jc = JAXBContext
					.newInstance("com.healthcit.cacure.security");
			// Create unmarshaller

			Marshaller m = jc.createMarshaller();
			ObjectFactory of = new ObjectFactory();
			Security security = of.createSecurity();

			Permissions permissionsJaxb = of.createPermissions();
			security.setPermissions(permissionsJaxb);
			List<EntityTagPermissions> entityTagPermissionsJaxb = permissionsJaxb
					.getEntityTagPermissions();
			EntityTagPermissions entityPermissionsJaxb = of
					.createEntityTagPermissions();
			entityTagPermissionsJaxb.add(entityPermissionsJaxb);
			entityPermissionsJaxb.setEntityId(entityId);

			FormTagPermissions formTagPermissionsJaxb = of
					.createFormTagPermissions();
			List<FormTag> formTags = formTagPermissionsJaxb.getTag();
			Iterator<Entry<String, List<FormTagPermission>>> it = permissionsMap
					.entrySet().iterator();

			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry pairs = (Map.Entry) it.next();
				FormTag formTag = of.createFormTag();
				formTag.setId(pairs.getKey().toString());
				@SuppressWarnings("unchecked")
				List<FormTagPermission> formTagPermissionList = (List<FormTagPermission>) pairs
						.getValue();
				formTag.getFormTagPermission().addAll(formTagPermissionList);
				formTags.add(formTag);
			}

			entityPermissionsJaxb.setFormTagPermissions(formTagPermissionsJaxb);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter ow = new OutputStreamWriter(out);
			m.marshal(of.createSecurity(security), ow);

			return out.toString();

		} catch (Exception e) {
			return e.toString();
		}

	}

	private void sendResults(HttpServletResponse response, String mimeType,
			String responseStatus) throws IOException {
		sendResults(response, mimeType, responseStatus, null);
	}

	private void sendResults(HttpServletResponse response, String mimeType,
			String responseSummary, Exception exception) throws IOException {
		PrintWriter out = null;

		try {
			out = response.getWriter();
			response.setContentType(mimeType);
			if (exception == null) {
				out.write(responseSummary);
			} else {
				JSONObject errorDetails = ExceptionUtils
						.getHttpResponseErrorDetails(responseSummary, exception);
				out.write(errorDetails.toString());
			}
		} catch (IOException ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

}
