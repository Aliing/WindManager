<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.admin.HmDomain"%>

<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=9" />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="Cache-Control" content="no-cache" />

		<!--CSS file (default YUI Sam Skin) -->
		<link rel="stylesheet" type="text/css" 
			href="<s:url value="/yui/container/assets/skins/sam/container.css" includeParams="none"/>" />
		<link type="text/css" rel="stylesheet"
			href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css" includeParams="none"/>"></link>
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/assets/skins/sam/resize.css" includeParams="none"/>"></link>
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/assets/skins/sam/layout.css" includeParams="none"/>"></link>
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/fonts/fonts-min.css"  includeParams="none"/>" />
		<link rel="stylesheet"
			href="<s:url value="/css/hm.css" includeParams="none"/>"
			type="text/css" />
		<!-- Dependencies -->
		<script type="text/javascript"
			src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" includeParams="none"/>"></script>
		<script type="text/javascript"
			src="<s:url value="/yui/element/element-beta-min.js" includeParams="none"/>"></script>
		<script type="text/javascript"
			src="<s:url value="/yui/datasource/datasource-min.js" includeParams="none"/>"></script>

		<!-- OPTIONAL: Connection Manager (enables XHR for DataSource) -->
		<script type="text/javascript"
			src="<s:url value="/yui/connection/connection-min.js" includeParams="none"/>"></script>

		<!-- OPTIONAL: Drag Drop (enables resizeable or reorderable columns) -->
		<script type="text/javascript"
			src="<s:url value="/yui/dragdrop/dragdrop-min.js" includeParams="none"/>"></script>

		<!-- Optional Resize Support -->
		<script type="text/javascript"
			src="<s:url value="/yui/resize/resize-min.js" includeParams="none"/>"></script>
		<!-- Source files -->
		<script type="text/javascript"
			src="<s:url value="/yui/datatable/datatable-min.js" includeParams="none"/>"></script>
		<script type="text/javascript"
			src="<s:url value="/yui/layout/layout-min.js" includeParams="none"/>"></script>

		<script type="text/javascript" 
			src="<s:url value="/js/hm.util.js" includeParams="none"/>"></script>
		
		<script type="text/javascript" 
			src="<s:url value="/yui/container/container-min.js" includeParams="none" />"></script>
			
		<script src="<s:url value="/yui/animation/animation-min.js" />?v=<s:property value="verParam" />"></script>
	<s:if test="%{l7SignaturePage||imagePage}">
		<link type="text/css" rel="stylesheet" href="<s:url value="/css/jquery.fixedheadertable.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>
		<script src="<s:url value="/js/jquery.min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
		<script src="<s:url value="/js/jquery.fixedheadertable.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
	</s:if>
<style type="text/css">
/* custom styles */
.yui-skin-sam .yui-dt-liner {
	white-space:nowrap;
}
.yui-skin-sam .yui-dt-scrollable .yui-dt-hd, .yui-skin-sam .yui-dt-scrollable .yui-dt-bd {
	border:medium none;
}
html {
	overflow: hidden;
}
</style>
<script>
var formName = 'hiveApFile';
var thisOperation;
var confirmFileDialog;
var cwpWebResourcePath;
function initConfirmFileDialog() {
	confirmFileDialog =
     new YAHOO.widget.SimpleDialog("dlg",
              { width: "350px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal:true,
                close: true,
                zIndex:10,
                text: "<html><body>This operation will remove the selected item(s).<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>",
                icon: YAHOO.widget.SimpleDialog.ICON_WARN,
                constraintoviewport: true,
                buttons: [ { text:"Yes", handler:handleFileYes, isDefault:true },
                           { text:"&nbsp;No&nbsp;", handler:handleFileNo } ]
              } );
	confirmFileDialog.setHeader("Confirm");
	confirmFileDialog.render(document.body);
}

var handleFileYes = function() {
    this.hide();
    doContinueOperation();
};

