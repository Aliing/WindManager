<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'localUser';
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
	waitingPanel.setHeader("Request is being processed...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

function submitAction(operation) {
	thisOperation = operation;
	if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
    	hm.util.checkAndConfirmClone();
    } else if (operation == 'email') {
    	var inputElements = document.getElementsByName('selectedIds');
		if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
			warnDialog.cfg.setProperty('text', "There is no item to email.");
			warnDialog.show();
		} else if (!hm.util.hasCheckedBoxes(inputElements)) {
			warnDialog.cfg.setProperty('text', "Please select at least one item.");
			warnDialog.show();
		} else {
			showProcessing();
        	doContinueOper();
		}
    } else if (operation == 'createDownloadData') {
    	var inputElements = document.getElementsByName('selectedIds');
		if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
			warnDialog.cfg.setProperty('text', "There is no item to export.");
			warnDialog.show();
		} else if (!hm.util.hasCheckedBoxes(inputElements)) {
			warnDialog.cfg.setProperty('text', "Please select at least one item.");
			warnDialog.show();
		} else {
			//showProcessing();
			//createTechData();
			openDownloadConfirmOverlay();
		}
		return;
    } else if(operation == 'macAuth'){
        var redirect_url = "<s:url action='macAuth' includeParams='none' />" + "?operation=macAuthList";
        window.location.href = redirect_url;
    } else {
    	//if (operation != 'download'){
    	//	showProcessing();
    	//}
        doContinueOper();
    }	
}
function doContinueOper() {
	if (thisOperation=='download' && Get("radioForDownload2").checked) {
		thisOperation = 'download2'
	}
	document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function createTechData()
{
	hideDownloadConfirmOverlay();
	if (Get("radioForDownload1").checked) {
		document.forms[formName].operation.value = "createDownloadData";
	} else {
		document.forms[formName].operation.value = "createDownloadDataImport";
	}
	
	var formObject = document.getElementById('localUser');
	YAHOO.util.Connect.setForm(formObject);
	var url = "<s:url action='localUser' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success: createDataResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}
var createDataResult = function(o) 
{	
	hm.util.hide('processing');
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	if(result.success)
	{
		showHangMessage("Download exported file.");
		hm.util.show('downloadSection');
	}
	else
	{
		showErrorMessage("Create export file failed!");
	}
}

var abortResult = function(o) 
{
	hm.util.hide('processing');
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}

var filterFormName = 'localUserFilter';
YAHOO.util.Event.onDOMReady(init);
var filterOverlay = null;
var downloadConfirmOverlay=null;
function init() {
// create filter overlay
	var div = document.getElementById('filterPanel');
	filterOverlay = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	filterOverlay.render(document.body);
	div.style.display = "";
	
	createWaitingPanel();
	
	createDownloadConfirmPanel();
}

function createDownloadConfirmPanel() {
	// create  Download Confirm overlay
	var div = document.getElementById('downloadConfirmPanel');
	downloadConfirmOverlay = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	downloadConfirmOverlay.render(document.body);
	div.style.display = "";
}

function openDownloadConfirmOverlay(){
	if(null != downloadConfirmOverlay){
		Get("radioForDownload1").checked=true;
		downloadConfirmOverlay.cfg.setProperty('visible', true);
	}
}

function hideDownloadConfirmOverlay(){
	if(null != downloadConfirmOverlay){
		downloadConfirmOverlay.cfg.setProperty('visible', false);
	}
}

function openFilterOverlay(){
	initialValues();
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', true);
	}
}

function hideOverlay(){
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', false);
	}
}
function initialValues(){
	document.getElementById(filterFormName+"_filterUserName").value = '';
	document.getElementById(filterFormName+"_filterDescription").value = '';
	document.getElementById(filterFormName+"_filterEmail").value = '';
}
function submitFilterAction(operation) {
	document.forms[filterFormName].operation.value = operation;
   	document.forms[filterFormName].submit();
}

function showHangMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	hm.util.show("noteSection");
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

</script>

