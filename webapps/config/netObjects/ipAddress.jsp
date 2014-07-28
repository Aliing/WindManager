<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.SingleTableItem"%>
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/ct.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/panel.css" includeParams="none"/>?v=<s:property value="verParam" />" /> 
<script>
var formName = 'ipAddress';
var operName = 'create';
var ruleIndex= '';
var USETYPE_GLOBAL = <%=SingleTableItem.TYPE_GLOBAL%>;
var USETYPE_MAP = <%=SingleTableItem.TYPE_MAP%>;
var USETYPE_HIVEAP = <%=SingleTableItem.TYPE_HIVEAPNAME%>;
var USETYPE_CLASSIFIER = <%=SingleTableItem.TYPE_CLASSIFIER%>;
var ipFlag = <s:property value="%{dataSource.typeFlag}"/>;
var beforeIpFlag =ipFlag;
var diffIpFlag =true;
function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_addressName").disabled == false) {
		document.getElementById(formName + "_dataSource_addressName").focus();
	}
	<s:if test="%{ipType == 'cwpSuccessURLRedirect' || ipType == 'cwpFailureURLRedirect'}">
		document.getElementById(formName + "_radioIpOrNamewebPage").disabled = false;
		document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
		document.getElementById(formName + "_radioIpOrNamename").disabled = true;
		document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
		document.getElementById(formName + "_radioIpOrNameaddress").disabled = true;
	</s:if>
	<s:else>
		<s:if test="%{(ipType == null || ''.equals(ipType)) && !disabledName}">
			document.getElementById(formName + "_radioIpOrNamewebPage").disabled = false;
		</s:if>
		<s:else>
			document.getElementById(formName + "_radioIpOrNamewebPage").disabled = true;
		</s:else>
	</s:else>
	
	<s:if test="%{jsonMode}">
		if(top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(1030, 450);
		}
		<s:if test="%{ipType == 'ipFilter'}">
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
			document.getElementById(formName + "_radioIpOrNamename").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		</s:if>
		<s:if test="%{ipType == 'capwapIp'}">
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
			document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		</s:if>
		<s:if test="%{ipType == 'capwapBackupIp'}">
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
			document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		</s:if>
		<s:if test="%{ipType == 'snmp'}">
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
			document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		</s:if>
		<s:if test="%{ipType == 'syslog'}">
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
			document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		</s:if>	
		<s:if test="%{ipType == 'dns'}">
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
			document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
			document.getElementById(formName + "_radioIpOrNamename").disabled = true;
		</s:if>	
		<s:if test="%{ipType == 'ntp'}">
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
			document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		</s:if>
		<s:if test="%{ipType == 'firewallNetwork'}">
			document.getElementById(formName + "_radioIpOrNameaddress").disabled = true;
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
			document.getElementById(formName + "_radioIpOrNamename").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		</s:if>
		<s:if test="%{ipType == 'firewallRange'}">
			document.getElementById(formName + "_radioIpOrNameaddress").disabled = true;
			document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
			document.getElementById(formName + "_radioIpOrNamename").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		</s:if>
		<s:if test="%{ipType == 'firewallWildcard'}">
			document.getElementById(formName + "_radioIpOrNameaddress").disabled = true;
			document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
			document.getElementById(formName + "_radioIpOrNamename").disabled = true;
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
		</s:if>
		<s:if test="%{ipType == 'firewallName'}">
			document.getElementById(formName + "_radioIpOrNameaddress").disabled = true;
			document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
		</s:if>
		<s:if test="%{ipType == 'tunnelDestination'}">
			document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
			document.getElementById(formName + "_radioIpOrNamename").disabled = true;
		</s:if>
		<s:if test="%{ipType == 'tunnelSource'}">
			document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
			document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
			document.getElementById(formName + "_radioIpOrNamename").disabled = true;
		</s:if>
		
	</s:if>
	//From navigation tree, we also need to add the control for ipPolicy
	<s:if test="%{ipType == 'ipPolicyIPType'}">
		document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
	</s:if>
	<s:if test="%{ipType == 'dnsServiceIPType'}">
		document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
		document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
		document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		document.getElementById(formName + "_radioIpOrNamename").disabled = true;
	</s:if>
	
	<s:if test="%{ipType == 'radiusOnHiveApIPType'}">
		document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
		document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
	</s:if>
	
	<s:if test="%{ipType == 'tunnelDestination'}">
		document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
		document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
		document.getElementById(formName + "_radioIpOrNamename").disabled = true;
	</s:if>
	<s:if test="%{ipType == 'tunnelSource'}">
		document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
		document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
		document.getElementById(formName + "_radioIpOrNamename").disabled = true;
	</s:if>
	<s:if test="%{ipType == 'WGIp'}">
		document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
	</s:if>
	<s:if test="%{ipType == 'activeDirectoryOrLdap'}">
		document.getElementById(formName + "_radioIpOrNamerange").disabled = true;
		document.getElementById(formName + "_radioIpOrNamenetwork").disabled = true;
		document.getElementById(formName + "_radioIpOrNamewildcard").disabled = true;
	</s:if>
	
	if(!window.jQuery) {
		head.js("<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	if(!window.jQuery.ui) {
		head.js("<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	head.js("<s:url value='/js/widget/classifiertag/ct-debug.js' includeParams='none'/>?v=<s:property value='verParam' />",
			"<s:url value='/js/widget/dialog/panel.js' includeParams='none'/>?v=<s:property value='verParam' />",
	function(){
		initClassifierWidget();
	});
}

<s:if test="%{!jsonMode}">
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="ipAddress" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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
</s:if>


function saveIpAddressJsonDlg(operation){
	var url = "<s:url action='ipAddress' includeParams='none' />?ignore="+new Date().getTime();
	document.forms[formName].operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("ipAddress"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveIpAddressJsonDlg, failure : failSaveIpAddressJsonDlg, timeout: 60000}, null);	
}

