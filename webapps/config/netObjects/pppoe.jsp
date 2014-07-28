<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script src="<s:url value="/js/hm.options.js" />"></script>
<script>
var formName = 'pppoe';

function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_pppoeName").disabled) {
		document.getElementById(formName + "_dataSource_pppoeName").focus();
	}
	<s:if test="%{jsonMode==true}">
		if (top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(800, 450);
		}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		<s:if test="%{jsonMode}">
		    if ('cancel' + '<s:property value="lstForward"/>' == operation) {
				parent.closeIFrameDialog();	
			} else if (operation == 'create<s:property value="lstForward"/>' 
				|| operation == 'update<s:property value="lstForward"/>'
				|| operation == 'create' 
				|| operation == 'update'){
				savePPPoE(operation);
			} else {
				if ('<%=Navigation.L2_FEATURE_PPPOE%>' != operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
					showProcessing();
				}
				document.forms[formName].operation.value = operation;
			    document.forms[formName].submit();
			}
		</s:if>
		<s:else>
			if ('<%=Navigation.L2_FEATURE_PPPOE%>' != operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
				showProcessing();
			}
			document.forms[formName].operation.value = operation;
		    document.forms[formName].submit();
		</s:else>
	}
}

function savePPPoE(operation) {
	if (validate(operation)) {
		var url = "<s:url action='pppoe' includeParams='none' />" + "?jsonMode=true" 
		+ "&ignore=" + new Date().getTime(); 
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["pppoe"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succPPPoE, failure : failPPPoE, timeout: 60000}, null);
	}
}

