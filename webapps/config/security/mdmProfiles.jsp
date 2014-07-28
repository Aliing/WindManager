<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<link rel="stylesheet" type="text/css" href="<s:url value="/css/hm.css"  includeParams="none"/>" />
<link rel="stylesheet" type="text/css" href="<s:url value="/css/te.css"  includeParams="none"/>" />
<script type="text/javascript" src="<s:url value="/js/jquery.min.js" includeParams="none"/>"></script>
<script type="text/javascript" src="<s:url value="/js/mvc/ae.js" includeParams="none"/>"></script>
<script type="text/javascript" src="<s:url value="/js/pluins/jquery.validate.js" includeParams="none"/>"></script>
 
<script>
var formName = 'mdmProfiles';
var thisOperation;

function validSettingChanged(radioBox){
	 if(radioBox.value == 0){
		 document.getElementById("mdmProfiles_validTimeInfo_keepTime").disabled = true;
		 document.getElementById("mdmProfiles_startTime").disabled = true;
		 document.getElementById("mdmProfiles_endTime").disabled = true
		 document.getElementById("mdmProfiles_validDay").disabled = true;
	}else if(radioBox.value == 1){
		document.getElementById("mdmProfiles_validTimeInfo_keepTime").disabled = false;
		document.getElementById("mdmProfiles_startTime").disabled = true;
		document.getElementById("mdmProfiles_endTime").disabled = true;
		document.getElementById("mdmProfiles_validDay").disabled = true;
	}else if(radioBox.value == 2){
		document.getElementById("mdmProfiles_validTimeInfo_keepTime").disabled = true;
		document.getElementById("mdmProfiles_startTime").disabled = false;
		document.getElementById("mdmProfiles_endTime").disabled = false;
		document.getElementById("mdmProfiles_validDay").disabled = false;
	}
}

function validateValueRange() {
    var inputElement=document.getElementById("attributeValue");
    if (inputElement.value.length == 0) {
         hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField.for.mdm"/>');
         inputElement.focus();
         return false;
     }
     var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.userprofile.attribute" />',
                                                 <s:property value="%{numberRange.min()+1}" />,
                                                 <s:property value="%{numberRange.max()}" />);
     if (message != null) {
         hm.util.reportFieldError(inputElement,message);
         inputElement.focus();
         return false;
     } 

     return true;
}

function submitAction(operation) {
	var el = $("#mdmProfiles");
    if(operation == 'create'+'<s:property value="lstForward"/>' 
        	|| operation == 'update'+'<s:property value="lstForward"/>' 
        	|| operation == 'update' || operation == 'create') {
    	if( !el.valid()){
    		el.find('.mdm-set-item').each(function(){
				var t = $(this),con = t.find('.mdm-set-item-content'),
					conWrap = t.find('.mdm-set-item-con-wrap');
					errors = t.find('span.form-error');
				
				errors.length && errors.css('display')!= 'none' && 
					!con.is(':visible') && conWrap.add(con).css('display','block');
			});
    		return;
    	}
    	if(!validateValueRange()){
    		   return ;
    	}
         
    }
    document.forms[formName].operation.value = operation;
	document.forms[formName].submit();
}

function saveMdmProfiles(operation) {
	var el = $("#mdmProfiles");
	if( el.valid()){
		if (operation == 'create') {
			url = "<s:url action='mdmProfiles' includeParams='none' />" + "?jsonMode=true" 
					+ "&ignore=" + new Date().getTime(); 
		} else if (operation == 'update') {
			url = "<s:url action='mdmProfiles' includeParams='none' />" + "?jsonMode=true" 
					+ "&ignore=" + new Date().getTime(); 
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["mdmProfiles"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveJsonMdmProfilesDlg, failure : failSaveJsonMdmProfilesDlg, timeout: 60000}, null);
	}else{
		el.find('.mdm-set-item').each(function(){
			var t = $(this),con = t.find('.mdm-set-item-content'),
				conWrap = t.find('.mdm-set-item-con-wrap');
				errors = t.find('span.form-error');
			
			errors.length && errors.css('display')!= 'none' && 
				!con.is(':visible') && conWrap.add(con).css('display','block');
		});
		return;
		
	}
}

var succSaveJsonMdmProfilesDlg = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.id != null && details.id != ''){
					hm.util.insertSelectValue(details.id, details.name, parentSelectDom, false, true);
				}
			}
		}
		parent.closeIFrameDialog();
	} catch(e) {
		// do nothing now.
	}
}

var failSaveJsonMdmProfilesDlg = function(o) {
	// do nothing now.
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="mdmProfiles" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}

function showIcon(e){
	$(e).parent().find("#haveIcons").val("1");
}

function showCredentialsDetail(e){
	var index = $(e).parent().parent().prev().find(".w170").attr("name").replace("credentialsProfileInfos[","").replace("].certificateFileName","");
	url = "<s:url action='mdmProfiles' includeParams='none' />" + "?index="+ index + "&ignore=" + new Date().getTime(); 
	document.forms[formName].operation.value = "showCredentialDetail";	
	document.forms[formName].enctype = "multipart/form-data";
	YAHOO.util.Connect.setForm(document.forms["mdmProfiles"],true);
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {upload : succShowCredentialDetail, timeout: 60000,argument: e}, null);
}

var succShowCredentialDetail = function(o) {
	try {
		eval("var details = " + o.responseText);
		var e = o.argument;
		if(details.havePwd == "0"){
			$(e).parent().parent().next().next().next().next().attr('style','');
			$(e).parent().parent().next().next().next().next().find(":password").val("");
			$(e).parent().parent().next().find("#issuer").html("");
			$(e).parent().parent().next().next().find("#notbefore").html("");
			$(e).parent().parent().next().next().next().find("#notafter").html("");
		}else{
			$(e).parent().parent().next().next().next().next().attr('style','display:none');
			$(e).parent().parent().next().find("#issuer").html(details.issuer);
			$(e).parent().parent().next().next().find("#notbefore").html(details.notbefore);
			$(e).parent().parent().next().next().next().find("#notafter").html(details.notafter);
			$(e).parent().find("#haveUploadCertificate").val("1");
		}
	} catch(e) {
		// do nothing now.
	}
}
var failShowCredentialDetail = function(o) {
	// do nothing now.
}

function getIssuer(e){
	var index = $(e).attr("name").replace("credentialsProfileInfos[","").replace("].password","");
	var pwd = $(e).val();
	url = "<s:url action='mdmProfiles' includeParams='none' />" + "?index="+ index + "&pwd=" + pwd + "&ignore=" + new Date().getTime(); 
	document.forms[formName].operation.value = "getIssuer12";	
	YAHOO.util.Connect.setForm(document.forms["mdmProfiles"],true);
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {upload : succGetIssuer12, timeout: 60000,argument: index}, null);
}
var succGetIssuer12 = function(o) {
	try {
		eval("var details = " + o.responseText);
		var index = o.argument;
		
		var obj = $("input[name='credentialsProfileInfos["+ index +"].certificateFileName']");
  		obj.parent().parent().next().next().find("#issuer").html(details.issuer);
		obj.parent().parent().next().next().next().find("#notbefore").html(details.notbefore);
		obj.parent().parent().next().next().next().next().find("#notafter").html(details.notafter);
		obj.parent().parent().next().find("#haveUploadCertificate").val("1");

	} catch(e) {
		// do nothing now.
	}
}
var failGetIssuer12 = function(o) {
	// do nothing now.
}

