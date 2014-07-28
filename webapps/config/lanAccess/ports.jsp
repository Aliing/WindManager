<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<s:if test="%{jsonMode && contentShownInSubDrawer}">
<script type="text/javascript">
    <s:if test="%{writeDisabled!=''}">
    showHideNetworkPolicySubSaveBT(false);
    </s:if>
    <s:else>
    showHideNetworkPolicySubSaveBT(true);
    </s:else>
</script>
</s:if>
<s:elseif test="%{!jsonMode}">
<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/widget/ports.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<style>
input[type="radio"] {
    vertical-align:middle; 
    margin-top:-2px;
    margin-bottom:1px;
}
#OkBtn {
    margin-right: 15px;
}
#CancelBtn {
    margin-left: 15px;
}
div {
    font-family: Helvetica, Arial, sans-serif;
    font-size: 12px;
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
        document.writeln('<td class="crumb" nowrap><a href="<s:url action="dnsService" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
        <s:if test="%{dataSource.id == null}">
            document.writeln('New </td>');
        </s:if>
        <s:else>
            document.writeln('Edit \'<s:property value="changedName" />\'</td>');
        </s:else>
    </s:else>
}
</script>
</s:elseif>

<div id="content">
<s:form action="portConfigure">
    <s:hidden name="groupNum"/>
    <s:hidden name="jsonMode"/>
    <s:hidden name="contentShowType"/>
    <s:hidden name="editAdditionalSettings"/>
    <s:hidden name="selectIDs"/>
    <s:hidden name="currentPolicyID"/>
    <s:hidden name="portTemplateIndex"/>
    <s:if test="%{jsonMode}">
    <s:hidden name="id"/>
    </s:if>
    <s:if test="%{jsonMode == true && contentShownInDlg == true}">
    <div class="topFixedTitle" style="position: static;">
        <table border="0" cellspacing="0" cellpadding="0"  width="100%">
            <tr>
                <td align="left">
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td style="padding-left: 15px;"><img src="<s:url value="/images/hm_v2/profile/HM-icon-LAN_Profile.png" includeParams="none"/>"
                            width="40" height="40" alt="" class="dblk" />
                        </td>
                        <s:if test="%{dataSource.id == null}">
                        <td class="dialogPanelTitle"><s:text name="config.port.dialog.new.title"/></td>
                        </s:if>
                        <s:else>
                        <s:if test="%{editAdditionalSettings}">
                        <td class="dialogPanelTitle">Additional Port Settings: <s:property value='dataSource.name'/></td>
                        </s:if>
                        <s:else>
                        <td class="dialogPanelTitle"><s:text name="config.port.dialog.edit.title"/></td>
                        </s:else>
                        </s:else>
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
                            <td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onclick="closePortTemplPanel();return false;" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
                            <td width="20px">&nbsp;</td>
                            <s:if test="%{dataSource.id == null}">
                                <s:if test="%{writeDisabled == ''}">
                                    <td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="savePortTemplate('create');return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
                                </s:if>
                            </s:if>
                            <s:else>
                                <s:if test="%{writeDisabled == ''}">
                                    <td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="updatePortTemplate();return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
                                </s:if>
                            </s:else>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
    </s:if>
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
            <td>
            <s:if test="%{jsonMode}">
            <table cellspacing="0" cellpadding="0" border="0" width="700">
            <tr id="fe_ActionErrorRow" style="display: none">
            <td class="noteError" id="textfe_ActionErrorRow" colspan="4">To be changed</td>
            </tr>
            </s:if>
            <s:else>
            <table class="editBox" cellspacing="0" cellpadding="0" border="0" width="700">
            </s:else>
                <tr>
                    <td style="padding: 0px 5px 6px 5px;">
                    <table cellspacing="0" cellpadding="0" border="0" width="100%">
                        <tr>
                          <td ><div id="errNote" class="noteError" style="margin-left:10px"></div></td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <s:if test="%{!editAdditionalSettings}">
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
                                <tr><td height="4px;"/></tr>
                                <tr id="fe_ErrorRow" style="display: none">
                                    <td/>
                                    <td class="noteError" id="textfe_ErrorRow" colspan="4">To be changed</td>
                                </tr>
                                <tr>
                                    <td colspan="3" class="labelT1">
                                    <div class="inline"><s:text name="config.port.device.selection.note"/></div>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="3" class="labelT1">
                                    <div class="inline">
                                    <input id="deviceModelBtn" type="button" value="Device Models" /></div>
                                    <div id="devicesTypeSection" class="inline"></div>
                                    </td>
                                </tr>
                                <tr id="noteAPFunctionSection" style="display: none;">
                                    <td colspan="3" class="labelT1 npcNoteTitle">
                                    <s:text name="config.port.device.apfunction.note"/>
                                    </td>
                                </tr>
                                <tr id="noteConfigeSection" style="display: none;">
                                    <td class="npcNoteTitle" colspan="3" style="padding-left: 8px;padding-right: 0;"><s:text name="config.port.device.changed.note"/></td>
                                </tr>
                                </table>
                                </s:if>
                                <s:else>
                                <div id="additionalPortSettingsPannel" style="min-height: 300px;">
                                <table cellspacing="0" cellpadding="0" border="0" width="100%">
                                <s:if test="%{dataSource.existPortChannel}">
                                <tr id="loadBalanceSection">
                                    <td colspan="3" class="labelT1" style="padding-left:2px;">
                                    <label><s:text name="config.port.agg.loadblance.note"/>&nbsp;</label>
                                    <s:select name="dataSource.loadBalanceMode"
                                    list="%{enumLoadblanceMode}" listKey="key" listValue="value" 
                                    value="dataSource.loadBalanceMode" cssStyle="width: 300px;" />
                                    </td>
                                </tr>
                                </s:if>
                                <tr id="portsPse" style="display: <s:property value="pseDivStyle"/>">
                                    <td colspan="3">
                                   		<s:if test="dataSource.portPseProfiles.size > 0">
                                   			<tiles:insertDefinition name="portPseJson" />
                                    	</s:if>
                                        
                                    </td>
                                </tr>
                                <tr><td height="10px;"></td></tr>
                                <tr id="monitorSection">
                                	<td colspan="3">
		                                <s:if test="%{monitor}">
		                                	<tiles:insertDefinition name="portMirroring" />
		                                </s:if>
	                                </td>
                                </tr>
                                </table>
                                </div>
                                </s:else>
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
<tiles:insertDefinition name="deviceModelPanel" />
<!-- Functions -->
<script>
var oldDeviceModels = null;
function onloadEvent() {
	<s:if test="%{!editAdditionalSettings}">
	$('#deviceModelBtn').click(function(){
			DMPANEL.open();
	});
	</s:if>
	
	<s:if test="%{monitor && editAdditionalSettings}">
		var enableSourceVlan =  Get("enableSourceVlan").checked;
		var enableSourcePort = Get("enableSourcePort").checked;
		changeSourceVlans(enableSourceVlan);
		enablePortBased(enableSourcePort);
		
	</s:if>
	// update device settings
    var settings = "<s:property value='dataSource.portSetting'/>";
    if(settings.length > 0) {
        var settingsData = eval("("+settings+")");
        DMPANEL.update(settingsData);
        oldDeviceModels = settingsData.deviceModels;
	}
    
    $("#additionalPortSettingsPannel").attr("style","");
}
function validatePortForm() {
    // basic
    <s:if test="%{editAdditionalSettings}">
    	<s:if test="%{monitor}">
	    	if(!validateMirrorSession()){
	    		return false;
	    	}
    	</s:if>
    </s:if>
    <s:else>
    var nameElement = Get(formName + "_dataSource_name");
    if (nameElement.value.length == 0) {
        hm.util.reportFieldError(nameElement, 
                '<s:text name="error.requiredField"><s:param><s:text name="config.dnsService.name" /></s:param></s:text>');
        nameElement.focus();
        return false;
    }
    var deviceModelsEl = Get(formName + "_deviceModels");
    if(deviceModelsEl == null  || deviceModelsEl.value.trim().length == 0) {
        hm.util.reportFieldError({id: 'ErrorRow'}, 
        		'<s:text name="error.requiredField"><s:param>Device Models</s:param></s:text>');
    	return false;
    }
    </s:else>
    
    return true;
}
function updatePortTemplate() {
   <s:if test="%{dataSource.id != null && !editAdditionalSettings}">
   var $noteSection = $('#noteConfigeSection');
   if($noteSection.length) {
	   if($noteSection.css('display') == 'none') {
		   if(!isChangeMulti2SignleModel()) {
			   savePortTemplate('update');
		   }
	   } else {
		   // prompt
		   promptClearPortType();
	   }
   }
   </s:if>
   <s:else>
   savePortTemplate('update');
   </s:else>
}
function isChangeMulti2SignleModel() {
	if(oldDeviceModels && Get("portConfigure_deviceModels") && $.isArray(oldDeviceModels) && oldDeviceModels.length > 1) {
		var newDeviceModels = Get("portConfigure_deviceModels").value.split(",");
		if(newDeviceModels && newDeviceModels.length == 1) {
			var newDeviceModel = parseInt(newDeviceModels[0]);
			var text1 = "", text2 = "", text3 = "";
			for(var i=0;i<oldDeviceModels.length;i++) {
				var li = $('#deviceListSection').find('ul li#'+oldDeviceModels[i]);
				if(oldDeviceModels[i] == newDeviceModel) {
					text2 = li.text();
				} else {
					text3 += (text3.length==0 ? '' : ', ') + li.text(); 
				}
				text1 += (i==0 ? '' : i==oldDeviceModels.length-1 ? ' and ' : ', ') + li.text();
			}
			var message = '<s:text name="config.port.device.warning.note"><s:param>' + text1 + '</s:param><s:param>' + text2 + '</s:param><s:param>' + text3 + '</s:param></s:text>';
		    var cancelBtn = function(){
		        this.hide();
		    },
		    continueBtn = function(){
		        this.hide();
		        savePortTemplate('update');
		    },
		    mybuttons = [ { text:"OK", handler: continueBtn }, 
		                      { text:"Cancel", handler: cancelBtn, isDefault:true} ];
		    var dlg = userDefinedConfirmDialog(message, mybuttons, "Warning");
		    dlg.show();
			return true;
		}
	}
	return false;
}
function promptClearPortType() {
    var cancelBtn = function(){
        this.hide();
    },
    continueBtn = function(){
        this.hide();
        savePortTemplate('update');
    },
    mybuttons = [ { text:"OK", handler: continueBtn }, 
                      { text:"Cancel", handler: cancelBtn, isDefault:true} ];
    var dlg = userDefinedConfirmDialog('<s:text name="warn.port.template.devicesChange"/>', 
            mybuttons, "Warning");
    dlg.show();
}
var formName = 'portConfigure';
function submitAction(operation) {
    // normal mode
    if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
        showProcessing();
    }
    
    document.forms[formName].operation.value = operation;
    document.forms[formName].submit();
}
<s:if test="%{jsonMode}">
window.setTimeout("onloadEvent()", 100);
</s:if>
<s:else>
$(document).ready(onloadEvent);
</s:else>
</script>