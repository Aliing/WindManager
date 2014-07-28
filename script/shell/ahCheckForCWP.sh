#!/bin/bash
#$1:cert $2:key $3:passwd
#$1:domain name
$$2:cert $3:key $4:passwd

. /HiveManager/script/shell/setHmEnv.sh

CA_HOME=/HiveManager/downloads/"$1"/aerohiveca

cd "$CA_HOME"

    HM_BIT=`getconf LONG_BIT`
    
    if [ $HM_BIT == 32 ]
    then    
      if [ $# -eq 3 ]
      then
        $SHELL_HOME/verifykey "$2" "$3" >>$HM_SCRIPT_LOGFILE
      
        echo  $? > /dev/stdout
      
        exit 0
       
      else
        $SHELL_HOME/verifykey "$2" "$3" "$4" >>$HM_SCRIPT_LOGFILE
       
        echo $? > /dev/stdout
       
        exit 0
      fi
    else
      if [ $# -eq 3 ]
      then
        $SHELL_HOME/verifykey64 "$2" "$3" >>$HM_SCRIPT_LOGFILE
      
        echo  $? > /dev/stdout
      
        exit 0
       
      else
        $SHELL_HOME/verifykey64 "$2" "$3" "$4" >>$HM_SCRIPT_LOGFILE
       
        echo $? > /dev/stdout
       
        exit 0
      fi    
    fi   
