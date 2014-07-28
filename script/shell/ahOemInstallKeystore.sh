# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 3 ]
then
  
  NMS_CA_HOME=/HiveManager/downloads/home/aerohiveca
 
 . $SHELL_HOME/setEnv.sh
  
  cd $HM_ROOT/oemfiles

  openssl pkcs12 -export -in $NMS_CA_HOME/"$1" -inkey $NMS_CA_HOME/"$2" -passin pass:$3 -out keystore -passout pass:Aerohive >>$HM_SCRIPT_LOGFILE 2>&1
  
  if [ $? != 0 ]
  then
    echo "install_ssl_err" > /dev/stderr
    exit 1
  fi  
  
  if [ -f $HM_ROOT/oemfiles/.oemkeystore  ]
  then 
   rm -rf $HM_ROOT/oemfiles/.oemkeystore
  fi
  
  $JAVA_HOME/bin/keytool -importkeystore -srckeystore keystore -destkeystore .oemkeystore -srcstoretype pkcs12 -deststoretype jks -srcstorepass Aerohive -deststorepass Aerohive -noprompt >>$HM_SCRIPT_LOGFILE 2>&1
  
  if [ $? != 0 ]
  then
    echo "install_ssl_err" > /dev/stderr
    exit 1
  fi   

else
  echo "install_ssl_err" > /dev/stderr
  exit 1
fi