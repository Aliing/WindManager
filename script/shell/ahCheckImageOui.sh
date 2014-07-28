#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ ! -d /oui_check_images ]
then
    /bin/mkdir /oui_check_images
fi

/bin/cp -vfu $3 /oui_check_images >>$HM_SCRIPT_LOGFILE 2>&1

$SHELL_HOME/ah_imginfo_oui_image_update $1 $2 /oui_check_images/`basename $3`