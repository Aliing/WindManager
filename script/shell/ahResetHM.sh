# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

TOMCAT_LOGS=$NMS_HOME/logs

BE_LOGS=$HM_ROOT/WEB-INF/logs

$SHELL_HOME/stopHiveManage.sh

sleep 30

. $SHELL_HOME/setEnv.sh


sleep 5

$SHELL_HOME/dropTable.sh  >>$HM_SCRIPT_LOGFILE 2>&1

sleep 5
 
$SHELL_HOME/createTable.sh  >>$HM_SCRIPT_LOGFILE 2>&1

rm -rf /HiveManager/downloads/*

rm -rf $HM_ROOT/domains/*

#clean logs
rm -rf $TOMCAT_LOGS/*
rm -rf $BE_LOGS/*

if [ -d /HiveManager/capwap/log ]
then
    rm -rf /HiveManager/capwap/log/*
fi

if [ -d /HiveManager/ha/logs ]
then
    rm -rf /HiveManager/ha/logs/*
fi

#reset to default value
  if [ -f /etc/hosts.hivemanager ]
  then
    /bin/cp -f /etc/hosts.hivemanager  /etc/hosts
  fi
  
  if [ -f /etc/resolv.conf.hivemanager ]
  then
    /bin/cp -f /etc/resolv.conf.hivemanager  /etc/resolv.conf
  fi
  
  if [ -f /etc/sysconfig/network.hivemanager ]
  then
    /bin/cp -f /etc/sysconfig/network.hivemanager  /etc/sysconfig/network
  fi 
  
  if [ -f /etc/sysconfig/network-scripts/hivemanager.ifcfg-eth0 ]
  then
    /bin/cp -f /etc/sysconfig/network-scripts/hivemanager.ifcfg-eth0   /etc/sysconfig/network-scripts/ifcfg-eth0 
  fi

  if [ -f /etc/sysconfig/network-scripts/hivemanager.ifcfg-eth1 ]
  then
    /bin/cp -f /etc/sysconfig/network-scripts/hivemanager.ifcfg-eth1   /etc/sysconfig/network-scripts/ifcfg-eth1 
  fi

  hostname hivemanager.aerohive.com >>$HM_SCRIPT_LOGFILE 2>&1

  service network restart >>$HM_SCRIPT_LOGFILE 2>&1
  
  #reset passwd for admin
  echo aerohive | passwd --stdin admin >>$HM_SCRIPT_LOGFILE 2>&1


sleep 30 


$SHELL_HOME/startHiveManage.sh
