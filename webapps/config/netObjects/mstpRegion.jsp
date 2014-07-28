<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/dataTable/ahDataTable.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/ahdatatable/ahdatatable.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/ahdatatable/jquery.ui.autocomplete.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/ahdatatable/jquery.ui.theme.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script>
var formName = 'mstpRegion';
var buttonShowing;
function onLoadPage() {	
	onLoadAhDataTableForMstpRegion();
    
	if (document.getElementById("regionName").disabled == false) {
			document.getElementById("regionName").focus();
	}
	var operation = "<s:property value="%{operation}"/>";
	buttonShowing = <s:property value="%{buttonShowing}"/>;
}

function saveMstp(operation) {
	if (validate(operation)){
		var	url = "<s:url action='mstpRegion' includeParams='none' />" + "?jsonMode=true" 
			+ "&ignore=" + new Date().getTime(); 
		if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["mstpRegion"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveMstp, failure : failSave, timeout: 60000}, null);
	}
	
}

var succSaveMstp = function(o) {
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

function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create' &&
		    operation != 'addMstp' &&
		    operation != 'removeMstp') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
}



function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_MSTP_REGION%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>' ||
		operation == 'newMstpRegion') {
		return true;
	}

	if(operation == 'create' || operation == 'create'+'<s:property value="lstForward"/>'){
       if (!validateName()) {
		   return false;
	   }
       
       if (!validateRevision()) {
		   return false;
	   }
       
       if (!validateMaxhops()) {
		   return false;
	   }
    }
	
	if(operation == 'update' || operation == 'update'+'<s:property value="lstForward"/>'){
		if (!validateRevision()) {
			return false;
		}
	       
	    if (!validateMaxhops()) {
			return false;
		}
	}
    
    if(operation=='addMstp')
      if(!validateIpAddress() || !checkItemLimit() || !checkCommunityString() || !checkPasswordWhenAdd() ||!checkV3ListString())
             return false;
    if(operation=='removeMstp' && !checkSelectedItems())
          return false;
          
    
	return true;
}

function validateName() {      
      var inputElement = document.getElementById("regionName");
       var message = hm.util.validateNameWithBlanks(inputElement.value, '<s:text name="config.switchSettings.mstp.name" />');
       if (message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
       }
      
      return true;
}

function validateRevision() {      
    var inputElement = document.getElementById("revision");
     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.switchSettings.mstp.revision" />',0,65535);
     if (message != null) {
         hm.util.reportFieldError(inputElement, message);
         inputElement.focus();
         return false;
     }
    
    return true;
}

