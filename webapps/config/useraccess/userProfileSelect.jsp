<%@page import="com.ah.bo.wlan.SsidProfile"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.util.bo.userprofile.selection.UserProfileSelection"%>
<script type="text/javascript">
	var USERPROFILE_ATTRIBUTE_SPECIFIED = <%=SsidProfile.USERPROFILE_ATTRIBUTE_SPECIFIED%>;
	var USERPROFILE_ATTRIBUTE_CUSTOMER = <%=SsidProfile.USERPROFILE_ATTRIBUTE_CUSTOMER%>;
	
	var formName2 = "userProfileSsidProfileSimpleForm";
	
	function doSave() {
		hm.util.hideFieldError();
		var selections = getSelectionResult();
		if (!validateInputs()) {
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
		var url = "<s:url action='ssidProfilesSimple' includeParams='none' />";
		document.forms['userProfileSsidProfileSimpleForm'].operation.value = "save";
		document.forms['userProfileSsidProfileSimpleForm'].userProfileSelectionStr.value = selections;
		YAHOO.util.Connect.setForm(document.forms['userProfileSsidProfileSimpleForm']);
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveSsidJSON, failure : failSaveSsidJSON, timeout: 60000}, null);
		if (actionTimeDisabled) {
			document.getElementById("actionTime").value = 60;
			document.getElementById("actionTime").disabled = true;
		}
	}
	var succSaveSsidJSON = function (o) {
		try {
			eval("var details = " + o.responseText);
		}catch(e){
			ErrorShownHelper.showError("Unknown error", "errorNoteShownId");
		}
		if (details.resultStatus == false) {
			ErrorShownHelper.showError(details.errMsg, "errorNoteShownId");
			return;
		}
		hideUserProfileSelectDialog();
		finishSelectUserProfileForCertainObj();
		//fetchConfigTemplate2Page(true);
	}
	var failSaveSsidJSON = function (o) {
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
		}
		if (!checked){
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
		var userProfileSubTabId = '';
		var args = null;
		if (userProfileTabviewSimple != null) {
			userProfileSubTabId = userProfileTabviewSimple.getOpenedSubTabview();
			args = {upType: getUpType()};
		}
		networkPolicyCallbackFn = (function(){
			return function() {
				succAddSsidUserProfileCallBack.cancelBack(<s:property value="ssidId"/>);
			}
		})();
		newUserProfiles2(<s:property value="ssidId" />, userProfileSubTabId, args);
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
	
	//function saveTempUserProfileSelection() {
	//	selectedUserProfileSsidTmp = {
	//			defaultTblData: defaultSelectionTbl.getSelectedRowsResult(),
	//			registrationTblData: registrationSelectionTbl.getSelectedRowsResult(),
	//			autnTblData: authSelectionTbl.getSelectedRowsResult()
	//	};
	//}
	
	//function restoreTempUserProfileSelection() {
	//
	//}
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
	
	function editUserProfileFromSsidUpDlg(upId) {
		succAddSsidUserProfileCallBack.setUpType(getUpType());
		succAddSsidUserProfileCallBack.setOperate("edit");
		if (userProfileTabviewSimple != null) {
			succAddSsidUserProfileCallBack.setUpTabId(userProfileTabviewSimple.getOpenedSubTabview());
		}
		hideUserProfileSelectDialog();
		networkPolicyCallbackFn = (function(){
			return function() {
				succAddSsidUserProfileCallBack.cancelBack(<s:property value="ssidId"/>);
			}
		})();
		editUserProfiles2RealOperate(<s:property value="ssidId"/>, upId);
	}
	function cloneUserProfileFromSsidUpDlg(upId) {
		succAddSsidUserProfileCallBack.setUpType(getUpType());
		succAddSsidUserProfileCallBack.setOperate("clone");
		if (userProfileTabviewSimple != null) {
			succAddSsidUserProfileCallBack.setUpTabId(userProfileTabviewSimple.getOpenedSubTabview());
		}
		hideUserProfileSelectDialog();
		networkPolicyCallbackFn = (function(){
			return function() {
				succAddSsidUserProfileCallBack.cancelBack(<s:property value="ssidId"/>);
			}
		})();
		cloneUserProfiles2SsidRealOperate(<s:property value="ssidId"/>, upId);
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
	
	var initAutoCompleteAttributeComboBox = function (){
		// init DataSource
		var dataStr = "<s:property value='attributeListString'/>";
		var dataSource = eval('[' + dataStr.replace(/\&quot;/g,'"') + ']');
		
		var attributeDataSource = new YAHOO.util.LocalDataSource(dataSource);
		attributeDataSource.responseSchema = {fields :["name" , "id"]};
		
		// AutoComplete BomboBox primary RADIUS server constructor
		var acComboBox = autoCompelteComboBox('assignUserProfileAttributeId', 'attributeContainer', 'attributeComboBox', attributeDataSource, dataSource.length,null,null,90);
		acComboBox.oAC.resultTypeList = false;
	}; 
	
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
<s:form action="ssidProfilesSimple" name="userProfileSsidProfileSimpleForm" id="userProfileSsidProfileSimpleForm">
<s:hidden name="ssidId" />
<s:hidden name="operation" />
<s:hidden name="userProfileSelectionStr" />
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
								</a> <s:if test="%{writeDisabled != 'disabled'}">
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
				<tr style="width: 100%;">
					<td>
						<table style="width:100%;" cellspacing="0" cellpadding="0">	
							<tr style="width: 100%;">	
							<td style="width:100%;">
							<div style="float:left;">
								<div id="subTabviewDefault" class="sub_tabview_title" style="width:110px;display:<s:property value="defaultUserProfieStype" />"><s:text name="config.v2.select.user.profile.popup.tab.default"/><span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>		
								<div id="subTabviewRegistration" class="sub_tabview_title" style="width:110px;display:<s:property value="selfRegUserProfieStype" />"><s:text name="config.v2.select.user.profile.popup.tab.reg"/><span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>	
								<div id="subTabviewGuest" class="sub_tabview_title" style="width:110px;display:<s:property value="guestUserProfieStype" />"><s:text name="glasgow_10.config.v2.select.user.profile.popup.tab.guest"/><span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>						
								<div id="subTabviewAuth" class="sub_tabview_title" style="width:110px;display:<s:property value="radiusUserProfieStype" />"><s:text name="config.v2.select.user.profile.popup.tab.auth"/><span class="sub_tabview_title_other_content">&nbsp;&gt;</span></div>						
							</div>
							<div id="subTabview_content_container" style="float:left;position: relative;">
								<!-- where all contents shown -->	
								<!-- default -->
								<div id="subTabviewDefault_Content" class="sub_tabview_content_hide sub_tabview_content">
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
												<input type="hidden" name="idValue" value="<s:property value="id"/>"></input>
												<div class="trToolListMenuStyle" style="display: none;" id="defaultUpMenu<s:property value="id"/>">
													<ul>
														<li onclick="editUserProfileFromSsidUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Edit <s:property value="value"/>">Edit</li>
														<li onclick="cloneUserProfileFromSsidUpDlg(<s:property value="id"/>);" 
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
								<div id="subTabviewRegistration_Content" class="sub_tabview_content_hide sub_tabview_content">
								<s:if test="%{upSelection.selfUserProfileSupport == true}">
									<table id="userProfilesSelectionTblRegistration" class="tblSelectContainer" cellspacing="5px" cellpadding="2px">
									<s:iterator value="%{upSelection.selfRegUserProfiles}" id="itReg" status="status">
										<s:if test="%{#itReg.checked == true}">
										<tr class="trSelectedCss">
										</s:if>
										<s:else>
										<tr>
										</s:else>
											<td class="selected_table_td_list">
												<input type="hidden" name="idValue" value="<s:property value="id"/>"></input>
												<div class="trToolListMenuStyle" style="display: none;" id="regUpMenu<s:property value="id"/>">
													<ul>
														<li onclick="editUserProfileFromSsidUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Edit <s:property value="value"/>">Edit</li>
														<li onclick="cloneUserProfileFromSsidUpDlg(<s:property value="id"/>);" 
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
								<div id="subTabviewGuest_Content" class="sub_tabview_content_hide sub_tabview_content">
								<s:if test="%{upSelection.guestUserProfileSupport == true}">
									<table id="userProfilesSelectionTblGuest" class="tblSelectContainer" cellspacing="5px" cellpadding="2px">
									<s:iterator value="%{upSelection.guestUserProfiles}" id="itGuest" status="status">
										<s:if test="%{#itGuest.checked == true}">
										<tr class="trSelectedCss">
										</s:if>
										<s:else>
										<tr>
										</s:else>
											<td class="selected_table_td_list">
												<input type="hidden" name="idValue" value="<s:property value="id"/>"></input>
												<div class="trToolListMenuStyle" style="display: none;" id="guestUpMenu<s:property value="id"/>">
													<ul>
														<li onclick="editUserProfileFromSsidUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Edit <s:property value="value"/>">Edit</li>
														<li onclick="cloneUserProfileFromSsidUpDlg(<s:property value="id"/>);" 
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
								<!-- authentication -->
								<div id="subTabviewAuth_Content" class="sub_tabview_content_hide sub_tabview_content">
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
												<input type="hidden" name="idValue" value="<s:property value="id"/>"></input>
												<div class="trToolListMenuStyle" style="display: none;" id="authUpMenu<s:property value="id"/>">
													<ul>
														<li onclick="editUserProfileFromSsidUpDlg(<s:property value="id"/>);" 
																onmouseover="mouseOverMenuItem(this, event);" onmouseout="mouseOutMenuItem(this, event);"
																title="Edit <s:property value="value"/>">Edit</li>
														<li onclick="cloneUserProfileFromSsidUpDlg(<s:property value="id"/>);" 
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
					<td style="width:160px;" class="dialogTip">
							<span style="width:100%;" id="upRelatedTipId"></span>
					</td>
				</tr>
			</table>
			</div>
		</div>
		</div>
		<div id="userProfileSelect_content_setting1" style="width:500px;padding-left:20px;margin-top:15px;">
			<table>
				<tr style="display:<s:property value="%{showAssignUserProfile}" />">
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
						</table>
					</td>
				</tr>
				<tr style="display:<s:property value="osDetectionStyle"/>">
					<td style="font-size:11px;">
					<s:checkbox name="enableOsDection" id="enableOsDectionChkId" onclick="javascript: clickOsDectionCheckbox(this.checked);"></s:checkbox>
					<label for="enableOsDectionChkId" style="font-size: 11px;"><s:text name="config.ssid.enable.os.detection" /></label></td>
				</tr>
				<tr style="display:<s:property value="hideOsDectionNote"/>" id="hideOsDectionNote">
						<td style="padding-left:55px; font-size: 11px;" class="noteInfo" colspan="2">
							<s:text name="config.ssid.classification.rule.note"/>
						</td>
				</tr>
				<tr style="display:<s:property value="upSelection.chkUserOnlyStyle"/>">
					<td style="font-size:11px;">
					<s:checkbox name="chkUserOnly" id="chkUserOnlyId" onclick="showActionEdit(this.checked);" ></s:checkbox>
					<label for="chkUserOnlyId"><s:text name="config.configTemplate.wizard.chkOnly" /></label></td>
				</tr>
				<tr>
					<td>
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
												<td style="font-size:11px;" colspan="2"><s:checkbox name="chkDeauthenticate" id="chkDeauthenticate"></s:checkbox><s:text name="config.configTemplate.wizard.chkDeauthenticate"></s:text></td>
											</tr>
										</table>
									</td>
								</tr>
								</table>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<!-- <div style="width:100%;padding-left:20px;margin-bottom:5px;">
			
		</div> -->
	</div>
	<div id="userProfileSelect_footer"></div>
</div>
</s:form>

<script type="text/javascript">
	
	var upType_default = <%= UserProfileSelection.USER_PROFILE_TYPE_DEFAULT %>;
	var upType_reg = <%= UserProfileSelection.USER_PROFILE_TYPE_REGISTRATION %>;
	var upType_auth = <%= UserProfileSelection.USER_PROFILE_TYPE_AUTH %>;
	var upType_guest = <%= UserProfileSelection.USER_PROFILE_TYPE_GUEST %>; // AirWatch Non-Compliance

	var SelectedTable = YAHOO.aerohive.widget.SelectedTable;
	var TabviewSimple = YAHOO.aerohive.widget.TabviewSimple;
	var defaultSelectionTbl = null;
	var registrationSelectionTbl = null;
	var authSelectionTbl = null;
	var guestSelectionTbl = null;
	function initializeSelectionTbls() {
		<s:if test="%{upSelection.defaultUserProfileSupport == true}">
		defaultSelectionTbl = new SelectedTable("userProfilesSelectionTblDefault",
	            {    id: "userProfilesSelectionTblDefault",
	                 idValue: "idValue",
	                 overflowDiv: "subTabviewDefault_Content",
	                 auxiliaryContent: "userProfilesSelectionTblDefault_post_",//prefix, end with unique id
	                 minSelect: 1,
	                 rowafterselected: afterDefaultUpSelect,
	                 rowafterdeselected: afterDefaultUpDeSelect,
	                 multiSelect: false});
		hm.util.addMouseScrollAction(Get("subTabviewDefault_Content"), hideAllToolMenus);
		</s:if>
		<s:if test="%{upSelection.selfUserProfileSupport == true}">
		registrationSelectionTbl = new SelectedTable("userProfilesSelectionTblRegistration",
	            {    id: "userProfilesSelectionTblRegistration",
	                 idValue: "idValue",
	                 overflowDiv: "subTabviewRegistration_Content",
	                 auxiliaryContent: "userProfilesSelectionTblRegistration_post_",//prefix, end with unique id
	                 minSelect: 1,
	                 rowafterselected: afterRegUpSelect,
	                 rowafterdeselected: afterRegUpDeSelect,
	                 multiSelect: false});
		hm.util.addMouseScrollAction(Get("subTabviewRegistration_Content"), hideAllToolMenus);
		</s:if>
		<s:if test="%{upSelection.guestUserProfileSupport == true}">
		guestSelectionTbl = new SelectedTable("userProfilesSelectionTblGuest",
	            {    id: "userProfilesSelectionTblGuest",
	                 idValue: "idValue",
	                 overflowDiv: "subTabviewGuest_Content",
	                 auxiliaryContent: "userProfilesSelectionTblGuest_post_",//prefix, end with unique id
	                 minSelect: 1,
	                 rowafterselected: afterGuestUpSelect,
	                 rowafterdeselected: afterGuestUpDeSelect,
	                 multiSelect: false});
		hm.util.addMouseScrollAction(Get("subTabviewGuest_Content"), hideAllToolMenus);
		</s:if>
		<s:if test="%{upSelection.authUserProfileSupport == true}">
		authSelectionTbl = new SelectedTable("userProfilesSelectionTblAuth",
	            {    id: "userProfilesSelectionTblAuth",
	                 idValue: "idValue",
	                 overflowDiv: "subTabviewAuth_Content",
	                 auxiliaryContent: "userProfilesSelectionTblAuth_post_",//prefix, end with unique id
	                 rowbeforeselect: beforeAuthTblSelect,
	                 showDisabledType: "disabled",
	                 rowafterselected: afterAuthUpSelect,
	                 rowafterdeselected: afterAuthUpDeSelect,
	                 multiSelect: true});
		hm.util.addMouseScrollAction(Get("subTabviewAuth_Content"), hideAllToolMenus);
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
	function initUserProfileTabviewSimple1() {
		userProfileTabviewSimple = new TabviewSimple("userProfileSelectionTabview",
	           {	id : "userProfileSelectionTabview",
	              	disabledType: "hide",
	              	subTabviews : [
						<s:if test="%{upSelection.defaultUserProfileSupport == true}">
	                	{
	                    	id: "subTabviewDefault",
	                        contentDiv: "subTabviewDefault_Content",
	                        opened: (<s:property value="upSelection.userProfileSubTabId"/> == null || <s:property value="upSelection.userProfileSubTabId"/> == -1 || <s:property value="upSelection.userProfileSubTabId"/> == upType_default) ? true : false,
	                        getContentResult: getContentResultDefault,
	                        afterOpened: showTipOfDefault,
	                        beforeOpen: disableOtherSelectedItemsDefault
	                    }
	                    <s:if test="%{upSelection.selfUserProfileSupport == true || upSelection.guestUserProfileSupport == true || upSelection.authUserProfileSupport == true}">
	                    ,
	                    </s:if>
	                    </s:if>
	            		<s:if test="%{upSelection.selfUserProfileSupport == true}">
	                    {
	                    	id: "subTabviewRegistration",
	                        contentDiv: "subTabviewRegistration_Content",
	                        opened: <s:property value="upSelection.userProfileSubTabId"/> == upType_reg ? true : false,
	                        getContentResult: getContentResultRegistration,
	                       	afterOpened: showTipOfReg,
	                       	beforeOpen: disableOtherSelectedItemsRegistration
	                    }
	                    <s:if test="%{upSelection.guestUserProfileSupport == true || upSelection.authUserProfileSupport == true}">
	                    ,
	                    </s:if>
	                    </s:if>
	            		<s:if test="%{upSelection.guestUserProfileSupport == true}">
	                    {
	                    	id: "subTabviewGuest",
	                        contentDiv: "subTabviewGuest_Content",
	                        opened: <s:property value="upSelection.userProfileSubTabId"/> == upType_guest ? true : false,
	                        getContentResult: getContentResultGuest,
	                       	afterOpened: showTipOfGuest,
	                       	beforeOpen: disableOtherSelectedItemsGuest
	                    }
	                    <s:if test="%{upSelection.authUserProfileSupport == true}">
	                    ,
	                    </s:if>
	                    </s:if>
	            		<s:if test="%{upSelection.authUserProfileSupport == true}">
	                    {
	                    	id: "subTabviewAuth",
	                        contentDiv: "subTabviewAuth_Content",
	                        opened: <s:property value="upSelection.userProfileSubTabId"/> == upType_auth ? true : false,
	                        getContentResult: getContentResultAuth,
	                        afterOpened: showTipOfAuth,
	                        beforeOpen: disableOtherSelectedItemsAuth
	                    }
	                    </s:if>
	                    ]
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
		Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfReg'/>";
	}
	
	var showTipOfAuth = function(arg) {
		Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfAuth'/>";
	}
	var showTipOfGuest = function(arg) {
		Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfGuest'/>";
	}
	
	var disableOtherSelectedItemsAuth = function(arg) {
	    if (userProfileTabviewSimple != null 
	    		&& authSelectionTbl != null
	    		&& userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuth") == false) {
	    	authSelectionTbl.removeAllDisabledRowStyle();
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
		       	authSelectionTbl.disableRowsWithAuxiliary(getContentResultDefault(null), "(default)");
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
		        authSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultRegistration(null), "(self-reg)");
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewGuest") == false) {
		        authSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultGuest(null), "(guest)");
	    	}
	    }
	    return true;
	}
	
	var disableOtherSelectedItemsDefault = function(arg) {
	    if (userProfileTabviewSimple != null 
	    		&& defaultSelectionTbl != null
	    		&& userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
	    	defaultSelectionTbl.removeAllDisabledRowStyle();
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuth") == false) {
		       	defaultSelectionTbl.disableRowsWithAuxiliary(getContentResultAuth(null), "(auth)");
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
		        defaultSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultRegistration(null), "(self-reg)");
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewGuest") == false) {
		        defaultSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultGuest(null), "(guest)");
	    	}
	    }
	    return true;
	}
	
	var disableOtherSelectedItemsRegistration = function(arg) {
	    if (userProfileTabviewSimple != null 
	    		&& registrationSelectionTbl != null
	    		&& userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
	    	registrationSelectionTbl.removeAllDisabledRowStyle();
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuth") == false) {
		        registrationSelectionTbl.disableRowsWithAuxiliary(getContentResultAuth(null), "(auth)");
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
		        registrationSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultDefault(null), "(default)");
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewGuest") == false) {
		        registrationSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultGuest(null), "(guest)");
	    	}
	    }
	    return true;
	}
	
	var disableOtherSelectedItemsGuest = function(arg) {
	    if (userProfileTabviewSimple != null 
	    		&& guestSelectionTbl != null
	    		&& userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewGuest") == false) {
	    	guestSelectionTbl.removeAllDisabledRowStyle();
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewAuth") == false) {
		        guestSelectionTbl.disableRowsWithAuxiliary(getContentResultAuth(null), "(auth)");
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewDefault") == false) {
		        guestSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultDefault(null), "(default)");
	    	}
	    	if (userProfileTabviewSimple.isCertainSubTabviewDisabled("subTabviewRegistration") == false) {
		        guestSelectionTbl.disableRowsWithAuxiliaryWithPrevious(getContentResultRegistration(null), "(self-reg)");
	    	}
	    }
	    return true;
	}
	
	function initUserProfileSubTabviews() {
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
		<s:if test="%{upSelection.selfUserProfileSupport == false}">
			if (userProfileTabviewSimple != null) {
				userProfileTabviewSimple.disableSubTabviews('subTabviewRegistration');
			}
		</s:if>
		<s:if test="%{upSelection.guestUserProfileSupport == false}">
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
		} else if ('subTabviewGuest' == subTabview) {
            return upType_guest;
        } else if ('subTabviewAuth' == subTabview) {
			return upType_auth;
		}
		return upType_default;
	}
	
	function getSelectionResult() {
		var result = '';
		if (userProfileTabviewSimple != null) {
            var tabviewResult = userProfileTabviewSimple.getAllContentResults();
            if (tabviewResult != null && tabviewResult.length > 0) {
            	for (var i = 0; i < tabviewResult.length; i++ ) {
            		var subTabviewResult = tabviewResult[i];
            		if (subTabviewResult != null && subTabviewResult.length > 1) {
            			var tblResult = subTabviewResult[1];
            			var upType = getCertainUpType(subTabviewResult[0]);
            			for (var j = 0; j < tblResult.length; j++) {
            				if (result == '') {
            					result = tblResult[j] + "_" + upType;
            				} else {
            					result += ","+tblResult[j] + "_" + upType;
            				}
            			}
            		}
            	}
            }
 		}
		return result;
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
		}
		else if (userProfileTabviewSimple != null) {
			var curOpenedSubTabview = userProfileTabviewSimple.getOpenedSubTabview();
			if ("subTabviewDefault" == curOpenedSubTabview && defaultSelectionTbl != null) {
				defaultSelectionTbl.startScrollToTr(addedUPId);
			} else if ("subTabviewRegistration" == curOpenedSubTabview && registrationSelectionTbl != null) {
				registrationSelectionTbl.startScrollToTr(addedUPId);
			} else if ("subTabviewGuest" == curOpenedSubTabview && guestSelectionTbl != null) {
                guestSelectionTbl.startScrollToTr(addedUPId);
            } else if ("subTabviewAuth" == curOpenedSubTabview && authSelectionTbl != null) {
				authSelectionTbl.startScrollToTr(addedUPId);
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
        } else if ("subTabviewAuth" == curOpenedSubTabview && authSelectionTbl != null) {
			disableOtherSelectedItemsAuth(null);
		}
	}
	
	function adjustInitTipShow() {
		var curUpType = getUpType();
		if (curUpType == upType_reg) {
			Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfReg'/>";
		} else if (curUpType == upType_auth) {
			Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfAuth'/>";
		} else if (curUpType == upType_guest) {
            Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfGuest'/>";
        }  else {
			Get("upRelatedTipId").innerHTML = "<s:property value='upSelection.upSelectionTipOfDefault'/>";
		}
	}
	
	function initializeTabview4SelectionDelay() {
		//action settings
		prepareActionSettings();
		
		initializeSelectionTbls();
		initUserProfileTabviewSimple1();
		//initUserProfileSubTabviews();
		adjustInitTipShow();
		initDisabledItemsCheck();
		scrollToSelectedItems();
		
		adjustTableWidthForIE();
		initAutoCompleteAttributeComboBox();
		
		if(Get("userProfileSelectPanelId_mask")) {
			Get("userProfileSelectPanelId_mask").onclick = hideAllToolMenus;
		}
		if(Get("userProfileSelectPanelId_c")) {
			Get("userProfileSelectPanelId_c").onclick = hideAllToolMenus;
		}
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
				if (Get("userProfilesSelectionTblGuest")) {
					Get("userProfilesSelectionTblGuest").style.width = "100%";
				}
				if (Get("userProfilesSelectionTblAuth")) {
					Get("userProfilesSelectionTblAuth").style.width = "100%";
				}
			}
			blnScrollBarExist = false;
		} else {
			blnScrollBarExist = true;
		}
	}
	
	var initializeTabview4Selection = function(arg) {
		setTimeout("initializeTabview4SelectionDelay()", 100);
	}
	YAHOO.util.Event.onContentReady("userProfileSelectionTabview", initializeTabview4Selection, this);

</script>