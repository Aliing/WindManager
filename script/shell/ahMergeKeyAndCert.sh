# !/bin/bash
#$1:name of cert $2:name of privatekey $3:output file name $4:psd of private key

. /HiveManager/script/shell/setHmEnv.sh

NMS_CA_ROOT=/HiveManager/downloads/home/aerohiveca

if [ $# == 3 ]
then
     openssl pkcs12 -export -in $NMS_CA_ROOT/"$1" -inkey $NMS_CA_ROOT/"$2" -passin pass:"" -out $NMS_CA_ROOT/"$3".tmp -passout pass:""
     
     if [ $? != 0 ]
     then
         echo "error_message" > /dev/stderr
         exit 1
     fi
  
     openssl pkcs12 -in $NMS_CA_ROOT/"$3".tmp -passin pass:"" -out $NMS_CA_ROOT/"$3" -passout pass:""  -nodes

     if [ $? != 0 ]
     then
         echo "error_message" > /dev/stderr
         exit 1
     fi
  
   
     rm -rf $NMS_CA_ROOT/"$1"
     rm -rf $NMS_CA_ROOT/"$2"
     rm -rf $NMS_CA_ROOT/"$3".tmp    

     exit 0
else
    if [ $# == 4 ]
    then
         openssl pkcs12 -export -in $NMS_CA_ROOT/"$1" -inkey $NMS_CA_ROOT/"$2" -passin pass:"$4" -out $NMS_CA_ROOT/"$3".tmp -passout pass:"$4"
         
         if [ $? != 0 ]
         then
             echo "error_message" > /dev/stderr
             exit 1
         fi

         openssl pkcs12 -in $NMS_CA_ROOT/"$3".tmp -passin pass:"$4"  -out $NMS_CA_ROOT/"$3" -passout pass:"$4"  -nodes

         if [ $? != 0 ]
         then
           echo "error_message" > /dev/stderr
           exit 1
         fi
         
         rm -rf $NMS_CA_ROOT/"$1"
         rm -rf $NMS_CA_ROOT/"$2"
         rm -rf $NMS_CA_ROOT/"$3".tmp        
          
         exit 0
         
    else
        echo "$1:name of keyfile $2:name of cert $3:output file name $4:psd of private key"
        exit 1
    fi

fi
