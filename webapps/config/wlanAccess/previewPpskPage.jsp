<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">
<!--
.style1_Current {
	font-family: Arial, Helvetica, sans-serif;
	font-weight: bold;
	font-size: 30px;
	text-align: center;
	color: <s:property value="%{currentForegroundColor}" />;}

.style2_Current {
	font-family: Arial, Helvetica, sans-serif;
	font-weight: bold;
	font-size: 14px;
	text-align: left;
	color: <s:property value="%{currentForegroundColor}" />;}

.style5_Current {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	text-align: left;
	color: <s:property value="%{currentForegroundColor}" />;}
	
.style-input {
    background-color: Transparent !important;
    border-width: 1px;
    color: gray;
    margin-bottom: 1px;
    margin-top: 5px;
    width: 240px;
}
-->

</style>
<script>
var formName = 'captivePortalWeb';

function submitAction(operation) {
	document.forms[formName].operation.value = operation;
    document.forms[formName].submit();
}

</script>

<div id="content">
<s:form action="captivePortalWeb">
<s:hidden name="operation" />
<s:hidden name="customPage" />
<s:hidden name="registType" />

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
	<tr>
		<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<input type="button" class="button" name="return" value="Return"
							onClick="submitAction('previewReturn');" />
					</td>
					<td class="labelT1" width="120px" style="padding:4px 0 4px 10px">
									   <s:text	name="config.cwp.language.support.previewLanguage_label" />
								    </td>
								    <td>
									<s:select name="previewLanguage" id="changelanguagecombobox"
										list="%{previewenumlanguage}" listKey="key" listValue="value" onChange="submitAction('previewPpskPageMultiLan')"
										value="previewLanguage" cssStyle="width: 150px;" 
										/>
					 </td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0"
				style="background-color: #333333;">
				<tr>
					<td align="center">
					<div  style="background-image: url(<s:url value="%{currentBackgroundImage}" />);
						background-repeat: <s:property value="%{currentBackgroundTile}" />; 
						background-position: center;
						
						height: 668px;">
						<table border="0" cellspacing="0" cellpadding="0" style="height: 459px;">
							<tr>
								<td height="100"></td>
							</tr>
							<tr style="display: <s:property value="%{showH1Title}" />;">
								<td align="center"><p class="style1_Current">
								<s:text name="%{CWPPreviewPpskSecureInternetPortal_label}"></s:text>
								</p></td>
							</tr>
							<tr>
								<td height="360px" colspan="1">
									<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td>&nbsp;</td>
											<td width="380">
												<div align="center" style="display: <s:property value="%{showAuthOrPPSK}"/>;">
													<table width="100%"  border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td colspan="2" style="padding:5px 0 5px 0; width: 280px;" class="style2_Current">
																<s:text name="%{CWPPreviewPpskAuthNotes_label}"></s:text>					
															</td>
														</tr>
														<tr>
															<td style="padding: 4px 0 4px 0;" align="center">
																<s:textfield name="auth_username" cssStyle="width: 240px; color: gray;"
																	value="%{CWPPreviewPpskUsername_label}" disabled="true"/>					
															</td>
														</tr>
														<tr>
															<td style="padding: 4px 0 4px 0;" align="center">
																<s:textfield name="auth_pass" cssStyle="width: 240px; color: gray;" 
																	value="%{CWPPreviewPpskPassword_label}" disabled="true"/>					
															</td>
														</tr>
														<tr align="center">
															<td align="center" colspan="2">
																<table>
																	<tr>
																	<td style="height:50px;font-size:25px;text-align:center;color:white;float:left;margin-top:13px;">
																	<s:text name="%{CWPPreviewIndexloginbutton_label}"></s:text>
																</td>
																		<td>
																			<img id="loginImg" src="<s:url value="/images/cwp/login.png"/>" 
																				class="dblk"  />					
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</div>
												<div align="center" style="display: <s:property value="%{showPPSKServerReg}"/>;">
													<table width="100%"  border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td colspan="2" style="padding:5px 0 5px 0; width: 280px;" class="style2_Current">
																<s:text name="%{CWPPreviewPpskRegNotes_label}"></s:text>					
															</td>
														</tr>
														<tr>
															<td style="padding: 4px 0 4px 0;" align="center">
																<s:textfield name="firstname" cssClass="style-input"
																	value="%{CWPPreviewPpskFirstName_label}" />					
															</td>
														</tr>
														<tr>
															<td style="padding: 4px 0 4px 0;" align="center">
																<s:textfield name="lastname" cssClass="style-input"
																	value="%{CWPPreviewPpskLastName_label}" />					
															</td>
														</tr>
														<tr>
															<td style="padding: 4px 0 4px 0;" align="center">
																<s:textfield name="email" cssClass="style-input"
																	value="%{CWPPreviewPpskEmail_label}" />					
															</td>
														</tr>
														<tr>
															<td style="padding: 4px 0 4px 0;" align="center">
																<s:textfield name="phone" cssClass="style-input"
																	value="%{CWPPreviewPpskPhone_label}" />					
															</td>
														</tr>
														<tr>
															<td style="padding: 4px 0 4px 0;" align="center">
																<s:textfield name="visiting" cssClass="style-input"
																	value="%{CWPPreviewPpskVisiting_label}" />					
															</td>
														</tr>
														<tr>
															<td style="padding: 4px 0 4px 0;" align="center">
																<s:textfield name="comment" cssClass="style-input"
																	value="%{CWPPreviewPpskReason_label}" />					
															</td>
														</tr>
														<tr>
															<td>
																<table>
																	<tr>
																		<td class="style5_Current">
																		<s:text name="%{CWPPreviewPpskRegPrompt_label}"></s:text>	
																		</td>
																				<td style="height:50px;font-size:25px;text-align:center;color:white;float:left;margin-top:13px;">
																	<s:text name="%{CWPPreviewIndexRegister_button_label}"></s:text>
																</td>
																		<td colspan="2" style="padding:10px 0 5px 0;" align="center">
																			<img id="loginImg" src="<s:url value="/images/cwp/register.png"/>" 
																				class="dblk"  />				
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</div>
											</td>
											<td>&nbsp;</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>&nbsp;</td>
							</tr>
							<s:if test="%{!hMForOEM}">
								<tr>
									<td colspan="1">
									<p style="text-align: right;" class="style5_Current">
										<img alt="" src="<s:url value="%{currentFootImage}"/>" style="width: 235px; height: 69px;"><br>
	            					</p>
									</td>
								</tr>
							</s:if>
						</table>
					</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
			
</table>
</s:form>
</div>