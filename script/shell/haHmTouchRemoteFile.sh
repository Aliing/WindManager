#!/bin/bash
##$1 remote ip
##$2 file path

ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no $1 touch $2
if [ #? = 1 ]
then
	 /HiveManager/script/shell/haHmTouchRemoteFile.sh
fi