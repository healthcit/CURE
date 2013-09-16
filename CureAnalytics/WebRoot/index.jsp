<%--
Copyright (c) 2013 HealthCare It, Inc.
All rights reserved. This program and the accompanying materials
are made available under the terms of the BSD 3-Clause license
which accompanies this distribution, and is available at
http://directory.fsf.org/wiki/License:BSD_3Clause

Contributors:
    HealthCare It, Inc - initial API and implementation
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<%@include file="/WEB-INF/includes/taglibs.jsp" %>
<%@ page import="com.healthcit.analytics.service.UserService"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.springframework.context.ApplicationContext" %>

<%
ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
UserService userService = (UserService)context.getBean("userService");
%>

<c:set var="authType"><%= userService.getAuthType()%></c:set>
<c:choose>
	<c:when test="${authType == 'ldap' }">
		<c:url var="adminUrl" value="/ldap" />
	</c:when>
	<c:otherwise>
		<c:url var="adminUrl" value="/admin" />
	</c:otherwise>
</c:choose>


<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
    <title>CURE Analytics</title>

	<!--  <script src="http://code.google.com/js/codesite.pack.01312008.js" type="text/javascript"></script>
	<link href="http://code.google.com/css/codesite.pack.01312008.css" type="text/css" rel="stylesheet"> -->
	<link type="text/css" href="css/redmond/jquery-ui-1.8.4.custom.css" rel="stylesheet" />		
	<link type="text/css" href="css/visualization.css" rel="stylesheet" />
	<link type="text/css" href="css/jpaginate.css" rel="stylesheet" />
	<link type="text/css" href="css/upAndDownSelectBox.css" rel="stylesheet" />		
    <link href="css/codesite.pack.01312008.HOW.css" type="text/css" rel="stylesheet">
    <link rel="stylesheet" href="modalbox1.5.5/modalbox.css" type="text/css" media="screen" />	  

    <!--[if IE]><link rel="stylesheet" type="text/css" href="/css/iehacks.css" /><![endif]-->

    <script src="https://www.google.com/jsapi"></script>
	<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.4.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.tools.min.js"></script>    
	<script type="text/javascript" src="js/jquery-loadmask/jquery.loadmask.min.js"></script>
    <script type="text/javascript" src="js/load1.js"></script>
    <script type="text/javascript" src="js/datelib/date.js"></script> 
    <script type="text/javascript" src="js/utils.js"></script>
    <script type="text/javascript" src="js/utils_googleExt.js"></script>
	<script type="text/javascript">
		google.load("prototype", "1.6.0.2");
		google.load("scriptaculous", "1.8.1");
		google.load("visualization", "1", {packages:["table","linechart","areachart","barchart","piechart","imagelinechart","imageareachart","imagebarchart","imagepiechart","annotatedtimeline"]});
		jQuery.noConflict();
		jQuery(function(){
			jQuery('#dialog_link, ul#icons li').hover(
				function() { jQuery(this).addClass('ui-state-hover'); }, 
				function() { jQuery(this).removeClass('ui-state-hover'); }
			);
		});
	</script>
    <script type="text/javascript" src="modalbox1.5.5/modalbox.js"></script>
    <script type="text/javascript" src="http://systemsbiology-visualizations.googlecode.com/svn/trunk/src/main/js/load.js"></script>
    <script type="text/javascript" src="js/upAndDownSelectBox.js"></script>
	<script type="text/javascript">
	   systemsbiology.load("visualization", "1.0", {packages:["filterDataTableControl"]});
	</script>
    <script type="text/javascript" src="js/analyticsSearch.js"></script>
    <script type="text/javascript" src="js/main.js"></script>
	<script type="text/javascript" src="js/textboxlist/GrowingInput.js"> </script>
	<script type="text/javascript" src="js/textboxlist/TextboxList.js"> </script>
    <link type="text/css" href="js/textboxlist/TextboxList.css" rel="stylesheet" />
    <script type="text/javascript" src="js/reportWizard.js"></script>
    <script type="text/javascript" src="js/analyticsTables.js"></script>
    <script type="text/javascript" src="js/answers.js"></script>
    <script type="text/javascript" src="js/tableColumnTransformations.js"></script>
    <script type="text/javascript" src="js/dateFilterColumnControl.js"></script>
	<script type="text/javascript" src="js/filterDataTableControl1.js"></script>
    <script type="text/javascript" src="js/filterDataTableControlExt.js"></script>
    <script src="jquery-alerts/jquery.alerts.js" type="text/javascript"></script>
    <script type="text/javascript" src="js/fancy-menus.js"></script>
    <script type="text/javascript" src="js/jPaginate/jquery.paginate.js"></script>
    <script type="text/javascript" src="js/analyticsPaginator.js"></script>
    <script type='text/javascript' src='dwr/engine.js'> </script>
	<script type='text/javascript' src='dwr/util.js'> </script>
	<script type='text/javascript' src='dwr/interface/moduleMetadataService.js'> </script>
    
    <!-- PROPRIETARY CODE -->
    <script type="text/javascript" src="js/analyticsExt.js"></script>
	<script type='text/javascript' src='dwr/interface/reportTemplateService.js'> </script>
    <!-- END Proprietary Code -->
    
	<link href="jquery-alerts/jquery.alerts.css" rel="stylesheet" type="text/css" media="screen" />
	<link href="js/jquery-loadmask/jquery_loadmask.css" rel="stylesheet" type="text/css" />
	<link type="text/css" href="css/cacure.css" rel="stylesheet" />
			
	<link rel="SHORTCUT ICON" href="files/HCIT_Favicon.ico"/>
	
