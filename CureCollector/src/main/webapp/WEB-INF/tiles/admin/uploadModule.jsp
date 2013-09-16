<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ page import="com.healthcit.how.web.controller.admin.UploadModuleController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<script language="javascript">
function init() {
	var status = document.getElementById("uploadStatus").innerHTML.toString();
	/*if(status=="error")
	{
		var message="The module that you are trying to upload already exists in the system. " +
		"All the forms that were removed from the new version will be removed from the server" +
		" and the data will be lost. If the user had already submitted the forms, " +
		" then the form will be unsubmitted." +
		" Additionally, if the hierarchy of any of the forms changed, " +
		" any existing forms with a new parent will be automatically assigned to the first instance of the parent." +
		" Are you sure you would like to proceed?";
		var moduleContext = $('#moduleContext').val();
		var newModuleContext = $('#newModuleContext').val();
		if(moduleContext!= null && newModuleContext!= null && moduleContext != newModuleContext)
		{
			message += "\n" + "The module context " + newModuleContext + " is not the same as the stored context for the module. The original " + moduleContext +" context will be preserved"; 		
		}
		var confirm = window.confirm(message);
		if (confirm)
		{
			$('#uploadModule').submit();
		}

	}*/

}
window.onload = init; 
</script>
<div id="content">
  <table width="650px">
	<tr>
		<td width="25"></td>
		<td width="625px">
		<div id="uploadStatus">${uploadStatus.status}</div>
			<table width="625" border="0" cellpadding="5px 0px 5px 0px" cellspacing="8px 0px 8px 0px">
				<tr align="left" valign="middle">
					<td align="left" class="FAQQuestion"><b>Please upload a file</b></td>
				</tr>
				<tr align="left" valign="middle">
				    <td class="FAQText">
						<form:form modelAttribute="<%=UploadModuleController.COMMAND_NAME%>" method="post" enctype="multipart/form-data" id="uploadModule">
							<form:errors path="*" cssClass="errorBox" />
<%--							<c:choose>
							<c:when test="${uploadStatus.uploadPage!=true}">
							<input type="hidden" name="confirmed" value="yes"/>
							<input type="hidden" name="dirName" value="${uploadStatus.tmpLocation}"/>
							<input type="hidden" name="archiveName" value="${uploadStatus.fileName}"/>
							<input type="hidden" name="moduleContext" id="moduleContext" value="${uploadStatus.moduleContext}"/>
							<input type="hidden" name="newModuleContext" id="newModuleContext" value="${uploadStatus.newModuleContext}"/>
							Module's Context: ${uploadStatus.moduleContext} <br/>
            				Upload Module Archive: ${uploadStatus.fileName}
							</c:when>
							<c:otherwise> --%>
							Module's Context: <input type="text" name="context" /><br/><br/>
            				Upload Module Archive: <input type="file" name="file"/> ex: module.zip<br/><br/>
							<input type="submit"/>
<%--							</c:otherwise>
							</c:choose>
          					--%>
						 </form:form>
					</td>
				</tr>
			</table>
		</td>
	</tr>
  </table>
</div>
