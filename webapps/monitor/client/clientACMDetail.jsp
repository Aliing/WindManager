<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
 <link type="text/css" rel="stylesheet" href="<s:url value="/css/hm_tab.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>
 <link rel="stylesheet" type="text/css" href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css"  includeParams="none"/>" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/paginator/assets/skins/sam/paginator.css"  includeParams="none"/>" />

<script src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js"  includeParams="none"/>"></script>
<script src="<s:url value="/yui/element/element-min.js"  includeParams="none"/>"></script>
<script src="<s:url value="/yui/datasource/datasource-min.js"  includeParams="none"/>"></script>
<script src="<s:url value="/yui/paginator/paginator-min.js"  includeParams="none"/>"></script>
<script src="<s:url value="/yui/datatable/datatable-min.js"  includeParams="none"/>"></script>

 <style>
.activeRound{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/activeRound.png) no-repeat center;
	vertical-align:center;
}
.mdm-set-item-content-enroll{
	padding:0px 0 0px 0px
}
.mdm-set-item-content-enroll-location{
	padding:0px 0 0px 2px;
	width:500px;
}
.inactiveRound{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/inactiveRound.png) no-repeat center;
	vertical-align:center;
}
.css_icon_managed_yes{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/yes.png) no-repeat center;
	vertical-align:center;
}
.css_icon_managed_no{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/no.png) no-repeat center;
	vertical-align:center;
}
.css_log_status_notsend{
	width:16px;
	height:16px;
	float:left;
	display: inline-block;
	background:url(../hm/images/icons/osicon.png) no-repeat;
	background-position: -80px -22px;
}
.css_log_status_ok{
	width:16px;
	height:16px;
	float:left;
	display: inline-block;
	background:url(../hm/images/icons/osicon.png) no-repeat;
	background-position: -40px -2px;
}
.css_log_status_error{
	width:16px;
	height:16px;
	float:left;
	display: inline-block;
	background:url(../hm/images/icons/osicon.png) no-repeat;
	background-position: -60px -2px;
}
.css_log_status_formaterror{
	width:16px;
	height:16px;
	float:left;
	display: inline-block;
	background:url(../hm/images/icons/osicon.png) no-repeat;
	background-position: -60px -22px;
}
.css_log_status_warn{
	width:16px;
	height:16px;
	float:left;
	display: inline-block;
	background:url(../hm/images/icons/osicon.png) no-repeat;
	background-position: -80px -2px;
}
.locationIconInfo{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/loca_info.png) no-repeat center;
	vertical-align:center;
}
td.panelLabelAcm {
	padding: 0px 0px 5px 18px;
	width: 30%;
	color: #003366;
}

td.panelTextAcm {
	width: 70%;
}
.panelDivTd {
	padding: 12px 0 12px 0;
}
.yui-skin-sam .yui-dt table { 
	width: 100%; 
} 

.css_icon_encrypt_no{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/no.png) no-repeat center;
	vertical-align:center;
}

.css_icon_encrypt_yes{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/yes.png) no-repeat center;
	vertical-align:center;
}

.css_ostype_ios{
	width:12px;
	height:14px;
	float:left;
	display: inline-block;
	background:url(../hm/images/icons/osicon.png) no-repeat;
	background-position: -23px -3px;
}
.css_ostype_andriod{
	width:13px;
	height:16px;
	float:left;
	display: inline-block;
	background:url(../hm/images/icons/osicon.png) no-repeat;
	background-position: -3px -2px;
}
.css_ostype_chrome{
	width:14px;
	height:14px;
	float:left;
	display: inline-block;
	background:url(../hm/images/icons/osicon.png) no-repeat;
	background-position: -3px -21px;
}

</style>
<script>
var basicAcmProfile;
var basicAcmProfileData = [];
var profileDispalyFlag=false;

var basicAcmActivitylog;
var basicAcmActivitylogData = [];
var activitylogDispalyFlag=false;

var basicAcmScanResult;
var basicAcmScanResultData = [];
var scanResultDispalyFlag=false;

var basicAcmCertificate;
var basicAcmCertificateData = [];
var certificateDispalyFlag=false;


var currentDeMacAddress='<s:property value="%{macAddress}" />';
var currentCustomId='<s:property value="%{currentCustomId}" />';

