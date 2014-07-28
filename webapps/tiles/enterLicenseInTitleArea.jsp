<%@taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
var primaryLsInTitle;
var secondLsInTitle;
var enterPanel;

YAHOO.util.Event.onDOMReady(function(){
	//Create enter entitle key panel
	var div = document.getElementById('enterPanel');
	enterPanel = new YAHOO.widget.Panel(div, { width:"500px",visible:false,draggable:false,constraintoviewport:true,modal:true,zIndex:100 } );
	var code = document.getElementById('contentCtrl');
	if(code) {
		enterPanel.cfg.setProperty('x', YAHOO.util.Dom.getX(code)+30);
		enterPanel.cfg.setProperty('y', YAHOO.util.Dom.getY(code));
	}
	enterPanel.render(document.body);
	div.style.display = "";
});

function getOrImport(ifGet) {
	document.getElementById("emailMessage").style.display = ifGet ? "" : "none";
	document.getElementById("importMessage").style.display = ifGet ? "none" : "";
}

function enterKeyInTopPannel() {
	// oem
	var lsUrl = '<s:url action="licenseMgr" includeParams="none" />?operation=enterKeyInTop';
	var enterKey;
	if ('none' == '<s:property value="%{oldLicenseDisplay}"/>') {
		enterKey = document.getElementById("primaryOrderKey_1");
	} else {
		// license string
		if ('' == document.getElementById("importMessage").style.display) {
			if (checkLicenseStr()) {
				lsUrl += ('&primaryLicense='+encodeURIComponent(primaryLsInTitle));
				if (null != secondLsInTitle) {
					lsUrl += ('&secondaryLicense='+encodeURIComponent(secondLsInTitle));
				}
			} else {
				return;
			}
		} else {
			enterKey = document.getElementById("primaryOrderKey_2");
		}
	}
	if (null != enterKey) {
		if (!validateActKey(enterKey, '<s:text name="order.key" />')) {
			return;
		} else {
			lsUrl += ('&primaryOrderKey='+enterKey.value);
		}
	}
	lsUrl += ('&ignore='+new Date().getTime());
	var transaction = YAHOO.util.Connect.asyncRequest('GET', lsUrl, callbackLs, null);
}

var detailsSuccessLs = function(o) {
	eval("var data = " + o.responseText);
	if (data.result) {
		changeEnterPanel(false);
		
		var licenseEnterInfoDialog = new YAHOO.widget.SimpleDialog("infoDlg", {
		width: "350px",
		fixedcenter:true,
		modal:true,
	    visible:false,
		draggable:true,
		constraintoviewport: true,
		icon: YAHOO.widget.SimpleDialog.ICON_INFO,
		buttons: [ { text:"&nbsp;OK&nbsp;", handler:licenseEnterHandleNo, isDefault:true } ]});
		licenseEnterInfoDialog.setHeader("Information");
		licenseEnterInfoDialog.render(document.body);
		licenseEnterInfoDialog.cfg.setProperty("text",data.message);
		licenseEnterInfoDialog.show();
	} else {
		hm.util.reportFieldError(document.getElementById("noteErrorTd"), data.message);
	}
};

var licenseEnterHandleNo = function() {
    this.hide();
    hideMessageInSession();
};

var detailsFailedLs = function(o) {
//	alert("failed.");
};

var callbackLs = {
	success : detailsSuccessLs,
	failure : detailsFailedLs
};

function changeEnterPanel(checked) {
	if (enterPanel != null) {
		enterPanel.cfg.setProperty('visible', checked);
		if(checked && !document.getElementById('contentCtrl')) {
			enterPanel.center();	
		}
	}
}

