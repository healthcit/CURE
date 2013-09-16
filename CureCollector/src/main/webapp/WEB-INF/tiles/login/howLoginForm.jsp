<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp" %>

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
				<td class="titleBold">HOW Study Member Login</td>
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
     <tr>
		<td> </td>
	</tr>
     <tr>
         <td align="left" class="camatch_lnav_nolink_small">Lost User Name or Password?<br/>Click for <a href="#" class="lnav_linkSmall"><u>HELP</u></a></td>
     </tr>
     <tr>
				<td class="camatch_lnav_nolink_small">Not a Member of the HOW Study?<br/>Click to <a href="#"><u>sign up today</u></a></td>
	</tr>
     <tr>
		<td> </td>
	</tr>


</table>
</form>
