#!/bin/bash
#this file only use in HA mode, call by c program

. /HiveManager/script/shell/setHmEnv.sh

UPDATE_INDEX_FILE=hm_soft_upgrade/updateSoftware.sh
PARAM_LIST_FILE=update_param_list.txt
UPDATE_FILE=update_fiel_tmp.tar.gz

if [ -f $UPDATE_HOME/$PARAM_LIST_FILE ]
then
	FILES=`cat $UPDATE_HOME/$PARAM_LIST_FILE`
	
	for file in $FILES
	do
	    UPDATE_OPT="$UPDATE_OPT $file"
	done	
else
  UPDATE_OPT=0
fi

cd $NMS_HOME

if [ -d /hivemap ]
then
    touch /hivemap/HiveManager/tomcat/ha2nodes
fi

./$UPDATE_INDEX_FILE $UPDATE_FILE $UPDATE_OPT >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
  exit 1
else
  exit 0
fi