#!/bin/bash
. /HiveManager/script/shell/setHmEnv.sh

dec_file=$NMS_HOME/tmp/dbxmlfile/hivemanager.ver
src_file=$NMS_HOME/webapps/"$APP_HOME"/WEB-INF/hmconf/hivemanager.ver

SRC_MAINVERSION=`cat $src_file | sed -n 's/.*MAINVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
SRC_SUBVERSION=`cat $src_file | sed -n 's/.*SUBVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

DES_MAINVERSION=`cat $dec_file | sed -n 's/.*MAINVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
DES_SUBVERSION=`cat $dec_file | sed -n 's/.*SUBVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

if [ "$SRC_MAINVERSION" != "$DES_MAINVERSION" -o "$SRC_SUBVERSION" != "$DES_SUBVERSION" ]
then
	echo "restore_in_progress" > /dev/stderr
fi