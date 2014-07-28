#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

HA_EXEC_HOME=/HiveManager/ha/ha-d
PARAM_LIST_FILE=update_param_list.txt

if [ -z "$1" ]
then
  echo "1" > /dev/stdout
  exit 0
fi

if [ ! -x $HA_EXEC_HOME/ha-d ]
then
  echo  "1" > /dev/stdout 
  exit 0
fi

if [ -f $UPDATE_HOME/$PARAM_LIST_FILE ]
then
  rm -rf $UPDATE_HOME/$PARAM_LIST_FILE >>$HM_SCRIPT_LOGFILE 2>&1
fi

echo "$1" >> $UPDATE_HOME/$PARAM_LIST_FILE

$HA_EXEC_HOME/ha-d -c 3 >>$HM_SCRIPT_LOGFILE 2>&1

echo  $? > /dev/stdout      
exit 0