# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

restore_name=$1

echo "restore $restore_name start..." >>$HM_SCRIPT_LOGFILE 2>&1

##picture
if [ $restore_name = "company_logo_reverse" ]
then
	if [ -f $HM_ROOT/images/default_company_logo_reverse.png ]
	then
		/bin/rm -f $HM_ROOT/images/company_logo_reverse.png
		/bin/mv $HM_ROOT/images/default_company_logo_reverse.png $HM_ROOT/images/company_logo_reverse.png
	fi
    if [  -f $CATALINA_HOME/webapps/cas/images/ah/default_company_logo_reverse.png ]
    then
    	/bin/rm -f $CATALINA_HOME/webapps/cas/images/ah/company_logo_reverse.png
    	/bin/mv $CATALINA_HOME/webapps/cas/images/ah/default_company_logo_reverse.png $CATALINA_HOME/webapps/cas/images/ah/company_logo_reverse.png
    fi
fi
if [ $restore_name = "favicon" ]
then
	if [ -f $HM_ROOT/images/default_favicon.ico ]
	then
		/bin/rm -f $HM_ROOT/images/favicon.ico
	    /bin/mv $HM_ROOT/images/default_favicon.ico $HM_ROOT/images/favicon.ico
	fi
    if [  -f $CATALINA_HOME/webapps/cas/default_favicon.ico ]
    then
    	/bin/rm -f $CATALINA_HOME/webapps/cas/favicon.ico
    	/bin/mv $CATALINA_HOME/webapps/cas/default_favicon.ico $CATALINA_HOME/webapps/cas/favicon.ico
    fi
fi
if [ $restore_name = "bkg" ]
then
	if [ -f $HM_ROOT/images/hm/default_bkg.gif ]
	then
		/bin/rm -f $HM_ROOT/images/hm/bkg.gif
	    /bin/mv $HM_ROOT/images/hm/default_bkg.gif $HM_ROOT/images/hm/bkg.gif
	fi
    if [  -f $CATALINA_HOME/webapps/cas/images/ah/default_bkg.gif ]
    then
    	/bin/rm -f $CATALINA_HOME/webapps/cas/images/ah/bkg.gif
    	/bin/mv $CATALINA_HOME/webapps/cas/images/ah/default_bkg.gif $CATALINA_HOME/webapps/cas/images/ah/bkg.gif
    fi
fi
if [ $restore_name = "company_logo" ]
then
	if [ -f $HM_ROOT/images/default_company_logo.png ]
	then
		/bin/rm -f $HM_ROOT/images/company_logo.png
	    /bin/mv  $HM_ROOT/images/default_company_logo.png  $HM_ROOT/images/company_logo.png
	fi
fi
if [ $restore_name = "config_footer" ]
then
	if [ -f $HM_ROOT/images/hm_v2/default_HM-config-footer.png ] 
	then
		/bin/rm -f $HM_ROOT/images/hm_v2/HM-config-footer.png
	    /bin/mv $HM_ROOT/images/hm_v2/default_HM-config-footer.png $HM_ROOT/images/hm_v2/HM-config-footer.png
	fi
fi

##EULA
if [ $restore_name = "eula" ]
then
	if [ -f $HM_ROOT/tiles/default_companyEula.htm ]
	then
		/bin/rm -f $HM_ROOT/tiles/companyEula.htm
	    /bin/mv $HM_ROOT/tiles/default_companyEula.htm $HM_ROOT/tiles/companyEula.htm
	fi
fi

##settings
##if [ $restore_name=="settings" ]
##then
	
##fi

echo "restore $restore_name end." >>$HM_SCRIPT_LOGFILE 2>&1