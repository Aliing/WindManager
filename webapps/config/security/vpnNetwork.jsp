<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.network.DhcpServerOptionsCustom"%>
<%@page import="com.ah.bo.network.SingleTableItem"%>
<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/widget/ct.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/widget/panel.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<s:if test="%{jsonMode == true && contentShownInDlg == true}">
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery-ui.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/innerhtml.js" />?v=<s:property value="verParam" />"></script>
<link type="text/css" rel="stylesheet"
	href="<s:url value="/css/jquery-ui.css" includeParams="none"/>"></link>
</s:if>

<s:if test="%{jsonMode==false}">
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery-ui.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/innerhtml.js" />?v=<s:property value="verParam" />"></script>
<link type="text/css" rel="stylesheet"
	href="<s:url value="/css/jquery-ui.css" includeParams="none"/>"></link>
<style type="text/css">
#dhcp-slider, #guest-dhcp-slider {
	background: none;
	background-color: #ffc721;
}
#dhcp-slider .ui-widget-header, #guest-dhcp-slider .ui-widget-header{
	border: 1px solid #dddddd;
	background: #eeeeee
		url(/hm/images/jquery/ui-bg_highlight-soft_100_eeeeee_1x100.png) 50%
		top repeat-x;
	color: #333333;
}
input[type="radio"] {
	vertical-align:middle;
	margin-top:-2px;
	margin-bottom:1px;
}
#webSecurityFailNoteRowId td, #webSecurityNoteId {
	font-size: 11px;
}
</style>
</s:if>

<style type="text/css">
#editClassifierTagContainer td.listHead {
    background-color: #FFFFFF; 
    height: 24px;   
}
#editClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 5px;	
}
#editClassifierTagContainer td#tagCtner li.tag-choice{
	padding-left: 75px;	
}
.ui-classifier td#deviceCtner div ul li a.none{
	margin-left: 5px;	
	width: 120px;
} 
.ui-classifier td#topologyCtner div ul li a.none{
	margin-left: 5px;	
	width: 120px;
}
.ui-classifier td#tagCtner div ul li a.none{
	margin-left: 75px;
	width: 120px;	
}
.ui-classifier td#topologyCtner div ul li  input{
	margin-left: 5px;
    width: 140px;
}
.ui-classifier td#deviceCtner div ul li  input{
	margin-left: 5px;
    width: 140px;
}
.ui-classifier td#tagCtner div ul li  input{
	margin-left: 75px;
    width: 140px;
}
#subnet1ClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 5px;	
}
#subnet1ClassifierTagContainer td#tagCtner li.tag-choice{
	padding-left: 75px;	
}
#subnet2ClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 5px;	
}
#subnet2ClassifierTagContainer td#tagCtner li.tag-choice{
	padding-left: 75px;	
}
.ui-classifier-items ul.itemContainer .item span.pointer{
	white-space:normal;
}
 
.ui-menu .ui-menu-item a {
 	width: 120px;
 }
 
#subnet1ClassifierTagContainer span.optmenu {
    left: 100px;
}
</style>

<script>

function widgetClick(sType,sTagValue) {
	var ids= $("#editClassifierTagContainer").find("td.listHead:nth-child(1)").attr("id");
	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
	if(ids==undefined)
	$("#editClassifierTagContainer").find("td.listHead:nth-child(1)").remove();
	if(sType==4){
		$("#editClassifierTagContainer").find("#deviceCtner").hide();
		$("#editClassifierTagContainer").find("#topologyCtner").hide();
		$("#editClassifierTagContainer").find("#tagCtner").show();
	}
	if(sType==3){
		$("#editClassifierTagContainer").find("#tagCtner").hide();
		$("#editClassifierTagContainer").find("#topologyCtner").hide();
		$("#editClassifierTagContainer").find("#deviceCtner").show();
	}
	if(sType==2){
		$("#editClassifierTagContainer").find("#deviceCtner").hide();
		$("#editClassifierTagContainer").find("#tagCtner").hide();
		$("#editClassifierTagContainer").find("#topologyCtner").show();
	}
}

YAHOO.util.Event.onDOMReady(function(){
setTimeout(function(){
	if(!window.jQuery) {
		head.js("<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	if(!window.jQuery.ui) {
		head.js("<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	head.js("<s:url value='/js/widget/classifiertag/ct-debug.js' includeParams='none'/>?v=<s:property value='verParam' />",
			"<s:url value='/js/widget/dialog/panel.js' includeParams='none'/>?v=<s:property value='verParam' />",
	function(){
	    var DOM = YAHOO.util.Dom;
	    var editPanel;
	    var ctt;
	    DOM.get('ahTempClassifierEditBtn').onclick = function(){
	    	if(null == editPanel) {
	    		editPanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierEditPanel', {width: 270, closeIcon: 'images/cancel.png'});
	    	}
	    	if(null == ctt){
	    	 ctt=$("#editClassifierTagContainer").classifierTag(
	 				{
	 					key: 2,
	 					types:  [ {key: 2, text: 'Topology Node'},null,null],
	 					itemWidths:{tagValue: 120 },
	 					widgetWidth: {desc: 0},
	 					valueProps: null,
	 					itemEditable: false,
	 					describable: false,
	 					zindexDisplay: true,
	 					needShowTagFields: true,
	 					needShowDeviceNames: true,
	 					needShowTopology: true,
	 					zindexDisplayAtTop: true
	 				}
	 			);
	    	}
	    	$("#editClassifierTagContainer").show();
	    	var tempEditType=$("input#ahTempClassifierItemType").val();
	    	var tempEditValue=$("input#ahTempClassifierKeyValue").val();
	    	setTimeout(function(){
	    		widgetClick(tempEditType,tempEditValue);
	    		editPanel.openDialog();
	    	}, 300);
	    };
	
	    DOM.get('cancelInEditClassifierTagContainer').onclick = function(){
	    	editPanel.closeDialog();
	    };
	
	    DOM.get('saveInEditClassifierTagContainer').onclick = function(){
	    	var stag1=$("#editClassifierTagContainer").find("li#li_tag_input_0").find("input").val();
	    	var stag2=$("#editClassifierTagContainer").find("li#li_tag_input_1").find("input").val();
	    	var stag3=$("#editClassifierTagContainer").find("li#li_tag_input_2").find("input").val();
	    	var dvcName=$("#editClassifierTagContainer").find("li#li_device_input").find("input").val();
	    	var topoName=$("#editClassifierTagContainer").find("li#li_topo_input").find("#locationId_hidden").val();
	    	var tpName=$("#editClassifierTagContainer").find("li#li_topo_input").find("#locationId").val();
	
	    	var idx=$("input#ahTempClassifierIndexValue").val();
	    	var itemType=$("input#ahTempClassifierItemType").val();
	    	var optKeyValue=$("input#ahTempClassifierKeyValue").val();
	    	var keyVlu=parseInt(optKeyValue);
	
	    	if(itemType==4&&stag1==""&&stag2==""&&stag3==""){
	    		alert('<s:text name="home.hmSettings.devicetag.deviceTagNull" />');return;
	    	}
			if(itemType==3&&dvcName==""){
				alert('<s:text name="home.hmSettings.devicetag.deviceNameNull" />');return;
	    	}
			if(itemType==2&&topoName==""&&tpName==""){
				alert('<s:text name="home.hmSettings.devicetag.topologyNull" />');return;
			}
			if(itemType==2&&topoName==""){editPanel.closeDialog(); return;}
	
			var vlanIdValue=$("input#vlanId").val();
		    $.getJSON("classifierTag.action",
						{tagKey: keyVlu, operation: 'edit', selectedItems: idx,tag1:stag1 ,tag2:stag2 ,tag3:stag3 ,typeName:dvcName,locationId:topoName,vlanId: vlanIdValue},
					function(data) {
						if(data.succ) {
							//just set the edited value back to jsp page
							var htmlIdx=parseInt(idx)+1;
							var targetString=data.value;
							if(keyVlu==2)
								$("#subnet1ItemsRow").find("li.item:nth-child("+htmlIdx+")").find("span:nth-child(3)").html(targetString);
							if(keyVlu==3)
								$("#subnet2ItemsRow").find("li.item:nth-child("+htmlIdx+")").find("span:nth-child(3)").html(targetString);
							editPanel.closeDialog();
						}else{
							var ttab = document.getElementById("editClassifierTagContainer");
							hm.util.reportFieldError(ttab,data.errmsg);
						}
						})
					.error(function() {
						debug("editItems, Error occurs...");
					});
	   	 };
	   	 
	   	 var ct = $("#subnet1ClassifierTagContainer").classifierTag(
					{
						key: 2,
						itemWidths:{value: false,type: 180, tagValue: 240,desc: 0},
						zindexDisplay:true,
						valueProps: null,
						describable: false,
						needGlobalItem: false,
						checkable:null,
						widgetWidth: {desc: 400},
						itemOperation: [   null,null,{text : 'Edit', fn:  function(){}},{text : 'Remove', fn: function(){}}],
						itemTableId: 'subnet1ItemsRow',
						renderDiv: 'subnetPanel_c',
						errorEl: {id: 'checkAllSubnetClass'}
					});
			var domainId=<s:property value="domainId" />;
			 $("#subnet1ClassifierTagContainer").hide();
			 $('input#newItemBtn').click(function() {
					hm.util.hide('newButton');
					hm.util.show('createButton');
					hm.util.show('subnet1ClassifierTagContainer');
				});
				$('input#cancelItemBtn').click(function() {
				    hm.util.hide('createButton');
				    hm.util.show('newButton');
				    hm.util.hide('subnet1ClassifierTagContainer');
				});
				$('input#addItemBtn').click(function() {
					$("#subnet1ClassifierTagContainer").classifierTag('saveItem');
				});
				$('input#resetButton').click(function() {
					$("#subnet1ClassifierTagContainer").classifierTag('resetOrder');
				});
				
				var ct1 = $("#subnet2ClassifierTagContainer").classifierTag(
					{
						key: 3,
						itemWidths:{value: 120,type: 100, tagValue: 200,desc: 0},
						itemEditable: {value: false, valueName: 'ipReserveClassIp'},
						zindexDisplay:true,
						valueProps: {
							validateFn: function(){return checkIpAddress(Get("ipReserveClassIp"), Get("ipReserveClassIp"),Get("ipReserveClassIp").value, '<s:text name="config.vpn.subnet.ipAddress" />');},
							items: {id: 'ipReserveClassIp',field: 'ipReserveClassIp', onkeypress: 'onkeypress="return hm.util.keyPressPermit(event,\'ip\');"', maxlength: 15, size: 15}
						},
						describable: false,
						needGlobalItem: false,
						checkable:null,
						widgetWidth: {desc: 400},
						itemOperation: [   null,null,{text : 'Edit', fn:  function(){}},{text : 'Remove', fn: function(){}}],
						itemTableId: 'subnet2ItemsRow',
						renderDiv: 'subnetPanel_c',
						errorEl: {id: 'checkAllIpReserveClass'}
					});

			 $("#subnet2ClassifierTagContainer").hide();

			 $('input#newItemBtn1').click(function() {
					hm.util.hide('newButton');
					hm.util.show('createButton');
					hm.util.show('subnet2ClassifierTagContainer');
				});
				$('input#cancelItemBtn1').click(function() {
				    hm.util.hide('createButton');
				    hm.util.show('newButton');
				    hm.util.hide('subnet2ClassifierTagContainer');
				});
				$('input#addItemBtn1').click(function() {
					$("#subnet2ClassifierTagContainer").classifierTag('saveItem');
				});
				$('input#resetButton1').click(function() {
					$("#subnet2ClassifierTagContainer").classifierTag('resetOrder');
				});
	   	 
   	 });
    }, 300);
	if(Get(formName + "_overrideDNS").value == "true"){
		modifySubnetwork(<s:property value="%{ruleOneIndex}"/>);
	}
});
</script>

<script>
var CUSTOM_TYPE_INT = <%=DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER%>;
var CUSTOM_TYPE_IP = <%=DhcpServerOptionsCustom.CUSTOM_TYYPE_IP%>;
var CUSTOM_TYPE_STR = <%=DhcpServerOptionsCustom.CUSTOM_TYYPE_STRING%>;
var CUSTOM_TYPE_HEX = <%=DhcpServerOptionsCustom.CUSTOM_TYYPE_HEX%>;

var IPRESERVATION_TYPE_MAP = <%=SingleTableItem.TYPE_MAP%>;
var IPRESERVATION_TYPE_HIVEAP = <%=SingleTableItem.TYPE_HIVEAPNAME%>;
var IPRESERVATION_TYPE_CLASSIFIER = <%=SingleTableItem.TYPE_CLASSIFIER%>;

var displayErrorObjCus;

var DEFAULT_BRANCHES_MAX_SIZE = 4096;
var MAX_DHCP_CLIENT_SIZE = 512;

var limitDHCPOptionValues = [3, 6, 15, 42, 51];

var formName = 'vpnNetworks';
var positionRange;
var enableDHCPServer;
var subNtClz=new Array();
var totalSize=0;
var totalIpRsvClzSize=0;

if(<s:property value="%{dataSource.subNetwokClass.size()==0}"/>) {} else {
	<s:iterator value="%{dataSource.subNetwokClass}" status="status">
		var item = {};
		item.id = <s:property value="%{#status.index}"/>;
		item.ruleKey =<s:property value="%{key}"/>;
		item.type = <s:property value="%{type}"/>;
		item.tagValue = '<s:property value="getTagValue(domainId)"/>';
		subNtClz[<s:property value="%{#status.index}"/>]=item;
		totalSize=<s:property value="%{#status.index}"/>;
	</s:iterator>
}

var ipRsvClz=new Array();
if(<s:property value="%{dataSource.reserveClass.size()==0}"/>) {} else {
	<s:iterator value="%{dataSource.reserveClass}" status="status">
		var item1 = {};
		item1.id = <s:property value="%{#status.index}"/>;
		item1.value = '<s:property value="%{ipAddress}"/>';
		item1.ruleKey =<s:property value="%{key}"/>;
		item1.type = <s:property value="%{type}"/>;
		item1.tagValue = '<s:property value="getTagValue(domainId)"/>';
		ipRsvClz[<s:property value="%{#status.index}"/>]=item1;
		totalIpRsvClzSize=<s:property value="%{#status.index}"/>;
	</s:iterator>
}

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_networkName").disabled == false) {
		document.getElementById(formName + "_dataSource_networkName").focus();
	}
	 displayErrorObjCus = document.getElementById("checkAllCustom");

	 // initial
	 changeNetworkType(<s:property value="dataSource.networkType"/>);
	 changeSecurityType(<s:property value="dataSource.webSecurity"/>, <s:property value="dataSource.failConnectionOption"/>);
	 <s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(850,630);
	 	}
	 </s:if>
	 initGuestDHCPPoolSlider();
	 loadWaterMark();
}
function showCustomContent(){
	showHideContent("customOption","");
}

function handleDNSService(operation) {
	<s:if test="%{jsonMode==true}">
	if (parent) {
		if(parent.isIFrameDialogOpen()) {
			document.forms[formName].parentIframeOpenFlg.value = true;
			if (operation != 'create') {
				showProcessing();
			}
			//top.changeIFrameDialog(740, 700);
		} else {
			var selectElementId = formName + "_dnsServiceId";
			var url = '<s:url action="vpnNetworks" includeParams="none"/>'
				+ '?operation='+ operation
				+ '&jsonMode=true'
				+ '&parentDomID=' + selectElementId;

			if (operation == 'editDnsService'){
				var value = Get(selectElementId).value;
				url = url + "&dnsServiceId=" + value;
			}
			openIFrameDialog(740, 700, url + '&ignore=' + new Date().getTime());
			return;
		}
	}
	</s:if>
	submitSubnetAction('saveSubnet');
	Get(formName + "_dataSource_customOptionDisplayStyle").value = Get("customOption").style.display;
	document.forms[formName].operation.value = operation;
    document.forms[formName].submit();
}

function handleOverrideDNSService(operation) {
	
	if(operation == "editOverrideDNS"){
		if(!validateOverrideDNSService){
			return false;
		}
	}
	submitSubnetAction(operation);
}

var selectedSubArray = new Array();
function submitAction(operation) {
	if (validate(operation)) {
		if(operation=="newDnsService" || operation=="editDnsService") {
			handleDNSService(operation);
		} else if(operation=="newOverrideDNS" || operation=="editOverrideDNS") {
			handleOverrideDNSService(operation);
		}else if(operation=="removeSubnetwork") {
			var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=removeSubnetwork";
			var subNetworksIndex = document.getElementsByName('ruleIndices');
			selectedSubArray = new Array();
			for (var index=0; index < subNetworksIndex.length; index++) {
				if (subNetworksIndex[index].checked) {
					url =  url + "&ruleIndices="+subNetworksIndex[index].value;
					selectedSubArray.push(subNetworksIndex[index]);
				}
			}
			url = url + "&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('get', url,
					{success : removeSubNetworkAck, failure : failRemoveSubNetwork, timeout: 60000}, null);
			return;
		} else {
			Get(formName + "_dataSource_customOptionDisplayStyle").value = Get("customOption").style.display;
			document.forms[formName].operation.value = operation;
		    <s:if test="%{jsonMode == true && contentShownInSubDrawer == true}">
				if (operation == 'removeCustom'
						|| operation == 'removeCustomNone'
						|| operation == 'addCustom') {
					submitAsynActionSubDrawer(operation);
				} else {
					document.forms[formName].submit();
				}
			</s:if>
			<s:else>
				if (operation != 'create') {
					showProcessing();
				}
				document.forms[formName].submit();
			</s:else>
		}
	}
}

var removeSubNetworkAck = function(o) {
	eval("var details = " + o.responseText);
	if(details.succ) {
		// Delete the selected rows
		var element, row;
		var flag = false;
		while(selectedSubArray.length > 0) {
			element = selectedSubArray.pop();
			if(element.parentNode) {
				row = element.parentNode.parentNode;
				if(row) {
					row.parentNode.removeChild(row);
					flag = true;
				}
			}
		}
		if(flag) {
			set_innerHTML("mainSubnetDetailDiv", details.table);
		}
	} else {
		// Error message
	}
}
var failRemoveSubNetwork = function(o) {

}

function disableIpBranchTag() {
	$( "#slider-thumb" ).slider( "option", "disabled", true );
	$( "#slider-thumb" ).slider( "option", "value", 1);
	Get("branchSize").innerHTML=1;
	var subNetIpCount=calcuNetIPCount(1);
	Get("perBranch").innerHTML=subNetIpCount;
	setDHCPPoolSlider(subNetIpCount, [0, subNetIpCount]);
}

function enableIpBranchTag() {
	$( "#slider-thumb" ).slider( "option", "disabled", false );
}

function changeSubnetworkIpAddressMode() {
	var uniqueSubnetwork = Get("uniqueSubnetwork");
	Get("natIPLocalIPMappedNote").innerHTML = "";
	portForwardingCancelButtonTR();
	Get("enablePortForwardingClass").checked=false;
	enablePortForwardingClass(false);
	
	if(uniqueSubnetwork.checked) {
		hm.util.show("tr_ipBranchTag");
		Get("subnetIpnetwork").innerHTML = '<s:text name="config.vpn.subnet.localIpAddressSpace"/><font color="red"><s:text name="*"/></font>';
		Get("firstIPAsDefaultGateway").innerHTML = '<s:text name="config.vpn.subnet.firstPartitionedIPAdDefaultGateway"/>';
		Get("lastIPAsDefaultGateway").innerHTML = '<s:text name="config.vpn.subnet.lastPartitionedIPAdDefaultGateway"/>';
		Get("enableNatClass").disabled = false;
		enableNatClass(false);
		Get("useIpClass").innerHTML = '<s:text name="config.vpn.subnet.useIp.class"/>';
		
		Get("tr_natIPAddressSpaceNote").style.display="";
		Get("tr_natIPAddressSpace").style.display="";
		Get("tr_natIPLocalIPMappedNote").style.display="";
		Get("tr_numberOfBranches").style.display="none";
		Get("tr_subNatIpSpacePool").style.display="none";
		showHideContent("natSettings", "none");
		enableIpBranchTag();
		setBranches();
		
		if(isMgt0()) {
			Get("enableNatClass").disabled = true;
			Get("replicateSameSubnetwork").disabled = true;
		}
	} else {
		hm.util.hide("tr_ipBranchTag");
		Get("subnetIpnetwork").innerHTML = '<s:text name="config.vpn.subnet.localSubnetwork"/><font color="red"><s:text name="*"/></font>';
		Get("firstIPAsDefaultGateway").innerHTML = '<s:text name="config.vpn.subnet.firstLocalIPAdDefaultGateway"/>';
		Get("lastIPAsDefaultGateway").innerHTML = '<s:text name="config.vpn.subnet.lastLocalIPAdDefaultGateway"/>';
		enableNatClass(true);
		Get("enableNatClass").disabled = true;
		Get("useIpClass").innerHTML = '<s:text name="config.vpn.subnet.natIp.class"/>';
		
		Get("tr_natIPAddressSpaceNote").style.display="none";
		Get("tr_natIPAddressSpace").style.display="none";
		Get("tr_natIPLocalIPMappedNote").style.display="none";
		Get("tr_numberOfBranches").style.display="";
		Get("tr_subNatIpSpacePool").style.display="";
		Get("numberOfBranches").value="";
		Get("subNatIpSpacePool").value="";
		Get("natNetMask").value="";
		disableIpBranchTag();
	}
	
	var ipNetwork=Get("subNetwork");
	var ipNatNetwork=Get("subNatIpNetwork");
	if(ipNetwork.value != "" && ipNatNetwork.value != "") {
		if(uniqueSubnetwork.checked) {
			Get("natIPLocalIPMappedNote").innerHTML = '<s:text name="config.vpn.subnet.localIPMappedToNatIp"><s:param>' + ipNetwork.value + '</s:param><s:param>' + ipNatNetwork.value + '</s:param></s:text>';
		} else {
			var subnetworkCount = countSubnetworkCont(ipNatNetwork.value, ipNetwork.value);
			if(subnetworkCount > 0) {
				Get("natIPLocalIPMappedNote").innerHTML = '<s:text name="config.vpn.subnet.natIpPartitionedSubnetworks"><s:param>' + ipNatNetwork.value + '</s:param><s:param>' + subnetworkCount + '</s:param></s:text>';
			}
		}
	}
}

