#!/bin/bash
#because our Hivemanager have much memory so we want to set some parameters bigger,
#then HM performance will be enhancement.

. /HiveManager/script/shell/setHmEnv.sh

if [ -f /root/.is_field_hmol ]
then
  exit 0
fi

CONF_HOME=$HM_ROOT/WEB-INF/hmconf

DB_CONF_HOME=/var/lib/pgsql/9.1/data

HM_BIT=`getconf LONG_BIT`

if [ $HM_BIT == 32 ]
then
  if [ -f $CONF_HOME/postgresql_1U.conf ]
  then
    /bin/cp -f $CONF_HOME/postgresql_1U.conf $DB_CONF_HOME/postgresql.conf
  fi
else
  if [ -f $CONF_HOME/postgresql_2U.conf ]
  then
    /bin/cp -f $CONF_HOME/postgresql_2U.conf $DB_CONF_HOME/postgresql.conf
  fi
fi

#set db memory, just upgrade, not install
if [ "$1" != "" ]; then
	if [ -f /hivemap$HM_ROOT/WEB-INF/hmconf/dbconfigure.conf ]
	then
		db_memory=`cat /hivemap$HM_ROOT/WEB-INF/hmconf/dbconfigure.conf | grep DB_MEMORY | sed -n "s/.*DB_MEMORY=\(.*\)/\1/p" `
	    if [ ! -z $db_memory ]
	    then
		    cd $SHELL_HOME
	        if [ -f setDBConfigure.sh ]
	        then
	            if [ -f /var/lib/pgsql/9.1/data/postgresql.conf ]
	            then
	        	    ./setDBConfigure.sh $db_memory  >>$HM_SCRIPT_LOGFILE 2>&1
	            fi
	        fi
	    fi
	fi
else
	if [ -f $SHELL_HOME/confPostgresMem.sh ]; then
		# for upgrade use
		exist_flag=0
		if [ -f /hivemap/root/.auto_db_memory ]; then
			$exist_flag=1
		fi
		touch /root/.auto_db_memory

		$SHELL_HOME/confPostgresMem.sh

		if [ $exist_flag == 0 ]; then
			/bin/rm -f /root/.auto_db_memory
		fi
	fi
fi