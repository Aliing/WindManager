<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
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
<script src="<s:url value="/js/hm.newsimpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/dataTable/ahDataTable.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/innerhtml.js" />"></script>
<script src="<s:url value="/js/widget/tableSort.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>
var formName = 'qosClassification';
var displayErrorObj ;
var displayErrorObjMac ;
//var displayErrorObjSsid ;

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		//add handler to deal with something before form submit.
		beforeSubmitAction(document.forms[formName]);
		
		<s:if test="%{jsonMode==true}">
			if (operation=='editService' || operation=='newService' || operation=='editMac' || operation=='newMac') {
				document.forms[formName].parentIframeOpenFlg.value = true;
			}
		</s:if>
		
	    document.forms[formName].submit();
	}
}

function submitActionJson(operation) {
	if (operation=='cancel') {
		parent.closeIFrameDialog();
		return false;
	}
	if (validate(operation)) {
		var url =  "<s:url action='qosClassification' includeParams='none' />" +
		"?jsonMode=true"+
		"&ignore="+new Date().getTime();
		document.forms["qosClassification"].operation.value = operation;
		YAHOO.util.Connect.setForm(document.getElementById("qosClassification"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveQosClassification, failure : resultDoNothing, timeout: 60000}, null);	
	}
}
var resultDoNothing = function(o) {
//	alert("failed.");
};

var succSaveQosClassification = function (o) {
	eval("var details = " + o.responseText);
	if (details.t) {
		if (details.n){
			if (details.pId && details.pId!='') {
				hm.util.insertSelectValue(details.nId, details.nName, parent.Get(details.pId), false, true);
			} 
		}
		parent.closeIFrameDialog();
	} else {
		hm.util.displayJsonErrorNote(details.m);
	}
}

function validate(operation) {
    var inputElement;

    if(operation=='create<s:property value="lstForward"/>' || operation=='create')
    {
       inputElement=document.getElementById("classificationName");
       var message = hm.util.validateName(inputElement.value, '<s:text name="config.qos.classification.name" />');

       if (message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
       }else{
	   		var nameVal = inputElement.value;
			if(nameVal.toLowerCase() == "null"){
				hm.util.reportFieldError(inputElement, '<s:text name="config.qos.classification.name" />'+" cannot be '"+nameVal+"'");
	    		name.focus();
	           	return false;
			}
		}
    }
    
    inputElement = document.getElementById(formName + "_dataSource_description");
	var description = inputElement.value;
	if(description.toLowerCase() == "null"){
		hm.util.reportFieldError(inputElement, '<s:text name="config.qos.classification.description" />'+" cannot be '"+description+"'");
       	inputElement.focus();
       	return false;
	}
    if(operation=='update<s:property value="lstForward"/>'
        || operation=='create<s:property value="lstForward"/>'
        || operation=='update'
        || operation=='create')
    {
        if(!checkSelectedValues())
          return false;
    }

	return true;
}

var nsEnabled;
var moEnabled;
var ssidEnabled;

var nsTab;

var serviceShowing;
var macShowing;
var ssidShowing;
var chboxP;
var chboxD;
var chboxE;
function onLoadPage() {
	<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(900, 650);
		}
	</s:if>
	nsEnabled = <s:property value="%{dataSource.networkServicesEnabled}"/>;
	moEnabled = <s:property value="%{dataSource.macOuisEnabled}"/>;
	ssidEnabled = <s:property value="%{dataSource.ssidEnabled}"/>;

    serviceShowing = <s:property value="%{serviceShowing}"/>;
    macShowing = <s:property value="%{macShowing}"/>;
    ssidShowing = <s:property value="%{ssidShowing}"/>;

    initMarks();
	var operation = "<s:property value="%{operation}"/>";

	createWaitingPanel();
	var ahServiceDtDatas = eval('<s:property escape="false" value="ahServiceDtDatas"/>');
	var ahServiceDtClumnDefs = eval('<s:property escape="false" value="ahServiceDtClumnDefs"/>');
	var editServiceInfo = '<s:property escape="false" value="editServiceInfo"/>';
	onLoadServiceAhDataTable(ahServiceDtClumnDefs,ahServiceDtDatas,editServiceInfo);
	
	var ahOuiDtDatas = eval('<s:property escape="false" value="ahOuiDtDatas"/>');
	var ahOuiDtClumnDefs = eval('<s:property escape="false" value="ahOuiDtClumnDefs"/>');
	var editOuiInfo = '<s:property escape="false" value="editOuiInfo"/>';
	onLoadMacOuiAhDataTable(ahOuiDtClumnDefs,ahOuiDtDatas,editOuiInfo);
	var ahSSIDDtDatas = eval('<s:property escape="false" value="ahSSIDDtDatas"/>');
	var ahSSIDDtClumnDefs = eval('<s:property escape="false" value="ahSSIDDtClumnDefs"/>');
	var editSSIDInfo = '<s:property escape="false" value="editSSIDInfo"/>';
	onLoadSSIDAhDataTable(ahSSIDDtClumnDefs,ahSSIDDtDatas,editSSIDInfo);
}

