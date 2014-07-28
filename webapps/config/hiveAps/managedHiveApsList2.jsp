<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.be.config.hiveap.UpdateParameters"%>
<%@page import="com.ah.ui.actions.hiveap.HiveApUpdateAction"%>

<%-- <link rel="stylesheet" type="text/css" href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />" /> --%>
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<link type="text/css" rel="stylesheet" href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css" includeParams="none"/>"></link>
<style type="text/css">
<!--
#cliInfoPanel .bd {
	padding: 0;
}

#cliInfoPanel .cli_viewer {
	padding: 10px;
	overflow: auto;
	height: 25em;
	font-family: sans-serif, Arial, Helvetica, Verdana;
	background-color: #fff;
}

#cliInfoPanel .ft {
	height: 15px;
	padding: 0;
}
-->

div.nptype_selection_desc {
	font-family: Helvetica, Arial, sans-serif;
	font-size: 14px;
	text-align: center;
	color: #4F4F4F;
	font-weight: bold;
	margin-top: 25px;
}

</style>

<script>
var formName = 'hiveApList';

var interval = 10;        // seconds
var duration = hm.util.sessionTimeout * 60;   // minutes * 60
var total = duration / interval;  // refresh will stop after 'total' refreshes
var count = 0;
var timeoutId = 0;

var UPDATE_FAILED = <%=UpdateParameters.UPDATE_FAILED%>
var UPDATE_SUCCESSFUL = <%=UpdateParameters.UPDATE_SUCCESSFUL%>
var UPDATE_TIMEOUT = <%=UpdateParameters.UPDATE_TIMEOUT%>
var UPDATE_ABORT = <%=UpdateParameters.UPDATE_ABORT%>
var UPDATE_CANCELED = <%=UpdateParameters.UPDATE_CANCELED%>
var UPDATE_STAGED = <%=UpdateParameters.UPDATE_STAGED%>
var ACTION_REBOOT = <%=UpdateParameters.ACTION_REBOOT%>
var REBOOTING = <%=UpdateParameters.REBOOTING%>
var WARNING = <%=UpdateParameters.WARNING%>
var REBOOT_SUCCESSFUL = <%=UpdateParameters.REBOOT_SUCCESSFUL%>
var deviceTypes = <s:property value="deviceTypesJsonString" escapeHtml="false" />;
var Device_TYPE_SWITCH = <%=HiveAp.Device_TYPE_SWITCH%>;
var REBOOT_TYPE_AUTO = <%=HiveApUpdateAction.REBOOT_TYPE_AUTO%>;
var REBOOT_TYPE_MANUAL = <%=HiveApUpdateAction.REBOOT_TYPE_MANUAL%>;

var allDevice = false;
var allAp = false;
var allBr = false;
var allSwitch = false;
var allL2Vpn = false;
var allL3Vpn = false;

function isAnySwitchSelected(selectedId){
	if(selectedId == null){
		return false;
	}
	for(var i=0; i<selectedId.length; i++){
		var id = selectedId[i];
		var type = deviceTypes["_" + id];
		if(type == Device_TYPE_SWITCH){
			return true;
		}
	}
	return false;
}

function insertPageContext() {
	// nothing
}

function onLoadPage(){
	setTimeout("hideNotes()", 2000);
	hideEditTable();
	setTimeout("hideErrorNode()", 2000);
	hm.util.registerRowClickedEvents();
	refreshGuidList(true);
	createTooltip();

	<s:if test="%{doneBtnDisplayFlag}">
	showGotoIDMButton();
	</s:if>

	$("#allItemsSelectedVarClear").click(function(){
		clearAllSelectedDeviceGroup();
	});
}

function hideEditTable(){
	var editTable = document.getElementById("editTable");
	if(editTable != null){
		document.getElementById("editTable").style.display = "none";
	}
}

function hideErrorNode(){
	var errorNode = document.getElementById("note");
	if (errorNode != null){
		document.getElementById("note").style.display = "none";
	}
}

function submitAction(operation){
	submitHiveApAction(operation);
}

function toggleHiveApTR() {
	if( $('tr.aplist_body').is(':hidden') ) {
		$("tr.aplist_body").show();
	} else {
		$("tr.aplist_body").hide();
	}

}
function toggleRouterTR() {
	if( $('tr.brlist_body').is(':hidden') ) {
		$("tr.brlist_body").show();
	} else {
		$("tr.brlist_body").hide();
	}
}
function toggleSwitchTR() {
	if( $('tr.swlist_body').is(':hidden') ) {
		$("tr.swlist_body").show();
	} else {
		$("tr.swlist_body").hide();
	}
}

function toggleL2GatewayTR() {
	if( $('tr.l2Cvglist_body').is(':hidden') ) {
		$("tr.l2Cvglist_body").show();
	} else {
		$("tr.l2Cvglist_body").hide();
	}

}
function toggleL3GatewayTR() {
	if( $('tr.l3Cvglist_body').is(':hidden') ) {
		$("tr.l3Cvglist_body").show();
	} else {
		$("tr.l3Cvglist_body").hide();
	}

}

function idmDoneClick(){
	var successDoneButton= function(o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Redirect to ID Manager error.", "Error");
			return false;
		}

		if(data.t) {
			var url = data.url + data.params + encodeURIComponent(data.paramsValue);
			window.location.href = url;
		} else {
			hm.util.displayJsonErrorNote(data.m);
			return false;
		}
	}
	var url = "hiveApUpdate.action?operation=doneToIDM&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : successDoneButton, timeout: 60000});
}
</script>

