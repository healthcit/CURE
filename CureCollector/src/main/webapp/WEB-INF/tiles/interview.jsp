<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>
  <div id="content">
  <div id="sidebar1">
    
<!-- Start of Menu -->
	<div id="left-menu">
			<div class="lh_menu">
              <div class="lh_menu_element"><a href="http://localhost:8080/HOW/PatientEditProfile.do">About Me</a></div>
              <div class="lh_menu_element" id="current"><a href="http://localhost:8080/HOW/PatientEditHealthToday.do">My Health as of Today</a></div>
              <div class="lh_menu_element"><a href="http://localhost:8080/HOW/XFormsLoader.do?xfPath=address-xform.xml">XForms</a></div>
            </div>
      </div>
<!-- End of Menu -->
    
    
    
  <!-- end #sidebar1 --></div>
  <div id="mainContent">
  
  	<div id="tocDiv">
    	<!-- Section Tabs -->
          <ol id="toc">
            <li><a href="${appPath}/Main.do"><span>Demo Main</span></a></li>
            <li class="current"><a href="${appPath}/Interview.do?xfPath=xf.xml"><span>Sample XForms</span></a></li>
          </ol>
          <!-- Section Tabs -->
    </div>
  	<div id="utilities">
    	<!-- size changer-->
 		<div id="u-fonts">
		  <div class="font-label">Font Size:</div>
		  <a href="javascript:void(0)" onclick="fontSizer(12,'px'); return false;" class="font-sm">&nbsp;</a>
          <a href="javascript:void(0)" onclick="fontSizer(15,'px'); return false;" class="font-med">&nbsp;</a>
          <a href="javascript:void(0)" onclick="fontSizer(18,'px'); return false;" class="font-lg">&nbsp;</a>
  		<!-- end of size changer--></div>
    
  <div id="u-icons">
  <a href="/HOW/Home/AboutHOWPrintPHR.do" target="_blank" class="print">Print</a>
   </div>
   
   <!-- end of utilities--></div>
 <div id="title"><h1>Sample XForms generated question</h1></div>

    <h2>Your Personal Data</h2>
    <p><!--  Here goes the XForms body -->
	${xformBody}    
    </p>
	
	</div><!-- end #mainContent -->
	<!-- This clearing element should immediately follow the #mainContent div in order to force the #container div to contain all child floats -->
	<br class="clearfloat" />
    </div><!-- end #content -->
  

