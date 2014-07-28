#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ ! -d /version_check_images ]
then
    /bin/mkdir /version_check_images
fi

/bin/cp -vfu $2 /version_check_images >>$HM_SCRIPT_LOGFILE 2>&1

$SHELL_HOME/ah_imginfo_version_image_update $1 /version_check_images/`basename $2` $3