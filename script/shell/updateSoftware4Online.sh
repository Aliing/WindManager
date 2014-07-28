#!/bin/bash

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

chmod u+x $UPDATE_HOME/HiveManager/script/shell/*.sh

#if [ -f /opt/amigopod/www/_site/AerohiveLicense2.dat ]
#then
#	echo "update_in_progress" > /dev/stderr
#	exit 1
#fi

#create backup dir for software and db
HM_BACKUP_DIR=/HiveManager/soft_backup

if [ -d $HM_BACKUP_DIR ]
then
	rm -rf $HM_BACKUP_DIR >>$HM_SCRIPT_LOGFILE 2>&1
fi

if [ -d /HiveManager/downloads/home/hiveManagerImage ]
then
	rm -rf /HiveManager/downloads/home/hiveManagerImage/* >>$HM_SCRIPT_LOGFILE 2>&1
fi

mkdir -p $HM_BACKUP_DIR
cd $HM_BACKUP_DIR
#tar czf hm_soft_bak.tar.gz /HiveManager/* >>$HM_SCRIPT_LOGFILE 2>&1
/bin/cp -rf $HM_ROOT/WEB-INF/hmconf/hivemanager.ver ./  >>$HM_SCRIPT_LOGFILE 2>&1
/bin/cp -rf $HM_ROOT/domains ./ >>$HM_SCRIPT_LOGFILE 2>&1
/bin/cp -rf $HM_ROOT/res ./ >>$HM_SCRIPT_LOGFILE 2>&1
mkdir dbxmlfile >>$HM_SCRIPT_LOGFILE 2>&1
/bin/cp -rf $NMS_HOME/dbxmlfile/* ./dbxmlfile >>$HM_SCRIPT_LOGFILE 2>&1

cd $NMS_HOME

. $SHELL_HOME/setEnv.sh

VERSIONFILE=$HM_ROOT/WEB-INF/hmconf/hivemanager.ver
MAP_VERSIONFILE=$UPDATE_HOME/hm/WEB-INF/hmconf/hivemanager.ver

#PG_SERVER_VERSION=`rpm -qa | grep postgresql-server-8.4.2-1PGDG.rhel5`
#if [ ! -z $PG_SERVER_VERSION ]
#then
	#rpm -e --nodeps postgresql-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
	#rpm -e --nodeps postgresql-libs-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
	#rpm -e --nodeps postgresql-server-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
#fi

#if [ -f $UPDATE_HOME/aeronms_os_pack/etc/yum.conf.hivemanager ]
#then
#	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/yum.conf.hivemanager  /etc/yum.conf
#	/bin/cp -rf /etc/yum.repos.d /etc/yum.repos.d.bak >>$HM_SCRIPT_LOGFILE 2>&1
#	rm -rf /etc/yum.repos.d/* >>$HM_SCRIPT_LOGFILE 2>&1
#fi

#rm -rf $UPDATE_HOME/rpms/kernel* >>$HM_SCRIPT_LOGFILE 2>&1

#if [ $(ls -A $UPDATE_HOME/rpms/*.rpm | wc -l) != 0 ]
#then
#	yum_rpms=`ls $UPDATE_HOME/rpms/*.rpm`
#    for file in $yum_rpms
#    do
#    	install_rpms="$install_rpms $file"
#    done
#    yum -y localupdate $install_rpms >>$HM_SCRIPT_LOGFILE 2>&1
#    yum -y localinstall $install_rpms >>$HM_SCRIPT_LOGFILE 2>&1
#fi

#chkconfig --level 2345 tomcat off     >>$HM_SCRIPT_LOGFILE 2>&1

#if [ -f /var/lib/pgsql/data/PG_VERSION ]
#then
#	PG_VERSION=`cat /var/lib/pgsql/data/PG_VERSION`
#    if [ $PG_VERSION != 9.1 ]
#    then
#    	rm -rf  /var/lib/pgsql/data  >>$HM_SCRIPT_LOGFILE 2>&1
#    fi
#fi

#if [ -d $UPDATE_HOME/rpms/ha ]
#then
#    if [ $(ls -A $UPDATE_HOME/rpms/ha/*.rpm | wc -l) != 0 ]
#    then
#    	yum_rpms=`ls $UPDATE_HOME/rpms/ha/*.rpm`
#        for file in $yum_rpms
#        do
#        	install_has="$install_has $file"
#        done
#        yum -y localupdate  $install_has >>$HM_SCRIPT_LOGFILE 2>&1
#        yum -y localinstall $install_has >>$HM_SCRIPT_LOGFILE 2>&1
#    fi
#fi

#touch /root/upgrade_file

#/bin/cp -rf $UPDATE_HOME/aeronms_os_pack/root/*  /root
#chmod u+x /root/*.sh
#/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/rc.d/init.d/aerohive-init /etc/rc.d/init.d/aerohive-init
#chmod u+x /etc/rc.d/init.d/aerohive-init
#/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/rc.local.hivemanager /etc/rc.local
#/bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/aerohive.pp  /root/aerohive.pp
#/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/ntp.conf.hivemanager  /etc/ntp.conf
#/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/iptables.hivemanager  /etc/sysconfig/iptables
#/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/iptables.hivemanager  /etc/sysconfig/iptables.hivemanager

## Fix 20117 You can SFTP/SCP into Hivemanager appliance 
if [ $(sed -n '/^[[:space:]]*Subsystem/'p /etc/ssh/sshd_config | wc -l) != 0 ]
then
	sed -i -e '/Subsystem/ s/^/#/' /etc/ssh/sshd_config
	#Restart sshd service to enable the change
	if [ -f $HM_SCRIPT_LOGFILE ]
	then
		date >> $HM_SCRIPT_LOGFILE
		service sshd restart >> $HM_SCRIPT_LOGFILE
	fi
fi

if [ $(sed -n '/^[[:space:]]*Subsystem/'p /hivemap/etc/ssh/sshd_config | wc -l) != 0 ]
then
	sed -i -e '/Subsystem/ s/^/#/' /hivemap/etc/ssh/sshd_config
fi
## End

#if [ -f $UPDATE_HOME/aeronms_os_pack/root/.bashrc.hivemanager ]
#then
#	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/.bashrc.hivemanager /root/.bashrc
#fi

#if [ -f $UPDATE_HOME/aeronms_os_pack/root/.pgpass ]
#then
#	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/.pgpass /root/.pgpass
#	chmod 600 /root/.pgpass
#fi

#if [ -f $HM_SHELL_HOME/server.xml.hivemanager ]
#then
#	/bin/cp -rf $HM_SHELL_HOME/server.xml.hivemanager $CATALINA_HOME/conf/server.xml
#fi

#if [ -f $HM_SHELL_HOME/web.xml.hivemanager  ]
#then
#	/bin/cp -rf $HM_SHELL_HOME/web.xml.hivemanager /HiveManager/tomcat/conf/web.xml
#fi

#if [ -f $UPDATE_HOME/aeronms_os_pack/root/pg_hba.conf.hivemanager ]
#then
#	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/pg_hba.conf.hivemanager /root/pg_hba.conf.hivemanager
#fi

#if [ -f $UPDATE_HOME/aeronms_os_pack/etc/selinux/config.hivemanager ]
#then
#	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/selinux/config.hivemanager /etc/selinux/config
#fi

#POST91=/etc/rc.d/init.d/postgresql-9.1
#if [ -f $POST91 ]
#then
#	cd /etc/rc.d/init.d/
#	ln -s postgresql-9.1 postgresql
#	cd $NMS_HOME
#fi

#chkconfig --level 2345 aerohive-init on >>$HM_SCRIPT_LOGFILE 2>&1
#chkconfig --level 2345 postgresql on  >>$HM_SCRIPT_LOGFILE 2>&1

#cp -rf /HiveManager/tomcat/hm_soft_upgrade/root/postgresql-9.1.hivemanager /etc/rc.d/init.d/postgresql-9.1

DATE=`date +%F%R`
SRC_MAINVERSION=`cat $VERSIONFILE | sed -n 's/.*MAINVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
SRC_SUBVERSION=`cat $VERSIONFILE | sed -n 's/.*SUBVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
SRC_BUILDTIME=`cat $VERSIONFILE | sed -n 's/.*BUILDTIME=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
DST_MAINVERSION=`cat $MAP_VERSIONFILE | sed -n 's/.*MAINVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
DST_SUBVERSION=`cat $MAP_VERSIONFILE | sed -n 's/.*SUBVERSION=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
DST_BUILDTIME=`cat $MAP_VERSIONFILE | sed -n 's/.*BUILDTIME=\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/p'`
RECORD="$DATE, version `echo $SRC_MAINVERSION`r`echo $SRC_SUBVERSION` $SRC_BUILDTIME upgraded to version `echo $DST_MAINVERSION`r`echo $DST_SUBVERSION` $DST_BUILDTIME"

echo $RECORD >> /HiveManager/update_history.log

if [ ! -f $NMS_HOME/conf/uploadfile.xml ]
then
	/bin/cp -rf  /HiveManager/script/shell/uploadfile.xml $NMS_HOME/conf
fi

echo "stop hivemanage..."
. /HiveManager/script/shell/stopHiveManage.sh

sleep 20

echo "copy files..."
. /HiveManager/script/shell/updateSingleSoftware.sh

sleep 10

echo "start hivemanage..."
. /HiveManager/script/shell/startHiveManage.sh

echo "upgrade complete!"