<div id="content"><s:form action="hiveAp" id="hiveApList" name="hiveApList">
	<s:hidden name="cacheId" />
	<s:hidden name="selectedNWPolicy" />
	<s:hidden name="hiveApId" />

	<s:hidden name="selectedDeviceIdStr" id="selectedDeviceIdStr_ID" />

	<s:hidden name="selectedAllStr" id="selectedAllStr_ID" />
	<s:hidden name="selectedAllApStr" id="selectedAllApStr_ID" />
	<s:hidden name="selectedAllBrStr" id="selectedAllBrStr_ID" />
	<s:hidden name="selectedAllSwitchStr" id="selectedAllSwitchStr_ID" />
	<s:hidden name="selectedAllL3VPNStr" id="selectedAllL3VPNStr_ID" />
	<s:hidden name="selectedAllL2VPNStr" id="selectedAllL2VPNStr_ID" />
	<!-- Simplly update param  -->
	<s:hidden name="simpleUpdate" id="simpleUpdate"/>
	<s:hidden name="completeCfgUpdate" id="completeCfgUpdate"/>
	<s:hidden name="imageUpgrade" id="imageUpgrade"/>
	<s:hidden name="forceImageUpgrade" id="forceImageUpgrade"/>
	<s:hidden name="simplifiedRebootType" id="simplifiedRebootType"/>
	<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding-left: 0px">
			<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="20"></td>
				</tr>
				<tr>
					<td>
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><s:text name="config.guid.hiveAp.list.introduction1" /><br>
												<s:text name="config.guid.hiveAp.list.introduction2" />
												<a href="<s:url value="configurationMenu.action"><s:param name="operation" value="%{'configHiveAps'}"/></s:url>">
												<s:text name="config.guid.hiveAp.list.introduction3" /></a>.&nbsp;
												<br/>
												<s:text name="config.guid.hiveAp.list.introduction4" />&nbsp;
												<a href="<s:url value="configurationMenu.action"><s:param name="operation" value="%{'hiveApUpdateRts'}"/></s:url>">
												<s:text name="config.guid.hiveAp.list.introduction5" /></a>.
											</td>
										</tr>
									</table>
								</td>
								<td align="right" valign="top">
									<table width="110px" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="20px"></td>
											<td class="npcButton" width="90px" align="right">
												<s:if test="%{writeDisabled == 'disabled'}">
													<a href="javascript:void(0);" class="btCurrent"  title="Settings" <s:property value="writeDisabled" />><span>Settings</span></a>
												</s:if>
												<s:else>
													<a href="javascript:void(0);" class="btCurrent" onclick='prepareSelectedDeviceIdStr();openUpdateOptionPanel("Device Upload Options", "updateOptionJson");'  title="Settings" <s:property value="writeDisabled" />><span>Settings</span></a>
												</s:else>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="20"></td>
				</tr>
				<tr>
					<td>
					<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td align="left" >
								<table width="550" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="190px" nowrap="nowrap" class="npcHead1" style="padding-left: 2px">
											<s:text name="config.guid.hiveAp.list.deviceUpdate" />
										</td>
										<!--
										<td class="npcButton" width="70px">
											<a href="javascript:void(0);" class="btCurrent" onclick="newHiveApAction();" title="Add" <s:property value="writeDisabled" />><span>Add</span></a>
										</td>
										<td class="npcButton" width="80px">
											<a href="javascript:void(0);" class="btCurrent" onclick="submitHiveApAction('remove');" title="Remove" <s:property value="writeDisabled" />><span>Remove</span></a>
										</td>
										 -->
										 <s:if test="%{userSourceFromIdm}">
											 <td class="npcButton" width="80px">
												<s:if test="%{writeDisabled == 'disabled'}">
													<a href="javascript:void(0);" class="btCurrent" title="Upload" <s:property value="writeDisabled" />><span>Upload</span></a>
												</s:if>
												<s:else>
													<a href="javascript:void(0);" class="btCurrent" id="updateMenu" onclick="updateButtonClick();" title="Upload" <s:property value="writeDisabled" />><span>Upload</span></a>
												</s:else>
											</td>
											<td class="npcButton" width="80px">
												<s:if test="%{writeDisabled == 'disabled'}">
													<a href="javascript:void(0);" class="btCurrent" title="Reboot" <s:property value="writeDisabled" />><span>Reboot</span></a>
												</s:if>
												<s:else>
													<a href="javascript:void(0);" class="btCurrent" onclick="prepareSelectedDeviceIdStr(); rebootHiveAps();" title="Reboot" <s:property value="writeDisabled" />><span>Reboot</span></a>
												</s:else>
											</td>
										 </s:if>
										 <s:else>
											 <td class="npcButton" width="70px">
												<s:if test="%{writeDisabled == 'disabled'}">
													<a href="javascript:void(0);" class="btCurrent" title="Update" <s:property value="writeDisabled" />><span>Update</span></a>
												</s:if>
												<s:else>
													<a href="javascript:void(0);" class="btCurrent" id="updateMenu" onclick="updateButtonClick();" title="Update" <s:property value="writeDisabled" />><span>Update</span></a>
												</s:else>
											</td>
											<td width="10px"/>
											<td class="npcButton" width="70px">
												<s:if test="%{writeDisabled == 'disabled'}">
													<a href="javascript:void(0);" class="btCurrent" title="Modify" <s:property value="writeDisabled" />><span>Modify</span></a>
												</s:if>
												<s:else>
													<a href="javascript:void(0);" class="btCurrent" onclick="prepareSelectedDeviceIdStr(); submitHiveApAction('multiEdit');" title="Modify" <s:property value="writeDisabled" />><span>Modify</span></a>
												</s:else>
											</td>
											<td width="10px"/>
											<td class="npcButton" width="72px">
												<s:if test="%{writeDisabled == 'disabled'}">
													<a href="javascript:void(0);" class="btCurrent" title="Reboot" <s:property value="writeDisabled" />><span>Reboot</span></a>
												</s:if>
												<s:else>
													<a href="javascript:void(0);" class="btCurrent" onclick="prepareSelectedDeviceIdStr(); rebootHiveAps();" title="Reboot" <s:property value="writeDisabled" />><span>Reboot</span></a>
												</s:else>
											</td>
											<td width="10px"/>
											<td class="npcButton" width="70px">
												<s:if test="%{writeDisabled == 'disabled'}">
													<a href="javascript:void(0);" class="btCurrent" title="Tools" <s:property value="writeDisabled" />><span>Tools...</span></a>
												</s:if>
												<s:else>
													<a href="javascript:void(0);" class="btCurrent" title="Tools" id="toolsMenu" onclick="toolsClick();" <s:property value="writeDisabled" />><span>Tools...</span></a>
												</s:else>
											</td>
										</s:else>
										<td>
										</td>
									</tr>
								</table>
							</td>
							<td align="right">
								<table width="270" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="filterH1" width="20px" align="right"><font color="#C84B00">Filter </font></td>
										<td align="right">
											<s:select name="filter" headerKey="-1" headerValue="None" id="filterSelect"
												list="filterList" cssStyle="width:160px;" onchange="filterChanged(this.value, 1);"/>
											<a class="marginBtn" href="javascript:openFilterOverlay();">
												<img class="dinl" src="<s:url value="/images/new.png" />"
												width="16" height="16" alt="New" title="New" /></a>
											<a class="marginBtn" href="javascript:editFilterOverlay();">
												<img class="dinl" src="<s:url value="/images/modify.png" />"
												width="16" height="16" alt="Modify" title="Modify" /></a>
											<a class="marginBtn" href="javascript:submitFilterAction('removeFilter');">
												<img class="dinl" src="<s:url value="/images/cancel.png" />"
												width="16" height="16" alt="Remove" title="Remove" /></a>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					</div>
					</td>
				</tr>
				<tr>
					<td><tiles:insertDefinition name="notes" /></td>
				</tr>
				<tr id="note" style="display:none">
					<td style="padding: 4px 0 0 4px">
					<div>
					<table border="0" cellspacing="0" cellpadding="0" width="500"
						class="note">
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td class="noteError" id="noteText">&nbsp;</td>
						</tr>
						<tr>
							<td height="6"></td>
						</tr>
					</table>
					</div>
					</td>
				</tr>
				<tr><td><table><tr><td><div id="errorNoteForList"></div></td></tr></table></td></tr>
				<tr>
					<td>
						<table id="hiveApListTableDraw" cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
							<tr class="jsonView">
								<th class="check">
									<input type="checkbox" id="checkAll"
										onClick="allDeviceChecked(this);">
								</th>
								<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 5}">
										<th width="30px">
											<ah:sortJson name="connectStatus" key="guided.configuration.hiveap.column.online" onclick="submitHiveApUrl" />
										</th>
									</s:if>
									<s:elseif test="%{columnId == 1}">
										<th style="padding-left: <s:property value="%{hostnamIndentInDraw}" />px">
											<ah:sortJson name="hostName" key="guided.configuration.hiveap.column.hostName" onclick="submitHiveApUrl" />
										</th>
									</s:elseif>
									<s:elseif test="%{columnId == 4}">
										<th>
											<ah:sortJson name="macAddress" key="guided.configuration.hiveap.column.macaddress" onclick="submitHiveApUrl" />
										</th>
									</s:elseif>
									<s:elseif test="%{columnId == 3}">
										<th>
											<ah:sortJson name="ipAddress" key="hiveAp.interface.ipAddress" onclick="submitHiveApUrl" />
										</th>
									</s:elseif>
									<s:elseif test="%{columnId == 13}">
										<th>
											<ah:sortJson name="configTemplate.configName" key="hiveAp.template" onclick="submitHiveApUrl" />
										</th>
									</s:elseif>
									<s:elseif test="%{columnId == 10}">
										<th>
											<ah:sortJson name="softver" key="guided.configuration.hiveap.column.version" onclick="submitHiveApUrl" />
										</th>
									</s:elseif>
									<s:elseif test="%{columnId == 28}">
										<th>
											<ah:sortJson name="pendingIndex" key="guided.configuration.hiveap.column.Updated" onclick="submitHiveApUrl" />
										</th>
									</s:elseif>
									<s:elseif test="%{columnId == 39}">
										<th>
											<s:text name="guided.configuration.hiveap.column.uploadStatus" />
										</th>
									</s:elseif>
								</s:iterator>
								<th width="30px" style="padding-right: 1px;"><div align="right" style="padding-right: 10px;"><a href="javascript: preShowColumnsPanel();">
								<img src="<s:url value="/images/edit_table/EditTable-bold.png" />"
									onMouseOver="this.src='<s:url value="/images/edit_table/EditTable-fade.png" />'"
									onMouseOut="this.src='<s:url value="/images/edit_table/EditTable-bold.png" />'"
									title="Edit Table" width="24" height="24" border="0px" class="dblk">
								</a></div></th>
							</tr>
							<s:if test="%{page.size() == 0}">
								<ah:emptyList />
							</s:if>
							<tiles:insertDefinition name="selectAll" />


							<s:if test="%{hiveApList.size > 0}">
							<tr>
								<td colspan="100">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
												<input id="apGroup" type="checkbox" name="deviceGroup" onclick="checkDeviceGroup(this, 'hiveAp')"/>
											</td>
											<td onclick="toggleHiveApTR();" class="cursorPointer" width="100%">
												<span class="lstHead2"><s:text name="hm.config.guide.hiveAp.aps.title"/></span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="pageSelected" id="apPageItemsSelectedRow" style="display:none">
								<td colspan="100" class="list" align="center">All <b><s:property
									value="%{hiveApList.size}" /></b> items on this page are selected.&nbsp;&nbsp;<a
									href="javascript: setGroupItemsSelected('hiveAp');">Select all <b><s:property
									value="%{selectedAllApCounts}" /></b> items.</a>
								</td>
							</tr>
							<tr class="allSelected" id="apAllItemsSelectedRow" style="display:none">
								<td colspan="100" class="list" align="center">All <b><s:property
									value="%{selectedAllApCounts}" /></b> items are selected.&nbsp;&nbsp;<a
									href="javascript: clearGroupSelection('hiveAp');" id="allItemsSelectedVarClear">Clear selection.</a></td>
							</tr>
							<s:iterator value="hiveApList" status="status" id="hiveApItem">
								<tiles:insertDefinition name="rowClass" />
								<tr id="r<s:property value="id" />" class="<s:property value="%{#rowClass}"/> aplist_body">
									<s:if
										test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
										<td class="listCheck" style="padding-left:10px">
											<input type="checkbox" disabled="disabled" />
										</td>
									</s:if>
									<s:else>
										<td class="listCheck" style="padding-left:10px">
											<ah:checkItem tag="hiveAp" />
										</td>
									</s:else>
									<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 5}">
										<td nowrap="nowrap" class="list" id="d5"
											style="<s:if test="%{allDisconnected}">text-align: center; </s:if>vertical-align: center; padding-top: 3px; padding-bottom: 0px;">
											<s:property value="connectionIcon" escape="false" />
										</td>
									</s:if>
									<s:elseif test="%{columnId == 1}">
										<td class="list" id="d1">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<s:if test="%{iconMaxCount > 0}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem1" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 1}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem2" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 2}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem3" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 3}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem4" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 4}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem5" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 5}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem6" escape="false" />
														</td>
													</s:if>

													<s:if test="%{showDomain && #hiveApItem.owner.domainName!='home' && #hiveApItem.owner.domainName!='global'}">
														<td class="textLink">
															<s:property value="dtlsIcon" escape="false" />
				       											<span title='<s:property value="hostName" />'><s:property value="hostNameSubstr" /></span>
														</td>
													</s:if>
													<s:else>
														<td class="textLink">
															<s:property value="dtlsIcon" escape="false" />
															<a href="javascript:void(0);" class="npcLinkA"
																onclick = 'editHiveApAction("<s:property value="%{#hiveApItem.id}" />");'>
															<span title='<s:property value="hostName" />'><s:property value="hostNameSubstr" /></span>
															</a>
														</td>
													</s:else>
												</tr>
											</table>
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 4}">
										<td class="list" nowrap="nowrap" id="d4">
											<s:property value="macAddressFormat" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 3}">
										<td class="list" nowrap="nowrap" id="d3">
											<s:property value="ipAddress" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 13}">
										<td class="list" nowrap="nowrap" id="d13">
											<span title='<s:property value="configTemplate.configName" />'><s:property value="configTemplate.configNameSubstr" /></span>
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 10}">
										<td class="list" nowrap="nowrap" id="d10">
											<s:property value="softVerString" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 28}">
										<td width="22px" class="list" style="text-align: center; padding-left: 10px;" id="d28">
											<s:property value="configIndicationIcon" escape="false" />
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 39}">
										<td class="list" id="d39">
											<s:property value="uploadStatus" />&nbsp;
										</td>
									</s:elseif>
									</s:iterator>
									<td width="20" class="list">&nbsp;</td>
								</tr>
							</s:iterator>
							</s:if>



							<s:if test="%{routerList.size > 0}">
							<tr>
								<td colspan="100">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
												<input id="routeGroup" type="checkbox" name="deviceGroup" onclick="checkDeviceGroup(this, 'router')"/>
											</td>
											<td onclick="toggleRouterTR();" class="cursorPointer" width="100%">
												<span class="lstHead2"><s:text name="config.guid.hiveAp.list.branchRouters"/></span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="pageSelected" id="brPageItemsSelectedRow" style="display:none">
								<td colspan="100" class="list" align="center">All <b><s:property
									value="%{routerList.size}" /></b> items on this page are selected.&nbsp;&nbsp;<a
									href="javascript: setGroupItemsSelected('router');">Select all <b><s:property
									value="%{selectedAllBrCounts}" /></b> items.</a>
								</td>
							</tr>
							<tr class="allSelected" id="brAllItemsSelectedRow" style="display:none">
								<td colspan="100" class="list" align="center">All <b><s:property
									value="%{selectedAllBrCounts}" /></b> items are selected.&nbsp;&nbsp;<a
									href="javascript: clearGroupSelection('router');">Clear selection.</a></td>
							</tr>
							<s:iterator value="routerList" status="status" id="hiveApItem">
								<tiles:insertDefinition name="rowClass" />
								<tr id="r<s:property value="id" />" class="<s:property value="%{#rowClass}"/> brlist_body">
									<s:if
										test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
										<td class="listCheck" style="padding-left:10px">
											<input type="checkbox" disabled="disabled" />
										</td>
									</s:if>
									<s:else>
										<td class="listCheck" style="padding-left:10px">
											<ah:checkItem tag="router" />
										</td>
									</s:else>
									<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 5}">
										<td nowrap="nowrap" class="list" id="d5"
											style="<s:if test="%{allDisconnected}">text-align: center; </s:if>vertical-align: center; padding-top: 3px; padding-bottom: 0px;">
											<s:property value="connectionIcon" escape="false" />
										</td>
									</s:if>
									<s:elseif test="%{columnId == 1}">
										<td class="list" id="d1">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<s:if test="%{iconMaxCount > 0}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem1" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 1}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem2" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 2}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem3" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 3}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem4" escape="false" />
														</td>
													</s:if>

													<s:if test="%{showDomain && #hiveApItem.owner.domainName!='home' && #hiveApItem.owner.domainName!='global'}">
														<td class="textLink">
															<s:property value="dtlsIcon" escape="false" />
				       											<span title='<s:property value="hostName" />'><s:property value="hostNameSubstr" /></span>
														</td>
													</s:if>
													<s:else>
														<td class="textLink">
															<s:property value="dtlsIcon" escape="false" />
															<a href="javascript:void(0);" class="npcLinkA"
																onclick = 'editHiveApAction("<s:property value="%{#hiveApItem.id}" />");'>
															<span title='<s:property value="hostName" />'><s:property value="hostNameSubstr" /></span>
															</a>
														</td>
													</s:else>
												</tr>
											</table>
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 4}">
										<td class="list" nowrap="nowrap" id="d4">
											<s:property value="macAddressFormat" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 3}">
										<td class="list" nowrap="nowrap" id="d3">
											<s:property value="ipAddress" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 13}">
										<td class="list" nowrap="nowrap" id="d13">
											<span title='<s:property value="configTemplate.configName" />'><s:property value="configTemplate.configNameSubstr" /></span>
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 10}">
										<td class="list" nowrap="nowrap" id="d10">
											<s:property value="softVerString" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 28}">
										<td width="22px" class="list" style="text-align: center; padding-left: 10px;" id="d28">
											<s:property value="configIndicationIcon" escape="false" />
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 39}">
										<td class="list" id="d39">
											<s:property value="uploadStatus" />&nbsp;
										</td>
									</s:elseif>
									</s:iterator>
									<td width="20" class="list">&nbsp;</td>
								</tr>
							</s:iterator>
							</s:if>

							<!-- SWITCH  -->
							<s:if test="%{switchList.size > 0}">
							<tr>
								<td colspan="100">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
												<input id="switchGroup" type="checkbox" name="deviceGroup" onclick="checkDeviceGroup(this, 'switch')"/>
											</td>
											<td onclick="toggleSwitchTR();" class="cursorPointer" width="100%">
												<span class="lstHead2"><s:text name="config.guid.hiveAp.list.switchs"/></span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="pageSelected" id="switchPageItemsSelectedRow" style="display:none">
								<td colspan="100" class="list" align="center">All <b><s:property
									value="%{switchList.size}" /></b> items on this page are selected.&nbsp;&nbsp;<a
									href="javascript: setGroupItemsSelected('switch');">Select all <b><s:property
									value="%{selectedAllSwitchCounts}" /></b> items.</a>
								</td>
							</tr>
							<tr class="allSelected" id="switchAllItemsSelectedRow" style="display:none">
								<td colspan="100" class="list" align="center">All <b><s:property
									value="%{selectedAllSwitchCounts}" /></b> items are selected.&nbsp;&nbsp;<a
									href="javascript: clearGroupSelection('switch');">Clear selection.</a></td>
							</tr>
							<s:iterator value="switchList" status="status" id="hiveApItem">
								<tiles:insertDefinition name="rowClass" />
								<tr id="r<s:property value="id" />" class="<s:property value="%{#rowClass}"/> swlist_body">
									<s:if
										test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
										<td class="listCheck" style="padding-left:10px">
											<input type="checkbox" disabled="disabled" />
										</td>
									</s:if>
									<s:else>
										<td class="listCheck" style="padding-left:10px">
											<ah:checkItem tag="switch" />
										</td>
									</s:else>
									<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 5}">
										<td nowrap="nowrap" class="list" id="d5"
											style="<s:if test="%{allDisconnected}">text-align: center; </s:if>vertical-align: center; padding-top: 3px; padding-bottom: 0px;">
											<s:property value="connectionIcon" escape="false" />
										</td>
									</s:if>
									<s:elseif test="%{columnId == 1}">
										<td class="list" id="d1">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<s:if test="%{iconMaxCount > 0}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem1" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 1}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem2" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 2}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem3" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 3}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem4" escape="false" />
														</td>
													</s:if>

													<s:if test="%{showDomain && #hiveApItem.owner.domainName!='home' && #hiveApItem.owner.domainName!='global'}">
														<td class="textLink">
															<s:property value="dtlsIcon" escape="false" />
				       											<span title='<s:property value="hostName" />'><s:property value="hostNameSubstr" /></span>
														</td>
													</s:if>
													<s:else>
														<td class="textLink">
															<s:property value="dtlsIcon" escape="false" />
															<a href="javascript:void(0);" class="npcLinkA"
																onclick = 'editHiveApAction("<s:property value="%{#hiveApItem.id}" />");'>
															<span title='<s:property value="hostName" />'><s:property value="hostNameSubstr" /></span>
															</a>
														</td>
													</s:else>
												</tr>
											</table>
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 4}">
										<td class="list" nowrap="nowrap" id="d4">
											<s:property value="macAddressFormat" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 3}">
										<td class="list" nowrap="nowrap" id="d3">
											<s:property value="ipAddress" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 13}">
										<td class="list" nowrap="nowrap" id="d13">
											<span title='<s:property value="configTemplate.configName" />'><s:property value="configTemplate.configNameSubstr" /></span>
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 10}">
										<td class="list" nowrap="nowrap" id="d10">
											<s:property value="softVerString" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 28}">
										<td width="22px" class="list" style="text-align: center; padding-left: 10px;" id="d28">
											<s:property value="configIndicationIcon" escape="false" />
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 39}">
										<td class="list" id="d39">
											<s:property value="uploadStatus" />&nbsp;
										</td>
									</s:elseif>
									</s:iterator>
									<td width="20" class="list">&nbsp;</td>
								</tr>
							</s:iterator>
							</s:if>

							<!-- L2 VPN Gateway  -->
							<s:if test="%{l2_vpnGatewayList.size > 0}">
							<tr>
								<td colspan="100">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
												<input id="l2_vpnGroup" type="checkbox" name="deviceGroup" onclick="checkDeviceGroup(this, 'l2_vpnGateway')"/>
											</td>
											<td onclick="toggleL2GatewayTR();" class="cursorPointer" width="100%">
												<span class="lstHead2"><s:text name="config.vpn.gateway.settings.gateways.l2" /></span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="pageSelected" id="l2vpnPageItemsSelectedRow" style="display:none">
								<td colspan="100" class="list" align="center">All <b><s:property
									value="%{l2_vpnGatewayList.size}" /></b> items on this page are selected.&nbsp;&nbsp;<a
									href="javascript: setGroupItemsSelected('l2_vpnGateway');">Select all <b><s:property
									value="%{selectedAllL2VPNCounts}" /></b> items.</a>
								</td>
							</tr>
							<tr class="allSelected" id="l2vpnAllItemsSelectedRow" style="display:none">
								<td colspan="100" class="list" align="center">All <b><s:property
									value="%{selectedAllL2VPNCounts}" /></b> items are selected.&nbsp;&nbsp;<a
									href="javascript: clearGroupSelection('l2_vpnGateway');">Clear selection.</a></td>
							</tr>
							<s:iterator value="l2_vpnGatewayList" status="status" id="hiveApItem">
								<tiles:insertDefinition name="rowClass" />
								<tr id="r<s:property value="id" />" class="<s:property value="%{#rowClass}"/> l2Cvglist_body">
									<s:if
										test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
										<td class="listCheck" style="padding-left:10px">
											<input type="checkbox" disabled="disabled" />
										</td>
									</s:if>
									<s:else>
										<td class="listCheck" style="padding-left:10px">
											<ah:checkItem tag="l2_vpnGateway" />
										</td>
									</s:else>
									<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 5}">
										<td nowrap="nowrap" class="list" id="d5"
											style="<s:if test="%{allDisconnected}">text-align: center; </s:if>vertical-align: center; padding-top: 3px; padding-bottom: 0px;">
											<s:property value="connectionIcon" escape="false" />
										</td>
									</s:if>
									<s:elseif test="%{columnId == 1}">
										<td class="list" id="d1">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<s:if test="%{iconMaxCount > 0}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem1" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 1}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem2" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 2}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem3" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 3}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem4" escape="false" />
														</td>
													</s:if>

													<s:if test="%{showDomain && #hiveApItem.owner.domainName!='home' && #hiveApItem.owner.domainName!='global'}">
														<td class="textLink">
															<s:property value="dtlsIcon" escape="false" />
				       											<span title='<s:property value="hostName" />'><s:property value="hostNameSubstr" /></span>
														</td>
													</s:if>
													<s:else>
														<td class="textLink">
														<s:property value="dtlsIcon" escape="false" />
															<a href="javascript:void(0);" class="npcLinkA"
																onclick = 'editHiveApAction("<s:property value="%{#hiveApItem.id}" />");'>
															<span title='<s:property value="hostName" />'><s:property value="hostNameSubstr" /></span>
															</a>
														</td>
													</s:else>
												</tr>
											</table>
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 4}">
										<td class="list" nowrap="nowrap" id="d4">
											<s:property value="macAddressFormat" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 3}">
										<td class="list" nowrap="nowrap" id="d3">
											<s:property value="ipAddress" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 13}">
										<td class="list" nowrap="nowrap" id="d13">
											N/A &nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 10}">
										<td class="list" nowrap="nowrap" id="d10">
											<s:property value="softVerString" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 28}">
										<td width="22px" class="list" style="text-align: center; padding-left: 10px;" id="d28">
											<s:property value="configIndicationIcon" escape="false" />
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 39}">
										<td class="list" id="d39">
											<s:property value="uploadStatus" />&nbsp;
										</td>
									</s:elseif>
									</s:iterator>
									<td width="20" class="list">&nbsp;</td>
								</tr>
							</s:iterator>
							</s:if>



							<!-- L3 VPN Gateway  -->
							<s:if test="%{l3_vpnGatewayList.size > 0}">
							<tr>
								<td colspan="100">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
												<input id="l3_vpnGroup" type="checkbox" name="deviceGroup" onclick="checkDeviceGroup(this, 'l3_vpnGateway')"/>
											</td>
											<td onclick="toggleL3GatewayTR();" class="cursorPointer" width="100%">
												<span class="lstHead2"><s:text name="config.vpn.gateway.settings.gateways.l3" /></span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="pageSelected" id="l3vpnPageItemsSelectedRow" style="display:none">
								<td colspan="100" class="list" align="center">All <b><s:property
									value="%{l3_vpnGatewayList.size}" /></b> items on this page are selected.&nbsp;&nbsp;<a
									href="javascript: setGroupItemsSelected('l3_vpnGateway');">Select all <b><s:property
									value="%{selectedAllL3VPNCounts}" /></b> items.</a>
								</td>
							</tr>
							<tr class="allSelected" id="l3vpnAllItemsSelectedRow" style="display:none">
								<td colspan="100" class="list" align="center">All <b><s:property
									value="%{selectedAllL3VPNCounts}" /></b> items are selected.&nbsp;&nbsp;<a
									href="javascript: clearGroupSelection('l3_vpnGateway');">Clear selection.</a></td>
							</tr>
							<s:iterator value="l3_vpnGatewayList" status="status" id="hiveApItem">
								<tiles:insertDefinition name="rowClass" />
								<tr id="r<s:property value="id" />" class="<s:property value="%{#rowClass}"/> l3Cvglist_body">
									<s:if
										test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
										<td class="listCheck" style="padding-left:10px">
											<input type="checkbox" disabled="disabled" />
										</td>
									</s:if>
									<s:else>
										<td class="listCheck" style="padding-left:10px">
											<ah:checkItem tag="l3_vpnGateway" />
										</td>
									</s:else>
									<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 5}">
										<td nowrap="nowrap" class="list" id="d5"
											style="<s:if test="%{allDisconnected}">text-align: center; </s:if>vertical-align: center; padding-top: 3px; padding-bottom: 0px;">
											<s:property value="connectionIcon" escape="false" />
										</td>
									</s:if>
									<s:elseif test="%{columnId == 1}">
										<td class="list" id="d1">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<s:if test="%{iconMaxCount > 0}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem1" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 1}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem2" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 2}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem3" escape="false" />
														</td>
													</s:if>
													<s:if test="%{iconMaxCount > 3}">
														<td style="padding-right: 3px;">
															<s:property value="iconItem4" escape="false" />
														</td>
													</s:if>

													<s:if test="%{showDomain && #hiveApItem.owner.domainName!='home' && #hiveApItem.owner.domainName!='global'}">
														<td class="textLink">
															<s:property value="dtlsIcon" escape="false" />
				       											<span title='<s:property value="hostName" />'><s:property value="hostNameSubstr" /></span>
														</td>
													</s:if>
													<s:else>
														<td class="textLink">
														<s:property value="dtlsIcon" escape="false" />
															<a href="javascript:void(0);" class="npcLinkA"
																onclick = 'editHiveApAction("<s:property value="%{#hiveApItem.id}" />");'>
															<span title='<s:property value="hostName" />'><s:property value="hostNameSubstr" /></span>
															</a>
														</td>
													</s:else>
												</tr>
											</table>
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 4}">
										<td class="list" nowrap="nowrap" id="d4">
											<s:property value="macAddressFormat" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 3}">
										<td class="list" nowrap="nowrap" id="d3">
											<s:property value="ipAddress" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 13}">
										<td class="list" nowrap="nowrap" id="d13">
											N/A &nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 10}">
										<td class="list" nowrap="nowrap" id="d10">
											<s:property value="softVerString" />
											&nbsp;
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 28}">
										<td width="22px" class="list" style="text-align: center; padding-left: 10px;" id="d28">
											<s:property value="configIndicationIcon" escape="false" />
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 39}">
										<td class="list" id="d39">
											<s:property value="uploadStatus" />&nbsp;
										</td>
									</s:elseif>
									</s:iterator>
									<td width="20" class="list">&nbsp;</td>
								</tr>
							</s:iterator>
							</s:if>
						</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td height="10px"></td>
		</tr>
	</table>
