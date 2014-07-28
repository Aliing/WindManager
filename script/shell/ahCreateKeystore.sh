#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

. $SHELL_HOME/setEnv.sh

KEYSTORE_FILE=$HM_ROOT/.keystore

if [ -f $KEYSTORE_FILE ]
then
  rm -rf $KEYSTORE_FILE
fi 

OEM_VALUE=`/HiveManager/script/shell/getOemName.sh`

if [ "$OEM_VALUE" == "Aerohive" -o "$OEM_VALUE" == "aerohive" -o "$OEM_VALUE" == "AEROHIVE" ]
then
  $JAVA_HOME/bin/keytool -genkey -dname "CN=Hivemanager, OU=Engineering, O=Aerohive, L=Sunnyvale, S=California, C=US" -alias Aerohive -keypass Aerohive -storepass Aerohive -validity 1825 -keystore $KEYSTORE_FILE -keyalg RSA
else
  $JAVA_HOME/bin/keytool -genkey -dname "CN=Black Box Authority Server, OU=Engineering, O=Black Box, L=Lawrence, S=Pennsylvania, C=US, emailAddress=TechSupport@BlackBox.com" -alias BlackBox -keypass Aerohive -storepass Aerohive -validity 1825 -keystore $KEYSTORE_FILE -keyalg RSA
fi



HTTPD_EXISTS=`chkconfig --list httpd > /dev/null 2>&1`
if [ $? != 0 ]
then
  HTTPD_EXISTS=1
else
  HTTPD_EXISTS=0
fi

if [ ! -z $HTTPD_EXISTS ] && [ $HTTPD_EXISTS -eq 0 ]
  then  
      KEYSTORE_FILE=$HM_ROOT/.keystore
      SSLCertificateFile=/etc/pki/tls/certs/localhost.crt
      SSLCertificateKeyFile=/etc/pki/tls/private/localhost.key
      # Generate keystore file if does not exists
      /bin/cp -f $SSLCertificateFile $SSLCertificateFile.bak
      /bin/cp -f $SSLCertificateKeyFile $SSLCertificateKeyFile.bak
      # Overwrite Apache default SSL certificate
      /root/convert_keystore_cert.sh $KEYSTORE_FILE Aerohive Aerohive $SSLCertificateFile $SSLCertificateKeyFile >>$HM_SCRIPT_LOGFILE 2>&1
      if [ $? != 0 ]; then
          #echo "Generate SSL certificate failure, reverting back to current certificate."
          /bin/cp -f $SSLCertificateFile.bak $SSLCertificateFile
          /bin/cp -f $SSLCertificateKeyFile.bak $SSLCertificateKeyFile
      else
          PRODUCT_TYPE=HM
          if [ -f /etc/sysconfig/product_type.conf ]
          then
            PRODUCT_TYPE=`cat /etc/sysconfig/product_type.conf`
          fi  
                  
          #case "$PRODUCT_TYPE" in
		  #HM_GM)
				 #service httpd restart
		   #      ;;
		  #*)
			     # Do nothing
			#     ;;
		  #esac
      fi
fi 			