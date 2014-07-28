#!/bin/bash
#$1:host
#$2:port
#$3:db
#$4:user
#$5:password
#$6:capwap file

HOST="$1"
PORT="$2"
DB="$3"
USER="$4"
PASSWD="$5"
CONF_FILE="$6"
URL_PREFIX=jdbc:postgresql:

sed -i "s/<value>${URL_PREFIX}\/\/.*<\/value>/<value>${URL_PREFIX}\/\/${HOST}:${PORT}\/${DB}<\/value>/" $CONF_FILE

sed -i "/<property name=\"username\">/{n;s/<value>.*<\/value>/<value>${USER}<\/value>/;}" $CONF_FILE
sed -i "/<property name=\"password\">/{n;s/<value>.*<\/value>/<value>${PASSWD}<\/value>/;}" $CONF_FILE