</s:form></div>

<div id="countryPannel" style="display: none;">
	<div class="hd"></div>
	<div class="bd" id="updateCountryCodeContent"></div>
</div>

<!-- panel for upload config setting -->
<div id="updateOptionPanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd" id="updateOptionPanelContent"></div>
</div>

<!-- insert HiveAp filter jsp -->
<div style="display: none;">
	<tiles:insertDefinition name="hiveApFilter" />
</div>

<!-- panel for reboot message -->
<div id="cliInfoPanel" style="display: none;">
	<div class="hd" id="cliInfoTitle">
		Dialog
	</div>
	<div class="bd">
		<div id="cli_viewer" class="cli_viewer"></div>
	</div>
	<div class="ft"></div>
</div>


<script>
// upload option settings
var updateOptionPanel = null;
var waitingPanel = null;
var updateOptionPanelAttr = null;
var parentHeight;

function createUpdateOptionPanel(width, height, title){
	var div = document.getElementById("updateOptionPanel");
	var content = document.getElementById("updateOptionPanelContent");
	content.width = width;
	content.height = height;
	updateOptionPanel = new YAHOO.widget.Panel(div, { 	width:(width+10)+"px",
														fixedcenter:"contained",
														visible:false,
														draggable: true,
														zindex:4,
														modal:true,
														constraintoviewport:true } );

	updateOptionPanel.setHeader(title);
	updateOptionPanel.render();
	div.style.display="";
	updateOptionPanel.beforeHideEvent.subscribe(closeUpdatePanelIE);
}

