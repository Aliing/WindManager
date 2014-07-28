<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script type="text/javascript">
	var formName2 = "modifyNetworkPolicyPanelForm";
	
	function finishModifyNetworkPolicy() {
		//check input first
		if (!validateInputs()) {
			return;
		}
		if (checkTypeChanged()) {
			doSubmitModifyNetworkPolicy();
		}
	}
	function doSubmitModifyNetworkPolicy() {
		var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
		document.forms['modifyNetworkPolicyPanelForm'].operation.value = "networkPolicyUpdate";
		//debug form items
		YAHOO.util.Connect.setForm(document.forms['modifyNetworkPolicyPanelForm']);
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succUpdateNetworkPolicy, failure : failUpdateNetworkPolicy, timeout: 60000}, null);
	}
	var succUpdateNetworkPolicy = function(o) {
		eval("var results = " + o.responseText);
		if (results.resultStatus == true) {
			if (results.routingChg == true && 'netWorkPolicy' == accordionView.getOpenedDrawerId()) {
				fetchConfigTemplate2Page(false);
			}
			hideModifyNetworkPolicyPanel();
		} else {
			hm.util.reportFieldError(Get("wlanErrSpanModifyNetworkPolicy"), results.e);
		}
	}
	var failUpdateNetworkPolicy = function(o) {
		//just hide now
		hideModifyNetworkPolicyPanel();
	}
	
	function validateInputs() {
		var errMsg = "";
		errMsg = hm.util.validateString(Get(formName2+"_configName").value, "<s:text name="config.configTemplate.configName" />");
		if (errMsg != null && errMsg != "") {
			hm.util.reportFieldError(Get(formName2+"_configName"), errMsg);
		}
		
		if (Get("npType_selection_tbl") != null && $("table#npType_selection_tbl div.nptype_selected").length < 1) {
			errMsg = "<s:text name='error.network.policy.type.least.one' />";
			hm.util.reportFieldError($("div#npType_selection_tbl_note")[0], errMsg);
		}
		
		if (errMsg != null && errMsg != "") {
			return false;
		}
		return true;
	}
	
	function doSubmitCloneNetworkPolicy() {
		if (!validateInputs()) {
			return;
		}
		
		var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
		document.forms['modifyNetworkPolicyPanelForm'].operation.value = "networkPolicyCloneDone";
		//debug form items
		YAHOO.util.Connect.setForm(document.forms['modifyNetworkPolicyPanelForm']);
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succCloneNetworkPolicy, failure : failCloneNetworkPolicy, timeout: 60000}, null);
	}
	
	var succCloneNetworkPolicy = function(o) {
		eval("var results = " + o.responseText);
		if (results.resultStatus == true) {
			hideModifyNetworkPolicyPanel();
			if (results.cloneId) {
				var url = "<s:url action='configTemplate' includeParams='none' />?operation=edit&id="+results.cloneId+"&ignore="+new Date().getTime();
				location.href = url;
			}
			// then, move to drawer two
		} else {
			hm.util.reportFieldError(Get("wlanErrSpanModifyNetworkPolicy"), results.e);
		}
	}
	var failCloneNetworkPolicy = function(o) {
		//just hide now
		hideModifyNetworkPolicyPanel();
	}
	
	<s:if test="%{blnCloneNetworkPolicy == false}">
	function saveModifyTmpNetworkPolicyArgs() {
		tmpNetworkPolicyModifyArgs = {name: Get(formName2+"_configName").value, desc: Get(formName2+"_description").value, npTypeValue: $("input:checked[type=radio][name=npType]").attr("id")}
	}
	function restoreModifyTmpNetworkPolicyArgs() {
		if (tmpNetworkPolicyModifyArgs && tmpNetworkPolicyModifyArgs.name) {
			Get(formName2+"_configName").value = tmpNetworkPolicyModifyArgs.name;
			if (tmpNetworkPolicyModifyArgs.desc) {
				Get(formName2+"_description").value = tmpNetworkPolicyModifyArgs.desc;
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
		}
		tmpNetworkPolicyModifyArgs = {};
	}
	
	function editHiveProfile_modifyNp() {
		saveModifyTmpNetworkPolicyArgs();
		curNetworkNewOrModifyType = '2';
		if (Get(formName2+"_hiveId") != null) {
			editHiveFromSelectNetworkPolicy_modifyNp(Get(formName2+"_hiveId").value);
		}
	}
	
	function editHiveFromSelectNetworkPolicy_modifyNp(id) {
		var url = "<s:url action='hiveProfiles' includeParams='none' />?jsonMode=true&operation=edit"
					+ "&id="+id
					+ "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditHiveProfiles_modifyNp, failure : resultDoNothing, timeout: 60000}, null);	
	}
	
	var succEditHiveProfiles_modifyNp = function(o) {
		subDrawerOperation='updateHiveProfile';
		openSelectNPSubDrawer({title: "Edit Hive", responseText: o.responseText});
		<s:if test="%{savePermit == true}">
			showHideSelectNetworkPolicySubSaveBT(true);
		</s:if>
		<s:else>
			showHideSelectNetworkPolicySubSaveBT(false);
		</s:else>
		hideModifyNetworkPolicyPanel();
	}
	</s:if>
	
</script>

	<s:form action="networkPolicy" id="modifyNetworkPolicyPanelForm" name="modifyNetworkPolicyPanelForm">
		<s:hidden name="operation" />
		<s:hidden name="networkPolicyId" />
		<s:hidden name="save2Db" />
		<s:hidden name="cloneSrcId" />
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td style="padding-top: 5px; padding-bottom: 2px; padding-right: 20px;">
					<table border="0" cellspacing="0" cellpadding="0" align="right" width="100%">
						<tr>
							<td width="100%">
								<img src="images/hm_v2/profile/HM-icon-network_policy.png" class="dialogTitleImg" />
								<s:if test="%{blnCloneNetworkPolicy == false}">
									<span class="npcHead1"><s:text name="config.title.network.polciy.edit" /></span>
								</s:if>
								<s:else>
									<span class="npcHead1"><s:text name="config.title.network.polciy.clone" /></span>
								</s:else>
							</td>
							<td align="right" style="padding-left:10px;"><a href="javascript:void(0);" class="btCurrent"
							onclick="javascript: hideModifyNetworkPolicyPanel();" title="close"><img class="dinl" width="16px" height="16px" src="images/cancel.png" style="border:none;"/></a></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td><table><tr><td><span id="wlanErrSpanModifyNetworkPolicy"/></span></td></tr></table></td>
					</tr>
					<tr>
						<td style="padding: 8px 5px 8px 5px;">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="130px"><s:text
									name="config.configTemplate.configName" /><font color="red"><s:text name="*" /></font></td>
								<td>
								<s:if test="%{blnCloneNetworkPolicy == false}">
									<s:textfield name="configName" size="32" maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'name');" disabled="true" />
								</s:if>
								<s:else>
									<s:textfield name="configName" size="32" maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'name');"/>
								</s:else>
								&nbsp;<s:text name="config.configTemplate.configName.range" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="130px"><s:text
									name="config.configTemplate.description" /></td>
								<td><s:textfield name="description" size="72"  maxlength="64"
									/> &nbsp;<s:text
									name="config.configTemplate.description.range" /></td>
							</tr>
						</table>
						</td>
					</tr>
				</table>
				</td>
			</tr>
			<s:if test="%{blnCloneNetworkPolicy == false}">
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
			</s:if>
			<tr>
				<td style="padding-top: 2px; padding-bottom: 5px; padding-right: 15px;">
					<table border="0" cellspacing="0" cellpadding="0" align="center" style="margin-top:15px;">
						<tr>
							<s:if test="%{savePermit == true}">
							<s:if test="%{blnCloneNetworkPolicy == false}">
								<td class="npcButton" align="center"><a href="javascript:void(0);" class="btCurrent"
									onclick="javascript: finishModifyNetworkPolicy();" title="<s:text name="config.v2.select.network.policy.popup.button.save"/>"><span class="minWidth"><s:text name="config.v2.select.network.policy.popup.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton" align="center"><a href="javascript:void(0);" class="btCurrent"
									onclick="javascript: doSubmitCloneNetworkPolicy();" title="<s:text name="common.button.clone"/>"><span class="minWidth"><s:text name="common.button.clone"/></span></a></td>
							</s:else>
							</s:if>
							<!--<s:else>
							<td class="npcButton" align="center"></td>
							<td class="npcButton" align="center"><a href="javascript:void(0);" class="btCurrent"
								title="<s:text name="config.v2.select.network.policy.popup.button.save"/>"><span class="minWidth"><s:text name="config.v2.select.network.policy.popup.button.save"/></span></a></td>
							</s:else>
						--></tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>

