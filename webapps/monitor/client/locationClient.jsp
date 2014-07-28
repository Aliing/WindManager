<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.SingleTableItem"%>
<%@page import="com.ah.be.common.NmsUtil"%>
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/ct.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/panel.css" includeParams="none"/>?v=<s:property value="verParam" />" />       
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
#locClientClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 5px;	
}
#locClientClassifierTagContainer td#tagCtner li.tag-choice{
	padding-left: 75px;	
}
.ui-classifier-items ul.itemContainer .item span.pointer{
	white-space:normal;
}
 
.ui-menu .ui-menu-item a {
 	width: 120px;
 }


#locClientItemsRow span.optmenu {    
    left: 30px;
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
var formName = 'watchList';
var operName = 'create';
var saveInJsonflag=false;
var ruleIndex= '';
function onLoadPage() {	
	<s:if test="%{jsonMode}">
	if(top.isIFrameDialogOpen()) {
    	top.changeIFrameDialog(950, 450);
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
}

function checkConfilct(operation,isInConfigure,callbackMehod,tagKeyVal) {
	saveInJsonflag=isInConfigure;
	var vlu=$("#locClientClassifierTagContainer").find("input#macEntry").val();
		var isTagVisable=$("#locClientClassifierTagContainer").is(':visible');
		if(vlu!=null&&vlu!=""&&(operation=='create'||operation=='update')&&isTagVisable){
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
    		var url = "<s:url action='classifierTag' includeParams='none' />" + "?tagKey="+tagKeyVal+"&operation=viewAll"+ "&ignore=" + new Date().getTime(); 	
			operName=operation;
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : callbackMehod}, null);
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
	var url = "<s:url action='classifierTag' includeParams='none' />" + "?tagKey="+tagKeyVal+"&operation=viewAll"+ "&ignore=" + new Date().getTime(); 	
	operName=operation;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : callbackMehod}, null);
}

function submitAction(operation) 
{
	if(operation=='create'||operation=='update'){		
		checkConfilct(operation,false,succView,40);
		return;
	 }
  if (validate(operation)) 
		{
		    showProcessing();
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
	  if(saveInJsonflag){
	    if (validate(operName)) {
				url = "<s:url action='watchList' includeParams='none' />" + "?jsonMode=true" 
				+ "&ignore=" + new Date().getTime(); 
					if (operName == 'create') {
							// 
					} else if (operName == 'update') {
		  				url = url + "&id="+'<s:property value="dataSource.id" />';
		   			}
			document.forms[formName].operation.value = operName;
			YAHOO.util.Connect.setForm(document.forms["watchList"]);
			var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSave, failure : failSave, timeout: 60000}, null);		
		}
	  }
	  else{
		if (validate(operName)) {
			document.forms[formName].operation.value = operName;
	    	document.forms[formName].submit();
		}
	   }
	 }
}	

