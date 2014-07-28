#!/bin/bash
#this is use for unison update files
#$1 is remote_root
LOCAL_ROOT="/HiveManager/tomcat/hm_soft_upgrade"
UNISON_OPT="-contactquietly -auto -silent -batch -force"

rm -rf $LOCAL_ROOT
mkdir $LOCAL_ROOT

touch $LOCAL_ROOT/unison.mak

export UNISON=/tmp

rm -rf /tmp/ar*

/usr/bin/unison "$1" $LOCAL_ROOT $UNISON_OPT "$1"