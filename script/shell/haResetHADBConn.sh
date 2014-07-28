#! /bin/bash

. /HiveManager/script/shell/setHmEnv.sh

Hibernate_cfg=$UPDATE_HOME/hm/WEB-INF/classes/hibernate.cfg.xml
db_host=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_port=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:\([^ ][-A-Za-z0-9\/._\+]*\)\/.*/\1/p'`
db_name=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:.*\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
Hm_cfg_pro=$UPDATE_HOME/hm/WEB-INF/classes/resources/hmConfig.properties
db_user=`grep hm.connection.username $Hm_cfg_pro  | sed -n 's/.*hm.connection.username=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_passwd=`grep hm.connection.password $Hm_cfg_pro | sed -n 's/.*hm.connection.password=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

if [ -f /HiveManager/ha/script/set_db_connection_conf.sh ]
then
	. /HiveManager/ha/script/set_db_connection_conf.sh -h $db_host -p $db_port -d $db_name -U $db_user -w $db_passwd >>$HM_SCRIPT_LOGFILE 2>&1
fi
