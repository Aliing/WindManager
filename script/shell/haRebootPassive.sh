#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

CAPWAP_HOME=/HiveManager/capwap
HOST=localhost
PORT=5432
DB=hm
USERNAME=hivemanager
PASSWORD=aerohive
POWEROFF=f

if [ $# -gt 0 -a $# -lt 5 ]; then
	echo Usage: $0 [HOST] [PORT] [DB] [USERNAME] [PASSWORD] [POWEROFF]
	exit 1
else
	if [ $# -gt 0 ]; then
		HOST=$1
		PORT=$2
		DB=$3
		USERNAME=$4
		PASSWORD=$5
	fi
	if [ $# -gt 5 ]; then
		POWEROFF=$6
	fi
fi
echo "running using command: '$0 $HOST $PORT $DB $USERNAME $PASSWORD $POWEROFF'"

sed -i 's/jdbc:postgresql:\/\/.*/jdbc:postgresql:\/\/'$HOST':'$PORT'\/'$DB'/' $HM_ROOT/WEB-INF/classes/hibernate.cfg.xml
sed -i 's/hm.connection.username=.*/hm.connection.username='$USERNAME'/' $HM_ROOT/WEB-INF/classes/resources/hmConfig.properties
sed -i 's/hm.connection.password=.*/hm.connection.password='$PASSWORD'/' $HM_ROOT/WEB-INF/classes/resources/hmConfig.properties

sed -i 's/DB_HOST=.*/DB_HOST='$HOST'/' $CAPWAP_HOME/capwap.conf
sed -i 's/DB_PORT=.*/DB_PORT='$PORT'/' $CAPWAP_HOME/capwap.conf
sed -i 's/DB_NAME=.*/DB_NAME='$DB'/' $CAPWAP_HOME/capwap.conf
sed -i 's/DB_USERNAME=.*/DB_USERNAME='$USERNAME'/' $CAPWAP_HOME/capwap.conf
sed -i 's/DB_PASSWORD=.*/DB_PASSWORD='$PASSWORD'/' $CAPWAP_HOME/capwap.conf

sleep 10s

if [ $POWEROFF == "t" ]; then
echo "stopping hivemanager ..."
$SHELL_HOME/stopHiveManage.sh
else
echo "restarting hivemanager ..."
$SHELL_HOME/ahRestartSoft.sh
fi