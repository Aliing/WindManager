# !/bin/bash

. /HiveManager/script/shell/setHmEnv.sh

echo "backup OEM files start..." >>$HM_SCRIPT_LOGFILE 2>&1

##EULA file
if [ ! -f $HM_ROOT/tiles/default_companyEula.htm ]
then
	/bin/cp -f $HM_ROOT/tiles/companyEula.htm $HM_ROOT/tiles/default_companyEula.htm
fi

##picture
if [ ! -f $CATALINA_HOME/webapps/cas/images/ah/default_company_logo_reverse.png ]
then
	/bin/cp -f $CATALINA_HOME/webapps/cas/images/ah/company_logo_reverse.png $CATALINA_HOME/webapps/cas/images/ah/default_company_logo_reverse.png
fi
if [ ! -f $HM_ROOT/images/default_company_logo_reverse.png ]
then
	/bin/cp -f $HM_ROOT/images/company_logo_reverse.png $HM_ROOT/images/default_company_logo_reverse.png
fi
if [ ! -f $HM_ROOT/images/default_favicon.ico ]
then
	/bin/cp -f $HM_ROOT/images/favicon.ico $HM_ROOT/images/default_favicon.ico
fi
if [ ! -f $CATALINA_HOME/webapps/cas/default_favicon.ico ]
then
	/bin/cp -f $CATALINA_HOME/webapps/cas/favicon.ico $CATALINA_HOME/webapps/cas/favicon.ico
fi
if [ ! -f $HM_ROOT/images/hm/default_bkg.gif ]
then
	/bin/cp -f $HM_ROOT/images/hm/bkg.gif $HM_ROOT/images/hm/default_bkg.gif
fi
if [ ! -f $CATALINA_HOME/webapps/cas/images/ah/default_bkg.gif ]
then
	/bin/cp -f $CATALINA_HOME/webapps/cas/images/ah/bkg.gif $CATALINA_HOME/webapps/cas/images/ah/default_bkg.gif
fi
if [ ! -f $HM_ROOT/images/default_company_logo.png ]
then
	/bin/cp -f $HM_ROOT/images/company_logo.png $HM_ROOT/images/default_company_logo.png
fi
if [ ! -f $HM_ROOT/images/hm_v2/default_HM-config-footer.png ]
then
	/bin/cp -f $HM_ROOT/images/hm_v2/HM-config-footer.png $HM_ROOT/images/hm_v2/default_HM-config-footer.png
fi

##oem settings
if [ ! -f $HM_ROOT/resources/default_oem-resource.txt ]
then
	/bin/cp -f $HM_ROOT/resources/oem-resource.txt $HM_ROOT/resources/default_oem-resource.txt
fi
echo "backup OEM files end..." >>$HM_SCRIPT_LOGFILE 2>&1