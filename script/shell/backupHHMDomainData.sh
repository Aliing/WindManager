#!/bin/bash
# $1:domainid
# $2:domainname
# $3:backupcontent
# $4:work dir
# $5:backup name
if [ -z "$1" -o -z "$2" -o -z "$3" -o -z "$4" -o -z "$5" ]
then
  echo "1" >/dev/stdout
  echo "the function need 5 parameters" >/dev/stdout
  exit 1
fi

. /HiveManager/script/shell/setHmEnv.sh

if [ ! -d "$4" ]
then
  mkdir -p "$4" >>$HM_SCRIPT_LOGFILE 2>&1
fi

#rm -rf "$4"/* >>$HM_SCRIPT_LOGFILE 2>&1

mkdir -p "$4"/dbxmlfile >>$HM_SCRIPT_LOGFILE 2>&1

cd  $NMS_HOME

. $SHELL_HOME/setEnv.sh

if [ -f "$4"/need_backup_domain_licnese ]
then
  mkdir "$4"/dbxmlfile/license >>$HM_SCRIPT_LOGFILE 2>&1
  
  $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupDomainOrderkey "$4"/dbxmlfile/license $2 >>$HM_SCRIPT_LOGFILE 2>&1
fi

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupDomainData $1 "$4"/dbxmlfile $3 >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
    echo "1">/dev/stdout
    echo "backup database have some error" >/dev/stdout
    exit 1
fi

WEBINFO_HOME=$HM_ROOT/WEB-INF

/bin/cp -rf $WEBINFO_HOME/hmconf/hivemanager.ver "$4"/dbxmlfile/ >>$HM_SCRIPT_LOGFILE 2>&1

cd "$4"

TARFILE=" dbxmlfile/* /HiveManager/downloads/"$2"/* $NMS_HOME/webapps/"$APP_HOME"/domains/"$2"/*"

mkdir -p "$4"/backupdir >>$HM_SCRIPT_LOGFILE 2>&1
rm -rf "$4"/backupdir/* >>$HM_SCRIPT_LOGFILE 2>&1

tar zcf "$4"/backupdir/"$5" $TARFILE >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
    echo "1">/dev/stdout
    echo "tar file have some error" >/dev/stdout
    exit 1
fi

mv -f "$4"/backupdir/"$5" "$4" >>$HM_SCRIPT_LOGFILE 2>&1
 
echo "0" >/dev/stdout
exit 0
 