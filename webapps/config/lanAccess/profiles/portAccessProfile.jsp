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
    padding: 0 0 0 25px;
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

var formName = 'portAccess';
function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
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
</s:else>
<div id="content">
<s:form action="portAccess">
    <s:hidden name="normalView" />
    <s:hidden name="limitType" />
	<s:hidden name="portTemplateId" />
	<s:hidden name="portNum" />
	<s:hidden name="deviceType" />
	<s:hidden name="multiRef" />
	<s:hidden name="unchangeWAN" />
	<s:hidden name="tmpIndex" />
	<s:if test="%{jsonMode}">
		<s:hidden name="id"/>
	</s:if>
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
                            onClick="submitAction('<%=Navigation.L2_FEATURE_PORTTYPE%>');">
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
                                        <s:if test="%{key == 6 && disabledWAN}">
                                        </s:if>
                                        <s:else>
	                                    <span <s:if test="!#portTypeStatus.last">style="padding-right: 15px;"</s:if>>
	                                       <s:radio name="dataSource.portType" list="#{key: value}" listKey="key" listValue="value"/>
	                                    </span>
                                        </s:else>
                                    </s:iterator>
                                    </td>
                                </tr>
                                 <tr>
                                    <td class="labelT1" width="125"><s:text
                                        name="config.port.access.port.description" /></td>
                                    <td colspan="2"><s:textfield name="dataSource.portDescription"
                                        size="24" maxlength="%{profilePortDescirptionLength}" onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />&nbsp;<s:text
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
                                <tr>
                                    <td colspan="3">
                                        <div id="profilesSection"></div>
                                    </td>
                                </tr>
                                
                                <tr id="authSettingsContent">
									<td colspan="3">
										<tiles:insertDefinition name="portAuthJson" />
									</td>
								</tr>
								
                                <tr id="qosSettingsContent" style="display: <s:property value="%{dataSource.qosSettingsContentStyle}"/>">
									<td colspan="3" style="padding-top: 10px;">
										<tiles:insertDefinition name="portQosJson" />
									</td>
								</tr>
								
							    <tr id="portOptionalSettingsContent">
									<td colspan="3" style="padding-top: 10px;">
										<tiles:insertDefinition name="portOptionalSettingJson" />
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
<script>
var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event;
var formName = "portAccess";
var selectedPortType, firstLoad = true;
var firstClickFlag = -1;
function onloadEvent() {
	YUE.addListener([formName+'_dataSource_portType1', 
                                  formName+'_dataSource_portType2', 
                                  formName+'_dataSource_portType3', 
                                  formName+'_dataSource_portType4', 
                                  formName+'_dataSource_portType5', 
                                  formName+'_dataSource_portType6'], 
            "click",  function(){
        /* used to identify update and new port type  start*/
        if(firstClickFlag > -1){
        	Get("shutDownPorts").checked = false;
        	shutDownPortsSetting(Get("shutDownPorts").checked);
        }
        firstClickFlag ++;
        /* used to identify update and new port type  end*/
        
        var curPortType = this.value;
        selectedPortType = curPortType;

        var $qosSettingsContent = $("#qosSettingsContent");
        var $qosSettings4WanDiv = $("#qosSettings4WanDiv");
        var $qosSettingsDiv = $("#qosSettingsDiv");
        var $optionContent = $("#portOptionalSettingsContent");
        
        var oldPortType = Get("oldPortType");
        if(oldPortType.value == 3 && curPortType != 3){
        	mybuttons = [ { text:"OK", handler: function(){
        		this.hide();
        	} }, 
    	   { text:"Cancel",isDefault:true, handler: function(){
    		   Get(formName+'_dataSource_portType3').click();
		       this.hide();
	    	 }
    	   }];
    	   
    	    mirrorTypeChangeMsg = "<html><body>"+'<s:text name ="warn.port.access.change.portType.mirror" />' +"</body></html>";
    	    dlg = userDefinedConfirmDialog(mirrorTypeChangeMsg, mybuttons, "Warning");
    	    dlg.show();
        }
        
       if(curPortType == 6){ // WAN
        	$qosSettingsDiv.hide();
        	$qosSettings4WanDiv.hide();//hide this div first 
        	$qosSettingsContent.hide();//hide this div first 
        	
        	$optionContent.hide();
        } else if(curPortType == 3){ // Monitor
        	$qosSettingsDiv.hide();
        	$qosSettings4WanDiv.hide();
        	$qosSettingsContent.hide();
        	
        	$optionContent.hide();
        } else {
        	$qosSettingsDiv.show();
        	$qosSettings4WanDiv.hide();
        	$qosSettingsContent.show();
        	
        	$optionContent.show();
        }
        
        // authentication part
        var authContent = $('#authSettingsContent');
        var authSetion = $('#authSection');
        var apSection = $('#apAuthenSection');
        var cwpSection = $('#cwpSection');
        var cwpNoteSection = $('#cwpNoteSection');
        if(curPortType == 1) {
        	authContent.show();
        	authSetion.show();
        	apSection.hide();
        	if(cwpSection.length) {
        	    cwpSection.hide();
        	    cwpNoteSection.hide();
        	}
        } else if(curPortType == 2) {
        	authContent.show();
        	apSection.show();
        	authSetion.hide();
        	if(cwpSection.length) {
        	    cwpSection.hide();
        	    cwpNoteSection.hide();
        	}
        } else if(curPortType == 4) {
            authContent.show();
            authSetion.show();
            if(cwpSection.length) {
                cwpSection.show();
                cwpNoteSection.show();
            }
            apSection.hide();
        } else {
            authContent.hide();
            apSection.hide();
            authSetion.hide();
            if(cwpSection.length) {
                cwpSection.hide();
                cwpNoteSection.hide();
            }
        }
        if(firstLoad) {
        	firstLoad = false;
        } else {
	        resetAPAuthetication();
	        resetAuthSection();
	        resetCWP();
	        resetClientReport();
        }
        setSameVlanStyle();
    });
	// init event for authentication
    YUE.on("selectAuthOpt", "change", function(){
        setAuthStyle(this.value);
    });
    YUE.on("enablePrimaryAuth", "click", function(){
        setPrimaryAuthStyle(this.checked);
        setSameVlanStyle();
        if(this.checked){
        	Get("enabledSameVlan").checked = this.checked;
        }
    });
    YUE.on("cwpChk", "click", function(){
        setSameVlanStyle();
        if(this.checked){
        	Get("enabledSameVlan").checked = this.checked;
        }
    });
    /*YUE.on(["enabledSameVlan", "cwpChk"], "click", function(){
		if(selectedPortType == 4) {
			 if(this.id == "enabledSameVlan" && this.checked) {
				resetCWP();
			} else {
				resetSameVlan();
			} 
			setSameVlanStyle();
		}
	});*/
	
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
	setSameVlanStyle();
	
	<s:if test="%{usedFlag}">
	// disable the radio buttons
	if(Get('accessPortType')) {
		$('#accessPortType input[name="dataSource.portType"]').attr("disabled", true);
	}
	</s:if>
	
	//control the show/hide port type setting by shutDown checkbox
	shutDownPortsSetting(Get("shutDownPorts").checked);
}
function setSameVlanStyle() {
	var selected = false;
    if(Get('enablePrimaryAuth')) {
    	selected = selected || Get('enablePrimaryAuth').checked;
    }
    if(Get('enableSecondaryAuth')) {
    	selected = selected || Get('enableSecondaryAuth').checked;
    }
    if(Get('cwpChk')){
    	selected = selected || Get('cwpChk').checked;
    }
    if(selected) {
        if(selectedPortType && selectedPortType == 4) {
            YUD.setStyle('sameVlanSection', 'display', '');
        }else{
        	YUD.setStyle('sameVlanSection', 'display', 'none');
        }
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
	if(Get("selectAuthOpt")) {
		Get("selectAuthOpt").options[0].selected = true;
		setAuthStyle(1);
	}
	
    if(Get('enablePrimaryAuth')) {
        Get('enablePrimaryAuth').checked = false;
    }
    if(Get('secondaryAuthRow')) {
        $('#secondaryAuthRow').hide();
    }
    if(Get('enableSecondaryAuth')) {
        Get('enableSecondaryAuth').checked = false;
    }
    if(Get(formName+"_dataSource_authProtocol")) {
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
function resetSameVlan() {
	if(Get('enabledSameVlan')) {
	    Get('enabledSameVlan').checked = false;
	}
}
function resetAPAuthetication() {
   if(Get('apAuthChk')) {
        Get('apAuthChk').checked = false;
    }
}
function resetClientReport() {
	if(Get('enabledClientReport')) {
		if(selectedPortType == 1 || selectedPortType == 4) {
			// access=4, phone&data=1
			Get('enabledClientReport').checked = true;
		} else {
			Get('enabledClientReport').checked = false;
		}
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
    
    var oldTypeEl = Get("oldPortType"), unchangeWANEl = Get(formName + "_unchangeWAN");
    if(oldTypeEl && unchangeWANEl) {
        if(unchangeWANEl.value === 'true'  && oldTypeEl.value == 6) {
        	showWarnDialog('<s:text name="warn.port.access.wan.unchange"/>');
            return false;
        }
    }
    
    if(!validateQos()){
    	return false;
    }
    
    return true;
}
function promptPortTypeChanged() {
	var oldTypeEl = Get("oldPortType"), multiUsedEl = Get(formName + "_multiRef");
	if(oldTypeEl && multiUsedEl && selectedPortType) {
		if(multiUsedEl.value === 'true'  && oldTypeEl.value != selectedPortType) {
			return true;
		}
	}
	return false;
}

function enableQosSettingsContent(checked){
	if(checked){
		$("tr[id=qosSettingsContent]").find("input,select").each(function(){
			 if(typeof(this.disabled) != "underfined"){
					this.disabled = true;
			 }
	 	});
	} else {
		$('input[name="dataSource.qosClassificationMode"]').attr("disabled",false);
		var qosClassificationMode = $('input[name="dataSource.qosClassificationMode"]:checked');
		clickQosClassificationMode(qosClassificationMode.val());
		$('#'+formName+"_dataSource_enableQosMark").attr("disabled",false);
		
		chickQosMark($('#'+formName+"_dataSource_enableQosMark").attr("checked"));
	}
	
}

function shutDownPortsSetting(checked){
	 if(selectedPortType != 3 && selectedPortType != 6){
		//change from hide to disable the all the descendant elements
		 /*$('#portOptionalSettingsContent').toggle(!checked);
		 $('#authSettingsContent').toggle(!checked);
		 $('#qosSettingsContent').toggle(!checked); */
		 
		 $("tr[id=authSettingsContent]").find("*").each(function(){
			 if(typeof(this.disabled) != "underfined"){
					if(checked){
						this.disabled = true;
					}else{
						this.disabled = false;
					}
			  }
	 	});
		
		//fix bug 32474
		enableQosSettingsContent(checked);
		
	    $("tr[id=portOptionalSettingsContent]").find("*").each(function(){
			 if(typeof(this.disabled) != "underfined"){
				if(checked){
					this.disabled = true;
				}else{
					this.disabled = false;
				}
			 }
		});
		 
	 }else{
		 $('#portOptionalSettingsContent').hide();
		 $('#authSettingsContent').hide();
		 $('#qosSettingsContent').hide();
	 }
	 
}
<s:if test="%{jsonMode}">
window.setTimeout("onloadEvent()", 100);
</s:if>
<s:else>
YUE.onDOMReady(onloadEvent);
</s:else>
</script>