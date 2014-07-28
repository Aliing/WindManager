#!/bin/bash
#$1: domainid
#$2: dir for the xml file
. /HiveManager/script/shell/setHmEnv.sh

. $SHELL_HOME/setEnv.sh

cd $NMS_HOME

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $RESTOREPATH com.ah.be.admin.restoredb.AhRestoreHHMDomainData $1 "$2"/ >>$HM_SCRIPT_LOGFILE 2>&1

echo "0" >/dev/stdout
exit 0
