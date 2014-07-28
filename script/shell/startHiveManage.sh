# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

. $SHELL_HOME/setEnv.sh

# note that all ENVs which be called in java code must be export, please be aware of this.
export HM_CITADEL=/HiveManager
export CATALINA_HOME
export HM_ROOT
export JAVA_HOME

DB_NAME=hm

if [ -f $SHELL_HOME/getDBName.sh  ]
then
	DB_NAME=`$SHELL_HOME/getDBName.sh`
fi

. $SHELL_HOME/ahshellfuncs

RESULT_USER_HM=`hmdbcheck "$DB_NAME" "hivemanager"  30 10 >>$HM_SCRIPT_LOGFILE 2>&1 `

if [ $? != 0 ] ; then
	echo "could not access db" >>$HM_SCRIPT_LOGFILE 2>&1
fi

##remove Aerohive NetWork Inc
sed -i "s/.*\(Aero.*Inc\.\)//" /etc/issueserial

##move fingerprints file to /HiveManager/tomcat/webapps/hm/res/home/
if [ ! -d $HM_ROOT/res/home/fingerprints ]
then
	mkdir -p $HM_ROOT/res/home/fingerprints
	if [ -d $HM_ROOT/fingerprints ]
	then
	/bin/cp -rf $HM_ROOT/fingerprints/* $HM_ROOT/res/home/fingerprints
	fi
fi

if [ -d $HM_ROOT/res/fingerprints ]
then
/bin/cp -rf $HM_ROOT/res/fingerprints/* $HM_ROOT/res/home/fingerprints
/bin/rm -rf $HM_ROOT/res/fingerprints
fi

##cp fingerprints
if [ -d /hivemap ]
then
	if [ -d /hivemap/HiveManager/tomcat/webapps/hm/res/fingerprints ]
	then
		/bin/cp -rf /hivemap/HiveManager/tomcat/webapps/hm/res/fingerprints/* $HM_ROOT/res/home/fingerprints
		/bin/rm -rf /hivemap/HiveManager/tomcat/webapps/hm/res/fingerprints
	fi
	if [ -d /hivemap/HiveManager/tomcat/webapps/hm/res/home/fingerprints ]
	then
		/bin/cp -rf /hivemap/HiveManager/tomcat/webapps/hm/res/home/fingerprints/* $HM_ROOT/res/home/fingerprints
		/bin/rm -rf /hivemap/HiveManager/tomcat/webapps/hm/res/home/fingerprints
	fi
	if [ -d /hivemap/HiveManager/tomcat/hm_soft_upgrade/hm/fingerprints ]
	then
	    /bin/cp -rf /hivemap/HiveManager/tomcat/hm_soft_upgrade/hm/fingerprints/* $HM_ROOT/res/home/fingerprints
	fi
else
    if [ -d $UPDATE_HOME/hm/fingerprints ]
    then
        /bin/cp -rf $UPDATE_HOME/hm/fingerprints/* $HM_ROOT/res/home/fingerprints
    fi
fi

## move files of image
cd $NMS_HOME
if [ -f .haupgrade ]
then
    . $SHELL_HOME/haUpdateSingSoftWare.sh
fi

##hm-4-nodes-ha, upgrade local schema
if [ -f /hivemap/HiveManager/tomcat/hm_soft_upgrade/appserver ]
then
	. $SHELL_HOME/ah2PartitionResetLocalSchema.sh
fi

#if [ -f /hivemap/root/.auto_jvm_memory ]; then
#	touch /root/.auto_jvm_memory
#fi
if [ -f /root/.auto_jvm_memory ] && [ -f $SHELL_HOME/setJVMMemory.sh ]; then
	$SHELL_HOME/setJVMMemory.sh
fi

HM_BIT=`getconf LONG_BIT`

JVM_OPT_FILE=$HM_ROOT/WEB-INF/hmconf/jvmoption.conf

if [ ! -f $JVM_OPT_FILE ]
then
  if [ $HM_BIT == 32 ]
  then
    export JAVA_OPTS="-Xms256m -Xmx512m -XX:MaxPermSize=128m -Dsun.net.inetaddr.ttl=0"
  else
    export JAVA_OPTS="-Xms256m -Xmx1024m -XX:MaxPermSize=512m -Dsun.net.inetaddr.ttl=0"

    memsize=`free | grep Mem | awk {'print $2'}`
    if [ $memsize -gt 4000000 ]
    then
      export JAVA_OPTS="-Xms256m -Xmx2048m -XX:MaxPermSize=512m -Dsun.net.inetaddr.ttl=0"
   fi
 fi
else
    MAX_MEM_OPT=`cat $JVM_OPT_FILE | sed -n 's/.*MAX_MEMORY[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`
    MAX_PEMMEM_OPT=`cat $JVM_OPT_FILE | sed -n 's/.*MAX_PERM_MEMORY[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`
    MIN_PEMMEM_OPT=`cat $JVM_OPT_FILE | sed -n 's/.*MIN_PERM_MEMORY[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`
    export JAVA_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${CATALINA_HOME}/logs/heapdump.hprof -Xms256m -Xmx${MAX_MEM_OPT}m -XX:PermSize=${MIN_PEMMEM_OPT}m -XX:MaxPermSize=${MAX_PEMMEM_OPT}m -Dsun.net.inetaddr.ttl=0"
fi

#if [ -f /etc/sysconfig/iptables.hivemanager ]
#then
#  /bin/cp -rf /etc/sysconfig/iptables.hivemanager /etc/sysconfig/iptables >>$HM_SCRIPT_LOGFILE 2>&1
#  service iptables restart >>$HM_SCRIPT_LOGFILE 2>&1
#fi


cd $NMS_HOME
##restore of upgrade
if [ -f .haupgraderestore ]
then
	. $SHELL_HOME/haRestoreDB.sh

fi

##restore
if [ -f dbxmlfile/.backupdump ]
then
	. $SHELL_HOME/haRestoreFromDump.sh >>$HM_SCRIPT_LOGFILE 2>&1
else
    . $SHELL_HOME/ahRestoreBeforeStart.sh  >>$HM_SCRIPT_LOGFILE 2>&1
fi


HM_SHELL_HOME=/HiveManager/script/shell


PASSWD=`/HiveManager/encryptscpuser/getScpuserPsd.sh`
APP_TYPE=`cat $HM_ROOT/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`

if [ -f $HM_SHELL_HOME/server.xml.hivemanager ]
then
  /bin/cp -rf $HM_SHELL_HOME/server.xml.hivemanager $CATALINA_HOME/conf/server.xml >>$HM_SCRIPT_LOGFILE 2>&1
fi

if [ -f $HM_SHELL_HOME/web.xml.hivemanager ]
then
  /bin/cp -rf $HM_SHELL_HOME/web.xml.hivemanager $CATALINA_HOME/conf/web.xml >>$HM_SCRIPT_LOGFILE 2>&1
fi

if [ $APP_TYPE != "HM" -a $APP_TYPE != "hm" ]
then
  if [ -f $HM_SHELL_HOME/catalina.properties.hmol ]
  then
    /bin/cp -rf $HM_SHELL_HOME/catalina.properties.hmol  $HM_SHELL_HOME/catalina.properties.old >>$HM_SCRIPT_LOGFILE 2>&1
    sed -e  "s,.*userpass.hmresprotect=\([^ ][-A-Za-z0-9\/._\+]*\).*,userpass.hmresprotect=${PASSWD},g" $HM_SHELL_HOME/catalina.properties.old > $CATALINA_HOME/conf/catalina.properties
    /bin/cp -rf $HM_SHELL_HOME/tomcat-users.xml.hivemanager $CATALINA_HOME/conf/tomcat-users.xml >>$HM_SCRIPT_LOGFILE 2>&1
    #/bin/rm -rf $HM_SHELL_HOME/tomcat-users.xml.hivemanager >>$HM_SCRIPT_LOGFILE 2>&1
  fi
else
  if [ -f /etc/sysconfig/product_type.conf ]
  then
    PRODUCT_TYPE=`cat /etc/sysconfig/product_type.conf`

    if [ $PRODUCT_TYPE == HM_GM ]
    then
      if [ -f $HM_SHELL_HOME/catalina.properties.hm ]
      then
        /bin/cp -rf $HM_SHELL_HOME/catalina.properties.hm  $HM_SHELL_HOME/catalina.properties.old >>$HM_SCRIPT_LOGFILE 2>&1
		sed -e  "s,.*userpass.hmresprotect=\([^ ][-A-Za-z0-9\/._\+]*\).*,userpass.hmresprotect=${PASSWD},g" $HM_SHELL_HOME/catalina.properties.old > $CATALINA_HOME/conf/catalina.properties
      	/bin/cp -rf $HM_SHELL_HOME/tomcat-users.xml.hivemanager $CATALINA_HOME/conf/tomcat-users.xml >>$HM_SCRIPT_LOGFILE 2>&1
      	#/bin/rm -rf $HM_SHELL_HOME/tomcat-users.xml.hivemanager >>$HM_SCRIPT_LOGFILE 2>&1
      fi
    elif [ $PRODUCT_TYPE == HM ]
    then
      if [ -f $HM_SHELL_HOME/catalina.properties.std.hm ]
      then
      	/bin/cp -rf $HM_SHELL_HOME/catalina.properties.std.hm  $HM_SHELL_HOME/catalina.properties.old >>$HM_SCRIPT_LOGFILE 2>&1
		sed -e  "s,.*userpass.hmresprotect=\([^ ][-A-Za-z0-9\/._\+]*\).*,userpass.hmresprotect=${PASSWD},g" $HM_SHELL_HOME/catalina.properties.old > $CATALINA_HOME/conf/catalina.properties
      	/bin/cp -rf $HM_SHELL_HOME/tomcat-users.xml.hivemanager $CATALINA_HOME/conf/tomcat-users.xml >>$HM_SCRIPT_LOGFILE 2>&1
      	#/bin/rm -rf $HM_SHELL_HOME/tomcat-users.xml.hivemanager >>$HM_SCRIPT_LOGFILE 2>&1
      fi
    fi
  else
  	if [ -f $HM_SHELL_HOME/catalina.properties.hm ]
  	then
  	  /bin/cp -rf $HM_SHELL_HOME/catalina.properties.hm  $HM_SHELL_HOME/catalina.properties.old >>$HM_SCRIPT_LOGFILE 2>&1
	  sed -e  "s,.*userpass.hmresprotect=\([^ ][-A-Za-z0-9\/._\+]*\).*,userpass.hmresprotect=${PASSWD},g" $HM_SHELL_HOME/catalina.properties.old > $CATALINA_HOME/conf/catalina.properties
  	  /bin/cp -rf $HM_SHELL_HOME/tomcat-users.xml.hivemanager $CATALINA_HOME/conf/tomcat-users.xml >>$HM_SCRIPT_LOGFILE 2>&1
  	  #/bin/rm -rf $HM_SHELL_HOME/tomcat-users.xml.hivemanager >>$HM_SCRIPT_LOGFILE 2>&1
  fi
  fi
fi

##sign jar for proxy
##$SHELL_HOME/signjar.sh

cd $NMS_HOME

if [ -f $CATALINA_HOME/bin/catalina.pid ]
then
  rm -rf $CATALINA_HOME/bin/catalina.pid >>$HM_SCRIPT_LOGFILE 2>&1
fi

if [ -f $HM_SHELL_HOME/modCasClientFromDef.sh ]
then
. $HM_SHELL_HOME/modCasClientFromDef.sh
fi

if [ ! -f /etc/secretkeys -a -f $HM_ROOT/WEB-INF/security/secretkeys ]
then
  /bin/cp -rf $HM_ROOT/WEB-INF/security/secretkeys /etc/secretkeys >>$HM_SCRIPT_LOGFILE 2>&1
  rm -rf $HM_ROOT/WEB-INF/security/secretkeys
fi

if [ -f /etc/secretkeys -a -f $HM_ROOT/WEB-INF/security/secretkeys ]
then
. $HM_SHELL_HOME/checkSecretkeys.sh >>$HM_SCRIPT_LOGFILE 2>&1
rm -rf $HM_ROOT/WEB-INF/security/secretkeys
fi

if [ ! -f /etc/secretkey -a -f $HM_ROOT/WEB-INF/security/secretkey ]
then
  /bin/cp -rf $HM_ROOT/WEB-INF/security/secretkey /etc/secretkey >>$HM_SCRIPT_LOGFILE 2>&1
  rm -rf $HM_ROOT/WEB-INF/security/secretkey
fi

if [ -f /etc/secretkey -a -f $HM_ROOT/WEB-INF/security/secretkey ]
then
. $HM_SHELL_HOME/checkSecretkey.sh >>$HM_SCRIPT_LOGFILE 2>&1
rm -rf $HM_ROOT/WEB-INF/security/secretkey
fi

if [ -f $HM_ROOT/WEB-INF/security/rest.login.config ]; then
	JAVA_OPTS="-Djava.security.auth.login.config=$HM_ROOT/WEB-INF/security/rest.login.config $JAVA_OPTS"
fi

if [ -f $HM_ROOT/WEB-INF/security/rest.access.policy ]; then
	JAVA_OPTS="-Djava.security.auth.policy=$HM_ROOT/WEB-INF/security/rest.access.policy $JAVA_OPTS"
fi

# fix bug 27370
JAVA_OPTS="-Djava.util.Arrays.useLegacyMergeSort=true $JAVA_OPTS"

export JAVA_OPTS

./bin/startup.sh >>$HM_SCRIPT_LOGFILE 2>&1  &

if [ -f /root/stop_by_shell ]
then
  rm -rf /root/stop_by_shell
fi


HM_CAPWAP_HOME=/HiveManager/capwap/

cd $HM_CAPWAP_HOME

if [ -f hm_daemon ]
then
   ulimit -n 65535

   sleep 60

 ./hm_daemon >>$HM_SCRIPT_LOGFILE 2>&1  &
fi
