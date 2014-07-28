<%@taglib prefix="s" uri="/struts-tags"%>
<%-- <s:if test="%{!dataSource.blnBonjourOnly}"> --%>
<s:if test="%{dataSource.configType.otherTypesSupportedExceptBonjour}">
    <tr>
        <td>
            <table cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td class="npcHead1" style="padding-left: 35px;padding-right: 20px;">
                    <s:if test="%{dataSource.blnWirelessRouter}">
                    <s:text name="config.networkpolicy.lan.title"/>
                    </s:if>
                    <s:else>
                    <s:text name="config.networkpolicy.lan.wireless.title"/>
                    </s:else>
                    </td>
                    <s:if test="%{writeDisabled != 'disabled'}">
                    	<td class="npcButton" ><a href="javascript:void(0);" onclick="addRemoveLan(false, <s:property value="%{dataSource.configType.wirelessOnly}"/>);" id="btAddRemoveLan" style="visibility: hidden;" class="btCurrent" title="<s:text name="config.networkpolicy.button.choose"/>"><span><s:text name="config.networkpolicy.button.choose"/></span></a></td>
                    </s:if>
                    <s:else>
						<td class="npcButton">
							<a href="javascript:void(0);"
								onclick="showWarmMessage();" class="btCurrent"
								title="<s:text name="config.networkpolicy.button.choose"/>">
								<span>
									<s:text name="config.networkpolicy.button.choose" />
								</span>
							</a>
						</td>
					</s:else>
                </tr>
            </table>
        </td>
    </tr>
    <%-- <s:if test="%{dataSource.blnWirelessOnly}"> --%>
    <s:if test="%{dataSource.configType.wirelessOnly}">
    <tr>
        <td class="npcNoteTitle" style="padding-top: 0;"><label><s:text name="config.networkpolicy.lan.wireless.note" /></label></td>
    </tr>
    </s:if>
    <s:if test="%{dataSource.lanProfiles != null && dataSource.lanProfiles.size>0}">
    <tr>
        <td>
            <table cellspacing="0" cellpadding="0" border="0" width="100%" style="table-layout: fixed;">
                <tr>
                    <td width="26px">&nbsp;</td>
                    <td class="npcHead2" width="150px"><s:text name="config.networkpolicy.ssid.list.name" /></td>
                    <td class="npcHead2" width="160px"><s:text name="config.networkpolicy.ssid.list.auth" /></td>
                    <td class="npcHead2" width="160px"><s:text name="config.networkpolicy.ssid.list.userprofile" /></td>
                    <td class="npcHead2" width="160px"><s:text name="config.networkpolicy.ssid.list.vlan" /></td>
                </tr>
                <s:iterator value="%{dataSource.lanProfiles}" status="status" id="landProfile">
                    <s:if test="%{#landProfile != null}">
                        <tr class="npcList">
                            <td class="imageTd"></td>                   
                            <td valign="top" style="padding-top: 11px;">
                                <table cellspacing="0" cellpadding="0" border="0">
                                    <tr>
                                        <td rowspan="2" class="imageTd" style="width: 30px;">
                                        <s:if test="%{#landProfile.enabled8021Q}">
                                            <img src="<s:url value="/images/hm_v2/profile/hm-icon-lan-new.png" />"
                                                width="30" height="30" alt="both" title="both" />
                                        </s:if>
                                        <s:else>
                                            <img src="<s:url value="/images/hm_v2/profile/hm-icon-lan.png" />"
                                                width="30" height="30" alt="both" title="both" />
                                        </s:else>
                                        </td>
                                        <td class="smallTd">
                                            &nbsp;
                                            <a class="npcLinkA" href="javascript:void(0);" onclick='viewLAN(<s:property value="%{#landProfile.id}"/>, <s:property value="%{dataSource.configType.wirelessOnly}"/>)'><span title="<s:property value="%{name}" />"><s:property value="%{nameSubstr}" /></span></a>
                                        </td>
                                        
                                    </tr>
                                    <tr>
                                        <td class="smallTd">
                                            &nbsp;
                                            <s:property value="%{#landProfile.accessModeValue}" />
                                        </td>
                                    </tr>
                                    <tr style="line-height: 5px;">
                                        <td>
                                        <s:if test="%{#landProfile.lanInterfacesMode.eth1On}">
                                        <span class="indicator activePort">&nbsp;</span>
                                        </s:if>
                                        <s:else>
                                        <span class="indicator inactivePort">&nbsp;</span>
                                        </s:else>
                                        <s:if test="%{#landProfile.lanInterfacesMode.eth2On}">
                                        <span class="indicator activePort">&nbsp;</span>
                                        </s:if>
                                        <s:else>
                                        <span class="indicator inactivePort">&nbsp;</span>
                                        </s:else>
                                        <s:if test="%{#landProfile.lanInterfacesMode.eth3On}">
                                        <span class="indicator activePort">&nbsp;</span>
                                        </s:if>
                                        <s:else>
                                        <span class="indicator inactivePort">&nbsp;</span>
                                        </s:else>
                                        <s:if test="%{#landProfile.lanInterfacesMode.eth4On}">
                                        <span class="indicator activePort">&nbsp;</span>
                                        </s:if>
                                        <s:else>
                                        <span class="indicator inactivePort">&nbsp;</span>
                                        </s:else>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                            <s:if test="%{#landProfile.enabled8021Q}">
                            <td colspan="2" valign="top" style="padding-right: 10px; padding-top: 11px;">
                              <label style="color: gray;"><s:text name="config.networkpolicy.lan.note" /></label>
                            </td>
                            <td valign="top">
                                <table cellspacing="0" cellpadding="0" border="0" width="100%">
                                
                                    <s:if test="%{#landProfile.nativeVlan != null}">
                                    <tr class="npcListBlock">
                                        <td>&nbsp;
                                                <table cellspacing="0" cellpadding="0" border="0">
                                                    <tr><td>
                                                        <img src="<s:url value="/images/hm_v2/profile/HM-icon-VLAN-30x30.png" />"
                                                            width="30px" height="30px" alt="vlan" title="vlan" />
                                                   </td>
                                                   <td>&nbsp;
                                                       <a class="npcLinkA" href="javascript:void(0);" onclick='editVlan(<s:property value="%{#landProfile.nativeVlan.id}"/>)'><span title="<s:property value="%{#landProfile.nativeVlan.vlanName}" />"><s:property value="%{#landProfile.nativeVlan.vlanName}" /></span></a>
                                                       <br/>&nbsp;&nbsp;<span class="smallTd">Native</span>
                                                   </td>
                                                   </tr>
                                                </table>
                                        </td>
                                    </tr>
                                    </s:if>
                                    <s:iterator value="%{#landProfile.regularVlans}" status="status" id="vlan">
                                    <tr class="npcListBlock">
                                        <td>&nbsp;
                                            <s:if test="%{#vlan != null}">
                                                <table cellspacing="0" cellpadding="0" border="0">
                                                    <tr><td>
                                                        <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                            width="30px" height="30px" alt="network" title="network" />
                                                </td><td>&nbsp;
                                                <a class="npcLinkA" href="javascript:void(0);" onclick='editVlan(<s:property value="%{#vlan.id}"/>)'><span title="<s:property value="%{#vlan.vlanName}" />"><s:property value="%{#vlan.vlanName}" /></span></a>
                                                </td></tr></table>
                                            </s:if>
                                        </td>
                                    </tr>
                                    </s:iterator>
                                    <tr>
                                        <td colspan="3">
                                            <table border="0" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td width="32px" />
                                                    <td style="padding-top:4px;">
                                                    <s:if test="%{writeDisabled != 'disabled'}">
                                                        <s:if test="%{#landProfile.nativeVlan !=null || (#landProfile.regularVlans!=null && #landProfile.regularVlans.size>0)}">
                                                            <a class="npcLinkA" href="javascript:void(0);" onClick="selectVlans4Lan('<s:property value="%{#landProfile.id}" />');" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:if>
                                                        <s:else>
                                                            <a class="npcLinkAEmpty" href="javascript:void(0);" onClick="selectVlans4Lan('<s:property value="%{#landProfile.id}" />');" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:else>
                                                    </s:if>
                                                    <s:else>
                                                    	 <s:if test="%{#landProfile.nativeVlan !=null || (#landProfile.regularVlans!=null && #landProfile.regularVlans.size>0)}">
                                                            <a class="npcLinkA" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:if>
                                                        <s:else>
                                                            <a class="npcLinkAEmpty" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:else>
                                                    </s:else>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>                                                       
                                </table>
                            </td>
                            </s:if>
                            <s:else>
                            <td style="padding-top: 11px;" valign="top">
                                <table cellspacing="0" cellpadding="0" border="0">
                                    <s:if test="%{#landProfile.cwpSelectEnabled}">
                                        <tr>
                                            <td>
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-cwp.png" />"
                                                    width="30" height="30" alt="CWP" title="CWP" />&nbsp;
                                            </td>
                                            <s:if test="%{writeDisabled != 'disabled'}">
                                            	<td>
	                                                <s:if test="%{#landProfile.cwp==null}">
	                                                    <a class="npcLinkAEmpty" href="javascript:void(0);" onclick="openCwpListDialog(2, <s:property value='#landProfile.id'/>, false);"><s:text name="config.networkpolicy.ssid.list.cwp" /></a>
	                                                </s:if>
	                                                <s:else>
	                                                    <table cellspacing="0" cellpadding="0" border="0">
	                                                        <tr>
	                                                            <td>
	                                                                <a class="npcLinkA" href="javascript:void(0);"
	                                                                    onclick="openCwpListDialog(2, <s:property value='#landProfile.id'/>, false);"><span title="<s:property value="%{#landProfile.cwp.cwpName}" />"><s:property value="%{#landProfile.cwp.cwpNameSubstr}" /></span></a>
	                                                            </td>
	                                                        </tr>
	                                                        <tr>
	                                                            <td class="smallTd">&nbsp;<s:property value="%{#landProfile.cwp.registrationTypeName}" /></td>
	                                                        </tr>
	                                                    </table>
	                                                </s:else>
	                                            </td>
                                            </s:if>
                                            <s:else>
                                            	<td>
	                                                <s:if test="%{#landProfile.cwp==null}">
	                                                    <a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.cwp" /></a>
	                                                </s:if>
	                                                <s:else>
	                                                    <table cellspacing="0" cellpadding="0" border="0">
	                                                        <tr>
	                                                            <td>
	                                                                <a class="npcLinkA" href="javascript:void(0);"
	                                                                    onclick="showWarmMessage();"><span title="<s:property value="%{#landProfile.cwp.cwpName}" />"><s:property value="%{#landProfile.cwp.cwpNameSubstr}" /></span></a>
	                                                            </td>
	                                                        </tr>
	                                                        <tr>
	                                                            <td class="smallTd">&nbsp;<s:property value="%{#landProfile.cwp.registrationTypeName}" /></td>
	                                                        </tr>
	                                                    </table>
	                                                </s:else>
	                                            </td>
                                            </s:else>
                                        </tr>
                                    </s:if>
                                    <s:if test="%{#landProfile.radiusAuthEnable}">
                                        <tr>
                                            <td>
                                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-HiveAP_AAA_Server_Settings_30x30.png" />"
                                                    width="30" height="30" alt="RADIUS" title="RADIUS" />&nbsp;
                                            </td>
                                            <td id="radiusTD3_<s:property value="%{#landProfile.id}" />">
                                            <s:if test="%{writeDisabled != 'disabled'}">
                                                <s:if test="%{#landProfile.radiusAssignment==null}">
                                                    <a class="npcLinkAEmpty" href="javascript:void(0);" onClick="showRadiusServerSelectDialog(<s:property value="%{#landProfile.id}" />, '', 3);"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
                                                </s:if>
                                                <s:else>
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                            <td>
                                                                <a class="npcLinkA" href="javascript:void(0);" onClick="showRadiusServerSelectDialog(<s:property value="%{#landProfile.id}" />, <s:property value="%{#landProfile.radiusAssignment.id}" />, 3);"><span title="<s:property value="%{#landProfile.radiusAssignment.radiusName}" />"><s:property value="%{#landProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
                                                        </tr>
                                                    </table>
                                                </s:else>
                                            </s:if>
                                            <s:else>
                                            	<s:if test="%{#landProfile.radiusAssignment==null}">
                                                    <a class="npcLinkAEmpty" href="javascript:void(0);" onClick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
                                                </s:if>
                                                <s:else>
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                            <td>
                                                                <a class="npcLinkA" href="javascript:void(0);" onClick="showWarmMessage();"><span title="<s:property value="%{#landProfile.radiusAssignment.radiusName}" />"><s:property value="%{#landProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
                                                        </tr>
                                                    </table>
                                                </s:else>
                                            </s:else>
                                            </td>
                                        </tr>
                                    </s:if>
                                    <s:if test="%{#landProfile.enableAssignUserProfile}">
                                        <tr>
                                            <td>
                                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-Local_User_Groups.png" />"
                                                    width="30" height="30" alt="RADIUS User Groups" title="RADIUS User Groups" />&nbsp;
                                            </td>
                                            <s:if test="%{writeDisabled != 'disabled'}">
                                            	<td>
	                                                <s:if test="%{#landProfile.radiusUserGroups==null || #landProfile.radiusUserGroups.size==0}">
	                                                    <a class="npcLinkAEmpty" href="javascript:void(0);" onclick="addRemoveUserGroups('LAN', '<s:property value="%{#landProfile.id}"/>', 'RADIUS');"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
	                                                </s:if>
	                                                <s:else>
	                                                    <table cellspacing="0" cellpadding="0" border="0">
	                                                        <tr>
	                                                            <td>
	                                                                <a class="npcLinkA" href="javascript:void(0);" onclick="addRemoveUserGroups('LAN', '<s:property value="%{#landProfile.id}"/>', 'RADIUS');"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
	                                                            </td>
	                                                        </tr>
	                                                        <s:iterator value="%{#landProfile.radiusUserGroups}" id="groupStatus">
	                                                            <tr>
	                                                                <td class="smallTd">&nbsp;<a href="javascript:void(0);" class="npcLinkA" onclick='editLocalUserGroup(<s:property value="%{#groupStatus.id}"/>)'><span title="<s:property value="%{#groupStatus.groupName}" />"><s:property value="%{#groupStatus.groupNameSubstr}" /></span></a></td>
	                                                            </tr>
	                                                        </s:iterator>
	                                                    </table>
	                                                </s:else>
	                                            </td>
                                            </s:if>
                                            <s:else>
                                            	<td>
	                                                <s:if test="%{#landProfile.radiusUserGroups==null || #landProfile.radiusUserGroups.size==0}">
	                                                    <a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
	                                                </s:if>
	                                                <s:else>
	                                                    <table cellspacing="0" cellpadding="0" border="0">
	                                                        <tr>
	                                                            <td>
	                                                                <a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
	                                                            </td>
	                                                        </tr>
	                                                        <s:iterator value="%{#landProfile.radiusUserGroups}" id="groupStatus">
	                                                            <tr>
	                                                                <td class="smallTd">&nbsp;<a href="javascript:void(0);" class="npcLinkA" onclick="showWarmMessage();"><span title="<s:property value="%{#groupStatus.groupName}" />"><s:property value="%{#groupStatus.groupNameSubstr}" /></span></a></td>
	                                                            </tr>
	                                                        </s:iterator>
	                                                    </table>
	                                                </s:else>
	                                            </td>
                                            </s:else>
                                        </tr>
                                    </s:if>
                                </table>
                            </td>
                            <td colspan="2" valign="top">
                                <table cellspacing="0" cellpadding="0" border="0" width="100%">
                                    <s:if test="%{#landProfile.userProfileDefault != null}">
                                        <tr class="npcListBlock">
                                            <td width="32px" style="padding-top: 8px;">
                                                <s:if test="%{#landProfile.userProfileDefault.enableAssign && #landProfile.userProfileDefault.assignRules!=null && #landProfile.userProfileDefault.assignRules.size>0}">
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                            </s:if>
                                            <s:else>
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                            </s:else>
                                            </td>
                                            <td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#landProfile.id}" />', '<s:property value="%{#landProfile.userProfileDefault.id}" />');"><span title="<s:property value="%{#landProfile.userProfileDefault.userProfileName}" />"><s:property value="%{#landProfile.userProfileDefault.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(default)</span></td>
                                            <td class="paddingLeft">&nbsp;
                                                
                                                    <s:if test="%{#landProfile.userProfileDefault.vlan != null}">
                                                        <table cellspacing="0" cellpadding="0" border="0">
                                                            <tr><td>
                                                                    <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                        width="30px" height="30px" alt="vlan" title="vlan" />
                                                            </td><td>&nbsp;<s:property value="%{#landProfile.userProfileDefault.vlan.vlanName}" />
                                                            </td></tr>
                                                        </table>
                                                    </s:if>
                                                    
                                            </td>
                                        </tr>
                                    </s:if>
                                    <s:if test="%{#landProfile.userProfileSelfReg != null}">
                                    <tr class="npcListBlock">
                                        <td width="32px" style="padding-top: 8px;">
                                            <s:if test="%{#landProfile.userProfileSelfReg.enableAssign && #landProfile.userProfileSelfReg.assignRules!=null && #landProfile.userProfileSelfReg.assignRules.size>0}">
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                            </s:if>
                                            <s:else>
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                            </s:else>
                                        </td>
                                        <td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#landProfile.id}" />', '<s:property value="%{#landProfile.userProfileSelfReg.id}" />');"><span title="<s:property value="%{#landProfile.userProfileSelfReg.userProfileName}" />"><s:property value="%{#landProfile.userProfileSelfReg.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(self-reg)</span></td>
                                        <td class="paddingLeft">&nbsp;
                                        
                                            <s:if test="%{#landProfile.userProfileSelfReg.vlan != null}">
                                                <table cellspacing="0" cellpadding="0" border="0">
                                                    <tr><td>
                                                            <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                width="30px" height="30px" alt="vlan" title="vlan" />
                                                    </td><td>&nbsp;<s:property value="%{#landProfile.userProfileSelfReg.vlan.vlanName}" />
                                                    </td></tr>
                                                </table>
                                            </s:if>
                                            
                                        </td>
                                    </tr>
                                </s:if>
                                <s:iterator value="%{#landProfile.radiusUserProfile}" id="lanStatus">
                                    <tr class="npcListBlock">
                                        <td width="32px" style="padding-top: 8px;">
                                            <s:if test="%{#lanStatus.enableAssign && #lanStatus.assignRules!=null && #lanStatus.assignRules.size>0}">
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                            </s:if>
                                            <s:else>
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                            </s:else>
                                        </td>
                                        <td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#landProfile.id}" />', '<s:property value="%{#lanStatus.id}" />');"><span title="<s:property value="%{#lanStatus.userProfileName}" />"><s:property value="%{#lanStatus.userProfileNameSubstr}" /></span></a></td>
                                        <td class="paddingLeft">&nbsp;
                                        
                                             <s:if test="%{#lanStatus.vlan != null}">
                                                 <table cellspacing="0" cellpadding="0" border="0">
                                                     <tr><td>
                                                             <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                 width="30px" height="30px" alt="vlan" title="vlan" />
                                                     </td><td>&nbsp;<s:property value="%{#lanStatus.vlan.vlanName}" />
                                                     </td></tr>
                                                 </table>
                                             </s:if>
                                             
                                        </td>
                                    </tr>
                                </s:iterator>
                                    <tr>
                                        <td colspan="3">
                                            <table border="0" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td width="32px;" />
                                                    <td style="padding-top:4px;">
                                                    <s:if test="%{writeDisabled != 'disabled'}">
                                                        <s:if test="%{#landProfile.userProfileDefault !=null || #landProfile.userProfileSelfReg != null || (#landProfile.radiusUserProfile != null && #landProfile.radiusUserProfile.size > 0)}">
                                                            <a class="npcLinkA" href="javascript:void(0);" onClick="javascript: selectUserProfile4Lan('<s:property value="%{#landProfile.id}" />', '');" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:if>
                                                        <s:else>
                                                            <a class="npcLinkAEmpty" href="javascript:void(0);" onClick="javascript: selectUserProfile4Lan('<s:property value="%{#landProfile.id}" />', '');" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:else>
                                                    </s:if>
                                                    <s:else>
                                                    	<s:if test="%{#landProfile.userProfileDefault !=null || #landProfile.userProfileSelfReg != null || (#landProfile.radiusUserProfile != null && #landProfile.radiusUserProfile.size > 0)}">
                                                            <a class="npcLinkA" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:if>
                                                        <s:else>
                                                            <a class="npcLinkAEmpty" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:else>
                                                    </s:else>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                            </s:else>
                        </tr>
                        <tr>
                            <td height="10px"></td>
                        </tr>
                    </s:if>
                </s:iterator>                               
            </table>
        </td>
    </tr>
    <tr>
        <td height="10px" class="smallTd">&nbsp;</td>
    </tr>
    </s:if>
    <s:else>
    <s:if test="%{dataSource.blnWirelessRouter}">
        <tr>
            <td align="center">
                <img src="<s:url value="/images/hm_v2/welcome-wired.png" />" class="dinl"/>
            </td>
        </tr>
    </s:if>
    </s:else>
</s:if>