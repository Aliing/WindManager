<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<style type="text/css">
/* custom styles */
.selectTabStyle {
	font-size: 14px;
	line-height: 30px;
	text-align: center;
	background-image: url("./images/dashboard/acenterTab.png");
    background-repeat: repeat-x;
	white-space: nowrap;
}

.selectTabStyle a {
	font-size: 15px;
	line-height: 30px;
	color: #000000;
	text-decoration: none;
}

.unSelectTabStyle {
	font-size: 14px;
	line-height: 30px;
	background-image: url("./images/dashboard/ucenterTab.png");
    background-repeat: repeat-x;
	text-align: center;
	white-space: nowrap;
}

.unSelectTabStyle a {
	font-size: 14px;
	line-height: 30px;
	color: #888888;
	text-decoration: none;
}

.unSelectTabStyle_left {
	background-image: url("./images/dashboard/uleftTab.png");
}
.selectTabStyle_left {
	background-image: url("./images/dashboard/aleftTab.png");
}
.selectTabStyle_right {
	background-image: url("./images/dashboard/arightTab.png");
}
.unSelectTabStyle_right{
	background-image: url("./images/dashboard/urightTab.png");
}

.noteInfoTitle {
	font-weight: bold;
	font-style:italic;
	font-size: 15;
	color: #666;
}
.noteInfoText {
	font-style:italic;
	font-size: 15;
	color: #666;
}
</style>
<script>
var importDialogResultOpen=3; //failure
var scanOverlay = null;
var scanOverlayResult = null;
function openScanOverlay() {
	// create filter overlay
	if(null == scanOverlay){
		var div = document.getElementById('scanPanel');
		scanOverlay = new YAHOO.widget.Panel(div, {
			width:"540px",
			visible:false,
			fixedcenter:"contained",
			draggable:true,
			modal:true,
			constraintoviewport:true,
			zIndex:1
			});
		scanOverlay.render(document.body);
		scanOverlay.moveTo(1,1);//fix scroll bar issue
		div.style.display = "";
	}
	initialScanValues();
	scanOverlay.cfg.setProperty('visible', true);
}

function openScanPanelResult(value) {
	// create filter overlay
	if(null == scanOverlayResult){
		var div = document.getElementById('scanPanelResult');
		scanOverlayResult = new YAHOO.widget.Panel(div, {
			width:"610px",
			visible:false,
			fixedcenter:"contained",
			draggable:true,
			close: false, 
			modal:true,
			constraintoviewport:true,
			zIndex:1
			});
		scanOverlayResult.render(document.body);
		scanOverlayResult.moveTo(1,1);//fix scroll bar issue
		div.style.display = "";
	}
	Get("apMappingInfoResult").innerHTML="";
	scanOverlayResult.cfg.setProperty('visible', true);
	if (value==2) {
		Get("radioForCloseResultTR").style.display="";
		Get("radioForCloseResult1").checked=true;
	} else {
		Get("radioForCloseResultTR").style.display="none";
	}
}

function hideScanOverlay(){
	if(null != scanOverlay){
		scanOverlay.cfg.setProperty('visible', false);
	}
}

function hideScanPanelResultOverlay(){
	if(null != scanOverlayResult){
		if (importDialogResultOpen ==2) {
			if (Get("radioForCloseResult1").checked) {
				importDialogResultOpen =3;
			} else {
				importDialogResultOpen =1;
			}
		}
		scanOverlayResult.cfg.setProperty('visible', false);
	}
	if (importDialogResultOpen==1) {
		hideScanOverlay();
	}
}
function initialScanValues(){
	Get("tr_import").style.display="none";
	Get("tr_serialNumber").style.display="";
	$("#td_serialNumber").attr("class","selectTabStyle");
	$("#td_serialNumber_l").attr("class","selectTabStyle_left");
	$("#td_serialNumber_r").attr("class","selectTabStyle_right");
	
	$("#td_import").attr("class","unSelectTabStyle");
	$("#td_import_l").attr("class","unSelectTabStyle_left");
	$("#td_import_r").attr("class","unSelectTabStyle_right");

	document.getElementById("apMappingInfo").value = '';
	document.getElementById("csvFile").value = '';
}

