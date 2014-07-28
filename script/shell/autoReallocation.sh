#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ ! -f /root/.memsize ]; then
	echo -n "0" > /root/.memsize
fi

memsize=`cat /root/.memsize`
total_memory=`free -m|grep "Mem:"|awk '{print $2}'`

if [ $memsize -ne $total_memory ]; then
	touch /root/.auto_jvm_memory
	$SHELL_HOME/setJVMMemory.sh >> $HM_SCRIPT_LOGFILE 2>&1

	touch /root/.auto_db_memory
	$SHELL_HOME/confPostgresMem.sh >> $HM_SCRIPT_LOGFILE 2>&1
	service postgresql restart >> $HM_SCRIPT_LOGFILE 2>&1

	echo -n "$total_memory" 1> /root/.memsize 2>> $HM_SCRIPT_LOGFILE
fi