# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 1 ]
then

  cd $NMS_HOME

  WEBINFO_HOME=$HM_ROOT/WEB-INF

  if [ ! -d $WEBINFO_HOME/downloads/backupstorage ]
  then
    mkdir $WEBINFO_HOME/downloads/backupstorage
  fi

  /bin/cp -rf $WEBINFO_HOME/downloads/$1 $WEBINFO_HOME/downloads/backupstorage/

  rm -rf $WEBINFO_HOME/downloads/$1
else
  exit 1
fi
