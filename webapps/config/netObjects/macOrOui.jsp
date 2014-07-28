<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.SingleTableItem"%>
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/ct.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/panel.css" includeParams="none"/>?v=<s:property value="verParam" />" />  
<script>
var formName = 'macAddress';
var operName = 'create';
var ruleIndex= '';
var USETYPE_GLOBAL = <%=SingleTableItem.TYPE_GLOBAL%>;
var USETYPE_MAP = <%=SingleTableItem.TYPE_MAP%>;
var USETYPE_HIVEAP = <%=SingleTableItem.TYPE_HIVEAPNAME%>;
var USETYPE_CLASSIFIER = <%=SingleTableItem.TYPE_CLASSIFIER%>;
var macFlag = <s:property value="%{dataSource.typeFlag}"/>;
var beforeMacFlag =macFlag;
var diffMacFlag =true;

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_macOrOuiName").disabled == false) {
		document.getElementById(formName + "_dataSource_macOrOuiName").focus();
	}
	<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(850, 450);
		}
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
	
	<s:if test="%{macType == 'macFilter'}">
		document.getElementById(formName + "_radioMacOrOuimacRange").disabled = true;
	</s:if>
}

function submitAction(operation) {	
	if(operation=='create'||operation=='update'||operation.indexOf('create')!=-1||operation.indexOf('update')!=-1){
		var vluMacAddr=$("#macClassifierTagContainer").find("input#macAddress4classifier").val();
		var vluMacRange=$("#macClassifierTagContainer").find("input#macAddress_macRangeFrom4classifier").val();
		var vluMacOui=$("#macClassifierTagContainer").find("input#macOui4classifier").val();
		var isTagVisable=$("#macClassifierTagContainer").is(':visible');
		if((operation=='create'||operation=='update')&&((vluMacAddr!=null&&vluMacAddr!="")||(vluMacRange!=null&&vluMacRange!="")||(vluMacOui!=null&&vluMacOui!=""))&&isTagVisable){
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
    		var url = "<s:url action='classifierTag' includeParams='none' />" + "?tagKey=24&operation=viewAll"+ "&ignore=" + new Date().getTime(); 	
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
		var url = "<s:url action='classifierTag' includeParams='none' />" + "?tagKey=24&operation=viewAll"+ "&ignore=" + new Date().getTime(); 	
		operName=operation;
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succView}, null);
		return;
	}
	if (validate(operation)) {
		if (operation == 'create'+'<s:property value="lstForward"/>'
				|| operation == 'update' + '<s:property value="lstForward"/>'
				|| operation == 'cancel' + '<s:property value="lstForward"/>') {
				showProcessing();
			}
		document.forms[formName].operation.value = operation;
    	document.forms[formName].submit();
	}
}


function succView(o) {	
	eval("var detailResult = " + o.responseText);
	if(detailResult.succ) {		
		ruleIndex=detailResult.items.split('|');		
		if(ruleIndex!=''){
			 $("input#ahTempClassifierConfilictBtn").click();
			 return;
		}
		else{
		if (validate(operName)) {
			document.forms[formName].operation.value = operName;
	    	document.forms[formName].submit();
		 	}
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
		if (validate(operName)) {
			document.forms[formName].operation.value = operName;
	    	document.forms[formName].submit();
		}
	 }
}	

<s:if test="%{jsonMode == true}">
function saveMacOuiJson(operation) {
	if (validate(operation)) {
		var url = "<s:url action='macAddress' includeParams='none' />" + "?jsonMode=true" 
					+ "&ignore=" + new Date().getTime(); 
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["macAddress"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveMacOuiJson, failure : failSaveMacOuiJson, timeout: 60000}, null);
	}
}

var succSaveMacOuiJson = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentDomIDs = details.parentDomID.split(",");
			if (parentDomIDs) {
				for(var i=0;i<parentDomIDs.length;i++) {
					var parentSelectDom = parent.document.getElementById(parentDomIDs[i]);
					if(parentSelectDom != null) {
						if(details.addedId != null && details.addedId != ''){
							if (i==0) {
								hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);	
							} else {
								hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, false);	
							}
						}				 
					}
				}
			} 	
		}
		parent.closeIFrameDialog();
	} catch(e) {
		// do nothing now.
	}
}

