<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script
	src="<s:url value="/js/hm.util.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<tiles:insertDefinition name="flashHeader" />

<script type="text/javascript"> 
function initConfirmDialog() {
	confirmDialog =
     new YAHOO.widget.SimpleDialog("confirmDialog",
              { width: "350px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal:true,
                close: true,
                text: "<html><body>This operation will remove the selected item(s).<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>",
                icon: YAHOO.widget.SimpleDialog.ICON_WARN,
                constraintoviewport: true,
                buttons: [ { text:"Yes", handler:handleYes, isDefault:true },
                           { text:"&nbsp;No&nbsp;", handler:handleNo } ]
              } );
     confirmDialog.setHeader("Confirm");
     confirmDialog.render(document.body);
     confirmDialog.cancelEvent.subscribe(confirmCancelHandler);
}
function initWarnDialog() {
	warnDialog =
     new YAHOO.widget.SimpleDialog("warnDialog",
              { width: "350px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal:true,
                close: true,
                icon: YAHOO.widget.SimpleDialog.ICON_ALARM,
                constraintoviewport: true,
                buttons: [ { text:"OK", handler:handleNo, isDefault:true } ]
              } );
     warnDialog.setHeader("Warning");
     warnDialog.render(document.body);
}
function showWarnDialog(message, headMessage){
	if (warnDialog == null){
		initWarnDialog();
	}
	if(headMessage != null){
		 warnDialog.setHeader(headMessage);
	}else{
		 warnDialog.setHeader("Warning");
	}
	warnDialog.cfg.setProperty("text",message);
	warnDialog.show();
}
function showInfoDialog(info){
	if (infoDialog == null)
	{
		infoDialog = new YAHOO.widget.SimpleDialog("infoDlg", {
		width: "350px",
		fixedcenter:true,
		modal:true,
	    visible:false,
		draggable:true,
		constraintoviewport: true,
		icon: YAHOO.widget.SimpleDialog.ICON_INFO,
		buttons: [ { text:"&nbsp;OK&nbsp;", handler:handleNo, isDefault:true } ]});
		infoDialog.setHeader("Information");
		infoDialog.render(document.body);
	}
	infoDialog.cfg.setProperty("text",info);
	infoDialog.show();
}
function confirmCancelHandler(){
}
var handleYes = function() {
    this.hide();
    doContinueOper();
};
var handleNo = function() {
    this.hide();
};
</script> 

<script type="text/javascript"> 
var swfu;
function initSwfUpload(){
	var settings = {
			flash_url : "<s:url value="/js/swfupload/swfupload.swf"  includeParams="none"/>",
			upload_url: "<s:property value="%{webAppHttpUrl}" />"+"<s:url value="mapSettings.action"  includeParams="none"/>",
			use_query_string: true,
			file_size_limit : "10 MB",
			file_types : "*.jpg;*.png",
			file_types_description : "Web Image Files",
			file_upload_limit : 0,
			file_queue_limit : 5,
			custom_settings : {
				JSESSIONID: "<%=request.getSession().getId()%>",
				isSecure: <%=request.isSecure()%>,
				path: "<%=request.getContextPath()%>",
				progressTarget : "fsUploadProgress",
				validateBeforeStarting: validateFileSize,
				uploadSuccessCallback: imageUploadSuccess,
				uploadErrorCallback: imageUploadFailed,
				MESSAGE_EXCEEDS_SIZE_LIMIT: "<s:text name="hm.planning.config.image.exceeds.limit" />"
			},
			debug: false,

			// Button settings
			button_width: "76",
			button_height: "24",
			button_image_url: "<s:url value="/images/upload_btns.png"  includeParams="none"/>",
			button_placeholder_id: "spanButtonPlaceHolder",
			button_window_mode: SWFUpload.WINDOW_MODE.OPAQUE,
			
			// The event handler functions are defined in handlers.js
			file_queued_handler : fileQueued,
			file_queue_error_handler : fileQueueError,
			file_dialog_complete_handler : fileDialogComplete,
			upload_start_handler : uploadStart,
			upload_progress_handler : uploadProgress,
			upload_error_handler : uploadError,
			upload_success_handler : uploadSuccess,
			upload_complete_handler : uploadComplete,
			queue_complete_handler : queueComplete	// Queue plugin event
		};
    if(document.getElementById('spanButtonPlaceHolder')) {
		swfu = new SWFUpload(settings);
    }
}

