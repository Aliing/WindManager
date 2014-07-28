<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.FirewallPolicy" %>

<style type="text/css">
.custom-class {  
    opacity: 0.6;filter:alpha(opacity=60);   
    color:blue;   
    border: 2px solid gray;   
} 
  
#datatable tr {
    cursor: pointer;   
}

#datatable th {  
    color: #003366;
    font-weight: bold;  
}

#datatable .yui-dt-selected {
    background-color: #000;
    color: #FFF;
} 
</style>

<link rel="stylesheet" href="<s:url value="/css/hm_v2.css" includeParams="none"/>" type="text/css" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />

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
	src="<s:url value="/yui/datatable/datatable-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>
var formName = 'firewallPolicy';

// editor cell for google browser
if (navigator.appVersion.indexOf('Chrome') > -1) {
	// Custom formatter for "action" column to string label   
    var formatAction = function(elCell, oRecord, oColumn, oData) {
    	var actionStr = oData=='1' ? 'Permit' : 'Deny';
        elCell.innerHTML = actionStr;
    };
    // Custom formatter for "log" column to string label   
    var formatActionLog = function(elCell, oRecord, oColumn, oData) {
    	var logStr = oData=='1' ? 'Enable' : 'Disable';
        elCell.innerHTML = logStr;
    };
	var myColumnDefs = [
		{key:"ruleId", hidden:"true", formatter:"number"},
	    {key:"srcStr", label:'<s:text name="config.firewall.policy.rule.title.source" />'},   
	    {key:"destStr", label:'<s:text name="config.firewall.policy.rule.title.dest" />'},
	    {key:"service", label:'<s:text name="config.ipPolicy.networkService" />'},   
	    {key:"action", label:'<s:text name="config.macFilter.action" />', formatter:formatAction, editor: new YAHOO.widget.DropdownCellEditor({dropdownOptions:[{label:"Permit", value:"1"}, {label:"Deny", value:"2"}],disableBtns:true})},
	    {key:"log", label:'<s:text name="config.qos.logging" />', formatter:formatActionLog, editor: new YAHOO.widget.DropdownCellEditor({dropdownOptions:[{label:"Enable", value:"1"}, {label:"Disable", value:"2"}],disableBtns:true})},   
	    {key:"disable", label:'<s:text name="config.firewall.policy.rule.title.disable" />', formatter:"checkbox"}  
	];
} else {
	var myColumnDefs = [
		{key:"ruleId", hidden:"true", formatter:"number"},
	    {key:"srcStr", label:'<s:text name="config.firewall.policy.rule.title.source" />'},   
	    {key:"destStr", label:'<s:text name="config.firewall.policy.rule.title.dest" />'},
	    {key:"service", label:'<s:text name="config.ipPolicy.networkService" />'},   
	    {key:"action", label:'<s:text name="config.macFilter.action" />', formatter:"dropdown", dropdownOptions:[{label:"Permit", value:"1"}, {label:"Deny", value:"2"}]},
	    {key:"log", label:'<s:text name="config.qos.logging" />', formatter:"dropdown", dropdownOptions:[{label:"Enable", value:"1"}, {label:"Disable", value:"2"}]},   
	    {key:"disable", label:'<s:text name="config.firewall.policy.rule.title.disable" />', formatter:"checkbox"}  
	];
}

var myDataSource = new YAHOO.util.DataSource('<s:url action="firewallPolicy" includeParams="none" />?operation=fetchPolicyLs&ignore='+new Date().getTime());
myDataSource.responseSchema = {
    fields: [ "ruleId", "srcStr", "destStr", "service", "action", "log", "disable"]
};
myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
		<s:if test="%{showUpInNavTree}">
			document.forms[formName].operation.value = operation;
    		document.forms[formName].submit();
		</s:if>
		<s:else>
			if ('cancel' == operation) {
				parent.closeIFrameDialog();
				<s:if test="%{dataSource.id != null && null != contentShowType && 'dlg' == contentShowType}">
					parent.selectFirewallPolicy('<s:property value="%{dataSource.id}" />');
				</s:if>
			} else {
				if ('create' == operation) {
					<s:if test="%{dataSource.id != null}">
						operation = 'update';
					</s:if>
				}
				document.forms[formName].operation.value = operation;
    			document.forms[formName].submit();
			}
		</s:else>
	}
}

