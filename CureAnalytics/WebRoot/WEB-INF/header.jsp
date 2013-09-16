<%--
Copyright (c) 2013 HealthCare It, Inc.
All rights reserved. This program and the accompanying materials
are made available under the terms of the BSD 3-Clause license
which accompanies this distribution, and is available at
http://directory.fsf.org/wiki/License:BSD_3Clause

Contributors:
    HealthCare It, Inc - initial API and implementation
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<%@include file="/WEB-INF/includes/taglibs.jsp" %>
<%@ page import="com.healthcit.analytics.service.UserService"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.springframework.context.ApplicationContext" %>

<%
ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
UserService userService = (UserService)context.getBean("userService");
%>

<c:set var="authType"><%= userService.getAuthType()%></c:set>
<c:choose>
	<c:when test="${authType == 'ldap' }">
		<c:url var="adminUrl" value="/ldap" />
	</c:when>
	<c:otherwise>
		<c:url var="adminUrl" value="/admin" />
	</c:otherwise>
</c:choose>
<c:url var="imgPath" value="/images"/>
<c:url var="homePath" value="/"/>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <title>CURE Analytics Admin</title>

	<script type="text/javascript" src="${homePath}/js/jquery-1.4.2.min.js"></script>
	<link href="css/codesite.pack.01312008.HOW.css" type="text/css" rel="stylesheet" />        
	<link href="${homePath}/jquery-alerts/jquery.alerts.css" rel="stylesheet" type="text/css" media="screen" />
	<link type="text/css" href="${homePath}/css/cacure.css" rel="stylesheet" />			
	<link rel="SHORTCUT ICON" href="${homePath}/files/HCIT_Favicon.ico"/>
	
	<script type="text/javascript">
    	jQuery(document).ready(function() {
        	if(!jQuery.browser.mozilla){
    	  		jQuery("#adminTitle").addClass("webkit-title");
        	}
    	});
    </script>
</head>

<body style="width: 100% !important;">
<div class="pageTitle" style="margin-left: 30px !important;"><span class="pageTitleImage">&nbsp;</span>&nbsp;</div>

	    <div class="menu_container" id="menu_container" style="margin-left: 30px !important;">
	    	<div class="menu_ctn_content">
	    	<a href="${homePath}" class="current" id="welcome_container_tab">Home&nbsp;&nbsp;</a>
	    	<sec:authorize ifAnyGranted="ROLE_ADMIN, ROLE_USER">
	    		<a href="${adminUrl}" class="" id="admin_tab">Admin&nbsp;&nbsp;</a>
	    	</sec:authorize>
	    	<a href="<c:url value="/logout"/>">Logout</a>
	    	</div>
	    </div>
