#!/bin/bash
#this use create defaut ca

. /HiveManager/script/shell/setHmEnv.sh

NMS_CA_ROOT=/HiveManager/downloads/home/aerohiveca
ROOT_CA=Default_CA.pem
ROOT_CA_KEY=Default_key.pem
ROOT_CA_KEY_PSD=hmkey.psd

if [ -f $NMS_CA_ROOT/$ROOT_CA ] && [ -f $NMS_CA_ROOT/$ROOT_CA_KEY ] && [ -f $NMS_CA_ROOT/$ROOT_CA_KEY_PSD ]
then
  exit 0
fi

NMS_CONF_ROOT=$NMS_HOME/conf

/bin/cp -rf $SHELL_HOME/defaultcsr.conf   $NMS_CONF_ROOT/hmcsr.conf >>$HM_SCRIPT_LOGFILE 2>&1

PASSWD=`cat $SHELL_HOME/hmkey.psd`

if [ -z $PASSWD ]
then
  PASSWD="aerohive"
  echo $PASSWD > $SHELL_HOME/hmkey.psd
fi

/bin/cp -rf $SHELL_HOME/hmkey.psd   $NMS_CA_ROOT >>$HM_SCRIPT_LOGFILE 2>&1

cd $NMS_HOME

$SHELL_HOME/ahCreateRootCA.sh $PASSWD 1024 7300
