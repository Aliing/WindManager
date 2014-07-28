#!/bin/bash
# cchen@aerohive.com
#
# set postgresql memory size according to system total memory
. /HiveManager/script/shell/setHmEnv.sh

DB_CONFIGURE_FILE=$HM_ROOT/WEB-INF/hmconf/dbconfigure.conf
DB_CONFIGURE_DEFAULT_FILE=$HM_ROOT/WEB-INF/hmconf/dbconfigure.conf.default
if [ ! -f $DB_CONFIGURE_FILE ] || [ ! -f $DB_CONFIGURE_DEFAULT_FILE ] || [ ! -f /root/.auto_db_memory ]; then
	exit 0
fi

#db_memory=`cat $DB_CONFIGURE_FILE | grep DB_MEMORY | sed -n "s/.*DB_MEMORY=\(.*\)/\1/p" `
#db_memory_default=`cat $DB_CONFIGURE_DEFAULT_FILE | grep DB_MEMORY | sed -n "s/.*DB_MEMORY=\(.*\)/\1/p" `

# default value of memory size for postgresql
pc_mode=`getconf LONG_BIT`
min_size=1024
if [ $pc_mode == 64 ]; then
	min_size=2048
fi

total_memory=`free -m|grep "Mem:"|awk '{print $2}'`
#if [ $db_memory == $db_memory_default ] && [ $total_memory -gt $min_size ]; then
if [ $total_memory -gt $min_size ]; then
	((memory_size=$total_memory/2))
	sed -i "s/\(DB_MEMORY=\).*/\1$memory_size/" $DB_CONFIGURE_FILE > /dev/null 2>&1
	sed -i "s/\(DB_MEMORY=\).*/\1$memory_size/" $DB_CONFIGURE_DEFAULT_FILE > /dev/null 2>&1
	cd $SHELL_HOME
    if [ -f setDBConfigure.sh ]
    then
        if [ -f /var/lib/pgsql/9.1/data/postgresql.conf ]
        then
    	    ./setDBConfigure.sh $memory_size  >>$HM_SCRIPT_LOGFILE 2>&1
        fi
    fi
fi
