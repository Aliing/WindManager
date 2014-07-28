<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/hm.css"  includeParams="none"/>" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/te.css"  includeParams="none"/>" />
<script type="text/javascript"
	src="<s:url value="/js/jquery.min.js" includeParams="none"/>"></script>
<script type="text/javascript"
	src="<s:url value="/js/mvc/ae.js" includeParams="none"/>"></script>
<script type="text/javascript"
	src="<s:url value="/js/pluins/jquery.validate.js" includeParams="none"/>"></script>
<script
	src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script>
var formName = 'onBoardUISetting';

function onLoadPage() 
{
	<s:if test="%{operation == 'edit'}">
	</s:if>

}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		if ("create" + '<s:property value="lstForward"/>'!=operation && operation != 'cancel' + '<s:property value="lstForward"/>') {
 			document.forms[formName].target = "_parent";
 		}
		document.forms[formName].submit();
	}
}
function validate(operation) {
	
	return true;
}
function insertAtCursor(myValue) {
	myField=document.getElementById('welcomeText');
	myValue="$"+"{"+myValue+"}";
	//IE support
	if (document.selection) {
	myField.focus();
	sel = document.selection.createRange();
	sel.text = myValue;
	sel.select();
	}
	//MOZILLA/NETSCAPE support
	else if (myField.selectionStart || myField.selectionStart == '0') {
	var startPos = myField.selectionStart;
	var endPos = myField.selectionEnd;
	// save scrollTop before insert www.keleyi.com
	var restoreTop = myField.scrollTop;
	myField.value = myField.value.substring(0, startPos) + myValue + myField.value.substring(endPos, myField.value.length);
	if (restoreTop > 0) {
	myField.scrollTop = restoreTop;
	}
	myField.focus();
	myField.selectionStart = startPos + myValue.length;
	myField.selectionEnd = startPos + myValue.length;
	} else {
	myField.value += myValue;
	myField.focus();
	}
	} 
	

function addWordsToWelcomeText(addWord){
	alert($("#welcomeText").val());
	var newText="$"+"{"+addWord+"}";
	alert(newText);
	$("#welcomeText").val(newText);
	
}
function onboardUIPageNameChanged(pageName){
	
	//alert(pageName.value);
}


function checkLast(str){  
    var flag=false;  
    var ext=str.split('.')[str.split('.').length-1];  
    if(ext=='png'||ext=='jpg'||ext=='bmp'||ext=='jpeg'||ext=='gif'){  
        flag=true;  
    }  
    return flag;  
}  

function checkPerm(obj){  
    if(!checkLast(obj.value.toLowerCase())){  
        alert("error!");  
        document.getElementByIdx_x("permitSpan").innerHTML='<s:file name="permitFile" size="14" id="permitCheck" onchange="checkPerm(this);"></s:file>';  
    }  
}  

function requestResetContinueOper(){
	
	var url = '<s:url action="onBoardUISetting" includeParams="none"></s:url>' + "?operation=reset"
			+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : fillResetData}, null);
}

var fillResetData = function(o){
	hideProcessing();
	
}

function requestPreviewPage(){
	showProcessing();
	var uiPageId=document.getElementById("onboardUIPageId").value;
	
	var uiPageShowMethod=document.getElementById("onboardUIPreviewMethod").value;
	
	var url = '<s:url action="onBoardUISetting" includeParams="none"></s:url>' + "?operation=openPreviewPage"
			+"&onboardUIPageId="+uiPageId
			+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : writeIFrameData}, null);
	
}

function hideProcessing() {
	hm.util.hide('processing');
}

function requestUIReset( ){
	
	var cancelBtn = function(){
		this.hide();
	};
	
	var mybuttons = [ { text:"OK", handler: function(){this.hide();submitAction("reset");} }, 
                      { text:"Cancel", handler: cancelBtn, isDefault:true} ];
    var requestUIResetMsg = "<html><body>"+'<s:text name ="warning.config.onboarduisetting.reset" />' +"</body></html>";
    var dlg = userDefinedConfirmDialog(requestUIResetMsg, mybuttons, "Warning");
    dlg.show();
}

var writeIFrameData = function(o){
	hideProcessing();
	eval("var details = " + o.responseText);

	if(!details.success){
		return;
	}

	var uiPageShowMethod=document.getElementById("onboardUIPreviewMethod").value;
	
	var data=details.iFrameData;
	$("a").remove(".container-close");
	//if(previewPagePanel==null)	{
		if(uiPageShowMethod==1){
			createPreviewPagePanel(1024*0.7-1, 768*0.7-1);
		}else{
			createPreviewPagePanel(768*0.7-1,887*0.7-1);
		}
	
	
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		Get("previewPageFrame").style.display = "";
	}
	

	
	previewPagePanel.setHeader(details.previewPageName);
	Get("previewPageFrame").contentDocument.write(data);
	previewPagePanel.show();
	
}

