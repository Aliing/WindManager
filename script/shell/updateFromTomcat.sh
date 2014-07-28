#!/bin/bash

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "updateFromTomcat start..." >>$HM_SCRIPT_LOGFILE
  
  if [ -f /opt/amigopod/www/_site/AerohiveLicense2.dat ]
  then
  	echo "update_in_progress" > /dev/stderr
  	exit 1
  fi

  RPMS_HOME=$UPDATE_HOME/rpms

  if [ ! -d $UPDATE_HOME/hm ] || [ ! -d $UPDATE_HOME/HiveManager ]
  then
	echo "update_in_progress" > /dev/stderr
	exit 1
  fi

  if [ ! -d $UPDATE_HOME/rpms ]
  then
	echo "update_in_progress" > /dev/stderr
	exit 1
  fi

  ##add some "condition" for normal HM and HMOL
  if [ ! -f $UPDATE_HOME/hm/config.ini ]
  then
    echo "update_in_progress" > /dev/stderr
	exit 1
  fi

  UPDATE_APPTYPE=`cat $UPDATE_HOME/hm/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`

  #add default
  HM_APPTYPE=HM

  if [ -f $HM_ROOT/config.ini ]
  then
    HM_APPTYPE=`cat $HM_ROOT/config.ini | sed -n 's/.*apptype[ ]*=[ ]*\([^ ][-A-Za-z0-9\/._\+]*\).*/\1/ip'`
  fi

  HTTPD_EXISTS=`chkconfig --list httpd > /dev/null 2>&1`

 # if [ $UPDATE_APPTYPE != "HM" -a $UPDATE_APPTYPE != "hm" ]
 # then
 #   if [ -d $RPMS_HOME/amigopod ]
 #   then
 #     echo "update_in_progress" > /dev/stderr
 #	  exit 1
 #   fi

 #  if  [ ! -z $HTTPD_EXISTS ] && [ $HTTPD_EXISTS -eq 0 ]
 #   then
 #     echo "update_in_progress" > /dev/stderr
 #	  exit 1
 #   fi
 # else
 #   if [ ! -d $RPMS_HOME/amigopod ]
 #   then
 #     echo "update_in_progress" > /dev/stderr
 #	  exit 1
 #   fi
 # fi

  if [ $HM_APPTYPE == "HM" -o $HM_APPTYPE == "hm" ]
  then
    if [ $UPDATE_APPTYPE != "HM" -a $UPDATE_APPTYPE != "hm" ]
    then
      echo "update_in_progress" > /dev/stderr
	  exit 1
    fi
  else
    if [ $UPDATE_APPTYPE == "HM" -o $UPDATE_APPTYPE == "hm" ]
    then
      echo "update_in_progress" > /dev/stderr
	  exit 1
    fi
  fi

   if [ -f /HiveManager/script/shell/restoreCasInfo.sh ]
   then
      . /HiveManager/script/shell/restoreCasInfo.sh $HM_ROOT/WEB-INF/web.xml $UPDATE_HOME/hm/WEB-INF/web.xml
   fi

  HIVEMAP_TMP=/hivemap/hivetmp

  rm -rf $HIVEMAP_TMP
  mkdir $HIVEMAP_TMP
  mkdir $HIVEMAP_TMP/yum_rpms

  ###check update rpms
#  KERNEL_VESION=`uname -r`
#  MAP_KERNEL_VERSION=`chroot /hivemap uname -r`
#  if [ $KERNEL_VESION != 2.6.18-92.1.6.el5 ] || [ $MAP_KERNEL_VERSION != 2.6.18-92.1.6.el5 ]
#  then
#    cd $RPMS_HOME
#    KERNEL_FILE=`ls kernel-2.6.18-92.1.6.el5.*.rpm`
#    KERNEL_HEAD=`ls kernel-headers-2.6.18-92.1.6.el5.*.rpm`
#    if [ ! -f $RPMS_HOME/$KERNEL_FILE ] || [ ! -f $RPMS_HOME/$KERNEL_HEAD ] || [ ! -f $UPDATE_HOME/grub.conf.hivemanager ]
#    then
#      echo "update_in_progress" > /dev/stderr
#      exit 1
#    fi
#  fi

