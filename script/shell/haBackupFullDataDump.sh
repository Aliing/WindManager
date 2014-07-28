#! /bin/bash
. /HiveManager/script/shell/setHmEnv.sh
. $SHELL_HOME/setEnv.sh

Hibernate_cfg=$HM_ROOT/WEB-INF/classes/hibernate.cfg.xml
db_host=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_port=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:\([^ ][-A-Za-z0-9\/._\+]*\)\/.*/\1/p'`
db_name=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:.*\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
Hm_cfg_pro=$HM_ROOT/WEB-INF/classes/resources/hmConfig.properties
db_user=`grep hm.connection.username $Hm_cfg_pro  | sed -n 's/.*hm.connection.username=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_passwd=`grep hm.connection.password $Hm_cfg_pro | sed -n 's/.*hm.connection.password=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

##if [ -d $NMS_HOME/dbxmlfile ]
##then
##	rm -rf $NMS_HOME/dbxmlfile/*
##fi

cd $NMS_HOME/dbxmlfile
export PGPASSWORD=$db_passwd
pg_dump -h $db_host -p $db_port -U $db_user $db_name > pg_dump.bak

cd $NMS_HOME
. $SHELL_HOME/haBackupFileDump.sh