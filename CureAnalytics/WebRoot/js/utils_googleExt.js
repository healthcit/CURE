/**
-- Temp header 
**/
/**
 * 
 * Extensions to the Google APIs
 * @author oawofolu
 */

// Utility function which checks if the given datatable has duplicate keys
function hasDuplicateKeysInDataTable( dataTable, key ) {
	return dataTable.getNumberOfRows() != google.visualization.data.group(dataTable, key ).getNumberOfRows();	
}

//This function can be used instead of the Google API function google.visualization.data.join().
// In cases where the second datatable (dt2) contains duplicate keys, the standard join function will not work
// (see documentation).
// This function provides a workaround for this.
// NOTE: "leadingCols" is a new parameter which specifies the first columns in the resulting dataset (it may or may not match the key columns).
function customizedJoin(dt1, dt2, joinType, keyCols, dt1Cols, dt2Cols, leadingCols){
	// split the keyCols parameter into separate arrays for ease of use
    var dt1Keys = [];
    var dt2Keys = [];
    for (i = 0; i < keyCols.length; i++) {
        dt1Keys.push(keyCols[i][0]);
        dt2Keys.push(keyCols[i][1]);
    }
    if (!leadingCols) leadingCols = [];
    
    // create a new DataTable object
    var results = new google.visualization.DataTable();
    // add the selected columns
    for (i=0; i < leadingCols.length; ++i) {
    	results.addColumn(dt1.getColumnType(leadingCols[i]), dt1.getColumnLabel(leadingCols[i]));
    }
    for (i = 0; i < dt1Keys.length; i++) {
        if ( !arrayContains( leadingCols, dt1Keys[i] ) ) results.addColumn(dt1.getColumnType(dt1Keys[i]), dt1.getColumnLabel(dt1Keys[i]));
    }
    for (i = 0; i < dt1Cols.length; i++) {
    	if ( !arrayContains( leadingCols, dt1Cols[i] ) ) results.addColumn(dt1.getColumnType(dt1Cols[i]), dt1.getColumnLabel(dt1Cols[i]));
    }
    for (i = 0; i < dt2Cols.length; i++) {
    	if ( !arrayContains( leadingCols, dt2Cols[i] ) ) results.addColumn(dt2.getColumnType(dt2Cols[i]), dt2.getColumnLabel(dt2Cols[i]));
    }
    
    // parse dt1 for key vals, then check dt2 for corresponding rows
    var keyVals = [];
    var dt2Rows = [];
    var dt2RowsAdded = [];
    var dtFilter = [];
    var rowData = [];
    for (i = 0; i < dt1.getNumberOfRows(); i++) {
        // grab the values from dt1 to join on
        for (j = 0; j < dt1Keys.length; j++) {
            keyVals[j] = getCellObject(dt1, i, dt1Keys[j]);
        }
        
        // build a filter for dt2
        for (j = 0; j < dt2Keys.length; j++) {
            dtFilter.push({column: dt2Keys[j], value: keyVals[j]});
        }
        
        // get the filtered rows
        dt2Rows = dt2.getFilteredRows(dtFilter);
        // join the rows together if left or full join, or inner or right join where matches from dt2 were found
        if (((joinType == 'inner' || joinType == 'right') && dt2Rows.length > 0) || (joinType == 'left' || joinType == 'full')) {
            if (dt2Rows.length == 0) {
                // if no matches from dt2 were found, add in dt1 data with nulls for dt2 columns
            	for (k=0; k < leadingCols.length; ++k) {
            		if ( arrayContains( dt1Keys, leadingCols[k] ) || arrayContains( dt1Cols, leadingCols[k] ) ) {
            			rowData.push(getCellObject(dt1, i, leadingCols[k]));
            		}
            		else {
            			rowData.push(null);
            		}
            	}
                for (k = 0; k < dt1Keys.length; k++) {
                    if ( !arrayContains(leadingCols,dt1Keys[k]) ) rowData.push(keyVals[k]);
                }
                for (k = 0; k < dt1Cols.length; k++) {
                	if ( !arrayContains(leadingCols,dt1Cols[k]) ) rowData.push(getCellObject(dt1, i, dt1Cols[k]));
                }
                for (k = 0; k < dt2Cols.length; k++) {
                	if ( !arrayContains(leadingCols,dt2Cols[k]) ) rowData.push(null);
                }
                results.addRow(rowData);
                rowData = [];
            }
            else {
                // add 1 row for each matching row in dt2
                // dt1 data will be the same for all
                for (j = 0; j < dt2Rows.length; j++) {
                    if (dt2RowsAdded.indexOf(dt2Rows[j]) == -1) {
                        dt2RowsAdded.push(dt2Rows[j]);
                    }
                    for (k=0; k < leadingCols.length; ++k) {
                    	if ( arrayContains( dt1Keys, leadingCols[k] ) || arrayContains( dt1Cols, leadingCols[k] ) ) {
                			rowData.push(getCellObject(dt1, i, leadingCols[k]));
                		}
                    	else if ( arrayContains( dt2Keys, leadingCols[k] ) || arrayContains( dt2Cols, leadingCols[k] ) ) {
                			rowData.push(getCellObject(dt2, dt2Rows[j], leadingCols[k]));
                		}
                    	else {
                    		rowData.push(null);
                    	}
                	}
                    for (k = 0; k < dt1Keys.length; k++) {
                    	if ( !arrayContains(leadingCols,dt1Keys[k]) ) rowData.push(keyVals[k]);
                    }
                    for (k = 0; k < dt1Cols.length; k++) {
                    	if ( !arrayContains(leadingCols,dt1Cols[k]) ) rowData.push(getCellObject(dt1, i, dt1Cols[k]));
                    }
                    for (k = 0; k < dt2Cols.length; k++) {
                    	if ( !arrayContains(leadingCols,dt2Cols[k]) ) rowData.push(getCellObject(dt2, dt2Rows[j], dt2Cols[k]));
                    }
                    results.addRow(rowData);
                    rowData = [];
                }
            }
        }
        
        keyVals = [];
        dtFilter = [];
    }
    
    rowData = [];
    
    if (joinType == 'full' || joinType == 'right') {
        // create a view based on dt2, hide the rows we've added already, then add what's left
        var v2 = new google.visualization.DataView(dt2);
        v2.hideRows(dt2RowsAdded);
        for (i = 0; i < v2.getNumberOfRows(); i++) {
        	for (j=0; j < leadingCols.length; ++j) {
        		if ( arrayContains( dt2Keys, leadingCols[j] ) || arrayContains( dt2Cols, leadingCols[j] ) ) {
        			rowData.push(getCellObject(v2, i, leadingCols[j]));
        		}
        		else{
        			rowData.push(null);
        		}
        	}
            for (j = 0; j < dt2Keys.length; j++) {
            	if ( !arrayContains(leadingCols,dt2Keys[j]) ) rowData.push(getCellObject(v2, i, dt2Keys[j]));
            }
            for (j = 0; j < dt1Cols.length; j++) {
            	if ( !arrayContains(leadingCols,dt1Cols[j]) )rowData.push(null);
            }
            for (j = 0; j < dt2Cols.length; j++) {
            	if ( !arrayContains(leadingCols,dt2Cols[j]) ) rowData.push(getCellObject( v2, i, dt2Cols[j]));
            }
            results.addRow(rowData);            
            rowData = [];
        }
    }
        
    return results;
}