function requestChangeImage(filePath,replaceImgId){
	showProcessing();
 	 if(replaceImgId=='logoImage-id'){
		$("#uploadImageName").val("logoImage"); 
	}else if(replaceImgId=='verMainImage-id'){
		$("#uploadImageName").attr("value","verMainImage");
	}else if(replaceImgId=='horMainImage-id'){
		$("#uploadImageName").attr("value","horMainImage"); 
	}  
	var url = "<s:url action='onBoardUISetting' includeParams='none' />?" 
		+"ignore="+new Date().getTime();
	<s:if test="%{jsonMode}">
	url += '&operation=previewImage';
</s:if>
<s:else>
	document.getElementById(formName).operation.value = 'previewImage';

</s:else>
	YAHOO.util.Connect.setForm(document.getElementById(formName),true,true);
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {upload: refreshImage}, null);

}

var refreshImage = function(o){
	hideProcessing();
	eval("var details = " + o.responseText);
	var freshImageName=details.freshImageName;
	if(freshImageName=='logoImage'){
		$("#logoImage-id").attr("src","data:image/gif;base64,"+details.logoImage);
	}else if(freshImageName=='verMainImage'){
		$("#verMainImage-id").attr("src","data:image/gif;base64,"+details.verMainImage);
	}else if(freshImageName=='horMainImage'){
		$("#horMainImage-id").attr("src","data:image/gif;base64,"+details.horMainImage);
	}
	
}



function insertPageContext() {

	document.writeln('<td class="crumb" nowrap><a href="<s:url action="onBoardUISetting" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a>  ');


}
</script>
<style type="text/css">
.preselect {
	width: 128px;
}

.mybutton {
	width: 110px;
}

.myLabelT1 {
	padding: 8px 0px 5px 10px;
	vertical-align: top;
	width: 200px;
}
</style>
<s:if test="%{easyMode && lastExConfigGuide!=null}">
	<div id="content" style="padding-left: 15px">
</s:if>
<s:else>
	<div id="content">
</s:else>
<s:form action="onBoardUISetting" enctype="multipart/form-data"
	method="POST">
	<s:hidden name="logoImageFile" />
	<s:hidden name="verMainImageFile" />
	<s:hidden name="horMainImageFile" />
	<s:hidden name="uploadImageName" id="uploadImageName" />
