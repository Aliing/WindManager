# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# != 3 ]
then
   echo "para is error. para1:is key size. para2:is file name"
   exit 1
else

    NMS_CA_ROOT=/HiveManager/downloads/"${3}"/aerohiveca

    NMS_CONF_ROOT="$NMS_CA_ROOT"    
    
    openssl genrsa -out "$NMS_CA_ROOT"/"$2"_key.pem $1
    
    if [ $? != 0 ]
    then
  	 echo "error_message" > /dev/stderr
     exit 1
    fi  
    
    openssl req -new -out "$NMS_CA_ROOT"/"$2".csr -key  "$NMS_CA_ROOT"/"$2"_key.pem -config "$NMS_CONF_ROOT"/servercsr.conf
  
    if [ $? != 0 ]
    then
  	 echo "error_message" > /dev/stderr
     exit 1
    fi  
    
    exit 0
fi