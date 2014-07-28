#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# -ge 3 ]
then 
  
  if [ $# -eq 3 ]
  
    CA_HOME=/HiveManager/downloads/"${3}"/aerohiveca
    
    cd $CA_HOME
    
    openssl rsa  -in "$1" -inform DER -out "$2" -outform PEM -passin pass:"" >>$HM_SCRIPT_LOGFILE 2>&1
    
    if [ $? == 0 ]
    then
      echo "0" >/dev/stdout
      rm -rf "$1"
      exit 0
    fi
    
    openssl dsa  -in "$1" -inform DER -out "$2" -outform PEM -passin pass:"" >>$HM_SCRIPT_LOGFILE 2>&1
    
    if [ $? == 0 ]
    then
      echo "0" >/dev/stdout
      rm -rf "$1"
      exit 0
    fi
    
    echo "1" >/dev/stdout
    rm -rf "$1"
    rm -rf "$2"
    exit 1
      
  else
    CA_HOME=/HiveManager/downloads/"${4}"/aerohiveca
    
    cd $CA_HOME
  
    openssl rsa  -in "$1" -inform DER -passin pass:"$3" -out "$2" -outform PEM -passout pass:"$3" >>$HM_SCRIPT_LOGFILE 2>&1
    
    if [ $? == 0 ]
    then
      echo "0" >/dev/stdout
      rm -rf "$1"
      exit 0
    fi
    
    openssl dsa  -in "$1" -inform DER -passin pass:"$3" -out "$2" -outform PEM -passout pass:"$3" >>$HM_SCRIPT_LOGFILE 2>&1
    
    if [ $? == 0 ]
    then
      echo "0" >/dev/stdout
      rm -rf "$1"
      exit 0
    fi
    
    echo "1" >/dev/stdout
    rm -rf "$1"
    rm -rf "$2"
    exit 1
  fi

else
  echo "1" >/dev/stdout
  exit 1
fi