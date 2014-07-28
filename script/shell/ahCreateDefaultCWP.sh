#!/bin/bash
#use for create default cwp key file for domain 
#the default cwp file name is "Default-CWPCert"
#para is domainname

. /HiveManager/script/shell/setHmEnv.sh

if [ -z $1 ]
then
  echo "need the domain name"
  exit 1
fi

NMS_CA_ROOT=/HiveManager/downloads/home/aerohiveca
NMS_CONF_ROOT=$NMS_HOME/conf

CWP_LOCATION=/HiveManager/downloads/"$1"/cwp/serverkey

if [ ! -f $NMS_CA_ROOT/Default-CWPCert.pem ]
then

 cd $NMS_HOME

#$SHELL_HOME/ahCreateDefaultCA.sh
 
#if [ $? != 0 ]
#then
#  echo "error_message" > /dev/stderr
#  exit 1
#fi
 
 /bin/cp -rf $SHELL_HOME/defaultcsr.conf   $NMS_CONF_ROOT/servercsr.conf >>$HM_SCRIPT_LOGFILE 2>&1
 
 cd $NMS_HOME
 
 $SHELL_HOME/ahCreateServerCsrNoPsd.sh 1024 Default-CWPCert
 
if [ $? != 0 ]
then
  echo "error_message" > /dev/stderr
  exit 1
fi
 
# PASSWD=`cat $SHELL_HOME/hmkey.psd`
 
# cd $NMS_HOME
 
# $SHELL_HOME/ahCAServerCsr.sh $PASSWD 7300 Default-CWPCert

openssl x509 -req -days 7300 -in $NMS_CA_ROOT/Default-CWPCert.csr -signkey $NMS_CA_ROOT/Default-CWPCert_key.pem  -out $NMS_CA_ROOT/Default-CWPCert_cert.pem -keyform PEM -outform PEM -extensions xpserver_ext -extfile $SHELL_HOME/xpextensions >>$HM_SCRIPT_LOGFILE 2>&1
 
 if [ $? != 0 ]
then
  echo "error_message" > /dev/stderr
  exit 1
fi
 
 cd $NMS_HOME
  
 $SHELL_HOME/ahMergeKeyAndCert.sh Default-CWPCert_cert.pem Default-CWPCert_key.pem Default-CWPCert.pem
 
if [ $? != 0 ]
then
  echo "error_message" > /dev/stderr
  exit 1
fi
 
fi

mkdir -p $CWP_LOCATION

/bin/cp -rf $NMS_CA_ROOT/Default-CWPCert.pem $CWP_LOCATION >>$HM_SCRIPT_LOGFILE 2>&1