function validate(operation)
{
	if('<%=Navigation.L2_FEATURE_LOCATIONCLIENTWATCH%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>')
	{
		return true;
	}

	if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'create') {
		var name = document.getElementById('watchName');
		var message = hm.util.validateName(name.value, '<s:text name="monitor.locationClientWatch.name" />');
    	if (message != null) {
    		hm.util.reportFieldError(name, message);
        	name.focus();
        	return false;
    	}
	}

	var table = document.getElementById("checkAll");
	if (operation == 'addEntry')
	{
		var macEntry = document.getElementById('macEntry');

		if (macEntry.value.length == 0) {
	        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="monitor.locationClientWatch.macEntry" /></s:param></s:text>');
	        macEntry.focus();
	        return false;
	    }
		if (<%=NmsUtil.isHostedHMApplication()%>)
		{
			if (!hm.util.validateMacAddress(macEntry.value, 12)) {
				hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param><s:text name="monitor.locationClientWatch.macEntry" /></s:param></s:text>');
				macEntry.focus();
				return false;
			}
		} else {
			if (!hm.util.validateMacAddress(macEntry.value, 6) && !hm.util.validateMacAddress(macEntry.value, 12)) {
				hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param><s:text name="monitor.locationClientWatch.macEntry" /></s:param></s:text>');
				macEntry.focus();
				return false;
			}
		}
		
		var useTypeSelect = document.getElementById('useTypeSelect');
		var useType = useTypeSelect.options[useTypeSelect.selectedIndex].value;
		if (useType == <%=SingleTableItem.TYPE_MAP%>)
		{
			var mapID = document.forms[formName].mapID;
	 		if(mapID.value == -1) {
	 			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.location" /></s:param></s:text>');
        		mapID.focus();
        		return false;
	 		}
		} 
		else if (useType == <%=SingleTableItem.TYPE_HIVEAPNAME%>)
		{
			var apName = document.getElementById("apName");

			if(apName.value.length == 0) {
				hm.util.reportFieldError(apName, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.hiveAP" /></s:param></s:text>');
        		apName.focus();
        		return false;
			} else if (apName.value.indexOf(' ') > -1) {
        		hm.util.reportFieldError(table, '<s:text name="error.name.containsBlank"><s:param><s:text name="config.ipAddress.hiveAP" /></s:param></s:text>');
        		apName.focus();
        		return false;
    		}
		} 
		else if (useType == <%=SingleTableItem.TYPE_CLASSIFIER%>)
		{
			var tag1 = document.getElementById("tag1");
	 		var tag2 = document.getElementById("tag2");
	 		var tag3 = document.getElementById("tag3");
	 		if(tag1.value.length == 0 && tag2.value.length == 0 && tag3.value.length == 0){
				hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="config.ipAddress.classifier.tag" /></s:param></s:text>');
	 			tag1.focus();
	 			return false;
			}
		}
	}
	
	if(operation == 'create'+'<s:property value="lstForward"/>' 
    	|| operation == 'update'+'<s:property value="lstForward"/>'
    	|| operation == 'create'
    	|| operation == 'update') {
    	var macEntries = document.getElementsByName('macEntries');
		if(macEntries.length == 0)
		{
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="monitor.locationClientWatch.macEntry" /></s:param></s:text>');
       		table.focus();
       		return false;
		}

		for(var i = 0; i < macEntries.length; i ++) {
			var macEntry = macEntries[i];
			if (macEntry.value.length == 0) {
		        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="monitor.locationClientWatch.macEntry" /></s:param></s:text>');
		        macEntry.focus();
		        return false;
		    } else if (!hm.util.validateMacAddress(macEntry.value, 6) && !hm.util.validateMacAddress(macEntry.value, 12)) {
				hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param><s:text name="monitor.locationClientWatch.macEntry" /></s:param></s:text>');
				macEntry.focus();
				return false;
			} 
		}
    }
    
    if (operation == 'removeEntry') {
		var cbs = document.getElementsByName('macIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="monitor.locationClientWatch.macEntry" /></s:param></s:text>');
			return false;
		}
	}
	
	return true;
}

