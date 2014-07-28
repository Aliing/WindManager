<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.BonjourServiceCategory"%>
<%@page import="com.ah.bo.network.IpPolicyRule"%>


<script src="<s:url value="/js/hm.options.js" />"></script>
<script src="<s:url value="/yui/treeview/treeview-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/hm.widget.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/treeview/assets/treeview.css" includeParams="none"/>" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/treeview/assets/skins/sam/treeview.css" includeParams="none"/>"/>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/hm.widget.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" href="<s:url value="/css/hm_v2.css" includeParams="none"/>" type="text/css" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/ahdatatable/ahdatatable.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/ahdatatable/jquery.ui.autocomplete.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/ahdatatable/jquery.ui.theme.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script
	src="<s:url value="/yui/yahoo/yahoo-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/event/event-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/element/element-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
 <script
	src="<s:url value="/yui/json/json-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/get/get-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/element/element-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script 
	src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery-ui.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/dataTable/ahDataTable.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/innerhtml.js" />"></script>
<script src="<s:url value="/js/underscore-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/drag/ahdrag.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<style type="text/css">
.textNode{
margin-left: 4px;
}
.ellipsis {
	width: 180px; 
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	-o-text-overflow: ellipsis; /*For Opera*/
	-ms-text-overflow: ellipsis; /*For IE8*/
	-moz-binding: url(assets/xml/ellipsis.xml#ellipsis); /*For Firefox3.x*/
}

</style>

<script>
var formName = 'bonjourGatewaySettings';

var SERVICE_CATEGORY_ALL = '<%=BonjourServiceCategory.SERVICE_CATEGORY_ALL%>';
var SERVICE_CATEGORY_CUSTIOM = '<%=BonjourServiceCategory.SERVICE_CATEGORY_CUSTIOM%>';
var tree;
var tableDragAndDropHelper;
function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_bonjourGwName").disabled) {
		document.getElementById(formName + "_dataSource_bonjourGwName").focus();
	}
	<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(1150, 600);
		}
	</s:if>
	tree = new YAHOO.widget.TreeView("serviceTree");
	var expandedNodes = <s:property escape="false" value="expandedNodes"/>;
	//if (expandedNodes != '') {
	//	expandedNodes = '[' + expandedNodes + ']'
	//}
	initTree(tree,expandedNodes);
	
	var dataSource = eval('<s:property escape="false" value="ahDtDatas"/>');
	var ahDtClumnDefs = eval('<s:property escape="false" value="ahDtClumnDefs"/>');
	var editInfo = '<s:property  escape="false" value="editInfo"/>';
	onLoadAhDataTable(ahDtClumnDefs,dataSource,editInfo);
}

//**** action for tree start ****
function initTree(tree,expandedNodes) {
	//var nodeList = eval(expandedNodes.replace(/\&quot;/g,'"').replace(/\&amp;/g,'&'));
	buildTree(tree.getRoot(), expandedNodes);
	
	tree.subscribe('clickEvent',tree.onEventToggleHighlight);
	tree.setNodesProperty('propagateHighlightUp',true);   
	tree.setNodesProperty('propagateHighlightDown',true);   

	tree.draw();
	
	if(document.getElementById(formName+'_operation').value =='new'){
		//All services will be selected for new
		selectAllServices();
	} else {
		setServicesCheckBox(document.getElementById('selectedServiceIDs').value);
		if(allServicesChecked()){
			selectAllServices();
		}
	}
}	

function allServicesChecked(){
	for(var i=1; i<=tree.getNodeCount(); i++) { 
	    var node = tree.getNodeByIndex(i); 
	    if(!node.data.isCategory){
	    	 if (node.highlightState == 0) { 
	    		 return false;
	    	 }
	    }
	}  
	return true;
}
	
function selectAllServices(){
	for(var i=1; i<=tree.getNodeCount(); i++) { 
	    tree.getNodeByIndex(i).highlight(false); 
	}  
}