function initMarks()
{
    chboxP= <s:property value="%{chboxP}"/>;
    chboxE= <s:property value="%{chboxE}"/>;
    chboxD= <s:property value="%{chboxD}"/>;
    if(chboxP)
      isDisabledAll("pP");
    if(chboxD)
      isDisabledAll("pD");
    if(chboxE)
      isDisabledAll("pE");
}

function checkSelectedValues(){
    var obj_service=document.getElementById("networkServicesEnabled");
    if(obj_service.checked){
        var cbs = document.getElementsByName('serviceNames');
		if (cbs.length == 0 ) {
		    //var feChild = document.getElementById("checkAll");
            hm.util.reportFieldError(document.getElementById("serviceTableError"), '<s:text name="error.pleaseAddItems"></s:text>');
            document.getElementById("serviceTableError").focus();
			return false;
		}else{
			if(cbs.length == 1 && cbs[0].value == ""){
				hm.util.reportFieldError(document.getElementById("serviceTableError"), '<s:text name="error.pleaseAddItems"></s:text>');
	            document.getElementById("serviceTableError").focus();
				return false;
			}
		}
    }
    var obj_mac=document.getElementById("macOuisEnabled");
    if(obj_mac.checked){
        var cbs = document.getElementsByName('ouiNames');

        if (cbs.length == 0 ) {
		   // var feChild = document.getElementById("checkAllMac");
            hm.util.reportFieldError(document.getElementById("macOuiTableError"), '<s:text name="error.pleaseAddItems"></s:text>');
            document.getElementById("macOuiTableError").focus();
			return false;
		}else{
			if(cbs.length == 1 && cbs[0].value == ""){
				hm.util.reportFieldError(document.getElementById("macOuiTableError"), '<s:text name="error.pleaseAddItems"></s:text>');
	            document.getElementById("macOuiTableError").focus();
				return false;
			}
		}
    }
    var obj_ssid=document.getElementById("ssidEnabled");
    if(obj_ssid.checked){
		var cbs = document.getElementsByName('ssidNames');
		if (cbs.length == 0 ) {
            hm.util.reportFieldError(document.getElementById("ssidTableError"), '<s:text name="error.pleaseAddItems"></s:text>');
            document.getElementById("ssidTableError").focus();
            return false;
		}else{
			if(cbs.length == 1 && cbs[0].value == ""){
				hm.util.reportFieldError(document.getElementById("ssidTableError"), '<s:text name="error.pleaseAddItems"></s:text>');
	            document.getElementById("ssidTableError").focus();
				return false;
			}
		}
    }
    return true;
}

function toggleNetworkServices(cb) {
	if (cb.checked) {
		document.getElementById("ahServiceDataTable").style.display = "";
	} else {
		document.getElementById("ahServiceDataTable").style.display = "none";
	}

	nsEnabled = cb.checked;
}

function toggleMacOuis(cb) {
	if (cb.checked) {
		document.getElementById("ahMacOuiDataTable").style.display = "";
	} else {
		document.getElementById("ahMacOuiDataTable").style.display = "none";
	}
	moEnabled = cb.checked;
}

function toggleSsids(cb) {
	if (cb.checked) {
		document.getElementById("ahSSIDDataTable").style.display = "";
	} else {
		document.getElementById("ahSSIDDataTable").style.display = "none";
	}

	ssidEnabled = cb.checked;
}

