
@echo off

set JOBRUN_DIR=D:/
set JOBRUN_FOLDER=INTEGRATION/PENTAHO/
set JOB_DIR=%JOBRUN_DIR%/%JOBRUN_FOLDER%/

REM ******************
REM   KETTLE Library
REM ******************
REM 

SET JOB_FOLDER=cure_etl
SET JOB_PORPERTIES=PROCESS_XML.properties
SET MAIN_JOB=Main.kjb
SET JOB=%JOB_DIR%/%JOB_FOLDER%/KETTLE/%MAIN_JOB%

SET PENTAHO_HOME=C:\Apps\PENTAHO\PDI-4.3
REM SET KETTLE_HOME=D:\INTEGRATION\conf

SET PROPERTIES=%JOB_DIR%/%JOB_FOLDER%/CONF/%JOB_PORPERTIES%
echo PROPERTIES=%PROPERTIES%

cd /d %PENTAHO_HOME%

call kitchen.bat /file:%JOB% /param:PROPERTIES="%PROPERTIES%" /param:IN_FOLDER="%1" -level=Detailed -log=C:\Users\madhava\Desktop\WORK\main.log

cd /d %JOB_DIR%/%JOB_FOLDER%/BAT
