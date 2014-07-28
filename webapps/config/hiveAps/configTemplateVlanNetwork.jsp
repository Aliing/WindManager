<%@taglib prefix="s" uri="/struts-tags"%>

<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<div>
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td class="npcHead1" style="padding-left: 35px; cursor: pointer;"
							 onclick="modifyVlanNetworkMapping();"><img id="modifyVlanNetworkMappingIcon"
							src="<s:url value="/images/expand_minus.gif" includeParams="none"/>"
							alt="Show/Hide Option" class="expandImg" style="display: inline" />&nbsp;<s:text
								name="config.configTemplate.vlanMap.title" /></td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
	<!--  tr>
		<td>
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
				<td class="npcHead1" style="padding-left: 35px;padding-right: 20px;"><s:text name="config.configTemplate.vlanMap.title"/></td>
				<s:if test="%{writeDisabled != 'disabled'}">
					<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" id="btModifyVlanNetworkMapping" style="visibility: hidden;" onclick="modifyVlanNetworkMapping();" title="Modify"><span id="modifyVlanNetworkMappingSpan"><s:text name="config.configTemplate.vlanMap.modify"/></span></a></td>
				</s:if>
				<s:else>
					<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="showWarmMessage();" title="<s:text name="config.configTemplate.vlanMap.modify"/>"><span><s:text name="config.configTemplate.vlanMap.modify"/></span></a></td>
				</s:else>
				</tr>
			</table>
		</td>
	</tr>
	-->
	<tr id="vlanNetworkMappingDetailTR">
		<td style="padding-left: 60px">
			<table cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td class="npcHead2" width="35px">&nbsp;</td>
					<td class="npcHead2" width="150px" colspan="2"><s:text name="config.configTemplate.vlanMap.vlan"/></td>
					<td class="npcHead2" width="250px"><s:text name="config.configTemplate.vlanMap.network"/></td>
					<td class="npcHead2" width="115px"><s:text name="config.configTemplate.vlanMap.dhcp"/></td>
					<td class="npcHead2" width="150px"><s:text name="config.configTemplate.vlanMap.websec"/></td>
				</tr>
				<s:iterator value="%{fetchVlanNetworkMapping}" id="vlanNetworkMapping">
				<tr class="npcListBlock">
					<td>
						<s:if test="%{writeDisabled != 'disabled'}">
							<s:if test="%{#vlanNetworkMapping.blnMgtVlan==false}">
								<a class="marginBtn" href="javascript: void(0);" onclick="removeSelectVlanMapping(<s:property value="%{#vlanNetworkMapping.vlan.id}"/>);">
                               	<img src="<s:url value="/images/ahdatatable/hm-trash.png" />"
                               	 	width="16" height="16" alt="Remove" title="Remove" class="dinl"/></a>
							</s:if>
						</s:if>
					</td>
					<td class="imageTd"><img src="<s:url value="/images/hm_v2/profile/hm-icon-vlan.png" />" width="30" height="30" alt="Vlan" title="Vlan" /></td>
					<td class="npcLinkA" style="padding-left: 2px">
						<s:if test="%{writeDisabled != 'disabled'}">
							<a class="npcLinkA" href="javascript:void(0);" onclick='editVlanNetworkMappingVlan(<s:property value="%{#vlanNetworkMapping.vlan.id}"/>)'>
							<s:property value="%{#vlanNetworkMapping.vlan.vlanName}"/></a>
						</s:if>
						<s:else>
							<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();">
							<s:property value="%{#vlanNetworkMapping.vlan.vlanName}"/></a>
						</s:else>
					</td>
					<s:if test="%{#vlanNetworkMapping.networkObj != null}">
						<td>
							<s:if test="%{writeDisabled != 'disabled'}">
								<a class="npcLinkA" href="javascript:void(0);" onclick='editVlanNetworkMappingNetwork(<s:property value="%{#vlanNetworkMapping.vlan.id}"/>,<s:property value="%{#vlanNetworkMapping.networkObj.id}"/>)'>
								<s:property value="%{#vlanNetworkMapping.networkObj.networkName}" /></a>
							</s:if>
							<s:else>
								<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();">
								<s:property value="%{#vlanNetworkMapping.networkObj.networkName}" /></a>
							</s:else>
							<span class="normalSpan" >&nbsp;(<s:property value="%{#vlanNetworkMapping.networkObj.networkTypeString}" />)</span></td>
						<td class="normalTd">&nbsp;<s:property value="%{#vlanNetworkMapping.networkObj.dhcpAllString}" /></td>
						<td class="normalTd" id="webSecurityStr">&nbsp;<s:property value="%{#vlanNetworkMapping.networkObj.webSecurityString}" /></td>
					</s:if>
					<s:else>
						<td>
							<s:if test="%{writeDisabled != 'disabled'}">
								<a class="npcLinkA" href="javascript:void(0);" onclick='editVlanNetworkMappingNetwork(<s:property value="%{#vlanNetworkMapping.vlan.id}"/>)'>
								<s:text name="config.configTemplate.vlanMap.select"/></a>
							</s:if>
							<s:else>
								<a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();">
								<s:text name="config.configTemplate.vlanMap.select"/></a>
							</s:else>
						</td>
						<td class="normalTd">&nbsp;</td>
						<td class="normalTd">&nbsp;</td>
					</s:else>
				</tr>
				<tr><td height="8px"/></tr>
				</s:iterator>
				<s:if test="%{writeDisabled != 'disabled'}">
					<tr>
						<td/><td/>
						<td>
							<a class="npcLinkA" href="javascript:void(0);" onclick="showVlanMappingSelectDialog();"><span title='<s:text name="button.add"/>'><s:text name="button.add"/></span></a>
						</td>
					</tr>
				</s:if>
			</table>
		</td>
	</tr>
</table>
