#!/bin/bash

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "updateFiles start..." >>$HM_SCRIPT_LOGFILE

MAP_HIVEMANAGE_HOME=/hivemap/HiveManager
MAP_CATALINA_HOME=$MAP_HIVEMANAGE_HOME/tomcat

MAP_HM_HOME=$MAP_CATALINA_HOME/webapps/"$APP_HOME"
MAP_SHELL_HOME=/hivemap/HiveManager/script/shell
MAP_CAS_HOME=$MAP_CATALINA_HOME/webapps/cas

MAP_WORK_HOME=$MAP_CATALINA_HOME/work/Catalina/localhost/_
MAP_CATA_LOG_HOME=$MAP_CATALINA_HOME/logs

if [ -d $MAP_WORK_HOME ]
then
  rm -rf $MAP_WORK_HOME
fi

#if [ -d  $MAP_CATALINA_HOME/webapps/ROOT ]
#then
#  rm -rf $MAP_CATALINA_HOME/webapps/ROOT
#fi

if [ ! -d  $MAP_HM_HOME ]
then
  mkdir -p $MAP_HM_HOME
fi

##remove the src lib
rm -rf $MAP_CATA_LOG_HOME/*
rm -rf $MAP_HM_HOME/*
rm -rf /hivemap/remote/*
##

if [ -d $MAP_CAS_HOME ]
then
  rm -rf $MAP_CAS_HOME
fi

if [ -d $MAP_HIVEMANAGE_HOME/open_file ]
then
    rm -rf $MAP_HIVEMANAGE_HOME/open_file/*
fi

##sync config.ini
if [ -f $HM_ROOT/config.ini ]
then
  $UPDATE_HOME/HiveManager/script/shell/syncCfgInfo.sh $HM_ROOT/config.ini  $UPDATE_HOME/hm/config.ini  >>$HM_SCRIPT_LOGFILE 2>&1
fi

if [ -f $HM_ROOT/WEB-INF/hmconf/jvmoption.conf -a -f $HM_ROOT/WEB-INF/classes/ehcache.xml ]
then
  JVM_OPT_FILE=$HM_ROOT/WEB-INF/hmconf/jvmoption.conf 
  MAX_MEM_OPT=`cat $JVM_OPT_FILE | sed -n 's/.*MAX_MEMORY[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'` 
  $UPDATE_HOME/HiveManager/script/shell/setJVMOption.sh $MAX_MEM_OPT $UPDATE_HOME/hm/WEB-INF/hmconf/jvmoption.conf   $UPDATE_HOME/hm/WEB-INF/classes/ehcache.xml  >>$HM_SCRIPT_LOGFILE 2>&1
fi  

if [ -f  $SHELL_HOME/setDBinfo2capwap.sh -a -f $SHELL_HOME/setDBinfo2web.sh  ]
then
  #get db infor
  Hibernate_cfg=$HM_ROOT/WEB-INF/classes/hibernate.cfg.xml
  db_host=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
  db_port=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:\([^ ][-A-Za-z0-9\/._\+]*\)\/.*/\1/p'`
  db_name=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:.*\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
  Hm_cfg_pro=$HM_ROOT/WEB-INF/classes/resources/hmConfig.properties
  db_user=`grep hm.connection.username $Hm_cfg_pro  | sed -n 's/.*hm.connection.username=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
  db_passwd=`grep hm.connection.password $Hm_cfg_pro | sed -n 's/.*hm.connection.password=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
  $UPDATE_HOME/HiveManager/script/shell/setDBinfo2web.sh $db_host $db_port $db_name $db_user $db_passwd  $UPDATE_HOME/hm/WEB-INF/classes/hibernate.cfg.xml  $UPDATE_HOME/hm/WEB-INF/classes/resources/hmConfig.properties
  $UPDATE_HOME/HiveManager/script/shell/setDBinfo2capwap.sh $db_host $db_port $db_name $db_user $db_passwd  $UPDATE_HOME/HiveManager/capwap/capwap.conf
fi


