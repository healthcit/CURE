<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/tiles/includes/taglibs.jsp"%>

<%@ page import="com.healthcit.cure.dataloader.controller.AccountEditController"%>

<div>
<form:form commandName="<%=AccountEditController.COMMAND_NAME%>" action="${appPath}/account.edit" >
Client identification string <form:input type="text" path="accountId" /><br/>
Secret string: <form:input type="text" path="token" /><br/>
Description: <form:input type="text" path="description" /><br/>
<c:choose>
<c:when test="${accountDetails.accountId == null}">
<input type="submit" name="create" value="Create" />
</c:when>
<c:otherwise>
	<c:choose>
	<c:when test="${accountDetails.enabled}">
	<input type="submit" name="disable" value="Disable" /><br/>
	</c:when>
	<c:otherwise>
	<input type="submit" name="enable" value="Enable" /><br/>
	</c:otherwise>
	</c:choose>

<input type="submit" name="delete" value="Delete" />
</c:otherwise>
</c:choose>
</form:form>
</div>
