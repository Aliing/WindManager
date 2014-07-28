#!/bin/bash
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "haHmAppVerify.sh start..." >>$HM_SCRIPT_LOGFILE

if [ -f $UPDATE_HOME/"$1" ]
then
    rm -rf $UPDATE_HOME/"$1"
fi

if [ -f $UPDATE_HOME/hm/WEB-INF/hmconf/hivemanager.ver ]
then
	MODEL=`cat $UPDATE_HOME/hm/WEB-INF/hmconf/hivemanager.ver | sed -n 's/.*MACHINETYPE=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
	if [ -z $MODEL ]
	then
		echo "update_in_progress" > /dev/stderr
	    exit 1
	fi
	MOCHINE_MODE=`getconf LONG_BIT`
	if [ $MODEL != $MOCHINE_MODE ]
	then
		echo "update_in_progress" > /dev/stderr
		exit 1
	fi
fi

RPMS_HOME=$UPDATE_HOME/rpms

if [ ! -d $UPDATE_HOME/hm ] || [ ! -d $UPDATE_HOME/HiveManager ]
then
	echo "update_in_progress" > /dev/stderr
	exit 1
fi

if [ ! -d $UPDATE_HOME/rpms ]
then
	echo "update_in_progress" > /dev/stderr
	exit 1
fi

if [ ! -f $UPDATE_HOME/hm/config.ini ]
then
	echo "update_in_progress" > /dev/stderr
	exit 1
fi

UPDATE_APPTYPE=`cat $UPDATE_HOME/hm/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`
HM_APPTYPE=HM

if [ -f $HM_ROOT/config.ini ]
then
	HM_APPTYPE=`cat $HM_ROOT/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`
fi

HTTPD_EXISTS=`chkconfig --list httpd > /dev/null 2>&1`

if [ $HM_APPTYPE == "HM" -o $HM_APPTYPE == "hm" ]
then
	if [ $UPDATE_APPTYPE != "HM" -a $UPDATE_APPTYPE != "hm" ]
	then
		echo "update_in_progress" > /dev/stderr
		exit 1
	fi
else
    if [ $UPDATE_APPTYPE == "HM" -o $UPDATE_APPTYPE == "hm" ]
    then
    	echo "update_in_progress" > /dev/stderr
    	exit 1
    fi
fi

echo "haHmAppVerify.sh end." >>$HM_SCRIPT_LOGFILE