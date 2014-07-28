
. /HiveManager/script/shell/setHmEnv.sh

cd $UPDATE_HOME

GM_UPDATE_FILE=`ls guestmanager-*`

if [ -z $GM_UPDATE_FILE ]
then
    echo "1"
    exit 1
else
    echo "0"
    exit 0
fi