var failSaveMacOuiJson = function(o) {
	// do nothing now.
}
</s:if>

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_MAC_OR_OUI%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>')
	{
		return true;
	}

	if(operation=='create' || operation == 'create'+'<s:property value="lstForward"/>') {
		var name = document.getElementById(formName + "_dataSource_macOrOuiName");
		var message = hm.util.validateName(name.value, '<s:text name="config.macOrOui.name" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
    	if (name.value == '<s:text name="config.ipPolicy.any" />') {
    		hm.util.reportFieldError(name, '<s:text name="error.ipOrMacOrService.nameLimit"><s:param><s:text name="config.ipPolicy.any" /></s:param></s:text>');
        	name.focus();
        	return false;
    	}
	}

	var table = document.getElementById("checkAll");
    if (operation == 'addMacAddress') {
    	if ((macFlag == 1 && !checkMacEntry('<s:text name="config.macOrOui.macAddress" />',document.getElementById(formName + "_macAddress"),12))
    		|| (macFlag == 2 && !checkMacEntry('<s:text name="config.macOrOui.macOui" />',document.getElementById(formName + "_macOui"),6))) {
			return false;
		}
	 	var hidename = document.getElementById("hideName");
	 	var hidelocation = document.getElementById("hideLocation");
	 	var hidehive = document.getElementById("hideHiveAP");
	 	var hideclass = document.getElementById("hideClassifier");
	 	var hideFrom = document.getElementById("hideRange");
	 	var macEntries = document.getElementsByName('macEntries');
    	var cbs = document.getElementsByName('ruleIndices');
		
		// mac address or mac oui
		if(hideFrom.style.display == "none") {
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
			 		if(tag1.value.length == 0 && tag2.value.length == 0 && tag3.value.length == 0){
						hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.classifier.tag" /></s:param></s:text>');
			 			tag1.focus();
			 			return false;
					}
			 	}
			 }
			if (cbs.length > 0 && (undefined == macEntries || macEntries.length == 0)) {
				hm.util.reportFieldError(table, '<s:text name="error.objectWithWrongType"></s:text>');
				return false;
			}
		// mac range
		} else {
			if (cbs.length > 0 && undefined != macEntries && macEntries.length > 0) {
				hm.util.reportFieldError(table, '<s:text name="error.objectWithWrongType"></s:text>');
				return false;
			}
			if (cbs.length >= 32) {
				hm.util.reportFieldError(table, '<s:text name="error.objectReachLimit"></s:text>');
				return false;
			}
			var macFrom = document.getElementById(formName + "_macRangeFrom");
			var macTo = document.getElementById(formName + "_macRangeTo");
			// mac range from value
			if (!checkMacEntry('<s:text name="config.macOrOui.macRangeFrom" />',macFrom,12)) {
				return false;
			}
			// mac range to value
			if (!checkMacEntry('<s:text name="config.macOrOui.macRangeTo" />',macTo,12)) {
				return false;
			}
			// check macRange from and to
			if (!checkMacRangeFromTo(macFrom, macTo)) {
				return false;
			}
		}
    }

    if (operation == 'removeMacAddress' || operation == 'removeMacAddressNone') {
		
	}

    if(operation == 'create'+'<s:property value="lstForward"/>' 
    	|| operation == 'update'+'<s:property value="lstForward"/>'
    	|| operation == 'create' || operation == 'update') {
    	var macEntries = document.getElementsByName('macEntries');
    	var isExistMac=isExistMacEntries(macEntries);
    	var cbs = document.getElementsByName('ruleIndices');
		var table = document.getElementById("checkAll");
		
		if (undefined != macEntries) {
			if (macFlag == 3 && macEntries.length > 0&&isExistMac) {
				hm.util.reportFieldError(table, '<s:text name="error.objectWithWrongType"></s:text>');
				return false;
			}
			for(var i = 0; i < macEntries.length; i ++) {
				if(macEntries[i].style.display=='none')continue;
				if ((macFlag == 1 && !checkMacEntry('<s:text name="config.macOrOui.macAddress" />'+' in '+(i+1)+' row',macEntries[i],12))
		    		|| (macFlag == 2 && !checkMacEntry('<s:text name="config.macOrOui.macOui" />'+' in '+(i+1)+' row',macEntries[i],6))) {
					return false;
				}
			}
		}
    }
	return true;
}
function isExistMacEntries(entry) {
	for(var i = 0; i < entry.length; i ++) {
		if(entry[i].style.display=='none')continue;
		else{ return true; }
	}
	return false;
}