function changeTabType(index) {
	hm.util.hideFieldError();
	if (index ==1) {
		Get("enterSpan").innerHTML="<s:text name="hm.missionux.importserial.enterserial.enter"/>";
		Get("tr_import").style.display="none";
		Get("tr_serialNumber").style.display="";
		$("#td_serialNumber").attr("class","selectTabStyle");
		$("#td_serialNumber_l").attr("class","selectTabStyle_left");
		$("#td_serialNumber_r").attr("class","selectTabStyle_right");
		
		$("#td_import").attr("class","unSelectTabStyle");
		$("#td_import_l").attr("class","unSelectTabStyle_left");
		$("#td_import_r").attr("class","unSelectTabStyle_right");
	} else {
		Get("enterSpan").innerHTML="<s:text name="hm.missionux.importserial.importserial"/>";
		Get("tr_import").style.display="";
		Get("tr_serialNumber").style.display="none";
		$("#td_serialNumber").attr("class","unSelectTabStyle");
		$("#td_serialNumber_l").attr("class","unSelectTabStyle_left");
		$("#td_serialNumber_r").attr("class","unSelectTabStyle_right");
		
		$("#td_import").attr("class","selectTabStyle");
		$("#td_import_l").attr("class","selectTabStyle_left");
		$("#td_import_r").attr("class","selectTabStyle_right");
	}
}

function submitActionScan(operation) {
	if (validateScan(operation)){
		var url = "<s:url action='deviceMappingRedirector' includeParams='none' />?ignore="+new Date().getTime();
		document.forms["deviceMappingRedirector"].operation.value = operation;
		if (operation=='import'){
			var uploadEl = document.getElementById("csvFile");
			var filePath = uploadEl.value.toLowerCase();
			if (filePath.search(".csv") == -1) {
				hm.util.reportFieldError(Get("errorNoteAdd"), '<s:text name="error.formatInvalid"><s:param>The file</s:param></s:text>');
				return false;
			}
			showWaitingPanel();
			YAHOO.util.Connect.setForm(document.getElementById("deviceMappingRedirector"), true, true);
			var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {upload : succSubmitAction, failure : resultDoNothingAdd, timeout: 60000}, null);
		} else {
			showWaitingPanel();
			YAHOO.util.Connect.setForm(document.getElementById("deviceMappingRedirector"));
			var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSubmitAction, failure : resultDoNothingAdd, timeout: 60000}, null);
		}
   	}
}

var resultDoNothingAdd = function(o) {
		hideWaitingPanel();
	};
	
var succSubmitAction = function (o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		showWarnDialog("Session timeout.", "Error");
		hideWaitingPanel();
		return;
	}

	if (details.rt) {
		if (details.rt == 1) {
			importDialogResultOpen=1;
		} else if (details.rt == 2) {
			importDialogResultOpen=2;
		} else {
			importDialogResultOpen=3;
		}
	} else {
		importDialogResultOpen=3;
	}
	
	var expr_1 = new RegExp('<', 'g');
	var expr_2 = new RegExp('</', 'g');
	var expr_3 = new RegExp('>', 'g');
	var expr_4 = new RegExp('&lt;br&gt;', 'g');
	var expr_5 = new RegExp('&lt;font color="#0093D1"&gt;', 'g');
	var expr_6 = new RegExp('&lt;/font&gt;', 'g');
	
	var s = details.m;
	s = s.replace(expr_1, '&lt;');
	s = s.replace(expr_2, '&lt;/');
	s = s.replace(expr_3, '&gt;');
	s = s.replace(expr_4, '<br>');
	s = s.replace(expr_5, '<font color="#0093D1">');
	s = s.replace(expr_6, '</font>');
	
	if (details.t) {
		openScanPanelResult(importDialogResultOpen);
		Get("apMappingInfoResult").innerHTML=s;
	} else {
		openScanPanelResult(importDialogResultOpen);
		Get("apMappingInfoResult").innerHTML=s;
	}
	
	hideWaitingPanel();
}

function showWaitingPanel(){
	if (waitingPanel==null) {
		createWaitingPanel();
	}
	waitingPanel.show();
}
function hideWaitingPanel(){
	if (waitingPanel!=null) {
		waitingPanel.hide();
	}
}

function validateScan(operation){
	hm.util.hideFieldError();
	if (operation=='add'){
		document.getElementById("csvFile").value = '';
		if (Get("apMappingInfo").value.length == 0) {
			hm.util.reportFieldError(Get("errorNoteAdd"), '<s:text name="hm.missionux.importserial.error.noserial"/>');
			Get("apMappingInfo").focus();
			return false;
		}
	} else if (operation=='import') {
		document.getElementById("apMappingInfo").value = '';
		if (Get("csvFile").value.length == 0) {
			hm.util.reportFieldError(Get("errorNoteAdd"), '<s:text name="hm.missionux.importserial.error.notcsv"/>');
			Get("csvFile").focus();
			return false;
		}
	}
	return true;
}

