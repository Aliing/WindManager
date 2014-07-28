#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

TMP_HOME=$NMS_HOME/tmp

DOWNLOAD_HOME=/HiveManager/downloads/home

MAPS_HOME=$NMS_HOME/webapps/"$APP_HOME"/domains/home

rm -rf dbxmlfile
mkdir dbxmlfile 

cd $TMP_HOME

if [ ! -d webapps ]
then
  echo "restore_in_progress" > /dev/stderr
  exit 1
fi

 rm -rf /HiveManager/downloads/*
  
 rm -rf $NMS_HOME/webapps/"$APP_HOME"/domains/*

 /bin/cp -rf dbxmlfile/* $NMS_HOME/dbxmlfile/ 
 
 mkdir $MAPS_HOME
 
 cp -rf webapps/ROOT/images/* $MAPS_HOME/
 
 mkdir -p $DOWNLOAD_HOME
 
 mkdir -p $DOWNLOAD_HOME/aerohiveca/
  
 if [ -d webapps/ROOT/WEB-INF/downloads/aerohiveca ]
 then   
  /bin/cp -rf webapps/ROOT/WEB-INF/downloads/aerohiveca $DOWNLOAD_HOME/    
 fi 
 
if [ -d webapps/ROOT/WEB-INF/downloads/image ]
 then
   mkdir -p $DOWNLOAD_HOME/image
   /bin/cp -rf webapps/ROOT/WEB-INF/downloads/image $DOWNLOAD_HOME/
 fi 

 if [ -d webapps/ROOT/WEB-INF/downloads/cwp/webpage ]
 then
   mkdir -p $DOWNLOAD_HOME/cwp/webpage
   /bin/cp -rf webapps/ROOT/WEB-INF/downloads/cwp/webpage $DOWNLOAD_HOME/cwp
 fi 
 
 if [ -d webapps/ROOT/WEB-INF/downloads/cwp/serverkey ]
 then
   mkdir -p $DOWNLOAD_HOME/cwp/serverkey
   /bin/cp -rf webapps/ROOT/WEB-INF/downloads/cwp/serverkey $DOWNLOAD_HOME/cwp
 fi

 
   
   mkdir -p $DOWNLOAD_HOME/script/bootstrap
  
   mkdir -p $DOWNLOAD_HOME/script/new
  
   mkdir -p $DOWNLOAD_HOME/script/run
  
   mkdir -p $DOWNLOAD_HOME/script/xml/bootstrap
  
   mkdir -p $DOWNLOAD_HOME/script/xml/new
   
   mkdir -p $DOWNLOAD_HOME/script/xml/old
  
   mkdir -p $DOWNLOAD_HOME/script/xml/run
   
   mkdir -p $DOWNLOAD_HOME/script/xml/view
 
  cd $NMS_HOME
  rm -fr tmp
    
  touch .swupdated    
 
  exit 0
 