function toggleGeneral(cb) {
	if (cb.checked) {
		document.getElementById("general").style.display = "";
	} else {
		document.getElementById("general").style.display = "none";
	}
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
		document.write('<td class="crumb" nowrap><a href="<s:url action="qosClassification" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="displayName" />\'</td>');
		</s:else>
	</s:else>
}
function isDisabledAll(value){
   var obj=document.getElementById(value);
   if(value=="pP")
      setChecked("protocolPName_",obj.checked);
   if(value=="pD")
      setChecked("protocolDName_",obj.checked);
   if(value=="pE")
      setChecked("protocolEName_",obj.checked);
}
function setChecked(name,bln)
{
   var obj;
   for(var i=0;i<8;i++)
   {
      obj=document.getElementById(name+i);
      obj.disabled=!bln;
   }
}
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
		width:"650px",
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
			top.changeIFrameDialog(1000, 700);
		}
		</s:if>
		$("#newServicePanelId").width(800);
		$("#tdUM_selectService").width(750);
		$("#tdBM_selectService").width(750);
		<s:if test="dataSource.id == null">
		url = "<s:url action='qosClassification' includeParams='none' />?operation=selectService&jsonMode=<s:property value="%{jsonMode}"/>"
		 + "&ignore="+new Date().getTime();
		</s:if>
		<s:else>
		url = "<s:url action='qosClassification' includeParams='none' />?operation=selectService&jsonMode=<s:property value="%{jsonMode}"/>" + "&id=<s:property value="%{dataSource.id}"/>"
		 + "&ignore="+new Date().getTime();
		</s:else>
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchServiceNewDlg, failure : resultDoNothing, timeout: 60000}, null);
		if(waitingPanel != null){
			waitingPanel.show();
		}
	}else if(value == "2"){
		<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(1250, 750);
		}
		</s:if>
		$("#newServicePanelId").width(1150);
		$("#tdUM_selectService").width(1080);
		$("#tdBM_selectService").width(1080);
		<s:if test="dataSource.id == null">
		url = "<s:url action='qosClassification' includeParams='none' />?operation=selectAppService&jsonMode=<s:property value="%{jsonMode}"/>"
		 + "&ignore="+new Date().getTime();
		</s:if>
		<s:else>
		url = "<s:url action='qosClassification' includeParams='none' />?operation=selectAppService&jsonMode=<s:property value="%{jsonMode}"/>" + "&id=<s:property value="%{dataSource.id}"/>"
		 + "&ignore="+new Date().getTime();
		</s:else>
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchServiceNewDlg, failure : resultDoNothing, timeout: 60000}, null);
		if(waitingPanel != null){
			waitingPanel.show();
		}
	}
}

