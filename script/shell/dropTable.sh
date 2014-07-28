#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh
cd $NMS_HOME
. $SHELL_HOME/setEnv.sh

 $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $HIBERNATEPATH com.ah.util.HibernateUtil drop  >>$HM_SCRIPT_LOGFILE 2>&1