var handleFileNo = function() {
    this.hide();
};

YAHOO.util.Event.addListener(window, "load", function() {
	
	//save this reference on the parent
	top.uploadFileIframeWindow = window;
	
	initConfirmFileDialog();
	cwpWebResourcePath = '<s:property value="%{cwpWebResourcePath}"/>';

	// for error message
	onLoadNotes();
	<s:if test="%{l7SignaturePage}">
	$('#l7SignatureTable').fixedHeaderTable({ height: 320, themeClass: 'embedded' });
	</s:if>
	<s:if test="%{imagePage}">
	   if("remote" == "<s:property value='selectType'/>"){
		   $('#hiveosImageTable').fixedHeaderTable({ height: 200, width: "520", themeClass: 'embedded' });   
	   }else{
		   $('#hiveosImageTable').fixedHeaderTable({ height: 370, width: "520", themeClass: 'embedded' });	   
	   }
	</s:if>
});

function submitAction(operation) {
	if ('removeFiles' == operation) {
		thisOperation = operation;
		if (validate(operation)) {
			confirmFileDialog.show();
		}
	} else if (validate(operation)) {
		document.forms[formName].operation.value = operation;
	     try {
		    document.forms[formName].submit();
		    showProcessing();
		} catch (e) {
			if (e instanceof Error && e.name == "TypeError") {
				var file = document.getElementById(getFileId());
				hm.util.reportFieldError(file, '<s:text name="error.fileNotExist"></s:text>');
				file.focus();
			}
		}
	}
}
function doContinueOperation() {
	showProcessing();
	document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}
