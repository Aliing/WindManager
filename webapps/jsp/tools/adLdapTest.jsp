<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.tools.AdLdapTestAction" %>

<script>
var formName = 'adLdapTest';
var TEST_TYPE_AD = <%=AdLdapTestAction.TEST_TYPE_AD%>;
var TEST_TYPE_LDAP = <%=AdLdapTestAction.TEST_TYPE_LDAP%>;
var TEST_TYPE_JOIN_DOMAIN = <%=AdLdapTestAction.TEST_TYPE_JOIN_DOMAIN%>;

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
});

function onLoadPage(){
	var username = document.forms[formName]["adUsername"];
	username.focus();
}


function testTypeChanged(option){
	var adTr = document.getElementById("adTr");
	var ldapTr = document.getElementById("ldapTr");
	var joinDomainTr = document.getElementById("joinDomainTr");
	var commentAdTr = document.getElementById("commentAdTr");
	var commentLdapTr = document.getElementById("commentLdapTr");
	var commentJoinDomainTr = document.getElementById("commentJoinDomainTr");
	var configureAd = document.getElementById("configureAd");
	var configureLdap = document.getElementById("configureLdap");
	var configureJoinDomain = document.getElementById("configureJoinDomain");
	document.getElementById("resultPanel").innerHTML = "";
	switch(+option){
	case TEST_TYPE_AD:
		hm.util.show(adTr);
		hm.util.hide(ldapTr);
		hm.util.hide(joinDomainTr);
		hm.util.show(commentAdTr);
		hm.util.hide(commentLdapTr);
		hm.util.hide(commentJoinDomainTr);
		hm.util.show(configureAd);
		hm.util.hide(configureLdap);
		hm.util.hide(configureJoinDomain);
		break;
	case TEST_TYPE_LDAP:
		hm.util.hide(adTr);
		hm.util.show(ldapTr);
		hm.util.hide(joinDomainTr);
		hm.util.hide(commentAdTr);
		hm.util.show(commentLdapTr);
		hm.util.hide(commentJoinDomainTr);
		hm.util.hide(configureAd);
		hm.util.show(configureLdap);
		hm.util.hide(configureJoinDomain);
		break;
	case TEST_TYPE_JOIN_DOMAIN:
		hm.util.hide(adTr);
		hm.util.hide(ldapTr);
		hm.util.show(joinDomainTr);
		hm.util.hide(commentAdTr);
		hm.util.hide(commentLdapTr);
		hm.util.show(commentJoinDomainTr);
		hm.util.hide(configureAd);
		hm.util.hide(configureLdap);
		hm.util.show(configureJoinDomain);
		break;
	}
}

function submitAction(operation){
	if(!validate(operation)){
		return;
	}
	waitingPanel.show();
	document.getElementById("resultPanel").innerHTML = "";
	document.forms[formName].operation.value = operation;
	ajaxRequest(formName, "<s:url action='adLdapTest' includeParams='none' />", processResult, "post");
}

function processResult(o){
	waitingPanel.hide();
	eval("var data = " + o.responseText);
	var resultPanel = document.getElementById("resultPanel");
	if(data.r){
		resultPanel.innerHTML = hm.util.convert2Html(data.r);
	}
}

function validate(operation){
	var options = document.forms[formName]["testType"];
	var selectType;
	for(var i=0; i<options.length; i++){
		if(options[i].checked){
			selectType = options[i].value;
			break;
		}
	}
	if(!validateServer()){
		return false;
	}
	switch(+selectType){
	case TEST_TYPE_AD:
		if(!validateAdConfig()){
			return false;
		}
		break;
	case TEST_TYPE_LDAP:
		if(!validateLdapConfig()){
			return false;
		}
		break;
	case TEST_TYPE_JOIN_DOMAIN:
		if(!validateJoinDomainConfig()){
			return false;
		}
		break;
	}
	return true;
}

function validateAdConfig(){
	var username = document.forms[formName]["adUsername"];
	var password = document.forms[formName]["adPassword"];
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
	return true;
}

