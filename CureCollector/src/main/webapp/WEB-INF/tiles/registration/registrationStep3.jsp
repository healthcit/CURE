<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ page import="com.healthcit.how.utils.Constants" %>
<%@ include file="/WEB-INF/includes/taglibs.jsp" %>	

<div id="step2">
  <table width="650px">
	<tr>
		<td width="25"></td>
		<td width="625px">
			<table width="625" border="0" cellpadding="5px 0px 5px 0px" cellspacing="8px 0px 8px 0px">
				<tr align="left" valign="middle">                         
					<td align="left" class="FAQQuestion"><b>STEP 2: Create NEW user account for the HOW Study</b> &nbsp;</td>
					<td align="left" class="pageHeader">
						<div id="startRegistrationButton" style="display: none">
							<input type="button" tabindex="1" value="Start Registration" onclick="ShowContent('register');document.getElementById('user_username').focus();"/>
						</div> 
					</td>
				</tr> 
				<tr align="left" valign="middle">
				    <td colspan="2" class="FAQText">
							After you have consented to the HOW study, you are asked to create a new HOW Study user account.  You will be asked to enter a unique user name and password.  <b>This is different from your Army of Women user name and password.</b>  We are asking you to create a new account for the HOW Study because in this account, you will be storing confidential, secure research health information.
					</td>	
					<td align="left" class="pageHeader">
						<div style="display: block;" id="startRegistrationSection">
							<input type="button" id="startRegistrationBtn" value="Start Registration" tabindex="1">
						</div> 
					</td>			
				</tr>				
			</table>
		</td>
	</tr>
  </table>
</div>

