<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'portalSettings';

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

function submitAction(operation) 
{
	if (validate(operation))
	{
		showProcessing();
	    document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) 
{
	if (operation != 'update')
	{
		return true;
	}

	var primaryCapwapIP = document.getElementById("primaryCapwapIP");
	var backupCapwapIP = document.getElementById("backupCapwapIP");
	if (primaryCapwapIP.value.length > 32) {
		hm.util.reportFieldError(primaryCapwapIP, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.primaryCapwapIP" /></s:param><s:param><s:text name="admin.capwap.serverNameRange" /></s:param></s:text>');
		primaryCapwapIP.focus();
		return false;
	}
	
	if (backupCapwapIP.value.length > 32) {
		hm.util.reportFieldError(backupCapwapIP, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.backupCapwapIP" /></s:param><s:param><s:text name="admin.capwap.serverNameRange" /></s:param></s:text>');
		backupCapwapIP.focus();
		return false;
	}
	
	var port = document.getElementById("capwapPort");
	var timeOut = document.getElementById("capwapTimeOut");
	var deadInterval = document.getElementById("deadInterval");
	
	if (port.value.length == 0) 
	{
        hm.util.reportFieldError(port, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.udpPort" /></s:param></s:text>');
        port.focus();
        return false;
    }
	
	if (timeOut.value.length == 0) 
	{
        hm.util.reportFieldError(timeOut, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.timeOut" /></s:param></s:text>');
        timeOut.focus();
        return false;
    }
    else if (!isValidTimeout(timeOut.value)) 
    {
		hm.util.reportFieldError(timeOut, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.timeOut" /></s:param><s:param><s:text name="admin.capwap.timeOutRange" /></s:param></s:text>');
		timeOut.focus();
		return false;
	}
	
	if (deadInterval.value.length == 0) 
	{
        hm.util.reportFieldError(deadInterval, '<s:text name="error.requiredField"><s:param><s:text name="admin.capwap.deadInterval" /></s:param></s:text>');
        deadInterval.focus();
        return false;
    }
    else if (!isValidDeadInterval(deadInterval.value,timeOut.value)) 
    {
		hm.util.reportFieldError(deadInterval, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.capwap.deadInterval" /></s:param><s:param><s:text name="admin.capwap.deadIntervalRange" /></s:param></s:text>');
		deadInterval.focus();
		return false;
	}
	
	return true;
}


function isValidTimeout(timeout)
{
	var intValue = timeout.valueOf();
	if ( intValue >=10 && intValue <= 120 )
	{
		return true;
	}
	
	return false;
}

function isValidDeadInterval(interval,timeout)
{
	var intValue = interval.valueOf();
	if ( intValue >=2*(timeout.valueOf()) && intValue <= 240 )
	{
		return true;
	}
	
	return false;
}

function isValidCapwapPort(port)
{
	var intValue = port.valueOf();
	if ( intValue >=1024 && intValue <= 65535 )
	{
		return true;
	}
	
	return false;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

function selectCapwapTransportMode(value)
{
	if (value == 1)
	{
		//udp
		document.getElementById("capwapPort").value = 12223;
	}
	else
	{
		//tcp
		document.getElementById("capwapPort").value = 80;
	}
}

</script>

<div id="content">
	<s:form action="portalSettings">
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
								<input type="button" name="ok" value="Update" class="button"
									onClick="submitAction('update');"
									<s:property value="writeDisabled" />>
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
				<td style="padding-top: 5px;">
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="800px">
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td class="labelT1" colspan="2" style="padding-left: 15px;">
								<s:checkbox id="capwapEnable" name="capwapEnable" />
								<label>
									<s:text name="admin.capwap.enableCapwap" />
								</label>
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="180" style="padding-left: 15px;">
								<label>
									<s:text name="admin.capwap.primaryCapwapIP" />
								</label>
							</td>
							<td>
								<s:textfield id="primaryCapwapIP" name="primaryCapwapIP"
									size="32" maxlength="32"
									onkeypress="return hm.util.keyPressPermit(event,'name');" />
								<s:text name="admin.capwap.serverNameRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="180" style="padding-left: 15px;">
								<label>
									<s:text name="admin.capwap.backupCapwapIP" />
								</label>
							</td>
							<td>
								<s:textfield id="backupCapwapIP" name="backupCapwapIP" size="32"
									maxlength="32"
									onkeypress="return hm.util.keyPressPermit(event,'name');" />
								<s:text name="admin.capwap.serverNameRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.capwap.timeOut" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="capwapTimeOut" name="capwapTimeOut"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="3" size="32" />
								<s:text name="admin.capwap.newTimeOutRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.capwap.deadInterval" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="deadInterval" name="deadInterval"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="3" size="32" />
								<s:text name="admin.capwap.deadIntervalRange" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.capwap.capwapTransportMode" />
								</label>
							</td>
							<td>
								<s:select name="capwapTransportMode" list="%{capwapTransportModes}" listKey="key"
								listValue="value" onchange="selectCapwapTransportMode(this.value)" cssStyle="width:106px;" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 15px;">
								<label>
									<s:text name="admin.capwap.capwapPort" />
								</label>
							</td>
							<td>
								<s:textfield id="capwapPort" name="capwapPort"
									maxlength="5" size="32" readonly='true' cssStyle="color:#808080"/>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
