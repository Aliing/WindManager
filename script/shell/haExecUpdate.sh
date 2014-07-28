#! /bin/bash
##$1 image name
##$2 update type
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

##check memory
$UPDATE_SHELL_HOME/ahCheckMemory.sh
if [ $? = 1 ]
then
	echo "update_in_progress" > /dev/stderr
    exit 1
fi

if [ $# -ge 1 ]
then

    #if [ -d /hivemap ]
    #then

    #else
         . $UPDATE_SHELL_HOME/haExecUpdateO.sh

         if [ $? != 0 ]
         then
         	exit 1
         else
            exit 0
         fi
    #fi
else
    echo "update_in_progress" > /dev/stderr
    exit 1
fi