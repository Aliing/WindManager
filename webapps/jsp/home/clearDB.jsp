<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'clearDB';

var thisOperation;
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
	waitingPanel.setHeader("The operation is progressing...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

function onLoadPage() 
{
	createWaitingPanel();
}

function submitAction(operation) {
	if (operation == 'clearDB' )
	{
		var btn = document.getElementById("clearDB");
		if(btn.value == "OK")
		{
		   if(waitingPanel != null)
			{
				waitingPanel.setHeader("The operation is progressing...");
				waitingPanel.show();
			}
			
			document.forms[formName].operation.value = operation;
	  		document.forms[formName].submit();
		}
		else
		{
			url = "<s:url action='clearDB' includeParams='none' />" + "?operation=checkHAStatus&ignore=" + new Date().getTime();	
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: checkResult,timeout: 120000}, null);
			if (waitingPanel != null)
			{
				waitingPanel.setHeader("Checking status...");
				waitingPanel.show();
			}
			return;
		}
	}else if (operation == 'cancel' ) {
		var btn = document.getElementById("clearDB");
		btn.value = "Erase Database";
		document.getElementById("hideCancelBtn").style.display="none";
		document.getElementById("hideConfirm").style.display="none";
		document.getElementById("hideExplain").style.display="block";
		
		return;
	}

}

function checkResult(o)
{
	if (waitingPanel != null)
	{
		waitingPanel.hide();
	}

	eval("var result = " + o.responseText);
	if(result.haEnable)
	{
		var haTipsText = '<font color="red"><s:text name="admin.hmOperation.clearDB.haTips" /></font>';
		if(result.slaveOnline){
			confirmDialog.cfg.setProperty('text', haTipsText + '<br/>' + '<s:text name="admin.hmOperation.continue.confirm" />');
			confirmDialog.show();
		}else{
			if(result.homeDomain){ // Home Domain
				// TODO Is home domain master HM continue this operation is good for the slave node??  
				confirmDialog.cfg.setProperty('text', '<s:text name="admin.hmOperation.clearDB.slaveNode" />'
						+ '<br/>' + haTipsText + '<br/>' + '<s:text name="admin.hmOperation.continue.confirm" />');
				confirmDialog.show();
			}else{ // VHM 
				confirmDialog.cfg.setProperty('text', '<s:text name="admin.hmOperation.clearDB.slaveNode" />'
						+ '<br/>' + haTipsText + '<br/>' + '<s:text name="admin.hmOperation.continue.confirm" />');
				confirmDialog.show();
			}
		}
	}
	else
	{
		doContinueOper();
	}
}

function doContinueOper() 
{
	var btn = document.getElementById("clearDB");
	btn.value = "OK";
	document.getElementById("hideCancelBtn").style.display="block";
	document.getElementById("hideConfirm").style.display="block";
	document.getElementById("hideExplain").style.display="none";
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="clearDB">
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
								<input type="button" name="clearDB" id="clearDB"
									value="Erase Database" style="width:105px" class="button"
									onClick="submitAction('clearDB');"
									<s:property value="writeDisabled" />>
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
								<td style="padding:10px 0 20px 10px" align="center" valign="middle" >
									<label>
										<strong><s:text name="admin.hmOperation.clearDB.confirm" />
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
												name="admin.hmOperation.clearDB" /> </strong>
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

