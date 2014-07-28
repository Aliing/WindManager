<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'certificates';


function onLoadPage(){
	<s:if test="%{jsonMode == true}">
	if(top.isIFrameDialogOpen()) {
		top.changeIFrameDialog(790, 380);
	}
</s:if>
}

function closeCertificateDlg(){
	<s:if test="%{jsonMode==true}">
	    if (top.isIFrameDialogOpen()) {
	    	parent.closeIFrameDialog();	
	    }
   </s:if>
}

function submitAction(operation) {
	if (validate(operation)) 
	{
		document.forms[formName].operation.value = operation;
		<s:if test="%{jsonMode && !parentIframeOpenFlg}">
			if(operation == 'cancel'+'<s:property value="lstForward"/>' ) {
				parent.closeIFrameDialog();
			}
			
			if (operation == 'importFile') {
				var url = "<s:url action='certificates' includeParams='none' />" + "?jsonMode=true"
					+ "&ignore=" + new Date().getTime(); 
				YAHOO.util.Connect.setForm(document.forms["certificates"],true,true);
			  try{
				  var transaction = YAHOO.util.Connect.asyncRequest('post', 
						url, {upload : succSaveCertificate, failure : failSaveCertificate, timeout: 60000}, null);
				  return;
			  }catch(e){
				  if (e instanceof Error && e.name == "TypeError") 
					{
						var certificateFile = document.getElementById("certificateFile");
						hm.util.reportFieldError(certificateFile, '<s:text name="error.fileNotExist"></s:text>');
						certificateFile.focus();
					}
			  }
			}
			
		</s:if>
		<s:else>
			try
			{
				document.forms[formName].submit();
				if ( operation == 'importFile' || operation == 'importFile' + '<s:property value="lstForward"/>')
				{
					showProcessing();
				}
			}catch(e){
				if (e instanceof Error && e.name == "TypeError") 
				{
					var certificateFile = document.getElementById("certificateFile");
					hm.util.reportFieldError(certificateFile, '<s:text name="error.fileNotExist"></s:text>');
					certificateFile.focus();
				}
			}
		</s:else>
	}
}

