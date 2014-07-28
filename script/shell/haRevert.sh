#!/bin/bash
. /HiveManager/script/shell/setHmEnv.sh

HA_EXEC_HOME=/HiveManager/ha/ha-d

if [ ! -x $HA_EXEC_HOME/ha-d ]
then
  echo  "1" > /dev/stdout 
  exit 0
fi

$HA_EXEC_HOME/ha-d -c 4 >>$HM_SCRIPT_LOGFILE 2>&1

echo  $? > /dev/stdout      
exit 0