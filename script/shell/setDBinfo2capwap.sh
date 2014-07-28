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

sed -i "s/DB_HOST=.*/DB_HOST=${HOST}/" $CONF_FILE
sed -i "s/DB_PORT=.*/DB_PORT=${PORT}/" $CONF_FILE
sed -i "s/DB_NAME=.*/DB_NAME=${DB}/" $CONF_FILE
sed -i "s/DB_USERNAME=.*/DB_USERNAME=${USER}/" $CONF_FILE
sed -i "s/DB_PASSWORD=.*/DB_PASSWORD=${PASSWD}/" $CONF_FILE