<div id="content"><s:form action="localUser">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="new" value="New" class="button"
						onClick="submitAction('new');"
						<s:property value="writeDisabled" />></td>
					<s:if test="%{lastExConfigGuide==null}">
					<td><input type="button" name="Bulk" value="Bulk" class="button"
						onClick="submitAction('newCreateBulk');"
						<s:property value="writeDisabled" />></td>
					</s:if>
					<td><input type="button" name="ignore" value="Clone"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="remove" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="email" value="Email PSK"
						class="button" onClick="submitAction('email');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="export" value="Export PSK"
						class="button" onClick="submitAction('createDownloadData');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="import" value="Import"
						class="button" onClick="submitAction('import');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="filter" value="Filter"
						class="button" onClick="openFilterOverlay();"
						<s:property value="writeDisabled" />></td>
					<s:if test="%{!BlnEasyMode}">
	                    <td><input type="button" name="macAuth" value="MAC Auth"
	                        class="button" onClick="submitAction('macAuth');"
	                        <s:property value="writeDisabled" />></td>	
                    </s:if>					
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
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
			<table cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th align="left" nowrap><ah:sort name="userName"
								key="config.localUser.userName" /></th>
						</s:if>
						<s:elseif test="%{columnId == 2}">
							<th align="left" nowrap><ah:sort name="userType"
								key="config.localUserGroup.userType" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 3}">
							<s:if test="%{showOrHidePskString == false}">
								<th align="left" nowrap><s:text name="config.localUser.PskTitle" />&nbsp;
								<a href='<s:url value="localUser.action">
								<s:param name="operation" value="%{'showHidePPSK'}"/>
								<s:param name="blnShowOrHidePsk" value="%{'true'}"/></s:url>'>
								<font color="blue"><s:text name="config.localUser.clearPsk" /></font></a>
								</th>
							</s:if>
							<s:else>
								<th align="left" nowrap><s:text name="config.localUser.PskTitle" />&nbsp;
								<a href='<s:url value="localUser.action">
								<s:param name="operation" value="%{'showHidePPSK'}"/>
								<s:param name="blnShowOrHidePsk" value="%{'false'}"/></s:url>'>
								<font color="blue"><s:text name="config.localUser.obscuredPsk" /></font></a>
								</th>
							</s:else>
						</s:elseif>
						<s:elseif test="%{columnId == 4}">
							<th align="left" nowrap><ah:sort name="localUserGroup"
								key="" /><s:property value="userGroupTitleForList" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 5}">
							<th align="left" nowrap><s:text name="report.client.startTime" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 6}">
							<th align="left" nowrap><s:text name="report.client.endTime" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 7}">
							<th align="left" nowrap><ah:sort name="mailAddress"
								key="config.localUser.emailTitle" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 8}">
							<th align="left" nowrap><ah:sort name="description"
								key="config.localUserGroup.description" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 9}">
							<th align="left" nowrap><s:text name="config.localUser.passwordDigest" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 10}">
							<th align="left" nowrap><s:text name="config.localUser.pskDigest" /></th>
						</s:elseif>
					</s:iterator>
					<s:if test="%{showDomain}">
						<th><ah:sort name="owner.domainName" key="config.domain" /></th>
   					</s:if>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status" id="pageRow">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
						<s:if test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
							<td class="listCheck"><input type="checkbox" disabled /></td>
   						</s:if>
   						<s:else>
							<td class="listCheck"><ah:checkItem /></td>
   						</s:else>
   						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
		   						<s:if test="%{showDomain}">
									<td class="list"><a
										href='<s:url value="localUser.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="userName" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="localUser.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="userName" /></a></td>
								</s:else>
							</s:if>
							<s:elseif test="%{columnId == 2}">
								<td class="list"><s:property value="strUserType" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 3}">
								<td class="list"><s:property value="strPskString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 4}">
								<td class="list"><s:property value="userGroupName" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 5}">
								<td class="list"><s:property value="startTimeString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 6}">
								<td class="list"><s:property value="expiredTimeString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 7}">
								<td class="list"><s:property value="mailAddress" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 8}">
								<td class="list"><s:property value="description" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 9}">
								<td class="list"><s:property value="strPasswordDigest" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 10}">
								<td class="list"><s:property value="strPSKDigest" />&nbsp;</td>
							</s:elseif>
						</s:iterator>
						<s:if test="%{showDomain}">
							<td class="list"><s:property value="%{owner.domainName}" /></td>
   						</s:if>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
<div id="filterPanel" style="display: none;">
	<div class="hd">
		Filter Local User By
	</div>
	<div class="bd">
		<s:form action="localUser" id="localUserFilter"
			name="localUserFilter">
			<s:hidden name="operation" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td style="padding: 6px 5px 5px 5px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1">
												<s:text name="config.localUser.userName" />
											</td>
											<td>
												<s:textfield name="filterUserName" maxlength="32"
													size="30" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:text name="config.localUser.emailTitle" />
											</td>
											<td>
												<s:textfield name="filterEmail" maxlength="128"
													size="30" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:text name="config.localUserGroup.description" />
											</td>
											<td>
												<s:textfield name="filterDescription" maxlength="64"
													size="30" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td style="padding-top: 8px;" colspan="2">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<input type="button" name="ignore" value="Search" id="search"
										class="button" onClick="submitFilterAction('search');">
								</td>
								<td>
									<input type="button" name="ignore" value="Cancel"
										class="button" onClick="hideOverlay();">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>
</div>

<div id="downloadConfirmPanel" style="display: none;">
	<div class="hd">
		Export
	</div>
	<div class="bd">

	<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td style="padding: 6px 5px 5px 5px;">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td class="labelT1" colspan="2">
											<s:text name="geneva_08.localuser.export.title"/>
										</td>
									</tr>
									<tr>
										<td class="labelT1">
											<s:radio label="Gender" name="radioForDownload"
												list="#{'2':''}" />
										</td>
										<td>
											<s:text name="geneva_08.localuser.export.import"/>
										</td>
									</tr>
									<tr>
										<td class="labelT1">
											<s:radio label="Gender" name="radioForDownload"
												list="#{'1':''}" value="1" />
										</td>
										<td>
											 <s:text name="geneva_08.localuser.export.report"/>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="5px"></td>
			</tr>
			<tr>
				<td style="padding-top: 8px;" colspan="2">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<input type="button" name="ignore" value="OK"
									class="button" onClick="createTechData();">
							</td>
							<td>
								<input type="button" name="ignore" value="Cancel"
									class="button" onClick="hideDownloadConfirmOverlay();">
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
