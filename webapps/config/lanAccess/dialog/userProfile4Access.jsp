<%@page import="com.ah.bo.port.PortAccessProfile"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.util.bo.userprofile.selection.UserProfileSelection"%>
<script type="text/javascript">
	var formName2 = "userProfileAccProfileSimpleForm";
	var USERPROFILE_ATTRIBUTE_SPECIFIED = <%=PortAccessProfile.USERPROFILE_ATTRIBUTE_SPECIFIED%>;
	var USERPROFILE_ATTRIBUTE_CUSTOMER = <%=PortAccessProfile.USERPROFILE_ATTRIBUTE_CUSTOMER%>;
	
	function doSave() {
		hm.util.hideFieldError();
		prepareSelectionResult();
		if (!validatePhoneVlan() || !validateInputs()) {
			return;
		}
		if (!document.getElementById("chkUserOnlyId").checked) {
			initHideActionValues();
		}
		var actionTimeDisabled = document.getElementById("actionTime").disabled;
		if (actionTimeDisabled) {
			document.getElementById("actionTime").disabled = false;
			document.getElementById("actionTime").value = 60;
		}
		var inputElement2 = document.getElementById(formName2 + "_assignUserProfileVenderId");
		if(inputElement2.value.length  == 0){
			document.getElementById(formName2 + "_assignUserProfileVenderId").value = 0;
		}
		var url = "<s:url action='portAccess' includeParams='none' />";
		document.forms[formName2].operation.value = "selectedUserProfile4Access";
		YAHOO.util.Connect.setForm(document.forms[formName2]);
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveLanUpJSON, failure : failSaveLanUpJSON, timeout: 60000}, null);
		if (actionTimeDisabled) {
			document.getElementById("actionTime").value = 60;
			document.getElementById("actionTime").disabled = true;
		}
	}
	function validatePhoneVlan() {
		return validateVoiceVlan() && validateDataVlan();
	}
	var succSaveLanUpJSON = function (o) {
		try {
			eval("var details = " + o.responseText);
		}catch(e){
			ErrorShownHelper.showError("Unknown error", "errorNoteShownId");
		}
		if(details.succ) {
			hideUserProfileSelectDialog();
			finishSelectUserProfileForCertainObj();
		} else {
			ErrorShownHelper.showError(details.errMsg, "errorNoteShownId");
			return;
		}
	}
	var failSaveLanUpJSON = function (o) {
		hideUserProfileSelectDialog();
		fetchConfigTemplate2Page(true);
	}
	
	function changeDenyAction(select) {
		if(select.value == 1) {
			document.getElementById("actionTime").disabled = false;
		} else {
			document.getElementById("actionTime").value = 60;
			document.getElementById("actionTime").disabled = true;
		}
	}
	
	function clickOsDectionCheckbox(checked){
		if (checked) {
			Get("hideOsDectionNote").style.display="";
		} else {
			Get("hideOsDectionNote").style.display="none";
		}
	}
	
	function showActionEdit(checked) {
	    var hideAction = document.getElementById("hideAction");
		if (checked){
			hideAction.style.display="block";
			document.getElementById("denyAction").value ="3";
			document.getElementById("actionTime").value ="60";
			document.getElementById("chkDeauthenticate").checked=false;
		} else {
			hideAction.style.display="none";
			document.getElementById("denyAction").value ="3";
			document.getElementById("actionTime").value ="60";
			document.getElementById("chkDeauthenticate").checked=false;
		}
	}
	
	function initHideActionValues() {
		document.getElementById("denyAction").value ="3";
		document.getElementById("actionTime").value ="60";
		document.getElementById("chkDeauthenticate").checked=false;
	}
	
	function createNewUserProfile() {
		succAddLanUserProfileCallBack.setUpType(getUpType());
		succAddLanUserProfileCallBack.setOperate("add");
		networkPolicyCallbackFn = (function(){
			return function() {
				succAddLanUserProfileCallBack.cancelBack(<s:property value="selectedAccessId"/>, <s:property value="configPhoneData"/>, <s:property value="support4LAN"/>);
			}
		})();
		newUserProfiles2Lan(<s:property value="selectedAccessId"/>);
	}
	
	function doCancelUserProfile() {
		hideUserProfileSelectDialog();
		<s:if test="%{addedUserProfileId != null && addedUserProfileId > 0L}">
			fetchConfigTemplate2Page(true);
		</s:if>
	}
	
	function validateInputs() {
		var errMsg = "";
		if (Get("chkUserOnlyId").checked) {
			errMsg = hm.util.validateIntegerRange(Get("actionTime").value, "<s:text name="config.configTemplate.wizard.actionTime" />", 1, 100000000);
			if (errMsg != null && errMsg != "") {
				hm.util.reportFieldError(Get("actionTime"), errMsg);
			}
		}
		
		if (userProfileTabviewSimple != null) {
			if (defaultSelectionTbl != null && userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
				var results = defaultSelectionTbl.getSelectedRowsResult();
	        	if (results != null && results.length < 1) {
	        		errMsg = "Please select one default user profile.";
	        		ErrorShownHelper.showError(errMsg, "errorNoteShownId");
	        		return false;
	        	}
			}
			if (registrationSelectionTbl != null && userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
				var results = registrationSelectionTbl.getSelectedRowsResult();
	        	if (results != null && results.length < 1) {
	        		errMsg = "Please select one registration user profile.";
	        		ErrorShownHelper.showError(errMsg, "errorNoteShownId");
	        		return false;
	        	}
			}
		}
		
		if (errMsg != null && errMsg != "") {
			return false;
		}
		
		if(!validateAssignUserProfile()){
			return false;
		}
		
		return true;
	}
	
	var shownMenuListUp = (function(){
		var _shownMenus = new Array();
		var _showMenuTool = function(elId, elType, blnShown) {
			var toolMenuId = elType + "UpMenu" + elId;
			var profileType = elType;
			if(elType == 'reg') {
				profileType = 'Registration';
			}
			var dialogEl = Get("subTabview"+hm.util.upperCaseInitialLetter(profileType)+"_Content");
			var el = Get(toolMenuId);
			if (el) {
				if (blnShown) {
					el.style.top = "";
					el.style.right = "0";
					el.style.display = "block";
					_shownMenus.push(el);
					if (!blnScrollBarExist) {
						el.style.right = "-25px";
					}
			        // calculte the position after display if scroll some height
		            // handle IE8+ after the meta element "X-UA-Compatible" set as "IE=9"
		            // handle Maxton like browser which has very wired rendering model
		            if(!isNaN(dialogEl.scrollTop)
		                    && (YAHOO.env.ua.opera || YAHOO.env.ua.webkit || YAHOO.env.ua.ie > 7 
		                    		|| (YAHOO.env.ua.ie == 7 && dialogEl.scrollWidth == dialogEl.clientWidth))) {
		                el.style.top = (el.offsetTop - dialogEl.scrollTop - 5) + "px";
		            }
				} else {
					el.style.display = "none";
				}
			}
		};
		var _hideAllMenuTools = function(curElId) {
			var blnCurrMenuTool = false;
			while (_shownMenus.length > 0) {
				var el = _shownMenus.pop();
				if (el) {
					if (curElId && blnCurrMenuTool == false) {
						if(_isCurrentToolMenu(curElId, el.id)) {
							blnCurrMenuTool = true;
						}
					}
					el.style.display = "none";
				}
			}
			return blnCurrMenuTool;
		};
		var _isCurrentToolMenu = function(elId, toolMenuId) {
			if (elId && toolMenuId) {
				if (toolMenuId == "defaultUpMenu"+elId
						|| toolMenuId == "regUpMenu"+elId
						|| toolMenuId == "authUpMenu"+elId
						|| toolMenuId == "authFailUpMenu"+elId
						|| toolMenuId == "guestUpMenu"+elId) { // AirWatch Non-Compliance
					return true;
				}
			}
			return false;
		}
		return {
			showDefaultMenuTool : function(elId, blnOnlyHideAll) {
				var blnCurrMenuTool = _hideAllMenuTools(elId);
				if (blnOnlyHideAll && blnOnlyHideAll == true) {
					return;
				}
				_showMenuTool(elId, "default", !blnCurrMenuTool);
			},
			showSelfRegMenuTool : function(elId, blnOnlyHideAll) {
				var blnCurrMenuTool = _hideAllMenuTools(elId);
				if (blnOnlyHideAll && blnOnlyHideAll == true) {
					return;
				}
				_showMenuTool(elId, "reg", !blnCurrMenuTool);
			},
			showAuthMenuTool : function(elId, blnOnlyHideAll) {
				var blnCurrMenuTool = _hideAllMenuTools(elId);
				if (blnOnlyHideAll && blnOnlyHideAll == true) {
					return;
				}
				_showMenuTool(elId, "auth", !blnCurrMenuTool);
			},
			showAuthFailMenuTool : function(elId, blnOnlyHideAll) {
				var blnCurrMenuTool = _hideAllMenuTools(elId);
				if (blnOnlyHideAll && blnOnlyHideAll == true) {
					return;
				}
				_showMenuTool(elId, "authFail", !blnCurrMenuTool);
			},
			showGuestMenuTool : function(elId, blnOnlyHideAll) { // AirWatch Non-Compliance
				var blnCurrMenuTool = _hideAllMenuTools(elId);
				if (blnOnlyHideAll && blnOnlyHideAll == true) {
					return;
				}
				_showMenuTool(elId, "guest", !blnCurrMenuTool);
			},
			hideAllMenuTools : function() {
				_hideAllMenuTools();
			}
		}
	})();
	function clickDefaultMenuTool(elId, event) {
		shownMenuListUp.showDefaultMenuTool(elId);
		hm.util.stopBubble(event);
	}
	function clickRegMenuTool(elId, event) {
		shownMenuListUp.showSelfRegMenuTool(elId);
		hm.util.stopBubble(event);
	}
	function clickAuthMenuTool(elId, event) {
		shownMenuListUp.showAuthMenuTool(elId);
		hm.util.stopBubble(event);
	}
	function clickAuthFailMenuTool(elId, event) {
		shownMenuListUp.showAuthFailMenuTool(elId);
		hm.util.stopBubble(event);
	}
	function clickGuestMenuTool(elId, event) {
		shownMenuListUp.showGuestMenuTool(elId);
		hm.util.stopBubble(event);
	}
	
	var mouseOverMenuItem = function(menu, event) {
		menu.className = "hover";
		hm.util.stopBubble(event);
	}
	var mouseOutMenuItem = function(menu, event) {
		menu.className = "";
		hm.util.stopBubble(event);
	}
	
	function editUserProfileFromLanUpDlg(upId) {
		succAddLanUserProfileCallBack.setUpType(getUpType());
		succAddLanUserProfileCallBack.setOperate("edit");
		hideUserProfileSelectDialog();
		networkPolicyCallbackFn = (function(){
			return function() {
				succAddLanUserProfileCallBack.cancelBack(<s:property value="selectedAccessId"/>, <s:property value="configPhoneData"/>, <s:property value="support4LAN"/>);
			}
		})();
		editUserProfiles2LanRealOperate(<s:property value="selectedAccessId"/>, upId);
	}
	function cloneUserProfileFromLanUpDlg(upId) {
		succAddLanUserProfileCallBack.setUpType(getUpType());
		succAddLanUserProfileCallBack.setOperate("clone");
		hideUserProfileSelectDialog();
		networkPolicyCallbackFn = (function(){
			return function() {
				succAddLanUserProfileCallBack.cancelBack(<s:property value="selectedAccessId"/>, <s:property value="configPhoneData"/>, <s:property value="support4LAN"/>);
			}
		})();
		cloneUserProfiles2LanRealOperate(<s:property value="selectedAccessId"/>, upId);
	}
	
	function onEnableAssignUserProfile(checked){
		if(checked){
			Get("assignUserProfileTr").style.display = "";
		}else{
			Get("assignUserProfileTr").style.display = "none";
		}
		Get("assignUserProfileAttributeId").disabled = !checked;
		Get(formName2 + "_assignUserProfileVenderId").disabled = !checked;
	}
	
	function validateAttributeContent(){
		var dataStr = "<s:property value='attributeListString'/>";
		var dataSource = eval('[' + dataStr.replace(/\&quot;/g,'"') + ']');
		var inputElement = document.getElementById("assignUserProfileAttributeId");
		for(var i= 0;i<dataSource.length;i++){
			if(inputElement.value == dataSource[i].name){
				return true;
			}
		}
		if(i == dataSource.length){
			var inputElementInfo = document.getElementById("assignUserProfileAttributeIdInfo");
			hm.util.reportFieldError(inputElementInfo, '<s:text name="error.radius.error.input"></s:text>');
			inputElement.focus();
			return false;
		}
	}
	
	function validateAssignUserProfile(){
		if(Get("enableAssignUserProfile").checked){
			if(Get(formName2 + "_userProfileAttributeType"+USERPROFILE_ATTRIBUTE_SPECIFIED).checked){
				var inputElement = document.getElementById("assignUserProfileAttributeId");
				if (inputElement.value.length == 0) {
					var inputElementInfo = document.getElementById("assignUserProfileAttributeIdInfo");
					hm.util.reportFieldError(inputElementInfo, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.assignusrprofile.onradius.attribute" /></s:param></s:text>');
					inputElement.focus();
					return false;
				}
			}else{
				var inputElement = document.getElementById("assignUserProfileAttributeCustomerId");
				if (inputElement.value.length == 0) {
					hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.assignusrprofile.onradius.attribute" /></s:param></s:text>');
					inputElement.focus();
					return false;
				}
				if(inputElement.value.length > 0){
					var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.configTemplate.assignusrprofile.onradius.attribute" />', 1, 255);
					if (message != null) {
						hm.util.reportFieldError(inputElement, message);
						inputElement.focus();
						return false;
					}
				}
				
				var inputElement2 = document.getElementById(formName2 + "_assignUserProfileVenderId");
				if (inputElement2.value.length == 0) {
					hm.util.reportFieldError(inputElement2, '<s:text name="error.requiredField"><s:param><s:text name="config.configTemplate.assignusrprofile.onradius.vender" /></s:param></s:text>');
					inputElement2.focus();
					return false;
				}
				if(inputElement2.value.length > 0){
					var message = hm.util.validateIntegerRange(inputElement2.value, '<s:text name="config.configTemplate.assignusrprofile.onradius.vender" />', 1, 65535);
					if (message != null) {
						hm.util.reportFieldError(inputElement2, message);
						inputElement2.focus();
						return false;
					}
				}
			}
			
			
		}
		return true;
	}
	
	function attributeTypeChanged(radioValue){
		if(USERPROFILE_ATTRIBUTE_SPECIFIED == radioValue.value){
			document.getElementById("attributeSpecifiedTr").style.display="";
			document.getElementById("attributeCustomerTr").style.display="none";
		}else{
			document.getElementById("attributeCustomerTr").style.display="";
			document.getElementById("attributeSpecifiedTr").style.display="none";
		}
	}
	
</script>

<!-- main content -->
<s:form action="portAccess" name="userProfileAccProfileSimpleForm" id="userProfileAccProfileSimpleForm">
<s:hidden name="selectedAccessId" />
<s:hidden name="operation" />
<s:hidden name="selectedVlanId" />
<s:hidden name="selectedDataVlanId" />
<div>
	<div id="userProfileSelect_header">
		<table width="100%">
			<tr>
				<td style="align:left;width:70%">
				<img src="images/hm_v2/profile/hm-icon-users-big.png" width="40px" height="40px" 
					title="<s:text name="config.v2.select.user.profile.popup.title" />" class="dialogTitleImg" />
				<span class="npcHead1" style="padding-left:10px;">
					<s:text name="config.v2.select.user.profile.popup.title" />
				</span>
				</td>
				<td align="right">
					<table cellspacing="0" cellpadding="0" align="center">
						<tr>
							<td class="npcButton">
								<a href="javascript:void(0);" class="btCurrent"
									style="margin-right: 10px;"
									onclick="javascript: doCancelUserProfile();"
									title="<s:text name="config.v2.select.user.profile.popup.cancel"/>">
										<span class="minWidth"> <s:text
												name="config.v2.select.user.profile.popup.cancel" />
									</span>
								</a> 
								<s:if test="%{writeDisabled != 'disabled'}">
										<a href="javascript:void(0);" class="btCurrent"
											style="margin-left: 10px;" onclick="javascript: doSave();"
											title="<s:text name="config.v2.select.user.profile.popup.save"/>"><span
											class="minWidth"><s:text
													name="config.v2.select.user.profile.popup.save" /></span></a>

									</s:if> <s:else>
										<a href="javascript:void(0);" class="btCurrent"
											style="margin-left: 10px;"
											title="<s:text name="config.v2.select.user.profile.popup.save"/>"><span
											class="minWidth"><s:text
													name="config.v2.select.user.profile.popup.save" /></span></a>
									</s:else>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
	<div><span id="errorNoteShownId"></span></div>
	<div id="userProfileSelect_content" style="width:100%;margin-top:5px;">
		<div id="userProfileSelect_content_selection" style="width:100%; height: 200px;">
		<div>
			<div id="userProfileSelectionTabview">
			<table style="width:100%;" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<table style="width:100%;" cellspacing="0" cellpadding="0">	
							<tr>	
							<td style="width:100%;">
							<div style="float:left;">
							<s:if test="%{configPhoneData}">
								<div id="subTabviewDefault" class="sub_tabview_title" style="width:120px;display:<s:property value="defaultUserProfieStype" />"><s:text name="config.v2.select.user.profile.popup.tab.default"/>&nbsp;<span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>
								<div id="subTabviewGuest" class="sub_tabview_title" style="width:120px;display:<s:property value="guestUserProfieStype" />"><s:text name="glasgow_10.config.v2.select.user.profile.popup.tab.guest"/>&nbsp;<span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>
								<s:if test="%{radiusAuthEnabled}">		
								<div id="subTabviewAuth" class="sub_tabview_title" style="width:120px;display:<s:property value="authUserProfieStype" />"><s:text name="config.v2.select.user.profile.popup.tab.authok"/>&nbsp;(Voice)<span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>						
								<div id="subTabviewAuthFail" class="sub_tabview_title" style="width:120px;display:<s:property value="authDataUserProfieStype" />"><s:text name="config.v2.select.user.profile.popup.tab.authok"/>&nbsp;(Data)<span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>
								</s:if>						
							</s:if>
							<s:else>
								<div id="subTabviewDefault" class="sub_tabview_title" style="width:110px;display:<s:property value="defaultUserProfieStype" />"><s:text name="config.v2.select.user.profile.popup.tab.default"/>&nbsp;<span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>		
								<div id="subTabviewRegistration" class="sub_tabview_title" style="width:110px;display:<s:property value="selfRegUserProfieStype" />"><s:text name="config.v2.select.user.profile.popup.tab.reg"/>&nbsp;<span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>
								<div id="subTabviewGuest" class="sub_tabview_title" style="width:110px;display:<s:property value="guestUserProfieStype" />"><s:text name="glasgow_10.config.v2.select.user.profile.popup.tab.guest"/>&nbsp;<span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>	
								<div id="subTabviewAuth" class="sub_tabview_title" style="width:110px;display:<s:property value="authUserProfieStype" />"><s:text name="config.v2.select.user.profile.popup.tab.authok"/>&nbsp;<span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>						
								<div id="subTabviewAuthFail" class="sub_tabview_title" style="width:110px;display:<s:property value="authFailUserProfieStype" />"><s:text name="config.v2.select.user.profile.popup.tab.authfail"/>&nbsp;<span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>						
							</s:else>
							</div>
							<div id="subTabview_content_container" style="float:left;position: relative;">
								<!-- default -->
								<div id="subTabviewDefault_Content" class="sub_tabview_content_hide sub_tabview_content" style="height: 165px;">
								<s:if test="%{upSelection.defaultUserProfileSupport == true}">
									<table id="userProfilesSelectionTblDefault" class="tblSelectContainer" cellspacing="5px" cellpadding="2px">
									<s:iterator value="%{upSelection.defaultUserProfiles}" id="itDef" status="status">
										<s:if test="%{#itDef.checked == true}">
										<tr class="trSelectedCss">
										</s:if>
										<s:else>
										<tr>
										</s:else>
											<td class="selected_table_td_list">
												<input type="checkbox" style='display:none;' name="accUpDefaultIds" value="<s:property value="id"/>"></input>
												<div class="trToolListMenuStyle" style="display: none;" id="defaultUpMenu<s:property value="id"/>">
													<ul>
														<li onclick="editUserProfileFromLanUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Edit <s:property value="value"/>">Edit</li>
														<li onclick="cloneUserProfileFromLanUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Clone <s:property value="value"/>">Clone</li>
													</ul>
												</div>
												<span class="trToolStyle" title="More" onclick="javascript: clickDefaultMenuTool(<s:property value="id"/>, event);">&nbsp;</span>
												<span class="selected_table_td_list_post_content" id="userProfilesSelectionTblDefault_post_<s:property value="id"/>"></span>
												<span class="checkedIcon">&nbsp;</span>
												<span class="word-wrap" title='<s:property value="value"/>'><s:property value="value"/></span>
											</td>
										</tr>
									</s:iterator>
									</table>
								</s:if>
								</div>
								<!-- self registration -->
								<div id="subTabviewRegistration_Content" class="sub_tabview_content_hide sub_tabview_content" style="height: 165px;">
								<s:if test="%{upSelection.selfUserProfileSupport}">
									<table id="userProfilesSelectionTblRegistration" class="tblSelectContainer" cellspacing="5px" cellpadding="2px">
									<s:iterator value="%{upSelection.selfRegUserProfiles}" id="itReg" status="status">
										<s:if test="%{#itReg.checked == true}">
										<tr class="trSelectedCss">
										</s:if>
										<s:else>
										<tr>
										</s:else>
											<td class="selected_table_td_list">
												<input type="checkbox" style='display:none;' name="accUpRegIds" value="<s:property value="id"/>"></input>
												<div class="trToolListMenuStyle" style="display: none;" id="regUpMenu<s:property value="id"/>">
													<ul>
														<li onclick="editUserProfileFromLanUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Edit <s:property value="value"/>">Edit</li>
														<li onclick="cloneUserProfileFromLanUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Clone <s:property value="value"/>">Clone</li>
													</ul>
												</div>
												<span class="trToolStyle" title="More" onclick="javascript: clickRegMenuTool(<s:property value="id"/>, event);">&nbsp;</span>
												<span class="selected_table_td_list_post_content" id="userProfilesSelectionTblRegistration_post_<s:property value="id"/>"></span>
												<span class="checkedIcon">&nbsp;</span>
												<span class="word-wrap" title='<s:property value="value"/>'><s:property value="value"/></span>
											</td>
										</tr>
									</s:iterator>
									</table>
								</s:if>
								</div>
								<!-- guest -->
								<div id="subTabviewGuest_Content" class="sub_tabview_content_hide sub_tabview_content" style="height: 165px;">
								<s:if test="%{upSelection.guestUserProfileSupport}">
									<table id="userProfilesSelectionTblGuest" class="tblSelectContainer" cellspacing="5px" cellpadding="2px">
									<s:iterator value="%{upSelection.guestUserProfiles}" id="itGuest" status="status">
										<s:if test="%{#itGuest.checked == true}">
										<tr class="trSelectedCss">
										</s:if>
										<s:else>
										<tr>
										</s:else>
											<td class="selected_table_td_list">
												<input type="checkbox" style='display:none;' name="accUpGuestIds" value="<s:property value="id"/>"></input>
												<div class="trToolListMenuStyle" style="display: none;" id="guestUpMenu<s:property value="id"/>">
													<ul>
														<li onclick="editUserProfileFromLanUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Edit <s:property value="value"/>">Edit</li>
														<li onclick="cloneUserProfileFromLanUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Clone <s:property value="value"/>">Clone</li>
													</ul>
												</div>
												<span class="trToolStyle" title="More" onclick="javascript: clickGuestMenuTool(<s:property value="id"/>, event);">&nbsp;</span>
												<span class="selected_table_td_list_post_content" id="userProfilesSelectionTblGuest_post_<s:property value="id"/>"></span>
												<span class="checkedIcon">&nbsp;</span>
												<span class="word-wrap" title='<s:property value="value"/>'><s:property value="value"/></span>
											</td>
										</tr>
									</s:iterator>
									</table>
								</s:if>
								</div>
								<!-- auth -->
								<div id="subTabviewAuth_Content" class="sub_tabview_content_hide sub_tabview_content" style="height: 165px;">
								<s:if test="%{upSelection.authUserProfileSupport == true}">
									<table id="userProfilesSelectionTblAuth" class="tblSelectContainer" cellspacing="5px" cellpadding="2px">
									<s:iterator value="%{upSelection.authUserProfiles}" id="itAuth" status="status">
										<s:if test="%{#itAuth.checked == true}">
										<tr class="trSelectedCss">
										</s:if>
										<s:else>
										<tr>
										</s:else>
											<td class="selected_table_td_list">
												<input type="checkbox" style='display:none;' name="accUpAuthIds" value="<s:property value="id"/>"></input>
												<div class="trToolListMenuStyle" style="display: none;" id="authUpMenu<s:property value="id"/>">
													<ul>
														<li onclick="editUserProfileFromLanUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Edit <s:property value="value"/>">Edit</li>
														<li onclick="cloneUserProfileFromLanUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Clone <s:property value="value"/>">Clone</li>
													</ul>
												</div>
												<span class="trToolStyle" title="More" onclick="javascript: clickAuthMenuTool(<s:property value="id"/>, event);">&nbsp;</span>
												<span class="selected_table_td_list_post_content" id="userProfilesSelectionTblAuth_post_<s:property value="id"/>"></span>
												<span class="checkedIcon">&nbsp;</span>
												<span class="word-wrap" title='<s:property value="value"/>'><s:property value="value"/></span>
											</td>
										</tr>
									</s:iterator>
									</table>
								</s:if>
								</div>
								<div id="subTabviewAuthFail_Content" class="sub_tabview_content_hide sub_tabview_content" style="height: 165px;">
								<s:if test="%{(configPhoneData && upSelection.authDataUserProfileSupport) || upSelection.authFailUserProfileSupport}">
									<table id="userProfilesSelectionTblAuthFail" class="tblSelectContainer" cellspacing="5px" cellpadding="2px">
									<s:iterator value="%{configPhoneData? upSelection.authDataUserProfiles : upSelection.authFailUserProfiles}" id="itAuth" status="status">
										<s:if test="%{#itAuth.checked == true}">
										<tr class="trSelectedCss">
										</s:if>
										<s:else>
										<tr>
										</s:else>
											<td class="selected_table_td_list">
												<input type="checkbox" style='display:none;' name="<s:if test='%{configPhoneData}'>accUpAuthDataIds</s:if><s:else>accUpAuthFailIds</s:else>" value="<s:property value="id"/>"></input>
												<div class="trToolListMenuStyle" style="display: none;" id="authFailUpMenu<s:property value="id"/>">
													<ul>
														<li onclick="editUserProfileFromLanUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Edit <s:property value="value"/>">Edit</li>
														<li onclick="cloneUserProfileFromLanUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Clone <s:property value="value"/>">Clone</li>
													</ul>
												</div>
												<span class="trToolStyle" title="More" onclick="javascript: clickAuthFailMenuTool(<s:property value="id"/>, event);">&nbsp;</span>
												<span class="selected_table_td_list_post_content" id="userProfilesSelectionTblAuthFail_post_<s:property value="id"/>"></span>
												<span class="checkedIcon">&nbsp;</span>
												<span class="word-wrap" title='<s:property value="value"/>'><s:property value="value"/></span>
											</td>
										</tr>
									</s:iterator>
									</table>
								</s:if>
								</div>
							</div>
							</td>
							</tr>
							<tr>
								<td>
									<table align="center" cellspacing="0" cellpadding="0" style="padding-left:110px;margin-top: 5px;">
										<tr>
											<s:if test="%{writeDisabled == 'disabled'}">
											<td class="npcButton">
											<a href="javascript:void(0);" class="btCurrent"
													title="<s:text name="config.v2.select.user.profile.popup.create.new"/>"><span class="maxWidth"><s:text name="config.v2.select.user.profile.popup.create.new"/></span></a>
											
											</td>
											</s:if>
											<s:else>
											<td class="npcButton">
											<a href="javascript:void(0);" class="btCurrent"
													onclick="javascript: createNewUserProfile();" title="<s:text name="config.v2.select.user.profile.popup.create.new"/>"><span class="maxWidth"><s:text name="config.v2.select.user.profile.popup.create.new"/></span></a>
											
											</td>
											</s:else>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
					<td style="width:150px;" class="dialogTip">
							<span style="width:100%;" id="upRelatedTipId"></span>
					</td>
				</tr>
			</table>
			</div>
		</div>
		</div>
		<s:if test="%{configPhoneData && !radiusAuthEnabled}">
		<div>
		  <table>
		      <tr>
		          <td class="labelT1" style="width: 80px;padding-left: 25px;">Voice VLAN&nbsp;<label style="color: red">*</label></td>
		          <td>
			          <ah:createOrSelect divId="errorDisplayVoiceVlan" list="vlanList" typeString="VoiceVlan"
			             newFn="newVoiceVlan" editFn="editVoiceVlan"
			             selectIdName="selecteVoiceVlanOpt" inputValueName="selectedVoiceVlanName" swidth="135px" />
		          </td>
		      </tr>
		      <tr>
		          <td class="labelT1" style="width: 80px;padding-left: 25px;">Data VLAN&nbsp;<label style="color: red">*</label></td>
		          <td>
			          <ah:createOrSelect divId="errorDisplayDataVlan" list="vlanList" typeString="DataVlan"
			             newFn="newDataVlan" editFn="editDataVlan"
			             selectIdName="selecteDataVlanOpt" inputValueName="selectedDataVlanName" swidth="135px" />
		          </td>
		      </tr>
		  </table>
		</div>
		</s:if>
		<div id="userProfileSelect_content_setting1" style="width:500px;padding-left:20px;margin-top:15px;">
			<table>
				<tr style="display:<s:property value="osDetectionStyle"/>">
					<td style="font-size:11px;">
					<s:checkbox name="enableOsDection" id="enableOsDectionChkId" onclick="javascript: clickOsDectionCheckbox(this.checked);"></s:checkbox>
					<label for="enableOsDectionChkId" style="font-size: 11px;"><s:text name="config.ssid.enable.os.detection" /></label></td>
				</tr>
				<tr style="display:<s:property value="hideOsDectionNote"/>" id="hideOsDectionNote">
						<td style="padding-left:55px; font-size: 11px;" class="noteInfo" colspan="2">
							<s:text name="config.lan.classification.rule.note"/>
						</td>
				</tr>
				<tr style="display:<s:property value="upSelection.chkUserOnlyStyle"/>">
					<td style="font-size:11px;">
					<s:checkbox name="chkUserOnly" id="chkUserOnlyId" onclick="showActionEdit(this.checked);" ></s:checkbox>
					<label for="chkUserOnlyId"><s:text name="config.configTemplate.wizard.chkOnly.lan.profile" /></label></td>
				</tr>
			</table>
		</div>
		<div id="userProfileSelect_content_setting2" style="width:500px;">
			<div id="hideAction" style="display:<s:property value="hideActionStyle" />">
				<table>
				<tr>
					<td width="70px"></td>
					<td>
						<table>
							<tr>
								<td style="font-size:11px;"><s:text name="config.configTemplate.wizard.denyAction"></s:text></td>
								<td>
									<s:select name="denyAction"	id="denyAction" value="%{denyAction}" list="%{enumDenyAction}" listKey="key"
										listValue="value" cssStyle="width: 108px;" onchange="changeDenyAction(this)"
										title="Action to perform if RADIUS returns an attribute for a user profile that is not selected"/>
								</td>
							</tr>
							<tr>
								<td style="font-size:11px;"><s:text name="config.configTemplate.wizard.actionTime"></s:text>
								</td>
								<td>
									<s:textfield name="actionTime" id="actionTime" maxlength="9" size="15"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										/>
									<s:text name="config.configTemplate.wizard.actionTimeRange"></s:text>
								</td>
							</tr>
							<tr>
								<td style="font-size:11px;" colspan="2"><s:checkbox name="chkDeauthenticate" id="chkDeauthenticate"></s:checkbox><s:text name="config.configTemplate.wizard.chkDeauthenticate.lan.profile"></s:text></td>
							</tr>
						</table>
					</td>
				</tr>
				</table>
			</div>
		</div>
		<div style="width:500px;padding-left:20px; display:<s:property value="%{showAssignUserProfile}" />">
			<table>
				<tr>
					<td style="font-size:11px;">
						<s:checkbox name="enableAssignUserProfile" id="enableAssignUserProfile" value="%{enableAssignUserProfile}" onclick="onEnableAssignUserProfile(this.checked);" />
						<label for="enableAssignUserProfile" style="font-size: 11px;"><s:text name="config.configTemplate.assignusrprofile.onradius" /></label>
					</td>
				</tr>
				<tr id="assignUserProfileTr" style="display:<s:property value="%{assignUserProfileStyle}" />">
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
						    <tr>
								<td colspan="5">
									<table>
										<tr>
											<td style="padding-left: 20px;">
												<s:radio onclick="this.blur();" onchange="attributeTypeChanged(this);" label="Gender" name="userProfileAttributeType" list="%{userProfileAttributeSpecified}" listKey="key" listValue="value"/>
											</td>
											<td >
												<td><s:radio onclick="this.blur();" onchange="attributeTypeChanged(this);" label="Gender" name="userProfileAttributeType" list="%{userProfileAttributeCustomer}" listKey="key" listValue="value"/>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
		        		 		<td style="font-size:11px;padding-left:65px;height:1px;" colspan="5">
		        		 			<table border="0" cellspacing="0" cellpadding="0">
		        		 				<tr>
		        		 					<td id="assignUserProfileAttributeIdInfo" colspan="5"></td>
		        		 				</tr>
		        		 			</table>
		        		 		</td>
						    </tr>
							<tr id="attributeSpecifiedTr" style="display:<s:property value="attributeSpecifiedStyle"/>">
								<td  style="font-size:11px;padding-left:28px;">
									<label>
										<s:text name="config.configTemplate.assignusrprofile.onradius.attribute" /><font color="red">*</font>
									</label>
								</td>
								<td colspan="4" id="attributeSpecifiedTd">
									 <div style="width:170px;">
						        		<div style="z-index: 90" id="attributeDiv">
						        		 <table>
						        		 	<tr>
						        		 		<td><s:textfield id="assignUserProfileAttributeId"  name="assignUserProfileAttributeIdShow" disabled="%{!enableAssignUserProfile}"
							                    maxlength="3" size="12" cssStyle="height:12px; width:12.1em;" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
						        		 		<td><a id="attributeComboBox" tabindex="-1" href="javascript:void(0);" class="acDropdown"></a></td>
						        		 	</tr>
						        		 </table>
						        			<div id="attributeContainer"></div>
						        		</div>
						        	</div>
								</td>
							</tr>
							<tr id="attributeCustomerTr" style="display:<s:property value="attributeCustomerStyle"/>">
								<td style="font-size:11px;padding-left:28px; padding-right:5px;">
									<label>
										<s:text name="config.configTemplate.assignusrprofile.onradius.attribute" /><font color="red"><s:text name="hm.common.required"/></font>
									</label>
								</td>
								<td >
						        	<s:textfield id="assignUserProfileAttributeCustomerId" name="assignUserProfileAttributeCustomerId"  value="%{assignUserProfileAttributeCustomerIdStr}"
							                    maxlength="3" size="12" cssStyle="height:12px;"  onkeypress="return hm.util.keyPressPermit(event,'ten');" />
							        <s:text name="config.configTemplate.assignusrprofile.onradius.attribute.range"/>
								</td>
								<td width="10px"></td>
								<td style="font-size:11px;padding-left:20px;padding-right:5px;">
									<label>
										<s:text name="config.configTemplate.assignusrprofile.onradius.vender" /><font color="red"><s:text name="hm.common.required"/></font>
									</label>
								</td>
								<td >
									<s:textfield name="assignUserProfileVenderId" size="12" cssStyle="height:12px;" maxlength="5" disabled="%{!enableAssignUserProfile}"
										 onkeypress="return hm.util.keyPressPermit(event,'ten');" value="%{assignUserProfileVenderIdStr}" />
									<s:text name="config.configTemplate.assignusrprofile.onradius.vender.range"/>
								</td>
							</tr>
							<tr>
								<td height="10px"></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<div id="userProfileSelect_footer"></div>
</div>
</s:form>

<script type="text/javascript">

	var upType_default = <%= UserProfileSelection.USER_PROFILE_TYPE_DEFAULT %>;
	var upType_reg = <%= UserProfileSelection.USER_PROFILE_TYPE_REGISTRATION %>;
	var upType_auth = <%= UserProfileSelection.USER_PROFILE_TYPE_AUTH %>;
	var upType_auth_fail = <%= UserProfileSelection.USER_PROFILE_TYPE_AUTH_FAIL %>;
	var upType_guest = <%= UserProfileSelection.USER_PROFILE_TYPE_GUEST %>; // AirWatch Non-Compliance

	var SelectedTable = YAHOO.aerohive.widget.SelectedTable;
	var TabviewSimple = YAHOO.aerohive.widget.TabviewSimple;
	var defaultSelectionTbl = null;
	var registrationSelectionTbl = null;
	var authSelectionTbl = null;
	var authFailSelectionTbl = null;
	var guestSelectionTbl = null; // AirWatch Non-Compliance
	var phoneDataFlag = <s:property value="configPhoneData"/>;
	function initializeSelectionTbls() {
		<s:if test="%{upSelection.defaultUserProfileSupport == true}">
		defaultSelectionTbl = new SelectedTable("userProfilesSelectionTblDefault",
	            {    id: "userProfilesSelectionTblDefault",
	                 idValue: "accUpDefaultIds",
	                 overflowDiv: "subTabviewDefault_Content",
	                 auxiliaryContent: "userProfilesSelectionTblDefault_post_",//prefix, end with unique id
	                 minSelect: 1,
	                 rowafterselected: afterDefaultUpSelect,
	                 rowafterdeselected: afterDefaultUpDeSelect,
	                 multiSelect: false});
		</s:if>
		<s:if test="%{upSelection.selfUserProfileSupport}">
		registrationSelectionTbl = new SelectedTable("userProfilesSelectionTblRegistration",
	            {    id: "userProfilesSelectionTblRegistration",
	                 idValue: "accUpRegIds",
	                 overflowDiv: "subTabviewRegistration_Content",
	                 auxiliaryContent: "userProfilesSelectionTblRegistration_post_",//prefix, end with unique id
	                 minSelect: 1,
	                 rowafterselected: afterRegUpSelect,
	                 rowafterdeselected: afterRegUpDeSelect,
	                 multiSelect: false});
		</s:if>
		<s:if test="%{upSelection.guestUserProfileSupport}">
		guestSelectionTbl = new SelectedTable("userProfilesSelectionTblGuest",
	            {    id: "userProfilesSelectionTblGuest",
	                 idValue: "accUpGuestIds",
	                 overflowDiv: "subTabviewGuest_Content",
	                 auxiliaryContent: "userProfilesSelectionTblGuest_post_",//prefix, end with unique id
	                 minSelect: 1,
	                 rowafterselected: afterGuestUpSelect,
	                 rowafterdeselected: afterGuestUpDeSelect,
	                 multiSelect: false});
		</s:if>
		<s:if test="%{upSelection.authUserProfileSupport == true}">
		authSelectionTbl = new SelectedTable("userProfilesSelectionTblAuth",
	            {    id: "userProfilesSelectionTblAuth",
	                 idValue: "accUpAuthIds",
	                 overflowDiv: "subTabviewAuth_Content",
	                 auxiliaryContent: "userProfilesSelectionTblAuth_post_",//prefix, end with unique id
	                 //rowbeforeselect: beforeAuthTblSelect,
	                 showDisabledType: "disabled",
	                 rowafterselected: afterAuthUpSelect,
	                 rowafterdeselected: afterAuthUpDeSelect,
	                 multiSelect: true});
		</s:if>
		<s:if test="%{(configPhoneData && upSelection.authDataUserProfileSupport) || upSelection.authFailUserProfileSupport}">
		authFailSelectionTbl = new SelectedTable("userProfilesSelectionTblAuthFail",
	            {    id: "userProfilesSelectionTblAuthFail",
	                 idValue: phoneDataFlag ? "accUpAuthDataIds" : "accUpAuthFailIds",
	                 overflowDiv: "subTabviewAuthFail_Content",
	                 auxiliaryContent: "userProfilesSelectionTblAuthFail_post_",//prefix, end with unique id
	                 //rowbeforeselect: beforeAuthFailTblSelect,
	                 showDisabledType: "disabled",
	                 rowafterselected: afterAuthFailUpSelect,
	                 rowafterdeselected: afterAuthFailUpDeSelect,
	                 multiSelect: <s:if test="%{configPhoneData}">true</s:if><s:else>false</s:else>});
		</s:if>
	}
	var hideAllToolMenus = function() {
		shownMenuListUp.hideAllMenuTools();
	}
	var afterDefaultUpSelect = function(arg) {
		hideAllToolMenus();
	}
	var afterDefaultUpDeSelect = function(arg) {
		hideAllToolMenus();
	}
	var afterRegUpSelect = function(arg) {
		hideAllToolMenus();
	}
	var afterRegUpDeSelect = function(arg) {
		hideAllToolMenus();
	}
	var afterAuthUpSelect = function(arg) {
		hideAllToolMenus();
	}
    var afterAuthUpDeSelect = function(arg) {
        hideAllToolMenus();
    }	
	var afterAuthFailUpSelect = function(arg) {
		hideAllToolMenus();
	}
	var afterAuthFailUpDeSelect = function(arg) {
		hideAllToolMenus();
	}
	var afterGuestUpSelect = function(arg) {
		hideAllToolMenus();
	}
	var afterGuestUpDeSelect = function(arg) {
		hideAllToolMenus();
	}
	
	var beforeAuthTblSelect = function(arg) {
		if (authSelectionTbl != null) {
			if (authSelectionTbl.getSelectedRowsCount() >= 62) {
				warnDialog.cfg.setProperty('text', "Select 62 user profiles at most.");
				warnDialog.show();
				return false;
			}
			return true;
		}
		return false;
	}

	
	var userProfileTabviewSimple = null;
	function initUserProfileTabviewSimple2() {
		var tabViews = [];
		<s:if test="%{upSelection.defaultUserProfileSupport}">
		tabViews.push({
            id: "subTabviewDefault",
            contentDiv: "subTabviewDefault_Content",
            opened: (<s:property value="upSelection.userProfileSubTabId"/> == null || <s:property value="upSelection.userProfileSubTabId"/> == -1 || <s:property value="upSelection.userProfileSubTabId"/> == upType_default) ? true : false,
            getContentResult: getContentResultDefault,
            afterOpened: showTipOfDefault,
            beforeOpen: disableOtherSelectedItemsDefault
        });
		</s:if>
        <s:if test="%{upSelection.selfUserProfileSupport}">
        tabViews.push({
            id: "subTabviewRegistration",
            contentDiv: "subTabviewRegistration_Content",
            opened: <s:property value="upSelection.userProfileSubTabId"/> == upType_reg ? true : false,
            getContentResult: getContentResultRegistration,
            afterOpened: showTipOfReg,
            beforeOpen: disableOtherSelectedItemsRegistration
        });
        </s:if>
        <s:if test="%{upSelection.guestUserProfileSupport}">
        tabViews.push({
            id: "subTabviewGuest",
            contentDiv: "subTabviewGuest_Content",
            opened: <s:property value="upSelection.userProfileSubTabId"/> == upType_guest ? true : false,
            getContentResult: getContentResultGuest,
            afterOpened: showTipOfGuest,
            beforeOpen: disableOtherSelectedItemsGuest
        });
        </s:if>
        <s:if test="%{upSelection.authUserProfileSupport == true}">
        tabViews.push({
            id: "subTabviewAuth",
            contentDiv: "subTabviewAuth_Content",
            opened: <s:property value="upSelection.userProfileSubTabId"/> == upType_auth ? true : false,
            getContentResult: getContentResultAuth,
            afterOpened: showTipOfAuth,
            beforeOpen: disableOtherSelectedItemsAuth
        });
        </s:if>
        <s:if test="%{(configPhoneData && upSelection.authDataUserProfileSupport) || upSelection.authFailUserProfileSupport}">
        tabViews.push({
            id: "subTabviewAuthFail",
            contentDiv: "subTabviewAuthFail_Content",
            opened: <s:property value="upSelection.userProfileSubTabId"/> == upType_auth_fail ? true : false,
            getContentResult: getContentResultAuthFail,
            afterOpened: showTipOfAuthFail,
            beforeOpen: disableOtherSelectedItemsAuthFail
        });
        </s:if>
		userProfileTabviewSimple = new TabviewSimple("userProfileSelectionTabview",
	           {	id : "userProfileSelectionTabview",
	              	disabledType: "hide",
	              	subTabviews : tabViews
	           });
	}

	var getContentResultDefault = function(arg) {
	    if (defaultSelectionTbl != null) {
	          return defaultSelectionTbl.getSelectedRowsResult();
	    }
	    return null;
	}
	var getContentResultRegistration = function(arg) {
	    if (registrationSelectionTbl != null) {
	          return registrationSelectionTbl.getSelectedRowsResult();
	    }
	    return null;
	}
	var getContentResultAuth = function(arg) {
	    if (authSelectionTbl != null) {
	          return authSelectionTbl.getSelectedRowsResult();
	    }
	    return null;
	}
	var getContentResultAuthFail = function(arg) {
	    if (authFailSelectionTbl != null) {
	          return authFailSelectionTbl.getSelectedRowsResult();
	    }
	    return null;
	}
	var getContentResultGuest = function(arg) {
	    if (guestSelectionTbl != null) {
	          return guestSelectionTbl.getSelectedRowsResult();
	    }
	    return null;
	}
	
	var showTipOfDefault = function(arg) {
		Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfDefault'/>";
	}
	
	var showTipOfReg = function(arg) {
		<s:if test="%{configPhoneData}">
		Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfDefaultData'/>";
		</s:if>
		<s:else>
		Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfReg'/>";
		</s:else>
	}
	
	var showTipOfAuth = function(arg) {
		Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfAuth'/>";
	}
	
	var showTipOfAuthFail = function(arg) {
		<s:if test="%{configPhoneData}">
		Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfAuthData'/>";
		</s:if>
		<s:else>
		Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfAuthFail'/>";
		</s:else>
	}
	
	var showTipOfGuest = function(arg) {
		Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfGuest'/>";
	}
	
	var labels = ["(default)", "(self-reg)", "(guest)", "(auth-ok)", "(auth-fail)"], 
	    phoneDataLabels = ["(default voice)", "(default data)", "(guest)", "(auth-vioce)", "(auth-data)"];
	var getLabel = function(index) {
		<s:if test="%{configPhoneData}">
		if(index == 0) {
			<s:if test="%{radiusAuthEnabled}">
			return labels[index];
			</s:if>
		}
		return phoneDataLabels[index];
		</s:if>
		<s:else>
		return labels[index];
		</s:else>
	};
	var disableOtherSelectedItemsAuthFail = function(arg) {
	    if (userProfileTabviewSimple != null 
	    		&& authFailSelectionTbl != null
	    		&& userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuthFail") == false) {
	    	authFailSelectionTbl.removeAllDisabledRowStyle();
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
		       	authFailSelectionTbl.disableRowsWithAuxiliary(getContentResultDefault(null), getLabel(0));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
		        authFailSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultRegistration(null), getLabel(1));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewGuest") == false) {
	    		authFailSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultGuest(null), getLabel(2));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuth") == false) {
		        authFailSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultAuth(null), getLabel(3));
	    	}
	    }
	    return true;
	}
	
	var disableOtherSelectedItemsAuth = function(arg) {
	    if (userProfileTabviewSimple != null 
	    		&& authSelectionTbl != null
	    		&& userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuth") == false) {
	    	authSelectionTbl.removeAllDisabledRowStyle();
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuthFail") == false) {
		       	authSelectionTbl.disableRowsWithAuxiliary(getContentResultAuthFail(null), getLabel(4));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
		       	authSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultDefault(null), getLabel(0));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
		        authSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultRegistration(null), getLabel(1));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewGuest") == false) {
	    		authSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultGuest(null), getLabel(2));
            }
	    }
	    return true;
	}
	
	var disableOtherSelectedItemsDefault = function(arg) {
	    if (userProfileTabviewSimple != null 
	    		&& defaultSelectionTbl != null
	    		&& userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
	    	defaultSelectionTbl.removeAllDisabledRowStyle();
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuthFail") == false) {
		       	defaultSelectionTbl.disableRowsWithAuxiliary(getContentResultAuthFail(null), getLabel(4));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuth") == false) {
		       	defaultSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultAuth(null), getLabel(3));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
		        defaultSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultRegistration(null), getLabel(1));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewGuest") == false) {
	    		defaultSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultGuest(null), getLabel(2));
            }
	    }
	    return true;
	}
	
	var disableOtherSelectedItemsRegistration = function(arg) {
	    if (userProfileTabviewSimple != null 
	    		&& registrationSelectionTbl != null
	    		&& userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
	    	registrationSelectionTbl.removeAllDisabledRowStyle();
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuthFail") == false) {
		        registrationSelectionTbl.disableRowsWithAuxiliary(getContentResultAuthFail(null), getLabel(4));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuth") == false) {
		        registrationSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultAuth(null), getLabel(3));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
		        registrationSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultDefault(null), getLabel(0));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewGuest") == false) {
	    		registrationSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultGuest(null), getLabel(2));
            }
	    }
	    return true;
	}
	
	var disableOtherSelectedItemsGuest = function(arg) {
	    if (userProfileTabviewSimple != null 
	    		&& guestSelectionTbl != null
	    		&& userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewGuest") == false) {
	    	guestSelectionTbl.removeAllDisabledRowStyle();
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuthFail") == false) {
		        guestSelectionTbl.disableRowsWithAuxiliary(getContentResultAuthFail(null), getLabel(4));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuth") == false) {
		        guestSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultAuth(null), getLabel(3));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
		        guestSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultDefault(null), getLabel(0));
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
                guestSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultRegistration(null), getLabel(1));
            }
	    }
	    return true;
	}
	
	function initUserProfileSubTabviews() {
		<s:if test="%{!((configPhoneData && upSelection.authDataUserProfileSupport) || upSelection.authFailUserProfileSupport)}">
			if (userProfileTabviewSimple != null) {
				userProfileTabviewSimple.disableSubTabviews('subTabviewAuthFail');
			}
		</s:if>
		<s:if test="%{upSelection.authUserProfileSupport == false}">
			if (userProfileTabviewSimple != null) {
				userProfileTabviewSimple.disableSubTabviews('subTabviewAuth');
			}
		</s:if>
		<s:if test="%{upSelection.defaultUserProfileSupport == false}">
			if (userProfileTabviewSimple != null) {
				userProfileTabviewSimple.disableSubTabviews('subTabviewDefault');
			}
		</s:if>
		<s:if test="%{!(upSelection.selfUserProfileSupport)}">
			if (userProfileTabviewSimple != null) {
				userProfileTabviewSimple.disableSubTabviews('subTabviewRegistration');
			}
		</s:if>
		<s:if test="%{!(upSelection.guestUserProfileSupport)}">
			if (userProfileTabviewSimple != null) {
				userProfileTabviewSimple.disableSubTabviews('subTabviewGuest');
			}
		</s:if>
	}
	
	function getUpType() {
		if (userProfileTabviewSimple != null) {
			return getCertainUpType(userProfileTabviewSimple.getOpenedSubTabview());
		}
	}
	
	function getCertainUpType(subTabview) {
		if ('subTabviewRegistration' == subTabview) {
			return upType_reg;
		} else if ('subTabviewAuth' == subTabview) {
			return upType_auth;
		} else if ('subTabviewAuthFail' == subTabview) {
			return upType_auth_fail;
		} else if ('subTabviewGuest' == subTabview) {
            return upType_guest;
        }
		return upType_default;
	}
	
	function prepareSelectionResult() {
		if (userProfileTabviewSimple != null) {
			if (defaultSelectionTbl != null) {
				defaultSelectionTbl.prepareCheckItems();
			}
			if (registrationSelectionTbl != null) {
				registrationSelectionTbl.prepareCheckItems();
			}
			if (authSelectionTbl != null) {
				authSelectionTbl.prepareCheckItems();
			}
			if (authFailSelectionTbl != null) {
				authFailSelectionTbl.prepareCheckItems();
			}
			if (guestSelectionTbl != null) {
				guestSelectionTbl.prepareCheckItems();
			}
 		}
	}
	
	var addedUPId = '<s:property value="addedUserProfileId" />';
	function scrollToSelectedItems() {
		if ((addedUPId == null || addedUPId == '') && userProfileTabviewSimple != null) {
			if (defaultSelectionTbl != null && userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
				var results = defaultSelectionTbl.getSelectedRowsResult();
	        	if (results != null && results.length > 0) {
	        		defaultSelectionTbl.startScrollToTr(results[0]);
	        	}
			}
			if (registrationSelectionTbl != null && userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
				var results = registrationSelectionTbl.getSelectedRowsResult();
	        	if (results != null && results.length > 0) {
	        		registrationSelectionTbl.startScrollToTr(results[0]);
	        	}
			}
			if (guestSelectionTbl != null && userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewGuest") == false) {
				var results = guestSelectionTbl.getSelectedRowsResult();
	        	if (results != null && results.length > 0) {
	        		guestSelectionTbl.startScrollToTr(results[0]);
	        	}
			}
			if (authSelectionTbl != null && userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuth") == false) {
				var results = authSelectionTbl.getSelectedRowsResult();
	        	if (results != null && results.length > 0) {
	        		authSelectionTbl.startScrollToTr(results[0]);
	        	}
			}
			if (authFailSelectionTbl != null && userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuthFail") == false) {
				var results = authFailSelectionTbl.getSelectedRowsResult();
	        	if (results != null && results.length > 0) {
	        		authFailSelectionTbl.startScrollToTr(results[0]);
	        	}
			}
		}
		else if (userProfileTabviewSimple != null) {
			var curOpenedSubTabview = userProfileTabviewSimple.getOpenedSubTabview();
			if ("subTabviewDefault" == curOpenedSubTabview && defaultSelectionTbl != null) {
				defaultSelectionTbl.startScrollToTr(addedUPId);
			} else if ("subTabviewRegistration" == curOpenedSubTabview && registrationSelectionTbl != null) {
				registrationSelectionTbl.startScrollToTr(addedUPId);
			} else if ("subTabviewGuest" == curOpenedSubTabview && guestSelectionTbl != null) {
                guestSelectionTbl.startScrollToTr(addedUPId);
            }  else if ("subTabviewAuth" == curOpenedSubTabview && authSelectionTbl != null) {
				authSelectionTbl.startScrollToTr(addedUPId);
			} else if ("subTabviewAuthFail" == curOpenedSubTabview && authFailSelectionTbl != null) {
                authFailSelectionTbl.startScrollToTr(addedUPId);
            }
		}
	} 
	
	function initDisabledItemsCheck() {
		var curOpenedSubTabview = userProfileTabviewSimple.getOpenedSubTabview();
		if ("subTabviewDefault" == curOpenedSubTabview && defaultSelectionTbl != null) {
			disableOtherSelectedItemsDefault(null);
		} else if ("subTabviewRegistration" == curOpenedSubTabview && registrationSelectionTbl != null) {
			disableOtherSelectedItemsRegistration(null);
		} else if ("subTabviewGuest" == curOpenedSubTabview && guestSelectionTbl != null) {
            disableOtherSelectedItemsGuest(null);
        }  else if ("subTabviewAuth" == curOpenedSubTabview && authSelectionTbl != null) {
			disableOtherSelectedItemsAuth(null);
		} else if ("subTabviewAuthFail" == curOpenedSubTabview && authFailSelectionTbl != null) {
            disableOtherSelectedItemsAuthFail(null);
        }
	}
	
	function adjustInitTipShow() {
		var curUpType = getUpType();
		if (curUpType == upType_reg) {
			<s:if test="%{configPhoneData}">
	        Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfDefaultData'/>";
	        </s:if>
	        <s:else>
	        Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfReg'/>";
	        </s:else>
		} else if (curUpType == upType_auth) {
			Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfAuth'/>";
		} else if (curUpType == upType_guest) {
            Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfGuest'/>";
        } else if (curUpType == upType_auth_fail) {
			<s:if test="%{configPhoneData}">
	        Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfAuthData'/>";
	        </s:if>
	        <s:else>
	        Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfAuthFail'/>";
	        </s:else>
        } else {
			Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfDefault'/>";
		}
	}
	
	function initializeTabview4SelectionDelay() {
		//action settings
		prepareActionSettings();
		
		initializeSelectionTbls();
		initUserProfileTabviewSimple2();
		//initUserProfileSubTabviews();
		adjustInitTipShow();
		initDisabledItemsCheck();
		scrollToSelectedItems();
		
		adjustTableWidthForIE();
		initAutoCompleteAttributeComboBox();
	}
	
	function prepareActionSettings() {
		var actionEl = document.getElementById("denyAction");
		if (actionEl) {
			changeDenyAction(actionEl);
		}
	}
	
	var blnScrollBarExist = false;
	
	function adjustTableWidthForIE() {
		var curUpType = getUpType();
		var defaultTbl = Get("subTabviewDefault_Content");
		if (curUpType == upType_reg) {
			defaultTbl = Get("subTabviewRegistration_Content");
		} else if (curUpType == upType_auth) {
			defaultTbl = Get("subTabviewAuth_Content");
		} else if (curUpType == upType_auth_fail) {
            defaultTbl = Get("subTabviewAuthFail_Content");
        } else if (curUpType == upType_guest) {
            defaultTbl = Get("subTabviewGuest_Content");
        }
		
		if(defaultTbl.scrollHeight <= defaultTbl.clientHeight
				|| defaultTbl.offsetHeight <= defaultTbl.clientHeight) {
			if (YAHOO.env.ua.ie) {
				if (Get("userProfilesSelectionTblDefault")) {
					Get("userProfilesSelectionTblDefault").style.width = "100%";
				}
				if (Get("userProfilesSelectionTblRegistration")) {
					Get("userProfilesSelectionTblRegistration").style.width = "100%";
				}
				if (Get("userProfilesSelectionTblAuth")) {
					Get("userProfilesSelectionTblAuth").style.width = "100%";
				}
				if (Get("userProfilesSelectionTblAuthFail")) {
					Get("userProfilesSelectionTblAuthFail").style.width = "100%";
				}
				if (Get("userProfilesSelectionTblGuest")) {
					Get("userProfilesSelectionTblGuest").style.width = "100%";
				}
			}
			blnScrollBarExist = false;
		} else {
			blnScrollBarExist = true;
		}
	}
	
	var initializeTabview4Selection = function(arg) {
		var timeoutId1 = setTimeout("initializeTabview4SelectionDelay()", 100);
	}
	YAHOO.util.Event.onContentReady("userProfileSelectionTabview", initializeTabview4Selection, this);

	var initAutoCompleteAttributeComboBox = function (){
		// init DataSource
		var dataStr = "<s:property value='attributeListString'/>";
		var dataSource = eval('[' + dataStr.replace(/\&quot;/g,'"') + ']');
		
		var attributeDataSource = new YAHOO.util.LocalDataSource(dataSource);
		attributeDataSource.responseSchema = {fields :["name" , "id"]};
		
		// AutoComplete BomboBox primary RADIUS server constructor
		var acComboBox = autoCompelteComboBox('assignUserProfileAttributeId', 'attributeContainer', 'attributeComboBox', attributeDataSource, dataSource.length,null,null,70);
		acComboBox.oAC.resultTypeList = false;
	};
	
	// for voice VLAN
	function newPhoneDataVlan(selectedElId) {
	    var url = "<s:url action='vlan' includeParams='none' />?operation=new&jsonMode=true"
            + "&contentShowType=dlg&parentDomID=" + selectedElId
            + "&ignore="+new Date().getTime();
	    openIFrameDialog(800, 400, url);
	}
	function editPhoneDataVlan(selectedElId) {
	    var selectedValue = hm.util.validateListSelection(selectedElId);
	    if(selectedValue < 0){
	        return;
	    }
	    var url = "<s:url action='vlan' includeParams='none' />?operation=edit&jsonMode=true"
	            + "&id="+selectedValue+"&contentShowType=dlg"
	            + "&parentDomID="+selectedElId
	            + "&ignore="+new Date().getTime();
	    openIFrameDialog(800, 400, url);
	}
	function validatePhoneDataVlan(selectedElId, inputElId, errorElId, text, selectedVlanId) {
	    var vlannames = Get(selectedElId);
	    var vlanValue = Get(inputElId);
	    if(vlannames && vlanValue) {
	    	if ("" == vlanValue.value) {
	    		hm.util.reportFieldError(Get(errorElId), 
	    				'<s:text name="error.config.network.object.input.direct"><s:param>'+ text +'</s:param></s:text>');
	    		vlanValue.focus();
	    		return false;
		     }
		    if (!hm.util.hasSelectedOptionSameValue(vlannames,vlanValue)) {
		        var message = hm.util.validateIntegerRange(vlanValue.value, text, 1, 4094);
		        if (message != null) {
		            hm.util.reportFieldError(Get(errorElId), message);
		            vlanValue.focus();
		            return false;
		        }
		        document.forms[formName2][selectedVlanId].value = -1;
		    } else {
		        document.forms[formName2][selectedVlanId].value = vlannames.options[vlannames.selectedIndex].value;
		    }
	    }
	    return true;
	}
	function newVoiceVlan() {
		newPhoneDataVlan("selecteVoiceVlanOpt");
	}
	function editVoiceVlan() {
		editPhoneDataVlan("selecteVoiceVlanOpt");
	}
	function validateVoiceVlan() {
		return validatePhoneDataVlan("selecteVoiceVlanOpt", "selectedVoiceVlanName", "errorDisplayVoiceVlan", "Voice VLAN", "selectedVlanId");
	}
	// for data VLAN
	function newDataVlan() {
		newPhoneDataVlan("selecteDataVlanOpt");
	}
	function editDataVlan() {
		editPhoneDataVlan("selecteDataVlanOpt");
	}
	function validateDataVlan() {
		return validatePhoneDataVlan("selecteDataVlanOpt", "selectedDataVlanName", "errorDisplayDataVlan", "Data VLAN", "selectedDataVlanId");
	}
</script>