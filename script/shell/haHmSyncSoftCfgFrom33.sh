#!/bin/bash

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

DOWNLOAD_HOME=/HiveManager/downloads

IMAGE_HOME=$NMS_HOME/webapps/"$APP_HOME"/domains

MAP_HIVEMANAGE_HOME=/hivemap/HiveManager
MAP_CATALINA_HOME=$MAP_HIVEMANAGE_HOME/tomcat
MAP_DOWNLOADS_HOME=/hivemap/HiveManager/downloads
MAP_IMAGE_HOME=$MAP_HIVEMANAGE_HOME/tomcat/webapps/"$APP_HOME"/domains


rm -rf $MAP_DOWNLOADS_HOME
mkdir $MAP_DOWNLOADS_HOME

if [ -d $MAP_IMAGE_HOME ]
then
  rm -rf $MAP_IMAGE_HOME/*
else
  mkdir $MAP_IMAGE_HOME
fi

#restore map
cd $IMAGE_HOME

for domain in ./*
do
  if [ -d $domain ]
  then
   mkdir $MAP_IMAGE_HOME/$domain

   /bin/cp -rf $IMAGE_HOME/$domain/* $MAP_IMAGE_HOME/$domain
  fi
done

cd $DOWNLOAD_HOME

for file in ./*
do
    if [ -d $file ]
    then
      mkdir $MAP_DOWNLOADS_HOME/$file

      mkdir $MAP_DOWNLOADS_HOME/$file/aerohiveca
      if [ -d ./$file/aerohiveca ]
      then
        /bin/cp -rf ./$file/aerohiveca $MAP_DOWNLOADS_HOME/$file
      else
        /bin/cp -rf ./home/aerohiveca $MAP_DOWNLOADS_HOME/$file
      fi

      if [ -d ./$file/image ]
      then
        mkdir -p $MAP_DOWNLOADS_HOME/$file/image
        #/bin/cp -rf ./$file/image $MAP_DOWNLOADS_HOME/$file
        if [ $file != home ]
        then
            rm -rf $MAP_DOWNLOADS_HOME/$file/image/*
        fi
        /bin/cp -rf ./$file/image $MAP_DOWNLOADS_HOME/home

      fi

      if [ -d ./"$file"/cwp ]
      then
        mkdir -p $MAP_DOWNLOADS_HOME/$file/cwp/
        /bin/cp -rf ./"$file"/cwp $MAP_DOWNLOADS_HOME/"$file"
      fi

      #if [ ! -d $MAP_HIVEMANAGE_HOME/tomcat/webapps/"$APP_HOME"/ fingerprints ]
      #then
      #	mkdir -p $MAP_HIVEMANAGE_HOME/tomcat/webapps/"$APP_HOME"/ fingerprints
      #fi
      #if [ -d $NMS_HOME/webapps/"$APP_HOME"/fingerprints ]
      #then
      #	/bin/cp -rf $NMS_HOME/webapps/"$APP_HOME"/fingerprints $MAP_HIVEMANAGE_HOME/tomcat/webapps/"$APP_HOME"/ fingerprints
      #fi

      mkdir -p $MAP_DOWNLOADS_HOME/$file/script/bootstrap

      mkdir -p $MAP_DOWNLOADS_HOME/$file/script/new

	  mkdir -p $MAP_DOWNLOADS_HOME/$file/script/run

	  #mkdir -p $MAP_DOWNLOADS_HOME/$file/script/xml/bak

	  mkdir -p $MAP_DOWNLOADS_HOME/$file/script/xml/bootstrap

	  #mkdir -p $MAP_DOWNLOADS_HOME/$file/script/xml/diff

	  mkdir -p $MAP_DOWNLOADS_HOME/$file/script/xml/new

	  mkdir -p $MAP_DOWNLOADS_HOME/$file/script/xml/old

	  mkdir -p $MAP_DOWNLOADS_HOME/$file/script/xml/run

	  mkdir -p $MAP_DOWNLOADS_HOME/$file/script/xml/view
    fi
 done

##   /bin/rm -rf $MAP_CATALINA_HOME/dbxmlfile
##   /bin/mkdir  $MAP_CATALINA_HOME/dbxmlfile

##  cd $NMS_HOME

##   . $SHELL_HOME/setEnv.sh

##   if [ $1 -ne 2 ]
##   then

##     $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupFullData $MAP_CATALINA_HOME/dbxmlfile $1 >>$HM_SCRIPT_LOGFILE 2>&1

##   fi

##   rm -rf $MAP_CATALINA_HOME/dbxmlfile/license

##   mkdir -p $MAP_CATALINA_HOME/dbxmlfile/license

##   $JAVA_HOME/bin/java -Xms256m -Xmx512m -cp $CLASSPATH com.ah.be.admin.adminBackupUnit.AhBackupLicenseHistory $MAP_CATALINA_HOME/dbxmlfile/license >>$HM_SCRIPT_LOGFILE 2>&1

##   touch $MAP_CATALINA_HOME/dbxmlfile/webversion
   #touch $MAP_CATALINA_HOME/dbxmlfile/new_struct_flag

##   touch $MAP_CATALINA_HOME/.swupdated

##   if [ -f $NMS_HOME/webapps/ROOT/WEB-INF/hmconf/hivemanager.ver ]
##   then
##     cp -rf $NMS_HOME/webapps/ROOT/WEB-INF/hmconf/hivemanager.ver  $MAP_CATALINA_HOME/dbxmlfile
##   else
##     cp -rf $NMS_HOME/webapps/"$APP_HOME"/WEB-INF/hmconf/hivemanager.ver  $MAP_CATALINA_HOME/dbxmlfile
##   fi


