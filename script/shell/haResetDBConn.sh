#!bin/bash
. /HiveManager/script/shell/setHmEnv.sh
. /HiveManager/script/shell/setEnv.sh

chmod u+x $UPDATE_SHELL_HOME/*.sh

cd $HM_ROOT

##get db info from app
Hibernate_cfg=$HM_ROOT/WEB-INF/classes/hibernate.cfg.xml
db_host=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_port=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:\([^ ][-A-Za-z0-9\/._\+]*\)\/.*/\1/p'`
db_name=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:.*\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
Hm_cfg_pro=$HM_ROOT/WEB-INF/classes/resources/hmConfig.properties
db_user=`grep hm.connection.username $Hm_cfg_pro  | sed -n 's/.*hm.connection.username=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
db_passwd=`grep hm.connection.password $Hm_cfg_pro | sed -n 's/.*hm.connection.password=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

if [ -z $db_name ]
then
	echo "update_in_progress" > /dev/stderr
	exit 1
fi

checkstr=`echo $db_name | grep "_ha_ol_hm_db_passive_c"`
if [ -z $checkstr ]
then
    basename=$db_name
else
    basename=`echo $db_name | sed "s/\(.*\)_ha_ol_hm_db_passive_c/\1/g"`
fi

##reset db name
if [ $db_name = $basename ]
then
	db_url="jdbc:postgresql://$db_host:$db_port/$db_name"
	db_name=$basename"_ha_ol_hm_db_passive_c"
else
    db_url="jdbc:postgresql://$db_host:$db_port/$db_name"
    db_name=$basename
fi

##reset local db schema
$UPDATE_HOME/HiveManager/script/shell/setDBinfo2web.sh $db_host $db_port $db_name $db_user $db_passwd  $HM_ROOT/WEB-INF/classes/hibernate.cfg.xml  $HM_ROOT/WEB-INF/classes/resources/hmConfig.properties

##reset capwap db
$UPDATE_SHELL_HOME/setDBinfo2capwap.sh $db_host $db_port $db_name $db_user $db_passwd  /HiveManager/capwap/capwap.conf >>$HM_SCRIPT_LOGFILE 2>&1

if [ -f /HiveManager/ha/script/set_db_connection_conf.sh ]
then
	. /HiveManager/ha/script/set_db_connection_conf.sh -h $db_host -p $db_port -d $db_name -U $db_user -w $db_passwd >>$HM_SCRIPT_LOGFILE 2>&1
fi