/**
-- Temp header
**/

/**

 * The user should define the following for each report (assuming the reports are identified as Report 1, 2, 3 etc):
 * 1. A global variable groupColumnHeaders123 (replace '123' with the Report Number) - an ordered list of column headers for the new chart/table
 * 2. A global variable groupRowHeaders123 (replace '123' with the Report Number) - an ordered list of row headers for the new chart/table
 * 3. A global variable chartArgs123 (replace '123' with the Report Number) - the configuration properties for the new chart/table
 * 4. A custom function "transform" which provides the data cell content for the new chart/table based on the given cell value from the source dataTable.
 *    It must have the following signature: 
 *          transform123(dataTable, rowIndex, columnIndex) //replace '123' with the Report Number
 *          where 
 *               dataTable - the source dataTable 
 *               rowIndex - the current rowIndex
 *               columnIndex - the current columnIndex
 * 5. A custom function "resetGlobalVariables123" (replace '123' with the Report Number) which resets the global variables used to generate the charts
 *    
 **/
 
var groupColumnHeaders1 = ['Time','Treatment A', 'Treatment B', 'Treatment C'];

var timeInterval = 3;

var groupRowHeaders1 = getTimeInterval(timeInterval);

var chartArgs1 = {width: 600, height: 400, legend: "bottom", title: 'Survivorship Trends', titleY: 'Number of Patients'};

function transform1(dataTable,rowHeaderIndex,columnHeaderIndex){

	var arr = dataTable.getFilteredRows([{column:4,value:groupColumnHeaders1[columnHeaderIndex]},{column:5,minValue:rowHeaderIndex*timeInterval}]);
	
	if ( format == 'number') {
	
		return arr.length;
	}
	else if ( format == 'percentage') {
		var total = getTotal(dataTable,groupColumnHeaders1[columnHeaderIndex]);
		
		return (arr.length/total) * 100;
	}
}

function resetGlobalVariables1(){
	groupRowHeaders1 = getTimeInterval(parseFloat(timeInterval));
	
	chartArgs1 = {width: 600, height: 400, legend: "bottom", title: 'Survivorship Trends', titleX: 'Time in months', titleY: format == 'number' ? 'Number of Patients' : 'Percentage of Patients'};
}

/**
 * Define any other global variables/functions that will be used by the "transform" function here
 **/ 
 var format = 'percentage';
 var totalCountsMappings = new Object();
  
 function getTimeInterval(timeInterval){
	timeInterval = timeInterval;
	
	arr = [];
	
	for ( index = 0; index <= 36; index += timeInterval ) {
		var start = index; 
		
		var end   = index + timeInterval;
		
		arr[index/timeInterval] = index == 36 ? '> 36' : start + '-' + end;
	}
	
	return arr;
 }
  
 function getTotal(dataTable,column) {
 
    if (!totalCountsMappings[column]) {
		var cnt = dataTable.getFilteredRows([{column:4,value:column}]).length;
		
		totalCountsMappings[column] = cnt;
	}
	
	return totalCountsMappings[column];
 }