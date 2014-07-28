#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ ! -f $HA_SHELL_HOME/check_ha_mode.sh ]
then
    echo  "1" > /dev/stdout 
    exit 0
fi

$HA_SHELL_HOME/check_ha_mode.sh >>$HM_SCRIPT_LOGFILE 2>&1

echo  $? > /dev/stdout      
exit 0