var succFetchServiceNewDlg = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	set_innerHTML("newServicePanelContentId",
			o.responseText);
	YAHOO.util.Event.onContentReady("newServicePanelContentId", showSelectServicePanel, this);
}

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
</script>
<div id="content"><s:form action="qosClassification" id="qosClassification">
<s:hidden name="parentDomID"/>
<s:hidden name="refreshPageFlag"/>
	<s:if test="%{jsonMode==true}">
		<s:hidden name="jsonMode" />
		<s:hidden name="operation" />
		<s:hidden name="id" />
		<s:hidden name="parentIframeOpenFlg"/>
	</s:if>
	<s:hidden name="selectedOuiId"/>
	<s:hidden id="selectedServiceId" name="selectedServiceId"/>
	<s:hidden name="serviceShowing" id="serviceShowing"
		value="%{serviceShowing}" />
	<s:hidden name="macShowing" id="macShowing" value="%{macShowing}" />
	<s:hidden name="ssidShowing" id="ssidShowing" value="%{ssidShowing}" />
	<s:if test="%{jsonMode==true}">
		<div class="topFixedTitle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td style="padding:10px 10px 10px 10px">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td align="left">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-Classifier_maps.png" includeParams="none"/>"
									width="40" height="40" alt="" class="dblk" />
								</td>
								<td class="dialogPanelTitle">
									<s:if test="%{dataSource.id == null}">
										<s:text name="config.title.classification"/>
									</s:if>
									<s:else>
										<s:text name="config.title.classification.edit"/>
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
						<td align="right">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('cancel')" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
								<td width="20px">&nbsp;</td>
								<td class="npcButton">
								<s:if test="%{dataSource.id == null}">
									<s:if test="%{writeDisabled == 'disabled'}">
										<a href="javascript:void(0);" class="btCurrent" onclick="return false;" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
									</s:if>
									<s:else>
										<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('create');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
									</s:else>
								</s:if>
								<s:else>
									<s:if test="%{writeDisabled == 'disabled'}">
										<a href="javascript:void(0);" class="btCurrent" onclick="return false;" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
									</s:if>
									<s:else>
										<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('update');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
									</s:else>
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
							<td><input type="button" name="ignore"
								value="<s:text name="button.create"/>" class="button"
								onClick="submitAction('create<s:property value="lstForward"/>');"
								<s:property value="writeDisabled" />></td>
						</s:if>
						<s:else>
							<td><input type="button" name="ignore"
								value="<s:text name="button.update"/>" class="button"
								onClick="submitAction('update<s:property value="lstForward"/>');"
								<s:property value="writeDisabled" />></td>
						</s:else>
						<s:if test="%{lstForward == null || lstForward == ''}">
							<td><input type="button" name="cancel" value="Cancel"
								class="button"
								onClick="submitAction('<%=Navigation.L2_FEATURE_QOS_CLASSIFICATION%>');">
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
			<td style="padding-top: 5px;">
				<s:if test="%{jsonMode == true}">
					<table cellspacing="0" cellpadding="0" border="0" width="760px">
				</s:if>
				<s:else>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="760px">
				</s:else>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td><table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="labelT1" width="90"><label><s:text
								name="config.qos.classification.name" /><font color="red"><s:text
								name="*" /></font></label></td>
							<td><s:textfield
								name="dataSource.classificationName" size="24"
								maxlength="%{nameLength}" id="classificationName"
								disabled="%{disabledName}"
								onkeypress="return hm.util.keyPressPermit(event,'name');" />
							<s:text name="config.name.range" /></td>
						</tr>
						<tr>
							<td class="labelT1" width="90"><label><s:text
								name="config.qos.classification.description" /></label></td>
							<td width="500"><s:textfield
								name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" /> <s:text
								name="config.description.range" /></td>
						</tr>
					</table></td>
				</tr>
				<tr>
					<td class="noteInfo" style="padding: 6px 0 0 16px"><s:text name="config.qos.classification.note"/></td>
				</tr>
				<!-- Network Service -->
    			<tr>
					<td style="padding: 2px 0 0 12px" align="left" colspan="2"><s:checkbox
						name="dataSource.networkServicesEnabled"
						id="networkServicesEnabled"
						onclick="toggleNetworkServices(this);" /> <s:text
						name="config.qos.classification.tab.networkServices" /></td>
				</tr>
				<tr>
					<td height="1" colspan="2"></td>
				</tr>
				<tr>
					<td colspan="2" style="padding-left: 28px;">
						<table width="100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
								<label id="serviceTableError"></label>
								</td>
								<td>
								<div id="ahServiceDataTable" style="display: <s:property value="%{dataSource.displayNetworkServices}"/>"></div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
    			<tr>
					<td height="1" colspan="2"></td>
				</tr>
				<!-- MAC OUI-->
    			<tr>
					<td style="padding: 2px 0 0 12px" align="left" colspan="2"><s:checkbox
						name="dataSource.macOuisEnabled" id="macOuisEnabled"
						onclick="toggleMacOuis(this);" /> <s:text
						name="config.qos.classification.tab.macOuis" /></td>
				</tr>
				<tr>
					<td height="1" colspan="2"></td>
				</tr>
				<tr>
					<td colspan="2" style="padding-left: 28px;">
						<table width="100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
								<label id="macOuiTableError"></label>
								</td>
								<td>
								<div id="ahMacOuiDataTable" style="display: <s:property value="%{dataSource.displayMacOui}"/>"></div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
    			<tr>
					<td height="1" colspan="2"></td>
				</tr>
				<!-- SSID -->
    			<tr>
					<td style="padding: 2px 0 0 12px" colspan="2"><s:checkbox
						name="dataSource.ssidEnabled" onclick="toggleSsids(this);" id="ssidEnabled"/>
						<s:text name="config.qos.classification.ssid" /></td>
				</tr>
				<tr>
					<td height="1" colspan="2"></td>
				</tr>
				<tr>
					<td colspan="2" style="padding-left: 28px;">
						<table width="100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
								<label id="ssidTableError"></label>
								</td>
								<td>
								<div id="ahSSIDDataTable" style="display: <s:property value="%{dataSource.displaySsid}"/>"></div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
    			<tr>
					<td height="1"></td>
				</tr>
				<!-- General-->
    			<tr>
					<td style="padding: 2px 0 0 12px" colspan="2"><s:checkbox
						name="dataSource.generalEnabled" onclick="toggleGeneral(this);" id="generalEnabled"/>
						<s:text name="config.qos.classification.general" /></td>
				</tr>
				<tr>
					<td height="1"></td>
				</tr>
				<tr>
       				<td style="padding:0 18px 0 18px;">
       					<fieldset id="general" style="display: <s:property value="%{dataSource.displayGeneral}"/>">
           				<div>
              			<table cellspacing="0" cellpadding="0" border="0" >
                        <tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td style="padding-left: 0px; padding-right: 30px;" class="borderRight">
							<table cellspacing="0" cellpadding="0" border="0" >
								<tr>
									<td colspan="2" style="padding-top: 2px;" align="left">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td><s:checkbox name="chboxP" id="pP"
												onclick="isDisabledAll('pP');" /> <s:text
												name="config.qos.protocolP" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<th align="center" style="padding-top: 5px;"><s:text
										name="802.1p" /></th>
									<th align="center" style="padding-top: 5px;"><s:text
										name="QoS Class" /></th>
								</tr>
								<s:iterator id="protocolP_id" value="%{protocolPP.values()}"
									status="status">
									<tr class="list" align="center">
										<td class="list"
											id="protocolPIndex"><s:property value="%{key}" /></td>
											<td class="list" align="center"><s:select
											name="protocolPName" value="value" list="%{enumQosClass}"
											listKey="key" listValue="value"
											id="protocolPName_%{#status.index}" disabled="%{!chboxP}" /></td>
									</tr>
								</s:iterator>
								<tr>
									<td height="5"></td>
								</tr>
							</table>
							</td>
							<td style="padding-left: 30px; padding-right: 30px;"
								class="borderRight">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td colspan="2" style="padding-top: 2px;" align="left">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td><s:checkbox name="chboxD" id="pD"
												onclick="isDisabledAll('pD');" /> <s:text
												name="config.qos.protocolD" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<th align="center" style="padding-top: 5px;"><s:text
										name="DiffServ" /></th>
									<th align="center" style="padding-top: 5px;"><s:text
										name="QoS Class" /></th>
								</tr>
								<s:iterator id="protocolD_id" value="%{protocolD.values()}"
									status="status">
									<tr class="list" align="center">
										<td class="list"
											id="protocolDIndex"><s:property value="%{key}" /></td>
										<td class="list" align="center"><s:select
											name="protocolDName"
											value="%{protocolDName[#status.index]}"
											list="%{enumQosClass}" listKey="key" listValue="value"
											id="protocolDName_%{#status.index}" disabled="%{!chboxD}" /></td>
									</tr>
								</s:iterator>
								<tr>
									<td height="5"></td>
								</tr>
							</table>
							</td>
							<td style="padding-left: 30px;">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td colspan="2" style="padding-top: 2px;" align="left">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td><s:checkbox name="chboxE" id="pE"
												onclick="isDisabledAll('pE');" /> <s:text
												name="config.qos.protocolE" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<th align="center" style="padding-top: 5px;"><s:text
										name="802.11e" /></th>
									<th align="center" style="padding-top: 5px;"><s:text
										name="QoS Class" /></th>
								</tr>
								<s:iterator id="protocolE_id" value="%{protocolPE.values()}"
									status="status">
									<tr class="list" align="center">
										<td class="list"
											id="protocolEIndex"><s:property value="%{key}" /></td>
										<td class="list" align="center"><s:select
											name="protocolEName" value="value" list="%{enumQosClass}"
											listKey="key" listValue="value"
											id="protocolEName_%{#status.index}" disabled="%{!chboxE}" /></td>
									</tr>
									</s:iterator>
								<tr>
									<td height="5"></td>
								</tr>
							</table>
							</td>
						</tr>
                      </table>
                  	</div></fieldset>
					</td>
    			</tr>
    			<tr>
					<td height="5"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
