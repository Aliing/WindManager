#!/bin/bash
#set env
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

#delete files
WORK_HOME=$CATALINA_HOME/work/Catalina/localhost/_
LOG_HOME=$CATALINA_HOME/logs
HM_BACKUP_DIR=/HiveManager/soft_backup

if [ -d $WORK_HOME ]
then
  rm -rf $WORK_HOME/*
fi

##sync config.ini
$UPDATE_HOME/HiveManager/script/shell/syncCfgInfo.sh $HM_ROOT/config.ini  $UPDATE_HOME/hm/config.ini >>$HM_SCRIPT_LOGFILE 2>&1

if [ -f /HiveManager/script/shell/restoreCasInfo.sh ]
then
   . /HiveManager/script/shell/restoreCasInfo.sh $HM_ROOT/WEB-INF/web.xml $UPDATE_HOME/hm/WEB-INF/web.xml >>$HM_SCRIPT_LOGFILE 2>&1
fi

if [ -f $HM_ROOT/WEB-INF/hmconf/jvmoption.conf -a -f  $HM_ROOT/WEB-INF/classes/ehcache.xml ]
then
  JVM_OPT_FILE=$HM_ROOT/WEB-INF/hmconf/jvmoption.conf
  MAX_MEM_OPT=`cat $JVM_OPT_FILE | sed -n 's/.*MAX_MEMORY[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`
  $UPDATE_HOME/HiveManager/script/shell/setJVMOption.sh $MAX_MEM_OPT $UPDATE_HOME/hm/WEB-INF/hmconf/jvmoption.conf   $UPDATE_HOME/hm/WEB-INF/classes/ehcache.xml   >>$HM_SCRIPT_LOGFILE 2>&1
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

##rm -rf $HM_ROOT/*
cd $HM_ROOT
/bin/mv .keystore $NMS_HOME
rm -rf `ls | grep -v "domains$" | grep -v "res$"`
##rm hide file
rm -rf `find ./ -name "\.[a-A0-9]*"`

cd $NMS_HOME
/bin/mv .keystore $HM_ROOT
rm -rf $LOG_HOME/*

if [ -d /remote ]
then
  rm -rf /remote/*
fi

if [ -d /HiveManager/open_file ]
then
  rm -rf /HiveManager/open_file/*
fi

/bin/cp -rf $UPDATE_HOME/hm/*  $HM_ROOT
/bin/cp -rf $UPDATE_HOME/HiveManager/* /HiveManager
/bin/cp -rf $HM_BACKUP_DIR/domains $HM_ROOT
/bin/cp -rf $HM_BACKUP_DIR/res  $HM_ROOT

if [ -d $UPDATE_HOME/HiveManager/cas ]
then
  if [ -d $CATALINA_HOME/webapps/cas ]
  then
    rm -rf $CATALINA_HOME/webapps/cas
  fi

  /bin/mv -f $UPDATE_HOME/HiveManager/cas  $CATALINA_HOME/webapps
  chmod u+x $CATALINA_HOME/webapps/cas/*.sh
fi

#cp data
if [ -d $CATALINA_HOME/dbxmlfile ]
then
  rm -rf $CATALINA_HOME/dbxmlfile
fi

mkdir -p $CATALINA_HOME/dbxmlfile

/bin/cp -rf  $HM_BACKUP_DIR/dbxmlfile/*  $CATALINA_HOME/dbxmlfile

if [ -f $UPDATE_HOME/hm/config.ini ]
then

  UPDATE_APPTYPE=`cat $UPDATE_HOME/hm/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`

  if [  $UPDATE_APPTYPE != "HM" -a $UPDATE_APPTYPE != "hm" ]
  then
    mkdir -p $CATALINA_HOME/webapps/ROOT

    /bin/cp -rf $UPDATE_HOME/aeronms_os_pack/root/index.html $CATALINA_HOME/webapps/ROOT/
  fi
fi

if [ -f $UPDATE_SHELL_HOME/catalina.sh ]
then
  /bin/cp -rf $UPDATE_SHELL_HOME/catalina.sh $CATALINA_HOME/bin

  chmod u+x $CATALINA_HOME/bin/*.sh
fi

/bin/cp -rf $UPDATE_SHELL_HOME/startupWizard.sh /root/

chmod u+x /root/*.sh

chmod u+x $HM_BASEHOME/capwap/*

chmod u+x $HM_BASEHOME/cli_parser/parse_cli

chmod u+x $HM_BASEHOME/license/*

chmod u+x $HM_BASEHOME/encryptscpuser/*

chmod u+x $HM_BASEHOME/image_signing/*

chmod u+x $HM_BASEHOME/generate_passwd/*

rm -rf $CATALINA_HOME/.singlepart

