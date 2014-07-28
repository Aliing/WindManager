#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -f /root/.is_field_hmol ]
then
  exit 0
fi

if [ -f $SHELL_HOME/setJVMMemory.sh ]; then
	# for upgrade use
	exist_flag=0
	if [ -f /hivemap/root/.auto_jvm_memory ]; then
		$exist_flag=1
	fi
	touch /root/.auto_jvm_memory

	$SHELL_HOME/setJVMMemory.sh

	if [ $exist_flag == 0 ]; then
		/bin/rm -f /root/.auto_jvm_memory
	fi
fi
