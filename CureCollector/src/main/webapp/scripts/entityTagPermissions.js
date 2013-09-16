/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
//jQuery(document).ready(function(){
//	
//	fillSharingGroupList();
//	fillTags();
//	
//});

loadfunction = window.onload ? window.onload : function(){};
window.onload=function(){
	fillSharingGroupList();
	fillTags();
}

function fillSharingGroupList(){	
	//clearDropDowns("both");
	jQuery.ajax({
		type: 'GET',
		url: '/caCure/api/GetAllSharingGroups',
		dataType: 'xml',
		success: function(xml) {
			jQuery(xml).find('group').each(function(){
				jQuery("#groups").append("<option id='"+jQuery(this).attr("id")+"' value='"+jQuery(this).attr("id")+"'>"+jQuery(this).find('name').text()+"</option>");
			});		
				
			jQuery("#groups").change(function(){	
				
				fillEntitiesForGroup(jQuery("#groups option:selected").attr("id"));				
			});
		}
	});
}

function fillEntitiesForGroup(group_id){	
	//clearDropDowns("both");
	jQuery("#entities").empty();
	jQuery.ajax({
		type: 'GET',
		url: '/caCure/api/GetEntitiesForSharingGroup?grpid='+group_id,
		dataType: 'xml',
		success: function(xml) {			
			jQuery(xml).find('entity').each(function(){			    
				jQuery("#entities").append("<input type='radio' name='entity' value='"+jQuery(this).attr("id")+"'>"+ jQuery(this).attr("id")+"<br>");			
			});		
		}
	});
}
 
function fillTags(){	
	var i = 0;
	jQuery.ajax({
		type: 'GET',
		url: '/caCure/api/GetTags',
		dataType: 'xml',
		success: function(xml) {	
			jQuery(xml).find('tag').each(function(){
				 if(i%2 == 0) {
					jQuery("#tags").append("<tr class=\"datacellone\"><td><input type='checkbox' name='forms' id='event' value='"+jQuery(this).attr("id")+"'>"+jQuery(this).attr("id")+"&nbsp;</td></tr>");				
					jQuery("#tags").append("<tr class=\"datacellone\"><td><input type='checkbox' name='read' value='"+jQuery(this).attr("id")+"'>READ  <input type='checkbox' name='write' value='"+jQuery(this).attr("id")+"'>WRITE <input type='checkbox' name='approve' value='"+jQuery(this).attr("id")+"'>APPROVE  </td></tr>");
					jQuery("#tags").append("<tr></tr>");
				 } else {
					jQuery("#tags").append("<tr class=\"datacelltwo\"><td><input type='checkbox' name='forms' id='event' value='"+jQuery(this).attr("id")+"'>"+jQuery(this).attr("id")+"&nbsp;</td></tr>");				
					jQuery("#tags").append("<tr class=\"datacelltwo\"><td><input type='checkbox' name='read' value='"+jQuery(this).attr("id")+"'>READ  <input type='checkbox' name='write' value='"+jQuery(this).attr("id")+"'>WRITE <input type='checkbox' name='approve' value='"+jQuery(this).attr("id")+"'>APPROVE  </td></tr>");
					jQuery("#tags").append("<tr></tr>");
				 }
					i++;
			});	
			jQuery("#tags").append("</table>");
		}
	});
} 
