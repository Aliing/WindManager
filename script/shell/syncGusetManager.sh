#!/bin/bash

if [ -f /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh ] 
then
  . /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh
else
  if [ -f /HiveManager/script/shell/setHmEnv.sh ] 
  then
    . /HiveManager/script/shell/setHmEnv.sh
  fi
fi
  if [ -f /var/www/html/gm/_site/AerohiveLicense.dat ] 
  then
    /bin/cp -f /var/www/html/gm/_site/AerohiveLicense.dat /hivemap//var/www/html/gm/_site/AerohiveLicense.dat
  fi 
  
  if [ -f /var/www/html/gm/_site/AerohiveLicense2.dat ]
  then
    /bin/cp -f /var/www/html/gm/_site/AerohiveLicense2.dat /hivemap//var/www/html/gm/_site/AerohiveLicense2.dat
  fi 
  
  rm -rf /hivemap/gm_data
  mkdir /hivemap/gm_data
   
  $SHELL_HOME/gm_backup.sh /hivemap/gm_data/gm_backup.dat