/bin/cp -rf $UPDATE_HOME/hm/* $MAP_HM_HOME

if [ -d $UPDATE_HOME/HiveManager/cas ]
then
        /bin/mv -f $UPDATE_HOME/HiveManager/cas $MAP_CATALINA_HOME/webapps
        if [ -d $NMS_HOME/webapps/cas ]
        then
        	    /bin/cp -f $NMS_HOME/webapps/cas/WEB-INF/deployerConfigContext.xml $MAP_CAS_HOME/WEB-INF/
        fi
                       SHELL_FILE_NAMES=`/bin/ls $MAP_CATALINA_HOME/webapps/cas/ |grep .sh`
				       if [ ! "$SHELL_FILE_NAMES" = "" ]
				       then
				       chmod u+x $MAP_CATALINA_HOME/webapps/cas/*.sh
				       fi
fi

if [ -f $UPDATE_HOME/hm/config.ini ]
then

  UPDATE_APPTYPE=`cat $UPDATE_HOME/hm/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`
  
  if [ $UPDATE_APPTYPE != "HM" -a $UPDATE_APPTYPE != "hm" ]
  then
    mkdir -p $MAP_CATALINA_HOME/webapps/ROOT
    
    /bin/cp -rf $UPDATE_HOME/aeronms_os_pack/root/index.html $MAP_CATALINA_HOME/webapps/ROOT/
  fi
fi

if [ -f $UPDATE_SHELL_HOME/catalina.sh ]
then
  /bin/cp -rf $UPDATE_SHELL_HOME/catalina.sh $MAP_CATALINA_HOME/bin
  
  chmod u+x $MAP_CATALINA_HOME/bin/*.sh
fi

if [ -f /etc/xinetd.d/tftp ]
then
  /bin/cp -rf /etc/xinetd.d/tftp /hivemap/etc/xinetd.d/tftp >>$HM_SCRIPT_LOGFILE 2>&1
fi
  
/bin/cp -rf $UPDATE_HOME/HiveManager/*  $MAP_HIVEMANAGE_HOME/

chmod u+x $MAP_SHELL_HOME/*

/bin/cp -rf $MAP_SHELL_HOME/startupWizard.sh /hivemap/root/

chmod u+x /hivemap/root/*.sh

chmod u+x $MAP_HIVEMANAGE_HOME/capwap/*

chmod u+x $MAP_HIVEMANAGE_HOME/cli_parser/parse_cli

chmod u+x $MAP_HIVEMANAGE_HOME/license/*

chmod u+x $MAP_HIVEMANAGE_HOME/encryptscpuser/*

chmod u+x $MAP_HIVEMANAGE_HOME/image_signing/*

chmod u+x $MAP_HIVEMANAGE_HOME/generate_passwd/*

MAP_CATALINA_HOME=/hivemap/$CATALINA_HOME

if [ -d $NMS_HOME/webapps/ROOT ]
then
  if [ -f $NMS_HOME/webapps/ROOT/.hm_license ]
  then
    /bin/cp -rf $NMS_HOME/webapps/ROOT/.hm_license  $MAP_CATALINA_HOME/webapps/$APP_HOME
  fi
    
  if [ -f $NMS_HOME/webapps/ROOT/WEB-INF/aerohive-routes ]
  then
    /bin/cp -rf $NMS_HOME/webapps/ROOT/WEB-INF/aerohive-routes $MAP_CATALINA_HOME/webapps/"$APP_HOME"/WEB-INF/
  fi  
fi

if [ -d $NMS_HOME/webapps/$APP_HOME ]
then

  if [ -f $NMS_HOME/webapps/"$APP_HOME"/.hm_license ]
  then
    /bin/cp -rf $NMS_HOME/webapps/"$APP_HOME"/.hm_license  $MAP_CATALINA_HOME/webapps/$APP_HOME
  fi
  
  if [ -f $NMS_HOME/webapps/"$APP_HOME"/WEB-INF/aerohive-routes ]
  then
    /bin/cp -rf $NMS_HOME/webapps/"$APP_HOME"/WEB-INF/aerohive-routes $MAP_CATALINA_HOME/webapps/"$APP_HOME"/WEB-INF/
  fi  
  
  if [ -d $NMS_HOME/webapps/$APP_HOME/upload ]
  then
    /bin/cp -rf $NMS_HOME/webapps/$APP_HOME/upload $MAP_CATALINA_HOME/webapps/$APP_HOME
  fi
  
fi
  
echo "updateFiles end." >>$HM_SCRIPT_LOGFILE

