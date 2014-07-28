<%@taglib prefix="s" uri="/struts-tags"%>
<%@page import="com.ah.ui.actions.hiveap.HiveApUpdateAction"%>

<style type="text/css">

#simpllyUpdateDiv .hd {
	padding: 0px;
}

#simpllyUpdateDiv .bd {
	padding: 10px;
	overflow: auto;
	font-family: sans-serif, Arial, Helvetica, Verdana;
	background-color: #FBFBFF;
}

#simpllyUpdateDiv .text_0 {
	font-family: Helvetica, Arial, sans-serif;
	font-size: 14px;
	text-align: left;
	color: #3C3C3C;
	font-weight: bold;
	margin-top: 5px;
}

#simpllyUpdateDiv .text_1 {
	font-family: Helvetica, Arial, sans-serif;
	font-size: 13px;
	text-align: left;
	color: #4F4F4F;
	font-weight: bold;
	margin-top: 5px;
}

#simpllyUpdateDiv .text_2 {
	font-family: Helvetica, Arial, sans-serif;
	font-size: 13px;
	text-align: left;
	color: #8E8E8E;
	font-weight: bold;
	margin-top: 5px;
}

#simpllyUpdateDiv .hr_style {
	height:1px;
	border:none;
	border-top:3px solid #4F4F4F;
}

#simpllyUpdateDiv .btn_update {
	height:25px;
    background-color: #0072E3;
    border: medium none;
    border-radius: 6px 6px 6px 6px;
    color: white;
    cursor: pointer;
    font-size: 12px;
    font-weight: bold;
}

#simpllyUpdateDiv .btn_cancel {
	height:25px;
    background-color: #6C6C6C;
    border: medium none;
    border-radius: 6px 6px 6px 6px;
    color: white;
    cursor: pointer;
    font-size: 12px;
    font-weight: bold;
}

#rebootWarningDiv .text_1 {
	font-family: Helvetica, Arial, sans-serif;
	font-size: 13px;
	text-align: left;
	margin-top: 5px;
}

#rebootWarningDiv .text_2 {
	font-family: Helvetica, Arial, sans-serif;
	font-size: 13px;
	text-align: left;
	color: #8E8E8E;
	font-weight: 600;
	margin-top: 5px;
}

</style>

