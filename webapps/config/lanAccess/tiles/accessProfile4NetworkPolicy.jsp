<%@taglib prefix="s" uri="/struts-tags"%>
<s:if test="%{!dataSource.configType.bonjourOnly}">
    <tr>
        <td>
            <table cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td class="npcHead1" style="padding-left: 35px;padding-right: 20px;">
                    <s:text name="config.networkpolicy.access.profile.title"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <s:if test="%{configType4Port == 3}">
    <tr>
        <td class="npcNoteTitle" style="padding-top: 0;"><label><s:text name="config.networkpolicy.lan.wireless.note" /></label></td>
    </tr>
    </s:if>
    <s:if test="%{dataSource.accessProfiles.size>0}">
    <tr>
        <td id="accessesContent">
            <table cellspacing="0" cellpadding="0" border="0" width="100%" style="table-layout: fixed;">
                <tr>
                    <td width="26px">&nbsp;</td>
                    <td class="npcHead2" width="150px"><s:text name="config.networkpolicy.ssid.list.name" /></td>
                    <td class="npcHead2" width="160px"><s:text name="config.networkpolicy.ssid.list.auth" /></td>
                    <td class="npcHead2" width="15px">&nbsp;</td>
                    <td class="npcHead2" width="160px"><s:text name="config.networkpolicy.ssid.list.userprofile" /></td>
                    <td class="npcHead2" width="160px"><s:text name="config.networkpolicy.ssid.list.vlan" /></td>
                </tr>
                <s:iterator value="%{dataSource.accessProfiles}" status="status" id="accessProfile">
                    <s:if test="%{#accessProfile != null}">
                        <tr class="npcList">
                            <td class="imageTd"></td>                   
                            <td valign="top" style="padding-top: 11px;" id="accessSection">
                                <table cellspacing="0" cellpadding="0" border="0">
                                    <tr>
                                        <td rowspan="2" class="imageTd port-ui <s:property value='%{#accessProfile.portClassName}'/>">
                                        <span class="portColor" id="colorLabel_<s:property value="%{#accessProfile.id}"/>"></span>
                                        </td>
                                        <td class="smallTd">
                                            &nbsp;
                                            <a class="npcLinkA" href="javascript:void(0);" 
                                                onclick='viewPortAccess(<s:property value="%{#accessProfile.id}"/>, true)'><span title="<s:property value="%{#accessProfile.name}" />"><s:property value="%{#accessProfile.nameSubstr}" /></span></a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="smallTd">
                                            &nbsp;
                                            <s:property value='%{#accessProfile.portTypeName}'/>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                            <!-- For Trunk mode -->
                            <s:if test="%{#accessProfile.portType == 2 || #accessProfile.portType == 5}">
                            <td colspan="3" valign="top" style="padding-right: 10px; padding-top: 11px;">
                              <label style="color: gray;"><s:text name="config.networkpolicy.lan.note" /></label>
                            </td>
                            <td valign="top">
                                <table cellspacing="0" cellpadding="0" border="0" width="100%">
                                
                                    <s:if test="%{#accessProfile.nativeVlan != null}">
                                    <tr class="npcListBlock">
                                        <td>&nbsp;
                                                <table cellspacing="0" cellpadding="0" border="0">
                                                    <tr><td class="accessNativeVlan" title="vlan"/>
                                                   <td id='accessNativeVlanSection_<s:property value="%{#accessProfile.id}"/>'>&nbsp;
                                                       <a class="npcLinkA" href="javascript:void(0);" onclick='editVlan(<s:property value="%{#accessProfile.nativeVlan.id}"/>)'><span title="<s:property value="%{#accessProfile.nativeVlan.vlanName}" />"><s:property value="%{#accessProfile.nativeVlan.vlanName}" /></span></a>
                                                       <br/>&nbsp;&nbsp;<span class="smallTd" title='<s:property value="%{#accessProfile.allowedVlan}"/>'><s:if test="%{#accessProfile.allowedVlanSubstr != ''}">(<s:property value="%{#accessProfile.allowedVlanSubstr}"/>)</s:if></span>
                                                   </td>
                                                   </tr>
                                                </table>
                                        </td>
                                    </tr>
                                    </s:if>
                                    <tr>
                                        <td colspan="3">
                                            <table border="0" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td width="25px" />
                                                    <td style="padding-top:4px;">
                                                    <s:if test="%{writeDisabled != 'disabled'}">
                                                        <a 
                                                        <s:if test="%{#accessProfile.nativeVlan != null}">class="npcLinkA"</s:if>
                                                        <s:else>class="npcLinkAEmpty" </s:else>
                                                        class="npcLinkAEmpty" 
                                                        href="javascript:void(0);" onClick="editTrunkModeVlan4Access('<s:property value="%{#accessProfile.id}" />');" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                    </s:if>
                                                    <s:else>
                                                        <a class="npcLinkA" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                    </s:else>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>                                                       
                                </table>
                            </td>
                            </s:if>
                            <!-- For Access mode and PhoneData -->
                            <s:elseif test="%{#accessProfile.portType == 1 || #accessProfile.portType == 4}">
                            <td style="padding-top: 11px;" valign="top">
                                <table cellspacing="0" cellpadding="0" border="0">
                                    <s:if test="%{#accessProfile.enabledCWP && !dataSource.configType.switchOnly}">
                                        <tr>
                                            <td>
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-cwp.png" />"
                                                    width="30" height="30" alt="CWP" title="CWP" />&nbsp;
                                            </td>
                                            <td>
                                                <s:if test="%{#accessProfile.cwp==null}">
		                                            <s:if test="%{writeDisabled != 'disabled'}">
                                                    <a class="npcLinkAEmpty" href="javascript:void(0);" onclick="openCwpListDialog(2, <s:property value='#accessProfile.id'/>, false);"><s:text name="config.networkpolicy.ssid.list.cwp" /></a>
		                                            </s:if>
		                                            <s:else>
		                                            <a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.cwp" /></a>
		                                            </s:else>
                                                </s:if>
                                                <s:else>
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                            <td>
                                                                <a class="npcLinkA" href="javascript:void(0);"
                                                                    <s:if test="%{writeDisabled != 'disabled'}">
                                                                    onclick="openCwpListDialog(2, <s:property value='#accessProfile.id'/>, false);"
                                                                    </s:if>
                                                                    <s:else>
                                                                    onclick="showWarmMessage();"
                                                                    </s:else>
                                                                    ><span title="<s:property value="%{#accessProfile.cwp.cwpName}" />"><s:property value="%{#accessProfile.cwp.cwpNameSubstr}" /></span></a>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="smallTd">&nbsp;<s:property value="%{#accessProfile.renameCWPType ? #accessProfile.cwp.registrationTypeName4IDM : #accessProfile.cwp.registrationTypeName}" /></td>
                                                        </tr>
                                                    </table>
                                                </s:else>
                                            </td>
                                        </tr>
                                    </s:if>
                                    <s:if test="%{#accessProfile.iDMAuthEnabled}">
	                                    <tr>
	                                        <td>
	                                            <img src="<s:url value="/images/hm_v2/profile/ah-hm-idm-40x40.png" />" 
	                                                width="40" height="40" alt="RADIUS" title="RADIUS" />&nbsp;
	                                        </td>
	                                        <td><label>&nbsp;<s:text name="config.radiusProxy.cloudAuth.ssid"/></label><br><label class="smallTd">&nbsp;<s:property value="%{#accessProfile.suffixIDMType}"/></label></td>
	                                    </tr>
                                    </s:if>
                                    <s:if test="%{#accessProfile.radiusAuthEnable}">
                                        <tr>
                                            <td>
                                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-HiveAP_AAA_Server_Settings_30x30.png" />"
                                                    width="30" height="30" alt="RADIUS" title="RADIUS" />&nbsp;
                                            </td>
                                            <td id="radiusTD3_<s:property value="%{#accessProfile.id}" />">
                                                <s:if test="%{#accessProfile.radiusAssignment==null}">
                                                    <s:if test="%{writeDisabled != 'disabled'}">
                                                    <a class="npcLinkAEmpty" href="javascript:void(0);" onClick="showRadiusServerSelectDialog(<s:property value="%{#accessProfile.id}" />, '', 3);"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
                                                    </s:if>
                                                    <s:else>
                                                    <a class="npcLinkAEmpty" href="javascript:void(0);" onClick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusserver"/></a>
                                                    </s:else>
                                                </s:if>
                                                <s:else>
                                                    <s:if test="%{writeDisabled != 'disabled'}">
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                            <td>
                                                                <a class="npcLinkA" href="javascript:void(0);" onClick="showRadiusServerSelectDialog(<s:property value="%{#accessProfile.id}" />, <s:property value="%{#accessProfile.radiusAssignment.id}" />, 3);"><span title="<s:property value="%{#accessProfile.radiusAssignment.radiusName}" />"><s:property value="%{#accessProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="smallTd">&nbsp;<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/></td>
                                                        </tr>
                                                    </table>
                                                    </s:if>
                                                    <s:else>
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                            <td>
                                                                <a class="npcLinkA" href="javascript:void(0);" onClick="showWarmMessage();"><span title="<s:property value="%{#accessProfile.radiusAssignment.radiusName}" />"><s:property value="%{#accessProfile.radiusAssignment.radiusNameSubstr}" /></span></a>
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
                                    <s:if test="%{#accessProfile.enableAssignUserProfile}">
                                        <tr>
                                            <td>
                                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-Local_User_Groups.png" />"
                                                    width="30" height="30" alt="RADIUS User Groups" title="RADIUS User Groups" />&nbsp;
                                            </td>
                                            <s:if test="%{writeDisabled != 'disabled'}">
                                                <td>
                                                    <s:if test="%{#accessProfile.radiusUserGroups==null || #accessProfile.radiusUserGroups.size==0}">
                                                        <a class="npcLinkAEmpty" href="javascript:void(0);" onclick="addRemoveUserGroups('LAN', '<s:property value="%{#accessProfile.id}"/>', 'RADIUS');"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
                                                    </s:if>
                                                    <s:else>
                                                        <table cellspacing="0" cellpadding="0" border="0">
                                                            <tr>
                                                                <td>
                                                                    <a class="npcLinkA" href="javascript:void(0);" onclick="addRemoveUserGroups('LAN', '<s:property value="%{#accessProfile.id}"/>', 'RADIUS');"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
                                                                </td>
                                                            </tr>
                                                            <s:iterator value="%{#accessProfile.radiusUserGroups}" id="groupStatus">
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
                                                    <s:if test="%{#accessProfile.radiusUserGroups==null || #accessProfile.radiusUserGroups.size==0}">
                                                        <a class="npcLinkAEmpty" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
                                                    </s:if>
                                                    <s:else>
                                                        <table cellspacing="0" cellpadding="0" border="0">
                                                            <tr>
                                                                <td>
                                                                    <a class="npcLinkA" href="javascript:void(0);" onclick="showWarmMessage();"><s:text name="config.networkpolicy.ssid.list.radiusUserGoup"/></a>
                                                                </td>
                                                            </tr>
                                                            <s:iterator value="%{#accessProfile.radiusUserGroups}" id="groupStatus">
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
                            <td colspan="3" valign="top">
                                <table cellspacing="0" cellpadding="0" border="0" width="100%">
                                    <s:if test="%{#accessProfile.defUserProfile != null}">
                                        <tr class="npcListBlock">
                                            <td width="15px">
                                                <s:if test="%{#accessProfile.defUserProfile.enableAssign && #accessProfile.defUserProfile.assignRules!=null && #accessProfile.defUserProfile.assignRules.size>0}">
                                                <img src="<s:url value="/images/expand_plus.gif" />"
                                                    width="12px" height="12px" alt="Display/Hide Reassign User Profile" title="Display/Hide Reassign User Profile"
                                                    onclick="displayHideAssignUs(this);"/>                                               
                                                </s:if>
                                            </td>
                                            <td width="32px" style="padding-top: 8px;">
                                                <s:if test="%{#accessProfile.defUserProfile.enableAssign && #accessProfile.defUserProfile.assignRules!=null && #accessProfile.defUserProfile.assignRules.size>0}">
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                                </s:if>
                                            <s:else>
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                            </s:else>
                                            </td>
                                            <td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#accessProfile.defUserProfile.id}" />');"><span title="<s:property value="%{#accessProfile.defUserProfile.userProfileName}" />"><s:property value="%{#accessProfile.defUserProfile.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(default)</span></td>
                                            <td class="paddingLeft">&nbsp;
                                                    <s:if test="%{#accessProfile.defUserProfile.vlan != null}">
                                                        <table cellspacing="0" cellpadding="0" border="0">
                                                            <tr>
                                                            <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                            <s:if test="%{#myUsMapping.userProfile.id==#accessProfile.defUserProfile.id}">
                                                            <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                    <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                        width="30px" height="30px" alt="vlan" title="vlan" />
                                                            </td><td>&nbsp;
                                                            <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                            </td></s:if>
                                                            </s:iterator>
                                                            <td></td></tr>
                                                        </table>                                                        
                                                    </s:if>
                                                    
                                            </td>
                                        </tr>
                                        <s:if test="%{#accessProfile.defUserProfile.enableAssign && #accessProfile.defUserProfile.assignRules!=null && #accessProfile.defUserProfile.assignRules.size>0}">
                                            <s:iterator value="%{#accessProfile.defUserProfile.assignRules}" id="userProfileDefaultAssignStatus">
                                                <tr class="npcListBlockSub" style="display:none;">
                                                    <td width="15px"></td>
                                                    <td width="32px" style="padding-top: 8px;"></td>
                                                    <td width="162px">
                                                    <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                                        style="vertical-align: middle;"
                                                                        width="30px" height="30px" alt="user profile" title="user profile" />&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#userProfileDefaultAssignStatus.userProfileId}" />');"><s:property value="%{#userProfileDefaultAssignStatus.UserProfileNameSubstr}" /></a></td>
                                                    <td class="paddingLeft">&nbsp;
                                                        <table cellspacing="0" cellpadding="0" border="0">
                                                            <tr>
                                                            <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                            <s:if test="%{#myUsMapping.userProfile.id==#userProfileDefaultAssignStatus.userProfileId}">
                                                            <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                    <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                        width="30px" height="30px" alt="vlan" title="vlan" />
                                                            </td><td>&nbsp;
                                                            <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                            </td></s:if>
                                                            </s:iterator>
                                                            <td></td></tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </s:iterator>
                                        </s:if>                                        
                                    </s:if>
                                    <s:if test="%{#accessProfile.selfRegUserProfile != null}">
                                        <tr class="npcListBlock">
                                            <td width="15px">
                                                <s:if test="%{#accessProfile.selfRegUserProfile.enableAssign && #accessProfile.selfRegUserProfile.assignRules!=null && #accessProfile.selfRegUserProfile.assignRules.size>0}">
                                                <img src="<s:url value="/images/expand_plus.gif" />"
                                                    width="12px" height="12px" alt="Display/Hide Reassign User Profile" title="Display/Hide Reassign User Profile"
                                                    onclick="displayHideAssignUs(this);"/>                                               
                                                </s:if>
                                            </td>
                                            <td width="32px" style="padding-top: 8px;">
                                                <s:if test="%{#accessProfile.selfRegUserProfile.enableAssign && #accessProfile.selfRegUserProfile.assignRules!=null && #accessProfile.selfRegUserProfile.assignRules.size>0}">
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                                </s:if>
                                            <s:else>
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                            </s:else>
                                            </td>
                                            <td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#accessProfile.selfRegUserProfile.id}" />');"><span title="<s:property value="%{#accessProfile.selfRegUserProfile.userProfileName}" />"><s:property value="%{#accessProfile.selfRegUserProfile.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(self-reg)</span></td>
                                            <td class="paddingLeft">&nbsp;
                                                    <s:if test="%{#accessProfile.selfRegUserProfile.vlan != null}">
                                                        <table cellspacing="0" cellpadding="0" border="0">
                                                            <tr>
                                                            <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                            <s:if test="%{#myUsMapping.userProfile.id==#accessProfile.selfRegUserProfile.id}">
                                                            <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                    <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                        width="30px" height="30px" alt="vlan" title="vlan" />
                                                            </td><td>&nbsp;
                                                            <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                            </td></s:if>
                                                            </s:iterator>
                                                            <td></td></tr>
                                                        </table>                                                        
                                                    </s:if>
                                            </td>
                                        </tr>
                                        <s:if test="%{#accessProfile.selfRegUserProfile.enableAssign && #accessProfile.selfRegUserProfile.assignRules!=null && #accessProfile.selfRegUserProfile.assignRules.size>0}">
                                            <s:iterator value="%{#accessProfile.selfRegUserProfile.assignRules}" id="userProfileRegAssignStatus">
                                                <tr class="npcListBlockSub" style="display:none;">
                                                    <td width="15px"></td>
                                                    <td width="32px" style="padding-top: 8px;"></td>
                                                    <td width="162px">
                                                    <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                                        style="vertical-align: middle;"
                                                                        width="30px" height="30px" alt="user profile" title="user profile" />&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#userProfileRegAssignStatus.userProfileId}" />');"><s:property value="%{#userProfileRegAssignStatus.UserProfileNameSubstr}" /></a></td>
                                                    <td class="paddingLeft">&nbsp;
                                                        <table cellspacing="0" cellpadding="0" border="0">
                                                            <tr>
                                                            <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                            <s:if test="%{#myUsMapping.userProfile.id==#userProfileRegAssignStatus.userProfileId}">
                                                            <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
																onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                    <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                        width="30px" height="30px" alt="vlan" title="vlan" />
                                                            </td><td>&nbsp;
                                                            <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                            </td></s:if>
                                                            </s:iterator>
                                                            <td></td></tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </s:iterator>
                                        </s:if>                                        
                                    </s:if>
                                    <s:if test="%{#accessProfile.guestUserProfile != null}">
                                        <tr class="npcListBlock">
                                            <td width="15px">
                                                <s:if test="%{#accessProfile.guestUserProfile.enableAssign && #accessProfile.guestUserProfile.assignRules!=null && #accessProfile.guestUserProfile.assignRules.size>0}">
                                                <img src="<s:url value="/images/expand_plus.gif" />"
                                                    width="12px" height="12px" alt="Display/Hide Reassign User Profile" title="Display/Hide Reassign User Profile"
                                                    onclick="displayHideAssignUs(this);"/>                                               
                                                </s:if>
                                            </td>
                                            <td width="32px" style="padding-top: 8px;">
                                                <s:if test="%{#accessProfile.guestUserProfile.enableAssign && #accessProfile.guestUserProfile.assignRules!=null && #accessProfile.guestUserProfile.assignRules.size>0}">
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                                </s:if>
                                            <s:else>
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                    width="30px" height="30px" alt="user profile" title="user profile" />
                                            </s:else>
                                            </td>
                                            <td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#accessProfile.guestUserProfile.id}" />');"><span title="<s:property value="%{#accessProfile.guestUserProfile.userProfileName}" />"><s:property value="%{#accessProfile.guestUserProfile.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(guest)</span></td>
                                            <td class="paddingLeft">&nbsp;
                                                    <s:if test="%{#accessProfile.guestUserProfile.vlan != null}">
                                                        <table cellspacing="0" cellpadding="0" border="0">
                                                            <tr>
                                                            <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                            <s:if test="%{#myUsMapping.userProfile.id==#accessProfile.guestUserProfile.id}">
                                                            <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
                                                                onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                    <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                        width="30px" height="30px" alt="vlan" title="vlan" />
                                                            </td><td>&nbsp;
                                                            <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                            </td></s:if>
                                                            </s:iterator>
                                                            <td></td></tr>
                                                        </table>                                                        
                                                    </s:if>
                                                    
                                            </td>
                                        </tr>
                                        <s:if test="%{#accessProfile.guestUserProfile.enableAssign && #accessProfile.guestUserProfile.assignRules!=null && #accessProfile.guestUserProfile.assignRules.size>0}">
                                            <s:iterator value="%{#accessProfile.guestUserProfile.assignRules}" id="userProfileGuestAssignStatus">
                                                <tr class="npcListBlockSub" style="display:none;">
                                                    <td width="15px"></td>
                                                    <td width="32px" style="padding-top: 8px;"></td>
                                                    <td width="162px">
                                                    <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                                        style="vertical-align: middle;"
                                                                        width="30px" height="30px" alt="user profile" title="user profile" />&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#userProfileGuestAssignStatus.userProfileId}" />');"><s:property value="%{#userProfileGuestAssignStatus.UserProfileNameSubstr}" /></a></td>
                                                    <td class="paddingLeft">&nbsp;
                                                        <table cellspacing="0" cellpadding="0" border="0">
                                                            <tr>
                                                            <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                            <s:if test="%{#myUsMapping.userProfile.id==#userProfileGuestAssignStatus.userProfileId}">
                                                            <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
                                                                onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                    <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                        width="30px" height="30px" alt="vlan" title="vlan" />
                                                            </td><td>&nbsp;
                                                            <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                            </td></s:if>
                                                            </s:iterator>
                                                            <td></td></tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </s:iterator>
                                        </s:if>                                        
                                    </s:if>
                                <s:iterator value="%{#accessProfile.authOkUserProfile}" id="authOkStatus">
                                    <tr class="npcListBlock">
                                        <td width="15px">
                                            <s:if test="%{#authOkStatus.enableAssign && #authOkStatus.assignRules!=null && #authOkStatus.assignRules.size>0}">
                                            <img src="<s:url value="/images/expand_plus.gif" />"
                                                width="12px" height="12px" alt="Display/Hide Reassign User Profile" title="Display/Hide Reassign User Profile"
                                                onclick="displayHideAssignUs(this);"/>                                               
                                            </s:if>
                                        </td>
                                        <td width="32px" style="padding-top: 8px;">
                                            <s:if test="%{#authOkStatus.enableAssign && #authOkStatus.assignRules!=null && #authOkStatus.assignRules.size>0}">
                                            <img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
                                                width="30px" height="30px" alt="user profile" title="user profile" />
                                        </s:if>
                                        <s:else>
                                            <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                width="30px" height="30px" alt="user profile" title="user profile" />
                                        </s:else>
                                        </td>
                                        <td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#authOkStatus.id}" />');"><span title="<s:property value="%{#authOkStatus.userProfileName}" />"><s:property value="%{#authOkStatus.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(Auth Ok<s:if test="%{#accessProfile.portType == 1}"> voice</s:if>)</span></td>
                                        <td class="paddingLeft">&nbsp;
                                                <s:if test="%{#authOkStatus.vlan != null}">
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                        <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                        <s:if test="%{#myUsMapping.userProfile.id==#authOkStatus.id}">
                                                        <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
															onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                    width="30px" height="30px" alt="vlan" title="vlan" />
                                                        </td><td>&nbsp;
                                                        <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                        </td></s:if>
                                                        </s:iterator>
                                                        <td></td></tr>
                                                    </table>                                                        
                                                </s:if>
                                        </td>
                                    </tr>
                                    <s:if test="%{#authOkStatus.enableAssign && #authOkStatus.assignRules!=null && #authOkStatus.assignRules.size>0}">
                                        <s:iterator value="%{#authOkStatus.assignRules}" id="authOkStatusAssignStatus">
                                            <tr class="npcListBlockSub" style="display:none;">
                                                <td width="15px"></td>
                                                <td width="32px" style="padding-top: 8px;"></td>
                                                <td width="162px">
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                                        style="vertical-align: middle;"
                                                                        width="30px" height="30px" alt="user profile" title="user profile" />&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#authOkStatusAssignStatus.userProfileId}" />');"><s:property value="%{#authOkStatusAssignStatus.UserProfileNameSubstr}" /></a></td>
                                                <td class="paddingLeft">&nbsp;
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                        <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                        <s:if test="%{#myUsMapping.userProfile.id==#authOkStatusAssignStatus.userProfileId}">
                                                        <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
															onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                    width="30px" height="30px" alt="vlan" title="vlan" />
                                                        </td><td>&nbsp;
                                                        <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                       	</td></s:if>
                                                        </s:iterator>
                                                        <td></td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </s:iterator>
                                    </s:if>                                        
                                </s:iterator>
                                <s:iterator value="%{#accessProfile.authOkDataUserProfile}" id="authOkDataStatus">
                                    <tr class="npcListBlock">
                                        <td width="15px">
                                            <s:if test="%{#authOkDataStatus.enableAssign && #authOkDataStatus.assignRules!=null && #authOkDataStatus.assignRules.size>0}">
                                            <img src="<s:url value="/images/expand_plus.gif" />"
                                                width="12px" height="12px" alt="Display/Hide Reassign User Profile" title="Display/Hide Reassign User Profile"
                                                onclick="displayHideAssignUs(this);"/>                                               
                                            </s:if>
                                        </td>
                                        <td width="32px" style="padding-top: 8px;">
                                            <s:if test="%{#authOkDataStatus.enableAssign && #authOkDataStatus.assignRules!=null && #authOkDataStatus.assignRules.size>0}">
                                            <img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
                                                width="30px" height="30px" alt="user profile" title="user profile" />
                                        </s:if>
                                        <s:else>
                                            <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                width="30px" height="30px" alt="user profile" title="user profile" />
                                        </s:else>
                                        </td>
                                        <td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#authOkDataStatus.id}" />');"><span title="<s:property value="%{#authOkDataStatus.userProfileName}" />"><s:property value="%{#authOkDataStatus.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(Auth OK <s:if test="%{#accessProfile.portType == 1}">data</s:if>)</span></td>
                                        <td class="paddingLeft">&nbsp;
                                                <s:if test="%{#authOkDataStatus.vlan != null}">
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                        <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                        <s:if test="%{#myUsMapping.userProfile.id==#authOkDataStatus.id}">
                                                        <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
															onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                    width="30px" height="30px" alt="vlan" title="vlan" />
                                                        </td><td>&nbsp;
                                                        <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                        </td></s:if>
                                                        </s:iterator>
                                                        <td></td></tr>
                                                    </table>                                                        
                                                </s:if>
                                        </td>
                                    </tr>
                                    <s:if test="%{#authOkDataStatus.enableAssign && #authOkDataStatus.assignRules!=null && #authOkDataStatus.assignRules.size>0}">
                                        <s:iterator value="%{#authOkDataStatus.assignRules}" id="authOkDataStatusAssignStatus">
                                            <tr class="npcListBlockSub" style="display:none;">
                                                <td width="15px"></td>
                                                <td width="32px" style="padding-top: 8px;"></td>
                                                <td width="162px">
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                                        style="vertical-align: middle;"
                                                                        width="30px" height="30px" alt="user profile" title="user profile" />&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#authOkDataStatusAssignStatus.userProfileId}" />');"><s:property value="%{#authOkDataStatusAssignStatus.UserProfileNameSubstr}" /></a></td>
                                                <td class="paddingLeft">&nbsp;
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                        <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                        <s:if test="%{#myUsMapping.userProfile.id==#authOkDataStatusAssignStatus.userProfileId}">
                                                        <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
															onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                    width="30px" height="30px" alt="vlan" title="vlan" />
                                                        </td><td>&nbsp;
                                                        <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                        </td></s:if>
                                                        </s:iterator>
                                                        <td></td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </s:iterator>
                                    </s:if>                                        
                                </s:iterator>
                                <s:iterator value="%{#accessProfile.authFailUserProfile}" id="authFailStatus">
                                    <tr class="npcListBlock">
                                        <td width="15px">
                                            <s:if test="%{#authFailStatus.enableAssign && #authFailStatus.assignRules!=null && #authFailStatus.assignRules.size>0}">
                                            <img src="<s:url value="/images/expand_plus.gif" />"
                                                width="12px" height="12px" alt="Display/Hide Reassign User Profile" title="Display/Hide Reassign User Profile"
                                                onclick="displayHideAssignUs(this);"/>                                               
                                            </s:if>
                                        </td>                                
                                        <td width="32px" style="padding-top: 8px;">
                                            <s:if test="%{#authFailStatus.enableAssign && #authFailStatus.assignRules!=null && #authFailStatus.assignRules.size>0}">
                                            <img src="<s:url value="/images/hm_v2/profile/hm-icon-user-re.png" />"
                                                width="30px" height="30px" alt="user profile" title="user profile" />
                                        </s:if>
                                        <s:else>
                                            <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                width="30px" height="30px" alt="user profile" title="user profile" />
                                        </s:else>
                                        </td>
                                        <td width="162px" style="padding-top: 18px;">&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#authFailStatus.id}" />');"><span title="<s:property value="%{#authFailStatus.userProfileName}" />"><s:property value="%{#authFailStatus.userProfileNameSubstr}" /></span></a><br/>&nbsp;<span class="smallTd">(Auth Fail)</span></td>
                                        <td class="paddingLeft">&nbsp;
                                                <s:if test="%{#authFailStatus.vlan != null}">
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                        <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                        <s:if test="%{#myUsMapping.userProfile.id==#authFailStatus.id}">
                                                        <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
															onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                    width="30px" height="30px" alt="vlan" title="vlan" />
                                                        </td><td>&nbsp;
                                                        <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                        </td></s:if>
                                                        </s:iterator>
                                                        <td></td></tr>
                                                    </table>                                                        
                                                </s:if>
                                        </td>
                                    </tr>
                                    <s:if test="%{#authFailStatus.enableAssign && #authFailStatus.assignRules!=null && #authFailStatus.assignRules.size>0}">
                                        <s:iterator value="%{#authFailStatus.assignRules}" id="authFailStatusAssignStatus">
                                            <tr class="npcListBlockSub" style="display:none;">
                                                <td width="15px"></td>
                                                <td width="32px" style="padding-top: 8px;"></td>
                                                <td width="162px">
                                                <img src="<s:url value="/images/hm_v2/profile/hm-icon-users.png" />"
                                                                        style="vertical-align: middle;"
                                                                        width="30px" height="30px" alt="user profile" title="user profile" />&nbsp;<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfiles2('<s:property value="%{#accessProfile.id}" />', '<s:property value="%{#authFailStatusAssignStatus.userProfileId}" />');"><s:property value="%{#authFailStatusAssignStatus.UserProfileNameSubstr}" /></a></td>
                                                <td class="paddingLeft">&nbsp;
                                                    <table cellspacing="0" cellpadding="0" border="0">
                                                        <tr>
                                                        <s:iterator value="%{usMapping.values}" id="myUsMapping">
                                                        <s:if test="%{#myUsMapping.userProfile.id==#authFailStatusAssignStatus.userProfileId}">
                                                        <td onmouseover="javascript: displayVlanItemsPanel(this, '<s:property value="%{#myUsMapping.vlan.id}" />');"
															onmouseout="javascript: hideIFrameNoModalPanel();">
                                                                <img src="<s:url value="/images/hm_v2/profile/HM-icon-networks-30.png" />"
                                                                    width="30px" height="30px" alt="vlan" title="vlan" />
                                                        </td><td>&nbsp;
                                                        <span>&nbsp;<s:property value="%{#myUsMapping.vlan.vlanName}" /></span>
                                                        </td></s:if>
                                                        </s:iterator>
                                                        <td></td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </s:iterator>
                                    </s:if>                                        
                                </s:iterator>
                                    <tr>
                                        <td colspan="3">
                                            <table border="0" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td width="32px;" />
                                                    <td style="padding-top:4px;">
                                                    <s:if test="%{#accessProfile.defUserProfile !=null || #accessProfile.selfRegUserProfile !=null || #accessProfile.authOkUserProfile.size > 0 || #accessProfile.authFailUserProfile.size > 0}">
                                                        <s:if test="%{writeDisabled != 'disabled'}">
                                                        	<a class="npcLinkA" href="javascript:void(0);" onClick="javascript: selectUserProfile4Lan('<s:property value="%{#accessProfile.id}" />', '', <s:property value="%{#accessProfile.portType == 1}"/>, <s:property value="%{#accessProfile.product != 3}"/>);" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:if>
                                                        <s:else>
                                                        	<a class="npcLinkA" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:else>
                                                    </s:if>
                                                    <s:else>
                                                        <s:if test="%{writeDisabled != 'disabled'}">
                                                        <a class="npcLinkAEmpty" href="javascript:void(0);" onClick="javascript: selectUserProfile4Lan('<s:property value="%{#accessProfile.id}" />', '', <s:property value="%{#accessProfile.portType == 1}"/>, <s:property value="%{#accessProfile.product != 3}"/>);" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:if>
                                                        <s:else>
                                                        <a class="npcLinkAEmpty" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.button.addremove"/>" ><span><s:text name="config.networkpolicy.button.addremove"/></span></a>
                                                        </s:else>
                                                    </s:else>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                        <td>
                                            <table border="0" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td width="32px;" />
                                                    <td style="padding-top:4px;">
                                                    <s:if test="%{#accessProfile.defUserProfile !=null || #accessProfile.selfRegUserProfile !=null || #accessProfile.authOkUserProfile.size > 0 || #accessProfile.authFailUserProfile.size > 0}">
                                                        <s:if test="%{writeDisabled != 'disabled'}">
                                                        <a class="npcLinkA" href="javascript:void(0);" onClick="javascript: editUserProfileVlanMappingPanel({type: 'port-access', id: '<s:property value="%{#accessProfile.id}" />', parentOpen: false});" title="<s:text name="config.networkpolicy.userprofile.vlan.mapping"/>" ><span><s:text name="config.networkpolicy.userprofile.vlan.mapping"/></span></a>
                                                        </s:if>
                                                        <s:else>
                                                        <a class="npcLinkA" href="javascript:void(0);" onClick="showWarmMessage();" title="<s:text name="config.networkpolicy.userprofile.vlan.mapping"/>" ><span><s:text name="config.networkpolicy.userprofile.vlan.mapping"/></span></a>
                                                        </s:else>
                                                    </s:if>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                            </s:elseif>
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
</s:if>