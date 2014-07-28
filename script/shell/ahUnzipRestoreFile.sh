#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 1 ]
then
  DOWNLOAD_HOME=$NMS_HOME/webapps/"$APP_HOME"/WEB-INF/downloads
  
  cd $NMS_HOME 
  
  rm -fr tmp
  
  mkdir tmp
  
  cd tmp

  tar zxf $DOWNLOAD_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1  
 
  if [ ! -d dbxmlfile ]
  then 
     echo "restore_in_progress"
     exit 1
  fi
  
  cd $NMS_HOME
  
  rm -rf $DOWNLOAD_HOME/"$1"

else
  echo "restore_in_progress" > /dev/stderr
  exit 1
fi