#!/bin/bash
#the sh is for switch the partition.

. /HiveManager/script/shell/setHmEnv.sh

/bin/cp -rf /boot/grub/grub.conf /boot/grub/grub.conf.bak >>$HM_SCRIPT_LOGFILE 2>&1

RESULT=`grep hivemap /proc/cmdline`

if [ $? = 0 ]; then
  sed -e 's/LABEL=\/hivemap/LABEL=\/1/g'  /boot/grub/grub.conf.bak > /boot/grub/grub.conf
else
  sed -e 's/LABEL=\/1/LABEL=\/hivemap/g'  /boot/grub/grub.conf.bak > /boot/grub/grub.conf  
fi;

/bin/cp -f /boot/grub/grub.conf /hivemap/boot/grub/grub.conf >>$HM_SCRIPT_LOGFILE 2>&1
   
rm -rf /boot/grub/grub.conf.bak >>$HM_SCRIPT_LOGFILE 2>&1

$SHELL_HOME/syncSystemCfg.sh

##$SHELL_HOME/syncGusetManager.sh

reboot 