=================================================================================
  Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
  Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
  Proprietary and confidential
=================================================================================

Quick setup for developers
==========================

1) You will need to have the following on your machine:
    SVN
    postgresql 8.4
    ant 1.7.x
    jdk 1.6
    tomcat 6.0.xx (Might work for 5.5.20 version, did not get a chance to test on this, it is always better to use latest version.)

2) Setting up database:
    a. install Postgres 8.4
	b. use pgAdmin (GUI) to create fbdev user (role)
	c. Create FormBuilder database
	d. Set FormBuilder database owner to fbdev user
	e. modify fbdev search path (this is needed for SQL not to have to use schema name) and install plpgsql language;
	    psql -U postgres
	    \c FormBuilder
		ALTER ROLE fbdev SET search_path TO 'FormBuilder','public';
	    CREATE LANGUAGE plpgsql;	

3) Configure your local build settings in ${workspace}/local-build.properties. The
   properties to pay attention to are:
    java.home=/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home
    tomcat.java.home=/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home
    tomcat.dir=../How2Tools/apache-tomcat-6.0.26
    ...
    # if you had setup your database differently than above
    hibernate.connection.username=fbdev
    hibernate.connection.password=fbdev
    hibernate.schema=FormBuilder
    
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
    Initially:
      - To build a war file with default properties use: 
      ant build-local-war
      - To deploy to a local tomcat directory specified in {username}.properties file use:
      ant deploy-local-as-war
      - To build a war file for deployment with environment specific properties use:
      ant build-for-remote-deployment
      you will be prompted for the name of the file to use for overriding properties values. The file name should not include ".properties" extension.
      For example, if the file is called "production.properties" enter just "production" when prompted for the environment name.
      
      to start tomcat execute
      ant tomcat.start
      You now should be able to see the web site at something like:
        http://localhost:8080/
    Modifying JSPs (no need to restart the server):
      ant copy-to-deploy
      reload the web page
    Modification to a controller:
      ant all
      ant tomcat-start


Additional notes:
* Don't commit changes to SVN if your local build is broken
* Provide a brief commit message that explains why/what the changes you just
  committed do.
* Happy coding :-)