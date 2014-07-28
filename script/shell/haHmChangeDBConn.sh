#!/bin/bash
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

MAP_HIVEMANAGE_HOME=/hivemap/HiveManager
MAP_CATALINA_HOME=$MAP_HIVEMANAGE_HOME/tomcat

if [ -f  $UPDATE_SHELL_HOME/setDBinfo2capwap.sh -a -f $UPDATE_SHELL_HOME/setDBinfo2web.sh  ]
then
	Hibernate_cfg=$HM_ROOT/WEB-INF/classes/hibernate.cfg.xml
	db_host=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
	db_port=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:\([^ ][-A-Za-z0-9\/._\+]*\)\/.*/\1/p'`
	db_name=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:.*\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
	Hm_cfg_pro=$HM_ROOT/WEB-INF/classes/resources/hmConfig.properties
	db_user=`grep hm.connection.username $Hm_cfg_pro  | sed -n 's/.*hm.connection.username=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
	db_passwd=`grep hm.connection.password $Hm_cfg_pro | sed -n 's/.*hm.connection.password=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
	$UPDATE_SHELL_HOME/setDBinfo2web.sh $db_host 9999 $db_name $db_user $db_passwd  $MAP_CATALINA_HOME/webapps/"$APP_HOME"/WEB-INF/classes/hibernate.cfg.xml  $MAP_CATALINA_HOME/webapps/"$APP_HOME"/WEB-INF/classes/resources/hmConfig.properties
	$UPDATE_SHELL_HOME/setDBinfo2capwap.sh $db_host 9999 $db_name $db_user $db_passwd  $MAP_HIVEMANAGE_HOME/capwap/capwap.conf
fi