function checkMacEntry(name, entry, max) {
	var table = document.getElementById("checkAll");
	if (entry.value.length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+name+'</s:param></s:text>');
        entry.focus();
        return false;
    } else if (!hm.util.validateMacAddress(entry.value, max)) {
		hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param>'+name+'</s:param></s:text>');
		entry.focus();
		return false;
	}
	return true;
}

function checkMacRangeFromTo(entryFrom, entryTo) {
	var valFrom = entryFrom.value.toLowerCase();
	var valTo = entryTo.value.toLowerCase();
	for (var count = 0; count < valFrom.length; count++) {
		var codeFrom = valFrom.charCodeAt(count);
		var codeTo = valTo.charCodeAt(count);
		if (codeFrom < codeTo) {
			return true;
		} else if (codeFrom == codeTo) {
			continue;
		} else {
			var table = document.getElementById("checkAll");
			hm.util.reportFieldError(table, '<s:text name="error.notLargerThan"><s:param><s:text name="config.macOrOui.macRangeFrom"/></s:param><s:param><s:text name="config.macOrOui.macRangeTo"/></s:param></s:text>');
			entry.focus();
			return false;
		}
	}
	return true;
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');
	setShowHide();

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

function addressLength(typeFlag) {
	diffMacFlag=true;
	beforeMacFlag=macFlag;
	macFlag = typeFlag;	
	if(beforeMacFlag==typeFlag){
		diffMacFlag=false;
	}
	
	setShowHide();
}

function setShowHide(){
	//title
	var titleType = document.getElementById("titleType");
	var titleValue = document.getElementById("titleValue");
	var titleRange = document.getElementById("titleRange");
	var titleEntry = document.getElementById("titleEntry");
	
	//detail
	
	
	
		if (macFlag == 1||macFlag == 2) {
			titleType.style.display= "";
			titleValue.style.display= "";
			titleEntry.style.display= "";
			titleRange.style.display= "none";
		} else {
			titleType.style.display= "none";
			titleValue.style.display= "none";
			titleEntry.style.display= "none";
			titleRange.style.display= "";
		}
	
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="macAddress" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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
</script>

<div id="content"><s:form action="macAddress">
	<s:hidden name="macType"/>
	<s:hidden name="macTypeSupported" />
	<s:if test="%{jsonMode == true}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
		<s:hidden name="id"/>
		<s:hidden name="parentIframeOpenFlg" />
		<s:hidden name="contentShowType" />
		<div class="topFixedTitle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-MAC_Objects.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.title.macOui"/>
								</s:if> <s:else>
									<s:text name="config.title.macOui.edit"/>
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
								<s:if test="%{!parentIframeOpenFlg}">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
											<td width="20px">&nbsp;</td>
											<s:if test="%{dataSource.id == null}">
												<s:if test="%{writeDisabled == 'disabled'}">
													<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
												</s:if>
												<s:else>
													<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onclick="saveMacOuiJson('create');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
												</s:else>
											</s:if>
											<s:else>
												<s:if test="%{updateDisabled == 'disabled'}">
													<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onclick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
												</s:if>
												<s:else>
													<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onclick="saveMacOuiJson('update');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
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
												<s:if test="%{writeDisabled == 'disabled'}">
													<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
												</s:if>
												<s:else>
													<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
												</s:else>
											</s:if>
											<s:else>
												<s:if test="%{updateDisabled == 'disabled'}">
													<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="return false;" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
												</s:if>
												<s:else>
													<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
												</s:else>
											</s:else>
										</tr>
									</table>
								</s:else>
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
							onClick="submitAction('<%=Navigation.L2_FEATURE_MAC_OR_OUI%>');">
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
				<table cellspacing="0" cellpadding="0" border="0" width="740">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="740">
			</s:else>
				<tr>
					<td style="padding: 6px 4px 5px 4px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr id="macGroups">
									<td style="padding-left:5px" width="120"><s:radio disabled="%{disabledName || !macTypeSupport.address}"
										label="Gender" name="radioMacOrOui"
										list="#{'address':'MAC Address'}"
										onclick="addressLength(1);"
										value="%{radioMacOrOui}" /></td>
									<td style="padding-left:5px" width="170"><s:radio label="Gender" disabled="%{disabledName || !macTypeSupport.range}"
										name="radioMacOrOui" list="#{'macRange':'MAC Address Range'}"
										onclick="addressLength(3);" value="%{radioMacOrOui}" /></td>
									<td style="padding-left:5px"><s:radio label="Gender" disabled="%{disabledName || !macTypeSupport.oui}"
										name="radioMacOrOui" list="#{'oui':'MAC OUI'}"
										onclick="addressLength(2);" value="%{radioMacOrOui}" /></td>
								</tr>
								<tr>
									<td height="3" colspan="3"></td>
								</tr>
								<tr>
									<td class="labelT1" width="120"><label><s:text
										name="config.macOrOui.name" /><font color="red"><s:text name="*"/></font></label></td>
									<td colspan="2"><s:textfield
										name="dataSource.macOrOuiName" size="24"
										maxlength="%{addressNameLength}" disabled="%{disabledName}"
										onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
										name="config.ssid.ssidName_range" /></td>
								</tr>
								<tr>
									<td style="padding:6px 0px 6px 0px" colspan="3">
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
									<td colspan="3" style="padding:4px 0px 4px 4px;" valign="top">
										<table cellspacing="0" cellpadding="0" border="0" class="embedded" width="100%">
											<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
												<td colspan="5" style="padding-bottom: 2px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><input type="button" name="ignore" value="New"
															class="button" id="newItemBtn"
															<s:property value="updateDisabled" />></td>	
														<td><input type="button" name="ignore" value="Reset Order" id="resetButton"
														class="button" <s:property value="updateDisabled" />></td>													
													</tr>
												</table>
												</td>
											</tr>
											<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
												<td colspan="5" style="padding-bottom: 2px;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
															class="button" id="addItemBtn"></td>														
														<td><input type="button" name="ignore" value="Cancel"
															class="button" id="cancelItemBtn"></td>
														<td><input type="button" name="ignore" value="Reset Order" id="resetButton"
														class="button" <s:property value="updateDisabled" />></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr id="headerSection">
												<!--  th align="left" style="padding-left: 0;" width="10"><input
													type="checkbox" id="checkAll" style="display:none"
													onClick="toggleCheckAllRules(this);"></th-->
												<th align="left" width="90" id="titleEntry" style="display:<s:property value="showAddressOrOui" />"><s:text
													name="config.macOrOui.macEntry" /><input type="checkbox" id="checkAll" style="display:none"></th>
												<th align="left" width="90" id="titleType" style="display:<s:property value="showAddressOrOui" />"><s:text
													name="config.ipAddress.type" /></th>
												<th align="left" width="280" id="titleValue" style="display:<s:property value="showAddressOrOui" />"><s:text
													name="config.ipAddress.value" /></th>
												<th align="left" width="700" id="titleRange" style="display:<s:property value="showMacRange" />"><s:text
													name="config.macOrOui.macRange" /></th>
												<th align="left" width="160"><s:text
													name="config.ipAddress.description" /></th>
											</tr>
											  <tr id="macClassifierTagContainer"/>
                                              <tr id="macItemsRow"/>																					
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
<div id="classfierHelpWholePanel" style="display: none;height: 160px">
	<div id="classfierHelpPanel" style="overflow: auto;word-wrap: break-word; word-break: normal;"></div>	
	<input id="classfierHelpOkButton" type="button" value="OK" style="margin-left: 180px;">
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
#macItemsRow span.optmenu {    
    left: 3px;
}
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
	width: 100px;
} 
.ui-classifier td#topologyCtner div ul li a.none{
	margin-left: 5px;	
	width: 100px;
}
.ui-classifier td#tagCtner div ul li a.none{
	margin-left: 75px;
	width: 100px;	
}
.ui-classifier td#topologyCtner div ul li  input{
	margin-left: 5px;
    width: 120px;
}
.ui-classifier td#deviceCtner div ul li  input{
	margin-left: 5px;
    width: 120px;
}
.ui-classifier td#tagCtner div ul li  input{
	margin-left: 75px;
    width: 120px;
}
#macClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 5px;	
}
#macClassifierTagContainer td#tagCtner li.tag-choice{
	padding-left: 75px;	
}
.ui-classifier-items ul.itemContainer .item span.pointer{
	white-space:normal;
}
 
