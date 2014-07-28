#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ ! -d /switchchip_check_images ]
then
    /bin/mkdir /switchchip_check_images
fi

/bin/cp -vfu $2 /switchchip_check_images >>$HM_SCRIPT_LOGFILE 2>&1

$SHELL_HOME/ah_imginfo_br200_new_switch_chip_check $1 /switchchip_check_images/`basename $2`