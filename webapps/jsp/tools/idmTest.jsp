<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
	var formName = 'idmTest';
	
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
	
	function submitAction(operation){
		if(!validate(operation)){
			return;
		}
		waitingPanel.show();
		document.getElementById("resultPanel").innerHTML = "";
		document.forms[formName].operation.value = operation;
		ajaxRequest(formName, "<s:url action='idmTest' includeParams='none' />", processResult, "post");
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
		
		
		return true;
	}
	
	function validateServer(){
		var serverMacAddress = document.forms[formName]["serverMacAddress"];
		if(serverMacAddress.value.trim()==""){
			hm.util.reportFieldError(serverMacAddress, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.test.aaa.server" /></s:param></s:text>');
			return false;
		}
		return true;
	}

	function insertPageContext() {
		document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
		document.writeln('</td>');
	}
	
	function chooseMode(proxyMode){
		doAjaxRequest("updateProxyMode",proxyMode,callback);
	}
	
	function doAjaxRequest(operation,proxyMode,callback){
		url = "<s:url action='idmTest' includeParams='none' />?operation="+operation+"&proxyMode="+proxyMode+"&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:callback,timeout: 60000});
		if(waitingPanel != null){
			waitingPanel.show();
		}
	}
	
	function callback(o){
		if(waitingPanel != null){
			waitingPanel.hide();
		}
		
		eval("var data = " + o.responseText);
		
		if(data != null){
			var apArray = new Array();
			apArray = data.result;
			var serverMacAddress = document.getElementById("serverMacAddress");
			removeAllOptions(serverMacAddress);
			for(var i=0;i<apArray.length;i++){
				var option = document.createElement("option");
				option.value = apArray[i].key;
				option.text = apArray[i].value;
				serverMacAddress.add(option);
			}
		}
	}
	
	function removeAllOptions(data){
		for(var i = 0 ; i < data.options.length;){
			data.remove(data.options[i]);
		}
	}
</script>

<div id="content"><s:form action="idmTest">
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
							<table border="0" cellspacing="0" cellpading="0"width="100%" class="embeddBox">
							<%-- 	<tr><!-- note -->
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
								<tr><td height="15px"></td></tr>--%>
								<tr><!-- Proxy Model -->
									<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td width="190px" class="labelT1"><s:text name="hm.tool.test.idm.proxy.mode" /></td>
															<td width="120px">
																<s:radio list="#{'radsec':'RadSec Proxy'}"
																 name="proxyMode" value="%{proxyMode}" onclick="chooseMode(this.value);"/>
															</td>
															<td width="120px">
																<s:radio list="#{'auth':'AUTH Proxy'}"
																 name="proxyMode" value="%{proxyMode}" onclick="chooseMode(this.value);"/>
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr><!-- HiveAP RADIUS server -->
									<td>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td id="radsecServer">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td width="190px" class="labelT1"><s:text name="hm.tool.test.idm.proxy.server.hostname" /><span class="required">*</span></td>
															<td><s:select cssStyle="width:194px;" list="%{servers}" listKey="key" listValue="value" name="serverMacAddress" id="serverMacAddress"/></td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
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