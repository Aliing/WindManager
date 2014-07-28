#!/bin/bash

tools_status=""
count=`chkconfig --list|grep vmware-tools|wc -l`
if [ $count -ne 0 ]; then
	tools_status="Installed"

	service vmware-tools status > /dev/null 2>&1
	if [ $? -eq 0 ]; then
		tools_status=$tools_status"+Running"
	else
		tools_status=$tools_status"+Not Running"
	fi

	uninstall_tools=`which vmware-uninstall-tools.pl 2> /dev/null`
	if [ $? -eq 0 -a -f "$uninstall_tools" ];then
		version_count=`grep '^  .buildNr = ' $uninstall_tools|wc -l`
		if [ $version_count -eq 1 ]; then
			version_string=`grep '^  .buildNr = ' $uninstall_tools`
			_header=${version_string#*\'}
			version=${_header%%\'*}
			if [ "$1" == "version" ]; then
				tools_status=$version
			else
				tools_status=$tools_status"\n"$version
			fi
		elif [ "$1" == "version" ]; then
			tools_status=""
		fi
	fi

	echo -e $tools_status
	exit 0
else
	tools_status="Not Installed"
	if [ "$1" == "version" ]; then
		tools_status=""
	fi
	echo -e $tools_status
	exit 1
fi

