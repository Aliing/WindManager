#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

PC_MODE=`getconf LONG_BIT`

if [ $PC_MODE != 64 ]
then
  echo "1U" >/dev/stdout 2>&1
  exit 0
fi

PN_NAME=`dmidecode -t 2|grep "Product Name:"|cut -f 2 -d ":"|grep "MAHOBAY"|wc -l`
if [ $PN_NAME -gt 0 ]; then
	echo "AH-HM-1U" >/dev/stdout 2>&1
else
	echo "2U" >/dev/stdout 2>&1
fi
