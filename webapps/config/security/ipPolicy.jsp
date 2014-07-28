<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.IpPolicyRule"%>
<script src="<s:url value="/js/hm.newsimpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/ahdatatable/ahdatatable.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/tableSort.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/tabview/assets/skins/sam/tabview.css" includeParams="none" />" />
<style>
    .ul {
    width:40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UL.png) no-repeat left top;
    }
    .um {
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UM.png) repeat-x center top;
    }
    .ur {
    width:40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UR.png) no-repeat right top;
    }
    .ml {
    width:40px;
    height:100%;
    background: transparent url(images/hm_v2/popup/HM-Popup-ML.png) repeat-y 0% 50%;
    }
    .mm {
    height:100%;
    background-color: #f9f9f7;
    }
    .mr {
    width:40px;
    height:100%;
    background: transparent url(images/hm_v2/popup/HM-Popup-MR.png) repeat-y 100% 50%;
    }
    .bl {
    width: 40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LL.png) no-repeat left bottom;
    }
    .bm {
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LM.png) repeat-x center bottom;
    }

	.br {
	width: 40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LR.png) no-repeat right bottom;
    }

#newServicePanelId.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}

.innerPage {
	background-color: #f9f9f7;
}
</style>
<style type="text/css">
.divcontainer {
	border-top: 1px solid #666666;
	border-bottom: 1px solid #666666;
	overflow-x: auto;
	overflow-y: hidden;
	width: 300px;
	height: 30px;
}
div.container {
	border-bottom: 1px solid #666666;
	overflow-x: hidden;
	overflow-y: auto;
	width: 300px;
	height: 250px;
}
table.show {
	width: 300px;
}
.thservicechk {
	width: 50px;
    background-color: #EEEEEE;
    vertical-align: middle;
}
.thservicehead{
	width: 240px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
table.show tr td {
	height: 20px;
}
#newNetworkServicePanelId.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}
.tableTdTextAlign{
	text-align: left;
}
#search_service_key{
border-radius:10px 10px 10px 10px;
color: #777777;
font-size: 12px;
border: 1px solid #DDDDDD;
height: 15px;
width: 140px;
padding: 2px 2px 2px 2px;
}
#searchServiceKeyContainerDiv{width: 140px; padding-top: 2px;height:22px;}
</style>
<style type="text/css">
.appdivcontainer {
	border-top: 1px solid #666666;
	border-bottom: 1px solid #666666;
	overflow-x: auto;
	overflow-y: hidden;
	width: 480px;
	height: 40px;
}
.customappdivcontainer {
	border-top: 1px solid #666666;
	border-bottom: 1px solid #666666;
	overflow-x: auto;
	overflow-y: hidden;
	width: 480px;
	height: 30px;
}
div.appcontainer {
	border-bottom: 1px solid #666666;
	overflow-x: hidden;
	overflow-y: auto;
	width: 480px;
	height: 250px;
}
table.appshow {
	width: 490px;
}
.thchk {
	width: 30px;
    background-color: #EEEEEE;
    vertical-align: middle;
}
.thapphead{
	width: 130px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
.thhead{
	width: 75px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
.thcustomapphead{
	width: 150px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
.thcustomhead{
	width: 130px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
table.appshow tr td {
	height: 20px;
}
.tdsystemchk{
	align: center;
	width: 30px;
	padding-left:8px;
	vertical-align: middle;
}
.tdsystemappname{width:120px;word-break:break-all;}
.tdsystemusage{
	align: right;
	width: 75px;
	vertical-align: middle;
}
.tdsystemgroup{
	width: 115px;
	padding-left: 8px;
	word-break:break-all;
}
.tdcustomchk{
	align: center;
	width: 30px;
	padding-left:8px;
	vertical-align: middle;
}
.tdcustomappname{width:150px;word-break:break-all;}
.tdcustomusage{
	align: right;
	width: 130px;
	padding-right: 5px;
	vertical-align: middle;
}
#system_search_key{
border-radius:10px 10px 10px 10px;
color: #777777;
font-size: 12px;
border: 1px solid #DDDDDD;
height: 15px;
width: 140px;
padding: 2px 2px 2px 2px;
}
#custom_search_key{
border-radius:10px 10px 10px 10px;
color: #777777;
font-size: 12px;
border: 1px solid #DDDDDD;
height: 15px;
width: 140px;
padding: 2px 2px 2px 2px;
}
#system_search_groupkey{
border-radius:10px 10px 10px 10px;
color: #777777;
font-size: 12px;
border: 1px solid #DDDDDD;
height: 15px;
width: 140px;
padding: 2px 2px 2px 2px;
}
#searchKeyContainerDiv{width: 140px; padding-top: 2px;height:22px;}
#searchGroupKeyContainerDiv{width: 140px; padding-top: 2px;height:22px;}
#searchCustomKeyContainerDiv{width: 140px; padding-top: 2px;height:22px;}
.searchElement{display: block;float: left;}
</style>
<script>!window.jQuery && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script src="<s:url value="/js/widget/dataTable/ahDataTableForIpPolicy.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/innerhtml.js" />"></script>
<script src="<s:url value="/js/underscore-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/drag/ahdrag.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/tableSort.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
var formName = 'ipPolicy';
var ACTION_PERMIT = <%=IpPolicyRule.POLICY_ACTION_PERMIT%>;
var ACTION_DENY = <%=IpPolicyRule.POLICY_ACTION_DENY%>;
var ACTION_NAT = <%=IpPolicyRule.POLICY_ACTION_NAT%>;
var ACTION_TRAFFIC_DROP = <%=IpPolicyRule.POLICY_ACTION_TRAFFIC_DROP%>;
function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_policyName").disabled) {
		document.getElementById(formName + "_dataSource_policyName").focus();
	}
	// Overlay for waiting dialog
	createWaitingPanel();
	<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(1010, 650);
		}
	</s:if>
	var ahIpPolicyDtDatas = eval('<s:property escape="false" value="ahIpPolicyDtDatas"/>');
	var ahIpPolicyDtClumnDefs = eval('<s:property escape="false" value="ahIpPolicyDtClumnDefs"/>');
	var editIpPolicyInfo = '<s:property  escape="false" value="editIpPolicyInfo"/>';
	onLoadIpPolicyAhDataTable(ahIpPolicyDtClumnDefs,ahIpPolicyDtDatas,editIpPolicyInfo);
	
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation=="newIpAddress" || operation=="editSourceIpAddress" || operation=="editDestIpAddress"
			|| operation=="newService" || operation=="editService") {
			<s:if test="%{jsonMode==true}">
				if (parent!=null && !parent.isIFrameDialogOpen()) {
					//do nothing now
				} else {
					if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
						showProcessing();
					}
					document.forms[formName].operation.value = operation;
					document.forms[formName].parentIframeOpenFlg.value = true;
				   	document.forms[formName].submit();
				}
			</s:if>
			<s:else>
				if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
					showProcessing();
				}
				document.forms[formName].operation.value = operation;
			   	document.forms[formName].submit();
			</s:else>
		} else {
			<s:if test="%{jsonMode==true}">
				<s:if test="%{!parentIframeOpenFlg}">
				 if ('cancel<s:property value="lstForward"/>' == operation) {
						parent.closeIFrameDialog();	
						return;
					} else if ('create<s:property value="lstForward"/>' == operation || 'update<s:property value="lstForward"/>' == operation){
						saveIpPolicy(operation);
						return;
					} 
				</s:if>
			</s:if>
			if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
				showProcessing();
			}
			document.forms[formName].operation.value = operation;
	    	document.forms[formName].submit();
		}
	}
}

