#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

rm -rf /etc/syslog-ng/syslog-ng.conf

touch /etc/syslog-ng/syslog-ng.conf

############echo the content of syslog-ng.conf
echo "#remote" >> /etc/syslog-ng/syslog-ng.conf
echo ""  >> /etc/syslog-ng/syslog-ng.conf
echo "options { long_hostnames(off); sync(0); };"  >> /etc/syslog-ng/syslog-ng.conf
echo "options { stats(18000); };"  >> /etc/syslog-ng/syslog-ng.conf
echo ""  >> /etc/syslog-ng/syslog-ng.conf
echo "source s_slog { unix-stream("/dev/log"); internal(); };"  >> /etc/syslog-ng/syslog-ng.conf
echo "source s_remote { udp(); };"  >> /etc/syslog-ng/syslog-ng.conf
echo ""  >> /etc/syslog-ng/syslog-ng.conf
echo "destination d_local_msg { file("/var/log/messages" create_dirs(yes)); };"  >> /etc/syslog-ng/syslog-ng.conf
echo "destination d_remote_msg { file("/remote/hiveos.log" owner("root") group("root") perm(0640) dir_perm(0750) create_dirs(yes)); };"  >> /etc/syslog-ng/syslog-ng.conf
echo ""  >> /etc/syslog-ng/syslog-ng.conf
echo "filter f_l_all { level(debug..emerg); };"  >> /etc/syslog-ng/syslog-ng.conf
echo ""  >> /etc/syslog-ng/syslog-ng.conf
echo $@  >> /etc/syslog-ng/syslog-ng.conf
echo ""  >> /etc/syslog-ng/syslog-ng.conf	       
echo ""  >> /etc/syslog-ng/syslog-ng.conf
echo "log { source(s_slog); filter(f_l_all); destination(d_local_msg); };"  >> /etc/syslog-ng/syslog-ng.conf
echo "log { source(s_remote); filter(f_net); destination(d_remote_msg); };"  >> /etc/syslog-ng/syslog-ng.conf
############over

sleep 2

service syslog-ng restart &