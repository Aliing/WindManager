#!/bin/bash
#it is use for stop a unison socket server
#only for sync updating files
#$1:port

unison_pid=`ps aux | grep "/usr/bin/unison" | grep "$1"| awk '{print $2}'`

if [ -z "$unison_pid" ]
then  
  exit 0
fi

kill -9 $unison_pid