function closeUpdateOptionPanel() {
	updateOptionPanel.hide();
	setTimeout("hideErrorNode()", 8000);
}

function closeUpdatePanelIE(){
	if(YAHOO.env.ua.ie){
		document.getElementById("updateOptionPanelContent").style.display = "none";
	}
	YAHOO.util.Dom.setStyle(Get(accordionView.getDrawerContentId('cofigurePolicy')), 'height', 'auto');
}

function openUpdateOptionPanel(title, doOperation){
	var width = 680, height = 400;

	if(null == updateOptionPanel){
		createUpdateOptionPanel(width , height, title);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("updateOptionPanelContent").style.display = "";
	}
	// set the inner iframe height
	var iframe = document.getElementById("updateOptionPanelContent");
	iframe.height  = height;
	updateOptionPanel.render();
	if(waitingPanel == null){
		createWaitingPanel();
	}
	if(waitingPanel != null){
		waitingPanel.setHeader("Loading configuration settings...");
		waitingPanel.show();
	}

	var url =  "<s:url action='hiveApUpdate' includeParams='none' />?operation="+ doOperation +
		"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('get', url, {success : succFetchUploadSettings, failure : resultDoNothing, timeout: 60000}, null);
}

function showOptionPanel(){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	// set the parent height to fix this panel
	var parentEl = parent.document.getElementById('cofigurePolicyContentId');
	if (parentEl) {
		parentHeight = YAHOO.util.Region.getRegion(parentEl).height;
		if (parentHeight < 500) {
			YAHOO.util.Dom.setStyle(parentEl, 'height', '480px');
		}
	}
	updateOptionPanel.show();
}

