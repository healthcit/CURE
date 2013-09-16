/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
var DIRECT_FORMS_SEPARATOR = '::';

jQuery(document).ready(function(){
	jQuery("#generate_entity").click(function(){
		var groupName = jQuery('#group_name').val();
		var groupId = jQuery('#newGroupId').val();
		var url='/caCure/api/';
		if(groupName !='')
		{
			url = url + 'GetNewEntityInNewGroup?name=' + groupName;
		}
		else if(groupId !='')
		{
			url = url + 'GetNewEntityInGroup?grpid=' + groupId;
		}
		else
		{
			alert("pease specify either Group Name or GroupId");
		}
		jQuery.get(url, function(data) {
			var entity_id = data;
			jQuery("#entityid").val(entity_id);
			fillModuleList(entity_id);
			updateFormAction(entity_id);
			enableButton();
		},'text');
	});
	jQuery("#groupId").change(function(){
		var changed_id = jQuery('#entityid').val();
		fillModuleList(changed_id);
		updateFormAction(changed_id);
		enableButton();
	});
	
	jQuery("#formandinstancedata").change(function(){
		updateFormOnFormAndInstanceChange();
	});
	
});
 
function fillModuleList(entity_id,showAlertMessage){	
	var group_id = getEntityGroupId();
	if (!group_id) return;
	clearDropDowns("both");
	jQuery.ajax({
		type: 'GET',
		url: '/caCure/api/'+entity_id+'/'+group_id+'/AllUserModules',
		dataType: 'xml',
		success: function(xml) {
			jQuery(xml).find('module').each(function(){
				jQuery("#moduleid").append("<option id='"+jQuery(this).attr("id")+"' value='"+jQuery(this).attr("id")+"'>"+jQuery(this).attr("name")+"</option>");
			});
			
			fillFormList(jQuery("#moduleid option:selected").attr("id"), xml);

			jQuery("#moduleid").change(function(){
				fillFormList(jQuery("#moduleid option:selected").attr("id"), xml);
			});
			
		}
	});
}
 
