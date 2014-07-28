#!/bin/bash
. /HiveManager/script/shell/setHmEnv.sh

Hibernate_cfg=$HM_ROOT/WEB-INF/classes/hibernate.cfg.xml
db_name=`grep jdbc:postgresql:// $Hibernate_cfg |  sed -n 's/.*jdbc:postgresql:\/\/.*:.*\/\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
echo $db_name


