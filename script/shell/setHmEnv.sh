#!/bin/bash
#
# Note: This file must be linux/unix format
#
export CATALINA_HOME=/HiveManager/tomcat

APP_HOME=hm
export HM_ROOT=$CATALINA_HOME/webapps/$APP_HOME

HM_SCRIPT_LOGFILE=$CATALINA_HOME/logs/hm_script.log
VMWARE_TOOL_LOGFILE=$CATALINA_HOME/logs/vmware_tool.log
NMS_HOME=$CATALINA_HOME
PSQL_DATA=/var/lib/pgsql/data
SHELL_HOME=/HiveManager/script/shell
HA_SHELL_HOME=/HiveManager/ha/scripts
HM_BASEHOME=/HiveManager
UPDATE_HOME=$CATALINA_HOME/hm_soft_upgrade
UPDATE_SHELL_HOME=$UPDATE_HOME/HiveManager/script/shell
cd $NMS_HOME
