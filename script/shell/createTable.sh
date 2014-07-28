#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

cd $NMS_HOME

. $SHELL_HOME/setEnv.sh

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
