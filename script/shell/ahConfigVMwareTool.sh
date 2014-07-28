#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -f /root/vmware-tools/.newinstalled ]; then
	/bin/rm -rf /root/vmware-tools/.newinstalled > /dev/null 2>&1

	tools_version=`$SHELL_HOME/ahCheckVMwareStatusVersion.sh version 2> /dev/null`
	need_install=n
	if [ $? -eq 0 ];then
		install_tools=`find /root/vmware-tools -name "vmware-install.pl" 2> /dev/null`
		if [ $? -eq 0 -a -f "$install_tools" ];then
			version_count=`grep '^  .buildNr = ' $install_tools|wc -l`
			if [ $version_count -eq 1 ]; then
				version_string=`grep '^  .buildNr = ' $install_tools`
				_header=${version_string#*\'}
				version=${_header%%\'*}
				if [ -n "$version" ] && [ "$version" != "$tools_version" ]; then
					need_install=y
				fi
			fi
		fi
	else
		need_install=y
	fi

	if [ "$need_install" == "y" ];then
		vmware_install=`find /root/vmware-tools -name "vmware-install.pl" 2> /dev/null`
		if [ $? -eq 0 -a -f "$vmware_install" ];then
			echo "Starting Install VMware Tools (Auto)" >> $VMWARE_TOOL_LOGFILE 2>&1
			date >> $VMWARE_TOOL_LOGFILE 2>&1
			echo "-----------------------------------------------" >> $VMWARE_TOOL_LOGFILE 2>&1
			$vmware_install --default >> $VMWARE_TOOL_LOGFILE 2>&1
			if [ $? -ne 0 ]; then
				echo "VMware Tools installation failed: Install Script Execution Error."
				exit 1
			fi
			echo "-----------------------------------------------" >> $VMWARE_TOOL_LOGFILE 2>&1
			date >> $VMWARE_TOOL_LOGFILE 2>&1
			echo "Install VMware Tools completed (Auto)" >> $VMWARE_TOOL_LOGFILE 2>&1
			exit 0
		else
			echo "VMware Tools installation failed (Auto): Install Script Notfound."
			exit 1
		fi
	fi
fi

if [ -f /root/vmware-tools/.uninstalled ]; then
	/bin/rm -rf /root/vmware-tools/.uninstalled > /dev/null 2>&1

	uninstall_tools=`which vmware-uninstall-tools.pl 2> /dev/null`
	if [ $? -eq 0 -a -f "$uninstall_tools" ];then
		echo "Starting Uninstall VMware Tools (Auto)" >> $VMWARE_TOOL_LOGFILE 2>&1
		date >> $VMWARE_TOOL_LOGFILE 2>&1
		echo "-----------------------------------------------" >> $VMWARE_TOOL_LOGFILE 2>&1
		$uninstall_tools --default >> $VMWARE_TOOL_LOGFILE 2>&1
		if [ $? -ne 0 ]; then
			echo "VMware Tools uninstallation failed: Uninstall Script Execution Error."
			exit 1
		fi
		echo "-----------------------------------------------" >> $VMWARE_TOOL_LOGFILE 2>&1
		date >> $VMWARE_TOOL_LOGFILE 2>&1
		echo "Uninstall VMware Tools completed (Auto)" >> $VMWARE_TOOL_LOGFILE 2>&1
	else
		echo "VMware Tools uninstallation failed (Auto): Uninstall Script Notfound."
		exit 1
	fi
fi