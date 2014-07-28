#!/bin/bash
#$1:word dir
#$2:file name
if [ -z "$1" -o -z "$2" ]
then
  echo "1" >/dev/stdout
  echo "the function need 2 parameters" >/dev/stdout
  exit 1
fi

. /HiveManager/script/shell/setHmEnv.sh

cd "$1"

newname=`echo "$2" | sed -n 's/[.]*\(:\)[.]*/-/p'`
##rename file, use "-" instead of ":"
if [ -z $newname ]
then
	newname=$2
else
    /bin/mv -f $2 $newname
fi

tar zxf "$newname" >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "1" >/dev/stdout
  echo "unzip backup file failed" >/dev/stdout
  exit 1
fi

echo "0" >/dev/stdout
exit 0

