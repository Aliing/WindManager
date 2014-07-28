#!/bin/bash
#tar the hiveap kernel dump files
#$1:the name for tar file

. /HiveManager/script/shell/setHmEnv.sh

if [ -z "$1" ]
then
  echo "error_tar_kernel_dump" >/dev/stderr
  exit 1
fi

KERNEL_DUMP_ROOT=/HiveAP/kernel_dump
TMP_DIR=tmp
ZIP_LIST_FILE=zip_file_list.txt
DOWNLOADS=downloads

if [ ! -f $KERNEL_DUMP_ROOT/$TMP_DIR/$ZIP_LIST_FILE ]
then
  echo "error_tar_kernel_dump" >/dev/stderr
  exit 1
fi

dirs=`cat $KERNEL_DUMP_ROOT/$TMP_DIR/$ZIP_LIST_FILE`

for dir in $dirs
do
  if [ -d $KERNEL_DUMP_ROOT/$dir ]
  then
    cp -rf $KERNEL_DUMP_ROOT/$dir $KERNEL_DUMP_ROOT/$TMP_DIR
  fi
done

cd $KERNEL_DUMP_ROOT/$TMP_DIR

tar czf "$1" * >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "error_tar_kernel_dump" >/dev/stderr
  exit 1
fi

rm -rf $KERNEL_DUMP_ROOT/$DOWNLOADS

mkdir -p $KERNEL_DUMP_ROOT/$DOWNLOADS

cp -rf $KERNEL_DUMP_ROOT/$TMP_DIR/"$1"  $KERNEL_DUMP_ROOT/$DOWNLOADS

if [ $? != 0 ]
then
  exit 1
fi

rm -rf $KERNEL_DUMP_ROOT/$TMP_DIR