<%-- 	<s:hidden name="dataSource.clientInfoTitle"></s:hidden>
	<s:hidden name="dataSource.userNameLabel"></s:hidden>
	<s:hidden name="dataSource.ownerShipLabel"></s:hidden>
	<s:hidden name="dataSource.cidLabel"></s:hidden>
	<s:hidden name="dataSource.byodLabel"></s:hidden>
	<s:hidden name="dataSource.targetUrl"></s:hidden>
	<s:hidden name="dataSource.cidText"></s:hidden>
	<s:hidden name="dataSource.byodText"></s:hidden>
	<s:hidden name="dataSource.agreementText"></s:hidden>
	<s:hidden name="dataSource.welcomeText"></s:hidden> --%>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><input type="button" id="ignore" name="ignore"
							value="Save" class="button" onClick="submitAction('customize');"
							<s:property value="writeDisabled" />></td>
						<td><input type="button" id="ignore" name="ignore"
							value="Reset" class="button" onClick="requestUIReset();"
							<s:property value="writeDisabled" />></td>
						<td><input type="button" id="ignore" name="ignore"
							value="Preview" class="button" onClick="requestPreviewPage();"
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
				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
					width="680px">
					<tr>
						<td height="10">
							<%-- add this password dummy to fix issue with auto complete function --%>
					</tr>
					<tr>
						<td class="labelT1" width="160px"><label> <s:text
									name="config.security.onboarduisetting.pageName.label" />
						</label></td>
						<td><s:select name="onboardUIPageId" cssClass="preselect"
								value="%{onboardUIPageId}" id="onboardUIPageId"
								list="%{enumOnboardUIPageName}" listKey="key" listValue="value"
								onchange="onboardUIPageNameChanged(this);" /></td>
					</tr>

					<tr>
						<td class="labelT1" width="160px"><label> <s:text
									name="config.security.onboarduisetting.PreviewMethod.label" />
						</label></td>
						<td><s:select name="onboardUIPreviewMethod"
								cssClass="preselect" value="%{onboardUIPreviewMethod}"
								id="onboardUIPreviewMethod" list="%{enumOnboardUIPreviewMethod}"
								listKey="key" listValue="value"
								onchange="onboardUIPageNameChanged(this);" /></td>
					</tr>

					<tr>
						<td colspan="2" class="labelT1" width="460px"><label>
								<s:text name="config.security.onboarduisetting.selectIcon.label" />
						</label></td>

					</tr>
					<tr>
						<td colspan="2" class="labelT1"><s:file id="logoImageFile"
								name="logoImageFile" size="60"
								onChange="requestChangeImage(this.value,'logoImage-id')" /></td>
					</tr>

					<tr>
						<td colspan="2" class="labelT1"><img id="logoImage-id"
							src="data:image/gif;base64,${dataSource.logoImage}" width="112px"
							height="42px" alt="Aerohive Networks" /></td>
					</tr>

					<tr>
						<td colspan="2" class="labelT1" width="460px"><label>
								<s:text
									name="config.security.onboarduisetting.selectVerMainImg.label" />
						</label></td>
					</tr>

					<tr>
						<td colspan="2" class="labelT1"><s:file id="verMainImageFile"
								name="verMainImageFile" size="60"
								onChange="requestChangeImage(this.value,'verMainImage-id')" /></td>
					</tr>
					<tr>
						<td colspan="2" class="labelT1"><img id="verMainImage-id"
							src="data:image/gif;base64,${dataSource.verMainImage}"
							width="315px" height="110px" alt="Aerohive Networks" /></td>
					</tr>

					<tr>
						<td colspan="2" class="labelT1" width="460px"><label>
								<s:text
									name="config.security.onboarduisetting.selectHorMainImg.label" />
						</label></td>
					</tr>
					<tr>
						<td colspan="2" class="labelT1"><s:file id="horMainImageFile"
								name="horMainImageFile" size="60"
								onChange="requestChangeImage(this.value,'horMainImage-id')" /></td>
					</tr>

					<tr>
						<td colspan="2" class="labelT1"><img id="horMainImage-id"
							src="data:image/gif;base64,${dataSource.horMainImage}"
							width="200px" height="250px" alt="Aerohive Networks" /></td>
					</tr>

					<tr>
						<td height="10"></td>
					</tr>

				</table>

				<table class="editBox" cellspacing="0" cellpadding="0" border="0"
					width="680px">

					<tr>
						<td height="10"></td>
					</tr>

					<tr>
						<td class="myLabelT1" width="200px"><s:text
								name="config.security.onboarduisetting.ClientInfoTitle.label" />
						</td>
						<td style="padding: 6px 0px 0px 0px;"><s:textfield
								name="dataSource.clientInfoTitle" size="60" id="clientInfoTitle"
								maxlength="16" 
								onkeypress="return hm.util.keyPressWithBlankPermit(event,'name');" />
							<s:text name="config.security.onboarduisetting.label.title.range" /></td>
					</tr>
					<tr>
						<td class="myLabelT1" width="200px"><s:text
								name="config.security.onboarduisetting.UserNameLable.label" />
						</td>
						<td style="padding: 6px 0px 0px 0px;"><s:textfield
								name="dataSource.userNameLabel" size="60" id="userNameLabel"
								maxlength="16" 
								onkeypress="return hm.util.keyPressWithBlankPermit(event,'name');" />
							<s:text name="config.security.onboarduisetting.label.username.range" /></td>
					</tr>
					<%-- <tr>
						<td class="myLabelT1" width="200px"><s:text
								name="config.security.onboarduisetting.OwnerShipLabel.label" />
						</td>
						<td style="padding: 6px 0px 0px 0px;"><s:textfield
								name="dataSource.ownerShipLabel" size="60" id="ownerShipLabel"
								maxlength="%{nameLength}" 
								onkeypress="return hm.util.keyPressWithBlankPermit(event,'name');" />
							<s:text name="config.security.onboarduisetting.label.range" /></td>
					</tr>

					<tr>
						<td class="myLabelT1" width="200px"><s:text
								name="config.security.onboarduisetting.CidLabel.label" /></td>
						<td style="padding: 6px 0px 0px 0px;"><s:textfield
								name="dataSource.cidLabel" size="60" id="cidLabel"
								maxlength="%{nameLength}" 
								onkeypress="return hm.util.keyPressWithBlankPermit(event,'name');" />
							<s:text name="config.security.onboarduisetting.label.range" /></td>
					</tr>

					<tr>
						<td class="myLabelT1" width="200px"><s:text
								name="config.security.onboarduisetting.ByodLabel.label" /></td>
						<td style="padding: 6px 0px 0px 0px;"><s:textfield
								name="dataSource.byodLabel" size="60" id="byodLabel"
								maxlength="%{nameLength}" 
								onkeypress="return hm.util.keyPressWithBlankPermit(event,'name');" />
							<s:text name="config.security.onboarduisetting.label.range" /></td>
					</tr> --%>

					<tr>
						<td class="myLabelT1" width="200px"><s:text
								name="config.security.onboarduisetting.TargetUrl.label" /></td>
						<td style="padding: 6px 0px 0px 0px;"><s:textfield
								name="dataSource.targetUrl" size="60" id="targetUrl"
								maxlength="255" 
								onkeypress="return hm.util.keyPressPermit(event,'name');" /> <s:text
								name="config.security.onboarduisetting.label.targeturl.range" /></td>
					</tr>

					<tr>
						<td class="myLabelT1" width="200px"><s:text
								name="config.security.onboarduisetting.CidText.label" /></td>

						<td style="padding: 6px 0px 0px 0px;"><s:textarea rows="5"
								cols="80" id="cidText" name="dataSource.cidText" maxlength="255"
								 /></td>
					</tr>

					<tr>
						<td class="labelT1" width="200px"><s:text
								name="config.security.onboarduisetting.ByodText.label" /></td>
						<td style="padding: 6px 0px 0px 0px;"><s:textarea rows="5"
								maxlength="255" cols="80" id="byodText"
								name="dataSource.byodText"  /></td>
					</tr>

					<tr>
						<td class="labelT1" width="200px"><s:text
								name="config.security.onboarduisetting.AgreementText.label" />
						</td>
						<td style="padding: 6px 0px 0px 0px;"><s:textarea rows="5"
								maxlength="2048" cols="80" id="agreementText"
								name="dataSource.agreementText"  /></td>
					</tr>

					<tr>
						<td height="10"></td>
					</tr>

					<tr>
						<td class="myLabelT1" width="200px"><s:text
									name="config.security.onboarduisetting.welcome.label" />
						</td>

						<td style="padding: 6px 0px 0px 0px;"><s:textarea rows="8"
								cols="80" id="welcomeText" name="dataSource.welcomeText"
								maxlength="2048"  /></td>
					</tr>

					<tr>
						<td class="myLabelT1" width="200px"></td>
						<td style="padding: 6px 0px 0px 0px;">
							<table cellspacing="0" cellpadding="0" border="0" width="400">
								<tr>
									<td><input type="button" name="ignore"
										value="Provisioning SSID" style="width: 120px"
										class="button"
										onClick="insertAtCursor('provisioningSsid');"></td>

									<td><input type="button" name="ignore"
										value="Access SSID" style="width: 120px"
										class="button"
										onClick="insertAtCursor('accessingSsid');"></td>

									<td><input type="button" name="ignore" value="Ownership"
										style="width: 120px" <s:property value="writeDisabled" />
										class="button" onClick="insertAtCursor('ownerType');"></td>
								</tr>
								<tr>

									<td><input type="button" name="ignore"
										value="Client MAC" style="width: 120px"
										class="button"
										onClick="insertAtCursor('clientMacAddress');"></td>
									<td><input type="button" name="ignore" value="OS Type"
										style="width: 120px;" <s:property value="writeDisabled" />
										class="button" onClick="insertAtCursor('osType');"></td>


									<!-- <td><input type="button" name="ignore"
										value="wifiProfileName" style="width: 120px"
										<s:property value="writeDisabled" /> class="button"
										onClick="insertAtCursor('wifiProfileName');"></td>

									<td><input type="button" name="ignore" value="deviceName"
										style="width: 120px;"
										<s:property value="writeDisabled" /> class="button"
										onClick="insertAtCursor('deviceName');"></td> -->
									<!-- <td><input type="button" name="ignore" value="deviceType"
										style="width: 120px;" <s:property value="writeDisabled" />
										class="button" onClick="insertAtCursor('deviceType');"></td> -->

								</tr>





							</table>



						</td>
					</tr>

					<tr>
						<td height="10"></td>
					</tr>

				</table>
			</td>
		</tr>
	</table>

