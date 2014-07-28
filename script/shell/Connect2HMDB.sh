#!/bin/bash
#
# cchen@aerohive.com
#
. /HiveManager/script/shell/setHmEnv.sh
. $(dirname "$0")/shellfuncs

JAVA_SHELL=$HM_BASEHOME/shell
CAPWAP_HOME=/HiveManager/capwap

function change_db_settings {
	HOST=${1-localhost}
	PORT=${2-5432}
	DB=${3-hm}
	USERNAME=${4-hivemanager}
	PASSWORD=${5-aerohive}

	echo "Connect2HMDB:change db settings ($HOST $PORT $DB $USERNAME $PASSWORD)" >>$HM_SCRIPT_LOGFILE

	sed -i 's/jdbc:postgresql:\/\/.*/jdbc:postgresql:\/\/'$HOST':'$PORT'\/'$DB'/' $HM_ROOT/WEB-INF/classes/hibernate.cfg.xml
	sed -i 's/hm.connection.username=.*/hm.connection.username='$USERNAME'/' $HM_ROOT/WEB-INF/classes/resources/hmConfig.properties
	sed -i 's/hm.connection.password=.*/hm.connection.password='$PASSWORD'/' $HM_ROOT/WEB-INF/classes/resources/hmConfig.properties

	sed -i 's/DB_HOST=.*/DB_HOST='$HOST'/' $CAPWAP_HOME/capwap.conf
	sed -i 's/DB_PORT=.*/DB_PORT='$PORT'/' $CAPWAP_HOME/capwap.conf
	sed -i 's/DB_NAME=.*/DB_NAME='$DB'/' $CAPWAP_HOME/capwap.conf
	sed -i 's/DB_USERNAME=.*/DB_USERNAME='$USERNAME'/' $CAPWAP_HOME/capwap.conf
	sed -i 's/DB_PASSWORD=.*/DB_PASSWORD='$PASSWORD'/' $CAPWAP_HOME/capwap.conf
}

identity_key()
{
  SSH
  KEY=$SSH/identity
  if [ ! -d $SSH ] ;then
    mkdir -p $SSH
  fi

  if [ ! -f $KEY ] ;then
    ssh-keygen -t rsa -f $KEY -P ''
  fi
}

function create_ssh_key {
	identity_key
	sed -i "/$host/d" $SSH/known_hosts
	$SHELL_HOME/SSHpassword.expect $user@$host >>$HM_SCRIPT_LOGFILE 2>&1
	if [ 0 != $? ] ;then
		$SHELL_HOME/SSH $password ssh-copy-id -i ${KEY}.pub $user@$host >>$HM_SCRIPT_LOGFILE 2>&1

		$SHELL_HOME/SSHpassword.expect $user@$host >>$HM_SCRIPT_LOGFILE 2>&1
		if [ 0 != $? ] ;then
			echo "Connect2HMDB:create_ssh_key failed" >>$HM_SCRIPT_LOGFILE
			exit 102
		fi
	fi
}

RESTORE_LOCAL=${1-extern}
EXT_DB_SETTINGS=$JAVA_SHELL/extdbsettings.properties

service postgresql status || service postgresql start >>$HM_SCRIPT_LOGFILE 2>&1
if [ 0 != $? ] ;then
	echo "Connect2HMDB:local db server status invalid." >>$HM_SCRIPT_LOGFILE
	exit 109
fi

PG_DATA=/var/lib/pgsql/9.1/data
if [ -e $PG_DATA/recovery.conf ]; then
    /bin/rm -f $PG_DATA/recovery.conf
fi

