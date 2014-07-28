# !/bin/bash
# $1:domainid
# $2:domainname
# $3:backupcontetn

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 3 ]
then
  cd  $NMS_HOME

  . $SHELL_HOME/setEnv.sh

  $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupDomainData $1 ./dbxmlfile $3 >>$HM_SCRIPT_LOGFILE 2>&1
  
  cd $NMS_HOME
  
  . $SHELL_HOME/ahBackupFile.sh "$2"
  
  exit 0
else 
  echo "error:exec cmd is not accurate" > /dev/stderr
  exit 1
fi