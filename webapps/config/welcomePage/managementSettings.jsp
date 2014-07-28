<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div class="settings_management">
	<s:hidden name="rebootFlag"/>
	<div class="step_sub_title"><s:text name="hm.missionux.wecomle.management.subtitle"/></div>
	<div class="step_sub_content">
		<div class="sub_content_tip"></div>
		<div class="settings_container">
			<div class="single_setting_area with_sub_setting <s:property value='newStart.hivemanagerPwdDisplay'/>">
				<div class="title"><s:text name="hm.missionux.wecomle.management.form.title.hivemanager"/></div>
				<div class="setting">
					<div class="single_setting">
						<div class="title"><s:text name="hm.missionux.wecomle.management.form.title.hivemanager.username"/></div>
						<div class="setting admin_name_field"><s:property value="userContext.userName" /></div>
					</div>
					<div class="single_setting">
						<div class="title"><s:text name="hm.missionux.wecomle.management.form.title.hivemanager.password"/></div>
						<div class="setting">
							<span class="required_mark left">*</span>
							<s:password name="adminPassword" size="46" maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'password');" id="adminPassword" showPassword="true"/>
							<s:textfield name="adminPassword" size="46" maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'password');" id="adminPassword_text" disabled="true" cssStyle="display: none;"/>
							&nbsp;<s:text name="hm.config.start.hivemanager.password.note"/>
						</div>
					</div>
					<div class="single_setting">
						<div class="title"><span class="ie_only_show"><s:text name="hm.missionux.wecomle.management.form.title.hivemanager.password.confirm"/></span></div>
						<div class="setting">
							<span class="required_mark left">*</span>
							<s:password size="46" maxlength="32"
								 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfAdminPassword" showPassword="true"/>
							<s:textfield size="46" maxlength="32"
								 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfAdminPassword_text" disabled="true" cssStyle="display: none;"/>
							<s:checkbox id="chkToggleDisplay1" name="ignore" value="true"
							 	onclick="hm.util.toggleObscurePassword(this.checked,['adminPassword','cfAdminPassword'],['adminPassword_text','cfAdminPassword_text']);" />
							<label for="chkToggleDisplay1"><s:text name="admin.user.obscurePassword" /></label>
						</div>
					</div>
				</div>
			</div>
			<div class="single_setting_area with_sub_setting">
				<div class="title"><s:text name="hm.missionux.wecomle.management.form.title.console.mode"/></div>
				<div class="setting">
					<div class="single_setting">
						<s:radio label="Gender" name="hmModeType" list="%{modeTypeNew1}" disabled="%{disableExpressMode}" listKey="key" listValue="value" cssClass="console_mode_express" />
						<div class="console_mode_tip"><s:text name="hm.missionux.wecomle.management.form.title.console.mode.express.tip"/></div>
						<s:radio label="Gender" name="hmModeType" list="%{modeTypeNew2}" disabled="%{disableMode}" listKey="key" listValue="value" cssClass="console_mode_enterprise" />
						<div class="console_mode_tip"><s:text name="hm.missionux.wecomle.management.form.title.console.mode.enterprise.tip"/></div>
					</div>
					<s:if test="%{newStart.noPwdFromMyHive}">
					<div class="single_setting">
						<div class="setting">
							<div class="quick_start_ssid_pwd_tip">
								<label><s:text name="hm.missionux.wecomle.update.preshared.key.tip" /></label>
							</div>
							<div class="access_console_pwd_setting">
								<div class="single_setting">
									<div class="title show_update_preshared_key"><span class="ie_only_show"><s:text name="hm.missionux.wecomle.update.preshared.key.title.default"/></span></div>
									<div class="setting">
										<span class="required_mark left">*</span>
										<s:password name="quickPassword" size="46" maxlength="32"
											 onkeypress="return hm.util.keyPressPermit(event,'password');" id="quickPassword" showPassword="true"/>
										<s:textfield name="quickPassword" size="46" maxlength="32"
											 onkeypress="return hm.util.keyPressPermit(event,'password');" id="quickPassword_text" disabled="true" cssStyle="display: none;"/>
										&nbsp;<s:text name="hm.config.start.hivemanager.password.note"/>
									</div>
								</div>
								<div class="single_setting">
									<div class="title show_update_preshared_key"><span class="ie_only_show"><s:text name="hm.missionux.wecomle.update.preshared.key.title.confirm"/></span></div>
									<div class="setting">
										<span class="required_mark left">*</span>
										<s:password size="46" maxlength="32" value="%{quickPassword}"
											 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfQuickPassword" showPassword="true"/>
										<s:textfield size="46" maxlength="32" value="%{quickPassword}"
											 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfQuickPassword_text" disabled="true" cssStyle="display: none;"/>
										<s:checkbox id="chkToggleDisplay2" name="ignore" value="true"
										 	onclick="hm.util.toggleObscurePassword(this.checked,['quickPassword','cfQuickPassword'],['quickPassword_text','cfQuickPassword_text']);" />
										 <label for="chkToggleDisplay2"><s:text name="admin.user.obscurePassword" /></label>
									</div>
								</div>
							</div>
						</div>
					</div>
					</s:if>
					<s:else>
					<div class="single_setting <s:property value='newStart.quickStartSsidPwdDisplay'/>" id="quick_start_ssid_pwd_section">
						<div class="setting">
							<div class="quick_start_ssid_pwd_tip">
								<input type="checkbox" name="chkUpdatePresharedKey" id="chkUpdatePresharedKey"></input>
								<label for="chkUpdatePresharedKey"><s:text name="hm.missionux.wecomle.update.preshared.key.tip" /></label>
							</div>
							<div class="quick_start_ssid_pwd_setting">
								<div class="single_setting">
									<div class="title"><span class="ie_only_show"><s:text name="hm.missionux.wecomle.update.preshared.key.title.default"/></span></div>
									<div class="setting">
										<span class="required_mark left">*</span>
										<s:password name="quickPassword" size="46" maxlength="32"
											 onkeypress="return hm.util.keyPressPermit(event,'password');" id="quickPassword" showPassword="true"/>
										<s:textfield name="quickPassword" size="46" maxlength="32"
											 onkeypress="return hm.util.keyPressPermit(event,'password');" id="quickPassword_text" disabled="true" cssStyle="display: none;"/>
										&nbsp;<s:text name="hm.config.start.hivemanager.password.note"/>
									</div>
								</div>
								<div class="single_setting">
									<div class="title"><span class="ie_only_show"><s:text name="hm.missionux.wecomle.update.preshared.key.title.confirm"/></span></div>
									<div class="setting">
										<span class="required_mark left">*</span>
										<s:password size="46" maxlength="32" value="%{quickPassword}"
											 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfQuickPassword" showPassword="true"/>
										<s:textfield size="46" maxlength="32" value="%{quickPassword}"
											 onkeypress="return hm.util.keyPressPermit(event,'password');" id="cfQuickPassword_text" disabled="true" cssStyle="display: none;"/>
										<s:checkbox id="chkToggleDisplay2" name="ignore" value="true"
										 	onclick="hm.util.toggleObscurePassword(this.checked,['quickPassword','cfQuickPassword'],['quickPassword_text','cfQuickPassword_text']);" />
										 <label for="chkToggleDisplay2"><s:text name="admin.user.obscurePassword" /></label>
									</div>
								</div>
							</div>
						</div>
					</div>
					</s:else>
				</div>
			</div>
			<div class="single_setting_area time_zone with_sub_setting">
				<div class="title"><s:text name="hm.missionux.wecomle.management.form.title.timezone"><s:param><s:property value="%{systemNmsName}"/></s:param></s:text></div>
				<div class="setting">
					<s:if test="%{hMOnline}">
						<div class="single_setting time_zone_select">
							<div class="title"><s:text name="hm.missionux.wecomle.management.form.title.timezone.country"/></div>
							<div class="setting ui-widget">
							<input id="tt_country_filter"/>
							<select name="newStart.country" id="tt_country">
							</select>
							</div>
						</div>
					</s:if>
					<s:else>
						<input type="hidden" name="newStart.country"/>
					</s:else>
					<div class="single_setting time_zone_select">
						<div class="title"><s:text name="hm.missionux.wecomle.management.form.title.timezone.timezone"/></div>
						<div class="setting">
						<select name="newStart.timeZone" id="tt_timezone">
						</select>
						</div>
					</div>
					<div class="single_setting time_zone_select">
						<div class="title"><s:text name="hm.missionux.wecomle.management.form.title.timezone.curtime"/></div>
						<div class="setting">
							<div class="time_zone_str">
								<span class="cur_time"></span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="timeZoneConfirmDlgId" style="display: none;">
	<div class="hd">
		Confirm
	</div>
	<div class="bd">
		<div id="restartLoadIcon">
			<div class="loadIcon">
			</div>
		</div>
		<table>
		<tr>
			<td colspan="2">
				<div id="processing" style="display:none">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					class="note">
					<tr>
						<td height="5"></td>
					</tr>
					<tr>
						<td class="noteError" id="showMessage">Your request is being processed ...</td>
					</tr>
					<tr>
						<td height="6"></td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<s:text name="hm.config.start.welcome.setting.timezone.reboot.note"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" ><s:radio onclick="this.blur();" label="Gender"	name="timeZoneReboot" value="1" list="%{timeZoneRebootNow}" listKey="key" listValue="value"/></td>
		</tr>
		<tr>
			<td colspan="2"><s:radio onclick="this.blur();"  label="Gender"	name="timeZoneReboot" list="%{timeZoneRebootCancel}" listKey="key" listValue="value"/></td>
		</tr>
 		<tr>
 			<td>
 				<input type="button" id="rebootOk" name="ignore" value="Ok" class="button"
									onclick="step3Space.saveTimeZoneConfig('configDone');" />
			</td>
			<td>
				<input type="button" id="rebootCancel" name="ignore" value="Cancel" class="button"
							onclick="hideTimeZoneConfirmDlg();" />
	   		</td>
	   	</tr>
		</table>
	</div>