if [ "$RESTORE_LOCAL" == "local" ]; then
	hostJDBC

    copyDB $HOST "localhost" >>$HM_SCRIPT_LOGFILE 2>&1
	if [ 0 != $? ];then
		echo "Connect2HMDB:restore data to local db server failed." >>$HM_SCRIPT_LOGFILE
		exit 107
	fi

	psql -d hm -U hivemanager -tA -c "update ha_settings set enableExternalDb = 0, primarydburl = '', primarydbpwd = ''" >>$HM_SCRIPT_LOGFILE 2>&1
	if test $? -ne 0 ; then
	    echo "update ha_settings.enableExternalDb to 0 has failed" >>$HM_SCRIPT_LOGFILE
	    exit 110
	fi

	change_db_settings
	rm -f $EXT_DB_SETTINGS

	#restartHM
	exit 0
fi

if [ ! -f "$EXT_DB_SETTINGS" ];then
	echo "Connect2HMDB:file $EXT_DB_SETTINGS not exist." >>$HM_SCRIPT_LOGFILE
	exit 101
fi

TEMPFILE=$(mktemp)
cat $EXT_DB_SETTINGS |
sed -re "s/=(.*)/='\1'/g" > $TEMPFILE
source $TEMPFILE
rm -f $TEMPFILE

create_ssh_key

ssh $user@$host "test -f $CATALINA_HOME/.dbOnly" >>$HM_SCRIPT_LOGFILE 2>&1
if [ 0 != $? ];then
	exit 100
fi

IP_ADDRESS=`ifconfig eth0 |grep "inet addr"| cut -f 2 -d ":"|cut -f 1 -d " " 2>> $HM_SCRIPT_LOGFILE`
if [ -z "$IP_ADDRESS" ]; then
	echo "Connect2HMDB:ip address not exist." >>$HM_SCRIPT_LOGFILE
	exit 103
fi

PG_HBA_CONF=/var/lib/pgsql/9.1/data/pg_hba.conf
ssh $user@$host "test -f $PG_HBA_CONF"
if [ 0 != $? ];then
	PG_HBA_CONF=/var/lib/pgsql/data/pg_hba.conf
	ssh $user@$host "test -f $PG_HBA_CONF"
	if [ 0 != $? ];then
		exit 104
	fi
fi

DB_HOST_CONF="host hm hivemanager $IP_ADDRESS/32 password"

COUNT=`ssh $user@$host "grep '$DB_HOST_CONF' $PG_HBA_CONF | wc -l" 2>> $HM_SCRIPT_LOGFILE`

if [ 0 == $COUNT ]; then
	ssh $user@$host "echo '#added by remote host,please DO NOT modify' >> $PG_HBA_CONF" >>$HM_SCRIPT_LOGFILE 2>&1
	ssh $user@$host "echo $DB_HOST_CONF >> $PG_HBA_CONF" >>$HM_SCRIPT_LOGFILE 2>&1
fi

ssh $user@$host "service postgresql restart" >>$HM_SCRIPT_LOGFILE 2>&1
if [ 0 != $? ];then
	echo "Connect2HMDB:postgresql service restart failed." >>$HM_SCRIPT_LOGFILE
	exit 105
fi

psql -h $host -U hivemanager -d hm -p $port -c "select current_date" >>$HM_SCRIPT_LOGFILE 2>&1
if [ 0 != $? ];then
	echo "Connect2HMDB:could not connect to database server." >>$HM_SCRIPT_LOGFILE
	exit 106
fi

psql -d hm -U hivemanager -tA -c "update ha_settings set enableExternalDb = 1, primarydburl = '$host', primarydbpwd = '$password'" >>$HM_SCRIPT_LOGFILE 2>&1
if test $? -ne 0 ; then
    echo "update ha_settings.enableExternalDb to 1 has failed" >>$HM_SCRIPT_LOGFILE
    exit 110
fi

copyDB "localhost" $host >>$HM_SCRIPT_LOGFILE 2>&1
if [ 0 != $? ];then
	echo "Connect2HMDB:move data to remote db server failed." >>$HM_SCRIPT_LOGFILE
	exit 108
fi

change_db_settings $host $port

#restartHM
