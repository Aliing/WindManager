<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
	var formName = 'librarySipTest';
	
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
		var username = document.forms[formName]["joinDomainUsername"];
		username.focus();
	}
	
	function submitAction(operation){
		if(!validate(operation)){
			return;
		}
		waitingPanel.show();
		document.getElementById("resultPanel").innerHTML = "";
		document.forms[formName].operation.value = operation;
		ajaxRequest(formName, "<s:url action='librarySipTest' includeParams='none' />", processResult, "post");
	}
	
	function processResult(o){
		waitingPanel.hide();
		eval("var data = " + o.responseText);
		var resultPanel = document.getElementById("resultPanel");
		if(data.r){
			resultPanel.innerHTML = hm.util.convert2Html(data.r);
			//resultPanel.innerText = hm.util.convert2Html(data.r);
		}
	}
	
	function validate(operation){
		if(!validateServer()){
			return false;
		}
		
		if(!validateJoinDomainConfig()){
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
			hm.util.reportFieldError(server, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.test.aaa.server" /></s:param></s:text>');
			return false;
		}
		return true;
	}

	function insertPageContext() {
		document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
		document.writeln('</td>');
	}
</script>

<div id="content"><s:form action="librarySipTest">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
				<table border="0" cellspacing="0" cellpading="0">
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
							<input style="display : none" name="dummy_pwd" id="dummy_pwd" type="password">
							<table border="0" cellspacing="0" cellpading="0" width="100%" class="embeddBox">
								<tr><!-- note -->
									<td style="padding-top: 10px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelH1">
												<s:text name="hm.tool.test.librarySip.note" />
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
															<td width="190px" class="labelT1" style="padding-left: 0px"><s:text name="hm.tool.test.aaa.server" /><span class="required">*</span></td>
															<td><s:select cssStyle="width:194px;" list="%{servers}" listKey="key" listValue="value" name="server" /></td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr><td height="10px"></td></tr>
								<tr><!-- comment -->
									<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="noteInfo">
													<s:text name="librarySipComment" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr><td height="5px"></td></tr>
								<tr><!-- configure fields -->
									<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td width="180px" class="labelT1"><s:text name="hm.tool.test.aaa.sipDomain" /></td>
															<td><s:select cssStyle="width:194px;" list="%{joinDomainSipDomains}" listKey="key" listValue="value" name="joinDomainSipDomain" /></td>
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