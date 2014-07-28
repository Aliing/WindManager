#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# -ge 3 ]
then 
   if [ $# -eq 3 ]
   then
      
      CA_HOME=/HiveManager/downloads/"${3}"/aerohiveca
 
      cd $CA_HOME
   
      openssl pkcs12 -in "$1" -out "$2" -nodes -passin pass:""  >>$HM_SCRIPT_LOGFILE 2>&1  
   else
   
      CA_HOME=/HiveManager/downloads/"${4}"/aerohiveca
 
      cd $CA_HOME
   
      openssl pkcs12 -in "$1" -passin pass:"$3" -out "$2" -passout pass:"$3" -nodes >>$HM_SCRIPT_LOGFILE 2>&1
   fi
   
   if [ $? != 0 ]
   then
     echo "1" >/dev/stdout
     rm -rf "$1"
     rm -rf "$2"
     exit 1
   fi
   
   echo "0" >/dev/stdout
   rm -rf "$1"
   exit 0

else
  echo "1" >/dev/stdout
  exit 1
fi