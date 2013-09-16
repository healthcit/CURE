<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ page import="com.healthcit.how.web.controller.admin.EntityTagPermissionsController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ include file="/WEB-INF/includes/taglibs.jsp" %>

<div>
  <table width="650px">
	<tr>
		<td width="25"></td>
		<td width="625px">
		<div id="directFormsWrapper"></div>
			<table width="625" border="0" cellpadding="5px 0px 5px 0px" cellspacing="8px 0px 8px 0px">
				<tr align="left" valign="middle">
					<td align="left" class="FAQQuestion"><b>Entity Tag Permissions Result</b></td>
				</tr>
				<tr align="left" valign="middle">
				    <td class="FAQText">
						<form:form method="post" id="entityTagPermissionsResult" action="entityTagPermissions.form">
							<form:errors path="*" cssClass="errorBox" />							
          					<div id="entityPermissionsXml" style="border : solid 2px #ff0000; background : #000000; color : #ffffff; padding : 4px; width : 800px; height : 200px; overflow : auto; ">
          					<textarea style="width: 794px; height: 206px;">${xml}</textarea>
          					</div>
							<input type="submit" value="Entity Tag Permissions"/>
						 </form:form>
					</td>
				</tr>
			</table>
		</td>
	</tr>
  </table>
</div>
