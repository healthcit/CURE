Quick setup for developers
==========================

1) You will need to have the following on your machine:
    postgresql 8.4 +
	CouchDB 1.01 +
    ant 1.7.x
    jdk 1.6
    tomcat 6.0.xx (Might work for 5.5.20 version, did not get a chance to test on this, it is always better to use latest version.)

2) Setting up database:
    a. install Postgres 8.4
	b. use pgAdmin (GUI) to create cacure user (role)
	c. Create cacure database
	d. Set cacure database owner to cacure user
	e. run db creation script from source code (src/main/db/cacure.sql)
	f. modify cacure search path (this is needed for SQL not to have to use schema name)
	 	psql -U postgres
	    \c cacure
		ALTER ROLE cacure SET search_path TO 'cacure','public';

3) Setting up data store:
    a. install CouchDB
	b. create cacure database
	c. in cacure database create a new design document names 'caCURE'
	d. install Couchapp version Couchapp-0.8.1( couch app can be downloaded from here: http://www.couchapp.org/page/windows-python-installers)
	e. change directory to src/main/db/designDocuments/caCURE
	f. execute the following :
	{PAth to Couchapp}/couchapp.exe push http://{couchdb-servername}:5984/{dbName}
	for example, C:\Couchapp\couchapp.exe push http://localhost:5984/cacure
	
		
3) Configure your local build settings in ${workspace}/local-build.properties. The
   properties to pay attention to are:
    java.home=/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home
    tomcat.java.home=/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home
    tomcat.dir=../How2Tools/apache-tomcat-6.0.26
    ...
    if you had setup your databases differently than above modify src/main/resources/cacure.properties

    If the project will be deployed to multiple environments that requires environment specific properties 
    you can create another properties file. the caCURE-FB.properties should contain all the default properties. The environment specific properties file should 
    contain only properties that will override the defaults.
    For example: 
    caCURE-FB.properties will contain:
    jpa.connection.host=localhost
    
    production.properties will contain:
    jpa.connection.host=prod-pgsql
    


4) Buillding and running the project. The following targets are useful from a
developer's perspective.

 - To build a war file with default properties use: 
      ant build-local-war
      - To deploy to a local tomcat directory specified in {username}.properties file use:
      ant deploy-local-as-war
      - To build a war file for deployment with environment specific properties use:
      ant build-for-remote-deployment
      you will be prompted for the name of the file to use for overriding properties values. The file name should not include ".properties" extension.
      For example, if the file is called "production.properties" enter just "production" when prompted for the environment name.

	 to start tomcat:    
      ant tomcat-start
      You now should be able to see the web site at something like:
        http://localhost:8080/
    Modifying JSPs (no need to restart the server):
      ant copy-to-deploy
      reload the web page
    Modification to a controller:
      ant all
      ant tomcat-start
      
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
1. The DB Schema/System Updater                                                 ~~~
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
ApplicationUpdater.java is the central class which handles both database schema and Java-based system updates upon deployment.

Updates are tracked via a DB table "sys_variables". This table consists of a database version-tracking column ("schema_version")
and a system version-tracking column ("system_update_version"). These columns are numeric values which represent the current database 
and system update versions respectively. 

When the version number is 0, it implies that no scripts have been executed for this particular instance of the application.
Because of this, the default values of the columns has been set to 0, and updates are assumed to start from version 1. 
Upon completion of each update, the columns are incremented by 1.

The table must be generated manually. The script to generate this table is included under the  directory /src/main/db.

To perform database updates:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1) Ensure that the "sys_variables" table has been generated correctly.

2) Create a SQL script X.up.sql under the /src/main/db/uppers folder, where "X" is a number which represents the next DB version
(for example: 2.up.sql is the update script for DB schema update version "2".)     

3) Deploy the application.

To perform (java-based) system updates:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1) Ensure that the "sys_variables" table has been generated correctly.

2) Create a Java class which will execute the system update(s). It must meet the following requirements:
	a) It must extend the class com.healthcit.how.scripts.utils.SystemUpdaterImpl.

 	b) It must be annotated as a @Component.
 
 	c) It must be created under the package com.healthcit.how.scripts.
 
3) Create a new text file, X.up.txt, containing the name of the class created in Step 2.
   Create the file under the /src/main/sys/uppers folder, where "X" is a number representing the next DB version
   (for example: 2.up.txt is the update script for system update version "2".)     

4) Deploy the application.

