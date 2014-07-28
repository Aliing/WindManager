<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.admin.PlanToolConfig"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<tiles:insertDefinition name="flashHeader" />

<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/swfupload.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script
	src="<s:url value="/js/swfupload/swfupload.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/swfupload/swfupload.queue.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/swfupload/fileprogress.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/swfupload/handlers.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>


<style>
a.wallColorPicker {
	display: block;
	width: 20px;
	height: 20px;
	background: #ffffff;
	border: 1px solid #000;
}
</style>

<script type="text/javascript"> 
var swfu;
function initSwfUpload(){
	var settings = {
			flash_url : "<s:url value="/js/swfupload/swfupload.swf"  includeParams="none"/>",
			upload_url: "<s:property value="%{webAppHttpUrl}" />"+"<s:url value="planTool.action"  includeParams="none"/>",
			use_query_string: true,
			post_params: {"operation": "upload", "domainId":<s:property value="%{domainId}" />},
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
var validateFileSize = function(file){
	var maxSize = <s:property value="%{imageMaxSize}" />;
	clearTimeout(pollTimeouted);
	if(file.size > maxSize){
		bigFiles.push(file);
	}
	pollTimeouted = setTimeout("startUploadImage()", 300);
}

function startUploadImage() {
	if(bigFiles.length > 0){
		hm.util.confirmUploadMapImage(bigFiles);
	}else{
		swfu.startUpload();
	}
}

function doContinueOper() {
	if (bigFiles.length > 0) {
		swfu.startUpload();
		bigFiles = [];
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
		var selector = document.getElementById(formName + "_dataSource_backgroundImg");
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
		showImage(selector);
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
</script>

<script>
var formName = 'planTool';

var BACKGROUND_TYPE_IMAGE = <%=PlanToolConfig.BACKGROUND_TYPE_IMAGE%>;
var BACKGROUND_TYPE_NO_IMAGE = <%=PlanToolConfig.BACKGROUND_TYPE_NO_IMAGE%>;
var RADIO_24GHZ = <%=PlanToolConfig.RADIO_24GHZ%>;
var RADIO_5GHZ = <%=PlanToolConfig.RADIO_5GHZ%>;
var MODEL_110 = <%=HiveAp.HIVEAP_MODEL_110%>;

var isFromTopology = <s:property value="%{fromTopology}" />;

function traditionalUpoad(){
	if(!validateUploadImage()){
		return;
	}
	document.forms[formName].operation.value = "upload";
	YAHOO.util.Connect.setForm(formName, true, true);
	var url = "<s:url action='planTool' includeParams='none' />";
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

function submitAction(operation){
	if (validate(operation)){
		showProcessing();
	    if (isFromTopology) {
	    	top.confirmed = true;
	    }
		//avoid upload file when submit form
		document.getElementById("imagedata").disabled = true;
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	var noMap = "<s:property value="%{mapConfigSectionStyle}"/>";
	if (noMap == "none") {
		return true;
	}
	if (operation == 'update') {
		var plannedMapCheckbox = Get("plannedMap");
		if(plannedMapCheckbox.checked){
			if(!validateMapImage()){
				return false;
			}
			if(!validateMapNoImage()){
				return false;
			}
		}
//		document.getElementById("imageFile").value = '';
	} else if (operation == 'planning') {
		var plannedMapCheckbox = Get("plannedMap");
		if(!plannedMapCheckbox.checked){
			hm.util.reportFieldError(plannedMapCheckbox, '<s:text name="error.map.planning.required"></s:text>');
			return false;
		}
		if(!validateMapImage()){
			return false;
		}
		if(!validateMapNoImage()){
			return false;
		}
//		document.getElementById("imageFile").value = '';
	}

	if(!validateHeight()) {
		return false;
	}
	
	return true;
}

function validateHeight() {
   var installHeightEl = document.getElementById(formName + "_dataSource_installHeight");
    if(installHeightEl) {
        var height = installHeightEl.value;
        if(isNaN(height)) {
            hm.util.reportFieldError(installHeightEl, 
                    '<s:text name="error.common.invalid"><s:param><s:text name="hm.planning.config.installationHeight"/></s:param></s:text>');
            installHeightEl.focus();
            return false;
        }
        
        var mapInstallUnit = document.getElementById("mapInstallUnit");
        if(mapInstallUnit.value == 1) {
            // meter
            if(height > 15) {
                hm.util.reportFieldError(installHeightEl, 
                        '<s:text name="error.map.exceed.limit"><s:param>'+ 15 +'</s:param></s:text>');
                installHeightEl.focus();
                return false;
            }
        } else {
            // feet
            var limitVlaue = 15 / 0.3048;
            if(height > limitVlaue) {
                hm.util.reportFieldError(installHeightEl, 
                        '<s:text name="error.map.exceed.limit"><s:param>'+ (limitVlaue - 0.0001).toFixed(4) +'</s:param></s:text>');
                installHeightEl.focus();
                return false;
            }
        }
    }
    return true;
}

function validateUploadImage(){
	var uploadImageElement = document.getElementById("imagedata");
	var displayelement = document.getElementById("importTr");
	var inputString = uploadImageElement.value;
	if(null == inputString.match(/.*\.jpg$|.*\.png$/ig)){
		hm.util.reportFieldError(uploadImageElement, '<s:text name="error.map.background.image.format"></s:text>');
		uploadImageElement.focus();
		return false;
	}
	return true;
}

function validateMapImage(){
	if(Get(formName + "_dataSource_backgroundType"+BACKGROUND_TYPE_IMAGE).checked){
		var mapEl = Get(formName + "_dataSource_backgroundImg");
		if(!mapEl.value){
			hm.util.reportFieldError(mapEl, '<s:text name="error.requiredField"><s:param><s:text name="hm.planning.config.background.label1" /></s:param></s:text>');
			return false;
		}
	}
	return true;
}

function validateMapNoImage(){
	if(Get(formName + "_dataSource_backgroundType"+BACKGROUND_TYPE_NO_IMAGE).checked){
		var widthEl = Get(formName + "_dataSource_actualWidth");
		var heightEl = Get(formName + "_dataSource_actualHeight");
		var message = hm.util.validateNumberRange(widthEl.value, '<s:text name="hm.planning.config.background.width" />',
                <s:property value="1" />,
                <s:property value="100000000" />);
		if (message != null) {
			hm.util.reportFieldError(widthEl, message);
			widthEl.focus();
			return false;
		}
		var message = hm.util.validateNumberRange(heightEl.value, '<s:text name="hm.planning.config.background.height" />',
                <s:property value="1" />,
                <s:property value="100000000" />);
		if (message != null) {
			hm.util.reportFieldError(widthEl, message);
			heightEl.focus();
			return false;
		}
		if(parseInt(widthEl.value/heightEl.value)>10 || parseInt(heightEl.value/widthEl.value)>10){
			hm.util.reportFieldError(widthEl, "The aspect ratio should not be more than 10x.");
			widthEl.focus();
			return false;
		}
	}
	return true;
}

function toggleLengthUnit(cb) {
	var lengthUnit = document.getElementById("lengthUnit");
	var mapInstallUnit = document.getElementById("mapInstallUnit");

	lengthUnit.value = mapInstallUnit.value = cb.value;
	
	var sizeX = document.getElementById(formName + '_dataSource_actualWidth');
	var sizeY = document.getElementById(formName + '_dataSource_actualHeight');
	toggleLength(sizeX, cb);
	toggleLength(sizeY, cb);
	
	var installHeight = document.getElementById(formName + "_dataSource_installHeight");
	toggleLength(installHeight, cb);
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

function backgroundTypeChange(typeValue){
	if(typeValue == BACKGROUND_TYPE_NO_IMAGE){
		Get("backgroundNoImageTr").style.display = "";
		Get("backgroundImageTr").style.display = "none";
	}else if(typeValue == BACKGROUND_TYPE_IMAGE){
		Get("backgroundImageTr").style.display = "";
		Get("backgroundNoImageTr").style.display = "none";
	}
}

function requestChannels(){
//	updateLayout();
//	var url = '<s:url action="planTool" includeParams="none"></s:url>' + "?ignore="+ + new Date().getTime();
//	document.forms[formName].operation.value = "requestChannels";
//	ajaxRequest(formName, url, processChannels);
}

function processChannels(o){
	eval("var data = " + o.responseText);
	if(data){
		if(data.wifi0){
			var wifi0Channel = Get(formName + "_dataSource_wifi0Channel");
			var selectVal = wifi0Channel.value;
			wifi0Channel.options.length = 0;
			for(var i=0; i<data.wifi0.length; i++){
				addOption(wifi0Channel, data.wifi0[i].value, data.wifi0[i].text, data.wifi0[i].value == selectVal)
			}
		}
		if(data.wifi1){
			var wifi1Channel = Get(formName + "_dataSource_wifi1Channel");
			var selectVal = wifi1Channel.value;
			wifi1Channel.options.length = 0;
			for(var i=0; i<data.wifi1.length; i++){
				addOption(wifi1Channel, data.wifi1[i].value, data.wifi1[i].text, data.wifi1[i].value == selectVal)
			}
		}
	}
}

function showImportTr(){
	Get("importTr").style.display = "";
	Get("importBtnTr").style.display = "";
	Get("importImageBtn").style.display = "none";
	Get("traditionalTr1").style.display = "";
	Get("traditionalTr2").style.display = "none";
}

function hideImportTr(){
	Get("importTr").style.display = "none";
	Get("importBtnTr").style.display = "none";
	Get("importImageBtn").style.display = "";
	Get("traditionalTr1").style.display = "none";
	Get("traditionalTr2").style.display = "none";
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

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

function onLoadPage(){
	// update layout (for 110HiveAP)
//	updateLayout();
	// init swf uploader
	initSwfUpload();
	// Overlay for images review
	createImageReviewPanel();
	// bind wall color selector
	bindWallColorSelector();
	<s:if test="%{fromTopology}">
		hm.util.resizeIframeSize("planning_tool", true, parent.window.planningPanel);
	</s:if>
	var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
	if(!hasRequestedVersion){
		showTraditionalContent();
	}
	if (isFromTopology && top.confirmed) {
		setTimeout("top.planningPanel.hide()", 1500); // seconds
	}
}

function updateLayout(){
	var apModelEl = document.getElementById(formName + "_dataSource_defaultApType");
	var radioEl = document.getElementById(formName + "_radio");
	var radio_2_row = document.getElementById("radio_2");
	var radio_5_row = document.getElementById("radio_5");
	var radioCell = document.getElementById("radio_select");
	
	if(parseInt(apModelEl.value) == MODEL_110){
		// update layout
		if(radioEl.value == RADIO_24GHZ){
			var radio_2_row = document.getElementById("radio_2");
			//radio_2_row.cells[0].replaceChild(radioEl,radio_2_row.cells[0].firstChild);
			hm.util.replaceChildren(radio_2_row.cells[0], radioEl);
			radio_5_row.style.display = "none";
			radio_2_row.style.display = "";
		}else if(radioEl.value == RADIO_5GHZ){
			var radio_5_row = document.getElementById("radio_5");
			//radio_5_row.cells[0].replaceChild(radioEl,radio_5_row.cells[0].firstChild);
			hm.util.replaceChildren(radio_5_row.cells[0], radioEl);
			radio_2_row.style.display = "none";
			radio_5_row.style.display = "";
		}
		radioEl.focus();
	}else{
		hm.util.replaceChildren(radioCell, radioEl);
		hm.util.replaceChildren(radio_2_row.cells[0], document.createTextNode("<s:text name="hiveAp.if.24G" />"));
		hm.util.replaceChildren(radio_5_row.cells[0], document.createTextNode("<s:text name="hiveAp.if.5G" />"));
		radio_5_row.style.display = "";
		radio_2_row.style.display = "";
	}
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

function selectImage(imageName){
	var imageSelector = Get(formName + "_dataSource_backgroundImg");
	if(imageSelector){
		imageSelector.value = imageName;
	}
	hidePreviewPanel();
}

function plannedMapEnable(checked){
	Get("plannedMapSection").style.display = checked? "" : "none";
}


function bindWallColorSelector(){
	updateWallColorSelector();
	registerWallColorSelector();
}

function registerWallColorSelector(){
	var els = document.getElementById("wallColorSelector").getElementsByTagName("a");
	for(var i=0; i<els.length; i++){
		var el = els[i];
		el.onclick = (function(o){return function(){colorPicker.show(colorSelect, o);}})({el:el, index:i})
	}
}

function colorSelect(color, options){
	var el = options.el;
	var index = options.index;
	el.style.backgroundColor = color;
	colorPicker.hide();
	var hiddenElement;
	switch(index){
	case 0:
		hiddenElement = document.getElementById(formName + "_dataSource_wallColorBookshelf");
		break;
	case  1:
		hiddenElement = document.getElementById(formName + "_dataSource_wallColorCubicle");
		break;
	case 2:
		hiddenElement = document.getElementById(formName + "_dataSource_wallColorDryWall");
		break;
	case 3:
		hiddenElement = document.getElementById(formName + "_dataSource_wallColorBrickWall");
		break;
	case 4:
		hiddenElement = document.getElementById(formName + "_dataSource_wallColorConcrete");
		break;
	case 5:
		hiddenElement = document.getElementById(formName + "_dataSource_wallColorElevatorShaft");
		break;
	case 6:
		hiddenElement = document.getElementById(formName + "_dataSource_wallColorThinDoor");
		break;
	case 7:
		hiddenElement = document.getElementById(formName + "_dataSource_wallColorThickDoor");
		break;
	case 8:
		hiddenElement = document.getElementById(formName + "_dataSource_wallColorThinWindow");
		break;
	case 9:
		hiddenElement = document.getElementById(formName + "_dataSource_wallColorThickWindow");
		break;
	}
	if(hiddenElement){
		hiddenElement.value = color;
	}
}

function updateWallColorSelector(){
	var els = document.getElementById("wallColorSelector").getElementsByTagName("a");
	var hiddenElement;
	for(var i=0; i<els.length; i++){
		switch(i){
		case 0:
			hiddenElement = document.getElementById(formName + "_dataSource_wallColorBookshelf");
			break;
		case  1:
			hiddenElement = document.getElementById(formName + "_dataSource_wallColorCubicle");
			break;
		case 2:
			hiddenElement = document.getElementById(formName + "_dataSource_wallColorDryWall");
			break;
		case 3:
			hiddenElement = document.getElementById(formName + "_dataSource_wallColorBrickWall");
			break;
		case 4:
			hiddenElement = document.getElementById(formName + "_dataSource_wallColorConcrete");
			break;
		case 5:
			hiddenElement = document.getElementById(formName + "_dataSource_wallColorElevatorShaft");
			break;
		case 6:
			hiddenElement = document.getElementById(formName + "_dataSource_wallColorThinDoor");
			break;
		case 7:
			hiddenElement = document.getElementById(formName + "_dataSource_wallColorThickDoor");
			break;
		case 8:
			hiddenElement = document.getElementById(formName + "_dataSource_wallColorThinWindow");
			break;
		case 9:
			hiddenElement = document.getElementById(formName + "_dataSource_wallColorThickWindow");
			break;
		}
		if(null != hiddenElement){
			els[i].style.backgroundColor = hiddenElement.value;
			hiddenElement = null; // reset
		}
	}
}
</script>
<style type="text/css">
div.colorPickerContainer {
	width: 176px;
	border: 1px solid #999;
	border-right: 1px solid #333;
	border-bottom: 1px solid #333;
	padding: 4px 2px;
	background-color: #FFF;
	position: absolute;
}

div.colorPickerContainer .pickerSection {
	margin: 0;
	padding: 0;
	list-style-type: none;
}

div.colorPickerContainer .pickerColor {
	width: 18px;
	height: 18px;
	line-height: 16px;
	padding: 2px;
	float: left;
}

div.colorPickerContainer .pickerColorCell {
	border: 1px solid #999;
	text-decoration: none;
	display: block;
}

div.colorPickerContainer .pickerColorCell:hover {
	border: 1px solid #CCFFFF;
}
</style>
<script type="text/javascript">
	var colorPicker = {
		colorPanel: null,
		options: null,
		callbackFn: null,
		_timer: null,
		colors: [['#000000','Black'],['#993300','Sienna'],['#333300','DarkOliveGreen'],['#003300','DarkGreen'],
		 		['#003366','DarkSlateBlue'],['#000080','Navy'],['#333399','Indigo'],['#333333','DarkSlateGray'],['#800000','DarkRed'],
		 		['#FF6600','DarkOrange'],['#808000','Olive'],['#008000','Green'],['#008080','Teal'],['#0000FF','Blue'],
		 		['#666699','SlateGray'],['#808080','DimGray'],['#FF0000','Red'],['#FF9900','SandyBrown'],['#99CC00','YellowGreen'],
		 		['#339966','SeaGreen'],['#33CCCC','MediumTurquoise'],['#3366FF','RoyalBlue'],['#800080','Purple'],['#999999','Gray'],
		 		['#FF00FF','Magenta'],['#FFCC00','Orange'],['#FFFF00','Yellow'],['#00FF00','Lime'],['#00FFFF','Cyan'],
		 		['#00CCFF','DeepSkyBlue'],['#993366','DarkOrchid'],['#C0C0C0','Silver'],['#FF99CC','Pink'],['#FFCC99','Wheat'],
		 		['#FFFF99','LemonChiffon'],['#CCFFCC','PaleGreen'],['#CCFFFF','PaleTurquoise'],['#99CCFF','LightBlue'],['#CC99FF','Plum'],
		 		['#FFFFFF','White']],
		init: function(){
			var container = document.createElement("div");
			container.className = "colorPickerContainer";
			var colorPicker = document.createElement("ul");
			colorPicker.className = "pickerSection";
			//var previewer = document.createElement("div");
			//previewer.className = "previewerSection";
			container.appendChild(colorPicker);
			//container.appendChild(previewer);
			var _this = this;
			for(var i=0; i<this.colors.length; i++){
				var li = document.createElement("li");
				li.className = "pickerColor";
				li.innerHTML = "<a href='javascript: void 0;' cellColor='"+this.colors[i][0]+"' class='pickerColorCell' style='background-color:"+this.colors[i][0]+";' title='"+this.colors[i][1]+"'>&nbsp;</a>";
				colorPicker.appendChild(li);
				li.firstChild.onclick = function(event){
					event = event || window.event;
					var cellColor = YAHOO.util.Dom.getAttribute(this, "cellColor");
					_this.options.event = event;
					_this.callbackFn(cellColor, _this.options);
					return false;
				}
			}
			document.body.appendChild(container);
			container.onmouseout = function(){
				_this._timer = setTimeout(function(){_this.colorPanel.style.display = "none";}, 1000);
			};
			container.onmouseover = function(){
				if(_this._timer){
					clearTimeout(_this._timer);
				}
			};
			container.style.display = "none";
			this.colorPanel = container;
		},
		show: function(callbackFn, options){
			this.callbackFn = callbackFn;
			this.options = options || {};
			if(null == this.colorPanel){
				this.init();
			}
			//get document width/height before show color pane to avoid include its width/height.
			var dw = document.documentElement.scrollWidth || document.body.scrollWidth;
			var dh = document.documentElement.scrollHeight || document.body.scrollHeight;
			if(this._timer){
				clearTimeout(this._timer);
			}
			this.colorPanel.style.display = "";
			var targetEl = options.el || document.body;
			var x = YAHOO.util.Dom.getX(targetEl);
			var y = YAHOO.util.Dom.getY(targetEl);
			var locateX, locateY;
			if((x + this.colorPanel.offsetWidth) > dw){
				locateX = x - this.colorPanel.offsetWidth - 5;
			}else{
				locateX = x + targetEl.offsetWidth + 5;
			}
			if((y + this.colorPanel.offsetHeight) > dh){
				locateY = y - this.colorPanel.offsetHeight + targetEl.offsetHeight;
			}else{
				locateY = y;
			}
			YAHOO.util.Dom.setXY(this.colorPanel, [locateX, locateY]);
		},
		hide: function(){
			if(this._timer){
				clearTimeout(this._timer);
			}
			this.colorPanel.style.display = "none";
		}
	}
</script>

<div id="content"><s:form action="planTool"
	enctype="multipart/form-data" method="post">
	<s:if test="%{fromTopology}">
		<s:hidden name="operation" />
		<s:hidden name="fromTopology" />
	</s:if>
	<s:hidden name="dataSource.wallColorBookshelf"></s:hidden>
	<s:hidden name="dataSource.wallColorCubicle"></s:hidden>
	<s:hidden name="dataSource.wallColorDryWall"></s:hidden>
	<s:hidden name="dataSource.wallColorBrickWall"></s:hidden>
	<s:hidden name="dataSource.wallColorConcrete"></s:hidden>
	<s:hidden name="dataSource.wallColorElevatorShaft"></s:hidden>
	<s:hidden name="dataSource.wallColorThinDoor"></s:hidden>
	<s:hidden name="dataSource.wallColorThickDoor"></s:hidden>
	<s:hidden name="dataSource.wallColorThinWindow"></s:hidden>
	<s:hidden name="dataSource.wallColorThickWindow"></s:hidden>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{!fromTopology}">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
		</s:if>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" id="ignore" name="ignore"
						value="Update" class="button" onClick="submitAction('update');"
						<s:property value="writeDisabled" />></td>
					<td><s:if test="%{!fromTopology}">
						<input type="button" id="ignore" name="ignore"
							value="Start Planning" style="width: 100px" class="button"
							onClick="submitAction('planning');"
							<s:property value="writeDisabled" />>
					</s:if></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="610px">
				<tr>
					<td height="4px"></td>
				</tr>
				<tr style="display:<s:property value="%{mapConfigSectionStyle}"/>;">
					<td
						style="display:<s:property value="%{fromTopology?'none':''}"/>;">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td style="padding-left: 5px; padding-bottom: 4px;"><s:checkbox
								name="plannedMap" id="plannedMap"
								onclick="plannedMapEnable(this.checked);" /></td>
							<td><s:text name="hm.planning.config.enableplannedmap" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr style="display:<s:property value="%{mapConfigSectionStyle}"/>;">
					<td>
					<div
						style="padding: 0 5px; display:<s:property value="%{plannedMapStyle}"/>;"
						id="plannedMapSection">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td height="4px"></td>
						</tr>
						<tr>
							<td style="padding-left: 10px;">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="240px"><s:radio
												onclick="backgroundTypeChange(this.value);" label="Gender"
												name="dataSource.backgroundType" list="%{backgroundType0}"
												listKey="key" listValue="value" /></td>
											<td><s:radio onclick="backgroundTypeChange(this.value);"
												label="Gender" name="dataSource.backgroundType"
												list="%{backgroundType1}" listKey="key" listValue="value" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td height="5px"></td>
								</tr>
								<tr id="backgroundImageTr"
									style="display:<s:property value="%{backgroundImageStyle}"/>;">
									<td>
									<div
										style="border: 1px solid #999999; padding: 5px; width: 515px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="144px"><label><s:text
												name="hm.planning.config.background.label1" /></label></td>
											<td width="155px"><s:select
												name="dataSource.backgroundImg" list="%{backgroundImages}"
												cssStyle="width: 150px;" /></td>
											<s:if test="%{!writeDisable4Struts}">
											<td><input id="importImageBtn" type="button"
												name="ignore" value="Import" class="button"
												onClick="showImportTr();"
												<s:property value="writeDisabled" />> <input
												id="importImageBtn" type="button" name="ignore"
												value="Preview" class="button" onClick="showReviewPanel();"
												<s:property value="writeDisabled" />></td>
											</s:if>
										</tr>
										<s:if test="%{!writeDisable4Struts}">
										<tr id="importTr" style="display: none;">
											<td></td>
											<td colspan="2">
											<div class="flash" id="fsUploadProgress"></div>
											<!--<div id="divStatus">0 Files Uploaded</div> --></td>
										</tr>
										<tr id="importBtnTr" style="display: none;">
											<td></td>
											<td colspan="2">
											<div><span id="spanButtonPlaceHolder"></span> <input
												type="button" name="ignore" value="Cancel" class="button"
												onClick="hideImportTr();"
												<s:property value="writeDisabled" />></div>
											</td>
										</tr>
										<tr id="traditionalTr1" style="display: none;">
											<td></td>
											<td colspan="2" style="height: 24px;"><a href="##"
												onclick="showTraditionalContent();"><s:text
												name="hm.topology.init.map.background.traditional" /></a></td>
										</tr>
										<tr id="traditionalTr2" style="display: none;">
											<td></td>
											<td colspan="2">
											<div
												style="background-color: #ddd; border: 1px solid #888; padding: 2px; margin-top: 5px;">
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td></td>
													<td class="noteInfo"><s:text
														name="hm.topology.init.map.background.note" /></td>
												</tr>
												<tr>
													<td></td>
													<td><s:file id="imagedata"
														onchange="traditionalUpoad();" name="imagedata"
														size="42px" /></td>
												</tr>
											</table>
											</div>
											</td>
										</tr>
										</s:if>
									</table>
									</div>
									</td>
								</tr>
								<tr id="backgroundNoImageTr"
									style="display:<s:property value="%{backgroundNoImageStyle}"/>;">
									<td>
									<div
										style="border: 1px solid #999999; padding: 5px; width: 515px;">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="144px"><label><s:text
												name="hm.planning.config.background.label" /></label></td>
											<td><s:textfield name="dataSource.actualWidth"
												value="%{dataSource.actualWidthString}" size="6"
												maxlength="8"
												onkeypress="return hm.util.keyPressPermit(event,'tendot');" />
											&nbsp;x&nbsp; <s:textfield name="dataSource.actualHeight"
												value="%{dataSource.actualHeightString}" size="6"
												maxlength="8"
												onkeypress="return hm.util.keyPressPermit(event,'tendot');" />
											</td>
											<td style="padding: 0 0 0px 12px;"><s:select
												list="%{mapUnits}" id="lengthUnit"
												name="dataSource.lengthUnit" cssStyle="width:72px;"
												onchange="toggleLengthUnit(this)"
												listKey="key" listValue="value"></s:select></td>
										</tr>
									</table>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="5px"></td>
						</tr>
						<tr>
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="labelT1" width="160px"><label><s:text
										name="hm.planning.config.apoperationEnvironment" /></label></td>
									<td><s:select name="dataSource.mapEnv"
										list="%{enumMapEnv}" listKey="key" listValue="value"
										cssStyle="width: 150px;" /></td>
								</tr>
								<tr>
									<td class="labelT1"><label><s:text
										name="hm.planning.config.installationHeight" /></label></td>
									<td><s:textfield name="dataSource.installHeight"
										value="%{dataSource.installHeightString}" size="8"
										maxlength="8"
										onkeypress="return hm.util.keyPressPermit(event,'tendot');" />
									&nbsp;<s:select list="%{mapUnits}" id="mapInstallUnit"
										name="dataSource.lengthUnit1" cssStyle="width:72px;"
										onchange="toggleLengthUnit(this)"
										listKey="key" listValue="value"></s:select></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="5px"></td>
						</tr>
						<tr>
							<td class="sepLine" colspan="3"><img
								src="<s:url value="/images/spacer.gif"/>" height="1"
								class="dblk" /></td>
						</tr>
						<tr>
							<td height="5px"></td>
						</tr>
					</table>
					</div>
					</td>
				</tr>
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelT1" width="145px"><label><s:text
								name="hm.planning.config.countrycode" /></label></td>
							<td nowrap><s:select name="dataSource.countryCode"
								onchange="requestChannels();" list="%{countryCodeValues}"
								listKey="key" listValue="value" cssStyle="width: 470px;" />&nbsp;&nbsp;&nbsp;</td>
						</tr>
						<tr>
							<td class="labelT1"><label><s:text
								name="hm.planning.config.defaultaptype" /></label></td>
							<td><s:select name="dataSource.defaultApType"
								onchange="requestChannels();" list="%{enumHiveApType}"
								listKey="key" listValue="value" cssStyle="width: 470px;" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<s:if test="%{planningOnly}">
					<tr>
						<td>
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="labelT1" width="145px"><s:text
									name="topology.map.bg.opacity" /></td>
								<td><s:select name="dataSource.bgMapOpacity"
									list="%{opacityValues}" listKey="id" listValue="value" />&nbsp;%
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<s:text
									name="topology.map.opacity" />&nbsp;<s:select
									name="dataSource.heatMapOpacity" list="%{opacityValues}"
									listKey="id" listValue="value" />&nbsp;%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;walls&nbsp;<s:select
									name="dataSource.wallsOpacity" list="%{opacityValues}"
									listKey="id" listValue="value" />&nbsp;%</td>
							</tr>
						</table>
						</td>
					</tr>
				</s:if>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td style="padding-left: 10px;">
					<fieldset id="wallColorSelector" style="width: 510px"><legend><s:text
						name="hm.planning.config.wall.tag" /></legend>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr align="left">
							<th width="25%"><label><s:text
								name="hm.planning.config.wall.name" /></label></th>
							<th width="13%" style="padding-left: 0;"><label><s:text
								name="hm.planning.config.wall.color" /></label></th>
							<th width="12%"><label><s:text
								name="hm.planning.config.wall.type" /></label></th>
							<th width="25%"><label><s:text
								name="hm.planning.config.wall.name" /></label></th>
							<th width="13%" style="padding-left: 0;"><label><s:text
								name="hm.planning.config.wall.color" /></label></th>
							<th width="12%"><label><s:text
								name="hm.planning.config.wall.type" /></label></th>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 0px;"><s:text
								name="hm.planning.config.wall.color.bookshelf" /></td>
							<td><a title="Click here to change the color"
								class="wallColorPicker" href="javascript:void 0;"> </a></td>
							<td><s:checkbox name="dataSource.wallTypeBookshelf" /></td>
							<td class="labelT1" style="padding-left: 0px;"><s:text
								name="hm.planning.config.wall.color.cubicle" /></td>
							<td><a title="Click here to change the color"
								class="wallColorPicker" href="javascript:void 0;"> </a></td>
							<td><s:checkbox name="dataSource.wallTypeCubicle" /></td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 0px;"><s:text
								name="hm.planning.config.wall.color.dryWall" /></td>
							<td><a title="Click here to change the color"
								class="wallColorPicker" href="javascript:void 0;"> </a></td>
							<td><s:checkbox name="dataSource.wallTypeDryWall" /></td>
							<td class="labelT1" style="padding-left: 0px;"><s:text
								name="hm.planning.config.wall.color.brickWall" /></td>
							<td><a title="Click here to change the color"
								class="wallColorPicker" href="javascript:void 0;"> </a></td>
							<td><s:checkbox name="dataSource.wallTypeBrickWall" /></td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 0px;"><s:text
								name="hm.planning.config.wall.color.concrete" /></td>
							<td><a title="Click here to change the color"
								class="wallColorPicker" href="javascript:void 0;"> </a></td>
							<td><s:checkbox name="dataSource.wallTypeConcrete" /></td>
							<td class="labelT1" style="padding-left: 0px;"><s:text
								name="hm.planning.config.wall.color.elevatorShaft" /></td>
							<td><a title="Click here to change the color"
								class="wallColorPicker" href="javascript:void 0;"> </a></td>
							<td><s:checkbox name="dataSource.wallTypeElevatorShaft" /></td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 0px;"><s:text
								name="hm.planning.config.wall.color.thinDoor" /></td>
							<td><a title="Click here to change the color"
								class="wallColorPicker" href="javascript:void 0;"> </a></td>
							<td><s:checkbox name="dataSource.wallTypeThinDoor" /></td>
							<td class="labelT1" style="padding-left: 0px;"><s:text
								name="hm.planning.config.wall.color.thickDoor" /></td>
							<td><a title="Click here to change the color"
								class="wallColorPicker" href="javascript:void 0;"> </a></td>
							<td><s:checkbox name="dataSource.wallTypeThickDoor" /></td>
						</tr>
						<tr>
							<td class="labelT1" style="padding-left: 0px;"><s:text
								name="hm.planning.config.wall.color.thinWindow" /></td>
							<td><a title="Click here to change the color"
								class="wallColorPicker" href="javascript:void 0;"> </a></td>
							<td><s:checkbox name="dataSource.wallTypeThinWindow" /></td>
							<td class="labelT1" style="padding-left: 0px;"><s:text
								name="hm.planning.config.wall.color.thickWindow" /></td>
							<td><a title="Click here to change the color"
								class="wallColorPicker" href="javascript:void 0;"> </a></td>
							<td><s:checkbox name="dataSource.wallTypeThickWindow" /></td>
						</tr>
					</table>
					</fieldset>
					</td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>

<div id="imageReviewPanel" style="display: none;">
<div class="hd">Preview Images</div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td style="padding: 6px 5px 5px 5px;">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td style="padding: 8px 0px 0px 0px;">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="noteInfo"><s:text
									name="hm.topology.init.map.review.note"></s:text></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td><s:select size="10" id="mapReviewImage"
							cssStyle="width: 150px;" onchange="showImage(this);"
							ondblclick="selectImage(this.options[this.selectedIndex].text);"
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