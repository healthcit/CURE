<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>
<c:set var="appPath" value="${pageContext.request.contextPath}" scope="request"/>
<?xml-stylesheet href="${appPath}/xsltforms/regular-html.xsl" type="text/xsl"?>
<link href="${appPath}/xsltforms/how-styles.css" rel="stylesheet" type="text/css"/>

<body class="twoColFixLtHdr">
	<div id="container">
		<CENTER>
		<BR><BR><BR><BR><BR><BR>
		<fieldset class="error">
		<legend>ERROR</legend>
		<b><font color="red">A database related error has occurred.</font></b>
		<BR><BR>
		Click <a href="${ appPath }/how-main.page">here</a> to go to the home page of the website.
		</fieldset>
		</CENTER>
	</div>
</body>
