#!/bin/bash
SYSTEM_ID=/HiveManager/license/system_id

if [ -f $SYSTEM_ID ]
then
  rm -rf $SYSTEM_ID >/dev/null
  
  sleep 3
  
  reboot  >/dev/null  
fi
