<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>
<%@page trimDirectiveWhitespaces="true"%>
<%-- setup common parameters --%>
<tiles:importAttribute name="title" scope="request"/>
<tiles:importAttribute name="jspLocation" scope="request"/>
<c:set var="appPath" value="${pageContext.request.contextPath}" scope="request"/>
<?xml version="1.0" encoding="utf-8"?>
<%
	response.setContentType("text/xml");
	response.setCharacterEncoding("UTF-8");
%>
<c:choose>
	<c:when test="${not empty xformModel}">
		<?xml-stylesheet href="${appPath}/xsltforms/xsltforms.xsl" type="text/xsl"?>
    </c:when>
    <c:otherwise>
		<?xml-stylesheet href="${appPath}/xsltforms/regular-html.xsl" type="text/xsl"?>
    </c:otherwise>
</c:choose>

<%-- c:if test="${not empty xformModel}">
<?xml-stylesheet href="${appPath}/xsltforms/xsltforms.xsl" type="text/xsl"?>
</c:if --%>
<!--  DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  -->
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xmlns:xform="http://www.w3.org/2002/xforms"
      xmlns:xsd="http://www.w3.org/2001/XMLSchema">

<head>

<title>${title}</title>
	<title>Love-Avon Health Of Women Study</title>
	<c:if test="${empty xformModel}">
		<link href="${appPath}/xsltforms/how-styles.css" rel="stylesheet" type="text/css"/>
	</c:if>
<%--
	<script type="text/javascript" src="includes/include.js"></script>
	<script type="text/javascript" src="includes/help.js"></script>
    <script type="text/javascript" src="includes/JS/prototype.js"></script>
	<script type="text/javascript" src="includes/JS/effects.js"></script>
	<script type="text/javascript" src="includes/JS/phase.js"></script>
	<script type="text/javascript" src="includes/JS/bct_app.js"></script>
	<script type="text/javascript" src="includes/JS/common.js"></script>
    <script type="text/javascript" src="includes/external.js"></script> <!-- this line and below was moved from <body> -->
	<script type="application/javascript">
	 <![CDATA[
		var current_tn = "none"; //This variable determines which top nav element is 'on'.
 	]]>
 	</script>
	<script type="text/javascript" src="includes/JS/bct_app.js"></script>

	<!-- Menu JS -->
	<script>
	 <![CDATA[
	var current_ln_menu = "patient_home2"; //This loads the appropriate graphics in /bct/includes/include.js
 	]]>
	</script>
--%>

<%-- XForms model must go into a head --%>

${xformModel}
<link type="text/css" href="/FormBuilder/xsltforms/xforms_main.css" rel="stylesheet"/>

</head>

<body class="twoColFixLtHdr">
<div id="container">
<tiles:insertAttribute name="header"/>

<%-- Main content --%>
<tiles:insertAttribute name="body"/>

<%-- Footer --%>
<tiles:insertAttribute name="footer"/><!-- end #container -->

</div></body>
</html>
