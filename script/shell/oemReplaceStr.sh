#!/bin/bash
## if process is upgrade, params are settings of  Aerohive($1:Aerohive), 
## if it is setting function, params are new settings of GUI.
## $1  company name
## $2  nms name
## $3  support email
## $4  home page
## $5  copyritht
## $6 type 0:upgrade 1:OEM set

if [ $# -lt 6 ]
then
	echo "replace_ssl_err" >/dev/stderr
	exit 1
fi

if [ -f /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh ]
then
  . /HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/setHmEnv.sh
else
  . /HiveManager/script/shell/setHmEnv.sh
fi
echo "oem replace string start..."  >>$HM_SCRIPT_LOGFILE 2>&1

echo "params: $1, $2, $3, $4, $5, $6," >>$HM_SCRIPT_LOGFILE 2>&1

cas_root=$CATALINA_HOME/webapps/cas

res_file=$HM_ROOT/resources/oem-resource.txt
company_name=`awk -F "=" '/company_name=/ {print $2}' $res_file`
nms_name=`awk -F "=" '/nms_name=/ {print $2}' $res_file`
support_mail_address=`awk -F "=" '/support_mail_address=/ {print $2}' $res_file`
home_page=`awk -F "=" '/home_page=/ {print $2}' $res_file`
nms_copyright=`awk -F "=" '/nms_copyright=/ {print $2}' $res_file`
##help_url=`awk -F "=" '/help_link=/ {print $2}' $res_file`

##replace "/" to "\/", replace "." to "\."
echo local_company_name=$company_name | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
echo local_support_mail_address=$support_mail_address | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
echo local_home_page=$home_page | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
##echo local_help_url=$help_url | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp

echo par_company_name=$1 | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
echo par_support_mail_address=$3 | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
echo par_home_page=$4 | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp
##echo par_help_url=$7 | sed "s:/:\\\/:g" | sed "s:\.:\\\.:g" >> ./url_tmp

local_company_name=`awk -F "=" '/local_company_name=/ {print $2}' ./url_tmp`
local_support_mail_address=`awk -F "=" '/local_support_mail_address=/ {print $2}' ./url_tmp`
local_home_page=`awk -F "=" '/local_home_page=/ {print $2}' ./url_tmp`
##local_help_url=`awk -F "=" '/local_help_url=/ {print $2}' ./url_tmp`

par_company_name=`awk -F "=" '/par_company_name=/ {print $2}' ./url_tmp`
par_support_mail_address=`awk -F "=" '/par_support_mail_address=/ {print $2}' ./url_tmp`
par_home_page=`awk -F "=" '/par_home_page=/ {print $2}' ./url_tmp`
##par_help_url=`awk -F "=" '/par_help_url=/ {print $2}' ./url_tmp`

local_nms_copyright=`echo "$nms_copyright" | awk -F ' ' '/Copyright/ {print $3}'`
par_nms_copyright=`echo "$5" | awk -F ' ' '/Copyright/ {print $3}'`

/bin/rm -rf ./url_tmp

if [ $6 = 1 ]
then
	##replace company name
	for file in `ls $HM_ROOT/WEB-INF/classes/resources/*.properties`
	do
		sed -i "s/an ${par_company_name}/a ${par_company_name}/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
		sed -i "s/${local_company_name}/${par_company_name}/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
	done
	for file in `ls $HM_ROOT/WEB-INF/classes/locale/en/*.properties`
	do
		sed -i "s/an ${local_company_name}/a ${par_company_name}/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
		sed -i "s/${local_company_name}/${par_company_name}/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
	done
	sed -i "s/${local_company_name}/${par_company_name}/g" $cas_root/WEB-INF/view/jsp/default/ui/includes/top.jsp
	sed -i "s/${local_company_name}/${par_company_name}/g" $HM_ROOT/resources/search/hm_search_tables.xml
	sed -i "s/${local_company_name}/${par_company_name}/g" $HM_ROOT/WEB-INF/navigation.xml
	sed -i "s/${local_company_name}/${par_company_name}/g" $cas_root/WEB-INF/view/jsp/default/ui/includes/bottom.jsp
	sed -i "s/${local_company_name}/${par_company_name}/g" $HM_ROOT/WEB-INF/hmconf/constant/defaultWidgetForImport.csv
	
	##replace nms name
	for file in `ls $HM_ROOT/WEB-INF/classes/resources/*.properties`
	do
		sed -i "s/${nms_name}/$2/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
	done
	for file in `ls $HM_ROOT/WEB-INF/classes/locale/en/*.properties`
	do
		sed -i "s/${nms_name}/$2/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
	done
	sed -i "s/$nms_name/$2/g" $HM_ROOT/WEB-INF/navigation.xml
	sed -i "s/$nms_name/$2/g" $HM_ROOT/resources/search/hm_search_tables.xml
	sed -i "s/$nms_name/$2/g" $HM_ROOT/WEB-INF/hmconf/constant/defaultWidgetForImport.csv
	
	##replace support email
	sed -i "s/$local_support_mail_address/$par_support_mail_address/g" $HM_ROOT/config.ini
	
	##replace home page
	sed -i "s/$local_home_page/$par_home_page/g" $cas_root/WEB-INF/view/jsp/default/ui/includes/bottom.jsp
	sed -i "s/$local_home_page/$par_home_page/g" $HM_ROOT/resources/oem-resource.txt
	
	##replace copyright
	sed -i "s/$local_nms_copyright/$par_nms_copyright/g" $cas_root/WEB-INF/view/jsp/default/ui/includes/bottom.jsp
	
	## rm search
	if [ -f $HM_ROOT/resources/search/allPageResources.res ]
	then
		/bin/rm -f $HM_ROOT/resources/search/allPageResources.res 
	fi
	if [ -f $HM_ROOT/resources/search/page_index_finished.flag ]
	then
		/bin/rm -f $HM_ROOT/resources/search/page_index_finished.flag
	fi
else
	##replace company name
    for file in `ls $UPDATE_HOME/hm/WEB-INF/classes/resources/*.properties`
	do
		sed -i "s/an ${par_company_name}/a ${local_company_name}/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
		sed -i "s/${par_company_name}/${local_company_name}/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
	done
	for file in `ls $UPDATE_HOME/hm/WEB-INF/classes/locale/en/*.properties`
	do
		sed -i "s/an ${par_company_name}/a ${local_company_name}/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
		sed -i "s/${par_company_name}/${local_company_name}/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
	done
	sed -i "s/${par_company_name}/${local_company_name}/g" $UPDATE_HOME//HiveManager/cas/WEB-INF/view/jsp/default/ui/includes/top.jsp
	sed -i "s/${par_company_name}/${local_company_name}/g" $UPDATE_HOME/hm/resources/search/hm_search_tables.xml
	sed -i "s/${par_company_name}/${local_company_name}/g" $UPDATE_HOME/hm/WEB-INF/navigation.xml
	sed -i "s/${par_company_name}/${local_company_name}/g" $UPDATE_HOME/HiveManager/cas/WEB-INF/view/jsp/default/ui/includes/bottom.jsp
	sed -i "s/${par_company_name}/${local_company_name}/g" $UPDATE_HOME/hm/WEB-INF/hmconf/constant/defaultWidgetForImport.csv
	sed -i "s/${par_company_name}/${local_company_name}/g" $UPDATE_HOME/hm/resources/oem-resource.txt
	sed -i "s/company_full_name=.*/company_full_name=${local_company_name}/g" $UPDATE_HOME/hm/resources/oem-resource.txt
	sed -i "s/company_name_abbreviation=.*/company_name_abbreviation=${local_company_name}/g" $UPDATE_HOME/hm/resources/oem-resource.txt
	##replace nms name
    for file in `ls $UPDATE_HOME/hm/WEB-INF/classes/resources/*.properties`
	do
		sed -i "s/$2/${nms_name}/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
	done
	for file in `ls $UPDATE_HOME/hm/WEB-INF/classes/locale/en/*.properties`
	do
		sed -i "s/$2/${nms_name}/g" $file >>$HM_SCRIPT_LOGFILE 2>&1
	done
	sed -i "s/$2/${nms_name}/g" $UPDATE_HOME/hm/WEB-INF/navigation.xml
	sed -i "s/$2/${nms_name}/g" $UPDATE_HOME/hm/resources/search/hm_search_tables.xml
	sed -i "s/$2/${nms_name}/g" $UPDATE_HOME/hm/resources/oem-resource.txt
	sed -i "s/$2/${nms_name}/g" $UPDATE_HOME/hm/WEB-INF/hmconf/constant/defaultWidgetForImport.csv
	
	##replace support email
	sed -i "s/$par_support_mail_address/$local_support_mail_address/g" $UPDATE_HOME/hm/config.ini
	sed -i "s/$par_support_mail_address/$local_support_mail_address/g" $UPDATE_HOME/hm/resources/oem-resource.txt
	
	##replace home page
	sed -i "s/$par_home_page/$local_home_page/g" $UPDATE_HOME/HiveManager/cas/WEB-INF/view/jsp/default/ui/includes/bottom.jsp
	sed -i "s/$par_home_page/$local_home_page/g" $UPDATE_HOME/hm/resources/oem-resource.txt
	
	##replace copyright
	sed -i "s/$par_nms_copyright/$local_nms_copyright/g" $UPDATE_HOME/HiveManager/cas/WEB-INF/view/jsp/default/ui/includes/bottom.jsp
	sed -i "s/$par_nms_copyright/$local_nms_copyright/g" $UPDATE_HOME/hm/resources/oem-resource.txt
	
	##help file root url
	##sed -i "s/$par_help_url/$local_help_url/g" $UPDATE_HOME/hm/resources/oem-resource.txt
fi
cd $CATALINA_HOME
echo "oem replace string end"  >>$HM_SCRIPT_LOGFILE 2>&1 