var succSaveIpAddressJsonDlg = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.addedId != null && details.addedId != ''){
					if(details.parentDomID == "successExternalURLId" || details.parentDomID == "failureExternalURLId"){
						insertIPObjectForCwpExternalURL(details);
					}else{
						hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
					}
					
				}
			}
		}
		top.closeIFrameDialog();
	} catch(e) {
	}
}
var failSaveIpAddressJsonDlg = function(o){
}

function insertIPObjectForCwpExternalURL(details){
	var successExternalIpObjectId = parent.Get("successExternalURLId");
	if (successExternalIpObjectId) {
		if($(successExternalIpObjectId).find(" option[value='"+details.addedId+"']").length ==0) {
			if(details.parentDomID == "successExternalURLId") {
				hm.util.insertSelectValue(details.addedId, details.addedName, successExternalIpObjectId, false, true);
			} else {
				hm.util.insertSelectValue(details.addedId, details.addedName, successExternalIpObjectId, false, false);
			}
		}
	}
	var failureExternalURLId = parent.Get("failureExternalURLId");
	if (failureExternalURLId) {
		if($(failureExternalURLId).find(" option[value='"+details.addedId+"']").length ==0) {
			if(details.parentDomID == "failureExternalURLId") {
				hm.util.insertSelectValue(details.addedId, details.addedName, failureExternalURLId, false, true);
			} else {
				hm.util.insertSelectValue(details.addedId, details.addedName, failureExternalURLId, false, false);
			}
		}
	}
}

function submitAction(operation) {
	if(operation=='create'||operation=='update'||operation.indexOf('create')!=-1||operation.indexOf('update')!=-1){
		var vluIpAddr=$("#ipClassifierTagContainer").find("input#ip_ipAddress").val();
		var vluStartIp=$("#ipClassifierTagContainer").find("input#ip_startIp").val();
		var vluHostName=$("#ipClassifierTagContainer").find("input#ip_hostName").val();
		var vluNetwork=$("#ipClassifierTagContainer").find("input#ip_network").val();
		var vluWildcard=$("#ipClassifierTagContainer").find("input#ip_ipWildcard").val();
		var vluWebPage=$("#ipClassifierTagContainer").find("input#url_webPage").val();
		var isTagVisable=$("#ipClassifierTagContainer").is(':visible');
		if((operation=='create'||operation=='update')&&
				((vluIpAddr!=null&&vluIpAddr!="")
				||(vluStartIp!=null&&vluStartIp!="")
				||(vluHostName!=null&&vluHostName!="")
				||(vluNetwork!=null&&vluNetwork!="")
				||(vluWildcard!=null&&vluWildcard!="")
				||(vluWebPage!=null&&vluWebPage!=""))&&isTagVisable){
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
    		var url = "<s:url action='classifierTag' includeParams='none' />" + "?tagKey=4&operation=viewAll"+ "&ignore=" + new Date().getTime(); 	
			operName=operation;
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succView}, null);
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
		var url = "<s:url action='classifierTag' includeParams='none' />" + "?tagKey=4&operation=viewAll"+ "&ignore=" + new Date().getTime(); 	
		operName=operation;
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succView}, null);
		return;
	}
	continueSubmit(operation);
}
function continueSubmit(operation) {
   if (validate(operation)) {
        if (operation == 'create'+'<s:property value="lstForward"/>'
            || operation == 'update' + '<s:property value="lstForward"/>') {
            showProcessing();
        }
        
        <s:if test="%{jsonMode}">
            <s:if test="%{contentShownInDlg && vpnServiceDnsIpFlag}">
                if ('cancelvpnService' == operation) {
                    top.closeIFrameDialog();
                }else if('createvpnService' == operation || 'updatevpnService' == operation){
                    saveIpAddressJsonDlg(operation);
                }else{
                    document.forms[formName].operation.value = operation;
                    document.forms[formName].submit();
                }
            </s:if>
            <s:else>
                if ('cancelhiveAp2' == operation || 'cancelhiveAp' == operation) {
                    <s:if test="%{parentIframeOpenFlg}">
                        document.forms[formName].operation.value = operation;
                        document.forms[formName].submit();
                    </s:if>
                    <s:else>
                        top.closeIFrameDialog();
                    </s:else>
                } else if ('cancelcwpWGIp' == operation
                        || 'cancelhiveApMulti' == operation 
                        || 'cancelhiveApMulti2' == operation
                        || 'cancelcwpSuccessURL' == operation
                        || 'cancelcwpFailureURL' == operation) {
                    //top.closeIFrameDialog();
                	 <s:if test="%{parentIframeOpenFlg}">
	                     document.forms[formName].operation.value = operation;
	                     document.forms[formName].submit();
	                 </s:if>
	                 <s:else>
	                     top.closeIFrameDialog();
	                 </s:else>
                } else if ('createhiveAp' == operation 
                            || 'updatehiveAp' == operation
                            || 'createhiveAp2' == operation 
                            || 'updatehiveAp2' == operation) {
                    <s:if test="%{parentIframeOpenFlg}">
                        document.forms[formName].operation.value = operation;
                        document.forms[formName].submit();
                    </s:if>
                    <s:else>
                        saveIpAddressJsonDlg(operation);
                    </s:else>
                } else if('createcwpWGIp' == operation 
                            || 'updatecwpWGIp' == operation
                            || 'createhiveApMulti' == operation
                            || 'updatehiveApMulti' == operation
                            || 'createhiveApMulti2' == operation
                            || 'updatehiveApMulti2' == operation
                            || 'createcwpSuccessURL' == operation
                            || 'updatecwpSuccessURL' == operation
                            || 'createcwpFailureURL' == operation
                            || 'updatecwpFailureURL' == operation){
                	<s:if test="%{parentIframeOpenFlg}">
	                    document.forms[formName].operation.value = operation;
	                    document.forms[formName].submit();
	                </s:if>
	                <s:else>
	                    saveIpAddressJsonDlg(operation);
	                </s:else>
                } else{
                    document.forms[formName].operation.value = operation;
                    document.forms[formName].submit();
                }
            </s:else>
        </s:if>
        <s:else>
            document.forms[formName].operation.value = operation;
            document.forms[formName].submit();
        </s:else>
    }
}
function succView(o) {	
	eval("var detailResult = " + o.responseText);
	if(detailResult.succ) {		
		ruleIndex=detailResult.items.split('|');		
		if(ruleIndex!=''){
			 $("input#ahTempClassifierConfilictBtn").click();
			 return;
		} else{
			continueSubmit(operName);
		}
	}
	
}
function saveAllItem() {
	var confilictVal=$("input#ahTempClassfierConfilict").val();
	if(confilictVal == 'true'){
		   for(var i=0; i<ruleIndex.length; i++) {	
				var tempIndex=parseInt(ruleIndex[i])+1;
				$("li.item:nth-child("+tempIndex+")").addClass('needAlertInRed');
		}
	}
	else{
		continueSubmit(operName);
	 }
}		

