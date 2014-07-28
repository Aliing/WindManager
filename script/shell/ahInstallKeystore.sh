# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ $# == 3 ]
then
  
  NMS_CA_HOME=/HiveManager/downloads/home/aerohiveca
 
 . $SHELL_HOME/setEnv.sh
  
  cd $HM_ROOT

  openssl pkcs12 -export -in $NMS_CA_HOME/"$1" -inkey $NMS_CA_HOME/"$2" -passin pass:$3 -out keystore -passout pass:Aerohive >>$HM_SCRIPT_LOGFILE 2>&1
  
  if [ $? != 0 ]
  then
    echo "install_ssl_err" > /dev/stderr
    exit 1
  fi  
  
  if [ -f $HM_ROOT/.keystore  ]
  then 
   rm -rf $HM_ROOT/.keystore
  fi
  
  $JAVA_HOME/bin/keytool -importkeystore -srckeystore keystore -destkeystore .keystore -srcstoretype pkcs12 -deststoretype jks -srcstorepass Aerohive -deststorepass Aerohive -noprompt >>$HM_SCRIPT_LOGFILE 2>&1
  
  if [ $? != 0 ]
  then
    echo "install_ssl_err" > /dev/stderr
    exit 1
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
      
      KS_ALIAS=`$JAVA_HOME/bin/keytool -list -v -keystore $KEYSTORE_FILE -storepass Aerohive | sed -n 's/.*Alias name: \([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
   
      if [ -z "$KS_ALIAS" ]
      then
        KS_ALIAS=Aerohive
      fi  
      
      SSLCertificateFile=/etc/pki/tls/certs/localhost.crt
      SSLCertificateKeyFile=/etc/pki/tls/private/localhost.key
      # Generate keystore file if does not exists
      /bin/cp -f $SSLCertificateFile $SSLCertificateFile.bak
      /bin/cp -f $SSLCertificateKeyFile $SSLCertificateKeyFile.bak
      # Overwrite Apache default SSL certificate
      /root/convert_keystore_cert.sh $KEYSTORE_FILE "$KS_ALIAS" Aerohive $SSLCertificateFile $SSLCertificateKeyFile >>$HM_SCRIPT_LOGFILE 2>&1
      if [ $? != 0 ]; then
          #echo "Generate SSL certificate failure, reverting back to current certificate."
          /bin/cp -f $SSLCertificateFile.bak $SSLCertificateFile
          /bincp -f $SSLCertificateKeyFile.bak $SSLCertificateKeyFile
      else
          PRODUCT_TYPE=HM
          if [ -f /etc/sysconfig/product_type.conf ]
          then
            PRODUCT_TYPE=`cat /etc/sysconfig/product_type.conf`
          fi  
                  
          #case "$PRODUCT_TYPE" in
		  #HM_GM)
		#		 service httpd restart
		 #        ;;
		  #*)
			     # Do nothing
		#	     ;;
		 # esac
      fi
  fi 			

else
  echo "install_ssl_err" > /dev/stderr
  exit 1
fi