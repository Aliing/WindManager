#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

netstat -nap | grep sshd | grep -v scpuser | sed -n -e 's/.*:::\([0-9][0-9]*\).*/\1/p' >/dev/stdout 2>&1