<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@page import="com.healthcit.how.api.FormActionsProvider"%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>
<script src="${appPath}/scripts/configureWorkflow.js" type="text/javascript"></script>
<div class="configureWorkflow">
	<div class="configureWorkflowCaption">Configure Workflow of Forms</div>
	<table>
		<thead>
			<tr>
				<th>Action</th>
				<th>Associated Button Label</th>
				<th>Associated Description</th>
				<th class="check">Hide Button</th>
				<th>&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${templateMap}" var="entry" varStatus="index">
				<tr>
					<td class="rowheader_${entry.key}"><span><c:out value="${entry.key}"/></span></td>
					<td><input type="text" id="${entry.key}_buttonLabel" name="${entry.key}_buttonLabel" value="${entry.value[0]}" onchange="updateOnChange(this,'${entry.key}')"/></td>
					<td><input type="text" id="${entry.key}_buttonDescription" name="${entry.key}_buttonDescription" value="${entry.value[1]}" onchange="updateOnChange(this,'${entry.key}')"/></td>
					<td class="check"><input type="checkbox" id="${entry.key}_buttonHide" name="${entry.key}_buttonHide" ${entry.value[2] ? 'checked' : ''} onchange="updateOnChange(this,'${entry.key}')"/></td>
					<td><input type="button" name="configure_${entry.key}" value="Update" id="configure_${entry.key}" onclick="updateWorkflow('${entry.key}')"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