function buildTree(parentNode, nodeList){
	var oData = null;
	for(i=0; i<nodeList.length; i++){
		oData = nodeList[i];
		if(oData.label == undefined || oData.label == '' ){ continue; }
		if(oData.isRoot){
			eval("var node" + oData.nodeId + "= new YAHOO.widget.TextNode(oData, parentNode, true);");
			var tmpNode = eval("node" + oData.nodeId);
			tmpNode.labelStyle ="textNode";
		} else {
			if(oData.isCategory){
				eval("var node" + oData.nodeId + "= new YAHOO.widget.TextNode(oData, eval(node" + oData.parentId  + "), true);");
				var tmpNode = eval("node" + oData.nodeId);
				tmpNode.labelStyle ="textNode";
			} else {
				if(oData.isCustom){
					setNodeHtml_custom(oData);
				} else {
					setNodeHtml(oData);
				}
				eval("var node" + oData.nodeId + "= new YAHOO.widget.HTMLNode(oData, eval(node" + oData.parentId  + "), true);");
			}
		}
	}
}

function setNodeHtml(oData){
	oData.html="<div class ='textNode' style='width:450px;'>"+"<span class='ellipsis' style='width:250px;float: right;' title='"+ oData.serviceType +"'>" +oData.serviceType+"</span>"+"<span class='ellipsis' style='float: left;width:200px;' title='"+ oData.label +"'>"+oData.label+"</span></div>"
}

function setNodeHtml_custom(oData){
	oData.html="<div class ='textNode' style='width:450px;'>"+
				"<div style='width:250px;float: right;'><span class='ellipsis' style='width:200px;float: left;' title='"+ oData.serviceType +"'>" +oData.serviceType+
				"</span><span style='width:50px;float: right;' title='delete'>"+
				"<a href='javascript:void(0);' onclick=doDelCustomService('"+oData.serviceId+"')>"+
				"<img src='<s:url value='/images/cancel.png'/>' class='dblk' with='16' height='16' />"+
				"</a></span></div>"+
				"<div class='ellipsis' style='float: left;width:200px;' title='"+ oData.label +"'>"+oData.label+"</div></div>"
}

function doDelCustomService(serviceId){
	confirmDialog.cfg.setProperty('text', '<s:text name="config.BonjourGatewaySetting.service.delete.tip" />');
	confirmDialog.show();
	document.getElementById('delServiceId').value=serviceId;
}

function handleNo() {
	//keep service status
	checkService(document.getElementById('delServiceId').value);
    this.hide();
}

function doContinueOper()
{
	/* var url = "<s:url action='bonjourGatewaySettings' includeParams='none' />?operation=delCustomService"+
	"&customServiceId="+ document.getElementById('delServiceId').value +
	"&ignore="+new Date().getTime(); 
	var transaction = YAHOO.util.Connect.asyncRequest('get', url, {success : succDelCustomService, failure : resultDoNothing, timeout: 60000}, null); */

	var reqArgs = {
			'operation': 'delCustomService',
			'customServiceId': document.getElementById('delServiceId').value
	};
	
	$.extend(true, reqArgs, getDataTableReqArgs());
	  
	$.post('bonjourGatewaySettings.action',
			$.param(reqArgs, true),
			function(data, textStatus) {
				succDelCustomService(data);
			},
			'json');


}
var succDelCustomService = function(details) {
	/* eval("var details = " + o.responseText); */
	if(!details.resultStatus) {
		showWarnDialog(details.errMsg);
    } else {
    	var node = tree.getNodeByProperty('serviceId', details.customServiceId);
    	tree.removeNode(node);
    	tree.subscribe('clickEvent',tree.onEventToggleHighlight);     
    	tree.setNodesProperty('propagateHighlightUp',true);   
    	tree.setNodesProperty('propagateHighlightDown',true);  
    	tree.draw();
    	
    	reflashDataTable(details);
	}
}

