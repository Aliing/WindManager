#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

NMS_HOME=`pwd`

UPDATE_HOME=$NMS_HOME/hm_soft_upgrade

UPDATE_SHELL_HOME=$NMS_HOME/hm_soft_upgrade/HiveManager/script/shell

UPDATE_FILE_LIST=update_check_list.txt

if [ ! -f $UPDATE_SHELL_HOME/update_check_list32.txt ]
then
  echo "update_in_progress" > /dev/stderr
  exit 1
fi

if [ ! -f $UPDATE_SHELL_HOME/update_check_list64.txt ]
then
  echo "update_in_progress" > /dev/stderr
  exit 1
fi

HM_BIT=`getconf LONG_BIT`

if [ $HM_BIT == 32 ]
then
  cp -rf $UPDATE_SHELL_HOME/update_check_list32.txt $UPDATE_SHELL_HOME/$UPDATE_FILE_LIST
else
  cp -rf $UPDATE_SHELL_HOME/update_check_list64.txt $UPDATE_SHELL_HOME/$UPDATE_FILE_LIST
fi

files=`cat $UPDATE_SHELL_HOME/$UPDATE_FILE_LIST`

for file in $files
do
    if [ ! -f $UPDATE_HOME/$file ]
    then
      echo "update_in_progress" > /dev/stderr
      exit 1
    fi
done

exit 0