// Returns the cell object that corresponds to this row/column
function getCellObject(dataTable,row,col){
	var val = dataTable.getValue( row, col );
	
	var formattedVal = dataTable.getFormattedValue( row, col );
	
	if ( ! formattedVal || ( val == formattedVal ) ) return val;
	
	else return { v: val, f: formattedVal };
}

// This function duplicates the column at index "oldColumnIndex" and inserts the duplicate column at the end of the datatable
function addDuplicateColumn( dataTable, oldColumnIndex, newColumnLabel ) {
	var numCols = dataTable.getNumberOfColumns();
	var newColumnIndex = numCols;
	if ( numCols > oldColumnIndex ) {
		dataTable.addColumn( dataTable.getColumnType(oldColumnIndex), newColumnLabel, newColumnLabel );
		for ( var i = 0; i < dataTable.getNumberOfRows(); ++i ) {
			dataTable.setValue( i, newColumnIndex, getCellObject( dataTable, i, oldColumnIndex ));
		}
	}
}

// This function copies data from column x to column y
function copyColumnInDataTable( dataTable, x, y ){
	for ( var i = 0; i < dataTable.getNumberOfRows(); ++i ) {
		dataTable.setValue( i, y, getCellObject( dataTable, i, x ));
	}
}

// This function moves the column at index "oldIndex" to index "newIndex"
function moveColumnInDataTable( dataTable, oldIndex, newIndex ) {
	// create metadata for the old and new columns
	var oldColumnType = dataTable.getColumnType( oldIndex );
	var oldColumnLabel = dataTable.getColumnLabel( oldIndex );
	var oldColumnId = dataTable.getColumnId( oldIndex );
	
	// insert 1 column at position "newIndex", this is where the data from column "oldIndex" will go
	dataTable.insertColumn( newIndex, oldColumnType, oldColumnLabel, oldColumnId );
	
	// reset "oldIndex" as appropriate
	if ( oldIndex > newIndex ) ++oldIndex;
	
	// copy data
	copyColumnInDataTable( dataTable, oldIndex, newIndex );
	
	// remove "old" column
	dataTable.removeColumn(oldIndex);
}