</div>

<script type="text/javascript">
	startStep.add_step_definition(startStep.STEP_DEF.MANAGEMENT_SETTINGS, {
		next: function() {
			//check input information, only do next when all info are right
			var operation = "configDone";
			if (!step3Space.validate()) {
				return false;
			}
			if (!step3Space.validateTimeZoneChange(operation)) {
				return false;
			}
			startStep.submitForm(operation);
		},
		onshow: function() {
			startStep.pageTitle.subTitle('<s:text name="hm.missionux.wecomle.management.subtitle"/>');
			startStep.BUTTONS.take("changeText", "<s:text name='common.button.finish'/>", "next");
			startStep.BUTTONS.take("activate", true, "next");
			startStep.BUTTONS.take("show", true, "next");
			$("input#adminPassword").focus();
		}
	});
	
	var managementSettingErrorHandler = hm.util.reportFieldErrorDivGen({
		className: "management_common_error"
	});
	var step3Space = {};
	step3Space.getSortByText = function(txt) {
		var ttStrs = txt.split(") "),
			ttStr = ttStrs[0],
			preStr = "",
			gmtValue = 0;
		if (ttStr == "(GMT") {
			preStr = "1";
		} else {
			gmtValue = parseFloat(ttStr.replace(/[\(GMT|:)]/g, ""));
		}
		if (gmtValue < 0) {
			preStr = 0;
		} else if (gmtValue > 0) {
			preStr = 2;
		}
		gmtValue += 1300;
		var result = preStr;
		if (gmtValue < 10) {
			result += "000";
		} else if (gmtValue < 100) {
			result += "00";
		} else if (gmtValue < 1000) {
			result += "0";
		}
		result += gmtValue + ttStrs[1];
		return result;
	};
	step3Space.lastSelectedCountry = "";
	step3Space.lastSelectedTimezone = "";
	step3Space.timezoneOperation = {
		init: function() {
			step3Space.countryTimezone = _.sortBy(_.keys(Aerohive.TIMEZONE_COUNTRY), function(ttCountry){return Aerohive.TIMEZONE_COUNTRY[ttCountry].text;});
			step3Space.countryTimezoneKey = _.keys(Aerohive.TIMEZONE_COUNTRY);
			step3Space.countryTimezoneValues = _.map(Aerohive.TIMEZONE_COUNTRY, function(obj) {return obj.text;});
			step3Space.countryTimezoneLower = [];
			step3Space.countryTimezoneMapObj = {};
			_.each(step3Space.countryTimezoneValues, function(country) {
				if (country) {
					step3Space.countryTimezoneLower.push(country.toLowerCase());
					step3Space.countryTimezoneMapObj[country.toLowerCase()] = country;
				} else {
					step3Space.countryTimezoneLower.push(country);
				}
			});
			step3Space.countryNameAndKeyMap = {};
			_.each(Aerohive.TIMEZONE_COUNTRY, function(countryObj, key) {
				step3Space.countryNameAndKeyMap[countryObj.text] = key;
			});
			
			step3Space.timezoneInCountryCache = {};
			step3Space.countrySelection = $("#tt_country");
			step3Space.countryFilter = $("#tt_country_filter");
			step3Space.ttSelection = $("#tt_timezone");
			step3Space.ttCurTimeEl = $(".cur_time");
		},
		countryTimezoneMap: {
			get: function(country) {
				if (country) {
					return step3Space.countryTimezoneMapObj[country.toLowerCase()];
				}
				return "";
			}
		},
		initCountryInfo: function(countryList) {
			var countries = "";
			countryList = countryList || step3Space.countryTimezone;
			_.each(countryList, function(country) {
				countries += "<option value='"+country+"'>" + Aerohive.TIMEZONE_COUNTRY[country].text + "</option>";
			});
			step3Space.countrySelection.html(countries);
		},
		adjustCityTimezoneInfo: function(country) {
			if (country == step3Space.lastSelectedCountry) {
				return;
			}
			step3Space.lastSelectedTimezone = step3Space.ttSelection.val();
			if (!(country in step3Space.timezoneInCountryCache)) {
				var ttEls = Aerohive.TIMEZONE_COUNTRY[country].zones;
				var ttMaps = _.sortBy(_.map(ttEls, function(tt) {return {id: tt, value: Aerohive.TIMEZONE[tt]};}), function(obj) {return step3Space.getSortByText(obj.value);});
				var cityTimezones = "";
				_.each(ttMaps, function(ttValue) {
					cityTimezones += "<option value='"+ttValue.id+"'>" + ttValue.value + "</option>";
				});
				step3Space.timezoneInCountryCache[country] = cityTimezones;
			}
			
			step3Space.ttSelection.html(step3Space.timezoneInCountryCache[country]);
			step3Space.ttSelection.val(step3Space.lastSelectedTimezone);
			step3Space.lastSelectedCountry = country;
			step3Space.timezoneOperation.getCurTimezoneTime(step3Space.ttSelection.val());
		},
		listAllTimezoneInfo: function() {
			var allTimezones = "";
			_.each(_.sortBy(_.keys(Aerohive.TIMEZONE), function(key) {return step3Space.getSortByText(Aerohive.TIMEZONE[key]);}), function(key) {
				allTimezones += "<option value='"+key+"'>" + Aerohive.TIMEZONE[key] + "</option>";
			});
			return allTimezones;
		},
		isInCountryList: function(country) {
			if (country) {
				return _.indexOf(step3Space.countryTimezoneLower, country.toLowerCase()) > -1;
			}
		},
		isInCountryListId: function(country) {
			if (country) {
				return _.indexOf(step3Space.countryTimezoneKey, country) > -1;
			}
		},
		getCountryId: function(country) {
			return step3Space.countryNameAndKeyMap[country];
		},
		getCountryName: function(country) {
			return Aerohive.TIMEZONE_COUNTRY[country].text;
		},
		getCountryListByTimezone: function(ttArg) {
			if (ttArg) {
				var headCountry = [],
					tailCountry = [];
				 _.each(step3Space.countryTimezone, function(country) {
					if (_.indexOf(Aerohive.TIMEZONE_COUNTRY[country].zones, ttArg) > -1) {
						headCountry.push(country);
					} else {
						tailCountry.push(country);
					}
				});
				return [].concat(headCountry, tailCountry);
			}
		},
		getCurTimezoneTime: function(timezone) {
			var tts = timezone.split(" ");
			if (tts.length > 1) {
				timezone = tts[1];
			}
			$.post("newStartHere.action",
					$.param({
						operation: "getTimezoneTime",
						"newStart.timeZone": timezone
					}), function(data, textStatus) {
							if (data.result) {
								step3Space.ttCurTimeEl.html(data.time);
							} else {
								step3Space.ttCurTimeEl.html(data.message);
							}
					}, 
					"json");
		}
	};
	
	var timeZoneConfirmDlg = null;
	function initTimeZoneConfirmDlg() {
	// create Dialog overlay
		var div = document.getElementById('timeZoneConfirmDlgId');
		timeZoneConfirmDlg = new YAHOO.widget.Panel(div, {
			width:"400px",
			visible:false,
			fixedcenter:false,
			close: false,
			draggable:false,
			modal:true,
			constraintoviewport:true,
			underlay: "none",
			zIndex:1
			});
		timeZoneConfirmDlg.render(document.body);
		div.style.display = "";
	}
	function openTimeZoneConfirmDlg(){
		if(null != timeZoneConfirmDlg){
			timeZoneConfirmDlg.center();
			timeZoneConfirmDlg.cfg.setProperty('visible', true);
		}
	}

	function hideTimeZoneConfirmDlg(){
		if(null != timeZoneConfirmDlg){
			timeZoneConfirmDlg.cfg.setProperty('visible', false);
		}
	}
	step3Space.validateTimeZoneChange = function(operation) {
		var isInHomeDomain = <s:property value="isInHomeDomain" />;
	 	if (isInHomeDomain){
			if(step3Space.initialTimeZone != step3Space.ttSelection.val()){
				openTimeZoneConfirmDlg();
				return false;
			}else{
				document.forms[formName].rebootFlag.value = false;
			}
	   	}else{
	   		document.forms[formName].rebootFlag.value = true;
	   	}
	 	return true;
	};
	step3Space.saveTimeZoneConfig = function(operation){
		var timeZoneReboot =document.getElementById(formName+"_timeZoneReboot1");
		if(timeZoneReboot.checked){
			var url = "<s:url action='newStartHere' includeParams='none'/>" +"?ignore=" + new Date().getTime();
			document.forms[formName].operation.value = operation;
			document.forms[formName].rebootFlag.value = true;
			YAHOO.util.Connect.setForm(document.getElementById(formName));
			hm.util.show("processing");
			Get("rebootOk").disabled = true;
			Get("rebootCancel").disabled = true;
			var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:step3Space.updateDateTimeResult,timeout: 120000}, null);
		}else{
			document.forms[formName].rebootFlag.value = false;
			document.forms[formName].operation.value = operation;
			Get("rebootOk").disabled = true;
			Get("rebootCancel").disabled = true;
			document.forms[formName].submit();
		}
	};
	
	step3Space.doRestart = function(){
		window.setTimeout("step3Space.reLogin()", 10000);
	}
	
	step3Space.restartSuccess = function(o){
		try{
			eval("var details = " + o.responseText);
			step3Space.doRestart();
		}catch(e){
			step3Space.doRestart();
		}
		
	}
	
	step3Space.failureRestart = function(o){
		step3Space.doRestart();
	}
	
	step3Space.updateDateTimeResult = function(o)
	{
		eval("var details = " + o.responseText);
		if(details.succ){
			if (details.restart)
			{
				Get("showMessage").innerHTML = details.message;
				//setTimeout("step3Space.hideMessage()", 5 * 1200);
				$("#restartLoadIcon").show();
				url = "<s:url action='newStartHere' includeParams='none'/>?operation=restartHM&ignore=" + new Date().getTime();
				YAHOO.util.Connect.asyncRequest('POST', url, {success:step3Space.restartSuccess,failure:step3Space.failureRestart,timeout: 12000}, null);
			}
		}else{
			Get("showMessage").innerHTML = details.message;
			setTimeout("step3Space.hideMessage()", 5 * 2000);
			Get("rebootOk").disabled = false;
			Get("rebootCancel").disabled = false;
		}
	};
	
	step3Space.hideMessage = function(){
		hm.util.hide('processing');
	}
	
	step3Space.loginAgain = function(o){
		//redirect to the login page
		try{
			eval("var details = " + o.responseText);
			step3Space.reLogin();
		}catch(e){
			$("#restartLoadIcon").hide();
			var redirect_url = "<s:url action='login' includeParams='none' />?ignore=" + new Date().getTime();
			window.location.href = redirect_url;
		}
	}
	
	step3Space.connectFailed = function(o){
		step3Space.reLogin();
	}
	
	step3Space.reLogin = function(){
		//$("#restartLoadIcon").show();
		var url = "<s:url action='newStartHere' includeParams='none'/>?operation=checkRestart&ignore=" + new Date().getTime();
		var reLoginObj= YAHOO.util.Connect.asyncRequest('POST', url, {success:step3Space.loginAgain,failure:step3Space.connectFailed,timeout:6000}, null);
	}
	
	var blnDisplayEnterprise = <s:property value="%{displayEnterprise}"/>;
	var userNameForCheck = '<s:property value="%{userContext.getUserName()}"/>';
	step3Space.validatePwdWithConfirm = function(options) {
		options = options || {};
		var passwordElement = options.passwordElement, 
			confirmElement = options.confirmElement, 
			limitLength = options.limitLength, 
			passTitle = options.passTitle, 
			confirmTitle = options.confirmTitle, 
			noteTitle = (options.noteTitle).replace("(", "").replace(")", "");
		var message = hm.util.validatePassword(passwordElement.value, passTitle);
		if (message != null) {
			managementSettingErrorHandler(passwordElement, message);
	    	passwordElement.focus();
	    	return false;
		}
		if (passwordElement.value.length < limitLength) {
			managementSettingErrorHandler(passwordElement, passTitle+' must be '+noteTitle+'.');
		    passwordElement.focus();
		    return false;
		}
	
		message = hm.util.validatePassword(confirmElement.value, confirmTitle);
		if (message != null) {
			managementSettingErrorHandler(confirmElement, message);
	    	confirmElement.focus();
	    	return false;
		}
		if (confirmElement.value.length < limitLength) {
			managementSettingErrorHandler(confirmElement, confirmTitle+' must be '+noteTitle+'.');
		    confirmElement.focus();
		    return false;
		}
		
		if (passwordElement.value != confirmElement.value) {
			managementSettingErrorHandler(confirmElement, confirmTitle+' is different from '+(options.checkHmPwd? 'the password' : passTitle)+'.');
			confirmElement.focus();
			return false;
		}
		if (options.checkHmPwd) {
			if (null == passwordElement.value.match(/[A-Z]/g) || null == passwordElement.value.match(/[0-9]/g)) {
				managementSettingErrorHandler(passwordElement, passTitle+" must include at least one number and one uppercase character.");
			    passwordElement.focus();
			    return false;
			}
			
			if (passwordElement.value == userNameForCheck) {
				managementSettingErrorHandler(passwordElement, passTitle+" cannot be the same as the user name.");
			    passwordElement.focus();
			    return false;
			}
		}
		return true;
	};
	step3Space.validate = function() {
		hm.util.hideFieldError();
		var result = true;
		var blnModeEnterprise = $("input[type=radio].console_mode_enterprise")[0].checked;
		
		var resultTmp = true;
		var passwordElement;
		var confirmElement;
		<s:if test="%{newStart.hivemanagerPwdDisplay == ''}">
	    if (document.getElementById("chkToggleDisplay1").checked){
	       passwordElement = document.getElementById("adminPassword");
	       confirmElement = document.getElementById("cfAdminPassword");
	    }else{
	       passwordElement = document.getElementById("adminPassword_text");
	       confirmElement = document.getElementById("cfAdminPassword_text");
	    }
	    var limitLength = 8;
	    if(!blnDisplayEnterprise || (passwordElement.value.length > 0
	    	|| confirmElement.value.length > 0)){
	    	resultTmp = step3Space.validatePwdWithConfirm({
	    		passwordElement: passwordElement,
	    		confirmElement: confirmElement,
	    		passTitle: 'The password',
	    		confirmTitle: 'The confirm password',
	    		noteTitle: '<s:text name="hm.config.start.hivemanager.password.note" />',
	    		limitLength: limitLength,
	    		checkHmPwd: true
	    	});
	    	result = result && resultTmp;
	    }
	    </s:if>
	    
	    if ('<s:property value="newStart.noPwdFromMyHive"/>' == 'true' || (blnModeEnterprise && document.getElementById("chkUpdatePresharedKey").checked)) {
			if (document.getElementById("chkToggleDisplay2").checked){
				passwordElement = document.getElementById("quickPassword");
			    confirmElement = document.getElementById("cfQuickPassword");
			}else{
			    passwordElement = document.getElementById("quickPassword_text");
			    confirmElement = document.getElementById("cfQuickPassword_text");
			}
			resultTmp = step3Space.validatePwdWithConfirm({
	    		passwordElement: passwordElement,
	    		confirmElement: confirmElement,
	    		passTitle: '<s:text name="hm.missionux.wecomle.update.preshared.key.title.default" />',
	    		confirmTitle: '<s:text name="hm.missionux.wecomle.update.preshared.key.title.confirm" />',
	    		noteTitle: '<s:text name="hm.config.start.hivemanager.password.note" />',
	    		limitLength: 8,
	    		checkHmPwd: false
	    	});
		    result = result && resultTmp;
	    } else {
	    	document.getElementById("quickPassword").value = "";
		    document.getElementById("cfQuickPassword").value = "";
		    document.getElementById("quickPassword_text").value = "";
		    document.getElementById("cfQuickPassword_text").value = "";
	    }
	    
	    return result;
	}
	startStep.add_onload_event(function() {
		initTimeZoneConfirmDlg();
		<s:if test="%{!newStart.noPwdFromMyHive}">
		step3Space.$quickStartSsidPwdSection = $("#quick_start_ssid_pwd_section");
		</s:if>
		step3Space.timezoneOperation.init();
		
		_.each({"adminPassword": "<s:text name='hm.missionux.wecomle.management.form.adminpwd.tip'/>",
				"adminPassword_text": "<s:text name='hm.missionux.wecomle.management.form.adminpwd.tip'/>", 
		        "cfAdminPassword": "<s:text name='hm.missionux.wecomle.management.form.adminpwd.confirm.tip'/>",
		        "cfAdminPassword_text": "<s:text name='hm.missionux.wecomle.management.form.adminpwd.confirm.tip'/>",
		        "quickPassword": "<s:text name='hm.missionux.wecomle.update.preshared.key.title.default'/>",
		        "quickPassword_text": "<s:text name='hm.missionux.wecomle.update.preshared.key.title.default'/>",
		        "cfQuickPassword": "<s:text name='hm.missionux.wecomle.update.preshared.key.title.confirm'/>",
		        "cfQuickPassword_text": "<s:text name='hm.missionux.wecomle.update.preshared.key.title.confirm'/>"}, function(value, key) {
					$("#"+key).attr("placeholder", value);
		 		});
		
		
		<s:if test="%{!newStart.noPwdFromMyHive}">
		$("input[type=radio].console_mode_enterprise").click(function(e){
			step3Space.$quickStartSsidPwdSection.toggleClass("hidden", !this.checked);
		});
		$("input[type=radio].console_mode_express").click(function(e){
			step3Space.$quickStartSsidPwdSection.toggleClass("hidden", this.checked);
		});
		
		$("#chkUpdatePresharedKey").click(function(e) {
			if (this.checked) {
				$(".quick_start_ssid_pwd_setting").show();
				var curpwd = $("#quickPassword").val();
				if (!Get("chkToggleDisplay2").checked) {
					curpwd = $("#quickPassword_text").val();
				}
				if (curpwd == null || curpwd == "") {
					var hmPwdVal = $("#adminPassword").val();
					if (!Get("chkToggleDisplay1").checked) {
						hmPwdVal = $("#adminPassword_text").val();
					}
					$("#quickPassword").val(hmPwdVal);
					$("#quickPassword_text").val(hmPwdVal);
					$("#cfQuickPassword").val(hmPwdVal);
					$("#cfQuickPassword_text").val(hmPwdVal);
				}
			} else {
				$(".quick_start_ssid_pwd_setting").hide();
			}
			$(".settings_management .single_setting .title").toggleClass("show_update_preshared_key", this.checked);
		});
		</s:if>
		
		<s:if test="%{hMOnline}">
		step3Space.timezoneOperation.initCountryInfo();
		step3Space.countryFilter.autocomplete({
			source: step3Space.countryTimezoneValues,
			select: function(event, ui) {
				exchangeFilterData(event, {value: ui.item.value});
			}
		});
		
		function exchangeFilterData(e, el) {
			if (step3Space.timezoneOperation.isInCountryList(el.value)) {
				var country = step3Space.timezoneOperation.countryTimezoneMap.get(el.value);
				var countryId = step3Space.timezoneOperation.getCountryId(country);
				step3Space.countrySelection.val(countryId);
				step3Space.timezoneOperation.adjustCityTimezoneInfo(countryId);
			} else {
				el.value = step3Space.timezoneOperation.getCountryName(step3Space.countrySelection.val());
			}
		}
		step3Space.countryFilter.blur(function(e) { 
				exchangeFilterData(e, this);
			}).keyup(function(e) {
				if (e.keyCode == 13) {
					if (step3Space.timezoneOperation.isInCountryList(this.value)) {
						this.value = step3Space.timezoneOperation.countryTimezoneMap.get(this.value);
						step3Space.countryFilter.autocomplete("close");
						var countryId = step3Space.timezoneOperation.getCountryId(this.value);
						step3Space.countrySelection.val(countryId);
						step3Space.timezoneOperation.adjustCityTimezoneInfo(countryId);
					}
				}
			});
		step3Space.countrySelection.change(function(e) {
			step3Space.countryFilter.val(step3Space.timezoneOperation.getCountryName(this.value));
			step3Space.timezoneOperation.adjustCityTimezoneInfo(this.value);
		});
		</s:if>
		
		step3Space.ttSelection.change(function(e) {
			step3Space.timezoneOperation.getCurTimezoneTime(this.value);
		});
		
		var defCountryCode = '<s:property value="newStart.country"/>';
		if (!step3Space.timezoneOperation.isInCountryListId(defCountryCode)) {
			defCountryCode = '<s:property value="newStart.defIfNotFoundCountry"/>';
		}
		var defTimezone = '<s:property value="newStart.timeZone"/>';
		if (defTimezone && defTimezone.indexOf(" ") > 0) {
			defTimezone = defTimezone.split(" ")[1];
		}
		step3Space.initialTimeZone = defTimezone;
		
		<s:if test="%{hMOnline}">
		if (defCountryCode) {
			if (step3Space.timezoneOperation.isInCountryListId(defCountryCode)) {
				step3Space.countrySelection.val(defCountryCode);
			}
		} else if (defTimezone) {
			step3Space.timezoneOperation.initCountryInfo(step3Space.timezoneOperation.getCountryListByTimezone(defTimezone));
		}
		
		step3Space.countrySelection.change();
		</s:if>
		
		<s:if test="%{!hMOnline}">
			step3Space.ttSelection.html(step3Space.timezoneOperation.listAllTimezoneInfo());
		</s:if>
		
		step3Space.ttSelection.val(defTimezone);
		
		<s:if test="%{!hMOnline}">
			step3Space.ttSelection.change();
		</s:if>
	});
</script>