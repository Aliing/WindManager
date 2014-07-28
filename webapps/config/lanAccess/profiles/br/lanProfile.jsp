<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<s:if test="%{jsonMode}">
<script type="text/javascript">
    <s:if test="%{writeDisabled!=''}">
    showHideNetworkPolicySubSaveBT(false);
    </s:if>
    <s:else>
    showHideNetworkPolicySubSaveBT(true);
    </s:else>
</script>
</s:if>
<s:else>
<style>
.npcNoteTitle {
    font-size: 11px;
    font-family: Helvetica, Arial, sans-serif;
    color: #6F6F6F;
    line-height: 15px;
    padding: 10px 0 0 10px !important;
}
</style>
<script type="text/javascript">
function insertPageContext() {
    <s:if test="%{lstTitle!=null && lstTitle.size>1}">
        document.writeln('<td class="crumb" nowrap>');
        <s:iterator value="lstTitle">
            document.writeln(" <s:property/> ");
        </s:iterator>
        document.writeln('</td>');
    </s:if>
    <s:else>
        document.writeln('<td class="crumb" nowrap><a href="<s:url action="portAccess" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
        <s:if test="%{dataSource.id == null}">
            document.writeln('New </td>');
        </s:if>
        <s:else>
            document.writeln('Edit \'<s:property value="changedName" />\'</td>');
        </s:else>
    </s:else>
}

</script>
</s:else>

<script type="text/javascript">
var formName = 'portAccess';

function submitAction(operation) {
	<s:if test="%{!jsonMode}">
		if (validate(operation)) {
			document.forms[formName].operation.value = operation;
			document.forms[formName].submit();
		}
	</s:if>
}

function validate(operation){
	if(operation == "create" || operation == "update"){
		if(!validatePortAccessForm()){
			return false;
		}
		
	}
	return true;
}

</script>

<div id="content">
<s:form action="portAccess">
    <s:if test="%{jsonMode}">
    	<s:hidden name="id"/>
    </s:if>
    <s:hidden name="normalView" />
    <s:hidden name="limitType" />
    <s:hidden name="portTemplateId" />
    <s:hidden name="portNum" />
    <s:hidden name="deviceType" />
    <s:hidden name="multiRef" />
    <s:hidden name="unchangeWAN" />
    <s:hidden name="tmpIndex" />
    <input id="oldPortType" type="hidden" value="<s:property value='dataSource.portType'/>"/>    
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <s:if test="%{!jsonMode}">
        <tr><td><tiles:insertDefinition name="context" /></td></tr>
        <tr>
            <td class="buttons">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <s:if test="%{dataSource.id == null}">
                        <td><input type="button" name="create"
                            value="<s:text name="button.create"/>" class="button"
                            onClick="submitAction('create<s:property value="lstForward"/>');"
                            <s:property value="writeDisabled" />></td>
                    </s:if>
                    <s:else>
                        <td><input type="button" name="update"
                            value="<s:text name="button.update"/>" class="button"
                            onClick="submitAction('update<s:property value="lstForward"/>');"
                            <s:property value="updateDisabled" />></td>
                    </s:else>
                    <s:if test="%{lstForward == null || lstForward == ''}">
                        <td><input type="button" name="cancel" value="Cancel"
                            class="button"
                            onClick="submitAction('<%=Navigation.L2_FEATURE_LAN%>');">
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
        <tr><td><tiles:insertDefinition name="notes" /></td></tr>
        <tr>
            <td style="padding-top: 5px;">
            <s:if test="%{jsonMode}">
            <table cellspacing="0" cellpadding="0" border="0" width="795">
            </s:if>
            <s:else>
            <table class="editBox" cellspacing="0" cellpadding="0" border="0" width="795">
            </s:else>
                <tr>
                    <td style="padding: 6px 5px 6px 5px;">
                    <table cellspacing="0" cellpadding="0" border="0" width="100%">
                        <tr>
                            <td colspan="4">
                            <table cellspacing="0" cellpadding="0" border="0" width="100%">
                                <tr>
                                    <td class="labelT1" width="125"><label><s:text
                                        name="config.dnsService.name" /><font color="red"><s:text name="*"/></font></label></td>
                                    <td colspan="2"><s:textfield name="dataSource.name"
                                        onkeypress="return hm.util.keyPressPermit(event,'name');"
                                        size="24" maxlength="%{profileNameLength}"
                                        disabled="%{disabledName}" />
                                        <s:text name="config.ns.name.range" /></td>
                                </tr>
                                <tr>
                                    <td class="labelT1" width="125"><s:text
                                        name="config.dnsService.description" /></td>
                                    <td colspan="2"><s:textfield name="dataSource.description"
                                        size="48" maxlength="%{profileDescirptionLength}" />&nbsp;<s:text
                                        name="config.ns.description.range" /></td>
                                </tr>
                                <tr>
                                    <td class="labelT1">Port Type</td>
                                    <td colspan="2" id="accessPortType">
                                    <s:iterator value="%{enumPortType}" status="portTypeStatus">
                                        <span <s:if test="!#portTypeStatus.last">style="padding-right: 15px;"</s:if>>
                                           <s:radio name="dataSource.portType" list="#{key: value}" listKey="key" listValue="value"/>
                                        </span>
                                    </s:iterator>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="labelT1" width="125"><s:text
                                        name="config.port.access.port.description" /></td>
                                    <td colspan="2"><s:textfield name="dataSource.portDescription"
                                        size="48" maxlength="%{profilePortDescirptionLength}"  onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>&nbsp;<s:text
                                        name="config.port.access.port.description.range" /></td>
                                </tr>
                                <tr><td height="4px;"/></tr>
                                <tr>
                                	<td class="labelT1">
                                		<s:checkbox id="shutDownPorts" name="dataSource.shutDownPorts" onclick="shutDownPortsSetting(this.checked);"/>
       										<label for="shutDownPorts"><s:text name="config.port.access.port.shutdown"/></label>
                                	</td>
                                </tr>
                                <tr><td height="4px;"/></tr>
                                <tr id="authSettingsContent">
                                    <td colspan="3">
                                        <tiles:insertDefinition name="lanAuthJson" />
                                    </td>
                                </tr>
                              
								<tr id="ConfigMdmContent">
									<td colspan="3" style="padding-top: 10px;" >
									<tiles:insertDefinition name="showConfigMdmAccess" />
									</td>
								</tr>
						 		
                                <tr id="portOptionalSettingsContent">
                                    <td colspan="3" style="padding-top: 10px;">
                                        <tiles:insertDefinition name="lanOptionalSettingJson" />
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
</s:form>
</div>
<s:if test="%{limitType != 3 && allowedTrial}">
<tiles:insertDefinition name="idmTrialSection" />
</s:if>
<script>
<s:if test="%{limitType != 3 && allowedTrial}">
var idmCustomerUrl = "<s:url action='portAccess' includeParams='none' />?operation=completeCustomer";    
var createIDMCustomerUrl = "<s:url action='portAccess' includeParams='none' />?operation=createIDMCustomer";    
var trialSettingsUrl = "<s:url action='portAccess' includeParams='none' />?operation=trialSettings" + "&ignore="+new Date().getTime();
</s:if>

var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event;
var formName = "portAccess";
var selectedPortType, firstLoad = true;
var firstClickFlag = -1;
function onloadEvent() {
	if(<s:property value="%{dataSource.enableMDM}"/>){
		Get(formName + "_dataSource_enableMDM").checked = true;	
	}else{
		Get(formName + "_dataSource_enableMDM").checked = false;	
		Get("enablemdmselect").style.display="none";
	}
    YUE.addListener([formName+'_dataSource_portType4', formName+'_dataSource_portType5', formName+'_dataSource_portType6'], 
            "click",  function(){
    	 /* used to identify update and new port type  start*/
        if(firstClickFlag > -1){
        	Get("shutDownPorts").checked = false;
        }
        firstClickFlag ++;
        /* used to identify update and new port type  end*/
        
        var curPortType = this.value;
        selectedPortType = curPortType;
       
        var $optionalContent = $("#portOptionalSettingsContent");
        var $authSequenceRow = $("#authSequenceRow");
       
        // authentication part
        var $authContent = $('#authSettingsContent');
        var authSetion = $('#authSection');
        var cwpSection = $('#cwpSection');
        
        var showConfigMdm=$('#ConfigMdmContent');
        
        if(curPortType == 4) {
            $authContent.show();
        	$authSequenceRow.show();
        	showConfigMdm.show();
        	$optionalContent.show();
        } else if(curPortType == 5){
            $authContent.hide();
        	$authSequenceRow.hide();
        	showConfigMdm.hide();
        	$optionalContent.show();
        } else {
            $authContent.hide();
        	$authSequenceRow.hide();
        	showConfigMdm.hide();
        	$optionalContent.hide();
        }
        if(firstLoad) {
            firstLoad = false;
        } else {
        	setPrimaryAuthStyle();
            resetCWP();
        }
    });
    // init event for authentication
    YUE.on("selectAuthOpt", "change", function(){
    	setAuthStyle(this.value);
    	checkIDMOpt();
    });
    YUE.on("enablePrimaryAuth", "click", function(){
    	setPrimaryAuthStyle(this.checked);
    	setSameVlanStyle();
    	if(this.checked){
    		Get("enabledSameVlan").checked = this.checked;
    	}
    });
    YUE.on(["enablePrimaryAuth", "enableSecondaryAuth"], "click", function(){
    	checkIDMOpt();
    });
    YUE.on("cwpChk", "click", function(){
        setSameVlanStyle();
        if(this.checked){
    		Get("enabledSameVlan").checked = this.checked;
    	}
    });
    YUE.on("enableIDMChk", "click", function(){
    	enabledIDM(this.checked);
    	setSameVlanStyle();
    	if(this.checked){
     		Get("enabledSameVlan").checked = this.checked;
     	}
    });
    YUE.on("enablePrimaryAuthHide", "click", function(){
    	if(!this.checked) {
    		Get('cwpChk').checked = true;	
    	}
    	Get("enabledSameVlan").checked = true;
    	setSameVlanStyle();
    	Get('cwpChk').disabled = !this.checked; 
    });
	
    // onload event
    var portTypeEl = Get(formName+"_dataSource_portType"+<s:property value="dataSource.portType"/>);
    if(portTypeEl) {
        portTypeEl.click();
    } else {
        portTypeEl = Get(formName+"_dataSource_portType4");
        if(portTypeEl) {
            portTypeEl.click();
        }
    }
    setPrimaryAuthStyle(<s:property value="dataSource.enabledPrimaryAuth"/>);

    enabledIDM(<s:property value="dataSource.enabledIDM"/>);
    setSameVlanStyle();
    <s:if test="%{usedFlag}">
    // disable the radio buttons
    if(Get('accessPortType')) {
        $('#accessPortType input[name="dataSource.portType"]').attr("disabled", true);
    }
    </s:if>
  	//control the show/hide port type setting by shutDown checkbox
	shutDownPortsSetting(Get("shutDownPorts").checked);
  	
    <s:if test="%{limitType != 3 && allowedTrial}">
    initTrialLink();
    new YAHOO.widget.Tooltip('explaination', {context: 'idmexplaination', width: "350px", container: 'portAccess'});
    </s:if>
}
function setSameVlanStyle() {
	var selected = false;
    if(Get('enablePrimaryAuth')) {
    	selected = selected || Get('enablePrimaryAuth').checked;
    }
    if(Get('enablePrimaryAuthHide')){
    	selected = selected || Get('enablePrimaryAuthHide').checked;
    }
    if(Get('enableSecondaryAuth')) {
    	selected = selected || Get('enableSecondaryAuth').checked;
    }
    if(Get('cwpChk')){
    	selected = selected || Get('cwpChk').checked;
    }
    if(selected) {
        YUD.setStyle('sameVlanSection', 'display', '');
    } else {
    	YUD.setStyle('sameVlanSection', 'display', 'none');
    }
}
function setPrimaryAuthStyle(flag) {
	if(flag) {
		if(Get('secondaryAuthRow')) {
			$('#secondaryAuthRow').show();
		}
	} else {
		if(Get('secondaryAuthRow')) {
			$('#secondaryAuthRow').hide();
		}
		resetAuthSection();
	}
}
function setAuthStyle(flag) {
    if(flag == 1) {
        $('#macAuthSection').appendTo('#secondaryAuthRow');
        $('#secondaryAuthRow span.show48021x').hide();
        $('#secondaryAuthRow span.show4MAC').show();
    } else {
        $('#macAuthSection').appendTo('#primaryAuthRow');
        $('#secondaryAuthRow span.show48021x').show();
        $('#secondaryAuthRow span.show4MAC').hide();
    }
}
function resetAuthSection() {
    if(Get('enablePrimaryAuth')) {
        Get('enablePrimaryAuth').checked = false;
    }
    if(Get('enableSecondaryAuth')) {
        Get('enableSecondaryAuth').checked = false;
    }
    if(Get(formName+"_dataSource_authProtocol")
    		&& Get('macAuthSection')) {
    	if($('#macAuthSection').parent().attr('id') == 'secondaryAuthRow') {
    		Get(formName+"_dataSource_authProtocol").options[0].selected = true;
    	}
    }
}
function resetCWP() {
    if(Get('cwpChk')) {
        Get('cwpChk').checked = false;
    }
}
function validatePortAccessForm() {
    // basic
    var nameElement = Get(formName + "_dataSource_name");
    if (nameElement.value.length == 0) {
        hm.util.reportFieldError(nameElement, 
        		'<s:text name="error.requiredField"><s:param><s:text name="config.dnsService.name" /></s:param></s:text>');
        nameElement.focus();
        return false;
    }
    
    var portDescription = Get(formName + "_dataSource_portDescription");
    if(portDescription.value.length > 0){
    	var message = hm.util.validateStringWithBlank(portDescription.value, '<s:text name="config.port.access.port.description" />');
    	if (message != null) {
    		hm.util.reportFieldError(portDescription, message);
    		portDescription.focus();
        	return false;
    	}
    }
    
    if($("#portAccess_dataSource_enableMDM").attr("checked") == "checked" 
		&& $("#portAccess_dataSource_portType4").attr("checked") == "checked"){
		if($("#portAccess_configmdmId").val() == "-1"){
			hm.util.reportFieldError(document.getElementById("portAccess_configmdmId"),
					'<s:text name="warn.port.access.mdm.invalid.message"/>');
			document.getElementById("portAccess_configmdmId").focus();
			return false;
		}
	}
    var oldTypeEl = Get("oldPortType"), unchangeWANEl = Get(formName + "_unchangeWAN");
    if(oldTypeEl && unchangeWANEl) {
        if(unchangeWANEl.value === 'true'  && oldTypeEl.value == 6) {
            showWarnDialog('<s:text name="warn.port.access.wan.unchange"/>');
            return false;
        }
    }
    
    return true;
}
function promptPortTypeChanged() {
    var oldTypeEl = Get("oldPortType"), multiUsedEl = Get(formName + "_multiRef");
    if(oldTypeEl && multiUsedEl && selectedPortType) {
        if(multiUsedEl.value === 'true' && oldTypeEl.value != selectedPortType) {
            return true;
        }
    }
    return false;
}
function shutDownPortsSetting(checked){
	 if(selectedPortType == 4){
		 $('#authSettingsContent').toggle(!checked);
		 $('#ConfigMdmContent').toggle(!checked);
		 $('#portOptionalSettingsContent').toggle(!checked);
	 }else if(selectedPortType == 5){
		 $('#portOptionalSettingsContent').toggle(!checked);
	 }else{
		 $('#authSettingsContent').hide();
		 $('#ConfigMdmContent').hide();
		 $('#portOptionalSettingsContent').hide();
	 }
}
/*IDM support*/
function showIDMIcon(flag) {
   if(Get("manageGuestIDMAnchor")) {
        Get("manageGuestIDMAnchor").style.display = (flag ? "" : "none");
    }
}
function resetIDM() {
	showIDMIcon(false);
	if(Get("enableIDMChk")) {
		Get("enableIDMChk").checked = false;
	}
}
function disabledIDM(flag) {
	if(flag) {
		resetIDM();
	}
	if(Get("enableIDMChk")) {
		Get("enableIDMChk").disabled = flag;
	}
}
function checkIDMOpt() {
    if(Get('selectAuthOpt')) {
	    var selected = false;
		if((Get('selectAuthOpt').value == 1 && 
				Get('enablePrimaryAuth') && Get('enablePrimaryAuth').checked
				&& Get('enableSecondaryAuth') && Get('enableSecondaryAuth').checked) //802.1x
				|| (Get('selectAuthOpt').value == 2 && Get('enablePrimaryAuth') && Get('enablePrimaryAuth').checked)) {//MAC
			disabledIDM(true);
		} else {
			disabledIDM(false);
		}
    }
}
function show8021XOnly(flag) {
	if(Get('8021xAuthRow')) {
		var selected = false;
		if(flag) {
			selected = Get('enablePrimaryAuth').checked;//802.1x was checked for non-IDM
			$('#8021xAuthRow').show();
			//reset
			Get('selectAuthOpt').options[0].selected = true;
			setAuthStyle(1);
			resetAuthSection();
		} else {
			selected = Get('enablePrimaryAuthHide').checked;//802.1x was checked for IDM
	        $('#8021xAuthRow').hide();
			//reset
	        $('#enablePrimaryAuthHide').attr('checked', false);
	    }
		$('#enablePrimaryAuthHide, #primaryAuthHide').attr('disabled', !flag);
		$('#enablePrimaryAuth, #enableSecondaryAuth, #selectAuthOpt').attr('disabled', flag);
		if(flag) {
			$('#primaryAuthRow, #secondaryAuthRow').css('display', 'none');
			if(selected) $('#enablePrimaryAuthHide').attr('checked', true);
		} else {
			$('#primaryAuthRow').css('display', '');
			if(selected && !Get('enablePrimaryAuth').checked) $('#enablePrimaryAuth').click();
		}
		
		Get('cwpChk').disabled = false;
        // if 802.1x is not selected, then CWP must be selected
        if(flag && (Get('enablePrimaryAuthHide') && !Get('enablePrimaryAuthHide').checked) && Get('cwpChk')) {
        	Get('cwpChk').checked = true;
            Get('cwpChk').disabled = true; 
        }
	}
}
function checkMACOpt() {
	if(Get("enableIDMChk")) {
		show8021XOnly(Get("enableIDMChk").checked);
	}
}
function enabledIDM(flag) {
    showIDMIcon(flag);
    checkMACOpt();
}
<s:if test="%{jsonMode}">
window.setTimeout("onloadEvent()", 100);
</s:if>
<s:else>
YUE.onDOMReady(onloadEvent);
</s:else>
</script>