################################################################################
 #Copyright (c) 2013 HealthCare It, Inc.
 #All rights reserved. This program and the accompanying materials
 #are made available under the terms of the BSD 3-Clause license
 #which accompanies this distribution, and is available at
 #http://directory.fsf.org/wiki/License:BSD_3Clause
 #
 #Contributors:
 #    HealthCare It, Inc - initial API and implementation
################################################################################

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Analytics for CURE
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

SETUP REQUIREMENTS FOR YOUR LOCAL WORKSTATION

1) Install Tomcat 6

2) Install Postgres 8.4

3) Install CouchDB 1.1.x : http://couchdb.apache.org/downloads.html

4) Install CouchApp (latest version) :  http://couchapp.org/page/installing

5) Set up the CouchDB database/views:
  
   - Create a new CouchDB database called "cacure" (alternatively, you may copy the database file located under 
     src/main/db/CouchDBSampleInstance to the following directory: <path_to_couchdb>/var/lib/couchdb). 
     
     (NOTE: If you wish to call your database something else, simply rename the database file 
     OR create a new CouchDB database with a different name.)
     
   - Get the latest CouchDB views by copying the directory src/main/db/CouchDBDesignDocuments/views to <path_to_couchapp>/templates/googleCharts
   
   - Push the latest "googleCharts" views as follows (using Couchapp):
     
     cd <path_to_couchapp>/templates
     
     couchapp push googleCharts http://<server_name_or_IP>:5984/cacure
     
   - Get the latest CouchDB "caCURE" views by copying the "views" directory from the SVN repository and copy them to <path_to_couchapp>/templates/caCURE.
   
   - Push the latest "caCURE" views as follows (using Couchapp):
   
     cd <path_to_couchapp>/templates
     
     couchapp push caCURE http://<server_name_or_IP>:5984/cacure   
     
6) Set up the Postgres database:

    - Use pgAdmin (GUI) to create a new Postgres user role called "cahope" with password "cahope".

	- Create a new Postgres database called CaHope. For configuration details, please refer to the configuration file
	src/main/resources/config.properties.
	
	From command line connect to postgres database:
	    psql.exe -U postgres;
	    \c CaHope;
	    ALTER ROLE cahope SET search_path TO 'CaHope','public';
		CREATE LANGUAGE plpgsql;
		
		(If an error displays that looks like, "plpgsql already exists", ignore this error...)
	
	- Execute the following scripts
		1) src/main/db/cahope_1.sql
		2) src/main/db/cahope_2.sql
     
 7) Build and deploy the analytics application:
 
    - Build and/or deploy the war file for the application using the build.xml Ant script provided in the application.
    
    (NOTE: You should provide a new properties file called <YOUR-USER-PROFILE-NAME>-build.properties in order to
    run some of the Ant tasks.)
    
 8) Test the deployment:
 
    - If deployed to your local workstation, the application should be accessible at http://localhost:<PORT>/analytics.
    
    - You may login with the default admin user: username "admin", password "admin".
   

