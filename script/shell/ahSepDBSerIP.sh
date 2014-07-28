#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh
. /HiveManager/script/shell/setEnv.sh

Hibernate_cfg=$HM_ROOT/WEB-INF/classes/hibernate.cfg.xml

remote_ip=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

if [ -z $remote_ip ]
then
	echo "update_in_progress" > /dev/stdout
else
    echo "$remote_ip" > /dev/stdout
fi