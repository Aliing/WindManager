# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

WEBINFO_HOME=$HM_ROOT/WEB-INF

CAPWAP_LOG_HOME=/HiveManager/capwap/log

CLI_PARSER_LOG=/HiveManager/cli_parser/log

CATALINA_LOG=$NMS_HOME/logs

HM_VERSION=$WEBINFO_HOME/hmconf/hivemanager.ver

HA_LOG=/HiveManager/ha/logs

HA_D_LOG=/HiveManager/ha/ha-d/logs

cd $NMS_HOME

rm -rf logpackdir
mkdir logpackdir

FILENAME="support_logs.tar.gz"

TARFILE="/var/log/* /etc/hosts /etc/resolv.conf /etc/sysconfig/network /etc/sysconfig/network-scripts/ifcfg-eth0 /etc/sysconfig/network-scripts/ifcfg-eth1 $WEBINFO_HOME/logs/* $CATALINA_LOG/* $HM_VERSION"

if [ -d /remote ]
then
  TARFILE="$TARFILE /remote/*"
fi

if [ -d $CAPWAP_LOG_HOME ]
then
  TARFILE="$TARFILE $CAPWAP_LOG_HOME/*"
fi

if [ -d $CLI_PARSER_LOG ]
then
  TARFILE="$TARFILE $CLI_PARSER_LOG/*"
fi

if [ -d $HA_LOG ]
then
  TARFILE="$TARFILE $HA_LOG/*"
fi

if [ -d $HA_D_LOG ]
then
  TARFILE="$TARFILE $HA_D_LOG/*"
fi

if [ -f /HiveManager/update_history.log ]
then
  TARFILE="$TARFILE /HiveManager/update_history.log"
fi

tar zcf $NMS_HOME/logpackdir/$FILENAME $TARFILE

/bin/cp -rf $NMS_HOME/logpackdir/$FILENAME  $WEBINFO_HOME/downloads/$FILENAME

rm -rf logpackdir

exit 0
