#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

ETH0_SETTING=/etc/sysconfig/network-scripts/.hm_setting_eth0
ETH1_SETTING=/etc/sysconfig/network-scripts/.hm_setting_eth1

if [ -f $ETH0_SETTING ]
then
  AUTO_VALUE=`cat $ETH0_SETTING | grep autoneg | sed -n -e 's/autoneg:\([a-z]*\)/\1/p'`
  
  if [ ! -z $AUTO_VALUE ]
  then  
   
    ethtool -s eth0 autoneg $AUTO_VALUE >>$HM_SCRIPT_LOGFILE 2>&1
  
    if [ $AUTO_VALUE != "on" ]
    then  
      SPEED_VALUE=`cat $ETH0_SETTING | grep speed | sed -n -e 's/speed:\([0-9]*\)/\1/p'`
  
      if [ ! -z $SPEED_VALUE ]
      then
        sleep 5
        ethtool -s eth0 speed $SPEED_VALUE >>$HM_SCRIPT_LOGFILE 2>&1
      fi
    
      DUPLEX_VALUE=`cat eth0 | grep duplex | sed -n -e 's/duplex:\([a-z]*\)/\1/p'`
    
      if [ ! -z $DUPLEX_VALUE ]
      then
        sleep 5
        ethtool -s eth0 duplex $DUPLEX_VALUE >>$HM_SCRIPT_LOGFILE 2>&1
      fi  
    fi
  
  fi
  
fi

if [ -f $ETH1_SETTING ]
then
  AUTO_VALUE_1=`cat $ETH1_SETTING | grep autoneg | sed -n -e 's/autoneg:\([a-z]*\)/\1/p'`
  
  if [ ! -z $AUTO_VALUE_1 ]
  then   
    ethtool -s eth1 autoneg $AUTO_VALUE_1 >>$HM_SCRIPT_LOGFILE 2>&1
  
    if [ $AUTO_VALUE_1 != "on" ]
    then
  
      SPEED_VALUE_1=`cat $ETH1_SETTING | grep speed | sed -n -e 's/speed:\([0-9]*\)/\1/p'`
  
      if [ ! -z $SPEED_VALUE_1 ]
      then
        sleep 5
        ethtool -s eth1 speed $SPEED_VALUE_1 >>$HM_SCRIPT_LOGFILE 2>&1
      fi
    
      DUPLEX_VALUE_1=`cat $ETH1_SETTING  | grep duplex | sed -n -e 's/duplex:\([a-z]*\)/\1/p'`
    
      if [ ! -z $DUPLEX_VALUE_1 ]
      then
        sleep 5
        ethtool -s eth1 duplex $DUPLEX_VALUE_1 >>$HM_SCRIPT_LOGFILE 2>&1
      fi  
    fi  
  fi
fi