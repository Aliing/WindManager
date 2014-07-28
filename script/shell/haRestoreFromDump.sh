#! /bin/bash
. /HiveManager/script/shell/setEnv.sh
. /HiveManager/script/shell/setHmEnv.sh

if [ -f $NMS_HOME/.swupdated ]
then
	. $SHELL_HOME/setEnv.sh
	cd $NMS_HOME

	if [ -f $NMS_HOME/.fullrestore ]
	then
		rm -rf $NMS_HOME/.fullrestore

        $SHELL_HOME/dropTable.sh >>$HM_SCRIPT_LOGFILE 2>&1

        Hibernate_cfg=$HM_ROOT/WEB-INF/classes/hibernate.cfg.xml
        db_host=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
        db_port=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:\([^ ][-A-Za-z0-9\/._\+]*\)\/.*/\1/p'`
        db_name=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:.*\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
        Hm_cfg_pro=$HM_ROOT/WEB-INF/classes/resources/hmConfig.properties
        db_user=`grep hm.connection.username $Hm_cfg_pro  | sed -n 's/.*hm.connection.username=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
        db_passwd=`grep hm.connection.password $Hm_cfg_pro | sed -n 's/.*hm.connection.password=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

        cd $NMS_HOME/dbxmlfile
        export PGPASSWORD=$db_passwd
        psql -h $db_host -p $db_port -U $db_user $db_name -f pg_dump.bak >>$HM_SCRIPT_LOGFILE 2>&1

#remove db_system_id start

		 LOCAL_APPTYPE=`cat $HM_ROOT/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`  
		 if [ $LOCAL_APPTYPE == "HM" -o $LOCAL_APPTYPE == "hm" ]
		 then 
		   $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.restoredb.AhCheckSystemId >>$HM_SCRIPT_LOGFILE 2>&1
		 fi
		 
#remove db_system_id end 

        rm -rf .backupdump
    fi

    rm -rf $NMS_HOME/.swupdated

fi