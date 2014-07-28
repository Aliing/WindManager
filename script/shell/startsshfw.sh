#!/bin/bash
#create .ssh directory
if [ -d /root/.ssh ]; then
   echo ".ssh is existed"
else
   mkdir /root/.ssh
   chmod 700 /root/.ssh
fi

#cp key from ssh_host_rsa
/bin/cp -f /etc/ssh/ssh_host_rsa_key /root/.ssh/identity
/bin/cp -f /etc/ssh/ssh_host_rsa_key.pub /root/.ssh/authorized_keys

#add 127.0.0.1 key in known_hosts
if [  -f /root/.ssh/known_hosts ]; then
    sed -e '/127.0.0.1/d' /root/.ssh/known_hosts >/tmp/known_hosts
    /bin/cp -f /tmp/known_hosts /root/.ssh/known_hosts
fi

ssh-keyscan -t rsa 127.0.0.1 >>/root/.ssh/known_hosts

# start openSSH forwarding
ssh -CfNg -D 14444 -l admin 127.0.0.1
if [ $? -eq 0 ]; then
   echo "Start openSSH forwarding successful"
   exit 0
else
   echo "Start openSSH forwarding failed"
   exit -1
fi
