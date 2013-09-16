<%--
Copyright (c) 2013 HealthCare It, Inc.
All rights reserved. This program and the accompanying materials
are made available under the terms of the BSD 3-Clause license
which accompanies this distribution, and is available at
http://directory.fsf.org/wiki/License:BSD_3Clause

Contributors:
    HealthCare It, Inc - initial API and implementation
--%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@include file="../header.jsp" %>
<div class="admin-container" align="center">
	<div class="admin-title" id="adminTitle">
		<c:choose><c:when test="${user.id ne null}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> user
	</div>
	<div class="admin-content">
	<form:form commandName="user" method="POST">
		<table>
			<tr>
				<td>User Name:</td>
				<td>
					<c:choose>
						<c:when test="${user.id ne null}">
							<form:hidden path="id" />
							<form:hidden path="username" />
							<c:out value="${user.username}"/>
						</c:when>
						<c:otherwise>
							<form:input path="username"/>
						</c:otherwise>
					</c:choose>
				</td>
				<td><form:errors path="username" cssClass="validation-error"/></td>
			</tr>
			<tr>
				<td>Email:</td>
				<td><form:input path="email"/></td>
				<td><form:errors path="email" cssClass="validation-error"/></td>
			</tr>
			<tr>
				<td>Roles:</td>
				<td><form:checkboxes path="roles" items="${roles}" itemValue="id" itemLabel="displayName" delimiter="<br/>"/></td>
				<td><form:errors path="roles" /></td>
			</tr>
			
			<tr>
				<td>Password <c:if test="${user.id ne null}">*</c:if>:</td>
				<td><form:password path="password"/></td>
				<td><form:errors path="password" cssClass="validation-error"/></td>
			</tr>
			<tr>
				<td>Confirm Password:</td>
				<td><form:password path="confirmPassword"/></td>
			</tr>
			<c:if test="${user.id ne null}">
				<tr>
					<td colspan="3"><small style="margin-left: 10px;">*Leave blank if don't want to change</small></td>
				</tr>
			</c:if>			
			<tr>
				<td></td>
				<td><input type="submit" value="Save"/>&nbsp;&nbsp;<input type="button" value="Cancel" onclick="jQuery(location).attr('href', '${adminUrl}')"/></td>
				<td></td>
			</tr>
		</table>
	</form:form>
	</div>
</div>

<%@include file="../footer.jsp" %>
