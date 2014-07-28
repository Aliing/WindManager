#!/bin/bash
#
# Copyright (C) 2006-2012 Aerohive Networks
#
# yZhong@AeroHive.com
#
##
. /HiveManager/script/shell/setHmEnv.sh
. $(dirname "$0")/shellfuncs

ssh $1 'cd /HiveManager/PGPool/script/master && ./addIPv4AuthUser.sh ${SSH_CLIENT/ *} && service postgresql status || service postgresql start'
hostJDBC
echo $HOST $1 >>$HM_SCRIPT_LOGFILE 2>&1
copyPostGreSQL $HOST $1 hivemanager hm >>$HM_SCRIPT_LOGFILE 2>&1