function filterDisableElements(eleName) {	
    var elements= $("[name="+eleName+"]");
	var array = new Array();
	for(var index = 0; index < elements.length; index++) {
		if(!elements[index].disabled) {
			//console.debug(index+", "+elements[index].id);
			array.push(elements[index]);
		}
	}
	return array;
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_IP_ADDRESS%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>')
	{
		return true;
	}

	if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'create') {
		var name = document.getElementById(formName + "_dataSource_addressName");
	    if (contains(name.value, "\\", true )) {
			hm.util.reportFieldError(name, "Object Name cannot contain '\\'.");
			return false;
		}
		if (contains(name.value, "\/", true )) {
			hm.util.reportFieldError(name, "Object Name cannot contain '\/'.");
			return false;
		} 
		if (!checkNameValid(name, name, '<s:text name="config.ipAddress.addressName" />')) {
        	return false;
    	}	 
	}

    var table = document.getElementById("checkAll");
    if (operation == 'addIPAddress') {
    	var ipAddresses = filterDisableElements('ipAddresses');
		var netmasks = filterDisableElements('netmasks');
		// check netmask fields
		// ip address and host name object does not have netmask
		if (ipFlag == 1 || ipFlag == 2) {
			//if (null != netmasks && netmasks.length > 0) {
    		//	hm.util.reportFieldError(table, '<s:text name="error.ObjectTypeErrorExists"></s:text>');
        	//	return false;
    		//}
    	// network and wildcard must contain netmask
		} else {
			if (ipAddresses.length != netmasks.length) {
    			hm.util.reportFieldError(table, '<s:text name="error.ObjectTypeErrorExists"></s:text>');
        		return false;
    		}
		}
    	if (ipFlag == 1) {
	    	if(!checkIpAddress(document.getElementById(formName + "_ipAddress"), '<s:text name="config.ipAddress.ipAddress" />')) {
				return false;
			}
    	} else if (ipFlag == 2){
    		var hostName = document.getElementById(formName + "_hostName");
    		if (!checkNameValid(hostName, table, 'Host Name')) {
	        	return false;
	    	}
	    	if (hm.util.validateIpAddress(hostName.value)) {
				hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>Host Name</s:param></s:text>');
				hostName.focus();
				return false;
			}
    	} else if (ipFlag == 3) {
    		if(!checkIpAddress(document.getElementById(formName + "_ipNetwork"), '<s:text name="config.ipAddress.ipAddress" />')
				|| !checkNetmask(document.getElementById(formName + "_netmask"), '<s:text name="config.ipAddress.netmask" />')) {
				return false;
			}
    	} else if (ipFlag == 4) {
    		if(!checkIpAddress(document.getElementById(formName + "_ipWildcard"), '<s:text name="config.ipAddress.ipAddress" />')
				|| !checkIpAddressFormat(document.getElementById(formName + "_wildcard"), '<s:text name="config.ipAddress.wildcard" />')) {
				return false;
			}
    	} else {
    		var startIp = document.getElementById(formName + "_startIp");
    		var endIp = document.getElementById(formName + "_endIp");
    		if(!checkIpAddress(startIp, '<s:text name="config.ipAddress.ipAddress.start" />')
				|| !checkIpAddress(endIp, '<s:text name="config.ipAddress.ipAddress.end" />')) {
				return false;
			}
			if (!hm.util.compareIpAddress(startIp.value, endIp.value)) {
				hm.util.reportFieldError(table, '<s:text name="error.notLargerThan"><s:param><s:text name="config.ipAddress.ipAddress.start" /></s:param><s:param><s:text name="config.ipAddress.ipAddress.end" /></s:param></s:text>');
				startIp.focus();
				return false;
			}
    	}
		 var hidename = document.getElementById("hideName");
		 var hidelocation = document.getElementById("hideLocation");
		 var hidehive = document.getElementById("hideHiveAP");
		 var hideclass = document.getElementById("hideClassifier");
		 if(hidename.style.display == "none") {
		 	if(hidelocation.style.display == "") {
		 		var location = document.forms[formName].locationId;
		 		if(location.value == -1) {
		 			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.location" /></s:param></s:text>');
	        		location.focus();
	        		return false;
		 		}
		 	}
		 	if(hidehive.style.display == "") {
		 		var typename = document.getElementById(formName + "_typeName");

				if(typename.value.length == 0) {
					hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.hiveAP" /></s:param></s:text>');
	        		typename.focus();
	        		return false;
				} else if (typename.value.indexOf(' ') > -1) {
	        		hm.util.reportFieldError(table, '<s:text name="error.name.containsBlank"><s:param><s:text name="config.ipAddress.hiveAP" /></s:param></s:text>');
	        		typename.focus();
	        		return false;
	    		}
		 	}
		 	if(hideclass.style.display == "") {
		 		var tag1 = document.getElementById(formName + "_tag1");
		 		var tag2 = document.getElementById(formName + "_tag2");
		 		var tag3 = document.getElementById(formName + "_tag3");
		 		// check tag value
		 		if(tag1.value.length == 0 && tag2.value.length == 0 && tag3.value.length == 0){
					hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.classifier.tag" /></s:param></s:text>');
		 			tag1.focus();
		 			return false;
				}
		 	}
		 }
    }

    if (operation == 'removeIPAddress' || operation == 'removeIPAddressNone') {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.ipAddress.ipAddress" /></s:param></s:text>');
			return false;
		}
	}

    if(operation == 'create'+'<s:property value="lstForward"/>'
    	|| operation == 'update' + '<s:property value="lstForward"/>'
    	|| operation == 'create'
    	|| operation == 'update') {
	   	var ipAddresses = filterDisableElements('ipAddresses');
		var netmasks = filterDisableElements('netmasks');
		var table = document.getElementById("checkAll");
		if(ipAddresses.length ==0 ||ipAddresses[0].value.length == 0) {
			if(ipFlag == 2){
				hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.hostName.title" /></s:param></s:text>');
			}else if(ipFlag == 6){
				hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="glasgow_08.config.cwp.ipAddress.webpage.title" /></s:param></s:text>');
			}else{
				hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.ipAddress.title" /></s:param></s:text>');
			}
       		table.focus();
       		return false;
		}
		// ip address and host name object does not have netmask
		if (ipFlag == 1 || ipFlag == 2 || ipFlag == 6) {
			if (null != netmasks && netmasks.length > 0) {
    			//hm.util.reportFieldError(table, '<s:text name="error.ObjectTypeErrorExists"></s:text>');
        		//return false;
    		}
    	// network and wildcard must contain netmask
		} else {
			if (ipAddresses.length != netmasks.length) {
    			hm.util.reportFieldError(table, '<s:text name="error.ObjectTypeErrorExists"></s:text>');
        		return false;
    		}
		}
		for(var i = 0; i < ipAddresses.length; i ++) {
			if(ipAddresses[i].value==null||ipAddresses[i].value==""){
				if(ipAddresses[i].id=='global_address'){
					hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.ipAddress.title" /></s:param></s:text>');
					table.focus();
					return false;
					}
				else
					continue;
			}
			if (ipFlag == 1) {
				if(!checkIpAddress(ipAddresses[i], '<s:text name="config.ipAddress.ipAddress" />'+' in '+(i+1)+' row')) {
					return false;
				}
			} else if (ipFlag == 2) {
				var hostTitle = 'Host Name in '+(i+1)+' row';
				if (contains(ipAddresses[i].value, "\\", true )) {
					hm.util.reportFieldError(table, hostTitle+" cannot contain '\\'.");
					return false;
				}
				if (contains(ipAddresses[i].value, "\/", true )) {
					hm.util.reportFieldError(table, hostTitle+" cannot contain '\/'.");
					return false;
				} 
				if (!checkNameValid(ipAddresses[i], table, hostTitle)) {
		        	return false;
		    	}
		    	if (hm.util.validateIpAddress(ipAddresses[i].value)) {
					hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+hostTitle+'</s:param></s:text>');
					ipAddresses[i].focus();
					return false;
				}
			} else if(ipFlag == 6){
				var value = ipAddresses[i].value;
				if ( !(value.indexOf('http') == 0
						|| value.indexOf('https') == 0)
						|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){ 
				    	hm.util.reportFieldError(table, 
					    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
				    	ipAddresses[i].focus();
						return false;
					
				    } 
			} else if (ipFlag == 3) {
				if(!checkIpAddress(ipAddresses[i], '<s:text name="config.ipAddress.ipAddress" />'+' in '+(i+1)+' row')
					|| !checkNetmask(netmasks[i], '<s:text name="config.ipAddress.netmask" />'+' in '+(i+1)+' row')) {
					return false;
				}
			} else if (ipFlag == 4) {
				if(!checkIpAddress(ipAddresses[i], '<s:text name="config.ipAddress.ipAddress" />'+' in '+(i+1)+' row')
					|| !checkIpAddressFormat(netmasks[i], '<s:text name="config.ipAddress.wildcard" />'+' in '+(i+1)+' row')) {
					return false;
				}
			} else {
				if(!checkIpAddress(ipAddresses[i], '<s:text name="config.ipAddress.ipAddress.start" />'+' in '+(i+1)+' row')
					|| !checkIpAddress(netmasks[i], '<s:text name="config.ipAddress.ipAddress.end" />'+' in '+(i+1)+' row')) {
					return false;
				}
				if (!hm.util.compareIpAddress(ipAddresses[i].value, netmasks[i].value)) {
					hm.util.reportFieldError(table, '<s:text name="error.notLargerThan"><s:param><s:text name="config.ipAddress.ipAddress.start" /></s:param><s:param><s:text name="config.ipAddress.ipAddress.end" /></s:param></s:text>');
					ipAddresses[i].focus();
					return false;
				}
			}
		}
    }

	return true;
}