function validate(operation) {
	var recordData = myDataTable.getRecordSet();
    if (null != recordData) {
    	var idArray = new Array();
    	var actionArray = new Array();
    	var logArray = new Array();
    	var disArray = new Array();
    	for (var i = 0; i < recordData.getLength(); i++) {
    		var singleRecord = recordData.getRecord(i);
    		idArray[i] = singleRecord.getData("ruleId");
			actionArray[i] = singleRecord.getData("action");
			logArray[i] = singleRecord.getData("log");
			disArray[i] = singleRecord.getData("disable");
		}
		document.forms[formName].ruleIdStr.value = idArray;
		document.forms[formName].actionStr.value = actionArray;
		document.forms[formName].logStr.value = logArray;
		document.forms[formName].disableStr.value = disArray;
    }
    
	if('<%=Navigation.L2_FEATURE_L3_FIREWALL_POLICY%>' == operation || operation == 'newIpAddress' || operation == 'newService'
		 || operation == 'newNetworkObj' || operation == 'newUserProfile' || operation == 'cancel<s:property value="lstForward"/>'
		 || operation == 'newDestNetworkObj' || operation == 'newDestIpAddress') {
		setSourceIpObjectId();
		setDestinationIpObjectId();
		return true;
	}
	if(operation == "editIpAddress"){
		var value = hm.util.validateListSelection("sourceIpSelect");
		if(value < 0){
			return false
		}else{
			setDestinationIpObjectId();
			document.forms[formName].sourceIpId.value = value;
		}
	}
	if(operation == "editDestIpAddress"){
		var value = hm.util.validateListSelection("destIpSelect");
		if(value < 0){
			return false
		}else{
			setSourceIpObjectId();
			document.forms[formName].destIpId.value = value;
		}
	}
	if(operation == "editService"){
		var value = hm.util.validateListSelection(formName + "_netServiceId");
		if(value < 0){
			return false
		}
		setSourceIpObjectId();
		setDestinationIpObjectId();
	}
	if(operation == "editNetworkObj"){
		var value = hm.util.validateListSelection("sourceNetObjId");
		if(value < 0){
			return false
		}
		setDestinationIpObjectId();
	}
	if(operation == "editDestNetworkObj"){
		var value = hm.util.validateListSelection("destNetObjId");
		if(value < 0){
			return false
		}
		setSourceIpObjectId();
	}
	if(operation == "editUserProfile"){
		var value = hm.util.validateListSelection("sourceUpId");
		if(value < 0){
			return false
		}
		setDestinationIpObjectId();
	}
	if (operation == 'addPolicyRules') {
		var sourceType = document.getElementById(formName + "_sourceType").value;
		switch (parseInt(sourceType)) {
			case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET%>:
			case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE%>:
			case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD%>:
				if (!validateIpObjectInput(parseInt(sourceType), document.getElementById("errorDisplaySr"), document.getElementById("sourceIpSelect"),
 					document.getElementById("srIpAddressStr"), true)) {
 					return false;
				} else {
					break;
				}
			case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ%>:
				var srNet = document.getElementById("sourceNetObjId");
				if (srNet.value <= 0) {
		            hm.util.reportFieldError(srNet, '<s:text name="error.pleaseSelect"><s:param><s:text name="config.firewall.policy.rule.title.net.obj" /></s:param></s:text>'); 
		            srNet.focus(); 
					return false;
				} else {
					break;
				}
			case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_UPOBJ%>:
				var srUp = document.getElementById("sourceUpId");
				if (srUp.value <= 0) {
		            hm.util.reportFieldError(srUp, '<s:text name="error.pleaseSelect"><s:param><s:text name="config.firewall.policy.rule.title.user.profile" /></s:param></s:text>'); 
		            srUp.focus(); 
					return false;
				} else {
					break;
				}
			default:
				break;
		}
		
		var destType = document.getElementById(formName + "_destType").value;
		switch (parseInt(destType)) {
			case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET%>:
			case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE%>:
			case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD%>:
			case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_HOST%>:
				if (!validateIpObjectInput(parseInt(destType), document.getElementById("errorDisplayDest"), document.getElementById("destIpSelect"),
 					document.getElementById("destIpAddressStr"), false)) {
 					return false;
				} else {
					break;
				}
			case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ%>:
				var destNet = document.getElementById("destNetObjId");
				if (destNet.value <= 0) {
		            hm.util.reportFieldError(destNet, '<s:text name="error.pleaseSelect"><s:param><s:text name="config.firewall.policy.rule.title.net.obj" /></s:param></s:text>'); 
		            destNet.focus(); 
					return false;
				} else {
					break;
				}
			default:
				break;
		}
	} 
	if (operation == 'removePolicyRules' || operation == 'removePolicyRulesNone') {
		var rows = myDataTable.getSelectedRows();
		if (undefined == rows || rows.length == 0) {
			hm.util.reportFieldError(document.getElementById("tableError"), '<s:text name="error.pleaseSelectItem"><s:param>Policy Rule</s:param></s:text>');  
			return false;
		}
		for (var i = 0; i < rows.length; i++) {
			myDataTable.deleteRow(rows[i]);
		}
		return false;
	} 
	var name = document.getElementById(formName + "_dataSource_policyName");
	if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'create'){
    	if (!checkNameValid(name, '<s:text name="config.ipPolicy.policyName" />', name)) {
        	return false;
    	}
    }
	return true;
}

