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
function (doc) {
	var formId = ( doc.instanceId ? doc.formId + '_' + doc.instanceId : doc.formId );
	
	// Module Id
	if ( doc.moduleId && doc.moduleName ) emit (['Module', doc.moduleId, doc.moduleName],null);
	
	// Form Id
	if ( doc.formId && doc.formName ) emit (['Form', formId, doc.formName],null);
}
