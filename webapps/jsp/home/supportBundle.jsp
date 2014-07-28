<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'supportBundle';

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
	waitingPanel.setHeader("Request is being processed...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

function onLoadPage() {
	// Overlay for waiting dialog
	createWaitingPanel();
}

function submitAction(operation) 
{
	if (validate(operation)) 
	{
		if (operation == 'createData') 
		{
			createTechData();
			return;
		}	
	
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function createTechData()
{
	url = "<s:url action='supportBundle' includeParams='none' />" + "?operation=createData&ignore=" + new Date().getTime();
	
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createDataResult, failure:abortResult,timeout: 3000000}, null);
	
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var createDataResult = function(o) 
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	
	eval("var result = " + o.responseText);
	if(result.success)
	{
		showHangMessage("Download technical support packet file ...");
		hm.util.show('downloadSection');
	}
	else
	{
		showErrorMessage(result.message);
	}
}

var abortResult = function(o) 
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function validate(operation) 
{
	return true;
}

function showHangMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteInfo";
	hm.util.show("noteSection");
}

function hideMessage()
{
	hm.util.hide('noteSection');
}

function showErrorMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteError";
	hm.util.show("noteSection");
}

function initNoteSection()
{
	hm.util.hide('downloadSection');
	hm.util.hide('noteSection');
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="supportBundle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<input type="button" name="save" value="Save" class="button"
						onClick="submitAction('createData');"
						<s:property value="writeDisabled" />>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td colspan="10">
					<div id="noteSection" style="display:none">
						<table width="400px" border="0" cellspacing="0" cellpadding="0"
							class="note">
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td id="noteTD">
								</td>
								<td class="buttons">
									<div id="downloadSection" style="display:none">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td>
													<input type="button" id="downloadBtn" name="ignore"
														value="Download" class="button"
														onClick="submitAction('download');">
												</td>
												<td>
													<input type="button" id="cancelBtn" name="ignore"
														value="Cancel" class="button"
														onClick="initNoteSection();">
												</td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
							<tr>
								<td height="5"></td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
			<tr>
				<td>
				<div>
						<table class="editBox" cellspacing="0" cellpadding="0" border="0"
							width="500">
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<td style="padding:10px 10px 20px 10px">
									<label>
										<strong><s:text name="admin.hmOperation.supportBundle" />
										</strong>
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