function getDataTableReqArgs(){
	var metrics = [];
	var fromVlanGroups = [];
	var toVlanGroups = [];
	var ruleIds = [];
	var serviceNames = [];
	var serviceTypes = [];
	var realms = [];
	var filterActions = [];
	
	$("input[name=metrics]").each(function(){
		metrics.push($(this).val());
	});
	$("input[name=fromVlanGroups]").each(function(){
		fromVlanGroups.push($(this).val());
	});
	$("input[name=toVlanGroups]").each(function(){
		toVlanGroups.push($(this).val());
	});
	$("input[name=ruleIds]").each(function(){
		ruleIds.push($(this).val());
	});
	$("input[name=serviceNames]").each(function(){
		serviceNames.push($(this).val());
	});
	$("input[name=serviceTypes]").each(function(){
		serviceTypes.push($(this).val());
	});
	$("input[name=realms]").each(function(){
		realms.push($(this).val());
	});
	$("input[name=filterActions]").each(function(){
		filterActions.push($(this).val());
	});
	
	var reqArgs = {
			'metrics': metrics,
			'fromVlanGroups': fromVlanGroups,
			'toVlanGroups': toVlanGroups,
			'ruleIds': ruleIds,
			'serviceNames': serviceNames,
			'serviceTypes': serviceTypes,
			'realms': realms,
			'filterActions': filterActions,
			"editInfo":ahDataTable.getRowData(),
			'ignore': new Date().getTime()
	};
	
	return reqArgs;
}

function reflashDataTable(details){
	$("#ahDataTable").empty();
	var dataSource = eval(details.ahDtDatas);
	var ahDtClumnDefs = eval(details.ahDtClumnDefs);
	var editInfo = details.editInfo;
	onLoadAhDataTable(ahDtClumnDefs,dataSource,editInfo);
}

function getSelectedTreeData(){
	var serviceIDs = new Array(); 
	var idx = 0; 
	var hiLit = tree.getNodesByProperty('highlightState',1); 
	if(hiLit != null){
		for(var i=0; i<hiLit.length; i++){
			var node =hiLit[i]; 
			if(!node.data.isCategory){
				 serviceIDs[idx] = node.data.serviceId; 
		    	 idx ++; 
			}
		}
	}
	return serviceIDs;
}

function checkService(id){
	var node = tree.getNodeByProperty('serviceId', id); 
    if (node != null && !node.hasChildren(true)) { 
    	if(node.highlightState == 0){
    		 node.highlight(false); 
    	} else {
    		 node.unhighlight(true); 
    	}
       
    } 
}

function setServicesCheckBox(ids){
	if(ids == null || ids == ''){
		return;
	}
	var serviceIds =eval('[' + ids + ']');
	// uncheck all checkbox
	for(var i=1; i<=tree.getNodeCount(); i++) { 
	    tree.getNodeByIndex(i).unhighlight(true); 
	}  
	// just need to set the children's checkbox, the parent's state will automatically change accordingly
	for(var j=0; j<serviceIds.length; j++) { 
	    var node = tree.getNodeByProperty('serviceId', serviceIds[j]); 
	    if (node != null && !node.hasChildren(true)) { 
	        node.highlight(false); 
	    } 
	}  
}

function validateCustomService(operation){
	if(operation == 'addCustomService'){
		var serviceName = document.getElementById(formName+"_service");
		var message = hm.util.validateName(serviceName.value, '<s:text name="config.BonjourGatewaySetting.service.name" />');
    	if (message != null) {
    		hm.util.reportFieldError(serviceName, message);
    		serviceName.focus();
        	return false;
    	}
		var serviceType = document.getElementById(formName+"_type");
		var message = hm.util.validateName(serviceType.value, '<s:text name="config.BonjourGatewaySetting.service.type" />');
    	if (message != null) {
    		hm.util.reportFieldError(serviceType, message);
    		serviceType.focus();
        	return false;
    	}
		/* if (serviceName.value.length == 0) {
			hm.util.reportFieldError(serviceName, '<s:text name="error.requiredField"><s:param><s:text name="config.BonjourGatewaySetting.service.name" /></s:param></s:text>');
			serviceName.focus();
			return false;
		}
		
		if (serviceType.value.length == 0) {
			hm.util.reportFieldError(serviceType, '<s:text name="error.requiredField"><s:param><s:text name="config.BonjourGatewaySetting.service.type" /></s:param></s:text>');
			serviceType.focus();
			return false;
		} else */ 
		if(serviceType.value.length > 64){
			hm.util.reportFieldError(serviceType, '<s:text name="error.keyLengthRange"><s:param><s:text name="config.BonjourGatewaySetting.service.type" /></s:param><s:param><s:text name="config.BonjourGatewaySetting.service.type.range" /></s:param></s:text>');
			serviceType.focus();
			return false;
		}
	}
	return true;
}

