#!/bin/bash
. /HiveManager/script/shell/setHmEnv.sh
. /HiveManager/script/shell/setEnv.sh

Hibernate_cfg=$HM_ROOT/WEB-INF/classes/hibernate.cfg.xml
hib_ip=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

local_ip=`ifconfig eth0 | sed -n 2p | awk '{print $2}' | awk -F : '{print $2}'`
remote_ip=`echo "$hib_ip" | awk '{print tolower($0)}'`

#if [ ! -f /HiveManager/shell/extdbsettings.properties ]
#then
#	echo "false" > /dev/stdout
#	exit 0
#fi

##remote_ip=`cat /HiveManager/shell/extdbsettings.properties | grep host | sed -n 's/host=\(.*\)/\1/p'`

if [ -z $remote_ip ]
then
	echo "update_in_progress" > /dev/stdout
	exit 1
else
    if [ $local_ip = $remote_ip -o $remote_ip = "localhost" -o $remote_ip = "127.0.0.1" ]
    then
    	echo "false" > /dev/stdout
    	exit 0
    else
        echo "true" > /dev/stdout
        exit 0
    fi
fi