// Returns the index of this column in the datatable
function getColumnIndexForColumnName( dataTable, columnName ) {
	for ( var index = 0; index < dataTable.getNumberOfColumns(); ++index ) {
		if ( dataTable.getColumnLabel( index ) == columnName ) {
			return index;
		}
	}
}

// This function removes duplicates from the datatable based on the given key columns;
// it keeps the order of the original columns, and for the non-key columns it returns the value in the first row, or null if no rows exist.
function removeDuplicatesFromDataTable( dataTable, keys ) {
	var func = function(values){return values.length > 0 ? values[ 0 ] : null;};
	// Get an array of non-key columns
	var otherColumns = new Array();	
	for ( var i = 0; i < dataTable.getNumberOfColumns(); ++i ) {
		if ( !arrayContains( keys, i ) ) 
		{
			otherColumns.push( { column: i, aggregation: func, type: dataTable.getColumnType(i), label: dataTable.getColumnLabel(i), id: dataTable.getColumnId(i)} );
		}
	}
	
	// Group the dataTable by the key columns
	var newDataTable = google.visualization.data.group( dataTable, keys, otherColumns );
	
	// Re-order the columns to match the datatable's previous column order before the grouping
	var columnIndexes = keys.concat( otherColumns );
	var leadingColumns =  getLeadingColumnIndexes();
	var columns = new Array();
	
	for ( var i = 0; i < leadingColumns.length; ++i ) {
		var actualColumnIndex = (isNaN( columnIndexes[i] ) ? columnIndexes[i]['column'] : columnIndexes[i]);
		if ( leadingColumns[ i ] != actualColumnIndex ) {
			for ( var j = 0; j < columnIndexes.length; ++j ) {
				var temp = isNaN( columnIndexes[j] ) ? columnIndexes[j]['column'] : columnIndexes[j];
				if ( temp == leadingColumns[ i ] ) {
					columns.push( j );
					break;
				}
			}
		}
		else {
			columns.push(i);
		}
	}
	
	for ( var i =0; i < newDataTable.getNumberOfColumns(); ++i ) {
		if ( !arrayContains( columns, i )) columns.push( i );
	}
	return google.visualization.data.group( newDataTable, columns );
	
}