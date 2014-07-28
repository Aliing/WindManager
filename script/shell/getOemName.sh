#!/bin/bash
#this sh is get oem company_name
#
. /HiveManager/script/shell/setHmEnv.sh

OEM_FILE="$HM_ROOT"/resources/oem-resource.txt
OEM_VALUE=Aerohive

if [ ! -f "$OEM_FILE" ]
then
  echo "$OEM_VALUE"
  exit 1
fi

OEM_VALUE=`less "$OEM_FILE" | grep company_name |sed -n 's/.*company_name[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+ ]*\).*/\1/ip'`

echo "$OEM_VALUE"

exit 0


