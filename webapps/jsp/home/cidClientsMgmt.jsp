<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'auxiliaryFileMgmt';
var uploadPanel = null;

function onLoadPage() {
	$("#editTable").hide();
	//Auto refresh
	//Create file upload Overlay
	var div = document.getElementById('uploadPanel');
	uploadPanel = new YAHOO.widget.Panel(div, { width:"540px",visible:false,draggable:true,constraintoviewport:true,modal:true } );
	var code = document.getElementById('title');
	uploadPanel.cfg.setProperty('x', YAHOO.util.Dom.getX(code) + 100);
	uploadPanel.cfg.setProperty('y', YAHOO.util.Dom.getY(code) + 25);
	uploadPanel.render();
	div.style.display = "";
}

function submitAction(operation) {
	if (operation == 'cidClientsUpload') {
		var file = document.getElementById("localFile");
		if (file.value.length == 0) {
			hm.util.reportFieldError(file, '<s:text name="error.requiredField"><s:param><s:text name="admin.auxiliary.file.new.dictionary" /></s:param></s:text>');
            file.focus();
		} else {
			document.forms[formName].operation.value = operation;
		    try {
			    document.forms[formName].submit();
			    showProcessing();
			} catch (e) {
				if (e instanceof Error && e.name == "TypeError") {
					hm.util.reportFieldError(file, '<s:text name="error.fileNotExist"></s:text>');
					file.focus();
				}
			}
			changeUploadPanel(false);
		}
	} else {
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

function changeUploadPanel(checked) {
	if (!checked) {
		document.getElementById("localFile").value = "";
	}
	if (uploadPanel != null) {
		uploadPanel.cfg.setProperty('visible', checked);
	}
}

function onUnloadPage() {
	  clearTimeout(clientPagingTimeoutId);
}
var doCustomAutoRefreshSettingSubmit = function(postfix) {
		var baseUrl = "<s:url action="auxiliaryFileMgmt" includeParams="none" />?ignore=" + new Date().getTime();
		var url = baseUrl + "&" + postfix;
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateAutoRefreshSetting }, null);
}
var updateAutoRefreshSetting = function(o) {
		eval("var result = " + o.responseText);
		if (hm.util.isAFunction(updateAutoRefreshStatus)) {
			updateAutoRefreshStatus(result.autoOn);
		}
		if (result.autoOn == false) {
			clearTimeout(clientPagingTimeoutId);
		} else {
			if (result.refreshOnce) {
				submitAction('refreshFromCache');
			} else {
				startClientPagingTimer();
			}
		}
}

var clientPagingLiveCount = 0;
var clientPagingTimeoutId;
var duration = <s:property value="%{sessionTimeOut}" /> * 60;  // minutes * 60
var interval = <s:property value="%{pageRefInterval}"/>; // seconds
var total = duration / interval;

function startClientPagingTimer() {
		if (clientPagingLiveCount++ < total) {
			clientPagingTimeoutId = setTimeout("pollCidClientsPaging()", interval * 1000);  // seconds
		}
}
function pollCidClientsPaging() {
		var url = "<s:url action="auxiliaryFileMgmt" includeParams="none" />?operation=pollCidClientsList&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateClient }, null);
}
//cached value for refresh which is updated when overlay is showing
var cachedRefresh = false;

function updateClient(o) {
		eval("var updates = " + o.responseText);

		for (var i = 0; i < updates.length; i++) {
			if (updates[i].id < 0) {
				submitAction('refreshFromCache');
				return;
			}
		}
		startClientPagingTimer();
}

</script>

<div id="content">
	<s:form action="auxiliaryFileMgmt" enctype="multipart/form-data"
		method="POST">
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
							<td id="title">
								<input type="button" name="download" value="Download" class="button"
									onClick="submitAction('cidClientsDownload');">
							</td>
							<td>
								<input type="button" name="upload" value="Upload" class="button"
									onClick="changeUploadPanel(true);" >
							</td>
							<td>
								<input type="button" name="refresh" value="Refresh" class="button"
									onClick="submitAction('cidClients');" >
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
				<td>
					<table border="0" cellspacing="0" cellpadding="0" width="240" >
						<tr>
							<td>
								<table width="100%" cellspacing="0" cellpadding="0" border="0"
									class="view">
									<thead>
									<tr>
										
									 <s:iterator value="%{selectedColumns}">
											<s:if test="%{columnId == 1}">
												<th>
													<ah:sort name="macAddress" key="home.administration.auxiliaryFiles.cidClients.macAddress"/>
												</th>
											</s:if>
											<!-- <s:if test="%{columnId == 2}">
												<th>
													<ah:sort name="imei" key="home.administration.auxiliaryFiles.cidClients.imei"/>
												</th>
											</s:if> -->
										</s:iterator> 
									</tr>
									</thead>
									<tbody>
									<tr>
										<td>
											<div id="warnMessage"></div>
										</td>
									</tr>
									<s:if test="page.size==0">
										<ah:emptyList/>
									</s:if>
									<tiles:insertDefinition name="selectAll" />
 								    <s:iterator value="page" status="num">
										 <tiles:insertDefinition name="rowClass" /> 
										<tr class="<s:property value='#rowClass'/>"> 
											
											 <s:iterator value="%{selectedColumns}">
												<s:if test="%{columnId == 1}">
													<td nowrap="nowrap" class="list">
														<s:property value="macAddress" />
														&nbsp;
													</td>
												</s:if>
												<!-- <s:if test="%{columnId == 2}">
													<td class="list" nowrap="nowrap">
													<s:property value="imei" />
														&nbsp;
													</td>
												</s:if> -->
											</s:iterator> 
										</tr>  
									</s:iterator> 
									</tbody>
								</table>
							</td>
						</tr>
						<tr>
							<td height="4"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<div id="uploadPanel" style="display: none;">
		<div class="hd">Upload New CID Clients File</div>
		<div class="bd">
			<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="520px">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td colspan="2" class="noteInfo" style="padding:0 10px 0 10px;"><s:text name="admin.auxiliary.file.new.cidClients.note" /></td>
				</tr>
				<tr>
					<td colspan="2" class="noteInfo" style="padding:0 10px 0 10px;"></td>
				</tr>
				<tr>
					<td colspan="2" class="noteInfo" style="padding:0 10px 0 10px;"><s:text name="admin.auxiliary.file.new.cidClients.note.example" /></td>
				</tr>
				<tr>
					<td colspan="2" class="noteInfo" style="padding:0 10px 0 10px;"><s:text name="admin.auxiliary.file.new.cidClients.note.c1" /></td>
				</tr>
				<tr>
					<td colspan="2" class="noteInfo" style="padding:0 10px 0 10px;"><s:text name="admin.auxiliary.file.new.cidClients.note.c2" /></td>
				</tr>
				<tr>
					<td colspan="2" class="noteInfo" style="padding:0 10px 0 10px;"><s:text name="admin.auxiliary.file.new.cidClients.note.c3" /></td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td class="labelT1" width="120px"><s:text name="admin.auxiliary.file.new.cidClients" /></td>
					<td><s:file id="localFile" name="upload" size="50" value="%{upload}" />
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td colspan="2" align="center">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="Upload"
								class="button" onClick="submitAction('cidClientsUpload');"></td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="changeUploadPanel(false);"></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
			</table>
		</div>
	    </div>
		</s:form>
	</div>
	    
	
