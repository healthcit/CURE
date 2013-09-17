#!/bin/sh
PENTAHO_HOME=$1
echo "Running" >/tmp/etl.log
ts=`date +%Y%m%d%H%M_%s`
stdout_file="$PENTAHO_HOME/cure_etl/logs/cure_etl_out_${ts}.log"
stderr_file="$PENTAHO_HOME/cure_etl/logs/cure_etl_error_${ts}.log"
#echo "Running" >$stderr_file
$PENTAHO_HOME/cure_etl/bin/main.sh "${@:2}" 1>${stdout_file} 2>${stderr_file}
