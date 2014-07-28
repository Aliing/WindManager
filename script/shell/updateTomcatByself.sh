#!/bin/bash
#set the env for sh
. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

if [ -f /opt/amigopod/www/_site/AerohiveLicense2.dat ]
then
	echo "update_in_progress" > /dev/stderr
	exit 1
fi

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

#backup software and usedata
TAR_FILE_DIR=/tmp
if [ ! -d $TAR_FILE_DIR ]
then
  mkdir -p $TAR_FILE_DIR
fi
cd $TAR_FILE_DIR
tar czf hm_soft_bak.tar.gz --exclude=$UPDATE_HOME /HiveManager/* >>$HM_SCRIPT_LOGFILE 2>&1
/bin/mv hm_soft_bak.tar.gz $HM_BACKUP_DIR >>$HM_SCRIPT_LOGFILE 2>&1

cd $HM_BACKUP_DIR
/bin/cp -rf $HM_ROOT/WEB-INF/hmconf/hivemanager.ver ./  >>$HM_SCRIPT_LOGFILE 2>&1
/bin/cp -rf $HM_ROOT/domains ./ >>$HM_SCRIPT_LOGFILE 2>&1
/bin/cp -rf $HM_ROOT/res ./ >>$HM_SCRIPT_LOGFILE 2>&1

#backup the db data
cd $NMS_HOME

. $SHELL_HOME/setEnv.sh

mkdir -p $HM_BACKUP_DIR/dbxmlfile

if [ $1 -ne 2 ]
then
   $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupFullData $HM_BACKUP_DIR/dbxmlfile $1 >>$HM_SCRIPT_LOGFILE 2>&1
 fi

mkdir -p $HM_BACKUP_DIR/dbxmlfile/license

$JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupLicenseHistory $HM_BACKUP_DIR/dbxmlfile/license >>$HM_SCRIPT_LOGFILE 2>&1

##earse data
if [ -f  $SHELL_HOME/dropTable.sh ]
then
 $SHELL_HOME/dropTable.sh
fi

touch $HM_BACKUP_DIR/dbxmlfile/webversion
cp -rf $HM_ROOT/WEB-INF/hmconf/hivemanager.ver  $HM_BACKUP_DIR/dbxmlfile

VERSIONFILE=$HM_ROOT/WEB-INF/hmconf/hivemanager.ver
MAP_VERSIONFILE=$UPDATE_HOME/hm/WEB-INF/hmconf/hivemanager.ver

 ##postgresql server version problem
##  PG_SERVER_VERSION=`rpm -qa | grep postgresql-server-8.3.0-1PGDG.rhel5`

##  if [ ! -z $PG_SERVER_VERSION ]
##  then
##    rpm -e --nodeps postgresql-server-8.3.0-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
##  fi

  PG_SERVER_VERSION=`rpm -qa | grep postgresql-server-8.4.2-1PGDG.rhel5`
  if [ ! -z $PG_SERVER_VERSION ]
  then
  	rpm -e --nodeps postgresql-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
  	rpm -e --nodeps postgresql-libs-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
  	rpm -e --nodeps postgresql-server-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
  fi

######need deny  yum to internet######
  if [ -f $UPDATE_HOME/aeronms_os_pack/etc/yum.conf.hivemanager ]
  then
      /bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/yum.conf.hivemanager  /etc/yum.conf

      /bin/cp -rf /etc/yum.repos.d /etc/yum.repos.d.bak >>$HM_SCRIPT_LOGFILE 2>&1

      rm -rf /etc/yum.repos.d/* >>$HM_SCRIPT_LOGFILE 2>&1
  fi

  ##remove kernel version
  rm -rf $UPDATE_HOME/rpms/kernel* >>$HM_SCRIPT_LOGFILE 2>&1
 #############yum-rpms-begin#######
  if [ $(ls -A $UPDATE_HOME/rpms/*.rpm | wc -l) != 0 ]
  then

    yum_rpms=`ls $UPDATE_HOME/rpms/*.rpm`
    for file in $yum_rpms
    do
        if [ $file == 'httpd-2.4.3-1.x86_64.rpm' -o $file == 'mod_ssl-2.4.3-1.x86_64.rpm' -o $file == 'httpd-2.4.3-1.i386.rpm' -o $file == 'mod_ssl-2.4.3-1.i386.rpm' ]; then
    		rpm_install="$rpm_install /hivetmp/yum_rpms/$file"
    		continue
    	fi
      install_rpms="$install_rpms $file"
    done
    
    if [ "$rpm_install" != "" ]; then
    	rpm -e --nodeps mod_ssl-2.2.3-11.el5_1.centos.3 >>$HM_SCRIPT_LOGFILE 2>&1
    	rpm -Uvh --nodeps $rpm_install >>$HM_SCRIPT_LOGFILE 2>&1
    fi
    
	chkconfig --level 2345 httpd off >>$HM_SCRIPT_LOGFILE 2>&1

    yum -y localupdate $install_rpms >>$HM_SCRIPT_LOGFILE 2>&1

    yum -y localinstall $install_rpms >>$HM_SCRIPT_LOGFILE 2>&1
  fi



 #############yum-rpms-end#######
 ##do some config
 chkconfig --level 2345 tomcat off     >>$HM_SCRIPT_LOGFILE 2>&1
 ####if update db
  if [ -f /var/lib/pgsql/data/PG_VERSION ]
  then
      PG_VERSION=`cat /var/lib/pgsql/data/PG_VERSION`
      if [ $PG_VERSION != 9.1 ]
      then
          rm -rf  /var/lib/pgsql/data  >>$HM_SCRIPT_LOGFILE 2>&1
      fi
  fi
  #####
 ##

 if [ -d $UPDATE_HOME/rpms/ha ]
 then
	 if [ $(ls -A $UPDATE_HOME/rpms/ha/*.rpm | wc -l) != 0 ]
	  then

	    yum_rpms=`ls $UPDATE_HOME/rpms/ha/*.rpm`

	    for file in $yum_rpms
	    do
	      install_has="$install_has $file"
	    done

	    yum -y localupdate  $install_has >>$HM_SCRIPT_LOGFILE 2>&1

	    yum -y localinstall $install_has >>$HM_SCRIPT_LOGFILE 2>&1
	  fi
 fi

#update system file
touch /root/upgrade_file

#restore config.ini
##. $UPDATE_SHELL_HOME/haChangeConfig.sh
  
/bin/cp -rf $UPDATE_HOME/aeronms_os_pack/root/*  /root

chmod u+x /root/*.sh

/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/rc.d/init.d/aerohive-init /etc/rc.d/init.d/aerohive-init

chmod u+x /etc/rc.d/init.d/aerohive-init

/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/rc.local.hivemanager /etc/rc.local

/bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/aerohive.pp  /root/aerohive.pp

/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/ntp.conf.hivemanager  /etc/ntp.conf

/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/iptables.hivemanager  /etc/sysconfig/iptables

/bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/iptables.hivemanager  /etc/sysconfig/iptables.hivemanager


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
## End

if [ -f $UPDATE_HOME/aeronms_os_pack/root/.bashrc.hivemanager ]
then
  /bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/.bashrc.hivemanager /root/.bashrc
fi

if [ -f $UPDATE_HOME/aeronms_os_pack/root/.pgpass ]
then
  /bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/.pgpass /root/.pgpass
  chmod 600 /root/.pgpass
fi

  if [ -f $HM_SHELL_HOME/server.xml.hivemanager ]
  then
    /bin/cp -rf $HM_SHELL_HOME/server.xml.hivemanager $CATALINA_HOME/conf/server.xml
  fi

  if [ -f $HM_SHELL_HOME/web.xml.hivemanager  ]
  then
    /bin/cp -rf $HM_SHELL_HOME/web.xml.hivemanager /HiveManager/tomcat/conf/web.xml
  fi

  if [ -f $UPDATE_HOME/aeronms_os_pack/root/pg_hba.conf.hivemanager ]
  then
    /bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/pg_hba.conf.hivemanager /root/pg_hba.conf.hivemanager
  fi

  if [ -f $UPDATE_HOME/aeronms_os_pack/etc/selinux/config.hivemanager ]
  then
      /bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/selinux/config.hivemanager /etc/selinux/config
  fi

#create the soft link for postgresql-9.1
POST91=/etc/rc.d/init.d/postgresql-9.1
if [ -f $POST91 ]
then
  cd /etc/rc.d/init.d/
  ln -s postgresql-9.1 postgresql
  cd $NMS_HOME
fi

  chkconfig --level 2345 aerohive-init on >>$HM_SCRIPT_LOGFILE 2>&1

  chkconfig --level 2345 postgresql on  >>$HM_SCRIPT_LOGFILE 2>&1

 ##rewrite pg start conf file
 if [ -f $UPDATE_HOME/root/postgresql-9.1.hivemanager ]
 then
   /bin/cp -rf $UPDATE_HOME/root/postgresql-9.1.hivemanager /etc/rc.d/init.d/postgresql-9.1
 fi
 
#update system file finished

#sync some file before reboot
##if [ -f /var/lib/heartbeat/crm/cib.xml ]
##then
##   rm -rf /var/lib/heartbeat/crm/cib.xml
##fi
#sync finished

##change config.ini
. $UPDATE_HOME/HiveManager/script/shell/haChangeConfig.sh >>$HM_SCRIPT_LOGFILE 2>&1

 ##L7 signature
if [ -d /HiveManager/l7_signatures ]
then
	/bin/rm -rf /HiveManager/l7_signatures/*
else
    mkdir /HiveManager/l7_signatures
fi
if [ -d /HiveManager/l7_signatures ]
then
	touch /HiveManager/l7_signatures/.update
fi
if [ -d /HiveManager/downloads/home/signature ]
then
    /bin/cp -rf /HiveManager/downloads/home/signature/* /HiveManager/l7_signatures
fi

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


#touch a flag update
touch  $CATALINA_HOME/.swupdated
touch  $CATALINA_HOME/.singlepart
