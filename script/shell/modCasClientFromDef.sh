#!/bin/bash
. /HiveManager/script/shell/setHmEnv.sh

cd $NMS_HOME

. $SHELL_HOME/setEnv.sh

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.util.setDefaultCasClient >>$HM_SCRIPT_LOGFILE 2>&1
