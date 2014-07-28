<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.hiveap.HiveApUpdateSettings"%>
<script type="text/javascript" src="<s:url value="/js/doT.min.js" includeParams="none"/>"></script>

<style type="text/css">
<!--
	#signatureOptions{
		height: 0px; 
		width: 100%;
		position: absolute; 
		padding: 0 10px 3px;
		top: -6px; 
		left: -10px; 
		background: #FFFFFF; 
		border-bottom: 1px solid #999;
		overflow: hidden;
	}
	
	.signatureToolBar{
		margin: 1px; 
		padding: 4px 10px 0; 
		text-align: right; 
/* 		background-color: #EDF5FF;  */
	}
	.signatureToolBar a{
		text-decoration: none;
		color: #003366;
		margin-right: 5px;
		font-weight: bold;
	}
	.signatureToolBar a span{
		font-size: 12px;
		color: #99CCCC;
	}
	a#saveOption:hover img, a#exitOption:hover img{
		filter: alpha(opacity="50");
		opacity: 0.5;
	}
	.signatureToolBar a:hover span{
		 color: #003366;
	}
-->
</style>

<script id="signatureFileInfo" type=" type="text/x-dot-template">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
	<td>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr><td><s:text name="hiveAp.file.l7.signature.selected.file.info" /></td></tr>
		</table>
	</td>
</tr>
<tr>
	<td style="background: #f2f2f2; border: 1px solid #ddd;">
		<table width="100%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td class="labelT1" width="160px"><s:text name="hiveAp.file.l7.signature.fileName" /></td>
				<td>{{=it.fn}}</td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name="hiveAp.file.l7.signature.version" /></td>
				<td>{{=it.ver}}</td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name="hiveAp.file.l7.signature.release.date" /></td>
				<td>{{=it.dr}}</td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name="hiveAp.file.l7.signature.platform" /></td>
				<td>{{=it.pf}}</td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name="hiveAp.file.l7.signature.type" /></td>
				<td>{{=it.pt}}</td>
			</tr>
		</table>
	</td>
</tr>
</table>
</script>

<script id="signatureVerInfo" type=" type="text/x-dot-template">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
	<td>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr><td><s:text name="hiveAp.file.l7.signature.selected.ver.info" /></td></tr>
		</table>
	</td>
</tr>
<tr>
	<td style="background: #f2f2f2; border: 1px solid #ddd; padding: 5px 2px;">
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<th style="text-align: left;"><s:text name="hiveAp.file.l7.signature.version" /></th>
				<th style="text-align: left;"><s:text name="hiveAp.file.l7.signature.fileName" /></th>
				<th style="text-align: left;"><s:text name="hiveAp.file.l7.signature.release.date" /></th>
				<th style="text-align: left;"><s:text name="hiveAp.file.l7.signature.platform" /></th>
				<th style="text-align: left;"><s:text name="hiveAp.file.l7.signature.type" /></th>
			</tr>
{{ for (var i = 0, l = it.length; i < l; i++) { }}
			<tr>
				{{? i == 0}}
				<td class="list" rowspan={{=it.length}}>{{=it[i].ver}}</td>
				{{?}}
				<td class="list">{{=it[i].fn}}</td>
				<td class="list">{{=it[i].dr}}</td>
				<td class="list">{{=it[i].pf}}</td>
				<td class="list">{{=it[i].pt}}</td>
			</tr>
{{ } }}
		</table>
	</td>
</tr>
<tr>
	<td>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr><td style="color: red;"><s:text name="hiveAp.file.l7.signature.selected.ver.note" /></td></tr>
		</table>
	</td>
</tr>
</table>
</script>

<script>
var formName = 'hiveApUpdate';
var NONE_ITEM = '<s:text name="config.optionsTransfer.none" />';

var SIGNATURE_SELECTION_VER = '<%=HiveApUpdateSettings.ImageSelectionType.softVer.toString()%>';
var CONNECT_TYPE_128 = <%=HiveApUpdateSettings.CONNECT_TYPE_128K%>;
var CONNECT_TYPE_256 = <%=HiveApUpdateSettings.CONNECT_TYPE_256K%>;
var CONNECT_TYPE_1500 = <%=HiveApUpdateSettings.CONNECT_TYPE_1500K%>;
var CONNECT_TYPE_2000 = <%=HiveApUpdateSettings.CONNECT_TYPE_2000K%>;
var CONNECT_TYPE_LOCAL = <%=HiveApUpdateSettings.CONNECT_TYPE_LOCAL%>;

