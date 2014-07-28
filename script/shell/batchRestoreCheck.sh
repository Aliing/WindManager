#! /bin/bash

. /HiveManager/script/shell/setHmEnv.sh

. $SHELL_HOME/setEnv.sh

echo ""

if [ $# = 1 ]
then
	UPLOAD_HOME=$1
else
    UPLOAD_HOME=/HiveManager/upload
fi


if [ ! -d $UPLOAD_HOME ]
then
	mkdir $UPLOAD_HOME
else
    cd $UPLOAD_HOME
    ## get files count
	FILE_COUNT=`ls -l | grep "^-" | wc -l`

	if [ $FILE_COUNT = 0 ]
	then
		echo "  [WARN] nothing be finded in upload folder!path:$UPLOAD_HOME"
		echo ""
	else
	    cd $NMS_HOME
	    echo "stop HiveManager..."
        . /HiveManager/script/shell/stopHiveManage.sh

        sleep 3

        for file in $UPLOAD_HOME/*.tar.gz
        do
        	echo "---------------$file start!---------------"
        	## get file name
        	tmp=`basename $file`
            FILE_NAME=${tmp%.*}

	        rm -rf $NMS_HOME/dbxmlfile/* >>$HM_SCRIPT_LOGFILE 2>&1

            if [ ! -d $NMS_HOME/filetmp ]
            then
            	mkdir $NMS_HOME/filetmp
            fi

            cd $UPLOAD_HOME
	        /bin/cp $file $NMS_HOME/filetmp >>$HM_SCRIPT_LOGFILE 2>&1
            ##rm -rf $file

     	    cd $NMS_HOME/filetmp
		    tar -xzf "$tmp" >>$HM_SCRIPT_LOGFILE 2>&1

		    if [ -d dbxmlfile ]
		    then
			    echo "move files to dbxmlfile..."
		        /bin/mv -f dbxmlfile/* $NMS_HOME/dbxmlfile >>$HM_SCRIPT_LOGFILE 2>&1

                sleep 3

                ## restore db
                . /HiveManager/script/shell/restore4Check.sh

                sleep 3

                echo "check db and xml file..."
                . /HiveManager/script/shell/checkAfterRestore.sh $FILE_NAME

			    echo ""
		    else
	            echo "  [WARN] $file is not file that backup from HiveManager!"
	            echo ""
		    fi

            ## clean tmp folder
		    rm -rf $NMS_HOME/filetmp/* >>$HM_SCRIPT_LOGFILE 2>&1

        done

        echo "start HiveManager..."
        . /HiveManager/script/shell/startHiveManage.sh
    fi
fi

echo ""
