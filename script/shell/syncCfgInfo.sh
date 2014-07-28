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

cd $UPDATE_HOME

UPDATE_LIB_HOME=$UPDATE_HOME/hm/WEB-INF/lib

UPDATE_JARS=`ls $UPDATE_LIB_HOME/*.jar`

SYNC_CFG_CLASSPATH=.:$UPDATE_HOME/hm/WEB-INF/classes

for jar in $UPDATE_JARS
do
  SYNC_CFG_CLASSPATH=$SYNC_CFG_CLASSPATH:$jar
done

cd $UPDATE_HOME/hm/WEB-INF/classes

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $SYNC_CFG_CLASSPATH com.ah.be.common.SyncConfigValues $1 $2 >>$HM_SCRIPT_LOGFILE 2>&1