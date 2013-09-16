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
var groupColumnHeaders = [];
var groupRowHeaders = [];

function setUpDataView(dataTable,reportNumber){

	if ( !reportNumber) reportNumber = 1;
	
	// call the resetGlobalVariables method to reset the global variables used to generate the charts
	resetGlobalVariables = window['resetGlobalVariables' + reportNumber ];

	resetGlobalVariables();
	
	// set up the groupColumnHeaders, groupRowHeaders, transform function
	groupColumnHeaders = window['groupColumnHeaders'+reportNumber];
	
	groupRowHeaders = window['groupRowHeaders'+reportNumber];
	
	transform = window['transform'+reportNumber];
	
	// set up the data table
	var dataTable2 = new google.visualization.DataTable();
		
	for ( index=0; index<groupColumnHeaders.length; ++index){
		dataTable2.addColumn(index == 0 ? 'string' : 'number', groupColumnHeaders[index]);
	}
	
	dataTable2.addRows(groupRowHeaders.length);
	
	setRowHeaderContent(dataTable2);
	
	for ( index = 0; index<groupRowHeaders.length; ++index ) {	
		for ( index2=1; index2<groupColumnHeaders.length; ++index2 ) {	 
		     dataTable2.setCell(index,index2,transform(dataTable,index,index2));
	     }
	}	
	
	return dataTable2;
}

function setRowHeaderContent(dataTable){

	for ( index = 0; index<groupRowHeaders.length; ++index ) {	
		dataTable.setCell(index,0,groupRowHeaders[index]);
	}
}

function linkNonControlledVisualizations(sourceVisualization,arr,reportNumber) {
	sourceVisualization['draw_old'] = sourceVisualization['draw'];
	
	sourceVisualization['draw'] = function() {
	
		sourceVisualization.draw_old(arguments[0],arguments[1]);
		
		var dt = arguments[0];
		
		var new_dt = setUpDataView(dt,reportNumber);
		
		for ( index=0; index < arr.length; ++index ){
			arr[index].draw(new_dt, window['chartArgs'+reportNumber]);
		}
	}	
}

function setUpChartControls(){
	var parentElm = document.getElementById('cChartControlContainer');
	
	var radioButtons = parentElm.getElementsByTagName('input');
	
	for ( i = 0; i < radioButtons.length; ++i ) {				
		
		if ( !radioButtons[i].onclick ) {
			radioButtons[i].onclick = function(){
				var propertyName = this.name;
		
				var propertyValue = this.value;
				
				window[propertyName] = propertyValue;
								
				resetCharts();
			}
		}
	}
}

function resetCharts(){
	filterControl.applyFilter();
}




