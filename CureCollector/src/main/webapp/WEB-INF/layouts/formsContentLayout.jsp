<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>
<%-- setup common parameters --%>
<tiles:importAttribute name="activePage" scope="request"/>
<%-- Main content --%>
<div id="content">
<tiles:insertAttribute name="content-sidebar"/>
<div id="mainContent">
<tiles:insertAttribute name="content-menu"/>
<tiles:insertAttribute name="content"/>
</div><!-- end #mainContent -->
<!-- This clearing element should immediately follow the #mainContent div in order to force the #container div to contain all child floats -->
<br class="clearfloat" />
</div><!-- end #content -->
