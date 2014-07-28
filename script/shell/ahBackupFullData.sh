# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 1 ]
then
  cd $NMS_HOME

  . $SHELL_HOME/setEnv.sh

  $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupFullData  ./dbxmlfile $1 >>$HM_SCRIPT_LOGFILE 2>&1
 
  HM_TYPE=`cat $HM_ROOT/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`
  
  if [ $HM_TYPE != "HM" -a $HM_TYPE != "hm" ]
  then
    cd $NMS_HOME  
    rm -rf ./dbxmlfile/license   
    mkdir -p ./dbxmlfile/license   
    $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupLicenseHistory ./dbxmlfile/license >>$HM_SCRIPT_LOGFILE 2>&1
  fi
  
  cd $NMS_HOME
  
  . $SHELL_HOME/ahBackupFile.sh home
  
  exit 0

else
  echo "error:exec cmd is not accurate" > /dev/stderr
  exit 1
fi