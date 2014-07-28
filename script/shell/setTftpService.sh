#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

CONF_HOME=$HM_ROOT/WEB-INF/hmconf

if [ ! -d /HiveManager/downloads/home/image ]
then
    mkdir /HiveManager/downloads/home/image
fi

chmod 777 /HiveManager/downloads/home/image

if [ ! -d /HiveManager/downloads/home/image/dump ]
then
    mkdir /HiveManager/downloads/home/image/dump
fi

chmod 777 /HiveManager/downloads/home/image/dump

TMP=`ls -A /HiveManager/downloads/home/image/dump | wc -l`

if [ $TMP != 0 ]
then
    chmod 777 /HiveManager/downloads/home/image/dump/*  >>$HM_SCRIPT_LOGFILE 2>&1
fi

if [ $# != 1 ]
then
   echo "1" >/dev/stdout
fi 

if [ $1 == on ]
then
  /bin/cp -rf $CONF_HOME/tftp.hivemanager /etc/xinetd.d/tftp >>$HM_SCRIPT_LOGFILE 2>&1
  
  service xinetd restart >>$HM_SCRIPT_LOGFILE 2>&1
  
  if [ $? != 0 ]
  then
    echo "1" >/dev/stdout
    exit 1
  else
    echo "0" >/dev/stdout
    exit 0
  fi
else
  /bin/cp -rf $CONF_HOME/tftp.hivemanager.off /etc/xinetd.d/tftp >>$HM_SCRIPT_LOGFILE 2>&1
  
  service xinetd restart >>$HM_SCRIPT_LOGFILE 2>&1
  
  if [ $? != 0 ]
  then
    echo "1" >/dev/stdout
    exit 1
  else
    echo "0" >/dev/stdout
    exit 0
  fi
fi