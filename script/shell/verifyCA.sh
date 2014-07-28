#!/bin/bash
#verify ca and cert
#$1: CA $2:cert $3:domain

. /HiveManager/script/shell/setHmEnv.sh

CA_HOME=/HiveManager/downloads/"${3}"/aerohiveca
cd $CA_HOME
#check if CA
openssl x509 -text -noout -in "$1" 2>&1 >/dev/null
if [ $? != 0 ]
then
 echo 1 >/dev/stdout
 exit 0
fi

#check if CA right
openssl verify -CAfile "$1" "$2" | grep error 2>&1 >>$HM_SCRIPT_LOGFILE        
if [ $? == 0 ]
then
  echo 1 > /dev/stdout
  exit 0
fi

echo 0 > /dev/stdout

