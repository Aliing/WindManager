#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

  TMP_HOME=$NMS_HOME/tmp
  
  if [ -f tmp/dbxmlfile/webversion ]
  then
     
    if [ -f tmp/dbxmlfile/hivemanager.ver ]
    then
        ##restore from 3.1
        . $SHELL_HOME/ahRestoreFrom31.sh
    else
        ##restore from 3.0
        . $SHELL_HOME/ahRestoreFrom30.sh
    fi    
  else
    ###restore from 2.1
    . $SHELL_HOME/ahRestoreFrom21.sh
  fi 