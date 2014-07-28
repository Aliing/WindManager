#!/bin/bash
. /HiveManager/script/shell/setHmEnv.sh
. /HiveManager/script/shell/setEnv.sh

##clear folder
rm -rf $NMS_HOME/dbxmlfile/*

cd $NMS_HOME

##backup db use app
##if [ $1 -ne 2 ]
##then
	$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupFullData $NMS_HOME/dbxmlfile $1
##fi

##backup license use app
mkdir -p $NMS_HOME/dbxmlfile/license
$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupLicenseHistory $NMS_HOME/dbxmlfile/license

touch $NMS_HOME/dbxmlfile/webversion
cp -rf $NMS_HOME/webapps/hm/WEB-INF/hmconf/hivemanager.ver  $NMS_HOME/dbxmlfile

sleep 15
touch $CATALINA_HOME/.haupgraderestore