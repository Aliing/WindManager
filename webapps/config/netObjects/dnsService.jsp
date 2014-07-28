<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.network.DnsServiceProfile"%>
<style>
td.dnsServer {
	width: 100px;
	line-height: 20px;
}
.hint {
color: gray;
}
input[type="radio"] {
	vertical-align:middle; 
	margin-top:-2px;
	margin-bottom:1px;
}
</style>
<script>
var formName = 'dnsService';
var SAME_DNS = <%=DnsServiceProfile.SAME_DNS%>;
var SEPARATE_DNS = <%=DnsServiceProfile.SEPARATE_DNS%>;
var EXTERNAL_DNS = <%=DnsServiceProfile.EXTERNAL_DNS%>;
var INTERNAL_DNS = <%=DnsServiceProfile.INTERNAL_DNS%>;

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		
		<s:if test="%{jsonMode && !parentIframeOpenFlg}">
		if(operation == 'cancel'+'<s:property value="lstForward"/>' ) {
			parent.closeIFrameDialog();
			if(Get(formName + "_overrideDNS").value == "true"){
				parent.openSubnetOverlay();
			}
			return;
		}
		if (operation == 'create'+'<s:property value="lstForward"/>' 
				|| operation == 'update'+'<s:property value="lstForward"/>') {
			var url = "<s:url action='dnsService' includeParams='none' />" + "?jsonMode=true" 
				+ "&ignore=" + new Date().getTime(); 
			YAHOO.util.Connect.setForm(document.forms[formName]);
			var transaction = YAHOO.util.Connect.asyncRequest('post', 
					url, {success : succSaveDNS, failure : failSaveDNS, timeout: 60000}, null);
			return;
		}
		</s:if>
		
		if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].submit();
	}
}

var succSaveDNS = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.succ) {
			if(details.overrideDNS){
				parent.openSubnetOverlay();
				
			}
			if(details.parentDomID) {
				var selectElement = parent.document.getElementById(details.parentDomID);
				if(selectElement) {
					if(details.addedId != null && details.addedId != ''){
						hm.util.insertSelectValue(details.addedId, details.addedName, selectElement, false, true);
					}
				}
			}
			parent.closeIFrameDialog();
		} else {
			var errorRow = new Object();
			errorRow.id='ErrorRow';
			if (details.error) {
				hm.util.reportFieldError(errorRow, details.msg);
			}
			return;
		}
	}catch(e){
		// do nothing now
	}	
}

var failSaveDNS = function(o) {}

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

