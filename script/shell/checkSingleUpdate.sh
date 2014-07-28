
if [ -f  /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh ]
then
  . /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh
  
  if [ -f $CATALINA_HOME/.swupdated -a -f $CATALINA_HOME/.singlepart -a -f $UPDATE_SHELL_HOME/updateSingleSoftware.sh ]
  then
    $UPDATE_SHELL_HOME/updateSingleSoftware.sh
  fi
fi