</script>
<div id="content" style="width:900px;">
<div class="J-mod" data-mod="AE.hm.mdm.profile" data-switch="on">

		<s:form action="mdmProfiles" id="mdmProfiles" name="mdmProfiles" enctype="multipart/form-data">
			<s:if test="%{jsonMode==true}">
				<s:hidden name="operation"/>
				<s:hidden name="jsonMode" />
				<s:hidden name="id" />
				<s:hidden name="parentDomID"/>
				<s:hidden name="contentShowType" />
				<s:hidden name="parentIframeOpenFlg" />
			</s:if>
			<s:if test="%{jsonMode==false}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><tiles:insertDefinition name="context" /></td>
				</tr>
				<tr>
				<td class="buttons">
					<table border="0" cellspacing="0"  cellpadding="0">
						<tr>
							<s:if test="%{dataSource.id == null}">
								<td>
								<input type="button" name="ignore" value="<s:text name="button.create"/>"
									class="button"
									onClick="submitAction('create<s:property value="lstForward"/>');"
									<s:property value="writeDisabled" />> 
									</td>
							</s:if>
							<s:else>
								<td><input type="button" name="ignore"  value="<s:text name="button.update"/>"
									class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
									<s:property value="updateDisabled" />></td>
							</s:else>
							<td><input type="button" name="ignore" value="Cancel"
								class="button"
								onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
						</tr>
					</table>
				</td>
			</tr>
			</table>
			</s:if>
			<s:else>
			<div id="vlanTitleDiv" class="topFixedTitle">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td style="padding:10px 10px 10px 10px">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td align="left">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-IP_Tracking.png" includeParams="none"/>"
										width="40" height="40" alt="" class="dblk" />
									</td>
									<td class="dialogPanelTitle">
										<s:if test="%{dataSource.id == null}">
											<s:text name="config.title.mdmProfiles.new"/>
										</s:if>
										<s:else>
											<s:text name="config.title.mdmProfiles.edit"/>
										</s:else>
										&nbsp;
									</td>
									<td>
										<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
											<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
												alt="" class="dblk"/>
										</a>
									</td>
								</tr>
							</table>
							</td>
							<td align="right">
							
							<s:if test="%{!parentIframeOpenFlg}">
							
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
								  
									<td class="npcButton"><a href="javascript:void(0);"  class="btCurrent" onclick="parent.closeIFrameDialog();" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
									<td width="20px">&nbsp;</td>
									<td class="npcButton">
									<s:if test="%{dataSource.id == null}">
										<s:if test="%{writeDisabled == 'disabled'}">
											&nbsp;</td>
										</s:if>
										<s:else>
											<a href="javascript:void(0);" class="btCurrent" onclick="saveMdmProfiles('create');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
										</s:else>
									</s:if>
									<s:else>
										<s:if test="%{updateDisabled == 'disabled'}">
											&nbsp;</td>
										</s:if>
										<s:else>
											<a href="javascript:void(0);" class="btCurrent" onclick="saveMdmProfiles('update');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
										</s:else>
									</s:else>
									 
								</tr>
							</table>
							</s:if>
							<s:else>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
									<td width="20px">&nbsp;</td>
									<s:if test="%{dataSource.id == null}">
										<s:if test="%{writeDisabled == ''}">
											<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
										</s:if>
									</s:if>
									<s:else>
										<s:if test="%{updateDisabled == ''}">
											<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
										</s:if>
									</s:else>
									 
								</tr>
							</table>
							</s:else>
							 
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</div>
			</s:else>
						
			<s:if test="%{jsonMode == true && contentShownInDlg == true}">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
			</s:if>
			<s:else>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
			</s:else>
				<tr>
					<td><tiles:insertDefinition name="notes" /></td>
				</tr>
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td>
			
			<div class="area-wrap">
			<fieldset style="margin-bottom:10px;">
				<legend>General Settings</legend>
					<ul class="mdm-set">
						<!-- General -->
						<li class="mdm-set-item">
							<p class="mdm-set-item-title mdm-set-item-title-normal mdm-set-item-title-reverse" data-type="on"></p>
							<div class="mdm-set-item-con-wrap first">
								<div class="mdm-set-item-content first">
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<tbody>
										<tr>
											<td width="40%">Name <span class="red">*</span></td>
											<td><s:textfield name="displayName" id="" cssClass="w170" maxlength="32" disabled="%{disabledName}"/>&nbsp;<s:text name="config.name.range"/>

											<input type="hidden" name="displayNameHid" value="<s:property value="%{displayName}"/>">
											</td>
										</tr>
										<tr>
											<td width="40%">User Profile Attribute<span class="red">*</span></td>
											<td><s:textfield name="userAttributeNum" id="attributeValue" cssClass="w170" maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp;<s:text name="config.userprofile.attrubute.range"/>
											</td>
										</tr>
										<tr>
											<td>Organization <span class="red">*</span></td>
											<td><s:textfield name="organization" id="" cssClass="w170" /></td>
										</tr>
										<tr style="display:none;">
											<td>Security</td>
											<td>
												<s:select name="security" id="" cssClass="w170 J-tag-select" data-name="GeneralSecurity"
												list="#{'Never':'Never','WithAuthentication':'With Authentication','Always':'Always'}" 
												listKey="key" listValue="value"/>
											</td>
										</tr>
										<s:if test="%{removalPasscodeProfileInfo != null}">
										<tr class="J-tag-select-con" data-name="GeneralSecurity" data-type="WithAuthentication" style="display:none;">
											<td style="display:none;">Authorization Password</td>
											<td style="display:none;">
												<s:password name="removalPasscodeProfileInfo.removalPassword" cssClass="w170" showPassword="true"/>
											</td>
										</tr>
										</s:if>
										<s:else>
										<tr class="J-tag-select-con" data-name="GeneralSecurity" data-type="WithAuthentication" style="display:none;">
											<td style="display:none;">Authorization Password</td>
											<td style="display:none;">
												<s:password name="removalPasscodeProfileInfo.removalPassword" cssClass="w170" showPassword="true"/>
											</td>
										</tr>
										</s:else>
										<tr>
											<td>Description</td>
											<td>
												<s:textarea name="description" id="" maxlength="64" cssClass="r-textarea"/>&nbsp;<s:text name="config.description.range" />
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							</div>
						</li>
					</ul>
			</fieldset>
			<fieldset>
					<legend>
						Profile Lifetime on Client Devices
					</legend>
					<p class="mt10">
						<table>
							<tr>
								<td><s:text name="config.secutity.mdmProfiles.validType.deleteAfter"/></td>
							</tr>
							<tr id="deleteOp1">
								<td style="padding-left:20px;"><s:radio label="Gender" name="validTimeInfo.validType" onclick="validSettingChanged(this);" list="%{deleteOption1}" listKey="key" listValue="value"/></td>
							</tr>
							<tr id="deleteOp2">
								<td style="padding-left:20px;"><s:radio label="Gender"	name="validTimeInfo.validType" onclick="validSettingChanged(this);" list="%{deleteOption2}" listKey="key" listValue="value"/>
								<s:textfield name="validTimeInfo.keepTime" size="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" maxlength="32" disabled="%{!(validTimeInfo.validType == 1)}"/> &nbsp;<s:text name="config.secutity.mdmProfiles.minutes" /></td>
							</tr>
						</table>
					</p>
					<p class="mt10" style="display:none;">
						<s:radio label="Gender"	name="validTimeInfo.validType" onclick="validSettingChanged(this);" list="%{validTimeOption3}" listKey="key" listValue="value"/>
						<s:select name="startTime" 
						list="#{'0 0 0':'0:00','0 0 1':'1:00','0 0 2':'2:00','0 0 3':'3:00','0 0 4':'4:00','0 0 5':'5:00','0 0 6':'6:00','0 0 7':'7:00','0 0 8':'8:00','0 0 9':'9:00','0 0 10':'10:00','0 0 11':'11:00'}" 
						listKey="key" listValue="value" disabled="%{!(validTimeInfo.validType == 2)}"/>
						<s:text name="config.secutity.mdmProfiles.validType.am" />
						<s:select name="endTime" 
						list="#{'0 0 12':'0:00','0 0 13':'1:00','0 0 14':'2:00','0 0 15':'3:00','0 0 16':'4:00','0 0 17':'5:00','0 0 18':'6:00','0 0 19':'7:00','0 0 20':'8:00','0 0 21':'9:00','0 0 22':'10:00','0 0 23':'11:00'}" 
						listKey="key" listValue="value" disabled="%{!(validTimeInfo.validType == 2)}"/>
						<s:text name="config.secutity.mdmProfiles.validType.pm" />
						<s:select name="validDay" 
						list="#{'-1':'everyday','0':'every working day','1':'everyMonday','2':'erery Tuesday','3':'every Wednesday','4':'every Thursday','5':'every Friday','6':'every Saturday','7':'every Sunday'}" listKey="key" listValue="value" 
							cssStyle="width: 120px;" disabled="%{!(validTimeInfo.validType == 2)}"/>
					</p>
			</fieldset>
			<fieldset class="mt10">
					<legend>					
						<s:text name="config.security.mdmProfiles.mdm.setting"/>
					</legend>
					<ul class="mdm-set">
						<!-- Passcode -->
						<li class="mdm-set-item" data-name="passcode">
							<p class="mdm-set-item-title mdm-set-item-title-normal" data-type="off">Passcode</p>
							<div class="mdm-set-item-con-wrap">
							<div class="mdm-set-item-config" style="display:<s:property value="%{passcodeDisplayStyle}"/>"><span>Configure</span></div>
							<s:if test="%{passcodeProfileInfos != null && passcodeProfileInfos.size()>0}">
							<s:iterator value="passcodeProfileInfos" status="e" id="passcode">
							<div class="mdm-set-item-content">
								<p class="mdm-set-item-ar"><span class="mdm-item-action" data-type="del">-</span></p>
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<tbody>
										<tr>
											<td width="55%">Allow simple value</td>
											<td><s:checkbox name="passcodeProfileInfos[%{#e.index}].allowSimple"/></td>
										</tr>						
										<tr>
											<td>Require alphanumeric value</td>
											<td><s:checkbox name="passcodeProfileInfos[%{#e.index}].requireAlphanumeric" /></td>
										</tr>												
										<tr>
											<td>Minimum passcode length</td>
											<td>
												<s:select name="passcodeProfileInfos[%{#e.index}].minLength"
												list="#{'-1':'--','1':'1','2':'2','3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9','10':'10','11':'11','12':'12','13':'13','14':'14','15':'15','16':'16'}" 
												listKey="key" listValue="value" cssClass="w170"/>
											</td>
										</tr>						
										<tr>
											<td>Minimum number of complex characters</td>
											<td>
												<s:select name="passcodeProfileInfos[%{#e.index}].minComplexChars"
												list="#{'-1':'--','1':'1','2':'2','3':'3','4':'4'}" 
												listKey="key" listValue="value" cssClass="w170"/>
											</td>
										</tr>					
										<tr>
											<td>Maximum Passcode Age</td>
											<td><s:textfield name="passcodeProfileInfos[%{#e.index}].maxPINAgeInDays" cssClass="w170 J-required" data-rules="{required:true,digits:true}"/><br></td>
										</tr>			
										<tr>
											<td>Auto-Lock</td>
											<td>
												<s:select name="passcodeProfileInfos[0].maxInactivity"
												list="#{'-1':'--','1':'1','2':'2','3':'3','4':'4','5':'5','10':'10','15':'15'}" 
												listKey="key" listValue="value" cssClass="w170"/>
											</td>
										</tr>						
										<tr>
											<td>Passcode history</td>
											<td><s:textfield name="passcodeProfileInfos[%{#e.index}].pinHistory" cssClass="w170 J-required" data-rules="{required:true,digits:true}"/></td>
										</tr>				 					
										<tr>
											<td>Grace period for device lock</td>
											<td>
												<s:select name="passcodeProfileInfos[%{#e.index}].maxGracePeriod" 
												list="#{'-1':'None','0':'Immediately','1':'One Minute','5':'Five Minutes','15':'Fifteen Minutes','60':'One Hour','240':'Four Hours'}" 
												listKey="key" listValue="value" cssClass="w170"/>
											</td>
										</tr>						
										<tr>
											<td>Maximum number of failed attempts</td>
											<td>
												<s:select name="passcodeProfileInfos[%{#e.index}].maxFailedAttempts" 
												list="#{'-1':'--','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9','10':'10'}" 
												listKey="key" listValue="value" cssClass="w170"/>
											</td>
										</tr>						
									</tbody>
								</table>
							</div>
							</s:iterator>
							</s:if>
							</div>
						</li>
						<!-- Restrictions -->
						<li class="mdm-set-item" data-name="restrictions">
							<p class="mdm-set-item-title mdm-set-item-title-normal" data-type="off">Restrictions</p>
							 
							<div class="mdm-set-item-con-wrap">
							
							<div class="mdm-set-item-config" style="display:<s:property value="%{restrictDisplayStyle}"/>"><span>Configure</span></div>
						<s:if test="%{restrictionsProfileInfos != null && restrictionsProfileInfos.size()>0}">
							
							<s:iterator value="restrictionsProfileInfos" status="e" id="restrict">
							
							 
							<div class="mdm-set-item-content">
								<p class="mdm-set-item-ar"><span class="mdm-item-action" data-type="del">-</span></p>
								
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<caption>Device Functionality</caption>
									<tbody>
										<tr>
											<td width="55%">Allow installing apps</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowAppInstallation"/></td>
										</tr>
										<tr>
											<td>Allow use of camera</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowCamera" cssClass="J-tag-checkbox" data-type="camera" /></td>
										</tr>
										<s:if test="allowCamera == true">
											<tr class="J-camera">
											<td style="padding-left:28px;">Allow FaceTime</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowVideoConferencing"/></td>
											</tr>
										</s:if>
										<s:else>
											<tr class="J-camera">
											<td style="padding-left:28px;">Allow FaceTime</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowVideoConferencing" disabled="true"/></td>
											</tr>
										</s:else>
										
										<tr>
											<td>Allow screen capture</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowScreenShot"/></td>
										</tr>
										<tr>
											<td>Allow automatic sync while roaming</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowGlobalBackgroundFetchWhenRoaming"/></td>
										</tr>
										<tr>
											<td>Allow Siri</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowAssistant" cssClass="J-tag-checkbox" data-type="siri" /></td>
										</tr>
										<s:if test="allowAssistant">
											<tr class="J-siri">
											<td style="padding-left:28px;">Allow Siri while device locked</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowAssistantWhileLocked"/></td>
											</tr>
										</s:if>
										<s:else>
											<tr class="J-siri">
											<td style="padding-left:28px;">Allow Siri while device locked</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowAssistantWhileLocked" disabled="true"/></td>
											</tr>
										</s:else>
										
										<tr>
											<td>Allow voice dialing</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowVoiceDialing"/></td>
										</tr>
										<tr>
											<td>Allow In-App Purchases</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowInAppPurchases"/></td>
										</tr>
										<tr>
											<td>Force user to enter iTunes Store password for all purchases</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].forceITunesStorePasswordEntry"/></td>
										</tr>
										<tr>
											<td>Allow multiplayer gaming</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowMultiplayerGaming"/></td>
										</tr>
										<tr>
											<td>Allow adding Game Center friends</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowAddingGameCenterFriends"/></td>
										</tr>
									</tbody>
								</table>
								
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<caption>Applications</caption>
									<tbody>
										<tr>
											<td width="55%">Allow use of YouTube(only support iOS versions prior to 6.0)</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowYouTube"/></td>
										</tr>
										<tr>
											<td>Allow use of iTunes</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowiTunes"/></td>
										</tr>
										<tr>
											<td>Allow use of Safari</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowSafari" cssClass="J-tag-checkbox" data-type="safari" /></td>
										</tr>
										<s:if test="allowSafari == true">
											<tr class="J-safari">
											<td style="padding-left:28px;">Enable autofill</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].safariAllowAutoFill"/></td>
										</tr>
										<tr class="J-safari">
											<td style="padding-left:28px;">Force fraud warning</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].safariForceFraudWarning" /></td>
										</tr>
										<tr class="J-safari">
											<td style="padding-left:28px;">Enable JavaScript</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].safariAllowJavaScript"/></td>
										</tr>
										<tr class="J-safari">
											<td style="padding-left:28px;">Block pop-ups</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].safariAllowPopups"/></td>
										</tr>
										</s:if>
										<s:else>
											<tr class="J-safari">
											<td style="padding-left:28px;">Enable autofill</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].safariAllowAutoFill" disabled="true"/></td>
										</tr>
										<tr class="J-safari">
											<td style="padding-left:28px;">Force fraud warning</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].safariForceFraudWarning" disabled="true"/></td>
										</tr>
										<tr class="J-safari">
											<td style="padding-left:28px;">Enable JavaScript</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].safariAllowJavaScript" disabled="true"/></td>
										</tr>
										<tr class="J-safari">
											<td style="padding-left:28px;">Block pop-ups</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].safariAllowPopups" disabled="true"/></td>
										</tr>
										</s:else>
										<tr>
											<td>Accept cookies</td>
											<td>
												<s:select name="restrictionsProfileInfos[%{#e.index}].safariAcceptCookies" cssClass="w170"
												list="#{'0':'Never','1':'From Visited Sites','2':'Always'}"
												listKey="key" listValue="value"/>
											</td>
										</tr>
									</tbody>
								</table>
								
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<caption>iCloud</caption>
									<tbody>
										<tr>
											<td width="55%">Allow backup</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowCloudBackup"/></td>
										</tr>
										<tr>
											<td>Allow document sync</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowCloudDocumentSync"/></td>
										</tr>
										<tr>
											<td>Allow Photo Stream(disallowing can cause data loss)</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowPhotoStream"/></td>
										</tr>
									</tbody>
								</table>
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<caption>Security and Privacy</caption>
									<tbody>
										<tr>
											<td width="55%">Allow diagnostic data to be sent to Apple</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowDiagnosticSubmission"/></td>
										</tr>
										<tr>
											<td>Allow user to accept untrusted TLS certificates</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowUntrustedTLSPrompt"/></td>
										</tr>
										<tr>
											<td>Force encrypted backup</td>
											<td><s:checkbox name="restrictionsProfileInfos[%{#e.index}].forceEncryptedBackup" /></td>
										</tr>
									</tbody>
								</table>
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
										<caption>Content Ratings</caption>
										<tbody>
											<tr>
												<td width="55%">The region for ratings</td>
												<td>
												<s:select name="restrictionsProfileInfos[%{#e.index}].ratingRegion" id="resContentRegionRating"
												list="#{'us':'United States','au':'Australia','ca':'Canada','de':'Germany','fr':'France','ie':'Ireland','jp':'Japan','nz':'New Zealand','gb':'United Kingdom'}"
												listKey="key" listValue="value" cssClass="w170"/>
											</td>
											</tr>
											<tr>
												<td>Allow explicit music and podcasts</td>
												<td>
													<s:checkbox name="restrictionsProfileInfos[%{#e.index}].allowExplicitContent" />
												</td>
											</tr>
											<tr>
												<td>Movies</td>
												<s:if test="choiceRatingShow == 'us'">
													<td id="movieSelectId">
														<s:select name="restrictionsProfileInfos[%{#e.index}].ratingMovies" list="%{usMovieParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'au'">
													<td id="movieSelectId">
														<s:select  name="restrictionsProfileInfos[%{#e.index}].ratingMovies" list="%{auMovieParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'ca'">
													<td id="movieSelectId">
														<s:select  name="restrictionsProfileInfos[%{#e.index}].ratingMovies" list="%{caMovieParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'de'">
													<td id="movieSelectId">
														<s:select  name="restrictionsProfileInfos[%{#e.index}].ratingMovies" list="%{deMovieParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'fr'">
													<td id="movieSelectId">
														<s:select  name="restrictionsProfileInfos[%{#e.index}].ratingMovies" list="%{frMovieParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'ie'">
													<td id="movieSelectId">
														<s:select  name="restrictionsProfileInfos[%{#e.index}].ratingMovies" list="%{ieMovieParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'jp'">
													<td id="movieSelectId">
														<s:select  name="restrictionsProfileInfos[%{#e.index}].ratingMovies" list="%{jpMovieParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'nz'">
													<td id="movieSelectId">
														<s:select  name="restrictionsProfileInfos[%{#e.index}].ratingMovies" list="%{nzMovieParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'gb'">
													<td id="movieSelectId">
														<s:select  name="restrictionsProfileInfos[%{#e.index}].ratingMovies" list="%{gbMovieParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
											</tr>
											<tr>
												<td>TV shows</td>
												<s:if test="choiceRatingShow == 'us'">
													<td id="tvShowSelectId">
														<s:select name="restrictionsProfileInfos[%{#e.index}].ratingTVShows" list="%{usTvParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'au'">
													<td id="tvShowSelectId">
														<s:select name="restrictionsProfileInfos[%{#e.index}].ratingTVShows" list="%{auTvParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'ca'">
													<td id="tvShowSelectId">
														<s:select name="restrictionsProfileInfos[%{#e.index}].ratingTVShows" list="%{caTvParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'de'">
													<td id="tvShowSelectId">
														<s:select name="restrictionsProfileInfos[%{#e.index}].ratingTVShows" list="%{deTvParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'fr'">
													<td id="tvShowSelectId">
														<s:select name="restrictionsProfileInfos[%{#e.index}].ratingTVShows" list="%{frTvParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'ie'">
													<td id="tvShowSelectId">
														<s:select name="restrictionsProfileInfos[%{#e.index}].ratingTVShows" list="%{ieTvParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'jp'">
													<td id="tvShowSelectId">
														<s:select name="restrictionsProfileInfos[%{#e.index}].ratingTVShows" list="%{jpTvParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'nz'">
													<td id="tvShowSelectId">
														<s:select name="restrictionsProfileInfos[%{#e.index}].ratingTVShows" list="%{nzTvParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
												<s:if test="choiceRatingShow == 'gb'">
													<td id="tvShowSelectId">
														<s:select name="restrictionsProfileInfos[%{#e.index}].ratingTVShows" list="%{gbTvParams}" listKey="keyCode" listValue="valueCode" cssClass="w170"/>
													</td>
												</s:if>
											</tr>
	 										<tr>
												<td>Apps</td>
												<td id="appSelectId">
													<s:select name="restrictionsProfileInfos[%{#e.index}].ratingApps" 
													list='#{"0":"Don\'t Allow Apps","100":"4+","200":"9+","300":"12+","600":"17+","1000":"Allow All Apps"}'
													listKey="key" listValue="value" cssClass="w170"/>
												</td>
											</tr>
										</tbody>
								</table>
							</div>
							</s:iterator>
							</s:if>
							</div>
						</li>
						<!-- Web Clips -->
						<li class="mdm-set-item" data-name="webclips">
							<p class="mdm-set-item-title mdm-set-item-title-normal" data-type="off">Web Clips</p>
							<div class="mdm-set-item-con-wrap">
							<div class="mdm-set-item-config" style="display:<s:property value="%{webclipsDisplayStyle}"/>"><span>Configure</span></div>
							<s:if test="%{webClipProfileInfos != null && webClipProfileInfos.size()>0}">
							<s:iterator value="webClipProfileInfos" status="e" id="webclips">
							<div class="mdm-set-item-content">
								<p class="mdm-set-item-ar"><span class="mdm-item-action" data-type="add">+</span>&ensp;<span class="mdm-item-action" data-type="del">-</span></p>
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<s:hidden name="webClipProfileInfos[%{#e.index}].webClipId"/>
									<tbody>
										<tr>
											<td width="55%">Label <span class="red">*</span></td>
											<td><s:textfield name="webClipProfileInfos[%{#e.index}].label" class="w170 J-required" /></td>
										</tr>
										<tr>
											<td>URL <span class="red">*</span></td>
											<td><s:textfield name="webClipProfileInfos[%{#e.index}].url" class="w170 J-required" data-rules="{required:true,digits:true}" /></td>
										</tr>
										<tr>
										<tr>
											<td>Removable</td>
											<td><s:checkbox name="webClipProfileInfos[%{#e.index}].removable" /></td>
										</tr>
										<tr>
											<td>Precomposed Icon</td>
											<td><s:checkbox name="webClipProfileInfos[%{#e.index}].precomposed" /></td>
										</tr>
										<tr>
											<td>Full Screen</td>
											<td><s:checkbox name="webClipProfileInfos[%{#e.index}].fullScreen" /></td>
										</tr>
										<tr>
											<td>Icon</td>
											<td><input type="file" name="icons" id="" onChange="showIcon(this);"/>
												<s:hidden id="haveIcons" name="haveIcons" value="0"/>
											</td>
										</tr>
										<tr>
											<td></td>
											<td text-align="left">
												<s:if test="iconStr != null">
													<img alt="Icon" vertical-align="middle" src="data:image/png;base64,<s:property value='iconStr'/>"/>
												</s:if>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							</s:iterator>
							</s:if>
							</div>
						</li>
						<li class="mdm-set-item" data-name="scep" >
							<p class="mdm-set-item-title mdm-set-item-title-normal" data-type="off">Mail, Contacts, Calendars</p>
							<div class="mdm-set-item-con-wrap">
								<ul class="mdm-set">
									<!-- Email -->
						<li class="mdm-set-item" data-name="email" style="padding-left:20px;">
							<p class="mdm-set-item-title mdm-set-item-title-normal" data-type="off">Email</p>
							<div class="mdm-set-item-con-wrap">
							<div class="mdm-set-item-config" style="display:<s:property value="%{emailDisplayStyle}"/>"><span>Configure</span></div>
							<s:if test="%{emailProfileInfos != null && emailProfileInfos.size()>0}">
							<s:iterator value="emailProfileInfos" status="e" id="email">
							<div class="mdm-set-item-content">
								<p class="mdm-set-item-ar"><span class="mdm-item-action" data-type="add">+</span>&ensp;<span class="mdm-item-action" data-type="del">-</span></p>
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<caption>Email</caption>
									<tbody>
										<tr>
											<td width="55%">Account Description</td>
											<td><s:textfield name="emailProfileInfos[%{#e.index}].accountDescription" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Account Type</td>
											<td>
												<s:select cssClass="w170 J-tag-select" name="emailProfileInfos[%{#e.index}].accountType" data-name="EmailAccountType"
												list="#{'EmailTypeIMAP':'IMAP','EmailTypePOP':'POP'}" 
												listKey="key" listValue="value"/>
											</td>
										</tr>
										<tr class="J-tag-select-con" data-name="EmailAccountType" data-type="EmailTypeIMAP">
											<td>Path Prefix</td>
											<td><s:textfield name="emailProfileInfos[%{#e.index}].incomingMailServerIMAPPathPrefix" id="" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>User Display Name</td>
											<td><s:textfield name="emailProfileInfos[%{#e.index}].accountName" id="" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Email Address</td>
											<td><s:textfield name="emailProfileInfos[%{#e.index}].address" id="" cssClass="w170 J-required" data-rules="{email:true}"/></td>
										</tr>
										<tr>
											<td>Allow messages to be moved between email accounts</td>
											<td><s:checkbox name="emailProfileInfos[%{#e.index}].preventMove" id="" /></td>
										</tr>
									</tbody>
								</table>
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<caption>Incoming Mail</caption>
									<tbody>
										<tr>
											<td width="55%">Mail Server <span class="red">*</span></td>
											<td><s:textfield name="emailProfileInfos[%{#e.index}].incomingMailServerHostName" id="" cssClass="w170 J-required" /></td>
										</tr>
										<tr>
											<td>Port</td>
											<td><s:textfield name="emailProfileInfos[%{#e.index}].incomingMailServerPortNumber" id="" cssClass="w170 J-required" data-rules="{digits:true}" /></td>
										</tr>
										<tr>
											<td>User Name</td>
											<td><s:textfield name="emailProfileInfos[%{#e.index}].incomingMailServerUsername" id="" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Authentication Type</td>
											<td>
												<s:select name="emailProfileInfos[%{#e.index}].incomingMailServerAuthentication" cssClass="w170 J-tag-select" data-name="EmailAuthenticationType"
													list="#{'EmailAuthNone':'None','EmailAuthPassword':'Password','EmailAuthCRAMMD5':'MD5 Challenge-Response','EmailAuthNTLM':'NTLM','EmailAuthHTTPMD5':'HTTP MD5 Digest'}" 
												listKey="key" listValue="value"/>
											</td>		
										</tr>
										<s:if test="incomingMailServerAuthentication == 'EmailAuthNone'">
										<tr style="display:none;" class="J-tag-select-con" data-name="EmailAuthenticationType" data-type="EmailAuthPassword EmailAuthCRAMMD5 EmailAuthNTLM EmailAuthHTTPMD5">
											<td>Password</td>
											<td>
											<s:password name="emailProfileInfos[%{#e.index}].incomingPassword" cssClass="w170 incomingPasswordText" showPassword="true"/>
											</td>
										</tr>
										</s:if>
										<s:else>
											<tr class="J-tag-select-con" data-name="EmailAuthenticationType" data-type="EmailAuthPassword EmailAuthCRAMMD5 EmailAuthNTLM EmailAuthHTTPMD5">
											<td>Password</td>
											<td>
											<s:password name="emailProfileInfos[%{#e.index}].incomingPassword" cssClass="w170 incomingPasswordText" showPassword="true"/>
											</td>
										</tr>
										</s:else>
										<tr>
											<td>Use SSL</td>
											<td><s:checkbox name="emailProfileInfos[%{#e.index}].incomingMailServerUseSSL" id="" /></td>
										</tr>
									</tbody>
								</table>	
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<caption>Outgoing Mail</caption>
									<tbody>
										<tr>
											<td width="55%">Mail Server <span class="red">*</span></td>
											<td><s:textfield name="emailProfileInfos[%{#e.index}].outgoingMailServerHostName" id="" cssClass="w170 J-required" /></td>
										</tr>
										<tr>
											<td>Port</td>
											<td><s:textfield name="emailProfileInfos[%{#e.index}].outgoingMailServerPortNumber" id="" cssClass="w170 J-required" data-rules="{digits:true}"/></td>
										</tr>
										<tr>
											<td>User Name</td>
											<td><s:textfield name="emailProfileInfos[%{#e.index}].outgoingMailServerUsername" id="" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Authentication Type</td>
											<td>
												<s:select name="emailProfileInfos[%{#e.index}].outgoingMailServerAuthentication" cssClass="w170 J-tag-select" data-name="EmailAuthenticationTypeOut" 
												list="#{'EmailAuthNone':'None','EmailAuthPassword':'Password','EmailAuthCRAMMD5':'MD5 Challenge-Response','EmailAuthNTLM':'NTLM','EmailAuthHTTPMD5':'HTTP MD5 Digest'}" 
												listKey="key" listValue="value"/>
											</td>
										</tr>
										<s:if test="outgoingMailServerAuthentication == 'EmailAuthNone'">
										<tr style="display:none;" class="J-tag-select-con" data-name="EmailAuthenticationTypeOut" data-type="EmailAuthPassword EmailAuthCRAMMD5 EmailAuthNTLM EmailAuthHTTPMD5">
											<td>Password</td>
											<td>
											<s:password name="emailProfileInfos[%{#e.index}].outgoingPassword" cssClass="w170 outgoingPasswordText" showPassword="true"/>
											</td>
										</tr>
										</s:if>
										<s:else>
											<tr class="J-tag-select-con" data-name="EmailAuthenticationTypeOut" data-type="EmailAuthPassword EmailAuthCRAMMD5 EmailAuthNTLM EmailAuthHTTPMD5">
											<td>Password</td>
											<td>
											<s:password name="emailProfileInfos[%{#e.index}].outgoingPassword" cssClass="w170 outgoingPasswordText" showPassword="true"/>
											</td>
										</tr>
										</s:else>
										<tr class="J-tag-select-con" data-name="EmailAuthenticationTypeOut" data-type="EmailAuthPassword EmailAuthCRAMMD5 EmailAuthNTLM EmailAuthHTTPMD5">
											<td>Outgoing Password Same As Incoming</td>
											<td><s:checkbox name="emailProfileInfos[%{#e.index}].outgoingPasswordSameAsIncomingPassword" cssClass="passwordSameCheckbox" id=""/></td>
										</tr>
										<tr>
											<td>Use Only in Mail</td>
											<td><s:checkbox name="emailProfileInfos[%{#e.index}].preventAppSheet" id="" /></td>
										</tr>
										<tr>
											<td>Use SSL</td>
											<td><s:checkbox name="emailProfileInfos[%{#e.index}].outgoingMailServerUseSSL" id="" /></td>
										</tr>
										<tr>
											<td>Use S/MIME</td>
											<td><s:checkbox name="emailProfileInfos[%{#e.index}].smimeEnabled" id="" /></td>
										</tr>
									</tbody>
								</table>
							</div>
							</s:iterator>
							</s:if>
							</div>
						</li>	
							<!-- Exchange ActiveSync -->
						<li class="mdm-set-item" data-name="exchangeactivesync" style="padding-left:20px;">
							<p class="mdm-set-item-title mdm-set-item-title-normal scepSelect" data-type="off">Exchange ActiveSync</p>
							<div class="mdm-set-item-con-wrap">
							<div class="mdm-set-item-config scepSelect"  style="display:<s:property value="%{exchangeDisplayStyle}"/>"><span>Configure</span></div>
							<s:if test="%{exchangeProfileInfos != null && exchangeProfileInfos.size()>0}">
							<s:iterator value="exchangeProfileInfos" status="e" id="caldav">	
							<div class="mdm-set-item-content">
								<p class="mdm-set-item-ar"><span class="mdm-item-action scepSelect" data-type="add">+</span>&ensp;<span class="mdm-item-action" data-type="del">-</span></p>
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<tbody>
										<tr>
											<td width="55%">Account Name</td>
											<td><s:textfield name="exchangeProfileInfos[%{#e.index}].displayName" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Exchange ActiveSync Host <span class="red">*</span></td>
											<td><s:textfield name="exchangeProfileInfos[%{#e.index}].host" cssClass="w170 J-required" /></td>
										</tr>
										<tr>
											<td>Allow messages to be moved between email accounts</td>
											<td><s:checkbox name="exchangeProfileInfos[%{#e.index}].preventMove"/></td>
										</tr>
										<tr>
											<td>Use Only in Mail</td>
											<td><s:checkbox name="exchangeProfileInfos[%{#e.index}].preventAppSheet" /></td>
										</tr>
										<tr>
											<td>Use SSL</td>
											<td><s:checkbox name="exchangeProfileInfos[%{#e.index}].ssl" /></td>
										</tr>
										<tr>
											<td>Use S/MIME</td>
											<td><s:checkbox name="exchangeProfileInfos[%{#e.index}].smimeEnabled" /></td>
										</tr>
										<tr>
											<td>Domain User</td>
											<td><s:textfield name="exchangeProfileInfos[%{#e.index}].userName" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Email Address</td>
											<td><s:textfield name="exchangeProfileInfos[%{#e.index}].emailAddress" cssClass="w170 J-required" data-rules="{email:true}"/></td>
										</tr>
										<tr>
											<td>Password</td>
											<td>
											<s:password name="exchangeProfileInfos[%{#e.index}].password" cssClass="w170" showPassword="true"/>
											</td>
										</tr>
										<tr>
											<td>Past Days of Mail to Sync</td>
											<td>												
												<s:select name="exchangeProfileInfos[%{#e.index}].mailNumberOfPastDaysToSync" 
												list="#{'0':'Unlimited','1':'One Day','3':'Three Days','7':'One Week','14':'Two Weeks','31':'One Month'}" 
												listKey="key" listValue="value" cssClass="w170"/>
											</td>
										</tr>
										<%-- <tr>
											<td>Certificate</td>
											<td>
												<s:select name="exchangeProfileInfos[%{#e.index}].certificateUrl" 
												list="#{'None':'None'}" 
												listKey="key" listValue="value" cssClass="w170 scepList"/>
												<s:hidden name="exchangeProfileInfos[%{#e.index}].certificateUrlHid"/>
											</td>
										</tr> --%>
									</tbody>
								</table>
							</div>
							</s:iterator>
							</s:if>
							</div>
						</li>
								<!-- CalDAV -->
						<li class="mdm-set-item" data-name="caldav" style="padding-left:20px;">
							<p class="mdm-set-item-title mdm-set-item-title-normal" data-type="off">CalDAV</p>
							<div class="mdm-set-item-con-wrap">
							<div class="mdm-set-item-config" style="display:<s:property value="%{caldavDisplayStyle}"/>"><span>Configure</span></div>
							<s:if test="%{calDavProfileInfos != null && calDavProfileInfos.size()>0}">
							<s:iterator value="calDavProfileInfos" status="e" id="caldav">		 
							<div class="mdm-set-item-content">
								<p class="mdm-set-item-ar"><span class="mdm-item-action" data-type="add">+</span>&ensp;<span class="mdm-item-action" data-type="del">-</span></p>
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<tbody>
										<tr>
											<td width="55%">Description</td>
											<td><s:textfield name="calDavProfileInfos[%{#e.index}].accountDescription" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Host Name <span class="red">*</span></td>
											<td>
											<s:textfield name="calDavProfileInfos[%{#e.index}].hostName" cssClass="w170 J-required" /></td>
										</tr>
										<tr>
											<td>Port</td>
											<td>
											<s:textfield name="calDavProfileInfos[%{#e.index}].port" cssClass="w170 J-required" data-rules="{digits:true}" /></td>
										</tr>
										<tr>
											<td>Principal URL</td>
											<td>
											<s:textfield name="calDavProfileInfos[%{#e.index}].principalURL" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>User Name</td>
											<td>
											<s:textfield name="calDavProfileInfos[%{#e.index}].username" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Password</td>
											<td>
												<s:password name="calDavProfileInfos[%{#e.index}].password" cssClass="w170" showPassword="true"/>
											</td>
										</tr>
										<tr>
											<td>Use SSL</td>
											<td>
											<s:checkbox name="calDavProfileInfos[%{#e.index}].useSSL"/></td>
										</tr>
									</tbody>
								</table>
							</div>
							</s:iterator>
							</s:if>
							</div>
						</li>
						<!-- CardDAV -->
						<li class="mdm-set-item" data-name="carddav" style="padding-left:20px;">
							<p class="mdm-set-item-title mdm-set-item-title-normal" data-type="off">CardDAV</p>
							<div class="mdm-set-item-con-wrap">
							<div class="mdm-set-item-config" style="display:<s:property value="%{carddavDisplayStyle}"/>"><span>Configure</span></div>
							<s:if test="%{cardDavProfileInfos != null && cardDavProfileInfos.size()>0}">
							<s:iterator value="cardDavProfileInfos" status="e" id="carddav">
							<div class="mdm-set-item-content">
								<p class="mdm-set-item-ar"><span class="mdm-item-action" data-type="add">+</span>&ensp;<span class="mdm-item-action" data-type="del">-</span></p>
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<tbody>
										<tr>
											<td width="55%">Description</td>
											<td><s:textfield name="cardDavProfileInfos[%{#e.index}].accountDescription" id="" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Host Name <span class="red">*</span></td>
											<td><s:textfield name="cardDavProfileInfos[%{#e.index}].hostName" id="" cssClass="w170 J-required" /></td>
										</tr>
										<tr>
											<td>Port</td>
											<td><s:textfield name="cardDavProfileInfos[%{#e.index}].port" id="" cssClass="w170 J-required" data-rules="{digits:true}" /></td>
										</tr>
										<tr>
											<td>Principal URL</td>
											<td><s:textfield name="cardDavProfileInfos[%{#e.index}].principalURL" id="" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>User Name</td>
											<td><s:textfield name="cardDavProfileInfos[%{#e.index}].username" id="" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Password</td>
											<td>
											<s:password name="cardDavProfileInfos[%{#e.index}].password" cssClass="w170" showPassword="true"/>
											</td>
										</tr>
										<tr>
											<td>Use SSL</td>
											<td><s:checkbox name="cardDavProfileInfos[%{#e.index}].useSSL" id=""/></td>
										</tr>
									</tbody>
								</table>
							</div>
							</s:iterator>
							</s:if>
							</div>
						</li>
								</ul>
							</div>
						</li>
						<li class="mdm-set-item" data-name="scep" >
							<p class="mdm-set-item-title mdm-set-item-title-normal" data-type="off">Advanced Settings</p>
							<div class="mdm-set-item-con-wrap">
								<ul class="mdm-set">
									<!-- VPN -->
						<li class="mdm-set-item" data-name="vpn" style="padding-left:20px;">
							<p class="mdm-set-item-title mdm-set-item-title-normal" data-type="off">VPN</p>
							<div class="mdm-set-item-con-wrap">
							<div class="mdm-set-item-config" style="display:<s:property value="%{vpnDisplayStyle}"/>"><span>Configure</span></div>
					 		<s:if test="%{vpnProfileInfos != null && vpnProfileInfos.size()>0}">
							<s:iterator value="vpnProfileInfos" status="e" id="vpn">
							<div class="mdm-set-item-content">
								<p class="mdm-set-item-ar"><span class="mdm-item-action" data-type="add">+</span>&ensp;<span class="mdm-item-action" data-type="del">-</span></p>
								
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<tbody>
					 					<tr>
											<td width="45%">Connection Name</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].userDefinedName" id="" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Connection Type</td>
											<td>
											<s:select cssClass="w170 J-tag-select" name="vpnProfileInfos[%{#e.index}].vpnType" data-name="VPNConnectionType"
												list="#{'L2TP':'L2TP','PPTP':'PPTP'}"
											    listKey="key" listValue="value"/>
											</td>
										</tr>
										<tr>
											<td>Server <span class="red">*</span></td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].commRemoteAddress" id="" cssClass="w170 J-required" /></td>
										</tr>
										<tr>
											<td>Account</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].authName" id="" cssClass="w170" /></td>
										</tr>						
										<tr class="J-tag-select-con" data-name="VPNConnectionType" data-type="L2TP PPTP">
											<td>User Authentication</td>
											<td>
												<s:select cssClass="w170 J-tag-select" name="vpnProfileInfos[%{#e.index}].authType" data-name="VPNUserAuthentication"
												list="#{'SecurID':'RSA SecurID','Password':'Password'}"              																		                 	
												listKey="key" listValue="value"/>
											</td>
										</tr>
										<s:if test="vpnType == 'L2TP'">
											<s:if test="authType == 'Password'">
												<tr class="J-tag-select-con" data-name="VPNUserAuthentication" data-type="Password" data-sub="Password">
													<td>Password</td>
													<td><s:password name="vpnProfileInfos[%{#e.index}].authPassword" id="" cssClass="w170" showPassword="true"/></td>
												</tr>
												<tr style="display:none;" class="J-tag-select-con" data-name="VPNUserAuthentication" id="J-tag-special-choice" data-type="SecurID" data-sub="SecurID" style="display:none;">
														<td>Shared Secret</td>
														<td><s:password name="vpnProfileInfos[%{#e.index}].sharedSecret" id="" cssClass="w170"  showPassword="true"/></td>
												</tr>
											</s:if>
											<s:if test="authType == 'SecurID'">
													<tr style="display:none;" class="J-tag-select-con" data-name="VPNUserAuthentication" data-type="Password" data-sub="Password">
														<td>Password</td>
														<td><s:password name="vpnProfileInfos[%{#e.index}].authPassword" id="" cssClass="w170" showPassword="true"/></td>
													</tr>
													<tr class="J-tag-select-con" data-name="VPNUserAuthentication" id="J-tag-special-choice" data-type="SecurID" data-sub="SecurID">
														<td>Shared Secret</td>
														<td><s:password name="vpnProfileInfos[%{#e.index}].sharedSecret" id="" cssClass="w170"  showPassword="true"/></td>
													</tr>
											</s:if>
										</s:if>
										<s:if test="vpnType == 'PPTP'">
											<s:if test="authType == 'Password'">
												<tr class="J-tag-select-con" data-name="VPNUserAuthentication" data-type="Password" data-sub="Password">
													<td>Password</td>
													<td><s:password name="vpnProfileInfos[%{#e.index}].authPassword" id="" cssClass="w170" showPassword="true"/></td>
												</tr>
												<tr style="display:none;" class="J-tag-select-con" data-name="VPNUserAuthentication" id="J-tag-special-choice" data-type="SecurID" data-sub="SecurID" style="display:none;">
														<td>Shared Secret</td>
														<td><s:password name="vpnProfileInfos[%{#e.index}].sharedSecret" id="" cssClass="w170"  showPassword="true"/></td>
												</tr>
											</s:if>
											<s:if test="authType == 'SecurID'">
												<tr style="display:none;" class="J-tag-select-con" data-name="VPNUserAuthentication" data-type="Password" data-sub="Password">
													<td>Password</td>
													<td><s:password name="vpnProfileInfos[%{#e.index}].authPassword" id="" cssClass="w170" showPassword="true"/></td>
												</tr>
													<tr class="J-tag-select-con" data-name="VPNUserAuthentication" id="J-tag-special-choice" data-type="SecurID" data-sub="SecurID" style="display:none;">
														<td>Shared Secret</td>
														<td><s:password name="vpnProfileInfos[%{#e.index}].sharedSecret" id="" cssClass="w170"  showPassword="true"/></td>
													</tr>
											</s:if>
										</s:if>
										<s:if test="vpnType == 'PPTP'">
											<tr class="J-tag-select-con" data-name="VPNConnectionType" data-type="PPTP">
											<td>Encryption Level</td>
											<td>
												<s:select name="vpnProfileInfos[%{#e.index}].encryptionLevel" cssClass="w170"
												list="#{'none':'None','automatic':'Automatic','maximumBit':'Maximum Bit'}"
												listKey="key" listValue="value"/>
											</td>
										</tr>
										</s:if>
										<s:else>
											<tr style="display:none;" class="J-tag-select-con" data-name="VPNConnectionType" data-type="PPTP">
											<td>Encryption Level</td>
											<td>
												<s:select name="vpnProfileInfos[%{#e.index}].encryptionLevel" cssClass="w170"
												list="#{'none':'None','automatic':'Automatic','maximumBit':'Maximum Bit'}"
												listKey="key" listValue="value"/>
											</td>
										</tr>
										</s:else>
										<tr class="J-tag-select-con" data-name="VPNConnectionType" data-type="L2TP PPTP">
											<td>Send All Traffic</td>
											<td><s:checkbox name="vpnProfileInfos[%{#e.index}].overridePrimary" id="" /></td>
										</tr>
										<tr>
											<td>Proxy</td>
											<td>
												<s:select cssClass="w170 J-tag-select" name="vpnProfileInfos[%{#e.index}].proxiesType" data-name="VPNProxy"
												 list="#{'None':'None','Manual':'Manual','Auto':'Auto'}" listKey="key" listValue="value" />
											</td>
										</tr>
										<s:if test="proxiesType == 'None'">
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual" style="display:none;">
											<td>Server</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].httpProxy" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual" style="display:none;">
											<td>Port</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].httpPort" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual" style="display:none;">
											<td>Authentication</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].httpProxyUsername" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Auto" style="display:none;">
											<td>Proxy Server URL</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].proxyAutoConfigURLString" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual" style="display:none;">
											<td>Password</td>
											<td><s:password name="vpnProfileInfos[%{#e.index}].httpProxyPassword" id="" cssClass="w170"  showPassword="true"/></td>
										</tr>
										</s:if>
										<s:if test="proxiesType == 'Manual'">
											<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual">
											<td>Server</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].httpProxy" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual">
											<td>Port</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].httpPort" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual">
											<td>Authentication</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].httpProxyUsername" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Auto" style="display:none;">
											<td>Proxy Server URL</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].proxyAutoConfigURLString" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual">
											<td>Password</td>
											<td><s:password name="vpnProfileInfos[%{#e.index}].httpProxyPassword" id="" cssClass="w170"  showPassword="true"/></td>
										</tr>
										</s:if>
										<s:if test="proxiesType == 'Auto'">
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual" style="display:none;">
											<td>Server</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].httpProxy" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual" style="display:none;">
											<td>Port</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].httpPort" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual" style="display:none;">
											<td>Authentication</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].httpProxyUsername" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Auto">
											<td>Proxy Server URL</td>
											<td><s:textfield name="vpnProfileInfos[%{#e.index}].proxyAutoConfigURLString" id="" cssClass="w170" /></td>
										</tr>
										<tr class="J-tag-select-con" data-name="VPNProxy" data-type="Manual" style="display:none;">
											<td>Password</td>
											<td><s:password name="vpnProfileInfos[%{#e.index}].httpProxyPassword" id="" cssClass="w170"  showPassword="true"/></td>
										</tr>
										</s:if>
					 				</tbody>
								</table>
							</div>
					 		</s:iterator>
					 		</s:if>
							</div>
						</li>
						<!-- LDAP -->
						<li class="mdm-set-item" data-name="ldap" style="padding-left:20px;">
							<p class="mdm-set-item-title mdm-set-item-title-normal" data-type="off">LDAP</p>
							<div class="mdm-set-item-con-wrap">
							<div class="mdm-set-item-config" style="display:<s:property value="%{ldapDisplayStyle}"/>"><span>Configure</span></div>
							<s:if test="%{ldapProfileInfos != null && ldapProfileInfos.size()>0}">
							<s:iterator value="ldapProfileInfos" status="e" id="caldav">
							<div class="mdm-set-item-content">
								<p class="mdm-set-item-ar"><span class="mdm-item-action" data-type="add">+</span>&ensp;<span class="mdm-item-action" data-type="del">-</span></p>
								<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
									<tbody>
										<tr>
											<td width="55%">Description</td>
											<td>
											<s:textfield name="ldapProfileInfos[%{#e.index}].accountDescription" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>User Name</td>
											<td>
											<s:textfield name="ldapProfileInfos[%{#e.index}].accountUserName" cssClass="w170" /></td>
										</tr>
										<tr>
											<td>Password</td>
											<td>
											<s:password name="ldapProfileInfos[%{#e.index}].accountPassword" cssClass="w170" showPassword="true"/>
											</td>
										</tr>
										<tr>
											<td>Host Name <span class="red">*</span></td>
											<td>
											<s:textfield name="ldapProfileInfos[%{#e.index}].accountHostName" cssClass="w170 J-required"/></td>
										</tr>
										<tr>
											<td>Use SSL</td>
											<td>
											<s:checkbox name="ldapProfileInfos[%{#e.index}].accountUseSSL" /></td>
										</tr>
									</tbody>
								</table>
							</div>
							</s:iterator>
							</s:if>
							</div>
						</li>
								</ul>
							</div>
						</li>
					</ul>
			</fieldset>
			</div>
			</td>
			</tr>
			</table>
		</s:form>
		</div>
	</div>
	<script type="text/javascript" src="<s:url value="/js/config/txt_configs.js" includeParams="none"/>"></script>
	<script type="text/javascript" src="<s:url value="/js/config/form_configs.js" includeParams="none"/>"></script>
	<script type="text/javascript" src="<s:url value="/js/common/tools.js" includeParams="none"/>"></script>
	<script type="text/javascript" src="<s:url value="/js/app/app.js" includeParams="none"/>"></script>
	<script type="text/javascript">
		AE.Mod.init();
	</script>
</div>