#!/bin/bash
###now delete the public key file for verify sign image
### then it wii use could not sign image to update

. /HiveManager/script/shell/setHmEnv.sh

if [ -f /etc/image_signing/rsapublickey.pem ]
then
  rm -rf /etc/image_signing/rsapublickey.pem
fi  