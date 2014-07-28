
. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 1 ]
then
  
  

  HM_BIT=`getconf LONG_BIT`
    
  if [ $HM_BIT == 32 ]
  then
    $SHELL_HOME/verifycpskey "$1" >>$HM_SCRIPT_LOGFILE    
    echo  $? > /dev/stdout      
    exit 0
  else
    $SHELL_HOME/verifycpskey64 "$1" >>$HM_SCRIPT_LOGFILE    
    echo  $? > /dev/stdout      
    exit 0
  fi

fi