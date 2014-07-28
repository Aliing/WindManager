#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -z "$1" -o -z "$2" -o -z "$3" -o -z "$4" ]
then
  echo "1" >/dev/stdout
  exit 1
fi

KERNEL_DUMP_DOWN_ROOT=/HiveAP/kernel_dump/downloads
TECH_DOWN_ROOT=/HiveAP/tech_dump/downloads
REBOOT_HISTORY_HOME=/HiveAP/reboot_dump
HIVEOS_SUPPORT=/HiveAP/support

KERNEL_FILE_NAME="$1"
TECH_FILE_NAME="$2"
REBOOT_HISTORY_FILE_NAME="$3"

##tar REBOOT_HISTORY_FILE
cd $REBOOT_HISTORY_HOME

if [ -f *.csv ]
then
tar czvf - *.csv | openssl enc -aes-256-cbc -e -pass pass:evih0rea >$REBOOT_HISTORY_FILE_NAME
rm -rf *.csv >>$HM_SCRIPT_LOGFILE 2>&1
#tar czf   $REBOOT_HISTORY_FILE_NAME *.csv>>$HM_SCRIPT_LOGFILE 2>&1

fi

if [ ! -d $HIVEOS_SUPPORT ]
then
  mkdir -p $HIVEOS_SUPPORT
fi

rm -rf $HIVEOS_SUPPORT/* >>$HM_SCRIPT_LOGFILE 2>&1

if [ -f $KERNEL_DUMP_DOWN_ROOT/$KERNEL_FILE_NAME ]
then
    cp -rf $KERNEL_DUMP_DOWN_ROOT/$KERNEL_FILE_NAME $HIVEOS_SUPPORT
    rm -rf $KERNEL_DUMP_DOWN_ROOT/$KERNEL_FILE_NAME
fi

if [ -f $TECH_DOWN_ROOT/$TECH_FILE_NAME ]
then
    cp -rf $TECH_DOWN_ROOT/$TECH_FILE_NAME $HIVEOS_SUPPORT
    rm -rf $TECH_DOWN_ROOT/$TECH_FILE_NAME
fi

if [ -f $REBOOT_HISTORY_HOME/$REBOOT_HISTORY_FILE_NAME ]
then
    cp -rf $REBOOT_HISTORY_HOME/$REBOOT_HISTORY_FILE_NAME $HIVEOS_SUPPORT
    rm -rf $REBOOT_HISTORY_HOME/$REBOOT_HISTORY_FILE_NAME
fi

cd $HIVEOS_SUPPORT

tar czf "$4" * >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "1" >/dev/stdout
  exit 1
fi

echo "0" >/dev/stdout
exit 0