function validateLdapConfig(){
	var username = document.forms[formName]["ldapUsername"];
	if(username.value.length == 0){
		hm.util.reportFieldError(username, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.test.aaa.username" /></s:param></s:text>');
		username.focus();
		return false;
	}
	return true;
}

function validateJoinDomainConfig(){
	var username = document.forms[formName]["joinDomainUsername"];
	var password = document.forms[formName]["joinDomainPassword"];
	if(password.value.length > 0 && username.value.length == 0){
		hm.util.reportFieldError(username, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.test.aaa.username" /></s:param></s:text>');
		username.focus();
		return false;
	}
	if(username.value.length > 0 && password.value.length == 0){
		hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.test.aaa.password" /></s:param></s:text>');
		password.focus();
		return false;
	}
	return true;
}

function validateServer(){
	var server = document.forms[formName]["server"];
	if(server.value.trim()==""){
		hm.util.reportFieldError(server, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.test.radius.label.server" /></s:param></s:text>');
		return false;
	}
	return true;
}



function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}
</script>

<div id="content"><s:form action="adLdapTest">
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
										<tr id="adTr">
											<td class="labelH1">
												<s:text name="hm.tool.test.ad.note" />
											</td>
										</tr>
										<tr id="ldapTr" style="display: none;">
											<td class="labelH1">
												<s:text name="hm.tool.test.ldap.note" />
											</td>
										</tr>
										<tr id="joinDomainTr" style="display: none;">
											<td class="labelH1">
												<s:text name="hm.tool.test.joinDomain.note" />
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr><td height="15px"></td></tr>
								<tr><!-- HiveAP RADIUS server -->
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td width="190px" class="labelT1" style="padding-left: 0px"><s:text name="hm.tool.test.radius.label.server" /><span class="required">*</span></td>
														<td><s:select cssStyle="width:194px;" list="%{servers}" listKey="key" listValue="value" name="server" /></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr><td height="5px"></td></tr>
								<tr><!-- Test options -->
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><s:radio cssStyle="margin-left: 0px;" label="Gender"	name="testType" list="%{testType1}" listKey="key" listValue="value" onclick="testTypeChanged(this.value);" /></td>
										</tr>
										<tr>
											<td><s:radio cssStyle="margin-left: 0px;" label="Gender"	name="testType" list="%{testType2}" listKey="key" listValue="value" onclick="testTypeChanged(this.value);" /></td>
										</tr>
										<tr>
											<td><s:radio cssStyle="margin-left: 0px;" label="Gender"	name="testType" list="%{testType3}" listKey="key" listValue="value" onclick="testTypeChanged(this.value);" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								<tr><!-- comment -->
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr id="commentAdTr">
											<td class="noteInfo">
												<s:text name="hm.tool.test.ad.comment" />
											</td>
										</tr>
										<tr id="commentLdapTr" style="display: none;">
											<td class="noteInfo">
												<s:text name="hm.tool.test.ldap.comment" />
											</td>
										</tr>
										<tr id="commentJoinDomainTr" style="display: none;">
											<td class="noteInfo">
												<s:text name="hm.tool.test.joinDomain.comment" />
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr><td height="5px"></td></tr>
								<tr><!-- configure fields -->
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr id="configureAd">
											<td>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td width="180px" class="labelT1"><s:text name="hm.tool.test.aaa.username" /><span class="required">*</span></td>
														<td><s:textfield name="adUsername" size="32" maxlength="32" /> <s:text name="hm.tool.test.aaa.username.note" /></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="hm.tool.test.aaa.password" /><span class="required">*</span></td>
														<td><s:password name="adPassword" size="32" maxlength="64" /> <s:text name="hm.tool.test.aaa.password.note" /></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="hm.tool.test.aaa.domain" /></td>
														<td><s:textfield name="adDomain" size="32" maxlength="64" /> <s:text name="hm.tool.test.aaa.domain.note" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr id="configureLdap" style="display: none;">
											<td>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td width="180px" class="labelT1"><s:text name="hm.tool.test.aaa.username" /><span class="required">*</span></td>
														<td><s:textfield name="ldapUsername" size="32" maxlength="32" /> <s:text name="hm.tool.test.aaa.username.note" /></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="hm.tool.test.aaa.adDomain" /></td>
														<td><s:textfield name="ldapAdDomain" size="32" maxlength="64" /> <s:text name="hm.tool.test.aaa.adDomain.note" /></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="hm.tool.test.ldap.label.basedn" /></td>
														<td><s:textfield name="ldapBaseDn" size="32" maxlength="256" /> <s:text name="hm.tool.test.ldap.label.basedn.note" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr id="configureJoinDomain" style="display: none;">
											<td>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td width="180px" class="labelT1"><s:text name="hm.tool.test.aaa.adDomain" /></td>
														<td><s:select cssStyle="width:194px;" list="%{joinDomainAdDomains}" listKey="key" listValue="value" name="joinDomainAdDomain" /></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="hm.tool.test.aaa.username" /></td>
														<td><s:textfield name="joinDomainUsername" size="32" maxlength="32" /> <s:text name="hm.tool.test.aaa.username.note" /></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="hm.tool.test.aaa.password" /></td>
														<td><s:password name="joinDomainPassword" size="32" maxlength="64" /> <s:text name="hm.tool.test.aaa.password.note" /></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr><td height="5px"></td></tr>
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
