#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh
. /HiveManager/script/shell/setEnv.sh

chmod u+x /HiveManager/script/shell/*.sh

Hm_cfg_pro=$HM_ROOT/WEB-INF/classes/resources/hmConfig.properties
db_user=`grep hm.connection.username $Hm_cfg_pro  | sed -n 's/.*hm.connection.username=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_passwd=`grep hm.connection.password $Hm_cfg_pro | sed -n 's/.*hm.connection.password=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

update_hm=/hivemap/HiveManager/tomcat/hm_soft_upgrade/hm
/hivemap/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setDBinfo2web.sh localhost 5432 hm $db_user $db_passwd  $update_hm/WEB-INF/classes/hibernate.cfg.xml  $update_hm/WEB-INF/classes/resources/hmConfig.properties

cd /hivemap/HiveManager/tomcat/hm_soft_upgrade/hm/WEB-INF/classes
$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $HIBERNATEPATH com.ah.util.HibernateUtil drop  >>$HM_SCRIPT_LOGFILE 2>&1
$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $HIBERNATEPATH com.ah.util.HibernateUtil create  >>$HM_SCRIPT_LOGFILE 2>&1

rm -rf /hivemap/HiveManager/tomcat/hm_soft_upgrade/appserver