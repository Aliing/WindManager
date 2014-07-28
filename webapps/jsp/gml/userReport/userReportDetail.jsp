<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<tiles:insertDefinition name="tabView" />
<tiles:insertDefinition name="flashHeader" />

<script>
var formName = 'userReports';
var allTabs;
var generalTab;
var reportTab;

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_name").disabled == false) {
		document.getElementById(formName + "_dataSource_name").focus();
	}
/**	if (!document.getElementById("enabledEmail").checked) {
		document.getElementById("emailAddress").value="";
		document.getElementById("emailAddress").readOnly=true;
	}**/

	allTabs = new YAHOO.widget.TabView("reportTabs", {activeIndex:0});
	generalTab = allTabs.getTab(0);
	reportTab = allTabs.getTab(1);

	var showReportTab = <s:property value="%{showReportTab}"/>;
	if (showReportTab) {
		allTabs.set('activeIndex',1);
		document.getElementById("export").disabled = false;
	} else {
		allTabs.removeTab(reportTab);
		document.getElementById("export").disabled = true;
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'createDownloadData')
		{
			showProcessing();
			createTechData();
			return;
		} else if (operation == 'download') {

		} else {
			showProcessing();
		}
		//add handler to deal with something before form submit.
		beforeSubmitAction(document.forms[formName]);
		
		document.forms[formName].tabIndex.value = allTabs.get('activeIndex');
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	if (operation == 'cancel') {
		return true;
	}

	var inputElement = document.getElementById(formName + "_dataSource_name");
	var message = hm.util.validateName(inputElement.value, '<s:text name="report.reportList.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}

	/**if (document.getElementById("enabledEmail").checked) {
		if (document.getElementById("emailAddress").value.trim()=="") {
		  hm.util.reportFieldError(document.getElementById("emailAddress"), '<s:text name="error.requiredField"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
          return false;
		}

		var emails = document.getElementById("emailAddress").value.split(";");
		for (var i=0;i<emails.length;i++) {
			if (i==emails.length-1 && emails[i].trim()=="") {
				break;
			}
			if (!hm.util.validateEmail(emails[i].trim())) {
				hm.util.reportFieldError(document.getElementById("emailAddress"), '<s:text name="error.formatInvalid"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
				document.getElementById("emailAddress").focus();
				return false;
			}
		}
	}**/
	return true;
}

function clickClientIp(value) {
	if (value) {
		Get("hideClientIpAddressDiv").style.display="block";
		Get(formName + "_dataSource_authIp").value="";
	} else {
		Get("hideClientIpAddressDiv").style.display="none";
		Get(formName + "_dataSource_authIp").value="";
	}
}
function clickClientHostName(value) {
	if (value) {
		Get("hideClientHostNameDiv").style.display="block";
		Get(formName + "_dataSource_authHostName").value="";
	} else {
		Get("hideClientHostNameDiv").style.display="none";
		Get(formName + "_dataSource_authHostName").value="";
	}
}
function clickClientUserName(value) {
	if (value) {
		Get("hideClientUserNameDiv").style.display="block";
		Get(formName + "_dataSource_authUserName").value="";
	} else {
		Get("hideClientUserNameDiv").style.display="none";
		Get(formName + "_dataSource_authUserName").value="";
	}
}


/**function enabledEmailAddress() {
	if (!document.getElementById("enabledEmail").checked) {
		document.getElementById("emailAddress").value="";
		document.getElementById("emailAddress").readOnly=true;
	} else {
		document.getElementById("emailAddress").readOnly=false;
	}
}**/

function createTechData()
{
	var url = "<s:url action='userReports' includeParams='none' />" + "?operation=createDownloadData&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: createDataResult, failure:abortResult,timeout: 180000}, null);

}
var createDataResult = function(o)
{
	hm.util.hide('processing');
	eval("var result = " + o.responseText);
	if(result.success)
	{
		submitAction('download');
	}
	else
	{
		if(warnDialog != null)
		{
			warnDialog.cfg.setProperty('text', "Create cvs data file failed!");
			warnDialog.show();
		}
	}
}