function openRedirectorServerPage() {
	var redirectorUrl = '<s:property value="redirectorServiceURL" escape="false"/>';
	var newWin = window.open(redirectorUrl);
	newWin.focus();
}

</script>


<div id="scanPanel" style="display: none;">
<div class="hd"><s:text name="Add Devices" /></div>
<div class="bd"><s:form action="deviceMappingRedirector" enctype="multipart/form-data"
	id="deviceMappingRedirector" name="deviceMappingRedirector" method="POST">
	<s:hidden name="operation" />
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
	
		<tr>
			<td>
				<span id="enterSpan"><s:text name="hm.missionux.importserial.enterserial.enter"/></span> <s:text name="hm.missionux.importserial.enterimport.title"/>
			</td>
		</tr>
		
		<tr>
			<td height="10px">

			</td>
		</tr>
		<tr>
			<td>
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td width="10px" id="td_serialNumber_l">
						<td width="120px" id="td_serialNumber"><a href="javascript:void(0);" onclick="changeTabType(1);"><s:text name="hm.missionux.importserial.enterserial"/></a></td> 
						<td width="10px" id="td_serialNumber_r">
						<td width="5px">
						<td width="10px" id="td_import_l">
						<td width="80px" id="td_import"><a href="javascript:void(0);" onclick="changeTabType(2);"><s:text name="hm.missionux.importserial.importserial"/></a></td>
						<td width="10px" id="td_import_r">
						<td width="220px"></td>
					</tr>
				</table>
			</td>
		</tr>
		
		<tr>
			<td><table><tr><td id="errorNoteAdd"></td></tr></table></td>
		</tr>
	
		<tr id="tr_serialNumber">
			<td>
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
		
					<tr>
						<td>
							<s:textarea rows="10" cols="80" id="apMappingInfo" name="apMappingInfo" style="resize:none; width:450px;" />
						</td>
					</tr>
					<tr>
						<td height="10px"></td>
					</tr>
					<tr>
						<td class="noteInfo"><s:text name="hm.missionux.importserial.enterserial.note"/></td>
					</tr>
					<tr>
						<td height="20px"></td>
					</tr>
					
					<tr>
						<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><input type="button" name="ignore" value="Add"
										class="button" onClick="submitActionScan('add');"></td>
									<td><input type="button" name="ignore" value="Close"
										class="button" onClick="hideScanOverlay();"></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr id="tr_import">
			<td>
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td>
							<s:file id="csvFile" name="upload" size="65" />
						</td>
					</tr>
					<tr>
						<td height="15px"/>
					</tr>
					<tr>
						<td class="noteInfoTitle">
							Note:
						</td>
					</tr>
					<tr>
						<td class="noteInfoText">
							<s:text name="glasgow_05.hm.missionux.importserial.importserial.note"/>
							<s:property value="csvImportFormatInfo" escape="false"/>
							<s:text name="glasgow_05.hm.missionux.importserial.importserial.note.end"/>
						</td>
					</tr>
					<tr>
						<td height="20px"></td>
					</tr>
					<tr>
						<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><input type="button" name="ignore" value="Add"
										class="button" onClick="submitActionScan('import');"></td>
									<td><input type="button" name="ignore" value="Close"
										class="button" onClick="hideScanOverlay();"></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</s:form></div>
</div>

<div id="scanPanelResult" style="display: none;">
<div class="hd"><s:text name="Result" /></div>
<div class="bd">
	<table cellspacing="0" cellpadding="0" border="0" width="100%" class="editBox">
		<tr>
			<td>
				<div  style="overflow: auto;height:300px">
				<span id="apMappingInfoResult" />
				</div>
			</td>
		</tr>	
	</table>
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr><td height="10px"/></tr>
		<tr id="radioForCloseResultTR">
			<td>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr >
						<td><s:radio label="Gender" name="radioForCloseResult" list="#{'1':''}" /></td>
						<td><s:text name="geneva_08.device.inventory.result.keep"/></td>
					</tr>
					 <tr> <td height="5px"/><tr>
					<tr>
						<td><s:radio label="Gender" name="radioForCloseResult" list="#{'2':''}" /></td>
						<td><s:text name="geneva_08.device.inventory.result.move"/></td>
					</tr>
					 <tr> <td height="10px"/><tr>
				</table>
			</td>
		</tr>
		<tr>
			<td><input type="button" name="ignore" value="Close"
				class="button" onClick="hideScanPanelResultOverlay();"></td>
		</tr>
	</table>
</div>
</div>
