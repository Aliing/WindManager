<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.util.EnumConstUtil"%>
<s:if test="%{jsonMode}">
<link rel="stylesheet" href="<s:url value="/css/hm_v2.css" includeParams="none"/>" type="text/css" />
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none"/>" ></script>
</s:if>

<script>
var formName = 'supplementalCLI';
function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_supplementalName").disabled) {
		document.getElementById(formName + "_dataSource_supplementalName").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		<s:if test="%{jsonMode}">
		 	if ('cancel' + '<s:property value="lstForward"/>' == operation) {
				parent.closeIFrameDialog();	
			} else{
 				saveSupplementalCLI(operation);
			}
		</s:if>
		<s:else>
			document.forms[formName].operation.value = operation;
		    document.forms[formName].submit();
		</s:else>
		
	}
}

function saveSupplementalCLI(operation) {
	 var url = "<s:url action='supplementalCLI' includeParams='none' />"+ "?jsonMode=true" +"&ignore="+new Date().getTime(); 
	 document.forms["supplementalCLI"].operation.value = operation;
	 YAHOO.util.Connect.setForm(document.forms["supplementalCLI"]);
	 var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveSupplementalCLI, failure : failSaveSupplementalCLI, timeout: 60000}, null);
}

var succSaveSupplementalCLI = function (o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			parent.closeIFrameDialog();
			if(details.id != null && details.name != null){	
				if (details.parentDomID) {
					var parentIpPolicySelect = parent.document.getElementById(details.parentDomID);
					if(parentIpPolicySelect != null) {
						hm.util.insertSelectValue(details.id,details.name,parentIpPolicySelect,false,true);	
					}
				}
			}
		}
	}catch(e){
		alert("error")
		return;
	}
}

var failSaveSupplementalCLI = function(o){
	
}

function validate(operation) {
	if (operation == 'cancel' + '<s:property value="lstForward"/>') {
		return true;
	}
	if (!validateSupplementalName()) {
		return false;
	}
	if(!validateCLIContent()){
		return false;
	}

	return true;
}

function validateSupplementalName() {
    var inputElement = Get(formName + "_dataSource_supplementalName");
    var message = hm.util.validateName(inputElement.value, '<s:text name="config.alg.configuration.name" />');
	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}
    return true;
}

function validateCLIContent(){
	var cliContent = Get("contentArea");
	if(cliContent.value.length > 0){
		var message = hm.util.validateCLIBlob(cliContent.value, '<s:text name="hollywood_02.cli.blob" />');
    	if (message != null) {
    		hm.util.reportFieldError(cliContent, message);
    		cliContent.focus();
        	return false;
    	}
	}
	return true;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="supplementalCLI" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedProfileName" />\'</td>');
		</s:else>
	</s:else>	
}

</script>

<div id="content"><s:form action="supplementalCLI" id="supplementalCLI">
	<s:if test="%{jsonMode == true}">
		<s:hidden name="id" />
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
		<s:hidden name="parentIframeOpenFlg" />
		<s:hidden name="contentShowType" />
		<div class="topFixedTitle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-CLI-Supplement.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="hollywood_02.cli.blob.title.new"/>
								</s:if> <s:else>
									<s:text name="hollywood_02.cli.blob.title.edit"/>
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
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitAction('cancel<s:property value="lstForward"/>');" title="Cancel"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Cancel</span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton">
								<s:if test="'' == writeDisabled">
									<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a>
								</s:if>
								</td>
							</s:if>
							<s:else>
								<td class="npcButton">
									<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
								</td>
							</s:else>
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
	<s:else>
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
	</s:else>
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
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="620">
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td class="labelT1" width="100"><label><s:text
									name="config.alg.configuration.name" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="dataSource.supplementalName"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									size="24" maxlength="%{profileNameLength}" disabled="%{disabledName}"/>&nbsp;<s:text
									name="config.ssid.ssidName_range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="config.localUser.description" /></label></td>
								<td><s:textfield size="48"
									name="dataSource.description" maxlength="%{commentLength}" />&nbsp;<s:text
									name="config.ssid.description_range" /></td>
							</tr>
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<td colspan="2">
									<fieldset style="width: 550px">
									<legend>
										&nbsp; CLIs &nbsp;
									</legend>
									<div>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td height="10"></td>
											</tr>
											<tr>
												<td height="10"></td>
											</tr>
											<tr>
												<td colspan="11">
													<s:textarea rows="20" cols="100" name="dataSource.contentAera" id="contentArea"
														onkeypress="return hm.util.keyPressPermit(event,'cliblob');"/>
												</td>
											</tr>
											<tr>
												<td height="10"></td>
											</tr>
											<tr>
												<td colspan="11" class="noteInfo" style="padding-left: 10px;">
													<s:text  name="hollywood_02.supp_cli_setting.note"/>
												</td>
											</tr>
										</table>
									</div>
									</fieldset>
								</td>
							</tr>
							
						</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