<div id="newServicePanelId" style="display: none;top: 20px;">
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
YAHOO.util.Event.onContentReady("newServicePanelId", function() {
	preparePanels4SelectService();
}, this);

var ahServiceDataTable;
function onLoadServiceAhDataTable(ahDtClumnDefs,dataSource,editInfo) {
	if (editInfo) {
		eval("var ahDtEditInfo = " + editInfo);
	}else {
		var ahDtEditInfo;
	}

    myConfigs = {
  		editInfo:{
  			name:"editServiceInfo"
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
	  	  		var actionWidget = ahServiceDataTable.getRowCertainEditWidget($(trEl), "serviceFilterActions");
	  	    	var widget = ahServiceDataTable.getRowCertainEditWidget($(trEl), "serviceNames");
	  			var serviceValue = widget.getShowServiceText();
	  			var dat = "";
	  			if(serviceValue.indexOf("Application Service") != -1) {
	  				dat = [{value:"1",label:"Permit"}];
	  				actionWidget.updateElData(dat);
	  			}
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
			type: "dropdownPopup",
			mark: "serviceNames",
			editMark:"serviceName_edit",
			display: '<s:text name="config.qos.service" />',
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
			mark: "serviceQosClasses",
			editMark:"serviceQosClass_edit",
			display: '<s:text name="config.qos.class" />',
			width:"150px"
		},
		{
			type: "dropdown",
			mark: "serviceFilterActions",
			editMark:"serviceFilterAction_edit",
			display: '<s:text name="config.qos.action" />',
			width:"120px",
		},
        {
			type: "dropdown",
			mark: "serviceLoggings",
			editMark:"serviceLogging_edit",
			display: '<s:text name="config.qos.logging" />',
			width:"120px",
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

	ahServiceDataTable = new AhDataTablePanel.DataTablePanel("ahServiceDataTable",myColumns,dataSource,myConfigs);
	ahServiceDataTable.render();
}

function decodeServiceNameSpecialCode(str, blnSlash) {
	if (str) {
		if (blnSlash) {
			return str.replace(/<==/g, "\\\'");
		} else {
			return str.replace(/<==/g, "'");
		}
	}
	return str;
}

function validateSelectedServices(){
	var widget = this.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "serviceNames");
	var selectedServices = widget.getHiddenValue();
	if(selectedServices == ""){
		hm.util.reportFieldError(document.getElementById("serviceTableError"), "Please select at least one service.");
		document.getElementById("serviceTableError").focus();
        return false;
	}
	return true;
}

function changeService(){
	var widget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "serviceNames");
	var actionWidget = ahServiceDataTable.getRowCertainEditWidget(ahServiceDataTable.getCurrentEditRow(), "serviceFilterActions");
	var serviceValue = widget.getEditWidgetValue();
	var data = "";
	if("2" == serviceValue) {
		data = [{value:"1",label:"Permit"}];
		actionWidget.updateElData(data);
	}
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

var ahMacOuiDataTable;
function onLoadMacOuiAhDataTable(ahDtClumnDefs,dataSource,editInfo) {
	if (editInfo) {
		eval("var ahDtEditInfo = " + editInfo);
	}else {
		var ahDtEditInfo;
	}

    myConfigs = {
  		editInfo:{
  			name:"editOuiInfo"
  		}
    }
        
    $.extend(true, myConfigs, ahDtEditInfo)
        
    myColumnDefs = [
		<s:if test="%{fullMode}">
		{
			type: "dropdowneidt",
			mark: "ouiNames",
			editMark:"ouiName_edit",
			display: '<s:text name="config.qos.macouis" />',
			width:"140px",
			events: {
				newClick: newToMac,
				editClick: editToMac
			}
		},
		</s:if>
		<s:elseif test="%{easyMode}">
		{
			type: "dropdownRemove",
			mark: "ouiNames",
			editMark:"ouiName_edit",
			display: '<s:text name="config.qos.macouis" />',
			width:"140px",
			events: {
				newClick: newToMacInExpress,
				editClick: editToMacInExpress
			}
		},
		</s:elseif>
		{
			type: "dropdown",
			mark: "ouiQosClasses",
			editMark:"ouiQosClass_edit",
			display: '<s:text name="config.qos.class" />',
			width:"120px"
		},
		{
			type: "dropdown",
			mark: "ouiFilterActions",
			editMark:"ouiFilterAction_edit",
			display: '<s:text name="config.qos.action" />',
			width:"90px",
		},
        {
			type: "dropdown",
			mark: "ouiLoggings",
			editMark:"ouiLogging_edit",
			display: '<s:text name="config.qos.logging" />',
			width:"90px",
		},
        {
			type: "text",
			mark: "ouiComments",
			editMark:"ouiComment_edit",
			display: '<s:text name="config.qos.comment" />',
			width:"120px",
			defaultValue:"default mac oui",
			validate:validateComment,
			maxlength:32
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

	ahMacOuiDataTable = new AhDataTablePanel.DataTablePanel("ahMacOuiDataTable",myColumns,dataSource,myConfigs);
	ahMacOuiDataTable.render();
}

function validateComment(data){
	var inputElement = data;
	if (inputElement.length != 0) {
		if (contains(inputElement, "\\", true )) {
		hm.util.reportFieldError(document.getElementById("macOuiTableError"), "Comment cannot contain '\\'.");
		return false;
		}
	}

	return true;	
}
function newToMac() {
	<s:if test="%{writeDisabled == 'disabled'}">
	return false;
	</s:if>
	<s:else>
	submitAction('newMac');
	</s:else>
}
function editToMac(editMacId) {
	<s:if test="%{writeDisabled == 'disabled'}">
	return false;
	</s:if>
	<s:else>
	var value = hm.util.validateListSelection(editMacId);
	if(value < 0){
		return;
	}else{
		document.forms[formName].selectedOuiId.value = value;
		submitAction("editMac");
	}
	</s:else>
}

function newToMacInExpress(editMacId) {
	<s:if test="%{writeDisabled == 'disabled'}">
	return false;
	</s:if>
	<s:else>
	hm.simpleObject.newSimple(hm.simpleObject.TYPE_MAC,hm.simpleObject.MAC_SUB_OUI,editMacId,'',<s:property value="%{domainId}"/>);
	</s:else>
}
function editToMacInExpress(editMacId) {
	<s:if test="%{writeDisabled == 'disabled'}">
	return false;
	</s:if>
	<s:else>
	var ouiNames = document.getElementsByName("ouiNames");
	var err_flag = false;
	if(ouiNames.length > 0){
		var selected_value = hm.util.validateListSelection(editMacId);
		for(var i = 0; i < ouiNames.length; i++){
			if(ouiNames[i].value == selected_value){
				err_flag = true;
				break;
			}
		}
	}
	if(err_flag){
		var widget = ahMacOuiDataTable.getRowCertainEditWidget(ahMacOuiDataTable.getCurrentEditRow(), "ouiNames");
		var selected_text = widget.getDisplayText();
		hm.util.reportFieldError(document.getElementById("macOuiTableError"), "The removal failed because  \""+selected_text+"\" is still use by another configuration item.");
		document.getElementById("macOuiTableError").focus();
		return false;
	}else{
		hm.simpleObject.removeSimple(hm.simpleObject.TYPE_MAC,editMacId,'');
	}
	</s:else>
}

var ahSSIDDataTable;
function onLoadSSIDAhDataTable(ahDtClumnDefs,dataSource,editInfo) {
	if (editInfo) {
		eval("var ahDtEditInfo = " + editInfo);
	}else {
		var ahDtEditInfo;
	}

    myConfigs = {
  		editInfo:{
  			name:"editSSIDInfo"
  		}
    }
        
    $.extend(true, myConfigs, ahDtEditInfo)
        
    myColumnDefs = [
		{
			type: "dropdown",
			mark: "ssidNames",
			editMark:"ssidName_edit",
			display: '<s:text name="config.qos.classification.ssids" />',
			width:"200px"
		},
		{
			type: "dropdown",
			mark: "ssidQosClasses",
			editMark:"ssidQosClass_edit",
			display: '<s:text name="config.qos.class" />',
			width:"200px"
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

	ahSSIDDataTable = new AhDataTablePanel.DataTablePanel("ahSSIDDataTable",myColumns,dataSource,myConfigs);
	ahSSIDDataTable.render();
}

function contains(string, substr, isIgnoreCase)
{
    if (isIgnoreCase)
    {
         string = string.toLowerCase();
         substr = substr.toLowerCase();
    }

    var startChar = substr.substring(0, 1);
    var strLen = substr.length;

    for (var j = 0; j<string.length - strLen + 1; j++)
    {
         if (string.charAt(j) == startChar)  
         {
             if (string.substring(j, j+strLen) == substr) 
             {
                 return true;
             }   
         }
    }
    return false;
}
</script>