<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/tiles/includes/taglibs.jsp"%>
<%@page trimDirectiveWhitespaces="true"%>
<%-- setup common parameters --%>
<tiles:importAttribute name="title" scope="request"/>
<c:set var="appPath" value="${pageContext.request.contextPath}" scope="request"/>
<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xmlns:xsd="http://www.w3.org/2001/XMLSchema">

<head>
<title>${title}</title> 
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
