#!/bin/bash
#
# Copyright (C) 2006-2012 Aerohive Networks
#
# yZhong@AeroHive.com
#
##

. $(dirname "$0")/shellfuncs

startLocalDB
copyDB $1 LocalHost

configureDB LocalHost
restartHM
ssh $1 'chkconfig postgresql off'
# stopHiveManage.sh mandates PostGreSQL running   stopDB $1