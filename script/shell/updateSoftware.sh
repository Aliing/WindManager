
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "updateSoftware start..." >>$HM_SCRIPT_LOGFILE

rm -rf $UPDATE_HOME/"$1"
 
##public check for old version before 6.1r2 
$UPDATE_SHELL_HOME/publicCheckForOldVersion.sh
if [ $? = 1 ]
then
	echo "update_in_progress" > /dev/stderr
    exit 1
fi
  
##check memory
$UPDATE_SHELL_HOME/ahCheckMemory.sh
if [ $? = 1 ]
then
	echo "update_in_progress" > /dev/stderr
    exit 1
fi

chmod u+x $UPDATE_SHELL_HOME/*.sh
  
if [ $? != 0 ]
then
    echo "update_in_progress" > /dev/stderr
	exit 1
fi

if [ $# -ge 1 ]
then    
  
  if [ -d /hivemap ]
  then
  
	  $UPDATE_SHELL_HOME/syncSystemCfg.sh
	  
	  #sync shell admin password when update
	  if [ -f /etc/shadow ]
	  then
	    /bin/cp -rf /etc/shadow /hivemap/etc/shadow
	  fi
	  
	  #call update 3.0 to later script
	  cd $NMS_HOME	    
	  if [ $# -eq 1 ] 
	  then
		  . $UPDATE_SHELL_HOME/updateFromTomcat.sh 0
		  
		  if [ $? != 0 ]
		  then
		    exit 1
		  else
		     exit 0
		  fi
	  else
		  . $UPDATE_SHELL_HOME/updateFromTomcat.sh $2
		  
		  if [ $? != 0 ]
		  then
		    exit 1
		  else
		    exit 0
		  fi
		fi  
	else
	  . $UPDATE_SHELL_HOME/updateTomcatByself.sh $2
	  
	  if [ $? != 0 ]
	  then
		  exit 1
	  else
		  exit 0
	  fi	  
	fi
  
else
  echo "update_in_progress" > /dev/stderr
  exit 1
fi
echo "updateSoftware end." >>$HM_SCRIPT_LOGFILE
