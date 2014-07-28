#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 1 ]
then
  DOWNLOAD_HOME=$NMS_HOME/webapps/"$APP_HOME"/WEB-INF/downloads

  /bin/rm -rf $UPDATE_HOME

  /bin/mkdir $UPDATE_HOME

  if [ $? != 0 ]
  then
  	  echo "update_in_progress" > /dev/stderr
      exit 1
  fi

  /bin/cp -rf $DOWNLOAD_HOME/"$1" $UPDATE_HOME

  if [ $? != 0 ]
  then
  	  echo "update_in_progress" > /dev/stderr
      exit 1
  fi

  /bin/rm -rf $DOWNLOAD_HOME/"$1"

  if [ $? != 0 ]
  then
  	  echo "update_in_progress" > /dev/stderr
      exit 1
  fi

  cd $UPDATE_HOME

  /bin/tar -xzf "$1"  >>$HM_SCRIPT_LOGFILE 2>&1

  if [ $? != 0 ]
  then
      echo "update_in_progress" > /dev/stderr
      exit 1
  fi

  chmod u+x $UPDATE_HOME/*.sh
  chmod u+x $UPDATE_HOME/HiveManager/script/shell/*.sh

  rm -rf $UPDATE_HOME/"$1"

else
  echo "update_in_progress" > /dev/stderr
  exit 1
fi