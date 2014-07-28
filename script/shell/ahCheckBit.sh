#! /bin/bash
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "upgrade: bit check start..." >>$HM_SCRIPT_LOGFILE 2>&1

if [ -f $UPDATE_HOME/hm/WEB-INF/hmconf/hivemanager.ver ] 
then
  MODEL=`cat $UPDATE_HOME/hm/WEB-INF/hmconf/hivemanager.ver | sed -n 's/.*MACHINETYPE=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
    
  if [ -z $MODEL ]
  then
      echo "[ERROR] the image's bit info is null! check failed." >>$HM_SCRIPT_LOGFILE 2>&1
      echo "upgrade: bit check end." >>$HM_SCRIPT_LOGFILE 2>&1
      exit 1
  fi
    
  MOCHINE_MODE=`getconf LONG_BIT`
    
  if [ $MODEL != $MOCHINE_MODE ]
  then
      echo "[ERROR] the image's bit and server's bit  is not match! check failed." >>$HM_SCRIPT_LOGFILE 2>&1
      echo "upgrade: bit check end." >>$HM_SCRIPT_LOGFILE 2>&1
      exit 1
  fi
  
  echo "upgrade: bit check end." >>$HM_SCRIPT_LOGFILE 2>&1
  exit 0
fi