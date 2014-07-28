<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>


<div id="credentialsTr">
	<s:if test="%{dataSource.deviceInfo.vhmFullMode}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.tab.authentication" />','credentials');</script></td>
		</tr>
		<tr>
			<td style="padding-left: 20px;">
			<div id="credentials"
				style="display: <s:property value="%{dataSource.credentialsDisplayStyle}"/>">
			<table cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0"
						width="100%">
						<tr>
							<td height="5px"></td>
						</tr>
						<tr>
							<td>
							<fieldset><legend><s:text
								name="hiveAp.superUser.tag" /></legend>
							<table cellspacing="0" cellpadding="0" border="0"
								class="embedded">
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="140px"><s:text
												name="hiveAp.newUser" /></td>
											<td><s:textfield name="dataSource.cfgAdminUser"
												size="24" maxlength="%{cfgAdminUserLength}"
												onkeypress="return hm.util.keyPressPermit(event,'username');" />
											<s:text name="hiveAp.currentUserRange" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="140px"><s:text
												name="hiveAp.newPassword" /></td>
											<td><s:password name="dataSource.cfgPassword"
												size="24" maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												id="cfgAdminPassword" showPassword="true" /> <s:textfield
												name="dataSource.cfgPassword" size="24" maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												id="cfgAdminPassword_text" disabled="true"
												cssStyle="display: none;" /> <s:text
												name="hiveAp.currentPasswordRange" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="140px"><s:text
												name="hiveAp.newConfirmPassword" /></td>
											<td><s:password name="confirmNewPassword"
												size="24" maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												id="cfCfgAdminPassword"
												value="%{dataSource.cfgPassword}" showPassword="true" />
											<s:textfield name="confirmNewPassword" size="24"
												maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												id="cfCfgAdminPassword_text"
												value="%{dataSource.cfgPassword}" disabled="true"
												cssStyle="display: none;" /></td>
										</tr>
										<tr>
											<td></td>
											<td><s:checkbox id="chkToggleDisplay"
												name="ignore" value="true"
												disabled="%{writeDisable4Struts}"
												onclick="hm.util.toggleObscurePassword(this.checked,['cfgAdminPassword','cfCfgAdminPassword'],['cfgAdminPassword_text','cfCfgAdminPassword_text']);" />
											<s:text name="admin.user.obscurePassword" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</fieldset>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td>
							<fieldset><legend><s:text
								name="hiveAp.readOnlyUser.tag" /></legend>
							<table cellspacing="0" cellpadding="0" border="0"
								class="embedded">
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="140px"><s:text
												name="hiveAp.newUser" /></td>
											<td><s:textfield name="dataSource.cfgReadOnlyUser"
												size="24" maxlength="%{cfgReadOnlyUserLength}"
												onkeypress="return hm.util.keyPressPermit(event,'username');" />
											<s:text name="hiveAp.currentUserRange" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="140px"><s:text
												name="hiveAp.newPassword" /></td>
											<td><s:password
												name="dataSource.cfgReadOnlyPassword" size="24"
												maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												id="cfgReadOnlyPassword" showPassword="true" /> <s:textfield
												name="dataSource.cfgReadOnlyPassword" size="24"
												maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												id="cfgReadOnlyPassword_text" disabled="true"
												cssStyle="display: none;" /> <s:text
												name="hiveAp.currentPasswordRange" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="140px"><s:text
												name="hiveAp.newConfirmPassword" /></td>
											<td><s:password name="confirmNewReadOnlyPassword"
												size="24" maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												id="confirmCfgReadOnlyPassword"
												value="%{dataSource.cfgReadOnlyPassword}"
												showPassword="true" /> <s:textfield
												name="confirmNewReadOnlyPassword" size="24"
												maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												id="confirmCfgReadOnlyPassword_text"
												value="%{dataSource.cfgReadOnlyPassword}"
												disabled="true" cssStyle="display: none" /></td>
										</tr>
										<tr>
											<td></td>
											<td><s:checkbox id="chkToggleDisplay_1"
												name="ignore" value="true"
												disabled="%{writeDisable4Struts}"
												onclick="hm.util.toggleObscurePassword(this.checked,['cfgReadOnlyPassword','confirmCfgReadOnlyPassword'],['cfgReadOnlyPassword_text','confirmCfgReadOnlyPassword_text']);" />
											<s:text name="admin.user.obscurePassword" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</fieldset>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td>
							<fieldset><legend><s:text
								name="hiveAp.capwap.tag" /></legend>
							<table cellspacing="0" cellpadding="0" border="0"
								class="embedded">
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><s:checkbox name="changePassPhrase"
												onclick="clickDtlsBox();"
												disabled="%{writeDisable4Struts}"
												value="%{changePassPhrase}" id="changePassPhrase" /></td>
											<td class="labelT1" width="140px"><s:text
												name="hiveAp.dtls.enableChange" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="140px"><s:text
												name="hiveAp.dtls.newPassPhrase" /></td>
											<td><s:password name="dataSource.passPhrase"
												size="24" id="newDtls"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												maxlength="32" showPassword="true"
												disabled="%{passPhraseDisabled}" /> <s:textfield
												name="dataSource.passPhrase" size="24"
												id="newDtls_text"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												maxlength="32" cssStyle="display: none;"
												disabled="true" /> <s:text
												name="hiveAp.dtls.passPhraseRange" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="140px"><s:text
												name="hiveAp.dtls.confirmPassPhrase" /></td>
											<td><s:password value="%{dataSource.passPhrase}"
												size="24" id="confirmDtls"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												maxlength="32" showPassword="true"
												disabled="%{passPhraseDisabled}" /> <s:textfield
												value="%{dataSource.passPhrase}" size="24"
												id="confirmDtls_text"
												onkeypress="return hm.util.keyPressPermit(event,'password');"
												maxlength="32" cssStyle="display: none;"
												disabled="true" /></td>
										</tr>
										<tr>
											<td></td>
											<td><s:checkbox id="chkToggleDisplay_2"
												name="ignore" value="true"
												disabled="%{passPhraseDisabled}"
												onclick="hm.util.toggleObscurePassword(this.checked,['newDtls','confirmDtls'],['newDtls_text','confirmDtls_text']);" />
											<s:text name="admin.user.obscurePassword" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="140px"><s:text
												name="hiveAp.capwap.server" /></td>
											<td width="260px"><ah:createOrSelect
												divId="errorDisplay" list="capwapIps"
												typeString="CapwapIp" selectIdName="capwapSelect"
												inputValueName="dataSource.capwapText" swidth="152px" />
											</td>
										</tr>
										<tr>
											<td class="labelT1"><s:text
												name="hiveAp.capwap.server.backup" /></td>
											<td><ah:createOrSelect divId="errorBackupDisplay"
												list="capwapIps" typeString="CapwapBackupIp"
												selectIdName="capwapBackupSelect"
												inputValueName="dataSource.capwapBackupText"
												swidth="152px" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</fieldset>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
	</table>
	</s:if>
</div>