var abortResult = function(o)
{
	hm.util.hide('processing');
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="userReports" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedReportName" />\'</td>');
		</s:else>
	</s:else>
}
</script>
<div id="content"><s:form action="userReports">
	<s:hidden name="tabIndex" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore"
							value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore"
							value="<s:text name="button.update"/>" class="button"
							onClick="submitAction('update');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Run Now"
						class="button" onClick="submitAction('run');"
						<s:property value="updateDisabled" />></td>
					<td><input type="button" name="ignore" value="Export" id="export"
						class="button" onClick="submitAction('createDownloadData');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">

			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td>

					<div id="reportTabs" class="yui-navset">
					<ul class="yui-nav">
						<li class="selected"><a href="#tab1"><em><s:text
							name="report.reportList.tab.general" /></em></a></li>
						<li><a href="#tab2"><em><s:text
							name="report.reportList.tab.report" /></em></a></li>
					</ul>
					<div class="yui-content"><!-- begin general  -->
					<div id="tab1">

					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="4"></td>
								</tr>
								<tr>
									<td class="labelT1" width="152px"><s:text
										name="report.reportList.name" /><font color="red"><s:text
										name="*" /></font></td>
									<td><s:textfield name="dataSource.name"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="%{nameLength}" disabled="%{disabledName}" />&nbsp;<s:text
										name="report.reportList.name.range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="152px"><s:text
										name="gml.permanent.description" /></td>
									<td nowrap="nowrap"><s:textfield name="dataSource.description"
										size="48" maxlength="64"/>&nbsp;<s:text
										name="config.ssid.description_range" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="152px"><s:text
										name="report.reportList.reportPeriod" /></td>
									<td><s:select name="dataSource.reportPeriod"
										list="%{enumReportPeriod}" listKey="key" listValue="value"
										value="dataSource.reportPeriod" cssStyle="width: 150px;" /></td>
								</tr>
								<tr>
									<td class="labelT1" width="152px"><s:text
										name="report.reportList.apName" /></td>
									<td><s:textfield name="dataSource.apName"
										size="24"
										onkeypress="return hm.util.keyPressPermit(event,'name');"
										maxlength="32" /></td>
								</tr>
								<tr>
									<td colspan="2">
										<div style="display:<s:property value="%{showClientCondition}"/>" id="hideClientMacConditionDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="152px"><s:text
														name="report.reportList.clientAuth.macAddress" /></td>
													<td width="200px"><s:textfield name="dataSource.authMac" size="24"
														onkeypress="return hm.util.keyPressPermit(event,'hex');"
														maxlength="12" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<div style="display:<s:property value="%{showClientCondition}"/>" id="hideClientOtherConditionDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="152px"><s:text
														name="report.customReport.otherClientInfo" /></td>
													<td><s:checkbox name="checkIp" onclick="clickClientIp(this.checked)"></s:checkbox><s:text
														name="report.customReport.checkIpAddress" /></td>
													<td><s:checkbox name="checkHostName" onclick="clickClientHostName(this.checked)"></s:checkbox><s:text
														name="report.customReport.checkHostName" /></td>
													<td><s:checkbox name="checkUserName" onclick="clickClientUserName(this.checked)"></s:checkbox><s:text
														name="report.customReport.checkUserName" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>

								<tr>
									<td colspan="2">
										<div style="display:<s:property value="%{showClientIpAddress}"/>" id="hideClientIpAddressDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="152px"><s:text
														name="report.reportList.title.currentClientIpAddress" /></td>
													<td><s:textfield name="dataSource.authIp"
														size="24"
														onkeypress="return hm.util.keyPressPermit(event,'ip');"
														maxlength="15" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<div style="display:<s:property value="%{showClientHostName}"/>" id="hideClientHostNameDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="152px"><s:text
														name="report.reportList.title.currentClientHostName" /></td>
													<td><s:textfield name="dataSource.authHostName"
														size="24" maxlength="32" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<div style="display:<s:property value="%{showClientUserName}"/>" id="hideClientUserNameDiv">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="152px"><s:text
														name="report.reportList.title.currentClientUserName" /></td>
													<td><s:textfield name="dataSource.authUserName"
														size="24" maxlength="32" /></td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								
							</table>
							</td>
						</tr>
						<tr>
							<td height="8px"></td>
						</tr>
					</table>
					</div>

					<!-- end general  --> <!-- begin report  -->
					<div id="tab2">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<s:if test="%{dataSource.reportType=='usersPerDay'}">
							<s:if test="%{reportResult==null || reportResult.size<2}">
								<tr>
									<td>
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<th align="left"> <s:text name="report.reportList.title.time" /> </th>
												<th align="left"> <s:text name="report.reportList.gml.clientCount" /> </th>
											</tr>
											<s:if test="%{reportResult==null || reportResult.size==0}">
												<ah:emptyList />
											</s:if>
											<s:else>
												<s:iterator value="reportResult" status="status">
													<tiles:insertDefinition name="rowClass" />
													<tr class="<s:property value="%{#rowClass}"/>">
														<td class="list" ><s:property value="%{value}" /></td>
														<td class="list" ><s:property value="%{id}" /></td>
													</tr>
												</s:iterator>
											</s:else>
										</table>
									</td>
								</tr>
							</s:if>
							<s:else>
								<tr>
									<td>
										<tiles:insertDefinition name="flash" />
									</td>
								</tr>
							</s:else>
						</s:if>
						<s:elseif test="%{dataSource.reportType=='sessPerDay'}">
							<s:if test="%{reportResult==null || reportResult.size<2}">
								<tr>
									<td>
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<th align="left"> <s:text name="report.reportList.title.time" /> </th>
												<th align="left"> <s:text name="report.reportList.gml.sessionCount" /> </th>
											</tr>
											<s:if test="%{reportResult==null || reportResult.size==0}">
												<ah:emptyList />
											</s:if>
											<s:else>
												<s:iterator value="reportResult" status="status">
													<tiles:insertDefinition name="rowClass" />
													<tr class="<s:property value="%{#rowClass}"/>">
														<td class="list" ><s:property value="%{value}" /></td>
														<td class="list" ><s:property value="%{id}" /></td>
													</tr>
												</s:iterator>
											</s:else>
										</table>
									</td>
								</tr>
							</s:if>
							<s:else>
								<tr>
									<td>
										<tiles:insertDefinition name="flash" />
									</td>
								</tr>
							</s:else>
						</s:elseif>
						<s:elseif test="%{dataSource.reportType=='avgStPerDay'}">
							<tr>
								<td>
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<th align="left"><s:text name="report.reportList.title.time" /> </th>
											<th align="left"> <s:text name="report.reportList.gml.avgSessionTime" /> </th>
										</tr>
										<s:if test="%{reportResult==null || reportResult.size==0}">
											<ah:emptyList />
										</s:if>
										<s:else>
											<s:iterator value="reportResult" status="status">
												<tiles:insertDefinition name="rowClass" />
												<tr class="<s:property value="%{#rowClass}"/>">
													<td class="list" ><s:property value="%{value}" /></td>
													<td class="list" ><s:property value="%{longToTime}" /></td>
												</tr>
											</s:iterator>
										</s:else>
									</table>
								</td>
							</tr>
						</s:elseif>
						<s:elseif test="%{dataSource.reportType=='sessPerNas'}">
							<tr>
								<td>
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<th align="left"><s:text name="report.reportList.apName" /> </th>
											<th align="left"> <s:text name="report.reportList.gml.sessionCount" /> </th>
										</tr>
										<s:if test="%{reportResult==null || reportResult.size==0}">
											<ah:emptyList />
										</s:if>
										<s:else>
											<s:iterator value="reportResult" status="status">
												<tiles:insertDefinition name="rowClass" />
												<tr class="<s:property value="%{#rowClass}"/>">
													<td class="list" ><s:property value="%{value}" /></td>
													<td class="list" ><s:property value="%{id}" /></td>
												</tr>
											</s:iterator>
										</s:else>
									</table>
								</td>
							</tr>
						</s:elseif>
					</table>
					</div>
					</div>
					</div>
					</td>
				</tr>
			</table>
			</td>
		</tr>

	</table>
</s:form></div>