var bigFiles = [];
var pollTimeouted;
function validateFileSize(file){
	// dynamically set the value before upload
	swfu.setPostParams({"operation": "uploadImage", "domainMapId":document.forms[formName].id.value});
	var maxSize = <s:property value="%{imageMaxSize}" />;
	clearTimeout(pollTimeouted);
	if(file.size > maxSize){
		bigFiles.push(file);
	}
	pollTimeouted = setTimeout("startUploadImage()", 300);
}

function doContinueOper() {
	if (bigFiles.length > 0) {
		swfu.startUpload();
		bigFiles = [];
	}
}

function startUploadImage() {
	if(bigFiles.length > 0){
		hm.util.confirmUploadMapImage(bigFiles);
	}else{
		swfu.startUpload();
	}
}

function handleNo(){
	this.hide();
	confirmCancelHandler();
}

function confirmCancelHandler(){
	if (bigFiles.length > 0) {
		for(var i=0; i<bigFiles.length; i++){
			swfu.cancelUpload(bigFiles[i].id);
		}
		bigFiles = [];
		//upload file that size is small
		swfu.startUpload();
	}
}

function imageUploadSuccess(serverData){
	eval("var result = " + serverData);
	if(result.uploaded){
		var existed = false;
		var selector = document.getElementById("mapImage");
		for(var i=0; i<selector.options.length; i++){
			if(result.image == selector.options[i].value){
				selector.options[i].selected = true;
				existed = true;
				break;
			}
		}
		if(!existed){
			addOption(selector, result.image, result.image, true);
		}
		existed = false;
		selector = document.getElementById("mapReviewImage");
		for(var i=0; i<selector.options.length; i++){
			if(result.imageFull == selector.options[i].value){
				selector.options[i].selected = true;
				existed = true;
				break;
			}
		}
		if(!existed){
			addOption(selector, result.imageFull, result.image, false);
		}
		// show uploaded image
		if(null != imageReviewPanel && imageReviewPanel.cfg.getProperty("visible")==true){
			showImage(selector);
		}
		toggleMapImage(document.getElementById("mapImage"));
		if(result.sucMsg){
			showInfoDialog(result.sucMsg);
		}
	}else if(result.error){
		showWarnDialog(result.error);
	}
}

function imageUploadFailed(){
	Get("traditionalTr1").style.display = "";
}

function addOption(selector, value, text, selected){
	if(null == selector){
		return;
	}
	var option = new Option(text, value, selected, selected);
	try{
		selector.add(option, null); // DOM
	}catch(e){
		selector.add(option); // IE
	}
}

function traditionalUpoad(){
	if(!validateUploadImage()){
		return;
	}
	document.forms[formName].operation.value = "uploadImage";
	document.forms[formName].domainMapId.value = document.forms[formName].id.value;
	YAHOO.util.Connect.setForm(formName, true, true);
	var url = "<s:url action='mapSettings' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest("post", url, {upload : traditionalUpoadSuccess}, null);
}

function traditionalUpoadSuccess(o){
	imageUploadSuccess(o.responseText);
}

function showTraditionalContent(){
	Get("importTr").style.display = "";
	Get("importBtnTr").style.display = "";
	Get("importImageBtn").style.display = "none";
	Get("traditionalTr1").style.display = "none";
	Get("traditionalTr2").style.display = "";
}
</script> 

<script>
var formName = 'mapSettings';

var planningOnly = false;
<s:if test="%{planningOnly}">
	planningOnly = true;
</s:if>

function submitAction(operation){
	if (validate(operation)){
		showProcessing();
		document.forms[formName].operation.value = operation;
		document.forms[formName].domainMapId.value = document.forms[formName].id.value;
		//avoid upload file when submit form
		document.getElementById("imagedata").disabled = true;
	    document.forms[formName].submit();
	}
}

function validate(operation){
	if(operation == 'updateMap'){
		if(!validateMapEdition()){
			return false;
		}
	}
	return true;
}

