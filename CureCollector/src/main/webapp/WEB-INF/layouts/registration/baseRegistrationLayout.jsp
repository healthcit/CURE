<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp" %>

<%-- setup common parameters --%>
<tiles:importAttribute name="activePage" scope="request"/>

<!-- Javascript -->
<script src="http://ajax.googleapis.com/ajax/libs/dojo/1.4/dojo/dojo.xd.js" type="text/javascript"></script>
<script src="${appPath}/scripts/application.js" type="text/javascript"></script>
<script type='text/javascript' src='${ appPath }/dwr/engine.js'></script>
<script type='text/javascript' src='${ appPath }/dwr/util.js'></script>
<script type='text/javascript' src='${ appPath }/dwr/interface/UserValidatorJS.js'></script>
<script src="${appPath}/scripts/registration.js" type="text/javascript"></script>
<script src="${appPath}/scripts/jquery.js" type="text/javascript"></script>
<script src="${appPath}/scripts/passwordStrengthMeter.js" type="text/javascript"></script>

<%-- Main content --%>
<div id="content">
	<tiles:insertAttribute name="content-sidebar"/>
	<div id="mainContent">
		<tiles:insertAttribute name="content-menu"/>

		<!-- Registration Step 1 -->
		<tiles:insertAttribute name="regStep1"/>

		<!-- Registration Step 2 -->
		<tiles:insertAttribute name="regStep2"/>

		<!-- Registration Step 3 -->
		<tiles:insertAttribute name="regStep3"/>
	</div><!-- end #mainContent -->

	<!-- This clearing element should immediately follow the #mainContent div in order to force the #container div to contain all child floats -->
	<br class="clearfloat" />
</div><!-- end #content -->
