<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>


<script>
	var formName = "clientMgmtTest";

	function submitAction(operation) {
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}

	function insertPageContext() {
		document
				.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
		document.writeln('</td>');
	}

	// ACM Troubleshooting
	
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
	waitingPanel.setHeader("Sending test email...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}
	
	var YUD = YAHOO.util.Dom;
	function detectTrouble() {
		var YUC = YAHOO.util.Connect;
		var url = "<s:url action='clientMgmtTest' includeParams='none' />?operation=test";
		YUC.asyncRequest("GET", url, {
			success : succTrouble,
			failure : failureTrouble,
			timeout : 60000
		}, null);
		if (null == waitingPanel) {
			createWaitingPanel();
		}
		waitingPanel.setHeader("Detecting Client Management services...");
		waitingPanel.show();
	}

	function failureTrouble(o) {
	}

	function succTrouble(o) {
		if (null != waitingPanel) {
			waitingPanel.hide();
		}
		eval("var details = " + o.responseText);
		if (details.succ) {
			showTroubleMsg(details.msg, true);
		} else {
			showTroubleMsg(details.msg, false);
		}
	}

	function showTroubleMsg(msg, isSucc) {
		//hm.util.hideFieldError();
		if (isSucc) {
			YUD.get("trbInfoMessage").innerHTML = msg;
			YUD.get("trbErrorMessage").innerHTML = '';
		} else {
			YUD.get("trbInfoMessage").innerHTML = '';
			YUD.get("trbErrorMessage").innerHTML = msg;
		}
		YUD.get("troubleshootingId").style.display = '';
		noteTimer = setTimeout("hideTroubleMsg()", 10 * 1000); // 5 seconds
	}

	function hideTroubleMsg() {
		YUD.get("trbInfoMessage").innerHTML = '';
		YUD.get("trbErrorMessage").innerHTML = '';
		YUD.get("troubleshootingId").style.display = 'none';
	}
</script>

<div id="content">
	<s:form action="clientMgmtTest">
		<table width="500px" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context"></tiles:insertDefinition>
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="test" value="Test"
								class="button" onclick="detectTrouble();" /></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td><tiles:insertDefinition name="notes"></tiles:insertDefinition>
				</td>
			</tr>
			<tr>
				<td>
					<table class="editBox" border="0" cellspacing="0" cellpadding="0"
						width="500px">
						<tr id="troubleshootingToolId">
							<td style="padding: 5px 5px 5px 5px">
								<fieldset style="width: 500px">
									<legend>
										<s:text
											name="home.hmSettings.clientManagement.clientMgmtTest.troubleshooting"></s:text>
									</legend>
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td style="padding-left: 10px">
												<div id="troubleshootingId" style="display: none">
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td class="noteInfo" id="trbInfoMessage"></td>
														</tr>
														<tr>
															<td class="noteError" id="trbErrorMessage"></td>
														</tr>
													</table>
												</div>
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
	</s:form>
</div>