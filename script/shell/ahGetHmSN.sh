#!/bin/bash

SN_NUM=`dmidecode -t 1|grep "Serial Number:"|cut -f 2 -d ":"`
if [ $? -eq 0 ]; then
	echo $SN_NUM >/dev/stdout 2>&1
else
	echo "" >/dev/stdout 2>&1
fi
