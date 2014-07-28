#!/bin/bash
#
# Copyright (C) 2006-2012 Aerohive Networks
#
# yZhong@AeroHive.com
#
##

#remove dbonly flag
if [ -e /HiveManager/tomcat/.dbOnly ]; then
	/bin/rm -f /HiveManager/tomcat/.dbOnly
fi

. /HiveManager/script/shell/setHmEnv.sh
. /HiveManager/script/shell/ahshellfuncs

. $(dirname "$0")/shellfuncs

#touch /var/lib/pgsql/9.1/trigger
#startLocalDB
#configureDB LocalHost

#cd $CATALINA_HOME
#$SHELL_SCRIPTs/startHiveManage.sh

#stop heartbeat
if [ -e /etc/ha.d/ha.cf ]; then
	service heartbeat-aerohive status
	if test $? -eq 0 ; then
		echo "INFO:Stop heartbeat service."
		service heartbeat-aerohive stop
	fi
fi

service logd status
if test $? -eq 0 ; then
	echo "INFO:Stop service logd."
	service logd stop
fi

#clean ha status file
if [ -e /HiveManager/ha/conf/aerohive_ha.conf ]; then
	/bin/rm -f /HiveManager/ha/conf/aerohive_ha.conf
fi

if [ -e /HiveManager/ha/conf/ha_node_status ]; then
	/bin/rm -f /HiveManager/ha/conf/ha_node_status
fi

#upgrade will switch to another partition, so need clean these file on another partition
# modify on 2012/09/12, fix revert issue, remove this codes
#if [ -e /hivemap/HiveManager/ha/conf/aerohive_ha.conf ]; then
#	/bin/rm -f /hivemap/HiveManager/ha/conf/aerohive_ha.conf
#fi

#if [ -e /hivemap/HiveManager/ha/conf/ha_node_status ]; then
#	/bin/rm -f /hivemap/HiveManager/ha/conf/ha_node_status
#fi

#stop HiveManager
TOMCAT_PID=`ps awx | grep "java.*tomcat" | grep -v grep | head -n 1 | awk '{print $1}'`
if [ ! -z "$TOMCAT_PID" ]; then
	echo "INFO:Tomcat is running, stop it."
	$SHELL_SCRIPTs/stopHiveManage.sh
	
	check_count=0
	check_retry=120
	check_interval=2
	
	TOMCAT_PID=`ps awx | grep "java.*tomcat" | grep -v grep | head -n 1 | awk '{print $1}'`
	while [ ! -z "$TOMCAT_PID" ]
	do
		echo "INFO:Tomcat is still running."
	  	sleep $check_interval
		TOMCAT_PID=`ps awx | grep "java.*tomcat" | grep -v grep | head -n 1 | awk '{print $1}'`
		check_count=`expr $check_count + 1`
		if [ $check_count -gt $check_retry ] && [ ! -z "$TOMCAT_PID" ]; then
			echo "INFO:Kill Tomcat."
			kill -9 $TOMCAT_PID
			killall hm_capwap
		fi
	done
fi

#stop pgpool
chkconfig pgpool-II-91 off
if [ -e /etc/pgpool-II-91/pgpool.conf ]; then
	service pgpool status
	if test $? -eq 0 ; then
		echo "INFO:Stop pgpool service."
		service pgpool stop
	fi
fi

#configure postgresql
chkconfig postgresql on
change_flag=0
PG_DATA=/var/lib/pgsql/9.1/data
if [ -e $PG_DATA/recovery.conf ]; then
	/bin/rm -f $PG_DATA/recovery.conf
	change_flag=1
fi
#restart postgresql if config change
if [ -e $PG_DATA/postgresql.conf ]; then
	service postgresql status
	if [ $? -eq 0 ]; then
		if [ $change_flag -eq 1 ]; then
			echo "INFO:Restart postgresql service."
			service postgresql restart
		fi
	else
		echo "INFO:Start postgresql service."
		service postgresql start
	fi
fi

#make jdbc connect to local database
configureDB LocalHost

# check database is ready
hmpsql hm hivemanager "select hastatus from ha_settings" 120 2 aerohive 1
if [ $? -ne 0 ]; then
	echo "ERROR: Access database failed."
	exit 1
fi

# set enable flag in database
pSQL "update ha_settings set hastatus='1'"
if [ $? -ne 0 ]; then
	echo "ERROR:Setting enable ha flag failed."
	exit 1
fi

pSQL "update ha_settings set enableexternaldb='0'"
if [ $? -ne 0 ]; then
	echo "ERROR:Setting enable ha flag failed."
	exit 1
fi

#start HiveManager
$SHELL_SCRIPTs/startHiveManage.sh

exit 0
