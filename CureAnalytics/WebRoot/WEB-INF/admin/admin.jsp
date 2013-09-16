<%--
Copyright (c) 2013 HealthCare It, Inc.
All rights reserved. This program and the accompanying materials
are made available under the terms of the BSD 3-Clause license
which accompanies this distribution, and is available at
http://directory.fsf.org/wiki/License:BSD_3Clause

Contributors:
    HealthCare It, Inc - initial API and implementation
--%>
<%@include file="../header.jsp" %>
<!--  
<center><div style="width: 800px; background-color: #E9EFF6 !important;"><h4 style="background-color: #E9EFF6 !important; color: white;">User List</h4></div></center>
-->
<c:url var="addUserUrl" value="/admin/add_user" />
<div class="admin-container" align="center">
	<div class="admin-title" id="adminTitle">
		User List
	</div>
	<div class="admin-content">
	<div style="position: fixed; width:780px; padding-top: 40px;" align="right"><a href="${addUserUrl}">Add User</a></div>
		<table cellpadding="0" cellspacing="0">
		<tr>
			<td align="left" width="200"><h4>Name</h4></td>
			<td align="left" width="300"><h4>Roles</h4></td>
		</tr>  
		<c:forEach items="${users}" var="current" varStatus="cnt">
	        <c:url var="userEditUrl" value="/admin/edit_user">
	        	<c:param name="id" value="${current.id}" />
	        </c:url>
	        <tr class="d1" >
	          <td>
	             <a href="${userEditUrl}" class="admin-user-edit"><img src="${imgPath}/edit.png" height="18" width="18" alt="Edit" border="0" /></a>
	             <c:out value="${current.username}" />
	          </td>
	          <td>
            	<c:out value="${current.listOfRoles}" />
	          </td>
	        </tr>    
	   </c:forEach> 
 </table>
	</div>
</div>

<%@include file="../footer.jsp" %>
