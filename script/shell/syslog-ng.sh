
. /HiveManager/script/shell/setHmEnv.sh

if [ -f /remote/hiveos.log ]
then
  SIZE=`wc -c /remote/hiveos.log | awk '{print $1}'`  
  if [ $SIZE -gt 100000000 ]
  then
     logrotate -f /etc/logrotate.d/syslog-ng >>$HM_SCRIPT_LOGFILE
  fi
fi
