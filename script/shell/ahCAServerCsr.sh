# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

NMS_CA_ROOT=/HiveManager/downloads/home/aerohiveca

NMS_SHELL_ROOT=/HiveManager/script/shell

openssl x509 -req -days $2 -in $NMS_CA_ROOT/"$3".csr -out $NMS_CA_ROOT/"$3"_cert.pem -keyform PEM -outform PEM -CAkey $NMS_CA_ROOT/Default_key.pem -CA $NMS_CA_ROOT/Default_CA.pem -CAcreateserial -passin pass:$1 -extensions xpserver_ext -extfile $NMS_SHELL_ROOT/xpextensions >>$HM_SCRIPT_LOGFILE 2>&1

 if [ $? != 0 ]
 then
  	  echo "error_message" > /dev/stderr
      exit 1
 fi

rm -rf $NMS_CA_ROOT/"$3".csr