# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

JAVA_HOME=/usr/java/latest

LIB_HOME=$HM_ROOT/WEB-INF/lib

JARS=`ls $LIB_HOME/*.jar`

COMMON_PATH=.:$HM_ROOT/WEB-INF/classes

for jar in $JARS
do
  COMMON_PATH=$COMMON_PATH:$jar
done

CLASSPATH=$COMMON_PATH

HIBERNATEPATH=$COMMON_PATH

RESTOREPATH=$COMMON_PATH