#!/bin/bash
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh
echo "check size of disk start..." >>$HM_SCRIPT_LOGFILE 2>&1
if [ -d /var/lib/pgsql/9.1/data/base ]
then
	cd /var/lib/pgsql/9.1/data/base/
	db_size=`du -shm | awk '{print $1}'`
	if [ "$db_size" = "" ]
	then
	db_size=3072
	fi
fi 

APP_TYPE=`cat $HM_ROOT/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`

if [ $APP_TYPE != "HM" -a $APP_TYPE != "hm" ]
then
	free_size=`df -hmP | grep mapper | awk '{print $4}'`
else

  /bin/rm -rf /hivemap/HiveManager/tomcat/dbxmlfile
  /bin/mkdir  /hivemap/HiveManager/tomcat/dbxmlfile
  free_size=`df -hmP | grep hivemap | awk '{print $4}'`
fi

times=`expr $free_size / $db_size` >>$HM_SCRIPT_LOGFILE 2>&1
if [ $? = 1 ]
then
	echo "division has some errors happened, 'free_size / db_size':  $free_size / $db_size" >>$HM_SCRIPT_LOGFILE 2>&1
	times=-1
fi
if [ $times -lt 3 ]
then
	echo "[ERROR] ahCheckDiskSize.sh: free_size less than db_size 3 times, cannot upgrade" >>$HM_SCRIPT_LOGFILE 2>&1
	let EtSize=$db_size*3
	echo "The free disk space on your HiveManager is insufficient:</br>Required free disk space:$EtSize M </br>Actual free disk space:$free_size M &&Be sure you have sufficient disk space on your HiveManager." > /dev/stdout
	exit 1
fi
echo "check size of disk end." >>$HM_SCRIPT_LOGFILE 2>&1
exit 0
