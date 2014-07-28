<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'usbModem';
var thisOperation;
function submitAction(operation) {
    thisOperation = operation;
    if (operation == 'import') {
    	if (!validateImportFile()) {
    		return;
    	} 
    } else if (operation == 'export') {
    	document.forms[formName].operation.value = thisOperation;
        document.forms[formName].submit();
        return;
    } 
    doContinueOper();   
}

function validateImportFile() {
	scanEl = document.getElementById("usbConfigFile");
	if (scanEl == null || scanEl.value == null || scanEl.value.trim() == "") {
		hm.util.reportFieldError(document.getElementById("selectFileError4UsbModem"), "Please select a XML file first.");
		return false;
	}
	return true;
}

function doContinueOper() {
    showProcessing();
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}

function toggleSelectConfigFile() {
	if (document.getElementById("showSelectFile") != null) {
		if (document.getElementById("showSelectFile").style.display == "none") {
			document.getElementById("showSelectFile").style.display = "";
		} else {
			document.getElementById("showSelectFile").style.display = "none";
		} 
	}
}

function hideSelectConfigFile() {
	if (document.getElementById("showSelectFile") != null) {
		document.getElementById("showSelectFile").style.display = "none";
	}
}
</script>

<div id="content"><s:form action="usbModem" 
		enctype="multipart/form-data" method="POST">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="import" value='<s:text name="config.usb.modem.button.import"/>' class="button"
						onClick="toggleSelectConfigFile();"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="export" value='<s:text name="config.usb.modem.button.export"/>' class="button"
						onClick="submitAction('export');"
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
				<table id="showSelectFile" class="view" style="display:none;">
					<tr>
						<td>
						<label><span id="selectFileError4UsbModem"></span></label>
						</td>
					</tr>
					<tr>
						<td>
						<label> <s:text name="config.usb.modem.selectFile" /> </label>
						<s:file name="upload" id="usbConfigFile" size="60"/><br/>
						</td>
					</tr>
					<tr>
						<td style="padding-top: 7px;">
						<table border="0" cellspacing="0" cellpadding="0" align="center">
							<tr id="selectBtnIpTr">
								<td><input type="button" name="ignore" value="<s:text name="config.usb.modem.selectFile.submit" />"
									class="button" style="padding-bottom: 0px;"
									onClick="submitAction('import');"></td>
								<td><input type="button" name="ignore" value="<s:text name="config.usb.modem.selectFile.cancel" />"
									class="button" style="padding-bottom: 0px;"
									onClick="hideSelectConfigFile();"></td>
							</tr>
						</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
			<table cellspacing="0" cellpadding="0" border="0"
				class="view">
				<tr>
					<th class="check" width="20px"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th align="left" width="200px"><ah:sort name="displayName" key="config.usb.modem.name" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">	
							<th align="left" width="180px"><ah:sort name="apn" key="config.usb.modem.apn" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">	
							<th align="left" width="150px"><ah:sort name="dailupNumber" key="config.usb.modem.dailup.number" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">	
							<th align="left" width="120px"><ah:sort name="userId" key="config.usb.modem.userId" /></th>
						</s:if>
						<s:if test="%{columnId == 5}">	
							<th align="left" width="120px"><ah:sort name="password" key="config.usb.modem.password" /></th>
						</s:if>
						<s:if test="%{columnId == 6}">	
							<th align="center" width="120px"><s:text name="config.usb.modem.obscure.password" /></th>
						</s:if>
					</s:iterator>
					<s:if test="%{showDomain}">
					    <th align="left"><ah:sort name="owner.domainName" key="config.domain" /></th>
					</s:if>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
						<td class="listCheck"><ah:checkItem /></td>
						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
								<td class="list">&nbsp;<s:property value="displayName" /></td>
							</s:if>
							<s:if test="%{columnId == 2}">	
								<td class="list">&nbsp;<s:property value="apn" /></td>
							</s:if>
							<s:if test="%{columnId == 3}">	
								<td class="list">&nbsp;<s:property value="dailupNumber" /></td>
							</s:if>
							<s:if test="%{columnId == 4}">	
								<td class="list">&nbsp;<s:property value="userId" /></td>
							</s:if>
							<s:if test="%{columnId == 5}">	
								<td class="list">&nbsp;<span id="<s:property value="modemName" />" style='display:"";'><input type="password" 
								value="<s:property value="password" />" disabled style="border:none;background:Transparent;width:110px;"/></span>
								<span id="<s:property value="modemName" />2" style='display:none;'><s:property value="password" /></span>
								</td>
							</s:if>
							<s:if test="%{columnId == 6}">	
								<td class="list" align="center">&nbsp;
								<input type="checkbox" name="chkToggleDisplay" checked
								onclick="hm.util.toggleObscurePassword(this.checked,['<s:property value="modemName" />'],['<s:property value="modemName" />2']);"/></td>
							</s:if>
						</s:iterator>
						<s:if test="%{showDomain}">
						    <td class="list"><s:property value="owner.domainName" /></td>
						</s:if>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
	
	<!--<div id="showSelectFile" style="display:none;">
	<div class="hd"><s:text name="config.usb.modem.selectFile" /></div>
	<div class="bd">
		<table>
			<tr>
				<td>
				<label> <s:text name="config.usb.modem.selectFile" /> </label>
				<s:file name="upload" id="usbConfigFile" size="60"/><br/>
				</td>
			</tr>
			<tr>
				<td style="padding-top: 7px;">
				<table border="0" cellspacing="0" cellpadding="0" align="center">
					<tr id="selectBtnIpTr">
						<td><input type="button" name="ignore" value="<s:text name="config.usb.modem.selectFile.submit" />"
							class="button" style="padding-bottom: 0px;"
							onClick="submitAction('import');"></td>
						<td><input type="button" name="ignore" value="<s:text name="config.usb.modem.selectFile.cancel" />"
							class="button" style="padding-bottom: 0px;"
							onClick="hideSelectConfigFile();"></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
	</div>
	</div>
	
--></s:form></div>