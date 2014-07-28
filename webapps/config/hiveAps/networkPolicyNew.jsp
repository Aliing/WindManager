<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script type="text/javascript">
	var formName2 = "newNetworkPolicyPanelForm";

	function doSubmitNewNetworkPolicy() {
		if (npOperationLock.npNew) {
			return;
		}
		//check inputs first
		if (!validateInputs()) {
			return;
		}
		npOperationLock.npNew = true;
		
		var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
		Get(formName2 + '_operation').value = "newNetworkPolicy";
		//debug form items
		YAHOO.util.Connect.setForm(Get('newNetworkPolicyPanelForm'));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succCreateNewNetworkPolicy, failure : failCreateNewNetworkPolicy, timeout: 60000}, null);
	}
	var succCreateNewNetworkPolicy = function(o) {
		npOperationLock.npNew = false;
		eval("var results = " + o.responseText);
		if (results.resultStatus == true) {
			//call parent page to get all policies
			fetchNetworkPolicy2PageWithSelectedItem(results.addedId);
			hideSelectNetworkPolicyPanel();
			setGotoNetworkPolicyDrawer();
		} else {
			hm.util.reportFieldError(Get("wlanErrSpan"), results.e);
		}
	}
	var failCreateNewNetworkPolicy = function(o) {
		npOperationLock.npNew = false;
	}
	
	function validateInputs() {
		var errMsg = "";
		errMsg = hm.util.validateString(Get(formName2+"_configName").value, "<s:text name="config.configTemplate.configName" />");
		if (errMsg != null && errMsg != "") {
			hm.util.reportFieldError(Get(formName2+"_configName"), errMsg);
		}
		
		if ($("table#npType_selection_tbl div.nptype_selected").length < 1) {
			errMsg = "<s:text name='error.network.policy.type.least.one' />";
			hm.util.reportFieldError($("div#npType_selection_tbl_note")[0], errMsg);
		}
		
		if (errMsg != null && errMsg != "") {
			return false;
		}
		return true;
	}
	
	function editHiveProfile() {
		saveTmpNetworkPolicyArgs();
		curNetworkNewOrModifyType = '1';
		if (Get(formName2+"_hiveId") != null) {
			editHiveFromSelectNetworkPolicy(Get(formName2+"_hiveId").value);
		}
	}
	
	function editHiveFromSelectNetworkPolicy(id) {
		var url = "<s:url action='hiveProfiles' includeParams='none' />?jsonMode=true&operation=edit"
					+ "&id="+id
					+ "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditHiveProfiles, failure : resultDoNothing, timeout: 60000}, null);	
	}
	
	var succEditHiveProfiles = function(o) {
		subDrawerOperation='updateHiveProfile';
		openSelectNPSubDrawer({title: "Edit Hive", responseText: o.responseText});
		<s:if test="%{savePermit == true}">
			showHideSelectNetworkPolicySubSaveBT(true);
		</s:if>
		<s:else>
			showHideSelectNetworkPolicySubSaveBT(false);
		</s:else>
		hideSelectNetworkPolicyPanel();
	}
	
	function saveTmpNetworkPolicyArgs() {
		tmpNetworkPolicyArgs = {name: Get(formName2+"_configName").value, desc: Get(formName2+"_description").value, npTypeValue: $("input:checked[type=radio][name=npType]").attr("id")}
	}
	function restoreTmpNetworkPolicyArgs() {
		if (tmpNetworkPolicyArgs && tmpNetworkPolicyArgs.name) {
			Get(formName2+"_configName").value = tmpNetworkPolicyArgs.name;
			if (tmpNetworkPolicyArgs.desc) {
				Get(formName2+"_description").value = tmpNetworkPolicyArgs.desc;
			}
			if (tmpNetworkPolicyArgs.npTypeValue) {
				$("input[type=radio][name=npType]").each(function(i, el){
					if (this.id === tmpNetworkPolicyArgs.npTypeValue) {
						this.checked = true;
					} else {
						this.checked = false;
					}
				});
			}
			tmpNetworkPolicyArgs = {};
		}
	}
</script>