function getFileId() {
	if(document.getElementById("imageTitle")) {
		return "localFile";
	} else if(document.getElementById("l7SignaturePageTitle")){
		return "signatureLocalFile";
	} else {
		return "cwpLocalFile";
	}
}
function validate(operation) {
	if ('addFiles' == operation) {
		var file = document.getElementById(getFileId());
		if (!file.disabled && file.value.length == 0) {
			hm.util.reportFieldError(file, '<s:text name="error.pleaseSelect"><s:param><s:text name="hiveAp.file.local" /></s:param></s:text>');
            file.focus();
			return false;
		}
		if (file.value.indexOf("&") >= 0){
			hm.util.reportFieldError(file, '<s:text name="error.invalidCharacters"><s:param>&</s:param></s:text>');
            file.focus();
			return false;
		}
		if(document.getElementById("imageTitle")) {
			// check the detail of scp server
			if("" == document.getElementById("scpDetail").style.display)
			{
				var ip = document.forms[formName].ipAddress;
				var port = document.forms[formName].port;
				var password;
	    		if (document.getElementById("chkToggleDisplay").checked) {
	       			password = document.getElementById("scpPass");
	    		} else {
	       			password = document.getElementById("scpPass_text");
				}
				if(!checkIfInput(ip, '<s:text name="admin.updateSoftware.ip" />')
					|| !checkIfInput(port, '<s:text name="admin.updateSoftware.scpPort" />')
					|| !checkValidString(document.forms[formName].filePath, '<s:text name="admin.updateSoftware.filePath" />')
					|| !checkValidString(document.forms[formName].scpUser, '<s:text name="admin.updateSoftware.user" />')
					|| !checkValidString(password, '<s:text name="admin.updateSoftware.Password" />')) {
					return false
				}
				if (! hm.util.validateIpAddress(ip.value)) {
					hm.util.reportFieldError(ip, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.updateSoftware.ip" /></s:param></s:text>');
					ip.focus();
					return false;
				}
				var message = hm.util.validateIntegerRange(port.value, 's:text name="admin.updateSoftware.scpPort" />',1,65535);
	    		if (message != null) {
	    			hm.util.reportFieldError(port, message);
	        		port.focus();
	       			return false;
	    		}
			}
		}
	}
	if ('removeFiles' == operation) {
		if("" == document.getElementById("cwpDirectory").style.display) {
			if(!noItemOrSelected(document.forms[formName].cwpDirs,"CWP web page directory",document.getElementById("imageError"))) {
				return false;
			}
		} else {
			if("" == document.getElementById("cwpTitle").style.display) {
				if(!noItemOrSelected(document.forms[formName].cwpFiles,"CWP web page",document.getElementById("imageError"))) {
					return false;
				}
			}
			if("" == document.getElementById("cwpPageCustomTitle").style.display) {
				if(!noItemOrSelected(document.forms[formName].pageResources,"CWP web page resource",document.getElementById("imageError"))) {
					return false;
				}
			}
			<%-- HiveOS image upload --%>
			if(document.getElementById("imageTitle")){
				if (!checkPermission('Remove Image File')) {
					return false;
				}
				var cbs = document.getElementsByName("imageFiles");
				var imageTh = document.getElementById("hiveosImageTh");
				if (cbs.length == 0) {
					hm.util.reportFieldError(imageTh, '<s:text name="info.emptyList"></s:text>');
					return false;
				}
				if (!hm.util.hasCheckedBoxes(cbs)) {
		            hm.util.reportFieldError(imageTh, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="hiveAp.file.available.image" /></s:param></s:text>');
					return false;
				}
			}			
			<%-- L7 signature upload --%>
			if(document.getElementById("l7SignaturePageTitle")){
				var cbs = document.getElementsByName("signatureFiles");
				var signatureTh = document.getElementById("signatureTh");
				if (cbs.length == 0) {
					hm.util.reportFieldError(signatureTh, '<s:text name="info.emptyList"></s:text>');
					return false;
				}
				if (!hm.util.hasCheckedBoxes(cbs)) {
		            hm.util.reportFieldError(signatureTh, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="hiveAp.file.available.l7.signature" /></s:param></s:text>');
					return false;
				}
			}
		}
	}
	if('newDir' == operation) {
		var name = document.forms[formName].directoryName;
		if(!checkIfInput(name, '<s:text name="hiveAp.file.directoryName" />')) {
			return false
		} else {
			var message = hm.util.validateDirectoryName(name.value, '<s:text name="hiveAp.file.directoryName" />');
		   	if (message != null) {
		   		hm.util.reportFieldError(name, message);
        		name.focus();
        		return false;
			}
		}
	}
	return true;
}

function checkValidString(inputElement, title) {
	if (!checkIfInput(inputElement, title)) {
		return false;
	}
	var message = hm.util.validateSsid(inputElement.value, title);
   	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
      	inputElement.focus();
      	return false;
	}
	if(inputElement.value.length >0 && inputElement.value.indexOf("&") >= 0){
		hm.util.reportFieldError(inputElement, '<s:text name="error.invalidCharacters"><s:param>&</s:param></s:text>');
        inputElement.focus();
		return false;
	}
	return true;
}

function checkPermission(title) {
	var domainNamen = '<s:property value="domainName"/>';
	if ('<%=HmDomain.HOME_DOMAIN%>' != domainNamen) {
		var permission = document.getElementById("permission");
		hm.util.reportFieldError(permission, '<s:text name="error.hiveAPFile.permissionDeniedFeature"><s:param>\''+title+'\'</s:param></s:text>');
		permission.focus();
        return false;
	}
	return true;
}

function hasSelectedOptions(options) {
	for (var i = 0; i < options.length; i++) {
		if (options[i].selected) {
			return true;
		}
	}
	return false;
}

function getSelectedCount(options) {
	var j = 0;
	for (var i = 0; i < options.length; i++) {
		if (options[i].selected) {
			j++;
		}
	}
	return j;
}

function noItemOrSelected(directory,title,dir)
{
	if(directory.options.length == 1 && '<s:text name="config.optionsTransfer.none" />' == directory.options[0].text) {
		hm.util.reportFieldError(dir, '<s:text name="info.emptyList" />');
        directory.focus();
		return false;
	}
	if (!hasSelectedOptions(directory.options)) {
		hm.util.reportFieldError(dir, '<s:text name="error.pleaseSelect"><s:param>'+title+'</s:param></s:text>');
        directory.focus();
		return false;
	}
	return true;
}

function enableLocal(flag) {
	document.getElementById("localFile").disabled = flag != 1;
	document.getElementById("scpDetail").style.display = flag != 2 ? "none" : "";
	if(flag != 2){
		$('#hiveosImageTable').fixedHeaderTable("destroy");
		$('#hiveosImageTable').fixedHeaderTable({ height: 370, width: "520", themeClass: 'embedded' });
	}else{
		$('#hiveosImageTable').fixedHeaderTable("destroy");
		$('#hiveosImageTable').fixedHeaderTable({ height: 200, width: "520", themeClass: 'embedded' });
	}
}

function checkIfInput(inputElement, title)
{
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        inputElement.focus();
        return false;
    }
    return true;
}

function onUnloadEvent() {
	hm.util.onUnloadValidation();
	onUnloadNotes();
}

function showImage(imageName){
	if('<s:text name="config.optionsTransfer.none" />' == imageName || getSelectedCount(document.forms[formName].pageResources.options) > 1) {
		return;
	}
	var contentEl = document.getElementById("previewImage");
	var value = cwpWebResourcePath + imageName;
	var img = document.createElement("img");
	img.src = value;
	img.width = "240";
	img.height = "200";
	hm.util.replaceChildren(contentEl, img);
}

function toggleHiveosImageFiles(checked){
	var cbs = document.getElementsByName("imageFiles");
	for(var i=0; i<cbs.length; i++){
		cbs[i].checked = checked;
	}
}

function toggleSignatureFiles(checked){
	var cbs = document.getElementsByName("signatureFiles");
	for(var i=0; i<cbs.length; i++){
		cbs[i].checked = checked;
	}
}

/**
var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var td = document.getElementById(details.id);
	var value = details.v;
	td.length=0;
	td.length=value.length;
	for(var i = 0; i < value.length; i ++) {
		td.options[i].text=value[i];
	}
};

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function showCwpFiles(name) {
	document.forms[formName].cwpDir.title=name;
	var url = '<s:url action="hiveApFile"><s:param name="operation" value="viewFile"/></s:url>' + '&cwpName='+encodeURIComponent(name)+'&key='+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}
*/

</script>
</head>
<body class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onunload="onUnloadEvent();" >
<s:if test="%{l7SignaturePage||imagePage}"><div style="width: 560px;"></s:if>
<s:else><div></s:else>
<s:form action="hiveApFile" enctype="multipart/form-data"
		method="POST">
	<s:hidden name="operation" />
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="10"><%-- add this password dummy to fix issue with auto complete function --%>
			<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password"></td>
		</tr>
		<tr>
			<td>
			<s:if test="%{l7SignaturePage||imagePage}"><table cellspacing="0" cellpadding="0" border="0" width="100%"></s:if>
			<s:else><table cellspacing="0" cellpadding="0" border="0"></s:else>
				<tr style="display:<s:property value="%{hideUploadFile}"/>">
					<td valign="top" style="padding: 5px 2px 0px 15px;">
					<fieldset>
						<table border="0" cellspacing="0" cellpadding="0">
							<s:if test="%{imagePage}">
							<tr id="imageTitle">
								<td colspan="3" class="noteInfo" style="padding:5px 0px 0 8px;"><s:text
									name="hiveAp.file.available.image.note" /><BR><s:text
									name="hiveAp.file.name.limit"><s:param value="64" /></s:text></td>
							</tr>
							</s:if>
							<tr style="display:<s:property value="%{hideCwp}"/>" id="cwpTitle">
								<td colspan="3" class="noteInfo" style="padding:5px 0px 0 8px;"><s:text
									name="hiveAp.file.available.cwpFile.note" /><BR><s:text
									name="hiveAp.file.name.limit"><s:param value="32" /></s:text><BR><s:text
									name="hiveAp.file.directory.size.limit" /></td>
							</tr>
							<tr style="display:<s:property value="%{hideCwpPageCustom}"/>" id="cwpPageCustomTitle">
								<td colspan="3" class="noteInfo" style="padding:5px 0px 0 8px;"><s:text
									name="hiveAp.file.available.cwpFile.note" /><BR><s:text
									name="hiveAp.file.name.limit"><s:param value="32" /></s:text><BR><s:text
									name="hiveAp.file.size.limit" /></td>
							</tr>
							<s:if test="%{l7SignaturePage}">
							<tr id="l7SignaturePageTitle">
								<td colspan="3" class="noteInfo" style="padding:5px 0px 0 8px;"><s:text
									name="hiveAp.file.available.l7.signature.note" /><BR><s:text
									name="hiveAp.file.name.limit"><s:param value="64" /></s:text></td>
							</tr>
							</s:if>
							<tr>
								<td height="6px" />
							</tr>
							<tr style="display:<s:property value="%{imagePage?'':'none'}"/>">
								<td>
									<table>
										<s:if test="%{!oEMSystem}">
										<tr>
											<td colspan="2" id="permission">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td nowrap><s:radio label="Gender" id="selectType"
															name="selectType" list="#{'license':''}"
															onclick="enableLocal(3);" value="%{selectType}" /></td>
														<td><label for="selectTypelicense"><s:property value="%{hiveAPImageVersion}"/></label>
														</td>
													</tr>
												</table>
											</td>										
										</tr>
										</s:if>
										<tr>
											<td width="95" nowrap><s:radio label="Gender"
												name="selectType" list="#{'local':'Local File'}"
												onclick="enableLocal(1);" value="%{selectType}" /></td>
											<td><s:file id="localFile" name="upload" size="42" disabled="%{disableLocal}" value="%{upload}" />
											</td>
										</tr>
										<tr>
											<td width="95" nowrap valign="top"><s:radio label="Gender"
												name="selectType" list="#{'remote':'SCP Server'}"
												onclick="enableLocal(2);" value="%{selectType}" />
											</td>
											<td rowspan="11" style="display:<s:property value="%{showScpServer}"/>" id="scpDetail">
												<fieldset>
												<table>
													<tr>
														<td width="65"><label><s:text
															name="admin.updateSoftware.ip" /></label></td>
														<td><s:textfield size="35" name="ipAddress" maxlength="15" value="%{ipAddress}"
															onkeypress="return hm.util.keyPressPermit(event,'ip');" />
														</td>
													</tr>
													<tr>
														<td width="65"><s:text
															name="admin.updateSoftware.scpPort" /></td>
														<td><s:textfield size="8" name="port" maxlength="4"
															onkeypress="return hm.util.keyPressPermit(event,'ten');" value="%{port}" /></td>
													</tr>
													<tr>
														<td width="65"><label><s:text
															name="admin.updateSoftware.filePath" /></label></td>
														<td><s:textfield size="35" name="filePath" value="%{filePath}"
															onkeypress="return hm.util.keyPressPermit(event,'ssid');" />
														</td>
													</tr>
													<tr>
														<td width="65"><label><s:text
															name="admin.updateSoftware.user" /></label></td>
														<td><s:textfield size="35" name="scpUser" value="%{scpUser}"
															onkeypress="return hm.util.keyPressPermit(event,'ssid');" />
														</td>
													</tr>
													<tr>
														<td width="65"><label><s:text
															name="admin.updateSoftware.Password" /></label></td>
														<td><s:password size="35" id="scpPass" name="scpPass" showPassword="true" value="%{scpPass}"
															onkeypress="return hm.util.keyPressPermit(event,'ssid');" />
															<s:textfield id="scpPass_text" name="scpPass" size="35" disabled="true" cssStyle="display:none"
															onkeypress="return hm.util.keyPressPermit(event,'ssid');" />
														</td>
													</tr>
													<tr>
														<td>&nbsp;</td>
														<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td>
																		<s:checkbox id="chkToggleDisplay" name="ignore" value="true"
																		onclick="hm.util.toggleObscurePassword(this.checked,['scpPass'],['scpPass_text']);" />
																	</td>
																	<td>
																		<s:text name="admin.user.obscurePassword" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
												</fieldset>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr style="display:<s:property value="%{(l7SignaturePage || imagePage)?'none':''}"/>">
								<td width="95" class="labelT1" nowrap>Local File</td>
								<td colspan="2"><s:file id="cwpLocalFile" name="upload" size="40" value="%{upload}" /></td>
							</tr>
							<s:if test="%{l7SignaturePage}">
							<tr>
								<td width="95" class="labelT1" nowrap>Local File</td>
								<td colspan="2"><s:file id="signatureLocalFile" name="upload" size="40" value="%{upload}" /></td>
							</tr>
							</s:if>
							<tr>
								<td height="6px" />
							</tr>
							<tr>
								<td colspan="3" align="center">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td><input type="button" name="add" value="Upload" class="button"
											onclick="submitAction('addFiles');">
										</td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
					</fieldset>
					</td>
				</tr>
				<tr>
					<td height="6px" />
				</tr>
				<tr style="display:<s:property value="%{hideUploadFile==''?'none':''}"/>;" id="cwpDirectory">
					<td style="padding: 0px 2px 0px 15px;">
					<fieldset>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelT1" width="93"><s:text name="hiveAp.file.directoryName" /></td>
							<td><s:textfield size="30" name="directoryName" maxlength="32"
								onkeypress="return hm.util.keyPressPermit(event,'directory');" />&nbsp;<s:text
								name="config.ssid.ssidName_range" />&nbsp;</td>
							<td><input type="button" name="newDir" value="Create"
								class="button" onclick="submitAction('newDir');"></td>
						</tr>
					</table>
					</fieldset>
					</td>
				</tr>
				<tr>
					<td style="padding: 5px 2px 5px 15px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><label id="imageError"></label></td>
							</tr>
						</table>	
					</td>
				</tr>
				<tr>
					<td style="display:<s:property value="%{hideUploadFile==''?'none':''}"/>;padding: 0px 2px 0px 17px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td align="center"><s:text name="hiveAp.file.available.cwpDirect" /></td>
							</tr>
							<tr>
								<td><s:select size="12" name="cwpDirs" multiple="true" onclick="hm.util.showtitle(this);"
									list="%{availableCwpDirs}" cssStyle="width: 500px;" /></td>
							</tr>
						</table>
					</td>
					<td style="display:<s:property value="%{hideCwp}"/>;padding: 0px 2px 0px 17px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td align="center"><s:text name="hiveAp.file.available.cwpFile" /></td>
							</tr>
							<tr>
								<td><s:select multiple="true" size="12" name="cwpFiles" id="cwpfiles" list="%{availableCwpFiles}"
									cssStyle="width: 540px;" onclick="hm.util.showtitle(this);" /></td>
							</tr>
						</table>
					</td>
					<td style="display:<s:property value="%{hideCwpPageCustom}"/>;padding: 0px 2px 0px 17px;">						
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td align="center"><s:text name="hiveAp.file.available.cwpPageResource" /></td>
								<td align="center"><s:text name="hiveAp.file.available.cwpFile.preview" /></td>
							</tr>
							<tr>
								<td><s:select multiple="true" size="14" name="pageResources" onclick="hm.util.showtitle(this);"
									list="%{availablePageResources}" cssStyle="width: 270px;" onchange="showImage(this.options[this.selectedIndex].text);" /></td>
								<td width="270px" style="padding-left:5px">
									<fieldset>
										<table>
											<tr>
											<td id="previewImage" height="200px"/>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>					
					</td>
				</tr>
				<s:if test="%{imagePage}">
				<tr>
					<td style="padding: 0px 2px 0px 17px;">
						<table id="hiveosImageTable" border="0" cellspacing="0" cellpadding="0" width="100%">
							<thead>
							<tr>
								<th id="hiveosImageTh" align="left" style="padding-left: 0;" width="20"><input
									type="checkbox" onclick="toggleHiveosImageFiles(this.checked);"></th>
								<th align="left"><s:text
									name="hiveAp.update.images.fileName" /></th>
								<th align="left"><s:text
									name="hiveAp.update.images.version" /></th>
								<th align="left"><s:text
									name="hiveAp.update.images.platform" /></th>
								<th align="left"><s:text
									name="hiveAp.update.images.size" /></th>
							</tr>
							</thead>
							<tbody>
							<s:if test="%{availableHiveosImages.isEmpty}">
							<tr class="list">
								<td width="20"></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<ah:emptyList/>
							</s:if>
							<s:else>
							<s:iterator value="availableHiveosImages" status="status">
							<tr class="list">
								<td class="listCheck" width="20"><s:checkbox name="imageFiles" fieldValue="%{imageName}" /></td>
								<td class="list"><s:property value="imageName" /></td>
								<td class="list"><s:property value="imageVersion" /></td>
								<td class="list"><s:property value="imagePlatformString" /></td>
								<td class="list"><s:property value="imageSizeString" /></td>
							</tr>
							</s:iterator></s:else>
							<tr><td height="10px"></td></tr>
							</tbody>
						</table>
					</td>
				</tr>
				</s:if>
				<s:if test="%{l7SignaturePage}">
				<tr>
					<td style="padding: 0px 2px 0px 17px;">
						<table id="l7SignatureTable" border="0" cellspacing="0" cellpadding="0" width="100%">
							<thead>
							<tr>
								<th id="signatureTh" align="left" style="padding-left: 0;" width="30"><input
									type="checkbox" onclick="toggleSignatureFiles(this.checked);"></th>
								<th align="left"><s:text
									name="hiveAp.file.l7.signature.fileName" /></th>
								<th align="left"><s:text
									name="hiveAp.file.l7.signature.version" /></th>
								<th align="left"><s:text
									name="hiveAp.file.l7.signature.release.date" /></th>
								<th align="left"><s:text
									name="hiveAp.file.l7.signature.platform" /></th>
								<th align="left"><s:text
									name="hiveAp.file.l7.signature.type" /></th>
							</tr>
							</thead>
							<tbody>
							<s:if test="%{availableL7Signatures.isEmpty}">
							<tr class="list">
								<td width="30"></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<ah:emptyList/>
							</s:if>
							<s:else>
							<s:iterator value="availableL7Signatures" status="status">
							<tr class="list">
								<td class="listCheck" width="30"><s:checkbox name="signatureFiles" fieldValue="%{fileName}" /></td>
								<td class="list"><s:property value="fileName" /></td>
								<td class="list"><s:property value="ahVersion" /></td>
								<td class="list"><s:property value="dateReleased" /></td>
								<td class="list"><s:property value="platformIdString" /></td>
								<td class="list"><s:property value="packageTypeString" /></td>
							</tr>
							</s:iterator></s:else>
							<tr><td height="10px"></td></tr>
							</tbody>
						</table>
					</td>
				</tr>
				</s:if>
				<tr><s:if test="%{l7SignaturePage||imagePage}"><s:set name="sbtn_align" value="left" /></s:if>
					<s:else><s:set name="sbtn_align" value="center" /></s:else>
					<td align="<s:property value="%{#sbtn_align}"/>" style="padding: 5px 2px 0px 15px;">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="remove" value="Remove" class="button"
								onclick="submitAction('removeFiles');">
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="6px" />
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
</body>
</html>