function validateMapEdition(){
	var mapName = document.getElementById("mapName");
	var message = hm.util.validateNameWithBlanks(mapName.value, '<s:text name="hm.topology.init.map.name" />');
	if (message != null) {
		hm.util.reportFieldError(mapName, message);
		mapName.focus();
    	return false;
	}
	// unsupport '|'
	if(mapName.value.indexOf("|")>-1){
		hm.util.reportFieldError(mapName, "<s:text name='error.value.internal.used'><s:param>'|'</s:param></s:text>");
		mapName.focus();
    	return false;
	}
	
	var mapImage = document.getElementById("mapImage");
	if (mapImage.value=="") {
		var sizeX = document.getElementById("sizeX");
		var sizeY = document.getElementById("sizeY");
		if (sizeX.value.length > 0) {
			var message = hm.util.validateNumberRange(sizeX.value, '<s:text name="hm.planning.config.background.width" />', 1, 100000000);
			if (message != null) {
				hm.util.reportFieldError(sizeX, message);
				sizeX.focus();
				return false;
			}
			var message = hm.util.validateNumberRange(sizeY.value, '<s:text name="hm.planning.config.background.height" />',1 , 100000000);
			if (message != null) {
				hm.util.reportFieldError(sizeX, message);
				sizeY.focus();
				return false;
			}
			if(parseInt(sizeX.value/sizeY.value)>10 || parseInt(sizeY.value/sizeX.value)>10){
				hm.util.reportFieldError(sizeX, "The aspect ratio should not be more than 10x.");
				sizeX.focus();
				return false;
			}
		}
	}
	return true;
}

function validateUploadImage(){
	var uploadImageElement = document.getElementById("imagedata");
	if(uploadImageElement.value.toLowerCase().search(".jpg") == -1
		&& uploadImageElement.value.toLowerCase().search(".png") == -1){
		hm.util.reportFieldError(uploadImageElement, '<s:text name="error.map.background.image.format"></s:text>');
		uploadImageElement.focus();
		return false;
	}
	return true;
}

function showImportTr(){
	document.getElementById("importTr").style.display = "";
	document.getElementById("importBtnTr").style.display = "";
	document.getElementById("importImageBtn").style.display = "none";
	Get("traditionalTr1").style.display = "";
	Get("traditionalTr2").style.display = "none";
}

function hideImportTr(){
	document.getElementById("importTr").style.display = "none";
	document.getElementById("importBtnTr").style.display = "none";
	document.getElementById("importImageBtn").style.display = "";
	Get("traditionalTr1").style.display = "none";
	Get("traditionalTr2").style.display = "none";
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

function onLoadPage(){
	toggleMapImage(document.getElementById("mapImage"));
	window.onresize = adjustContentHeight;
	adjustContentHeight();
	// init swf uploader
	initSwfUpload();
	// Overlay for images review
	createImageReviewPanel();
	document.getElementById("mapName").focus();
	var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
	if(!hasRequestedVersion){
		showTraditionalContent();
	}
}

function adjustContentHeight(){
	var wh = YAHOO.util.Dom.getViewportHeight();
	var vpY = YAHOO.util.Dom.getY("content");
	YAHOO.util.Dom.setStyle("content", "height", (wh-vpY) + "px");
	
}

var imageReviewPanel = null;
function createImageReviewPanel() {
	var div = document.getElementById('imageReviewPanel');
	imageReviewPanel = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:5
		});
	imageReviewPanel.render(document.body);
	div.style.display = "";
}

function showReviewPanel(){
	if (imageReviewPanel != null) {
		imageReviewPanel.cfg.setProperty('visible', true);
	}
}

function hidePreviewPanel(){
	if (imageReviewPanel != null) {
		imageReviewPanel.cfg.setProperty('visible', false);
	}
}

function showImage(imageElement){
	var imageName = imageElement.options[imageElement.selectedIndex].text;
	var value = imageElement.options[imageElement.selectedIndex].value;
	var contentEl = document.getElementById("imageContent");
	var img = document.createElement("img");
	img.src = value;
	img.width = "230";
	img.height = "165";
	hm.util.replaceChildren(contentEl, img);
}

function toggleMapImage(select) {
	hm.util.toggleHideElement("sizeImg", select.value.length == 0);
	hm.util.toggleHideElement("sizeBlank", select.value.length > 0);
}

