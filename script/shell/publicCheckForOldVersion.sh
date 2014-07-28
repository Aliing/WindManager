
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "upgrade: public check for old version start..." >>$HM_SCRIPT_LOGFILE 2>&1

#check version  
src_file=$NMS_HOME/webapps/"$APP_HOME"/WEB-INF/hmconf/hivemanager.ver

SRC_MAINVERSION=`cat $src_file | sed -n 's/.*MAINVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
SRC_SUBVERSION=`cat $src_file | sed -n 's/.*SUBVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`

if (echo ${SRC_MAINVERSION} 6.1 | awk '!($1<$2){exit 1}')
 then
		##check memory
	$UPDATE_SHELL_HOME/ahCheckMemory.sh
	if [ $? = 1 ]
	then
		echo "[ERROR] ahCheckMemory.sh: check memory error" >>$HM_SCRIPT_LOGFILE 2>&1
	    exit 1
	fi
	
	##check bit
	$UPDATE_SHELL_HOME/ahCheckBit.sh
	if [ $? = 1 ]
	then
		echo "[ERROR] ahCheckBit.sh: check bit error" >>$HM_SCRIPT_LOGFILE 2>&1
		exit 1
	fi
	
	##check disk size
	$UPDATE_SHELL_HOME/ahCheckDiskSize.sh
	if [ $? = 1 ]
	then
		echo "[ERROR] ahCheckDiskSize.sh: check disk size error" >>$HM_SCRIPT_LOGFILE 2>&1
		exit 1
	fi
fi

if (echo ${SRC_MAINVERSION} 6.1 | awk '!($1=$2){exit 1}')
then 
 if (echo ${SRC_SUBVERSION} 2 | awk '!($1<$2){exit 1}')
	then
			##check memory
			$UPDATE_SHELL_HOME/ahCheckMemory.sh
			if [ $? = 1 ]
			then
				echo "[ERROR] ahCheckMemory.sh: check memory error" >>$HM_SCRIPT_LOGFILE 2>&1
			    exit 1
			fi
			
			##check bit
			$UPDATE_SHELL_HOME/ahCheckBit.sh
			if [ $? = 1 ]
			then
				echo "[ERROR] ahCheckBit.sh: check bit error" >>$HM_SCRIPT_LOGFILE 2>&1
				exit 1
			fi
			
			##check disk size
			$UPDATE_SHELL_HOME/ahCheckDiskSize.sh
			if [ $? = 1 ]
			then
				echo "[ERROR] ahCheckDiskSize.sh: check disk size error" >>$HM_SCRIPT_LOGFILE 2>&1
				exit 1
			fi
 fi 
fi
	echo "upgrade: public check for old version end." >>$HM_SCRIPT_LOGFILE 2>&1
    exit 0