var succFetchUploadSettings = function(o){
	set_innerHTML("updateOptionPanelContent",o.responseText);
	setTimeout("showOptionPanel()",2000);
};

function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"280px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("Loading configuration...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" includeParams="none" />" />');
	waitingPanel.render(document.body);
}
</script>

<script>

function configSimplifiedUpdate(){
	arrayOperations = [
		"checkConnectStatus",
		"checkSelectedNWPolicy",
		"updateNetworkPolicy",
		"getDeviceCounts",
		"getRebootDevices",
		"checkNetworkPolicy",
		"uploadWizard"
	];
	configUpdateOperation();
}

function configUpdateAdvanceMode(params){
	arrayOperations = [
			"checkConnectStatus",
			"checkSelectedNWPolicy",
			"updateNetworkPolicy",
			"checkNetworkPolicy",
			["uploadWizard", params]
		];
	configUpdateOperation();
}

function checkSelectedNWPolicyForIDM(){
	var param = "configConfiguration=true&configCwp=true&configCertificate=true&configUserDatabase=true&saveUploadSetting=false";
	param = param + "&configSelectType=auto&configActivateType=activateAfterTime";
	checkSelectedNWPolicy(param);
}

/**
function checkDeviceConnectStatus(){
	if(!validateApSelection()){
		return false;
	}
	openWaitingPanel("Checking selected deivce connection status...");

	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.getElementById("hiveApList").operation.value = 'checkConnectStatus';
	YAHOO.util.Connect.setForm(document.getElementById("hiveApList"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succConnectStatusCheck, failure : resultDoNothing, timeout: 240000}, null);
}
**/

/**
//zjie add update HiveAp Network Policy to guid selected.
var advancedUpdate = false;
function checkSelectedNWPolicy(para, advancedUP){
	if (para) {
		uplaodOperationPara=para;
	} else {
		uplaodOperationPara=null;
	}
	
	if(advancedUP){
		advancedUpdate = advancedUP;
	}else{
		advancedUpdate = false;
	}
	
	if(!validateApSelection()){
		return false;
	}
	if(waitingPanel == null){
		createWaitingPanel();
	}
	if(waitingPanel != null){
		waitingPanel.setHeader("Checking selected network policy...");
		waitingPanel.show();
	}

	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.getElementById("hiveApList").operation.value = 'checkSelectedNWPolicy';
	document.getElementById("hiveApList").selectedNWPolicy.value = hm.util.getSelectedCheckItems("networkPolicys");
	YAHOO.util.Connect.setForm(document.getElementById("hiveApList"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSelectedNWPolicy, failure : resultDoNothing, timeout: 240000}, null);
}
**/
/**
function updateSelectedNetworkPolicy(){
	if(waitingPanel == null){
		createWaitingPanel();
	}
	if(waitingPanel != null){
		waitingPanel.setHeader("Updating selected network policy...");
		waitingPanel.show();
	}

	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.getElementById("hiveApList").operation.value = 'updateNetworkPolicy';
	document.getElementById("hiveApList").selectedNWPolicy.value = hm.util.getSelectedCheckItems("networkPolicys");
	YAHOO.util.Connect.setForm(document.getElementById("hiveApList"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succUpdateNWPolicy, failure : resultDoNothing, timeout: 240000}, null);
}
**/

/**
var succSelectedNWPolicy = function(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var resCheck = " + o.responseText);
	if(resCheck.t){
		if(resCheck.update){
			thisOperation ='checkSelectedNWPolicy';
			confirmDialog.cfg.setProperty('text', "<html><body>" + '<s:text name="config.guid.hiveAp.update.networkPolicy" />' + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
			confirmDialog.show();
		}else if(advancedUpdate){
			checkNetworkPolicy();
		}else{
			showSimpllyUpdatePanel();
		}
	}else{
		hm.util.displayJsonErrorNote(resCheck.m);
	}
}
**/
/**
var succUpdateNWPolicy = function(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	refreshGuidList(false);

	eval("var details = " + o.responseText);
	if(details.t){
		ignoreErrorDevice(details.ignore);
		if(details.ignoreMsg){
			hm.util.displayJsonErrorNote(details.ignoreMsg);
		}
		if(advancedUpdate){
			checkNetworkPolicy();
		}else{
			showSimpllyUpdatePanel();
		}
	}else{
		hm.util.displayJsonErrorNote(details.m);
	}
}
**/

function ignoreErrorDevice(dList){
	if(dList == null || dList.length == 0){
		return;
	}
	for(var index=0; index<dList.length; index++){
		var selecteds = document.getElementsByName("selectedIds");
		for(var i=0; i<selecteds.length; i++){
			if(selecteds[i].value == dList[index]){
				selecteds[i].checked = false;
				hm.util.toggleCheck(selecteds[i]);

				var tag = selecteds[i].getAttribute("tag");
				if(tag == "hiveAp"){
					document.getElementById("apGroup").checked = false;
				}else if(tag == "router"){
					document.getElementById("routeGroup").checked = false;
				}else if(tag == "switch"){
					document.getElementById("switchGroup").checked = false;
				}else if(tag == "l2_vpnGroup"){
					document.getElementById("l2_vpnGroup").checked = false;
				}else if(tag == "l3_vpnGroup"){
					document.getElementById("l3_vpnGroup").checked = false;
				}
			}
		}
	}
}

