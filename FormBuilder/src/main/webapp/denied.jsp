<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page isErrorPage="true" %>
<div style="text-align:center; padding-top: 100px;">
	<h1>Access denied to this content</h1>
	<% 
		Logger logger = Logger.getLogger(this.getClass());
		logger.error("Authorization exception reached", exception);
	%>
</div>

