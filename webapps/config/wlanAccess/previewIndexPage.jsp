<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">
.style_Title {
	font-family: Arial, Helvetica, sans-serif;
	font-weight: bold;
	font-size: 14px;}

.style1_Current {
	font-family:Century Gothic, Helvetica, Arial, sans-serif;
	font-size:30px;
	text-align:center;
	font-weight:200;
	color: <s:property value="%{currentForegroundColor}" />;}

.style2_Current {
	font-family:Century Gothic, Helvetica, Arial, sans-serif;
	font-size:20px;
	text-align:left;
	color: <s:property value="%{currentForegroundColor}" />;}

.style5_Current {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: <s:property value="%{currentForegroundColor}" />;}

.textarea_Current {
	color: <s:property value="%{currentForegroundColor}" />;
	background-color: #FFFFFF;
	border-width: 1px;
	border-color: #000000;
	border-style: solid;}
	
input.countryCode {
    width: 25px; 
    border-right-width: 0;
    color: gray;
}
input.countryCode + input[name="phone"] {
    border-left-width: 0;
}
</style>

<script>
var formName = 'captivePortalWeb';

function submitAction(operation) {
	document.forms[formName].operation.value = operation;
    document.forms[formName].submit();
}

function onLoadPage() {
	if(Get('countryCode')) {
		var phones = document.getElementsByName('phone');
		if(phones.length > 0) {
			phones[0].style.width = "215px";
		}
	}
}
</script>