//zjie add end
/**
// upload HiveAP
function uploadConfig(){
	var url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime()+"&exConfigGuideFeature=uploadConfigEx";
	uploadAction(url);
}

function uploadOperation(param){
	var url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime()+"&exConfigGuideFeature=uploadConfigEx";
	url = url + "&" + param;
	uploadAction(url);
}

function uploadAction(url){
	if(!validateHiveAp("uploadWizard")){
		return;
	}

	//top.hideWarningMessage();
	//document.forms[formName].operation.value = "uploadWizard";
	document.getElementById('hiveApList').operation.value = "uploadWizard";
	YAHOO.util.Connect.setForm(document.getElementById('hiveApList'));
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : uploadConfigResult, failure : resultDoNothing, timeout: 60000}, null);

	if(waitingPanel == null){
		createWaitingPanel();
	}
	if(waitingPanel != null){
		waitingPanel.setHeader("Loading configuration...");
		waitingPanel.show();
	}
}
**/

/**
var uploadConfigResult = function(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	//clear simplly update param.
	document.getElementById("simpleUpdate").value = false;
	document.getElementById("completeCfgUpdate").value = false;
	document.getElementById("imageUpgrade").value = false;
	document.getElementById("forceImageUpgrade").value = false;
	document.getElementById("simplifiedRebootType").value = REBOOT_TYPE_AUTO;
	
	eval("var resp = " + o.responseText);
	var actionErrors = resp.actionErrors;
	if(actionErrors != null && actionErrors.length>0){
		for(var i=0; i<actionErrors.length; i++){
			hm.util.displayJsonErrorNote(actionErrors[i]);
		}
	}else{
		//initAllCheckBox(false);
		refreshGuidList(false);

	}
};
**/

function showError(message) {
	var td = document.getElementById("noteText");
	td.removeChild(td.firstChild);
	td.appendChild(document.createTextNode(message));
	YAHOO.util.Dom.setStyle('note', "display", "");
	delayHideNotes(5);
}

var notesTimeoutId;
function delayHideNotes(seconds) {
	notesTimeoutId = setTimeout("hideNotes()", seconds * 2000);  // seconds
}
function hideNotes() {
	hm.util.wipeOut('notes', 800);
}

function refreshGuidList(timed) {
	//fix bug 24606 always refresh list, through to sesstion timeout.
	//if (!timed || (count++ < total)) {
		url = "<s:url action='hiveAp' includeParams='none'/>" + "?operation=uploadResultRefreshGuid" + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : refreshSuccess, argument: [timed]}, null);
	//}
}

var refreshSuccess = function(o){
	eval("var resp = " + o.responseText);
	var timed = o.argument[0];
	var jsonResults = resp.list;

	var rowChanged = false;
	for (var i = 0; i < jsonResults.length; i++) {
		var entryId = jsonResults[i].id;
		var entryContent = jsonResults[i].result;
		var trEl = document.getElementById("r" + entryId);
		// Child nodes of tr are td elements to be updated.
		if(null == trEl){
			rowChanged = true;
			continue;
		}else{
			updateRow(trEl,jsonResults[i]);
		}
	}
	// fix trigger issue
	tooltip.cfg.setProperty("context", tooltip.cfg.getProperty('context'));
	if(timed){
		timeoutId = window.setTimeout("refreshGuidList(true);", interval * 1000);
	}
};

function updateRow(tr,jsonResults){
	var result = jsonResults.result;
	for(var i=1; i<tr.cells.length; i++){
		var cell = tr.cells[i];
		var cellId = cell.id;
		if(cellId == "d5"){
			cell.innerHTML = jsonResults.d5;
		}else if(cellId == "d1"){
			cell.innerHTML = jsonResults.d1;
		}else if(cellId == "d4"){
			cell.innerHTML = jsonResults.d4;
		}else if(cellId == "d3"){
			cell.innerHTML = jsonResults.d3;
		}else if(cellId == "d10"){
			cell.innerHTML = jsonResults.d10;
		}else if(cellId == "d28"){
			cell.innerHTML = jsonResults.d28;
		}else if(cellId == "d28"){
			cell.innerHTML = jsonResults.d28;
		}else if(cellId == "d13"){
			cell.innerHTML = jsonResults.d13;
			cell.title=jsonResults.d13title;
		}else if(cellId == "d39" && result != null){
			///setup the download rate column
	 	 	if(result.d11_key == ACTION_REBOOT){
	 	 		cell.innerHTML = "<a class='actionType' href='javascript:requestReboot("+ jsonResults.id + ");'>Reboot</a>";
	 	 		showGotoIDMButton(true);
	 	 	}else if(result.d9_key == UPDATE_SUCCESSFUL){
		 	 	cell.innerHTML="<font color='green'><b>"+  result.updateTime + "</b></font>&nbsp;";
	 	 		showGotoIDMButton(true);
	 	 	}else if(result.d9_key == UPDATE_FAILED ||
				result.d9_key == UPDATE_STAGED ||
				result.d9_key == UPDATE_TIMEOUT ||
				result.d9_key == UPDATE_ABORT ||
				result.d9_key == UPDATE_CANCELED ||
				result.d9_key == REBOOTING ||
				result.d9_key == WARNING ||
				result.d9_key == REBOOT_SUCCESSFUL){

				var status = result.d9_value + "&nbsp;";
				if(result.d10){
					var desc = result.d10;
				}else{
					var desc = "";
				}
				setUploadStatus(cell, tr.id+"_desc", status, desc);
			}else{
				cell.innerHTML = getUploadRateDiv(result.d7);
			}
		}
	}
	//FIXME
	showGotoIDMButton(true);
}

function getUploadRateDiv(rate){
	return "<div class='a0'><div class='a1' style='width:" + rate + "px'>" + rate + "%</div></div>";
}

function setUploadStatus(cell, id, status, desc){
	var descSpan = cell.firstChild;
	if(!descSpan || descSpan.tagName != "SPAN" || descSpan.id != id){
		cell.innerHTML = "<span id='" + id + "'></span>";
		descSpan = cell.firstChild;
	}
	descSpan.innerHTML = status;
	//setup the description
	if(desc){
		descSpan.setAttribute("desc", desc);
 	}else{
 		descSpan.removeAttribute("desc");
 	}
}

function showGotoIDMButton(send2Server) {
	// For IDM flow
	if(Get("uploadDoneSpanId")) {
		Get("uploadDoneSpanId").onclick = idmDoneClick;
		YAHOO.util.Dom.replaceClass(Get("uploadDoneSpanId"), "hidden", "inline");

		if(send2Server) {
			 //uploadConfigSucc
		    var url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=uploadConfigSucc4IDM" +"&ignore="
		            + new Date().getTime();
		    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : function(o){
		        //dump
		    } }, null);
		}
	}
}

function createTooltip(){
	var table = document.getElementById("hiveApListTableDraw");
	if(!table || table == ""){
		return;
	}
	var rows = table.rows;
	var ids = [];
	if(rows.length > 0){
		for(var i=0; i< rows.length; i++){
			var rowId = rows[i].id;
			if(rowId){
				//if(document.getElementById(rowId+"_desc") != null){
				ids.push(rowId+"_desc");
				//}
			}
		}
	}
	// use YAHOO.util.Dom.generateId()
	// fix tooltip underlay overhead issue when page reload with innerHTML method.
	tooltip = new YAHOO.widget.Tooltip("ttDesc_" + YAHOO.util.Dom.generateId(), {
		context:ids,
		autodismissdelay: 900000,
		width: "300px"
	});

	tooltip.contextMouseOverEvent.subscribe(
		function(type, args) {
			var context = args[0];
			if (context.getAttribute("desc") == undefined) {
				return false;
			} else {
				return true;
			}
		}
	);
	tooltip.contextTriggerEvent.subscribe(
		function(type, args) {
			var context = args[0];
			this.cfg.setProperty("text", context.getAttribute("desc"));
		}
	);
}

//reboot HiveAp
var thisRebootId;
var thisOperation;
function requestReboot(id){
	thisOperation = "requestReboot";
	thisRebootId = id;
	hm.util.confirmRebooting(isAnySwitchSelected([id]));
}

function doContinueOper() {
	if(thisOperation == 'checkConnectStatus' || 
			thisOperation == 'checkSelectedNWPolicy' ||
			thisOperation == 'checkNetworkPolicy'){
		configUpdateOperation();
		return;
	}
	
	if (thisOperation == 'requestReboot') {
		doAjaxRequestReboot(thisRebootId, "rebootHiveAPs", cliInfoResult);
		return;
	}
	if(thisOperation == 'remove'){
		var url = "";
		document.forms["hiveApList"].operation.value = thisOperation;
		YAHOO.util.Connect.setForm(document.getElementById('hiveApList'));
		url =  "<s:url action='hiveAp' includeParams='none' />" +
			"?hmListType=manageAPGuid"+
			"&jsonMode=true"+
			"&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succFetchHiveApsList, failure : resultDoNothing, timeout: 60000}, null);
	}

	if(thisOperation == '<s:text name="topology.menu.hiveAp.reboot"/>'){
		doAjaxRequestReboot(null, "rebootHiveAPs", cliInfoResult);
		return;
	}

	if(thisOperation == 'multiEdit'){
		var selectedIds = hm.util.getSelectedIds();
		if (selectedIds.length == 1) {
			editHiveApAction(selectedIds[0]);
		} else {
			subOperation = "multiEdit";
			var url = "";
			document.forms["hiveApList"].operation.value = thisOperation;
			YAHOO.util.Connect.setForm(document.getElementById('hiveApList'));
			url =  "<s:url action='hiveAp' includeParams='none' />" +
				"?hmListType=manageAPGuid"+
				"&jsonMode=true"+
				"&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succMultiEditHiveAp, failure : resultDoNothing, timeout: 60000}, null);
		}
	}
}

