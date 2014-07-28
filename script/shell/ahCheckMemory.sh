#!/bin/bash
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "upgrade: memory check start..." >>$HM_SCRIPT_LOGFILE 2>&1
total_memory=`free -m|grep "Mem:"|awk '{print $2}'`

if [ $total_memory -lt 2900 ]
then
	echo "[ERROR] this server's memory size less than 3GBs! check failed." >>$HM_SCRIPT_LOGFILE 2>&1
	echo "upgrade: memory check end." >>$HM_SCRIPT_LOGFILE 2>&1
	##echo "Upgrade HiveManager was failed because " > /HiveManager/tomcat/logs/shMess.txt
	exit 1
else
	echo "upgrade: memory check end." >>$HM_SCRIPT_LOGFILE 2>&1
    exit 0
fi
