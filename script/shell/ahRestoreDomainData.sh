#!/bin/bash
#$1:new doamin name $2:domain id $3:old domain name
. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 3 ]
then
  TMP_HOME=$NMS_HOME/tmp
  DOWNLOAD_HOME=/HiveManager/downloads

  rm -rf dbxmlfile
  mkdir dbxmlfile 

  MAPS_HOME=$NMS_HOME/webapps/"$APP_HOME"/domains

  cd $TMP_HOME
  
  if [ -d HiveManager/downloads/"$3" ] 
  then
    
    if [ -d webapps/ROOT/domains/"$3" ]
    then
      BACK_MAPS_HOME=webapps/ROOT/domains/"$3"
    fi  
    
    if [ -d webapps/"$APP_HOME"/domains/"$3" ]
    then
      BACK_MAPS_HOME=webapps/"$APP_HOME"/domains/"$3"
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

  /bin/cp -rf dbxmlfile/* $NMS_HOME/dbxmlfile/
  
  rm -rf $MAPS_HOME/"$1"
  
  mkdir $MAPS_HOME/"$1"
  
  cp -rf "$BACK_MAPS_HOME"/* $MAPS_HOME/"$1"
  
  rm -rf $DOWNLOAD_HOME/"$1"
  
  mkdir $DOWNLOAD_HOME/"$1"
  
  mkdir $DOWNLOAD_HOME/"$1"/aerohiveca
  
  if [ -d HiveManager/downloads/"$3"/aerohiveca ]
  then   
  /bin/cp -rf HiveManager/downloads/"$3"/aerohiveca $DOWNLOAD_HOME/"$1"    
  fi
 
 
 if [ -d HiveManager/downloads/"$3"/image ]
 then
   mkdir -p $DOWNLOAD_HOME/"$1"/image
   /bin/cp -rf HiveManager/downloads/"$3"/image $DOWNLOAD_HOME/"$1"
 fi 

 if [ -d HiveManager/downloads/"$3"/cwp/webpage ]
 then
   mkdir -p $DOWNLOAD_HOME/"$1"/cwp/webpage
   /bin/cp -rf HiveManager/downloads/"$3"/cwp/webpage $DOWNLOAD_HOME/"$1"/cwp
 fi
 
 if [ -d HiveManager/downloads/"$3"/cwp/serverkey ]
 then
   mkdir -p $DOWNLOAD_HOME/"$1"/cwp/serverkey
   /bin/cp -rf HiveManager/downloads/"$3"/cwp/serverkey $DOWNLOAD_HOME/"$1"/cwp
 fi
     
   mkdir -p $DOWNLOAD_HOME/"$1"/script/bootstrap
  
   mkdir -p $DOWNLOAD_HOME/"$1"/script/new
  
   mkdir -p $DOWNLOAD_HOME/"$1"/script/run  
  
   mkdir -p $DOWNLOAD_HOME/"$1"/script/xml/bootstrap
   
   mkdir -p $DOWNLOAD_HOME/"$1"/script/xml/new
   
   mkdir -p $DOWNLOAD_HOME/"$1"/script/xml/old
  
   mkdir -p $DOWNLOAD_HOME/"$1"/script/xml/run
   
   mkdir -p $DOWNLOAD_HOME/"$1"/script/xml/view
    
  cd $NMS_HOME
  rm -fr tmp  

  . $SHELL_HOME/setEnv.sh

  cd $NMS_HOME
  
  $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $RESTOREPATH com.ah.be.admin.restoredb.AhRestoreDomainData $2 $3 >>$HM_SCRIPT_LOGFILE 2>&1 

else  
  echo "restore_in_progress" > /dev/stderr
  exit 1
fi