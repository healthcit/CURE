/*******************************************************************************
 *Copyright (c) 2013 HealthCare It, Inc.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the BSD 3-Clause license
 *which accompanies this distribution, and is available at
 *http://directory.fsf.org/wiki/License:BSD_3Clause
 *
 *Contributors:
 *    HealthCare It, Inc - initial API and implementation
 ******************************************************************************/
jQuery(document).ready(function(){
	jQuery(".showFilterLink").live("click", function() {
		moveClearBox();	
		adjustFilterHeights();
		enhanceFilter();
		autoScrollTabs();
		fancyDates();
	});

});

//Highlight a filter's label when it is being used
function enhanceFilter(){
	jQuery("#org-systemsbiology-visualization-filtercontainer-filteritemcontainers select").each(function(index){
		jQuery(this).change(function(){
			if(jQuery("option:selected", this).html()){
				highlightFilter(index);
			}
			else{
				removeFilter(index);
			}
		});
	});
}

//move the "clear buttons" next to filter control
function moveClearBox(){
	jQuery("#org-systemsbiology-visualization-filtercontainer-filterlist li button").each(function(index){
	//id doesn't always match up with expected index, this will always place clear box in correct box
		var button_id = jQuery(this).attr("id");
		var button_index = button_id.substring("org-systemsbiology-visualization-filteritem-resetter-".length, button_id.length);
		jQuery(this).appendTo(jQuery("#org-systemsbiology-visualization-filteritem-container-"+button_index)).addClass("moved_clear_box").before("<br>");
		});
}

function adjustFilterHeights(){
	jQuery("#org-systemsbiology-visualization-filtercontainer-filteritemcontainers div").each(function(){
		var currentFilterBox = jQuery(this);
		if(jQuery(".org-systemsbiology-visualization-filteritem-select", currentFilterBox).length>0){
			currentFilterBox.css("height", "115px");
		}
		if(jQuery(this).children("span").length>1){
			currentFilterBox.css("height", "160px");
		}
	});
}

//add jQueryUI calendar widget to date filters
function fancyDates(){
	jQuery("#filter_container .dateformat").each(function(index){
		jQuery(this).prev("input").datepicker({
			changeMonth: true,
			changeYear: true
		});
	});
}

function autoScrollTabs(){	
	//aribrarilly do autoscrolling if more than 15 filter buttons, can change to any number
	if(jQuery("#org-systemsbiology-visualization-filtercontainer-filterlist li a").length>15){
		jQuery('#org-systemsbiology-visualization-filtercontainer-header-titlelink').append(" <span id='scroll-message'>(use your mouse to horizontally scroll to more tabs)</span>");
	
		var ul = jQuery('#org-systemsbiology-visualization-filtercontainer-filterlist');

		ul.css("padding-left", "100px");
		ul.css("width", "2000px");
    
		var div = jQuery('#org-systemsbiology-visualization-filtercontainer-menu');
		var divWidth = div.width();

		div.css({overflow: 'hidden'});

		var lastLi = ul.find('li:last-child');

		//When user move mouse over menu
		div.mousemove(function(e){
			var ulWidth = lastLi[0].offsetLeft + lastLi.outerWidth() + 200;
			var left = (e.pageX - div.offset().left) * (ulWidth+divWidth) / divWidth;
			div.scrollLeft(left);
		});
	}
}

function highlightFilter(index){
	jQuery("#org-systemsbiology-visualization-filtercontainer-filterlist li a:eq("+index+")").addClass("active_filter_highlight");
}

/*remove filter highlight class*/
function removeFilter(index){
	if(index==undefined){
		jQuery("#org-systemsbiology-visualization-filtercontainer-filterlist li a.active_filter_highlight").each(function(){
			jQuery(this).removeClass("active_filter_highlight");
		});
	}
	else{
		jQuery("#org-systemsbiology-visualization-filtercontainer-filterlist li a:eq("+index+")").removeClass("active_filter_highlight");
	}
}
