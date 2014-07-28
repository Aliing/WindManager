#! /bin/bash

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

Hibernate_cfg=$UPDATE_HOME/hm/WEB-INF/classes/hibernate.cfg.xml
db_host=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_port=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:\([^ ][-A-Za-z0-9\/._\+]*\)\/.*/\1/p'`
db_name=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:.*\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
Hm_cfg_pro=$UPDATE_HOME/hm/WEB-INF/classes/resources/hmConfig.properties
db_user=`grep hm.connection.username $Hm_cfg_pro  | sed -n 's/.*hm.connection.username=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_passwd=`grep hm.connection.password $Hm_cfg_pro | sed -n 's/.*hm.connection.password=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

$UPDATE_SHELL_HOME/setDBinfo2capwap.sh $db_host $db_port $db_name $db_user $db_passwd  $UPDATE_HOME/HiveManager/capwap/capwap.conf >>$HM_SCRIPT_LOGFILE 2>&1
