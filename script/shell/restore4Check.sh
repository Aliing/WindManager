#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh
. $SHELL_HOME/setEnv.sh

cd $NMS_HOME

if [ ! -f $NMS_HOME/dbxmlfile/license/license_history_info.xml ]
then
    mkdir -p $NMS_HOME/dbxmlfile/license

    $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupLicenseHistory $NMS_HOME/dbxmlfile/license
fi

sleep 3

echo "clean db..."
$SHELL_HOME/dropTable.sh >>$HM_SCRIPT_LOGFILE 2>&1

sleep 3

$SHELL_HOME/createTable.sh >>$HM_SCRIPT_LOGFILE 2>&1

sleep 3

cd $NMS_HOME

echo "restore db..."
$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhRestoreLicenseHistory $NMS_HOME/dbxmlfile/license

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $RESTOREPATH com.ah.be.admin.restoredb.AhRestoreFullData