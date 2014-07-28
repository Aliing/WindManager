#!/bin/bash
. /HiveManager/script/shell/setHmEnv.sh

SRC_DIR=$HM_ROOT/WEB-INF/sign
DEST_DIR=$HM_ROOT/applets
KEYSTORE=$HM_ROOT/.keystore
KEYPASS=Aerohive

if [ ! -d $SRC_DIR ]
then
  echo "The source $SRC_DIR dir is not exist" >> $HM_SCRIPT_LOGFILE
  exit 1
fi

if [ ! -d $DEST_DIR ]
then
  mkdir -p $DEST_DIR
else
  rm -rf $DEST_DIR/*.jar
fi

KS_ALIAS=`$JAVA_HOME/bin/keytool -list -v -keystore $KEYSTORE -storepass $KEYPASS| sed -n 's/Alias name:[ ]*\(\)/\1/p'`

if [ -z $KS_ALIAS ]
then
  echo "There is not ALIAS in keystore" >> $HM_SCRIPT_LOGFILE
  exit 1
fi

##need loop for each jar file 
JARS=`ls $SRC_DIR/*.jar`

if [ $? != 0 ]
then
  echo "The source $SRC_DIR dir does not include jar files" >> $HM_SCRIPT_LOGFILE
  exit 1
fi

if [ ! -f $JAVA_HOME/bin/jarsigner ]
then
  HM_BIT=`getconf LONG_BIT`
  if [ $HM_BIT == 32 ]
  then
    /bin/cp -rf $SHELL_HOME/jarsigner_32 $JAVA_HOME/bin/jarsigner  >> $HM_SCRIPT_LOGFILE 2>&1
    chmod u+x $JAVA_HOME/bin/jarsigner
    /bin/cp -rf $SHELL_HOME/tools_32.jar $JAVA_HOME/lib/tools.jar >> $HM_SCRIPT_LOGFILE 2>&1
  else
    /bin/cp -rf $SHELL_HOME/jarsigner_64 $JAVA_HOME/bin/jarsigner  >> $HM_SCRIPT_LOGFILE 2>&1
    chmod u+x $JAVA_HOME/bin/jarsigner
    /bin/cp -rf $SHELL_HOME/tools_64.jar $JAVA_HOME/lib/tools.jar >> $HM_SCRIPT_LOGFILE 2>&1
  fi
fi

for jar in $JARS
do
  jar_file=`basename $jar`
  $JAVA_HOME/bin/jarsigner -sigfile shot -storepass $KEYPASS -signedjar $DEST_DIR/$jar_file  -keystore $KEYSTORE $SRC_DIR/$jar_file  $KS_ALIAS >> $HM_SCRIPT_LOGFILE 2>&1
done

exit 0