# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 1 ]
then
 WEBINFO_HOME=$HM_ROOT/WEB-INF
 
 rm -rf backupdir
 mkdir backupdir

 rm -rf $WEBINFO_HOME/downloads/backup*
 
 cp -rf $WEBINFO_HOME/hmconf/hivemanager.ver dbxmlfile/
 
 if [ ! -f dbxmlfile/new_struct_flag ]
 then
   touch dbxmlfile/new_struct_flag
 fi 
 
 HOST_NAME=$(hostname)

 FILENAME="backup_${HOST_NAME}_`date +%m%d%y%H%M%S`.tar.gz"
 
 if [ "$1" == "home" ]
 then
   TARFILE="/etc/hosts /etc/resolv.conf /etc/sysconfig/network /etc/sysconfig/network-scripts/ifcfg-eth[0,1] dbxmlfile/* /HiveManager/downloads/* webapps/"$APP_HOME"/domains/*"
 else
   TARFILE="/etc/hosts /etc/resolv.conf /etc/sysconfig/network /etc/sysconfig/network-scripts/ifcfg-eth[0,1] dbxmlfile/* /HiveManager/downloads/"$1"/* webapps/"$APP_HOME"/domains/"$1"/*"
 fi 

 tar zcf $NMS_HOME/backupdir/$FILENAME $TARFILE >>$HM_SCRIPT_LOGFILE 2>&1

 /bin/cp -rf $NMS_HOME/backupdir/$FILENAME  $WEBINFO_HOME/downloads/$FILENAME >>$HM_SCRIPT_LOGFILE 2>&1
 
 exit 0
else
  echo "error:exec cmd is not accurate" > /dev/stderr
  exit 1
fi