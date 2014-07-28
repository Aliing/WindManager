#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

SSLCertificateFile=/etc/pki/tls/certs/localhost.crt
SSLCertificateKeyFile=/etc/pki/tls/private/localhost.key
KEYSTORE_FILE=$HM_ROOT/.keystore

umask 077

if [ ! -f /etc/pki/tls/private/localhost.key ] ; then
/usr/bin/openssl genrsa -rand /proc/apm:/proc/cpuinfo:/proc/dma:/proc/filesystems:/proc/interrupts:/proc/ioports:/proc/pci:/proc/rtc:/proc/uptime 1024 > /etc/pki/tls/private/localhost.key 2> $HM_SCRIPT_LOGFILE
fi

FQDN=`hostname`
if [ "x${FQDN}" = "x" ]; then
   FQDN=localhost.localdomain
fi

if [ ! -f /etc/pki/tls/certs/localhost.crt ] ; then
cat << EOF | /usr/bin/openssl req -new -key /etc/pki/tls/private/localhost.key \
         -x509 -days 365 -set_serial $RANDOM \
         -out /etc/pki/tls/certs/localhost.crt 2>>$HM_SCRIPT_LOGFILE
--
SomeState
SomeCity
SomeOrganization
SomeOrganizationalUnit
${FQDN}
root@${FQDN}
EOF

/root/convert_keystore_cert.sh $KEYSTORE_FILE Aerohive Aerohive $SSLCertificateFile $SSLCertificateKeyFile >>$HM_SCRIPT_LOGFILE 2>&1

  PRODUCT_TYPE=HM
  if [ -f /etc/sysconfig/product_type.conf ]
  then
    PRODUCT_TYPE=`cat /etc/sysconfig/product_type.conf`
  fi  
          
  #case "$PRODUCT_TYPE" in
  #HM_GM)
#		 service httpd restart >>$HM_SCRIPT_LOGFILE 2>&1
 #        ;;
 # *)
	     # Do nothing
	#     ;;
 # esac

fi

