#!/bin/bash
#$1:host
#$2:port
#$3:db
#$4:user
#$5:password
#$6:cfg confile
#$7:pro confile

HOST="$1"
PORT="$2"
DB="$3"
USER="$4"
PASSWD="$5"
CFG_FILE="$6"
PRO_FILE="$7"

URL_PREFIX=jdbc:postgresql:
USER_PREFIX=hm.connection.username
PASS_PREFIX=hm.connection.password
sed -i "s/${URL_PREFIX}\/\/.*/${URL_PREFIX}\/\/${HOST}:${PORT}\/${DB}/" $CFG_FILE
sed -i "s/${USER_PREFIX}=.*/${USER_PREFIX}=${USER}/" $PRO_FILE
sed -i "s/${PASS_PREFIX}=.*/${PASS_PREFIX}=${PASSWD}/" $PRO_FILE