</s:form>
</div>

<div id="previewPagePanelElement" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="previewPageFrame" name="preivewPageFrame" width="0"
			height="0" frameborder="0" style="background-color: #FFFFFF;" src=""
			scrolling="auto"> </iframe>
	</div>
</div>
<script type="text/javascript">
var uploadFilePanel;
var previewPagePanel;


function closePreviewWindow() {
	previewPagePanel.hide();
}

function closePreviewPanel() {
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		Get("previewPageFrame").style.display = "none";
	}
	if(Get("previewPageFrame")!=null){
		Get("previewPageFrame").contentDocument.write("");
		Get("previewPageFrame").contentDocument.clear();
	}
}


function createPreviewPagePanel(width, height){
	var div = Get("previewPagePanelElement");
	var iframe = Get("previewPageFrame");
	iframe.width = width;
	iframe.height = height;
	previewPagePanel = new YAHOO.widget.Panel(div, 
	                                        { width:(width+20)+"px", 
											  fixedcenter:"contained", 
											  visible:false,
											  close: true,
											  modal:true,
											  draggable: true,
											  constraintoviewport:true } );
	previewPagePanel.render(document.body);
	div.style.display="";
	previewPagePanel.beforeHideEvent.subscribe(closePreviewPanel);
}





</script>