var connectedFailed = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function checkDeviceGroup(checkAll, operation){
	if(operation == "hiveAp"){
		<s:if test="%{selectedAllApCounts > hiveApList.size}">
			hm.util.toggleHideElement('apPageItemsSelectedRow', !checkAll.checked);
			if (!checkAll.checked) {
				hm.util.toggleHideElement('apAllItemsSelectedRow', true);
				hm.util.toggleAllItemsSelected(false);
				allAp = false;
			}
		</s:if>
	}else if(operation == "router"){
		<s:if test="%{selectedAllBrCounts > routerList.size}">
			hm.util.toggleHideElement('brPageItemsSelectedRow', !checkAll.checked);
			if (!checkAll.checked) {
				hm.util.toggleHideElement('brAllItemsSelectedRow', true);
				hm.util.toggleAllItemsSelected(false);
				allBr = false;
			}
		</s:if>
	}else if(operation == "switch"){
		<s:if test="%{selectedAllSwitchCounts > switchList.size}">
			hm.util.toggleHideElement('switchPageItemsSelectedRow', !checkAll.checked);
			if (!checkAll.checked) {
				hm.util.toggleHideElement('switchAllItemsSelectedRow', true);
				hm.util.toggleAllItemsSelected(false);
				allSwitch = false;
			}
		</s:if>
	}else if(operation == "l2_vpnGateway"){
		<s:if test="%{selectedAllL2VPNCounts > l2_vpnGatewayList.size}">
			hm.util.toggleHideElement('l2vpnPageItemsSelectedRow', !checkAll.checked);
			if (!checkAll.checked) {
				hm.util.toggleHideElement('l2vpnAllItemsSelectedRow', true);
				hm.util.toggleAllItemsSelected(false);
				allL2Vpn = false;
			}
		</s:if>
	}else if(operation == "l3_vpnGateway"){
		<s:if test="%{selectedAllL3VPNCounts > l3_vpnGatewayList.size}">
			hm.util.toggleHideElement('l3vpnPageItemsSelectedRow', !checkAll.checked);
			if (!checkAll.checked) {
				hm.util.toggleHideElement('l3vpnAllItemsSelectedRow', true);
				hm.util.toggleAllItemsSelected(false);
				allL3Vpn = false;
			}
		</s:if>
	}

	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
			if (!cb.disabled && cb.checked != checkAll.checked && cb.getAttribute("tag") == operation) {
				cb.checked = checkAll.checked;
				hm.util.toggleRow(cb);
				//fnr
			}
		}
	}
}

function allDeviceChecked(checkEle){
	hm.util.toggleCheckAll(checkEle);
	var inputElements = document.getElementsByName('deviceGroup');
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
			if (!cb.disabled && cb.checked != checkEle.checked) {
				cb.checked = checkEle.checked;
			}
		}
	}

	hm.util.toggleHideElement('apPageItemsSelectedRow', true);
	hm.util.toggleHideElement('apAllItemsSelectedRow', true);

	hm.util.toggleHideElement('brPageItemsSelectedRow', true);
	hm.util.toggleHideElement('brAllItemsSelectedRow', true);

	hm.util.toggleHideElement('switchPageItemsSelectedRow', true);
	hm.util.toggleHideElement('switchAllItemsSelectedRow', true);

	hm.util.toggleHideElement('l2vpnPageItemsSelectedRow', true);
	hm.util.toggleHideElement('l2vpnAllItemsSelectedRow', true);

	hm.util.toggleHideElement('l3vpnPageItemsSelectedRow', true);
	hm.util.toggleHideElement('l3vpnAllItemsSelectedRow', true);
}

function setGroupItemsSelected(group){
	if(group == "hiveAp"){
		hm.util.toggleHideElement('apPageItemsSelectedRow', true);
		hm.util.toggleHideElement('apAllItemsSelectedRow', false);
		allAp = true;
	}else if(group == "router"){
		hm.util.toggleHideElement('brPageItemsSelectedRow', true);
		hm.util.toggleHideElement('brAllItemsSelectedRow', false);
		allBr = true;
	}else if(group == "switch"){
		hm.util.toggleHideElement('switchPageItemsSelectedRow', true);
		hm.util.toggleHideElement('switchAllItemsSelectedRow', false);
		allSwitch = true;
	}else if(group == "l2_vpnGateway"){
		hm.util.toggleHideElement('l2vpnPageItemsSelectedRow', true);
		hm.util.toggleHideElement('l2vpnAllItemsSelectedRow', false);
		allL2Vpn = true;
	}else if(group == "l3_vpnGateway"){
		hm.util.toggleHideElement('l3vpnPageItemsSelectedRow', true);
		hm.util.toggleHideElement('l3vpnAllItemsSelectedRow', false);
		allL3Vpn = true;
	}
}

function clearGroupSelection(group){
	if(group == "hiveAp"){
		hm.util.toggleHideElement('apAllItemsSelectedRow', true);
		var apGroupCheck = $("#apGroup")[0];
		apGroupCheck.checked = false;
		apGroupCheck.value = false;
		checkDeviceGroup(apGroupCheck, group);
	}else if(group == "router"){
		hm.util.toggleHideElement('brAllItemsSelectedRow', true);
		var brGroupCheck = $("#routeGroup")[0];
		brGroupCheck.checked = false;
		brGroupCheck.value = false;
		checkDeviceGroup(brGroupCheck, group);
	}else if(group == "switch"){
		hm.util.toggleHideElement('switchAllItemsSelectedRow', true);
		var switchGroupCheck = $("#switchGroup")[0];
		switchGroupCheck.checked = false;
		switchGroupCheck.value = false;
		checkDeviceGroup(switchGroupCheck, group);
	}else if(group == "l2_vpnGateway"){
		hm.util.toggleHideElement('l2vpnAllItemsSelectedRow', true);
		var l2VpnGroupCheck = $("#l2_vpnGroup")[0];
		l2VpnGroupCheck.checked = false;
		l2VpnGroupCheck.value = false;
		checkDeviceGroup(l2VpnGroupCheck, group);
	}else if(group == "l3_vpnGateway"){
		hm.util.toggleHideElement('l3vpnAllItemsSelectedRow', true);
		var l3VpnGroupCheck = $("#l3_vpnGroup")[0];
		l3VpnGroupCheck.checked = false;
		l3VpnGroupCheck.value = false;
		checkDeviceGroup(l3VpnGroupCheck, group);
	}

}

function prepareSelectedDeviceIdStr(){
	if($("#allItemsSelectedVar").val() == "true"){
		$("#selectedDeviceIdStr_ID").val($("#selectedAllStr_ID").val());
		return;
	}

	var selectedDeviceIdStr = "";

	if(allAp){
		selectedDeviceIdStr += "," + $("#selectedAllApStr_ID").val();
	}
	if(allBr){
		selectedDeviceIdStr += "," + $("#selectedAllBrStr_ID").val();
	}
	if(allSwitch){
		selectedDeviceIdStr += "," + $("#selectedAllSwitchStr_ID").val();
	}
	if(allL2Vpn){
		selectedDeviceIdStr += "," + $("#selectedAllL2VPNStr_ID").val();
	}
	if(allL3Vpn){
		selectedDeviceIdStr += "," + $("#selectedAllL3VPNStr_ID").val();
	}

	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
    		var tag = cb.getAttribute("tag");
    		if( (tag == "hiveAp" && allAp) ||
    				(tag == "router" && allBr) ||
    				(tag == "switch" && allSwitch) ||
    				(tag == "l2_vpnGateway" && allL2Vpn) ||
    				(tag == "l3_vpnGateway" && allL3Vpn) ){
    			continue;
    		}

			if (!cb.disabled && cb.checked) {
				selectedDeviceIdStr += "," + cb.value;
			}
		}
	}

	if(selectedDeviceIdStr.length > 0){
		$("#selectedDeviceIdStr_ID").val(selectedDeviceIdStr.substring(1));
	}
}

function clearAllSelectedDeviceGroup(){
	var allGroup = document.getElementsByName('deviceGroup');
	if(allGroup){
		for(var i=0; i<allGroup.length; i++){
			allGroup[i].checked = false;
		}
	}
}

