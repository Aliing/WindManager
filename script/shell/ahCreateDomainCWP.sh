#!/bin/bash
#this is create the domain cwp cert
#$1:domain name $2:keysize $3:validay $4:file_name

. /HiveManager/script/shell/setHmEnv.sh

if [ -z $1 -o -z $2 -o -z $3 -o -z $4 ]
then
  echo "need the param"
  exit 1
fi

NMS_CA_ROOT=/HiveManager/downloads/home/aerohiveca
NMS_CONF_ROOT=$NMS_HOME/conf

CWP_LOCATION=/HiveManager/downloads/"$1"/cwp/serverkey

#cd $NMS_HOME

#$SHELL_HOME/ahCreateDefaultCA.sh

#if [ $? != 0 ]
#then
#  echo "error_message" > /dev/stderr
#  exit 1
#fi
 
cd $NMS_HOME
 
$SHELL_HOME/ahCreateServerCsrNoPsd.sh "$2" "$4"

if [ $? != 0 ]
then
  echo "error_message" > /dev/stderr
  exit 1
fi

#PASSWD=`cat $NMS_CA_ROOT/hmkey.psd`
 
# cd $NMS_HOME
 
# $SHELL_HOME/ahCAServerCsr.sh "$PASSWD" "$3" "$4"

openssl x509 -req -days "$3" -in $NMS_CA_ROOT/"$4".csr -signkey $NMS_CA_ROOT/"$4"_key.pem  -out $NMS_CA_ROOT/"$4"_cert.pem -keyform PEM -outform PEM -extensions xpserver_ext -extfile $SHELL_HOME/xpextensions >>$HM_SCRIPT_LOGFILE 2>&1
 
if [ $? != 0 ]
then
  echo "error_message" > /dev/stderr
  exit 1
fi
 
 cd $NMS_HOME
  
 $SHELL_HOME/ahMergeKeyAndCert.sh "$4"_cert.pem "$4"_key.pem "$4".pem
 
if [ $? != 0 ]
then
  echo "error_message" > /dev/stderr
  exit 1
fi
 
mkdir -p $CWP_LOCATION

/bin/cp -rf $NMS_CA_ROOT/"$4".pem $CWP_LOCATION >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  echo "error_message" > /dev/stderr
  exit 1
fi

rm -rf $NMS_CA_ROOT/"$4".*
 