function setSourceIpObjectId() {
	if (document.getElementById("srIpObjectTr").style.display == "") {
		var value = document.getElementById("sourceIpSelect").value;
		if(value > 0){
			document.forms[formName].sourceIpId.value = value;
		}
	}
}

function setDestinationIpObjectId() {
	if (document.getElementById("destIpObjectTr").style.display == "") {
		var value = document.getElementById("destIpSelect").value;
		if(value > 0){
			document.forms[formName].destIpId.value = value;
		}
	}
}

function hasSelectedOptions(options) {
	for (var i = 0; i < options.length; i++) {
		if (options[i].selected) {
			return true;
		}
	}
	return false;
}

function checkNameValid(name, title, errorDis) {
	var message = hm.util.validateName(name.value, title);
   	if (message != null) {
   		hm.util.reportFieldError(errorDis, message);
       	name.focus();
       	return false;
   	}
   	return true;
}

function validateIpObjectInput(typeFlag, showError, ipnames, ipValue, sourceFlag) {
	var errTitle = '<s:text name="config.firewall.policy.rule.title.ip.net" />';
	if (typeFlag == <%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE%>) {
		errTitle = '<s:text name="config.firewall.policy.rule.title.ip.range" />';
	} else if (typeFlag == <%=FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD%>) {
		errTitle = '<s:text name="config.firewall.policy.rule.title.wildcard" />';
	} else if (typeFlag == <%=FirewallPolicy.FIREWALL_POLICY_TYPE_HOST%>) {
		errTitle = '<s:text name="config.firewall.policy.rule.title.host" />';
	}

	if ("" == ipValue.value) {
        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param>'+errTitle+'</s:param></s:text>');
        ipValue.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {

		// ip range
		if (typeFlag == <%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE%>) {
			var twoIps = ipValue.value.split("-");
			if (twoIps.length != 2) {
				hm.util.reportFieldError(showError, '<s:text name="error.formatInvalid"><s:param>'+errTitle+'</s:param></s:text>');
		        ipValue.focus();
				return false;
			}
			if (!hm.util.validateIpAddress(twoIps[0])) {
				hm.util.reportFieldError(showError, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ipAddress.ipAddress.start" /></s:param></s:text>');
		        ipValue.focus();
				return false;
			}
			if (!hm.util.validateIpAddress(twoIps[1])) {
				hm.util.reportFieldError(showError, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ipAddress.ipAddress.end" /></s:param></s:text>');
		        ipValue.focus();
				return false;
			}
			if (!hm.util.compareIpAddress(twoIps[0], twoIps[1])) {
				hm.util.reportFieldError(showError, '<s:text name="error.notLargerThan"><s:param><s:text name="config.ipAddress.ipAddress.start" /></s:param><s:param><s:text name="config.ipAddress.ipAddress.end" /></s:param></s:text>');
				ipValue.focus();
				return false;
			}
		} else if (typeFlag == <%=FirewallPolicy.FIREWALL_POLICY_TYPE_HOST%>) {
			if (!checkNameValid(ipValue, showError, errTitle)) {
	        	return false;
	    	}
	    	if (hm.util.validateIpAddress(ipValue.value)) {
				hm.util.reportFieldError(showError, '<s:text name="error.formatInvalid"><s:param>'+errTitle+'</s:param></s:text>');
				ipValue.focus();
				return false;
			}
		} else {
			var twoIps = ipValue.value.split("/");
			if (twoIps.length != 2) {
				hm.util.reportFieldError(showError, '<s:text name="error.formatInvalid"><s:param>'+errTitle+'</s:param></s:text>');
		        ipValue.focus();
				return false;
			}
			if (!hm.util.validateIpAddress(twoIps[0])) {
				hm.util.reportFieldError(showError, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ipAddress.ipAddress" /></s:param></s:text>');
		        ipValue.focus();
				return false;
			}
			if (typeFlag == <%=FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD%>) {
				// wildcard
				if (!hm.util.validateIpAddressFormat(twoIps[1])) {
					hm.util.reportFieldError(showError, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ipAddress.wildcard" /></s:param></s:text>');
			        ipValue.focus();
					return false;
				}
			} else {
				// ip network
				var message = hm.util.validateIntegerRange(twoIps[1], '<s:text name="config.ipAddress.netmask" />', 0, 32);
			    if (message != null) {
			        hm.util.reportFieldError(showError, message);
			        ipValue.focus();
			        return false;
			    }
			}
		}
		if (sourceFlag) {
			document.forms[formName].sourceIpId.value = -1;
		} else {
			document.forms[formName].destIpId.value = -1;
		}
	} else {
		if (sourceFlag) {
			document.forms[formName].sourceIpId.value = ipnames.options[ipnames.selectedIndex].value;
		} else {
			document.forms[formName].destIpId.value = ipnames.options[ipnames.selectedIndex].value;
		}
	}
	return true;
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');
	var trh = document.getElementById('headerSection');
	var trc = document.getElementById('createSection');
	var table = trh.parentNode;	
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection() {
	hm.util.hide('createButton');
	hm.util.show('newButton');
	hm.util.hide('createSection');
}

function changeSourceType(srType) {
	var ipObjTr = document.getElementById("srIpObjectTr");
	var netObjTr = document.getElementById("srNetObjectTr");
	var upObjTr = document.getElementById("srUserProfileTr");
	switch (parseInt(srType)) {
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_ANY%>:
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_VPN%>:
			ipObjTr.style.display="none";
			netObjTr.style.display="none";
			upObjTr.style.display="none";
			break;
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET%>:
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE%>:
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD%>:
			ipObjTr.style.display="";
			
			// description display
			document.getElementById("srIpNetTrDesc").style.display=parseInt(srType)==<%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET%>?"":"none";
			document.getElementById("srIpRangeTrDesc").style.display=parseInt(srType)==<%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE%>?"":"none";
			document.getElementById("srIpWildTrDesc").style.display=parseInt(srType)==<%=FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD%>?"":"none";
			
			netObjTr.style.display="none";
			upObjTr.style.display="none";
			
			var sourceUrl = '<s:url action="firewallPolicy" includeParams="none" />?operation=changeSourceType&sourceType='+srType+'&key='+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('GET', sourceUrl, sourceCallback, null);
			break;
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ%>:
			ipObjTr.style.display="none";
			netObjTr.style.display="";
			upObjTr.style.display="none";
			break;
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_UPOBJ%>:
			ipObjTr.style.display="none";
			netObjTr.style.display="none";
			upObjTr.style.display="";
			break;
		default:
			break;
	}
}

var sourceChangeSuccess = function(o) {
	eval("var details = " + o.responseText);
	var tdList = document.getElementById(details.tdId);
	var ipObjs = details.v;
	tdList.length=0;
	if (null != ipObjs) {
		tdList.length=ipObjs.length;
		for(var i = 0; i < ipObjs.length; i ++) {
			tdList.options[i].value=ipObjs[i].id;
			tdList.options[i].text=ipObjs[i].valueStr;
		}
	}
	//when change between ipaddress type, clean the before value.
	if(details.tdId == "sourceIpSelect"){
		document.getElementById("srIpAddressStr").value = "";
	}else if(details.tdId == "destIpSelect"){
		document.getElementById("destIpAddressStr").value = "";
	}
	
};

var sourceChangeFailed = function(o) {
//	alert("failed.");
};

var sourceCallback = {
	success : sourceChangeSuccess,
	failure : sourceChangeFailed
};

function changeDestType(srType) {
	var ipObjTr = document.getElementById("destIpObjectTr");
	var netObjTr = document.getElementById("destNetObjectTr");
	switch (parseInt(srType)) {
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_ANY%>:
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_VPN%>:
			ipObjTr.style.display="none";
			netObjTr.style.display="none";
			break;
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET%>:
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE%>:
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD%>:
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_HOST%>:
			ipObjTr.style.display="";
			
			// description display
			document.getElementById("destIpNetTrDesc").style.display=parseInt(srType)==<%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET%>?"":"none";
			document.getElementById("destIpRangeTrDesc").style.display=parseInt(srType)==<%=FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE%>?"":"none";
			document.getElementById("destIpWildTrDesc").style.display=parseInt(srType)==<%=FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD%>?"":"none";
			document.getElementById("destIpHostTrDesc").style.display=parseInt(srType)==<%=FirewallPolicy.FIREWALL_POLICY_TYPE_HOST%>?"":"none";
			
			netObjTr.style.display="none";
			
			var sourceUrl = '<s:url action="firewallPolicy" includeParams="none" />?operation=changeDestType&destType='+srType+'&key='+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('GET', sourceUrl, sourceCallback, null);
			break;
		case <%=FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ%>:
			ipObjTr.style.display="none";
			netObjTr.style.display="";
			break;
		default:
			break;
	}
}

YAHOO.util.Event.addListener(window, "load", function() {   
	hm.util.initDragTableElement(myDataTable);
    myDataTable.subscribe("checkboxClickEvent", function(oArgs){   
        var elCheckbox = oArgs.target;   
        var oRecord = this.getRecord(elCheckbox);   
        oRecord.setData("disable", elCheckbox.checked);   
    });
    
    myDataTable.subscribe("dropdownChangeEvent", function(oArgs){   
        var elDropdown = oArgs.target;   
        var oRecord = this.getRecord(elDropdown);
        var elValue = elDropdown.options[elDropdown.selectedIndex].value;
        var elText = elDropdown.options[elDropdown.selectedIndex].text;
        if (elText == "Enable" || elText == "Disable") {
        	oRecord.setData("log", elValue);
        } else {
        	oRecord.setData("action", elValue);
        }
    });              
});

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_policyName").disabled) {
		document.getElementById(formName + "_dataSource_policyName").focus();
	}
	if (document.getElementById("srIpObjectTr").style.display=='') {
		var srIpId = '<s:property value="sourceIpId"/>';
		if (null != srIpId && srIpId != '-1') {
			var selectEle = document.getElementById("sourceIpSelect");
			var inputEle = document.getElementById("srIpAddressStr");
			for (var i = 0; i < selectEle.length; i++) {
				if (selectEle.options[i].value == srIpId) {
					selectEle.selectedIndex = i;
					inputEle.value = selectEle.options[i].text;
					inputEle.style.zIndex = 2;
				}
			}
		}
	}
	myDataTable = new YAHOO.widget.DataTable("datatable", myColumnDefs, myDataSource, null);
	
	<s:if test="%{!showUpInNavTree}">
		<s:if test="%{id != null && null != operation && ('create' == operation || 'update' == operation) && actionErrors.size == 0}">
			<s:if test="%{'create' == operation}">
				var url = '<s:url action="networkPolicy" includeParams="none" />?operation=finishSelectFwPolicy&selectFwPolicyId='+'<s:property value="%{id}" />';
				var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : top.succFinishSelectFw, failure : top.resultDoNothing, timeout: 60000}, null);
			</s:if>
			<s:else>
				parent.closeIFrameDialog();
				<s:if test="%{null != contentShowType && 'dlg' == contentShowType}">
					parent.selectFirewallPolicy('<s:property value="%{id}" />');
				</s:if>
			</s:else>
		</s:if>
		<s:else>
			parent.changeIFrameDialog(800, 600);
		</s:else>
	</s:if>
}

<s:if test="%{showUpInNavTree}">
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="firewallPolicy" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedPolicyName" />\'</td>');
		</s:else>
	</s:else>	
}
</s:if>
</script>
<div id="content"><s:form action="firewallPolicy">
	<s:hidden name="sourceIpId"></s:hidden>
	<s:hidden name="destIpId"></s:hidden>
	<s:hidden name="ruleIdStr" />
	<s:hidden name="actionStr" />
	<s:hidden name="logStr" />
	<s:hidden name="disableStr" />
	<s:if test="%{!showUpInNavTree}">
		<s:hidden name="id"/>
		<s:hidden name="operation" />
		<s:hidden name="contentShowType"></s:hidden>
	</s:if>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{showUpInNavTree}">
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
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_L3_FIREWALL_POLICY%>');">
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
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		</s:if>
		<s:else>
		<tr>
			<td>
			<div class="topFixedTitle">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-firewall-big.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.title.firewallPolicy.new"/>
								</s:if>
								<s:else>
									<s:text name="config.title.firewallPolicy.edit"/>
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
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitAction('cancel');" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<td class="npcButton">
								<s:if test="%{writeDisabled == 'disabled'}">
									&nbsp;
								</s:if>
								<s:else>
									<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('create');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a>
								</s:else>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		</s:else>
		<tr>
			<td>
			<s:if test="%{showUpInNavTree}">
				<table class="editBox" cellspacing="0" cellpadding="0" border="0">
			</s:if>
			<s:else>
				<table cellspacing="0" cellpadding="0" border="0" class="topFixedTitle">
				<tr>
					<td style="padding-top: 5px;"><tiles:insertDefinition name="notes" /></td>
				</tr>
			</s:else>
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
								<td height="2"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding:0 10px 0 10px">
						<fieldset><legend><s:text name="config.ipPolicy.rules" /></legend>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
									<label id="tableError"></label>
								</td>
								<td style="padding:4px 0px 4px 0px;" valign="top">
									<table cellspacing="0" cellpadding="0" border="0" class="embedded">
										<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
											<td colspan="7" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
														class="button" onClick="showCreateSection();"></td>
													<td><input type="button" name="ignore" value="Remove"
														class="button" <s:property value="writeDisabled" />
														onClick="submitAction('removePolicyRules');"></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
											<td colspan="8" style="padding-bottom: 2px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
														class="button" onClick="submitAction('addPolicyRules');"></td>
													<td><input type="button" name="ignore" value="Remove"
														class="button" <s:property value="writeDisabled" />
														onClick="submitAction('removePolicyRulesNone');"></td>
													<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
														class="button" onClick="hideCreateSection();"></td>
													<td style="padding-left:50px">
														<s:checkbox name="dataSource.addRuleInTop"></s:checkbox>
														<s:text name="config.firewall.policy.rule.add.on.top"></s:text>
													</td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="headerSection">
										</tr>
										<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
											<td colspan="6">
												<fieldset style="width: 600px;background-color: #edf5ff">
												<legend><s:text name="config.firewall.policy.rule.add.title" /></legend>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td class="labelT1" width="80"><label><s:text
															name="config.firewall.policy.rule.title.source" /></label></td>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:select name="sourceType" list="%{enumSourceType}" listKey="key" listValue="value"
																		cssStyle="width: 150px;" onchange="changeSourceType(this.options[this.selectedIndex].value);" /></td>
																</tr>
																<tr id="srIpObjectTr" style="display:<s:property value="(sourceType==2 || sourceType==3 || sourceType==7)?'':'none'" />">
																	<td>
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td height="5px"></td>
																			</tr>
																			<tr>
																				<td>
																					<label id="errorDisplaySr"></label>
																				</td>
																				<td>
																					<ah:createOrSelect divId="errorDisplaySrId" list="availableIpAddress" typeString="IpAddress"
																						selectIdName="sourceIpSelect" inputValueName="srIpAddressStr" swidth="200px" tlength="64" />
																				</td>
																				<td id="srIpNetTrDesc" style="display:<s:property value="sourceType==2?'':'none'" />">
																					&nbsp;<s:text name="config.firewall.policy.rule.ip.net.descr"></s:text>
																				</td>
																				<td id="srIpRangeTrDesc" style="display:<s:property value="sourceType==3?'':'none'" />">
																					&nbsp;<s:text name="config.firewall.policy.rule.ip.range.descr"></s:text>
																				</td>
																				<td id="srIpWildTrDesc" style="display:<s:property value="sourceType==7?'':'none'" />">
																					&nbsp;<s:text name="config.firewall.policy.rule.wildcard.descr"></s:text>
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
																<tr id="srNetObjectTr" style="padding-top:5px;display:<s:property value="sourceType==4?'':'none'" />">
																	<td><s:select name="sourceNetObjId" list="%{availableNetworkObj}" listKey="id" listValue="value"
																		cssStyle="width: 200px;" id="sourceNetObjId" /></td>
																	<td style="padding-left:3px;">
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New" title="New" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('newNetworkObj')"><img class="dinl"
																			src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																		</s:else>
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																			width="16" height="16" alt="Modify" title="Modify" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('editNetworkObj')"><img class="dinl"
																			src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																		</s:else>
																	</td>
																</tr>
																<tr id="srUserProfileTr" style="padding-top:5px;display:<s:property value="sourceType==5?'':'none'" />">
																	<td><s:select name="sourceUpId" list="%{availableUserProfile}" listKey="id" listValue="value"
																		cssStyle="width: 200px;" id="sourceUpId" /></td>
																	<td style="padding-left:3px;">
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New" title="New" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('newUserProfile')"><img class="dinl"
																			src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																		</s:else>
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																			width="16" height="16" alt="Modify" title="Modify" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('editUserProfile')"><img class="dinl"
																			src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																		</s:else>
																	</td>
																</tr>
																<tr>
																	<td height="5px"></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text
															name="config.firewall.policy.rule.title.dest" /></label></td>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:select name="destType" list="%{enumDestType}" listKey="key" listValue="value"
																		cssStyle="width: 150px;" onchange="changeDestType(this.options[this.selectedIndex].value);" /></td>
																</tr>
																<tr id="destIpObjectTr" style="display:<s:property value="(destType==2 || destType==3 || destType==7 || destType==8)?'':'none'" />">
																	<td>
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td height="5px"></td>
																			</tr>
																			<tr>
																				<td>
																					<label id="errorDisplayDest"></label>
																				</td>
																				<td>
																					<ah:createOrSelect divId="errorDisplayDestId" list="availableDestIpAddress" swidth="200px"
																						selectIdName="destIpSelect" inputValueName="destIpAddressStr" typeString="DestIpAddress" />
																				</td>
																				<td id="destIpNetTrDesc" style="display:<s:property value="destType==2?'':'none'" />">
																					&nbsp;<s:text name="config.firewall.policy.rule.ip.net.descr"></s:text>
																				</td>
																				<td id="destIpRangeTrDesc" style="display:<s:property value="destType==3?'':'none'" />">
																					&nbsp;<s:text name="config.firewall.policy.rule.ip.range.descr"></s:text>
																				</td>
																				<td id="destIpWildTrDesc" style="display:<s:property value="destType==7?'':'none'" />">
																					&nbsp;<s:text name="config.firewall.policy.rule.wildcard.descr"></s:text>
																				</td>
																				<td id="destIpHostTrDesc" style="display:<s:property value="destType==8?'':'none'" />">
																					&nbsp;<s:text name="config.firewall.policy.rule.host.descr"></s:text>
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
																<tr id="destNetObjectTr" style="padding-top:5px;display:<s:property value="destType==4?'':'none'" />">
																	<td><s:select name="destNetObjId" list="%{availableNetworkObj}" listKey="id" listValue="value"
																		cssStyle="width: 200px;" id="destNetObjId" /></td>
																	<td style="padding-left:3px;">
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New" title="New" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('newDestNetworkObj')"><img class="dinl"
																			src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																		</s:else>
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																			width="16" height="16" alt="Modify" title="Modify" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('editDestNetworkObj')"><img class="dinl"
																			src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																		</s:else>
																	</td>
																</tr>
																<tr>
																	<td height="5px"></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text
															name="config.ipPolicy.networkService" /></label></td>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:select name="netServiceId" list="%{availableNetworkServices}" listKey="id"
																		listValue="value" cssStyle="width: 200px;" /></td>
																	<td valign="top" style="padding-left:3px;">
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																			width="16" height="16" alt="New" title="New" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('newService')"><img class="dinl"
																			src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																		</s:else>
																		<s:if test="%{writeDisabled == 'disabled'}">
																			<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																			width="16" height="16" alt="Modify" title="Modify" />
																		</s:if>
																		<s:else>
																			<a class="marginBtn" href="javascript:submitAction('editService')"><img class="dinl"
																			src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																		</s:else>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text
															name="config.macFilter.action" /></label></td>
														<td><s:select name="filterAction"
															list="%{enumAction}" listKey="key" listValue="value" cssStyle="width: 80px;" /></td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text
															name="config.qos.logging" /></label></td>
														<td><s:select name="actionLog" id="actionLog"
															list="%{enumActionLog}" listKey="key" listValue="value" cssStyle="width: 80px;" /></td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text
															name="config.firewall.policy.rule.title.disable" /></label></td>
														<td><s:checkbox name="disableRule" /></td>
													</tr>
												</table>
												</fieldset>
											</td>
										</tr>
										<tr>
											<td style="padding:4px 0 4px 0px;" class="noteInfo">
												<s:text name="config.firewall.policy.rule.move.note"></s:text>
											</td>
										</tr>
										<tr>
											<td>
												<div id="datatable"></div>
											</td>
										</tr>
										<tr>
											<td style="padding:5px 0 0 5px">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td width="80"><label><s:text
															name="config.firewall.policy.rule.title.default" /></label></td>
														<td width="90px"><s:radio label="Gender" value="%{defRuleRadio}"
															name="defRuleRadio" list="#{'permit':'Permit all'}" /></td>
														<td width="90px"><s:radio label="Gender" value="%{defRuleRadio}"
															name="defRuleRadio" list="#{'deny':'Deny all'}" /></td>
														<td style="padding-left:10px"><label><s:text
															name="config.qos.logging" /></label></td>
														<td style="padding-left:5px">
															<s:select name="dataSource.defRuleLog" list="%{enumActionLog}" listKey="key"
																listValue="value" cssStyle="width: 80px;"/>
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
				<tr>
					<td height="10"></td>
				</tr>
			</table>
		</td>
		</tr>
	</table>
</s:form></div>
