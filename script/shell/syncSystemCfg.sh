#!/bin/bash
#########
#sync system config

if [ -f /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh ] 
then
  . /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh
else
  if [ -f /HiveManager/script/shell/setHmEnv.sh ] 
  then
    . /HiveManager/script/shell/setHmEnv.sh
  fi
fi

echo "syncSystemCfg start..." >>$HM_SCRIPT_LOGFILE

/bin/cp -rf /etc/hosts /hivemap/etc/hosts
/bin/cp -rf /etc/sysconfig/network /hivemap/etc/sysconfig/network
/bin/cp -rf /etc/sysconfig/network-scripts/ifcfg-eth0 /hivemap/etc/sysconfig/network-scripts/ifcfg-eth0
  
/bin/cp -rf /etc/ssh/sshd_config /hivemap/etc/ssh/sshd_config  
  
if [ -f /etc/sysconfig/network-scripts/ifcfg-eth1 ] 
then
  /bin/cp -rf /etc/sysconfig/network-scripts/ifcfg-eth1 /hivemap/etc/sysconfig/network-scripts/ifcfg-eth1
fi
  
if [ -f /etc/resolv.conf ] 
then
  /bin/cp -rf   /etc/resolv.conf  /hivemap/etc/resolv.conf
fi
    
if [ -f /etc/sysconfig/static-routes ]
then
  /bin/cp -rf /etc/sysconfig/static-routes /hivemap/etc/sysconfig/static-routes
fi  

if [ -d /etc/syslog-ng  ]
then
    /bin/cp -rf /etc/syslog-ng  /hivemap/etc
fi

if [ -d /remote ]
then
    /bin/cp -rf /remote /hivemap
fi
   
/bin/cp -rf /etc/sysconfig/clock   /hivemap/etc/sysconfig/clock

TIMEZONE=`cat /hivemap/etc/sysconfig/clock | sed -n 's/.*ZONE="\([^ ][-A-Za-z0-9\/_\+]*\).*/\1/p'`

ln -sf /usr/share/zoneinfo/$TIMEZONE /hivemap/etc/localtime

if [ -f /etc/sysconfig/.system-config-timer-activation ]
then 
  /bin/cp -rf /etc/sysconfig/.system-config-timer-activation /hivemap/etc/sysconfig/.system-config-timer-activation
else
  if [ -f /hivemap/etc/sysconfig/.system-config-timer-activation ]
  then
    rm -rf /hivemap/etc/sysconfig/.system-config-timer-activation
  fi
fi

##sync key files
/bin/cp -rf /etc/ssh/ssh_host_dsa_key /hivemap/etc/ssh/ssh_host_dsa_key
/bin/cp -rf /etc/ssh/ssh_host_dsa_key.pub /hivemap/etc/ssh/ssh_host_dsa_key.pub
/bin/cp -rf /etc/ssh/ssh_host_key /hivemap/etc/ssh/ssh_host_key
/bin/cp -rf /etc/ssh/ssh_host_key.pub /hivemap/etc/ssh/ssh_host_key.pub
/bin/cp -rf /etc/ssh/ssh_host_rsa_key /hivemap/etc/ssh/ssh_host_rsa_key
/bin/cp -rf /etc/ssh/ssh_host_rsa_key.pub /hivemap/etc/ssh/ssh_host_rsa_key.pub


echo "syncSystemCfg end." >>$HM_SCRIPT_LOGFILE