function doAddCustomService() {
	if(validateCustomService('addCustomService')){
		/* var url = "<s:url action='bonjourGatewaySettings' includeParams='none' />?operation=addCustomService"+
		"&customServiceName="+ encodeURIComponent(document.getElementById(formName+"_service").value)+
		"&customServiceType="+ encodeURIComponent(document.getElementById(formName+"_type").value)+
		"&ignore="+new Date().getTime(); 
		console.log(url);
		var transaction = YAHOO.util.Connect.asyncRequest('get', url, {success : succAddCustomService, failure : resultDoNothing, timeout: 60000}, null); */
		
		var reqArgs = {
				'operation': 'addCustomService',
				'customServiceName': encodeURIComponent(document.getElementById(formName+"_service").value),
				'customServiceType': encodeURIComponent(document.getElementById(formName+"_type").value)
		};
		
		$.extend(true, reqArgs, getDataTableReqArgs());
		
		$.post('bonjourGatewaySettings.action',
				$.param(reqArgs, true),
				function(data, textStatus) {
					succAddCustomService(data);
				},
				'json');
	}
}

var succAddCustomService = function(details) {
	/* eval("var details = " + o.responseText); */
	if(!details.resultStatus) {
		var input;
		if(details.inputName){
			input = document.getElementById(formName + "_"+details.inputName);
		} else {
			input = document.getElementById(formName + "_service");
		}
		hm.util.reportFieldError(input, details.errMsg);
		input.focus();
    } else {
    	addNode(details.serviceId,details.label,details.serviceType);
    	reflashDataTable(details);
	}
}

var resultDoNothing = function(o){
}

function addNode(serviceId,label,serviceType){
	if(label==null || label == ''){
		return;	
	}
	var pnode = tree.getNodeByProperty('label', SERVICE_CATEGORY_CUSTIOM);
	var oData = {"serviceId":serviceId,"label":label,"serviceType":serviceType,"isCategory":false,"isCustom":true,"isRoot":false};
	setNodeHtml_custom(oData);
	//oData.html="<div>"+oData.serviceType+"<span style='float: left;width:150px'>"+oData.label+"</span></div>"
	var count = tree.getNodeCount();
	eval("var node" + count+1 +"= new YAHOO.widget.HTMLNode(oData, pnode, true);");

	tree.subscribe('clickEvent',tree.onEventToggleHighlight);     
	tree.setNodesProperty('propagateHighlightUp',true);   
	tree.setNodesProperty('propagateHighlightDown',true);  
	tree.draw();
	var tmpNode = eval("node" + count+1);
	tmpNode.focus(); // focus
	tmpNode.highlight(false);  //checked
}
// **** action for tree end ****

