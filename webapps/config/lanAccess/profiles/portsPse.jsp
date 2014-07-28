<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
function clickPseProfile(operation,index,length){
	var url = '<s:url action="portConfigure" includeParams="none"/>' 
		+ '?operation='+operation
		+ '&jsonMode=true'
		+ '&ignore=' + new Date().getTime();
	
		var parenetDomId = 'portPseProfileIds_'+index;
		if(length && length > 0){
			for(var i=0;i<length;i++){
				if(i!=index){
					parenetDomId += ',portPseProfileIds_'+i;
				}
			}
		}
		
		if (operation == 'editPseProfile'){
			url = url + "&pseProfileId=" + $("#portPseProfileIds_"+index).val() + "&parentDomID=" + parenetDomId;
		} else if (operation == 'newPseProfile'){
			url = url + "&parentDomID=" + parenetDomId;
		} 
		
		openIFrameDialog(880, 450, url);
	
}

function clickEnabelIfPseCheckBox(index,length){
	var writeDisabled = '<s:property value="writeDisabled"/>';
	if(Get('arrayEnabelIfPse_' + index).checked ){
		Get('iterfaceNum_' + index).disabled = false;
		Get('portPseProfileIds_' + index).disabled = false;
		var newAction = "javascript:clickPseProfile('newPseProfile',"+index+","+length+")";
		var editAction = "javascript:clickPseProfile('editPseProfile',"+index+","+length+")";
		if(writeDisabled != 'disabled'){
			enableLink(document.getElementById("newPseProfileId_"+ index),newAction);
			enableLink(document.getElementById("editPseProfileId_"+ index),editAction);
			changeImage(document.getElementById("newPseProfileImgId_"+ index),'<s:url value="/images/new.png" />');
			changeImage(document.getElementById("editPseProfileImgId_"+ index),'<s:url value="/images/modify.png" />');
		}
		
	} else {
		Get('iterfaceNum_' + index).disabled = true;
		Get('portPseProfileIds_' + index).disabled = true;
		if(writeDisabled != 'disabled'){
			disableLink(document.getElementById("newPseProfileId_"+ index));
			disableLink(document.getElementById("editPseProfileId_"+ index));
			changeImage(document.getElementById("newPseProfileImgId_"+ index),'<s:url value="/images/new_disable.png" />');
			changeImage(document.getElementById("editPseProfileImgId_"+ index),'<s:url value="/images/modify_disable.png" />');
		}
	}
}

function changeImage(image,src) {
	image.src=src
}

function disableLink(link) {
	link.setAttribute("disabled",true);
	link.removeAttribute('href');
}

function enableLink(link,href) {
	link.setAttribute("disabled",false);
	link.setAttribute("href",href);
}
</script>
			

<div id="pseSettingAllDiv">
<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.brRouter.pse.settings" />','pseSettingDiv');</script></td>
	</tr>
	<tr>
		<td>
			<div id="pseSettingDiv" style="overflow:auto;height:320px;display: <s:property value="%{dataSource.pseSettingsDisplayStyle}"/>">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr><td height="10px"/>
					</tr>
					<tr>
						<td valign="top" style="padding-left: 15px">
							<table cellspacing="0" cellpadding="0" border="0" width="100%"  class="view">
								<tr>
									<td colspan="3" class="noteInfo">
										<s:text name="config.pse.prot.note"></s:text>
									</td>
								</tr>
								<tr>
									<th align="left"><s:text name="config.pse.port.interface" /></th>
									<th align="left"><s:text name="config.pse.port.Enable"/></th>
									<th align="left"><s:text name="config.pse.port.profile"/></th>
								</tr>
								<s:iterator value="%{dataSource.portPseProfiles}" status="status" id="portPseProfiles">
									<tiles:insertDefinition name="rowClass" />
									<tr class="<s:property value="%{#rowClass}"/>">
										<td class="list"><s:property value="interfaceName" />
											<s:hidden name="arrayInterfaceNum"
													id="iterfaceNum_%{#status.index}"
													disabled="%{!#portPseProfiles.enabelIfPse}"
													value="%{#portPseProfiles.interfaceNum}" />
										</td>
										<td class="list"><s:checkbox name="arrayEnabelIfPse"
													value="%{#portPseProfiles.enabelIfPse}"
													id="arrayEnabelIfPse_%{#status.index}"
													fieldValue="%{interfaceNum}"
													onclick="clickEnabelIfPseCheckBox(%{#status.index},%{dataSource.portPseProfiles.size()});"></s:checkbox></td>
										<td class="list">
											<s:select name="portPseProfileIds" id="portPseProfileIds_%{#status.index}"
												list="%{availablePortPseProfile}" value="%{#portPseProfiles.pseProfile.id}" 
												disabled="%{!#portPseProfiles.enabelIfPse}"
												listKey="id" listValue="value" cssStyle="width: 140px;" />
												<s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
												</s:if>
												<s:else>
													<s:if test="%{#portPseProfiles.enabelIfPse}">
														<a class="marginBtn" 
															id="newPseProfileId_<s:property value='%{#status.index}'/>"
															href="javascript:clickPseProfile('newPseProfile',<s:property value="%{#status.index}"/>,<s:property value="%{dataSource.portPseProfiles.size()}"/>);">
														<img class="dinl" src="<s:url value="/images/new.png" />" 
															id="newPseProfileImgId_<s:property value='%{#status.index}'/>"
															width="16" height="16" alt="New" title="New" /></a>
													</s:if>
													<s:else>
														<a class="marginBtn" 
														   id="newPseProfileId_<s:property value='%{#status.index}'/>">
														<img class="dinl" id="newPseProfileImgId_<s:property value='%{#status.index}'/>"
															src="<s:url value="/images/new_disable.png" />"
															width="16" height="16" alt="New" title="New" /></a>
													</s:else>
												</s:else> 
												<s:if test="%{writeDisabled == 'disabled'}" >
													<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
												</s:if> 
												<s:else>
													<s:if test="%{#portPseProfiles.enabelIfPse}">
														<a class="marginBtn" 
															id="editPseProfileId_<s:property value='%{#status.index}'/>"
															href="javascript:clickPseProfile('editPseProfile',<s:property value="%{#status.index}"/>,<s:property value="%{dataSource.portPseProfiles.size()}"/>);">
														<img class="dinl" src="<s:url value="/images/modify.png" />" id="editPseProfileImgId_<s:property value='%{#status.index}'/>"
															width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:if>
													<s:else>
														<a class="marginBtn" 
														   id="editPseProfileId_<s:property value='%{#status.index}'/>">
														<img class="dinl"
															src="<s:url value="/images/modify_disable.png" />" id="editPseProfileImgId_<s:property value='%{#status.index}'/>"
															width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</s:else>
										</td>
									</tr>
								</s:iterator>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
</table>
</div>

	