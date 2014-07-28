#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

setFlag="f"

if [ $(sed -n '/^[[:space:]]*GSSAPIAuthentication/'p /etc/ssh/sshd_config | wc -l) != 0 ]
then
	sed -i -e '/GSSAPIAuthentication/ s/^/#/' /etc/ssh/sshd_config
	setFlag="t"
fi

if [ $(sed -n '/^[[:space:]]*GSSAPICleanupCredentials/'p /etc/ssh/sshd_config | wc -l) != 0 ]
then
	sed -i -e '/GSSAPICleanupCredentials/ s/^/#/' /etc/ssh/sshd_config
	setFlag="t"
fi

if [ "$setFlag" == "t" ]; then
	date >> $HM_SCRIPT_LOGFILE
	service sshd reload >> $HM_SCRIPT_LOGFILE
fi