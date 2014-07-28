#!/bin/bash
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

. $UPDATE_SHELL_HOME/setEnv.sh

echo "----haHmDBBackup begin..." >> $HM_SCRIPT_LOGFILE 2>&1

MAP_HIVEMANAGE_HOME=/hivemap/HiveManager
MAP_CATALINA_HOME=$MAP_HIVEMANAGE_HOME/tomcat

##is_master=`/bin/sh /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/haHmCheckDBMaster.sh` >> $HM_SCRIPT_LOGFILE 2>&1

if [ -f /HiveManager/tomcat/hm_soft_upgrade/db_master_ip ]
then
	MAP_CATALINA_HOME=/hivemap/HiveManager/tomcat

	/bin/rm -rf $MAP_CATALINA_HOME/dbxmlfile
	/bin/mkdir  $MAP_CATALINA_HOME/dbxmlfile

	cp -rf $CATALINA_HOME/hm_soft_upgrade/hm/WEB-INF/hmconf/hivemanager.ver $MAP_CATALINA_HOME/dbxmlfile/


	##backup db
	cd $NMS_HOME/hm_soft_upgrade/hm/WEB-INF/classes
	$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupFullData $MAP_CATALINA_HOME/dbxmlfile $1 >>$HM_SCRIPT_LOGFILE 2>&1

	mkdir -p $MAP_CATALINA_HOME/dbxmlfile/license
	$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupLicenseHistory $MAP_CATALINA_HOME/dbxmlfile/license >>$HM_SCRIPT_LOGFILE 2>&1

	touch $MAP_CATALINA_HOME/dbxmlfile/webversion
	
fi
 
touch $MAP_CATALINA_HOME/.ha4NodesRestore  

echo "----haHmDBBackup end." >> $HM_SCRIPT_LOGFILE 2>&1