##this file to makesure if the tftp service on or off

. /HiveManager/script/shell/setHmEnv.sh

if [ ! -f /etc/xinetd.d/tftp ] 
then
   echo "1" >/dev/stdout
   exit 1
fi

less /etc/xinetd.d/tftp | grep disable | grep no >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? != 0 ]
then
   echo "1" >/dev/stdout
   exit 1
else
   echo "0" >/dev/stdout
   exit 0
fi