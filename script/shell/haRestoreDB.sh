#! /bin/bash
. /HiveManager/script/shell/setHmEnv.sh
. /HiveManager/script/shell/setEnv.sh

## $1:cd path,it will dicide use app code or image code
## eg. /HiveManager/tomcat or /HiveManager/tomcat/hm_soft_upgrade/hm/WEB-INF/classes
##cd $1
##$JAVA_HOME/bin/java -agentlib:jdwp=transport=dt_socket,address=8001,server=y,suspend=y -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhRestoreLicenseHistory $NMS_HOME/dbxmlfile/license

cd $NMS_HOME
$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhRestoreLicenseHistory $NMS_HOME/dbxmlfile/license >> $HM_SCRIPT_LOGFILE 2>&1
$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $RESTOREPATH com.ah.be.admin.restoredb.AhRestoreFullData >> $HM_SCRIPT_LOGFILE 2>&1

rm -rf .haupgraderestore
##>>$HM_SCRIPT_LOGFILE 2>&1
