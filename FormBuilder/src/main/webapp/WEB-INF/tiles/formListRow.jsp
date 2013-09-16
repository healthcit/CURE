<%--
Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
Proprietary and confidential
--%>
<%@page import="com.healthcit.cacure.web.controller.FormListController"%>
<%@page import="com.healthcit.cacure.utils.Constants"%>
<%@ include file="/WEB-INF/includes/taglibs.jsp"%>



<%
	pageContext.setAttribute("newLineChar", "\n");
%>

<%-- <c:set var="item" value="${param.item}" />
<c:set var="moduleId" value="${param.moduleId}" />

<c:set var="formExportUrl">${appPath}/<%= Constants.FORM_EXPORT_URI %></c:set>
--%>
<ul>
<c:forEach items="${forms}" var="item">
<li id="${item.id}">
<c:url var="questionListUrl"
					value="<%= Constants.QUESTION_LISTING_URI %>" context="${appPath}">
					<c:param name="moduleId" value="${moduleId}" />
					<c:param name="formId" value="${item.id}" />
					<c:param name="lckUser" value="${item.lockedBy.userName}" />
				</c:url>
<a style="width: ${param.formNameColumnWidth}px;" class="jstree-grid-col-0 " href="${questionListUrl}">${item.name}</a>
<c:if test="${!item.libraryForm}">
					<div class="questionListQuestionIcon">
						<c:if test="${fn:length(item.formSkipRule.questionSkipRules) > 0}">
							<a href="javascript:ReverseContentDisplay('${item.id}.skipsDiv')"><img
									src="${appPath}/images/skip.gif" alt="Skip Pattern"
									title="Skip Pattern" border="0" />
							</a>
						</c:if>
					</div>
					<c:if test="${fn:length(item.formSkipRule.questionSkipRules) > 0}">
						<div id="${item.id}.skipsDiv"
							class="questionListHiddenValue questionListSkipList">
							<table class="skipRulesDescriptionTable">
								<c:forEach items="${item.formSkipRule.questionSkipRules}"
									var="curSkip" varStatus="stat">
									<tr>
										<td>
											<c:if test="${not stat.first}">${item.formSkipRule.logicalOp}</c:if>
										</td>
										<td>
											${fn:replace(curSkip.description, newLineChar, '<br/>')}
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>
					</c:if>
				</c:if>

<div class="jstree-grid-cell jstree-grid-cell-regular jstree-grid-col-1" style="display: inline-block;  width: 68px; padding-left: 7px;">
<span style="margin-right: 0px; display: inline-block;" >${item.author.userName}</span>
</div>
<c:if test="${!moduleCmd.library}">
<div class="jstree-grid-cell jstree-grid-cell-regular jstree-grid-col-2" style="display: inline-block;  width: 85px; padding-left: 7px;">
<span style="margin-right: 0px; display: inline-block;" >

	<spring:message code="formstatus.${item.status}" />
	
</span>
</div>
</c:if>
<div class="jstree-grid-cell jstree-grid-cell-regular jstree-grid-col-3" style="display: inline-block;  width: 130px; padding-left: 7px;">
<span style="margin-right: 0px; display: inline-block;" >
<fmt:formatDate value="${item.updateDate}" type="both"
					timeStyle="short" dateStyle="short" />
</span></div>

<div class="jstree-grid-cell jstree-grid-cell-regular jstree-grid-col-4" style="display: inline-block;  width: 110px; padding-left: 7px;">
<span style="margin-right: 0px; display: inline-block;">${item.lastUpdatedBy.userName}</span></div>

<div class="jstree-grid-cell jstree-grid-cell-regular jstree-grid-col-5 " style="display: inline-block;  width: 30px; padding-left: 7px;">
<span style="margin-right: 0px;  display: inline-block;" class="exportModule" alt="Export '${item.name}'" title="Export '${item.name}'">
	             <a href="javascript:void(0);"
	             onclick="$('#exportFormDialog').dialog({close: function(event, ui) { window.location.reload(); resetFormExportUrls()},  open: function(event, ui) { generateFormExportUrl('${item.id}'); }, modal: true, height: 500, width: 700});">&nbsp;&nbsp;</a>
	             <!--  <a href="${formExportUrl}?id=${item.id}">&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
	             
	             </span>
	    </div>
						
