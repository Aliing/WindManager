#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -f $SHELL_HOME/ahCheckVMwareStatusVersion.sh ]; then
	$SHELL_HOME/ahCheckVMwareStatusVersion.sh > /dev/null 2>&1
	if [ $? -eq 0 ]; then
		echo "VMware Tools already installed."
		exit 0
	else
		has_mount=`fdisk -l /dev/cdrom 2> /dev/null | wc -l`
		if [ $has_mount -eq 0 ]; then
			echo "select_first"
			exit 1
		else
			if [ ! -d /media ]; then
				mkdir /media > /dev/null 2>&1
			fi

			/bin/umount -f /dev/cdrom > /dev/null 2>&1

			disk_mount=`df -ah|grep "/media"|wc -l`
			if [ $disk_mount -eq 0 ];then
				/bin/mount -t iso9660 /dev/cdrom /media > /dev/null 2>&1
				if [ $? -ne 0 ]; then
					echo "VMware Tools installation failed: Disk Error."
					/bin/umount -f /dev/cdrom > /dev/null 2>&1
					exit 1
				fi
			fi

			if [ ! -d /root/vmware-tools ]; then
				mkdir /root/vmware-tools > /dev/null 2>&1
			else
				/bin/rm	-rf /root/vmware-tools/* > /dev/null 2>&1
				/bin/rm	-f /root/vmware-tools/.newinstalled > /dev/null 2>&1
			fi

			/bin/tar -xf /media/VMwareTools*.tar.gz -C /root/vmware-tools > /dev/null 2>&1
			if [ $? -ne 0 ];then
				echo "VMware Tools installation failed: Extract Package Error."
				/bin/umount -f /dev/cdrom > /dev/null 2>&1
				exit 1
			else
				vmware_install=`find /root/vmware-tools -name "vmware-install.pl"`
				if [ $? -eq 0 -a -f "$vmware_install" ];then
					echo "Starting Install VMware Tools" >> $VMWARE_TOOL_LOGFILE 2>&1
					date >> $VMWARE_TOOL_LOGFILE 2>&1
					echo "-----------------------------------------------" >> $VMWARE_TOOL_LOGFILE 2>&1
					$vmware_install --default >> $VMWARE_TOOL_LOGFILE 2>&1
					if [ $? -ne 0 ]; then
						echo "VMware Tools installation failed: Install Script Execution Error."
						/bin/umount -f /dev/cdrom > /dev/null 2>&1
						exit 1
					else
						disk_mount=`df -ah|grep "/hivemap"|wc -l`
						if [ $disk_mount -eq 1 ];then
							if [ ! -d /hivemap/root/vmware-tools ];then
								mkdir -p /hivemap/root/vmware-tools > /dev/null 2>&1
							fi

							/bin/rm -rf /hivemap/root/vmware-tools/* > /dev/null 2>&1
							/bin/cp -rf /root/vmware-tools/* /hivemap/root/vmware-tools > /dev/null 2>&1

							/bin/rm -f /hivemap/root/vmware-tools/.uninstalled > /dev/null 2>&1
							touch /hivemap/root/vmware-tools/.newinstalled > /dev/null 2>&1
						fi
					fi
					echo "-----------------------------------------------" >> $VMWARE_TOOL_LOGFILE 2>&1
					date >> $VMWARE_TOOL_LOGFILE 2>&1
					echo "Install VMware Tools completed" >> $VMWARE_TOOL_LOGFILE 2>&1
				else
					echo "VMware Tools installation failed: Install Script Notfound."
					/bin/umount -f /dev/cdrom > /dev/null 2>&1
					exit 1
				fi
				/bin/umount -f /dev/cdrom > /dev/null 2>&1
			fi
		fi
	fi
	exit 0
else
	echo "Unsupported for install VMware Tools."
	exit 1
fi