function saveIpPolicy(operation) {
	var url= "<s:url action='ipPolicy' includeParams='none' />"+ "?jsonMode=true" +"&ignore="+new Date().getTime();
	 document.forms["ipPolicy"].operation.value = operation;
	 YAHOO.util.Connect.setForm(document.forms["ipPolicy"]);
	 var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveIpPolicy, failure : failSaveIpPolicy, timeout: 60000}, null);
}

var succSaveIpPolicy = function (o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			parent.closeIFrameDialog();
			if(details.id != null && details.name != null){	
				if (details.parentDomID) {
					var parentDomIDs = details.parentDomID.split(',');
					if (parentDomIDs) {
						for(var i=0;i<parentDomIDs.length;i++) {
							var parentIpPolicySelect = parent.document.getElementById(parentDomIDs[i]);
							if(parentIpPolicySelect != null) {
								//dynamicAddSelect(parentMacPolicySelect,details.name, details.id);
								if (i==0) {
									hm.util.insertSelectValue(details.id,details.name,parentIpPolicySelect,false,true);	
								} else {
									hm.util.insertSelectValue(details.id,details.name,parentIpPolicySelect,false,false);	
								}
								 
							}
						}
					} 
				}
			}
		}
	}catch(e){
		alert("error")
		
		return;
	}
}