function onLoadPage(){
	requestSignatureInfo();
	//create Animation;
	createAnimation();
	//bind animation listener
	bindAnimationListener();
	// show the unsupported image update Aps Message
	showUnsupportedApsMessage();
}

function showUnsupportedApsMessage(){
	var unsupportedApsMessage  = "<s:property value='%{unsupportedApsMessage}' />" ;
	if(unsupportedApsMessage){
		//showInfoDialog(unsupportedApsMessage);
		showPageNotes(unsupportedApsMessage);
	}
}

function requestSignatureInfo(){
	var fileStyle = document.getElementById("signatureSelTypeFileTr").style.display;
	var verStyle = document.getElementById("signatureSelTypeVerTr").style.display;
	var versionTypes = document.getElementsByName("signatureVersionSelectType");
	var versionValue = "";
	for(var i=0; i<versionTypes.length; i++){
		if(versionTypes[i].checked){
			versionValue = versionTypes[i].value;
			break;
		}
	}
	if("" == verStyle){
		var selectedVersion;
		if("latest" == versionValue){
			selectedVersion = document.getElementById(formName + "_latestVersion").value;
		}else{
			selectedVersion = document.getElementById(formName + "_selectedVersion").value;
		}
		if(selectedVersion != NONE_ITEM){
			var url = '<s:url action="hiveApUpdate" includeParams="none"></s:url>' + "?operation=fetchSignatureInfo&selectedVersion="+encodeURIComponent(selectedVersion)+"&ignore="+ + new Date().getTime();
			ajaxRequest(null, url, signatureCallback);
		}else{
			document.getElementById("signatureVerInfoBox").style.display = "none";
		}
	}else if("" == fileStyle){
		var selectedImage = document.getElementById(formName + "_selectedImage").value;
		if(selectedImage != NONE_ITEM){
			var url = '<s:url action="hiveApUpdate" includeParams="none"></s:url>' + "?operation=fetchSignatureInfo&selectedImage="+encodeURIComponent(selectedImage)+"&ignore="+ + new Date().getTime();
			ajaxRequest(null, url, signatureCallback);
		}else{
			document.getElementById("signatureFileInfoBox").style.display = "none";
		}
	}
}

function signatureCallback(o){
	eval("var result = " + o.responseText);
	if(result.ver){
		document.getElementById("signatureVerInfoBox").style.display = "";
	}else{
		document.getElementById("signatureVerInfoBox").style.display = "none";
	}
	if(result.file){
		document.getElementById("signatureFileInfoBox").style.display = "";
	}else{
		document.getElementById("signatureFileInfoBox").style.display = "none";
	}
	if(result.ver){
		document.getElementById("signatureVerInfoBox").innerHTML = doT.template(document.getElementById("signatureVerInfo").innerHTML)(result.ver);
	}
	if(result.file){
		document.getElementById("signatureFileInfoBox").innerHTML = doT.template(document.getElementById("signatureFileInfo").innerHTML)(result.file);
	}
}

function selectTypeChanged(type){
	<%-- do not trigger as it's in settings page. trigger after save settings.
	var signatureSelTypeFileTr = document.getElementById("signatureSelTypeFileTr");
	var signatureSelTypeVerTr = document.getElementById("signatureSelTypeVerTr");
	if("softVer" == type){
		signatureSelTypeFileTr.style.display = "none";
		signatureSelTypeVerTr.style.display = "";
	}else if("imgName" == type){
		signatureSelTypeFileTr.style.display = "";
		signatureSelTypeVerTr.style.display = "none";
	}else{
		signatureSelTypeFileTr.style.display = "none";
		signatureSelTypeVerTr.style.display = "none";
	}
	requestSignatureInfo();
	--%>
}

function selectVersionTypeChanged(type){
	var selectedVersion = document.getElementById(formName + "_selectedVersion");
	var latestVersion = document.getElementById(formName + "_latestVersion");
	if("latest" == type){
		selectedVersion.disabled = true;
	}else{
		selectedVersion.disabled = false;
	}
	if(selectedVersion.value != latestVersion.value){
		requestSignatureInfo();
	}
}

function signatureFileSelectionChanged(){
	requestSignatureInfo();
}

function signatureVerSelectionChanged(){
	requestSignatureInfo();
}

