#!/bin/bash
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

$UPDATE_SHELL_HOME/ahCheckMemory.sh
if [ $? = 1 ]
then
	echo "Unable to upgrade HiveManager due to insufficient memory. Increase memory to at least 3 GB for a 32-bit HiveManager or 8 GB for a 64-bit HiveManager, and try again.&&Be sure to add sufficient memory to your HiveManager." > /dev/stdout
	exit 1
fi

$UPDATE_SHELL_HOME/ahCheckBit.sh
if [ $? = 1 ]
then
	echo "The image file bit code (32-bit or 64-bit) does not match that required by HiveManager. (Note: The '1U' in the image file name means the file is a 32-bit code file;'2U' means it is a 64-bit code file.)&&Check that the image file has the correct bit code for your HiveManager unit and try again." > /dev/stdout
	exit 1
fi

$UPDATE_SHELL_HOME/ahCheckDiskSize.sh

