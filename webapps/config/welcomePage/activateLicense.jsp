<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div class="license_activate">
	<div class="step_sub_title"><s:text name="hm.missionux.wecomle.license.subtitle"/></div>
	<div class="step_sub_content">
		<div class="sub_content_tip">
			<div class="tip_entitlement_key">
				<div class="tip_content_text"><s:text name="hm.missionux.wecomle.license.subtitle.tip"/></div>
				<s:if test="%{newStart.blnVirtualMachine == false}">
					<div class="tip_addi_operation"><a
						title="<s:text name='geneva_31.hm.missionux.wecomle.license.link.to.licensekey.tip' />" 
						href="javascript:void(0);"><s:text name="geneva_31.hm.missionux.wecomle.license.guide.to.license.key"/></a></div>
				</s:if>
			</div>
			<div class="tip_license_key">
				<div class="tip_content_text"><s:text name="geneva_31.hm.missionux.wecomle.license.subtitle.tip.licensekey"/></div>
				<div class="tip_addi_operation"><a 
					title="<s:text name='geneva_31.hm.missionux.wecomle.license.link.to.entitlementkey.tip' />"
					href="javascript:void(0);"><s:text name="geneva_31.hm.missionux.wecomle.license.guide.to.entitlement.key"/></a></div>
			</div>
		</div>
		<div class="activate_result">
			<div class="activate_older_result">
				<div class="enter_title"><s:text name="hm.missionux.wecomle.license.key.title"/></div>
				<div class="enter_content">
				<s:if test="%{newStart.hasOrderedKey}">
					<s:iterator value="%{newStart.orderedKeys}" id="myOrderedKey">
						<div class="activate_older_single_result entitlement_key">
							<div class="activated_info">
								<div>
								<table>
									<tr>
										<td>
											<div class="entered_entitlementkey"><s:property value="%{#myOrderedKey.orderKey}" /></div>
										</td>
										<td>
											<div class="entered_entitlementkey_succ_indicator">&nbsp;</div>
										</td>
									</tr>
								</table>
								</div>
								<div class="key_succ_info_container">
									<div class="key_succ_indicator"><s:text name="hm.missionux.wecomle.license.key.activated.succ"/></div>
									<div class="key_detail_info">
										<s:text name="geneva_31.hm.missionux.wecomle.license.succ.license.single.info">
											<s:param><s:property value="%{#myOrderedKey.numberOfAps}" /></s:param>
										</s:text>
										<s:if test="%{#myOrderedKey.numberOfEvalValidDays > 1}">
											for <span class='key_total_days'><s:property value="%{#myOrderedKey.numberOfEvalValidDays}" /> days</span>
										</s:if>
										<s:elseif test="%{#myOrderedKey.subEndDate == 0 && #myOrderedKey.supportEndDate == 0}">
											for <span class='key_total_days'><s:property value="%{#myOrderedKey.numberOfEvalValidDays}" /> day</span>
										</s:elseif>
										<s:elseif test="%{#myOrderedKey.supportEndDate > 0}">
											to <span class='key_total_days'><s:property value="%{#myOrderedKey.supportEndTimeStr}" /></span>
										</s:elseif >
										<s:else>
											to <span class='key_total_days'><s:property value="%{#myOrderedKey.subEndTimeStr}" /></span>
										</s:else>
									</div>
								</div>
							</div>
						</div>
					</s:iterator>
				</s:if>
				</div>
			</div>
		</div>
		
		<div class="entitlement_key_add_another_area">
			<div class="enter_title">&nbsp;</div>
			<div class="enter_area"><a href="javascript: void(0);" id="add_another_key_link"><s:text name="hm.missionux.wecomle.license.key.add.another"/></a></div>
		</div>
		
		<div class="entitlement_key_area">
			<div class="enter_title"><s:text name="hm.missionux.wecomle.license.key.title"/></div>
			<div class="enter_area">
				<div id="enter_entitlement_key_tip_text"><div class="ie_only_show"><s:text name='hm.missionux.wecomle.license.key.placeholder'/></div></div>
				<input type="text" name="licenseKey" id="license_key_input" placeholder="<s:text name='hm.missionux.wecomle.license.key.placeholder'/>"></input>
			</div>
			<div class="enter_active">
				<input type="button" value="Activate" id="activate_license_btn" class="startHere_common_btn"/>
				<span class="progress_img"><img src="<s:url value="/images/blank.png" />"/></span>
			</div>
		</div>
		
		<div class="license_key_area">
			<table cellspacing="0" cellpadding="0">
				<tr>
					<td width="180px"></td>
					<td width="338px"><div class="license_key_error_container" id="error_container_01"></div></td>
					<td></td>
				</tr>
				<tr>
					<td>
						<div class="enter_title"><s:text name="geneva_31.hm.missionux.wecomle.license.key.license.title" /></div>
					</td>
					<td>
						<div class="enter_area">
							<div id="enter_entitlement_key_tip_text"><div class="ie_only_show"><s:text name='geneva_31.hm.missionux.wecomle.license.key.license.placeholder'/></div></div>
							<textarea name="licenseLicenseKey" id="license_license_key_input" placeholder="<s:text name='geneva_31.hm.missionux.wecomle.license.key.license.placeholder'/>"></textarea>
						</div>
					</td>	
					<td>
						<div class="enter_active">
							<input type="button" value="Activate" id="activate_license_license_btn" class="startHere_common_btn"/>
							<span class="progress_img"><img src="<s:url value="/images/blank.png" />"/></span>
						</div>
					</td>
				</tr>
			</table>
		</div>
		
		<div class="use_30_trial"><a href="javascript: void(0);"><s:text name="hm.missionux.wecomle.license.key.30days.trial"/></a></div>
	</div>
