#!/bin/bash
### this file for call the check image sh
### and put some result to the java can get!
### if have error then it will clear the garbage

# Copyright 2008 Aerohive Networks, Inc.

. /HiveManager/script/shell/setHmEnv.sh

if [ -z "$1" ] 
then
	echo "1" >/dev/stdout
	exit 1
fi

SIGN_HOME=/etc/image_signing

if [ ! -f $SIGN_HOME/rsapublickey.pem ]
then
    echo "1" >/dev/stdout
    exit 1
fi

DOWNLOAD_HOME=$HM_ROOT/WEB-INF/downloads

IMG_TMP=image_tmp

rm -rf $IMG_TMP >>$HM_SCRIPT_LOGFILE 2>&1
mkdir  $IMG_TMP >>$HM_SCRIPT_LOGFILE 2>&1

/bin/cp -rf $DOWNLOAD_HOME/"$1" $IMG_TMP >>$HM_SCRIPT_LOGFILE 2>&1
rm -rf $DOWNLOAD_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1

##the image name after verify
FINAL_IMAGE=final.tar.gz

cd $IMG_TMP >>$HM_SCRIPT_LOGFILE 2>&1

chmod u+x $SIGN_HOME/check_image.sh >>$HM_SCRIPT_LOGFILE 2>&1

$SIGN_HOME/check_image.sh "$1" $SIGN_HOME/rsapublickey.pem $FINAL_IMAGE >>$HM_SCRIPT_LOGFILE 2>&1

if [ $? = 0 ]
then
  /bin/cp -rf $FINAL_IMAGE $DOWNLOAD_HOME/"$1" >>$HM_SCRIPT_LOGFILE 2>&1
  
  rm -rf $NMS_HOME/$IMG_TMP >>$HM_SCRIPT_LOGFILE 2>&1

  echo "0" >/dev/stdout
  exit 0
else
  rm -rf $NMS_HOME/$IMG_TMP >>$HM_SCRIPT_LOGFILE 2>&1
   
  echo "1" >/dev/stdout
  exit 1
fi

