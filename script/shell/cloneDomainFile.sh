#!/bin/bash
##$1 is src domain
##$2 is dest domain

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 2 ]
then
  
  DOWNLOAD_HOME=/HiveManager/downloads
  
  
  
  IMAGE_HOME=$HM_ROOT/domains
  
  if [ ! -d $DOWNLOAD_HOME/"$1" ] || [ ! -d $IMAGE_HOME/"$1" ]
  then
    echo "clone_error" >/dev/stderr
    exit 1
  fi
  
  if [ ! -d $DOWNLOAD_HOME/"$2" ] || [ ! -d $IMAGE_HOME/"$2" ]
  then
    echo "clone_error" >/dev/stderr
    exit 1
  fi
  
  /bin/cp -rf $IMAGE_HOME/"$1"/* $IMAGE_HOME/"$2"
  
  if [ -d $DOWNLOAD_HOME/"$1"/cwp ]
  then
    /bin/cp -rf $DOWNLOAD_HOME/"$1"/cwp $DOWNLOAD_HOME/"$2"
  fi
  
  if [ -d $DOWNLOAD_HOME/"$1"/aerohiveca ]
  then
    /bin/cp -rf $DOWNLOAD_HOME/"$1"/aerohiveca $DOWNLOAD_HOME/"$2"
  fi
  
  if [ -d $DOWNLOAD_HOME/"$1"/ppsk ]
  then
    /bin/cp -rf $DOWNLOAD_HOME/"$1"/ppsk $DOWNLOAD_HOME/"$2"
  fi
    
else
  echo "clone_error" >/dev/stderr
  exit 1
fi