// fix bug 15218
function preShowColumnsPanel() {
	var culumnsPanel_C = document.getElementById('culumnsPanel_c');
	if (culumnsPanel_C) {
		var a = culumnsPanel_C.getElementsByTagName('a');
		for (var i=0;i<a.length;i++) {
			a[i].parentNode.removeChild(a[i]);
		}
	}
	culumnsPanel = null;
	showColumnsPanel();
}

<s:if test="%{blnResetColumnItems}">
	var leftColumns = new Array();
	var rightColumns = new Array();
	<s:iterator value="%{availableColumns}">
		leftColumns.push(encapColumnOption('<s:property value="%{columnId}" />', '<s:property value="%{columnDescription}" />'));
	</s:iterator>
	<s:iterator value="%{selectedColumns}">
		rightColumns.push(encapColumnOption('<s:property value="%{columnId}" />', '<s:property value="%{columnDescription}" />'));
	</s:iterator>
	initializeColumns(leftColumns, rightColumns);
</s:if>

//reboot HiveAp
function rebootHiveAps(){
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	thisOperation = '<s:text name="topology.menu.hiveAp.reboot"/>';
	hm.util.confirmRebooting(isAnySwitchSelected(selectedIds));
}

function doAjaxRequestReboot(id, operation, callback, postData ){
	var url = "<s:url action='hiveApUpdate' includeParams='none' />?jsonMode=true";
	if(id > 0){
		url +="&operation=" + operation + "&selectedIds=" + id;
	}else{
		document.forms["hiveApList"].operation.value = operation;
		YAHOO.util.Connect.setForm(document.getElementById('hiveApList'));
	}
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:callback,failure:connectedFailed,timeout: 60000}, postData);
	if(waitingPanel == null){
		createWaitingPanel();
	}
	if(waitingPanel != null){
		waitingPanel.setHeader("Rebooting devices...");
		waitingPanel.show();
	}
}

var cliInfoResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	refreshGuidList(false);
	eval("var details = " + o.responseText);
	if (!details.t){
		hm.util.displayJsonErrorNote(details.m);
	}
}

var cliInfoPanel = null;
function createCliInfoPanel() {
	var div = window.document.getElementById('cliInfoPanel');
	cliInfoPanel = new YAHOO.widget.Panel(div, { width:"600px",
												visible:false,
												fixedcenter:"contained",
												draggable:true,
												constraintoviewport:true,
												modal:true } );
	cliInfoPanel.render();
	div.style.display = "";
<%--	cliInfoPanel.beforeHideEvent.subscribe(closeCliInfoPanelIE);--%>
}
<%-- function not works in IE, not need this function in fact.
function closeCliInfoPanelIE(){
	if(YAHOO.env.ua.ie){
		document.getElementById("cliInfoPanel").style.display = "none";
	}
}--%>

// Add Tools button
function onMenuItemClick(p_sType, p_aArgs, p_oValue) {
	prepareSelectedDeviceIdStr();
	if(p_oValue == "upgradeCountryCode"){
		openUpdateCountryCodePanel("Device Upload Country Code", "countryCodeInputJson")
	}else if(p_oValue == "simpllyUpdate"){
		configSimplifiedUpdate();
	}else if(p_oValue == "advancedUpdate"){
		openUpdateOptionPanel("Device Upload Options", "updateOptionJson");
	}
}

function toolsClick(){
	if(g_oMenu == null){
		g_oMenu = new YAHOO.widget.Menu("toolsMenuDiv", { fixedcenter: false, zIndex: 999 });

		g_oMenu.addItems([
       	    [
       	        { text: '<s:text name="hiveAp.update.countryCode"/>', onclick: { fn: onMenuItemClick, obj: "upgradeCountryCode" } }
       	    ]
       	]);

       	g_oMenu.subscribe("beforeShow", function(){
       		var x = YAHOO.util.Dom.getX('toolsMenu');
       		var y = YAHOO.util.Dom.getY('toolsMenu');
       		YAHOO.util.Dom.setX('toolsMenuDiv', x);
       		YAHOO.util.Dom.setY('toolsMenuDiv', y+20);
       	});

       	g_oMenu.render();
	}
	g_oMenu.show();
}

function updateButtonClick(){
	if(up_menu == null){
		// updateMenuDiv	toolsMenuDiv
		up_menu = new YAHOO.widget.Menu("updateMenuDiv", { fixedcenter: false, zIndex: 999 });
		
		var tItems=[];
		tItems.push([{ text: '<s:text name="geneva_06.update.menu.update"/>', onclick: { fn: onMenuItemClick, obj: "simpllyUpdate" } }]);
		<s:if test="%{!userSourceFromIdm}">
			tItems.push([{ text: '<s:text name="geneva_06.update.menu.advanced_update"/>', onclick: { fn: onMenuItemClick, obj: "advancedUpdate" } }]);
		</s:if>
		up_menu.addItems(tItems);
		
		up_menu.subscribe("beforeShow", function(){
       		var x = YAHOO.util.Dom.getX('updateMenu');
       		var y = YAHOO.util.Dom.getY('updateMenu');
       		YAHOO.util.Dom.setX('updateMenuDiv', x);
       		YAHOO.util.Dom.setY('updateMenuDiv', y+20);
       	});

		up_menu.render();
	}
	up_menu.show();
}

function openUpdateCountryCodePanel(title, doOperation){
	//var width = 680, height = 400;

	//if(null == updateCountryCodePanel){
	//	createUpdateCountryCodePanel(width , height, title);
	//}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	//if(YAHOO.env.ua.ie){
	//	document.getElementById("updateCountryCodeContent").style.display = "";
	//}
	// set the inner iframe height
	//var iframe = document.getElementById("updateCountryCodeContent");
	//iframe.height  = height;
	//updateCountryCodePanel.render();

	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}

	if(waitingPanel == null){
		createWaitingPanel();
	}
	if(waitingPanel != null){
		waitingPanel.setHeader("Loading Country Code Update settings...");
		waitingPanel.show();
	}

	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime() +
			"&jsonMode=true";
	document.getElementById("hiveApList").operation.value = doOperation;
	YAHOO.util.Connect.setForm(document.getElementById("hiveApList"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succFetchCountryCode, failure : resultDoNothing, timeout: 60000}, null);
}

function uploadCountryCodeJson(operation, countryCode, offset){
	hideCountryCodePanel();
	if(waitingPanel != null){
		waitingPanel.setHeader("Upload Device Country Code...");
		waitingPanel.show();
	}

	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime() +
		"&jsonMode=true"+
		"&countryCode="+countryCode+
		"&countryCode_offSet="+offset;
	document.getElementById("hiveApList").operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("hiveApList"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succUploadCountryCode, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchCountryCode = function(o){
	set_innerHTML("updateCountryCodeContent",o.responseText);
	setTimeout("showCountryCodePanel()",500);
};

var succUploadCountryCode = function(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	eval("var details = " + o.responseText);
	if (details.t){
		refreshGuidList(false);
	} else {
		hm.util.displayJsonErrorNote(details.m);
	}
};

function showCountryCodePanel(){
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	// set the parent height to fix this panel
	//var parentEl = parent.document.getElementById('cofigurePolicyContentId');
	//if (parentEl) {
	//	parentHeight = YAHOO.util.Region.getRegion(parentEl).height;
	//	if (parentHeight < 500) {
	//		YAHOO.util.Dom.setStyle(parentEl, 'height', '480px');
	//	}
	//}

	if(null == countryPannel){
		createCountryCodePanel();
	}
	countryPannel.show();
}

function hideCountryCodePanel(){
	countryPannel.hide();
	closeCountryPanelIE();
}

var countryPannel = null;

function createCountryCodePanel() {
	var div = window.document.getElementById('countryPannel');
	countryPannel = new YAHOO.widget.Panel(div, { width:"600px",
												visible:false,
												fixedcenter:"contained",
												draggable:true,
												constraintoviewport:true,
												modal:true } );
	countryPannel.render();
	div.style.display = "";
	countryPannel.setHeader("Country Code Update settings");
	countryPannel.beforeHideEvent.subscribe(closeCountryPanelIE);
}

function closeCountryPanelIE(){
	if(YAHOO.env.ua.ie){
		document.getElementById("countryPannel").style.display = "none";
	}
}

function requestMismatchAudit(hiveApId){
	if(waitingPanel != null){
		waitingPanel.setHeader("Retrieving Information...");
	}
	document.getElementById("hiveApList_hiveApId").value = hiveApId;
	doAjaxRequestForMisAudit("configurationAudit", "", cliInfoResult);
}

function doAjaxRequestForMisAudit(operation, menuText, callback, postData ){
	document.forms["hiveApList"].operation.value = operation;
	if(menuText != undefined && menuText != ""){
		document.forms["hiveApList"].menuText.value = menuText;
	}
	YAHOO.util.Connect.setForm(document.forms["hiveApList"]);
	url = "<s:url action='mapNodes' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:callback,failure:connectedFailed,timeout: 60000}, postData);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

// End Tools button

</script>
<tiles:insertDefinition name="deviceMappingRedirector" />
<tiles:insertDefinition name="updateSimpleModel" />

