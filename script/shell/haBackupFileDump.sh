#! /bin/bash
. /HiveManager/script/shell/setHmEnv.sh

WEBINFO_HOME=$HM_ROOT/WEB-INF
rm -rf backupdir
mkdir backupdir

rm -rf $WEBINFO_HOME/downloads/backup*
cp -rf $WEBINFO_HOME/hmconf/hivemanager.ver dbxmlfile/

if [ ! -f dbxmlfile/new_struct_flag ]
then
	touch dbxmlfile/new_struct_flag
fi

if [ ! -f dbxmlfile/.backupdump ]
then
	touch dbxmlfile/.backupdump
fi

HOST_NAME=$(hostname)
FILENAME="backup_dump_${HOST_NAME}_`date +%m%d%y%H%M%S`.tar.gz"
TARFILE="/etc/hosts /etc/resolv.conf /etc/sysconfig/network /etc/sysconfig/network-scripts/ifcfg-eth[0,1] dbxmlfile/* dbxmlfile/.backupdump /HiveManager/downloads/* webapps/"$APP_HOME"/domains/*"

tar zcf $NMS_HOME/backupdir/$FILENAME $TARFILE >>$HM_SCRIPT_LOGFILE 2>&1

/bin/cp -rf $NMS_HOME/backupdir/$FILENAME  $WEBINFO_HOME/downloads/$FILENAME >>$HM_SCRIPT_LOGFILE 2>&1