var failSaveIpPolicy = function(o){
	
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_IP_POLICY%>' == operation || operation == 'newIpAddress' || operation == 'newService'
	 	|| operation == 'cancel<s:property value="lstForward"/>') {
		return true;
	}
	var name = document.getElementById(formName + "_dataSource_policyName");
	if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'create'){
		var message = hm.util.validateDirectoryName(name.value, '<s:text name="config.ipPolicy.policyName" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}else{
    		var nameVal = name.value;
    		if(nameVal.toLowerCase() == "null"){
    			hm.util.reportFieldError(name, '<s:text name="config.ipPolicy.policyName" />'+" cannot be '"+nameVal+"'");
        		name.focus();
               	return false;
    		}
    	}
    }
	var inputElement = document.getElementById(formName + "_dataSource_description");
	var description = inputElement.value;
	if(description.toLowerCase() == "null"){
		hm.util.reportFieldError(inputElement, '<s:text name="config.ipPolicy.description" />'+" cannot be '"+description+"'");
       	inputElement.focus();
       	return false;
	}
	return true;
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="ipPolicy" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedPolicyName" />\'</td>');
		</s:else>
	</s:else>	
}

</script>
<div id="content"><s:form action="ipPolicy">
	<s:hidden name="ipAddressId" />
	<s:hidden  id="selectedServiceId" name="netServiceId" />
	<s:hidden name="refreshPageFlag"/>
	<s:hidden name="parentDomID" />
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentIframeOpenFlg"/>
		<div class="topFixedTitle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="90%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-IP_Policies.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.title.ipPolicy.new"/>
								</s:if> <s:else>
									<s:text name="config.title.ipPolicy.edit"/>
								</s:else>
							</td>
							<td style="padding-left:10px;">
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="submitAction('cancel<s:property value="lstForward"/>');" title="Cancel"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton">
								<s:if test="'' == writeDisabled">
									<a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a>
								</s:if>
								</td>
							</s:if>
							<s:else>
								<td class="npcButton">
								<s:if test="%{'' == updateDisabled}">
									<a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
								</s:if>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		</table>
		</div>
	</s:if>
	<s:else>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_IP_POLICY%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:else>
					
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</s:else>
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td class="labelT1" width="90"><label><s:text
									name="config.ipPolicy.policyName" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield size="24" name="dataSource.policyName"
									maxlength="%{policyNameLength}" disabled="%{disabledName}" 
									onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
									name="config.ssid.ssidName_range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="config.ipPolicy.description" /></label></td>
								<td><s:textfield size="48" name="dataSource.description"
									maxlength="%{commentLength}" />&nbsp;<s:text
									name="config.ssid.description_range" /></td>
							</tr>
							<tr>
								<td colspan="2" style="padding-left: 0 10px 0 10px">
									<table width="100%" cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
											<label id="serviceTableError"></label>
											</td>
											<td>
											<div id="ahIpPolicyDataTable"></div>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td height="2"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="10"></td>
				</tr>
			</table>
		</td>
		</tr>
	</table>
</s:form></div>
<div id="newServicePanelId" style="display: none;top:20px; ">
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="100%">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ul"></td><td class="um" id="tdUM_selectService" style="width:700px;"></td><td class="ur"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
			<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ml"></td>
					<td class="mm">
						<div id="newServicePanelContentId"></div>
					</td>
					<td class="mr"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
		    <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="bl"></td><td class="bm" id="tdBM_selectService" style="width:700px;"></td><td class="br"></td>
				</tr>
			</table>
		</td></tr>
	</table>
	<s:hidden name="selectedServices" id="selectedServices"/>
</div>
<script>
var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"260px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("Preparing resources...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}
var newServicePanel = null;
function preparePanels4SelectService() {
	var div = document.getElementById('newServicePanelId');
	newServicePanel = new YAHOO.widget.Panel(div, {
		width:"250px",
		underlay: "none",
		fixedcenter:"contained",
		visible:false,
		draggable:false,
		close:false,
		modal:true,
		constraintoviewport:true,
		zIndex:5
		});
	newServicePanel.render(document.body);
	div.style.display = "";
}

