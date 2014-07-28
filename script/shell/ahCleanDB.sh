# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

$SHELL_HOME/stopHiveManage.sh

sleep 30

if [ ! -d ./dbxmlfile/home ]
then
    mkdir -p ./dbxmlfile/home
else
    rm -rf ./dbxmlfile/home/*
fi

. $SHELL_HOME/setEnv.sh

rm -rf $NMS_HOME/dbxmlfile/license

mkdir -p $NMS_HOME/dbxmlfile/license

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupLicenseHistory $NMS_HOME/dbxmlfile/license

sleep 5

$SHELL_HOME/dropTable.sh  >>$HM_SCRIPT_LOGFILE 2>&1
sleep 5

$SHELL_HOME/createTable.sh >>$HM_SCRIPT_LOGFILE 2>&1

rm -rf /HiveManager/downloads/*

rm -rf $HM_ROOT/domains/*

#For L7
touch /HiveManager/l7_signatures/.update

sleep 30 

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhRestoreLicenseHistory $NMS_HOME/dbxmlfile/license

##add flag for ha
if [ -d /HiveManager/ha/opt ]
then
  touch /HiveManager/ha/opt/ha_clear_db
fi


$SHELL_HOME/startHiveManage.sh