<div id="content">
<s:form action="captivePortalWeb">
<s:hidden name="operation" />
<s:hidden name="customPage" />
<s:hidden name="registType" />
<s:hidden name="idmSelfReg" />
	
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
										list="%{previewenumlanguage}" listKey="key" listValue="value" onChange="submitAction('previewIndexPageMultiLan')"
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
					<td id="currentPage" align="center">
					<div  style="background-image: url(<s:url value="%{currentBackgroundImage}" />);
						background-repeat: <s:property value="%{currentBackgroundTile}" />; 
						background-position: -50px 0px;
						width: 100%;
						height: 800px;">
						<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td height="60px"></td>
							</tr>
							<tr style="display: <s:property value="%{showH1Title}" />;">
								<td align="center"><p class="style1_Current">
								<s:text name="%{CWPPreviewIndexSecureInternetPortal_label}"></s:text>
								</p></td>
							</tr>
							<tr>
								<td align="center">
												<s:if test="%{showCurrentEula}">
									<table>
										<tr>
											<td class="style2_Current">
											<s:text name="%{CWPPreviewIndexAcceptableUsePolicy_label}"></s:text>
											</td>
										</tr>
										<tr>
											<td>
									<s:textarea value="%{currentUserPolicy}" cols="55" rows="15">
																	</s:textarea>
											</td>
										</tr>
									</table>
												</s:if>
												<s:else>											
									<table>
										<tr>
											<td class="style2_Current" style="display: <s:property value="%{showCurrentAuthenticated}" />;">
											<s:text name="%{CWPPreviewIndexExistingUsers_label}"></s:text>
											</td>
										</tr>
										<tr>
											<td class="style5_Current" style="display: <s:property value="%{showCurrentAuthenticated}" />;">
												<strong>
												<s:text name="%{CWPPreviewIndexLoginforSIA_label}"></s:text>
												</strong>
											</td>
										</tr>
										<tr>
											<td>
											<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" 
															style="display: <s:property value="%{showCurrentAuthenticated}" />;">
															<tr>
													<td style="padding: 4px 0 4px 0;" align="center" colspan="2">
														<input name="username" type="text" id="field12" 
															style="width: 240px; color: gray;" maxlength="32" value="<s:text name="%{CWPPreviewIndexUsername_label}" />" ></td>
															</tr>
															<tr>
													<td style="padding: 4px 0 4px 0;" align="center" colspan="2">
														<input name="password" type="text" id="field22" 
														style="width: 240px; color: gray;" maxlength="32" value="<s:text name="%{CWPPreviewIndexPassword_label}" />" ></td>
															</tr>
															<tr>
													<td align="center" colspan="2">
														<table>
															<tr>
																<td style="width: 190px;" class="style5_Current">
																	<s:text name="%{CWPPreviewIndexloginprompt2_label}"></s:text>
																</td>
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
											</td>
										</tr>
															<tr> 
											                   	<td>
												<HR style="display: <s:property value="%{showCurrentBoth}" />;">
											                     	</td>
											               	</tr>
										<tr>
											<td align="left" class=style2_Current style="display: <s:property value="%{showCurrentRegistrated}" />;">
												<s:text name="%{CWPPreviewIndexNewUsers_label}"></s:text>
											</td>
										</tr>
										<tr>
											<td align="left" class=style5_Current style="display: <s:property value="%{showCurrentRegistrated}" />;">
												<strong>
												<s:text name="%{CWPPreviewIndexRegistertoaccess_label}"></s:text>
												</strong>
											</td>
										</tr>
										<tr>
											<td>
											<s:if test="%{currentEnabledPageFields.size > 0}">
											<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" 
													style="display: <s:property value="%{showCurrentRegistrated}" />;">
											               	<s:iterator value="%{currentEnabledPageFields}" status="status">
											               	<tr>
								               		<td style="padding: 4px 0 4px 0;" align="center">
								               		   <s:if test="%{idmSelfReg && registType == 3 && !''.equals(labelName) && labelName.equals('phone')}">
								               		   <input type="text" class="countryCode" id="countryCode" name="countryCode" value="+1" maxlength="4" readonly/>
								               		   </s:if>
								               			<s:if test="%{required == true}">
									               			<s:if test="%{previewLanguage== 1}">
									               				<s:if test="%{!idmSelfReg}">
											               			<input class="style5_Current" name="field%{#status.index}" 
											               				type="text"  style="width: 240px; color: gray;" maxlength="32"
											               				value="<s:property value='label'/>" />
										               			</s:if>
										               			<s:else>
										               				<input class="style5_Current" name="<s:property value='labelName'/>" 
											               				type="text"  style="width: 240px; color: gray;" maxlength="32"
											               				value="<s:property value='label'/>" />
										               			</s:else>
										               		</s:if>
										               		<s:elseif test="%{previewLanguage== 2}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label2'/>" />
										               			</s:if>
										               			<s:else>
										               				<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label2'/>" />
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 3}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label3'/>" />
										               			</s:if>
										               			<s:else>
										               				<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label3'/>" />
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 4}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label4'/>" />
										               			</s:if>
										               			<s:else>
										               				<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label4'/>" />
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 5}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label5'/>" />
										               			</s:if>
										               			<s:else>
										               				<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label5'/>" />
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 6}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label6'/>" />
										               			</s:if>
										               			<s:else>
										               				<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label6'/>" />
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 7}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label7'/>" />
										               			</s:if>
										               			<s:else>
										               				<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label7'/>" />
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 8}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label8'/>" />
										               			</s:if>
										               			<s:else>
										               				<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label8'/>" />
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 9}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label9'/>" />
										               			</s:if>
										               			<s:else>
										               				<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label9'/>" />
										               			</s:else>
										               		</s:elseif>
										               		<s:else>
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label'/>" />
										               			</s:if>
										               			<s:else>
										               				<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label'/>" />
										               			</s:else>
										               		</s:else>
									               				
											            </s:if>
											            <s:else>
												            <s:if test="%{previewLanguage== 1}">
												            	<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
											               				type="text"  style="width: 240px; color: gray;" maxlength="32"
											               				value="<s:property value='label'/>" />
										               			</s:if>
										               			<s:else>
										               				<s:if test="%{!''.equals(label) && !label.equals('Comment')}">
										               					<input class="style5_Current" name="<s:property value='labelName'/>" 
												               				type="text"  style="width: 240px; color: gray;" maxlength="32"
												               				value="<s:property value='label'/>" />
										               				</s:if>
										               			</s:else>
										               		</s:if>
										               		<s:elseif test="%{previewLanguage== 2}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label2'/>" />
										               			</s:if>
										               			<s:else>
										               				<s:if test="%{!''.equals(label) && !label.equals('Comment')}">
										               					<input class="style5_Current" name="<s:property value='labelName'/>" 
												               				type="text"  style="width: 240px; color: gray;" maxlength="32"
												               				value="<s:property value='label2'/>" />
										               				</s:if>
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 3}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label3'/>" />
										               			</s:if>
										               			<s:else>
										               				<s:if test="%{!''.equals(label) && !label.equals('Comment')}">
										               					<input class="style5_Current" name="<s:property value='labelName'/>" 
												               				type="text"  style="width: 240px; color: gray;" maxlength="32"
												               				value="<s:property value='label3'/>" />
										               				</s:if>
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 4}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label4'/>" />
										               			</s:if>
										               			<s:else>
										               				<s:if test="%{!''.equals(label) && !label.equals('Comment')}">	
										               					<input class="style5_Current" name="<s:property value='labelName'/>" 
												               				type="text"  style="width: 240px; color: gray;" maxlength="32"
												               				value="<s:property value='label4'/>" />
										               				</s:if>
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 5}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label5'/>" />
										               			</s:if>
										               			<s:else>
										               				<s:if test="%{!''.equals(label) && !label.equals('Comment')}">	
										               					<input class="style5_Current" name="<s:property value='labelName'/>" 
												               				type="text"  style="width: 240px; color: gray;" maxlength="32"
												               				value="<s:property value='label5'/>" />
										               				</s:if>
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 6}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label6'/>" />
										               			</s:if>
										               			<s:else>
										               				<s:if test="%{!''.equals(label) && !label.equals('Comment')}">
										               					<input class="style5_Current" name="<s:property value='labelName'/>" 
												               				type="text"  style="width: 240px; color: gray;" maxlength="32"
												               				value="<s:property value='label6'/>" />
										               				</s:if>
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 7}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label7'/>" />
										               			</s:if>
										               			<s:else>
										               				<s:if test="%{!''.equals(label) && !label.equals('Comment')}">
										               					<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label7'/>" />
										               				</s:if>
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 8}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label8'/>" />
										               			</s:if>
										               			<s:else>
										               				<s:if test="%{!''.equals(label) && !label.equals('Comment')}">
										               					<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label8'/>" />
										               				</s:if>
										               			</s:else>
										               		</s:elseif>
										               		<s:elseif test="%{previewLanguage== 9}">
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label9'/>" />
										               			</s:if>
										               			<s:else>
										               				<s:if test="%{!''.equals(label) && !label.equals('Comment')}">
										               					<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label9'/>" />
										               				</s:if>
										               			</s:else>
										               		</s:elseif>
										               		<s:else>
										               			<s:if test="%{!idmSelfReg}">
										               				<input class="style5_Current" name="field%{#status.index}" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label'/>" />
										               			</s:if>
										               			<s:else>
										               				<s:if test="%{!''.equals(label) && !label.equals('Comment')}">
										               					<input class="style5_Current" name="<s:property value='labelName'/>" 
										               				type="text"  style="width: 240px; color: gray;" maxlength="32"
										               				value="<s:property value='label'/>" />
										               				</s:if>
										               			</s:else>
										               		</s:else>
									               				
											            </s:else>
											               		</td>
											               	</tr>
											               	</s:iterator>
											            </table>
											            </s:if>
											</td>
										</tr>
										<tr>
											<td>
											<table width="100%" height="100%" style="display: <s:property value="%{showCurrentRegistrated}" />;">
												<tr>
													<td style="width: 200px;" class="style5_Current">
														<s:text name="%{CWPPreviewIndexRegPrompt_label}"></s:text>
													</td>
													<td style="height:50px;font-size:25px;text-align:center;color:white;float:left;margin-top:13px;">
																	<s:text name="%{CWPPreviewIndexRegister_button_label}"></s:text>
																</td>
													<td>
														<img id="loginImg" src="<s:url value="/images/cwp/register.png"/>" 
															class="dblk"  />					
													</td>
												</tr>
											</table>
											</td>
										</tr>
									</table>
													</s:else>
													<s:if test="%{showCurrentEula}">
											 			 <div align="center"><br>
											              	<table>
											              		<tr>
											              			<td width="120px"><input type="button" name="Accept" value=<s:text name="%{CWPPreviewIndexAcceptButton_label}"></s:text>></td>
											              			<td width="120px"><input type="button" name="Cancel" value=<s:property value="%{CWPPreviewIndexCancelButton_label}" /> ></td>
											              		</tr>
											              	</table>
											             </div>
													</s:if>
										            
								</td>
							</tr>
							<s:if test="%{!hMForOEM}">
								<tr>
									<td align="right">
										<img alt="" src="<s:url value="%{currentFootImage}" />" style="width: 235px; height: 69px;"><br>
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