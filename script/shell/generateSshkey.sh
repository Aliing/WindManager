#!/bin/bash
###this is create the ssh key for authenticate.

. /HiveManager/script/shell/setHmEnv.sh

if [ -z $1 ]
then
  echo 1 >/dev/stdout
  exit 1
fi

if [ $1 != dsa -a $1 != rsa ]
then
   echo 1 >/dev/stdout
   exit 1
fi

if [ ! -f /etc/ssh/ssh_host_rsa_key -o ! -f /etc/ssh/ssh_host_dsa_key -o ! -f /etc/ssh/ssh_host_rsa_key.pub -o ! -f /etc/ssh/ssh_host_dsa_key.pub ]
then
  rm -rf /etc/ssh/ssh_host*
  /bin/ssh-keygen -t rsa -f /etc/ssh/ssh_host_rsa_key -N "" >>$HM_SCRIPT_LOGFILE 2>&1
  /bin/ssh-keygen -t dsa -f /etc/ssh/ssh_host_dsa_key -N "" >>$HM_SCRIPT_LOGFILE 2>&1
fi

###Remove the root scpuser account###
SCPUSER_HOME=/home/scpuser
if [ ! -d $SCPUSER_HOME ]
then
	userdel scpuser
	useradd scpuser
fi

SSH_KEY_HOME=/HiveManager/ssh_key

if [ ! -d $SSH_KEY_HOME ]
then
  mkdir -p $SSH_KEY_HOME >>$HM_SCRIPT_LOGFILE 2>&1
fi

SSH_KEY_TMP=ssh_tmp

cd $SSH_KEY_HOME >>$HM_SCRIPT_LOGFILE 2>&1

rm -rf $SSH_KEY_TMP >>$HM_SCRIPT_LOGFILE 2>&1
mkdir $SSH_KEY_TMP >>$HM_SCRIPT_LOGFILE 2>&1

ssh-keygen -t $1 -f $SSH_KEY_TMP/ssh_login_key -N "" -q >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo 1 >/dev/stdout
  exit 1
fi

/bin/cp -rf $SSH_KEY_TMP/ssh_login_key ./ssh_login_key >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo 1 >/dev/stdout  
  exit 1
fi

SSH_HOME=/home/scpuser/.ssh

if [ ! -d $SSH_HOME ]
then
  mkdir -p $SSH_HOME >>$HM_SCRIPT_LOGFILE 2>&1
fi

AUTH_FILE=$SSH_HOME/authorized_keys

rm -rf $AUTH_FILE >>$HM_SCRIPT_LOGFILE 2>&1

cat $SSH_KEY_TMP/ssh_login_key.pub > $AUTH_FILE 

chmod 700 $SSH_HOME >>$HM_SCRIPT_LOGFILE 2>&1

chmod 600 $AUTH_FILE >>$HM_SCRIPT_LOGFILE 2>&1

chmod o-r /root

chmod o-r /hivetmp

chmod o-r /hivemap

chmod o-r /HiveManager/*

if [ -d /HiveManager/PGPool ]
then
	chmod o+r /HiveManager/PGPool
fi

if [ -d /HiveManager/tomcat ]
then
	chmod o+r /HiveManager/tomcat
	chmod o-r /HiveManager/tomcat/*
fi

if [ -d /HiveManager/tomcat/webapps ]
then
	chmod o+x /HiveManager/tomcat/webapps
	chmod o-r /HiveManager/tomcat/webapps/*
fi

if [ -d /HiveManager/tomcat/webapps/hm ]
then
	chmod o+r /HiveManager/tomcat/webapps/hm
	chmod o-r /HiveManager/tomcat/webapps/hm/*
fi


if [ -d /HiveManager/tomcat/webapps/hm/res ]
then
	chmod o+r /HiveManager/tomcat/webapps/hm/res
fi

if [ -d /HiveManager/tomcat/webapps/hm/upload ]
then
	chmod o+wr -R /HiveManager/tomcat/webapps/hm/upload
fi

chown scpuser:scpuser -R $SSH_HOME

rm -rf  $SSH_KEY_TMP >>$HM_SCRIPT_LOGFILE 2>&1

if [ $1 = rsa ]
then
  /bin/cp -rf /etc/ssh/ssh_host_rsa_key.pub  ./ssh_host_id_key >>$HM_SCRIPT_LOGFILE 2>&1
else
  /bin/cp -rf /etc/ssh/ssh_host_dsa_key.pub  ./ssh_host_id_key >>$HM_SCRIPT_LOGFILE 2>&1
fi

echo 0 >/dev/stdout

