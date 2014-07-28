<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.admin.HmUser"%>

<script>
var formName = 'userPasswordModify';

function onLoadPage()
{
	<s:if test="%{'disabled' != writeDisabled4HHM}">
	  	var modifyPsdEl = document.getElementById("enableChangePassword");
		if(modifyPsdEl.checked){
		    var modifyArea = document.getElementById("modifyArea");
		    modifyArea.style.display = "block";
		}
	</s:if>
}

function submitAction(operation) {
	if (validate(operation)) {
		showProcessing();
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}
function validate(operation)
{
	<s:if test="%{'disabled' != writeDisabled4HHM}">
	var modifyPsdEl = document.getElementById("enableChangePassword");
	if(modifyPsdEl.checked){
		var objOld;
		var objNew;
		var objConfirm;
		if (document.getElementById("chkToggleDisplay").checked)
		{
			objOld=document.getElementById("passwordOld");
			objNew=document.getElementById("passwordNew");
			objConfirm=document.getElementById("passwordConfirm");
		}
		else
		{
			objOld=document.getElementById("passwordOld_text");
			objNew=document.getElementById("passwordNew_text");
			objConfirm=document.getElementById("passwordConfirm_text");
		}

		if(objOld.value.length==0)
		{
		    hm.util.reportFieldError(objOld, '<s:text name="error.requiredField"><s:param><s:text name="admin.user.password.old" /></s:param></s:text>');
	        objOld.focus();
	        return false;
		}
		
		return hm.util.validateUserNewPasswordFormat(objNew, objConfirm, '<s:text name="admin.user.password.new" />',
    			'<s:text name="admin.user.password.confirm" />', 8, '<s:text name="hm.config.start.hivemanager.password.note" />', '<s:property value="dataSource.userName" />');
	}
	</s:if>
	return true;
}

function modifyPassword(checked){
	var modifyArea = document.getElementById("modifyArea");
	if(checked){
		modifyArea.style.display = "block";
	}else{
		modifyArea.style.display = "none";
	}
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}
</script>

<div id="content">
	<s:form action="userPasswordModify">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
								<input type="button" name="ignore"
									value="<s:text name="button.update"/>" class="button"
									onClick="submitAction('update');" >
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="500" style="padding: 3px 6px 3px 6px;">
						<tr>
							<td>
								<%-- add this password dummy to fix issue with auto complete function --%>
								<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
									type="password">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td colspan="2">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="160">
														<label>
															<s:text name="admin.user.userName" />
														</label>
													</td>
													<td>
													<%-- customer user info in MyHive --%>
														<s:if test="%{'disabled' == writeDisabled4HHM && userContext != null && userContext.getId() > 0}">
															<s:property value="dataSource.userFullName" />
														</s:if>
														<s:else>
															<s:property value="dataSource.userName" />
														</s:else>
													</td>
												</tr>
												<s:if test="%{'disabled' != writeDisabled4HHM}">
												<tr>
													<td class="labelT1">
														<label>
															<s:text name="admin.user.userFullName" />
														</label>
													</td>
													<td>
														<s:property value="dataSource.userFullName" />
													</td>
												</tr>
												</s:if>
												<tr>
													<td class="labelT1">
														<label>
															<s:text name="admin.user.emailAddress" />
														</label>
													</td>
													<td>
														<s:property value="dataSource.emailAddress" />
													</td>
												</tr>
												<tr>
													<td class="labelT1">
														<label>
															<s:text name="admin.user.userGroup" />
														</label>
													</td>
													<td>
														<s:property value="%{groupName}" />
													</td>
												</tr>
												<tr>
													<td class="labelT1">
														<label>
															<s:text name="admin.user.promptChanges" />
														</label>
													</td>
													<td>
														<s:checkbox name="promptChanges"></s:checkbox>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="4"></td>
									</tr>
									<s:if test="%{'disabled' != writeDisabled4HHM}">
									<tr>
										<td class="sepLine" colspan="3">
											<img src="<s:url value="/images/spacer.gif"/>" height="1"
												class="dblk" />
										</td>
									</tr>
									<tr>
										<td style="padding-left: 6px;" colspan="2" class="labelT1">
											<s:checkbox id="enableChangePassword"
												name="enableChangePassword"
												onclick="modifyPassword(this.checked)"></s:checkbox>
											<label>
												<s:text name="admin.user.changePasswordLabel" />
											</label>
										</td>
									</tr>
									<tr>
										<td colspan="2">
											<div id="modifyArea" style="display: none;">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td class="labelT1" width="160">
															<label>
																<s:text name="admin.user.password.old" /><font color="red"><s:text name="*" /> </font>
															</label>
														</td>
														<td>
															<s:password id="passwordOld" name="passwordOld" size="24"
																maxlength="%{passwdLength}" />
															<s:textfield id="passwordOld_text" name="passwordOld"
																size="24" maxlength="%{passwdLength}"
																cssStyle="display:none" disabled="true" />
															<s:text name="admin.user.password.ranger" />
														</td>
													</tr>
													<tr>
														<td class="labelT1" width="160">
															<label>
																<s:text name="admin.user.password.new" /><font color="red"><s:text name="*" /> </font>
															</label>
														</td>
														<td>
															<s:password id="passwordNew" name="passwordNew" size="24"
																maxlength="%{passwdLength}" />
															<s:textfield id="passwordNew_text" name="passwordNew"
																size="24" maxlength="%{passwdLength}"
																cssStyle="display:none" disabled="true" />
															<s:text name="hm.config.start.hivemanager.password.note" />
														</td>
													</tr>
													<tr>
														<td class="labelT1" width="160">
															<label>
																<s:text name="admin.user.password.confirm" /><font color="red"><s:text name="*" /> </font>
															</label>
														</td>
														<td>
															<s:password id="passwordConfirm" name="passwordConfirm"
																size="24" maxlength="%{passwdLength}" />
															<s:textfield id="passwordConfirm_text"
																name="passwordConfirm" size="24"
																maxlength="%{passwdLength}" cssStyle="display:none"
																disabled="true" />
															<s:text name="hm.config.start.hivemanager.password.note" />
														</td>
													</tr>
													<tr>
														<td>
															&nbsp;
														</td>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td>
																		<s:checkbox id="chkToggleDisplay" name="ignore"
																			value="true"
																			onclick="hm.util.toggleObscurePassword(this.checked,['passwordOld','passwordNew','passwordConfirm'],['passwordOld_text','passwordNew_text','passwordConfirm_text']);" />
																	</td>
																	<td>
																		<s:text name="admin.user.obscurePassword" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
									</s:if>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
