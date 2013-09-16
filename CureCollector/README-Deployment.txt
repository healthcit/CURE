README
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