YAHOO.example.acmProfilePagination = function() {
	basicAcmProfile=function (){
        var myColumnDefs = [
            {key:"displayname", label:"<s:text name='monitor.enrolled.device.profile.displayname'/>", sortable:true, resizeable:true},
            {key:"organization", label:"<s:text name='monitor.enrolled.device.profile.organization'/>", sortable:true, resizeable:true},
            {key:"encrypted", label:"<s:text name='monitor.enrolled.device.profile.encrypted'/>", sortable:true, resizeable:true},
            {key:"managed", label:"<s:text name='monitor.enrolled.device.profile.managed'/>",sortable:true, resizeable:true},
            {key:"configuredItems", label:"<s:text name='monitor.enrolled.device.profile.configuredItems'/>", sortable:true, resizeable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicAcmProfileData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["displayname","organization","encrypted","managed","configuredItems"]
        };

        var oConfigs = {
        		sortedBy : {key:"displayname", dir:YAHOO.widget.DataTable.CLASS_ASC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    //template: "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {CurrentPageReport}",
                    containers : 'acmProfilePage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("acmProfileDataDiv", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
}

YAHOO.example.acmActivitylogPagination = function() {
	basicAcmActivitylog=function (){
        var myColumnDefs = [
            {key:"status", label:"<s:text name='monitor.enrolled.device.activitylog.status'/>",  sortable:true, width:130, resizeable:true},
            {key:"name", label:"<s:text name='monitor.enrolled.device.activitylog.name'/>", sortable:true, resizeable:true},
            {key:"deviceName", label:"<s:text name='monitor.enrolled.device.activitylog.deviceName'/>", sortable:true, resizeable:true},
            {key:"startTime", label:"<s:text name='monitor.enrolled.device.activitylog.startTime'/>",sortable:true, resizeable:true},
            {key:"endTime", label:"<s:text name='monitor.enrolled.device.activitylog.endTime'/>", sortable:true, resizeable:true},
            {key:"failedReason", label:"<s:text name='monitor.enrolled.device.activitylog.failedReason'/>", sortable:true, resizeable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicAcmActivitylogData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["status","name","deviceName","startTime","endTime","failedReason"]
        };

        var oConfigs = {
        		sortedBy : {key:"startTime", dir:YAHOO.widget.DataTable.CLASS_DESC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    //template: "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {CurrentPageReport}",
                    containers : 'acmActivitylogPage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("acmActivitylogDataDiv", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
}

YAHOO.example.acmScanResultPagination = function() {
	basicAcmScanResult=function (){
        var myColumnDefs = [
            {key:"ssid", label:"<s:text name='monitor.enrolled.device.scanresult.ssid'/>", sortable:true, resizeable:true},
            {key:"bssid", label:"<s:text name='monitor.enrolled.device.scanresult.bssid'/>", sortable:true, resizeable:true},
            {key:"security", label:"<s:text name='monitor.enrolled.device.scanresult.security'/>", sortable:true, resizeable:true},
            {key:"band", label:"<s:text name='monitor.enrolled.device.scanresult.band'/>",sortable:true, resizeable:true},
            {key:"channelNumber", label:"<s:text name='monitor.enrolled.device.scanresult.channelNumber'/>", sortable:true, resizeable:true},
            {key:"strength", label:"<s:text name='monitor.enrolled.device.scanresult.strength'/>", sortable:true, resizeable:true},
            {key:"rssi", label:"<s:text name='monitor.enrolled.device.scanresult.rssi'/>", sortable:true, resizeable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicAcmScanResultData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["ssid","bssid","security","band","channelNumber","strength", "rssi"]
        };

        var oConfigs = {
        		sortedBy : {key:"bssid", dir:YAHOO.widget.DataTable.CLASS_ASC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    //template: "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {CurrentPageReport}",
                    containers : 'acmScanResultPage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("acmScanResultDataDiv", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
}

YAHOO.example.acmCertificatePagination = function() {
	basicAcmCertificate=function (){
        var myColumnDefs = [
            {key:"identity", label:"<s:text name='monitor.enrolled.device.certificate.identity'/>",  sortable:true, resizeable:true},
            {key:"name", label:"<s:text name='monitor.enrolled.device.certificate.name'/>", sortable:true, resizeable:true},
            {key:"issuedby", label:"<s:text name='monitor.enrolled.device.certificate.issuedby'/>", sortable:true, resizeable:true},
            {key:"notBefore", label:"<s:text name='monitor.enrolled.device.certificate.notBefore'/>",sortable:true, resizeable:true},
            {key:"notAfter", label:"<s:text name='monitor.enrolled.device.certificate.notAfter'/>", sortable:true, resizeable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(basicAcmCertificateData);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["identity","name","issuedby","notBefore","notAfter"]
        };

        var oConfigs = {
        		sortedBy : {key:"name", dir:YAHOO.widget.DataTable.CLASS_ASC},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 10,
                    alwaysVisible: false,
                    //template: "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {CurrentPageReport}",
                    containers : 'acmCertificatePage'
                })
        };
        var myDataTable = new YAHOO.widget.DataTable("acmCertificateDataDiv", myColumnDefs,
                myDataSource, oConfigs);
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
     }();  
}


function changeTabType(index) {
	if (index ==1) {
		Get("tr_network").style.display="";
		Get("tr_scanresult").style.display="none";
		Get("tr_activitylog").style.display="none";
		Get("tr_profile").style.display="none";
		Get("tr_certificate").style.display="none";
		$("#td_network").attr("class","tab_selectTabStyle");
		if ($("#td_scanresult")) {
			$("#td_scanresult").attr("class","tab_unSelectTabStyle");
		}
		$("#td_activitylog").attr("class","tab_unSelectTabStyle");
		if ($("#td_profile")) {
			$("#td_profile").attr("class","tab_unSelectTabStyle");
		}
		if ($("#td_certificate")) {
			$("#td_certificate").attr("class","tab_unSelectTabStyle");
		}
	} else if (index ==2) { 
		if (scanResultDispalyFlag==true) {
			Get("tr_network").style.display="none";
			Get("tr_scanresult").style.display="";
			Get("tr_activitylog").style.display="none";
			Get("tr_profile").style.display="none";
			Get("tr_certificate").style.display="none";
			$("#td_scanresult").attr("class","tab_selectTabStyle");
			$("#td_network").attr("class","tab_unSelectTabStyle");
			$("#td_activitylog").attr("class","tab_unSelectTabStyle");
			if ($("#td_profile")) {
				$("#td_profile").attr("class","tab_unSelectTabStyle");
			}
			if ($("#td_certificate")) {
				$("#td_certificate").attr("class","tab_unSelectTabStyle");
			}
		} else {
			var createAcmScanResultPanel = function(o) {
				if(waitingPanel != null){
					waitingPanel.hide();
				}
				eval("basicAcmScanResultData = " + o.responseText);
				Get("tr_network").style.display="none";
				Get("tr_scanresult").style.display="";
				Get("tr_activitylog").style.display="none";
				Get("tr_profile").style.display="none";
				Get("tr_certificate").style.display="none";
				$("#td_scanresult").attr("class","tab_selectTabStyle");
				$("#td_network").attr("class","tab_unSelectTabStyle");
				$("#td_activitylog").attr("class","tab_unSelectTabStyle");
				if ($("#td_profile")) {
					$("#td_profile").attr("class","tab_unSelectTabStyle");
				}
				if ($("#td_certificate")) {
					$("#td_certificate").attr("class","tab_unSelectTabStyle");
				}
				if (basicAcmScanResultData.length==0) {
					Get("acmScanResultDataDiv").innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No items were found.</br></br>"
				} else {
					Get("acmScanResultDataDiv").innerHTML="";
					YAHOO.example.acmScanResultPagination();
				}
				scanResultDispalyFlag=true;
			};
			
			var url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=showAcmScanResultPanel&currentCustomId="+ currentCustomId +"&currentDeMacAddress="+ currentDeMacAddress +"&ignore=" + new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createAcmScanResultPanel, failure:abortResult,timeout: 60000}, null);
			if(waitingPanel != null){
				waitingPanel.show();
			}
		}
	} else if(index ==3) { 
		if (profileDispalyFlag==true) {
			Get("tr_network").style.display="none";
			Get("tr_scanresult").style.display="none";
			Get("tr_activitylog").style.display="none";
			Get("tr_profile").style.display="";
			Get("tr_certificate").style.display="none";
			if ($("#td_scanresult")) {
				$("#td_scanresult").attr("class","tab_unSelectTabStyle");
			}
			$("#td_network").attr("class","tab_unSelectTabStyle");
			$("#td_activitylog").attr("class","tab_unSelectTabStyle");
			$("#td_profile").attr("class","tab_selectTabStyle");
			if ($("#td_certificate")) {
				$("#td_certificate").attr("class","tab_unSelectTabStyle");
			}
		} else {
			var createAcmProfilePanel = function(o) {
				if(waitingPanel != null){
					waitingPanel.hide();
				}
				eval("basicAcmProfileData = " + o.responseText);
				Get("tr_network").style.display="none";
				Get("tr_scanresult").style.display="none";
				Get("tr_activitylog").style.display="none";
				Get("tr_profile").style.display="";
				Get("tr_certificate").style.display="none";
				if ($("#td_scanresult")) {
					$("#td_scanresult").attr("class","tab_unSelectTabStyle");
				}
				$("#td_network").attr("class","tab_unSelectTabStyle");
				$("#td_activitylog").attr("class","tab_unSelectTabStyle");
				$("#td_profile").attr("class","tab_selectTabStyle");
				if ($("#td_certificate")) {
					$("#td_certificate").attr("class","tab_unSelectTabStyle");
				}
				if (basicAcmProfileData.length==0) {
					Get("acmProfileDataDiv").innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No items were found.</br></br>"
				} else {
					Get("acmProfileDataDiv").innerHTML="";
					YAHOO.example.acmProfilePagination();
				}
				profileDispalyFlag=true;
			};
			
			var url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=showAcmProfilePanel&currentCustomId="+ currentCustomId +"&currentDeMacAddress="+ currentDeMacAddress +"&ignore=" + new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createAcmProfilePanel, failure:abortResult,timeout: 60000}, null);
			if(waitingPanel != null){
				waitingPanel.show();
			}
		}
	} else if(index ==4) { 
		if (certificateDispalyFlag==true) {
			Get("tr_network").style.display="none";
			Get("tr_scanresult").style.display="none";
			Get("tr_activitylog").style.display="none";
			Get("tr_profile").style.display="none";
			Get("tr_certificate").style.display="";
			if ($("#td_scanresult")) {
				$("#td_scanresult").attr("class","tab_unSelectTabStyle");
			}
			$("#td_network").attr("class","tab_unSelectTabStyle");
			$("#td_activitylog").attr("class","tab_unSelectTabStyle");
			if ($("#td_profile")) {
				$("#td_profile").attr("class","tab_unSelectTabStyle");
			}
			$("#td_certificate").attr("class","tab_selectTabStyle");
		} else {
			var createAcmCertificatePanel = function(o) {
				if(waitingPanel != null){
					waitingPanel.hide();
				}
				eval("basicAcmCertificateData = " + o.responseText);
				Get("tr_network").style.display="none";
				Get("tr_scanresult").style.display="none";
				Get("tr_activitylog").style.display="none";
				Get("tr_profile").style.display="none";
				Get("tr_certificate").style.display="";
				if ($("#td_scanresult")) {
					$("#td_scanresult").attr("class","tab_unSelectTabStyle");
				}
				$("#td_network").attr("class","tab_unSelectTabStyle");
				$("#td_activitylog").attr("class","tab_unSelectTabStyle");
				if ($("#td_profile")) {
					$("#td_profile").attr("class","tab_unSelectTabStyle");
				}
				$("#td_certificate").attr("class","tab_selectTabStyle");
				
				if (basicAcmCertificateData.length==0) {
					Get("acmCertificateDataDiv").innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No items were found.</br></br>"
				} else {
					Get("acmCertificateDataDiv").innerHTML="";
					YAHOO.example.acmCertificatePagination();
				}
				certificateDispalyFlag=true;
			};
			
			var url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=showAcmCertificatePanel&currentCustomId="+ currentCustomId +"&currentDeMacAddress="+ currentDeMacAddress +"&ignore=" + new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createAcmCertificatePanel, failure:abortResult,timeout: 60000}, null);
			if(waitingPanel != null){
				waitingPanel.show();
			}
		}
	} else if(index ==5) { 
		if (activitylogDispalyFlag==true) {
			Get("tr_network").style.display="none";
			Get("tr_scanresult").style.display="none";
			Get("tr_activitylog").style.display="";
			Get("tr_profile").style.display="none";
			Get("tr_certificate").style.display="none";
			if ($("#td_scanresult")) {
				$("#td_scanresult").attr("class","tab_unSelectTabStyle");
			}
			$("#td_network").attr("class","tab_unSelectTabStyle");
			$("#td_activitylog").attr("class","tab_selectTabStyle");
			if ($("#td_profile")) {
				$("#td_profile").attr("class","tab_unSelectTabStyle");
			}
			if ($("#td_certificate")) {
				$("#td_certificate").attr("class","tab_unSelectTabStyle");
			}
		} else {
			var createAcmActivityLogPanel = function(o) {
				if(waitingPanel != null){
					waitingPanel.hide();
				}
				eval("basicAcmActivitylogData = " + o.responseText);
				Get("tr_network").style.display="none";
				Get("tr_scanresult").style.display="none";
				Get("tr_activitylog").style.display="";
				Get("tr_profile").style.display="none";
				Get("tr_certificate").style.display="none";
				if ($("#td_scanresult")) {
					$("#td_scanresult").attr("class","tab_unSelectTabStyle");
				}
				$("#td_network").attr("class","tab_unSelectTabStyle");
				$("#td_activitylog").attr("class","tab_selectTabStyle");
				if ($("#td_profile")) {
					$("#td_profile").attr("class","tab_unSelectTabStyle");
				}
				if ($("#td_certificate")) {
					$("#td_certificate").attr("class","tab_unSelectTabStyle");
				}
				
				if (basicAcmActivitylogData.length==0) {
					Get("acmActivitylogDataDiv").innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No items were found.</br></br>"
				} else {
					Get("acmActivitylogDataDiv").innerHTML="";
					YAHOO.example.acmActivitylogPagination();
				}
				activitylogDispalyFlag=true;
			};
			
			var url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=showAcmActivityLogPanel&currentCustomId="+ currentCustomId +"&currentDeMacAddress="+ currentDeMacAddress +"&ignore=" + new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createAcmActivityLogPanel, failure:abortResult,timeout: 60000}, null);
			if(waitingPanel != null){
				waitingPanel.show();
			}
		}
	}
}

function showEnrolledClientDetails(url) {
	window.open(url, target='_blank');
}

</script>
				

<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
	<tr>
		<th colspan="2" align="left"><s:text
			name="monitor.enrolled.device.datail.info" /></th>
	</tr>
	<tr>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td colspan="2" class="labelT1"><b><s:text name="monitor.enrolled.device.active.client.detail.info"/></b>
					(&nbsp;<a href="javascript: void(0);" onclick="showEnrolledClientDetails('<s:property value="acmUrlSuffix" />')"><s:property value="enrolledClientDetail.deviceName"/></a>&nbsp;)</td>
				</tr>
				<tr>
					<td class="panelLabelAcm" width="150px"><s:text name="monitor.enrolled.client.status"/></td>
					<td class="panelTextAcm">
					<s:if test="%{enrolledClientDetail.activeStatusOn}">
						<span class="activeRound"></span>
					</s:if>
					<s:else>
						<span class="inactiveRound"/></span>
					</s:else>
					<s:property value="enrolledClientDetail.activeStatusString"/>
					</td>
				</tr>
				<!--  tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.client.connect"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.lastConnectedTimeString"/></td>
				</tr>
				-->
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.general.info.phone"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientNetworkInfo.phoneNumber"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.client.plat.form"/></td>
					<td class="panelTextAcm">
					<s:if test="%{enrolledClientDetail.osTypeApple}" >
						<span class="css_ostype_ios"></span>&nbsp;
					</s:if>
					<s:elseif test="%{enrolledClientDetail.osTypeAndroid}" >
						<span class="css_ostype_andriod"></span>&nbsp;
					</s:elseif>
					<s:elseif test="%{enrolledClientDetail.osTypeChrome}" >
						<span class="css_ostype_chrome"></span>&nbsp;
					</s:elseif>
					<s:property value="enrolledClientDetail.osTypeString"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.client.os.version"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.osVersion"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.client.build.version"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.buildVersion"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.general.info.device.type"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.modelName"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.client.sys.mode"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.productName"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.client.modelNumber"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.model"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.client.serialNumber"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.serialNumber"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.client.warranty"/></td>
					<td class="panelTextAcm">
					<s:if test="%{enrolledClientDetail.osTypeApple}">
					<a target="_blank" href="https://selfsolve.apple.com/wcResults.do?cn=&amp;locale=&amp;caller=&amp;num=REMOTE&amp;sn=<s:property value="enrolledClientDetail.serialNumber"/>">Apple</a>
					</s:if>
					<s:else>
						--
					</s:else>
					</td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.general.info.udid"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.udid"/></td>
				</tr>
				<s:if test="%{enrolledClientDetail.osTypeMacOsx}">
				<tr>
					<td colspan="2" class="labelT1"><b><s:text name="monitor.enrolled.device.general.info.userinfo"/></b></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.general.info.userinfo.userid"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.userId"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.general.info.userinfo.username"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.userLongName"/></td>
				</tr>
				</s:if>
				<s:if test="%{!enrolledClientDetail.osTypeChrome}">
				<tr>
					<td colspan="2" class="labelT1"><b><s:text name="monitor.enrolled.device.general.info.diskUsage"/></b></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.general.info.storage"/></td>
					<td>
						<div>
							<p style="border:1px solid #8692A6;height:18px;position:relative;width:230px;">
								<span style="background-color:#A4B73E;height:16px;left:1px;position:1px;top:1px;width:<s:property value="enrolledClientDetail.storagePercentage"/>;position:absolute;">
								
								</span>
							</p>
							<s:property value="enrolledClientDetail.availableDeviceCapacityString"/> GB free of  <s:property value="enrolledClientDetail.deviceCapacityString"/> GB
						</div>
					</td>
				</tr>
				</s:if>
				
				<s:if test="%{!enrolledClientDetail.osTypeMacOsx}">
				<tr>
					<td colspan="2" class="labelT1"><b><s:text name="monitor.enrolled.device.general.info.batteryStatus"/></b></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.general.info.battery.level"/></td>
					<td>
						<div>
							<p style="border:1px solid #8692A6;height:18px;position:relative;width:230px;">
								<span style="background-color:#A4B73E;height:16px;left:1px;position:1px;top:1px;width:<s:property value="enrolledClientDetail.batteryLevelString"/>;position:absolute;">
								
								</span>
							</p>
							<s:property value="enrolledClientDetail.batteryLevelString"/> Capacity
						</div>
					</td>
				</tr>
				</s:if>
			</table>
		
		</td>
		
		<td>
		 <%-- 
		 <div class="mdm-set-item-content-enroll-location" style="display:block;">
				<div id="locationDiv" class="edit">
					<table>
						<tr>
							<td width="190px"><s:text name="monitor.enrolled.device.loc.latt.longi" /></td>
								<s:if test="%{enrolledClientDetail.latitude == '' || enrolledClientDetail.longitude == ''}">
									<td title="<s:text name='monitor.enrolled.device.loc.aero.def.info'/>">
									<div style="width: 100%;">-122.013881,37.409608</div></td>
								</s:if>
								<s:else>
									<td title="<s:property value='enrolledClientDetail.address'/>">
									<div style="width: 100%;" id="long_lati_location"><s:property value="enrolledClientDetail.latitude" /><s:property value="enrolledClientDetail.longitude" />
									</div></td>
								</s:else>
						</tr>
						<tr>
							<td colspan="2"><div id="J-map" style="min-width: 440px;min-height: 300px"></div></td>
						</tr>
					</table>
				</div>
			</div>
		--%>
		</td>
	
	</tr>
	
	<tr>
		<td class="menu_bg" style="padding-top:10px">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="15px" class="tab_unSelectStyle">&nbsp;&nbsp;&nbsp;</td>
					<td id="td_network" class="tab_selectTabStyle" nowrap>
						<a href="javascript:void(0);" onclick="changeTabType(1);">
						<s:text name="monitor.enrolled.device.network.tab"/></a></td>
					<s:if test="%{enrolledClientDetail.needScanResultData}">
					<td id="td_scanresult" class="tab_unSelectTabStyle" nowrap>
						<a href="javascript:void(0);" onclick="changeTabType(2);">
						<s:text name="monitor.enrolled.device.scanresult.tab"/></a>
					</td>
					</s:if>
					<s:if test="%{!enrolledClientDetail.osTypeChrome}">
					<td id="td_profile" class="tab_unSelectTabStyle" nowrap>
						<a href="javascript:void(0);" onclick="changeTabType(3);">
						<s:text name="monitor.enrolled.device.profile.tab"/></a>
					</td>
					<td id="td_certificate" class="tab_unSelectTabStyle" nowrap>
						<a href="javascript:void(0);" onclick="changeTabType(4);">
						<s:text name="monitor.enrolled.device.certificate.tab"/></a>
					</td>
					</s:if>
					<td id="td_activitylog" class="tab_unSelectTabStyle" nowrap>
						<a href="javascript:void(0);" onclick="changeTabType(5);">
						<s:text name="monitor.enrolled.device.activitylog.tab"/></a>
					</td>
					<td width="100%" class="tab_unSelectStyle">&nbsp;&nbsp;&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>

	<tr id="tr_network">
		<td class="panelDivTd">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td colspan="2" class="labelT1"><b><s:text name="monitor.enrolled.device.network.cellular"/></b></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.network.cellular.tech.info"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.cellularTechnologyString"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.network.operator.info"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientNetworkInfo.simCarrierNetwork"/></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.network.modem.info"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientDetail.modemFirmwareVersion"/></td>
				</tr>
				<s:if test="%{enrolledClientDetail.displayCellularRssi}">
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.network.rssi"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientNetworkInfo.cellRssi"/></td>
				</tr>
				</s:if>
				<tr>
					<td colspan="2" class="labelT1"><b><s:text name="monitor.enrolled.device.network.wifi"/></b></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.network.wifi.macAddress"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientNetworkInfo.wifiMac"/></td>
				</tr>
				<s:if test="%{enrolledClientDetail.displayWifiRssi}">
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.network.rssi"/></td>
						<td class="panelTextAcm"><s:property value="enrolledClientNetworkInfo.wifiRssi"/></td>
					</tr>
				</s:if>
				<s:if test="%{enrolledClientDetail.displayWifiSsid}">
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.network.wifi.ssid"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientNetworkInfo.ssid"/></td>
				</tr>
				</s:if>
				<s:if test="%{enrolledClientDetail.displayWifiBssid}">
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.network.wifi.bssid"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientNetworkInfo.bssid"/></td>
				</tr>
				</s:if>
				<s:if test="%{enrolledClientDetail.displayWifiLinkSpeed}">
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.network.wifi.linkspeed"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientNetworkInfo.linkSpeed"/></td>
				</tr>
				</s:if>
				<tr>
					<td colspan="2" class="labelT1"><b><s:text name="monitor.enrolled.device.network.blue"/></b></td>
				</tr>
				<tr>
					<td class="panelLabelAcm"><s:text name="monitor.enrolled.device.network.blue.macAddress"/></td>
					<td class="panelTextAcm"><s:property value="enrolledClientNetworkInfo.blueToothMAC"/></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr id="tr_scanresult" style="display: none;">
		<td class="panelDivTd">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td align="right" style="padding-right: 30px"><div id="acmScanResultPage"></div></td>
				</tr>
				<tr>
					<td><div id="acmScanResultDataDiv"></div></td>
				</tr>
			</table>
		</td>
	</tr>	
	<tr id="tr_profile" style="display: none;">
		<td class="panelDivTd">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td align="right" style="padding-right: 30px"><div id="acmProfilePage"></div></td>
				</tr>
				<tr>
					<td><div id="acmProfileDataDiv"></div></td>
				</tr>
			</table>
		</td>
	</tr>	
	<tr id="tr_certificate" style="display: none;">
		<td class="panelDivTd">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td align="right" style="padding-right: 30px"><div id="acmCertificatePage"></div></td>
				</tr>
				<tr>
					<td><div id="acmCertificateDataDiv"></div></td>
				</tr>
			</table>
		</td>
	</tr>	
	<tr id="tr_activitylog" style="display: none;">
		<td class="panelDivTd">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td align="right" style="padding-right: 30px"><div id="acmActivitylogPage"></div></td>
				</tr>
				<tr>
					<td><div id="acmActivitylogDataDiv"></div></td>
				</tr>
			</table>
		</td>
	</tr>		
</table>

	
 
 <%-- <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA-rO5NL7YvRJ2LxEfBIugDeOtpBdfKzzs&sensor=false"></script> --%>
 <%-- 
<s:if test="enableGoogleMapKey == false">
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3&sensor=false<s:property escape="false" value="gmeKey"/>"></script>
</s:if>
<s:else>
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=<s:property escape="false" value="gmeKey"/>&sensor=false"></script>
</s:else>

--%>
<script >
/**
	var latitude="<s:property value="enrolledClientDetail.latitude"/>";
	var longitude="<s:property value="enrolledClientDetail.longitude"/>";
	var mapAddress="<s:property value="enrolledClientDetail.address"/>";
	var ipAddress="<s:property value="enrolledClientDetail.publicIp"/>";

	$(function() { 
		 var location = {
	            areacode : '',
	            city : '',
	            country_code : '',
 	            country_name : mapAddress,
	            ip : ipAddress,
	            latitude : latitude,
	            longitude : longitude,
	            metrocode : '',
	            region_code : '',
	            region_name : '',
	            zipcode :''
	    }, ipQueryReturned = false;
	    var long_lati_location = $('#long_lati_location');
	    
	    if(location.latitude == '' || location.longitude == ''){
	        var url = 'http://freegeoip.net/json/' + location.ip;
	        $.get(url, function(result){
	            location = result;
	            ipQueryReturned = true;
	        }, 'json');
	    }else{
	        ipQueryReturned = true;
	    }
	    
	    var initialize = function(){
	        // default ,we suppose we can't find the latitude and longitude
	        var zoom = 16;
	        if(location.latitude == '' || location.longitude == ''){
	            location.country_name = 'Aerohive Networks 330 Gibraltar Drive Sunnyvale, CA 94089 United States';
	            location.latitude = 37.409608, location.longitude = -122.013881;
	        }
	        if(long_lati_location.length){
	            long_lati_location.attr('title', location.city+' '+location.region_name+' '+location.country_name);
	            long_lati_location.html(location.longitude + ' , ' + location.latitude);
	        }
	        var center = new google.maps.LatLng(location.latitude, location.longitude);
	       var mapOptions = {
	                center: center,
	                scaleControl: true,
	                zoom: zoom,
	                mapTypeId: google.maps.MapTypeId.ROADMAP,
	                panControl: true,
	                streetViewControl: false,
	                zoomControl: true
	                };
	        var map = new google.maps.Map(document.getElementById('J-map'),mapOptions);
	        var infowindow = new google.maps.InfoWindow(); 
	        infowindow.setContent("<div style='min-height:80px;'><table><tr><td>Address:</td><td>"+location.country_name+' '+location.region_name+' '+location.city+"</td></tr><tr><td>Longitude:</td><td>"+location.longitude+"</td></tr><tr><td>Latitude:</td><td>"+location.latitude+"</td></tr></table></div>");
	        var marker = new google.maps.Marker({
	            position: center,
	            map: map
	        });
	        google.maps.event.addListener(marker, 'click', function(event) {
	            infowindow.open(map, marker);
	       }); 
	    };
	  	 window.onload = function(){
	        var interval = setInterval(function(){
	            if(ipQueryReturned){
	                clearInterval(interval);
	                initialize();
	            }
	        }, 100);
	    };
	});
	// Overlay for waiting dialog
	//createWaitingPanel();
	//function refresh()
	//{
	//    showWaiting();
	//    location.reload();
	//} 
**/

	
</script>
 <%-- 	
<script type="text/javascript" src="js/config/txt_configs.js"></script>
<script type="text/javascript" src="js/config/form_configs.js"></script>
<script type="text/javascript" src="js/common/tools.js"></script>
<script type="text/javascript" src="js/pluins/google_map_load.js"></script>
<script type="text/javascript" src="js/pluins/data.table.js"></script>
<script type="text/javascript" src="js/app/enrolled_detail.js"></script>
<script type="text/javascript">
	//AE.Mod.init();
</script>
--%>