</div>

<script type="text/javascript">
	var step2Space = {};
	step2Space.blnHasOrderedKey = false;
	step2Space.blnClickTrialButton = false;
	<s:if test="%{newStart.hasOrderedKey || newStart.licenseInfo != null}">
		step2Space.blnHasOrderedKey = true;
	</s:if>
	step2Space.btnNext = {
		active: function() {
			startStep.BUTTONS.take("activate", true, "next");
			startStep.BUTTONS.take("jq", ['removeClass', 'disabled'], "next");
			step2Space.blnHasOrderedKey = true;
		},
		disable: function() {
			startStep.BUTTONS.take("activate", false, "next");
			startStep.BUTTONS.take("jq", ['addClass', 'disabled'], "next");
			step2Space.blnHasOrderedKey = false;
		}
	};
	startStep.add_step_definition(startStep.STEP_DEF.ACTIVATE_LICENSE, {
		onshow: function() {
			startStep.pageTitle.subTitle('<s:text name="hm.missionux.wecomle.license.subtitle"/>');
			if (step2Space.blnHasOrderedKey) {
				step2Space.btnNext.active();
			} else {
				step2Space.btnNext.disable();
			}
			step2Space.blnClickTrialButton = false;
			step2Space.curEnterInputArea.focus();
		},
		next: function() {
			if (!step2Space.blnClickTrialButton && !step2Space.blnHasOrderedKey) {
				return false;
			}
			startStep.BUTTONS.take("jq", ['removeClass', 'disabled'], "next");
			return true;
		},
		back: function() {
			startStep.BUTTONS.take("jq", ['removeClass', 'disabled'], "next");
			return true;
		}
	});
	
	var activateLicenseErrorHandler = hm.util.reportFieldErrorDivGen({
		className: "activate_license_error" 
	});
	step2Space.validateEntitlementKey = function() {
		var activeValue = step2Space.curEnterInputArea;
		var keyStr = activeValue.value;
		var errorPosition = step2Space.errorPosition;
		step2Space.curEnteredKey = keyStr;
		
		var title = '<s:text name="order.key" />';
		if (keyStr.length == 0) {
			activateLicenseErrorHandler(errorPosition, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
			activeValue.focus();
			return false;
		}
		var subActive = activeValue.value.trim().split("-");
		if (6 != subActive.length) {
			activateLicenseErrorHandler(errorPosition, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
			activeValue.focus();
			return false;
		}
		for (var i = 0; i < subActive.length; i++) {
			if(subActive[i].length != 5 || !hm.util.validateActivationKeyString(subActive[i])) {
				activateLicenseErrorHandler(errorPosition, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
				activeValue.focus();
	        	return false;
			}
		}
		return true;
	};
	step2Space.validateLicenseKey = function() {
		var activeValue = step2Space.curEnterInputAreaLicense;;
		var keyStr = activeValue.value;
		var errorPosition = step2Space.errorPositionLicense;
		step2Space.curEnteredLicenseLicenseKey = keyStr;
		
		var title = '<s:text name="license.key" />';
		if (keyStr.length == 0) {
			activateLicenseErrorHandler(errorPosition, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
			activeValue.focus();
			return false;
		}
		
		return true;
	};
	step2Space.addAInstalledKeyInfo = function(data, keyType) {
		keyType = keyType || "entitlement_key";
		var single_html = '<div class="activate_older_single_result '+ keyType +'">'
							+ '<div class="activated_info">'
							+ '<div>'
							+ '<table>'
							+ '<tr>'
							+ '<td>'
							+ '<div class="entered_entitlementkey">' + data.key + '</div>'
							+ '</td>'
							+ '<td>'
							+ '<div class="entered_entitlementkey_succ_indicator">&nbsp;</div>'
							+ '</td>'
							+ '</tr>'
							+ '</table>'
							+ '</div>'
							+ '<div class="key_succ_info_container">'
							+ '<div class="key_succ_indicator"><s:text name="hm.missionux.wecomle.license.key.activated.succ"/></div>'
							+ '<div class="key_detail_info">' + data.welmsg + '</div>'
							+ '</div>'
							+ '</div>'
							+ '</div>';
		$(".activate_older_result .enter_content").append($(single_html));
		$(".activate_older_result").show();
	};
	step2Space.activateEntitlementKey = function(entitlementKey) {
		entitlementKey = entitlementKey || step2Space.curEnteredKey;
		$.post("newStartHere.action",
				{
					"operation": "activate_license",
					"primaryOrderKey": entitlementKey
				},
				function(data, textStatus) {
					if (data.result === false) {
						activateLicenseErrorHandler(step2Space.errorPosition, data.message);
					} else {
						activateLicenseErrorHandler(step2Space.errorPosition, "");
						$(".entitlement_key_area").hide();
						$(".use_30_trial").hide();
						$(".activate_result").show();
						step2Space.addAInstalledKeyInfo(data);
						$(".entitlement_key_add_another_area").show();
					
						startStep.BUTTONS.take("disable", false, ["back", "next"]);
						if (!step2Space.blnHasOrderedKey) {
							step2Space.btnNext.active();
						}
					}
					
					startStep.BUTTONS.take("disable", false, ["back"]);
					if (step2Space.blnHasOrderedKey) {
						startStep.BUTTONS.take("disable", false, ["next"]);
					}
					$(".enter_area input[name=licenseKey]").attr("disabled", false);
					$("#activate_license_btn").attr("disabled", false)
												.removeClass("disabled")
												.siblings(".progress_img").hide();
					step2Space.curEnterInputArea.focus();
				},
				"json");
	};
	step2Space.activateLicenseKey = function(licenseKey) {
		licenseKey = licenseKey || step2Space.curEnteredLicenseLicenseKey;
		$.post("newStartHere.action",
				{
					"operation": "activate_license",
					"primaryLicense": licenseKey
				},
				function(data, textStatus) {
					if (data.result === false) {
						activateLicenseErrorHandler(step2Space.errorPositionLicense, data.message);
					} else {
						activateLicenseErrorHandler(step2Space.errorPositionLicense, "");
						$(".license_key_area").hide();
						$(".use_30_trial").hide();
						$(".activate_result").show();
						step2Space.addAInstalledKeyInfo(data, "license_key");
						$(".entitlement_key_add_another_area").hide();
						
						startStep.BUTTONS.take("disable", false, ["back", "next"]);
						if (!step2Space.blnHasOrderedKey) {
							step2Space.btnNext.active();
						}
					}
					
					startStep.BUTTONS.take("disable", false, ["back"]);
					if (step2Space.blnHasOrderedKey) {
						startStep.BUTTONS.take("disable", false, ["next"]);
					}
					$(".enter_area textarea[name=licenseLicenseKey]").attr("disabled", false);
					$("#activate_license_license_btn").attr("disabled", false)
												.removeClass("disabled")
												.siblings(".progress_img").hide();
					step2Space.curEnterInputAreaLicense.focus();
				},
				"json");
	};
	startStep.add_onload_event(function() {
		step2Space.curEnterInputArea = $(".enter_area input[name=licenseKey]")[0];
		step2Space.errorPosition = $(".entitlement_key_area .enter_area")[0];
		
		step2Space.curEnterInputAreaLicense = $(".enter_area textarea[name=licenseLicenseKey]")[0];
		step2Space.errorPositionLicense = $(".license_key_area .license_key_error_container")[0];
		
		$("#activate_license_btn").click(function(e) {
			if (!step2Space.validateEntitlementKey()) {
				return;
			}
			startStep.BUTTONS.take("disable", true, ["back", "next"]);
			$(".enter_area input[name=licenseKey]").attr("disabled", true);
			step2Space.activateEntitlementKey();
			$(this).attr("disabled", true)
					.addClass("disabled")
					.siblings(".progress_img").show();
		});
		
		$("#activate_license_license_btn").click(function(e) {
			if (!step2Space.validateLicenseKey()) {
				return;
			}
			startStep.BUTTONS.take("disable", true, ["back", "next"]);
			$(".enter_area input[name=licenseLicenseKey]").attr("disabled", true);
			step2Space.activateLicenseKey();
			$(this).attr("disabled", true)
					.addClass("disabled")
					.siblings(".progress_img").show();
		});
		
		$("#add_another_key_link").click(function(e) {
			$(".entitlement_key_add_another_area").hide();
			$(".entitlement_key_area .enter_title").html("&nbsp;");
			$(".entitlement_key_area").show();
			$("#enter_entitlement_key_tip_text").show();
			step2Space.curEnterInputArea.value = "";
			$(step2Space.curEnterInputArea).focus();
		});
		
		var toggleEntitlementAndLicenseKey = function(keyType) {
			keyType = keyType || "entitlement";
			var anotherKeyType = (keyType === "entitlement" ? "license" : "entitlement");
			$(".tip_" + anotherKeyType + "_key").hide();
			$(".tip_" + keyType + "_key").show();
			$("." + anotherKeyType + "_key_area").hide();
			
			var hasInstalledKeys = false;
			var $installedCurrentKeys = $(".activate_older_single_result." + keyType + "_key");
			if ($installedCurrentKeys.length > 0) {
				$installedCurrentKeys.show();
				hasInstalledKeys = true;
			}
			$(".activate_older_single_result." + anotherKeyType + "_key").hide();
			
			if (keyType === "entitlement") {
				$(".activate_older_result .enter_title").text('<s:text name="hm.missionux.wecomle.license.key.title"/>');
			} else {
				$(".activate_older_result .enter_title").text('<s:text name="geneva_31.hm.missionux.wecomle.license.key.license.title"/>');
			}
			
			$(".entitlement_key_add_another_area").hide();
			
			if (hasInstalledKeys) {
				$(".activate_older_result").show();
				$(".activate_result").show();
				if (keyType === "entitlement") {
					$(".entitlement_key_add_another_area").show();
				}
			} else {
				$(".activate_result").hide();
				$("." + keyType + "_key_area").show();
			}
		};
		$(".tip_entitlement_key .tip_addi_operation a").click(function() {
			toggleEntitlementAndLicenseKey("license");
			$(step2Space.curEnterInputAreaLicense).focus();
		});
		
		$(".tip_license_key .tip_addi_operation a").click(function() {
			toggleEntitlementAndLicenseKey("entitlement");
			$(step2Space.curEnterInputArea).focus();
		});
		
		
		<s:if test="%{newStart.hasOrderedKey}">
			$(".use_30_trial a").hide();
			$(".entitlement_key_area").hide();
			$(".activate_older_result").show();
			$(".entitlement_key_area .enter_title").html("&nbsp;");
			$(".entitlement_key_add_another_area").show();
		</s:if>
		<s:elseif test="%{newStart.licenseInfo != null}">
			var _installedLicense = {
				key: '<s:property value="newStart.licenseInfo.orderKey" />',
				welmsg: "<s:property escape='false' value='newStart.licenseKeySummary' />"
			};
			step2Space.addAInstalledKeyInfo(_installedLicense, "license_key");
			$(".use_30_trial a").hide();
			toggleEntitlementAndLicenseKey("license");
		</s:elseif>
		<s:else>
			$(".use_30_trial a").click(function(e) {
				step2Space.blnClickTrialButton = true;
				startStep.BUTTONS.take("click", "next");
			});
		</s:else>
	});
</script>