function showCreateSection() 
{
	document.getElementById("addBtn").style.display = "none";
	document.getElementById("removeBtn").style.display = "none";
	hm.util.show('createButton');
	hm.util.show('createSection');
	// to fix column overlap issue on certain browsers
	var trh = document.getElementById('headerSection');
	var trc = document.getElementById('createSection');
	var table = trh.parentNode;	
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection() 
{
	document.getElementById("addBtn").style.display = "block";
	document.getElementById("removeBtn").style.display = "block";
	hm.util.hide('createButton');
	hm.util.hide('createSection');
}

function macEntryTypeChange(type) {
	var global = document.getElementById("value_global");
	var map = document.getElementById("value_map");
	var hiveap = document.getElementById("value_hiveap");
	var classifier = document.getElementById("value_classifier");

	switch(parseInt(type)) {
		case <%=SingleTableItem.TYPE_GLOBAL%>:
			global.style.display= "";
	    	map.style.display="none";
			hiveap.style.display="none";
			classifier.style.display="none";
			break;
		case <%=SingleTableItem.TYPE_MAP%>:
			global.style.display= "none";
	    	map.style.display="";
			hiveap.style.display="none";
			classifier.style.display="none";
			break;
		case <%=SingleTableItem.TYPE_HIVEAPNAME%>:
			global.style.display= "none";
	    	map.style.display="none";
			hiveap.style.display="";
			classifier.style.display="none";
			break;
		case <%=SingleTableItem.TYPE_CLASSIFIER%>:
			global.style.display= "none";
	    	map.style.display="none";
			hiveap.style.display="none";
			classifier.style.display="";
			break;
		default:
			break;
	}
}

function toggleIndices(cb) {
	var cbs = document.getElementsByName('macIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function save(operation) {
    checkConfilct(operation,true,succsSaveView,40);		
}

function succsSaveView(o) {	
	eval("var detailResult = " + o.responseText);
	if(detailResult.succ) 
	{		
		ruleIndex=detailResult.items.split('|');		
		if(ruleIndex!=''){
			 $("input#ahTempClassifierConfilictBtn").click();
			 return;
		}
		else
		{
			if (validate(operName)) {
				url = "<s:url action='watchList' includeParams='none' />" + "?jsonMode=true" 
				+ "&ignore=" + new Date().getTime(); 
					if (operName == 'create') {
							// 
					} else if (operName == 'update') {
		  				url = url + "&id="+'<s:property value="dataSource.id" />';
		   			}
			document.forms[formName].operation.value = operName;
			YAHOO.util.Connect.setForm(document.forms["watchList"]);
			var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSave, failure : failSave, timeout: 60000}, null);		
			}
		}
	}
}

