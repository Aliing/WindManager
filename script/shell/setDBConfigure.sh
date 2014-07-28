#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -z $1 ]
then
	echo "need the param DB memory size"
	exit 1
fi

DB_CONFIGURE_FILE=$2
DB_POSTGRESQL_FILE=$3

if [ -z $2 ]
then
	DB_CONFIGURE_FILE=$HM_ROOT/WEB-INF/hmconf/dbconfigure.conf
fi

if [ -z $3 ]
then
	DB_POSTGRESQL_FILE=/var/lib/pgsql/9.1/data/postgresql.conf
fi

DB_MEMORY_SIZE=$1
((WORK_MEMORY_SIZE=$DB_MEMORY_SIZE/4))

if [ $DB_MEMORY_SIZE -lt 512 ]; then
	echo "memory size should not less than 512"
	exit 1
fi

#calculate configure item by memory size
shared_buffers=256
temp_buffers=16
work_mem=16
maintenance_work_mem=128
effective_cache_size=128
checkpoint_segments=5

#modify db configure file
sed -i "s/\(DB_MEMORY=\).*/\1$DB_MEMORY_SIZE/" $DB_CONFIGURE_FILE
sed -i "s/\(DB_WORK_MEMORY=\).*/\1$WORK_MEMORY_SIZE/" $DB_CONFIGURE_FILE


multiple=1

((multiple=($DB_MEMORY_SIZE)/256))

if [ $multiple -gt 100 ] ; then
	multiple=100
elif [ $multiple -lt 1 ] ; then
	multiple=1
fi

((shared_buffers=$DB_MEMORY_SIZE/4))
((temp_buffers=16+4*$multiple))
((work_mem=8+$multiple))
((maintenance_work_mem=64+16*$multiple))
((effective_cache_size=128+$multiple*4))
((checkpoint_segments=5+$multiple))


#modify hibernate cache configure file
sed -i "s/[#]\{0,1\}\(shared_buffers\)[ \t]\{0,\}=[ \t]\{0,\}[0-9]\{0,\}\(.*\)/\1 = ${shared_buffers}\2/" ${DB_POSTGRESQL_FILE}
sed -i "s/[#]\{0,1\}\(temp_buffers\)[ \t]\{0,\}=[ \t]\{0,\}[0-9]\{0,\}\(.*\)/\1 = ${temp_buffers}\2/" ${DB_POSTGRESQL_FILE}
sed -i "s/[#]\{0,1\}\(work_mem\)[ \t]\{0,\}=[ \t]\{0,\}[0-9]\{0,\}\(.*\)/\1 = ${work_mem}\2/" ${DB_POSTGRESQL_FILE}
sed -i "s/[#]\{0,1\}\(maintenance_work_mem\)[ \t]\{0,\}=[ \t]\{0,\}[0-9]\{0,\}\(.*\)/\1 = ${maintenance_work_mem}\2/" ${DB_POSTGRESQL_FILE}
sed -i "s/[#]\{0,1\}\(effective_cache_size\)[ \t]\{0,\}=[ \t]\{0,\}[0-9]\{0,\}\(.*\)/\1 = ${effective_cache_size}\2/" ${DB_POSTGRESQL_FILE}
sed -i "s/[#]\{0,1\}\(checkpoint_segments\)[ \t]\{0,\}=[ \t]\{0,\}[0-9]\{0,\}\(.*\)/\1 = ${checkpoint_segments}\2/" ${DB_POSTGRESQL_FILE}

exit 0