function fetchSelectServiceNewDlg(value) {
	var url = "";
	if(value == "1"){
		<s:if test="%{jsonMode==true}">
			if (top.isIFrameDialogOpen()) {
				top.changeIFrameDialog(1150, 750);
			}
		</s:if>
		$("#newServicePanelId").width(800);
		$("#tdUM_selectService").width(750);
		$("#tdBM_selectService").width(750);
		<s:if test="dataSource.id == null">
		url = "<s:url action='ipPolicy' includeParams='none' />?operation=selectIpPolicyService&jsonMode=<s:property value="%{jsonMode}"/>"
		 + "&ignore="+new Date().getTime();
		</s:if>
		<s:else>
		url = "<s:url action='ipPolicy' includeParams='none' />?operation=selectIpPolicyService&jsonMode=<s:property value="%{jsonMode}"/>" + "&id=<s:property value="%{dataSource.id}"/>"
		 + "&ignore="+new Date().getTime();
		</s:else>
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchServiceNewDlg, failure : resultDoNothing, timeout: 60000}, null);
		if(waitingPanel != null){
			waitingPanel.show();
		}
	}else if(value == "2"){
		<s:if test="%{jsonMode==true}">
			if (top.isIFrameDialogOpen()) {
				top.changeIFrameDialog(1250, 780);
			}
		</s:if>
		$("#newServicePanelId").width(1150);
		$("#tdUM_selectService").width(1080);
		$("#tdBM_selectService").width(1080);
		<s:if test="dataSource.id == null">
		url = "<s:url action='ipPolicy' includeParams='none' />?operation=selectIpPolicyAppService"
		 + "&ignore="+new Date().getTime();
		</s:if>
		<s:else>
		url = "<s:url action='ipPolicy' includeParams='none' />?operation=selectIpPolicyAppService" + "&id=<s:property value="%{dataSource.id}"/>"
		 + "&ignore="+new Date().getTime();
		</s:else>
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchServiceNewDlg, failure : resultDoNothing, timeout: 60000}, null);
		if(waitingPanel != null){
			waitingPanel.show();
		}
	}
}

var succFetchServiceNewDlg = function(o) {
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	set_innerHTML("newServicePanelContentId",
			o.responseText);
	YAHOO.util.Event.onContentReady("newServicePanelContentId", showSelectServicePanel, this);
}

var resultDoNothing = function(){}

function showSelectServicePanel(){
	if(null != newServicePanel){
		newServicePanel.cfg.setProperty('visible', true);
		newServicePanel.center();
	}
}

function hideSelectServicePanel(){
	if(null != newServicePanel){
		set_innerHTML("newServicePanelContentId", "");
		newServicePanel.cfg.setProperty('visible', false);
	}
}

function hideContentSelectServicePanel(){
	if(null != newServicePanel){
		newServicePanel.cfg.setProperty('visible', false);
	}
}

YAHOO.util.Event.onContentReady("newServicePanelId", function() {
	preparePanels4SelectService();
}, this);