var succSave = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectEl = parent.document.getElementById(details.parentDomID);
			if(parentSelectEl != null) {
				if(details.newObjId != null && details.newObjId != ''){
					dynamicAddSelect(parentSelectEl, details.newObjName, details.newObjId);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSave = function(o) {
	// do nothing now
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="watchList" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="displayName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="displayName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}
</s:if>
</script>

<div id="content">
	<s:form action="watchList">
		<s:if test="%{jsonMode == true}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
		<div id="titleDiv" style="margin-bottom:15px;">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td align="left">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="<s:url value="/images/hm_v2/profile/hm-icon-vlan-big.png" includeParams="none"/>"
									width="40" height="40" alt="" class="dblk" />
								</td>
								<s:if test="%{dataSource.id == null}">
								<td class="dialogPanelTitle"><s:text name="monitor.locationClientWatch.dlg.title.new"/></td>
								</s:if>
								<s:else>
								<td class="dialogPanelTitle"><s:text name="monitor.locationClientWatch.dlg.title.edit"/></td>
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
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
								<s:if test="%{dataSource.id == null}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" style="float: right;" onclick="save('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
								<s:else>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" style="float: right;" onclick="save('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:else>
							</tr>
						</table>
						</td>
					</tr>
				</table>
		</div>
		</s:if>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{jsonMode == false}">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<s:if test="%{dataSource.id == null}">
								<td>
									<input type="button" name="ignore"
										value="<s:text name="button.create"/>" class="button"
										onClick="submitAction('create<s:property value="lstForward"/>');"
										<s:property value="writeDisabled" />>
								</td>
							</s:if>
							<s:else>
								<td>
									<input type="button" name="ignore"
										value="<s:text name="button.update"/>" class="button"
										onClick="submitAction('update<s:property value="lstForward"/>');"
										<s:property value="writeDisabled" />>
								</td>
							</s:else>
							<s:if test="%{lstForward == null || lstForward == ''}">
								<td>
									<input type="button" name="cancel" value="Cancel"
										class="button"
										onClick="submitAction('<%=Navigation.L2_FEATURE_LOCATIONCLIENTWATCH%>');">
								</td>
							</s:if>
							<s:else>
								<td>
									<input type="button" name="cancel" value="Cancel"
										class="button"
										onClick="submitAction('cancel<s:property value="lstForward"/>');">
								</td>
							</s:else>
						</tr>
					</table>
				</td>
			</tr>
			</s:if>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
				<s:if test="%{jsonMode == true}">
					<table cellspacing="0" cellspacing="0" cellpadding="0" border="0" width="800px">
				</s:if>
				<s:else>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="800px">
				</s:else>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td class="labelT1" width="160">
								<label>
									<s:text name="monitor.locationClientWatch.name" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="watchName" name="dataSource.name" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									maxlength="32" disabled="%{disabledName}" />
								<s:text name="monitor.locationClientWatch.name.range" />
							</td>
						</tr>
						<tr>
							<td class="labelT1">
								<s:text name="monitor.locationClientWatch.description" />
							</td>
							<td>
								<s:textfield name="dataSource.description" size="48"
									maxlength="64" />
								<s:text name="monitor.locationClientWatch.description.range" />
							</td>
						</tr>
						<tr>
							<td style="padding:6px 4px 6px 4px" colspan="2">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="sepLine">
											<img src="<s:url value="/images/spacer.gif"/>" height="1"
												class="dblk" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="2" style="padding:0px 4px 4px 8px;" valign="top">
								<table cellspacing="0" cellpadding="0" border="0"
									class="embedded" width="100%">
									<tr>
										<td class="buttons" colspan="2">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr id="newButton">
													<td>
														<input type="button"  name="add" value="New"  
														class="button"   id="newItemBtn" />
													</td>									
																
													<td>
														<input type="button" id="resetButton" name="reset"
															value="Reset Order" class="button"															
															<s:property value="writeDisabled" />>
													</td>
												</tr>
												<tr style="display:none;" id="createButton">
													<td>
														<input type="button" name="ignore" value="Apply"
															class="button" id="addItemBtn" />
													</td>
													<td>
														<input type="button" name="ignore" value="Cancel"
															class="button" id="cancelItemBtn" />
													</td>
													<td>
														<input type="button" id="resetButton" name="reset"
															value="Reset Order" class="button"															
															<s:property value="writeDisabled" />>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								
									
									<tr id="headerSection">
										
										<th align="left" width="200px">
											<s:text name="monitor.locationClientWatch.macEntry" />(6 or 12 hex digits)<input type="checkbox" id="checkAll"
												onClick="toggleCheckAllIndices(this);" style="display:none">
										</th>
										<th align="left" width="100px">
											<s:text name="monitor.locationClientWatch.type" />
										</th>
										<th align="left" width="500px">
											<s:text name="monitor.locationClientWatch.value" />
										</th>
									</tr>
									<tr id="locClientClassifierTagContainer"></tr>									
									<tr id="locClientItemsRow"/>		
									
									<s:if test="%{gridCount.size > 0}">
										<s:generator separator="," val="%{' '}" count="%{gridCount}">
											<s:iterator>
												<tr>
													<td class="list" colspan="4">
														&nbsp;
													</td>
												</tr>
											</s:iterator>
										</s:generator>
									</s:if>
									
										<tr>
											<td class="noteInfo" colspan="8"><s:text name="config.common.classifier.tag.note"/></td>
										</tr>	
										<tr>
											<td  class="noteInfo" colspan="8"><s:text name="config.port.item.note3" /></td>
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
	</s:form>
</div>
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
	<input id="classfierPopupPanelOkButton" type="button" value="OK" style="margin-left: 80px; margin-top: 10px;">
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
<input type="button" value="AH Panel" id="ahTempClassifierHelpBtn">
<input type="text" value="Submit" id="ahTempClassifierIndexValue" value="">
<input type="text" value="Submit" id="ahTempClassifierItemType" value="">
<input type="text" value="Submit" id="ahTempClassfierConfilict" value="">
<input type="button" value="Submit" id="ahTempClassifierConfilictBtn">
<input type="button" value="Submit" id="ahTempClassifierNewRuleBtn">
</div>
	
<script>
function initClassifierWidget() {	
		var ct = $("#locClientClassifierTagContainer").classifierTag(
				{
					key: 40,					
					itemWidths:{value: 180, type: 80, tagValue: 400},					
					checkable:null,
					widgetWidth: {desc: 0},
					//<s:text name="monitor.locationClientWatch.macEntry.range" />					
					valueProps: {validateFn: function(el){return checkAddMacObject();},items: {id: 'macEntry',field: 'macEntry', onkeypress: 'onkeypress="return hm.util.keyPressPermit(event,\'hex\');"', maxlength: 12, size: 15}},
					//valueProps: {items: {id: 'userAttribute_attributeValue',field: 'attributeValue', onkeypress: 'return hm.util.keyPressPermit(event,\'ten\');', maxlength: 4, size: 15}, desc: '<br><s:text name="config.userAttribute.attributeValue.allow" />', validateFn: function(){return checkAttributeValue(Get('userAttribute_attributeValue'), '<s:text name="config.userAttribute.attributeValue" />');}},
					itemTableId: 'locClientItemsRow',
					itemEditable: {value: true, valueName: 'macEntries'},
					describable: false
				});
		var domainId=<s:property value="domainId" />;		
		if(<s:property value="%{dataSource.items.size()==0}"/>) {
		  $("#locClientClassifierTagContainer").classifierTag('addEmptyGlobalItem');
		} else {			
			<s:iterator value="%{dataSource.items}" status="status">
			var item = {};
			item.id = <s:property value="%{#status.index}"/>;
			item.value = '<s:property value="%{macEntry}"/>';
			item.type = <s:property value="%{type}"/>;
			item.tagValue = '<s:property value="getTagValue(domainId)"/>';			
			if(item.type == 1) {
				$("#locClientClassifierTagContainer").classifierTag('addItem', item, -1);
			} else {
				$("#locClientClassifierTagContainer").classifierTag('addItem', item);
			}
			</s:iterator>
		}
		 $("#locClientClassifierTagContainer").hide();	
		 $('input#newItemBtn').click(function() {
			hm.util.hide('newButton');
			hm.util.show('createButton');
			hm.util.show('locClientClassifierTagContainer');
		});
		$('input#cancelItemBtn').click(function() {
	   		hm.util.hide('createButton');
	   		hm.util.show('newButton');
	    	hm.util.hide('locClientClassifierTagContainer');		
		});
		$('input#addItemBtn').click(function() {
			$("#locClientClassifierTagContainer").classifierTag('saveItem');
		});
		$('input#resetButton').click(function() {
			$("#locClientClassifierTagContainer").classifierTag('resetOrder');
		});		 
}
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
					{tagKey: 40, operation: 'edit', selectedItems: idx,tag1:stag1 ,tag2:stag2 ,tag3:stag3 ,typeName:dvcName,locationId:topoName}, 
				function(data) {
					if(data.succ) {	
						//just set the edited value back to jsp page
						var htmlIdx=parseInt(idx)+1;
						var targetString=data.value;
						$("#locClientItemsRow").find("li.item:nth-child("+htmlIdx+")").find("span:nth-child(3)").html(targetString);
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
    					key: 40,
    					types:  [ {key: 9, text: 'test'},null,null],
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
	var macEntry = document.getElementById('macEntry');
	if (macEntry.value.length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param><s:text name="monitor.locationClientWatch.macEntry" /></s:param></s:text>');
        macEntry.focus();
        return false;
    }
	if (<%=NmsUtil.isHostedHMApplication()%>)
	{
		if (!hm.util.validateMacAddress(macEntry.value, 12)) {
			hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param><s:text name="monitor.locationClientWatch.macEntry" /></s:param></s:text>');
			macEntry.focus();
			return false;
		}
	} else {
		if (!hm.util.validateMacAddress(macEntry.value, 6) && !hm.util.validateMacAddress(macEntry.value, 12)) {
			hm.util.reportFieldError(table, '<s:text name="error.formatInvalid"><s:param><s:text name="monitor.locationClientWatch.macEntry" /></s:param></s:text>');
			macEntry.focus();
			return false;
		}
	}
	return true;
}
</script>	