var succPPPoE = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			if(parentSelectDom != null) {
				if(details.addedId != null && details.addedId != ''){
					hm.util.insertSelectValue(details.addedId, details.addedName, parentSelectDom, false, true);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failPPPoE = function(o) {
	// do nothing now
}

function validate(operation) {
	
    if ((operation == 'create<s:property value="lstForward"/>' || operation == 'create') && !validateName()) {
		return false;
	}
	if ('<%=Navigation.L2_FEATURE_PPPOE%>' == operation || operation == 'cancel' + '<s:property value="lstForward"/>') {
		return true;
	}

	if(operation == 'update<s:property value="lstForward"/>' 
	|| operation == 'create<s:property value="lstForward"/>'
	|| operation == 'update' 
	|| operation == 'create'){
		if(!validateUsername()) {
			return false;
		}
		if(!validatePassword()) {
			return false;
		}
		//if(!validateDomain()) {
		//	return false;
		//}
	}

	return true;
}

function validateUsername(){
	var inputElement = document.getElementById(formName + "_dataSource_username");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.pppoe.username" />');
   	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}
    if (inputElement.value.length < 1 || inputElement.value.length >32) {
        hm.util.reportFieldError(inputElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.pppoe.username" /></s:param><s:param><s:text name="config.ipFilter.name.range" /></s:param></s:text>');
        inputElement.focus();
        return false;
    }
    return true;
}

function validateName() {
    var inputElement = document.getElementById(formName + "_dataSource_pppoeName");
    var message = hm.util.validateName(inputElement.value, '<s:text name="config.pppoe.name" />');
   	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}
    return true;
}

function validatePassword() {
	var passwordElement;
    if (document.getElementById("chkToggleDisplay").checked) {
    	passwordElement = document.getElementById("password");
    } else {
    	passwordElement = document.getElementById("password_text");
    }

	if (passwordElement.value.length ==0) {
		hm.util.reportFieldError(passwordElement, '<s:text name="error.requiredField"><s:param><s:text name="config.pppoe.password" /></s:param></s:text>');
	    passwordElement.focus();
	    return false;
    }

    if (passwordElement.value.length < 1 || passwordElement.value.length >32) {
        hm.util.reportFieldError(passwordElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.pppoe.password" /></s:param><s:param><s:text name="config.ipFilter.name.range" /></s:param></s:text>');
        passwordElement.focus();
        return false;
    }

    var message = hm.util.validatePassword(passwordElement.value, '<s:text name="config.pppoe.password" />');
	if (message != null) {
	    hm.util.reportFieldError(passwordElement, message);
	    passwordElement.focus();
	    return false;
	}
	
    return true;
}

//function validateDomain() {
//	var inputElement = document.getElementById(formName + "_dataSource_domain");
//	if (inputElement.value.length == 0) {
//		return true;
//	}
//   if (inputElement.value.length < 8 || inputElement.value.length >63) {
//        hm.util.reportFieldError(inputElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.pppoe.username" /></s:param><s:param><s:text name="config.pppoe.password.range" /></s:param></s:text>');
//        inputElement.focus();
//       return false;
//   }
//    return true;
//}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="pppoe" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}

</script>
<div id="content"><s:form action="pppoe" id="pppoe">
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
		<div class="topFixedTitle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-access-consoles.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.pppoe.title"/>
								</s:if> <s:else>
									<s:text name="config.pppoe.title.edit"/>
								</s:else>
							</td>
							<td style="padding-left:10px;">
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
										<td width="20px">&nbsp;</td>
										<s:if test="%{dataSource.id == null}">
											<s:if test="%{writeDisabled == 'disabled'}">
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
											</s:if>
											<s:else>
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
											</s:else>
										</s:if>
										<s:else>
											<s:if test="%{writeDisabled == 'disabled'}">
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
											</s:if>
											<s:else>
												<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
											</s:else>
										</s:else>
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
	</div>
	</s:if>
	<s:if test="%{jsonMode==false}">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_PPPOE%>');">
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
	</table>
	</s:if>
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">		
			<table  class="editBox" border="0" cellspacing="0" cellpadding="0" width="660">
				<tr>
					<td height="10"><%-- add this password dummy to fix issue with auto complete function --%>
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
						type="password"></td>
				</tr>
				<tr>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="labelT1" width="145"><s:text name="config.pppoe.name" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield name="dataSource.pppoeName" size="24"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									maxlength="%{nameLength}" disabled="%{disabledName}"/>&nbsp;<s:text
									name="config.ipFilter.name.range" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="145"><s:text name="config.ipFilter.description" /></td>
								<td><s:textfield name="dataSource.description" size="48"
									maxlength="%{descriptionLength}" />&nbsp;<s:text
									name="config.ipFilter.description.range" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding:4px 5px 4px 5px">
					<fieldset><legend><s:text name="config.pppoe.credentials" /></legend>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelT1" width="127">
								<s:text	name="config.pppoe.username" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.username" size="48" maxlength="32" 
								onkeypress="return hm.util.keyPressPermit(event,'name');"/>
								<s:text name="config.ipFilter.name.range" /></td>
						</tr>
						<tr>
							<td class="labelT1">
								<s:text name="config.pppoe.password" /><font color="red"><s:text name="*"/></font></td>
							<td><s:password id="password" name="dataSource.password" size="48" maxlength="32" showPassword="true" 
								onkeypress="return hm.util.keyPressPermit(event,'password');"/>
								<s:textfield id="password_text" name="dataSource.password" size="48" maxlength="32" cssStyle="display:none" 
								onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true"/>
								<s:text name="config.ipFilter.name.range" />
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>
								<table cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td>
											<s:checkbox id="chkToggleDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword(this.checked,['password'],['password_text']);"
												disabled="%{writeDisable4Struts}" />
										</td>
										<td>
											<s:text name="admin.user.obscurePassword" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="1"></td>
						</tr>
						<!-- 
						<tr>
							<td class="labelT1" >
								<s:text name="config.pppoe.domain" />
							</td>
							<td><s:textfield name="dataSource.domain" size="48"
								onkeypress="return hm.util.keyPressPermit(event,'name');"
								maxlength="63" />&nbsp;<s:text
								name="config.pppoe.password.range" /></td>
						</tr>	
						-->
						<tr>
							<td class="labelT1"><s:text name="config.pppoe.encryption.method" /></td>
							<td><s:select name="dataSource.encryptionMethod"
								list="%{enumEncryptionMethod}" listKey="key" listValue="value" cssStyle="width: 200px;" /></td>
						</tr>
						<tr>
							<td height="4"></td>
						</tr>
					</table>
					</fieldset>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>