#!/bin/bash
. /HiveManager/script/shell/setHmEnv.sh

TUNNEL_HOME=/HiveManager/capwap/hm_tunnel
PRODUCT_TYPE_FILE=/etc/sysconfig/product_type.conf
HTTPD_FILE=/etc/httpd/conf/httpd.conf
SHELL_HOME=/HiveManager/script/shell

if [ ! -f $SHELL_HOME/httpd.conf.tunnel ] || [ ! -f $SHELL_HOME/ssl.conf.tunnel ]
then
  exit 0
fi

if [ -f /etc/sysconfig/product_type.conf ]
then
  product_type=`cat /etc/sysconfig/product_type.conf`

  if [ $product_type == HM_GM ]
  then
    /bin/cp -rf $SHELL_HOME/httpd.conf.tunnel /etc/httpd/conf/httpd.conf >>$HM_SCRIPT_LOGFILE 2>&1
    /bin/cp -rf $SHELL_HOME/ssl.conf.tunnel /etc/httpd/conf.d/ssl.conf >>$HM_SCRIPT_LOGFILE 2>&1
    if [ -f /etc/httpd/conf.d/php.conf ]; then
    	rm -rf /etc/httpd/conf.d/php.conf
    fi
    #service httpd restart >>$HM_SCRIPT_LOGFILE 2>&1
    rm -rf $SHELL_HOME/httpd.conf.tunnel
    rm -rf $SHELL_HOME/ssl.conf.tunnel
  fi
else
  if [ -f /etc/httpd/conf/httpd.conf ]
  then
    /bin/cp -rf $SHELL_HOME/httpd.conf.tunnel /etc/httpd/conf/httpd.conf >>$HM_SCRIPT_LOGFILE 2>&1
    /bin/cp -rf $SHELL_HOME/ssl.conf.tunnel /etc/httpd/conf.d/ssl.conf >>$HM_SCRIPT_LOGFILE 2>&1
  	if [ -f /etc/httpd/conf.d/php.conf ]; then
    	rm -rf /etc/httpd/conf.d/php.conf
    fi
    #service httpd restart >>$HM_SCRIPT_LOGFILE 2>&1
    rm -rf $SHELL_HOME/httpd.conf.tunnel
    rm -rf $SHELL_HOME/ssl.conf.tunnel
  fi
fi
