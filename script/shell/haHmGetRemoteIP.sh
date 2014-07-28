#!/bin/bash

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

remote_ip=`cat /HiveManager/tomcat/hm_soft_upgrade/dbserver | grep $1 | sed -n "s/^$1\:\([0-9\.]*\)/\1/p"` >> $HM_SCRIPT_LOGFILE 2>&1

if [  -z $remote_ip ]
then
	echo "update_in_progress" > /dev/stdout
else
    echo "$remote_ip" > /dev/stdout
fi