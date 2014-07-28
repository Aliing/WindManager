#this function is clean the logs in HM

. /HiveManager/script/shell/setHmEnv.sh

TOMCAT_LOGS=$NMS_HOME/logs

BE_LOGS=$HM_ROOT/WEB-INF/logs

rm -rf $TOMCAT_LOGS/admin.*
rm -rf $TOMCAT_LOGS/catalina.*
rm -rf $TOMCAT_LOGS/hivemanager.log.*
echo "" > $TOMCAT_LOGS/hivemanager.log
rm -rf $TOMCAT_LOGS/host-manager.*
rm -rf $TOMCAT_LOGS/localhost.*
rm -rf $TOMCAT_LOGS/localhost_access_log.*
rm -rf $TOMCAT_LOGS/manager.*

rm -rf $BE_LOGS/administrator.log.*
echo "" > $BE_LOGS/administrator.log
rm -rf $BE_LOGS/common.log.*
echo "" > $BE_LOGS/common.log
rm -rf $BE_LOGS/config.log.*
echo "" > $BE_LOGS/config.log
rm -rf $BE_LOGS/discovery.log.*
echo "" > $BE_LOGS/discovery.log
rm -rf $BE_LOGS/fault.log.*
echo "" > $BE_LOGS/fault.log
rm -rf $BE_LOGS/license.log.*
echo "" > $BE_LOGS/license.log
rm -rf $BE_LOGS/parameter.log.*
echo "" > $BE_LOGS/parameter.log
rm -rf $BE_LOGS/performance.log.*
echo "" > $BE_LOGS/performance.log
rm -rf $BE_LOGS/restore.log.*
echo "" > $BE_LOGS/restore.log
rm -rf $BE_LOGS/topo.log.*
echo "" > $BE_LOGS/topo.log

if [ -d /HiveManager/capwap/log ]
then
    rm -rf /HiveManager/capwap/log/*
fi

if [ -d /HiveManager/ha/logs ]
then
    rm -rf /HiveManager/ha/logs/*
fi
