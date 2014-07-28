#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

. $SHELL_HOME/setEnv.sh

echo ""
echo "check start, please waiting for a monment..."

##create log file
if [ ! -d $NMS_HOME/restorechecklogs ]
then
	mkdir $NMS_HOME/restorechecklogs
fi
if [ $# = 1 ]
then
	LOG_FILE=$NMS_HOME/restorechecklogs/$1.txt
else
	LOG_FILE=$NMS_HOME/restorechecklogs/checkAfterRestore.txt
fi
if [ -f $LOG_FILE ]
then
	rm -rf $LOG_FILE >>$HM_SCRIPT_LOGFILE 2>&1
fi
echo "" > $LOG_FILE

XMLFILE_TMP=$NMS_HOME/xmltmp

if [ -d $XMLFILE_TMP ]
then
  rm -rf $XMLFILE_TMP >>$HM_SCRIPT_LOGFILE 2>&1
fi

mkdir $XMLFILE_TMP >>$HM_SCRIPT_LOGFILE 2>&1

for file in $NMS_HOME/dbxmlfile/*
do
	if [ -d $file ]
	then
		/bin/cp  $file/* $XMLFILE_TMP >>$HM_SCRIPT_LOGFILE 2>&1
	fi
done

cd $NMS_HOME

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.restoredb.CheckAfterRestore $XMLFILE_TMP $LOG_FILE

sleep 5

rm -rf $XMLFILE_TMP >>$HM_SCRIPT_LOGFILE 2>&1

echo ""
echo "check end, log file : $LOG_FILE"
echo ""