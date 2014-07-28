#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

LOG_HOME=$HM_ROOT/WEB-INF/logs

SHOW_LOG=$LOG_HOME/showshell.log

if [ -f $SHOW_LOG ]
then

  echo "" > $SHOW_LOG
fi