function fillFormList(module_id, xml){		
		clearDropDowns("formandinstancedata");
		jQuery(xml).find('module').each(function(){
			if(jQuery(this).attr("id")==module_id){
				jQuery(this).find('form').each(function(index,value){
					var form_id = jQuery(value).attr("id");
					var parentFormObj = jQuery(value).parent('form')[0];
					var parentFormInstances = [];
					
					if ( parentFormObj ) {
						
						jQuery( parentFormObj ).children('formInstances').find('instance').each(function(index,value){							
							parentFormInstances.push( value );							
						})	;			
					} 
										
					var formInstances = jQuery(value).children('formInstances')[0];					
					
					if ( formInstances ){
						var existingInstances = jQuery(formInstances).attr('existingInstances');
						var instanceGroup = jQuery(formInstances).attr('instanceGroup');						
						var minInstanceId = 1;
						var maxInstanceId = parseInt(existingInstances) + 1;
						
						// Populate existing form instances, and
						// Add an option to create a new form instance
						for ( var i = minInstanceId; i <= maxInstanceId; ++i ) {
							
							// The value of the dropdown is form_id::instance_id::parent_instance_id 
							// (parent_instance_id = 0 if there is no parent instance)
							
							var formName = jQuery(value).attr("name");
							var instanceName = formName + ': ' + (instanceGroup || 'Instance') + ' ' + i;
							
							
							if ( i < maxInstanceId ) { // existing instances
								
								var instanceObj = jQuery(formInstances).children('instance')[i-1];
								var instanceId = jQuery(instanceObj).attr('instanceId');
								var parentInstanceId = jQuery(instanceObj).attr('parentInstanceId') || 0;
								var creationDateStr = jQuery(instanceObj).attr('creationDate');
								if ( creationDateStr ) instanceName = new Date(creationDateStr).toString('yyyy-MM-dd-HHmm') + '-' + instanceId;
																
								var val = form_id + DIRECT_FORMS_SEPARATOR + instanceId + DIRECT_FORMS_SEPARATOR + parentInstanceId;
								
								// Determine the name of the parent instance (if any)
								var parentInstanceName = null;
								if ( parentInstanceId > 0 ) {
									for ( var k = 0; k < parentFormInstances.length; ++k ) {
										if ( jQuery(parentFormInstances[ k ]).attr('instanceId') == parentInstanceId ){
											parentInstanceCreationDateStr = jQuery(parentFormInstances[ k ]).attr('creationDate');
											if ( parentInstanceCreationDateStr ) parentInstanceName = new Date(parentInstanceCreationDateStr).toString('yyyy-MM-dd-HHmm') + '-' + parentInstanceId;
										}
									}
								}
								
								jQuery("#formandinstancedata").append("<option id='"+val+"' value='"+val+"'>"
										+ formName + ': ' + instanceName 
										+ (parentInstanceName ? ' (child of ' + parentInstanceName + ')': '') + "</option>");
								
							}
							else { // new instance

								if ( parentFormInstances.length > 0 ) { // parent form instances exist

									// Allow creation of a child instance for each parent instance
									for ( var index=0; index < parentFormInstances.length; ++index ){
										var parentFormInstanceObj = parentFormInstances[ index ];
										var parentCreationDateStr = jQuery(parentFormInstanceObj).attr('creationDate');
										var instanceId = i;
										var parentInstanceId = jQuery(parentFormInstanceObj).attr('instanceId');
										var parentInstanceName = new Date(parentCreationDateStr).toString('yyyy-MM-dd-HHmm') + '-' + parentInstanceId;
										var val = form_id + DIRECT_FORMS_SEPARATOR + instanceId + DIRECT_FORMS_SEPARATOR + parentInstanceId;
										jQuery("#formandinstancedata").append("<option id='"+val+"' value='"+val+"'>"+ formName + ': CREATE NEW INSTANCE FOR ' + parentInstanceName +"</option>");
									}
								}						 
								// Else create a top-level instance 
								else{
									val = form_id + DIRECT_FORMS_SEPARATOR + i + DIRECT_FORMS_SEPARATOR + 0;
									jQuery("#formandinstancedata").append("<option id='"+val+"' value='"+val+"'>"+ formName + ': CREATE NEW INSTANCE' +"</option>");
								}
							}
						}							
					}
				});
				
				// update hidden fields and form action as appropriate
				updateFormOnFormAndInstanceChange();
			}	
		});
	
}

function updateFormOnFormAndInstanceChange(){
	updateHiddenFields();
	updateFormAction(jQuery('#entityid').val());	
}
 
function clearDropDowns(dropdown_id){
	if(dropdown_id=="both"){
		jQuery("#moduleid").empty();
		jQuery("#formandinstancedata").empty();
	}
	else{
		jQuery("#"+dropdown_id).empty();
	}
}
 
function updateFormAction(entityId){
	var groupId = getEntityGroupId();
	if (!groupId) return;
	jQuery("#directForms").attr("action", entityId+"/"+ groupId + "/directForms.form");
	return false;
}

function updateHiddenFields(){
	var form_and_instance_id = jQuery("#formandinstancedata").val();
	if ( form_and_instance_id ) {
		var arr = form_and_instance_id.split(DIRECT_FORMS_SEPARATOR);
		jQuery('#formid').val(arr[0]);
		jQuery('#instanceid').val(arr[1] || 1);
		jQuery('#parentinstanceid').val(arr[2] || 0);
	}
}

function enableButton(){
	jQuery('#directForms .submit').removeAttr('disabled');
}

function getEntityGroupId(){
	return jQuery('#groupId').val();
}

function getFormId(){
	return jQuery('#formid').val();
}

function getInstanceId(){
	return jQuery('#instanceid').val();
}

function showEntityGroupRequiredMessage(){
	alert('You must specify a group ID.');
}
