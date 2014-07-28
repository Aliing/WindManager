#!/bin/bash


TRANSFER_DIR=/HiveManager/tomcat/hm_soft_upgrade
ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no $1 rm -rf $TRANSFER_DIR
ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no $1 mkdir -p $TRANSFER_DIR


if [ $? != 0 ]
then
    echo "update_in_progress" > /dev/stderr
    exit 1
fi

scp -r -o ConnectTimeout=3 -o PasswordAuthentication=no -o IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no /HiveManager/tomcat/hm_soft_upgrade/ $1:/HiveManager/tomcat/


if [ $? != 0 ]
then
    echo "update_in_progress" > /dev/stderr
    exit 1
else
    exit 0
fi