function submitAction(operation) {
	if (validate(operation)) {
		var selectedServiceIDs = getSelectedTreeData().toString();
		document.getElementById('selectedServiceIDs').value=selectedServiceIDs;
		
		if (operation=="newFromVlanGroup" || operation=="newToVlanGroup" || operation=="editToVlanGroup" || operation=="editFromVlanGroup") {
			<s:if test="%{jsonMode==true}">
				if (parent!=null && !parent.isIFrameDialogOpen()) {
					//do nothing now
				} else {
					if ('<%=Navigation.L2_FEATURE_BONJOUR_GATEWAY_SETTINGS%>' != operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
						showProcessing();
					}
					document.forms[formName].operation.value = operation;
					document.forms[formName].parentIframeOpenFlg.value = true;
				   	document.forms[formName].submit();
				}
			</s:if>
			<s:else>
				if ('<%=Navigation.L2_FEATURE_BONJOUR_GATEWAY_SETTINGS%>' != operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
					showProcessing();
				}
				document.forms[formName].operation.value = operation;
			   	document.forms[formName].submit();
			</s:else>
		} else {
			<s:if test="%{jsonMode}">
			    if ('cancel' + '<s:property value="lstForward"/>' == operation) {
					parent.closeIFrameDialog();	
				} else if (operation == 'create<s:property value="lstForward"/>' 
					|| operation == 'update<s:property value="lstForward"/>'
					|| operation == 'create' 
					|| operation == 'update'){
					//add for json mode 
		            url = "<s:url action='bonjourGatewaySettings' includeParams='none' />" + "?jsonMode=true"
		            		+ "&ignore=" + new Date().getTime();
		            document.forms[formName].operation.value = operation;
		            YAHOO.util.Connect.setForm(document.forms["bonjourGatewaySettings"]);
		            var transaction = YAHOO.util.Connect.asyncRequest('post', url, 
		            		{success : finishJsonBonjour, failure : failSaveJsonBonjour, timeout: 60000}, null);
					
				} else {
					if ('<%=Navigation.L2_FEATURE_BONJOUR_GATEWAY_SETTINGS%>' != operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
						showProcessing();
					}
					document.forms[formName].operation.value = operation;
				    document.forms[formName].submit();
				}
			</s:if>
			<s:else>
				if ('<%=Navigation.L2_FEATURE_BONJOUR_GATEWAY_SETTINGS%>' != operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
					showProcessing();
				}
				document.forms[formName].operation.value = operation;
			    document.forms[formName].submit();
			</s:else>
		}
	}
}

function finishJsonBonjour (o) {
	try {
		eval("var details = " + o.responseText);
		if(details.resultStatus) {
			parent.closeIFrameDialog();
			if(details.addedId) {
				// new
			    var url = "<s:url action='networkPolicy' includeParams='none' />?operation=finishSelectBonjourGw&selectBonjourGwId="
		            + details.addedId +"&ignore="+new Date().getTime();
			    YAHOO.util.Connect.asyncRequest('get', url, 
			            {success : top.finishSelectedBonjourGw, failure : top.resultDoNothing, timeout: 60000}, null);  
			}
		} else {
		    var errorRow = new Object();
		    errorRow.id='ErrorRow';
			hm.util.reportFieldError(errorRow, details.errMsg);
		}
	} catch(e) {
		// Do nothing
	}
}
function failSaveJsonBonjour (o) {}

function validate(operation) {
    
    if ((operation == 'create<s:property value="lstForward"/>' || operation == 'create') && !validateName()) {
		return false;
	}
	if ('<%=Navigation.L2_FEATURE_BONJOUR_GATEWAY_SETTINGS%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>') {
		return true;
	}

	if(operation == 'update<s:property value="lstForward"/>' 
	|| operation == 'create<s:property value="lstForward"/>'
	|| operation == 'update' 
	|| operation == 'create'){
		
		if(!validateVlans()){
			return false;
		}
	}
	
	return true;
}

function validateName() {
    var inputElement = document.getElementById(formName + "_dataSource_bonjourGwName");
    var message = hm.util.validateName(inputElement.value, '<s:text name="config.pppoe.name" />');
   	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}
    return true;
}

