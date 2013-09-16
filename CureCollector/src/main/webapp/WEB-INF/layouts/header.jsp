<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>
  <div id="header">
  	<div id="primDivContainer">
    	<div id="primDiv">
			<!-- Begin Module Navigation -->
				<!-- Add a list here for navigation-->
			<!-- End Module Navigation -->	
		</div>
    <!-- end #primDivContainer --></div>
		<div id="utilDiv">
        	<div id="bannerLinks">
        		<authz:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN,ROLE_DEPLOYER">
        			<a href="${appPath}/admin/admin.view">Home</a> |
            		<a href="${appPath}/j_spring_security_logout">Logout</a> |
            	</authz:authorize>
            
			</div>
		</div>
  </div><!-- end #header -->