function checkNameValid(name, foc, title) {
	var message = hm.util.validateName(name.value, title);
   	if (message != null) {
   		hm.util.reportFieldError(foc, message);
       	name.focus();
       	return false;
   	}
   	return true;
}

function checkIpAddress(ip, title) {
	var table = document.getElementById("checkAll");
	if (ip.value.length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        ip.focus();
        return false;
    } else if (!hm.util.validateIpAddress(ip.value)) {
		hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		ip.focus();
		return false;
	}
	return true;
}

function checkIpAddressFormat(ip, title) {
	var table = document.getElementById("checkAll");
	if (ip.value.length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        ip.focus();
        return false;
    } else if (!hm.util.validateIpAddressFormat(ip.value)) {
		hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		ip.focus();
		return false;
	}
	return true;
}

function checkNetmask(mask, title) {
	var table = document.getElementById("checkAll");
	if (mask.value.length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        mask.focus();
        return false;
    } else if (!hm.util.validateMask(mask.value)) {
		hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		mask.focus();
		return false;
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

function toggleCheckAllRules(cb) {
	var cbs = document.getElementsByName('ruleIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function changeType(type) {
	var hidename = document.getElementById("hideName");
	var hidelocation = document.getElementById("hideLocation");
	var hidehive = document.getElementById("hideHiveAP");
	var hideclass = document.getElementById("hideClassifier");

	switch(parseInt(type)) {
		case USETYPE_GLOBAL:
			hidename.style.display= "";
	    	hidelocation.style.display="none";
			hidehive.style.display="none";
			hideclass.style.display="none";
			break;
		case USETYPE_MAP:
			hidename.style.display= "none";
	    	hidelocation.style.display="";
			hidehive.style.display="none";
			hideclass.style.display="none";
			break;
		case USETYPE_HIVEAP:
			hidename.style.display= "none";
	    	hidelocation.style.display="none";
			hidehive.style.display="";
			hideclass.style.display="none";
			break;
		case USETYPE_CLASSIFIER:
			hidename.style.display= "none";
	    	hidelocation.style.display="none";
			hidehive.style.display="none";
			hideclass.style.display="";
			break;
		default:
			break;
	}
}

function setToAddress(typeFlag) {
	diffIpFlag=true;
    beforeIpFlag=ipFlag;
	var ipChecked = typeFlag == 1;
	var nameChecked = typeFlag == 2;
	var netChecked = typeFlag == 3;
	var cardChecked = typeFlag == 4;
	var rangeChecked = typeFlag == 5;
	//document.getElementById("address").style.display = ipChecked ? "" : "none";
	//document.getElementById("hostName").style.display = nameChecked ? "" : "none";
	//document.getElementById("network").style.display = netChecked ? "" : "none";
	//document.getElementById("wildcard").style.display = cardChecked ? "" : "none";
	//document.getElementById("range").style.display = rangeChecked ? "" : "none";
	ipFlag = typeFlag;
	if(beforeIpFlag==typeFlag){
		diffIpFlag=false;
	}
	
	if(typeFlag == 2){
		$("#hostNameTitle").show();
		$("#ipAddressTitle").hide();
		$("#webPageTitle").hide();
	}else if(typeFlag == 6){
		$("#hostNameTitle").hide();
		$("#ipAddressTitle").hide();
		$("#webPageTitle").show();
	}else{
		$("#ipAddressTitle").show();
		$("#hostNameTitle").hide();
		$("#webPageTitle").hide();
	}
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
<div id="content"><s:form action="ipAddress" id="ipAddress">
<s:hidden name="parentDomID" />
<s:hidden name="ipType" />
	<s:if test="%{jsonMode}">
		<s:hidden name="operation" />
		<s:hidden name="parentIframeOpenFlg" />
		<s:hidden name="contentShowType" />
		<s:hidden name="vpnServiceDnsIpFlag" />
		<s:hidden name="id" />
		<s:hidden name="jsonMode" />
	</s:if>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{jsonMode}">
			<tr>
				<td style="padding:10px 10px 10px 10px">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td align="left">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-IP_Objects_Hostnames.png" includeParams="none"/>"
									width="40" height="40" alt="" class="dblk" />
								</td>
								<td class="dialogPanelTitle">
									<s:if test="%{dataSource.id == null}">
										<s:text name="config.title.ipAddress"/>
									</s:if>
									<s:else>
										<s:text name="config.title.ipAddress.edit"/>
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
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
								<td width="20px">&nbsp;</td>
								<td class="npcButton">
								<s:if test="%{dataSource.id == null}">
									<s:if test="%{writeDisabled == 'disabled'}">
										&nbsp;
									</s:if>
									<s:else>
										<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a>
									</s:else>
								</s:if>
								<s:else>
									<s:if test="%{updateDisabled == 'disabled'}">
										&nbsp;
									</s:if>
									<s:else>
										<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a>
									</s:else>
								</s:else>
								</td>
							</tr>
						</table>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</s:if>
		<s:else>
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
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
						class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_IP_ADDRESS%>');">
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
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode}">
				<table cellspacing="0" cellpadding="0" border="0" width="920">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="920">
			</s:else>
				<tr>
					<td style="padding: 6px 4px 5px 4px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td colspan="4">
								<table cellspacing="0" cellpadding="0" border="0">
									<tr id="ipGroups">
										<td width="100px" style="padding-left:5px"><s:radio disabled="%{disabledName}"
											label="Gender" name="radioIpOrName" list="#{'address':'IP Address'}"
											onclick="setToAddress(1);" value="%{radioIpOrName}" /></td>
										<td width="100px"><s:radio label="Gender" disabled="%{disabledName}"
											name="radioIpOrName" list="#{'range':'IP Range'}"
											onclick="setToAddress(5);" value="%{radioIpOrName}" /></td>
										<td width="100px"><s:radio label="Gender" disabled="%{disabledName}"
											name="radioIpOrName" list="#{'name':'Host Name'}"
											onclick="setToAddress(2);" value="%{radioIpOrName}" /></td>
										<td width="100px"><s:radio label="Gender" disabled="%{disabledName}"
											name="radioIpOrName" list="#{'network':'Network'}"
											onclick="setToAddress(3);" value="%{radioIpOrName}" /></td>
										<td width="100px"><s:radio label="Gender" disabled="%{disabledName}"
											name="radioIpOrName" list="#{'wildcard':'Wildcard'}"
											onclick="setToAddress(4);" value="%{radioIpOrName}" /></td>
										<td width="230px"><s:radio label="Gender" disabled="%{disabledName}"
											name="radioIpOrName" list="#{'webPage':'Web Page (Captive Web Portals only)'}"
											onclick="setToAddress(6);" value="%{radioIpOrName}" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="3"></td>
						</tr>
						<tr>
							<td colspan="4">
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="labelT1" width="100"><label><s:text
											name="config.ipAddress.addressName" /><font color="red"><s:text name="*"/></font>
											</label></td>
										<td><s:textfield size="24" name="dataSource.addressName"
											maxlength="%{addressNameLength}" disabled="%{disabledName}"
											onkeypress="return hm.util.keyPressPermit(event,'name');" />
											<s:text name="config.ssid.ssidName_range" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td style="padding:6px 0px 6px 0px" colspan="4">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td class="sepLine"><img
										src="<s:url value="/images/spacer.gif"/>" height="1"
										class="dblk" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td colspan="4" style="padding:4px 0px 4px 4px;" valign="top">
								<table cellspacing="0" cellpadding="0" border="0" class="embedded" width="100%">
									<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
										<td colspan="7" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="New"
													class="button" id="newItemBtn"
													<s:property value="updateDisabled" />></td>
												<td><input type="button" name="ignore" value="Reset Order"
													class="button" id="resetButton" <s:property value="updateDisabled" />></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
										<td colspan="7" style="padding-bottom: 2px;">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
													class="button" id="addItemBtn"></td>												
												<td><input type="button" name="ignore" value="Cancel"
													class="button" id="cancelItemBtn"></td>
												<td><input type="button" name="ignore" value="Reset Order"
													class="button" id="resetButton"></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr id="headerSection">										
										<th align="left" style="padding-left: 0;" width="10"><input
											type="checkbox" id="checkAll" onClick="toggleCheckAllRules(this);" ></th>
										<th align="left" width="205" id="hostNameTitle" style="display:<s:property value="%{hostNameHeaderTitleStyle}"/>"><s:text name="config.ipAddress.hostName.title" /></th>
										<th align="left" width="205" id="webPageTitle" style="display:<s:property value="%{webPageHeaderTitleStyle}"/>"><s:text name="glasgow_08.config.cwp.ipAddress.webpage.title" /></th>
										<th align="left" width="205" id="ipAddressTitle" style="display:<s:property value="%{ipEntryHeaderTitleStyle}"/>"><s:text name="config.ipAddress.ipAddress.title" /></th>
										<th align="left" width="100"><s:text name="config.ipAddress.type" /></th>
										<th align="left" width="250"><s:text name="config.ipAddress.value" /></th>
										<th align="left" width="150"><s:text name="config.ipAddress.description" /></th>
									</tr>
                                    <tr id="ipClassifierTagContainer"></tr>
                                    <tr id="ipItemsRow"/>
									<s:if test="%{gridCount.size > 0}">
										<s:generator separator="," val="%{' '}" count="%{gridCount}">
											<s:iterator>
												<tr>
													<td class="list" colspan="6">&nbsp;</td>
												</tr>
											</s:iterator>
										</s:generator>
									</s:if>																
									<tr>
											<td class="noteInfo" colspan="8"><s:text name="config.common.classifier.tag.note"/></td>
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
<div id="classfierHelpWholePanel" style="display: none;height: 160px">
	<div id="classfierHelpPanel" style="overflow: auto;word-wrap: break-word; word-break: normal;"></div>	
	<input id="classfierHelpOkButton" type="button" value="OK" style="margin-left: 100px;">
</div>
<div id="classfierConfilictPanel" style="display: none;height: 80px">
	<table width="140" border="0" >
		<tr>
		  <td align="left" ><s:text name="home.hmSettings.devicetag.confilctalert" /></td>
		</tr>
        <tr>
           <td align="center" >
              <input type="button" value="OK" id="cancelInClassfierConfilictPanel">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <input type="button" value="Fix" id="fixInClassfierConfilictPanel">
           </td>
        </tr>
     </table>
</div>
<div id="classfierPopupWholePanel" style="display: none;height: 100px">
	<div id="classfierPopupPanelTitle"></div>
	<div id="classfierPopupPanel" style="overflow: auto;"></div>	
	<input id="classfierPopupPanelOkButton" type="button" value="OK" style="margin-left: 180px; margin-top: 10px;">
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
<input type="button" value="AH Panel" id="ahTempClassifierMatchBtn">
<input type="button" value="Submit" id="ahTempClassifierEditBtn">
<input type="text" value="Submit" id="ahTempClassifierIndexValue" value="">
<input type="text" value="Submit" id="ahTempClassifierItemType" value="">
<input type="text" value="Submit" id="ahTempClassfierConfilict" value="">
<input type="button" value="Submit" id="ahTempClassifierConfilictBtn">
<input type="button" value="AH Panel" id="ahTempClassifierHelpBtn">
<input type="button" value="Submit" id="ahTempClassifierNewRuleBtn">

</div>
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
#ipClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 5px;	
}
#ipClassifierTagContainer td#tagCtner li.tag-choice{
	padding-left: 75px;	
}
.ui-classifier-items ul.itemContainer .item span.pointer{
	white-space:normal;
}
 
.ui-menu .ui-menu-item a {
 	width: 120px;
 }
 
#resetButton {
 	width: 90px;
 } 
