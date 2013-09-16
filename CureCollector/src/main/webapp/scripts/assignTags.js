/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
var _filledContainers = new Array();
var selectedId = null;
function dialog(containerId,afterLoadHandler, elementId, dialogParams, loadOnce, onCloseButtonClick) {
	selectedId = elementId;
	var dialogContainer = $('#' + containerId);
	var contains = false;
	if(loadOnce) {
		contains = jQuery.inArray(dialogContainer[0], _filledContainers) > -1;
		if(!contains) {
			_filledContainers.push(dialogContainer[0]);
		}
	}
	
	if(onCloseButtonClick) {
		var onOpen = dialogParams.open;
		dialogParams.open = function() {
			var dc = dialogContainer;
			dc.prev().find('span.ui-icon-closethick').bind('click', onCloseButtonClick);
			if(onOpen) {
				onOpen();
			}
		};
	}
	dialogContainer.dialog(dialogParams);
	
//	if(urlToLoadFrom && !contains) {
//		var loadCallBack = function(responseText, textStatus, req) {
//			if(textStatus != "error") {
//				if(afterLoadHandler) {
//					afterLoadHandler();
//				}
//			} else {
//				alert('Error!');
//				_filledContainers.pop(dialogContainer[0]);
//			}
//		};
//		dialogContainer.load(urlToLoadFrom, loadCallBack);
//	} else {
//		afterLoadHandler();
//	}
}

function closeDiv(containerId)
{
	var dialogContainer = $('#' + containerId);
	dialogContainer.dialog('close');
	closeCreateTag();
}
function closeCreateTag()
{
	$('#newTag').val("");
	selectedId= null;
}

function addNewTag()
{
	
	var tagValue = $('#newTag').val();
	tagValue = tagValue.replace(/^\s+|\s+$/g,"");
	if(tagValue.match(/\S/))
	{
//      var exists = 0 != $('#tags_0 option[value='+tagValue+']').length;
	  var exists = false;
	  $('#tags_0 option').each(function(){
	      if (this.value.toLowerCase() == tagValue.toLowerCase()) {
	          exists = true;
	          return false;
	      }
	  });

      if(!exists)
      {
		  var elements = $("select"); 
		  for(var i=0; i<elements.length; i++)
		  {
			  var elSel = elements[i];
			  var elOptNew = document.createElement('option');
			  elOptNew.text = tagValue;
			  elOptNew.value = tagValue;
			  if(elSel.id == selectedId)
			  {
			 	elOptNew.selected =true;  
			  }
			  
			  try {
				  elSel.add(elOptNew, null); // standards compliant; doesn't work in IE
			 }
			 catch(ex) {
			    elSel.add(elOptNew); // IE only
			 }
		  }
      }
    }

	  
}