function submitAction(operation) {
	if (validate(operation)) {
		showProcessing();
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(!validateSignatureParams(operation)){
		return false;
	}
	if(!validateApSelection(operation)){
		return false;
	}
	return true;
}

function validateSignatureParams(operation){
	if(operation == 'uploadSignature'){
		var fileStyle = document.getElementById("signatureSelTypeFileTr").style.display;
		var verStyle = document.getElementById("signatureSelTypeVerTr").style.display;
		if(fileStyle == ""){
			var selectedImage = document.getElementById(formName+"_selectedImage");
			var selectedValue = selectedImage.options[selectedImage.selectedIndex].value;
			if(!selectedValue.match('.tar.gz')){
				hm.util.reportFieldError(selectedImage, '<s:text name="error.pleaseSelect"><s:param><s:text name="hiveAp.file.l7.signature.fileName.label" /></s:param></s:text>');
				return false;
			}
		}else if(verStyle == ""){
			var selectVersionLatest = document.getElementById(formName + "_signatureVersionSelectTypelatest");
			var selectVersionLatestLabel = document.getElementById("selectVersionLatestLabel").innerHTML;
			var selectedImage = document.getElementById(formName+"_selectedVersion");
			var selectedValue = selectedImage.options[selectedImage.selectedIndex].value;
			if(selectVersionLatest.checked){
				if(selectVersionLatestLabel == NONE_ITEM){
					hm.util.reportFieldError(selectVersionLatest, '<s:text name="error.none.item"></s:text>');
					return false;
				}
			}else{
				if(selectedValue == NONE_ITEM){
					hm.util.reportFieldError(selectVersionLatest, '<s:text name="error.none.item"></s:text>');
					return false;
				}
			}
		}
	}
	return true;
}

function validateApSelection(operation){
	if(operation == 'uploadSignature'){
		var cbs = document.getElementsByName('selectedIds');
		var isSelected = false;
		for (var i = 0; i < cbs.length; i++) {
			if (cbs[i].checked) {
				isSelected = true;
				break;
			}
		}
		if(!isSelected){
			var listElement = document.getElementById('checkAll');
			hm.util.reportFieldError(listElement, '<s:text name="info.selectObject"></s:text>');
			return false;
		}
	}
	return true;
}


function validateSignatureTimedout(){
	var timedoutEl = document.getElementById(formName + "_dataSource_signatureTimedout");
	
	if(timedoutEl.value.length == 0){
		hm.util.reportFieldError(timedoutEl, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.update.images.timeout" /></s:param></s:text>');
		return false;
	}
	var message = hm.util.validateIntegerRange(timedoutEl.value, '<s:text name="hiveAp.update.images.timeout" />',
	                                           <s:property value="15" />,
	                                           <s:property value="720" />);
	if (message != null) {
		hm.util.reportFieldError(timedoutEl, message);
		return false;
	}
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveApUpdate" includeParams="none"/>?operation=<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln('<s:text name="hiveAp.update.l7.signature"/> </td>');
}

function connectTypeChange(connectType){
	var timedoutEl = document.getElementById(formName + "_dataSource_signatureTimedout");
	switch(parseInt(connectType)){
	case CONNECT_TYPE_128:
	case CONNECT_TYPE_256:
		timedoutEl.value = 30;
		break;
	case CONNECT_TYPE_1500:
	case CONNECT_TYPE_2000:
	case CONNECT_TYPE_LOCAL:
		timedoutEl.value = 15;
		break;
	}
}

function bindAnimationListener(){
	<s:if test="%{writeDisabled != 'disabled'}">
	var showOptionEl = document.getElementById("showOption");
	showOptionEl.onclick = showOption;
	showOptionEl.style.visibility = 'visible';
	</s:if>
}

function createAnimation(){
	signatureOptionsAnim = new YAHOO.util.Anim('signatureOptions');
	signatureOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
}

function signatureOptionsExpand(){
	var signatureListDiv = document.getElementById("signatureListDiv");
	signatureOptionsAnim.stop();
	signatureOptionsAnim.attributes.height = { to: signatureListDiv.offsetHeight + 3 };
	signatureOptionsAnim.animate();
}

function signatureOptionsCollapse(){
	signatureOptionsAnim.stop();
	signatureOptionsAnim.attributes.height = { to: 0 };
	signatureOptionsAnim.animate();
}

function showOption(){
	document.getElementById("showOption").style.display = "none";
	document.getElementById("saveOption").style.display = "";
	document.getElementById("exitOption").style.display = "";
	document.getElementById("updateBtn").disabled = true;
	// fix select component cannot be mask issue in IE6
	if(YAHOO.env.ua.ie == 6){
		showOption.showSelector = document.getElementById("signatureSelTypeFileTr").style.display 
			== "none"? document.getElementById("signatureSelTypeVerTr"):document.getElementById("signatureSelTypeFileTr");
		showOption.showSelector.style.visibility ="hidden";
	}
	signatureOptionsExpand();
}
function saveOption(){
	if(!validateSignatureTimedout()){
		return;
	}
	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.forms[formName].operation.value = 'saveSignatureOption';
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('get', url, {success:saveSignatureOptionsResult}, null);
}
function exitOption(){
	document.getElementById("showOption").style.display = "";
	document.getElementById("saveOption").style.display = "none";
	document.getElementById("exitOption").style.display = "none";
	document.getElementById("updateBtn").disabled = false;
	// fix select component cannot be mask issue in IE6
	if(YAHOO.env.ua.ie == 6 && showOption.showSelector){
		showOption.showSelector.style.visibility ="visible";
	}
	signatureOptionsCollapse();
}
function saveSignatureOptionsResult(o){
	eval("var details = " + o.responseText);
	if(details.suc){
		exitOption();
		document.getElementById("connectionTypeTd").innerHTML = details.conn;
		document.getElementById("timedoutTd").innerHTML = details.timedout;
		if(SIGNATURE_SELECTION_VER == details.select){
			document.getElementById("signatureSelTypeFileTr").style.display = "none";
			document.getElementById("signatureSelTypeVerTr").style.display = "";
		}else{
			document.getElementById("signatureSelTypeFileTr").style.display = "";
			document.getElementById("signatureSelTypeVerTr").style.display = "none";
		}
		requestSignatureInfo();
	}else{
		warnDialog.cfg.setProperty('text', '<s:text name="error.hiveap.l7.signature.option.save.failed" />');
		warnDialog.show();
	}
}
</script>

<div id="content"><s:form id="hiveApUpdate" action="hiveApUpdate">
	<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input id="updateBtn" type="button" name="ignore" value="Upload"
						class="button" onClick="submitAction('uploadSignature');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="cancel" value="Cancel"
						class="button"
						onClick="submitAction('<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>');"
						<s:property value="writeDisabled" />>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table  class="editBox" cellspacing="0" cellpadding="0" border="0" width="700px">
				<tr>
					<td>
						<div class="signatureToolBar">
						<a id="showOption" href="#showOptions" style="visibility: hidden;"><s:text name="hiveAp.update.images.settings.label"/><span>&#9660;</span></a>
						<a id="saveOption" href="#saveOptions" onclick="saveOption();" style="display: none;">
							<img alt="Save" title="Save" src="<s:url value="/images/save.png" includeParams="none"/>" width="16" class="dinl"></a>
						<a id="exitOption" href="#exitOptions" onclick="exitOption();" style="display: none;">
							<img alt="Cancel" title="Cancel" src="<s:url value="/images/cancel.png" includeParams="none"/>" width="16" class="dinl"></a>
					</div>
					</td>
				</tr>
				<tr>
					<td style="padding: 4px 10px 10px 10px;" valign="top">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td>
									<div id="signatureListDiv" style="position: relative;">
										<div>
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr><td height="5px"></td></tr>
															<tr>
																<td class="labelT1" colspan="10"><s:text name="hiveAp.file.l7.signature.update.label" /></td>
															</tr>
															<tr id="signatureSelTypeFileTr" style="display: <s:property value="signatureSelTypeFileStyle" />">
																<td class="labelT1" width="170px"><s:text
																	name="hiveAp.file.l7.signature.fileName.label" /></td>
																<td nowrap="nowrap"><s:select onchange="signatureFileSelectionChanged();" name="selectedImage" list="%{availableSignatureFiles}" cssStyle="width: 200px;" />
																<s:if test="%{isInHomeDomain}">
																<input type="button" name="importSignature1" value="Add/Remove" style="width:100px"
																class="button" onClick='openUploadFilePanel("Add/Remove <s:text name='hiveAp.file.l7.signature.label'/>", "newL7Signature");'></s:if></td>
															</tr>
															<tr id="signatureSelTypeVerTr" style="display: <s:property value="signatureSelTypeVerStyle" />">
																<td>
																	<table width="100%" cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td width="1px"></td><td class="labelT1" style="padding-left: 2px;"><s:radio onchange="selectVersionTypeChanged(this.value);" label="Gender" name="signatureVersionSelectType" list="%{signatureVersionSelectType1}" listKey="key" listValue="value"/>
																			&nbsp;&nbsp;&nbsp;<s:hidden name="latestVersion" value="%{latestSignatureVersion.key}" /><span id="selectVersionLatestLabel"><s:property value="%{latestSignatureVersion.value}" /></span></td>
																			<td rowspan="2">
																			<s:if test="%{isInHomeDomain}">
																			<input type="button" name="importSignature2" value="Add/Remove" style="width:100px"
																			class="button" onClick='openUploadFilePanel("Add/Remove <s:text name='hiveAp.file.l7.signature.label'/>", "newL7Signature");'></s:if>
																			</td>
																		</tr>
																		<tr>
																			<td></td><td class="labelT1" style="padding-left: 2px;"><s:radio onchange="selectVersionTypeChanged(this.value);" label="Gender" name="signatureVersionSelectType" list="%{signatureVersionSelectType2}" listKey="key" listValue="value"/>
																			&nbsp;&nbsp;&nbsp;<s:select onchange="signatureVerSelectionChanged();" name="selectedVersion" list="%{availableSignatureVersions}" cssStyle="width: 200px;" listKey="key" listValue="value" disabled="%{otherSignatureVersionDisabled}" />
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr><td height="5px"></td></tr>
														</table>
													</td>
												</tr>
												<tr>
													<td>
														<table width="100%" cellspacing="0" cellpadding="0" border="0">
															<tr><td id="signatureVerInfoBox" style="display: none; padding-left: 10px; padding-right: 10px;"></td></tr>
														</table>
													</td>
												</tr>
												<tr>
													<td>
														<table width="100%" cellspacing="0" cellpadding="0" border="0">
															<tr><td id="signatureFileInfoBox" style="display: none; padding-left: 10px; padding-right: 10px;"></td></tr>
														</table>
													</td>
												</tr>
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" width="170px"><s:text name="hiveAp.update.images.connect.type" /></td>
																<td id="connectionTypeTd"><s:property value="dataSource.signatureConnectionString" /></td>
															</tr>
															<tr>
																<td class="labelT1" width="170px"><s:text name="hiveAp.update.images.timeout" /></td>
																<td id="timedoutTd"><s:property value="dataSource.signatureTimedoutString" /></td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
										<div id="signatureOptions">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr><!-- update type section -->
													<td style="padding-left: 5px;">
														<table cellspacing="0" cellpadding="0" border="0">
															<tr><td height="5px"></td></tr>
															<tr>
																<td width="250px"><s:radio onchange="selectTypeChanged(this.value);" label="Gender" name="imageSelectType" value="%{dataSource.signatureSelectType}" list="%{signatureSelectType1}" listKey="key" listValue="value"/></td>
																<td><s:radio onchange="selectTypeChanged(this.value);" label="Gender" name="imageSelectType" value="%{dataSource.signatureSelectType}" list="%{signatureSelectType2}" listKey="key" listValue="value"/></td>
															</tr>
															<tr><td height="10px"></td></tr>
														</table>
													</td>
												</tr>
												<tr><!-- update connection type and timeout -->
													<td>
														<table>
															<tr>
																<td class="labelT1" width="120px"><s:text name="hiveAp.update.images.connect.type" /></td>
																<td><s:select name="dataSource.signatureConnType" list="%{connectTypes}" listKey="key" listValue="value" onchange="connectTypeChange(this.value);"/></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="hiveAp.update.images.timeout" /></td>
																<td><s:textfield name="dataSource.signatureTimedout" onkeypress="return hm.util.keyPressPermit(event,'ten');" maxlength="4" size="4" />
																	<s:text name="hiveAp.update.images.timeout.label" /></td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</div>
								</td>
							</tr>
							<tr>
								<td>
									<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%" id="listTable">
										<tr>
											<th class="check"><input type="checkbox" id="checkAll"
												onClick="hm.util.toggleCheckAll(this);"></th>
											<th align="left"><ah:sort name="hostName" key="hiveAp.hostName" /></th>
											<th align="left"><ah:sort name="macAddress" key="hiveAp.macaddress" /></th>
											<th align="left"><ah:sort name="ipAddress" key="hiveAp.head.interface.ip" /></th>
											<th align="left"><ah:sort name="productName" key="monitor.hiveAp.model" /></th>
											<th align="left"><ah:sort name="displayVer" key="hiveAp.head.hive.os" /></th>
											<th align="left"><ah:sort name="signatureVer" key="hiveAp.head.l7.dpi.ver" /></th>
											<th align="left"><ah:sort name="signatureVer" key="hiveAp.head.l7.signature.ver" /></th>
											<th align="left"><ah:sort name="lastSignatureTime" key="hiveAp.update.time" /></th>
										</tr>
										<s:if test="%{page.size() == 0}">
											<ah:emptyList />
										</s:if>
										<tiles:insertDefinition name="selectAll" />
										<s:iterator value="page" status="status">
											<tiles:insertDefinition name="rowClass" />
											<tr class="<s:property value="%{#rowClass}"/>">
												<td class="listCheck"><ah:checkItem /></td>
												<td class="list"><s:property value="hostName" /></td>
												<td class="list"><s:property value="macAddress" />&nbsp;</td>
												<td class="list"><s:property value="ipAddress" />&nbsp;</td>
												<td class="list"><s:property value="productName" />&nbsp;</td>
												<td class="list"><s:property value="displayVerNoBuild" />&nbsp;</td>
												<td class="list"><s:property value="signatureDPIVerString" />&nbsp;</td>
												<td class="list"><s:property value="signatureVerStringWithPrefix" />&nbsp;</td>
												<td class="list"><s:property value="lastSignatureTimeString" />&nbsp;</td>
											</tr>
										</s:iterator>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
<div id="uploadFilePanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="uploadFileFrame" name="uploadFileFrame" width="0" height="0"
			frameborder="0" src="">
		</iframe>
	</div>
</div>
<script id="signatureFileList" type=" type="text/x-dot-template">
{{ for (var i = 0, l = it.list.length; i < l; i++) { }}
	<option value="{{=it.list[i].fn}}" {{ if(it.list[i].fn == it.val) { }} selected="selected" {{ } }}>{{=it.list[i].fn}}</option>
{{ } }}
</script>
<script id="signatureVerList" type=" type="text/x-dot-template">
{{ for (var i = 0, l = it.list.length; i < l; i++) { }}
	<option value="{{=it.list[i].ver}}" {{ if(it.list[i].ver == it.val) { }} selected="selected" {{ } }}>{{=it.list[i].verDisp}}</option>
{{ } }}
</script>

<script type="text/javascript">
var uploadFilePanel = null;
function createUploadFilePanel(width, height, title){
	var div = document.getElementById("uploadFilePanel");
	var iframe = document.getElementById("uploadFileFrame");
	iframe.width = width;
	iframe.height = height;
	uploadFilePanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false, constraintoviewport:true } );
	uploadFilePanel.setHeader(title);
	uploadFilePanel.render();
	div.style.display="";
	uploadFilePanel.beforeHideEvent.subscribe(requestSignatureList);
}

function requestSignatureList(){
	var url = '<s:url action="hiveApUpdate" includeParams="none"></s:url>' + "?operation=fetchSignatureList&ignore="+ + new Date().getTime();
	ajaxRequest(null, url, signatureListCallback);
}

function signatureListCallback(o){
	var selVer = document.getElementById(formName + "_selectedVersion").value;
	var selFile = document.getElementById(formName + "_selectedImage").value;
	eval("var result = " + o.responseText);
	if(result.files){
		swapInnerHTML(formName + "_selectedImage", doT.template(document.getElementById("signatureFileList").innerHTML)({"list": result.files, "val": selFile}));
	}
	if(result.vers){
		swapInnerHTML(formName + "_selectedVersion", doT.template(document.getElementById("signatureVerList").innerHTML)({"list": result.vers, "val": selVer}));
		//update latest version info
		document.getElementById(formName + "_latestVersion").value = result.vers[0].ver;
		document.getElementById("selectVersionLatestLabel").innerHTML = result.vers[0].verDisp;
	}
	requestSignatureInfo();
}

/* hack for across that works in both IE and other browsers*/
function swapInnerHTML(objID,newHTML) {
	var el=document.getElementById(objID);
	el.outerHTML=el.outerHTML.replace(el.innerHTML+'</select>',newHTML+'</select>');
}

function openUploadFilePanel(title, doOperation){
	if(null == uploadFilePanel){
		createUploadFilePanel(575,500,title);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("uploadFileFrame").style.display = "";
	}
	uploadFilePanel.show();
	var iframe = document.getElementById("uploadFileFrame");
	iframe.src ="<s:url value='hiveApFile.action' includeParams='none' />?operation="+doOperation;
}
var uploadFileIframeWindow;
</script>