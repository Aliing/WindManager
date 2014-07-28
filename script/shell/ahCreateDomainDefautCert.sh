#!/bin/bash
#$1:the name of domain
if [ -z $1 ]
then
  echo "1" > /dev/stdout
  exit 1
fi

. /HiveManager/script/shell/setHmEnv.sh

NMS_CA_ROOT=/HiveManager/downloads/"$1"/aerohiveca
ROOT_CA=Default_CA.pem
ROOT_CA_KEY=Default_key.pem
ROOT_CA_KEY_PSD=hmkey.psd
DEFAULT_SERVER_CSR=Default-Server.csr
DEFAULT_SERVER_CERT=Default-Server_cert.pem
DEFAULT_CERT_NAME=Default-Server
DEFAULT_SERVER_KEY=Default-Server_key.pem

if [ ! -d "$NMS_CA_ROOT" ]
then
    mkdir -p "$NMS_CA_ROOT"
fi

if [ -f "$NMS_CA_ROOT"/$DEFAULT_SERVER_CERT ]
then
  echo "0" > /dev/stdout
  exit 0
fi

if [ ! -f "$NMS_CA_ROOT"/$ROOT_CA ]
then
  /bin/cp -f $SHELL_HOME/$ROOT_CA  "$NMS_CA_ROOT" >>$HM_SCRIPT_LOGFILE 2>&1
  /bin/cp -f $SHELL_HOME/$ROOT_CA_KEY  "$NMS_CA_ROOT" >>$HM_SCRIPT_LOGFILE 2>&1
  /bin/cp -f $SHELL_HOME/$ROOT_CA_KEY_PSD  "$NMS_CA_ROOT" >>$HM_SCRIPT_LOGFILE 2>&1
fi

/bin/cp -f $SHELL_HOME/$DEFAULT_SERVER_CSR  "$NMS_CA_ROOT" >>$HM_SCRIPT_LOGFILE 2>&1
/bin/cp -f $SHELL_HOME/$DEFAULT_SERVER_KEY  "$NMS_CA_ROOT" >>$HM_SCRIPT_LOGFILE 2>&1
/bin/cp -f $SHELL_HOME/defaultcsr.conf    "$NMS_CA_ROOT"/servercsr.conf  >>$HM_SCRIPT_LOGFILE 2>&1

PASSWD=`cat $NMS_CA_ROOT/$ROOT_CA_KEY_PSD`

$SHELL_HOME/ahCADomainServerCsr.sh $PASSWD 3650 $DEFAULT_CERT_NAME "$1" >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "1" > /dev/stdout
  exit 1
fi

echo "0" > /dev/stdout
exit 0

