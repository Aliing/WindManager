#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -z $1 ]
then
    echo "need the param"
    exit 1
fi

SSHD_CONF=/etc/ssh/sshd_config
SSHD_CONF_BAK=/etc/ssh/sshd_config.bak

rm -rf $SSHD_CONF_BAK

cp -rf $SSHD_CONF $SSHD_CONF_BAK >>$HM_SCRIPT_LOGFILE 2>&1

echo "" > $SSHD_CONF

sed -e 's/#*Port [0-9][0-9]*/Port '$1'/g'  $SSHD_CONF_BAK  >> $SSHD_CONF

kill -HUP `cat /var/run/sshd.pid` 

sleep 2

service sshd restart >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then 
  echo "1" >/dev/stdout 2>&1
  exit 1
fi

echo "0" >/dev/stdout 2>&1
exit 0