function submitAsynActionSubDrawer(operation) {
	var url = "<s:url action='vpnNetworks' includeParams='none' />"
			+"?jsonMode=true&ignore="+new Date().getTime();
	YAHOO.util.Connect.setForm(document.forms["vpnNetworks"]);
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succAsynVpnNetwork, failure : resultDoNothing, timeout: 60000}, null);
}

var succAsynVpnNetwork = function(o) {
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
	set_innerHTML(subDrawerContentId,o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 4000);
}

function saveVpnNetwork(operation) {
	if (validate(operation)) {
		var url = "";
		if (operation == 'create') {
			url = "<s:url action='vpnNetworks' includeParams='none' />" + "?jsonMode=true"
					+ "&ignore=" + new Date().getTime();
		} else if (operation == 'update') {
			url = "<s:url action='vpnNetworks' includeParams='none' />" + "?jsonMode=true"
					+ "&ignore=" + new Date().getTime();
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["vpnNetworks"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveJsonVpnNetwork, failure : failSaveVpnNetwork, timeout: 60000}, null);
	}
}

var succSaveJsonVpnNetwork = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			showPageNotes(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.addedId != null && details.addedId != ''){
					hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
				}
				if(details.removeId != null && details.removeId != ''){
					var idTmps = new Array();
					idTmps.push(details.removeId);
					hm.simpleObject.removeOptions(parentSelectDom, idTmps);
					if (parentSelectDom.length==0) {
						parentSelectDom.length=1;
						parentSelectDom.options[0].value = -1;
						parentSelectDom.options[0].text = 'None available';
						parentSelectDom.selectedIndex = 0;
					}
				}
				if (details.warnMsg != null && details.warnMsg != '') {
					alert(details.warnMsg);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSaveVpnNetwork = function(o) {
	// do nothing now
}

function validateNetworkForJson(operation){
	if (validate(operation)){
		Get(formName + "_dataSource_customOptionDisplayStyle").value = Get("customOption").style.display;
		return true;
	}
	return false;
}

function validate(operation) {
	if (operation == 'cancel' + '<s:property value="lstForward"/>') {
		Get(formName + "_dataSource_leaseTime").value=86400;
		return true;
	}

	if(operation == "editDnsService"){
		var value = hm.util.validateListSelection(formName + "_dnsServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].dnsServiceId.value = value;
		}
	}
	
	if(operation == "editOverrideDnsService"){
		var value = hm.util.validateListSelection(formName + "_overrideDNSServiceId");
		if(value < 0){
			return false;
		}else{
			document.forms[formName].overrideDNSServiceId.value = value;
		}
	}
	<s:if test="%{jsonMode==true}">
	if (operation == 'create'
		|| operation == 'update') {
		if (!validateNetworkName()) {
			return false;
		}
		if (!validateDNSService()) {
			return false;
		}
		if (!validateDhcpSetting()) {
			return false;
		}
		
	}
	</s:if>

	if (operation == 'create' + '<s:property value="lstForward"/>' ||
		operation == 'update' + '<s:property value="lstForward"/>') {
		if (!validateNetworkName()) {
			return false;
		}
		if (!validateDNSService()) {
		      return false;
		  }
		if (!validateDhcpSetting()) {
			return false;
		}
	}

	if (operation == 'addCustom') {
 		if(!checkCustomNumber()) {
       		return false;
 		}
		var hideInteger = document.getElementById("hideInteger");
		var hideIp = document.getElementById("hideIp");
		var hideString = document.getElementById("hideString");
		var hideHex = document.getElementById("hideHex");

		var customNumber = document.getElementById(formName + "_customNumber").value;
		if (customNumber == 226) {
			if (hideIp.style.display == "none") {
				hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.hivemanager.ip" /></s:param></s:text>');
				return false;
			}
		}
		if (customNumber == 225 && hideString.style.display == "none") {
			hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.hivemanager.name" /></s:param></s:text>');
			return false;
		}

		if(hideInteger.style.display == "") {
	 		var integerValue = document.getElementById(formName + "_integerValue");
	 		if(!checkNumber(displayErrorObjCus, integerValue, '<s:text name="config.network.object.dhcp.server.options.custom.value" />', 0, 2147483647)) {
        		return false;
	 		}
	 	}
	 	if(hideIp.style.display == "") {
	 		var ipValue = document.getElementById(formName + "_ipValue");
			if(!checkIpAddress(displayErrorObjCus, ipValue,ipValue.value, '<s:text name="config.network.object.dhcp.server.options.custom.value" />')) {
				return false;
			}
	 	}
	 	if(hideString.style.display == "") {
	 		var strValue = document.getElementById(formName + "_strValue");
	 		if(strValue.value.length == 0){
				hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
	 			strValue.focus();
	 			return false;
			}
	 	}
	 	if(hideHex.style.display == "") {
	 		var hexValue = document.getElementById(formName + "_hexValue");
	 		if(YAHOO.util.Dom.hasClass(hexValue, hintClassName)) {
				hm.util.reportFieldError(displayErrorObjCus,
				'<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
				hexValue.focus();
				return false;
			} else  {
				if(hexValue.value.length == 0){
					hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
		 			hexValue.focus();
		 			return false;
				}
				if(hexValue.value.length > 254){
					hm.util.reportFieldError(displayErrorObjCus,
						'<s:text name="config.network.object.dhcp.server.options.custom.outOfRange"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
					hexValue.focus();
					return false;
				}
			}
			var message = hm.util.validateHexRange(hexValue.value, '<s:text name="config.network.object.dhcp.server.options.custom.value" />');
      	  	if (message != null) {
	            hm.util.reportFieldError(displayErrorObjCus, message);
	            hexValue.focus();
	            return false;
      	  	}
	 	}
    }

    if (operation == 'removeCustom' || operation == 'removeCustomNone') {
		var cbs = document.getElementsByName('customIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(displayErrorObjCus, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.network.object.dhcp.server.options.custom" /></s:param></s:text>');
			return false;
		}
	}

	if (operation == 'removeSubnetwork') {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(Get("checkAll"), '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(Get("checkAll"), '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.vpn.network.subLst.subnetwork" /></s:param></s:text>');
			return false;
		}
	}

	if(!checkGuestSubNetworks(true)) {
		return false;
	}

	return true;
}

function checkGuestSubNetworks(validateFlag) {
	var networkType = document.getElementById(formName + "_dataSource_networkType");
	if(networkType && networkType.value == 2) {
		var ipAddressSpace = document.getElementById(formName + "_dataSource_ipAddressSpace");
		if(ipAddressSpace) {
			if(ipAddressSpace.value.length > 0) {
				var ipArr=ipAddressSpace.value.split("/");
				if (ipArr.length!=2) {
					hm.util.reportFieldError(ipAddressSpace,
							'<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.network.guestUse.ipAddressSpace" /></s:param></s:text>');
					ipAddressSpace.focus();
					return false;
				}
				if(!checkIpAddress(ipAddressSpace, ipAddressSpace, ipArr[0],
						'<s:text name="config.vpn.network.guestUse.ipAddressSpace" />')) {
					return false;
				}

				var message = hm.util.validateIntegerRange(ipArr[1],
						'<s:text name="config.vpn.network.guestUse.ipAddressSpace" />' + ' netmask',4,31);
				if (message != null) {
		            hm.util.reportFieldError(ipAddressSpace, message);
		            ipAddressSpace.focus();
		            return false;
				}
				if(validateFlag) {
	               var remainAddressPoolCount = 0;
	                if(Get("guestRemainAddressId") && Get("guestRemainAddressId").innerHTML > 0) {
	                    remainAddressPoolCount = Get("guestRemainAddressId").innerHTML;
	                }
	                if(remainAddressPoolCount > MAX_DHCP_CLIENT_SIZE) {
	                    // The maximum number of DHCP clients is 512 because of the limited amount of flash memory
	                    hm.util.reportFieldError(Get("guestRemainAddressIdErr"), 'Address Pool cannot exceed 512.');
	                    return false;
	                }
				}
			} else {
				if(validateFlag) {
					hm.util.reportFieldError(ipAddressSpace,
						'<s:text name="error.requiredField"><s:param><s:text name="config.vpn.network.guestUse.ipAddressSpace" /></s:param></s:text>');
					ipAddressSpace.focus();
					return false;
				}
			}
		}
	}
	return true;
}

function checkCustomNumber(){
	var customNumber = document.getElementById(formName + "_customNumber").value;
	if (customNumber.length == 0) {
		 hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.number" /></s:param></s:text>');
		 return false;
    } else if(customNumber.length > 1 && customNumber.substring(0,1) == '0') {
    	hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.formatInvalid"><s:param><s:text name="config.network.object.dhcp.server.options.custom.number" /></s:param></s:text>');
    	return false;
	}
	if (isNaN(customNumber)) {
		hm.util.reportFieldError(displayErrorObjCus, '<s:text name="config.network.object.dhcp.server.options.custom.number" />' + ' must be a positive integer number.');
		return false;
	} else {
		for (var count = 0; count<customNumber.length; count++) {
	       var code = customNumber.charCodeAt(count);
	       if (48 > code || code > 57) {
	    	   hm.util.reportFieldError(displayErrorObjCus, '<s:text name="config.network.object.dhcp.server.options.custom.number" />' + ' must be a positive integer number.');
	    	   return false;
	       }
	   }
	}

	for (var index in limitDHCPOptionValues) {
		if (customNumber == limitDHCPOptionValues[index]) {
			hm.util.reportFieldError(displayErrorObjCus, '<s:text name="error.config.network.dhcp.custom.number2"><s:param>'+customNumber+'</s:param></s:text>');
	        return false;
		}
	}

	if (customNumber < 2 || customNumber > 254){
		hm.util.reportFieldError(displayErrorObjCus, '<s:text name="config.network.object.dhcp.server.options.custom.number" />' + ' must be between 2 and 254.');
        return false;
	}
	return true;
}

function checkNumber(focus, field, title, min, max, fn){
	if (field.value.length == 0) {
        hm.util.reportFieldError(focus, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
        field.focus();
        return false;
    } else if(field.value.length > 1 && field.value.substring(0,1) == '0') {
		hm.util.reportFieldError(focus, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
		field.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(field.value, title, min, max);
    if (message != null) {
        hm.util.reportFieldError(focus, message);
		if(null != fn){
			fn();
		}
        field.focus();
        return false;
    }
	return true;
}

function checkIpAddress(focus, ip, ipValue,title, fn) {
	if (ipValue.length == 0) {
        hm.util.reportFieldError(focus, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
        ip.focus();
        return false;
    } else if (!hm.util.validateIpAddress(ipValue) || hm.util.validateMulticastAddress(ipValue)) {
		hm.util.reportFieldError(focus, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		if(null != fn){
			fn();
		}
		ip.focus();
		return false;
	}
	return true;
}

function validateNetworkName() {
    var inputElement = document.getElementById(formName + "_dataSource_networkName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.vpn.network.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}
function isMgmtNetworkType() {
	var networkTypeEl = document.getElementById(formName + "_dataSource_networkType");
	return networkTypeEl ? (networkTypeEl.value == 3) : (<s:property value="dataSource.networkType"/> == 3);
}
function validateDNSService(){
	if(isMgmtNetworkType()) {
		 var dnsServiceEl = document.getElementById(formName + "_dnsServiceId");
	        if (dnsServiceEl.value == -1) {
	            hm.util.reportFieldError(dnsServiceEl,
	            		'<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.vpn.network.dnsService" /></s:param></s:text>');
	            dnsServiceEl.focus();
	            return false;
	        }
	}
	return true;
}

function validateOverrideDNSService(){
	if(Get(formName + "_overrideDNSService").checked){
		var dnsServiceEl = document.getElementById(formName + "_overrideDNSServiceId");
		if (dnsServiceEl.value == -1) {
			hm.util.reportFieldError(dnsServiceEl,
		      		'<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.vpn.network.dnsService" /></s:param></s:text>');
			dnsServiceEl.focus();
		    return false;
		}
	}
	
	return true;
}

function validateDhcpSetting() {
	if (Get(formName+"_dataSource_enableDhcp").checked) {

 		var ntpServerIp = Get(formName + "_dataSource_ntpServerIp");
 		if (ntpServerIp.value.length != 0) {
			if(!checkIpAddress(ntpServerIp, ntpServerIp,ntpServerIp.value, '<s:text name="config.vpn.network.ntpServerIp" />')) {
				return false;
			}
		}

		var leaseTime = Get(formName + "_dataSource_leaseTime");
 		if(!checkNumber(leaseTime, leaseTime, '<s:text name="config.vpn.network.leaseTime" />', 60, 86400000)) {
       		return false;
 		}
	}
	return true;
}

function changeCustomType(type) {
	var hideInteger = document.getElementById("hideInteger");
	var hideIp = document.getElementById("hideIp");
	var hideString = document.getElementById("hideString");
	var hideHex = document.getElementById("hideHex");

	switch(parseInt(type)) {
		case CUSTOM_TYPE_INT:
			hideInteger.style.display= "";
	    	hideIp.style.display="none";
			hideString.style.display="none";
			hideHex.style.display="none";
			break;
		case CUSTOM_TYPE_IP:
			hideInteger.style.display= "none";
	    	hideIp.style.display="";
			hideString.style.display="none";
			hideHex.style.display="none";
			break;
		case CUSTOM_TYPE_STR:
			hideInteger.style.display= "none";
	    	hideIp.style.display="none";
			hideString.style.display="";
			hideHex.style.display="none";
			break;
		case CUSTOM_TYPE_HEX:
			hideInteger.style.display= "none";
	    	hideIp.style.display="none";
			hideString.style.display="none";
			hideHex.style.display="";
			break;
		default:
			break;
	}
}

function showCreateSection() {
	hm.util.hide('customNewButton');
	hm.util.show('customCreateButton');
	hm.util.show('customCreateSection');
	var trh = document.getElementById('customHeaderSection');
	var trc = document.getElementById('customCreateSection');
	var table = trh.parentNode;
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection() {
	hm.util.hide('customCreateButton');
	hm.util.show('customNewButton');
	hm.util.hide('customCreateSection');
}

function toggleCheckAll(cb, ids) {
	var cbs = document.getElementsByName(ids);
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function changeNetworkType(value, invokeOnclick){
	hm.util.hideFieldError();
	if (value==1 || value == 3) {
		hm.util.show("subnetworkDiv");
		hm.util.hide("ipAddressSpaceDiv");
		hm.util.hide("dhcpSettingsRow");
		hm.util.hide("gusetUseNote");
		if(value == 1) {
		    hm.util.show("webSecurityRow");
		    hm.util.hide("redDNSLabel");
		    if(invokeOnclick) {
		    	 var networkTypeEl = document.getElementById(formName + "_dataSource_webSecurity");
		    	 if(networkTypeEl) {
			        changeSecurityType(networkTypeEl.value);
		    	 }
		    }
		} else {
		    hm.util.hide("webSecurityRow");
		    hm.util.hide("webSecurityFailNoteRowId");
		    hm.util.show("redDNSLabel");
		}
	} else {
		hm.util.hide("subnetworkDiv");
		hm.util.show("ipAddressSpaceDiv");
		hm.util.show("dhcpSettingsRow");
		hm.util.show("gusetUseNote");
	    hm.util.show("webSecurityRow");
	    hm.util.hide("redDNSLabel");
		if(invokeOnclick) {
			Get(formName+"_dataSource_enableDhcp").checked = true;
			Get(formName+"_dataSource_enableDhcp").onclick();

            var networkTypeEl = document.getElementById(formName + "_dataSource_webSecurity");
            if(networkTypeEl) {
               changeSecurityType(networkTypeEl.value);
            }
		}
	}
}

function changeDhcpValue(check){
	if (check) {
		Get("dhcpDiv").style.display="";
	} else {
		Get("dhcpDiv").style.display="none";
		Get(formName + "_dataSource_ntpServerIp").value="";
		Get(formName + "_dataSource_leaseTime").value=86400;
		Get(formName + "_dataSource_domainName").value="";
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="vpnNetworks" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}

function onLoadEventForJsonMode(){
	onLoadPage();
	//initSubnetPanel();
	<s:if test="%{writeDisabled!=''}">
		showHideNetworkPolicySubSaveBT(false);
	</s:if>
	<s:else>
		showHideNetworkPolicySubSaveBT(true);
	</s:else>
}

function changeOverrideDNS(checked){
	if(checked){
		Get("overrideDNSService").style.display="";
	}else{
		Get("overrideDNSService").style.display="none";
	}
}

</script>

<div id="content"><s:form action="vpnNetworks" id="vpnNetworks">
	<s:hidden name="vlanId" id="vlanId" />
	<s:hidden name="ipReserveClassTag" />
	<s:hidden name="ipReserveClassValue" />
	<s:hidden name="dataSource.guestLeftReserved" />
	<s:hidden name="dataSource.guestRightReserved" />
	<s:hidden name="dataSource.customOptionDisplayStyle"></s:hidden>
	<s:hidden name="referenced"></s:hidden>
	<s:hidden name="overrideDNS" />
	<s:hidden id="positionRange"></s:hidden>
	<s:hidden name="saveOrCancelSubnet" id="saveOrCancelSubnet"/>
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
		<s:hidden name="blnMgtNetwork" />
		<s:hidden name="selectedLANId" />
	</s:if>
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="vpnNetworkTitleDiv" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-40.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id == null}">
						<td class="dialogPanelTitle"><s:text name="config.title.vpn.network"/></td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.title.vpn.network.edit"/></td>
						</s:else>
						<td style="padding-left: 10px">
						    <a href="javascript:void(0);"  onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
                                   alt="" class="dblk" />
                            </a>
						</td>
					</tr>
				</table>
				</td>
				<td align="right">
				<s:if test="%{!parentIframeOpenFlg}">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<s:if test="%{writeDisabled == 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vpnNetworkSaveBtnId" onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vpnNetworkSaveBtnId" onclick="saveVpnNetwork('create');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</s:if>
						<s:else>
							<s:if test="%{updateDisabled == 'disabled'}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vpnNetworkSaveBtnId" onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vpnNetworkSaveBtnId" onclick="saveVpnNetwork('update');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</s:else>
					</tr>
				</table>
				</s:if>
				<s:else>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{dataSource.id == null}">
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vpnNetworkSaveBtnId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:if>
						<s:else>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="vpnNetworkSaveBtnId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
						</s:else>
					</tr>
				</table>
				</s:else>
				</td>
			</tr>
		</table>
	</div>
	</s:if>
			<s:if test="%{jsonMode == true && contentShownInDlg == true}">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
			</s:if>
			<s:else>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
			</s:else>
		<s:if test="%{jsonMode==false}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
				</tr>
			</table>
			</td>
		</tr>
		</s:if>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true && contentShownInDlg == true}">
				<table cellspacing="0" cellpadding="0" border="0" width="750px">
			</s:if>
			<s:elseif test="%{jsonMode == true }">
				<table cellspacing="0" cellpadding="0" border="0" width="800px">
			</s:elseif>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="750px">
			</s:else>
					<tr>
						<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td class="labelT1" width="120px"><label><s:text
									name="config.vpn.network.name" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield
									name="dataSource.networkName" size="25"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									maxlength="32" disabled="%{disabledName}" />
									&nbsp;<s:text name="config.vpn.network.name.range" /></td>
							</tr>

							<!--  <tr>
								<td class="labelT1"><s:text name="config.vpn.network.vlan" /><font color="red"><s:text name="*"/></font></td>
								<td>
									<ah:createOrSelect divId="errorDisplayVlan" list="list_vlan" typeString="Vlan"
										selectIdName="vlanIdSelect" inputValueName="inputVlanIdValue" swidth="160px" />
								</td>
							</tr>-->
							<tr id="webSecurityRow">
								<td class="labelT1"><s:text name="config.vpn.network.webSecurity" /></td>
								<td><s:select name="dataSource.webSecurity"
										list="%{enumWebSecurity}"
										listKey="key" listValue="value"
										onchange="changeSecurityType(this.value);"
										cssStyle="width: 160px;"/><br/><span id="webSecurityNoteId" style="display: none; color: #003366;"><s:text name="config.vpn.network.webSecurity.note"/></span></td>
							</tr>
							<tr style="font-size: 9px;display: none;" id="webSecurityFailNoteRowId">
								<td></td>
								<td colspan="3">
								<table>
								<tr><td><s:text name="config.vpn.network.webSecurity.failNote" /></td></tr>
								<tr>
									<td align="right" style="padding-right: 20px;"><s:radio label="Gender"
										name="dataSource.failConnectionOption" id="denyTrafficId"
										list="#{'1':''}"
										listKey="key" listValue="value" cssStyle="margin-left: 0"/>
										<s:text name="config.vpn.network.webSecurity.failNote.deny"/></td>
									<td align="left"><s:radio label="Gender"
										name="dataSource.failConnectionOption"
										list="#{'2':''}"
										listKey="key" listValue="value"/>
										<s:text name="config.vpn.network.webSecurity.failNote.permit"/></td>
								</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td class="labelT1" id=""><s:text name="config.vpn.network.dnsService" /><font color="red"><label id="redDNSLabel" <s:if test="dataSource.networkType != 3">style="display:none;"</s:if>>*</label></font></td>
								<td><s:select name="dnsServiceId"
										list="%{list_dnsService}"
										listKey="id" listValue="value"
										cssStyle="width: 160px;"/>
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/new_disable.png" />"
										width="16" height="16" alt="New" title="New" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('newDnsService')"><img class="dinl"
										src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
									</s:else>
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/modify_disable.png" />"
										width="16" height="16" alt="Modify" title="Modify" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('editDnsService')"><img class="dinl"
										src="<s:url value="/images/modify.png" />"
										width="16" height="16" alt="Modify" title="Modify" /></a>
									</s:else>

								</td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.vpn.network.description" /></td>
								<td colspan="2"><s:textfield name="dataSource.description" size="48"
									maxlength="64" />&nbsp;<s:text name="config.vpn.network.description.range" /></td>
							</tr>
							<tr id="gusetUseNote" style="display: none;">
							    <td />
							    <td class="noteInfo"><s:text name="config.vpn.network.guestUse.vpnNote" />
							    </td>
							</tr>
							<s:if test="%{displayNetworkType}">
							<tr>
								<td class="labelT1"><s:text name="config.vpn.network.type" /></td>
								<td>
									<s:select name="dataSource.networkType"
										list="%{networkType}"
										listKey="key" listValue="value"
										cssStyle="width: 160px;"
										onchange="changeNetworkType(this.value, true);"/>
								</td>
							</tr>
							</s:if>
						</table>
						</td>
					</tr>
					<tr style="display: <s:property value="%{hideIPAddressSpaceDiv}"/>" id="ipAddressSpaceDiv">
						<td style="padding: 0 4px 6px 8px">
						<fieldset>
							<legend><s:text name="config.vpn.network.subnetwork" /></legend>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td width="120px">
										<s:text name="config.vpn.network.guestUse.ipAddressSpace" /><font color="red"><s:text name="*"/></font>
									</td>
									<td style="padding-left: 20px;"><s:textfield name="dataSource.ipAddressSpace"  maxlength="18"
												    onkeypress="return hm.util.keyPressPermit(event,'ipMask');"
												    title="e.g., 10.10.10.1/24"
												    cssStyle="width: 140px"
												    onchange="changeGuestIpSpace();"
												    size="18"/>&nbsp;<s:text name="config.vpn.network.guestUse.note" />
									</td>
								</tr>
								<tr>
									<td colspan="2" style="padding-top: 10px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td width="150px">DHCP Address Pool</td>
												<td align="center"><b><span id="guestStartReservedId">0</span></b><br/>Excluded</td>
												<td width="200px" style="padding-left: 20px;padding-right: 20px;">
													<div id="guest-dhcp-slider" tabindex="-1" title="Slider"></div>
												</td>
												<td align="center"><b><span id="guestEndReservedId">0</span></b><br/>Excluded</td>
											</tr>
											<tr>
											<td/><td/>
											<td style="text-align: center;"><span id="guestRemainAddressIdErr"></span></td>
											</tr>
											<tr>
											<td/><td/>
											<td style="text-align: center;"><b><span id="guestRemainAddressId">0</span></b> in Address Pool</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</fieldset>
						</td>
					</tr>
					<tr style="display: <s:property value="%{hideSubnetworkDiv}"/>" id="subnetworkDiv">
						<td style="padding:0 4px 6px 8px">
							<fieldset><legend><s:text name="config.vpn.network.subnetwork" /></legend>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td height="4px"/>
									</tr>
									<tr>
										<td colspan="8">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td><input type="button" name="ignore" value="New"
														class="button" onClick="newSubnetwork();"
														<s:property value="writeDisabled" />></td>
													<td><input type="button" name="ignore" value="Modify"
														class="button" onClick="modifySubnetwork(-1);"
														<s:property value="writeDisabled" />></td>
													<td><input type="button" name="ignore" value="Remove"
														class="button" onClick="submitAction('removeSubnetwork');"
														<s:property value="writeDisabled" />></td>
													<td><input type="button" name="ignore" value="Clone"
														class="button" onClick="cloneSubnetwork();"
														<s:property value="writeDisabled" />></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr height="15px;"><td/><td/><td/><td/>
										<td colspan="2" style="color: #003366;font-weight: bold;padding-left: 5px;">
											<span style="position: relative; top: 9px;"><s:text name="config.vpn.network.subLst.reserved"/></span>
										</td>
									</tr>
									<tr>
										<th align="left" width="10px" style="padding-left: 0;"><input
											type="checkbox" id="checkAll"
											onClick="toggleCheckAll(this, 'ruleIndices');"></th>
										<th align="left" width="110px"><s:text name="config.vpn.network.subLst.subnetwork" /></th>
										<th align="left" width="110px"><s:text name="config.vpn.network.subLst.localSubnetwork" /></th>
										<th align="left" width="60px"><s:text name="config.vpn.network.subLst.branches" /></th>
										<th align="left" width="90px"><s:text name="config.vpn.network.subLst.clientBranch" /></th>
										<th align="left" width="40px"><s:text name="config.vpn.network.subLst.start" /></th>
										<th align="left" width="40px"><s:text name="config.vpn.network.subLst.end" /></th>
										<th align="left" width="75px"><s:text name="config.vpn.network.subLst.rangeSize" /></th>
									</tr>
									<tr id="mainSubnetDetailTR">
										<td colspan="8" id="mainSubnetDetailDiv">
											<table class="view" cellspacing="0" cellpadding="0" border="0">
												<s:iterator value="%{dataSource.subItems}" status="status">
													<tiles:insertDefinition name="rowClass" />
													<tr class="<s:property value="%{#rowClass}"/>" >
														<td class="listCheck" width="25px" style="padding-left: 0px;"><s:checkbox name="ruleIndices"
															fieldValue="%{#status.index}" /></td>
														<td class="list" width="110px"><a href="javascript:void(0);" onclick="modifySubnetwork(<s:property value="%{#status.index}"/>);"><s:property value="%{ipNetwork}"/></a></td>
														<td class="list" width="110px"><s:property value="%{localIpNetwork}"/>&nbsp;</td>
														<td class="list" width="60px"><s:property value="%{ipBranches}"/>&nbsp;</td>
														<td class="list" width="90px"><s:property value="%{ipBranchesCount}"/>&nbsp;</td>
														<td class="list" width="40px"><s:property value="%{leftEnd}"/></td>
														<td class="list" width="40px"><s:property value="%{rightEnd}"/></td>
														<td class="list" width="75px"><s:property value="%{rangeSize}"/>&nbsp;</td>
													</tr>
													<s:if test="%{subnetClassification}">
													<tr>
														<td/>
														<td colspan="7">
															<table>
															<tr>
																<td>
																<span style="cursor: pointer;" onclick="alternateFoldingContent('subNetworkClassifierRow_<s:property value="%{#status.index}"/>');">
																	<img id="subNetworkClassifierRow_<s:property value="%{#status.index}"/>ShowImg" 
																	src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
																		alt="Show Option" class="expandImg" style="display: inline"/>
																	<img id="subNetworkClassifierRow_<s:property value="%{#status.index}"/>HideImg" 
																	src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
																		alt="Hide Option" class="expandImg" style="display: none"/>
																		<s:text name="config.vpn.network.subLst.deviceClass" />
																</span>
																</td>
															</tr>
															<tr id="subNetworkClassifierRow_<s:property value='%{#status.index}'/>" style="display: none;"><td style="padding-left: 20px"><s:property value="%{dataSource.reserveItems2String(key,dataSource.subNetwokClass,domainId)}" escape="false"/>&nbsp;</td></tr>
															</table>
														</td>
													</tr>
													</s:if>
													<s:if test="%{reserveClassification}">
													<tr>
														<td/>
														<td colspan="7">
															<table>
															<tr>
																<td>
																<span style="cursor: pointer;" onclick="alternateFoldingContent('ipClassificationRow_<s:property value="%{#status.index}"/>');">
																	<img id="ipClassificationRow_<s:property value="%{#status.index}"/>ShowImg" 
																	src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
																		alt="Show Option" class="expandImg" style="display: inline"/>
																	<img id="ipClassificationRow_<s:property value="%{#status.index}"/>HideImg" 
																	src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
																		alt="Hide Option" class="expandImg" style="display: none"/>
																		<s:text name="config.vpn.network.subLst.ipReser" />
																</span>
																</td>
															</tr>
															<tr id="ipClassificationRow_<s:property value='%{#status.index}'/>" style="display: none;"><td style="padding-left: 20px"><s:property value="%{dataSource.reserveItems2String(key, dataSource.reserveClass,domainId)}" escape="false"/>&nbsp;</td></tr>
															</table>
														</td>
													</tr>
													</s:if>
												</s:iterator>
											</table>
										</td>
									</tr>
									<tr id="emptyMainSubnetTR" style="display:<s:property value="%{hideEmptyMainSubnetTR}"/>">
										<td colspan="8">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td class="list" colspan="8">&nbsp;</td>
												</tr>
												<tr>
													<td class="list" colspan="8">&nbsp;</td>
												</tr>
												<tr>
													<td class="list" colspan="8">&nbsp;</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr id="dhcpSettingsRow">
						<td style="padding:0 4px 6px 8px">
							<fieldset><legend><s:text name="config.vpn.network.dhcpSetting" /></legend>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td style="padding:0 2px 0 2px">
											<s:checkbox name="dataSource.enableDhcp"
												value="%{dataSource.enableDhcp}"
												onclick="changeDhcpValue(this.checked);"/>
											<s:text name="config.vpn.network.enableDhcp" /></td>
									</tr>
									<tr style="display: <s:property value="%{hideDhcpDiv}"/>" id="dhcpDiv">
										<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" width="120px"><s:text name="config.vpn.network.ntpServerIp" /></td>
																<td><s:textfield name="dataSource.ntpServerIp"
																	maxlength="15" size="24"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.vpn.network.leaseTime" /><font color="red"><s:text name="*"/></font></td>
																<td><s:textfield name="dataSource.leaseTime"
																	maxlength="8" size="24"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																	<s:text name="config.vpn.network.leaseTime.range" /></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.vpn.network.domainName" /></td>
																<td><s:textfield name="dataSource.domainName"
																	onkeypress="return hm.util.keyPressPermit(event,'dnsDomain');"
																	maxlength="32" size="24"/>
																	<s:text name="config.vpn.network.domainName.range" /></td>
															</tr>
															<tr>
																<td colspan="3" style="padding: 5px 0px 5px 5px;">
																	<s:checkbox name="dataSource.enableArpCheck" cssStyle="margin-bottom: 1px;"/>
																	<label for="dataSource.enableArpCheck"><s:text name="config.network.object.dhcp.server.arp" /></label>
																</td>
															</tr>														
														</table>
													</td>
												</tr>
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td style="padding-left: 10px"><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.network.object.dhcp.server.options.custom" />','customOption');</script></td>
															</tr>
															<tr>
																<td height="4px" />
															</tr>
															<tr id="customOption" style="display: <s:property value="%{dataSource.customOptionDisplayStyle}"/>">
																<td valign="top" style="padding-left: 25px">
																	<fieldset style="padding-top: 4px;">
																	<table cellspacing="0" cellpadding="0" border="0" class="embedded">
																		<tr style="display:<s:property value="%{hideCustomNewButton}"/>" id="customNewButton">
																			<td colspan="4" style="padding-bottom: 2px;">
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td><input type="button" name="ignore" value="New"
																						class="button" onClick="showCreateSection();"
																						<s:property value="writeDisabled" />></td>
																					<td><input type="button" name="ignore" value="Remove"
																						class="button" <s:property value="writeDisabled" />
																						onClick="submitAction('removeCustom');"></td>
																				</tr>
																			</table>
																			</td>
																		</tr>
																		<tr style="display:<s:property value="%{hideCustomCreateItem}"/>" id="customCreateButton">
																			<td colspan="4" style="padding-bottom: 2px;">
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																						class="button" <s:property value="writeDisabled" /> onClick="submitAction('addCustom');"></td>
																					<td><input type="button" name="ignore" value="Remove"
																						class="button" <s:property value="writeDisabled" />
																						onClick="submitAction('removeCustomNone');"></td>
																					<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																						class="button" onClick="hideCreateSection();"></td>
																				</tr>
																			</table>
																			</td>
																		</tr>
																		<tr>
																			<td>&nbsp;</td>
																			<td colspan="3" class="noteInfo" width="100%"><label><s:text
																				name="config.network.object.dhcp.server.hivemanager.range" /></label></td>
																		</tr>
																		<tr id="customHeaderSection">
																			<th align="left" style="padding-left: 0;" width="10"><input id="checkAllCustom"
																				type="checkbox" onClick="toggleCheckAll(this, 'customIndices');"></th>
																			<th align="left" width="100"><s:text
																				name="config.network.object.dhcp.server.options.custom.number" /></th>
																			<th align="left" width="100"><s:text
																				name="config.network.object.dhcp.server.options.custom.type" /></th>
																			<th align="left" width="200"><s:text
																				name="config.network.object.dhcp.server.options.custom.value" /></th>
																		</tr>
																		<tr style="display:<s:property value="%{hideCustomCreateItem}"/>" id="customCreateSection">
																			<td class="listHead" width="10">&nbsp;</td>
																			<td class="listHead" valign="top"><s:textfield size="20" name="customNumber" maxlength="3"
																				onkeypress="return hm.util.keyPressPermit(event,'ten');" /><BR/><s:text
																				name="config.network.object.dhcp.server.options.custom.number.range" /></td>
																			<td width="100" class="listHead" valign="top"><s:select name="customType"
																				list="%{enumCustomType}" listKey="key" listValue="value"
																				onchange="changeCustomType(this.options[this.selectedIndex].value);"/></td>
																			<td class="listHead" valign="top" width="200" id="hideInteger" style="display:<s:property value="displayCustomInt"/>" >
																				<s:textfield size="30" maxlength="10"
																				name="integerValue" onkeypress="return hm.util.keyPressPermit(event,'ten');" /><BR/>
																				<s:text name="config.network.object.dhcp.server.options.custom.int.range" /></td>
																			<td class="listHead" valign="top" width="200" id="hideIp" style="display:<s:property value="displayCustomIp"/>" >
																				<s:textfield size="30" maxlength="15"
																				name="ipValue" onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																			<td class="listHead" valign="top" width="200" id="hideString" style="display:<s:property value="displayCustomStr"/>" >
																				<s:textfield size="30" maxlength="128"
																				name="strValue" onkeypress="return hm.util.keyPressPermit(event,'name');" /><BR/>
																				<s:text name="admin.email.address.range" /></td>
																			<td class="listHead" valign="top" width="200" id="hideHex" style="display:<s:property value="displayCustomHex"/>" >
																				<s:textarea name="hexValue"  cssStyle="width: 280px;resize:none" rows="7" maxLength="254"
																					onkeypress="return hm.util.keyPressPermit(event,'hex');"/>
																				<BR/>
																				<s:text name="config.network.object.dhcp.server.options.custom.hex" /></td>
																		</tr>
																		<s:iterator value="%{dataSource.customOptions}" status="status">
																			<tr>
																				<td class="listCheck"><s:checkbox name="customIndices"
																					fieldValue="%{#status.index}" /></td>
																				<td class="list"><s:property value="number" /></td>
																				<td class="list"><s:property value="strType" /></td>
																				<td class="list"><span class="ellipsis" title="<s:property value='value' />" style="width:200px;"><s:property value="value" /></span></td>
																			</tr>
																		</s:iterator>
																		<s:if test="%{customGridCount > 0}">
																			<s:generator separator="," val="%{' '}" count="%{customGridCount}">
																				<s:iterator>
																					<tr>
																						<td class="list" colspan="4">&nbsp;</td>
																					</tr>
																				</s:iterator>
																			</s:generator>
																		</s:if>
																	</table>
																	</fieldset>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<div id="subnetPanel" style="display: none;">
		<div class="hd">
			Configure Subnetwork
		</div>
		<div class="bd" style="height: 640px; overflow-x:auto">
			<table cellspacing="0" cellpadding="0" border="0" width="100%" >
				<tr>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr style="display: none">
								<td colspan="2"><s:textfield name="subKey" id="subKey"/><s:textfield name="oldSubnetwork" id="oldSubnetwork"/></td>
							</tr>
							<tr>
								<td colspan="2"><input type="radio"
									name="subUniqueSubnetworkForEachBranches"
									id="uniqueSubnetwork" value="true"
									onclick="changeSubnetworkIpAddressMode()" /><s:text name="config.vpn.subnet.uniqueSubnetwork"/>&nbsp;&nbsp;&nbsp;&nbsp; <input type="radio"
									name="subUniqueSubnetworkForEachBranches"
									id="replicateSameSubnetwork" value="false" checked
									onclick="changeSubnetworkIpAddressMode()" /><s:text name="config.vpn.subnet.replicateSameSubnetwork"/></td>
							</tr>
							<tr>
								<td><br /></td>
							</tr>
							<tr>
								<td colspan="2">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="140px"
											id="subnetIpnetwork"><s:if
												test="%{subUniqueSubnetworkForEachBranches == true}">
												<s:text
													name="config.vpn.subnet.localIpAddressSpace" />
											</s:if> <s:else>
												<s:text name="config.vpn.subnet.localSubnetwork" />
											</s:else><font color="red"><s:text name="*" /></font></td>
											<td><s:textfield name="subNetwork" id="subNetwork" maxlength="18"
											    onkeyup="changeIpNetWork(false)" onkeypress="return keypressSubnetwork(event);"
											    size="18" onchange="changeIpNetWork(true)"/>&nbsp;&nbsp;<s:text name="config.vpn.subnet.range" /></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2"><span id="ipBranchTag"/></td>
							</tr>
							<tr id="tr_ipBranchTag" style="display: none">
								<td colspan="2" style="padding-top: 10px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" colspan="6"><s:text
													name="config.vpn.subnet.ipallocation" /></td>
										</tr>
										<tr>
											<td class="labelT1" width="50px" />
											<td align="center" width="50px"><b><span
													id="branchSize" style="font-size: 16px;">0</span></b><br />
											<s:text name="config.vpn.network.subLst.branches" /></td>
											<td width="10px"></td>
											<td width="200px">
												<div id="slider-thumb">
											</td>
											<td width="10px"></td>
											<td align="center" width="50px"><b><span
													id="perBranch" style="font-size: 16px;" />0</span></b><br />
											<s:text
													name="config.vpn.network.subLst.clientBranch" /></td>
										</tr>
										<tr>
											<td colspan="3"></td><td align="center"><span id="subnetBranchNetMaskSpan"> aaaa</span></td><td colspan="2"/>
										</tr>
									</table>
								</td>
							</tr>
							<tr><td height="10px"/></tr>
							<tr>
								<td colspan="2" style="padding-left: 30px;"><input
									type="radio" name="subDefaultGateway"
									id="subDefaultGatewayFirstIP" value="0" checked /> <font
									id="firstIPAsDefaultGateway"> <s:if
											test="%{subUniqueSubnetworkForEachBranches == true}">
											<s:text
												name="config.vpn.subnet.firstPartitionedIPAdDefaultGateway" />
										</s:if> <s:else>
											<s:text
												name="config.vpn.subnet.firstLocalIPAdDefaultGateway" />
										</s:else>
								</font></td>
							</tr>
							<tr><td height="10px"/></tr>
							<tr>
								<td colspan="2" style="padding-left: 30px;"><input
									type="radio" name="subDefaultGateway" value="1"
									id="subDefaultGatewayLastIP" /> <font
									id="lastIPAsDefaultGateway"> <s:if
											test="%{subUniqueSubnetworkForEachBranches == true}">
											<s:text
												name="config.vpn.subnet.lastPartitionedIPAdDefaultGateway" />
										</s:if> <s:else>
											<s:text
												name="config.vpn.subnet.lastLocalIPAdDefaultGateway" />
										</s:else>
								</font></td>
							</tr>
							<tr>
								<td height="20px" />
							</tr>
							<tr id="fe_ErrorEnableDHCP" style="display: none;">
                                <td class="noteError" id="textfe_ErrorEnableDHCP" colspan="2" style="padding-left: 28px;">To be changed</td>
                            </tr>
							<tr>
								<td style="padding:0 2px 0 6px">
									<s:checkbox name="subEnableDhcp"
										onclick="changeSubDhcpValue(this.checked);"/>
									<label for="vpnNetworks_subEnableDhcp"><s:text name="config.vpn.network.enableDhcp" /></label></td>
							</tr>
							<tr id="subDHCPPoolSetction" style="display: none;">
								<td colspan="2" style="padding-top: 10px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="130px" style="padding-left: 30px;">DHCP Address Pool</td>
											<td align="center"><b><span id="startReservedId" style="font-size: 16px;">0</span></b><br/>Excluded<br/>at start</td>
											<td width="200px" style="padding-left: 20px;padding-right: 35px;">
												<div id="dhcp-slider" tabindex="-1" title="Slider"></div>
											</td>
											<td align="center"><b><span id="endReservedId" style="font-size: 16px;">0</span></b><br/>Excluded<br/>at end</td>
										</tr>
										<tr>
										<td/><td/>
										<td style="text-align: center;"><span id="remainAddressIdErr"></span></td>
										</tr>
										<tr>
										<td/><td/>
										<td style="text-align: center;"><b><span id="remainAddressId">0</span></b> in Address Pool</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr id="subDHCPSettingsSection" style="display: none;">
								<td style="padding-top: 5px;">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td class="labelT1" width="100px" style="padding-left: 30px;"><s:text
													name="config.vpn.network.leaseTime"/><font
													color="red"><s:text name="*" /></font></td>
												<td><s:textfield name="subLeaseTime"
													maxlength="8" size="24"
													onkeypress="return hm.util.keyPressPermit(event,'ten');" />
												<s:text name="config.vpn.network.leaseTime.range" /></td>
											</tr>
											<tr>
												<td class="labelT1" width="100px" style="padding-left: 30px;"><s:text
													name="config.vpn.network.ntpServerIp" /></td>
												<td><s:textfield name="subNtpServerIp"
													maxlength="15" size="24"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
											</tr>
											<tr>
												<td class="labelT1" style="padding-left: 30px;"><s:text
													name="config.vpn.network.domainName"/></td>
												<td><s:textfield name="subDomainName"
													maxlength="32" size="24" /> <s:text
													name="config.vpn.network.domainName.range" /></td>
													<%-- onkeypress="return hm.util.keyPressPermit(event,'dnsDomain');" --%>
											</tr>
											<tr>
												<td colspan="3" style="padding: 5px 0px 5px 25px;">
													<s:checkbox name="enableArpCheck" cssStyle="margin-bottom: 1px;"/>
													<label for="enableArpCheck"><s:text name="config.network.object.dhcp.server.arp" /></label>
												</td>
											</tr>
										</table>
										</td>
									</tr>
									<tr>
										<td style="padding-top: 5px;padding-left: 20px;">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td style="padding-left: 10px">
													<span
														style="cursor: pointer;"
														onclick="javascript: alternateFoldingContent('subCustomOption');">
													<img id="subCustomOptionShowImg"
														src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
														alt="Show Option" class="expandImg"
														style="display: inline" />
													<img id="subCustomOptionHideImg"
														src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
														alt="Hide Option" class="expandImg"
														style="display: none" /> <s:text
														name="config.network.object.dhcp.server.options.custom" />
													</span>
												</td>
											</tr>
											<tr>
												<td height="4px" />
											</tr>
											<tr id="subCustomOption" style="display: none;">
												<td valign="top" style="padding-left: 15px">
												<table cellspacing="0" cellpadding="0" border="0"
													class="embedded" width="90%">
													<tr style="display:<s:property value="%{hideCustomNewButton}"/>"
														id="subCustomNewButton">
														<td colspan="4" style="padding-bottom: 2px;">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td><input type="button" name="ignore"
																	value="New" class="button"
																	onClick="displaySubCustomCreateSection(true);"
																	<s:property value="writeDisabled" />></td>
																<td><input type="button" name="ignore"
																	value="Remove" class="button"
																	<s:property value="writeDisabled" />
																	onClick="removeSubnetworkCustom();"></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr style="display:<s:property value="%{hideCustomCreateItem}"/>"
														id="subCustomCreateButton">
														<td colspan="4" style="padding-bottom: 2px;">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td><input type="button" name="ignore"
																	value="<s:text name="button.apply"/>"
																	class="button"
																	<s:property value="writeDisabled" />
																	onClick="addSubnetworkCustom();"></td>
																<td><input type="button" name="ignore"
																	value="Remove" class="button"
																	<s:property value="writeDisabled" />
																	onClick="removeSubnetworkCustom();"></td>
																<td><input type="button" name="ignore"
																	value="Cancel"
																	<s:property value="writeDisabled" />
																	class="button" onClick="displaySubCustomCreateSection(false);"></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<td>&nbsp;</td>
														<td colspan="3" class="noteInfo" width="100%"><label><s:text
															name="config.network.object.dhcp.server.hivemanager.range" /></label></td>
													</tr>
													<tr id="subCustomHeaderSection">
														<th align="left" style="padding-left: 0;" width="10">
															<input
																id="subCheckAllCustom" type="checkbox"
																onClick="toggleCheckAll(this, 'subCustomIndices');">
														</th>
														<th align="left" width="100">
															<s:text
																name="config.network.object.dhcp.server.options.custom.number" />
														</th>
														<th align="left" width="100">
															<s:text
																name="config.network.object.dhcp.server.options.custom.type" />
														</th>
														<th align="left" width="200">
															<s:text
																name="config.network.object.dhcp.server.options.custom.value" />
															</th>
													</tr>
													<tr style="display:<s:property value="%{hideCustomCreateItem}"/>"
														id="subCustomCreateSection">
														<td class="listHead" width="10">&nbsp;</td>
														<td class="listHead" valign="top">
															<s:textfield
																size="20" name="subCustomNumber" maxlength="3"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" /><BR />
															<s:text
																name="config.network.object.dhcp.server.options.custom.number.range" /></td>
														<td width="100" class="listHead" valign="top">
															<s:select
																name="subCustomType" list="%{enumCustomType}"
																listKey="key" listValue="value"
																onchange="changeSubCustomType(this.options[this.selectedIndex].value);" />
														</td>
														<td class="listHead" valign="top" width="200"
															id="subIntegerCell"
															style="display:<s:property value="displayCustomInt"/>">
															<s:textfield size="30" maxlength="10"
																name="subIntegerValue"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" /><BR />
															<s:text
																name="config.network.object.dhcp.server.options.custom.int.range" />
															</td>
														<td class="listHead" valign="top" width="200"
															id="subIpCell"
															style="display:<s:property value="displayCustomIp"/>">
															<s:textfield size="30" maxlength="15" name="subIpValue"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
														</td>
														<td class="listHead" valign="top" width="200"
															id="subStringCell"
															style="display:<s:property value="displayCustomStr"/>">
															<s:textfield size="30" maxlength="255" name="subStrValue"
																onkeypress="return hm.util.keyPressPermit(event,'name');" /><BR />
															<s:text name="config.string.range" />
														</td>
														<td class="listHead" valign="top" width="200"
															id="subHexCell"
															style="display:<s:property value="displayCustomHex"/>">
															<s:textarea name="subHexValue"  cssStyle="width: 280px;resize:none" rows="7" maxLength="254"
																	onkeypress="return hm.util.keyPressPermit(event,'hex');"/>
															<BR />
															<s:text
																name="config.network.object.dhcp.server.options.custom.hex" />
														</td>
													</tr>
													<tr>
														<td colspan="4" id="subnetCustomCell">
														</td>
													</tr>
													<tr id="emptySubnetCustomTR" style="display:none;">
														<td colspan="4">
															<table border="0" cellspacing="0" cellpadding="0" width="100%">
																<tr>
																	<td class="list" colspan="3">&nbsp;</td>
																</tr>
																<tr>
																	<td class="list" colspan="3">&nbsp;</td>
																</tr>
																<tr>
																	<td class="list" colspan="3">&nbsp;</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
												</td>
											</tr>
										</table>
										</td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td height="10px" />
							</tr>
							<tr>
								<td style="padding-left: 10px"><span
									style="cursor: pointer;"
									onclick="javascript: alternateFoldingContent('natSettings');">
										<img id="natSettingsShowImg"
										src="<s:url value="/images/expand_plus.gif" includeParams="none"/>"
										alt="Show Option" class="expandImg"
										style="display: inline" /> <img
										id="natSettingsHideImg"
										src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
										alt="Hide Option" class="expandImg"
										style="display: none" /> <s:text
											name="config.vpn.subnet.natSettings" />
								</span></td>
							</tr>
							<tr id="natSettings" style="display: none;">
								<td colspan="2" style="padding-top: 4px;">
									<table border="0" cellspacing="0" cellpadding="0">
									  <tr>
										   <td colspan="3" style="padding-left:20px">
										      <table border="0" cellspacing="0" cellpadding="0">
										           <tr>
									                  <td class="labelT1"><s:checkbox name="subEnableNat"
													     id="enableNatClass"
													      onclick="enableNatClass(this.checked);"
													      value="true" disabled="true" /> <label
												          for="enableNat"><s:text
															name="config.vpn.subnet.enableNat" /></label>
											          </td>
											          <td>&nbsp;&nbsp;&nbsp;</td>
											           <td id="exportMapping"><input type="button"
												       class="button long" 
												       value="<s:text
																name="config.vpn.subnet.exportmappingbutton" />" onclick="exportPortForwarding()"/>
												     </td>
										          </tr>
										    </table>
										</td>
									</tr>
										<tr id="natClassDiv">
											<td colspan="3" style="padding-left: 35px">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr id="tr_natIPAddressSpaceNote" style="display: none;"> 
														<td colspan="3" style="padding-left: 18px;">
														<label>
															<s:text
																name="config.vpn.subnet.natIPAddressSpaceNote" />
														</label>
														</td>
													</tr>
													<tr id="tr_natIPAddressSpace" style="display: none;">
														<td style="padding-left: 18px;" class="labelT1"
															width="160px">
																<s:text
																	name="config.vpn.subnet.natIPAddressSpace" /><font color="red"><s:text name="*" /></font></td>
														<td><s:textfield name="subNatIpNetwork"
																id="subNatIpNetwork" maxlength="18"
																onkeypress="return keypressSubnetwork(event);"
																size="18" onchange="validNatNetWorkIp(true)" onkeyup="validNatNetWorkIp(false)"/>&nbsp;&nbsp;<s:text
																name="config.vpn.subnet.range" /></td>
													</tr>
													<tr style="display: none;" id="tr_natIPLocalIPMappedNote">
														<td colspan="3" style="padding-left: 18px;">
														<label> <font
															id="natIPLocalIPMappedNote"> </font>
														</label>
														</td>
													</tr>																							
													<tr id="tr_numberOfBranches">
														<td style="padding-left: 18px;" class="labelT1"
															width="160px">
																<s:text name="config.vpn.subnet.numberOfBranches"/><font color="red"><s:text name="*" /></font></td>
														<td>
															<s:textfield name="numberOfBranches" id="numberOfBranches" maxlength="10"
															onkeypress="return hm.util.keyPressPermit(event,'ten');"
															onkeyup="validNumberOfBranches()"
															size="15"/></td>																							
													</tr>																							
													<tr id="tr_subNatIpSpacePool">
														<td style="padding-left: 18px;" class="labelT1"
															width="160px">
																<s:text
																	name="config.vpn.subnet.natIPAddressSpacePool" /><font color="red"><s:text name="*" /></font></td>
														<td><s:textfield name="subNatIpSpacePool"
																id="subNatIpSpacePool" maxlength="15"
																onkeypress="return hm.util.keyPressPermit(event,'ip');"
																size="15"/>&nbsp;&nbsp;
															<s:text name="config.vpn.subnet.mask"/>
															<s:textfield name="natNetMask" id="natNetMask" maxlength="2" readonly="true"
															size="4"/></td>
														
													</tr>																							
												</table>
											</td>
										</tr>
										<tr>
											<td class="labelT1" style="padding-left: 30px;"
												colspan="2"><s:checkbox
													name="enablePortForwardingClass"
													id="enablePortForwardingClass"
													onclick="enablePortForwardingClass(this.checked);" />
												<label for="enablePortForwarding"><s:text
														name="config.vpn.subnet.enablePortForwarding" /></label>
												<input type="button"
												       class="button long" value="<s:text
																name="config.vpn.subnet.viewsystemportbutton"/>" onclick="openPortListPanel(this)"/>
												<div id="portPanel" style="display: none"></div>
											</td>
										</tr>
										<tr id="portForwardingClassDiv">
											<td colspan="3" style="padding-left: 35px">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td colspan="3" style="padding-left: 18px;">
														<label><s:text
																name="config.vpn.subnet.portForwardingNote" /></label>
														</td>
													</tr>
													<tr id="portForwardingDiv">
														<td colspan="2" style="padding-left: 35px">
															<table border="0" cellspacing="0"
																cellpadding="0">
																<tr id="portForwardingNewButtonTR">
																	<td colspan="3">
																		<table border="0" cellspacing="0"
																			cellpadding="0">
																			<tr>
																				<td><input type="button"
																					name="ignore" value="New" class="button"
																					onClick="newPortForwarding();"></td>
																				<td><input type="button"
																					name="ignore" value="Remove"
																					class="button"
																					onClick="removePortForwarding()"></td>
																				<td><input type="button"
																					name="ignore" 
																					value='<s:text name="config.vpn.subnet.ipAddressMapping"></s:text>'
																					class="button long" onClick="showAllBranchIps()"></td>
																			</tr>
																		</table>
																	</td>
																</tr>
																<tr id="portForwardingApplyButtonTR"
																	style="display: none;">
																	<td colspan="4">
																		<table border="0" cellspacing="0"
																			cellpadding="0">
																			<tr>
																				<td><input type="button"
																					name="ignore" value="Apply"
																					class="button"
																					onClick="addportForwarding();"></td>
																				<td><input type="button"
																					name="ignore" value="Remove"
																					class="button"
																					onClick="removePortForwarding();"></td>
																				<td><input type="button"
																					name="ignore" value="Cancel"
																					class="button"
																					onClick="portForwardingCancelButtonTR()"></td>
																					<td><input type="button"
																					name="ignore" 
																					value='<s:text name="config.vpn.subnet.ipAddressMapping"></s:text>'
																					class="button long" onClick="showAllBranchIps()"></td>
																				<div id="allBranchIpsDiv" style="display: none;">
	                                                                                <div class="bd">
		                                                                                  <iframe id="allBranchIpsFrame" name="allBranchIpsDivFrame" width="0" height="0"
			                                                                            frameborder="0" src=""> </iframe>
	                                                                                </div>
                                                                               </div>
																			</tr>
																		</table>
																	</td>
																</tr>
																<tr>
																	<th align="left" style="padding-left: 0;"
																		width="10px"><input type="checkbox"
																		id="checkAllPortForwarding"
																		onClick="toggleCheckAll(this, 'PortForwardingIndices');"></th>
																	<th align="left" width="120px"><s:text name="config.vpn.subnet.DestinationPortNumber"></s:text> </th>
																	<th align="left" width="120px"><s:text name="config.vpn.subnet.InternalIPAddress"></s:text></th>
																	<th align="left" width="120px"><s:text name="config.vpn.subnet.InternalPortNumber"></s:text></th>
																	<th align="left" width="100px"><s:text name="config.vpn.subnet.TrafficProtocol"></s:text></br>&nbsp;</th>
																</tr>
																<tr id="portForwardingApplyDetailTR"
																	style="display: none;">
																	<td class="listHead" valign="top">&nbsp;</td>
																	<td class="listHead" valign="top"><s:textfield size="15"
																			name="destinationPortNumber" maxlength="5"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"></s:textfield><BR />
																			<s:text name="config.vpn.subnet.portRange"></s:text>
																	</td>
																	<td class="listHead" valign="top" width="80px">
																	<s:select name="positionId" cssStyle="width:85%"
																		          list="%{selPositions}" listKey="key" 
																		          listValue="value"></s:select>
																	</td>
																	<td class="listHead" valign="top"><s:textfield size="15"
																			name="internalHostPortNumber" maxlength="5"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');"></s:textfield></td>
																	<td class="listHead" valign="top" width="80px">
																	<s:select   name="protocol" cssStyle="width:100%"
																					list="%{enumProtocol}" listKey="key"
																					listValue="value"/>
																	</td>
																</tr>
																<tr>
																	<td colspan="5" id="showPortForwardingDiv"></td>
																</tr>
																<tr>
																<td colspan="5">
																	 <div  id="showScroPortForwardingDiv" style="overflow-y: auto; height: 160px; width:100%;">
																	 </div>
																	</td>
																</tr>
																<tr id="emptyPortForwarding"
																	style="display: none;">
																	<td colspan="5">
																		<table border="0" cellspacing="0"
																			cellpadding="0" width="100%">
																			<tr>
																				<td class="list" colspan="5">&nbsp;</td>
																			</tr>
																			<tr>
																				<td class="list" colspan="5">&nbsp;</td>
																			</tr>
																			<tr>
																				<td class="list" colspan="5">&nbsp;</td>
																			</tr>
																		</table>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2" style="padding: 10px 0 2px 6px">
									<s:checkbox name="subEnableNetClass"
									id="subEnableNetClass"
									onclick="enableSubnetClass(this.checked);"/>
									<label for="subEnableNetClass"><s:text name="config.vpn.subnet.useSubnet.class"/></label>
								</td>
							</tr>
							<tr id="subnetClassDiv" style="display: none;">
								<td colspan="2" style="padding-left: 35px">
									<table border="0" cellspacing="0" cellpadding="0" width="100%" >
										<tr id="subnetClassNewButtonTR">
											<td colspan="3">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td> <input type="button" name="ignore" value="New" id="newItemBtn"
																class="button" onClick="displaySubnetClassApplyTR();"
																<s:property value="writeDisabled" />></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr id="subnetClassApplyButtonTR" style="display:none;">
											<td colspan="3">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td> <input type="button" name="ignore" value="Apply" id="addItemBtn"
																class="button"
																<s:property value="writeDisabled" />></td>
														<td> <input type="button" name="ignore" value="Cancel" id="cancelItemBtn"
															class="button" onClick="hideSubnetClassApplyTR();"
															<s:property value="writeDisabled" />></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<!--  th align="left" style="padding-left: 0;" width="10px"><input
												type="checkbox" id="checkAllSubnetClass"
												onClick="toggleCheckAll(this, 'subnetClassIndices');"></th-->										
											<th align="left" width="200"><s:text name="config.vpn.subnet.type" />
											<input type="checkbox" id="checkAllSubnetClass" style="display:none"></th>
											<th align="left" width="400"><s:text name="config.vpn.subnet.value" /></th>
										</tr>
										<tr><td height="2px"/></td>
                                        <tr id="fe_subneterrorRow" style="display: none">
                                            <td/><td/>
                                            <td class="noteError" id="textfe_subneterrorRow" colspan="3">To be changed</td>
                                        </tr>
										 <tr id="subnet1ClassifierTagContainer"></tr>
										 <tr id="subnet1ItemsRow"></tr>
										<tr height="12px"/>


									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2" style="padding: 4px 0 2px 6px">
									<s:checkbox name="subEnableIpReserveClass"
									id="subEnableIpReserveClass"
									onclick="enableIpReserveClass(this.checked);"/>
									<label for="subEnableIpReserveClass"> 
										<font id="useIpClass"> <s:if
													test="%{subUniqueSubnetworkForEachBranches == true}">
													<s:text name="config.vpn.subnet.useIp.class" />
												</s:if> <s:else>
													<s:text name="config.vpn.subnet.natIp.class" />
												</s:else>
										</font>
									</label>
								</td>
							</tr>
							<tr id="ipReserveClassDiv" style="display: none;">
								<td colspan="2" style="padding-left: 35px">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr id="ipClassNewButtonTR">
											<td colspan="4">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td> <input type="button" name="ignore" value="New" id="newItemBtn1"
																class="button" onClick="displayIpClassApplyTR();"
																<s:property value="writeDisabled" />></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr id="ipClassApplyButtonTR" style="display:none;">
											<td colspan="4">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td> <input type="button" name="ignore" value="Apply"
																class="button" id="addItemBtn1"
																<s:property value="writeDisabled" />></td>
														<td> <input type="button" name="ignore" value="Cancel"
															class="button" id="cancelItemBtn1"  onClick="hideIpClassApplyTR();"
															<s:property value="writeDisabled" />></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<!-- th align="left" style="padding-left: 0;" width="10px"><input
												type="checkbox" id="checkAllIpReserveClass"
												onClick="toggleCheckAll(this, 'ipReserveClassIndices');"></th-->
											<th align="left" width="110px"><s:text name="config.vpn.subnet.ipAddress" />
											<input type="checkbox" id="checkAllIpReserveClass" style="display:none"></th>
											<th align="left" width="120px"><s:text name="config.vpn.subnet.type" /></th>
											<th align="left" width="300px"><s:text name="config.vpn.subnet.value" /></th>
										</tr>
										<tr><td height="2px"/></tr>
										<!-- Error message row -->
										<tr id="fe_errorRow" style="display: none">
											<td/><td/>
											<td class="noteError" id="textfe_errorRow" colspan="3">To be changed</td>
										</tr>
										 <tr id="subnet2ClassifierTagContainer"></tr>
										 <tr id="subnet2ItemsRow"></tr>
										<tr height="12px"/>
										</tr>
										<tr id="emptyIpReserveClassTR" style="display:none;">
											<td colspan="4">
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td class="list" colspan="4">&nbsp;</td>
													</tr>
													<tr>
														<td class="list" colspan="4">&nbsp;</td>
													</tr>
													<tr>
														<td class="list" colspan="4">&nbsp;</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td style="padding: 4px 0px 2px 6px;" colspan="3">
									<s:checkbox name="overrideDNSService" onclick="changeOverrideDNS(this.checked);" cssStyle="margin-bottom: 1px;"/>
									<label for="overrideDNSService" ><s:text name="geneva_10.config.vpn.subnet.override.dns" /></label>
								</td>
							</tr>
							<tr>
								<td>
									<table id="overrideDNSService" style="display:<s:property value="hideOverrideDNSService" />">
										<tr>
											<td style="padding-left:28px"><s:text name="config.vpn.network.dnsService" /><font color="red"><label id="redDNSLabel" <s:if test="dataSource.networkType != 3">style="display:none;"</s:if>>*</label></font></td>
											<td><s:select name="overrideDNSServiceId"
													list="%{list_dnsService}"
													listKey="id" listValue="value"
													cssStyle="width: 160px;"/>
												<s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />"
													width="16" height="16" alt="New" title="New" />
												</s:if>
												<s:else>
													<a class="marginBtn" href="javascript:submitAction('newOverrideDNS')"><img class="dinl"
													src="<s:url value="/images/new.png" />"
													width="16" height="16" alt="New" title="New" /></a>
												</s:else>
												<s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
													src="<s:url value="/images/modify_disable.png" />"
													width="16" height="16" alt="Modify" title="Modify" />
												</s:if>
												<s:else>
													<a class="marginBtn" href="javascript:submitAction('editOverrideDNS')"><img class="dinl"
													src="<s:url value="/images/modify.png" />"
													width="16" height="16" alt="Modify" title="Modify" /></a>
												</s:else>
			
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr><td height="6px"/> </tr>
						</table>
					</td>
				</tr>
				<tr>
					<td align="center" style="padding: 10px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td style="padding-right: 10px;">
									<input type="button" name="ignore" value="Save"
										class="button" onClick="submitSubnetAction('saveSubnet');"
										<s:property value="writeDisabled" />>
								</td>
								<td style="padding-left: 10px;">
									<input type="button" name="ignore" value="Cancel"
										class="button" onClick="hideSubnetOverlay();">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</div>


</s:form></div>
<div id="classfierNewRulePanel" style="display: none;height: 90px">
	<table width="140" border="0" >
		<tr>
		  <td align="left" ><s:text name="home.hmSettings.devicetag.newrulealert" /></td>
		</tr>
        <tr>
           <td align="center" >
              <input type="button" value="Yes" id="discardNewRule">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <input type="button" value="No" id="returnEditNewRule">
           </td>
        </tr>
     </table>
</div> 
<div id="classfierEditPanel" style="display: none;">
	<table width="270" border="0" >
		<tr id="editClassifierTagContainer">
		</tr>
        <tr>
           <td align="center" >
              <input type="button" value="Save" id="saveInEditClassifierTagContainer">
              <input type="button" value="Cancel" id="cancelInEditClassifierTagContainer">
           </td>
        </tr>
     </table>
</div>

<div style="display: none;">
<input type="button" value="Submit" id="ahTempClassifierEditBtn">
<input type="button" value="Submit" id="ahTempClassifierNewRuleBtn">  
<input type="text" value="Submit" id="ahTempClassifierIndexValue" value="">
<input type="text" value="Submit" id="ahTempClassifierItemType" value="">
<input type="text" value="Submit" id="ahTempClassifierKeyValue" value="">

</div>

<script>
var subnetOverlay = null;
function initSubnetPanel() {
	// create subnet overlay
	var div = document.getElementById('subnetPanel');
	subnetOverlay = new YAHOO.widget.Panel(div, {
		width:"650px",
		visible:false,
		draggable:false,
		modal:true,
		constraintoviewport:true,
		zIndex:10
		});
	subnetOverlay.render(document.body);
	div.style.display = "";

	initThumbSlider();

	initDHCPPoolSlider();

	// fire the onclick event
	var selectUI = Get("ipReserveClassType");
	if(selectUI) {
		selectUI.onclick();
	}

}

var portListPanel = null;
function initportListPanel() {
// create subnet overlay
	var div = document.getElementById('portPanel');
	portListPanel = new YAHOO.widget.Panel(div, {
		width:"500px",
		visible:false,
		draggable:true,
		modal:true,
		fixedcenter:true,
		constraintoviewport:true,
		zIndex:10
		});
	portListPanel.setHeader('<s:text name="config.vpn.subnet.aerohivePorts"/>');
	portListPanel.render(document.body);
	div.style.display = "";
}

function openPortListPanel(src){
	if(null==portListPanel){
		initportListPanel();
	}
    portListPanel.center();
   //var x = YAHOO.util.Dom.getX(src);
   //var y = YAHOO.util.Dom.getY(src);
   //portListPanel.cfg.setProperty("xy",[x-290,y-350]);
	portListPanel.cfg.setProperty('visible', true);
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=viewPortList&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succViewPort, failure : resultDoNothing, timeout: 60000}, null);
}
var succViewPort = function(o) {
	eval("var details = " + o.responseText);
	portListPanel.setBody(details.portListStr); 
}

function openSubnetOverlay(){
	if(null == subnetOverlay){
		initSubnetPanel();
	}
	subnetOverlay.center();
	hm.util.hide('subnet1ClassifierTagContainer');
	hm.util.hide('subnet2ClassifierTagContainer');
	subnetOverlay.cfg.setProperty('visible', true);
}

function hideSubnetOverlay(){
	setTagForCancellingSubnet();
	if(null != subnetOverlay){
		subnetOverlay.cfg.setProperty('visible', false);
	}
}

function initialValues(){
	Get("replicateSameSubnetwork").checked = true;
	Get("subNetwork").value = '';
	Get("oldSubnetwork").value = '';

	Get("subEnableNetClass").checked = false;
	//Get("subnetDeviceName").value = '';
    //initTagValues("subnetClassTag", "subnetClassValue");

	Get("subEnableIpReserveClass").checked = false;
	//Get("ipReserveClassIp").value = '';
	//Get("deviceName").value = '';
   // initTagValues("ipReserveClassTag", "ipReserveClassValue");
   
    Get("uniqueSubnetwork").value = false;
    Get("subDefaultGatewayFirstIP").checked = true;
	enableNatClass(true);
	Get("enableNatClass").disabled = true;
	disableIpBranchTag();
    
	Get("enablePortForwardingClass").checked = false;
	Get("subNatIpNetwork").value="";
	
	Get("natIPLocalIPMappedNote").innerHTML = "";
	Get("subnetIpnetwork").innerHTML = '<s:text name="config.vpn.subnet.localSubnetwork"/><font color="red"><s:text name="*"/></font>';
	Get("firstIPAsDefaultGateway").innerHTML = '<s:text name="config.vpn.subnet.firstLocalIPAdDefaultGateway"/>';
	Get("lastIPAsDefaultGateway").innerHTML = '<s:text name="config.vpn.subnet.lastLocalIPAdDefaultGateway"/>';
	Get("useIpClass").innerHTML = '<s:text name="config.vpn.subnet.natIp.class"/>';
	
	Get("tr_natIPAddressSpaceNote").style.display="none";
	Get("tr_natIPAddressSpace").style.display="none";
	Get("tr_natIPLocalIPMappedNote").style.display="none";
	Get("tr_numberOfBranches").style.display="";
	Get("tr_subNatIpSpacePool").style.display="";
	 
	Get("numberOfBranches").value="";
	Get("subNatIpSpacePool").value="";
	Get("natNetMask").value="";
}

function displayIpClassApplyTR(){
	Get("ipClassNewButtonTR").style.display="none";
	Get("ipClassApplyButtonTR").style.display="";
	//Get("ipClassApplyDetailTR").style.display="";
}
function hideIpClassApplyTR(){
	Get("ipClassNewButtonTR").style.display="";
	Get("ipClassApplyButtonTR").style.display="none";
	//Get("ipClassApplyDetailTR").style.display="none";
}

function displaySubnetClassApplyTR(){
	Get("subnetClassNewButtonTR").style.display="none";
	Get("subnetClassApplyButtonTR").style.display="";
	//Get("subnetClassApplyDetailTR").style.display="";
}
function hideSubnetClassApplyTR(){
	Get("subnetClassNewButtonTR").style.display="";
	Get("subnetClassApplyButtonTR").style.display="none";
	//Get("subnetClassApplyDetailTR").style.display="none";
}

function newSubnetwork(){
	var cbs = document.getElementsByName('ruleIndices');
	if (cbs.length>15) {
		hm.util.reportFieldError(Get("checkAll"), 'You cannot create more than 16 subnetworks.');
		return false;
	}
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=newSubnet" + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succnew, failure : resultDoNothing, timeout: 60000}, null);
}

var succnew = function(o) {
	if(null == subnetOverlay){
		initSubnetPanel();
	}
	eval("var details = " + o.responseText);
	Get("subKey").value = details.v;
	document.forms[formName].vlanId.value = Get("subKey").value;
	//Get("subnetClassDetailDiv").innerHTML=details.t1;
	//Get("ipReserveClassDetailDiv").innerHTML=details.t2;
	setPortForwarding(details.t3)
	initialValues();
	//Get("emptySubnetClassTR").style.display="";
	Get("emptyIpReserveClassTR").style.display="";
	Get("ipReserveClassDiv").style.display="none";
	Get("subnetClassDiv").style.display="none";
	Get("natSettings").style.display="";
	hideIpClassApplyTR();
	hideSubnetClassApplyTR();
	portForwardingCancelButtonTR();
	Get("portForwardingClassDiv").style.display="none";
	
    $("#subnet1ItemsRow ul").empty();
    $("#subnet2ItemsRow ul").empty();
    $("#subnet1ClassifierTagContainer").hide();
    $("#subnet2ClassifierTagContainer").hide();

	$( "#slider-thumb" ).slider( "option", "disabled", true );
	$( "#slider-thumb" ).slider( "option", "value", 1);
	Get("branchSize").innerHTML=1;
	Get("subnetBranchNetMaskSpan").innerHTML="";
	Get("perBranch").innerHTML=0;
	setDHCPPoolSlider(0);
	hm.util.hide("tr_ipBranchTag");

	/*====== DHCP Settings =====*/
	Get(formName + "_subEnableDhcp").checked = true;
	changeSubDhcpValue(true);
	Get(formName + "_subLeaseTime").value = 86400;
	Get(formName + "_subNtpServerIp").value = "";
	Get(formName + "_subDomainName").value = "";
	Get(formName + "_enableArpCheck").checked = true;
	/*===== DHCP Custom =====*/
	setSubnetworkCustom();
	hm.util.show("subCustomCreateButton");
	hm.util.show("subCustomCreateSection");
	hm.util.hide("subCustomNewButton");
	showHideContent("subCustomOption", "none");
	showHideContent("natSettings", "none");
	/*======= Override DNS======*/
	Get(formName + "_overrideDNSService").checked = false;
	Get(formName + "_overrideDNSServiceId").value = -1;
	changeOverrideDNS(false);
	
	openSubnetOverlay();

	//for fix bug 23244, set unique subnetwork is default value
	Get("uniqueSubnetwork").checked = true;
	changeSubnetworkIpAddressMode();
	
	if(isMgt0()) {
		Get("enableNatClass").disabled = true;
		Get("replicateSameSubnetwork").disabled = true;
	}
};

function isMgt0() {
	return Get("vpnNetworks_blnMgtNetwork") != null && Get("vpnNetworks_blnMgtNetwork").value == 'true';
}

function cloneSubnetwork() {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(Get("checkAll"), '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(Get("checkAll"), 'Please select one item.');
			return false;
		}
		var ruleOneIndex=0;
		var selectCount = 0;
		for (var i = 0; i < cbs.length; i++) {
			if (cbs[i].checked) {
				selectCount++;
				ruleOneIndex=i;
			}
		}
		if (selectCount != 1) {
			hm.util.reportFieldError(Get("checkAll"), 'Please select one item.');
			return false;
		}
		var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=cloneSubnetwork" + "&ruleOneIndex=" + ruleOneIndex + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succModify, failure : resultDoNothing, timeout: 60000}, null);
}
function modifySubnetwork(index) {
	if (index==-1) {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(Get("checkAll"), '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(Get("checkAll"), 'Please select one item.');
			return false;
		}
		var ruleOneIndex=0;
		var selectCount = 0;
		for (var i = 0; i < cbs.length; i++) {
			if (cbs[i].checked) {
				selectCount++;
				ruleOneIndex=i;
			}
		}
		if (selectCount != 1) {
			hm.util.reportFieldError(Get("checkAll"), 'Please select one item.');
			return false;
		}
		var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=modifySubnetwork" + "&ruleOneIndex=" + ruleOneIndex + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succModify, failure : resultDoNothing, timeout: 60000}, null);
	} else {
		var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=modifySubnetwork" + "&ruleOneIndex=" + index + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succModify, failure : resultDoNothing, timeout: 60000}, null);

	}
}

function countSubnetworkCont(natnetwork, network) {
	var natIpArr=natnetwork.split("/");
	if (natIpArr.length!=2) return 0;
	var ipArr=network.split("/");
	if (ipArr.length!=2) return 0;
	var count = Math.pow(2,parseInt(ipArr[1]) - parseInt(natIpArr[1]));
	return count;
}

var succModify = function(o) {
	if(null == subnetOverlay){
		initSubnetPanel();
	}
	eval("var details = " + o.responseText);
	
	initialValues();
	if(details.uniqueSubnetwork) {
		Get("uniqueSubnetwork").checked = true;
		changeSubnetworkIpAddressMode();
	} else {
		changeSubnetworkIpAddressMode();
		if(details.ip != "") {
			var natIpArr=details.ip.split("/");
			Get("subNatIpSpacePool").value = natIpArr[0];
			Get("natNetMask").value = natIpArr[1];
			Get("numberOfBranches").value = countSubnetworkCont(details.ip, details.localIpNetwork);
		}
	}
	if(details.defaultGateway == 1) {
		Get("subDefaultGatewayLastIP").checked = true;
	} else if(details.defaultGateway == 0) {
		Get("subDefaultGatewayFirstIP").checked = true;
	}
	
	if(details.enableNat) {
		enableNatClass(true);
		Get("subNatIpNetwork").value=details.ip;
		Get("natIPLocalIPMappedNote").innerHTML = '<s:text name="config.vpn.subnet.localIPMappedToNatIp"><s:param>' + details.localIpNetwork + '</s:param><s:param>' + details.ip + '</s:param></s:text>';
	} else {
		enableNatClass(false);
	}
	if(details.enablePortForwarding) {
		Get("portForwardingClassDiv").style.display="";
		Get("enablePortForwardingClass").checked = true;
		portForwardingCancelButtonTR();
	} else {
		Get("portForwardingClassDiv").style.display="none";
		Get("enablePortForwardingClass").checked = false;
	}
	if(details.enableNat || details.enablePortForwarding){
		showHideContent("natSettings", "");
	}else{
		showHideContent("natSettings", "none");
	}
	var tempSubKeyValue=details.v;
	var getRuleKey=tempSubKeyValue;
	if(null!=details.cloneSubNetwokClass){
		getRuleKey=details.cloneSubNetwokClass;
	}
	if(null!=details.cloneReserveClass){
		getRuleKey=details.cloneReserveClass;
	}
	$("#subnet1ItemsRow ul").empty();
	$("#subnet2ItemsRow ul").empty();
	Get("subKey").value = tempSubKeyValue;


	var detailt1=details.tt1;
	var subJsonobj = eval('('+detailt1+')');
	var size = subJsonobj.subNetwokClass.length;
	 for(var k=0;k<size;k++){
		 subNtClz[k]=subJsonobj.subNetwokClass[k];
	 }
	 for(var k=0;k<size;k++){
		 if(subNtClz[k]==undefined) continue;
		 var tmpItme=subNtClz[k];
		 if(tmpItme.ruleKey==getRuleKey)
			 $("#subnet1ClassifierTagContainer").classifierTag('addItem', tmpItme);
	 }

	var detailt2=details.tt2;
	var ipJsonobj = eval('('+detailt2+')');
	size = ipJsonobj.reserveClass.length;
	for(var k=0;k<ipJsonobj.reserveClass.length;k++){
		ipRsvClz[k]=ipJsonobj.reserveClass[k];
	}
	for(var k=0;k<size;k++){
		 if(ipRsvClz[k]==undefined) continue;
		 var tmpItme=ipRsvClz[k];
		 if(tmpItme.ruleKey==getRuleKey)
		 $("#subnet2ClassifierTagContainer").classifierTag('addItem', tmpItme);
	}
	 
	document.forms[formName].vlanId.value = tempSubKeyValue;
	Get("subNetwork").value = details.localIpNetwork;
	Get("oldSubnetwork").value = details.localIpNetwork;

	Get("subEnableNetClass").checked = details.e1;
	//Get("subnetDeviceName").value = '';
    //initTagValues("subnetClassTag", "subnetClassValue");

	Get("subEnableIpReserveClass").checked = details.e2;
	//Get("ipReserveClassIp").value = '';
	//Get("deviceName").value = '';
   // initTagValues("ipReserveClassTag", "ipReserveClassValue");

	//Get("subnetClassDetailDiv").innerHTML=details.t1;
	//Get("ipReserveClassDetailDiv").innerHTML=details.t2;
	setPortForwarding(details.t3)
	if (details.e1) {
		Get("subnetClassDiv").style.display="";
	} else {
		Get("subnetClassDiv").style.display="none";
	}

	if (details.e2) {
		Get("ipReserveClassDiv").style.display="";
	} else {
		Get("ipReserveClassDiv").style.display="none";
	}

	if (details.nt1){
		//Get("emptySubnetClassTR").style.display="";
	} else {
		//Get("emptySubnetClassTR").style.display="none";
	}

	if (details.nt2){
		Get("emptyIpReserveClassTR").style.display="";
	} else {
		Get("emptyIpReserveClassTR").style.display="none";
	}
	
	if (details.nt3){
		Get("emptyPortForwarding").style.display="";
	} else {
		Get("emptyPortForwarding").style.display="none";
	}
	hideSubnetClassApplyTR();
	hideIpClassApplyTR();

	$("#slider-thumb").slider( "option", "disabled", false );
	var totlaC= countNetWrokIp(Get("subNetwork"));
	if (totlaC>=4) {
		var maxSize= parseInt(totlaC/4);
		if(maxSize>DEFAULT_BRANCHES_MAX_SIZE) {
			maxSize = DEFAULT_BRANCHES_MAX_SIZE;
		}
		$( "#slider-thumb" ).slider( "option", "max", maxSize);
		$( "#slider-thumb" ).slider( "option", "value", details.b);
		Get("branchSize").innerHTML=details.b;

		var ic=0;
		while (details.b>=Math.pow(2,ic)){
			ic++;
		}
		var ipNetwork=Get("subNetwork");
		var ipArr=ipNetwork.value.split("/");
		var netmaskInteger= parseInt(ipArr[1]);
		if (ic>0) {
			netmaskInteger = netmaskInteger + ic -1;
		}

		Get("subnetBranchNetMaskSpan").innerHTML="Netmask: " + hm.util.intToStringNetMask(netmaskInteger) + " (/" + netmaskInteger + ")";

		var subNetIpCount=calcuNetIPCount(details.b);
		if (isNaN(subNetIpCount) || subNetIpCount<0) {
			subNetIpCount=0;
		}
		Get("perBranch").innerHTML=subNetIpCount;

		setDHCPPoolSlider(subNetIpCount, [details.le, subNetIpCount - details.re]);
	} else {
		$( "#slider-thumb" ).slider( "option", "disabled", true );
		Get("branchSize").innerHTML=1;
		Get("subnetBranchNetMaskSpan").innerHTML="";
		Get("perBranch").innerHTML=0;
		setDHCPPoolSlider(0);
	}

	/*====== DHCP Settings =====*/
	Get(formName + "_subEnableDhcp").checked = details.enableDHCP;
	changeSubDhcpValue(details.enableDHCP);
	Get(formName + "_subLeaseTime").value = details.leaseTime;
	Get(formName + "_subNtpServerIp").value = details.ntpIP;
	Get(formName + "_subDomainName").value = details.dName;
	Get(formName + "_enableArpCheck").checked = details.enableArpCheck;
	/*===== DHCP Custom =====*/
	if(details.tableHTML) {
		setSubnetworkCustom(details.tableHTML);
		hm.util.hide("subCustomCreateButton");
		hm.util.hide("subCustomCreateSection");
		hm.util.show("subCustomNewButton");
		showHideContent("subCustomOption", "");
	} else {
		setSubnetworkCustom();
		hm.util.show("subCustomCreateButton");
		hm.util.show("subCustomCreateSection");
		hm.util.hide("subCustomNewButton");
		showHideContent("subCustomOption", "none");
	}

	openSubnetOverlay();
	
	if(isMgt0() && details.uniqueSubnetwork) {
		Get("replicateSameSubnetwork").disabled = true;
		Get("enableNatClass").disabled = !details.enableNat;
	}
	
	// BR for GWL
	Get(formName + "_overrideDNSService").checked = details.overrideDNSService;
	if(details.overrideDNSService){
		Get(formName + "_overrideDNSService").checked = details.overrideDNSService;
		Get(formName + "_overrideDNSServiceId").value = details.overrideDNSServiceId;
	}
	changeOverrideDNS(details.overrideDNSService);
};


var resultDoNothing = function(o) {
	//	alert("failed.");
};

function submitSubnetAction(opt){
	if (opt=='addSubnetClass') {
	    var url = validateAddSubnetClass();
	    if(url) {
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
					{success : succAddnetClass, failure : resultDoNothing, timeout: 60000}, null);
	    } else {
	    	return false;
	    }
	} else if (opt=='addReserveIpClass') {
		if (!validNetWorkIp()) {
			return false;
		}
		
		var subUniqueSubnetworkForEachBranches = Get("uniqueSubnetwork").checked;
		if (!subUniqueSubnetworkForEachBranches && !validNatNetWorkIp(true)) {
			Get("natSettings").style.display="";
			return false;
		}

		if (cbs.length>255) {
			hm.util.reportFieldError(Get("checkAllIpReserveClass"), 'You cannot create more than 256 items.');
			return false;
		}

		if(subUniqueSubnetworkForEachBranches) {
			var sliderValue = $("#slider-thumb").slider( "option", "value");
			var ic=0;
			while (sliderValue>=Math.pow(2,ic)){
				ic++;
			}
			sliderValue =Math.pow(2,ic-1);

			if (cbs.length>=sliderValue){
				hm.util.reportFieldError(Get("checkAllIpReserveClass"), 
						'The branches size is '+ sliderValue +', you cannot create more than '+ sliderValue +' items.');
				return false;
			}
		} else {
			var subnetworkCounts = countSubnetworkCont(Get("subNatIpNetwork").value, Get("subNetwork").value);
			if (cbs.length>=subnetworkCounts){
				hm.util.reportFieldError(Get("checkAllIpReserveClass"), 
						'The subnetworks size is '+ subnetworkCounts +', you cannot create more than '+ subnetworkCounts +' items.');
				return false;
			}
		}
		
		var ipReserveClassIp=Get("ipReserveClassIp").value;
		if (ipReserveClassIp.length==0) {
			hm.util.reportFieldError(Get("ipReserveClassIp"),
					'<s:text name="error.requiredField"><s:param><s:text name="config.vpn.subnet.ipAddress" /></s:param></s:text>');
	 		Get("ipReserveClassIp").focus();
	 		return false;
		}
		if(!checkIpAddress(Get("ipReserveClassIp"), Get("ipReserveClassIp"),ipReserveClassIp, '<s:text name="config.vpn.subnet.ipAddress" />')) {
			return false;
		}
		// create an object for the error row message
		var errorRow = new Object();
		errorRow.id='errorRow';

		var ipClassType = Get("ipReserveClassType").value;
		debug("ipClassType:"+ipClassType);
		var url;
		var subKey=Get("subKey").value;
		if(ipClassType == IPRESERVATION_TYPE_MAP) {
			// Map type
			debug("map type.");
			var locationElement = Get("locationId");
	 		if(locationElement.value == -1) {
				hm.util.reportFieldError(errorRow,
						'<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.location" /></s:param></s:text>');
				locationElement.focus();
			    return false;
			}
			url = "<s:url action='vpnNetworks' includeParams='none' />?operation=addReserveIpClass" + "&subKey="+ subKey
			+ "&ipReserveClassType=" + ipClassType + "&ipReserveClassIp=" + ipReserveClassIp
			+ "&locationId=" + locationElement.value
			+ "&ignore="+new Date().getTime();
		} else if(ipClassType == IPRESERVATION_TYPE_HIVEAP) {
			// Device type
			debug("device type.");
	 		var deviceName = Get("deviceName").value;
			if(deviceName.length == 0) {
				hm.util.reportFieldError(errorRow,
						'<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.hiveAP" /></s:param></s:text>');
				Get("deviceName").focus();
        		return false;
			} else if (deviceName.indexOf(' ') > -1) {
        		hm.util.reportFieldError(errorRow,
        				'<s:text name="error.name.containsBlank"><s:param><s:text name="config.ipAddress.hiveAP" /></s:param></s:text>');
        		Get("deviceName").focus();
        		return false;
    		}
			url = "<s:url action='vpnNetworks' includeParams='none' />?operation=addReserveIpClass" + "&subKey="+ subKey
			+ "&ipReserveClassType=" + ipClassType + "&ipReserveClassIp=" + ipReserveClassIp
			+ "&deviceName=" + encodeURIComponent(deviceName)
			+ "&ignore="+new Date().getTime();
		} else if(ipClassType == IPRESERVATION_TYPE_CLASSIFIER) {
			// Classifier type
			debug("classifier type.");
			if(!isCheckedTag([Get("ipReserveClassTag_1"), Get("ipReserveClassTag_2"), Get("ipReserveClassTag_3")], errorRow)) {
				return false;
			}
	        // Classifier type
	        var ipArray = new Array();
	        if(!checkSubNetworkTag(Get("ipReserveClassTag_1"), "ipReserveClassValue", "1", errorRow, ipArray)
	                ||!checkSubNetworkTag(Get("ipReserveClassTag_2"), "ipReserveClassValue", "2", errorRow, ipArray)
	                ||!checkSubNetworkTag(Get("ipReserveClassTag_3"), "ipReserveClassValue", "3", errorRow, ipArray)) {
	            return false;
	        }

			url = "<s:url action='vpnNetworks' includeParams='none' />?operation=addReserveIpClass" + "&subKey="+ subKey
					+ "&ipReserveClassType=" + ipClassType + "&ipReserveClassIp=" + ipReserveClassIp
					+ "&ipReserveClassValue=" + encodeURIComponent(ipArray.toString())
					+ "&ignore="+new Date().getTime();
		} else {
        		hm.util.reportFieldError(errorRow,
        				'<s:text name="error.requiredField"><s:param><s:text name="config.vpn.subnet.type" /></s:param></s:text>');
        		Get("ipReserveClassType").focus();
        		return false;
		}

		var messages;
		if(subUniqueSubnetworkForEachBranches) {
			var ipArr=Get("subNetwork").value.split("/");
			var messages = hm.util.validateIpSubnet(ipReserveClassIp,'<s:text name="config.vpn.subnet.ipAddress" />',
					ipArr[0],'<s:text name="config.vpn.subnet.ipnetwork"/>',
					hm.util.intToStringNetMask(parseInt(ipArr[1])));
		} else {
			var natIpArr=Get("subNatIpNetwork").value.split("/");
			var messages = hm.util.validateIpSubnet(ipReserveClassIp,'<s:text name="config.vpn.subnet.ipAddress" />',
					natIpArr[0],'<s:text name="config.vpn.subnet.natipnetwork"/>',
					hm.util.intToStringNetMask(parseInt(natIpArr[1])));
		}
		if (null != messages) {
		 	hm.util.reportFieldError(Get("ipReserveClassIp"), messages);
            Get("ipReserveClassIp").focus();
            return false;
		}

		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succAddIpReserveClass, failure : resultDoNothing, timeout: 60000}, null);
	} else if (opt=='removeSubnetClass'){
		var cbs = document.getElementsByName('subnetClassIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(Get("checkAllSubnetClass"), '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(Get("checkAllSubnetClass"), '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		var selectArray="";
		for (var i = 0; i < cbs.length; i++) {
			if (cbs[i].checked) {
				if (selectArray.length==0) {
					selectArray= selectArray + cbs[i].value;
				} else {
					selectArray= selectArray + "," +  cbs[i].value;
				}
			}
		}
		var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=removeSubnetClass"
				+ "&subnetClassIndices="+ selectArray  + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
				{success : succRemoveSubnetClass, failure : resultDoNothing, timeout: 60000}, null);

	} else if(opt=='removeReserveIpClass'){
		var cbs = document.getElementsByName('ipReserveClassIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(Get("checkAllIpReserveClass"), '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(Get("checkAllIpReserveClass"), '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
			return false;
		}
		var selectArray="";
		for (var i = 0; i < cbs.length; i++) {
			if (cbs[i].checked) {
				if (selectArray.length==0) {
					selectArray= selectArray + cbs[i].value;
				} else {
					selectArray= selectArray + "," + cbs[i].value;
				}
			}
		}
		var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=removeReserveIpClass" +
				"&ipReserveClassIndices="+ selectArray  + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
				{success : succRemoveReserveIpClass, failure : resultDoNothing, timeout: 60000}, null);
	} else if (opt=="saveSubnet" || opt == "newOverrideDNS" || opt == "editOverrideDNS") {
		var subUniqueSubnetworkForEachBranches = Get("uniqueSubnetwork").checked;
		
		if (!validNetWorkIp()) {
			return false;
		}
		
		if((opt=="saveSubnet"|| opt == "editOverrideDNS") && !validateOverrideDNSService()){
			return false;
		}
		
		var vlu11=$("#subnet1ClassifierTagContainer").find("input#deviceId").val();
		var vlu12=$("#subnet1ClassifierTagContainer").find("input#locationId_hidden").val();
		var vlu13=$("#subnet1ClassifierTagContainer").find("input#tag1Id").val();
		var vlu14=$("#subnet1ClassifierTagContainer").find("input#tag2Id").val();
		var vlu15=$("#subnet1ClassifierTagContainer").find("input#tag3Id").val();		
		var vlu2=$("#subnet2ClassifierTagContainer").find("input#ipReserveClassIp").val();
		var isS1Visable=$("#subnet1ClassifierTagContainer").is(':visible');
		var isS2Visable=$("#subnet2ClassifierTagContainer").is(':visible');		
		
		if((vlu2!=null&&vlu2!=""&&isS2Visable)||((vlu11!=null&&vlu11!="")||(vlu12!=null&&vlu12!="")||(vlu13!=null&&vlu13!="")||(vlu14!=null&&vlu14!="")||(vlu15!=null&&vlu15!=""))&&isS1Visable){
		var DOM = YAHOO.util.Dom;	    
	    var newRulePanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierNewRulePanel', {width: 140, closeIcon: 'images/cancel.png'});
	    
        DOM.get('ahTempClassifierNewRuleBtn').onclick = function(){
    		$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
    		newRulePanel.openDialog();
    		 DOM.get('returnEditNewRule').focus();
    	};
	    
	     DOM.get('returnEditNewRule').onclick = function(){     		
    		newRulePanel.closeDialog();
  		 };
  		 
   		 DOM.get('discardNewRule').onclick = function(){ 
    		continueSaveSubnet(opt);
     		newRulePanel.closeDialog();
    	 };
    	 
    	 $('#classfierNewRulePanelAPanel_c').keyup(function(e) {
           if (event.which === 13) {
        		 newRulePanel.closeDialog();
          }
          });
    	 
    	 $("input#ahTempClassifierNewRuleBtn").click();
	       return;	
		}
		var returnFlag= continueSaveSubnet(opt);
		return returnFlag;
	}
}
function setTagForCancellingSubnet(){
	$("#saveOrCancelSubnet").attr("value","cancel");
}
function setTagForSavingSubnet(){
	$("#saveOrCancelSubnet").attr("value","save");
}
function continueSaveSubnet(opt){
		if(opt == ""){
			opt = "saveSubnet"
		}
		var subUniqueSubnetworkForEachBranches = Get("uniqueSubnetwork").checked;
		var sliderValue = $("#slider-thumb").slider( "option", "value");
		var ic=0;
		while (sliderValue>=Math.pow(2,ic)){
			ic++;
		}
		sliderValue =Math.pow(2,ic-1);

		var subNetIpCount=calcuNetIPCount(sliderValue);
		if (isNaN(subNetIpCount) || subNetIpCount<0) {
			subNetIpCount=0;
		}

		if (subNetIpCount<=0){
			hm.util.reportFieldError(Get("ipBranchTag"), 'Clients/Branch cannot be 0.');
	 		return false;
		}
		var enableDHCP = Get("vpnNetworks_subEnableDhcp").checked;
		if(isMgmtNetworkType() && !enableDHCP) {
		    var errorRow = new Object();
	        errorRow.id='ErrorEnableDHCP';
			hm.util.reportFieldError(errorRow, '<s:text name="warn.config.vpnnetwork.not.enableDHCP"/>');
			return false;
		}
		
		if(Get("enableNatClass").checked && !validNatNetWorkIp(true)) {
			return false;
		}
		
		var dhcpOption = "";
		if (enableDHCP) {
			var leaseTime = Get("vpnNetworks_subLeaseTime");
			var ntpServer = Get("vpnNetworks_subNtpServerIp");
			var domainName = Get("vpnNetworks_subDomainName");
			var enableArpCheck = Get("vpnNetworks_enableArpCheck");
	 		if(!checkNumber(leaseTime, leaseTime,
	 				'<s:text name="config.vpn.network.leaseTime" />', 60, 86400000)) {
	       		return false;
	 		}
	 		if (ntpServer.value.length != 0) {
				if(!checkIpAddress(ntpServer, ntpServer, ntpServer.value,
						'<s:text name="config.vpn.network.ntpServerIp" />')) {
					return false;
				}
			}
	 		dhcpOption = "&subLeaseTime=" + leaseTime.value +"&subEnableDhcp=" + true;
			if(ntpServer.value.length != 0) {
				dhcpOption = dhcpOption + "&subNtpServerIp=" + ntpServer.value;
			}
			if(domainName.value.length != 0) {
				dhcpOption = dhcpOption + "&subDomainName=" + encodeURIComponent(domainName.value);
			}
			dhcpOption = dhcpOption + "&enableArpCheck=" + enableArpCheck.checked;
			
		}
		
		if (Get("subEnableNetClass").checked) 
		{		
		   var  netClassSize=$("#subnet1ItemsRow ul.itemContainer").find("li.item").size();			
			if (netClassSize==0) 
			{
				hm.util.reportFieldError(Get("checkAllSubnetClass"),'Items is a required field.');
				return false;
			}
		} 
		if(Get("enablePortForwardingClass").checked){
			var cbs = document.getElementsByName('PortForwardingIndices');
			if (cbs.length==0) {
				hm.util.reportFieldError(Get("checkAllPortForwarding"),
						'<s:text name="error.requiredField"><s:param>Items</s:param></s:text>');
				return false;
			}
		}
		if (Get("subEnableIpReserveClass").checked) 
		{		
		   var  ipReserveClassSize=$("#subnet2ItemsRow ul.itemContainer").find("li.item").size();			
			if (ipReserveClassSize==0) 
			{
				hm.util.reportFieldError(Get("checkAllIpReserveClass"),'Items is a required field.');
				return false;
			}
		} 

		var isDHCPSliderDisable = $("#dhcp-slider").slider("option", "disabled");

		var subLeftEnd = isDHCPSliderDisable? 0 : $("#dhcp-slider").slider("values", 0);
		var subRightEnd = isDHCPSliderDisable? 0 : $("#dhcp-slider").slider("option", "max") - $("#dhcp-slider").slider("values", 1);

		//fnr add, when not enabled dhcp don't check , need confirm by yunzhi
		if (Get(formName + "_subEnableDhcp").checked) {
			if (subLeftEnd+subRightEnd>=subNetIpCount){
				hm.util.reportFieldError(Get("remainAddressIdErr"), 'Address Pool cannot be 0.');
		 		return false;
			} else if(subNetIpCount-subLeftEnd-subRightEnd > MAX_DHCP_CLIENT_SIZE) {
				// The maximum number of DHCP clients is 512 because of the limited amount of flash memory
				hm.util.reportFieldError(Get("remainAddressIdErr"), 'Address Pool cannot exceed 512.');
		 		return false;
			}
		}

		var subDefaultGateway = 0;
		if (!Get("subDefaultGatewayFirstIP").checked) {
			subDefaultGateway = 1;
		}
		
		var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=" + opt
		  + "&subNetwork="+ encodeURIComponent(Get("subNetwork").value)
		  + "&subLeftEnd="+ subLeftEnd
		  + "&subRightEnd="+ subRightEnd
		  + "&subBranch="+ sliderValue
		  + "&subEnableNetClass="+ Get("subEnableNetClass").checked
		  + "&subEnableIpReserveClass="+ Get("subEnableIpReserveClass").checked
		  + "&subKey="+ Get("subKey").value
		  + "&subUniqueSubnetworkForEachBranches="+ subUniqueSubnetworkForEachBranches
		  + "&subDefaultGateway="+ subDefaultGateway
		  + "&subEnableNat="+ Get("enableNatClass").checked
		  + "&subNatIpNetwork="+ Get("subNatIpNetwork").value
		  + "&positionRange="+positionRange
		  + "&subEnablePortForwarding="+ Get("enablePortForwardingClass").checked
		  + "&overrideDNSService=" + Get(formName + "_overrideDNSService").checked
		  + "&overrideDNSServiceId=" + Get(formName + "_overrideDNSServiceId").value
		  + dhcpOption
		  + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succSaveSubnet, failure : resultDoNothing, timeout: 60000}, null);
}

function validNatNetWorkIp(showErrorMsg){
	var subUniqueSubnetworkForEachBranches = Get("uniqueSubnetwork").checked;
	if(subUniqueSubnetworkForEachBranches) {
		Get("natIPLocalIPMappedNote").innerHTML = "";
		var ipNatNetwork=Get("subNatIpNetwork");
		if (ipNatNetwork.value.length==0) {
			if(showErrorMsg) {
				hm.util.reportFieldError(ipNatNetwork, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.subnet.natIPAddressSpace" /></s:param></s:text>');
			}
			ipNatNetwork.focus();
	 		return false;
		}
		var ipArr=ipNatNetwork.value.split("/");
		if (ipArr.length!=2) {
			if(showErrorMsg) {
				hm.util.reportFieldError(ipNatNetwork, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.subnet.natIPAddressSpace" /></s:param></s:text>');
			}
			ipNatNetwork.focus();
			return false;
		}
		if(!checkIpAddress(ipNatNetwork, ipNatNetwork, ipArr[0], '<s:text name="config.vpn.subnet.natIPAddressSpace" />')) {
			return false;
		}

		var	message = hm.util.validateIntegerRange(ipArr[1], '<s:text name="config.vpn.subnet.natIPAddressSpace" />' + ' netmask',4,31);
		if (message != null) {
			if(showErrorMsg) {
	        	hm.util.reportFieldError(ipNatNetwork, message);
			}
	        ipNatNetwork.focus();
	        return false;
	    }
		var ipNetwork=Get("subNetwork");
		var ipNetworkArr=ipNetwork.value.split("/");
		if(ipArr[1] != ipNetworkArr[1]) {
			if(showErrorMsg) {
				hm.util.reportFieldError(ipNatNetwork, '<s:text name="error.vpn.subnet.mustSameIpSpace"></s:text>');
			}
			return false;
		}
		
		var messages = hm.util.validateIpSubnet(ipArr[0],'', ipNetworkArr[0],'', hm.util.intToStringNetMask(parseInt(ipNetworkArr[1])));

		if (typeof(messages) == "undefined") {
			if(showErrorMsg) {
				hm.util.reportFieldError(ipNatNetwork, '<s:text name="error.vpn.subnet.natnetworkSameAsNetwork"><s:param><s:text name="config.vpn.subnet.natIPAddressSpace" /></s:param><s:param><s:text name="config.vpn.subnet.localIpAddressSpace" /></s:param></s:text>');
			}
			return false;
		}
		
		Get("natIPLocalIPMappedNote").innerHTML = '<s:text name="config.vpn.subnet.localIPMappedToNatIp"><s:param>' + ipNetwork.value + '</s:param><s:param>' + ipNatNetwork.value + '</s:param></s:text>';
	} else {
		if(!validNumberOfBranches()) {
			return false;
		}
		var subNatIpSpacePool=Get("subNatIpSpacePool");
		if (subNatIpSpacePool.value.length==0) {
			if(showErrorMsg) {
				hm.util.reportFieldError(subNatIpSpacePool, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.subnet.natIPAddressSpacePool" /></s:param></s:text>');
			}
			subNatIpSpacePool.focus();
	 		return false;
		}
		if(!checkIpAddress(subNatIpSpacePool, subNatIpSpacePool, subNatIpSpacePool.value, '<s:text name="config.vpn.subnet.natIPAddressSpacePool" />')) {
			return false;
		}

		var messages = hm.util.validateIpSubnet(subNatIpSpacePool.value,'', Get("subNetwork").value.split("/")[0],'', hm.util.intToStringNetMask(parseInt(Get("natNetMask").value)));
		if (typeof(messages) == "undefined") {
			if(showErrorMsg) {
				hm.util.reportFieldError(subNatIpSpacePool, '<s:text name="error.vpn.subnet.natnetworkConstainsNetwork"><s:param><s:text name="config.vpn.subnet.natIPAddressSpacePool" /></s:param><s:param><s:text name="config.vpn.subnet.localSubnetwork" /></s:param></s:text>');
			}
			return false;
		}
		Get("subNatIpNetwork").value = Get("subNatIpSpacePool").value + "/" + Get("natNetMask").value;
	}
  return true;
}

function validNumberOfBranches() {
	if(validNetWorkIp()) {
		var branches = Get("numberOfBranches");
		if (branches.value.length==0) {
			hm.util.reportFieldError(branches, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.subnet.numberOfBranches" /></s:param></s:text>');
			branches.focus();
	 		return false;
		}
		
		if(branches.value < 1) {
			hm.util.reportFieldError(Get("numberOfBranches"), '<s:text name="error.vpn.subnet.natnetwork.numberOfBranchesTooSmall"><s:param><s:text name="config.vpn.subnet.numberOfBranches" /></s:param></s:text>');
			mask = "";
			branches.focus();
			return false;
		}
		
		var i=0;
		while(branches.value > Math.pow(2,i)) { 
			i++;
		}
		var mask;
		if(branches.value.trim() != "" && branches.value > 0) {
			mask = parseInt(Get("subNetwork").value.split("/")[1]) - i;
			if (mask < 4) {
				hm.util.reportFieldError(Get("numberOfBranches"), '<s:text name="error.vpn.subnet.natnetwork.numberOfBranchesTooLarge"><s:param><s:text name="config.vpn.subnet.numberOfBranches" /></s:param></s:text>');
				mask = "";
				return false;
			}
		} else {
			mask = "";
		}
	} else {
		mask = "";
		Get("numberOfBranches").value = "";
		return false;
	}
	Get("natNetMask").value = mask;
	return true;
}

function validNetWorkIp(){
		var ipNetwork=Get("subNetwork");
		var subUniqueSubnetworkForEachBranches = Get("uniqueSubnetwork").checked;
		if (ipNetwork.value.length==0) {
			if(subUniqueSubnetworkForEachBranches) {
				hm.util.reportFieldError(ipNetwork, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.subnet.localIpAddressSpace" /></s:param></s:text>');
			} else {
				hm.util.reportFieldError(ipNetwork, '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.subnet.localSubnetwork" /></s:param></s:text>');
			}
	 		ipNetwork.focus();
	 		return false;
		}
		var ipArr=ipNetwork.value.split("/");
		if (ipArr.length!=2) {
			if(subUniqueSubnetworkForEachBranches) {
				hm.util.reportFieldError(ipNetwork, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.subnet.localIpAddressSpace" /></s:param></s:text>');
			} else {
				hm.util.reportFieldError(ipNetwork, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vpn.subnet.localSubnetwork" /></s:param></s:text>');
			}
			ipNetwork.focus();
			return false;
		}
		
		if(subUniqueSubnetworkForEachBranches) {
			if(!checkIpAddress(ipNetwork, ipNetwork, ipArr[0], '<s:text name="config.vpn.subnet.localIpAddressSpace" />')) {
				return false;
			}
		} else {
			if(!checkIpAddress(ipNetwork, ipNetwork, ipArr[0], '<s:text name="config.vpn.subnet.localSubnetwork" />')) {
				return false;
			}
		}

		var message = null;
		if(subUniqueSubnetworkForEachBranches) {
			message = hm.util.validateIntegerRange(ipArr[1], '<s:text name="config.vpn.subnet.localIpAddressSpace" />' + ' netmask',4,31);
		} else {
			message = hm.util.validateIntegerRange(ipArr[1], '<s:text name="config.vpn.subnet.localSubnetwork" />' + ' netmask',4,31);
		}
		if (message != null) {
            hm.util.reportFieldError(ipNetwork, message);
            ipNetwork.focus();
            return false;
      }
      return true;
}

var succSaveSubnet=function(o) {
	eval("var details = " + o.responseText);
	if (details.r) {
		Get("emptyMainSubnetTR").style.display="none";
		Get("mainSubnetDetailTR").style.display="";
		set_innerHTML("mainSubnetDetailDiv", details.t);
		hideSubnetOverlay();
		setTagForSavingSubnet();
		if(details.modify) {
			if(details.operation != "newOverrideDNS" && details.operation != "editOverrideDNS"){
				showInfoDialog('<s:text name="warn.config.vpnnetwork.subnetwork.changed"/>');
			}else{
				<s:if test="%{jsonMode==true}">
				if (parent) {
					if(parent.isIFrameDialogOpen()) {
						document.forms[formName].parentIframeOpenFlg.value = true;
						if (operation != 'create') {
							showProcessing();
						}
						//top.changeIFrameDialog(740, 700);
					} else {
						var selectElementId = formName + "_overrideDNSServiceId";
						var overrideDNS = Get(formName + "_overrideDNSService");
						var url = '<s:url action="vpnNetworks" includeParams="none"/>'
							+ '?operation='+ details.operation + "Service"
							+ '&jsonMode=true'
							+ '&parentDomID=' + selectElementId
							+ '&overrideDNSService=' + overrideDNS.checked;
						
						if (details.operation + "Service" == 'editOverrideDNSService'){
							var value = Get(selectElementId).value;
							url = url + "&overrideDNSServiceId=" + value;
						}
						openIFrameDialog(740, 700, url + '&ignore=' + new Date().getTime());
						return;
					}
				}
				</s:if>
				<s:else>
					Get(formName + "_dataSource_customOptionDisplayStyle").value = Get("customOption").style.display;
					document.forms[formName].operation.value = details.operation + "Service";
					beforeSubmitAction(document.forms[formName]);
				    document.forms[formName].submit();	
				</s:else>
			}
		}
	} else {
		if(details.operation == "newOverrideDNS" ||details.operation == "editOverrideDNS"  ){
			if(details.operation != "newOverrideDNS" && details.operation != "editOverrideDNS"){
				showInfoDialog('<s:text name="warn.config.vpnnetwork.subnetwork.changed"/>');
			}else{
				<s:if test="%{jsonMode==true}">
				if (parent) {
					if(parent.isIFrameDialogOpen()) {
						document.forms[formName].parentIframeOpenFlg.value = true;
						if (operation != 'create') {
							showProcessing();
						}
						//top.changeIFrameDialog(740, 700);
					} else {
						var selectElementId = formName + "_overrideDNSServiceId";
						var overrideDNS = Get(formName + "_overrideDNSService");
						var url = '<s:url action="vpnNetworks" includeParams="none"/>'
							+ '?operation='+ details.operation + "Service"
							+ '&jsonMode=true'
							+ '&parentDomID=' + selectElementId
							+ '&overrideDNSService=' + overrideDNS.checked;
						
						if (details.operation + "Service" == 'editOverrideDNSService'){
							var value = Get(selectElementId).value;
							url = url + "&overrideDNSServiceId=" + value;
						}
						openIFrameDialog(740, 700, url + '&ignore=' + new Date().getTime());
						return;
					}
				}
				</s:if>
				<s:else>
					Get(formName + "_dataSource_customOptionDisplayStyle").value = Get("customOption").style.display;
					document.forms[formName].operation.value = details.operation + "Service";
					beforeSubmitAction(document.forms[formName]);
				    document.forms[formName].submit();	
				</s:else>
			}
		}
		if(details.m) {
			hm.util.reportFieldError(Get("checkAllIpReserveClass"), details.m);
		} else if(details.m2) {
			hm.util.reportFieldError(Get("checkAllPortForwarding"), details.m2);
		}else if(details.errorMsg){
			hm.util.reportFieldError(Get(formName + "_destinationPortNumber"),'<s:text name="config.vpn.subnet.errorBranch"></s:text>');
		}else if(details.addPostionError){
			hm.util.reportFieldError(Get(formName + "_destinationPortNumber"),'<s:text name="config.vpn.subnet.errorPosition"><s:param>'+details.errorValue+'</s:param></s:text>');
		}else if(details.name) {
			hm.util.reportFieldError(Get("subNetwork"), details.name);
		} else if(details.natExist) {
			hm.util.reportFieldError(Get("subNatIpNetwork"), details.natExist);
		}
	}
}

var succAddnetClass = function(o) {
	eval("var details = " + o.responseText);
	if (details.r) {
		if (details.v) {
			//Get("emptySubnetClassTR").style.display="none";
			//Get("subnetClassDetailTR").style.display="";
			//Get("subnetClassDetailDiv").innerHTML=details.t;

		   // Get("subnetDeviceName").value = '';
          //  initTagValues("subnetClassTag", "subnetClassValue");
		}
	} else {
		hm.util.reportFieldError(Get("checkAllSubnetClass"), details.m);
	}
};

var succAddIpReserveClass = function(o) {
	eval("var details = " + o.responseText);
	if (details.r) {
		if (details.v) {
			Get("emptyIpReserveClassTR").style.display="none";
			Get("ipReserveClassDetailTR").style.display="";
		//	Get("ipReserveClassDetailDiv").innerHTML=details.t;
		//	Get("ipReserveClassIp").value = '';

			//Get("deviceName").value = '';
	       // initTagValues("ipReserveClassTag", "ipReserveClassValue");
		}
	} else {
		hm.util.reportFieldError(Get("checkAllIpReserveClass"), details.m);
	}
};

var succRemoveSubnetClass = function(o) {
	eval("var details = " + o.responseText);
	if (details.v) {
		//Get("emptySubnetClassTR").style.display="none";
		//Get("subnetClassDetailTR").style.display="";
		//Get("subnetClassDetailDiv").innerHTML=details.t;
	} else {
		//Get("emptySubnetClassTR").style.display="";
		//Get("subnetClassDetailTR").style.display="none";
		//Get("subnetClassDetailDiv").innerHTML=details.t;
	}

	Get("checkAllSubnetClass").checked=false;
}

var succRemoveReserveIpClass = function(o) {
	eval("var details = " + o.responseText);
	if (details.v) {
		Get("emptyIpReserveClassTR").style.display="none";
		Get("ipReserveClassDetailTR").style.display="";
		//Get("ipReserveClassDetailDiv").innerHTML=details.t;
	} else {
		Get("emptyIpReserveClassTR").style.display="";
		Get("ipReserveClassDetailTR").style.display="none";
		//Get("ipReserveClassDetailDiv").innerHTML=details.t;
	}
	Get("checkAllIpReserveClass").checked=false;
}

function enableIpReserveClass(checked){
	if (checked){
		Get("ipReserveClassDiv").style.display="";
	} else {
		Get("ipReserveClassDiv").style.display="none";
	}
}
function enableSubnetClass(checked){
	if (checked){
		Get("subnetClassDiv").style.display="";

	} else {
		Get("subnetClassDiv").style.display="none";

	}
}

function enableNatClass(checked){
	if (checked){
		Get("natSettings").style.display="";
		Get("natClassDiv").style.display="";
		Get("enableNatClass").checked = true;
		hm.util.show("exportMapping");
	} else {
		Get("natClassDiv").style.display="none";
		hm.util.hide("exportMapping");
		Get("enableNatClass").checked = false;
		Get("subNatIpNetwork").value = "";
		Get("enableNatClass").disabled = isMgt0();
	}
}
function enablePortForwardingClass(checked){
	if (checked){
		Get("portForwardingClassDiv").style.display="";
	} else {
		Get("portForwardingClassDiv").style.display="none";
	}
}

function changeIpNetWork(showErrorMessage) {
	if (!validNetWorkIp()) {
		if(!showErrorMessage) {
			hm.util.reportFieldError(Get("subNetwork"), '');
		}
		$( "#slider-thumb" ).slider( "option", "disabled", true );
		$( "#slider-thumb" ).slider( "option", "value", 1);
		Get("branchSize").innerHTML=1;
		Get("subnetBranchNetMaskSpan").innerHTML="";
		Get("perBranch").innerHTML=0;
		setDHCPPoolSlider(0);
		Get("oldSubnetwork").value = "";
		return false;
	}
	if(isSameSubnets("subNetwork", "oldSubnetwork")) {
		return false;
	}
	var subUniqueSubnetworkForEachBranches = Get("uniqueSubnetwork").checked;
	if(!subUniqueSubnetworkForEachBranches) {
		$( "#slider-thumb" ).slider( "option", "max", 0);
		$( "#slider-thumb" ).slider( "option", "value", 1);
		Get("branchSize").innerHTML=1;
		var subNetIpCount=calcuNetIPCount(1);
		Get("perBranch").innerHTML=subNetIpCount;
		setDHCPPoolSlider(subNetIpCount, [0, subNetIpCount]);
	} else {
		setBranches();
	}
}

function setBranches(){
	$( "#slider-thumb" ).slider( "option", "disabled", false );
	var totlaC= countNetWrokIp(Get("subNetwork"));
	if (totlaC>=4) {
		var maxSize= parseInt(totlaC/4);
		if(maxSize>DEFAULT_BRANCHES_MAX_SIZE) {
			maxSize = DEFAULT_BRANCHES_MAX_SIZE;	
		}
		$( "#slider-thumb" ).slider( "option", "max", maxSize);
		$( "#slider-thumb" ).slider( "option", "value", 1);
		Get("branchSize").innerHTML=1;
		
		var ipNetwork=Get("subNetwork");
		var ipArr=ipNetwork.value.split("/");
		var netmaskInteger= parseInt(ipArr[1]);
		Get("subnetBranchNetMaskSpan").innerHTML="Netmask: " + hm.util.intToStringNetMask(netmaskInteger) + " (/" + netmaskInteger + ")";
		
		var subNetIpCount=calcuNetIPCount(1);
		if (isNaN(subNetIpCount) || subNetIpCount<0) {
			subNetIpCount=0;
		}
		Get("perBranch").innerHTML=subNetIpCount;
		
		setDHCPPoolSlider(subNetIpCount, [0, subNetIpCount]);
	} else {
		$( "#slider-thumb" ).slider( "option", "disabled", true );
		$( "#slider-thumb" ).slider( "option", "value", 1);
		Get("branchSize").innerHTML=1;
		Get("subnetBranchNetMaskSpan").innerHTML="";
		Get("perBranch").innerHTML=0;
		setDHCPPoolSlider(0);
	}
	Get("oldSubnetwork").value = Get("subNetwork").value;
}

function isSameSubnets(newSubnetworkId, oldSubnetworkId) {
	var newValue = Get(newSubnetworkId).value;
	var oldValue = Get(oldSubnetworkId).value;
	if(newValue.length >0 && oldValue.length > 0) {
		var nArray = newValue.split("/");
		var oArray = oldValue.split("/");
		if(nArray.length == 2 && oArray.length ==2) {
			if(nArray[1] == oArray[1]) {
				return true;
			}
		}
	}
	return false;
}

function countNetWrokIp(v) {
	if (v.value=='') return 0;
	var ipArr=v.value.split("/");
	if (ipArr.length!=2) return 0;
	var count = Math.pow(2,32-parseInt(ipArr[1]));
	return count;
}

function calcuNetIPCount(sliderV) {
	var totalC= countNetWrokIp(Get("subNetwork"));
	var clients = parseInt(totalC/sliderV);
	return clients - 3;
}

function initThumbSlider() {
	$("#slider-thumb").slider({
		range: "min",
		disabled: true,
		min: 1,
		max: 100,
		value:1,
		slide: function( event, ui ) {
			var ic=0;
			while (ui.value>=Math.pow(2,ic)){
				ic++;
			}
			ui.value =Math.pow(2,ic-1);
			Get("branchSize").innerHTML=ui.value;
			var ipNetwork=Get("subNetwork");
			var ipArr=ipNetwork.value.split("/");
			var netmaskInteger= parseInt(ipArr[1]);
			if (ic>0) {
				netmaskInteger = netmaskInteger + ic -1;
			}

			Get("subnetBranchNetMaskSpan").innerHTML="Netmask: " + hm.util.intToStringNetMask(netmaskInteger) + " (/" + netmaskInteger + ")";
			var netCount= calcuNetIPCount(ui.value);
		    if (isNaN(netCount) ||  netCount<0) {
		    	netCount=0;
		    }
		    Get("perBranch").innerHTML=netCount;

		    setDHCPPoolSlider(netCount, [0, netCount]);
		}
	});
	Get("branchSize").innerHTML=1;

	var ipNetwork=Get("subNetwork");
	var ipArr=ipNetwork.value.split("/");
	var netmaskInteger= parseInt(ipArr[1]);
	Get("subnetBranchNetMaskSpan").innerHTML="Netmask: " + hm.util.intToStringNetMask(netmaskInteger) + " (/" + netmaskInteger + ")";

	var netCount= calcuNetIPCount(1);
    if (isNaN(netCount) ||  netCount<0) {
    	netCount=0;
    }
    Get("perBranch").innerHTML=netCount;
};

function setDHCPPoolSlider(range, values) {
	//console.debug("range:"+range+" values:"+values);
	if(range == 0) {
		$("#dhcp-slider").slider("option", "values", [0, $("#dhcp-slider").slider("option", "max")]);
		$("#dhcp-slider").slider("option", "disabled", true);
	} else {
		$("#dhcp-slider").slider("option", "disabled", false);
		$("#dhcp-slider").slider("option", "max", range);
		$("#dhcp-slider").slider("option", "values", values);
	}
	var minRange = $("#dhcp-slider").slider("values", 0);
	var maxRange = $("#dhcp-slider").slider("values", 1);
	var totalRange = $("#dhcp-slider").slider("option", "max");
	Get("startReservedId").innerHTML = minRange;
	if(totalRange>=maxRange){
		Get("endReservedId").innerHTML = totalRange - maxRange;
	}else{
		Get("endReservedId").innerHTML=0;
	}
	Get("remainAddressId").innerHTML = (range == 0 ? 0 : maxRange - minRange);
	initializePosition();
}

function initDHCPPoolSlider() {
	$("#dhcp-slider").slider({
		range: true,
		min: 0,
		max: 100,
		values: [0,100],
		disabled: true,
		slide: function (event, ui) {
			//console.debug("slider..."+YAHOO.util.Lang.dump(ui));
			var totalRange = $("#dhcp-slider").slider("option", "max"),
			minRange = ui.values[0],
			maxRange = ui.values[1];
			Get("startReservedId").innerHTML = minRange;
			Get("endReservedId").innerHTML = totalRange - maxRange;
			Get("remainAddressId").innerHTML = maxRange - minRange;
			initializePosition();
		}
	});
	//$("#dhcp-slider").slider("option", "disabled", true);
}
/*------------Use classification for Subnetwork reservation---------------------*/
function changeSubnetReserveType(typeValue) {
	var hideDeviceBlock=function() {
		hm.util.hide("subnetDeviceValueBlockId");
		Get("subnetDeviceName").value = '';
	};
	var hideClassifierBlock=function() {
		hm.util.hide("subnetClassifierValueBlockId");
		initTagValues("subnetClassTag", "subnetClassValue");
	};
	if(typeValue == IPRESERVATION_TYPE_MAP) {
		// Map
		hm.util.show("subnetMapValueBlockId");
		hideDeviceBlock();
		hideClassifierBlock();
	} else if(typeValue == IPRESERVATION_TYPE_HIVEAP) {
		// device
		hm.util.hide("subnetMapValueBlockId");
		hm.util.show("subnetDeviceValueBlockId");
		hideClassifierBlock();
	} else {
		// classifier
		hm.util.hide("subnetMapValueBlockId");
		hideDeviceBlock();
		hm.util.show("subnetClassifierValueBlockId");
	}
}
function validateAddSubnetClass() {
    var cbs = document.getElementsByName('subnetClassIndices');
    if (cbs.length>255) {
        hm.util.reportFieldError(Get("checkAllSubnetClass"), 'You cannot create more than 256 items.');
        return false;
    }

    var subnetClassType = Get("subnetClassType").value;
    var url;
    var subKey=Get("subKey").value;
    // create an object for the error row message
    var errorRow = new Object();
    errorRow.id='subneterrorRow';
    if(subnetClassType == IPRESERVATION_TYPE_MAP) {
        // Map type
        var locationElement = Get("subnetLocationId");
        if(locationElement.value == -1) {
            hm.util.reportFieldError(errorRow,
                    '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.location" /></s:param></s:text>');
            locationElement.focus();
            return false;
        }
        url = "<s:url action='vpnNetworks' includeParams='none' />?operation=addSubnetClass" + "&subKey="+ subKey
	        + "&subnetClassType=" + subnetClassType
	        + "&subnetLocationId=" + locationElement.value
	        + "&ignore="+new Date().getTime();
    } else if(subnetClassType == IPRESERVATION_TYPE_HIVEAP) {
        // Device type
        var deviceNameEl = Get("subnetDeviceName");
        if(deviceNameEl.value.length == 0) {
            hm.util.reportFieldError(errorRow,
                    '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.hiveAP" /></s:param></s:text>');
            deviceNameEl.focus();
            return false;
        } else if (deviceNameEl.value.indexOf(' ') > -1) {
            hm.util.reportFieldError(errorRow,
                    '<s:text name="error.name.containsBlank"><s:param><s:text name="config.ipAddress.hiveAP" /></s:param></s:text>');
            deviceNameEl.focus();
            return false;
        }
        url = "<s:url action='vpnNetworks' includeParams='none' />?operation=addSubnetClass" + "&subKey="+ subKey
	        + "&subnetClassType=" + subnetClassType
	        + "&subnetDeviceName=" + encodeURIComponent(deviceNameEl.value)
	        + "&ignore="+new Date().getTime();
    } else if(subnetClassType == IPRESERVATION_TYPE_CLASSIFIER) {
    	if(!isCheckedTag([Get("subnetClassTag_1"), Get("subnetClassTag_2"), Get("subnetClassTag_3")], errorRow)) {
    		return false;
    	}
    	var subnetClassArray = new Array();
        // Classifier type
        if(!checkSubNetworkTag(Get("subnetClassTag_1"), "subnetClassValue", "1", errorRow, subnetClassArray)
        		||!checkSubNetworkTag(Get("subnetClassTag_2"), "subnetClassValue", "2", errorRow, subnetClassArray)
        		||!checkSubNetworkTag(Get("subnetClassTag_3"), "subnetClassValue", "3", errorRow, subnetClassArray)) {
        	return false;
        }

        url = "<s:url action='vpnNetworks' includeParams='none' />?operation=addSubnetClass" + "&subKey="+ subKey
    		+ "&subnetClassType=" + subnetClassType
            + "&subnetClassValue=" + subnetClassArray.toString()
            + "&ignore="+new Date().getTime();
    } else {
            hm.util.reportFieldError(errorRow,
                    '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.subnet.type" /></s:param></s:text>');
            Get("subnetClassType").focus();
            return false;
    }
    return url;
}
function isCheckedTag(elements, errorRow) {
	var size = elements.length;
	for(var index=size-1; index >=0; index--) {
		if(elements[index].checked) {
			return true;
		}
	}
    hm.util.reportFieldError(errorRow,
            '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.subnet.tag" /></s:param></s:text>');
	return false;
}
function checkSubNetworkTag(element, textEl_prefix,tagIndex, errorRow, array) {
	if(element && element.checked) {
	    var subnetClassTag = element.value;
	    var subnetClassValue= encodeURIComponent(Get(textEl_prefix+"_"+tagIndex).value);
	    if (subnetClassValue.trim().length==0) {
	        hm.util.reportFieldError(errorRow,
	                '<s:text name="error.requiredField"><s:param><s:text name="config.vpn.subnet.value" /></s:param></s:text>');
	        Get(textEl_prefix+"_"+tagIndex).focus();
	        return false;
	    }
	    array.push(subnetClassValue);
	} else {
	    array.push(' ');
	}
	return true;
}
/*------------Use classification for IP reservation---------------------*/
function changeIPReserveType(typeValue) {
	debug("change IP Reserved:"+typeValue);
	   var hideDeviceBlock=function() {
	        hm.util.hide("deviceValueBlockId");
	     //   Get("deviceName").value = '';
	    };
	    var hideClassifierBlock=function() {
	        hm.util.hide("classifierValueBlockId");
	       // initTagValues("ipReserveClassTag", "ipReserveClassValue");
	    };
	if(typeValue == IPRESERVATION_TYPE_MAP) {
		// Map
		debug("..map");
		hm.util.show("mapValueBlockId");
		hideDeviceBlock();
		hideClassifierBlock();
	} else if(typeValue == IPRESERVATION_TYPE_HIVEAP) {
		// device
		debug("..device");
		hm.util.hide("mapValueBlockId");
		hm.util.show("deviceValueBlockId");
		hideClassifierBlock();
	} else {
		// classifier
		debug("..classifier");
		hm.util.hide("mapValueBlockId");
		hideDeviceBlock();
		hm.util.show("classifierValueBlockId");
	}
}

function checkOnTag(prefix, key, checked) {
	Get(prefix+"_"+key).value = "";
	if(checked) {
		Get(prefix+"_"+key).disabled = "";
	} else {
		Get(prefix+"_"+key).disabled = true;
	}
}
function focusRadioTextField(prefix, key) {
    //Get(prefix+"_"+key).click();
}

function initTagValues(tagName, textName) {
    Get(tagName+"_1").checked = true;
    Get(textName+"_1").disabled = false;
    Get(textName+"_1").value = '';

    Get(tagName+"_2").checked = false;
    Get(textName+"_2").disabled = true;
    Get(textName+"_2").value = '';

    Get(tagName+"_3").checked = false;
    Get(textName+"_3").disabled = true;
    Get(textName+"_3").value = '';
}
/*----------------------Web Security--------------------------------*/
function changeSecurityType(type, failOptionValue) {
	if(isMgmtNetworkType()) return;
	if(type == 0) {
		hm.util.hide("webSecurityNoteId");
		hm.util.hide("webSecurityFailNoteRowId");
	} else {
		hm.util.show("webSecurityNoteId");
		hm.util.show("webSecurityFailNoteRowId");
		if(!failOptionValue) {
			Get("denyTrafficId1").checked = true;
		}
	}
}

/*----------------------Guest SubNetworks--------------------------------*/
function initGuestDHCPPoolSlider() {
	// initial constuct
	$("#guest-dhcp-slider").slider({
		range: true,
		min: 0,
		max: 100,
		values: [0,100],
		disabled: true,
		slide: function (event, ui) {
			var totalRange = $("#guest-dhcp-slider").slider("option", "max"),
			minRange = ui.values[0],
			maxRange = ui.values[1];
			Get("guestStartReservedId").innerHTML = minRange;
			Get("guestEndReservedId").innerHTML = totalRange - maxRange;
			Get("guestRemainAddressId").innerHTML = maxRange - minRange;

			Get(formName + "_dataSource_guestLeftReserved").value = minRange;
			Get(formName + "_dataSource_guestRightReserved").value = totalRange - maxRange;
		}
	});
	$("#guest-dhcp-slider").slider("option", "disabled", true);
	// initial values
	var totlaCount= countNetWrokIp(Get(formName + "_dataSource_ipAddressSpace"));
	if(totlaCount >= 4) {
		totlaCount = totlaCount -3;
		var starPos = Get(formName + "_dataSource_guestLeftReserved").value
		if (isNaN(starPos) || starPos < 0) {
			starPos = 0;
		}
		var endPos = totlaCount - Get(formName + "_dataSource_guestRightReserved").value;
		if (isNaN(endPos) || endPos < 0) {
			endPos = totlaCount;
		}
		setGuestDHCPPoolSlider(totlaCount, [starPos, endPos]);
	}
}
function setGuestDHCPPoolSlider(range, values) {
	if(range == 0) {
		$("#guest-dhcp-slider").slider("option", "values", [0, $("#guest-dhcp-slider").slider("option", "max")]);
		$("#guest-dhcp-slider").slider("option", "disabled", true);
	} else {
		$("#guest-dhcp-slider").slider("option", "disabled", false);
		$("#guest-dhcp-slider").slider("option", "max", range);
		$("#guest-dhcp-slider").slider("option", "values", values);
	}
	var minRange = $("#guest-dhcp-slider").slider("values", 0);
	var maxRange = $("#guest-dhcp-slider").slider("values", 1);
	var totalRange = $("#guest-dhcp-slider").slider("option", "max");
	Get("guestStartReservedId").innerHTML = minRange;
	Get("guestEndReservedId").innerHTML = totalRange - maxRange;
	Get("guestRemainAddressId").innerHTML = (range == 0 ? 0 : maxRange - minRange);

}
function changeGuestIpSpace() {
	if (!checkGuestSubNetworks()) {
		setGuestDHCPPoolSlider(0);
		return false;
	}
	$("#guest-dhcp-slider").slider( "option", "disabled", false );
	var totlaCount= countNetWrokIp(Get(formName + "_dataSource_ipAddressSpace"));
	if (totlaCount>=4) {
		totlaCount = totlaCount-3;
		setGuestDHCPPoolSlider(totlaCount, [0, totlaCount]);
	} else {
		setGuestDHCPPoolSlider(0);
	}

}

/*----------------------Subnetwork DHCP--------------------------------*/
function changeSubDhcpValue(flag) {
	enableDHCPServer=flag;
	if(flag) {
		hm.util.show("subDHCPPoolSetction");
		hm.util.show("subDHCPSettingsSection");
	} else {
		hm.util.hide("subDHCPPoolSetction");
		hm.util.hide("subDHCPSettingsSection");
	}
	initializePosition();
}
function changeSubCustomType(type) {
	hm.util.hide("subIntegerCell");
	Get(formName + "_subIntegerValue").value = "";
	hm.util.hide("subIpCell");
	Get(formName + "_subIpValue").value = "";
	hm.util.hide("subStringCell");
	Get(formName + "_subStrValue").value = "";
	hm.util.hide("subHexCell");
	Get(formName + "_subHexValue").value = "";

	switch(parseInt(type)) {
		case CUSTOM_TYPE_INT:
			hm.util.show("subIntegerCell");
			break;
		case CUSTOM_TYPE_IP:
			hm.util.show("subIpCell");
			break;
		case CUSTOM_TYPE_STR:
			hm.util.show("subStringCell");
			break;
		case CUSTOM_TYPE_HEX:
			hm.util.show("subHexCell");
			showWaterMark(Get(formName+'_subHexValue'), hexValueHint);
			break;
		default:
			break;
	}
}
// Add a Custom
function addSubnetworkCustom() {
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=addSubCustom"
		+ "&subKey="+ Get("subKey").value;

	var displayErrorObjCus = Get("subCheckAllCustom");
	var typeElement = Get(formName + "_subCustomType");
	var type = typeElement.options[typeElement.selectedIndex].value;
	url = url + "&subCustomType=" + type;
	var customNumber = Get(formName + "_subCustomNumber").value;
	if(customNumber.length == 0 || customNumber <= 1) {
		hm.util.reportFieldError(displayErrorObjCus,
				'<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.number" /></s:param></s:text>');
		return false;
	}
	if (customNumber > 254 || customNumber < 2) {
		hm.util.reportFieldError(displayErrorObjCus,
				'<s:text name="config.network.object.dhcp.server.options.custom.outOfRange"><s:param><s:text name="config.network.object.dhcp.server.options.custom.number" /></s:param></s:text>');
		return false;
	}

	for (var index in limitDHCPOptionValues) {
		if (customNumber == limitDHCPOptionValues[index]) {
			hm.util.reportFieldError(displayErrorObjCus,
					'<s:text name="error.config.network.dhcp.custom.number2"><s:param>'+customNumber+'</s:param></s:text>');
	        return false;
		}
	}
	url = url + "&subCustomNumber=" + customNumber;

	if(type == 1) {
		var integerValue = Get(formName + "_subIntegerValue");
		if(!checkNumber(displayErrorObjCus, integerValue,
				'<s:text name="config.network.object.dhcp.server.options.custom.value" />', 0, 2147483647)) {
			integerValue.focus();
			return false;
		}
		url = url + "&subCustomValue=" + integerValue.value;
	} else if(type == 2) {
		var ipValue = document.getElementById(formName + "_subIpValue");
		if(!checkIpAddress(displayErrorObjCus, ipValue, ipValue.value,
				'<s:text name="config.network.object.dhcp.server.options.custom.value" />')) {
			ipValue.focus();
			return false;
		}
		url = url + "&subCustomValue=" + ipValue.value;
	} else if (type == 3) {
		var strValue = Get(formName + "_subStrValue");
		if(strValue.value.length == 0){
			hm.util.reportFieldError(displayErrorObjCus,
					'<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
			strValue.focus();
			return false;
		}
		var message = hm.util.validateString(strValue.value, '<s:text name="config.network.object.dhcp.server.options.custom.value" />');
    	if (message != null) {
    		hm.util.reportFieldError(strValue, message);
    		strValue.focus();
        	return false;
    	}
		url = url + "&subCustomValue=" + encodeURIComponent(strValue.value);
	} else if (type == 4) {
		var hexValue = Get(formName + "_subHexValue");
		if(YAHOO.util.Dom.hasClass(hexValue, hintClassName)) {
			hm.util.reportFieldError(displayErrorObjCus,
			'<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
			hexValue.focus();
			return false;
		} else  {
			if(hexValue.value.length == 0){
				hm.util.reportFieldError(displayErrorObjCus,
						'<s:text name="error.requiredField"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
				hexValue.focus();
				return false;
			}
			if(hexValue.value.length > 254){
				hm.util.reportFieldError(displayErrorObjCus,
					'<s:text name="config.network.object.dhcp.server.options.custom.outOfRange"><s:param><s:text name="config.network.object.dhcp.server.options.custom.value" /></s:param></s:text>');
				hexValue.focus();
				return false;
			}
		}
		
		var message = hm.util.validateHexRange(hexValue.value,
				'<s:text name="config.network.object.dhcp.server.options.custom.value" />');
	  	if (message != null) {
	        hm.util.reportFieldError(displayErrorObjCus, message);
	        hexValue.focus();
	        return false;
	  	}
		url = url + "&subCustomValue=" + hexValue.value;
	}

	url = url + "&ignore="+new Date().getTime();

	var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
			{success : addSubCostumAck, failure : resultDoNothing, timeout: 60000}, null);
}


function getStartIpAddressValue(startIpAddress, netmask){
	var intAddress = hm.util.getIpAddressValue(startIpAddress);
	var maskValue = hm.util.getIpAddressValue(netmask);
	var s = intAddress & maskValue;
	return s < 0 ? Math.pow(2,32)+s : s;
}

function getEndIpAddressValue(startIpAddress, netmask){
	var intAddress = hm.util.getIpAddressValue(startIpAddress);
	var maskValue = hm.util.getIpAddressValue(netmask);
	var s = intAddress & maskValue;
	var ipCount = Math.pow(2,32) - maskValue;
	
	return s < 0 ? Math.pow(2,32)+s + ipCount -1 : s + ipCount - 1;
}

function addportForwarding() {
	var destinationPortNumber=Get(formName + "_destinationPortNumber");
	var positionId=Get(formName + "_positionId");
	var internalHostPortNumber=Get(formName + "_internalHostPortNumber");
	var protocol=Get(formName + "_protocol");
	var message = hm.util.validateIntegerRange(destinationPortNumber.value, '<s:text name="config.vpn.subnet.DestinationPortNumber" />',1,65535);
	if (message != null) {
	    hm.util.reportFieldError(destinationPortNumber, message);
	    destinationPortNumber.focus();
	    return false;
	}
    message = hm.util.validateIntegerRange(internalHostPortNumber.value, '<s:text name="config.vpn.subnet.InternalPortNumber" />',1,65535);
	if (message != null) {
	    hm.util.reportFieldError(destinationPortNumber, message);
	    internalHostPortNumber.focus();
	    return false;
	}
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=addportForwarding" 
		+ "&subKey="+ Get("subKey").value;
	url = url +"&subDestinationPortNumber="+destinationPortNumber.value+"&positionId="+positionId.value
	+"&subInternalHostPortNumber="+internalHostPortNumber.value+"&protocol="+protocol.value
	+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, 
			{success : addPortForwardingSuc, failure : resultDoNothing, timeout: 60000}, null);
}
var addPortForwardingSuc=function(o){
	eval("var details = " + o.responseText);
	if(details.succ) {
		setPortForwarding(details.tableHTML);
		Get(formName + "_destinationPortNumber").value="";
		Get(formName + "_internalHostPortNumber").value="";
	} else {
		if("addDestinationPortNumberError"==details.errorMsg){
			hm.util.reportFieldError(Get(formName + "_destinationPortNumber"),'<s:text name="config.vpn.subnet.addDestinationPortNumberError"><s:param>'
					+Get(formName + "_destinationPortNumber").value+'</s:param></s:text>');
		}else if("destinationPortNumberNotSameError"==details.errorMsg){
			hm.util.reportFieldError(Get(formName + "_destinationPortNumber"),'<s:text name="config.vpn.subnet.destinationPortNumberNotSameError"><s:param>'
					+Get(formName + "_destinationPortNumber").value+'</s:param></s:text>');
		}else if("addIpAddressOurRangeError"==details.errorMsg){
			hm.util.reportFieldError(Get(formName + "_destinationPortNumber"),'<s:text name="config.vpn.subnet.errorBranch"></s:text>');
		}else if("addProtocolError"==details.errorMsg){
			var protocolValue=Get(formName + "_protocol").value;
			var paramValue="";
			if(protocolValue=="1"){
				paramValue="Any";
			}else if(protocolValue=="2"){
				paramValue="TCP";
			}else if(protocolValue=="3"){
				paramValue="UDP";
			}
			hm.util.reportFieldError(Get(formName + "_destinationPortNumber"),'<s:text name="config.vpn.subnet.addProtocolError"><s:param>'
					+paramValue+'</s:param></s:text>');
		}
	}
}

function removePortForwarding() {
	var cbs = document.getElementsByName('PortForwardingIndices');
	if (cbs.length == 0) {
		hm.util.reportFieldError(Get("checkAllPortForwarding"), '<s:text name="info.emptyList"></s:text>');
		return false;
	}
	if (!hm.util.hasCheckedBoxes(cbs)) {
        hm.util.reportFieldError(Get("checkAllPortForwarding"), '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
		return false;
	}
	var selectArray="";
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked) {
			if (selectArray.length==0) {
				selectArray= selectArray + cbs[i].value;
			} else {
				selectArray= selectArray + "," +  cbs[i].value;
			}
		}
	}
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=removePortForwarding"
			+ "&PortForwardingIndices="+ selectArray  + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, 
			{success : succRemovePortForwarding, failure : resultDoNothing, timeout: 60000}, null);
}
var succRemovePortForwarding = function (o) {
	eval("var details = " + o.responseText);
	if(details.showTable) {
		setPortForwarding(details.tableHTML);
	} else {
		setPortForwarding();
	}
	Get("checkAllPortForwarding").checked=false;
}
function setPortForwarding(text){
	Get("showPortForwardingDiv").innerHTML ="";
	Get("showScroPortForwardingDiv").innerHTML ="";
	if(text) {
		Get("showPortForwardingDiv").innerHTML = text;
		var cbs = document.getElementsByName('PortForwardingIndices');
		if(cbs.length>4){
			Get("showScroPortForwardingDiv").innerHTML = text;
			Get("showPortForwardingDiv").innerHTML = "";
			hm.util.show("showScroPortForwardingDiv");
			hm.util.hide("showPortForwardingDiv");
		}else{
			Get("showScroPortForwardingDiv").innerHTML ="";
			hm.util.show("showPortForwardingDiv");
			hm.util.hide("showScroPortForwardingDiv");
		}
		hm.util.hide("emptyPortForwarding");
	} else {
		hm.util.hide("showPortForwardingDiv");
		hm.util.hide("showScroPortForwardingDiv");
		hm.util.show("emptyPortForwarding");
	}
}
function initializePosition(){
  var poolSize=parseInt(Get("remainAddressId").innerHTML);
  var minRange=parseInt(Get("startReservedId").innerHTML);
  if(enableDHCPServer){
	  positionRange=minRange;
  }else{
	  positionRange=poolSize+minRange;
  }
  if(positionRange>=50){
	  positionRange=50;
  }
  if(positionRange==0){
	   Get("enablePortForwardingClass").checked=false;
	   enablePortForwardingClass(false);
	   document.getElementById("enablePortForwardingClass").disabled=true;
  }else{
	   document.getElementById("enablePortForwardingClass").disabled=false;
  }
	var obj = Get(formName + "_positionId");
	obj.options.length = 0;
	for(var i=1;i<=positionRange;i++){
		obj.add(new Option(i,i));
	}
	document.getElementById("positionRange").value=positionRange;
} 
function newPortForwarding(){
	Get("portForwardingNewButtonTR").style.display="none";
	Get("portForwardingApplyButtonTR").style.display="";
	Get("portForwardingApplyDetailTR").style.display="";
}
function portForwardingCancelButtonTR(){
		Get("portForwardingNewButtonTR").style.display="";
		Get("portForwardingApplyButtonTR").style.display="none";
		Get("portForwardingApplyDetailTR").style.display="none";
}
var allBranchIpsPanel=null;
function showAllBranchIps(){
	if(allBranchIpsPanel==null){
		createAllBranchIpsPanel(450,280);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("allBranchIpsFrame").style.display = "";
	}
	allBranchIpsPanel.show();
	var branchSize=parseInt(Get("branchSize").innerHTML);
	var localNetWork=Get("subNetwork").value;
	var positionRange=document.getElementById("positionRange").value;
	var firstIpIsGateWay = Get("subDefaultGatewayFirstIP").checked;
	var uniqueSubnetwork = Get("uniqueSubnetwork").checked;
	var iframe = document.getElementById("allBranchIpsFrame");
	iframe.src ="<s:url value='vpnNetworks.action' includeParams='none' />?operation=ipMappingIframeDiag&branchSize="
			+branchSize+"&localNetWork="+localNetWork+"&positionRange="+positionRange+"&firstIpIsGateWay="+firstIpIsGateWay
			+"&uniqueSubnetwork="+uniqueSubnetwork;
}
function createAllBranchIpsPanel(width, height){
	var div=document.getElementById("allBranchIpsDiv");
	var iframe = document.getElementById("allBranchIpsFrame");
	iframe.width = width;
	iframe.height = height;
	allBranchIpsPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false,modal:true, constraintoviewport:true } );
	allBranchIpsPanel.render(document.body);
	allBranchIpsPanel.setHeader('<s:text name="config.vpn.subnet.mappingButton"/>');
	div.style.display="";
}
function exportPortForwarding(){
	var uniqueSubnetwork = Get("uniqueSubnetwork");
	var mask = 0;
	if(uniqueSubnetwork.checked){
		if(Get("enableNatClass").checked && !validNatNetWorkIp(true)) {
			return false;
		}
		mask = Get("subNatIpNetwork").value.split("/")[1];
	}else{
		if(!validNatNetWorkIp(true)){
			return false;
		}
		mask = Get("natNetMask").value;
	}
	if(parseInt(mask) < 10) {
		hm.util.reportFieldError(Get("enableNatClass"), '<s:text name="error.vpn.subnet.natnetwork.exportDataTooLarge"></s:text>');
		return false;
	}
	var localNetWork=Get("subNetwork").value;
	var natNetWork=Get("subNatIpNetwork").value;
	var branchSize=parseInt(Get("branchSize").innerHTML);
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=exportPortForwarding"
		+ "&subKey="+ Get("subKey").value;
	url = url+"&localNetWork="+localNetWork+"&natNetWork="+natNetWork
	+"&uniqueSubnetwork="+ Get("uniqueSubnetwork").checked+"&enableNat="
	+Get("enableNatClass").checked+"&branchSize="+branchSize+"&ignore="+new Date().getTime();
	location.href=url;
}

var addSubCostumAck = function (o) {
	eval("var details = " + o.responseText);
	if(details.succ) {
		setSubnetworkCustom(details.tableHTML);
	} else {
		hm.util.reportFieldError(Get("subCheckAllCustom"), details.msg);
	}
}
// Remove Customs
function removeSubnetworkCustom() {
	var cbs = document.getElementsByName('subCustomIndices');
	if (cbs.length == 0) {
		hm.util.reportFieldError(Get("subCheckAllCustom"), '<s:text name="info.emptyList"></s:text>');
		return false;
	}
	if (!hm.util.hasCheckedBoxes(cbs)) {
        hm.util.reportFieldError(Get("subCheckAllCustom"), '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
		return false;
	}
	var selectArray="";
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked) {
			if (selectArray.length==0) {
				selectArray= selectArray + cbs[i].value;
			} else {
				selectArray= selectArray + "," +  cbs[i].value;
			}
		}
	}
	debug("selected array:"+selectArray);
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=removeSubCustom"
			+ "&subCustomIndices="+ selectArray  + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
			{success : succRemoveSubnetCustomAck, failure : resultDoNothing, timeout: 60000}, null);
}
var succRemoveSubnetCustomAck = function (o) {
	eval("var details = " + o.responseText);
	if(details.showTable) {
		setSubnetworkCustom(details.tableHTML);
	} else {
		setSubnetworkCustom();
		displaySubCustomCreateSection(true);
	}
	Get("subCheckAllCustom").checked=false;
}

function setSubnetworkCustom(text) {
	if(text) {
		hm.util.show("subnetCustomCell");
		Get("subnetCustomCell").innerHTML = text;
		hm.util.hide("emptySubnetCustomTR");
	} else {
		hm.util.hide("subnetCustomCell");
		hm.util.show("emptySubnetCustomTR");
	}
}

function displaySubCustomCreateSection(flag) {
	if(flag) {
		hm.util.hide("subCustomNewButton");
		hm.util.show("subCustomCreateButton");
		hm.util.show("subCustomCreateSection");
	} else {
		hm.util.hide("subCustomCreateButton");
		hm.util.hide("subCustomCreateSection");
		hm.util.show("subCustomNewButton");
	}
}
// Enable slider once IP network is provided (after enter or click outside textbox)
function keypressSubnetwork(e) {
	var keycode;
	if(window.event) {
		keycode = e.keyCode;
	} else if(e.which) {
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}
	if (keycode == 13) {
		var silder = Get("slider-thumb");
		if(silder) {
			var anchor = silder.getElementsByTagName("a");
			if(anchor.length != 0) {
				anchor[0].focus();
			}
		}
		return false;
	}
	return hm.util.keyPressPermit(e,'ipMask');
}

function debug(msg) {/*console.debug(msg);*/}

<s:if test="%{jsonMode == true && contentShownInDlg == false}">
	function judgeFoldingIcon() {
		adjustFoldingIcon('customOption');
	}

	window.setTimeout("onLoadEventForJsonMode()", 100);
	YAHOO.util.Event.onContentReady("customOption", judgeFoldingIcon, this);
</s:if>
<s:if test="%{jsonMode == true && contentShownInSubDrawer}">
   setCurrentHelpLinkUrl('<s:property value="helpLink" />');
</s:if>
</script>

<style>
.hint {
color: gray;
}
</style>
<script>
var hintClassName="hint";
var hexValueHint = '<s:text name="guadalupe_2.config.network.object.dhcp.server.options.custom.hex.hint"/>';

function loadWaterMark(){
	// initial water mark
	showWaterMark(Get(formName+'_hexValue'), hexValueHint);
	showWaterMark(Get(formName+'_subHexValue'), hexValueHint);
	// water mark event
	YAHOO.util.Event.on(Get(formName+'_subHexValue'), "focus", focusAction);
	YAHOO.util.Event.on(Get(formName+'_subHexValue'), "blur", blurAction);
	YAHOO.util.Event.on(Get(formName+'_subHexValue'), "keyup", keyupAction);
	YAHOO.util.Event.on(Get(formName+'_hexValue'), "focus", focusAction);
	YAHOO.util.Event.on(Get(formName+'_hexValue'), "blur", blurAction);
	YAHOO.util.Event.on(Get(formName+'_hexValue'), "keyup", keyupAction);
}
///--------------- Hex value: WaterMark -----------------///
function focusAction(e) {
	hideWaterMark(this.id);
}

function blurAction(e) {
	showWaterMark(this.id, hexValueHint);
}

function keyupAction(e){
	checkMaxLength(this.id);
}

function hideWaterMark(elementId) {
	var el = Get(elementId);
	if(el) {
		if(YAHOO.util.Dom.hasClass(el, hintClassName)) {
			YAHOO.util.Dom.removeClass(el, hintClassName);
			el.value = "";
		}
	}
}
function showWaterMark(elementId, text) {
	var el = Get(elementId);
	if(el) {
		var value = el.value;
		if (value.length == 0 || value.trim().length == 0 || text == value) {
			YAHOO.util.Dom.addClass(el, hintClassName);
			el.value = text;
		} else {
			checkMaxLength(elementId);
		}
	}
}
function checkMaxLength(elementId){
	var el = $("#"+elementId);
	var maxLength = parseInt(el.attr('maxlength'));
	if(maxLength > 0 && el.val().length > maxLength){
		el.val(el.val().substr(0,maxLength));
	}
}
</script>