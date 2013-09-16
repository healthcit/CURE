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
/*====================================================================*/
// Override the registerEvents method in the main FilterDataTableControl control
// so that the filter control is automatically collapsed
// whenever new filter criteria is submitted or reset
/*==================================================================== */
org.systemsbiology.visualization.FilterDataTableControl.addMethods({
	toggleContainerOpen: function(){
		if ( !this.openFilterContainer ){
			this.openFilterContainer = true;
			Effect.BlindDown($("org-systemsbiology-visualization-filtercontainer-header"));
	        Effect.BlindDown($("org-systemsbiology-visualization-filtercontainer-menu"));
	        Effect.BlindDown($("org-systemsbiology-visualization-filtercontainer-filteritemcontainers"));
	        $("org-systemsbiology-visualization-filtercontainer-header-titlelink").innerHTML = this.getTitleHtml();
		}
	},
	
	toggleContainerClose: function(){
		if ( this.openFilterContainer ){
			this.openFilterContainer = false;
			Effect.BlindUp($("org-systemsbiology-visualization-filtercontainer-header"));
	        Effect.BlindUp($("org-systemsbiology-visualization-filtercontainer-menu"));
	        Effect.BlindUp($("org-systemsbiology-visualization-filtercontainer-filteritemcontainers"));
	        $("org-systemsbiology-visualization-filtercontainer-header-titlelink").innerHTML = this.getTitleHtml();
		}
	},
	
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	// EXISTING version of registerEvents
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/	
	registerEvents: function (selectedColumnIndexes) {
    var control = this;
    var listenerOptions = {
        stopEventAfterCallback: true
    };
    this.addOnClickEventListener($("org-systemsbiology-visualization-globalAnd"), function () {
        control.togglePassButtons(true);
        control.applyFilter();
    }, listenerOptions);
    this.addOnClickEventListener($("org-systemsbiology-visualization-globalOr"), function () {
        control.togglePassButtons(false);
        control.applyFilter();
    }, listenerOptions);
    this.addOnClickEventListener($("org-systemsbiology-visualization-apply"), function () {
        control.applyFilter();
    }, listenerOptions);
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	// END EXISTING version of registerEvents
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/	
	
	//This listener is custom to account for removing the filter tab highlighting when Clear All is clicked
    this.addOnClickEventListener($("org-systemsbiology-visualization-clear"), function () {
        control.resetFilterAndFixHighlight();
    });
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	// EXISTING version of registerEvents
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/	
    this.addOnClickEventListener($("org-systemsbiology-visualization-filtercontainer-header-titlelink"), function () {
        control.toggleContainer();
    }, listenerOptions);
    var activeSelectorClick = function () {
           /*take care of highlighting selector*/
            var activeSelector = $("org-systemsbiology-visualization-filtercontainer-filteritem-active");
            if (activeSelector) {
                if (this.parentNode && activeSelector != this.parentNode) {
                    activeSelector.id = null;
                    this.parentNode.id = "org-systemsbiology-visualization-filtercontainer-filteritem-active";
                } else if (this.parent && activeSelector != this.parent) {
                    activeSelector.id = null;
                    this.parent.id = "org-systemsbiology-visualization-filtercontainer-filteritem-active";
                }
            }
        }
    var getOnSelectorClick = function (columnIndex) {
            return function () {
                // take care of visualizing container
                var currentSelection = $("org-systemsbiology-visualization-filteritem-container-" + columnIndex);
                var previousSelection = $(control.openFilterItemContainer);
                if (currentSelection) {
                    if (previousSelection) {
                        if (previousSelection != currentSelection) {
                            previousSelection.style.display = "none";
                            currentSelection.style.display = "";
                        }
                    } else {
                        currentSelection.style.display = "";
                    }
                    control.openFilterItemContainer = currentSelection.id;
                }
            }
        }
    var getOnSelectorClickChangeResetter = function (columnIndex) {
            return function () {
			
                // take care of visualizing container
                var currentResetter = $("org-systemsbiology-visualization-filteritem-resetter-" + columnIndex);
                var previousResetter = $(control.resetFilterItemButton);
                if (currentResetter) {
                    if (previousResetter) {
                        if (previousResetter != currentResetter) {
                            previousResetter.style.display = "none";
                            currentResetter.style.display = "";
                        }
                    } else {
                        currentResetter.style.display = "";
                    }
                    control.resetFilterItemButton = currentResetter.id;
                }
            }
        }
    var getOnResetterClick = function (columnIndex) {
            return function () {
				/*remove corresponding highlighted tab when clear button is clicked*/
				removeFilter(columnIndex);
				
                var fcc = control.filterColumnControlByColumnIndex.get(columnIndex);
                fcc.resetFilter();
                control.applyFilter();
            }
        }
    for (var i = 0; i < selectedColumnIndexes.length; i++) {
        var selectedColumnIndex = selectedColumnIndexes[i];
        var selectorId = "org-systemsbiology-visualization-filteritem-selector-" + selectedColumnIndex;
        var resetterId = "org-systemsbiology-visualization-filteritem-resetter-" + selectedColumnIndex;
        this.addOnClickEventListener($(selectorId), activeSelectorClick, listenerOptions);
        this.addOnClickEventListener($(selectorId), getOnSelectorClick(selectedColumnIndex), listenerOptions);
        this.addOnClickEventListener($(selectorId), getOnSelectorClickChangeResetter(selectedColumnIndex), listenerOptions);
        this.addOnClickEventListener($(resetterId), getOnResetterClick(selectedColumnIndex), listenerOptions);
    }
},
/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
// END EXISTING version of registerEvents
/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/		
	
	resetFilter : function() {	
		//this.toggleContainerClose(); // added code to collapse the filter control when submitting new filter criteria
		

		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		// EXISTING version of resetFilter()
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        this.clearMappings();
        this.filterColumnControlByColumnIndex.values().each(function(filterColumnControl) {
            filterColumnControl.resetFilter();
        });

        this.propagate(this.data);
        
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		// END EXISTING version of resetFilter()
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    },
	
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		// 	CUSTOM resetFilterAndFixHighlight()
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/		
		resetFilterAndFixHighlight : function() {	
		//this.toggleContainerClose(); // added code to collapse the filter control when submitting new filter criteria
		
			removeFilter();	//remove highlighted tab on Clear All
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

        this.clearMappings();
		
        this.filterColumnControlByColumnIndex.values().each(function(filterColumnControl) {
            filterColumnControl.resetFilter();
        });

        this.propagate(this.data);
        
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		// END CUSTOM version of resetFilterAndFixHighlight()
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    },
	
	applyFilter: function() {	
		//this.toggleContainerClose(); /* added code to collapse the filter control when submitting new filter criteria*/
		
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		// EXISTING version of applyFilter()
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		this.clearMappings();

        var control = this;
        var activeFilters = new Array();
        this.filterColumnControlByColumnIndex.keys().each(function(columnIndex) {
            var fcc = control.filterColumnControlByColumnIndex.get(columnIndex);
            if (fcc.isActive()) {
				
                activeFilters[activeFilters.length] = columnIndex;
                control.toggleContainerClose(); /* added code to collapse the filter control when submitting new filter criteria*/

            }
        });

        if (activeFilters.length == 0) {
            this.resetFilter();
            return;
        }

        var numberOfPasses = 1;
        if (this.globalAnd) {
            numberOfPasses = activeFilters.length;
        }

        var passedRows = new Array();
        for (var i = 0; i < this.data.getNumberOfRows(); i++) {
            var passingValues = new Array();
            activeFilters.each(function(columnIndex) {
                var value = control.data.getValue(i, parseInt(columnIndex));
                var fcc = control.filterColumnControlByColumnIndex.get(columnIndex);
                if (fcc.passes(value)) {
                    passingValues[passingValues.length] = columnIndex;
                }
            });
            if (passingValues.length >= numberOfPasses) {
                passedRows[passedRows.length] = i;
            }
        }

        // create table with filters
        var filteredData = new google.visualization.DataTable();
        for (var c = 0; c < this.data.getNumberOfColumns(); c++) {
            filteredData.addColumn(this.data.getColumnType(c), this.data.getColumnLabel(c));
        }

        // populate filterData with rows passing filters
        if (passedRows.length) {
            filteredData.addRows(passedRows.length);
            var rowIndex = 0;
            passedRows.uniq().sort().each(function(passedRowIndex) {
                for (var i = 0; i < control.data.getNumberOfColumns(); i++) {
                    filteredData.setCell(rowIndex, i, control.data.getValue(passedRowIndex, i));
                }
                control.addMapping({row:rowIndex++}, {row:passedRowIndex});
            });
        }
        this.propagate(filteredData);
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		// END EXISTING version of applyFilter()
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	}
});

