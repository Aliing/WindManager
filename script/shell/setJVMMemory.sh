#!/bin/bash
# cchen@aerohive.com
#
# set JVM memory size according to system total memory
. /HiveManager/script/shell/setHmEnv.sh

JVMOPTION_FILE=$HM_ROOT/WEB-INF/hmconf/jvmoption.conf
JVMOPTION_DEFAULT_FILE=$HM_ROOT/WEB-INF/hmconf/jvmoption.conf.default
if [ ! -f $JVMOPTION_FILE ] || [ ! -f $JVMOPTION_DEFAULT_FILE ] || [ ! -f /root/.auto_jvm_memory ]; then
	exit 0
fi

# ignore hmol
app_type=hm
if [ -f $HM_ROOT/config.ini ]
then
	app_type=`cat $HM_ROOT/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`
fi
if [ $app_type == "hhm" -o $app_type == "HHM" -o $app_type == "Hhm" ]; then
	exit 0
fi

#max_memory=`cat $JVMOPTION_FILE | grep MAX_MEMORY | sed -n "s/.*MAX_MEMORY=\(.*\)/\1/p"`
#max_memory_default=`cat $JVMOPTION_DEFAULT_FILE | grep MAX_MEMORY | sed -n "s/.*MAX_MEMORY=\(.*\)/\1/p"`

# default value of memory size for JVM
pc_mode=`getconf LONG_BIT`
min_size=1366
if [ $pc_mode == 64 ]; then
	min_size=2731
fi

total_memory=`free -m|grep "Mem:"|awk '{print $2}'`
#if [ $max_memory == $max_memory_default ] && [ $total_memory -gt $min_size ]; then
if [ $total_memory -gt $min_size ]; then
	((memory_size=$total_memory*3/8))
	sed -i "s/\(MAX_MEMORY=\).*/\1$memory_size/" $JVMOPTION_FILE > /dev/null 2>&1
	sed -i "s/\(MAX_MEMORY=\).*/\1$memory_size/" $JVMOPTION_DEFAULT_FILE > /dev/null 2>&1
	cd $SHELL_HOME
    if [ -f setJVMOption.sh ]
    then
       ./setJVMOption.sh $memory_size  >>$HM_SCRIPT_LOGFILE 2>&1
    fi
fi