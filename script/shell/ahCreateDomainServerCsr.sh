# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 4 ]
then

NMS_CA_ROOT=/HiveManager/downloads/"${4}"/aerohiveca

NMS_CONF_ROOT="$NMS_CA_ROOT"

openssl genrsa -des3 -out "$NMS_CA_ROOT"/"$3"_key.pem -passout pass:$1 $2  

 if [ $? != 0 ]
 then
  	  echo "error_message" > /dev/stderr
      exit 1
 fi

openssl req -new -key "$NMS_CA_ROOT"/"$3"_key.pem -out "$NMS_CA_ROOT"/"$3".csr -passin pass:$1 -config "$NMS_CONF_ROOT"/servercsr.conf 

 if [ $? != 0 ]
 then
  	  echo "error_message" > /dev/stderr
      exit 1
 fi

else
  echo "error:must input 4 param" > /dev/stderr  
  exit 1
fi