var succSaveCertificate = function(o){
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			showPageNotes(details.errMsg);
			return;
		} else {
			var parentSelectEl = parent.document.getElementById(details.parentDomID);
			if(parentSelectEl != null) {
				if(details.newObjId != null && details.newObjId != ''){
					dynamicAddSelect(parentSelectEl, details.newObjName, details.newObjId);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSaveCertificate = function(o){
	// do nothing now
}

function validate(operation) 
{
	if (operation == 'return' || operation == 'return' + '<s:property value="lstForward"/>')
	{
		return true;
	}
	
	var certificateFile = document.getElementById("certificateFile");
	if ( certificateFile.value.length == 0) 
	{
        hm.util.reportFieldError(certificateFile, '<s:text name="error.requiredField"><s:param><s:text name="admin.importCertificate.file" /></s:param></s:text>');
        certificateFile.focus();
        return false;
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
		document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />  &gt; ');
		document.writeln('Import certificate </td>');
	</s:else>	
}

function selectPfx2pem(checked)
{
	if (checked)
	{
		document.getElementById('chkToggle_pfx').disabled = false;
		
		if (document.getElementById('chkToggle_pfx').checked)
		{
			document.getElementById('pfxPassword').disabled = false;
			document.getElementById('pfxPassword_text').disabled = true;
		}
		else
		{
			document.getElementById('pfxPassword').disabled = true;
			document.getElementById('pfxPassword_text').disabled = false;
		}
		
		if (document.getElementById('der2pem').checked)
		{
			document.getElementById('der2pem').checked = false;
			selectDer2pem(false);
		}
	}
	else
	{
		document.getElementById('pfxPassword').disabled = true;
		document.getElementById('pfxPassword_text').disabled = true;
		document.getElementById('chkToggle_pfx').disabled = true;
	}
}

function selectDer2pem(checked)
{
	if (checked)
	{
		document.getElementById('radio_key').disabled = false;
		document.getElementById('radio_cert').disabled = false;
		
		if (document.getElementById('pfx2pem').checked)
		{
			document.getElementById('pfx2pem').checked = false;
			selectPfx2pem(false);
		}
		
		if (document.getElementById('radio_key').checked)
		{
			selectDERKeyRd(true);
		}
		
		if (document.getElementById('radio_cert').checked)
		{
			selectDERCertRd(true);
		}
	}
	else
	{
		document.getElementById('radio_key').disabled = true;
		document.getElementById('radio_cert').disabled = true;
		document.getElementById('derPassword').disabled = true;
		document.getElementById('derPassword_text').disabled = true;
		document.getElementById('chkToggle_der').disabled = true;
	}
}

function selectDERKeyRd(checked)
{
	if (checked)
	{
		document.getElementById('chkToggle_der').disabled = false;
		if (document.getElementById('chkToggle_der').checked)
		{
			document.getElementById('derPassword').disabled = false;
			document.getElementById('derPassword_text').disabled = true;
		}
		else
		{
			document.getElementById('derPassword').disabled = true;
			document.getElementById('derPassword_text').disabled = false;
		}
	}
}

function selectDERCertRd(checked)
{
	if (checked)
	{
		document.getElementById('derPassword').disabled = true;
		document.getElementById('derPassword_text').disabled = true;
		document.getElementById('chkToggle_der').disabled = true;
	}
}

</script>

<s:if test="%{easyMode && lastExConfigGuide!=null}">
<div id="content" style="padding-left: 20px">
</s:if>
<s:else>
<div id="content">
</s:else>
	<s:form action="certificates" enctype="multipart/form-data"
		method="POST" id="certificates">
		<s:if test="%{jsonMode}">
			<s:hidden name="operation" />
			<s:hidden name="jsonMode" />
			<s:hidden name="importFileType" />
			<s:hidden name="parentDomID" />
			<s:hidden name="contentShowType" />
			<s:hidden name="parentIframeOpenFlg"/>
	    </s:if>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<s:if test="%{jsonMode == false}">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<s:if test="%{lstForward != null && lstForward != ''}">
								<td>
									<input type="button" name="import" value="Import"
										class="button"
										onClick="submitAction('importFile<s:property value="lstForward"/>');"
										<s:property value="writeDisabled" />>
								</td>
								<td>
									<input type="button" name="cancel" value="Return"
										class="button"
										onClick="submitAction('return<s:property value="lstForward"/>');">
								</td>
							</s:if>
							<s:else>
								<td>
									<input type="button" name="import" value="Import"
										class="button" onClick="submitAction('importFile');"
										<s:property value="writeDisabled" />>
								</td>
								<td>
									<input type="button" name="cancel" value="Return"
										class="button" onClick="submitAction('return');">
								</td>
							</s:else>
						</tr>
					</table>
				</td>
			</tr>
			</s:if>
			<s:else>
			<tr>
				<td style="padding:10px 10px 10px 10px">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="84%">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-import_cert.png" includeParams="none"/>"
									width="40" height="40" alt="" class="dblk" />
								</td>
								<td class="dialogPanelTitle"><s:text name="admin.title.cafile.import"/></td>
								<td style="padding-left:10px;">
									<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
										<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
											alt="" class="dblk"/>
									</a>
								</td>
							</tr>
						</table>
						</td>
						<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<s:if test="%{parentIframeOpenFlg}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style=" margin-right: 20px;" tittle="Return" onClick="submitAction('return<s:property value="lstForward"/>');"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Return</span></a></td>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;"  onClick="submitAction('importFile<s:property value="lstForward"/>');" title="Import"><span style="padding-bottom: 2px; padding-top: 2px;">Import</span></a></td>
								</s:if>
								<s:else>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style=" margin-right: 20px;" tittle="Return" onclick="closeCertificateDlg();"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Return</span></a></td>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent"  style="float: right;"  onclick="submitAction('importFile');" title="Import"><span style="padding-bottom: 2px; padding-top: 2px;">Import</span></a></td>
								</s:else>
							</tr>
						</table>
						</td>
					</tr>
				</table>
				</td>
			</tr>
			</s:else>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<s:if test="%{jsonMode == true}">
						<table  border="0" cellspacing="0"
							cellpadding="0">
					</s:if>
					<s:else>
						<table class="editBox" border="0" cellspacing="0"
							cellpadding="0">
					</s:else>
						<tr>
							<td height="10">
								<%-- add this password dummy to fix issue with auto complete function --%>
								<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="120px">
								<label>
									<s:text name="admin.importCertificate.file" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:file id="certificateFile" name="certificateFile"
									accept="text/html,text/plain" size="80" />
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td colspan="2" style="padding-left: 15px">
								<fieldset style="width: 600px">
									<legend>
										<s:text name="admin.importCertificate.convertOptions" />
									</legend>
									<div>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td height="5"></td>
											</tr>
											<tr>
												<td colspan="3">
													<s:checkbox name="pfx2pem" id="pfx2pem"
														onclick="selectPfx2pem(this.checked);" />
													<label>
														<s:text name="admin.importCertificate.PFXtoPEM" />
													</label>
												</td>
											</tr>
											<tr>
												<td class="labelT1" width="80px" style="padding-left: 20px">
													<label>
														<s:text name="admin.importCertificate.password" />
													</label>
												</td>
												<td width="140px">
													<s:password id="pfxPassword" name="pfxPassword" size="20"
														maxlength="32" disabled="true"
														onkeypress="return hm.util.keyPressPermit(event,'password');" />
													<s:textfield id="pfxPassword_text" name="pfxPassword"
														disabled="true" size="20" maxlength="32"
														cssStyle="display:none" />
												</td>
												<td>
													&nbsp;<s:text name="admin.importCertificate.password.ranger" />
												</td>
											</tr>
											<tr>
												<td>
													&nbsp;
												</td>
												<td colspan="2">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<s:checkbox id="chkToggle_pfx" name="ignore"
																	value="true" disabled="true"
																	onclick="hm.util.toggleObscurePassword(this.checked,['pfxPassword'],['pfxPassword_text']);" />
															</td>
															<td>
																<s:text name="admin.user.obscurePassword" />
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td colspan="3">
													<s:checkbox name="der2pem" id="der2pem"
														onclick="selectDer2pem(this.checked);" />
													<label>
														<s:text name="admin.importCertificate.DERtoPEM" />
													</label>
												</td>
											</tr>
											<tr>
												<td style="padding-left: 15px" colspan="3">
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td colspan="2">
																<s:radio label="Gender" id="radio_" name="derType"
																	list="#{'cert':'Certificate'}"
																	onclick="selectDERCertRd(this.checked);"
																	value="%{derType}" disabled="true" />
															</td>
														</tr>
														<tr>
															<td width="85px">
																<s:radio label="Gender" id="radio_" name="derType"
																	list="#{'key':'Key'}"
																	onclick="selectDERKeyRd(this.checked);"
																	value="%{derType}" disabled="true" />
															</td>
															<td width="140px">
																<s:password id="derPassword" name="derPassword"
																	size="20" maxlength="32"
																	onkeypress="return hm.util.keyPressPermit(event,'password');"
																	disabled="true" />
																<s:textfield id="derPassword_text" name="derPassword"
																	disabled="true" size="20" maxlength="32"
																	cssStyle="display:none" />
															</td>
															<td>
																&nbsp;<s:text name="admin.importCertificate.password.ranger" />
															</td>
														</tr>
														<tr>
															<td>
																&nbsp;
															</td>
															<td colspan="2">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<s:checkbox id="chkToggle_der" name="ignore"
																				value="true" disabled="true"
																				onclick="hm.util.toggleObscurePassword(this.checked,['derPassword'],['derPassword_text']);" />
																		</td>
																		<td>
																			<s:text name="admin.user.obscurePassword" />
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td height="5"></td>
											</tr>
										</table>
									</div>
								</fieldset>
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
