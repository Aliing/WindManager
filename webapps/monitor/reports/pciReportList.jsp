<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'pciCompliance';
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
	waitingPanel.setHeader("Retrieving information...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

function submitAction(operation) {
    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
        hm.util.checkAndConfirmClone();
    } else if (operation == 'exportInList') {
    	var inputElements = document.getElementsByName('selectedIds');
		if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
			warnDialog.cfg.setProperty('text', "There is no item to export.");
			warnDialog.show();
		} else {
			var selectCount = 0;
			for (var i = 0; i < inputElements.length; i++) {
				if (inputElements[i].checked) {
					selectCount++;
				}
			}
			if (selectCount != 1) {
				warnDialog.cfg.setProperty('text', "Please select one item.");
				warnDialog.show();
			} else {
				//showProcessing();
				createTechData();
			}
		}
		return;
    } else {
        doContinueOper();
    }   
}
function doContinueOper() {
	if (thisOperation!="download") {
    	showProcessing();
	}
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function createTechData()
{
	document.forms[formName].operation.value = thisOperation;
	var formObject = document.getElementById('pciCompliance');
	YAHOO.util.Connect.setForm(formObject);
	var url = "<s:url action='pciCompliance' includeParams='none' />" + "?ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success: createDataResult, failure:abortResult,timeout: 3000000}, null);
	
	if(waitingPanel == null) {
		createWaitingPanel();
	}
	waitingPanel.show();
}
var createDataResult = function(o) {
	
	hm.util.hide('processing');
	if(waitingPanel != null) {
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	//alert(result);
	if(result.success) {
		showHangMessage("Download exported file.");
		hm.util.show('downloadSection');
	} else {
		if (result.msg) {
			warnDialog.cfg.setProperty('text', result.msg);
			warnDialog.show();
		} else {
			warnDialog.cfg.setProperty('text', "Create export file failed!");
			warnDialog.show();
			//showErrorMessage("Create export file failed!");
		}
	}
}
 
var abortResult = function(o)
{
	if(waitingPanel != null) {
		waitingPanel.hide();
	}
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

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>
<style type="text/css">
<!--
td.tableClassifyLabel{
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14px;
	font-style: normal;
	font-weight: bold;
	color: #fff;
	background-color: #333333;
	padding: 2px 4px;
	cursor: default;
}
-->
</style>

<div id="content"><s:form action="pciCompliance">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="New"
						class="button" onClick="submitAction('new');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Clone"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Export Now"
						class="button" onClick="submitAction('exportInList');"
						<s:property value="writeDisabled" />></td>
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
			<table id ="hiveTable" cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th><ah:sort name="name" key="report.reportList.name" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">
							<th><ah:sort name="excuteType" key="report.reportList.excuteType" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">
							<th><ah:sort name="emailAddress" key="report.reportList.emailAddress" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">	
							<th><s:text name="report.reportList.startTime" /></th>
						</s:if>
					</s:iterator>
					<s:if test="%{showDomain}">
         				<th><ah:sort name="owner.domainName" key="config.domain" /></th>
   					</s:if>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:if test="%{hasPredefinedReport}">
					<tr>
						<td class="tableClassifyLabel" colspan="100">
							<s:text name="hm.recurreport.list.report.predefined" />
						</td>
					</tr>
				</s:if>
				<s:iterator value="page" status="status" id="pageRow">
					<tiles:insertDefinition name="rowClass" />
					<s:if test="%{defaultFlag}">
						<s:set name="rowStatus" value="%{'defaultFlag'}" />
					</s:if>
					<s:else>
						<s:if test="%{hasPredefinedReport && #rowStatus != ''}">
							<tr>
								<td class="tableClassifyLabel" colspan="100">
									<s:text name="hm.recurreport.list.report.userdefined" />
								</td>
							</tr>
						</s:if>
						<s:set name="rowStatus" value="%{''}" />
					</s:else>
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
										href='<s:url value="pciCompliance.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="name" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="pciCompliance.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="name" /></a></td>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 2}">
								<td class="list"><s:property value="excuteTypeString" /></td>
							</s:if>
							<s:if test="%{columnId == 3}">
								<td class="list">&nbsp;<s:property value="emailAddress" /></td>
							</s:if>
							<s:if test="%{columnId == 4}">
								<td class="list">&nbsp;<s:property value="nextScheduleTimeString" /></td>
   							</s:if>
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