<div id="content"><s:form action="dnsService">
	<s:hidden name="dataIndex"/>
	<s:hidden name="tmpDNSServerId"/>
	<s:hidden name="overrideDNS" />
	<s:if test="%{jsonMode == true}">
	<s:hidden name="id" />
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<s:hidden name="parentIframeOpenFlg" />
	<s:hidden name="contentShowType" />
	<div class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="80%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-DNS_Service.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
							<td class="dialogPanelTitle"><s:text name="config.title.dnsService"/>&nbsp;</td>
							</s:if>
							<s:else>
							<td class="dialogPanelTitle"><s:text name="config.title.dnsService.edit"/>&nbsp;</td>
							</s:else>
							<td>
								<a href="javascript:void(0);"  onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk" />
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="submitAction('cancel<s:property value="lstForward"/>');" title="Cancel"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Cancel</span></a></td>
								<s:if test="%{dataSource.id == null}">
								<td class="npcButton">
								<s:if test="%{'' == writeDisabled}">
									<a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a>
								</s:if>
								</td>
								</s:if>
								<s:else>
								<td class="npcButton">
								<s:if test="%{'' == writeDisabled}">
									<a href="javascript:void(0);" class="btCurrent" style="float: right;" onclick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
								</s:if>
								</td>
								</s:else>
							</tr>
						</table>
					</td>
				</tr>
			</table>
	</div>
	</s:if>
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
	<s:if test="%{jsonMode == false}">
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
							onClick="submitAction('<%=Navigation.L2_FEATURE_DNS_SERVICE%>');">
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
		<tr id="fe_ErrorRow" style="display: none">
			<td class="noteError" id="textfe_ErrorRow" colspan="4">To be changed</td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
			<table cellspacing="0" cellpadding="0" border="0"
				width="630">
			</s:if>
			<s:else>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="650">
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
									<td colspan="2"><s:textfield name="dataSource.serviceName"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										size="24" maxlength="%{serviceNameLength}"
										disabled="%{disabledName}" />&nbsp;<s:text
										name="config.ns.name.range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="125"><s:text
										name="config.dnsService.description" /></td>
									<td colspan="2"><s:textfield name="dataSource.description"
										size="48" maxlength="%{descriptionLength}" />&nbsp;<s:text
										name="config.ns.description.range" /></td>
								</tr>
								<tr>
									<td colspan="3">
									<table>
									<!-- // FIXME: because struts2 bad implement(can't change the layout for verticality) -->
										<%-- <s:iterator value="dnsServiceMode">
										<tr>
										<td><input id="DNSSplit_<s:property value="modeType"/>"
													type="radio" value="<s:property value="modeType"/>"
													name="dataSource.serviceType" onclick="prepareServiceType(this.value);"
													<s:if test="%{dataSource.serviceType == modeType}">
													checked="checked"
													</s:if>
													/>
											<label for="DNSSplit_<s:property value="modeType"/>"><s:property value="modeDesc" /></label>
										</td>
										</tr>
										</s:iterator> --%>
										
										<tr>
												<td>
												<s:iterator value="dnsServiceMode">
													<s:if test="%{modeType == 2}">
														<input id="DNSSplit_<s:property value="modeType"/>"
															type="radio" value="<s:property value="modeType"/>"
															name="dataSource.serviceType" onclick="prepareServiceType(this.value);"
															<s:if test="%{dataSource.serviceType == modeType}">
															checked="checked"
															</s:if>
															/>
														<label for="DNSSplit_<s:property value="modeType"/>"><s:property value="modeDesc" /></label>
													</s:if>
												</s:iterator>
												</td>
											</tr>
											<tr>
												<td><input id="DNSSplit_<%=DnsServiceProfile.INTERNAL_DNS%>" type="radio" value="<%=DnsServiceProfile.INTERNAL_DNS%>"
														checked="%{internalDNSChecked}" onclick="prepareServiceType(this.value);"/>
													<label for="DNSSplit_<%=DnsServiceProfile.INTERNAL_DNS%>"><s:text name="geneva_10.config.title.dnsService.dnsProxy.internal.desc" /></label>
												</td>
											</tr>
											<tr id = "internalRadioSection">
												<td style="padding-left: 17px;">
													<table>
														<tr>
															<td>
															<s:iterator value="dnsServiceMode">
																<s:if test="%{modeType == 0}">
																	<input id="DNSSplit_<s:property value="modeType"/>"
																		type="radio" value="<s:property value="modeType"/>"
																		name="dataSource.serviceType" onclick="prepareServiceType(this.value);"
																		<s:if test="%{dataSource.serviceType == modeType}">
																		checked="checked"
																		</s:if>
																		/>
																	<label for="DNSSplit_<s:property value="modeType"/>"><s:property value="modeDesc" /></label>
																</s:if>
															</s:iterator>
															</td>
														</tr>
														<tr>
															<td>
															<s:iterator value="dnsServiceMode">
																<s:if test="%{modeType == 1}">
																	<input id="DNSSplit_<s:property value="modeType"/>"
																		type="radio" value="<s:property value="modeType"/>"
																		name="dataSource.serviceType" onclick="prepareServiceType(this.value);"
																		<s:if test="%{dataSource.serviceType == modeType}">
																		checked="checked"
																		</s:if>
																		/>
																	<label for="DNSSplit_<s:property value="modeType"/>"><s:property value="modeDesc" /></label>
																</s:if>
															</s:iterator>
															</td>
														</tr>
													</table>
												</td>
											</tr>
									</table>
									</td>
								</tr>
								<tr id="wordSpaceSection">
								<td colspan="3">
									<fieldset>
										<legend><s:text name="config.title.dnsService.fieldSet.workspace"/></legend>
										<table>
											<tr>
												<td>
												<table>
													<tr>
														<td>
														<table>
														<tr><td><s:text name="config.title.dnsService.domainNames"/><font color="red"><s:text name="*"/></font></td></tr>
														<tr id="fe_dnsService_domainNames" style="display: none">
															<td class="noteError" id="textfe_dnsService_domainNames">To be changed</td>
														</tr>
														<tr><td><s:textarea name="domainNames" cssStyle="width: 260px;ime-mode: disabled;resize:none;" rows="4"
																	onkeypress="return keyPressPermitTextarea(event);"/></td></tr>
														</table>
														</td>
														<td>
														<table>
														<s:iterator value="dnsNameItems" id="internalDnsName" status="st">
															<tr>
															<td class="dnsServer" nowrap="nowrap"><s:property value="internalDnsName"/>
															<s:if test="#st.First"><font color="red"><s:text name="*"/></font></s:if>
															</td>
															<td valign="top" width="210" style="padding:2px 5px 2px 0px;" nowrap="nowrap">
															<s:if test="%{#st.index+1 ==1}">
														    <ah:createOrSelect divId="errorDisplay11" swidth="155px"
																		list="availableDnsServers" typeString="IpAddressIn1" inputKeyPress="ip" 
																		selectIdName="internalDNS_1" inputValueName="dataSource.internalIP1" />
															</s:if>
															<s:elseif test="%{#st.index+1 ==2}">
														    <ah:createOrSelect divId="errorDisplay12" swidth="155px"
																		list="availableDnsServers" typeString="IpAddressIn2" inputKeyPress="ip" 
																		selectIdName="internalDNS_2" inputValueName="dataSource.internalIP2" />
															</s:elseif>
															<s:else>
														    <ah:createOrSelect divId="errorDisplay13" swidth="155px"
																		list="availableDnsServers" typeString="IpAddressIn3" inputKeyPress="ip" 
																		selectIdName="internalDNS_3" inputValueName="dataSource.internalIP3" />
															</s:else>
															</td>
															</tr>
															<s:if test="!#st.Last"><tr><td height="5px;"/></tr></s:if>
															</s:iterator>
														</table>
														</td>
													</tr>
												</table>
												</td>
											</tr>
											<tr><td height="5px;"></td></tr>
											<!-- Advanced -->
											<tr>
											<td>
												<script type="text/javascript">insertFoldingLabelContext('<s:text name="config.title.dnsService.advanced" />','specificDNSSettings');</script>
											</td>
											</tr>
											<tr>
											<td id="specificDNSSettings">
												<fieldset style="padding: 0px 5px 0px 5px;">
													<legend><s:text name="config.title.dnsService.specific"/></legend>
													<table cellspacing="0" id="specificTableId">
														<tr>
														<td class="buttons" colspan="2">
															<table>
															<tr>
																<td><input type="button" name="new" value="New"
																	class="button" onclick="addSpecificDNSRows();"
																	<s:property value="writeDisabled" />></td>
																<td><input type="button" name="remove" value="Remove"
																	class="button" onclick="removeSpecificRows();"
																	<s:property value="writeDisabled" />></td>
															</tr>
															</table>
														</td>
														</tr>
														<tr id="specificHeader">
															<th class="check" align="left"><input type="checkbox" id="checkAll" onclick="toggleCheckAll(this);"/></th>
															<th><s:text name="config.title.dnsService.specific.domainName" /></th>
															<th><s:text name="config.title.dnsService.specific.dnsServer" /></th>
														</tr>
														<tr id="fe_specificErrorRow" style="display: none">
															<td/>
															<td class="noteError" id="textfe_specificErrorRow" colspan="2" style="padding-left: 40px;">To be changed</td>
														</tr>
														<s:iterator value="specificDNSPair" status="st" id="dnsPair">
														<tr id="specificRow_<s:property value='%{#st.index+1}' />">
															<td class="listCheck"><s:checkbox name="specificDNSChk" onclick="toggleCheck(this)"/></td>
															<td class="list" style="width: 280px;padding-left: 40px;"><s:textfield size="24" name="specificDomain"
																maxlength="32" cssStyle="width: 200px;"
																onkeypress="return hm.util.keyPressPermit(event,'dnsDomain');" /></td>
															<td class="list" style="width: 280px;padding-left: 40px;">
																<div id="errorDisplay_<s:property value='%{#st.index+1}' />" style="position: relative;">
																<span style="overflow: hidden;">
																<s:select list="availableDnsServers" name="specificDNSSelect" cssStyle="width: 155px; " 
																	id="specificDNSId_%{#st.index+1}" value="%{specificDNSId}"
																	listKey="id" listValue="value"
																	title="To add: Choose the blank space and type a new entry."
																	onchange="hm.util.singObjectSelect(this, 'specificDNSText_%{#st.index+1}')"/>
																<s:textfield size="24" name="specificDNS" maxlength="32"
																	id="specificDNSText_%{#st.index+1}"
																	cssStyle="position: absolute; width: 126px; z-index: 2; left: 0px;"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" />	
																	</span>&nbsp;
																	<s:if test="%{'disabled' == writeDisabled}">
																	<img width="16" height="16" title="New" alt="New" src="images/new_disable.png" class="dinl">
																	<img width="16" height="16" title="Modify" alt="Modify" src="images/modify_disable.png" class="dinl">
																	</s:if>
																	<s:else>
																	<a href="javascript:jumpSpecificDNS('newSpecificDNS', <s:property value="%{#st.index+1}"/>)" 
																	class="marginBtn"><img width="16" height="16" title="New" alt="New" src="images/new.png"
																	class="dinl"></a>
																	<a href="javascript:jumpSpecificDNS('editSpecificDNS', <s:property value="%{#st.index+1}"/>)"
																	class="marginBtn"><img width="16" height="16" title="Modify"
																	alt="Modify" src="images/modify.png" class="dinl"></a>
																	</s:else>
																</div>
															</td>
														</tr>
														</s:iterator>
														<tr id="lastEmptyRow"><td height="5px"/></tr>
													</table>
												</fieldset>
											</td>
											</tr>
										</table>
									</fieldset>
								</td>
								</tr>
								<tr><td height="10px"/></tr>
								<tr id="externalSection">
								<td colspan="3">
									<fieldset id="externalFieldSetId">
										<legend id="externalLegendId"><label id="externalLableId"><s:text name="config.title.dnsService.fieldSet.external"/></label></legend>
										
										<table id="externalTableId" width="100%">
											<s:iterator value="externalDnsMode">
											<tr>
											<td>
											<!-- FIXME: temporary hide OpenDNS -->
											<s:if test="%{modeType != 2}">
											<input id="externalServerType_<s:property value="modeType"/>"
														type="radio" value="<s:property value="modeType"/>"
														name="dataSource.externalServerType"
														onclick="changeExternalDNS(this.value);"
														<s:if test="%{dataSource.externalServerType == modeType}">
														checked="checked" 
														</s:if>
														/>
													<label for="externalServerType_<s:property value="modeType"/>"><s:property value="modeDesc" /></label>
											</s:if>
											</td>
											</tr>
											<s:if test="%{modeType == 2}">
											<tr id="hyperLinkRow">
												<td style="padding-left: 25px;"><label>Click <a href="http://www.opendns.com" target="_blank">here</a> to register</label></td>
											</tr>
											</s:if>
											<s:if test="%{modeType == 3}">
											<tr id="externalDNSRow">
											<td rowspan="3" colspan="3" width="310" style="padding-left: 25px;">
												<table>
													<s:iterator value="dnsNameItems" id="externalDnsName" status="st">
													<tr>
													<td class="dnsServer"><s:property value="externalDnsName"/>
													<s:if test="#st.First"><font color="red"><s:text name="*"/></font></s:if>
													</td>
													<td valign="top" width="210" style="padding:2px 5px 2px 0px;">
													<s:if test="%{#st.index+1 == 1}">
												    <ah:createOrSelect divId="errorDisplay21" swidth="155px"
																list="availableDnsServers" typeString="IpAddressEx1"
																selectIdName="externalDNS_1" inputValueName="dataSource.externalIP1" />
													</s:if>
													<s:elseif test="%{#st.index+1 == 2}">
												    <ah:createOrSelect divId="errorDisplay22" swidth="155px"
																list="availableDnsServers" typeString="IpAddressEx2"
																selectIdName="externalDNS_2" inputValueName="dataSource.externalIP2" />
													</s:elseif>
													<s:else>
												    <ah:createOrSelect divId="errorDisplay23" swidth="155px"
																list="availableDnsServers" typeString="IpAddressEx3"
																selectIdName="externalDNS_3" inputValueName="dataSource.externalIP3" />
													</s:else>
													</td>
													</tr>
													</s:iterator>
												</table>
											</td>
											</tr>
											</s:if>
											</s:iterator>
										</table>
									</fieldset>
								</td>
								</tr>
								<tr id="externalDNSSection">
									<td colspan="3">
										<fieldset>
											<legend>
												<label><s:text name="config.title.dnsService.external.servers"/></label>
											</legend>
											<table width="100%">
												<tr>
													<td class="noteInfo"><s:text name="geneva_10.config.title.dnsService.external.servers.note"/></td>
												</tr>
												<tr>
													<td rowspan="3" colspan="3" width="310">
														<table>
															<s:iterator value="dnsNameItems" id="externalDnsName" status="st">
															<tr>
															<td class="dnsServer"><s:property value="externalDnsName"/>
															<s:if test="#st.First"><font color="red"><s:text name="*"/></font></s:if>
															</td>
															<td valign="top" width="210" style="padding:2px 5px 2px 0px;">
															<s:if test="%{#st.index+1 == 1}">
														    <ah:createOrSelect divId="externalDnsServerIP11" swidth="155px"
																		list="availableDnsServers" typeString="IpAddressEx1"
																		selectIdName="externalDnsServerIP_1" inputValueName="externalDnsServerIP1"/>
															</s:if>
															<s:elseif test="%{#st.index+1 == 2}">
														    <ah:createOrSelect divId="externalDnsServerIP12" swidth="155px"
																		list="availableDnsServers" typeString="IpAddressEx2"
																		selectIdName="externalDnsServerIP_2" inputValueName="externalDnsServerIP2" />
															</s:elseif>
															<s:else>
														    <ah:createOrSelect divId="externalDnsServerIP13" swidth="155px"
																		list="availableDnsServers" typeString="IpAddressEx3"
																		selectIdName="externalDnsServerIP_3" inputValueName="externalDnsServerIP3" />
															</s:else>
															</td>
															</tr>
															</s:iterator>
														</table>
													</td>
												</tr>
											</table>
										</fieldset>
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
</s:form></div>
<div style="display: none">
<table>
		<tr id="specificRow_Template">
			<td class="listCheck"><s:checkbox name="specificDNSChk"/></td>
			<td class="list" style="width: 280px;padding-left: 40px;"><s:textfield size="24" name="specificDomain"
				maxlength="32" cssStyle="width: 200px;"
				onkeypress="return hm.util.keyPressPermit(event,'dnsDomain');" /></td>
			<td class="list" style="width: 280px;padding-left: 40px;">
			<div id="errorDisplay_Template" style="position: relative;">
			<span style="overflow: hidden;">
			<s:select list="availableDnsServers" name="specificDNSSelect" cssStyle="width: 155px; "
				id="specificDNSId_Template"
				listKey="id" listValue="value"
				title="To add: Choose the blank space and type a new entry."
				onchange="hm.util.singObjectSelect(this, 'specificDNSText_Template');"/>
			<input type="text" maxlength="32"
				onkeypress="return hm.util.keyPressPermit(event,'ip');"
				style="position: absolute; width: 126px; z-index: 2; left: 0px;"
				autocomplete="off" name="specificDNS" id="specificDNSText_Template">
				</span>&nbsp;
				<s:if test="%{'disabled' == writeDisabled}">
				<img width="16" height="16" title="New" alt="New" src="images/new_disable.png" class="dinl">
				<img width="16" height="16" title="Modify" alt="Modify" src="images/modify_disable.png" class="dinl">
				</s:if>
				<s:else>
				<a href="javascript:submitAction('newIpAddress')" class="marginBtn"><img
				width="16" height="16" title="New" alt="New" src="images/new.png"
				class="dinl"></a>
				<a href="javascript:submitAction('editIpAddress')"
				class="marginBtn"><img width="16" height="16" title="Modify"
				alt="Modify" src="images/modify.png" class="dinl"></a>
				</s:else>
			</div>
			 </td>
		</tr>
</table>
</div>
<script>
var rowCount = <s:property value="%{specificDNSPair.size}"/>; // sepcific row count
var hintClassName="hint";
var domainNamesHint = '<s:text name="config.title.dnsService.domainNames.hint"/>';
//-------------Common functions-----------------//
YAHOO.util.Event.onDOMReady(function () {
	prepareServiceType(<s:property value="%{dataSource.serviceType}"/>);
	// add a new blank row 
	if(rowCount == 0) {
		addSpecificDNSRows();
	}
	// initial water mark
	showWaterMark(Get(formName+'_domainNames'), domainNamesHint);
	// water mark event
	YAHOO.util.Event.on(Get(formName+'_domainNames'), "focus", focusAction);
	YAHOO.util.Event.on(Get(formName+'_domainNames'), "blur", blurAction);
	// initial external section
	changeExternalDNS('<s:property value="dataSource.externalServerType"/>');
	 <s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(740, 720);
	 	}
	 </s:if>
});

function debugLog(msg) {
	// console.debug(msg);
}

function validate(operation) {
	debugLog("validate operation: " +operation);
	// go thru if cancel
	if('<%=Navigation.L2_FEATURE_DNS_SERVICE%>' == operation 
			|| 'cancel' + '<s:property value="lstForward"/>' == operation) {
		return true;
	}
	// go thru if new DNS server
	if('newIpAddressIn1' == operation || 'newIpAddressIn2' == operation || 'newIpAddressIn3' == operation
			|| 'newIpAddressEx1' == operation || 'newIpAddressEx2' == operation || 'newIpAddressEx3' == operation) {
		return true;
	}
	// edit DNS server
	if('editIpAddressIn1' == operation || 'editIpAddressIn2' == operation || 'editIpAddressIn3' == operation
			|| 'editIpAddressEx1' == operation || 'editIpAddressEx2' == operation || 'editIpAddressEx3' == operation) {
		var ipAddressId = getDNSSeverId(operation);
    	var selectedValue = hm.util.validateListSelection(ipAddressId);
    	if(selectedValue < 0){
			return false;
		}
    	return true;
	}
	// check service name
	var message;
	if(operation == 'create'+'<s:property value="lstForward"/>') {
		var nameEl = Get(formName+'_dataSource_serviceName');
		message = hm.util.validateName(nameEl.value, '<s:text name="config.dnsService.name"/>');
	  	if (null != message) {
    		hm.util.reportFieldError(nameEl, message);
    		nameEl.focus();
        	return false;
    	}
	}
	// check Workspace
	if(Get('DNSSplit_1').checked) {
		// check Domain Names
		var dnEl = Get(formName+'_domainNames');
		if(YAHOO.util.Dom.hasClass(dnEl, hintClassName)) {
    		hm.util.reportFieldError(dnEl, 
    				'<s:text name="error.requiredField"><s:param><s:text name="config.title.dnsService.domainNames"/></s:param></s:text>');
    		dnEl.focus();
        	return false;
		} else {
			if(dnEl.value.length == 0) {
	    		hm.util.reportFieldError(dnEl, 
	    				'<s:text name="error.requiredField"><s:param><s:text name="config.title.dnsService.domainNames"/></s:param></s:text>');
	    		dnEl.focus();
	        	return false;
			}
			
			var lines = dnEl.value.replace(/\r\n/g, "\n").split("\n");
			if(lines.length > 32) {
	    		hm.util.reportFieldError(dnEl, 
	    				'<s:text name="error.config.dnsService.domains.exceed"/>');
	    		dnEl.focus();
	    		return false;
			}
			for(var index=lines.length-1;index>=0;index--){
				if(lines[index] && !validateDomainName(dnEl, lines[index])) {
					return false;
				}
			}
		}
	  	
		// check internal DNS servers
		var ipAddressListEl = Get('internalDNS_1');
		var ipAddressValueEl = Get('dataSource.internalIP1');
		var errorDiv = Get("errorDisplay11");
		var labelName = '<s:text name="config.title.dnsService.dns1" />';
		
		if(!checkDNSServer1(ipAddressListEl, ipAddressValueEl, errorDiv, labelName)) {
			return false;
		}
		
		var ipAddressListE2 = Get('internalDNS_2'); 
		var ipAddressValueEl2 = Get('dataSource.internalIP2');
		errorDiv = Get("errorDisplay12");
		var labelName2 = '<s:text name="config.title.dnsService.dns2" />';
		
		if(!checkDNSServerNotRequired(ipAddressListE2,ipAddressValueEl2,errorDiv,labelName2)){
			return false;
		}
		
		if(!checkSameDNSServer(ipAddressValueEl2, labelName2, ipAddressValueEl, labelName, errorDiv)) {
			return false;
		}
		
		var ipAddressListE3 = Get('internalDNS_3'); 
		var ipAddressValueEl3 = Get('dataSource.internalIP3');
		errorDiv = Get("errorDisplay13");
		var labelName3 = '<s:text name="config.title.dnsService.dns3" />';
		
		if(!checkDNSServerNotRequired(ipAddressListE3,ipAddressValueEl3,errorDiv,labelName3)){
			return false;
		}
		
		if(!checkSameDNSServer(ipAddressValueEl3, labelName3, ipAddressValueEl, labelName, errorDiv)) {
			return false;
		}
		if(!checkSameDNSServer(ipAddressValueEl3, labelName3, ipAddressValueEl2, labelName2, errorDiv)) {
			return false;
		}
		
		// check specific Domain/DNS pair
		var domainArray = document.getElementsByName("specificDomain");
		var dnsArray = document.getElementsByName("specificDNS");
		var dnsSelect = document.getElementsByName("specificDNSSelect");
		var specificDomain, specificDNS,specificDNSSelect;
		// create an object
		var errorRow = new Object();
		errorRow.id='specificErrorRow';
		var domainArrayLen = domainArray.length-1;
		var specificDNSValiedFlag = false;
		for(var index = 0 ; index < domainArrayLen; index++) {
			specificDomain = domainArray[index].value;
			specificDNS = dnsArray[index].value;
			specificDNSSelect = dnsSelect[index].value;
			if(specificDomain.trim() == '' && specificDNS.trim() == '') {
				continue;
			} else if(specificDomain.trim() != '' && specificDNS.trim() != '') {
				if(!validateDomainName(domainArray[index], specificDomain, errorRow)) {
					return false;
				}
				if(specificDNSSelect != -1){
					continue;
				}
				
				for(var optionIndex = 0; optionIndex < dnsSelect[index].options.length;optionIndex ++){
					var optionTest = dnsSelect[index].options[optionIndex].text;
					if(optionTest == specificDNS){
						specificDNSValiedFlag = true;
					}
				}
				
				if(specificDNSValiedFlag){
					continue;
				}
				
				if (!hm.util.validateIpAddress(specificDNS)) {
	       			 hm.util.reportFieldError(errorRow, '<s:text name="error.formatInvalid"><s:param>'+'<s:text name="config.title.dnsService.specific.dnsServer"/>'+'</s:param></s:text>');
	       			 domainArray[index].focus();
                   	 return false;
             	 } 
				continue;
			}else{
				hm.util.reportFieldError(errorRow, '<s:text name="error.config.dnsService.advanced.nomatch"/>');
				domainArray[index].focus();
				return false;
			}
		}
	}
	// check external DNS servers
	if(Get('externalServerType_3').checked) {
		debugLog("prepare to check the external DNS servers");
		
		var ipAddressListEl = Get('externalDNS_1');
		var ipAddressValueEl = Get('dataSource.externalIP1');
		var errorDiv = Get("errorDisplay21");
		var labelName = '<s:text name="config.title.dnsService.dns1" />';
		
		if(!checkDNSServer1(ipAddressListEl, ipAddressValueEl, errorDiv, labelName)) {
			return false;
		}
		
		var ipAddressListE2 = Get('externalDNS_2');
		var ipAddressValueEl2 = Get('dataSource.externalIP2');
        errorDiv = Get("errorDisplay22");
        var labelName2 = '<s:text name="config.title.dnsService.dns2" />';
        
        if(!checkDNSServerNotRequired(ipAddressListE2,ipAddressValueEl2,errorDiv,labelName2)){
			return false;
		}
        
        if(!checkSameDNSServer(ipAddressValueEl2, labelName2, ipAddressValueEl, labelName, errorDiv)) {
            return false;
        }
        
        var ipAddressListE3 = Get('externalDNS_3');
        var ipAddressValueEl3 = Get('dataSource.externalIP3');
        errorDiv = Get("errorDisplay23");
        var labelName3 = '<s:text name="config.title.dnsService.dns3" />';
        
        if(!checkDNSServerNotRequired(ipAddressListE3,ipAddressValueEl3,errorDiv,labelName3)){
			return false;
		}
        
        if(!checkSameDNSServer(ipAddressValueEl3, labelName3, ipAddressValueEl, labelName, errorDiv)) {
            return false;
        }
        if(!checkSameDNSServer(ipAddressValueEl3, labelName3, ipAddressValueEl2, labelName2, errorDiv)) {
            return false;
        }
	}
	
	if(Get('DNSSplit_2').checked){
		var ipAddressListEl = Get('externalDnsServerIP_1');
		var ipAddressValueEl = Get('externalDnsServerIP1');
		var errorDiv = Get("externalDnsServerIP11");
		var labelName = '<s:text name="config.title.dnsService.dns1" />';
		
		if(!checkDNSServer1(ipAddressListEl, ipAddressValueEl, errorDiv, labelName)) {
			return false;
		}
		
		var ipAddressListE2 = Get('externalDnsServerIP_2');
		var ipAddressValueEl2 = Get('externalDnsServerIP2');
        errorDiv = Get("externalDnsServerIP12");
        var labelName2 = '<s:text name="config.title.dnsService.dns2" />';
        
        if(!checkDNSServerNotRequired(ipAddressListE2,ipAddressValueEl2,errorDiv,labelName2)){
			return false;
		}
        
        if(!checkSameDNSServer(ipAddressValueEl2, labelName2, ipAddressValueEl, labelName, errorDiv)) {
            return false;
        }
        
        var ipAddressListE3 = Get('externalDnsServerIP_3');
        var ipAddressValueEl3 = Get('externalDnsServerIP3');
        errorDiv = Get("externalDnsServerIP13");
        var labelName3 = '<s:text name="config.title.dnsService.dns3" />';
        
        if(!checkDNSServerNotRequired(ipAddressListE3,ipAddressValueEl3,errorDiv,labelName3)){
			return false;
		}
        
        if(!checkSameDNSServer(ipAddressValueEl3, labelName3, ipAddressValueEl, labelName, errorDiv)) {
            return false;
        }
        if(!checkSameDNSServer(ipAddressValueEl3, labelName3, ipAddressValueEl2, labelName2, errorDiv)) {
            return false;
        }
	}
	return true;
}
function validateDomainName(domainUI, domainName, errorUI) {
    if(domainName.length > 32) {
        hm.util.reportFieldError(errorUI ? errorUI : domainUI, '<s:text name="error.config.dnsService.domainNames.exceed"/>');
        domainUI.focus();
        return false;
    }
    if(domainName.indexOf('-') == 0 || domainName.indexOf('.') == 0) {
    	hm.util.reportFieldError(errorUI ? errorUI : domainUI, '<s:text name="error.config.dnsService.serviceName.startChar"/>');
        domainUI.focus();
    	return false;
    }
    var msg = hm.util.validateDnsDomainString(domainName, '<s:text name="config.title.dnsService.domainNames"/>');
    if(msg) {
        hm.util.reportFieldError(errorUI ? errorUI : domainUI, msg);
        domainUI.focus();
        return false;
    }
    return true;
}
function checkSameDNSServer(ipAddressValueEl, label, ipAddressValueEl2, label2, errorDiv) {
    if(ipAddressValueEl.value && ipAddressValueEl.vlaue != "" && ipAddressValueEl2 && ipAddressValueEl2.value != "") {
        if(ipAddressValueEl.value == ipAddressValueEl2.value) {
           hm.util.reportFieldError(errorDiv, '<s:text name="error.equal"><s:param>'+label+'</s:param><s:param>'+label2+'</s:param></s:text>');
           ipAddressValueEl.focus();
           return false;
        }
   }
   return true;
}
function checkDNSServer1(ipAddressListEl, ipAddressValueEl, errorDiv, labelName) {
	
	if(ipAddressListEl.value != -1){
		return true;
	}
	
	if ("" == ipAddressValueEl.value || "" == ipAddressValueEl.value.trim()) {
	    hm.util.reportFieldError(errorDiv, '<s:text name="error.config.network.object.input.direct"><s:param>'+labelName+'</s:param></s:text>');
	    ipAddressListEl.focus();
		return false;
	}
	debugLog("ipaddress value: "+ipAddressValueEl.value);
	if (!hm.util.validateIpAddress(ipAddressValueEl.value)) {
		debugLog("ipaddress is invalidate.");
		hm.util.reportFieldError(errorDiv, '<s:text name="error.formatInvalid"><s:param>'+labelName+'</s:param></s:text>');
		return false;
	}
	return true;
}

function checkDNSServerNotRequired(ipAddressListEle, ipAddressValueEle, errorDiv, labelName){
	if(ipAddressListEle.value != -1){
		return true;
	}
	if(ipAddressValueEle.value != "" && ipAddressValueEle.value.trim() != ""){
		if (!hm.util.validateIpAddress(ipAddressValueEle.value)) {
			debugLog("ipaddress is invalidate.");
			hm.util.reportFieldError(errorDiv, '<s:text name="error.formatInvalid"><s:param>'+labelName+'</s:param></s:text>');
			return false;
		}
	}
	return true;
}


function getDNSSeverId(operation) {
	var serviceType = document.getElementsByName("dataSource.serviceType");
	var tempServerType;
	
	for(var i = 0; i < serviceType.length; i ++){
		if(serviceType[i].checked){
			tempServerType = parseInt(serviceType[i].value);
			break;
		}
	}
	
	var ipAddressId = 'externalDNS_1';
	switch(operation) {
		case 'editIpAddressIn1':
			ipAddressId = 'internalDNS_1';
			break;
		case 'editIpAddressIn2':
			ipAddressId = 'internalDNS_2';
			break;
		case 'editIpAddressIn3':
			ipAddressId = 'internalDNS_3';
			break;
		case 'editIpAddressEx1':
			if(tempServerType == EXTERNAL_DNS){
				ipAddressId = "externalDnsServerIP_1";
			}else{
				ipAddressId = 'externalDNS_1';
			}
			break;
		case 'editIpAddressEx2':
			if(tempServerType == EXTERNAL_DNS){
				ipAddressId = "externalDnsServerIP_2";
			}else{
				ipAddressId = 'externalDNS_2';
			}
			break;
		case 'editIpAddressEx3':
			if(tempServerType == EXTERNAL_DNS){
				ipAddressId = "externalDnsServerIP_3";
			}else{
				ipAddressId = 'externalDNS_3';
			}
			break;
	}
	return ipAddressId;
}
/**
 * check if contains charaters in string
 */
function strContains(str, sub){
 return (str.indexOf(sub) != -1);
}
///--------------- Domain Names: KeyPress -----------------///
function keyPressPermitTextarea(event) {
	var keycode;
	if(window.event) {
		 // IE
		keycode = event.keyCode;
	} else if(event.which) {
		// Netscape/Firefox/Opera
		keycode = event.which;
		if (keycode==8) {
			return true;
		}
	} else {
		return true;
	}
	if(keycode == 13) {
		// allow carriage-return
		return true;
	}
	// 'dnsDomain' : permit '0'~'9','A'~'Z','a'~'z' and '-' '.'
	if((48 <= keycode && keycode <=57) || keycode == 45 || (65 <= keycode && keycode <=90)
			|| (97 <= keycode && keycode <=122) || keycode ==46){
		return true;
	}
	return false;
}
///--------------- Domain Names: WaterMark -----------------///
function focusAction(e) {
	hideWaterMark(this.id);
}

function blurAction(e) {
	showWaterMark(this.id, domainNamesHint);
}

function hideWaterMark(elementId) {
	var el = Get(elementId);
	if(el) {
		if(YAHOO.util.Dom.hasClass(el, hintClassName)) {
			YAHOO.util.Dom.removeClass(el, hintClassName);
			el.value = "";
		}
	}
}
function showWaterMark(elementId, text) {
	var el = Get(elementId);
	if(el) {
		var value = el.value;
		if (value.length == 0 || value.trim().length == 0) {
			YAHOO.util.Dom.addClass(el, hintClassName);
			el.value = text;
		}
	}
}
//-------------Advanced section-----------------//
function toggleCheckAll(toggle) {
	var chk = document.getElementsByName('specificDNSChk');
	for (var i = 0; i < chk.length; i++) {
		if (chk[i].checked != toggle.checked) {
			chk[i].checked = toggle.checked;
		}
	}
}
function toggleCheck(toggle) {
	if(!toggle.checked && Get('checkAll').checked) {
		Get('checkAll').checked = false;
	}
}
function addSpecificDNSRows() {
	var lastRow = Get('lastEmptyRow');
	var templateRow = Get('specificRow_Template'); 
	var anotherRow;
	if(lastRow) {
		debugLog("templateRow is "+templateRow.id);
		// check if exist blank line
		if(isExistBlankLine()){
			return;
		}
		
		// create a specific row with javascript
		if(templateRow) {
			anotherRow = templateRow.cloneNode(true);
			var suffix = rowCount++ + 1;
			anotherRow.id = 'specificRow_' + suffix;
			// unchecked the new row
			var elements = anotherRow.getElementsByTagName("input");
			for(var index=0; index<elements.length; index++) {
				debugLog("input " + index+": " + elements[index].id);
				if(elements[index].type == "checkbox") {
					elements[index].checked = false;
				//	break;
				}
				if(elements[index].type == "text") {
					elements[index].value = "";
					//break;
				}
				
			}
			// change the specifiy DNS server UI
			changeSpecificRowDom(anotherRow, suffix);
		}

		if (anotherRow) {
			YAHOO.util.Dom.insertBefore(anotherRow, lastRow);
		}
	}
}
function isExistBlankLine() {
	// check specific Domain/DNS pair
	var domainArray = document.getElementsByName("specificDomain");
	var dnsArray = document.getElementsByName("specificDNS");
	var dnsSelect = document.getElementsByName("specificDNSSelect");
	var specificDomain, specificDNS,specificDNSSelect;
	// create an object
	var errorRow = new Object();
	errorRow.id='specificErrorRow';
	var domainArrayLen = domainArray.length -1;
	var specificDNSValiedFlag = false;
	for(var index = 0 ; index < domainArrayLen; index++) {
		var row = domainArray[index].parentNode.parentNode;
		debugLog("check if exist blank row: id="+row.id);
		if(row && row.id == 'specificRow_Template') {
			// skip the template row
			continue;
		}
		specificDomain = domainArray[index].value;
		specificDNS = dnsArray[index].value;
		specificDNSSelect = dnsSelect[index].value;
		if(specificDomain.trim() == '' && specificDNS.trim() == '') {
			hm.util.reportFieldError(errorRow, '<s:text name="error.config.dnsService.advanced.blank"/>');
			domainArray[index].focus();
			return true;
		}else if(specificDomain.trim() != '' && specificDNS.trim() != '') {
			//when select the exist dns server from select list, there is no need to validate.
			if(specificDNSSelect != -1){
				continue;
			}
			for(var optionIndex = 0; optionIndex < dnsSelect[index].options.length;optionIndex ++){
				var optionTest = dnsSelect[index].options[optionIndex].text;
				if(optionTest == specificDNS){
					specificDNSValiedFlag = true;
				}
			}
			if(specificDNSValiedFlag){
				continue;
			}
			if (!hm.util.validateIpAddress(specificDNS)) {
				hm.util.reportFieldError(errorRow, '<s:text name="error.formatInvalid"><s:param>'+'<s:text name="config.title.dnsService.specific.dnsServer"/>'+'</s:param></s:text>');
				domainArray[index].focus();
				return true;
			}
			continue;
		}else {
			hm.util.reportFieldError(errorRow, '<s:text name="error.config.dnsService.advanced.nomatch"/>');
			domainArray[index].focus();
			return true;
		}
	}
	return false;
}
function removeSpecificRows() {
	var chk = document.getElementsByName('specificDNSChk');
	var isSelected = false; // prompt warnning message if false
	var removedRows = []; // tempatory
	for (var i = 0; i < chk.length; i++) {
		if(chk[i].checked) {
			var row = chk[i].parentNode.parentNode;
			debugLog("row is "+row.id +" row index:"+row.rowIndex);
			if(row && row.id != 'specificRow_Template') {
				isSelected = true;
				//row.parentNode.removeChild(row);
				removedRows.push(row);
				debugLog(">>>row "+row.id+" will be removed");
			}
		}
	}
	if(isSelected) {
		while(removedRows.length > 0){
			var removedRow = removedRows.pop();
			debugLog("removing row: "+removedRow.id);
			removedRow.parentNode.removeChild(removedRow);
		}
	} else {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	}
}

function changeSpecificRowDom(anotherRow, suffix) {
	var elements = anotherRow.getElementsByTagName("div");
	for(var index=0; index<elements.length; index++) {
		debugLog("div " + index+": "+elements[index].id);
		if(elements[index].id == "errorDisplay_Template") {
			elements[index].id = 'errorDisplay_' + suffix;
			debugLog("change the id to " + elements[index].id);
			var subElements = elements[index].getElementsByTagName("select");
			if(subElements.length == 1) {
				subElements[0].id = 'specificDNSId_' + suffix; 
				debugLog("|__change the select id to :" + subElements[0].id);
				debugLog("|__change the select id to :" + subElements[0].onchange);
				subElements[0].onchange = function(){
					debugLog("onchange suffix="+suffix);
					hm.util.singObjectSelect(this, 'specificDNSText_' + suffix);
				};
				debugLog("|__change the select onchange to :" + subElements[0].onchange);
			}
			subElements = elements[index].getElementsByTagName("input");
			if(subElements.length == 1) {
				subElements[0].id = 'specificDNSText_' + suffix; 
				debugLog("|__change the input id to :" + subElements[0].id);
			}
			subElements = elements[index].getElementsByTagName("a");
			if(subElements.length == 2) {
				debugLog("|__find the a tag :" + subElements[0].href);
				if(strContains(subElements[0].href, "newIpAddress")) {
					subElements[0].href = "javascript:jumpSpecificDNS('newSpecificDNS', " + suffix + ")";
					subElements[1].href = "javascript:jumpSpecificDNS('editSpecificDNS', " + suffix +")";
				} else {
					subElements[1].href = "javascript:jumpSpecificDNS('newSpecificDNS', " + suffix +")";
					subElements[0].href = "javascript:jumpSpecificDNS('editSpecificDNS', " + suffix +")";
				}
			}
			break;
		}
	}
}
function jumpSpecificDNS(operation, suffix) {
	var row = Get("specificRow_" + suffix);
	if(row) {
		debugLog("row.id=" + row.id + " row.rowIndex=" + row.rowIndex +" date.index=" + (row.rowIndex -3));
		if(strContains(operation, "editSpecificDNS")) {
	    	var selectedValue = hm.util.validateListSelection("specificDNSId_" + suffix);
	    	if(selectedValue < 0){
	    		Get("specificDNSText_" + suffix).focus();
				return;
			}
			document.forms[formName].tmpDNSServerId.value = selectedValue;
		}
		document.forms[formName].operation.value = operation;
		document.forms[formName].dataIndex.value = row.rowIndex -3;
		beforeSubmitAction(document.forms[formName]);
		document.forms[formName].submit();
	}
}
//-------------Split/Nosplit mode-----------------//
function splitDNSMode () {
	debugLog("split DNS mode");
	displaySplitPage(true);
	
	var fieldSet = Get('externalFieldSetId');
	var legend = Get('externalLegendId');
	var table = Get('externalTableId');
	if(fieldSet && legend && table) {
		hm.util.show('externalFieldSetId');
		if(YAHOO.util.Dom.hasClass(table, 'embeddBox')) {
			YAHOO.util.Dom.removeClass(table, 'embeddBox');
		}
		YAHOO.util.Dom.insertAfter(table, legend);
	}
}
function nosplitDNSMode() {
	debugLog("noSplit DNS mode");
	displaySplitPage(false);
	
	var fieldSet = Get('externalFieldSetId');
	var legend = Get('externalLegendId');
	var table = Get('externalTableId');
	if(fieldSet && legend && table) {
		hm.util.hide('externalFieldSetId');
		if(!YAHOO.util.Dom.hasClass(table, 'embeddBox')) {
			YAHOO.util.Dom.addClass(table, 'embeddBox');
		}
		YAHOO.util.Dom.insertBefore(table, fieldSet);
	}
}

var previousInternalServiceType = <s:property value="%{dataSource.serviceType}"/>;
function prepareServiceType(serviceType){
	Get("DNSSplit_" + serviceType).checked = true;
	var fieldSet = Get('externalFieldSetId');
	var legend = Get('externalLegendId');
	var table = Get('externalTableId');
	if(serviceType == SEPARATE_DNS){
		displaySplitPage(true);
		hm.util.hide('externalDNSSection');
		
		if(fieldSet && legend && table) {
			hm.util.show('externalFieldSetId');
			if(YAHOO.util.Dom.hasClass(table, 'embeddBox')) {
				YAHOO.util.Dom.removeClass(table, 'embeddBox');
			}
			YAHOO.util.Dom.insertAfter(table, legend);
		}
		hm.util.show('externalSection');
		hm.util.show('internalRadioSection');
		Get("DNSSplit_"+INTERNAL_DNS).checked = true;
		previousInternalServiceType = serviceType;
	}
	
	if(serviceType == SAME_DNS){
		displaySplitPage(false);
		hm.util.hide('externalDNSSection');
		if(fieldSet && legend && table) {
			hm.util.hide('externalFieldSetId');
			if(!YAHOO.util.Dom.hasClass(table, 'embeddBox')) {
				YAHOO.util.Dom.addClass(table, 'embeddBox');
			}
			YAHOO.util.Dom.insertBefore(table, fieldSet);
		}
		hm.util.show('externalSection');
		hm.util.show('internalRadioSection');
		Get("DNSSplit_"+INTERNAL_DNS).checked = true;
		previousInternalServiceType = serviceType;
	}
	
	if(serviceType == EXTERNAL_DNS){
		displaySplitPage(false);
		hm.util.hide('externalSection');
		hm.util.show('externalDNSSection');
		hm.util.hide('internalRadioSection');
		Get("DNSSplit_"+INTERNAL_DNS).checked = false;
	}
	
	if(serviceType == INTERNAL_DNS){
		hm.util.show('internalRadioSection');
		Get("DNSSplit_" + EXTERNAL_DNS).checked = false;
		if(previousInternalServiceType == EXTERNAL_DNS){
			prepareServiceType(SEPARATE_DNS);
		} else {
			prepareServiceType(previousInternalServiceType);
		}
	}
}

function displaySplitPage(flag) {
	if(flag) {
		hm.util.show('wordSpaceSection');
	} else {
		hm.util.hide('wordSpaceSection');
	}
}
function changeExternalDNS(type) {
	//console.debug(type+" typeof:"+typeof(type));
	if(type == 3) {
		hm.util.show("externalDNSRow");
		hm.util.hide("hyperLinkRow");
	} else {
		hm.util.hide("externalDNSRow");
		if(type == 2) {
			// FIXME temporary hide OpenDNS
			//hm.util.show("hyperLinkRow");
			hm.util.hide("hyperLinkRow");
		} else {
			hm.util.hide("hyperLinkRow");
		}
	}
}
</script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>