/**
 * The filter should display formatted values (instead of raw values) whenever such values exist
 */
org.systemsbiology.visualization.SelectDistinctValuesStringFilterColumnControl.addMethods({
    initialize: function(container) {
        this.containerElement = container;
    },

    draw : function(data, options) {
        var columnIndex = options.assignedColumnIndex;
        var columnValues = new Array();
        var columnFormattedValues = {};
        for (var rowId = 0; rowId < data.getNumberOfRows(); rowId++) {
        	var value                    = data.getValue(rowId, columnIndex);
            columnValues[rowId]          = value;
            if ( value ) columnFormattedValues[value] = data.getFormattedValue(rowId, columnIndex);
        }

        var uniqueValues = columnValues.uniq().sort();
        if (uniqueValues.length > 1) {
            var multiSize = 4;
            if (multiSize > uniqueValues.length) {
                multiSize = uniqueValues.length;
            }

            this.selectionElement = "filter_item_" + columnIndex;
            var html = "Select Any";
            html += "<select id='" + this.selectionElement + "' class='org-systemsbiology-visualization-filteritem-select'";
            html += " multiple='multiple' size='" + multiSize + "'>";
            uniqueValues.each(function(uniqueValue) {
            	var uniqueValueLabel = columnFormattedValues[uniqueValue] || uniqueValue;
                html += "<option value='" + uniqueValue + "'>" + uniqueValueLabel + "</option>";
            });
            html += "</select>";
        }
        this.containerElement.innerHTML = html;
    },

    isActive: function() {
        var selectBox = $(this.selectionElement);
        for (var i = 0; i < selectBox.options.length; i++) {
            if (selectBox.options[i].selected) return true;
        }
        return false;
    },

    passes: function(value) {
        if (this.isActive()) {
            var selectedValues = new Array();
            var selectBox = $(this.selectionElement);
            for (var i = 0; i < selectBox.options.length; i++) {
                var option = selectBox.options[i];
                if (option.selected) {
                    selectedValues[selectedValues.length] = option.value;
                }
            }

            if (selectedValues.length) {
                var without = selectedValues.without(value);
                return (without.length < selectedValues.length);
            }
        }
        return true;
    },

    resetFilter: function() {
        var selectBox = $(this.selectionElement);
        for (var i = 0; i < selectBox.options.length; i++) {
            selectBox.options[i].selected = false;
        }
    }
});