<div id="simpllyUpdateDiv" style="display: none;">
	<div class="hd">
	</div>
	<div class="bd" id="simpllyUpdateBD">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><div class="text_0"><s:text name="geneva_06.update.panel.title"/></div></td>
							<td align="right" style="padding-right:10px;"><a href="javascript:void(0);" class="btCurrent"
								onclick="javascript: hideSimpllyUpdatePanel();" title="close"><img class="dinl" width="16px" height="16px" src="images/cancel.png" style="border:none;"/></a></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="15px"><hr class="hr_style" /></td>
			</tr>
			<tr>
				<td><div class="text_1"><s:text name="geneva_06.update.ui.devcie.num"/></div></td>
			</tr>
			<tr>
				<td height="15px"/>
			</tr>
			<tr>
				<td>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="140px"><div class="text_0"><s:text name="geneva_06.update.ui.title.configuration"/></div></td>
							<td><div class="text_1"><s:text name="geneva_06.update.ui.config.num"/></div></td>
						</tr>
						<tr>
							<td></td>
							<td><div class="text_2"><s:text name="geneva_06.update.ui.config.desc"/></div></td>
						</tr>
						<tr>
							<td colspan="2" height="5px"/>
						</tr>
						<tr>
							<td></td>
							<td><div class="text_1">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td valign="top" width="25px"><input type="checkbox" id="simplly_update_complete_config" /></td>
											<td><label for="simplly_update_complete_config"><s:text name="geneva_06.update.ui.complete.config"/></label></td>
										</tr>
										<tr>
											<td colspan="2" ><div class="text_2"><s:text name="geneva_06.update.ui.complete.config.note"/></div></td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
				<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td colspan="10" height="15px"/>
						</tr>
						<tr>
							<td width="140px" valign="top"><div class="text_0" style="padding-left:44px;"><s:text name="geneva_06.update.ui.title.hiveos"/></div></td>
							<td><table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<div id="update_image_all_num" class="text_1"><s:text name="geneva_06.update.ui.image.force.num"/></div>
										<div id="update_image_lower_num" class="text_1"><s:text name="geneva_06.update.ui.image.num"/></div>
									</td>
								</tr>
								<tr>
									<td>
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
											<tr id="simplly_image_style">
												<td style="padding-top:3px;" width="25px"><input type="checkbox" id="simplly_update_image" onclick="imageCheckBoxClick(this);"/></td>
												<td>
													<div class="text_1"><label for="simplly_update_image"><s:text name="geneva_06.update.ui.image.label"/></label></div>
												</td>
											</tr>
											<tr id="force_image_style">
												<td style="padding-top:3px;" width="25px"><input type="checkbox" id="force_update_image" onclick="imageCheckBoxClick(this);"/></td>
												<td>
													<div class="text_1"><label for="force_update_image"><s:text name="geneva_06.update.ui.image.force.label"/></label></div>
												</td>
											</tr>
											<tr id="reboot_warning_style">
												<td/>
												<td>
													<div class="text_2" id="imageNote"><s:text name="geneva_06.update.ui.image.desc"/></div>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table></td>
						</tr>
						
					</table>
				</div>
				</td>
			</tr>
			<tr>
				<td height="10px"/>
			</tr>
			<tr>
				<td height="15px"><hr class="hr_style" /></td>
			</tr>
			<tr>
				<td>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="80px"><input type="button" onclick="submitSimplifiedUpdate();" class="button" value="Update"></td>
							<td width="60px"/>
							<td width="80px"><input type="button" onclick="hideSimpllyUpdatePanel();" class="button" value="Cancel"></td>
							<td width="20px"/>
							<td><div class="text_1">
								<s:if test="%{listType == 'manageAPGuid' || hmListType == 'manageAPEx'}">
									<s:text name="geneva_06.update.ui.redirect.desc.guided"/>
								</s:if>
								<s:else>
									<s:text name="geneva_06.update.ui.redirect.desc"/>
								</s:else>
							</div></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>

<div id="rebootWarningDiv" style="display: none;">
	<div class="hd">
	</div>
	<div class="bd" id="rebootWarningBD">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<label class="text_1"><s:text name="geneva_06.update.ui.need.reboot"/></label>
							</td>
						</tr>
						<tr>
							<td>
								<input id="rebootAtOnce" type="radio" onclick="this.blur();" value="1" checked="checked" name="simplifyRebootType"/>
								<label class="text_1" for="rebootAtOnce"><s:text name="geneva_06.update.ui.reboot.type.auto"/></label>
							</td>
						</tr>
						<tr>
							<td>
								<input id="rebootManually" type="radio" onclick="this.blur();" value="2" name="simplifyRebootType"/>
								<label class="text_1" for="rebootManually"><s:text name="geneva_06.update.ui.reboot.type.manual"/></label>
							</td>
						</tr>
						<tr>
							<td height="10px"/>
						</tr>
						<tr>
							<td><label class="text_1"><s:text name="geneva_06.update.ui.reboot.submit.message"/></label></td>
						</tr>
						<tr>
							<td><label class="text_2"><s:text name="geneva_06.update.ui.reboot.important.message"/></label></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="10px"/>
			</tr>
			<tr align="right">
				<td><table>
					<tr>
						<td width="60px"><input type="button" onclick="submitDeviceRebootOpt();" class="button" value="OK"></td>
						<td width="30px"/>
						<td width="60px"><input type="button" onclick="hideRebootPanel();" class="button" value="Cancel"></td>
					</tr>
				</table></td>
			</tr>
		</table>
	</div>
</div>

<script>
//update work flow.
var RESULT_SUFFIX = "_result";

