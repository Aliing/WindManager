#!/bin/bash

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "syncSoftCfgFromTomcat start..." >> $HM_SCRIPT_LOGFILE  2>&1

DOWNLOAD_HOME=/HiveManager/downloads

IMAGE_HOME=$NMS_HOME/webapps/ROOT/domains
IMAGE_HOME_HM=$NMS_HOME/webapps/"$APP_HOME"/domains

MAP_HIVEMANAGE_HOME=/hivemap/HiveManager
MAP_CATALINA_HOME=$MAP_HIVEMANAGE_HOME/tomcat

#MAP_VERSIONFILE=$MAP_CATALINA_HOME/webapps/ROOT/WEB-INF/hmconf/hivemanager.ver
MAP_VERSIONFILE=$MAP_CATALINA_HOME/webapps/"$APP_HOME"/WEB-INF/hmconf/hivemanager.ver

if [ -d $NMS_HOME/webapps/ROOT ]
then
  VERSIONFILE=$NMS_HOME/webapps/ROOT/WEB-INF/hmconf/hivemanager.ver  
  if [ -f $NMS_HOME/webapps/ROOT/.keystore ]
  then
  /bin/cp -rf $NMS_HOME/webapps/ROOT/.keystore    $MAP_CATALINA_HOME/webapps/$APP_HOME  
  fi
fi

if [ -d $NMS_HOME/webapps/$APP_HOME ]
then
   VERSIONFILE=$NMS_HOME/webapps/"$APP_HOME"/WEB-INF/hmconf/hivemanager.ver  
   if [ -f $NMS_HOME/webapps/"$APP_HOME"/.keystore ]
   then
  /bin/cp -rf $NMS_HOME/webapps/"$APP_HOME"/.keystore    $MAP_CATALINA_HOME/webapps/$APP_HOME  
   fi
fi

SSLCertificateFile=/etc/pki/tls/certs/localhost.crt
SSLCertificateKeyFile=/etc/pki/tls/private/localhost.key

if [ -f $SSLCertificateFile ]
then
  /bin/cp -rf  $SSLCertificateFile /hivemap/$SSLCertificateFile
fi

if [ -f $SSLCertificateKeyFile ]
then
  /bin/cp -rf $SSLCertificateKeyFile /hivemap/$SSLCertificateKeyFile
fi	

if [ -f $NMS_HOME/conf/uploadfile.xml ]
then
    /bin/cp -rf $NMS_HOME/conf/uploadfile.xml $MAP_CATALINA_HOME/conf
else
    /bin/cp -rf  /hivemap/HiveManager/script/shell/uploadfile.xml $MAP_CATALINA_HOME/conf
fi

if [ -f /HiveManager/ha/opt/ha_initialized ]
then
  if [ ! -d /hivemap/HiveManager/ha/opt ]
  then
      mkdir -p /hivemap/HiveManager/ha/opt      
  fi
  
   /bin/cp -rf /HiveManager/ha/opt/ha_initialized /hivemap/HiveManager/ha/opt
fi

if [ -f /HiveManager/ha/opt/ha_node_num ]
then
    if [ ! -d /hivemap/HiveManager/ha/opt ]
    then
      mkdir -p /hivemap/HiveManager/ha/opt      
    fi
    
    /bin/cp -rf /HiveManager/ha/opt/ha_node_num /hivemap/HiveManager/ha/opt
fi

##if [ -f /hivemap/var/lib/heartbeat/crm/cib.xml ]
##then
##   rm -rf /hivemap/var/lib/heartbeat/crm/cib.xml
##fi

cd $NMS_HOME

if [ -d $DOWNLOAD_HOME ] && [ -d $IMAGE_HOME_HM ]  
then
    . $UPDATE_SHELL_HOME/syncSoftCfgFrom33.sh $1
    if [ -d /var/www/html/gm ]
    then
      . $UPDATE_SHELL_HOME/syncGusetManager.sh
    fi
else
  if [ -d $DOWNLOAD_HOME ] && [ -d $IMAGE_HOME ]
  then
    . $UPDATE_SHELL_HOME/syncSoftCfgFrom31.sh $1
  else
    . $UPDATE_SHELL_HOME/syncSoftCfgFrom30.sh $1
  fi
fi

if [ ! -f $MAP_HIVEMANAGE_HOME/update_history.log ]
then
  touch $MAP_HIVEMANAGE_HOME/update_history.log
fi

if [ -f /HiveManager/update_history.log ]
then
    echo "" > $MAP_HIVEMANAGE_HOME/update_history.log
    cat  /HiveManager/update_history.log >>  $MAP_HIVEMANAGE_HOME/update_history.log 
fi

DATE=`date +%F%R`

SRC_MAINVERSION=`cat $VERSIONFILE | sed -n 's/.*MAINVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

SRC_SUBVERSION=`cat $VERSIONFILE | sed -n 's/.*SUBVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

SRC_BUILDTIME=`cat $VERSIONFILE | sed -n 's/.*BUILDTIME=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

DST_MAINVERSION=`cat $MAP_VERSIONFILE | sed -n 's/.*MAINVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

DST_SUBVERSION=`cat $MAP_VERSIONFILE | sed -n 's/.*SUBVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

DST_BUILDTIME=`cat $MAP_VERSIONFILE | sed -n 's/.*BUILDTIME=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

RECORD="$DATE, version `echo $SRC_MAINVERSION`r`echo $SRC_SUBVERSION` $SRC_BUILDTIME upgraded to version `echo $DST_MAINVERSION`r`echo $DST_SUBVERSION` $DST_BUILDTIME"

echo $RECORD >> $MAP_HIVEMANAGE_HOME/update_history.log

if [ -d /HiveManager/downloads/home/hiveManagerImage ]
then
  rm -rf /HiveManager/downloads/home/hiveManagerImage/* >>$HM_SCRIPT_LOGFILE 2>&1
fi

echo "syncSoftCfgFromTomcat end." >>$HM_SCRIPT_LOGFILE
