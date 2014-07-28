<%@taglib prefix="s" uri="/struts-tags"%>
<s:if test="%{!dataSource.configType.bonjourOnly}">
    <tr>
        <td>
            <table cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td class="npcHead1" style="padding-left: 35px;padding-right: 20px;">
                    <s:text name="config.networkpolicy.port.template.title"/>
                    </td>
                    <s:if test="%{writeDisabled != 'disabled'}">
                        <td class="npcButton" ><a href="javascript:void(0);" onclick="addRemovePort(false, <s:property value="%{dataSource.configType.wirelessOnly}"/>);" id="btAddRemoveLan" style="visibility: hidden;" class="btCurrent" title="<s:text name="config.networkpolicy.button.choose"/>"><span><s:text name="config.networkpolicy.button.choose"/></span></a></td>
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
    <s:set name="ownerID" value="domainId" scope="session"/>
    <s:if test="%{dataSource.portProfiles != null && dataSource.portProfiles.size>0}">
    <tr>
    	<td style="padding-left: 35px;line-height:20px;" class="noteInfo">
    		<s:text name="config.portTemplate.note" />
    	</td>
	</tr>    	
    <s:iterator value="%{dataSource.sortedPortProfiles}" status="status" id="portProfile">
    <s:if test="%{#portProfile != null}">
    <tr>
        <td style="padding:5px 0 5px 0;">
        <table cellspacing="0" cellpadding="0" border="0" width="100%">
         <tr>
           <td style="padding-left: 35px;"><label class="npcHead2" style="font-size:14px;"><s:property value="%{#portProfile.selectedItemPurposeDesc}"/></label>
               <label style="margin-left:20px"><a class="npcLinkA" href="javascript: void(0);" alt="<s:text name="config.networkpolicy.button.addTemplate" />" title="<s:text name="config.networkpolicy.button.addTemplate" />" onclick="addRemovePortItem(<s:property value='%{#portProfile.id}'/>);"><s:text name="config.networkpolicy.button.addTemplate"></s:text> </a></label>
           </td>
         </tr>
      
         <tr>
           <td>
            <table cellspacing="0" cellpadding="0" border="0" width="100%" style="table-layout: fixed;">
             <!-- non default -->
             	<s:if test="%{#portProfile.items !=null && #portProfile.items.size>0}">
             		<s:iterator value="%{#portProfile.portTemplateForDisplay}" status="tem" id="port">
             				<tr style="height:30px;">
             					<td class="smallTd portTemplate" style="padding-left:40px;"height="22px">
             					<table>
             						<tbody>
             						<s:iterator value="port" status="mark" id="items">
             						<s:if test="%{#items.nonDefault != null}">
             						<tr style="height: 25px">
             						<td>
             						<s:if test="#mark.index == 0">
             							 <span class="arrowRight" id="span_<s:property value='%{#portProfile.id}'/>_nonDefault<s:property value='%{#items.nonDefault.id}'/>"
                               				ref="<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>"
                               				tmpId="<s:property value='%{#items.nonDefault.id}'/>"
                              				portNum="<s:property value='%{#items.nonDefault.portNum}'/>"
                               				models="<s:property value='%{#items.nonDefault.deviceModels}'/>"
                               				deviceType="<s:property value='%{#items.nonDefault.deviceType}'/>">
                               				&nbsp;<label class="npcLinkA"><s:property value="%{#items.nonDefault.name}"/></label>&nbsp;
                               			</span>
             						</s:if>
             						<s:else>
             							<span style="display:none" class="arrowRight" id="span_<s:property value='%{#portProfile.id}'/>_nonDefault<s:property value='%{#items.nonDefault.id}'/>"
                               				ref="<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>"
                               				tmpId="<s:property value='%{#items.nonDefault.id}'/>"
                              				portNum="<s:property value='%{#items.nonDefault.portNum}'/>"
                               				models="<s:property value='%{#items.nonDefault.deviceModels}'/>"
                               				deviceType="<s:property value='%{#items.nonDefault.deviceType}'/>">
                               				&nbsp;<label class="npcLinkA"><s:property value="%{#items.nonDefault.name}"/></label>&nbsp;
                               			</span>
             						</s:else>
             						</td>
             						<td>
             						<span>
                              			<label style="font-size:11px;color: #6F6F6F">
                               			<s:if test="%{#items.type == 4}">
                               	   			<s:property value="#ownerID"/>
                                   		<span class="tagButton">
                                   			<s:property value="#items.getClassifierName1(#session.ownerID)"/>
                                   		</span>
                                  			&nbsp;&nbsp;
                                   		<span class="tagButton">
                                   			<s:property value="#items.getClassifierName2(#session.ownerID)"/>
                                   		</span>
                                   		&nbsp;&nbsp;
                                   		<span class="tagButton">
                                   			<s:property value="#items.getClassifierName3(#session.ownerID)"/>
                                   		</span>
                              			</s:if>
                               			<s:elseif test="#items.type == 3">
                               	   			<span class="tagButton">
                                   				<s:property value="#items.useTypeName"/>&nbsp;&#58;&nbsp;<s:property value="#items.typeName"/>
                                   			</span>
                               			</s:elseif>
                               			<s:elseif test="#items.type == 2">
                                			<span class="tagButton">
                                				<s:property value="#items.useTypeName"/>&nbsp;&#58;&nbsp;<s:property value="#items.location.MapName"/>
                                			</span>
                               			</s:elseif>
                                		</label>
                              		</span>
                              		</td>
                              		</tr>
                              		</s:if>
             					</s:iterator>
                              		</tbody>
             					</table>
             					</td>
             				</tr>
             			<s:iterator value="port" status="mark" id="items">
             				<s:if test="%{#items.nonDefault != null}">
             				<s:if test="#mark.index == 0">
                        		<tr><td id="wiredPortTmpl_<s:property value="%{#items.nonDefault.id}"/>_<s:property value='%{#mark.index}'/>" style="display:none;<s:if test="%{#items.nonDefault.portNum < 24}">padding-left:35px;</s:if>">
                        		<div class="portTemplate <s:if test="%{#items.nonDefault.portNum == 48}">sr48</s:if><s:elseif test="%{#items.nonDefault.portNum == 24}">sr24</s:elseif>" style="<s:if test="%{#items.nonDefault.portNum < 24}">width: 680px;padding-left: 20px;</s:if>border: 1px solid;">
	                               <div>
	                                  <p style="margin: 5px 0;"><s:text name="config.port.access.selection.note"></s:text></p>
	                                  <s:if test="%{#items.nonDefault.portNum >= 24}">
	                                     <div style="position: relative;">
		                                    <a class="npcLinkA clearSelection" href="javascript: void(0);" tabindex="-1" style="display: none;" 
	                                             id="enableDeselectAnchor_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>">Clear Port Selections</a>
	                                         <label class="clearSelection" tabindex="-1" 
	                                             id="disabledDeselectAnchor_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>">Clear Port Selections</label>
	                                      </div>
	                                 </s:if>
	                               </div>
	                            	<div class="clearfix" id="wirePortGroupSection_<s:property value="%{#items.nonDefault.id}"/>_<s:property value='%{#mark.index}'/>" 
	                                   style="padding: 10px 0;<s:if test="%{#items.nonDefault.portNum >= 24}">margin-left:10px;</s:if>"></div>
                                	<s:if test="%{#items.nonDefault.portNum > 6}">
	                                <div class="linkagg">
	                                   <input type="checkbox" name="enableLinkAgg" id="enableLinkAggChk_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>" disabled="disabled"/>
	                                   <label for="enableLinkAggChk_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>"><s:text name="config.port.linkaggregation.desc"/></label>&nbsp;
	                                   <input id="portChannel_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>" style="width: 60px;" maxlength="2" class="portChannel"
	                                       onkeypress="return hm.util.keyPressPermit(event,'ten');" disabled/>&nbsp;(1-30)
	                                </div>
                                	</s:if>
                                 	<div style="display: none;" id="fe_errorRow_<s:property value="%{#items.nonDefault.id}"/>_<s:property value='%{#mark.index}'/>">
                                    <div class="noteError"  id="textfe_errorRow_<s:property value="%{#items.nonDefault.id}"/>_<s:property value='%{#mark.index}'/>">ChangeMe</div>
                                 	</div>
			                     	<div style="text-align: center; position: relative;">
			                          <input type="button" name="assignPorts" value="Configure" id="assignPorts_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>" disabled="disabled"/>
			                          <!-- input type="button" name="resetPorts" value="Reset" id="resetPorts_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>" disabled="disabled"/-->
				                     <s:if test="%{#items.nonDefault.portNum > 6}">
			                            <s:if test="%{#items.nonDefault.configured}">
			                            <a class="npcLinkA optional" href="javascript: void(0);" onclick="viewPortAddition(<s:property value='%{#items.nonDefault.id}'/>)"
			                                 tabindex="-1"
			                                 <s:if test="%{!#items.nonDefault.configured}">style="display: none;"</s:if> 
			                                 id="editAPSAnchor_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>">Additional Port Settings</a>
			                            </s:if>
			                            <s:else>
			                            <label class="optional" tabindex="-1"
			                                 <s:if test="%{!#items.nonDefault.configured}">style="display: none;"</s:if> 
			                                 id="editAPSAnchor_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>">Additional Port Settings</label>
			                            </s:else>
			                            <label class="optional" tabindex="-1" 
			                                 <s:if test="%{#items.nonDefault.configured}">style="display: none;"</s:if> 
			                                 id="disabledEditAPSAnchor_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>">Additional Port Settings</label>
			                        </s:if>
			                    	</div>
			                    	<div id="wiredPortDescTmpl_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>" class="portDesc" style="padding-bottom: 10px;"></div>
						        	<div id="wiredPortTooltip_<s:property value='%{#items.nonDefault.id}'/>_<s:property value='%{#mark.index}'/>" class="ui-tooltip" style="display: none;">
						             	<div class="ui-tooltip-content">Select all ports using this port type</div>
						             	<div class="arrow"></div>
						        	</div>
			              		</div>
                       		</td></tr>
                       		</s:if>
             			 </s:if>
             			</s:iterator>
             		</s:iterator>
             	</s:if>
          <!-- default -->
                <tr style="height:30px;"><td class="smallTd portTemplate" style="padding-left:40px;" height="22px">
                <table>
                	<tbody>
                		<tr>
                			<td>
                				<span class="arrowRight" id="span_<s:property value='%{#portProfile.id}'/>"
                    			ref="<s:property value='%{#portProfile.id}'/>" 
                    			tmpId="<s:property value='%{#portProfile.id}'/>"
                    			portNum="<s:property value='%{#portProfile.portNum}'/>"
                    			models="<s:property value='%{#portProfile.deviceModels}'/>"
                    			deviceType="<s:property value='%{#portProfile.deviceType}'/>">
                    			&nbsp;<label class="npcLinkA"><s:property value="%{#portProfile.name}"/></label>&nbsp;
                				</span>
                			</td>
                			<td>
                				<span>     
                    				<label style="font-size:11px;color: #6F6F6F"><span class="tagButton"><s:text name="config.v2.select.user.profile.popup.default"></s:text></span></label>
                				</span>
                			</td>
                		</tr>
                	</tbody>
                </table>
                </td></tr>
                <tr><td id="wiredPortTmpl_<s:property value="%{#portProfile.id}"/>" style="display:none;<s:if test="%{#portProfile.portNum < 24}">padding-left:35px;</s:if>">
                <div class="portTemplate <s:if test="%{#portProfile.portNum == 48}">sr48</s:if><s:elseif test="%{#portProfile.portNum == 24}">sr24</s:elseif>" style="<s:if test="%{#portProfile.portNum < 24}">width: 680px;padding-left: 20px;</s:if>border: 1px solid;">
	                <div>
	                   <p style="margin: 5px 0;"><s:text name="config.port.access.selection.note"></s:text></p>
	                   <s:if test="%{#portProfile.portNum >= 24}">
	                   <div style="position: relative;">
		                   <a class="npcLinkA clearSelection" href="javascript: void(0);" tabindex="-1"
	                             style="display: none;" 
	                             id="enableDeselectAnchor_<s:property value='%{#portProfile.id}'/>">Clear Port Selections</a>
	                       <label class="clearSelection" tabindex="-1" 
	                             id="disabledDeselectAnchor_<s:property value='%{#portProfile.id}'/>">Clear Port Selections</label>
	                   </div>
	                   </s:if>
	                </div>
	                <div class="clearfix" id="wirePortGroupSection_<s:property value="%{#portProfile.id}"/>" 
	                   style="padding: 10px 0;<s:if test="%{#portProfile.portNum >= 24}">margin-left:10px;</s:if>"></div>
                <s:if test="%{#portProfile.portNum > 6}">
	                <div class="linkagg">
	                    <input type="checkbox" name="enableLinkAgg" id="enableLinkAggChk_<s:property value='%{#portProfile.id}'/>" disabled="disabled"/>
	                    <label for="enableLinkAggChk_<s:property value='%{#portProfile.id}'/>"><s:text name="config.port.linkaggregation.desc"/></label>&nbsp;
	                    <input id="portChannel_<s:property value='%{#portProfile.id}'/>" style="width: 60px;" maxlength="2" class="portChannel"
	                        onkeypress="return hm.util.keyPressPermit(event,'ten');" disabled/>&nbsp;(1-30)
	                </div>
                </s:if>
                    <div style="display: none;" id="fe_errorRow_<s:property value="%{#portProfile.id}"/>">
                        <div class="noteError"  id="textfe_errorRow_<s:property value="%{#portProfile.id}"/>">ChangeMe</div>
                    </div>
			        <div style="text-align: center; position: relative;">
			            <input type="button" name="assignPorts" value="Configure" id="assignPorts_<s:property value='%{#portProfile.id}'/>" disabled="disabled"/>
			            <!-- input type="button" name="resetPorts" value="Reset" id="resetPorts_<s:property value='%{#portProfile.id}'/>" disabled="disabled"/-->
                        <s:if test="%{#portProfile.portNum > 6}">
	                        <s:if test="%{#portProfile.configured}">
				            <a class="npcLinkA optional" href="javascript: void(0);" onclick="viewPortAddition(<s:property value='%{#portProfile.id}'/>)"
				                 tabindex="-1"
				                 <s:if test="%{!#portProfile.configured}">style="display: none;"</s:if> 
				                 id="editAPSAnchor_<s:property value='%{#portProfile.id}'/>">Additional Port Settings</a>
				            </s:if>
	                        <s:else>
				            <label class="optional" tabindex="-1"
				                 <s:if test="%{!#portProfile.configured}">style="display: none;"</s:if> 
				                 id="editAPSAnchor_<s:property value='%{#portProfile.id}'/>">Additional Port Settings</label>
				            </s:else>
				            <label class="optional" tabindex="-1" 
				                 <s:if test="%{#portProfile.configured}">style="display: none;"</s:if> 
				                 id="disabledEditAPSAnchor_<s:property value='%{#portProfile.id}'/>">Additional Port Settings</label>
				        </s:if>
			        </div>
			        <div id='wiredPortDescTmpl_<s:property value="%{#portProfile.id}"/>' class="portDesc" style="padding-bottom: 10px;"></div>
			        <div id='wiredPortTooltip_<s:property value="%{#portProfile.id}"/>' class="ui-tooltip" style="display: none;">
			             <div class="ui-tooltip-content">Select all ports using this port type</div>
			             <div class="arrow"></div>
			        </div>
			    </div>
                </td></tr>
            </table>
           </td>
          </tr></table>
        </td>
    </tr>
    </s:if>
    </s:iterator>
    </s:if>
    <s:else>
    <s:if test="%{!dataSource.configType.wirelessOrBonjourOnly}">
        <tr>
            <td align="center">
            <div class="chooseNote">Click <span style="font-weight:bold;">choose</span> to add wired devices to your network.</div>
            </td>
        </tr>
    </s:if>
    </s:else>
    <tr><td><div id="portTemplatePanel"></div></td></tr>
    <tr><td><div id="portMirrorPanel"></div></td></tr>
</s:if>