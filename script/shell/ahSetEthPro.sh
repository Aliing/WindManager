#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -z $1 -o -z $2 -o -z $3 -o -z $4 ]
then
  echo "1" >/dev/stdout 2>&1
  exit 1
fi

SETTING_FILE=/etc/sysconfig/network-scripts/.hm_setting_$1

rm -rf $SETTING_FILE

if [ "$4" == "on" ]
then
  ethtool -s $1 autoneg "$4" >>$HM_SCRIPT_LOGFILE 2>&1
  
  if [ $? != 0 ]
  then
    echo "1" >/dev/stdout 2>&1
    exit 1
  fi
  
  touch $SETTING_FILE
  
  echo "autoneg:on" > $SETTING_FILE
  
  echo "0" >/dev/stdout 2>&1
  
  exit 0
  
fi

ethtool -s $1 autoneg "$4" >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
  then
    echo "1" >/dev/stdout 2>&1
    exit 1
fi

sleep 5

ethtool -s $1 speed $2 >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "1" >/dev/stdout 2>&1
  exit 1
fi

sleep 5

ethtool -s $1 duplex $3 >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "1" >/dev/stdout 2>&1
  exit 1
fi

touch $SETTING_FILE
echo "autoneg:$4" >> $SETTING_FILE
echo "speed:$2" >> $SETTING_FILE
echo "duplex:$3" >> $SETTING_FILE

echo "0" >/dev/stdout 2>&1