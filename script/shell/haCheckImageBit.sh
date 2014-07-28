#! /bin/bash
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

rm -rf $UPDATE_HOME/"$1"

if [ -f $UPDATE_HOME/hm/WEB-INF/hmconf/hivemanager.ver ]
then
	MODEL=`cat $UPDATE_HOME/hm/WEB-INF/hmconf/hivemanager.ver | sed -n 's/.*MACHINETYPE=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

	if [ -z $MODEL ]
	then
		echo "update_in_progress" > /dev/stderr
		exit 1
	fi

	MOCHINE_MODE=`getconf LONG_BIT`

	if [ $MODEL != $MOCHINE_MODE ]
	then
		echo "update_in_progress" > /dev/stderr
		exit 1
	fi
fi

chmod u+x $UPDATE_SHELL_HOME/*.sh
