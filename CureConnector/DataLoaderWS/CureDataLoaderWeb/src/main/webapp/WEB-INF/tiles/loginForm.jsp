<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/tiles/includes/taglibs.jsp" %>

<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="lh_menu_spacer"></td>
			<td> </td>
		</tr>
	</table>


    <form name='f' action="<c:url value='j_spring_security_check'/>" method='POST' >

    	<table class="lh_menu">
    		<tr>
    			<td>
    				<!-- Display login-related validation errors, if applicable -->
				    <c:if test="${ param['loginError'] == 1 }">
				    	<span class="errorClass">Could not login with the supplied username/password.</span>
				    </c:if>
				    
				    <c:if test="${ param['loginError'] == 2 }">
				    	<span class="errorClass">Sorry, you are not authorized to access this application.</span>
				    </c:if>

				    <!-- If the user logged out, then display a message to that effect -->
				    <c:if test="${ param['logoutSuccess'] == 1 }">
				    	<span class="errorClass">You have logged out.</span>
				    </c:if>
    			</td>
    		</tr>
	        <tr>
				<td> </td>
			</tr>
	        <tr>
				<td class="titleBold">Login</td>
			</tr>
	        <tr>
				<td> </td>
			</tr>
		    <tr>
		        <td> </td>
		    </tr>
		    <tr>
		        <td><input type="text" name="j_username" id="j_username" size="15" maxlength="25"/></td>
		    </tr>
		    <tr>
		        <td> </td>
		    </tr>

		    <tr>
		        <td width="80"><input type="password" name="j_password" id="j_password" size="15" maxlength="10"/></td>
		    </tr>
		    <tr>
				<td> </td>
			</tr>
		    <tr>
		        <td width="100"><input type="submit" value="Login"/></td>
		    </tr>


</table>
</form>
