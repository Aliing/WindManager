#!/bin/bash
#$1:source file
#$2:destination file
if [ $# != 2 ]
then
  exit 1
fi 

. /HiveManager/script/shell/setHmEnv.sh

cd $NMS_HOME

. $SHELL_HOME/setEnv.sh

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.util.RestoreCasInfo $1 $2 >>$HM_SCRIPT_LOGFILE 2>&1

