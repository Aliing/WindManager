#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

UPLOAD_ROOT=/HiveManager/statistic/upload
TMP_ROOT=${UPLOAD_ROOT}/tmp

if [ ! -d $TMP_ROOT ]
then
  echo "1" >/dev/stdout
  exit 1 
fi

cd $TMP_ROOT >>$HM_SCRIPT_LOGFILE 2>&1

tar czf "$1" * >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "1" >/dev/stdout
  exit 1
fi

mv -f ${TMP_ROOT}/"$1" ${UPLOAD_ROOT}  >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "1" >/dev/stdout
  exit 1
fi

rm -rf ${TMP_ROOT}/* >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "1" >/dev/stdout
  exit 1
fi

echo "0" >/dev/stdout
exit 0