function validateVlans(){
	var vlan = document.getElementById('bonjourGatewaySettings_dataSource_vlans');
	if(vlan.value.length == 0){
		hm.util.reportFieldError(vlan, '<s:text name="error.requiredField"><s:param><s:text name="config.BonjourGatewaySetting.vlans" /></s:param></s:text>');
		vlan.focus();
        return false;
	}
	var vlanList =vlan.value.split(',');
	var vlanValues = new Array();
	var vlanRangerValues = new Array();
	for(var j=0;j<vlanList.length;j++){
		var pattern = /^(\d+-)?\d+$/;
		if(!pattern.test(vlanList[j])){
			hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.BonjourGatewaySetting.vlans" /></s:param></s:text>');
			vlan.focus();
	        return false;
		}
		var vlans = vlanList[j].split('-');
		var numVlan0=Number(vlans[0]);
		var numVlan1=Number(vlans[1]);
	    if(vlans.length>1 && numVlan0 > numVlan1){
	    	hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.BonjourGatewaySetting.vlans" /></s:param></s:text>');
			vlan.focus();
	        return false;
		}
	    var pattern_value = /^[1-9]\d{0,2}$|^[1-3]\d{3}$|^40[0-8][0-9]$|^409[0-4]$/; //1-4094
		for(var i=0;i<vlans.length;i++) {
			if(!pattern_value.test(vlans[i])){
				hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="config.BonjourGatewaySetting.vlans" /></s:param></s:text>');
				vlan.focus();
		        return false;
			}
		}
		if(vlans.length>1){
	    	for(var i=0;i<=numVlan1-numVlan0;i++){
				if(vlanValues.contains(numVlan0+i)){
					hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate" />');
					vlan.focus();
			        return false;
				} else {
					vlanValues.push(numVlan0+i);
				}
			}
	    } else {
	    	if(vlanValues.contains(numVlan0)){
				hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate" />');
				vlan.focus();
		        return false;
			} else {
				vlanValues.push(numVlan0);
			}
	    }
		if(vlanRangerValues.contains(vlanList[j])){
			hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate" />');
			vlan.focus();
	        return false;
		} else {
			vlanRangerValues.push(vlanList[j]);
		}
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="bonjourGatewaySettings" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}

</script>
<div id="content"><s:form action="bonjourGatewaySettings" id="bonjourGatewaySettings">
	<s:hidden name="selectedServiceIDs" id="selectedServiceIDs"/>
	<s:hidden name="delServiceId" id="delServiceId"></s:hidden>
	<s:hidden name="vlanGroupId" />
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
		<div class="topFixedTitle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-bonjour-40x40.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.BonjourGatewaySetting.title"/>
								</s:if> <s:else>
									<s:text name="config.BonjourGatewaySetting.title.edit"/>
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
							<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
										<td width="20px">&nbsp;</td>
										<s:if test="%{dataSource.id == null}">
											<s:if test="%{writeDisabled == 'disabled'}">
												<td class="npcButton"></td>
											</s:if>
											<s:else>
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
											</s:else>
										</s:if>
										<s:else>
											<s:if test="%{writeDisabled == 'disabled'}">
												<td class="npcButton"></td>
											</s:if>
											<s:else>
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
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
			</td>
		</tr>
	</table>
	</div>
	</s:if>
	<s:if test="%{jsonMode==false}">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_BONJOUR_GATEWAY_SETTINGS%>');">
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
	</s:if>
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
        <tr id="fe_ErrorRow" style="display: none">
            <td class="noteError" id="textfe_ErrorRow" colspan="4">To be changed</td>
        </tr>		
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			</s:if>
			<s:else>
			<table width="100%" class="editBox" border="0" cellspacing="0" cellpadding="0">
			</s:else>
				<tr>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="labelT1" width="120px;"><s:text name="config.pppoe.name" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield name="dataSource.bonjourGwName" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									maxlength="%{nameLength}" disabled="%{disabledName}"/>&nbsp;<s:text
									name="config.ipFilter.name.range" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="120px;"><s:text name="config.BonjourGatewaySetting.description" /></td>
								<td><s:textfield name="dataSource.description" size="80"
									maxlength="128" />&nbsp;<s:text
									name="config.BonjourGatewaySetting.description.range" /></td>
							</tr>
							<tr>
								<td class="labelT1" colspan="2"><s:text name="config.BonjourGatewaySetting.vlans" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="120px;"></td>
								<td><s:textfield name="dataSource.vlans" size="48" onkeypress="return hm.util.keyPressPermit(event,'attribute');"
									maxlength="128" />&nbsp;<s:text
									name="config.vlanGroup.vlans.range" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="120px;"></td>
								<td class="noteInfo"><s:text name="config.vlanGroup.valns.note"/></td>
							</tr>
							<tr>
								<td height="4" colspan="2">
								</td>
							</tr>
							<tr>
								<td class="labelT1" colspan="2">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td height="10px" colspan="2"></td>
										</tr>
										<tr>
											<td colspan="2"><script type="text/javascript">insertFoldingLabelContext('<s:text name="monitor.bonjour.gateway.bonjour.services.add.title" />','bonjourServices');</script></td>
										</tr>
										<tr>
											<td colspan="2" style="padding-left: 20px;">
											<div id="bonjourServices" style="display: none;">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td colspan="2">
														<div style="width:500px;">
															<span style='width:250px;float: right;font-weight:bold;'><s:text name="config.BonjourGatewaySetting.service.type"/></span><span style='float: left;width:250px;font-weight:bold;'><s:text name="config.BonjourGatewaySetting.service.name"/></span>
														</div>
													</td>
												</tr>
												<tr>
													<td colspan="2">
														<div style="overflow-x:hidden; overflow-y: auto; position: relative;width:520px; height: 250px; border: 1px solid darkgray;">
														 <!-- fix bug 23044 just hidden checkbox, not modify DB structure -->
														 <!-- <div id="serviceTree" class="ygtv-checkbox"></div> -->
														 <div id="serviceTree"></div>
														</div>
													</td>
												</tr>
												<tr>
													<td height="2">
													</td>
												</tr>
												<tr>
													<td colspan="2" style="font-weight:bold;"><s:text name="config.BonjourGatewaySetting.service.add" /></td>
												</tr>
												<tr>
													<td colspan="2" class="noteInfo" style="padding-left: 10px;" ><s:text name="config.BonjourGatewaySetting.service.note"/></td>
												</tr>
												<tr>
													<td style="padding-left:20px" width="65px;"><s:text name="config.BonjourGatewaySetting.service.name"/></td>
													<td><s:textfield name="service" size="24" maxlength="32" cssStyle="width: 200px;"
														onkeypress="return hm.util.keyPressPermit(event,'name');"/> 
														&nbsp;<s:text name="config.ipFilter.name.range"/></td>
												</tr>
												<tr>
													<td style="padding-left:20px" width="65px;"><s:text name="config.BonjourGatewaySetting.service.type"/></td>
													<td><s:textfield name="type" size="24" maxlength="64" cssStyle="width: 200px;"
														onkeypress="return hm.util.keyPressPermit(event,'name');"/>
														&nbsp;<s:text name="config.serviceFilter.name.range"/>&nbsp;&nbsp;&nbsp;
														<input type="button" name="add" value="Add"  onClick="doAddCustomService();" <s:property value="writeDisabled" /> >
													</td>
												</tr>
												<tr>
													<td height="4">
													</td>
												</tr>
											</table>
											</div>
											</td>
										</tr>
										<tr>
											<td height="4" colspan="2">
											</td>
										</tr>
										<tr>
											<td colspan="2"  style="padding-left: 15px;" class="noteInfo">
												<s:text name="monitor.bonjour.gateway.rule.move.note"></s:text>
											</td>
										</tr>
										<tr>
											<td>
												<label id="tableError"></label>
											</td>
											<td>
												<div id="ahDataTable" style="width: 900px;"></div>
											</td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td height="6">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</s:form></div>

<script>
var ahDataTable;
function onLoadAhDataTable(ahDtClumnDefs,dataSource,editInfo) {
	if (editInfo) {
		eval('var ahDtEditInfo = ' + editInfo);
	}else {
		var ahDtEditInfo;
	}

    myConfigs = {
  		editInfo:{
  			name:"editInfo"
  		},
  		dragEvents: { //for drag
  	        customForNewRow: function() {
  	            tableDragAndDropHelper.enableDragForElement(this);
  	        }
  	    },
    }
        
    $.extend(true, myConfigs, ahDtEditInfo)
        
    myColumnDefs = [
		{
			type: "hidden",
			mark: "ruleIds",
			editMark:"ruleId_edit",
			display: "RowId",
			defaultValue: ""
		},
		{
			type: "dropdownChange",
			mark: "serviceNames",
			editMark:"service_edit",
			display: '<s:text name="config.BonjourGatewaySetting.service.name" />',
			changeCol:2,
			defaultValue: 0,
			width:"150px"
		},
		{
			type: "text",
			mark: "serviceTypes",
			editMark:"serviceType_edit",
			display: '<s:text name="config.BonjourGatewaySetting.service.type" />',
			defaultValue: "",
			disabled:true,
			width:"150px",
			validate:validateService
		},
        {
			type: "dropdowneidt",
			mark: "fromVlanGroups",
			editMark:"fromVlanGroup_edit",
			display: '<s:text name="monitor.bonjour.gateway.from.vlan.group" />',
			defaultValue: -1,
			width:"110px",
			events: {
				newClick: newFromVlanGroup,
				editClick: editFromVlanGroup
			}
		},
        {
			type: "dropdowneidt",
			mark: "toVlanGroups",
			editMark:"toVlanGroup_edit",
			display: '<s:text name="monitor.bonjour.gateway.to.vlan.group" />',
			defaultValue: -1,
			width:"100px",
			events: {
				newClick: newToVlanGroup,
				editClick: editToVlanGroup
			}
		},
		{
			type: "text",
			mark: "metrics",
			display: '<s:text name="monitor.bonjour.gateway.metric" />',
			editMark:"metric_edit",
			defaultValue: "",
			keypress:"ten",
			validate:validateMetric,
			width:"110px",
			maxlength:3,
		},
		{
			type: "dropdown",
			mark: "realms",
			editMark:"Realm_edit",
			display: '<s:text name="monitor.bonjour.gateway.realm" />',
			defaultValue: "[-any-]",
			width:"100px",
			filter:true,
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

    ahDataTable = new AhDataTablePanel.DataTablePanel("ahDataTable",myColumns,dataSource,myConfigs);
    ahDataTable.render();
    
    
    tableDragAndDropHelper = new hm.util.AhDragAndDropTableHelper({
		  "dragContainers": ["ahDataTable_tbody"],
		  "dragElPattern": "div#ahDataTable table tbody tr",
		  "dragHandler": "td.dragMe",
		  events: {
			  whenEndDrag: function() {
				  ahDataTable.alterTrBgColor();
			  }
		  }
		 });
	tableDragAndDropHelper.render();
    
    function newFromVlanGroup() {
    	submitAction("newFromVlanGroup");
    }
    function editFromVlanGroup(editVlanGroupId) {
   		var value = hm.util.validateListSelection(editVlanGroupId);
   		if(value < 0){
   			return;
   		}else{
   			document.forms[formName].vlanGroupId.value = value;
   			submitAction("editFromVlanGroup");
   		}
    }

    function newToVlanGroup() {
    	submitAction("newToVlanGroup");
    }
    function editToVlanGroup(editVlanGroupId) {
    	var value = hm.util.validateListSelection(editVlanGroupId);
   		if(value < 0){
   			return;
   		}else{
   			document.forms[formName].vlanGroupId.value = value;
   			submitAction("editToVlanGroup");
   		}
    }
    function validateMetric(data){
    	var metric = data;
    	if(metric.length > 0 && (metric < 0 || metric > 100)){
    		hm.util.reportFieldError(document.getElementById("tableError"), '<s:text name="error.keyValueRange"><s:param><s:text name="monitor.bonjour.gateway.metric" /></s:param><s:param><s:text name="monitor.bonjour.gateway.metric.range" /></s:param></s:text>');
    		return false;
    	}
    	
    	return true;
    }

    function validateService(data){
    	if(data.length < 1 || data == ""){
    		hm.util.reportFieldError(document.getElementById("tableError"), '<s:text name="error.requiredField"><s:param>Service and Type</s:param></s:text>');
    		return false;
    	}
    	
    	return true;
    }
}

</script>