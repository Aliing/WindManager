#!/bin/bash
#tar the hiaveap tech files
#$1 the tar tech file name 

. /HiveManager/script/shell/setHmEnv.sh

if [ -z "$1" ]
then
  echo "error_tar_tech" >/dev/stderr
  exit 1
fi

TECH_ROOT=/HiveAP/tech_dump
TMP_DIR=tmp
ZIP_LIST_FILE=readme.txt
DOWNLOADS=downloads

cd $TECH_ROOT

if [ ! -f $TECH_ROOT/$ZIP_LIST_FILE ]
then
  echo "error_tar_tech" >/dev/stderr
  exit 1
fi

rm -rf $TECH_ROOT/$TMP_DIR

mkdir $TECH_ROOT/$TMP_DIR

cp -rf *.* $TECH_ROOT/$TMP_DIR

rm -rf *.*

cd $TECH_ROOT/$TMP_DIR

tar czf "$1" * >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "error_tar_tech" >/dev/stderr
  exit 1
fi

#rm -rf *.txt

rm -rf $TECH_ROOT/$DOWNLOADS

mkdir $TECH_ROOT/$DOWNLOADS

cp -rf $TECH_ROOT/$TMP_DIR/"$1" $TECH_ROOT/$DOWNLOADS

if [ $? != 0 ]
then
  echo "error_tar_tech" >/dev/stderr
  exit 1
fi

rm -rf $TECH_ROOT/$TMP_DIR