function validateMaxhops() {      
    var inputElement = document.getElementById("hops");
     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.switchSettings.mstp.maxhops" />',1,40);
     if (message != null) {
         hm.util.reportFieldError(inputElement, message);
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
       document.writeln('<td class="crumb" nowrap><a href="<s:url action="mstpRegion" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
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

function onLoadAhDataTableForMstpRegion(){
	var dataSource = eval('<s:property escape="false" value="mstp_ahDtDatas"/>');
    var ahDtClumnDefs = eval('<s:property escape="false" value="mstpi_ahDtClumnDefs"/>');
    var myColumnDefs = [
		{
			type: "text",
			mark: "instances",
			editMark:"edit_instance",
			display: '<s:text name="config.switchSettings.mstp.instance"/> <s:text name="config.switchSettings.mstp.instance.range" />',
			validate:validateInstance,
			keypress:"ten",
			width:"150px",
			maxlength:2
		},
        {
			type: "dropdown",
			mark: "priorities",
			editMark:"edit_prioity",
			display: '<s:text name="config.switchSettings.mstp.priority"/>  <s:text name="config.switchSettings.mstp.priority.range" />',
			defaultValue: 8,
			width:"100px"
		},
		{
			type: "text",
			mark: "vlans",
			display: '<s:text name="config.switchSettings.mstp.vlans"/>  <s:text name="config.switchSettings.mstp.vlans.range" />',
			editMark:"edit_vlan",
			defaultValue: "",
			keypress:"attribute",
			validate:validateVlans,
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
    var ahDataTable = new AhDataTablePanel.DataTablePanel("ahDataTableForMSTP",myColumns,dataSource);
    ahDataTable.render();
    
}

function validateVlans(data){
	var inputElement = document.getElementById("tbl_errorMessage")
	if(data.length != 0){
		var vlanList =data.split(',');
		var vlanValues = new Array();
		var vlanRangerValues = new Array();
		for(var j=0;j<vlanList.length;j++){
			var pattern = /^(\d+-)?\d+$/;
			if(!pattern.test(vlanList[j])){
				hm.util.reportFieldError(inputElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vlanGroup.vlans" /></s:param></s:text>');
		        return false;
			}
			var vlans = vlanList[j].split('-');
			var numVlan0=Number(vlans[0]);
			var numVlan1=Number(vlans[1]);
			var pattern_value = /^[1-9]\d{0,2}$|^[1-3]\d{3}$|^40[0-8][0-9]$|^409[0-4]$/; //1-4094
			for(var i=0;i<vlans.length;i++) {
				if(!pattern_value.test(vlans[i])){
					hm.util.reportFieldError(inputElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.vlanGroup.vlans" /></s:param></s:text>');
			        return false;
				}
			}
		    if(vlans.length>1 && numVlan0 > numVlan1){
		    	hm.util.reportFieldError(inputElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.BonjourGatewaySetting.vlans" /></s:param></s:text>');
		        return false;
			}
		    if(vlans.length>1){
		    	for(var i=0;i<=numVlan1-numVlan0;i++){
					if(vlanValues.contains(numVlan0+i)){
						hm.util.reportFieldError(inputElement, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
				        return false;
					} else {
						vlanValues.push(numVlan0+i);
					}
				}
		    } else {
		    	if(vlanValues.contains(numVlan0)){
					hm.util.reportFieldError(inputElement, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
			        return false;
				} else {
					vlanValues.push(numVlan0);
				}
		    }
			
			if(vlanRangerValues.contains(vlanList[j])){
				hm.util.reportFieldError(inputElement, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
		        return false;
			} else {
				vlanRangerValues.push(vlanList[j]);
			}
		}
	}
	
    return true;
}

function validateInstance(data) {
	var errorElement = document.getElementById("tbl_errorMessage");
	if(data.length < 1 || data == ""){
		hm.util.reportFieldError(errorElement, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.mstp.instance" /></s:param></s:text>');
		return false;
	}
	
	var message = hm.util.validateIntegerRange(data, '<s:text name="config.switchSettings.mstp.instance" />',1,63);
	if (message != null) {
        hm.util.reportFieldError(errorElement, message);
        return false;
    }	 
    
    return true;
}

function validatePriority(data) {
	var errorElement = document.getElementById("tbl_errorMessage");
	if(data.length < 1 || data == ""){
		hm.util.reportFieldError(errorElement, '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.mstp.priority" /></s:param></s:text>');
		return false;
	}
	
	var message = hm.util.validateIntegerRange(data, '<s:text name="config.switchSettings.mstp.priority" />',0,61440);
	if (message != null) {
        hm.util.reportFieldError(errorElement, message);
        return false;
    }	 
    
    return true;
}
</script>
<div id="content">
	<s:form action="mstpRegion" id="mstpRegion" name="mstpRegion">
	    <s:hidden name="buttonShowing" id="buttonShowing" value="%{buttonShowing}"/>
	    <s:hidden name="ipAddressMstpIds"/>
	    <s:if test="%{jsonMode}">
			<s:hidden name="operation" />
			<s:hidden name="jsonMode" />
			<s:hidden name="parentDomID" />
		</s:if>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	  		<s:if test="%{jsonMode == false}">
				<tr>
					<td><tiles:insertDefinition name="context" /></td>
				</tr>
				<tr>
					<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<s:if test="%{dataSource.id == null}">
								<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
									class="button" onClick="submitAction('create<s:property value="lstForward"/>');"
									<s:property value="writeDisabled" />></td>
							</s:if>
							<s:else>
								<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
									class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
									<s:property value="updateDisabled" />></td>
							</s:else>
							<s:if test="%{lstForward == null || lstForward == ''}">
								<td><input type="button" name="cancel" value="Cancel"
									class="button"
									onClick="submitAction('<%=Navigation.L2_FEATURE_MSTP_REGION %>');">
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
			</s:if>
			<s:else>
				<tr>
					<td style="padding:10px 10px 10px 10px">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td  align="left">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-mstp-75x75.png" includeParams="none"/>"
										width="40" height="40" alt="" class="dblk" />
									</td>
									<s:if test="%{dataSource.id == null}">
										<td class="dialogPanelTitle"><s:text name="config.switchSettings.mstp.title.new"/></td>
									</s:if>
									<s:else>
										<td class="dialogPanelTitle"><s:text name="config.switchSettings.mstp.title.edit"/></td>
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
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style=" margin-right: 20px;" tittle="<s:text name="common.button.cancel"/>" onclick="parent.closeIFrameDialog();"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
									<s:if test="%{dataSource.id == null}">
										<s:if test="'' == writeDisabled">
											<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;"  onclick="saveMstp('create');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a></td>
										</s:if>
									</s:if>
									<s:else>
										<s:if test="%{'' == updateDisabled}">
											<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;" onclick="saveMstp('update');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a></td>
										</s:if>
									</s:else>
								</tr>
							</table>
							</td>
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
			<s:if test="%{jsonMode == true}">
				<table border="0" cellspacing="0" cellpadding="0" width="700px">
			</s:if>
			<s:else>
				<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="760">
			</s:else>
				
				
				<tr>
					<td colspan="2">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="120" style="padding:6px 0px 4px 10px;">
								<s:text	name="config.switchSettings.mstp.name" /><font color="red"><s:text name="hm.common.required"/></font>
							</td>
							<td>
								<s:textfield name="dataSource.regionName" size="24" id="regionName"
								maxlength="%{nameLength}" disabled="%{disabledName}" 
								onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
								<s:text name="config.name.range"/>
							</td>
						</tr>		
						
						<tr>
							<td class="labelT1" width="120" style="padding:6px 0px 4px 10px;"><s:text name="config.switchSettings.mstp.description" /></td>
							<td colspan="2"><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" />
								<s:text name="config.description.range"/></td>
						</tr>
						<tr>
							<td class="labelT1" width="120" style="padding:6px 0px 4px 10px;">
								<s:text	name="config.switchSettings.mstp.revision" /><font color="red"><s:text name="hm.common.required"/></font>
							</td>
							<td>
								<s:textfield name="dataSource.revision" size="24" id="revision"
								onkeypress="return hm.util.keyPressPermit(event,'integer');" /> 
								<s:text name="config.switchSettings.mstp.revision.range"/>
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="120" style="padding:6px 0px 4px 10px;">
								<s:text	name="config.switchSettings.mstp.maxhops" /><font color="red"><s:text name="hm.common.required"/></font>
							</td>
							<td>
								<s:textfield name="dataSource.hops" size="24" id="hops"
								onkeypress="return hm.util.keyPressPermit(event,'integer');" /> 
								<s:text name="config.switchSettings.mstp.maxhops.range"/>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="3" colspan="2"></td>
				</tr>
				<tr>
					<td><s:label id="tbl_errorMessage" /></td>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<div id="ahDataTableForMSTP"></div>
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