<div class="jstree-grid-cell jstree-grid-cell-regular jstree-grid-col-6" style="display: inline-block;  width: 20px; padding-left: 7px;vertical-align:top;">
<span style="margin-right: 0px; display: inline-block;" >
<c:url var="deleteFormUrl"
					value="<%= Constants.QUESTIONNAIREFORM_LISTING_URI %>"
					context="${appPath}">
					<c:param name="moduleId" value="${moduleId}" />
					<c:param name="formId" value="${item.id}" />
					<c:param name="delete" value="true" />
				</c:url>
				<authz:authorize ifAnyGranted="ROLE_ADMIN">
					<c:set var="isAdmin" value="true" />
				</authz:authorize>
				<authz:authorize ifAnyGranted="ROLE_APPROVER">
					<c:set var="isApprover" value="true" />
				</authz:authorize>
				<a href="javascript::void(0);"
					style="display:${!cacure:contains(nonEmptyForms, item.id) && isEditable && item.editable || isAdmin ? 'block' : 'none'};"
					onclick="deleteForm(${item.id}, '${deleteFormUrl}');"> <img
						src="images/delete.png" title="delete" height="18" width="18"
						style="border: none;" /> </a></span></div>
						
<div class="jstree-grid-cell jstree-grid-cell-regular jstree-grid-col-7" style="display: inline-block; width: 20px; padding-left: 7px;vertical-align:top;">
<span style="margin-right: 0px; display: inline-block;" >
<c:url var="formEditUrl" value="${cacure:objectUrl(item, 'EDIT')}"
					context="${appPath}">
					<c:param name="moduleId" value="${moduleId}" />
					<c:param name="id" value="${item.id}" />
				</c:url>
				<c:if test="${isEditable and item.editable or ((isApprover or isAdmin) and (item.status == 'IN_REVIEW' or item.status == 'APPROVED'))}">
					<a href="${formEditUrl}" class="editFormLink"> <img src="images/edit.png" height="18" width="18" alt="Edit" style="border: none;"  /> </a>
				</c:if></span></div>
						

<div class="jstree-grid-cell jstree-grid-cell-regular jstree-grid-col-8" style="display: inline-block;  width: 20px; padding-left: 7px; vertical-align:top;">
<c:if test="${not item.libraryForm}">
<span style="margin-right: 0px; display: inline-block;" >
<c:choose>
						<c:when test="${item.locked}">
							<c:choose>
								<c:when
									test="${item.lockedBy.userName == pageContext.request.userPrincipal.name}">
									<!-- Locked by current user -->
									<div class="lockedByYouForm" title="This item is locked by you"
										onclick="toggleLock(this, '${item.id}');">
										&nbsp;
									</div>
								</c:when>
								<c:when
									test="${isAdmin}">
									<!-- Locked by another user -->
									<div class="lockedBySomebodyElseForm"
										title="This item is locked by ${item.lockedBy.userName}"
										onclick="toggleLock(this, '${item.id}');">
										&nbsp;
									</div>
								</c:when>
								<c:otherwise>
									<!-- Locked by another user -->
									<div class="lockedBySomebodyElseForm"
										title="This item is locked by ${item.lockedBy.userName}">
										&nbsp;
									</div>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<!-- Unlocked -->
							<div class="unlockedForm" title="The item is unlocked"
								<authz:authorize ifAnyGranted="ROLE_ADMIN, ROLE_AUTHOR">onclick="toggleLock(this, '${item.id}');"</authz:authorize>
							>
								&nbsp;
							</div>
						</c:otherwise>
					</c:choose></span></c:if>
</div>


<div class="jstree-grid-cell jstree-grid-cell-regular jstree-grid-col-9 jstree-grid-cell-last" style="display: inline-block;  width: 20px; padding-left: 7px;vertical-align:top;">
<span style="margin-right: 0px; display: inline-block;" class="c0">
<authz:authorize ifAnyGranted="ROLE_ADMIN, ROLE_LIBRARIAN">
						<c:if test="${addToLibraryAvailability[item.id]}">
							<c:url var="addToLibraryUrl"
								value="<%= Constants.ADD_FORM_TO_LIBRARY_URI %>"
								context="${appPath}">
								<c:param name="<%= Constants.FORM_ID %>" value="${item.id}" />
								<c:param name="<%= Constants.MODULE_ID %>" value="${moduleId}" />
							</c:url>
							<a
								href="javascript:addToLibrary('${item.name}', '${addToLibraryUrl}')">
								<img src="images/library_icon.png" title="Add to library"
									onclick=""  /> </a>
						</c:if>
					</authz:authorize>
</span></div>				
		
<div style="clear: both;"></div>
<c:if test="${!item.libraryForm }">
<c:if test="${not empty item.children }">
<c:set var="forms" value="${item.children}" scope="request"/>
<jsp:include page="formListRow.jsp">
<jsp:param name="formNameColumnWidth" value="${param.formNameColumnWidth-18}"/>
</jsp:include>
</c:if>
</c:if>
</li>
</c:forEach>

</ul>
 
