#!/bin/bash

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

DOWNLOAD_HOME=$NMS_HOME/webapps/ROOT/WEB-INF/downloads
IMAGE_HOME=$NMS_HOME/webapps/ROOT/images/maps
SHELL_HOME_V30=$NMS_HOME/webapps/ROOT/WEB-INF/shell

MAP_HIVEMANAGE_HOME=/hivemap/HiveManager
MAP_CATALINA_HOME=$MAP_HIVEMANAGE_HOME/tomcat
MAP_DOWNLOADS_HOME=/hivemap/HiveManager/downloads
MAP_IMAGE_HOME=$MAP_HIVEMANAGE_HOME/tomcat/webapps/"$APP_HOME"/domains

rm -rf $MAP_DOWNLOADS_HOME
mkdir $MAP_DOWNLOADS_HOME

if [ -d $MAP_IMAGE_HOME ]
then
  rm -rf $MAP_IMAGE_HOME/*
else
  mkdir $MAP_IMAGE_HOME
fi

mkdir $MAP_DOWNLOADS_HOME/home
mkdir $MAP_IMAGE_HOME/home 

#restore map
mkdir $MAP_IMAGE_HOME/home/maps
 cp -rf $IMAGE_HOME/* $MAP_IMAGE_HOME/home/maps 

#restire download
 mkdir -p $MAP_DOWNLOADS_HOME/home/aerohiveca/
  
 if [ -d $DOWNLOAD_HOME/aerohiveca ]
 then   
  /bin/cp -rf $DOWNLOAD_HOME/aerohiveca $MAP_DOWNLOADS_HOME/home/    
 fi 
 
if [ -d $DOWNLOAD_HOME/image ]
 then
   mkdir -p $MAP_DOWNLOADS_HOME/home/image
   /bin/cp -rf $DOWNLOAD_HOME/image $MAP_DOWNLOADS_HOME/home/
 fi 

 if [ -d $DOWNLOAD_HOME/cwp/webpage ]
 then
   mkdir -p $MAP_DOWNLOADS_HOME/home/cwp/webpage
   /bin/cp -rf $DOWNLOAD_HOME/cwp/webpage $MAP_DOWNLOADS_HOME/home/cwp
 fi 
 
 if [ -d $DOWNLOAD_HOME/cwp/serverkey ]
 then
   mkdir -p $MAP_DOWNLOADS_HOME/home/cwp/serverkey
   /bin/cp -rf $DOWNLOAD_HOME/cwp/serverkey $MAP_DOWNLOADS_HOME/home/cwp
 fi

 
   
   mkdir -p $MAP_DOWNLOADS_HOME/home/script/bootstrap
  
   mkdir -p $MAP_DOWNLOADS_HOME/home/script/new
  
   mkdir -p $MAP_DOWNLOADS_HOME/home/script/run
    
   mkdir -p $MAP_DOWNLOADS_HOME/home/script/xml/bootstrap
  
   mkdir -p $MAP_DOWNLOADS_HOME/home/script/xml/new
   
   mkdir -p $MAP_DOWNLOADS_HOME/home/script/xml/old
  
   mkdir -p $MAP_DOWNLOADS_HOME/home/script/xml/run
   
   mkdir -p $MAP_DOWNLOADS_HOME/home/script/xml/view
   
   /bin/rm -rf $MAP_CATALINA_HOME/dbxmlfile
   /bin/mkdir  $MAP_CATALINA_HOME/dbxmlfile   
   
   cd $NMS_HOME
  
   . $SHELL_HOME_V30/setEnv.sh

   $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupDatabase $MAP_CATALINA_HOME/dbxmlfile $1
 
   touch $MAP_CATALINA_HOME/dbxmlfile/webversion
  
   touch $MAP_CATALINA_HOME/.swupdated  
