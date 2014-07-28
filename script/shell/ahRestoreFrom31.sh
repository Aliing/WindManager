#!/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

 TMP_HOME=$NMS_HOME/tmp
 DOWNLOAD_HOME=/HiveManager/downloads
 
 MAPS_HOME=$NMS_HOME/webapps/"$APP_HOME"/domains
 
 rm -rf dbxmlfile
 mkdir dbxmlfile
 
 cd $TMP_HOME
 
  if [ -d HiveManager/downloads ] 
  then
    
    if [ -d webapps/ROOT/domains ]
    then
      BACK_MAPS_HOME=webapps/ROOT/domains
    fi  
    
    if [ -d webapps/"$APP_HOME"/domains ]
    then
      BACK_MAPS_HOME=webapps/"$APP_HOME"/domains
    fi
    
    if [ -z "$BACK_MAPS_HOME" ]
    then
      echo "restore_in_progress" > /dev/stderr
      exit 1
    fi
  else  
    echo "restore_in_progress" > /dev/stderr
    exit 1
  fi
  
 rm -rf $DOWNLOAD_HOME/*
  
 rm -rf $MAPS_HOME/*
  


  /bin/cp -rf dbxmlfile/* $NMS_HOME/dbxmlfile/
  if [ -f dbxmlfile/.backupdump ]
  then
      /bin/cp -rf dbxmlfile/.backupdump $NMS_HOME/dbxmlfile/
  fi

  #cd webapps/ROOT/domains
  cd "$BACK_MAPS_HOME"
  
  for domain in ./*
  do
    if [ -d "$domain" ]
    then
      mkdir $MAPS_HOME/"$domain"
      
      cp -rf ./"$domain"/* $MAPS_HOME/"$domain"    
    fi
  done
  
  cd $TMP_HOME
  
  cd HiveManager/downloads
  
  for file in ./*
  do
    if [ -d "$file" ]
    then
      mkdir $DOWNLOAD_HOME/"$file"
      
      mkdir $DOWNLOAD_HOME/"$file"/aerohiveca
      if [ -d ./"$file"/aerohiveca ]
      then   
        /bin/cp -rf ./"$file"/aerohiveca $DOWNLOAD_HOME/"$file"    
      fi        
      
      if [ -d ./"$file"/image ]
      then
        mkdir -p $DOWNLOAD_HOME/"$file"/image
        /bin/cp -rf ./"$file"/image $DOWNLOAD_HOME/"$file"
      fi  
      
      if [ -d ./"$file"/cwp ]   
      then
        mkdir -p $DOWNLOAD_HOME/"$file"/cwp
        /bin/cp -rf ./"$file"/cwp $DOWNLOAD_HOME/"$file"
      fi
      
      if [ -d ./"$file"/signature ]
      then
      	mkdir -p $DOWNLOAD_HOME/"$file"/signature
      	/bin/cp -rf ./"$file"/signature/*.tar.gz $DOWNLOAD_HOME/"$file"/signature
      	touch $DOWNLOAD_HOME/"$file"/signature/.new
      fi
      
      mkdir -p $DOWNLOAD_HOME/"$file"/script/bootstrap
  
      mkdir -p $DOWNLOAD_HOME/"$file"/script/new
  
	  mkdir -p $DOWNLOAD_HOME/"$file"/script/run
	  
	  mkdir -p $DOWNLOAD_HOME/"$file"/script/xml/bootstrap
	  
	  mkdir -p $DOWNLOAD_HOME/"$file"/script/xml/new
	   
	  mkdir -p $DOWNLOAD_HOME/"$file"/script/xml/old
	  
	  mkdir -p $DOWNLOAD_HOME/"$file"/script/xml/run
	  
	  mkdir -p $DOWNLOAD_HOME/"$file"/script/xml/view
      
    fi
  done
  
  cd $NMS_HOME
  rm -fr tmp
    
  touch .swupdated    
  touch .fullrestore  
  exit 0
