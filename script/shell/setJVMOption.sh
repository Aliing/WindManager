#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

if [ -z $1 ]
then
	echo "need the param JVM startup memory size"
	exit 1
fi

JVMOPTION_FILE=$2
CACHE_FILE=$3

if [ -z $2 ]
then
	JVMOPTION_FILE=$HM_ROOT/WEB-INF/hmconf/jvmoption.conf
fi

if [ -z $3 ]
then
	CACHE_FILE=$HM_ROOT/WEB-INF/classes/ehcache.xml
fi

MEMORY_SIZE=$1

if [ $MEMORY_SIZE -lt 512 ]; then
	echo "memory size should not less than 512"
	exit 1
fi

#calculate JVM max perm size
PERM_SIZE=128
if [ $MEMORY_SIZE -lt 600 ]; then
        PERM_SIZE=256
else
        PERM_SIZE=512
fi
#modify JVM option configure file
sed -i "s/\(MAX_MEMORY=\).*/\1$MEMORY_SIZE/" $JVMOPTION_FILE
sed -i "s/\(MAX_PERM_MEMORY=\).*/\1$PERM_SIZE/" $JVMOPTION_FILE

#calculate max element size of hibernate cache 
ELEMENT_SIZE=10000

if [ $MEMORY_SIZE -lt 600 ]; then
	ELEMENT_SIZE=20000
elif [ $MEMORY_SIZE -lt 1200 ]; then
	ELEMENT_SIZE=50000
elif [ $MEMORY_SIZE -lt 2400 ]; then
	ELEMENT_SIZE=200000
elif [ $MEMORY_SIZE -lt 3600 ]; then
        ELEMENT_SIZE=300000
else
	ELEMENT_SIZE=400000
fi

#modify hibernate cache configure file
sed -i "s/\(maxElementsInMemory=\).*/\1\"$ELEMENT_SIZE\"/" $CACHE_FILE

exit 0
