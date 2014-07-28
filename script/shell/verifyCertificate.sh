#!/bin/bash
###this file is using for verify the CA Certificate and private key
###$1:ca certificate $2: need to be verified certificate $3: private key

. /HiveManager/script/shell/setHmEnv.sh

#CA_HOME=/HiveManager/downloads/home/aerohiveca

if [ $# -ge 4 ]
then    
    
    HM_BIT=`getconf LONG_BIT`
    
    if [ $HM_BIT == 32 ]
    then    
      if [ $# -eq 4 ]
      then
      
        CA_HOME=/HiveManager/downloads/"${4}"/aerohiveca
      
        cd $CA_HOME
        
       # openssl verify -CAfile "$1" "$2" | grep error 2>&1 >>$HM_SCRIPT_LOGFILE        
        #if [ $? == 0 ]
        #then
         # echo 1 > /dev/stdout
         # exit 0
        #fi
      
        $SHELL_HOME/verifykey "$2" "$3" >>$HM_SCRIPT_LOGFILE
      
        echo  $? > /dev/stdout
      
        exit 0
       
      else
        
        CA_HOME=/HiveManager/downloads/"${5}"/aerohiveca
      
        cd $CA_HOME
        
       $SHELL_HOME/verifykey "$2" "$3" $4 >>$HM_SCRIPT_LOGFILE
       
        echo $? > /dev/stdout
       
        exit 0
      fi
    else
      if [ $# -eq 4 ]
      then
      
        CA_HOME=/HiveManager/downloads/"${4}"/aerohiveca
      
        cd $CA_HOME
      
        $SHELL_HOME/verifykey64 "$2" "$3" >>$HM_SCRIPT_LOGFILE
      
        echo  $? > /dev/stdout
      
        exit 0
       
      else
      
        CA_HOME=/HiveManager/downloads/"${5}"/aerohiveca
      
        cd $CA_HOME
      
        $SHELL_HOME/verifykey64 "$2" "$3" $4 >>$HM_SCRIPT_LOGFILE
       
        echo $? > /dev/stdout
       
        exit 0
      fi    
    fi   
else
  echo "cert_verify_error" > /dev/stdout
  exit 1
fi

