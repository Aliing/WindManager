#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

/bin/cp -rf /etc/syslog-ng/syslog-ng.conf.default  /etc/syslog-ng/syslog-ng.conf

sleep 2

service syslog-ng restart &