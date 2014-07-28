#!/bin/sh

if [ -f /HiveManager/PGPool/script/getNodeStatus.sh -a -f /HiveManager/ha/scripts/get_master_ip.sh -a $# = 1 ]
then
    cd /HiveManager/PGPool/script
    ./getNodeStatus.sh > getNodeStatus
    ACTIVE_DB=`grep master getNodeStatus | awk {'print $2'}`
    PASSIVE_DB=`grep -v master getNodeStatus | awk {'print $2'}`
    /bin/rm -f getNodeStatus

    
	if [ $1 = "dbpassive" ]
	then
		echo "$PASSIVE_DB" > /dev/stdout
		exit 0
	fi

	if [ $1 = "dbactive" ]
	then
		echo "$ACTIVE_DB" > /dev/stdout
		exit 0
	fi

	if [ $1 = "apppassive" ]
	then
		cd /HiveManager/ha/scripts
		
		./get_slave_ip.sh
	    if [ $? = 0 ]
	    then
	    	SECONDARY_IP=`./get_slave_ip.sh`
	    else
	        echo "update_in_progress" > /dev/stderr
	        exit 1
	    fi
	    
		echo "$SECONDARY_IP" > /dev/stdout
		exit 0
	fi

	if [ $1 = "appactive" ]
	then
		cd /HiveManager/ha/scripts
		
	    ./get_master_ip.sh
	    if [ $? = 0 ]
	    then
	        PRIMARY_IP=`./get_master_ip.sh`
	    else
	        echo "update_in_progress" > /dev/stderr
	        exit 1
	    fi
	    
		echo "$PRIMARY_IP" > /dev/stdout
		exit 0
	fi

else
    echo "update_in_progress" > /dev/stderr
    exit 1
fi