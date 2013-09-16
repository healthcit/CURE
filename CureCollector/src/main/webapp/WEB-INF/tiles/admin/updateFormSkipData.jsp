<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ page import="com.healthcit.how.web.controller.admin.UploadModuleController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<script src="${appPath}/scripts/uploadModule.js" type="text/javascript"></script>

<div id="content">
  <table width="650px"> 
	<tr>
		<td width="25"></td>
		<td width="625px">
		<form:form modelAttribute="<%=UploadModuleController.MODULE%>" method="post" id="updateFormSkipData">	
			<input type="hidden" name="recalculateFormSkips" id="recalculateFormSkips" value = "yes"/>		
			<table width="625" border="0" cellpadding="5px 0px 5px 0px" cellspacing="8px 0px 8px 0px">
				<tr>
					<th><b>Update Form Skip Data</b></th>
				</tr>
				<tr>
					<td>
						<br/><br/>
						The uploaded module updates have been applied successfully.<br/><br/>
						Now, click on "Recalculate Form Skips" to update the data based on any new form skip patterns.<br/>
						<br/><br/>						 
					</td>
			    </tr>			
			</table>
			<input type="submit" id="recalculateFormSkipsBtn" name="Recalculate Form Skips" value="Recalculate Form Skips" onclick="showUploadModuleSplashScreen();"/>
		</form:form>
		<div id="loadingSpinner">&nbsp;&nbsp;&nbsp;&nbsp;<span class="text">Please wait as updates are processed...</span></div>
		</td>
	</tr>
  </table>
</div>
