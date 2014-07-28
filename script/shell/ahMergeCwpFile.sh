#!/bin/bash
#this use for merge the cwp files
#$1:domain name $2:name of cert $3:name of privatekey $4:output file name $5:psd of private key

. /HiveManager/script/shell/setHmEnv.sh

NMS_CA_ROOT=/HiveManager/downloads/"$1"/aerohiveca

CWP_LOCATION=/HiveManager/downloads/"$1"/cwp/serverkey

if [ $# == 4 ]
then
     if [ "$2" == "$3" ]
     then
         /bin/cp -rf $NMS_CA_ROOT/"$2" $NMS_CA_ROOT/"$4" >>$HM_SCRIPT_LOGFILE 2>&1
     else
	     openssl pkcs12 -export -in $NMS_CA_ROOT/"$2" -inkey $NMS_CA_ROOT/"$3" -passin pass:"" -out $NMS_CA_ROOT/"$4".tmp -passout pass:"" >>$HM_SCRIPT_LOGFILE 2>&1
	     
	     if [ $? != 0 ]
	     then
	         echo "error_message" > /dev/stderr
	         rm -rf $NMS_CA_ROOT/"$4".tmp  
	         exit 1
	     fi
	  
	     openssl pkcs12 -in $NMS_CA_ROOT/"$4".tmp -passin pass:"" -out $NMS_CA_ROOT/"$4" -passout pass:""  -nodes >>$HM_SCRIPT_LOGFILE 2>&1
	
	     if [ $? != 0 ]
	     then
	         echo "error_message" > /dev/stderr
	         rm -rf $NMS_CA_ROOT/"$4".tmp  
	         rm -rf $NMS_CA_ROOT/"$4"
	         exit 1
	     fi
	  
	     rm -rf $NMS_CA_ROOT/"$4".tmp    
	 fi
     
     mkdir -p  $CWP_LOCATION
     
     /bin/cp -rf $NMS_CA_ROOT/"$4" $CWP_LOCATION/"$4" >>$HM_SCRIPT_LOGFILE 2>&1
     
     rm -rf $NMS_CA_ROOT/"$4"

     exit 0
else
    if [ $# == 5 ]
    then
         if [ "$2" == "$3" ]
         then
             openssl pkcs12 -export -in $NMS_CA_ROOT/"$2" -inkey $NMS_CA_ROOT/"$3" -passin pass:"$5" -out $NMS_CA_ROOT/"$4".tmp -passout pass:"$5" >>$HM_SCRIPT_LOGFILE 2>&1
	         if [ $? != 0 ]
	         then
	             echo "error_message" > /dev/stderr
	             rm -rf $NMS_CA_ROOT/"$4".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             exit 1
	         fi
	         
	         openssl pkcs12 -in $NMS_CA_ROOT/"$4".tmp -passin pass:"$5" -out $NMS_CA_ROOT/"$4"_cert.tmp -nokeys >>$HM_SCRIPT_LOGFILE 2>&1
	         if [ $? != 0 ]
	         then
	             echo "error_message" > /dev/stderr
	             rm -rf $NMS_CA_ROOT/"$4".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             rm -rf $NMS_CA_ROOT/$NMS_CA_ROOT/"$4"_cert.tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             exit 1
	         fi
	         
	         openssl pkcs12 -in $NMS_CA_ROOT/"$4".tmp -passin pass:"$5" -out $NMS_CA_ROOT/"$4"_key.tmp -nocerts -passout pass:"$5" >>$HM_SCRIPT_LOGFILE 2>&1
	         if [ $? != 0 ]
	         then
	             echo "error_message" > /dev/stderr
	             rm -rf $NMS_CA_ROOT/"$4".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             rm -rf $NMS_CA_ROOT/$NMS_CA_ROOT/"$4"_cert.tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             rm -rf $NMS_CA_ROOT/$NMS_CA_ROOT/"$4"_key.tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             exit 1
	         fi
	         
	         rm -rf $NMS_CA_ROOT/"$4".tmp
	         
	         openssl rsa -in $NMS_CA_ROOT/"$4"_key.tmp -passin pass:"$5" -out $NMS_CA_ROOT/"$3".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	         if [ $? != 0 ]
	         then
	             echo "error_message" > /dev/stderr
	             rm -rf $NMS_CA_ROOT/"$3".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             rm -rf $NMS_CA_ROOT/$NMS_CA_ROOT/"$4"_cert.tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             rm -rf $NMS_CA_ROOT/$NMS_CA_ROOT/"$4"_key.tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             exit 1
	         fi
	         
	         rm -rf $NMS_CA_ROOT/$NMS_CA_ROOT/"$4"_key.tmp >>$HM_SCRIPT_LOGFILE 2>&1
	         
	         openssl pkcs12 -export -in $NMS_CA_ROOT/"$4"_cert.tmp -inkey $NMS_CA_ROOT/"$3".tmp -passin pass:"" -out $NMS_CA_ROOT/"$4".tmp -passout pass:"" >>$HM_SCRIPT_LOGFILE 2>&1
	        
	         if [ $? != 0 ]
	         then
	             echo "error_message" > /dev/stderr
	             rm -rf $NMS_CA_ROOT/"$4".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             rm -rf $NMS_CA_ROOT/"$3".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             exit 1
	         fi
	         
	         rm -rf $NMS_CA_ROOT/"$3".tmp >>$HM_SCRIPT_LOGFILE 2>&1
         else
             openssl rsa -in $NMS_CA_ROOT/"$3" -passin pass:"$5" -out $NMS_CA_ROOT/"$3".tmp >>$HM_SCRIPT_LOGFILE 2>&1
             
             if [ $? != 0 ]
	         then
	             echo "error_message" > /dev/stderr
	             rm -rf $NMS_CA_ROOT/"$3".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             exit 1
	         fi
         
	         openssl pkcs12 -export -in $NMS_CA_ROOT/"$2" -inkey $NMS_CA_ROOT/"$3".tmp -passin pass:"" -out $NMS_CA_ROOT/"$4".tmp -passout pass:"" >>$HM_SCRIPT_LOGFILE 2>&1
	        
	        if [ $? != 0 ]
	         then
	             echo "error_message" > /dev/stderr
	             rm -rf $NMS_CA_ROOT/"$4".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             rm -rf $NMS_CA_ROOT/"$3".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	             exit 1
	         fi
	
	        rm -rf $NMS_CA_ROOT/"$3".tmp >>$HM_SCRIPT_LOGFILE 2>&1
	     fi
	     
         openssl pkcs12 -in $NMS_CA_ROOT/"$4".tmp -passin pass:""  -out $NMS_CA_ROOT/"$4" -passout pass:""  -nodes >>$HM_SCRIPT_LOGFILE 2>&1

         if [ $? != 0 ]
         then
           echo "error_message" > /dev/stderr
           rm -rf $NMS_CA_ROOT/"$4".tmp  >>$HM_SCRIPT_LOGFILE 2>&1
           rm -rf $NMS_CA_ROOT/"$4" >>$HM_SCRIPT_LOGFILE 2>&1
           exit 1
         fi
         
         rm -rf $NMS_CA_ROOT/"$4".tmp    >>$HM_SCRIPT_LOGFILE 2>&1         
         
         mkdir -p  $CWP_LOCATION >>$HM_SCRIPT_LOGFILE 2>&1
     
         /bin/cp -rf $NMS_CA_ROOT/"$4" $CWP_LOCATION/"$4" >>$HM_SCRIPT_LOGFILE 2>&1
     
         rm -rf $NMS_CA_ROOT/"$4" >>$HM_SCRIPT_LOGFILE 2>&1
          
         exit 0
         
    else
        echo "$2:name of keyfile $3:name of cert $4:output file name $5:psd of private key"
        exit 1
    fi

fi