##check pg_pool
pg_pool_num=`chroot /hivemap rpm -qa | grep pgpool | wc -l`

  LM_SENSORS_VERSIONS=`chroot /hivemap rpm -qa | grep lm_sensors`

  LM_SENSORS_VERSION=lm_sensors-2.10.0-3.1

  for VERSION in $LM_SENSORS_VERSIONS
  do
    if [ $VERSION == lm_sensors-2.10.6-1.fc8 ]
    then
      LM_SENSORS_VERSION=$VERSION
    fi
  done

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

  SYSLOG_NG_VERSION=`chroot /hivemap rpm -qa | grep syslog-ng`

  if [ -z $SYSLOG_NG_VERSION ]
  then
    cd $RPMS_HOME

    SYSLOG_NG_FILE=`ls syslog-ng-*.rpm`
    if [ ! -f $RPMS_HOME/$SYSLOG_NG_FILE ] || [ ! -d $UPDATE_HOME/aeronms_os_pack/etc/syslog-ng ] || [ ! -d $UPDATE_HOME/aeronms_os_pack/etc/logrotate.d ]
    then
      echo "update_in_progress" > /dev/stderr
      exit 1
    fi

    cp -rf $RPMS_HOME/$SYSLOG_NG_FILE $HIVEMAP_TMP/yum_rpms
  fi

  CROND_VERSION=`chroot /hivemap rpm -qa | grep vixie-cron`

  if [ -z $CROND_VERSION ]
  then
      cd $RPMS_HOME

      CROND_FILE=`ls vixie-cron-*.rpm`
      CRON_TABLES_FILE=`ls crontabs-*.rpm`

      if [ ! -f $RPMS_HOME/$CROND_FILE ] || [ ! -f $RPMS_HOME/$CRON_TABLES_FILE ]
      then
        echo "update_in_progress" > /dev/stderr
        exit 1
      fi

      cp -rf $RPMS_HOME/$CROND_FILE $HIVEMAP_TMP/yum_rpms
      cp -rf $RPMS_HOME/$CRON_TABLES_FILE $HIVEMAP_TMP/yum_rpms
  fi

  TFTP_VERSION=`chroot /hivemap rpm -qa | grep tftp-server`

  if [ -z $TFTP_VERSION ]
  then
      cd $RPMS_HOME

      TFTP_SERVER_FILE=`ls tftp-server-*.rpm`
      XINETD_FILE=`ls xinetd-*.rpm`

      if [ ! -f $RPMS_HOME/$TFTP_SERVER_FILE ] || [ ! -f $RPMS_HOME/$XINETD_FILE ] || [ ! -f $UPDATE_HOME/aeronms_os_pack/etc/xinetd.d/tftp.hivemanager ]
      then
          echo "update_in_progress" > /dev/stderr
          exit 1
      fi

      cp -rf $RPMS_HOME/$TFTP_SERVER_FILE $HIVEMAP_TMP/yum_rpms
      cp -rf $RPMS_HOME/$XINETD_FILE $HIVEMAP_TMP/yum_rpms
  fi

  AEROHIVE_LICENSE_VERSION=`chroot /hivemap rpm -qa | grep aerohive_lic`

  if [ -z $AEROHIVE_LICENSE_VERSION ] ||  [ $AEROHIVE_LICENSE_VERSION != aerohive_lic-0.6r1-1 ]
  then
    cd $RPMS_HOME

    AEROHIVE_LICENSE_FILE=`ls aerohive_lic-0.6r1-1*.rpm`

    if [ ! -f $RPMS_HOME/$AEROHIVE_LICENSE_FILE ]
    then
      echo "update_in_progress" > /dev/stderr
      exit 1
    fi
  fi

  MAP_TRACEROUTE=`chroot /hivemap rpm -qa | grep traceroute`

  if [ -z $MAP_TRACEROUTE ]
  then
    cd  $RPMS_HOME

    TRACEROUTE_FILE=`ls traceroute-2.0.1-2.el5.*.rpm`

    if [ ! -f $RPMS_HOME/$TRACEROUTE_FILE ]
    then
      echo "update_in_progress" > /dev/stderr
      exit 1
    fi

    cp -rf $RPMS_HOME/$TRACEROUTE_FILE $HIVEMAP_TMP/yum_rpms

  fi

  #TZDATA=`chroot /hivemap rpm -qa | grep tzdata-2008i-1.el5`

  #if [ -z $TZDATA ]
  #then
    #cd $RPMS_HOME

    #TZDATA_FILE=`ls tzdata-2008i-1.el5*.rpm`

    #if [ ! -f $RPMS_HOME/$TZDATA_FILE ]
    #then
      #echo "update_in_progress" > /dev/stderr
      #exit 1
    #fi

    #cp -rf $RPMS_HOME/$TZDATA_FILE $HIVEMAP_TMP/yum_rpms
  #fi

  PROCINFO=`chroot /hivemap rpm -qa | grep procinfo`

  if [ -z $PROCINFO ]
  then
    cd $RPMS_HOME

    PROCINFO_FILE=`ls  procinfo-18-19*.rpm`

    if [ ! -f $RPMS_HOME/$PROCINFO_FILE ]
    then
      echo "update_in_progress" > /dev/stderr
      exit 1
    fi

    cp -rf $RPMS_HOME/$PROCINFO_FILE $HIVEMAP_TMP/yum_rpms
  fi

  MAP_GLIBC_COMMON=`chroot /hivemap rpm -qa | grep glibc-common`

  if [ -z $MAP_GLIBC_COMMON ]
  then
    cd  $RPMS_HOME

    GLIBC_COMMON_FILE=`ls glibc-common*.rpm`

    if [ ! -f $RPMS_HOME/$GLIBC_COMMON_FILE ]
    then
      echo "update_in_progress" > /dev/stderr
      exit 1
    fi
    cp -rf $RPMS_HOME/$GLIBC_COMMON_FILE $HIVEMAP_TMP/yum_rpms
  fi

  SHLL_PASSWD_VERSION=`chroot /hivemap rpm -qa | grep aerohive_shell_passwd-0.1r1-1`

  if [ -z $SHLL_PASSWD_VERSION ]
  then
    cd  $RPMS_HOME

    SHLL_PASSWD_FILE=`ls aerohive_shell_passwd*`

    if [ ! -f $RPMS_HOME/$SHLL_PASSWD_FILE ]
    then
      echo "update_in_progress" > /dev/stderr
      exit 1
    fi

    cp -rf $RPMS_HOME/$SHLL_PASSWD_FILE $HIVEMAP_TMP/yum_rpms
  fi

  cd $RPMS_HOME

  SYSLOG_FILE=`ls sysklogd*`
  if [ -f $RPMS_HOME/$SYSLOG_FILE ]
  then
      /bin/cp -rf $RPMS_HOME/$SYSLOG_FILE  $HIVEMAP_TMP/yum_rpms
  fi

  ####if update db
  if [ -f /hivemap/var/lib/pgsql/data/PG_VERSION ]
  then
      PG_VERSION=`cat /hivemap/var/lib/pgsql/data/PG_VERSION`
      if [ $PG_VERSION != 9.1 ]
      then
          rm -rf  /hivemap/var/lib/pgsql/data
      fi
  fi
  #####

  /bin/cp -rf $RPMS_HOME/*.rpm $HIVEMAP_TMP/yum_rpms

  MAP_HTTPD_VERSION=`chroot /hivemap rpm -qa | grep httpd`

  ##if [ -d $RPMS_HOME/amigopod ]
  ##then
  ## /bin/cp -rf  $RPMS_HOME/amigopod $HIVEMAP_TMP
  ##fi

  /bin/cp -rf $RPMS_HOME/ha $HIVEMAP_TMP


  cd $NMS_HOME

  if [ $LM_SENSORS_VERSION != lm_sensors-2.10.6-1.fc8 ]
  then
    cp -rf $RPMS_HOME/$LM_SENSORS_FILE $HIVEMAP_TMP

    rpm -e --allmatches $LM_SENSORS_VERSION

    chroot /hivemap rpm -e --allmatches $LM_SENSORS_VERSION

    rpm -Uvh --replacefiles $RPMS_HOME/$LM_SENSORS_FILE >>$HM_SCRIPT_LOGFILE 2>&1

    chroot /hivemap rpm -Uvh --replacefiles /hivetmp/$LM_SENSORS_FILE >>$HM_SCRIPT_LOGFILE 2>&1

    # Motherboard detection for use with lm_sensor chip determination
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
	   rm -f sysconfig/lm_sensors.hivemanager*
	   rm -f sensors.conf.hivemanager*

	   # Turn off lm_sensors for non-recognized motherboards (i.e. VMWare).
	   chkconfig lm_sensors off
	   chroot /hivemap chkconfig --level 2345 lm_sensors off
    esac

  fi

#  if [ $KERNEL_VESION != 2.6.18-92.1.6.el5 ] || [ $MAP_KERNEL_VERSION != 2.6.18-92.1.6.el5 ]
#  then
#    cp -rf $RPMS_HOME/$KERNEL_FILE $HIVEMAP_TMP
#    cp -rf $RPMS_HOME/$KERNEL_HEAD $HIVEMAP_TMP
#    cp -rf $RPMS_HOME/grub.conf.hivemanager $HIVEMAP_TMP

#    chmod u+x $UPDATE_HOME/aeronms_os_pack/root/w83793.ko

#    cp -rf $UPDATE_HOME/aeronms_os_pack/root/w83793.ko $HIVEMAP_TMP

#    chmod u+x $UPDATE_HOME/aeronms_os_pack/etc/rc.upgrade
#    /bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/rc.upgrade /hivemap/etc/rc.upgrade

#    /bin/cp -rf /boot/grub/grub.conf /boot/grub/grub.conf.bak
#    /bin/cp -rf /hivemap/boot/grub/grub.conf /hivemap/boot/grub/grub.conf.bak

    #chroot /hivemap rpm -Uvh /hivetmp/$KERNEL_HEAD >>$HM_SCRIPT_LOGFILE 2>&1
#    rpm -Uvh $RPMS_HOME/$KERNEL_HEAD >>$HM_SCRIPT_LOGFILE 2>&1

    #chroot /hivemap rpm -i /hivetmp/$KERNEL_FILE >>$HM_SCRIPT_LOGFILE 2>&1
#    rpm -i --noscripts $RPMS_HOME/$KERNEL_FILE >>$HM_SCRIPT_LOGFILE 2>&1

#    if [ `uname -i` == "x86_64" -o `uname -i` == "i386" ]; then
#      if [ -f /etc/sysconfig/kernel ]; then
#        /bin/sed -i -e 's/^DEFAULTKERNEL=kernel-smp$/DEFAULTKERNEL=kernel/' /etc/sysconfig/kernel >> /hivemap/install.log 2>&1
#      fi
#    fi
#   /sbin/new-kernel-pkg --package kernel --mkinitrd --depmod --install 2.6.18-92.1.6.el5 >> /hivemap/install.log 2>&1
#    if [ -x /sbin/weak-modules ]
#    then
#        /sbin/weak-modules --add-kernel 2.6.18-92.1.6.el5 >> /hivemap/install.log 2>&1
#    fi
#    MB=`dmidecode -t 2 | grep "Product Name" | sed -e 's/.*: //g'`
    # Re-install the sensor module for 2U X7DBN
#    case "$MB" in
#       Grantsdale)
#	   # Nothing here
#         ;;
#      X7DBN)
#	   cp $UPDATE_HOME/aeronms_os_pack/root/w83793.ko /lib/modules/2.6.18-92.1.6.el5/kernel/drivers/hwmon/
#	   depmod -a $version
#	   ;;
#       *)
#	  # Nothing here
#    esac

#    /bin/cp -rf  /boot/grub/grub.conf.bak /boot/grub/grub.conf
#    /bin/cp -rf  /hivemap/boot/grub/grub.conf.bak /hivemap/boot/grub/grub.conf

#    rm -rf  /boot/grub/grub.conf.bak
#    rm -rf  /hivemap/boot/grub/grub.conf.bak
#  fi

  ##postgresql server version problem
##  PG_SERVER_VERSION=`chroot /hivemap rpm -qa | grep postgresql-server-8.3.0-1PGDG.rhel5`

##  if [ ! -z $PG_SERVER_VERSION ]
##  then
##    chroot /hivemap rpm -e --nodeps postgresql-server-8.3.0-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
##  fi

  PG_SERVER_VERSION=`chroot /hivemap rpm -qa | grep postgresql-server-8.4.2-1PGDG.rhel5`
  if [ ! -z $PG_SERVER_VERSION ]
  then
  	chroot /hivemap rpm -e --nodeps postgresql-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
  	chroot /hivemap rpm -e --nodeps postgresql-libs-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
  	chroot /hivemap rpm -e --nodeps postgresql-server-8.4.2-1PGDG.rhel5 >>$HM_SCRIPT_LOGFILE 2>&1
  fi

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

  ######need deny hivemap yum to internet######
  if [ -f $UPDATE_HOME/aeronms_os_pack/etc/yum.conf.hivemanager ]
  then
      /bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/yum.conf.hivemanager /hivemap/etc/yum.conf
  fi

  /bin/cp -rf /hivemap/etc/yum.repos.d /hivemap/etc/yum.repos.d.bak >>$HM_SCRIPT_LOGFILE 2>&1

  rm -rf /hivemap/etc/yum.repos.d/* >>$HM_SCRIPT_LOGFILE 2>&1

  ##remove kernel version
  rm -rf $HIVEMAP_TMP/yum_rpms/kernel* >>$HM_SCRIPT_LOGFILE 2>&1

  #############yum-rpms-begin#######
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

  #yum amigopod rpms

  ##if [ -d $HIVEMAP_TMP/amigopod ]
  ##then

  	  ##if [ $(ls -A $HIVEMAP_TMP/amigopod | wc -l) != 0 ]
	  ##then

	    ##yum_rpms=`ls $HIVEMAP_TMP/amigopod`

	    ##for file in $yum_rpms
	    ##do
	    ##  install_gms="$install_gms /hivetmp/amigopod/$file"
	    ##done

	    ##chroot /hivemap yum -y localupdate  $install_gms >>$HM_SCRIPT_LOGFILE 2>&1

	    ##chroot /hivemap yum -y localinstall $install_gms >>$HM_SCRIPT_LOGFILE 2>&1
	  ##fi

  ##fi
  #############yum-rpms-end#########

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
  #############yum-rpms-end#########

  if [ -z $SYSLOG_NG_VERSION ]
  then

    /bin/cp -rf $UPDATE_HOME/aeronms_os_pack/etc/syslog-ng/* /hivemap/etc/syslog-ng
    /bin/cp -rf /hivemap/etc/syslog-ng/syslog-ng.conf.default  /hivemap/etc/syslog-ng/syslog-ng.conf
    /bin/cp -rf $UPDATE_HOME/aeronms_os_pack/etc/logrotate.d/syslog-ng.hivemanager /hivemap/etc/logrotate.d/syslog-ng

    chroot /hivemap chkconfig --level 2345 syslog-ng on

  fi

  if [ -z $CROND_VERSION ]
  then
    chroot /hivemap chkconfig --level 2345 crond on
  fi

  if [ -z $TFTP_VERSION ]
  then
    chroot /hivemap chkconfig --level 2345 xinetd on
  fi

  if [ $UPDATE_APPTYPE == "HM" -o $UPDATE_APPTYPE == "hm" ]
  then

	  if [ -z $MAP_HTTPD_VERSION ]
	  then
	    /bin/cp -rf $UPDATE_HOME/aeronms_os_pack/etc/httpd/conf/httpd.conf.aerohive /hivemap/etc/httpd/conf/httpd.conf
	    /bin/cp -rf $UPDATE_HOME/aeronms_os_pack/etc/httpd/conf.d/ssl.conf.aerohive /hivemap/etc/httpd/conf.d/ssl.conf
	    /bin/cp -rf $UPDATE_HOME/aeronms_os_pack/etc/httpd/conf.d/gm.conf.aerohive  /hivemap/etc/httpd/conf.d/gm.conf
	    /bin/cp -rf $UPDATE_HOME/aeronms_os_pack/root/catalina.properties.aerohive /hivemap/$CATALINA_HOME/conf/catalina.properties

	    #chroot /hivemap chkconfig --level 2345 httpd on
	  fi

	  if [ -f $UPDATE_HOME/aeronms_os_pack/etc/httpd/conf/httpd.conf.aerohive ]
      then
        /bin/cp -rf $UPDATE_HOME/aeronms_os_pack/etc/httpd/conf/httpd.conf.aerohive /hivemap/etc/httpd/conf/httpd.conf
      fi

   fi

  ### Touch the upgrade file ###
  touch /hivemap/root/upgrade_file

#restore config.ini
. $UPDATE_SHELL_HOME/haChangeConfig.sh

##OEM sign file
##/bin/cp -f $UPDATE_HOME/hm/oemfiles/* /hivemap/hivetmp
##chroot /hivemap /bin/cp -f /hivetmp/jarsigner $JAVA_HOME/bin
##chroot /hivemap /bin/cp -f /hivetmp/tools.jar $JAVA_HOME/lib
##chroot /hivemap chmod u+x $JAVA_HOME/bin/jarsigner
##chroot /hivemap chmod u+x $JAVA_HOME/lib/tools.jar
##/bin/rm -f /hivemap/hivetmp/jarsigner
##/bin/rm -f /hivemap/hivetmp/tools.jar

. $UPDATE_SHELL_HOME/oemRestoreUploadFiles.sh

  /bin/cp -rf $UPDATE_HOME/aeronms_os_pack/root/*  /hivemap/root

  chmod u+x /hivemap/root/*.sh

  /bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/rc.d/init.d/aerohive-init /hivemap/etc/rc.d/init.d/aerohive-init

  chmod u+x /hivemap/etc/rc.d/init.d/aerohive-init

  /bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/rc.local.hivemanager /hivemap/etc/rc.local

  /bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/aerohive.pp  /hivemap/root/aerohive.pp

  /bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/ntp.conf.hivemanager  /hivemap/etc/ntp.conf

  /bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/iptables.hivemanager  /hivemap/etc/sysconfig/iptables

  /bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/sysconfig/iptables.hivemanager  /hivemap/etc/sysconfig/iptables.hivemanager
  
  sshPort=`$SHELL_HOME/ahGetSshdPort.sh | awk '{print $1}'`
  SSHD_CONF=/hivemap/etc/ssh/sshd_config
  SSHD_CONF_BAK=/hivemap/etc/ssh/sshd_config.bak
  /bin/cp -f $UPDATE_HOME/aeronms_os_pack/etc/ssh/sshd_config.hivemanager $SSHD_CONF
  rm -rf $SSHD_CONF_BAK
  cp -rf $SSHD_CONF $SSHD_CONF_BAK >>$HM_SCRIPT_LOGFILE 2>&1
  echo "" > $SSHD_CONF
  sed -e 's/#*Port [0-9][0-9]*/Port '$sshPort'/g'  $SSHD_CONF_BAK  >> $SSHD_CONF


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

  if [ -f $UPDATE_HOME/aeronms_os_pack/root/.bashrc.hivemanager ]
  then
    /bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/.bashrc.hivemanager /hivemap/root/.bashrc
  fi

  if [ -f $UPDATE_HOME/aeronms_os_pack/root/.pgpass ]
  then
    /bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/.pgpass /hivemap/root/.pgpass
    chmod 600 /hivemap/root/.pgpass
  fi

  if [ -f $HM_SHELL_HOME/server.xml.hivemanager ]
  then
    /bin/cp -rf $HM_SHELL_HOME/server.xml.hivemanager /hivemap/$CATALINA_HOME/conf/server.xml
  fi

  if [ -f $HM_SHELL_HOME/web.xml.hivemanager  ]
  then
    /bin/cp -rf $HM_SHELL_HOME/web.xml.hivemanager /hivemap/HiveManager/tomcat/conf/web.xml
  fi

  if [ -f $UPDATE_HOME/aeronms_os_pack/root/pg_hba.conf.hivemanager ]
  then
    /bin/cp -f $UPDATE_HOME/aeronms_os_pack/root/pg_hba.conf.hivemanager /hivemap/root/pg_hba.conf.hivemanager
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

  #update HA logd config file
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

  if [ -d /hivemap/var/lib/pgsql/data ]
  then
    rm -rf /hivemap/var/lib/pgsql/data
  fi

  ##if [ -f /hivemap/opt/amigopod/etc/postgresql-initialized ]
  ##then
  ##  if [ -f /hivemap/opt/amigopod/etc/postgresql-capable ]
  ##  then
  ##    mv -f /hivemap/opt/amigopod/etc/postgresql-capable /hivemap/opt/amigopod/postgresql-capable
  ##  fi
  ##  rm -rf /hivemap/opt/amigopod/etc/*
  ##  if [ -f /hivemap/opt/amigopod/postgresql-capable ]
  ##  then
  ##    mv -f /hivemap/opt/amigopod/postgresql-capable /hivemap/opt/amigopod/etc/postgresql-capable
  ##  fi
  ##fi

  ##stop amigopod
  chroot /hivemap service amigopod-init stop
  chroot /hivemap service amigopod-logimport stop
  chroot /hivemap service amigopod-runtime stop

  ##remove amigopod from chkconfig
  chroot /hivemap chkconfig amigopod-init off >>$HM_SCRIPT_LOGFILE 2>&1
  chroot /hivemap chkconfig amigopod-logimport off >>$HM_SCRIPT_LOGFILE 2>&1
  chroot /hivemap chkconfig amigopod-runtime off >>$HM_SCRIPT_LOGFILE 2>&1

  ##stop amigopod make httpd off, need make it on
  #chroot /hivemap chkconfig --level 2345 httpd on >>$HM_SCRIPT_LOGFILE 2>&1

  ##copy pg_pool file
  if [ -f /hivemap/var/lib/pgsql/9.1/data/postgresql.conf ]
  then
  	/bin/cp -rf /var/lib/pgsql/9.1/data/postgresql.conf /hivemap/var/lib/pgsql/9.1/data/
  fi
  if [ -f /hivemap/var/lib/pgsql/9.1/data/pg_hba.conf ]
  then
  	/bin/cp -rf /var/lib/pgsql/9.1/data/pg_hba.conf /hivemap/var/lib/pgsql/9.1/data/
  fi
  ##if [ -f /hivemap/var/lib/pgsql/9.1/data/recovery.conf ]
  ##then
  ##	cp -rf /var/lib/pgsql/9.1/data/recovery.conf /hivemap/var/lib/pgsql/9.1/data/
  ##fi
  if [ -f /hivemap/etc/pgpool-II-91/pgpool.conf ]
  then
  	/bin/cp -rf /etc/pgpool-II-91/pgpool.conf /hivemap/etc/pgpool-II-91/
  fi
  if [ -f /hivemap/etc/pgpool-II-91/pcp.conf ]
  then
  	/bin/cp -rf /etc/pgpool-II-91/pcp.conf /hivemap/etc/pgpool-II-91/
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
      /bin/cp -rf /root/.ssh/* /hivemap/root/.ssh/
  fi

  ##for heartbeat
  if [ -f /HiveManager/ha/conf/aerohive_ha.conf ]
  then
  	/bin/cp -f /HiveManager/ha/conf/aerohive_ha.conf /hivemap/HiveManager/ha/conf/
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
  if [ -f  /var/lib/heartbeat/hb_uuid ]
  then
  	/bin/cp -f /var/lib/heartbeat/hb_uuid /hivemap/var/lib/heartbeat
  fi

  touch /hivemap/HiveManager/ha/conf/.switch_part

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

  ##move gm
  if [ -d /hivemap/var/www/html/gm ]
  then
  	mv /hivemap/var/www/html/gm /hivemap/var/www/html/gm.bak
  fi


  chroot /hivemap chkconfig --level 2345 aerohive-init on >>$HM_SCRIPT_LOGFILE 2>&1

##create the soft link for postgresql-9.1
POST91=/hivemap/etc/rc.d/init.d/postgresql-9.1
if [ -f $POST91 ]
then
	chroot /hivemap ln -s -f /etc/rc.d/init.d/postgresql-9.1 /etc/rc.d/init.d/postgresql

fi

  chroot /hivemap chkconfig --level 2345 postgresql on  >>$HM_SCRIPT_LOGFILE 2>&1

  chroot /hivemap chkconfig --level 2345 tomcat off     >>$HM_SCRIPT_LOGFILE 2>&1

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

  cd $NMS_HOME

  . $UPDATE_SHELL_HOME/updateFiles.sh $1


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

/bin/rm -f /hivemap/var/lib/pgsql/9.1/data/recovery.conf
/bin/rm -f /var/lib/pgsql/9.1/data/recovery.conf

##make both partition are same
if [ -f /HiveManager/tomcat/.dbOnly ]
then
	/bin/cp -f /HiveManager/tomcat/.dbOnly /hivemap/HiveManager/tomcat/
	chroot /hivemap chkconfig --level 2345 pgpool-II-91 off >>HM_SCRIPT_LOGFILE 2>&1
else
    /bin/rm -f /hivemap/HiveManager/tomcat/.dbOnly
fi

##rewrite pg start conf file
if [ -f /hivemap/etc/rc.d/init.d/postgresql-9.1 -a -f $UPDATE_HOME/aeronms_os_pack/root/postgresql-9.1.hivemanager ]
then
	/bin/cp -rf $UPDATE_HOME/aeronms_os_pack/root/postgresql-9.1.hivemanager /hivemap/etc/rc.d/init.d/postgresql-9.1
fi

##change config.ini
#. $UPDATE_HOME/HiveManager/script/shell/haChangeConfig.sh >>$HM_SCRIPT_LOGFILE 2>&1

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
if [ -f /root/.memsize ]; then
	/bin/rm -f /root/.memsize >>$HM_SCRIPT_LOGFILE 2>&1
fi
echo "updateFromTomcat end." >>$HM_SCRIPT_LOGFILE 2>&1