<div id="register">
<form:form commandName="<%= Constants.COMMAND_NAME %>">
  <!--  Display errors, if any exist when the "Register" button is clicked -->
  <spring:hasBindErrors name="<%= Constants.COMMAND_NAME %>">
  	<span class="errorClass global">
	  	REGISTRATION UNSUCCESSFUL:<br/>
	  	<li>
		  	<c:forEach items="${ errors.allErrors }" var="error">
		    	<spring:message code="${error.code}" text="${error.defaultMessage}"/><br/>
			</c:forEach>
		</li>
  	</span>
  </spring:hasBindErrors>
  
  <!-- Display form -->
  <table width="650px">
	<tr>
		<td width="25px"></td>
		<td>	   
			<table width="625px" border="0" cellpadding="2px 0px 2px 0px" cellspacing="0px 0px 0px 0px">
				<tr>
					<td width="150px"></td>
					<td width="175px"></td>
					<td width="325px"></td>
				</tr>
				<tr>										
					<td align="left" colspan="3" class="FAQHeader">Register to Participate:</td>
				</tr>
				<tr>
					<td colspan="3"><hr style="solid" color="black" size="3"/></td>
				</tr> 
				<tr><td ><font class="required">*</font><font class="note"> Required Fields</font></td></tr>
				<tr class="user_nameBlk"> <td colspan="3" class="camatch_lnav_nolink_small"><br/><div class="inbox1"  >INSTRUCTIONS FOR CREATING A USERNAME:<br>Your User Name should be at least 2 characters and no more than 25 characters, with a mix of alpha-numeric characters.</div></td></tr>
				<tr class="user_nameBlk">
						<td class="fieldLabelAMR" >Create User Name:<font class="required">*</font></td>
						<td>
							<spring:bind path="username">
								<form:input path="username" id="username" size="25" maxlength="25"  tabindex="1" cssErrorClass="errorClass"/>
								<span id="<c:out value='usernameError'/>" class="errorClass">
									<c:out value="${ status.errorMessage }"/>
								</span>
							</spring:bind>
						</td>
						<td></td>
					
				</tr>
				<tr>
					<td colspan="3"><hr style="solid" color="black" size="1"/></td>
				</tr>
				<tr class="user_PasswordBlk">
				  <td colspan="3" class="camatch_lnav_nolink_small"><br/><div class="inbox1">INSTRUCTIONS FOR CREATING A STRONG PASSWORD: <br>Your password must be at least 8 characters and no more than 10 characters, with a mix of letters, numbers and special characters, such as $, @, and ! When your password is considered "strong"
					, as indicated by the colored meter, the REGISTER button will be enabled. (Test$123 is a good example of strong password )</div><br/></td>
				</tr>
			
				<tr class="user_PasswordBlk">
					<td class="fieldLabelAMR" >Create a Password:<font class="required">*</font></td>
					<td>
						<spring:bind path="password">
							<form:password path="password" id="password" size="25" maxlength="10"  tabindex="2"  cssErrorClass="errorClass"/>
							<span id="<c:out value='passwordError'/>" class="errorClass">
								<c:out value="${ status.errorMessage }"/>
							</span>
						</spring:bind>
						<form:hidden path="passwordStrengthPercent" id="passwordStrengthPercent"/>
					</td>
					<td><table border="0" width="230px"><tr><td>
						<div class="inbox" align="left" >
								<div class="graybar" id="graybar"></div>
								<div class="colorbar" id="colorbar"></div>
								<br /><span class="percent" id="percent">0%</span>&nbsp;&nbsp;
								<span class="result" id='result'>	password Strength</span>
						</div></td></tr></table>
					</td>
				</tr>     
					
									
				<tr class="user_PasswordBlk">
					<td class="fieldLabelAMR">Confirm Password:<font class="required">*</font></td>
					<td>
					<!-- <input type="password" id="confirmPassword" size="25" maxlength="10" tabindex="3"  onblur="validateFieldsCPWD();"/> -->
						<spring:bind path="passwordConfirmation">
							<form:password path="passwordConfirmation"  id="passwordConfirmation" size="25" maxlength="10" tabindex="3"  cssErrorClass="errorClass"/>
							<span id="<c:out value='${ status.expression }Error'/>" class="errorClass">
								<c:out value="${ status.errorMessage }"/>
							</span>
						</spring:bind>
					</td>
					<td class="camatch_lnav_nolink_small">Having trouble creating your <a href="#" onClick="alert('Your password must be between 8 to 10 characters, with a mix of alpha-numeric and special characters such as $, @, and !. Enforcing these rules adds an extra layer of protection to your research health information.');return false">password?</a></td>
				</tr >
				<tr>
					<td colspan="3"><hr style="solid" color="black" size="1"/></td>
				</tr>
				<tr class="user_sequrityBlk">
					<td class="fieldLabelAMR">Email Address:<font class="required">*</font></td>
					<td>
						<!-- <input type="text" property="patientDemographic_emailAddr" id="email" size="25" maxlength="60"  tabindex="4" onblur="validateFieldsEmail();" /> -->
						<spring:bind path="emailAddress">
							<form:input path="emailAddress" id="emailAddress" size="25" maxlength="60"  tabindex="4"  cssErrorClass="errorClass"/>
							<span id="<c:out value='${ status.expression }Error'/>" class="errorClass">
								<c:out value="${ status.errorMessage }"/>
							</span>
						</spring:bind>
					</td>
					<td></td>
				</tr>
				  <tr class="user_sequrityBlk">
					<td class="fieldLabelAMR" width="270px">Confirm Email Address:<font class="required">*</font></td>
					<td>
					   <!-- <input type="text" id="confirmEmailAddr" size="25" maxlength="60"  tabindex="5"  onblur="validateFieldsCEmail();"/> -->
					   <spring:bind path="emailAddressConfirmation">
					   	  <form:input path="emailAddressConfirmation" id="emailAddressConfirmation" size="25" maxlength="60"  tabindex="5" cssErrorClass="errorClass"/>
					   	  <span id="<c:out value='${ status.expression }Error'/>" class="errorClass">
							<c:out value="${ status.errorMessage }"/>
						  </span>
					   </spring:bind>	
					</td>
					<td></td>
				</tr>
				<tr class="user_sequrityBlk">
					<td class="fieldLabelAMR">Security Question:<font class="required">*</font></td>
					<td>
						<spring:bind path="securityQuestion">
						&nbsp;<form:select path="securityQuestion" id="securityQuestion"  cssErrorClass="errorClass" tabindex="6">
								<form:option value="0" label=""/>
								<form:options items="${ securityQuestionList }" itemLabel="value" itemValue="id"/>								
							  </form:select>
							<span id="<c:out value='${ status.expression }Error'/>" class="errorClass">
								<c:out value="${ status.errorMessage }"/>
							</span>
						</spring:bind>
					</td>
					<td></td>
				</tr>
				<tr class="user_sequrityBlk">
					<td class="fieldLabelAMR">Security Answer: <font class="required">*</font></td>
					<td>
						<spring:bind path="securityQuestionAnswer">
							<form:input path="securityQuestionAnswer"  id="securityQuestionAnswer" size="25" maxlength="50" tabindex="8"  cssErrorClass="errorClass" />
							<span id="<c:out value='${ status.expression }Error'/>" class="errorClass">
								<c:out value="${ status.errorMessage }"/>
							</span>	
						</spring:bind>
					</td>					
			<td></td>
				</tr>
				<tr class="user_sequrityBlk">
					<td>
					</td>
					<td colspan="2" class ="fieldLabelAML" ><i> Please note that your answer is case sensitive</i></td>
				</tr>
				
				<tr>																														
					<td></td>
								
					<td align="right">																								
						<div id="registrationButton" >						
							<input type="submit" title="Password should be strong to enable the button." value="Register" id="submit1" tabindex="9" />							
						</div>						
					</td>
					<td class="camatch_lnav_nolink_small"><div id="registrationButtondisablemsg" >Is button  <a href="#" onClick="alert('All required fields should be filled out appropriately. Please review the error messages.');return false">Disabled?</a></div></td>
				<td></td>
				</tr>
			</table>
		</td>
	</tr>
</table>	
</form:form>
</div>

<div id="step3">
  <table width="650px">
	<tr>
		<td width="25"></td>
		<td width="625px">
			<table width="625" border="0" cellpadding="5px 0px 5px 0px" cellspacing="8px 0px 8px 0px">
				<tr align="left" valign="middle">                         
					<td align="left" class="FAQQuestion"><b>STEP 3: Complete your research health information</b> &nbsp;</td>
				</tr> 
				<tr align="left" valign="middle">
				    <td class="FAQText">
						After you create your HOW Study user account, you are redirected to "my personal home page".  From this page, you can access the HOW modules, which will contain the set of questions we will be asking you about your personal health.
					</td>				
				</tr>				
			</table>
		</td>
	</tr>
  </table>
</div>
