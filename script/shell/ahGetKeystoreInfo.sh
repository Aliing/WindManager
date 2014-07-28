#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

. $SHELL_HOME/setEnv.sh

KEYSTORE_FILE=$HM_ROOT/.keystore

if [ ! -f $KEYSTORE_FILE ]
then
  echo "no https certificate file " > /dev/stdout
  exit 0
fi

$JAVA_HOME/bin/keytool -list -v -keystore $HM_ROOT/.keystore -storepass Aerohive > /dev/stdout

