<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/tiles/includes/taglibs.jsp"%>

<div>
<table width="80%" border="1" >
<tr>
<th>Account</th><th>Description</th><th>Is Enabled</th>
</tr>
<c:forEach var="account" items="${accountList}">
<tr>
<td><a href="${appPath}/account.edit?account_id=${account.accountId}">${account.accountId}</a>
</td>
<td>
${account.description}
</td>
<td>
${account.enabled}
</td>
</tr>
</c:forEach>
</table>
</div>

<div>
<a href="${appPath}/account.edit">Add Account</a>
</div>
