<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'cwpCertMgmt';
function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		
		<s:if test="%{jsonMode && !parentIframeOpenFlg}">
		if(operation == 'cancel'+'<s:property value="lstForward"/>' ) {
			parent.closeIFrameDialog();
			return;
		}
		if (operation == 'update'+'<s:property value="lstForward"/>') {
			var url = "<s:url action='cwpCertMgmt' includeParams='none' />" + "?jsonMode=true" 
				+ "&ignore=" + new Date().getTime(); 
			YAHOO.util.Connect.setForm(document.forms[formName]);
			var transaction = YAHOO.util.Connect.asyncRequest('post', 
					url, {success : succUpdateCWPCert, failure : failUpdateCWPCert, timeout: 60000}, null);
			return;
		}
		</s:if>
		
		showProcessing();
	    document.forms[formName].submit();
	}
}
var succUpdateCWPCert = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.succ) {
			parent.closeIFrameDialog();
		} else {
			var errorRow = new Object();
			errorRow.id='ErrorRow';
			if (details.error) {
				hm.util.reportFieldError(errorRow, details.msg);
			}
			return;
		}
	}catch(e){
		// do nothing now
	}	
}
var failUpdateCWPCert = function(o) {}

//var $ = function(o){ return typeof o == 'string'? document.getElementById(o): o};

function validate(operation) {
	
	var certName = document.getElementById("certName");
	if ( certName.value.length == 0) 
	{
        hm.util.reportFieldError(certName, '<s:text name="error.requiredField"><s:param><s:text name="config.cwpCert.certName" /></s:param></s:text>');
        certName.focus();
        return false;
  	}
  	var message = validateInput(certName.value, '<s:text name="config.cwpCert.certName" />');
   	if (message != null) {
   		hm.util.reportFieldError(certName, message);
       	certName.focus();
       	return false;
   	}
	
	return true;
}

function validateInput(value,label)
{
	for(var i = 0; i< value.length; i++){
		var charCode = value.charCodeAt(i);
		if(!((charCode >= 32 && charCode <= 34) || (128 > charCode && charCode > 36)))
		{
			return label + " cannot contain '" + String.fromCharCode(charCode) + "'.";
		}
	}
}

function keyPressCheck(e)
{
	var keycode;
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}
	
	if(keycode != 35 && keycode != 36)
	{
		return true;
	}
	
	return false;
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="cwpCertMgmt" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="dataSource.certName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="dataSource.certName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>	
}
</script>

<s:if test="%{easyMode && lastExConfigGuide!=null}">
<div id="content" style="padding-left: 20px">
</s:if>
<s:else>
<div id="content">
</s:else>
	<s:form action="cwpCertMgmt">
	<s:if test="%{jsonMode}">
	<s:hidden name="id" />
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
		<div style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="80%">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-cerificate_Mgmt.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{dataSource.id != null}">
						<td class="dialogPanelTitle"><s:text name="config.title.cwpCertificate.edit"/></td>
						</s:if>
						
					</tr>
				</table>
				</td>
				<td align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="submitAction('cancel<s:property value="lstForward"/>');" title="Cancel"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Cancel</span></a></td>
							<s:if test="%{dataSource.id != null && '' == updateDisabled}">
							<td class="npcButton">
									<a href="javascript:void(0);" class="btCurrent" style="float: right;" 
									onclick="submitAction('update<s:property value="lstForward"/>');" 
									title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
							</td>
							</s:if>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</div>
	</s:if>	
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{!jsonMode}">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<input type="button" name="ignore" value="Update" class="button"
									onClick="submitAction('update<s:property value="lstForward"/>');"
									<s:property value="updateDisabled" />>
							</td>
							<td>
								<input type="button" name="cancel" value="Cancel" class="button"
									onClick="submitAction('cancel<s:property value="lstForward"/>');">
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</s:if>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr id="fe_ErrorRow" style="display: none">
				<td class="noteError" id="textfe_ErrorRow" colspan="4">To be changed</td>
			</tr>
			<tr>
				<td>
					<s:if test="%{jsonMode}">
					<table cellspacing="0" cellpadding="0" border="0"
						width="700px">
					</s:if>
					<s:else>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="700px">
					</s:else>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td class="labelT1" width="180px" style="padding-left: 15px;">
								<label>
									<s:text name="config.cwpCert.certName" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="certName" name="dataSource.certName"
									onkeypress="return keyPressCheck(event);" maxlength="20" />
								<s:text name="config.cwpCert.certNameRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="config.cwpCert.cert" />
								</label>
							</td>
							<td>
								<s:textfield id="certificate" name="dataSource.srcCertName"
									size="20" disabled="true" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="config.cwpCert.privateKey" />
								</label>
							</td>
							<td>
								<s:textfield id="privateKey" name="dataSource.srcKeyName"
									size="20" disabled="true" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="config.cwpCert.description" />
								</label>
							</td>
							<td>
								<s:textfield id="description" name="dataSource.description"
									maxlength="64" size="64" />
								<s:text name="config.cwpCert.64charsRangeExt" />
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