var arrayOperations = new Array();
function configUpdateOperation(){
	if( !arrayOperations || arrayOperations.length == 0){
		//no operation need execute
		return;
	}

	//no selected devices
	if(!validateApSelection()){
		clearArrayOperations();
		return false;
	}
	//get next operation from array.
	var operation = arrayOperations.shift();

	var url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	var formNameApList;
	if(fromGuidePage()){
		formNameApList = "hiveApList";
	}else{
		formNameApList = "hiveAp";
	}
	var formEle = document.getElementById(formNameApList);

	//judge all operation
	if(operation == "checkConnectStatus"){
		openWaitingPanel("Checking selected deivce connection status...");
	}else if(operation == "checkSelectedNWPolicy"){
		formEle.selectedNWPolicy.value = hm.util.getSelectedCheckItems("networkPolicys");
	}else if(operation == "checkNetworkPolicy"){
		openWaitingPanel("Checking configuration...");
		hideRebootPanel();
	}else if(operation == "updateNetworkPolicy"){
		openWaitingPanel("Updating selected network policy...");
		formEle.selectedNWPolicy.value = hm.util.getSelectedCheckItems("networkPolicys");
	}else if(operation == "getDeviceCounts"){
		openWaitingPanel("Open simplified update window...");
	}else if(operation == "getRebootDevices"){
		openWaitingPanel("Counting the count of device need reboot...");
		//close pannel
		hideSimpllyUpdatePanel();
	}else if( (operation == "uploadWizard") || 
		(operation instanceof Array && operation[0] == "uploadWizard") ){

		url = url + "&exConfigGuideFeature=uploadConfigEx"
		if(operation instanceof Array && operation[0] == "uploadWizard"){
			url = url + "&" + operation[1];
		}

		hideRebootPanel();
		openWaitingPanel("Loading configuration...");

		if(!fromGuidePage() && !fromExpressPage()){
			submitAction(operation);
			return;
		}

		operation = "uploadWizard";
	}else if(operation == "upgradeConfiguration"){
		submitAction(operation);
	}

	formEle.operation.value = operation;
	YAHOO.util.Connect.setForm(formEle);
	YAHOO.util.Connect.asyncRequest('POST', url, {success : succUpdateWorkFlow, failure : resultDoNothing, timeout: 240000}, null);
}

var succUpdateWorkFlow = function(o){
	//close waiting panel.
	closeWaitingPanel();

	eval("var result = " + o.responseText);

	//if failed display error message, and stop next operation
	if(!(result.t)){
		if(result.errorMsg){
			hm.util.displayJsonErrorNote(result.errorMsg);
		}
		clearArrayOperations();
		return;
	}

	var resultType = result.resultType;
	if(resultType == "checkConnectStatus"+RESULT_SUFFIX){
		resultForCheckConnectStatus(result);
	}else if(resultType == "checkSelectedNWPolicy"+RESULT_SUFFIX){
		resultForcheckSelectedNWPolicy(result);
	}else if(resultType == "checkNetworkPolicy"+RESULT_SUFFIX){
		resultForCheckNetworkPolicy(result);
	}else if(resultType == "updateNetworkPolicy"+RESULT_SUFFIX){
		resultForUpdateNetworkPolicy(result);
	}else if(resultType == "getDeviceCounts"+RESULT_SUFFIX){
		resultForGetDeviceCounts(result);
	}else if(resultType == "getRebootDevices"+RESULT_SUFFIX){
		resultForGetRebootDevices(result);
	}else if(resultType == "uploadWizard"+RESULT_SUFFIX){
		resultForUploadWizard(result);
	}
}

var resultDoNothing = function(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
};

function validateApSelection(){
	var cbs = document.getElementsByName('selectedIds');
	var isSelected = false;
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked) {
			isSelected = true;
			break;
		}
	}
	if(!isSelected){
		var listElement = document.getElementById('errorNoteForList');
		hm.util.reportFieldError(listElement, '<s:text name="info.selectDevice"></s:text>');
		return false;
	}
	return true;
}