var ahServiceDataTable;
var tableDragAndDropHelper;
function onLoadIpPolicyAhDataTable(ahDtClumnDefs,dataSource,editInfo) {
	if (editInfo) {
		eval("var ahDtEditInfo = " + editInfo);
	}else {
		var ahDtEditInfo;
	}
    myConfigs = {
  		editInfo:{
  			name:"editIpPolicyInfo"
  		},
  		dragEvents: { //for drag
  	        customForNewRow: function() {
  	            tableDragAndDropHelper.enableDragForElement(this);
  	        }
  	    },
  	    whenSaveRow: function(trEl) {
  		 			var values = this.getRowValues($(trEl));
  		  	  		 if (values && (!values.serviceNames || (values.serviceNames && values.serviceNames.indexOf(",") < 0))) {
  		  	  		  return;
  		  	  		 }
  		  	  		 return this.cloneRowLine({
  		  	  		    trEl: $(trEl),
  		  	  		    valueDeal: {
  		  	  		     "serviceNames": {
  		  	  		      func: function(value) {
  		  	  		       eval('var result = [' + value + ']');
  		  	  		       return result;
  		  	  		      }
  		  	  		     }
  		  	  		    },
  		  	  		    oriRow: "remove"
  		  	  		   });
  		  	  		},
  	    whenEditRow : function(trEl){
  	  		var actionWidget = ahServiceDataTable.getRowCertainEditWidget($(trEl), "ipPolicyFilterActions");
  	    	var widget = ahServiceDataTable.getRowCertainEditWidget($(trEl), "serviceNames");
  	    	var log_widget = ahServiceDataTable.getRowCertainEditWidget($(trEl), "ipPolicyLoggings");
  			var actionValue = actionWidget.getCurValue();
  			var serviceValue = widget.getShowServiceText();
  			var loggingValue = log_widget.getCurValue();
  			var dat = "";
  			if(serviceValue.indexOf("Application Service") != -1) {
  				dat = [{value:ACTION_PERMIT,label:"Permit"},{value:ACTION_DENY,label:"Deny"},{value:actionValue,label:""}];
  				actionWidget.updateElData(dat);
  			}
  			var data = "";
  			if(ACTION_PERMIT == actionValue) {
  				data = [{value:1,label:"Off"},{value:2,label:"Session Initiation"},{value:3,label:"Session Termination"},{value:4,label:"Both"},{value:loggingValue,label:""}];
  			} else if(ACTION_DENY == actionValue) {
  				data = [{value:1,label:"Off"},{value:5,label:"Dropped Packets"},{value:loggingValue,label:""}];
  			} else if(ACTION_NAT == actionValue){
  				data = [{value:1,label:"Off"}];
  			}else {
  				data = [{value:1,label:"Off"},{value:2,label:"Session Initiation"},{value:3,label:"Session Termination"},{value:5,label:"Dropped Packets"},{value:loggingValue,label:""}];
  			}
  			log_widget.updateElData(data);
  	    },
  	  	whenCancelEditRow: function(trEl) {
  	  		var widget = this.getRowCertainEditWidget($(trEl), "serviceNames");
  	  		if (widget && !widget.getHiddenValue()) {
  	  			this.removeRow($(trEl));
  	  		}
  	  	}
  	}
    
    $.extend(true, myConfigs, ahDtEditInfo)
    myColumnDefs = [
		{
			type: "text",
			mark: "ruleIds",
			editMark:"ruleId_edit",
			display: '<s:text name="config.ipPolicy.ruleId" />',
			disabled: true,
			width:"60px",
			defaultValue: ""
		},
		<s:if test="%{fullMode}">
		{
			type: "dropdowneidt",
			mark: "ipPolicySourceIps",
			editMark:"ipPolicySourceIp_edit",
			display: '<s:text name="config.ipPolicy.sourceIps" />',
			width:"160px",
			defaultValue: -1,
			events: {
				newClick: newToSourceIp,
				editClick: editToSourceIp
			}
		},
		{
			type: "dropdowneidt",
			mark: "ipPolicyDestinationIps",
			editMark:"ipPolicyDestinationIp_edit",
			display: '<s:text name="config.ipPolicy.destinationIps" />',
			width:"160px",
			defaultValue: -1,
			events: {
				newClick: newToDestinationIp,
				editClick: editToDestinationIp
			}
		},
		</s:if>
		<s:elseif test="%{easyMode}">
		{
			type: "dropdownRemove",
			mark: "ipPolicySourceIps",
			editMark:"ipPolicySourceIp_edit",
			display: '<s:text name="config.ipPolicy.sourceIps" />',
			width:"160px",
			events: {
				newClick: newToSourceIpInExpress,
				editClick: editToSourceIpInExpress
			}
		},
		{
			type: "dropdownRemove",
			mark: "ipPolicyDestinationIps",
			editMark:"ipPolicyDestinationIp_edit",
			display: '<s:text name="config.ipPolicy.destinationIps" />',
			width:"160px",
			events: {
				newClick: newToDestinationIpInExpress,
				editClick: editToDestinationIpInExpress
			}
		},
		</s:elseif>
		{
			type: "dropdownPopup",
			mark: "serviceNames",
			editMark:"serviceName_edit",
			display: '<s:text name="config.ipPolicy.networkService" />',
			width:"200px",
			validate:validateSelectedServices,
			events: {
				change: changeService,
				go: goToService
			},
			classNames:{
				displayText:"tableTdTextAlign"
			}
		},
		{
			type: "dropdown",
			mark: "ipPolicyFilterActions",
			editMark:"ipPolicyFilterAction_edit",
			display: '<s:text name="config.ipPolicy.action" />',
			width:"100px",
			events: {
				change: changeItemActionLog
			}
		},
        {
			type: "dropdown",
			mark: "ipPolicyLoggings",
			editMark:"ipPolicyLogging_edit",
			display: '<s:text name="config.ipPolicy.logging" />',
			width:"100px"
		}
	];
       
	var myColumns = [];
	for (var i = 0; i < myColumnDefs.length; i++) {
		var optionTmp = myColumnDefs[i];
		var bln = false;
		if(ahDtClumnDefs){
			for (var j = 0; j < ahDtClumnDefs.length; j++) {
				if (myColumnDefs[i].mark == ahDtClumnDefs[j].mark) {
					optionTmp = $.extend(true, optionTmp, ahDtClumnDefs[j]);
					myColumns.push(optionTmp);
					bln = true;
					break;
				}
			}
		}
		if(!bln){
			myColumns.push(optionTmp);
		}
		
	} 

	ahServiceDataTable = new AhDataTablePanel.DataTablePanel("ahIpPolicyDataTable",myColumns,dataSource,myConfigs);
	ahServiceDataTable.render();

	tableDragAndDropHelper = new hm.util.AhDragAndDropTableHelper({
		  "dragContainers": ["ahIpPolicyDataTable_tbody"],
		  "dragElPattern": "div#ahIpPolicyDataTable table tbody tr",
		  "dragHandler": "td.dragMe",
		  events: {
			  whenEndDrag: function() {
				  ahServiceDataTable.alterTrBgColor();
			  }
		  }
		 });
	tableDragAndDropHelper.render();
	
	function newToSourceIp() {
		<s:if test="%{writeDisabled == 'disabled'}">
		return false;
		</s:if>
		<s:else>
		submitAction("newSourceIpAddress");
		</s:else>
	}
	function editToSourceIp(editSourceIp) {
		<s:if test="%{writeDisabled == 'disabled'}">
		return false;
		</s:if>
		<s:else>
			var value = hm.util.validateListSelection(editSourceIp);
			if(value < 0){
				return;
			}else{
				document.forms[formName].ipAddressId.value = value;
				submitAction("editSourceIpAddress");
			}
		</s:else>
	}
	
	function newToDestinationIp() {
		<s:if test="%{writeDisabled == 'disabled'}">
		return false;
		</s:if>
		<s:else>
		submitAction("newDestIpAddress");
		</s:else>
	}
	function editToDestinationIp(editDestinationIp) {
		<s:if test="%{writeDisabled == 'disabled'}">
		return false;
		</s:if>
		<s:else>
		var value = hm.util.validateListSelection(editDestinationIp);
		if(value < 0){
			return;
		}else{
			document.forms[formName].ipAddressId.value = value;
			submitAction("editDestIpAddress");
		}
		</s:else>
	}
	
	function newToSourceIpInExpress(editSourceIp) {
		<s:if test="%{writeDisabled == 'disabled'}">
		return false;
		</s:if>
		<s:else>
		hm.simpleObject.newSimple(hm.simpleObject.TYPE_IP,hm.simpleObject.IP_SUB_WILDCARD,editSourceIp,'updateDestList',<s:property value="%{domainId}"/>);
		</s:else>
	}
	function editToSourceIpInExpress(editSourceIp) {
		<s:if test="%{writeDisabled == 'disabled'}">
		return false;
		</s:if>
		<s:else>
		var ipPolicySourceIps = document.getElementsByName("ipPolicySourceIps");
		var err_flag = false;
		if(ipPolicySourceIps.length > 0){
			var selected_value = hm.util.validateListSelection(editSourceIp);
			for(var i = 0; i < ipPolicySourceIps.length; i++){
				if(ipPolicySourceIps[i].value == selected_value){
					err_flag = true;
					break;
				}
			}
		}
		if(err_flag){
			var widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "ipPolicySourceIps");
			var selected_text = widget.getDisplayText();
			if(selected_text != '[-any-]'){
				hm.util.reportFieldError(document.getElementById("serviceTableError"), "The removal failed because  \""+selected_text+"\" is still use by another configuration item.");
				document.getElementById("serviceTableError").focus();
				return false;
			}
		}else{
			hm.simpleObject.removeSimple(hm.simpleObject.TYPE_IP,editSourceIp,'updateDestList');
		}
		</s:else>
	}
	
	function newToDestinationIpInExpress(editDestinationIp) {
		<s:if test="%{writeDisabled == 'disabled'}">
		return false;
		</s:if>
		<s:else>
		hm.simpleObject.newSimple(hm.simpleObject.TYPE_IP,hm.simpleObject.IP_SUB_WILDCARD,editDestinationIp,'updateSourceList',<s:property value="%{domainId}"/>)
		</s:else>
	}
	function editToDestinationIpInExpress(editDestinationIp) {
		<s:if test="%{writeDisabled == 'disabled'}">
		return false;
		</s:if>
		<s:else>
		var ipPolicyDestinationIps = document.getElementsByName("ipPolicyDestinationIps");
		var err_flag = false;
		if(ipPolicyDestinationIps.length > 0){
			var selected_value = hm.util.validateListSelection(editDestinationIp);
			for(var i = 0; i < ipPolicyDestinationIps.length; i++){
				if(ipPolicyDestinationIps[i].value == selected_value){
					err_flag = true;
					break;
				}
			}
		}
		if(err_flag){
			var widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "ipPolicyDestinationIps");
			var selected_text = widget.getDisplayText();
			if(selected_text != '[-any-]'){
				hm.util.reportFieldError(document.getElementById("serviceTableError"), "The removal failed because  \""+selected_text+"\" is still use by another configuration item.");
				document.getElementById("serviceTableError").focus();
				return false;
			}
		}else{
			hm.simpleObject.removeSimple(hm.simpleObject.TYPE_IP,editDestinationIp,'updateSourceList');
		}
		</s:else>
	}
	
	function validateSelectedServices(){
		var widget = this.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "serviceNames");
		var selectedServices = widget.getHiddenValue();
		if(selectedServices == ""){
			hm.util.reportFieldError(document.getElementById("serviceTableError"), "Please select at least one service.");
	        return false;
		}
		return true;
	}
	
	function changeItemActionLog() {
		var widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "ipPolicyFilterActions");
		var log_widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "ipPolicyLoggings");
		var actionValue = widget.getCurValue();
		var data = "";
		if(ACTION_PERMIT == actionValue) {
			data = [{value:1,label:"Off"},{value:2,label:"Session Initiation"},{value:3,label:"Session Termination"},{value:4,label:"Both"}];
		} else if(ACTION_DENY == actionValue) {
			data = [{value:1,label:"Off"},{value:5,label:"Dropped Packets"}];
		} else if(ACTION_NAT == actionValue){
			data = [{value:1,label:"Off"}];
		}else {
			data = [{value:1,label:"Off"},{value:2,label:"Session Initiation"},{value:3,label:"Session Termination"},{value:5,label:"Dropped Packets"}];
		}
		log_widget.updateElData(data);
	}
	
	function changeService(){
		var widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "serviceNames");
		var actionWidget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "ipPolicyFilterActions");
		var serviceValue = widget.getEditWidgetValue();
		var data = "";
		if("2" == serviceValue) {
			data = [{value:ACTION_PERMIT,label:"Permit"},{value:ACTION_DENY,label:"Deny"}];
		}else{
			data = [{value:ACTION_PERMIT,label:"Permit"},{value:ACTION_DENY,label:"Deny"},{value:ACTION_TRAFFIC_DROP,label:"Drop traffic between stations"},{value:ACTION_NAT,label:"NAT"}];
		}
		actionWidget.updateElData(data);
		changeItemActionLog();
		widget.disabledService();
		fetchSelectServiceNewDlg(serviceValue);
	}
	
	function goToService(){
		var widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "serviceNames");
		var serviceValue = widget.getEditWidgetValue();
		if(serviceValue == "1" || serviceValue == "2"){
			showSelectServicePanel();
		}
	}
}

function updateSourceList(data){
	if(data){
		var widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "ipPolicySourceIps");
		var sourceListEl = widget.getSelectEl();
		if(data.type == "add" && data.item){
			hm.simpleObject.addOption(sourceListEl, data.item.key, data.item.value, false);
		}else if(data.type == "remove" && data.items){
			hm.simpleObject.removeOptions(sourceListEl, data.items);
		}
	}
}

function updateDestList(data){
	if(data){
		var widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "ipPolicyDestinationIps");
		var destListEl = widget.getSelectEl();
		if(data.type == "add" && data.item){
			hm.simpleObject.addOption(destListEl, data.item.key, data.item.value, false);
		}else if(data.type == "remove" && data.items){
			hm.simpleObject.removeOptions(destListEl, data.items);
		}
	}
}

</script>
