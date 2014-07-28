<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<style>
.labelHere {
	height: 26px;
	font-size: 13pt;
	font-weight: bold;
	color: #003366;
	}
.labelT2{
	padding: 20px 0px 20px 10px;
}
</style>

<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="800px">
	<tr>
		<td height="1">
			<%-- add this password dummy to fix issue with auto complete function --%>
			<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
			type="password"></td>
	</tr>
	<tr>
		<td>
			<fieldset style="padding: 5px 25px; margin: 8px">
				<legend>
					<s:text name="hm.config.global.setting.other" />
				</legend>
				<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">

						<s:if test="%{hideTopPannel}">
							<tr>
								<td align="center" class="labelHere" colspan="2"><s:property
										value="%{selectedL2Feature.description}" /></td>
							</tr>
							<tr>
								<td style="padding: 2px 4px 4px 4px;" colspan="2">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="sepLine" width="100%"><img
												src="<s:url value="/images/spacer.gif" includeParams="none"/>"
												height="1" class="dblk" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2"><tiles:insertDefinition name="notes" /></td>
							</tr>
						</s:if>
						<tr style="display:<s:property value="displayNtpSet"/>">
							<td class="labelT1" colspan="2">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="noteInfo" width="380px"><label><s:text
													name="hm.config.start.setting.help" />
										</label>
										</td>
										<td>
											<a href='#guideHelps' onclick="openHelpPage();">
												<s:text name="hm.config.start.setting.help.click" />
											</a>
										</td>
									</tr>
									<tr>
										<td height="2px"></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr style="display:<s:property value="displayNtpSet"/>">
							<td class="labelT1" width="360px">
								<label> <s:text name="hm.config.start.setting" /> </label>
							</td>
							<td>
								<s:radio label="Gender" name="dataSource.modeType"
									list="%{modeType1}" disabled="%{disableMode}" listKey="key"
									listValue="value" onclick="selectExpress(true);" />
							</td>
						</tr>
						<tr style="display:<s:property value="displayNtpSet"/>">
							<td></td>
							<td><s:radio label="Gender" name="dataSource.modeType"
									list="%{modeType2}" disabled="%{disableMode}" listKey="key"
									listValue="value" onclick="selectExpress(false);" /></td>
						</tr>
						<tr>
							<td class="labelT1" colspan="2"><label> <s:text
										name="hm.config.start.security.note" /> </label></td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 25px;"><label>
									<s:text name="hm.config.start.hiveAp.password" /><font color="red">*</font> </label></td>
							<td><s:password name="dataSource.hiveApPassword" size="24"
									maxlength="32"
									onkeypress="return hm.util.keyPressPermit(event,'password');"
									id="hiveApPassword" showPassword="true" /> <s:textfield
									name="dataSource.hiveApPassword" size="24" maxlength="32"
									onkeypress="return hm.util.keyPressPermit(event,'password');"
									id="hiveApPassword_text" disabled="true"
									cssStyle="display: none;" /> <s:text
									name="hm.config.start.hiveAp.password.note" /></td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 25px;"><label>
									<s:text name="hm.config.start.hiveAp.password.confirm" /><font color="red">*</font></label></td>
							<td><s:password name="hiveApPasswordConfirm" size="24"
									maxlength="32" value="%{dataSource.hiveApPassword}"
									onkeypress="return hm.util.keyPressPermit(event,'password');"
									id="cfHiveApPassword" showPassword="true" /> <s:textfield
									name="hiveApPasswordConfirm" size="24" maxlength="32"
									onkeypress="return hm.util.keyPressPermit(event,'password');"
									id="cfHiveApPassword_text" disabled="true"
									cssStyle="display: none;" /> <s:checkbox id="chkToggleDisplay"
									name="ignore" value="true" disabled="%{writeDisable4Struts}"
									onclick="hm.util.toggleObscurePassword(this.checked,['hiveApPassword','cfHiveApPassword'],['hiveApPassword_text','cfHiveApPassword_text']);" />
								<s:text name="admin.user.obscurePassword" /></td>
						</tr>
						
						<tr>
							<td class="labelT1" colspan="2"><s:checkbox id="dataSource.enableAutoDiscovery" name="dataSource.enableAutoDiscovery" />
							<label><s:text name="hm.config.start.autodiscovery.enable" /></label></td>
						</tr>
								
						<tr style="display: none" id="quickStartPwdNoteTr">
							<td class="labelT1" colspan="2"><label> <s:text
										name="hm.missionux.wecomle.update.preshared.key.tip" /> </label></td>
						</tr>
						<tr style="display: none" id="quickStartPwdTr">
							<td class="labelT1" style="padding-left: 25px;"><label>
									<s:text
										name="hm.missionux.wecomle.update.preshared.key.title.default" /><font color="red">*</font> </label></td>
							<td><s:password name="dataSource.quickStartPwd" size="24"
									maxlength="32"
									onkeypress="return hm.util.keyPressPermit(event,'password');"
									id="quickPassword" showPassword="true" /> <s:textfield
									name="dataSource.quickStartPwd" size="24" maxlength="32"
									onkeypress="return hm.util.keyPressPermit(event,'password');"
									id="quickPassword_text" disabled="true"
									cssStyle="display: none;" /> <s:text
									name="hm.config.start.hivemanager.password.note" /></td>
						</tr>
						<tr style="display: none" id="quickStartPwdConfirmTr">
							<td class="labelT1" style="padding-left: 25px;"><label>
									<s:text name="hm.missionux.wecomle.update.preshared.key.title.confirm" /><font color="red">*</font> </label></td>
							<td><s:password size="24" maxlength="32"
									onkeypress="return hm.util.keyPressPermit(event,'password');"
									id="cfQuickPassword" showPassword="true" /> <s:textfield
									size="24" maxlength="32"
									onkeypress="return hm.util.keyPressPermit(event,'password');"
									id="cfQuickPassword_text" disabled="true"
									cssStyle="display: none;" /> <s:checkbox id="chkToggleDisplay3"
									name="ignore" value="true" disabled="%{writeDisable4Struts}"
									onclick="hm.util.toggleObscurePassword(this.checked,['quickPassword','cfQuickPassword'],['quickPassword_text','cfQuickPassword_text']);" />
								<s:text name="admin.user.obscurePassword" /></td>
						</tr>
						<s:if test="%{hideTopPannel}">
							<tr>
								<td height="5px"></td>
							</tr>
							<tr>
								<td class="labelT1" style="padding-left: 25px;"><label>
										<s:text name="hm.config.start.hivemanager.password" /> </label></td>
								<td><s:password name="adminPassword" size="24"
										maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'password');"
										id="adminPassword" showPassword="true" /> <s:textfield
										name="adminPassword" size="24" maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'password');"
										id="adminPassword_text" disabled="true"
										cssStyle="display: none;" /> <s:text
										name="hm.config.start.hivemanager.password.note" /></td>
							</tr>
							<tr>
								<td class="labelT1" style="padding-left: 25px;"><label>
										<s:text name="hm.config.start.hivemanager.password.confirm" />
								</label></td>
								<td><s:password name="adminPasswordConfirm" size="24"
										maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'password');"
										id="cfAdminPassword" showPassword="true" /> <s:textfield
										name="adminPasswordConfirm" size="24" maxlength="32"
										onkeypress="return hm.util.keyPressPermit(event,'password');"
										id="cfAdminPassword_text" disabled="true"
										cssStyle="display: none;" /> <s:checkbox
										id="chkToggleDisplay1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
										onclick="hm.util.toggleObscurePassword(this.checked,['adminPassword','cfAdminPassword'],['adminPassword_text','cfAdminPassword_text']);" />
									<s:text name="admin.user.obscurePassword" /></td>
							</tr>
						</s:if>
						<tr>
							<td height="10px"></td>
						</tr>
						<tr style="display:<s:property value="displayNtpSet"/>"
							id="expDetail">
							<td colspan="2">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="labelT1" width="360px"><label> <s:text
													name="hm.config.start.network" /><font color="red">*</font>
										</label></td>
										<td><s:textfield name="dataSource.networkName" size="24"
												maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'name');" />
											<s:text name="config.radiusOnHiveAp.nameRange1" /></td>
									</tr>
									<tr>
										<td height="10px"></td>
									</tr>
									<tr>
										<td class="labelT1" colspan="2"><label> <s:text
													name="hm.config.start.ntp.title" /> </label></td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 25px;"><label>
												<s:text name="hm.config.start.ntp" /> </label></td>
										<td><s:textfield name="ntpServer" value="%{ntpServer}"
												size="24" maxlength="32"
												onkeypress="return hm.util.keyPressPermit(event,'name');" />
											<s:text name="config.radiusOnHiveAp.nameRange0" /></td>
									</tr>
									<tr>
										<td height="10px"></td>
									</tr>
									<tr>
										<td class="labelT1" style="padding-left: 25px;"><label>
												<s:text name="hm.config.start.network.admin.time.zone" /> </label>
										</td>
										<td><s:select id="timezone" name="timezone"
												value="%{timezone}" list="%{enumTimeZone}" listKey="key"
												listValue="value" cssStyle="width: 265px;" /></td>
									</tr>
									<tr>
										<td height="10px"></td>
									</tr>
									<tr>
										<td class="labelT1"><label> <s:text
													name="hm.config.start.dns.primary" /> </label></td>
										<td><s:textfield name="dnsSer1" value="%{dnsSer1}"
												size="24" maxlength="15"
												onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
									</tr>
									<tr>
										<td height="10px"></td>
									</tr>
									<tr>
										<td class="labelT1"><label> <s:text
													name="hm.config.start.dns.secondary" /> </label></td>
										<td><s:textfield name="dnsSer2" value="%{dnsSer2}"
												size="24" maxlength="15"
												onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
									</tr>
									<tr>
										<td height="10px"></td>
									</tr>
									<tr>
										<td class="labelT1"><s:text
												name="hm.config.start.mgmtservice.option.led" />
										</td>
										<td><s:select name="dataSource.ledBrightness"
												list="%{enumSystemLed}" listKey="key" listValue="value"
												cssStyle="width: 100px;" /> <s:text
												name="config.mgmtservice.option.led.description" />
										</td>
									</tr>
									<tr>
										<td height="10px"></td>
									</tr>
									<tr>
										<td colspan="2" style="padding-left: 5px;"><s:checkbox
												name="dataSource.useAccessConsole"
												value="%{dataSource.useAccessConsole}"
												onclick="showConsolePass(this.checked);" /> <s:text
												name="hm.config.start.access.console.label" /></td>
									</tr>
									<tr>
										<td height="10px"></td>
									</tr>
									<tr
										style="display:<s:property value="dataSource.useAccessConsole?'':'none'"/>"
										id="consoleDetail">
										<td colspan="2" style="padding-left: 20px;">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="340px"><s:text
															name="hm.config.start.access.console.password" /> <font
														color="red"><s:text name="*" />
													</font>
													</td>
													<td><s:password id="keyValue"
															name="dataSource.asciiKey" size="24" maxlength="63"
															showPassword="true"
															onkeypress="return hm.util.keyPressPermit(event,'password');" />
														<s:textfield id="keyValue_text" name="dataSource.asciiKey"
															size="24" maxlength="63" cssStyle="display:none"
															onkeypress="return hm.util.keyPressPermit(event,'password');"
															disabled="true" /> <s:text
															name="config.ssid.keyValue_range" />
													</td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
															name="config.access.console.key.confirm" /> <font
														color="red"><s:text name="*" />
													</font>
													</td>
													<td><s:password id="confirmKeyValue" size="24"
															maxlength="63" showPassword="true"
															value="%{dataSource.asciiKey}"
															onkeypress="return hm.util.keyPressPermit(event,'password');" />
														<s:textfield id="confirmKeyValue_text" size="24"
															maxlength="63" cssStyle="display:none"
															onkeypress="return hm.util.keyPressPermit(event,'password');" />
														<s:checkbox id="chkToggleDisplay2" name="ignore"
															value="true" disabled="%{writeDisable4Struts}"
															onclick="hm.util.toggleObscurePassword(this.checked,['keyValue','confirmKeyValue'],['keyValue_text','confirmKeyValue_text']);" />
														<s:text name="config.access.console.key.obscure" /></td>
												</tr>
												<tr>
													<td height="10px"></td>
												</tr>
											</table></td>
									</tr>
								</table></td>
						</tr>
						<s:if test="%{hideTopPannel}">
							<tr>
								<td align="center" colspan="2"><input type="button"
									id="ignore" name="ignore" value="Save" class="button"
									onClick="submitAction('updateStart');"></td>
							</tr>
							<tr>
								<td height="5px"></td>
							</tr>
						</s:if>

					</table>

				</div>

			</fieldset>
		</td>
	</tr>
	<s:if test="%{!hideAccessPannel}">
	<tr>
		<td>
			<fieldset style="padding: 5px 25px;margin:8px">
				<legend><s:text name="hm.config.start.supportAccess.title"/></legend>
										<div>
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td class="labelT1" colspan="2"><label><s:text name="hm.config.start.supportAccess.selectOption" /></label>
														</td>
													</tr>
													<!-- select access option 1: readOnlyForGivenTime
													      fields:accOption accHours
													-->
													<tr>
														<td class="labelT1"></td>
														<td style="padding-bottom:10px">
															<s:radio
																label="option" name="accessOption"
																list="%{accessOption1}"
																listKey="key" 
																listValue="value"
																onclick="changeAccessMode('1')"
																value="%{accessOption}" 
																disabled="%{writeDisable4Access}"
																/>
																<s:textfield name="authorizedTime1" id="authorizedTime1"
																		value="%{authorizedTime1}" size="5"  disabled="%{writeDisable4Access}"/>
															<s:text name="hm.config.start.supportAccess.hours.note" />
														</td>
													</tr>
													<!-- select access option 2: readAndWriteForGivenTime
													      fields:accOption extentHours
													-->
													<tr>
														<td></td>
														<td style="padding-bottom:10px">
															<s:radio
																label="option" name="accessOption"
																list="%{accessOption2}"
																listKey="key" 
																listValue="value"
																onclick="changeAccessMode('2')"
																value="%{accessOption}" 
																disabled="%{writeDisable4Access}"
																/>
																
																<s:textfield name="authorizedTime2" id="authorizedTime2"
																		value="%{authorizedTime2}" size="5"  disabled="%{writeDisable4Access}"/>
															<s:text name="hm.config.start.supportAccess.hours.note" />
														</td>
													</tr>
													<!-- select access option 3: readOnlyForUnlimitedTime
													      fields:accOption 
													-->
													<tr>
														<td></td>
														<td style="padding-bottom:10px">
															<s:radio
																label="option" name="accessOption"
																list="%{accessOption3}"
																listKey="key" 
																listValue="value"
																onclick="changeAccessMode('3')"
																value="%{accessOption}" 
																disabled="%{writeDisable4Access}"
																/>
														</td>
													</tr>
													
													<!-- select access option 4: readAndWriteForUnlimitedTime
													      fields:accOption 
													-->
													<tr>
														<td></td>
														<td style="padding-bottom:10px">
															<s:radio
																label="option" name="accessOption"
																list="%{accessOption4}"
																listKey="key" 
																listValue="value"
																onclick="changeAccessMode('4')"
																value="%{accessOption}" 
																disabled="%{writeDisable4Access}"
																/>
																
														</td>
													</tr>
													
													<!-- select access option 5: readAndWriteNotAllowed
													      fields:accOption 
													-->
													<tr>
														<td></td>
														<td>
															<s:radio
																label="option" name="accessOption"
																list="%{accessOption0}"
																listKey="key" 
																listValue="value"
																onclick="changeAccessMode('0')"
																value="%{accessOption}" 
																disabled="%{writeDisable4Access}"
																/>
											
														</td>
													</tr>
													
													<tr>
														<td></td>
														<td class="labelT2">
															<table>
																<tr><td colspan="2"><label><s:text name="hm.config.start.supportAccess.timePanel.title"/></label></td></tr>
															</table>
															
														</td>
													</tr>
													<tr>
														<td></td>
														<td>
															<div id="remainingTimeDetails" style="width:450px;height:135px;border:1px solid #999">
																<table>
																	<tr>
																		<td><label><s:text name="hm.config.start.supportAccess.timeLeft">
																			<s:param><s:property value="%{defaultAuthorizedTime}" /></s:param></s:text>
																		</label></td>
																	</tr>
																	<tr>
																		<td style="padding:10px 20px">
																			<s:property value="%{leftHours}" />&nbsp&nbsp <label><s:text name="hm.config.start.supportAccess.hours"/>,</label>
																			<s:property value="%{leftMinutes}"/>&nbsp&nbsp <label><s:text name="hm.config.start.supportAccess.minutes"/>,</label>
																			<s:property value="%{leftSeconds}"/>&nbsp&nbsp<label><s:text name="hm.config.start.supportAccess.seconds"/></label>
																		</td>
																	</tr>
																	<tr><td><label><s:text name="hm.config.start.supportAccess.endTime"/></label></td></tr>
																	<tr><td style="padding:10px 20px"><s:property value="%{endTime}"/></td></tr>
																</table>
																
															</div>
															</td>
															</tr>
															</table>
															
										</div>
			</fieldset>
		</td>
</tr>
</s:if>
</table>