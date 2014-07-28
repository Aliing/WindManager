#!/bin/bash
. /hivemap/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

. /hivemap/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setEnv.sh

echo "----haHmDBRestore begin..." >> $HM_SCRIPT_LOGFILE 2>&1

##make db not read-only
if [ -f /var/lib/pgsql/9.1/data/recovery.conf ]
then
	/bin/rm -rf /var/lib/pgsql/9.1/data/recovery.conf
fi

##is_master=`/bin/sh /hivemap/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/haHmCheckDBMaster.sh` >> $HM_SCRIPT_LOGFILE 2>&1


if [ -f /hivemap/HiveManager/tomcat/hm_soft_upgrade/db_master_ip ]
then
	
    cd /hivemap/$NMS_HOME/hm_soft_upgrade/hm/WEB-INF/classes

    if [ ! -f $NMS_HOME/dbxmlfile/license/license_history_info.xml ]
    then
	    mkdir -p $NMS_HOME/dbxmlfile/license
	    $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupLicenseHistory $NMS_HOME/dbxmlfile/license >>$HM_SCRIPT_LOGFILE 2>&1
    fi

    sleep 5

	pkill -f "postgres: hivemanager hm_temp"
	su postgres -c "psql -c \"DROP DATABASE hm_temp;\""  >>$HM_SCRIPT_LOGFILE 2>&1
	su postgres -c "psql -c \"CREATE DATABASE hm_temp WITH OWNER = hivemanager TABLESPACE = pg_default;\"" >>$HM_SCRIPT_LOGFILE 2>&1
	while [ $? != 0 ]
	do
		pkill -f "postgres: hivemanager hm_temp"
		su postgres -c "psql -c \"DROP DATABASE hm_temp;\""  >>$HM_SCRIPT_LOGFILE 2>&1
		su postgres -c "psql -c \"CREATE DATABASE hm_temp WITH OWNER = hivemanager TABLESPACE = pg_default;\"" >>$HM_SCRIPT_LOGFILE 2>&1
	done
	su postgres -c "createlang plpgsql hm_temp"
	/hivemap/$NMS_HOME/hm_soft_upgrade/$SHELL_HOME/createTable.sh hm_temp  >>$HM_SCRIPT_LOGFILE 2>&1
	
	pkill -f "postgres: hivemanager hm"
	su postgres -c "psql -c \"DROP DATABASE hm;\""  >>$HM_SCRIPT_LOGFILE 2>&1
	su postgres -c "psql -c \"CREATE DATABASE hm WITH OWNER = hivemanager TABLESPACE = pg_default;\"" >>$HM_SCRIPT_LOGFILE 2>&1
	while [ $? != 0 ]
	do
		pkill -f "postgres: hivemanager hm"
		su postgres -c "psql -c \"DROP DATABASE hm;\""  >>$HM_SCRIPT_LOGFILE 2>&1
		su postgres -c "psql -c \"CREATE DATABASE hm WITH OWNER = hivemanager TABLESPACE = pg_default;\"" >>$HM_SCRIPT_LOGFILE 2>&1
	done
	su postgres -c "createlang plpgsql hm" >>$HM_SCRIPT_LOGFILE 2>&1
	
	sleep 5
	
	/hivemap/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/haHmDBCreateTable.sh >>$HM_SCRIPT_LOGFILE 2>&1
	
	cd /hivemap/HiveManager/tomcat/hm_soft_upgrade/hm/WEB-INF/classes
	$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhRestoreLicenseHistory $NMS_HOME/dbxmlfile/license >>$HM_SCRIPT_LOGFILE 2>&1
	$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $RESTOREPATH com.ah.be.admin.restoredb.AhRestoreFullData >>$HM_SCRIPT_LOGFILE 2>&1
	
	##check restore
    . /hivemap/$NMS_HOME/hm_soft_upgrade/HiveManager/script/shell/checkAfterRestore.sh

fi

if [ ! -d /HiveManager/tomcat/hm_soft_upgrade ]
then
	mkdir /HiveManager/tomcat/hm_soft_upgrade
fi
##app_master_ip=`cat /hivemap/HiveManager/tomcat/hm_soft_upgrade/dbserver | grep dbactive | sed -n "s/^dbactive\:\([0-9\.]*\)/\1/p"` >> $HM_SCRIPT_LOGFILE 2>&1
if [ -f /hivemap/HiveManager/tomcat/hm_soft_upgrade/db_master_ip ]
then
    ##ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no $app_master_ip touch /HiveManager/tomcat/hm_soft_upgrade/dbactive
    touch /HiveManager/tomcat/hm_soft_upgrade/dbactive
else
    ##ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no $app_master_ip touch /HiveManager/tomcat/hm_soft_upgrade/dbpassive
    service postgresql stop >>$HM_SCRIPT_LOGFILE 2>&1 
    touch /HiveManager/tomcat/hm_soft_upgrade/dbpassive
fi

/bin/rm -f /hivemap/HiveManager/tomcat/hm_soft_upgrade/db_master_ip
/bin/rm -f /HiveManager/tomcat/.ha4NodesRestore

echo "----haHmDBRestore end." >> $HM_SCRIPT_LOGFILE 2>&1