<div>
	<s:form action="networkPolicy" id="newNetworkPolicyPanelForm" name="newNetworkPolicyPanelForm">
		<s:hidden name="operation" />
		<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td style="padding-top: 5px; padding-bottom: 2px; padding-right: 20px;">
					<table border="0" cellspacing="0" cellpadding="0" align="right" width="100%">
						<tr>
							<td width="100%" align="left">
								<img src="images/hm_v2/profile/HM-icon-network_policy.png" class="dialogTitleImg" />
								<span class="npcHead1"><s:text name="config.title.network.polciy" /></span>
							</td>
							<td align="right" style="padding-left:10px;" width="80px" nowrap><a href="javascript:void(0);" class="btCurrent"
							onclick="javascript: hideSelectNetworkPolicyPanel();" title="close"><img class="dinl" width="16px" height="16px" src="images/cancel.png" style="border:none;"/></a></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td><table><tr><td><span id="wlanErrSpan"/></span></td></tr></table></td>
					</tr>
					<tr>
						<td style="padding: 8px 5px 8px 5px;">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="100px"><s:text
									name="config.configTemplate.configName" /><font color="red"><s:text name="*" /></font></td>
								<td><s:textfield name="configName" size="32" maxlength="32"
									onkeypress="return hm.util.keyPressPermit(event,'name');" />
								<s:text name="config.configTemplate.configName.range" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="100px"><s:text
									name="config.configTemplate.description" /></td>
								<td><s:textfield name="description" size="72"  maxlength="64"
									/>&nbsp;<s:text
									name="config.configTemplate.description.range" /></td>
							</tr>
							<%-- <tr>
								<td class="labelT1" width="150px"><s:text
									name="config.configTemplate.hive" /><font color="red"><s:text name="*" /></font></td>
								<td style="padding-right: 5px;">
									<s:if test="%{addedHiveId != null && addedHiveId > 0}">
									<s:select name="hiveId"
									list="%{list_hive}" listKey="id" listValue="value" cssStyle="width: 160px;" value="addedHiveId"/>
									</s:if>
									<s:else>
									<s:select name="hiveId"
									list="%{list_hive}" listKey="id" listValue="value" cssStyle="width: 160px;"/>
									</s:else>
									&nbsp;
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/new_disable.png" />"
										width="16" height="16" alt="New" title="New" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:addNewHiveFromSelectNetworkPolicy();"><img class="dinl"
										src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
									</s:else>
									<s:if test="%{writeDisabled == 'disabled'}">
										<img class="dinl marginBtn"
										src="<s:url value="/images/modify_disable.png" />"
										width="16" height="16" alt="Modify" title="Modify" />
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:editHiveProfile();"><img class="dinl"
										src="<s:url value="/images/modify.png" />"
										width="16" height="16" alt="Modify" title="Modify" /></a>
									</s:else>
								</td>
							</tr> --%>
						</table>
						</td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td>
					<div class="nptype_selection_desc"><s:text name="config.network.policy.type.selection.tip" /></div>
				</td>
			</tr>
			<tr>
				<td>
					<table id="npType_selection_tbl" style="margin-left:25px;margin-top:8px;">
						<tr>
							<td colspan="4"><div id="npType_selection_tbl_note"></div></td>
						</tr>
						<tr>
							<td>
								<s:if test='%{npTypeCtl.wirelessSelectedValue == "1"}'>
									<div id="np_type_check_wireless" class="nptype_selected" onclick="javascript: toggleNpTypeChecking(event, 'np_type_check_wireless');">
								</s:if>
								<s:else>
									<div id="np_type_check_wireless" class="nptype" onclick="javascript: toggleNpTypeChecking(event, 'np_type_check_wireless');">
								</s:else>
									<input type="hidden" class="valueStore" name="npTypeCtl.wirelessSelectedValue" value="<s:property value="npTypeCtl.wirelessSelectedValue"/>"/>
									<div class="nptype_check"><img src="<s:url value="/images/blank.png" />"/></div>
									<div class="nptype_desc">
										<div class="nptype_head"><s:text name="config.network.policy.type.wireless.name"/></div>
										<div class="nptype_content"><img src="<s:url value="/images/blank.png" />" class="wireless"/></div>
										<div class="nptype_footer"><s:text name="config.network.policy.type.wireless.desc"/></div>
									</div>
								</div>
							</td>
							<td>
								<s:if test='%{npTypeCtl.switchSelectedValue == "1"}'>
									<div id="np_type_check_switch" class="nptype_selected" onclick="javascript: toggleNpTypeChecking(event, 'np_type_check_switch');">
								</s:if>
								<s:else>
									<div id="np_type_check_switch" class="nptype" onclick="javascript: toggleNpTypeChecking(event, 'np_type_check_switch');">
								</s:else>
									<input type="hidden" class="valueStore" name="npTypeCtl.switchSelectedValue" value="<s:property value="npTypeCtl.switchSelectedValue"/>"/>
									<div class="nptype_check"><img src="<s:url value="/images/blank.png" />"/></div>
									<div class="nptype_desc">
										<div class="nptype_head"><s:text name="config.network.policy.type.switch.name"/></div>
										<div class="nptype_content"><img src="<s:url value="/images/blank.png" />" class="switch"/></div>
										<div class="nptype_footer"><s:text name="config.network.policy.type.switch.desc"/></div>
									</div>
								</div>
							</td>
							<td>
								<s:if test='%{npTypeCtl.routerSelectedValue == "1"}'>
									<div id="np_type_check_router" class="nptype_selected" onclick="javascript: toggleNpTypeChecking(event, 'np_type_check_router');">
								</s:if>
								<s:else>
									<div id="np_type_check_router" class="nptype" onclick="javascript: toggleNpTypeChecking(event, 'np_type_check_router');">
								</s:else>
									<input type="hidden" class="valueStore" name="npTypeCtl.routerSelectedValue" value="<s:property value="npTypeCtl.routerSelectedValue"/>"/>
									<div class="nptype_check"><img src="<s:url value="/images/blank.png" />"/></div>
									<div class="nptype_desc">
										<div class="nptype_head"><s:text name="config.network.policy.type.router.name"/></div>
										<div class="nptype_content"><img src="<s:url value="/images/blank.png" />" class="router"/></div>
										<div class="nptype_footer"><s:text name="config.network.policy.type.router.desc"/></div>
									</div>
								</div>
							</td>
							<td>
								<s:if test='%{npTypeCtl.bonjourSelectedValue == "1"}'>
									<div id="np_type_check_bonjour" class="nptype_selected" onclick="javascript: toggleNpTypeChecking(event, 'np_type_check_bonjour');">
								</s:if>
								<s:else>
									<div id="np_type_check_bonjour" class="nptype" onclick="javascript: toggleNpTypeChecking(event, 'np_type_check_bonjour');">
								</s:else>
									<input type="hidden" class="valueStore" name="npTypeCtl.bonjourSelectedValue" value="<s:property value="npTypeCtl.bonjourSelectedValue"/>"/>
									<div class="nptype_check"><img src="<s:url value="/images/blank.png" />"/></div>
									<div class="nptype_desc">
										<div class="nptype_head"><s:text name="config.network.policy.type.bonjour.name"/></div>
										<div class="nptype_content"><img src="<s:url value="/images/blank.png" />" class="bonjour"/></div>
										<div class="nptype_footer"><s:text name="config.network.policy.type.bonjour.desc"/></div>
									</div>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="padding-top: 2px; padding-bottom: 5px; padding-right: 15px;">
					<table border="0" cellspacing="0" cellpadding="0" align="center" style="margin-top: 15px;">
						<tr>
							<s:if test="%{writeDisabled == 'disabled'}">
							<td class="npcButton" align="center"></td>
							<td class="npcButton" align="center"><a href="javascript:void(0);" class="btCurrent" 
								title="<s:text name="config.v2.select.network.policy.popup.button.new"/>"><span><s:text name="config.v2.select.network.policy.popup.button.new"/></span></a></td>
							</s:if>
							<s:else>
							<td class="npcButton" align="center"><a href="javascript:void(0);" class="btCurrent" 
								onclick="javascript: doSubmitNewNetworkPolicy();" title="<s:text name="config.v2.select.network.policy.popup.button.new"/>"><span><s:text name="config.v2.select.network.policy.popup.button.new"/></span></a></td>
							</s:else>						
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>

<script type="text/javascript">
	function toggleNpTypeChecking(e, npTypeElId) {
		var el = document.getElementById(npTypeElId);
		if (el) {
		 	var valueEls = $(el).find(".valueStore");
			if (valueEls && valueEls.length > 0) {
				if (el.className === "nptype") {
					valueEls[0].value = "1";
					el.className = "nptype_selected";
				} else {
					valueEls[0].value = "-1";
					el.className = "nptype";
				}
			}
		}
		hm.util.stopBubble(e);
	}
</script>