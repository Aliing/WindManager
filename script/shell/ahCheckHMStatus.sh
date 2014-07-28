# !/bin/bash
. /HiveManager/script/shell/setHmEnv.sh

$SHELL_HOME/ahCheckCAPWAP.sh 2>>$HM_SCRIPT_LOGFILE
if [ $? != 0 ]; then
  echo -1 > /dev/stdout
else
  echo -e
fi

$SHELL_HOME/ahCheckTomcat.sh 2>>$HM_SCRIPT_LOGFILE
if [ $? != 0 ]; then
  echo -1 > /dev/stdout
else
  echo -e
fi

$SHELL_HOME/ahCheckDB.sh 2>>$HM_SCRIPT_LOGFILE

if [ $? != 0 ]; then
  echo -1 > /dev/stdout
fi