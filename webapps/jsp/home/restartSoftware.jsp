<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'restartSoftware';

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'restart' )
		{
			var btn = document.getElementById("restart");
			if(btn.value == "OK")
			{
			    showProcessing();
			}
			else
			{
				btn.value = "OK";
				document.getElementById("hideCancelBtn").style.display="block";
				document.getElementById("hideConfirm").style.display="block";
				document.getElementById("hideExplain").style.display="none";
				
				return;
			}
		}
		
		document.forms[formName].operation.value = operation;
   		document.forms[formName].submit();
	}
}

function validate(operation) {
	    
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="restartSoftware">
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
								<input type="button" name="restart" id="restart"
									value=<s:property value="%{restartBtnName}" /> class="button"
									onClick="submitAction('restart');"
									<s:property value="restartButtonDisabled" />>
							</td>
							<td>
								<div style="display:<s:property value="%{hideCancelBtn}"/>"
									id="hideCancelBtn">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<input type="button" name="cancel" value="Cancel"
													class="button" onClick="submitAction('cancel');">
											</td>
										</tr>
									</table>
								</div>
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
			<script>
				function delayHideNotes() {}
			</script>
			<tr>
				<td>
					<div style="display:<s:property value="%{hideConfirm}"/>"
						id="hideConfirm">
						<table class="editBox" cellspacing="0" cellpadding="0" border="0"
							width="500">
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<td style="padding:10px 10px 20px 10px" align="center"
									valign="middle">
									<label>
										<strong><s:text name="admin.hmOperation.confirm" />
										</strong>
									</label>
								</td>
							</tr>
						</table>
					</div>
					<div style="display:<s:property value="%{hideExplain}"/>"
						id="hideExplain">
						<table class="editBox" cellspacing="0" cellpadding="0" border="0"
							width="500">
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<td style="padding:10px 10px 20px 10px">
									<label>
										<strong><s:text
												name="admin.hmOperation.restartSoftware" /> </strong>
									</label>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</s:form>
</div>

