/**
-- Temp header
**/

/**
 * This script is used to generate a DataView whose columns
 * consist of data which has been transformed from its original format in the DataTable
 * into a new form (with new formatting, number/order of columns, etc.)
 * @author oawofolu
 */

// Transformation types
var COMPLETE_DATE = 1;
var MONTH_AND_YEAR_ONLY = 2;
var MONTH_AND_DAY_ONLY = 3;
var YEAR_ONLY = 4;
var DAY_OF_WEEK_ONLY = 5;
var MONTH_ONLY = 6;

// Format types
var MONTH_DATE_4DIGITYR_FORMAT = 1;
var MONTH_DATE_2DIGITYR_FORMAT = 2;
var MONTH_4DIGITYR_FORMAT = 3;
var MONTH_2DIGITYR_FORMAT = 4;
var MONTH_DAY_FORMAT = 5;
var MONTH_FORMAT = 6;
var DAY_FORMAT = 7;

com.healthcit.TableColumnTransformations = Class.create({
	initialize: function(oColumnIndex,oTransformType,oFormatType){
		this.columnIndex = oColumnIndex;
		this.transformType = oTransformType;
		this.formatType = oFormatType;
	},
	transform: function(dataTable,rownum){
	},
	format: function(dataTable){
	},
	destroy: function(){
		this.columnIndex = null;
		this.transformType = null;
		this.formatType = null;
	}
});

com.healthcit.DateTableColumnTransformations = Class.create(
	com.healthcit.TableColumnTransformations,{
	initialize: function($super,oColumnIndex,oTransformType,oFormatType){
		$super(oColumnIndex,oTransformType,oFormatType);
	},
	transform: function(dataTable,rownum){
		var cellValue = dataTable.getValue(rownum,this.columnIndex);
		if ( ! cellValue ) return cellValue;
		
		if (this.transformType == COMPLETE_DATE){ 
			;
		}
		if (this.transformType == MONTH_AND_YEAR_ONLY){ 
			cellValue.setDate(1);
		}
		if (this.transformType == MONTH_AND_DAY_ONLY){ 
			cellValue.setYear(2000);
		}
		if (this.transformType == YEAR_ONLY){
			cellValue.setDate(1);
			cellValue.setMonth(1);
		}
		if (this.transformType == DAY_OF_WEEK_ONLY){
			cellValue.setMonth(1);
			cellValue.setYear(2000);
		}
		if (this.transformType == MONTH_ONLY){
			cellValue.setDate(1);
			cellValue.setYear(2000);
		}
		return cellValue;
	},
	format: function(dataTable){
		var formats = { 1: "MM/dd/yyyy", /*month/date/4-digit year*/ 
				        2: "MM/dd/yyyy", /*month/date/2-digit year*/
				        3: "MM/yyyy", /*month/4-digit year*/
				        //4: "MM/yy", /*month/2-digit year*/
				        5: "MMMM yyyy", /*calendar month,4-digit year*/
				        6: "MMMM dd", /*calendar month,date*/
				        7: "MMMM", /*calendar month*/
				        8: "dddd", /*day of week*/
				        9: "yyyy" /*year*/};
		
		
		var formatToUse;
		if ( this.transformType == MONTH_AND_DAY_ONLY ) formatToUse = formats[ '6' ];
		else if ( this.transformType == MONTH_ONLY ) formatToUse = formats[ '7' ];
		else if ( this.transformType == YEAR_ONLY ) formatToUse = formats['9'];
		else if ( this.transformType == DAY_OF_WEEK_ONLY ) formatToUse = formats[ '8' ];
		else if ( this.formatType ) formatToUse = formats[ this.formatType ];
		
		if ( formatToUse ) {
			var visualizationFormatter = new google.visualization.DateFormat({pattern:formatToUse});
			visualizationFormatter.format(dataTable,this.columnIndex);
		}
		return formatToUse;
	}		
});
	