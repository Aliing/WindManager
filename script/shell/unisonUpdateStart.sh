#!/bin/bash
#it is use for start a unison socket server
#only for sync updating files
#$1:port $2:IP

unison_pid=`ps aux | grep /usr/bin/unison| grep "$1"| awk '{print $2}'`

if [ ! -z "$unison_pid" ]
then  
  kill -9 "$unison_pid"
fi

export UNISON=/tmp

rm -rf /tmp/ar*

/usr/bin/unison -socket "$1" -host "$2" &

sleep 10
if [ $? != 0 ]
then
  exit 1
else
  exit 0
fi