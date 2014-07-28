#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# != 3 ]
then
   echo "1" >/dev/stdout
   exit 1
fi

CA_HOME=/HiveManager/downloads/"${3}"/aerohiveca

cd $CA_HOME

openssl x509 -in "$1" -inform DER -out "$2" -outform PEM >>$HM_SCRIPT_LOGFILE 2>&1

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
