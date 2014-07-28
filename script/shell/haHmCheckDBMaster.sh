#!/bin/bash

. /hivemap/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "haHmCheckDBMaster.sh begin..." >> $HM_SCRIPT_LOGFILE 2>&1
##local_ip=`ifconfig eth0 | grep "inet addr" | sed -n "s/.*inet\s*addr:\([0-9\.]*\).*Bcast.*/\1/p"` >> $HM_SCRIPT_LOGFILE 2>&1
##echo "local ip:"$local_ip

##cd /hivemap/HiveManager/tomcat/hm_soft_upgrade
##db_master_ip=`cat /hivemap/HiveManager/tomcat/hm_soft_upgrade/dbserver | grep dbactive | sed -n "s/^dbactive\:\([0-9\.]*\)/\1/p"` >> $HM_SCRIPT_LOGFILE 2>&1
##echo "db ha master ip:"$db_master_ip

##echo "haHmCheckDBMaster.sh end. local ip: $local_ip. db ha master ip:$db_master_ip" >> $HM_SCRIPT_LOGFILE 2>&1

if [ -f /HiveManager/tomcat/hm_soft_upgrade/db_master_ip ]
then
	echo "master" > /dev/stdout
else
    echo "slave" > /dev/stdout
fi
