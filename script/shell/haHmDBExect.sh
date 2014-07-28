#!/bin/bash

chmod u+x /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/*.sh

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "copy files and install rpms..." >> $HM_SCRIPT_LOGFILE
## set LM_SENSORS_VERSION
LM_SENSORS_VERSIONS=`chroot /hivemap rpm -qa | grep lm_sensors`
LM_SENSORS_VERSION=lm_sensors-2.10.0-3.1

for VERSION in $LM_SENSORS_VERSIONS
do
	if [ $VERSION == lm_sensors-2.10.6-1.fc8 ]
	then
		LM_SENSORS_VERSION=$VERSION
	fi
done

RPMS_HOME=$UPDATE_HOME/rpms
if [ $LM_SENSORS_VERSION != lm_sensors-2.10.6-1.fc8 ]
then
	cd $RPMS_HOME
	LM_SENSORS_FILE=`ls lm_sensors-*.rpm`
	if [ ! -f $RPMS_HOME/$LM_SENSORS_FILE ] || [ ! -f $UPDATE_HOME/aeronms_os_pack/etc/sensors.conf.hivemanager.grantsdale ] || [ ! -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/lm_sensors.hivemanager.grantsdale ] || [ ! -f $UPDATE_HOME/aeronms_os_pack/etc/sensors.conf.hivemanager.x7dbn ] || [ ! -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/lm_sensors.hivemanager.x7dbn ] || [ ! -f $UPDATE_HOME/aeronms_os_pack/root/w83793.ko ]
	then
		echo "update_in_progress" > /dev/stderr
		exit 1
	fi
fi

##create tmp folder for rpms
HIVEMAP_TMP=/hivemap/hivetmp
rm -rf $HIVEMAP_TMP
mkdir $HIVEMAP_TMP
mkdir $HIVEMAP_TMP/yum_rpms

##check pg_pool
pg_pool_num=`chroot /hivemap rpm -qa | grep pgpool | wc -l`

/bin/cp -rf $RPMS_HOME/*.rpm $HIVEMAP_TMP/yum_rpms
/bin/cp -rf $RPMS_HOME/ha $HIVEMAP_TMP

if [ $LM_SENSORS_VERSION != lm_sensors-2.10.6-1.fc8 ]
then
	cp -rf $RPMS_HOME/$LM_SENSORS_FILE $HIVEMAP_TMP
	rpm -e --allmatches $LM_SENSORS_VERSION
	chroot /hivemap rpm -e --allmatches $LM_SENSORS_VERSION
	rpm -Uvh --replacefiles $RPMS_HOME/$LM_SENSORS_FILE >>$HM_SCRIPT_LOGFILE 2>&1
	chroot /hivemap rpm -Uvh --replacefiles /hivetmp/$LM_SENSORS_FILE >>$HM_SCRIPT_LOGFILE 2>&1

	MB=`dmidecode -t 2 | grep "Product Name" | sed -e 's/.*: //g'`
	case "$MB" in
		Grantsdale)
		cp -f $UPDATE_HOME/aeronms_os_pack/etc/sensors.conf.hivemanager.grantsdale /etc/sensors.conf
		cp -f $UPDATE_HOME/aeronms_os_pack/etc/sensors.conf.hivemanager.grantsdale /hivemap/etc/sensors.conf
		cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/lm_sensors.hivemanager.grantsdale /etc/sysconfig/lm_sensors
		cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/lm_sensors.hivemanager.grantsdale /hivemap/etc/sysconfig/lm_sensors
		chkconfig --level 2345 lm_sensors on
		chroot /hivemap chkconfig --level 2345 lm_sensors on
		;;

		X7DBN)
		# Add support for w83793 chipset
		chmod u+x $UPDATE_HOME/aeronms_os_pack/root/w83793.ko
		versions=`rpm -q kernel | sed -e "s/kernel-//g"`
		for version in $versions; do
			cp $UPDATE_HOME/aeronms_os_pack/root/w83793.ko /lib/modules/$version/kernel/drivers/hwmon/
			cp $UPDATE_HOME/aeronms_os_pack/root/w83793.ko /hivemap/lib/modules/$version/kernel/drivers/hwmon/
			depmod -a $version
			chroot /hivemap depmod -a $version
		done
		cp -f $UPDATE_HOME/aeronms_os_pack/etc/sensors.conf.hivemanager.x7dbn /etc/sensors.conf
		cp -f $UPDATE_HOME/aeronms_os_pack/etc/sensors.conf.hivemanager.x7dbn /hivemap/etc/sensors.conf
		cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/lm_sensors.hivemanager.x7dbn /etc/sysconfig/lm_sensors
		cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/lm_sensors.hivemanager.x7dbn /hivemap/etc/sysconfig/lm_sensors
		chkconfig --level 2345 lm_sensors on
		chroot /hivemap chkconfig --level 2345 lm_sensors on
		;;

		*)
		rm -f $NMS_HOME/sysconfig/lm_sensors.hivemanager*
		rm -f $NMS_HOME/sensors.conf.hivemanager*
		# Turn off lm_sensors for non-recognized motherboards (i.e. VMWare).
		chkconfig lm_sensors off
		chroot /hivemap chkconfig --level 2345 lm_sensors off
	esac
fi

##remove postgresql 8.4
PG_SERVER_VERSION=`chroot /hivemap rpm -qa | grep postgresql-server-8.4.2-1PGDG.rhel5`
if [ ! -z $PG_SERVER_VERSION ]
then
	chroot /hivemap rpm -e --nodeps postgresql-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
	chroot /hivemap rpm -e --nodeps postgresql-libs-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
	chroot /hivemap rpm -e --nodeps postgresql-server-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
fi

 AEROHIVE_LICENSE_VERSION=`chroot /hivemap rpm -qa | grep aerohive_lic`
if [ -z $AEROHIVE_LICENSE_VERSION ] ||  [ $AEROHIVE_LICENSE_VERSION != aerohive_lic-0.6r1-1 ]
then
	cp -rf $RPMS_HOME/$AEROHIVE_LICENSE_FILE $HIVEMAP_TMP
    if [ -z $AEROHIVE_LICENSE_VERSION ]
    then
    	rm -rf /hivemap/HiveManager/license/*
    else
        chroot /hivemap rpm -e --noscripts $AEROHIVE_LICENSE_VERSION >>$HM_SCRIPT_LOGFILE 2>&1
        rm -rf /hivemap/HiveManager/license/*
    fi
    chroot /hivemap rpm -Uvh --replacefiles /hivetmp/$AEROHIVE_LICENSE_FILE >>$HM_SCRIPT_LOGFILE 2>&1
fi

if [ -f /hivemap/var/lib/pgsql/data/PG_VERSION ]
then
	PG_VERSION=`cat /hivemap/var/lib/pgsql/data/PG_VERSION`
	if [ $PG_VERSION != 9.1 ]
	then
		rm -rf  /hivemap/var/lib/pgsql/date
	fi
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/etc/yum.conf.hivemanager ]
then
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/yum.conf.hivemanager /hivemap/etc/yum.conf
fi
/bin/cp -rf /hivemap/etc/yum.repos.d /hivemap/etc/yum.repos.d.bak >>$HM_SCRIPT_LOGFILE 2>&1
rm -rf /hivemap/etc/yum.repos.d/* >>$HM_SCRIPT_LOGFILE 2>&1
rm -rf $HIVEMAP_TMP/yum_rpms/kernel* >>$HM_SCRIPT_LOGFILE 2>&1

#yum hm and os rpms
if [ $(ls -A $HIVEMAP_TMP/yum_rpms | wc -l) != 0 ]
then
	yum_rpms=`ls $HIVEMAP_TMP/yum_rpms`
    for file in $yum_rpms
    do
       if [ $file == 'httpd-2.4.3-1.x86_64.rpm' -o $file == 'mod_ssl-2.4.3-1.x86_64.rpm' -o $file == 'httpd-2.4.3-1.i386.rpm' -o $file == 'mod_ssl-2.4.3-1.i386.rpm' ]; then
    		rpm_install="$rpm_install /hivetmp/yum_rpms/$file"
    		continue
    	fi
    	install_rpms="$install_rpms /hivetmp/yum_rpms/$file"
    done
    if [ "$rpm_install" != "" ]; then
    	chroot /hivemap rpm -e --nodeps mod_ssl-2.2.3-11.el5_1.centos.3 >>$HM_SCRIPT_LOGFILE 2>&1
    	chroot /hivemap rpm -Uvh --nodeps $rpm_install >>$HM_SCRIPT_LOGFILE 2>&1
    fi
    chroot /hivemap chkconfig --level 2345 httpd off >>$HM_SCRIPT_LOGFILE 2>&1
    chroot /hivemap yum -y localupdate $install_rpms >>$HM_SCRIPT_LOGFILE 2>&1
    chroot /hivemap yum -y localinstall $install_rpms >>$HM_SCRIPT_LOGFILE 2>&1
fi

#yum ha rpms
if [ $(ls -A $HIVEMAP_TMP/ha | wc -l) != 0 ]
then
	yum_rpms=`ls $HIVEMAP_TMP/ha`
    for file in $yum_rpms
    do
    	install_has="$install_has /hivetmp/ha/$file"
    done
    chroot /hivemap yum -y localupdate  $install_has >>$HM_SCRIPT_LOGFILE 2>&1
    chroot /hivemap yum -y localinstall $install_has >>$HM_SCRIPT_LOGFILE 2>&1
fi

#restore config.ini
. $UPDATE_SHELL_HOME/haChangeConfig.sh

##OEM sign file
##/bin/cp -f $UPDATE_HOME/hm/oemfiles/* /hivemap/hivetmp
##chroot /hivemap /bin/cp -f /hivetmp/jarsigner $JAVA_HOME/bin
##chroot /hivemap /bin/cp -f /hivetmp/tools.jar $JAVA_HOME/lib
##chroot /hivemap chmod u+x $JAVA_HOME/bin/jarsigner
#chroot /hivemap chmod u+x $JAVA_HOME/lib/tools.jar
##/bin/rm -f /hivemap/hivetmp/jarsigner
##/bin/rm -f /hivemap/hivetmp/tools.jar

. $UPDATE_SHELL_HOME/oemRestoreUploadFiles.sh

##copy sys files
/bin/cp -rf $UPDATE_HOME/aeronms_os_pack/root/*  /hivemap/root
chmod u+x /hivemap/root/*.sh
/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/rc.d/init.d/aerohive-init /hivemap/etc/rc.d/init.d/aerohive-init
chmod u+x /hivemap/etc/rc.d/init.d/aerohive-init
/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/rc.local.hivemanager /hivemap/etc/rc.local
/bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/aerohive.pp  /hivemap/root/aerohive.pp
/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/ntp.conf.hivemanager  /hivemap/etc/ntp.conf
/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/iptables.hivemanager  /hivemap/etc/sysconfig/iptables
/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/iptables.hivemanager  /hivemap/etc/sysconfig/iptables.hivemanager
/bin/cp -rf $UPDATE_HOME/HiveManager/* /hivemap/HiveManager
chmod u+x /hivemap/HiveManager/script/shell/*.sh
chmod u+x /hivemap/HiveManager/ha/*.sh
chmod u+x /hivemap/HiveManager/ha/script/*.sh
chmod u+x /hivemap/HiveManager/PGPool/*.sh
chmod u+x /hivemap/HiveManager/PGPool/script/*.sh

## Fix 20117 You can SFTP/SCP into Hivemanager appliance 
if [ $(sed -n '/^[[:space:]]*Subsystem/'p /hivemap/etc/ssh/sshd_config | wc -l) != 0 ]
then
	sed -i -e '/Subsystem/ s/^/#/' /hivemap/etc/ssh/sshd_config
fi
## End
if [ -f $UPDATE_HOME/aeronms_os_pack/root/.bashrc.hivemanager ]
then
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/.bashrc.hivemanager /hivemap/root/.bashrc
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/root/.pgpass ]
then
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/.pgpass /hivemap/root/.pgpass
	chmod 600 /hivemap/root/.pgpass
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/etc/selinux/config.hivemanager ]
then
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/selinux/config.hivemanager /hivemap/etc/selinux/config
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/etc/hosts.hivemanager ]
then
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/hosts.hivemanager  /etc/hosts.hivemanager
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/hosts.hivemanager  /hivemap/etc/hosts.hivemanager
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/etc/resolv.conf.hivemanager ]
then
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/resolv.conf.hivemanager  /etc/resolv.conf.hivemanager
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/resolv.conf.hivemanager  /hivemap/etc/resolv.conf.hivemanager
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/network.hivemanager ]
then
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/network.hivemanager  /etc/sysconfig/network.hivemanager
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/network.hivemanager  /hivemap/etc/sysconfig/network.hivemanager
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/network-scripts/hivemanager.ifcfg-eth0 ]
then
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/network-scripts/hivemanager.ifcfg-eth0   /etc/sysconfig/network-scripts/hivemanager.ifcfg-eth0
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/network-scripts/hivemanager.ifcfg-eth0   /hivemap/etc/sysconfig/network-scripts/hivemanager.ifcfg-eth0
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/network-scripts/hivemanager.ifcfg-eth1 ]
then
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/network-scripts/hivemanager.ifcfg-eth1   /etc/sysconfig/network-scripts/hivemanager.ifcfg-eth1
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/network-scripts/hivemanager.ifcfg-eth1   /hivemap/etc/sysconfig/network-scripts/hivemanager.ifcfg-eth1
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/etc/logd.cf.hivemanager ]
then
	if [ -f /hivemap/var/www/html/gm/_templates/ha/static/etc_logd.cf ]
	then
		/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/logd.cf.hivemanager /hivemap/var/www/html/gm/_templates/ha/static/etc_logd.cf
	fi
	if [ -f /hivemap/var/www/html/gm/_plugins/highavailability-2.9.5/_templates/ha/static/etc_logd.cf ]
	then
		/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/logd.cf.hivemanager /hivemap/var/www/html/gm/_plugins/highavailability-2.9.5/_templates/ha/static/etc_logd.cf
	fi
fi

if [ -f /hivemap/etc/logrotate.d/syslog-ng.hivemanager ]
then
	rm -rf /hivemap/etc/logrotate.d/syslog-ng.hivemanager
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/etc/logrotate.d/syslog-ng.hivemanager ]
then
	/bin/cp -rf $UPDATE_HOME/aeronms_os_pack/etc/logrotate.d/syslog-ng.hivemanager /hivemap/etc/logrotate.d/syslog-ng
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/etc/logrotate.d/hm_script ]
then
	/bin/cp -rf $UPDATE_HOME/aeronms_os_pack/etc/logrotate.d/hm_script /hivemap/etc/logrotate.d/hm_script
fi
##for new 1U
MB=`dmidecode -t 2 | grep "Product Name" | sed -e 's/.*: //g'`
if [ "$MB" = "Grantsdale" -o "$MB" = "X7DBN" -o "$MB" = "MAHOBAY" ]
then
	if [ -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/lm_sensors.hivemanager.FW-8758 ]
	then
		if [ ! -d /hivemap/etc/sysconfig ]
		then
			mkdir /hivemap/etc/sysconfig
		fi
		/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/lm_sensors.hivemanager.FW-8758 /hivemap/etc/sysconfig/lm_sensors
	fi	
	if [ -f $UPDATE_HOME/aeronms_os_pack/etc/sensors.conf.hivemanager.FW-8758 ]
	then
		/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sensors.conf.hivemanager.FW-8758 /hivemap/etc/sensors.conf
	fi
	if [ -f $UPDATE_HOME/aeronms_os_pack/etc/modprobe.d/w83627dhg.conf ]
	then
		if [ ! -d /hivemap/etc/modprobe.d ]
		then
			mkdir /hivemap/etc/modprobe.d
		fi
		/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/modprobe.d/w83627dhg.conf /hivemap/etc/modprobe.d/w83627dhg.conf
	fi
	##chkconfig --level 2345 lm_sensors on
	chroot /hivemap chkconfig --level 2345 lm_sensors on
fi

##copy pg_pool file
if [ -f /hivemap/var/lib/pgsql/9.1/data/postgresql.conf ]
then
	/bin/cp -rf /var/lib/pgsql/9.1/data/postgresql.conf /hivemap/var/lib/pgsql/9.1/data
fi

if [ -f /hivemap/var/lib/pgsql/9.1/data/pg_hba.conf ]
then
	/bin/cp -rf /var/lib/pgsql/9.1/data/pg_hba.conf /hivemap/var/lib/pgsql/9.1/data
fi

if [ -f /hivemap/var/lib/pgsql/9.1/data/recovery.conf ]
then
	/bin/cp -rf /var/lib/pgsql/9.1/data/recovery.conf /hivemap/var/lib/pgsql/9.1/data
fi

if [  -f /hivemap/etc/pgpool-II-91/pgpool.conf ]
then
	/bin/cp -rf /etc/pgpool-II-91/pgpool.conf /hivemap/etc/pgpool-II-91
fi

if [ -f /hivemap/etc/pgpool-II-91/pcp.conf ]
then
	/bin/cp -rf /etc/pgpool-II-91/pcp.conf /hivemap/etc/pgpool-II-91
fi
if [ -f $UPDATE_HOME/aeronms_os_pack/root/pgpool-II-91.default ]
then
	/bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/pgpool-II-91.default /hivemap/etc/init.d/pgpool-II-91
fi

if [ ! -d /hivemap/root/.ssh ]
then
	mkdir /hivemap/root/.ssh
fi

if [ -d /root/.ssh ]
then
	/bin/cp -rf /root/.ssh/* /hivemap/root/.ssh
fi

##for heartbeat
if [ -f /HiveManager/ha/conf/aerohive_ha.conf ]
then
	/bin/cp -f /HiveManager/ha/conf/aerohive_ha.conf /hivemap/HiveManager/ha/conf
fi
if [ -f /etc/ha.d/ha.cf ]
then
	/bin/cp -f /etc/ha.d/ha.cf /hivemap/etc/ha.d/
fi
if [ -f /etc/ha.d/authkeys ]
then
	/bin/cp -f /etc/ha.d/authkeys /hivemap/etc/ha.d/
fi
if [ -f /var/lib/heartbeat/crm/cib.xml ]
then
	/bin/cp -f /var/lib/heartbeat/crm/cib.xml /hivemap/var/lib/heartbeat/crm/
fi
if [ -f /var/lib/heartbeat/hb_uuid ]
then
	/bin/cp -f /var/lib/heartbeat/hb_uuid /hivemap/var/lib/heartbeat
fi

touch /hivemap/HiveManager/ha/conf/.switch_part

chroot /hivemap chkconfig --level 2345 aerohive-init on >>$HM_SCRIPT_LOGFILE 2>&1

##create the soft link for postgresql-9.1
POST91=/hivemap/etc/rc.d/init.d/postgresql-9.1
if [ -f $POST91 ]
then
	chroot /hivemap ln -s -f /etc/rc.d/init.d/postgresql-9.1 /etc/rc.d/init.d/postgresql
fi

chroot /hivemap chkconfig --level 2345 postgresql on  >>$HM_SCRIPT_LOGFILE 2>&1

##chanage config file if from old version to new
$HA_SHELL_HOME/check_ha_mode.sh
if [ $? = 0 ]
then
	chroot /hivemap chkconfig --level 2345 pgpool-II-91 on >>HM_SCRIPT_LOGFILE 2>&1
	if [ $pg_pool_num = 0 ]
	then
		. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/haHmChangeDBConn.sh
	fi
fi

. $UPDATE_SHELL_HOME/haHmDBBackup.sh $1

sleep 10

$UPDATE_SHELL_HOME/switchPartitionInHa.sh

sleep 15

##make db is not read-only
##if [ -f /hivemap/var/lib/pgsql/9.1/data/recovery.conf -o -f /var/lib/pgsql/9.1/data/recovery.conf]
##then
	/bin/rm -rf /hivemap/var/lib/pgsql/9.1/data/recovery.conf
	/bin/rm -rf /var/lib/pgsql/9.1/data/recovery.conf
##fi

##sleep 5

##rewrite pg start conf file
if [ -f /hivemap/etc/rc.d/init.d/postgresql-9.1 -a -f $UPDATE_HOME/root/postgresql-9.1.hivemanager ]
then
	/bin/cp -rf $UPDATE_HOME/root/postgresql-9.1.hivemanager /hivemap/etc/rc.d/init.d/postgresql-9.1
fi

##copy app files
. $UPDATE_SHELL_HOME/haHmDBCopyApp.sh
. $UPDATE_SHELL_HOME/haHmSyncSoftCfgFromTomcat.sh

##make both partition are same
if [ -f /HiveManager/tomcat/.dbOnly ]
then
	/bin/cp -f /HiveManager/tomcat/.dbOnly /hivemap/HiveManager/tomcat/
	chroot /hivemap chkconfig --level 2345 pgpool-II-91 off >>HM_SCRIPT_LOGFILE 2>&1
else
    /bin/rm -f /hivemap/HiveManager/tomcat/.dbOnly
fi

##change config.ini
. $UPDATE_HOME/HiveManager/script/shell/haChangeConfig.sh >>$HM_SCRIPT_LOGFILE 2>&1

##L7 signature
if [ -d /hivemap/HiveManager/l7_signatures ]
then
	/bin/rm -rf /hivemap/HiveManager/l7_signatures/*
else
    mkdir /hivemap/HiveManager/l7_signatures
fi
if [ -d /hivemap/HiveManager/l7_signatures ]
then
	touch /hivemap/HiveManager/l7_signatures/.update
fi
if [ -d /HiveManager/downloads/home/signature ]
then
    /bin/cp -rf /HiveManager/downloads/home/signature/* /hivemap/HiveManager/l7_signatures
fi

##change db memory
if [ -f $HM_ROOT/WEB-INF/hmconf/dbconfigure.conf ]
then
	if [ -f /root/.auto_db_memory ]; then
 		/bin/cp -f /root/.auto_db_memory /hivemap/root >>$HM_SCRIPT_LOGFILE 2>&1
	else
	    /bin/rm -f /hivemap/root/.auto_db_memory >>$HM_SCRIPT_LOGFILE 2>&1
	fi

	_OLD_FILE=$HM_ROOT/WEB-INF/hmconf/dbconfigure.conf.default
	_NEW_FILE=/hivemap$HM_ROOT/WEB-INF/hmconf/dbconfigure.conf.default
	OLD_DB_MEMORY=`sed -n "s/DB_MEMORY=\(.*\)/\1/p" $_OLD_FILE 2>/dev/null`
	if [ -n "$OLD_DB_MEMORY" ]; then
		sed -i "s/DB_MEMORY=.*/DB_MEMORY=${OLD_DB_MEMORY}/" $_NEW_FILE
	fi

	db_memory=`cat $HM_ROOT/WEB-INF/hmconf/dbconfigure.conf | grep DB_MEMORY | sed -n "s/.*DB_MEMORY=\(.*\)/\1/p" `
    if [ ! -z $db_memory ]
    then
    	cd $UPDATE_SHELL_HOME
        if [ -f setDBConfigure.sh ]
        then
        	if [ -f /hivemap/var/lib/pgsql/9.1/data/postgresql.conf ]
        	then
    	        ./setDBConfigure.sh $db_memory /hivemap$HM_ROOT/WEB-INF/hmconf/dbconfigure.conf /hivemap/var/lib/pgsql/9.1/data/postgresql.conf >>$HM_SCRIPT_LOGFILE 2>&1
        	fi
        fi
    fi
fi

sleep 5

reboot

echo "copy files and install rpms end." >>$HM_SCRIPT_LOGFILE 2>&1