</head>

<body class="gc-documentation">

<meta charset="UTF-8" />

<div class="pageTitle"><span class="pageTitleImage">&nbsp;</span>&nbsp;</div>

<div class="demo">
	<!-- for now, redirect if the user is using an IE browser -->
	<c:choose>
	<c:when test="${fn:contains(header['User-Agent'],'MSIE')}">
		<h3 class="incompatBrowser">Incompatible Browser</h3>
		<script type="text/javascript">alert("Internet Explorer is not currently supported.");</script>
	</c:when>
	<c:otherwise>
	<div>
	    <div class="menu_container" id="menu_container">
	    	<div class="menu_ctn_content">
	    	<a href="#" class="current" id="welcome_container_tab" onclick="navigateToWelcome();">Home&nbsp;&nbsp;</a>
	    	<a href="#" id="table_container_tab" onclick="linkToCreateReports();">Create Reports&nbsp;&nbsp;</a>
	    	<a href="#" class="" id="reports_container_tab">View Reports&nbsp;&nbsp;</a>
	    	<sec:authorize ifAnyGranted="ROLE_ADMIN">
	    		<a href="${adminUrl}" class="" id="admin_tab">Admin&nbsp;&nbsp;</a>
	    	</sec:authorize>
	    	<a href="#" class="" id="wizard_container_tab" onclick="linkToReportWizard();">Report Wizard&nbsp;&nbsp;</a>
	    	<a href="<c:url value="/logout"/>">Logout</a>
	    	<!-- <a href="#" class="" id="documents_container_tab">Generate Documents&nbsp;&nbsp;</a> -->
	    	<!-- <a href="#" class="" id="documents_container_tab">Documents</a> -->
	    	</div>
	    </div>
	    <div class="submenu_container" id="submenu_container"></div>
		<!-- QUERY BUILDER HELP SECTION -->
	    <div id="help_container">
	    	<div id="welcomeMessageContainer" class="container">
	    		<div class="title">About Analytics</div>
	    		<div class="content">This application allows you to perform ad-hoc analysis of data previously stored by the CURE Data Collector.</div>
	    	</div>
	    	<div id="selectedQuestionAnswers" class="container" style="display:none;">
	    	<div id="help_container_header">Selected Questions</div>
	    	<div id="help_container_content">
	    		<div id="helpSearchBox">
	    			<!-- <legend>Search</legend> -->
				    <div>
				    <div id="searchFormStatusFld"></div>
				    <div class="searchSection" onclick="showSearchBoxes('table_container_content','table_container');">
				    <span class="searchIconImg">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
				    <span class="searchLabel" style="background: none; color: #262262; font-weight: bold;">Search for question</span></div>
				    </div>
				</div>
			    <fieldset id="independentQtnSet" class="selectAnswerSpan">
					<legend class="answerSpanHeader"><center><b>Groups</b><br/>(Independent Variables)</center></legend>
					<ul>
						<li class="none">(None selected)</li>
					</ul>
				</fieldset>
				<fieldset id="dependentQtnSet" class="selectAnswerSpan">
					<legend class="answerSpanHeader"><center><b>Aggregations</b><br/>(Dependent Variables)</center></legend>
					<ul>
						<li class="none">(None selected)</li>
					</ul>
				</fieldset>
				<fieldset id="filterQtnSet" class="selectAnswerSpan">
					<legend class="answerSpanHeader">Filter Variables</legend>
					<ul>
						<li class="none">(None selected)</li>
					</ul>
				</fieldset>
				<div id="selectedQuestionAnswerBtnPanel">
					<input type="button" name="DONE" value="PROCEED" onclick="generateReportsSection();"/>
					<input type="button" name="CLEAR" value="CLEAR" onclick="clearReportsSection();"/>
				</div>
			</div>
			</div>
		</div>   
		<!-- Filter Container -->
		<div id="filter_container_wrapper" style="display:none;">
			<a href="#" class="closeWrapper" onclick="jQuery('#filter_container_wrapper').fadeOut(1000);">
			<b>X</b>&nbsp;&nbsp;&nbsp;(Click to close)
			</a>
		    <div id="filter_container">				    	
		    </div>
	    </div>		
		<div id="page_container">	
			<!-- REPORTS SECTION -->
			<a name="reportresults"></a>
			<div id="reports_container" class="container" style="display:none;">
				<div class="reportsHeaderBanner">
				<ul class="menutabs">
					<li>
						<a href="#" onmouseover="mopen('menuM1')" onmouseout="mclosetime()">Dashboard</a>
						<div id="menuM1" >
							<ul>
							<li><a href="#reportresults" id ="reportType_0_tab">View Dashboard</a></li>
							</ul>
						</div>
					</li>
					<li>
						<a href="#" onmouseover="mopen('menuM2')" onmouseout="mclosetime()">Charts and Tables</a>
						<div id="menuM2" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
							<a href="#reportresults" onclick="generateCombinedResultSet(1)" id ="reportType_1_tab">Bar Chart</a>
							<a href="#reportresults" onclick="generateCombinedResultSet(2)" id ="reportType_2_tab">Area Chart</a>
							<a href="#reportresults" onclick="generateCombinedResultSet(3)" id ="reportType_3_tab">Pie Chart</a>
							<a href="#reportresults" onclick="generateCombinedResultSet(4)" id ="reportType_4_tab">Line Chart</a>
							<a href="#reportresults" onclick="generateCombinedResultSet(10)" id ="reportType_10_tab">Annotated Time Line</a>
							<a href="#reportresults" onclick="generateCombinedResultSet(5)" id ="reportType_5_tab">Table</a>
						</div>
					</li>
					<li>
						<a href="#" onmouseover="mopen('menuM3')" onmouseout="mclosetime()">Data Dump</a>
						<div id="menuM3" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
							<a href="#reportresults" onclick="exportData()" id="reportType_99_tab">Download Data Dump</a>
						</div>
					</li>
				</ul>
				</div>
				<div class="reportsContent">
					<!-- Page loading spinner -->
				    <div id="reports_container_spinner" class="loading">&nbsp;</div>
				    <!-- Report Controls -->
				    <div id="report_controls">				    	
				    </div>		
				    <!-- Chart/Report Containers -->	    
					<div id="reports_div_1" style="display:none;">
					</div>
					<div id="reports_div_2" style="display:none;">
					</div>
					<div id="reports_div_3" style="display:none;">
					</div>
					<div id="reports_div_4" style="display:none;">
					</div>
					<div id="reports_div_10" style="display:none;width:700px; height:350px;">
					</div>
				    <!-- Table -->
					<div id="reports_div_5" style="display:none;">
					</div>				    
				    <!-- Report Image Containers -->
					<div id="reportimages_div_6" style="display:none;">
					</div>
					<div id="reportimages_div_7" style="display:none;">
					</div>
					<div id="reportimages_div_8" style="display:none;">
					</div>
					<div id="reportimages_div_9" style="display:none;">
					</div>
				</div>
			</div>
			
			<!-- WELCOME SECTION -->
			<div id="welcome_container" class="container">
				<!-- New Reports -->
				<div id="generateNewReportsContainer">
					<div class="title">Generate Reports</div>
					<div class="content">
					<div class="item" onclick="linkToCreateReports();" id="link_create_new_reports"><span class="titleText">Generate New Reports</span></div>
					<!-- <div class="item last"><span class="titleText">Generate Documents</span></div> -->
					</div>
				</div>
				<!-- Saved Reports -->
				<div id="showSavedReportsContainer">
					<div class="title">Show Saved Reports</div>
					<div class="content">
					<div class="reportList">
					<div class="reports" id="savedReportsDiv">
						<script type="text/javascript">populateSavedQueryListSection();</script>						
					</div>
					</div>
					<div id="saved_reports_container_spinner" class="loading" style="display:none;">&nbsp;</div>
					</div>
					&nbsp;
				</div>
			</div>
			
			<!-- REPORTS WIZARD SECTION -->
			<%@include file="reportWizard.html" %>
			
			<!-- QUERY BUILDER SECTION -->
			<div id="table_container" class="tableContent container" style="display:none;">
				<div id="table_container_spinner" class="loading">&nbsp;</div>
				<div id="table_container_content" ></div>
			</div>
		</div>
	</div>
	</c:otherwise>
	</c:choose>
</div><!-- End demo -->

<div id="gc-container">
	<div id="codesiteContent">
		<div class="g-section g-tpl-170">
			<div id="gc-pagecontent">
				<script type="text/javascript">					
					google.setOnLoadCallback(preHandleCallback); // this is the Google Charts API method that is invoked upon loading of the page
					loadjscssfile("css/filterDataTableControl1.css", "css"); //dynamically load and add this .css file			
				</script>
			</div>
		</div>
	</div>


</div> <!-- end gc-containter -->

	<div id="gc-footer" dir="ltr">
		<div id="gc-footer-img"></div>
		<div class="text">
			&copy;2010 HCIT 
		</div>
	</div><!-- end gc-footer -->
					
</body>
</html>