</style>
<script>!window.jQuery && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script>!window.jQuery.ui && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script src="<s:url value="/js/widget/classifiertag/ct-debug.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/dialog/panel.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
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
    var DOM = YAHOO.util.Dom;
    var aerohivePanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierPopupWholePanel', {width: 200, closeIcon: 'images/cancel.png'});
    DOM.get('ahTempClassifierMatchBtn').onclick = function(){
    	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
    	aerohivePanel.openDialog();
    };    
    DOM.get('classfierPopupPanelOkButton').onclick = function(){ 
    	aerohivePanel.closeDialog();
    };    
    var helpPanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierHelpWholePanel', {width: 420, closeIcon: 'images/cancel.png'});
	DOM.get('ahTempClassifierHelpBtn').onclick = function(){    	
    	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
    	helpPanel.openDialog();
    };
 	DOM.get('classfierHelpOkButton').onclick = function(){ 
    	helpPanel.closeDialog();
    };
    
    var confilictPanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierConfilictPanel', {width: 140, closeIcon: 'images/cancel.png'});
    DOM.get('ahTempClassifierConfilictBtn').onclick = function(){
    	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
    	confilictPanel.openDialog();
    };
    DOM.get('cancelInClassfierConfilictPanel').onclick = function(){ 
    	$("input#ahTempClassfierConfilict").attr("value","false");
    	saveAllItem();
    	confilictPanel.closeDialog();
    };
    DOM.get('fixInClassfierConfilictPanel').onclick = function(){ 
    	$("input#ahTempClassfierConfilict").attr("value","true");
    	saveAllItem();
    	confilictPanel.closeDialog();
    };
    
    var editPanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierEditPanel', {width: 270, closeIcon: 'images/cancel.png'});
    DOM.get('ahTempClassifierEditBtn').onclick = function(){   
    	$("#editClassifierTagContainer").show();
    	editPanel.openDialog();
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
	    $.getJSON("classifierTag.action", 
					{tagKey: 4, operation: 'edit', selectedItems: idx,tag1:stag1 ,tag2:stag2 ,tag3:stag3 ,typeName:dvcName,locationId:topoName}, 
				function(data) {
					if(data.succ) {	
						//just set the edited value back to jsp page
						var htmlIdx=parseInt(idx)+1;
						var targetString=data.value;
						$("#ipItemsRow").find("li.item:nth-child("+htmlIdx+")").find("span:nth-child(4)").html(targetString);		
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
    setTimeout(function(){		
    	 var ctt=$("#editClassifierTagContainer").classifierTag(
    				{
    					key: 4,
    					types:  [ {key: 2, text: 'Topology Node'},null,null],
    					itemWidths:{tagValue: 120 },
    					widgetWidth: {desc: 0},
    					valueProps: null,
    					itemEditable: false,
    					describable: false,	
    					zindexDisplay: true,
    					needShowTagFields: true,
    					needShowDeviceNames: true,
    					needShowTopology: true
    				}
    		); 
    }, 1000);
});

function checkAddIPObject() {
	var table = document.getElementById("checkAll");
	var ip;
	if(ipFlag==1){
		ip=$('#ip_ipAddress').val();				
	}	
	if(ipFlag==3){
		ip=$('#ip_network').val();		
	}
	if(ipFlag==4){
		ip=$('#ip_ipWildcard').val();	
	}
	if(ipFlag==5){
		ip=$('#ip_startIp').val();		
	}
	if(ipFlag==2){
		ip=$('#ip_hostName').val();		
	}
	if(ipFlag==6){
		ip=$('#url_webPage').val();		
	}
	if (ip.length == 0) {
		if(ipFlag == 2){
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.hostName.title" /></s:param></s:text>');
		}else if(ipFlag == 6){
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="glasgow_08.config.cwp.ipAddress.webpage.title" /></s:param></s:text>');
		}else{
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.ipAddress.title" /></s:param></s:text>');
		}
		$('#ip_ipAddress').focus();
		return false;
	}
	if(ipFlag==2){
		if (contains(ip, "\\", true )) {
			hm.util.reportFieldError(table, "Host Name cannot contain '\\'.");
			return false;
		}
		if (contains(ip, "\/", true )) {
			hm.util.reportFieldError(table, "Host Name cannot contain '\/'.");
			return false;
		} 
		return true;
	}
	if(ipFlag==6){
		if ( !(ip.indexOf('http') == 0
				|| ip.indexOf('https') == 0)
				|| ip.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ) {
			hm.util.reportFieldError(table, "Please enter a valid Web Page");
			return false;
		}
		 
		return true;
	}else{
		if (!hm.util.validateIpAddress(ip)) {
			hm.util.reportFieldError(table, 'IP Address format is invalid.');
			$('#ip_ipAddress').focus();
			return false;
	}	
	}
	
	return true;
};

function initClassifierWidget() {
var props = [
	             {
	            	 flag: {type: 1, name: 'address'}, //IP Address
	            	 validateFn: function(el){return checkAddIPObject();},
	            	 <s:if test="%{dataSource.typeFlag == 1}">
	            	 selected: true,
	            	 </s:if>
	            	 desc: '<br>&nbsp;&nbsp;&nbsp;IP Address',
	            	 items: [{id: 'ip_ipAddress', elType: 'input', field: 'ipAddress',
	            		     onkeypress: 'return hm.util.keyPressPermit(event,\'ip\');',
	            		     maxlength: 15, size: 10}]
	             },
	             {
	            	 flag: {type:5, name: 'range'}, // IP Range
	            	 <s:if test="%{dataSource.typeFlag == 5}">
	            	 selected: true,
	            	 </s:if>
	            	 validateFn: function(el){return checkAddIPObject();},
	            	 concatenateHTML: "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;",
	            	 desc: '<br>&nbsp;&nbsp;&nbsp;Start IP Address&nbsp;&nbsp;End IP Address',
	            	 items: [{id: 'ip_startIp', elType: 'input',  field: 'ipAddress',
			            	 onkeypress: 'return hm.util.keyPressPermit(event,\'ip\');', 
			            	 maxlength: 15, size: 10},
			            	 {id: 'ip_endIp', elType: 'input',  field: 'netmask',
	                             onkeypress: 'return hm.util.keyPressPermit(event,\'ip\');', 
	                             maxlength: 15, size: 10}]
	             },
	             {
	            	 flag: {type:2, name: 'name'}, // Host Name
	            	 <s:if test="%{dataSource.typeFlag == 2}">
	            	 selected: true,
	            	 </s:if>
	            	 validateFn: function(el){return checkAddIPObject();},
	            	 desc: '<br>Host Name&nbsp;(1-32 characters)',
	            	 items: [{id: 'ip_hostName', elType: 'input', field: 'ipAddress',
			            	 onkeypress: 'return hm.util.keyPressPermit(event,\'name\');', 
			            	 maxlength: 32, size:24}]
	             },
	             {
	            	 flag: {type: 3, name: 'network'}, // Network
	            	 <s:if test="%{dataSource.typeFlag == 3}">
	            	 selected: true,
	            	 </s:if>
	            	 validateFn: function(el){return checkAddIPObject();},
	            	 concatenateHTML: "&nbsp;&nbsp;&nbsp;&nbsp;",
	            	 desc: '<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;IP Address&nbsp;&nbsp;Netmask',
	            	 items: [{id: 'ip_network', elType: 'input', field: 'ipAddress',
			            	 onkeypress: 'return hm.util.keyPressPermit(event,\'ip\');', 
			            	 maxlength: 15, size: 10},
			            	 {id: 'ip_netmask', elType: 'input', field: 'netmask',
	                             onkeypress: 'return hm.util.keyPressPermit(event,\'ip\');', 
	                             maxlength: 15, size: 10}]
	             },
	             {
	            	 flag: {type: 4, name: 'wildcard'}, // Wildcard
	            	 <s:if test="%{dataSource.typeFlag == 4}">
	            	 selected: true,
	            	 </s:if>
	            	 validateFn: function(el){return checkAddIPObject();},
	            	 concatenateHTML: "&nbsp;&nbsp;&nbsp;&nbsp;",
	            	 desc: '<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;IP Address&nbsp;&nbsp;Wildcard',
	            	 items: [{id: 'ip_ipWildcard', elType: 'input', field: 'ipAddress',
			            	 onkeypress: 'return hm.util.keyPressPermit(event,\'ip\');', 
			            	 maxlength: 15, size: 10},
			            	 {id: 'ip_wildcard', elType: 'input', field: 'netmask',
	                             onkeypress: 'return hm.util.keyPressPermit(event,\'ip\');', 
	                             maxlength: 15, size: 10}]
	             },
	             {
	            	 flag: {type:6, name: 'webPage'}, // web page
	            	 <s:if test="%{dataSource.typeFlag == 6}">
	            	 selected: true,
	            	 </s:if>
	            	 validateFn: function(el){return checkAddIPObject();},
	            	 desc: '<br>Web Page&nbsp;(1-256 characters, <br>and must begin with "http://" or "https://")',
	            	 items: [{id: 'url_webPage', elType: 'input', field: 'ipAddress',
			            	/*  onkeypress: 'return hm.util.keyPressPermit(event,\'name\');',  */
			            	 maxlength: 256, size:24}]
	             },
	      ];
	       var ct = $("#ipClassifierTagContainer").classifierTag(
                {
                    key: 4,
                    valueProps: props,
                    widgetWidth: {desc: 150},
                    globalProps: [{flagType: 1, id: 'global_address'}, 
                                  {flagType: 5, id: 'global_start|global_end'},
                                  {flagType: 2, id: 'global_hostname'},
                                  {flagType: 3, id: 'global_network|global_netmask'},
                                  {flagType: 4, id: 'global_ipWildCard|global_wildCard'},
                                  {flagType: 6, id: 'global_webPage'},],
                    itemTableId: 'ipItemsRow',
                   // itemWidths: {value: 240}, 
                    itemWidths: { value: 245, type: 100, tagValue: 285, desc: 150},
                   
                    itemEditable: {value: true, valueName: 'ipAddresses|netmasks', desc: true, descName: 'descriptions'}
                });   
        var domainId=<s:property value="domainId" />;		
        if(<s:property value="%{dataSource.items.size()==0}"/>) {
            $("#ipClassifierTagContainer").classifierTag('addEmptyGlobalItem');
        } else {
        	<s:iterator value="%{dataSource.items}" status="status">
            var item = {};
            item.id = <s:property value="%{#status.index}"/>;  
            <s:if test="%{dataSource.typeFlag == 1||dataSource.typeFlag == 2 || dataSource.typeFlag == 6}">			
			    item.value = '<s:property value="%{ipAddress}" escapeHtml="false" />';
		   </s:if>
		   <s:else>
		 		  item.value = '<s:property value="%{ipAddress}"/>|<s:property value="%{netmask}"/>';
		   </s:else>		           
            item.type = <s:property value="%{type}"/>;            
            item.tagValue = '<s:property value="getTagValue(domainId)"/>';	
            item.desc = '<s:property value="%{descriptionStr}" escapeHtml="false"/>';
            if(item.type == 1) {
                $("#ipClassifierTagContainer").classifierTag('addItem', item, -1);
            } else {
                $("#ipClassifierTagContainer").classifierTag('addItem', item);
            }
            </s:iterator> 
        }
        $("#ipClassifierTagContainer").hide(); 
	    $('tr#ipGroups').delegate('input[type="radio"]', 'click', function() {
    if(diffIpFlag){
    	$("#ipClassifierTagContainer").classifierTag('showValueByFlag', $(this)[0].value);
    }
    });
    $('input#newItemBtn').click(function() {
        hm.util.hide('newButton');
        hm.util.show('createButton');
        hm.util.show('ipClassifierTagContainer');
    });
    $('input#cancelItemBtn').click(function() {
        hm.util.hide('createButton');
        hm.util.show('newButton');
        hm.util.hide('ipClassifierTagContainer');     
    });
    $('input#addItemBtn').click(function() {
        $("#ipClassifierTagContainer").classifierTag('saveItem');
    });
    $('input#resetButton').click(function() {
        $("#ipClassifierTagContainer").classifierTag('resetOrder');
    }); 
}
</script>
