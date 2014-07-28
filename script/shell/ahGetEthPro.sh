#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -z $1 ]
then
  echo "need the param eth0 or eth1"
  exit 1
fi

#ethtool "$1" | grep Speed | sed -n -e 's/.*Speed: *\([0-9]*\).*/\1/p' >/dev/stdout 2>&1
ethtool "$1" | grep Speed >/dev/stdout 2>&1

#ethtool "$1" | grep Duplex | sed -n -e 's/.*Duplex: *\([a-zA-Z]*\).*/\1/p' >/dev/stdout 2>&1
ethtool "$1" | grep Duplex  >/dev/stdout 2>&1

exit 0