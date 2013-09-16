<%--
Copyright (c) 2013 HealthCare It, Inc.
All rights reserved. This program and the accompanying materials
are made available under the terms of the BSD 3-Clause license
which accompanies this distribution, and is available at
http://directory.fsf.org/wiki/License:BSD_3Clause

Contributors:
    HealthCare It, Inc - initial API and implementation
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<c:url var="homePath" value="/"/>
<spring:url value="/j_spring_security_check" var="authURL" />
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<script type="text/javascript" src="${homePath}/js/jquery-1.4.2.min.js"></script>
    <title>CURE Analytics</title>
    
    <link type="text/css" href="css/cacure.css" rel="stylesheet" />
    <script type="text/javascript">
    	jQuery(document).ready(function() {
        	if(!jQuery.browser.mozilla){
    	  		jQuery("#loginTitle").addClass("webkit-title");
        	}
    	});
    </script>
    
</head>
<body style="width: 100% !important;">
<div class="pageTitle"><span class="pageTitleImage">&nbsp;</span>&nbsp;</div>

<div class="loginFormOuterContainer">
    <div class="loginFormContainer">
		<div class="loginTitle" id="loginTitle">
			Login
		</div>
		<c:if test="${errorMessage ne null}">
			
		</c:if>
		<div class="loginForm" id="loginForm">		
			<form:form commandName="userCredentials" action="${authURL}" method="POST" cssStyle="margin-top: 40px; ">
			<table>
			<tr>
				<td colspan="2"><div align="center" class="login-errror" ><c:out value="${errorMessage}" /></div></td>
			</tr>
			<tr>
				<td><span class="requiredField">User name: &nbsp;&nbsp;</span></td>
				<td><input type="text" name="userName" id="j_username" size="15" maxlength="25" style="width: 100%; font-weight: bold;"/></td>
			</tr>
			<tr>
				<td><span class="requiredField">Password:</span></td>
				<td><input type="password" name="password" id="j_password" size="15" style="width: 100%; font-weight: bold;"/><br/>
			</tr>
			<tr>
				<td align="right" colspan="2">
					<input name="submit" type="submit" value="Log In" />
				</td>
			</tr>
			</table>
			</form:form>
		</div>
	</div>
</div>

</body>
</html>