function toggleWidthUnit(cb) {
	var select = document.getElementById('apElevationUnit');
	if (cb.selectedIndex == select.selectedIndex) {
		return;
	}
	select.options[cb.value - 1].selected = true;
	select = document.getElementById('mapBlankUnit');
	select.options[cb.value - 1].selected = true;
	toggleLengthUnit(cb);
}
function toggleBlankUnit(cb) {
	var select = document.getElementById('apElevationUnit');
	if (cb.selectedIndex == select.selectedIndex) {
		return;
	}
	select.options[cb.value - 1].selected = true;
	select = document.getElementById('mapWidthUnit');
	select.options[cb.value - 1].selected = true;
	toggleLengthUnit(cb);
}
function toggleApElevationUnit(cb) {
	var select = document.getElementById('mapWidthUnit');
	if (cb.selectedIndex == select.selectedIndex) {
		return;
	}
	select.options[cb.value - 1].selected = true;
	select = document.getElementById('mapBlankUnit');
	select.options[cb.value - 1].selected = true;
	toggleLengthUnit(cb);
}
function toggleLengthUnit(cb) {
	var width = document.getElementById('mapWidth');
	var elevation = document.getElementById('apElevation');
	var sizeX = document.getElementById('sizeX');
	var sizeY = document.getElementById('sizeY');
	toggleLength(width, cb);
	toggleLength(elevation, cb);
	toggleLength(sizeX, cb);
	toggleLength(sizeY, cb);
}
function toggleLength(input, cb) {
	if (input.value.length != 0) {
		if (cb.value == 2) {
			input.value /= 0.3048;
		} else {
			input.value *= 0.3048;
		}
		input.value = Math.round(input.value * 100000) / 100000;
	}
}

function selectImage(imageName){
	var imageSelector = document.getElementById("mapImage");
	if(imageSelector){
		imageSelector.value = imageName;
		toggleMapImage(imageSelector);
	}
	hidePreviewPanel();
}

function createPlanningPanel(width, height){
	var div = document.getElementById("planningPanel");
	width = width || 300;
	height = height || 200;
	var iframe = document.getElementById("planning_tool");
	iframe.width = width;
	iframe.height = height;
	planningPanel = new YAHOO.widget.Panel(div, {modal:true, fixedcenter:"contained", visible:false, constraintoviewport:true } );
	planningPanel.render(document.body);
	div.style.display="";
	planningPanel.beforeHideEvent.subscribe(clearPlanningData);
}
function clearPlanningData(){
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("planning_tool").style.display = "none";
	}
}
</script>

