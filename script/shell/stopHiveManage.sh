# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

#NMS_WEBINFO_HOME=$NMS_HOME/webapps/ROOT/WEB-INF

. $SHELL_HOME/setEnv.sh

export JAVA_HOME

killall -9 hm_daemon >>$HM_SCRIPT_LOGFILE 2>&1
killall -9 hm_tunnel >>$HM_SCRIPT_LOGFILE 2>&1

cd $NMS_HOME

#killall hm_capwap >>$HM_SCRIPT_LOGFILE 2>&1 &
#./bin/shutdown.sh -force >>$HM_SCRIPT_LOGFILE 2>&1 &

./bin/shutdown.sh 60 -force >>$HM_SCRIPT_LOGFILE 2>&1

TOMCAT_PIDS=`ps aux|grep "catalina.startup.Bootstrap"|grep -v "grep"|awk '{print $2}'`
if [ ! -z "$TOMCAT_PIDS" ];then
	echo "Killing PID(S) by manual operation: $TOMCAT_PIDS" >>$HM_SCRIPT_LOGFILE 2>&1
	kill -9 $TOMCAT_PIDS >>$HM_SCRIPT_LOGFILE 2>&1
fi
pkill -9 hm_capwap >>$HM_SCRIPT_LOGFILE 2>&1

touch /root/stop_by_shell