.ui-menu .ui-menu-item a {
 	width: 100px;
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
					{tagKey: 24, operation: 'edit', selectedItems: idx,tag1:stag1 ,tag2:stag2 ,tag3:stag3 ,typeName:dvcName,locationId:topoName}, 
				function(data) {
					if(data.succ) {	
						//just set the edited value back to jsp page
						var htmlIdx=parseInt(idx)+1;
						var targetString=data.value;
						$("#macItemsRow").find("li.item:nth-child("+htmlIdx+")").find("span:nth-child(3)").html(targetString);	
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
    					key: 24,
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

function checkAddMacObject() {
	var table = document.getElementById("checkAll");
	var ip;
	if(macFlag==1){
		var entry=document.getElementById("macAddress4classifier");
		ip=$('#macAddress4classifier').val();	
		if (ip.length == 0) {			
			$('#macAddress4classifier').focus();
			hm.util.reportFieldError(table, 'MAC Address is a required field.');
			return false;
		}
		if (!checkMacEntry('<s:text name="config.macOrOui.macAddress" />',entry,12)) {			
		 	return false;
		}
	}	
	if(macFlag==2){
		var entry=document.getElementById("macOui4classifier");
		ip=$('#macOui4classifier').val();		
		if (ip.length == 0) {			
			$('#macOui4classifier').focus();
			hm.util.reportFieldError(table, 'MAC OUI Address is a required field.');
			return false;
		}
		if (!checkMacEntry('<s:text name="config.macOrOui.macOui" />',entry,6)) {			
		 	return false;
		}
	}
	if(macFlag==3){
		var entryFrom=document.getElementById("macAddress_macRangeFrom4classifier");
		var entryTo=document.getElementById("macAddress_macRangeTo4classifier");
		var macRangeFromTemp=$('#macAddress_macRangeFrom4classifier').val();	
		var macRangeToTemp=$('#macAddress_macRangeTo4classifier').val();	
		if (macRangeFromTemp.length == 0||macRangeToTemp.length == 0) {			
			$('#macAddress_macRangeFrom4classifier').focus();
			hm.util.reportFieldError(table, 'MAC Range is a required field.');
			return false;
		}
		// mac range from value
		if (!checkMacEntry('<s:text name="config.macOrOui.macRangeFrom" />',entryFrom,12)) {			
			return false;
		}
		// mac range to value
		if (!checkMacEntry('<s:text name="config.macOrOui.macRangeTo" />',entryTo,12)) {			
			return false;
		}
		// check macRange from and to
		if (!checkMacRangeFromTo(entryFrom, entryTo)) {			
			return false;
		}
	}	
	return true;
};
</script>
<script>
var addEmptyGlobalItem = function (){
	 $("#macClassifierTagContainer").classifierTag('addEmptyGlobalItem');  
}
function initButton() {			 
	  $('tr#macGroups').delegate('input[type="radio"]', 'click', function() {
	   if(diffMacFlag){  		  
    	$("#macClassifierTagContainer").classifierTag('showValueByFlag', $(this)[0].value);
   	     $("#macItemsRow ul").empty();
   		   		
    	if(macFlag==1 || macFlag==2){    	
    		$("#macClassifierTagContainer").classifierTag('removeAllItem',addEmptyGlobalItem);     	     
    	} else {
    		$("#macClassifierTagContainer").classifierTag('removeAllItem');  
    	}
     }	  	
    });
    $('input#newItemBtn').click(function() {
        hm.util.hide('newButton');
        hm.util.show('createButton');
        hm.util.show('macClassifierTagContainer');
    });
    $('input#cancelItemBtn').click(function() {
        hm.util.hide('createButton');
        hm.util.show('newButton');
        hm.util.hide('macClassifierTagContainer');     
    });
    $('input#addItemBtn').click(function() {
    	if(macFlag==3){
    		$("#macClassifierTagContainer").classifierTag('saveMacRangeItem');
    	}else{
        $("#macClassifierTagContainer").classifierTag('saveItem');
        }
    });
    $('input#resetButton').click(function() {
        $("#macClassifierTagContainer").classifierTag('resetOrder');
    });
}
function initClassifierWidget() {	
		var props = [
		             {
		            	 flag: {type: 1, name: 'address'}, //MAC Address
		            	 validateFn: function(el){return checkAddMacObject();},
		            	 <s:if test="%{dataSource.typeFlag == 1}">
		            	 selected: true,
		            	 </s:if>
		            	 desc: '<br>&nbsp;&nbsp;&nbsp;(12 hex digits)',
		            	 items: [{id: 'macAddress4classifier', elType: 'input', field: 'macEntry',
		            		     onkeypress: 'return hm.util.keyPressPermit(event,\'hex\');',
		            		     maxlength: 12, size: 10}]
		             },		             
		             
		             {
		            	 flag: {type: 2, name: 'oui'}, // MacOui
		            	 <s:if test="%{dataSource.typeFlag == 2}">
		            	 selected: true,
		            	 </s:if>
		            	 validateFn: function(el){return checkAddMacObject();},
		            	 desc: '<br>&nbsp;&nbsp;&nbsp;(6 hex digits)',		            	
		            	 items: [{id: 'macOui4classifier', elType: 'input', field: 'macEntry',
				            	 onkeypress: 'return hm.util.keyPressPermit(event,\'hex\');', 
				            	 maxlength: 6, size: 10}]
		             },
		             
		             {
		            	 flag: {type:3, name: 'macRange'}, // Mac Range
		            	 <s:if test="%{dataSource.typeFlag == 3}">
		            	 selected: true,
		            	 </s:if>
		            	 validateFn: function(el){return checkAddMacObject();},
		            	 concatenateHTML: "&nbsp;&nbsp;&nbsp;--&nbsp;&nbsp;&nbsp;",
		            	 subItemWidths:{value: 270, type: 80, tagValue: 230, desc: 160},
		            	 subItemOperation:[ {text : 'Matching Devices',display:false},
		         		                   {text : 'View Conflicts',display:false},
		        		                   {text : 'Edit',display:false},
		        		                   {text : 'Remove', display:true}],
		            	 desc: '<br>&nbsp;&nbsp;&nbsp; (12 hex digits)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; (12 hex digits)',
		            	 items: [{id: 'macAddress_macRangeFrom4classifier', elType: 'input',  field: 'macRangeFrom',
			            	 onkeypress: 'return hm.util.keyPressPermit(event,\'hex\');', 
			            	 maxlength: 12, size: 10},
			            	 {id: 'macAddress_macRangeTo4classifier', elType: 'input',  field: 'macRangeTo',
		                        onkeypress: 'return hm.util.keyPressPermit(event,\'hex\');', 
		                        maxlength: 12, size: 10}]
		             }
		             ];		
		
		var ct = $("#macClassifierTagContainer").classifierTag(
				{
					key: 24,					
					widgetWidth: {desc: 160},
					globalProps: [{flagType: 1, id: 'macEntry'}, 			                       
			                       {flagType: 2, id: 'macEntry'},
			                       {flagType: 3, id: 'macRangeFrom|macRangeTo'}],
					valueProps: props,
					itemTableId: 'macItemsRow',					
					itemWidths:{value: 100, type: 80, tagValue: 260, desc: 160},
					checkable:null,
					onlyDisplayValue:{ name: 'macRange'},
					itemEditable: {value: true, valueName: 'macEntries', desc: true, descName: 'descriptions'}
				});
		
		<s:if test="%{dataSource.typeFlag == 3}">;	
		$("#macClassifierTagContainer").classifierTag('showValueByFlag', 'macRange');	
		 </s:if>
		 var domainId=<s:property value="domainId" />;		
		if(<s:property value="%{dataSource.items.size()==0}"/>) {
			<s:if test="%{dataSource.typeFlag != 3}">
		       $("#macClassifierTagContainer").classifierTag('addEmptyGlobalItem');
		    </s:if>
		} else {
			<s:iterator value="%{dataSource.items}" status="status">
			var item = {};
			item.id = <s:property value="%{#status.index}"/>;
			item.value = '<s:property value="%{macEntry}"/>';
			<s:if test="%{dataSource.typeFlag == 3}">
			    		
			   item.value ='<s:property value="%{macRangeFrom}"/>|<s:property value="%{macRangeTo}"/>';
			 </s:if>
			item.type = <s:property value="%{type}"/>;			
			item.tagValue = '<s:property value="getTagValue(domainId)"/>';		
			item.desc = '<s:property value="%{descriptionStr}"/>';
			if(item.type == 1) {
				$("#macClassifierTagContainer").classifierTag('addItem', item, -1);
			} else {
				$("#macClassifierTagContainer").classifierTag('addItem', item);
			}
			</s:iterator>
		}
		$("#macClassifierTagContainer").hide();	
		initButton();
}
</script>
