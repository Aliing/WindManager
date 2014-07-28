#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -f $SHELL_HOME/ahCheckVMwareStatusVersion.sh ]; then
	$SHELL_HOME/ahCheckVMwareStatusVersion.sh > /dev/null 2>&1
	if [ $? -ne 0 ]; then
		echo "VMware Tools not installed."
		exit 0
	else
		/bin/umount -f /dev/cdrom > /dev/null 2>&1

		uninstall_tools=`which vmware-uninstall-tools.pl 2> /dev/null`
		if [ $? -eq 0 -a -f "$uninstall_tools" ];then
			echo "Starting Uninstall VMware Tools" >> $VMWARE_TOOL_LOGFILE 2>&1
			date >> $VMWARE_TOOL_LOGFILE 2>&1
			echo "-----------------------------------------------" >> $VMWARE_TOOL_LOGFILE 2>&1
			$uninstall_tools --default >> $VMWARE_TOOL_LOGFILE 2>&1
			if [ $? -ne 0 ]; then
				echo "VMware Tools uninstallation failed: Uninstall Script Execution Error."
				exit 1
			else
				disk_mount=`df -ah|grep "/hivemap"|wc -l`
				if [ $disk_mount -eq 1 ];then
					if [ ! -d /hivemap/root/vmware-tools ];then
						mkdir -p /hivemap/root/vmware-tools > /dev/null 2>&1
					fi
					/bin/rm -f /hivemap/root/vmware-tools/.newinstalled > /dev/null 2>&1
					touch /hivemap/root/vmware-tools/.uninstalled > /dev/null 2>&1
				fi
			fi
			echo "-----------------------------------------------" >> $VMWARE_TOOL_LOGFILE 2>&1
			date >> $VMWARE_TOOL_LOGFILE 2>&1
			echo "Uninstall VMware Tools completed" >> $VMWARE_TOOL_LOGFILE 2>&1
		else
			echo "VMware Tools uninstallation failed: Uninstall Script Notfound."
			exit 1
		fi
	fi
	exit 0
else
	echo "Unsupported for Uninstall VMware Tools."
	exit 1
fi
