#!/bin/bash
. /hivemap/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh
. /hivemap/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setEnv.sh

cd /hivemap/HiveManager/tomcat/hm_soft_upgrade/hm/WEB-INF/classes

if [ $# == 0 ]
then
	$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $HIBERNATEPATH com.ah.util.HibernateUtil create  >>$HM_SCRIPT_LOGFILE 2>&1
else
    if [ -z $2 ]
    then
    	$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $HIBERNATEPATH com.ah.util.HibernateUtil create "$1" >>$HM_SCRIPT_LOGFILE 2>&1
    else
        $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $HIBERNATEPATH com.ah.util.HibernateUtil create "$1" "$2" >>$HM_SCRIPT_LOGFILE 2>&1
    fi
fi