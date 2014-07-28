#!/bin/bash
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "restore config.ini begin..." >> $HM_SCRIPT_LOGFILE 2>&1
src_file=/HiveManager/tomcat/webapps/hm/config.ini
des_file=/HiveManager/tomcat/hm_soft_upgrade/hm/config.ini

myhive=`cat $src_file | grep myhive_url=`
redirector=`cat $src_file | grep redirector_url=`
idm=`cat $src_file | grep idm_url=`
portal=`cat $src_file | grep portal_url=`
as=`cat $src_file | grep as_url=`

dsenable=`cat $src_file | grep ds_enable=`
dsserver=`cat $src_file | grep ds_server=`
simulatorenable=`cat $src_file | grep simulator_enable=`
testenv=`cat $src_file | grep test_env=`

oem=`cat $src_file | grep oem=`

##replace "/" to "\/", replace "." to "\."
echo $myhive | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
echo $redirector | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
echo $idm | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
echo $portal | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
echo $as | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp

echo $dsenable >> ./url_tmp
echo $dsserver | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
echo $simulatorenable >> ./url_tmp
echo $testenv >> ./url_tmp

echo $oem >> ./url_tmp

myhive_url=`cat url_tmp | grep myhive_url`
redirector_url=`cat url_tmp | grep redirector_url`
idm_url=`cat url_tmp | grep idm_url`
portal=`cat url_tmp | grep portal_url`
as=`cat url_tmp | grep as_url`

ds_enable=`cat url_tmp | grep ds_enable`
ds_server=`cat url_tmp | grep ds_server`
simulator_enable=`cat url_tmp | grep simulator_enable`
test_env=`cat url_tmp | grep test_env`

oem=`cat url_tmp | grep oem`

/bin/rm -f ./url_tmp 

if [ ! -z $myhive_url ]
then
    sed -i "s/myhive_url=.*/${myhive_url}/" $des_file
fi
if [ ! -z $redirector_url ]
then
    sed -i "s/redirector_url=.*/${redirector_url}/" $des_file
fi
if [ ! -z $idm_url ]
then
	sed -i "s/idm_url=.*/${idm_url}/" $des_file
fi
if [ ! -z $portal_url ]
then
	sed -i "s/portal_url=.*/${portal_url}/" $des_file
fi
if [ ! -z $as_url ]
then
	sed -i "s/as_url=.*/${as_url}/" $des_file
fi

if [ ! -z $ds_enable ]
then
	sed -i "s/ds_enable=.*/${ds_enable}/" $des_file
fi
if [ ! -z $ds_server ]
then
	sed -i "s/ds_server=.*/${ds_server}/" $des_file
fi
if [ ! -z $simulator_enable ]
then
	sed -i "s/simulator_enable=.*/${simulator_enable}/" $des_file
fi
if [ ! -z $test_env ]
then
	sed -i "s/test_env=.*/${test_env}/" $des_file
fi
if [ ! -z "$oem" ]
then
	sed -i "s/oem=.*/${oem}/" $des_file
fi
echo "restore config.ini end..." >> $HM_SCRIPT_LOGFILE 2>&1