function clearArrayOperations(){
	arrayOperations = null;
}

function openWaitingPanel(message){
	if(waitingPanel == null){
		createWaitingPanel();
	}
	if(waitingPanel != null){
		waitingPanel.setHeader(message);
		waitingPanel.show();
	}
}

function closeWaitingPanel(){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function resultForCheckConnectStatus(result){
	var message = result.msg;
	if(message && message != null){
		thisOperation ='checkConnectStatus';
		confirmDialog.cfg.setProperty('text', "<html><body>" + message + "</body></html>");
		confirmDialog.show();
	}else{
		//next process
		configUpdateOperation();
	}
}

function resultForcheckSelectedNWPolicy(result){
	if(result.update){
		thisOperation ='checkSelectedNWPolicy';
		confirmDialog.cfg.setProperty('text', "<html><body>" + '<s:text name="config.guid.hiveAp.update.networkPolicy" />' + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		confirmDialog.show();
	}else{
		//if no need update network policy, remove the operation "updateNetworkPolicy"
		for(var i=0; i<arrayOperations.length; i++){
			if(arrayOperations[i] == "updateNetworkPolicy"){
				arrayOperations.splice(i, 1);
				break;
			}
		}
		//next process
		configUpdateOperation();
	}
}

function resultForCheckNetworkPolicy(result){
	if (result.wn) {
		thisOperation ='checkNetworkPolicy';
		confirmDialog.cfg.setProperty('text', "<html><body>" + result.wn + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		confirmDialog.show();
	} else {
		//next process
		configUpdateOperation();
	}
}

function resultForUpdateNetworkPolicy(result){
	refreshGuidList(false);
	ignoreErrorDevice(result.ignore);
	if(result.ignoreMsg){
		hm.util.displayJsonErrorNote(details.ignoreMsg);
	}
	
	//next process
	configUpdateOperation();
}

function resultForGetDeviceCounts(result){
	totalDevice = result.all;
	lowerVerDevice = result.lowerImage;
	forceVerDevice = result.forceImage;
	if(result.imageWarning){
		document.getElementById("imageNote").innerHTML = result.imageWarning;
	}
	
	//init jsp page.
	simpllyUpdatePageInit();
	
	//this is only for express model
	var parentEl = parent.document.getElementById('uploadAnimation');
	if (parentEl) {
		parentHeight = YAHOO.util.Region.getRegion(parentEl).height;
		if (parentHeight < 500) {
			YAHOO.util.Dom.setStyle(parentEl, 'height', '450px');
		}
	}
	
	document.getElementById("simpllyUpdateBD").style.display = "";
	if(null == simpllyUpdatePannel){
		createSimpllyUpdatePanel();
	}
	simpllyUpdatePannel.show();
}

function resultForGetRebootDevices(result){
	var totalReboots = result.counts;
	if(totalReboots > 0){
		document.getElementById("deviceNeedRebootNum").innerHTML = totalReboots;
		document.getElementById("rebootWarningBD").style.display = "";
		if(null == rebootPannel){
			createRebootPanel();
		}
		rebootPannel.show();
	}else{
		//next process
		configUpdateOperation();
	}
}

function resultForUploadWizard(result){
	//clear simplly update param.
	document.getElementById("simpleUpdate").value = false;
	document.getElementById("completeCfgUpdate").value = false;
	document.getElementById("imageUpgrade").value = false;
	document.getElementById("forceImageUpgrade").value = false;
	document.getElementById("simplifiedRebootType").value = REBOOT_TYPE_AUTO;
	
	var actionErrors = result.actionErrors;
	if(actionErrors != null && actionErrors.length>0){
		for(var i=0; i<actionErrors.length; i++){
			hm.util.displayJsonErrorNote(actionErrors[i]);
		}
	}else if(typeof(refreshGuidList) != 'undefined'){
		refreshGuidList(false);
	}else if(typeof(refreshList) != 'undefined'){
		refreshList();
	}
}
	
</script>

<script>
var REBOOT_TYPE_AUTO = <%=HiveApUpdateAction.REBOOT_TYPE_AUTO%>;
var REBOOT_TYPE_MANUAL = <%=HiveApUpdateAction.REBOOT_TYPE_MANUAL%>;

var simpllyUpdatePannel = null;
var rebootPannel = null;
var parentHeight;

function fromGuidePage(){
	<s:if test="%{listType == 'manageAPGuid'}">
		return true;
	</s:if>
	<s:else>
		return false;
	</s:else>
}

function fromExpressPage(){
	<s:if test="%{hmListType == 'manageAPEx'}">
		return true;
	</s:if>
	<s:else>
		return false;
	</s:else>
}

function prepareUpdateParams(){
	//init simplly update param.
	document.getElementById("simpleUpdate").value = true;
	document.getElementById("completeCfgUpdate").value = document.getElementById("simplly_update_complete_config").checked;
	document.getElementById("imageUpgrade").value = document.getElementById("simplly_update_image").checked;
	document.getElementById("forceImageUpgrade").value = document.getElementById("force_update_image").checked;
}

function prepareRebootParams(){
	if(document.getElementById("rebootAtOnce").checked){
		document.getElementById("simplifiedRebootType").value = REBOOT_TYPE_AUTO;
	}else if(document.getElementById("rebootManually").checked){
		document.getElementById("simplifiedRebootType").value = REBOOT_TYPE_MANUAL;
	}else{
		document.getElementById("simplifiedRebootType").value = REBOOT_TYPE_AUTO;
	}
}

function submitSimplifiedUpdate(){
	prepareUpdateParams();
	configUpdateOperation();
}

function submitDeviceRebootOpt(){
	prepareRebootParams();
	configUpdateOperation();
}

/**
function rebootWarningFunc(){
	prepareUpdateParams();
	
	//close pannel
	hideSimpllyUpdatePanel();
	
	var formNameApList = null;
	if(fromGuidePage()){
		formNameApList = "hiveApList";
	}else{
		formNameApList = "hiveAp";
	}
	
	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.getElementById(formNameApList).operation.value = "getRebootDevices";
	YAHOO.util.Connect.setForm(document.getElementById(formNameApList));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succGetRebootDevices}, null);
}
**/
/**
var succGetRebootDevices = function(o){
	eval("var resObj = " + o.responseText);
	
	var totalReboots = resObj.counts;
	
	if(totalReboots <= 0){
		checkNetworkPolicy();
		return;
	}
	
	document.getElementById("deviceNeedRebootNum").innerHTML = totalReboots;
	document.getElementById("rebootWarningBD").style.display = "";
	if(null == rebootPannel){
		createRebootPanel();
	}
	rebootPannel.show();
}
**/
/**
function submitSimpllyUpdate(){
	prepareRebootParams();
	
	if(fromGuidePage()){
		if (uplaodOperationPara!=null) {
			uploadOperation(uplaodOperationPara);
		} else {
			uploadConfig();
		}
	}else if(fromExpressPage()){
		uploadConfig();
	}else{
		submitAction("simpllyUpdate");
	}
}
**/

/**
function showSimpllyUpdatePanel(){
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	
	if(waitingPanel != null){
		waitingPanel.setHeader('Open simplified update window  ...');
		waitingPanel.show();
	}
	
	var formNameApList = null;
	if(fromGuidePage()){
		formNameApList = "hiveApList";
	}else{
		formNameApList = "hiveAp";
	}
	
	url = "<s:url action='hiveAp' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.getElementById(formNameApList).operation.value = "getDeviceCounts";
	YAHOO.util.Connect.setForm(document.getElementById(formNameApList));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succGetDeviceCounts}, null);
}
**/

var totalDevice;
var lowerVerDevice;
var forceVerDevice;
/**
var succGetDeviceCounts = function(o){
	eval("var resObj = " + o.responseText);
	
	totalDevice = resObj.all;
	lowerVerDevice = resObj.lowerImage;
	forceVerDevice = resObj.forceImage;
	
	//init jsp page.
	simpllyUpdatePageInit();
	
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	
	//this is only for express model
	var parentEl = parent.document.getElementById('uploadAnimation');
	if (parentEl) {
		parentHeight = YAHOO.util.Region.getRegion(parentEl).height;
		if (parentHeight < 500) {
			YAHOO.util.Dom.setStyle(parentEl, 'height', '450px');
		}
	}
	
	document.getElementById("simpllyUpdateBD").style.display = "";
	if(null == simpllyUpdatePannel){
		createSimpllyUpdatePanel();
	}
	simpllyUpdatePannel.show();
}
**/

/**
function checkNetworkPolicy(){
	//close pannel
	hideRebootPanel();
	
	if(validateHiveAp("uploadWizard")){
		if(waitingPanel == null){
			createWaitingPanel();
		}
		if(waitingPanel != null){
			waitingPanel.setHeader("Checking configuration...");
			waitingPanel.show();
		}
		
		var formNameApList = null;
		if(fromGuidePage()){
			formNameApList = "hiveApList";
		}else{
			formNameApList = "hiveAp";
		}
		
		url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
		document.forms[formNameApList].operation.value = 'checkNetworkPolicy';
		YAHOO.util.Connect.setForm(document.getElementById(formNameApList));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succCheckNetworkPolicy}, null);
	}
}
**/

var uplaodOperationPara=null;

/**
var succCheckNetworkPolicy=function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var details = " + o.responseText);
	if (details.t){
		if (details.wn) {
			thisOperation ='uploadWizard';
			confirmDialog.cfg.setProperty('text', "<html><body>" + details.wn + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
			confirmDialog.show();
		} else {
			submitSimpllyUpdate();
		}
	} else {
		hm.util.displayJsonErrorNote(details.m);
	}
}
**/

function validateHiveAp(operation) {
	/**
	if(operation == 'uploadWizard'){
		var cbs = document.getElementsByName('selectedIds');
		var isSelected = false;
		for (var i = 0; i < cbs.length; i++) {
			if (cbs[i].checked) {
				isSelected = true;
				break;
			}
		}
		return isSelected;
	}
	**/
	return true;
}

function imageUpgradeStyleControl(){
	var simplly_image_checked = document.getElementById("simplly_update_image").checked;
	var force_image_checked = document.getElementById("force_update_image").checked;
	
	//update_image_all_num
	if(force_image_checked || lowerVerDevice == 0){
		document.getElementById("update_image_all_num").style.display = "";
	}else{
		document.getElementById("update_image_all_num").style.display = "none";
	}
	
	//update_image_lower_num
	if(lowerVerDevice > 0 && !force_image_checked){
		document.getElementById("update_image_lower_num").style.display = "";
	}else{
		document.getElementById("update_image_lower_num").style.display = "none";
	}
	
	//simplly_image_style
	if(lowerVerDevice > 0){
		document.getElementById("simplly_image_style").style.display = "";
	}else{
		document.getElementById("simplly_image_style").style.display = "none";
	}
	
	//simplly_update_image
	if(lowerVerDevice > 0 && !force_image_checked){
		document.getElementById("simplly_update_image").disabled = false;
	}else{
		document.getElementById("simplly_update_image").disabled = true;
	}
	
	//force_image_style
	if(forceVerDevice - lowerVerDevice > 0){
		document.getElementById("force_image_style").style.display = "";
	}else{
		document.getElementById("force_image_style").style.display = "none";
	}
	
	//simpllyUpdateForceImageNum
	if(document.getElementById("force_update_image").checked){
		document.getElementById("simpllyUpdateForceImageNum").innerHTML = forceVerDevice;
	}else{
		document.getElementById("simpllyUpdateForceImageNum").innerHTML = 0;
	}
	
	//Image update warning message style.
	if( (lowerVerDevice > 0 && document.getElementById("simplly_update_image").checked) ||
		(forceVerDevice > 0 && document.getElementById("force_update_image").checked) ){
		document.getElementById("reboot_warning_style").style.display = "";
	}else{
		document.getElementById("reboot_warning_style").style.display = "none";
	}
}

function imageCheckBoxClick(checkbox){
	var checkBoxId = checkbox.id;
	if(checkBoxId == "simplly_update_image"){
		if(checkbox.checked){
			document.getElementById("force_update_image").checked = false;
		}
	}else if(checkBoxId == "force_update_image"){
		if(checkbox.checked){
			document.getElementById("simplly_update_image").checked = false;
		}else if(lowerVerDevice > 0){
			document.getElementById("simplly_update_image").checked = true;
		}
	}
	imageUpgradeStyleControl();
}

function simpllyUpdatePageInit(){
	document.getElementById("simpllyUpdateTotalNum").innerHTML = totalDevice;
	document.getElementById("simpllyUpdateConfigNum").innerHTML = totalDevice;
	document.getElementById("simpllyUpdateForceImageNum").innerHTML = forceVerDevice;
	document.getElementById("simpllyUpdateImageNum").innerHTML = lowerVerDevice;
	
	if(lowerVerDevice > 0){
		document.getElementById("simplly_update_image").checked = true;
	}else{
		document.getElementById("simplly_update_image").checked = false;
	}
	
	document.getElementById("force_update_image").checked = false;
	
	imageUpgradeStyleControl();
}

function hideSimpllyUpdatePanel(){
	if(simpllyUpdatePannel != null){
		simpllyUpdatePannel.hide();
	}
}

function hideRebootPanel(){
	if(rebootPannel != null){
		rebootPannel.hide();
	}
}

function createSimpllyUpdatePanel() {
	var div = window.document.getElementById('simpllyUpdateDiv');
	simpllyUpdatePannel = new YAHOO.widget.Panel(div, { width:"620px",
												visible:false,
												fixedcenter:"contained",
												close: false,
												draggable:true,
												constraintoviewport:true,
												modal:true } );
	simpllyUpdatePannel.render();
	div.style.display = "";
	document.getElementById("simpllyUpdateBD").style.display = "";
	simpllyUpdatePannel.beforeHideEvent.subscribe(closeSimpllyUpdate);
}

function createRebootPanel() {
	var div = window.document.getElementById('rebootWarningDiv');
	rebootPannel = new YAHOO.widget.Panel(div, { width:"480px",
												visible:false,
												fixedcenter:"contained",
												close: true,
												draggable:true,
												constraintoviewport:true,
												modal:true } );
	div.style.display = "";
	document.getElementById("rebootWarningBD").style.display = "";
	rebootPannel.beforeHideEvent.subscribe(closeRebootPanel);
	rebootPannel.setHeader("Reboot Warning");
	rebootPannel.render();
}

function closeSimpllyUpdate(){
	//if(YAHOO.env.ua.ie){
	//	document.getElementById("simpllyUpdateDiv").style.display = "none";
	//}
	document.getElementById("simpllyUpdateBD").style.display = "none";
	
	// reset the parent height
	var parentEl = parent.document.getElementById('uploadAnimation');
	if (parentEl) {
		YAHOO.util.Dom.setStyle(parentEl, 'height', parentHeight+'px');
		//YAHOO.util.Dom.setStyle(document.getElementById("simpllyUpdateDiv"), 'height', '0px');
		document.getElementById("simpllyUpdateBD").height = 0;
	}
}

function closeRebootPanel(){
	document.getElementById("rebootWarningBD").style.display = "none";
}

function redirectToConfigAdvance(){
	if(fromGuidePage()){
		hideSimpllyUpdatePanel();
		openUpdateOptionPanel("Device Upload Options", "updateOptionJson");
	}else if(fromExpressPage()){
		hideSimpllyUpdatePanel();
		openUpdateOptionPanel("Device Upload Options", "updateOptionEx");
	}else{
		submitAction("upgradeConfiguration");
	}
}

function redirectToImageAdvance(){
	submitAction("upgradeImage");
}

</script>