<s:if test="%{blnCloneNetworkPolicy == false}">
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

	var initTypeSelection = {
		bWireless: '<s:property value="npTypeCtl.wirelessSelectedValue"/>',
		bRouter: '<s:property value="npTypeCtl.routerSelectedValue"/>',
		bSwitch: '<s:property value="npTypeCtl.switchSelectedValue"/>',
		bBonjour: '<s:property value="npTypeCtl.bonjourSelectedValue"/>'
	};
	function checkTypeChanged() {
		var curTypeSelection = {
			bWireless: document.forms[formName2]["npTypeCtl.wirelessSelectedValue"].value,
			bRouter: document.forms[formName2]["npTypeCtl.routerSelectedValue"].value,
			bSwitch: document.forms[formName2]["npTypeCtl.switchSelectedValue"].value,
			bBonjour: document.forms[formName2]["npTypeCtl.bonjourSelectedValue"].value
		};
		var checkPass = true;
		for (var key in initTypeSelection) {
			if (initTypeSelection[key] > curTypeSelection[key]) {
				checkPass = false;
				break;
			}
		}
		
		if(initTypeSelection["bWireless"] == curTypeSelection["bWireless"] && initTypeSelection["bRouter"] < curTypeSelection["bRouter"]){
			checkPass = false;
		}
		
		if (!checkPass) {
			confirmDialog.cfg.setProperty('text', "<html><body>Some profiles may lost because some types of networking support are removed. Do you want to continue?</body></html>");
			confirmDialog.show();
			doNetworkPolicyContinueOper = function() {
				doSubmitModifyNetworkPolicy();
			};
		}
		
		return checkPass;
	};
	
	YAHOO.util.Event.onContentReady('modifyNetworkPolicyPanelForm', restoreModifyTmpNetworkPolicyArgs, this);
</script>
</s:if>
<s:if test="%{blnCloneNetworkPolicy}">
<script type="text/javascript">
	function initializeFocus() {
		if (Get(formName2 + "_configName")) {
			Get(formName2 + "_configName").focus();
		}
	}
	YAHOO.util.Event.onContentReady('modifyNetworkPolicyPanelForm', initializeFocus , this);
</script>
</s:if>