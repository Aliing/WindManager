#!/bin/bash
#$1:is the src dir
  if [ -z "$1" ]
  then
    echo "param src need"
    exit 1
  fi

  GM_APP_HOME=/var/www/html/gm
  
  if [ ! -f $GM_APP_HOME/admin_restore_cli.php ]
  then
    echo "not have gm restore php file"
    exit 1
  fi
  
  php $GM_APP_HOME/admin_restore_cli.php "$1"
  
  sleep 3
  
  service amigopod-runtime  restart >/dev/null 2>&1