<div id="content">
	<s:form action="mapSettings" enctype="multipart/form-data" method="post" >
		<s:hidden name="domainMapId"></s:hidden>
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
								<input type="button" id="ignore" name="ignore" value="Update"
									class="button" onClick="submitAction('updateMap');" <s:property value="writeDisabled" />>
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
					<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="600px">
						<tr>
							<td style="padding: 6px 5px 5px 5px;">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td class="labelT1" width="160px"><s:text name="hm.topology.init.map.name" /> <font
										color="red"><s:text name="*" /></font></td>
									<td width="160px"><s:textfield id="mapName"
										size="25" name="mapName"
										onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
										maxlength="32" /></td>
									<td></td>
								</tr>
								<tr style="display: none;">
									<td class="labelT1"><s:text name="hm.topology.init.map.icon" /></td>
									<td style="padding-left: 10px;"><s:select name="mapIcon" cssStyle="width: 158px;" list="%{mapIcons}" listKey="key" listValue="value" /></td>
									<td></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="hm.topology.init.map.apoperationEnvironment" /></td>
									<td><s:select id="mapEnv"
										name="mapEnv" list="%{enumMapEnv}" listKey="key"
										listValue="value" cssStyle="width: 158px;" /></td>
									<td></td>
								</tr>
								<tr>
									<td class="labelT1" nowrap="nowrap"><s:text
										name="hm.topology.init.map.background" /></td>
									<td><s:select id="mapImage" name="mapImage" value="%{selectedImage}" 
										list="%{mapImages}" headerKey="" headerValue="None" cssStyle="width: 158px;" onchange="toggleMapImage(this)" 
										onkeyup="toggleMapImage(this)"></s:select>
									</td>
									<td>
										<input id="importImageBtn" type="button" name="ignore" value="Import" 
											class="button" onClick="showImportTr();" <s:property value="writeDisabled" />>
										<input id="importImageBtn" type="button" name="ignore" value="Preview" 
											class="button" onClick="showReviewPanel();" <s:property value="writeDisabled" />>
									</td>
								</tr>
								<s:if test="%{!writeDisable4Struts}">
								<tr id="importTr" style="display: none;">
									<td></td>
									<td colspan="2">
										<div class="flash" id="fsUploadProgress"></div> 
										<!--<div id="divStatus">0 Files Uploaded</div> -->
									</td>
								</tr>
								<tr id="importBtnTr" style="display: none;">
									<td></td>
									<td colspan="2">
										<div> 
											<span id="spanButtonPlaceHolder"></span>
											<input type="button" name="ignore" value="Cancel" 
												class="button" onClick="hideImportTr();" <s:property value="writeDisabled" />>
										</div>
									</td>
								</tr>
								<tr id="traditionalTr1" style="display: none;">
									<td></td>
									<td colspan="2" style="height: 24px;">
										<a href="##" onclick="showTraditionalContent();"><s:text name="hm.topology.init.map.background.traditional" /></a>
									</td>
								</tr>
								<tr id="traditionalTr2" style="display: none; ">
									<td></td>
									<td colspan="2">
										<div style="background-color: #ddd; border: 1px solid #888; padding: 2px; margin-top:5px;">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr><td></td><td class="noteInfo"><s:text name="hm.topology.init.map.background.note" /></td></tr>
											 <tr><td></td><td><s:file id="imagedata" onchange="traditionalUpoad();" name="imagedata" size="42px" /></td></tr> 
										</table></div>
									</td>
								</tr>
								</s:if>
								<tr id="sizeImg" style="display: none">
									<td class="labelT1" nowrap="nowrap"><s:text name="hm.topology.init.map.width.optional" /></td>
									<td><s:textfield id="mapWidth"
										size="25" name="mapWidth" maxlength="16"
										onkeypress="return hm.util.keyPressPermit(event,'tendot');" /></td>
									<td style="padding: 0 0 0px 4px;"><select id="mapWidthUnit"
										name="mapWidthUnit" onchange="toggleWidthUnit(this)"
										onkeyup="toggleWidthUnit(this)">
										<option value="1">meters</option>
										<option value="2">feet</option>
									</select></td>
								</tr>
								<tr id="sizeBlank" style="display: none">
									<td class="labelT1" nowrap="nowrap"><s:text name="hm.topology.init.map.size" /></td>
									<td nowrap="nowrap"><s:textfield
										id="sizeX" size="8" name="sizeX" maxlength="16"
										onkeypress="return hm.util.keyPressPermit(event,'tendot');" />&nbsp;x&nbsp;<s:textfield
										id="sizeY" size="8" name="sizeY" maxlength="16"
										onkeypress="return hm.util.keyPressPermit(event,'tendot');" /></td>
									<td style="padding: 0 0 0px 4px;"><select id="mapBlankUnit"
										name="mapBlankUnit" onchange="toggleBlankUnit(this)"
										onkeyup="toggleBlankUnit(this)">
										<option value="1">meters</option>
										<option value="2">feet</option>
									</select></td>
								</tr>
								<tr>
									<td class="labelT1" nowrap="nowrap"><s:text name="hm.topology.init.map.installationHeight" /></td>
									<td><s:textfield
										id="apElevation" size="25" name="apElevation" maxlength="16"
										onkeypress="return hm.util.keyPressPermit(event,'tendot');" /></td>
									<td style="padding: 0 0 0px 4px;"><select
										id="apElevationUnit" name="apElevationUnit"
										onchange="toggleApElevationUnit(this)"
										onkeyup="toggleApElevationUnit(this)">
										<option value="1">meters</option>
										<option value="2">feet</option>
									</select></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>

<div id="imageReviewPanel" style="display: none;">
<div class="hd">Preview Images</div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td style="padding: 8px 0px 0px 8px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td class="noteInfo">
								<s:text name="hm.topology.init.map.review.note"></s:text>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="padding: 6px 5px 5px 5px;">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td><s:select size="10" id="mapReviewImage" cssStyle="width: 150px;" 
							onchange="showImage(this);" ondblclick="selectImage(this.options[this.selectedIndex].text);"
							list="%{mapReviewImages}" listKey="key" listValue="value"></s:select></td>
						<td id="imageContent"></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>