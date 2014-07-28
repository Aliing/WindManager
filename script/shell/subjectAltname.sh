#!/bin/bash
. /HiveManager/script/shell/setHmEnv.sh

#$1:file name $2:domain name
if [ $# == 2 ]
then

  CERT_FILE=/HiveManager/downloads/"${2}"/aerohiveca/"${1}"

  HM_BIT=`getconf LONG_BIT`
    
  if [ $HM_BIT == 32 ]
  then
    $SHELL_HOME/subjectAltname "$CERT_FILE" >/dev/stdout
    echo  0 > /dev/stdout      
    exit 0
  else
    $SHELL_HOME/subjectAltname64 "$CERT_FILE" >/dev/stdout  
    echo  0 > /dev/stdout      
    exit 0
  fi

fi
