#!/bin/bash

chmod u+x /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/*.sh

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

##check memory
$UPDATE_SHELL_HOME/ahCheckMemory.sh
if [ $? = 1 ]
then
	echo "update_in_progress" > /dev/stderr
    exit 1
fi

$UPDATE_SHELL_HOME/syncSystemCfg.sh

#sync shell admin password when update
if [ -f /etc/shadow ]
then
	/bin/cp -rf /etc/shadow /hivemap/etc/shadow
fi

cd $NMS_HOME

. $UPDATE_SHELL_HOME/haHmUpdate.sh


##for db separate
if [ -f /HiveManager/shell/extdbsettings.properties ]
then
	/bin/cp -f /HiveManager/shell/extdbsettings.properties /hivemap/HiveManager/shell/
fi

cd /HiveManager/tomcat/hm_soft_upgrade
##app_master_ip=`cat /HiveManager/tomcat/hm_soft_upgrade/dbserver | grep appmaster | sed -n "s/^appmaster\:\([0-9\.]*\)/\1/p"` >> $HM_SCRIPT_LOGFILE 2>&1
##db_passive_ip=`cat /HiveManager/tomcat/hm_soft_upgrade/dbserver | grep dbpassive | sed -n "s/^dbpassive\:\([0-9\.]*\)/\1/p"` >> $HM_SCRIPT_LOGFILE 2>&1

##cd /HiveManager/ha/scripts
##app_master=`./query_node_status.sh`

##if [ $app_master = "2" ]
##then
	##stop db passive db-service
##	ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no $db_passive_ip /hivemap/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/ahStopDBService.sh >> $HM_SCRIPT_LOGFILE 2>&1
	
	##join again
##	cd /HiveManager/PGPool/script/
##	./setupSlaveNode.sh -s $db_passive_ip -f >> $HM_SCRIPT_LOGFILE 2>&1
	
##	sleep 10
##fi

##for hm db separate
two_parations=`echo /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/isDBPartition.sh`
if [ -d /hivemap/HiveManager/tomcat/hm_soft_upgrade/ -a "$two_parations" = "true" ]
then
	if [ ! -f /hivemap/HiveManager/tomcat/hm_soft_upgrade/appserver ]
	then
		touch /hivemap/HiveManager/tomcat/hm_soft_upgrade/appserver
	fi
fi

##rm -rf /HiveManager/tomcat/hm_soft_upgrade/appserver
/bin/rm -rf /HiveManager/tomcat/hm_soft_upgrade/dbserver
/bin/rm -rf /hivemap/HiveManager/tomcat/hm_soft_upgrade/dbserver

echo "updateSoftware end." >> $HM_SCRIPT_LOGFILE 2>&1
