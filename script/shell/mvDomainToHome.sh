#!/bin/bash
#$1:domain name

. /HiveManager/script/shell/setHmEnv.sh

 TMP_HOME=$NMS_HOME/tmp
 
 cd $TMP_HOME
 
  if [ -d HiveManager/downloads ] 
  then
    
    if [ -d webapps/ROOT/domains ]
    then
      BACK_MAPS_HOME=webapps/ROOT/domains
    fi  
    
    if [ -d webapps/"$APP_HOME"/domains ]
    then
      BACK_MAPS_HOME=webapps/"$APP_HOME"/domains
    fi
    
    if [ -z "$BACK_MAPS_HOME" ]
    then
      echo "restore_in_progress" > /dev/stderr
      exit 1
    fi
  else  
    echo "restore_in_progress" > /dev/stderr
    exit 1
  fi 
  
  cd $TMP_HOME
  cd dbxmlfile
  
  if [ -d "$1" ]
  then  
    mv -f "$1" home 
  
    if [ -f home/hm_domain.xml ]
    then
      rm -rf home/hm_domain.xml
    fi
  
  fi
  
  #cd webapps/ROOT/domains
  cd $TMP_HOME
  cd "$BACK_MAPS_HOME"
  
  if [ -d "$1" ]
  then
    mv -f "$1" home   
  fi
  
  cd $TMP_HOME
  cd HiveManager/downloads
  
  if [ -d "$1" ]
  then
    mv -f "$1" home 
  fi
  
  exit 0