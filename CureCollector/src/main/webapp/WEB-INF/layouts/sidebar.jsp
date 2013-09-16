<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>
<tiles:importAttribute name="content"/>
<tiles:importAttribute name="menuList"/>

<div id="sidebar1">
	<!-- Start of Content Page (if applicable) -->
	<tiles:insertAttribute name="content"/>
	<!-- End of Content Page -->
	<!-- Start of Menu -->
	<tiles:useAttribute id="list" name="menuList" classname="java.util.List"/>

	<c:if test="${ not empty list }">
		<div id="left-menu">
			<div class="lh_menu">
				<c:forEach items="${ list }" var="menuItem">
					<div class="lh_menu_element">
						<a href="${ appPath }${ menuItem.link }">${ menuItem.value }</a>
					</div>
				</c:forEach>
			</div>
		</div>
	</c:if>
	<!-- End of Menu -->
</div>
<!-- end #sidebar1 -->
