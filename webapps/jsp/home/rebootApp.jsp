<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'rebootApp';

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
	waitingPanel.setHeader("Initializing...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

function onLoadPage() 
{
	var submitBtn = document.getElementById('submitBtn');
	<s:if test="%{isInHomeDomain}">
		submitBtn.value = 'Reboot';
	</s:if>
	<s:else>
		submitBtn.value = 'Revert';
	</s:else>
	
	createWaitingPanel();
}

function submitAction() 
{
	var operation;
	<s:if test="%{isInHomeDomain}">
		operation = 'reboot';
	</s:if>
	<s:else>
		operation = 'revert';
	</s:else>
	
	if (validate(operation)) 
	{
		document.forms[formName].operation.value = operation;
		
		confirmDialog.cfg.setProperty('text', "Are you sure you want to perform the selected operation?");
  		confirmDialog.show();
	}
}

function doContinueOper() 
{
    document.forms[formName].submit();
    showProcessing();
}

function validate(operation) 
{
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="rebootApp">
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
								<input type="button" id="submitBtn" name="ignore" value="Reboot"
									class="button" onClick="submitAction();"
									<s:property value="rebootButtonDisabled" />>
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
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="600">
						<s:if test="%{isInHomeDomain}">
							<tr>
								<td style="padding:10px 10px 10px 10px">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<th align="center">
												<s:text name="admin.rebootApp.defaultBoot" />
											</th>
											<th align="center">
												<s:text name="admin.rebootApp.version" />
											</th>
											<th align="center">
												<s:text name="admin.rebootApp.status" />
											</th>
										</tr>
										<s:iterator id="bootInfoTable" value="%{bootInfoList}"
											status="status">
											<tr class="list">
												<td class="list" align="center" >
													<s:if test="canShow == true ">
													<s:radio label="Gender" id="rebootSoft" name="rebootSoft" 
														list="#{label:''}" value="%{rebootSoft}"  />
													</s:if>
													<s:elseif test="status == \"Active\"">
														<s:radio label="Gender" id="rebootSoft" name="rebootSoft" 
														list="#{label:''}" value="%{rebootSoft}"  />
													</s:elseif>
													<s:else>
														<s:radio label="Gender" id="rebootSoft" name="rebootSoft" 
														list="#{label:''}" value="%{rebootSoft}" disabled="true"/>
													</s:else>
												</td>
												<td class="list" align="center">
													<s:property value="%{version}" />
												</td>
												<td class="list" align="center">
													<s:property value="%{status}" />
												</td>
											</tr>
										</s:iterator>
									</table>
								</td>
							</tr>
						</s:if>
						<s:else>
							<tr>
								<td style="padding:10px 10px 10px 10px">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<th align="center">
												<s:text name="admin.rebootApp.revertSelect" />
											</th>
											<th align="center">
												<s:text name="admin.rebootApp.version" />
											</th>
											<th align="center">
												<s:text name="admin.rebootApp.status" />
											</th>
										</tr>
										<s:iterator id="revertTable" value="%{revertList}"
											status="status">
											<tr class="list">
												<td class="list" align="center" id="defboot_div">
													<s:radio label="Gender" id="revertTarget"
														name="revertTarget" list="#{ipAddress:''}"
														value="%{revertTarget}" />
												</td>
												<td class="list" align="center">
													<s:property value="%{hmVersion}" />
												</td>
												<td class="list" align="center">
													<s:property value="%{statusShow}" />
												</td>
											</tr>
										</s:iterator>
									</table>
								</td>
							</tr>
						</s:else>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>