function validateActKey(activeValue, title) {
	if (activeValue.value.length == 0) {
		hm.util.reportFieldError(activeValue, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		activeValue.focus();
		return false;
	}
	var subActive = activeValue.value.trim().split("-");
	if (6 != subActive.length) {
		hm.util.reportFieldError(activeValue, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		activeValue.focus();
		return false;
	}
	for (var i = 0; i < subActive.length; i++) {
		if(subActive[i].length != 5 || !hm.util.validateActivationKeyString(subActive[i])) {
			hm.util.reportFieldError(activeValue, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
			activeValue.focus();
        	return false;
		}
	}
	return true;
}

function checkLicenseStr() {
	var primary = document.getElementById("primaryLicense");
	var errorTitle = '<s:text name="license.key" />';
	<s:if test="%{showTwoSystemId}">
		errorTitle = 'primary <s:text name="license.key" />';
	</s:if>
	if (primary.value.length == 0) {
		hm.util.reportFieldError(primary,'<s:text name="error.requiredField"><s:param>'+errorTitle+'</s:param></s:text>');
		primary.focus();
		return false;
	}
	primaryLsInTitle = primary.value;
	<s:if test="%{showTwoSystemId}">		
		var second = document.getElementById("secondaryLicense");
		if (second.value.length == 0) {
			hm.util.reportFieldError(second, '<s:text name="error.requiredField"><s:param>secondary <s:text name="license.key"/></s:param></s:text>');
			second.focus();
			return false;
		}
		secondLsInTitle = second.value;
	</s:if>
	return true;
}
</script>

<div id="enterPanel" style="display: none;">
	<div class="bd">
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="480px">
			<tr>
				<td height="2"></td>
			</tr>
			<tr>
				<td style="padding-left:10px">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="noteError">
							<label id="noteErrorTd"></label>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td>
<table cellspacing="0" cellpadding="0" border="0">
	<tr style="display:<s:property value="%{oldLicenseDisplay==''?'none':''}" />">
		<td>
			<table>
				<tr>
					<td style="padding-left:15px" width="95px"><s:text name="order.key" /><font color="red"><s:text name="*" /></font></td>
					<td><s:textfield id="primaryOrderKey_1" value="%{primaryOrderKey}" maxlength="41" cssStyle="width:300px"
						onkeypress="return hm.util.keyPressPermit(event,'activation');" />&nbsp;&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr style="display:<s:property value="%{oldLicenseDisplay}" />">
		<td>
			<table>
				<tr>
					<td class="login"><s:radio label="Gender" name="radioMethod" list="%{radioEmailMessage}" listKey="key" listValue="value"
					onclick="getOrImport(true);" /></td>
				</tr>
				<tr id="emailMessage" style="display:<s:property value="%{radioMethod==1?'':'none'}" />">
					<td style="padding-left:5px">
						<table>
							<tr>
								<td style="padding-left:10px" width="95px"><s:text name="order.key" /><font color="red"><s:text name="*" /></font></td>
								<td><s:textfield id="primaryOrderKey_2" value="%{primaryOrderKey}" maxlength="41" cssStyle="width:300px"
									onkeypress="return hm.util.keyPressPermit(event,'activation');" />&nbsp;&nbsp;</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="login"><s:radio label="Gender" name="radioMethod" list="%{radioImportMessage}" listKey="key" listValue="value"
					onclick="getOrImport(false);" /></td>
				</tr>
				<tr id="importMessage" style="display:<s:property value="%{radioMethod==2?'':'none'}" />">
					<td style="padding-left:5px">
						<table>
							<tr>
								<td align="left">
									<table border="0" cellspacing="0" cellpadding="0">
										<s:if test="%{showTwoSystemId}">
											<tr>
												<td height="2"></td>
											</tr>
											<tr>
												<td colspan="3" class="noteInfo" style="padding-left:10px"><s:property value="%{primarySystemId}" /></td>
											</tr>
											<tr>
												<td height="2"></td>
											</tr>
											<tr>
												<td style="padding-left:10px" width="105px" rowspan="2" valign="middle"><s:text name="license.key" /><font color="red"><s:text name="*" /></font></td>
												<td rowspan="2"><s:textarea name="primaryLicense" id="primaryLicense" cssStyle="width:330px" rows="2" /></td>
											</tr>
											<tr>
												<td height="2"></td>
											</tr>
											<tr>
												<td colspan="3" class="noteInfo" style="padding-left:10px"><s:property value="%{secondarySystemId}" /></td>
											</tr>
											<tr>
												<td height="2"></td>
											</tr>
											<tr>
												<td style="padding-left:10px" width="105px" rowspan="2" valign="middle"><s:text name="license.key" /><font color="red"><s:text name="*" /></font></td>
												<td rowspan="2"><s:textarea name="secondaryLicense" id="secondaryLicense" cssStyle="width:330px" rows="2" />&nbsp;&nbsp;</td>
											</tr>
										</s:if>
										<s:else>
											<tr>
												<td style="padding-left:10px" width="105px" rowspan="2" valign="middle"><s:text name="license.key" /><font color="red"><s:text name="*" /></font></td>
												<td rowspan="2"><s:textarea name="primaryLicense" id="primaryLicense" cssStyle="width:330px" rows="2" />&nbsp;&nbsp;</td>
											</tr>
										</s:else>
									</table>
								</td>
							</tr>
							<tr>
								<td height="4"></td>
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
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td align="center">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><input type="button" name="ignore" value="Enter"
							class="button" onClick="enterKeyInTopPannel();"></td>
						<td><input type="button" name="ignore" value="Cancel"
							class="button" onClick="changeEnterPanel(false);"></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td height="4"></td>
			</tr>
		</table>
	</div>
</div>