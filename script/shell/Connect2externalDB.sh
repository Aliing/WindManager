#!/bin/bash
#
# Copyright (C) 2006-2012 Aerohive Networks
#
# yZhong@AeroHive.com
#
##

. $(dirname "$0")/shellfuncs

copyRemoteDB LocalHost $1

configureDB $1
restartHM
chkconfig postgresql off
# stopHiveManage.sh mandates PostGreSQL running   stopLocalDB
