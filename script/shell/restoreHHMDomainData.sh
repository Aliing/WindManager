#!/bin/bash
#$1:new domain name
#$2:domain id
#$3:old domain name
#$4:work dir
if [ -z "$1" -o -z "$2" -o -z "$3" -o -z "$4" ]
then
  echo "1" >/dev/stdout
  echo "the function need 5 parameters" >/dev/stdout
  exit 1
fi

. /HiveManager/script/shell/setHmEnv.sh

cd "$4"
 
DOWNLOAD_HOME=/HiveManager/downloads
  
MAPS_HOME=$NMS_HOME/webapps/"$APP_HOME"/domains
 
if [ -d webapps/ROOT/domains/"$3" ] || [ -d HiveManager/tomcat/webapps/ROOT/domains/"$3" ]
then  
  rm -rf $MAPS_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1
  
  mkdir $MAPS_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1
  
  if [ -d webapps/ROOT/domains/"$3" ]
  then
    BACK_MAPS_HOME=webapps/ROOT/domains/"$3" 
  fi

  if [ -d HiveManager/tomcat/webapps/ROOT/domains/"$3" ]
  then
    BACK_MAPS_HOME=HiveManager/tomcat/webapps/ROOT/domains/"$3"
  fi
  
  /bin/cp -rf "$BACK_MAPS_HOME"/* $MAPS_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1
fi  

if [ -d webapps/"$APP_HOME"/domains/"$3" ] || [ -d HiveManager/tomcat/webapps/"$APP_HOME"/domains/"$3" ]
then
  rm -rf $MAPS_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1
  
  mkdir $MAPS_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1
  
  if [ -d webapps/"$APP_HOME"/domains/"$3" ]
  then
     BACK_MAPS_HOME=webapps/"$APP_HOME"/domains/"$3"
  fi

  if [ -d HiveManager/tomcat/webapps/"$APP_HOME"/domains/"$3" ]
  then
     BACK_MAPS_HOME=HiveManager/tomcat/webapps/"$APP_HOME"/domains/"$3"
  fi
  
  /bin/cp -rf "$BACK_MAPS_HOME"/* $MAPS_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1
fi

rm -rf $DOWNLOAD_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1
  
mkdir $DOWNLOAD_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1

mkdir $DOWNLOAD_HOME/"$1"/aerohiveca >>$HM_SCRIPT_LOGFILE 2>&1

if [ -d HiveManager/downloads/"$3" ]
then
  /bin/cp -rf HiveManager/downloads/"$3"/*  $DOWNLOAD_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1
fi

if [ -d $DOWNLOAD_HOME/"$1"/script ]
then
  rm -rf $DOWNLOAD_HOME/"$1"/script/* >>$HM_SCRIPT_LOGFILE 2>&1
fi

mkdir -p $DOWNLOAD_HOME/"$1"/script/bootstrap >>$HM_SCRIPT_LOGFILE 2>&1
  
mkdir -p $DOWNLOAD_HOME/"$1"/script/new >>$HM_SCRIPT_LOGFILE 2>&1
  
mkdir -p $DOWNLOAD_HOME/"$1"/script/run  >>$HM_SCRIPT_LOGFILE 2>&1
  
mkdir -p $DOWNLOAD_HOME/"$1"/script/xml/bootstrap >>$HM_SCRIPT_LOGFILE 2>&1
   
mkdir -p $DOWNLOAD_HOME/"$1"/script/xml/new >>$HM_SCRIPT_LOGFILE 2>&1
   
mkdir -p $DOWNLOAD_HOME/"$1"/script/xml/old >>$HM_SCRIPT_LOGFILE 2>&1
  
mkdir -p $DOWNLOAD_HOME/"$1"/script/xml/run >>$HM_SCRIPT_LOGFILE 2>&1
   
mkdir -p $DOWNLOAD_HOME/"$1"/script/xml/view >>$HM_SCRIPT_LOGFILE 2>&1

## call restore data.
. $SHELL_HOME/setEnv.sh

cd $NMS_HOME

if [ -d  "$4"/dbxmlfile/license ]
then
$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhRestoreLicenseHistory "$4"/dbxmlfile/license/ >>$HM_SCRIPT_LOGFILE 2>&1
fi
  
$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $RESTOREPATH com.ah.be.admin.restoredb.AhRestoreHHMDomainData $2 "$4"/dbxmlfile/"$3"/ "$3" >>$HM_SCRIPT_LOGFILE 2>&1

echo "0" >/dev/stdout
exit 0
