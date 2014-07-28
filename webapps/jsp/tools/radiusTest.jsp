<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.tools.RadisuTestAction" %>

<script>
var formName = 'radiusTest';
var TEST_TYPE_AUTH = <%=RadisuTestAction.TEST_TYPE_AUTH%>;
var TEST_TYPE_ACCT = <%=RadisuTestAction.TEST_TYPE_ACCT%>;

var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"260px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("Retrieving Information...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

YAHOO.util.Event.addListener(window, "load", function() {
		createWaitingPanel();
		hm.util.createEditableSelect(document.forms[formName]["server"], "serverInput");
});

function testTypeChanged(option){
	var authCommentTitleTr = document.getElementById("authCommentTitleTr");
	var authCommentTr = document.getElementById("authCommentTr");
	var authCommentBlankTr = document.getElementById("authCommentBlankTr");
	var authUPTr = document.getElementById("authUPTr");
	var authUPBlankTr = document.getElementById("authUPBlankTr");
	document.getElementById("resultPanel").innerHTML = "";
	switch(+option){
	case TEST_TYPE_AUTH:
		hm.util.show(authCommentTitleTr);
		hm.util.show(authCommentTr);
		hm.util.show(authCommentBlankTr);
		hm.util.show(authUPTr);
		hm.util.show(authUPBlankTr);
		break;
	case TEST_TYPE_ACCT:
		hm.util.hide(authCommentTitleTr);
		hm.util.hide(authCommentTr);
		hm.util.hide(authCommentBlankTr);
		hm.util.hide(authUPTr);
		hm.util.hide(authUPBlankTr);
		break;
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

function validate(operation){
	var server = document.forms[formName]["server"];
	var serverInput = server.inputElement;
	var client = document.forms[formName]["client"];
	if(serverInput.value.length == 0){
		hm.util.reportFieldError(server.parentNode, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.test.radius.label.server" /></s:param></s:text>');
		return false;
	}
	if(client.value.length == 0){
		hm.util.reportFieldError(client, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.test.radius.label.client" /></s:param></s:text>');
		return false;
	}

	var options = document.forms[formName]["testType"];
	var selectType;
	for(var i=0; i<options.length; i++){
		if(options[i].checked){
			selectType = options[i].value;
			break;
		}
	}
	if(selectType == TEST_TYPE_AUTH){
		var username = document.forms[formName]["username"];
		var password = document.forms[formName]["password"];
		if(username.value.length == 0){
			hm.util.reportFieldError(username, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.test.aaa.username" /></s:param></s:text>');
			username.focus();
			return false;
		}
		if(password.value.length == 0){
			hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.test.aaa.password" /></s:param></s:text>');
			password.focus();
			return false;
		}
	}
	return true;
}

function submitAction(operation){
	if(!validate(operation)){
		return;
	}
	waitingPanel.show();
	document.getElementById("resultPanel").innerHTML = "";
	document.forms[formName].operation.value = operation;
	ajaxRequest(formName, "<s:url action='radiusTest' includeParams='none' />", processResult, "post");
}

function processResult(o){
	waitingPanel.hide();
	eval("var data = " + o.responseText);
	var resultPanel = document.getElementById("resultPanel");
	if(data.r){
		resultPanel.innerHTML = hm.util.convert2Html(data.r);
	}
}
</script>

<div id="content"><s:form action="radiusTest">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<input type="button" id="ignore" name="ignore" value="Test"
							class="button" onClick="submitAction('test');"
							<s:property value="writeDisabled" />>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="620px" style="padding: 8px;">
					<tr>
						<td>
							<%-- add this password dummy to fix issue with auto complete function --%>
							<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
							<div>
							<fieldset>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr><!-- note -->
									<td style="padding-top: 10px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelH1">
												<s:text name="hm.tool.test.radius.note" />
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr><td height="15px"></td></tr>
								<tr><!-- server/client -->
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td width="190px" class="labelT1" style="padding-left: 0px"><s:text name="hm.tool.test.radius.label.server" /><span class="required">*</span></td>
														<td><s:select cssStyle="width:194px;" list="%{servers}" listKey="key" listValue="value" name="server" /></td>
													</tr>
													<tr>
														<td class="labelT1" style="padding-left: 0px"><s:text name="hm.tool.test.radius.label.client" /><span class="required">*</span></td>
														<td><s:select cssStyle="width:194px;" list="%{clients}" listKey="key" listValue="value" name="client" /></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								<tr><!-- Test options -->
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><s:text name="hm.tool.test.radius.option.label" /></td>
										</tr>
										<tr><td height="2px"></td></tr>
										<tr>
											<td style="padding-left: 10px"><s:radio cssStyle="margin-left: 0px;" label="Gender"	name="testType" list="%{testType1}" listKey="key" listValue="value" onclick="testTypeChanged(this.value);" /></td>
										</tr>
										<tr>
											<td style="padding-left: 10px"><s:radio cssStyle="margin-left: 0px;" label="Gender"	name="testType" list="%{testType2}" listKey="key" listValue="value" onclick="testTypeChanged(this.value);" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								<tr id="authCommentTitleTr"><!-- comment title -->
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelH1">
												<s:text name="hm.tool.test.radius.comment.title" />
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr id="authCommentTr"><!-- comment -->
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="noteInfo">
												<s:text name="hm.tool.test.radius.comment" />
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr id="authCommentBlankTr"><td height="5px"></td></tr>
								<tr id="authUPTr"><!-- username/password -->
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td width="180px" class="labelT1"><s:text name="hm.tool.test.aaa.username" /><span class="required">*</span></td>
														<td><s:textfield name="username" size="32" maxlength="32" /> <s:text name="hm.tool.test.aaa.username.note" /></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="hm.tool.test.aaa.password" /><span class="required">*</span></td>
														<td><s:password name="password" size="32" maxlength="32" /> <s:text name="hm.tool.test.aaa.password.note" /></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr id="authUPBlankTr"><td height="5px"></td></tr>
							</table>
							</fieldset>
							</div>
							<div style="height: 5px;"></div>
							<div>
							<fieldset><legend><s:text name="hm.tool.test.aaa.result" /></legend>
							<div id="resultPanel" style="height: 100%; width: 100%; padding-top: 5px; word-wrap: break-word; overflow: auto;">
							</div>
							</fieldset>
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table> 
</s:form></div>
