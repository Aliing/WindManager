<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script src="<s:url value="/js/hm.paintbrush.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script>
var formName = 'osObject';
var thisOperation;
function submitAction(operation) {
	thisOperation = operation;
	if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
    	hm.util.checkAndConfirmClone();
    } else {
    	doContinueOper();
    }
}
function doContinueOper() {
	if(thisOperation != "export"){
		showProcessing();
	}
	document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}

//export OS type file
var exportOSFilePanel = null;
function createExportOSFilePanel(){
	var div = window.document.getElementById('exportOSFilePanel');
	exportOSFilePanel = new YAHOO.widget.Panel(div, {
		width:"400px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		constraintoviewport:true,
		modal:true
	} );
	exportOSFilePanel.render(document.body);
	div.style.display = "";
	overlayManager.register(exportOSFilePanel);
	exportOSFilePanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function hideExportOSFilePanel(){
	if(null != exportOSFilePanel){
		exportOSFilePanel.hide();
	}
}

function openExportOSFilePanel(){
	if(null != exportOSFilePanel){
		exportOSFilePanel.show();
	}else{
		createExportOSFilePanel();
		exportOSFilePanel.show();
	}
}

var waitingPanel = null;
var hiveApPagingLiveCount = 0;
var interval = 10;        // seconds
var osDetectionTimeoutId;
var duration = 60;  // minutes * 60
var total = duration / interval;
function syncOsobjectFile(){
	if(waitingPanel == null){
		createWaitingPanel();
	}
	var url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?operation=uploadOsDetection&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success:syncOsobjectFileResult, failure:syncOsobjectFileFailed, timeout:60000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function createWaitingPanel() {
	var div = document.getElementById("osDetectionFilePanel");
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel(div,
			{ width:"240px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	div.style.display="";
	waitingPanel.render(document.body);
}

function syncOsobjectFileResult(o){
	eval("var result = " + o.responseText);
	if(result.suc){
		startSyncOsDetection();
	}else{
		syncOsobjectFileFailed(o);
		showWarnDialog("Download OS Detection Service failed.");

	}
}

function startSyncOsDetection(){
	osDetectionTimeoutId = setTimeout("updateOsDetection()", interval * 1000);  // seconds
}

function updateOsDetection(){
	var url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?operation=updateOsDetection&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success:updateOsobjectFileResult, failure:syncOsobjectFileFailed, timeout:60000}, null);
}

function updateOsobjectFileResult(o){
	eval("var result = " + o.responseText);
	var osDetectionFilePanelTD = document.getElementById("osDetectionFilePanelTD");
	var osDetectionFilePanelTDlabel = document.getElementById("osDetectionFilePanelTDlabel");
	var osDetectionFilePanelTR = document.getElementById("osDetectionFilePanelTR");
	var osDetectionFilePanelTRText = document.getElementById("osDetectionFilePanelTRText");
	var osDetectionFilePanelTR2 = document.getElementById("osDetectionFilePanelTR2");
	var osDetectionFilePanelTR3 = document.getElementById("osDetectionFilePanelTR3");
	osDetectionFilePanelTD.style.display="";
	if (result.end || (hiveApPagingLiveCount++ > total)) {
		clearTimeout(osDetectionTimeoutId);
		osDetectionFilePanelTDlabel.innerHTML=result.succ+"/"+result.err;
		osDetectionFilePanelTR2.style.display="none";
		osDetectionFilePanelTR3.style.display="";
		if (result.err>0){
			osDetectionFilePanelTRText.value=result.errhost;
			osDetectionFilePanelTR.style.display="";
		}
	}else{
		osDetectionFilePanelTDlabel.innerHTML=result.succ+"/"+result.err;
		startSyncOsDetection();
	}
}

function syncOsobjectFileFailed(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	var osDetectionFilePanelTD = document.getElementById("osDetectionFilePanelTD");
	var osDetectionFilePanelTDlabel = document.getElementById("osDetectionFilePanelTDlabel");
	var osDetectionFilePanelTR = document.getElementById("osDetectionFilePanelTR");
	var osDetectionFilePanelTRText = document.getElementById("osDetectionFilePanelTRText");
	var osDetectionFilePanelTR2 = document.getElementById("osDetectionFilePanelTR2");
	var osDetectionFilePanelTR3 = document.getElementById("osDetectionFilePanelTR3");
	osDetectionFilePanelTD.style.display="none";
	osDetectionFilePanelTDlabel.innerHTML="";
	osDetectionFilePanelTR.style.display="none"
	osDetectionFilePanelTRText.value="";
	osDetectionFilePanelTR2.style.display="";
	osDetectionFilePanelTR3.style.display="none";
	hiveApPagingLiveCount = 0;

}

function radioModeTypeChanged(type){
	document.getElementById("exportFileType").value = type;
}
</script>

<div id="content"><s:form action="osObject">
<s:hidden id="exportFileType" name="exportFileType"/>
<div id="exportOSFilePanel" style="display:none">
	<div class="hd">
		<s:text name="config.textfile.title.osObject.export" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td>
								<div style="margin: 5px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" colspan="10">
												<s:radio name="fileType"
												list="#{'default':'Default OS Version File'}" value="%{exportFileType}"
												onchange="radioModeTypeChanged(this.value);" />
											</td>
										</tr>

										<tr>
											<td class="labelT1">
												<s:radio name="fileType"
												list="#{'current':'Current OS Version File'}" value="%{exportFileType}"
												onchange="radioModeTypeChanged(this.value);" />
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
				<td height="10px"></td>
			</tr>
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="<s:text name="config.usb.modem.selectFile.submit" />"
								class="button" onClick="submitAction('export');">
							</td>
							<td><input type="button" name="ignore" value="<s:text name="config.usb.modem.selectFile.cancel" />"
								class="button" onClick="hideExportOSFilePanel();">
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
<div id="osDetectionFilePanel" style="display:none">
	<div class="hd">
		<s:text name="config.usb.modem.button.sync.title"/>
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td id="osDetectionFilePanelTD" style="display:none">
								<s:text name="config.usb.modem.button.sync.succ"/><label id="osDetectionFilePanelTDlabel"></label>
							</td>
						</tr>
						<tr>
							<td id="osDetectionFilePanelTR" style="display:none">
								<s:text name="config.usb.modem.button.sync.failure"/>
								<s:textarea name="osDetectionFilePanelTRText" id="osDetectionFilePanelTRText"
								wrap="true" cssStyle="width:200px" rows="5" />
							</td>
						</tr>
						<tr id ="osDetectionFilePanelTR2">
							<td>
								<img src="<s:url value="/images/waiting.gif" includeParams="none" />" />
							</td>
						</tr>
						<tr  id ="osDetectionFilePanelTR3" style="display:none" >
							<td align="right"><input type="button" name="ignore" value="<s:text name="config.usb.modem.button.sync.ok" />"
								class="short" onClick="syncOsobjectFileFailed();">
							</td>
						</tr>

					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
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
					<td><input type="button" name="ignore" value="Clone"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="<s:text name="button.paintbrush"/>"
						id="brushTriggerBtn"
						class="button" onclick="hm.paintbrush.triggerPaintbrush('osObject')"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="remove" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="import" value="<s:text name="config.usb.modem.button.import"/>"
						class="button" onClick="submitAction('import');" style="display:<s:property value="%{displayInHomeDomain}"/>"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="export" value="<s:text name="config.usb.modem.button.export"/>"
						class="button" onClick="openExportOSFilePanel();" style="display:<s:property value="%{displayInHomeDomain}"/>"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="syncbtn" value="<s:text name="config.usb.modem.button.name"/>"
						class="button" onClick="syncOsobjectFile();" style="width:90px;display:<s:property value="%{displayInHomeDomain}"/>"
						<s:property value="writeDisabled" />></td>

				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th align="left" nowrap><ah:sort name="osName"
								key="config.osObject.name" /></th>
						</s:if>
						<s:elseif test="%{columnId == 2}">
							<th align="left" nowrap>
								<s:text name="config.osObject.version"/></th>
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
										href='<s:url value="osObject.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="osName" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="osObject.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="osName" /></a></td>
								</s:else>
							</s:if>
							<s:elseif test="%{columnId == 2}">
								<td class="list"><s:select cssStyle="width: 200px;"
									list="%{versionList}" /></td>
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
