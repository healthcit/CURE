<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@page import="java.net.URLEncoder"%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>

<%@ page import="com.healthcit.how.businessdelegates.UserService"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.springframework.context.ApplicationContext" %>


<%
ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
UserService userService = (UserService)context.getBean("userService");
%>

<script src="${appPath}/scripts/admin.js" type="text/javascript"></script>

<c:set var="authType"><%= userService.getAuthType()%></c:set>

<div id="content">
	<c:if test="${not empty adminMessages}">	
		<ul class="adminMsg">
			<c:forEach items="${adminMessages}" var="msg">
			${msg}<br/> 
			</c:forEach>
		</ul>
	</c:if>
	<c:if test="${not empty uploadMessages}">	
		<ul class="adminMsg">
			<c:forEach items="${uploadMessages}" var="msg">
			${msg}<br/> 
			</c:forEach>
		</ul>
	</c:if>
	<c:if test="${not empty moduleRedeploymentUrls}">	
		<div class="adminMsg2">
		<c:forEach items="${moduleRedeploymentUrls}" var="url" varStatus="status">
			<span class="desc">Details</span>
			<table>
				<tr><th>Module Redeployment Update</th><th>Status</th></tr>
				<tr><td>Update ${status.index + 1}</td><td><c:import url="${ url }"/></td></tr>
			</table>
		</c:forEach>
		</div>
	</c:if>
	<ol id="admin-links">	
			<li><a href="${appPath}/admin/uploadModule.form" >Upload a module</a></li>
			<li><a href="${appPath}/admin/couch/trancate.do" >Truncate CouchDB</a></li>
			<li><a href="#" onclick="javascript:truncateModule('${appPath}/admin/module/truncate.do')" >Truncate Data By Module</a><br/></li>
			<li><a href="${appPath}/admin/couch/truncateByContext.do?context=<%= URLEncoder.encode("patient","UTF-8") %>"
			       onclick="return confirm('WARNING: This will truncate data for the context: \'patient\');">
			Truncate Patient Context in CouchDB
			</a></li>
			<li><a href="${appPath}/admin/directForms.form" >Generate and Save a Form</a></li>
			<li><a href="${appPath}/admin/workflow/configure.do">Customize Workflow</a></li>
			<c:choose>
				<c:when test="${authType == 'ldap'}" >
					<li><a href="${appPath}/ldap/ldapList.view">Manage Users</a><br/></li>
				</c:when>
				<c:otherwise>
					<li><a href="${appPath}/admin/registration.form">Manage Users</a><br/></li>
				</c:otherwise>
			</c:choose>  
	</ol>
</div>
	
	
<div id="truncateByModuleDialog" style="display:none;" title="Truncate By Module">
	Select a module to truncate.<br/>
	<select name="moduleSelectWidget" id="moduleSelectWidget">
		<c:forEach items="${moduleList}" var="moduleElm">
			<option value="${moduleElm.id}">${ moduleElm.description }</option>
		</c:forEach>
	</select>
</div>