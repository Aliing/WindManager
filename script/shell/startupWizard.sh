# !/bin/bash
# trap SIGHUP,SIGINT,SIGQUIT,SIGTERM,SIGTSTP
trap "" 1 2 3 15 20

$JAVA_HOME/bin/java -Xrs -jar /HiveManager/shell/shell.jar

while [ $? != 0 ]
do 
  $JAVA_HOME/bin/java -Xrs -jar /HiveManager/shell/shell.jar
done