#!/bin/bash
#$1:is the dest dir
  if [ -z "$1" ]
  then
    echo "param dest need"
    exit 1
  fi

  GM_APP_HOME=/var/www/html/gm
  
  if [ ! -f /opt/amigopod/www/_site/AerohiveLicense2.dat ] 
  then
    exit 0
  fi
  
  if [ ! -f $GM_APP_HOME/admin_backup_cli.php ]
  then
    echo "not have gm backup php file"
    exit 1
  fi
  
  php $GM_APP_HOME/admin_backup_cli.php "$1"