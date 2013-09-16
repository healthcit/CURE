<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ page
	import="com.healthcit.how.web.controller.admin.EntityTagPermissionsController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>

<script src="${appPath}/scripts/entityTagPermissions.js"
	type="text/javascript"></script>
<style type="text/css">
tr.datacellone {
	background-color: #D0D0D0; color: black;
}
tr.datacelltwo {
	background-color: 	#FFFFFF; color: black;
}
</style>

<div>
	<table width="650px">
		<tr>
			<td width="25"></td>
			<td width="625px">
				<div id="directFormsWrapper"></div>
				<table width="625" border="0" cellpadding="5px 0px 5px 0px"
					cellspacing="8px 0px 8px 0px">
					<tr align="left" valign="middle">
						<td align="left" class="FAQQuestion"><b>Entity Tag
								Permissions</b></td>
					</tr>
					<tr align="left" valign="middle">
						<td class="FAQText"><form:form method="post"
								id="entityTagPermissions" action="entityTagPermissionsResult.form">
								<form:errors path="*" cssClass="errorBox" />
								<b>Group: </b>
								<select id="groups" name="groups">
									<option>Please select a group</option>
								</select>
								<br />
								<br />
								<b>Select Entity</b>
								<br>
								<div id="entities"
									style="border: solid 2px #D8D8D8; background: #F2F2F2; color: #19070B; padding: 4px; width: 600px; height: 100px; overflow: auto;">
								</div>
								<br />
								<b>Select Tags</b>
								<br>
								<div>
									<div id="tags"
										style="border: solid 2px #D8D8D8; background: #F2F2F2; color: #19070B; padding: 4px; width: 700px; height: 200px; overflow: auto;">
										      
												<input type='checkbox' name='forms' id='event' value=''
												style="display: none;"> 
											 
												<input type='checkbox'
												name='read' id='read' value='' style="display: none;">
											 	
									</div>
									<input type="submit" value="Save" />
								</div>
							</form:form></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
