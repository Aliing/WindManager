<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<div>
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="500px">
				 <p class="dialogPanelTitle" style="margin-top: 0;">
				 	Additional Mirror Port Settings for "<s:property value='dataSource.name'/>"
				 </p>
			</td>
			<td align="right" style="margin-top: 0px;width:200px">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);"
							class="btCurrent" onclick="closeMirrorPannel();return false;"
							title="<s:text name="common.button.cancel"/>"><span
								style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text
										name="common.button.cancel" /></span></a></td>
						<td width="20px">&nbsp;</td>
						<s:if test="%{writeDisabled == ''}">
							<td class="npcButton"><a href="javascript:void(0);"
								class="btCurrent"
								onclick="saveMirrorSettings();return false;"
								title="<s:text name="common.button.save"/>"><span
									style="padding-bottom: 2px; padding-top: 2px;"><s:text
											name="common.button.save" /></span></a></td>
						</s:if>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
<s:form id="mirrorSettings">
<s:hidden name="id"/>
<s:hidden name="portNum" />
<s:hidden name="deviceType" />
<s:hidden name="accessProfileId"/>
<div>
<tiles:insertDefinition name="portMirroring" />
</div>
</s:form>
<script>
$(document).ready(window.setTimeout("loadTableItem()", 100));

function loadTableItem(){
	/* var enableSourceVlan = eval('<s:property escape="false" value="enableSourceVlan"/>');
	var destinationInterface = eval('<s:property escape="false" value="destinationInterface"/>');
	if(enableSourceVlan && typeof destinationInterface){
		$("#destinationInterface").attr("value", destinationInterface);
		$("#enableSourceVlan").attr("value", enableSourceVlan);
		$("#destinationList").attr("value", destinationInterface);
	}
	
	if(typeof enableSourceVlan && enableSourceVlan == true){
		hiddenAllTableItem(destinationInterface);
	}else{
		showAllTableItem();
	} */
	
	var enableSourceVlan = eval('<s:property escape="false" value="enableSourceVlan"/>');
	var enableSourcePort = eval('<s:property escape="false" value="enableSourcePort"/>');
	
	changeSourceVlans(enableSourceVlan);
	enablePortBased(enableSourcePort);
		
}
	
function saveMirrorSettings() {
	//TODO
    if(!validateMirrorSession()){
    	return false;
    }
	
	if(mirrorPanel) {
		var url = "<s:url action='portConfigure' includeParams='none' />?operation=updateMirrorSettings&ignore="+new Date().getTime() ;
		 YAHOO.util.Connect.setForm(document.getElementById("mirrorSettings"));
		    var transaction = YAHOO.util.Connect.asyncRequest('post', url,
		            {success : saveMirrorPort, failure : resultDoNothing, timeout: 60000}, null);
	}
}

function saveMirrorPort(){
	mirrorPanel.closeDialog();
	var portTemplateEl = Get("mirrorSettings_id");
	fetchConfigTemplate2Page(true, false, portTemplateEl ? "&expandPortTemplateId="+portTemplateEl.value : "");
	Get('portMirrorPanel').innerHTML = '';
}

function closeMirrorPannel(){
	var portNum = 0, deviceType = 0, portTemplateId = 0, lanId = 0;
	
	if (Get('mirrorSettings_portNum')) {
		portNum = Get('mirrorSettings_portNum').value;
	}
	if (Get('mirrorSettings_deviceType')) {
		deviceType = Get('mirrorSettings_deviceType').value;
	}
	if (Get('mirrorSettings_id')) {
		portTemplateId = Get('mirrorSettings_id').value;
	}
	if(Get("mirrorSettings_accessProfileId")){
		lanId = Get("mirrorSettings_accessProfileId").value;
	}
	
	
	
	
	if (mirrorPanel) {
		mirrorPanel.closeDialog();
		addRemoveAccess(null, portTemplateId, deviceType, portNum,
				lanId ? "&selectedAccessId=" + lanId : null);
		Get('portMirrorPanel').innerHTML = '';
	}
}
</script>
