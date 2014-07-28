# !/bin/bash
#$1:passwd,$2:keysize, $3:days $4:domainname

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 4 ]
then

NMS_CA_ROOT=/HiveManager/downloads/"${4}"/aerohiveca

NMS_CONF_ROOT="$NMS_CA_ROOT"

if [ ! -d "$NMS_CA_ROOT" ]
then 
  mkdir -p "$NMS_CA_ROOT"
fi

openssl genrsa -des3 -out "$NMS_CA_ROOT"/Default_key.pem -passout pass:$1 $2  

 if [ $? != 0 ]
 then
  	  echo "error_message" > /dev/stderr
      exit 1
 fi

openssl req -new -key "$NMS_CA_ROOT"/Default_key.pem -out "$NMS_CA_ROOT"/hm.csr -passin pass:$1 -config "$NMS_CONF_ROOT"/hmcsr.conf 

 if [ $? != 0 ]
 then
  	  echo "error_message" > /dev/stderr
      exit 1
 fi

openssl x509 -req -days $3 -in "$NMS_CA_ROOT"/hm.csr -signkey "$NMS_CA_ROOT"/Default_key.pem -out "$NMS_CA_ROOT"/Default_CA.pem -keyform PEM -outform PEM -passin pass:$1 

 if [ $? != 0 ]
 then
  	  echo "error_message" > /dev/stderr
      exit 1
 fi

else
  echo "error:must input 4 param" > /dev/stderr  
  exit 1
fi