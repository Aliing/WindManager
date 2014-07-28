# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -f $NMS_HOME/.swupdated ]
then

. $SHELL_HOME/setEnv.sh

cd $NMS_HOME

if [ ! -f $NMS_HOME/dbxmlfile/license/license_history_info.xml ]
then
    mkdir -p $NMS_HOME/dbxmlfile/license

    $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupLicenseHistory $NMS_HOME/dbxmlfile/license
fi

sleep 5

##for hm-ha-2-nodes, restore local db instand of remote db
Hibernate_cfg=$HM_ROOT/WEB-INF/classes/hibernate.cfg.xml
db_host=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_port=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:\([^ ][-A-Za-z0-9\/._\+]*\)\/.*/\1/p'`
db_name=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:.*\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
Hm_cfg_pro=$HM_ROOT/WEB-INF/classes/resources/hmConfig.properties
db_user=`grep hm.connection.username $Hm_cfg_pro  | sed -n 's/.*hm.connection.username=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_passwd=`grep hm.connection.password $Hm_cfg_pro | sed -n 's/.*hm.connection.password=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

if [ -f /HiveManager/tomcat/ha2nodes ]
then
	/HiveManager/script/shell/setDBinfo2web.sh $db_host 5432 $db_name $db_user $db_passwd  $HM_ROOT/WEB-INF/classes/hibernate.cfg.xml  $HM_ROOT/WEB-INF/classes/resources/hmConfig.properties >>$HM_SCRIPT_LOGFILE 2>&1
fi

if [ -f $NMS_HOME/.fullrestore ]
then
  rm -rf $NMS_HOME/.fullrestore
  if [ -f $SHELL_HOME/dropTable.sh  ]
  then
   $SHELL_HOME/dropTable.sh
  fi
else
  if [ -d /hivemap ]
  then
     pkill -f "postgres: hivemanager hm_temp"
    su postgres -c "psql -c \"DROP DATABASE hm_temp;\""  >>$HM_SCRIPT_LOGFILE 2>&1
    su postgres -c "psql -c \"CREATE DATABASE hm_temp WITH OWNER = hivemanager TABLESPACE = pg_default;\"" >>$HM_SCRIPT_LOGFILE 2>&1
    while [ $? != 0 ]
    do
      pkill -f "postgres: hivemanager hm_temp"
      su postgres -c "psql -c \"DROP DATABASE hm_temp;\""  >>$HM_SCRIPT_LOGFILE 2>&1
      su postgres -c "psql -c \"CREATE DATABASE hm_temp WITH OWNER = hivemanager TABLESPACE = pg_default;\"" >>$HM_SCRIPT_LOGFILE 2>&1
    done
    su postgres -c "createlang plpgsql hm_temp"
    $SHELL_HOME/createTable.sh hm_temp  >>$HM_SCRIPT_LOGFILE 2>&1

    pkill -f "postgres: hivemanager hm"
    su postgres -c "psql -c \"DROP DATABASE hm;\""  >>$HM_SCRIPT_LOGFILE 2>&1
    su postgres -c "psql -c \"CREATE DATABASE hm WITH OWNER = hivemanager TABLESPACE = pg_default;\"" >>$HM_SCRIPT_LOGFILE 2>&1
    while [ $? != 0 ]
    do
      pkill -f "postgres: hivemanager hm"
      su postgres -c "psql -c \"DROP DATABASE hm;\""  >>$HM_SCRIPT_LOGFILE 2>&1
      su postgres -c "psql -c \"CREATE DATABASE hm WITH OWNER = hivemanager TABLESPACE = pg_default;\"" >>$HM_SCRIPT_LOGFILE 2>&1
    done
    su postgres -c "createlang plpgsql hm" >>$HM_SCRIPT_LOGFILE 2>&1
  fi
fi
sleep 5

#$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $HIBERNATEPATH com.ah.util.HibernateUtil create >>$HM_SCRIPT_LOGFILE 2>&1
$SHELL_HOME/createTable.sh >>$HM_SCRIPT_LOGFILE 2>&1

sleep 5

cd $NMS_HOME

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhRestoreLicenseHistory $NMS_HOME/dbxmlfile/license

$JAVA_HOME/bin/java -Xms256m -Xmx1024m -cp $RESTOREPATH com.ah.be.admin.restoredb.AhRestoreFullData >>$HM_SCRIPT_LOGFILE 2>&1

rm -rf $NMS_HOME/.swupdated

DOWNLOAD_HOME=/HiveManager/downloads

cd $DOWNLOAD_HOME

for file in ./*
do
  if [ -d $file ]
  then
      touch $file/.need_show_logs
  fi
done

. /HiveManager/script/shell/checkAfterRestore.sh

if [ -f /HiveManager/tomcat/ha2nodes ]
then
	/HiveManager/script/shell/setDBinfo2web.sh $db_host $db_port $db_name $db_user $db_passwd  $HM_ROOT/WEB-INF/classes/hibernate.cfg.xml  $HM_ROOT/WEB-INF/classes/resources/hmConfig.properties >>$HM_SCRIPT_LOGFILE 2>&1
	rm -rf /HiveManager/tomcat/ha2nodes
fi
	
cd $NMS_HOME

fi
