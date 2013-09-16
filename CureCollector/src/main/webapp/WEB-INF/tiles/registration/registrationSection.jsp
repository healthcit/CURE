<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>

<!-- Display a Table of Users -->
<div class="userManagement">
	<div class="caption">
		Manage Users
	</div>
<table class="manageUsersTable">
	<tr>
		<th>Username</th>
		<th>Role</th>
		<th>Actions</th>
	</tr>
	<tr>
		<td class="addNew" colspan="3"><a href="#" onclick="addNewUser();">Add a new user</a></td>
	</tr>
	<c:forEach items="${commandName}" var="user" varStatus="status">
		<tr class="${ (status.index % 2 == 0 && user.id > -1) ? 'even' : 'odd' } ${ user.id > -1 ? '' : 'createNew' }" id="showUserRow_${ user.id }">
			<td><span class="username">${ user.username }</span></td>
			<td><span class="role">${ user.role }</span></td>
			<td>
				<span class="actions">
					<a href="#" onclick="editExistingUser(${ user.id })">Edit</a>&nbsp;
					<a href="#" onclick="deleteExistingUser(${ user.id })">Delete</a>
				</span>
			</td>
		</tr>
		<tr class="editUserRow" id="editUserRow_${ user.id }" style="display:none;">
			<td>
				<div class="editUserContent">
					<!-- Username -->
					<div>
						<label><span class="asterisk">*</span>Username:</label> 
						<input type="text" value="${ user.username }" id="username_${ user.id }"/><br/>
					</div>
					
					<!-- Password -->
					<div>
						<label><span class="asterisk">*</span>Password:</label> 
						<input type="password" value="${ user.password }" id="password_${ user.id }"/><br/>
					</div>
					
					<!-- Role -->
					<div>
						<label><span class="asterisk">*</span>Role:</label> 
						<select id="role_${ user.id }">
							<c:forTokens items="ROLE_ADMIN,ROLE_DEPLOYER,ROLE_USER" var="currRole" delims=",">
								<c:if test="${ currRole == user.role }">
									<option value="${ currRole }" selected="selected">${ currRole }</option>
								</c:if>
								<c:if test="${ currRole != user.role }">
									<option value="${ currRole }">${ currRole }</option>
								</c:if>
							</c:forTokens>
						</select>
					</div>
					<div class="buttonPanel">
						<input type="button" value="Save" onclick="saveUserUpdates(${ user.id })"/>&nbsp;
						<input type="button" value="Cancel" onclick="cancelUserUpdates(${ user.id })"/>
					</div>
				</div>
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
	</c:forEach>
</table>
</div>
