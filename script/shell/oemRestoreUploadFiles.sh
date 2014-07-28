#!/bin/bash

. /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh

echo "oem restore image files start..."  >>$HM_SCRIPT_LOGFILE 2>&1

if [ -f $HM_ROOT/oemfiles/config ]
then
	
	if [ -f $HM_ROOT/images/default_favicon.ico ]
	then
		/bin/cp -rf $UPDATE_HOME/hm/images/favicon.ico $UPDATE_HOME/hm/images/default_favicon.ico
		/bin/cp -rf $HM_ROOT/images/favicon.ico $UPDATE_HOME/hm/images/
	fi
    if [ -f $CATALINA_HOME/webapps/cas/default_favicon.ico ]
	then
		/bin/cp -rf $UPDATE_HOME/HiveManager/cas/favicon.ico $UPDATE_HOME/HiveManager/cas/default_favicon.ico
		/bin/cp -rf $CATALINA_HOME/webapps/cas/favicon.ico $UPDATE_HOME/HiveManager/cas/
	fi
	if [ -f $HM_ROOT/images/default_company_logo.png ]
	then
		/bin/cp -rf $UPDATE_HOME/hm/images/company_logo.png $UPDATE_HOME/hm/images/default_company_logo.png
		/bin/cp -rf $HM_ROOT/images/company_logo.png $UPDATE_HOME/hm/images/
	fi
	if [ -f $HM_ROOT/images/default_company_logo_reverse.png ]
	then
		/bin/cp -rf $UPDATE_HOME/hm/images/company_logo_reverse.png $UPDATE_HOME/hm/images/default_company_logo_reverse.png
	    /bin/cp -rf $HM_ROOT/images/company_logo_reverse.png $UPDATE_HOME/hm/images/
	fi
    if [ -f $CATALINA_HOME/webapps/cas/images/ah/default_company_logo_reverse.png ]
	then
		/bin/cp -rf $UPDATE_HOME/HiveManager/cas/images/ah/company_logo_reverse.png $UPDATE_HOME/HiveManager/cas/images/ah/default_company_logo_reverse.png
		/bin/cp -rf $CATALINA_HOME/webapps/cas/images/ah/company_logo_reverse.png $UPDATE_HOME/HiveManager/cas/images/ah/
	fi
	if [ -f $HM_ROOT/images/hm/default_bkg.gif ]
	then
		/bin/cp -rf $UPDATE_HOME/hm/images/hm/bkg.gif $UPDATE_HOME/hm/images/hm/default_bkg.gif
		/bin/cp -rf $HM_ROOT/images/hm/bkg.gif $UPDATE_HOME/hm/images/hm/
	fi
	if [ -f $CATALINA_HOME/webapps/cas/images/ah/default_bkg.gif ]
	then
		/bin/cp -f $UPDATE_HOME/HiveManager/cas/images/ah/bkg.gif $UPDATE_HOME/HiveManager/cas/images/ah/default_bkg.gif
		/bin/cp -f $CATALINA_HOME/webapps/cas/images/ah/bkg.gif $UPDATE_HOME/HiveManager/cas/images/ah/
	fi
	if [ -f $HM_ROOT/images/hm_v2/default_HM-config-footer.png ]
	then
		/bin/cp -rf $UPDATE_HOME/hm/images/hm_v2/HM-config-footer.png $UPDATE_HOME/hm/images/hm_v2/default_HM-config-footer.png
		/bin/cp -rf $HM_ROOT/images/hm_v2/HM-config-footer.png $UPDATE_HOME/hm/images/hm_v2/
	fi
	
    ##EULA
	if [ -f $HM_ROOT/tiles/default_companyEula.htm ]
	then
		/bin/cp -f $UPDATE_HOME/hm/tiles/companyEula.htm $UPDATE_HOME/hm/tiles/default_companyEula.htm
		/bin/cp -f $HM_ROOT/tiles/companyEula.htm $UPDATE_HOME/hm/tiles/
	fi	 

    ##oem settings
    if [ -f $HM_ROOT/resources/default_oem-resource.txt ]
    then
    	/bin/cp -f $UPDATE_HOME/hm/resources/oem-resource.txt $UPDATE_HOME/hm/resources/default_oem-resource.txt
    	/bin/cp -f $HM_ROOT/oemfiles/config $UPDATE_HOME/hm/oemfiles
    	. $UPDATE_SHELL_HOME/oemReplaceStr.sh Aerohive HiveManager "inside-sales@aerohive.com" "http://www.aerohive.com" "Copyright &copy; 2006-2014" 0
    fi
	
fi
echo "oem restore image files end..."  >>$HM_SCRIPT_LOGFILE 2>&1