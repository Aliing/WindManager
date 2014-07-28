<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>!window.jQuery && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script src="<s:url value="/js/hm.options.js" includeParams="none" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script type="text/javascript"
	src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
<script type="text/javascript"
	src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<style type="text/css">
div.yuimenu .bd {
	zoom: normal;
}

#calendarcontainer {
	padding: 10px;
}

#calendarpicker1 button {
	background: url(<s:url value ="/images/calendar_icon.gif" includeParams ="none"/>) center center no-repeat;
	margin: 2px 0; /* For IE */
	height: 1.5em; /* For IE */
}

#calendarmenu {
	position: absolute;
}

.options {
	height: 0px;
	width: 100%;
	position: absolute;
	padding: 0 10px 0px;
	top: 0px;
	left: -10px;
	background: #EEEEEE;
	overflow: hidden;
	border-bottom: 1px solid #999;
}

.settingsToolBar {
	text-align: right;
	background-color: #EEEEEE;
	height: 22px;
}

.settingCaption {
	font-size: 12px;
	font-weight: bold;
	font: solid;
	color: #003366;
	padding: 2px 10px 0px 10px;
	float: left;
}

.settingsToolBar a {
	text-decoration: none;
	color: #003366;
	margin-right: 5px;
	font-weight: bold;
}

.settingsToolBar a span {
	font-size: 12px;
	color: #99CCCC;
}

a#saveOption:hover img,a#exitOption:hover img {
	filter: alpha(opacity ="50");
	opacity: 0.5;
}

.settingsToolBar a:hover span {
	color: #003366;
}

td.showValue {
	padding: 8px 0px 5px 10px;
	vertical-align: top;
	color: #000080;
}

</style>

<script>
var formName = 'oemSettings';
var thisOperation;

var locatePosition = '<s:property value="locatePosition" />';

function onLoadPage()
{
	onLoadNotes();
	createWaitingPanel();
	createAnimation();
    
	if (<s:property value="showOemOption" />)
	{
		showOemOption();
	}
	
	if(locatePosition != null && locatePosition !=""){
		window.location="#"+locatePosition;
	}
}



var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"270px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("The operation is progressing...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

var oemOptionsAnim = null;
var oemSpaceAnim = null;

function createAnimation(){
	oemOptionsAnim = new YAHOO.util.Anim('pictureOptions');
	oemOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	oemSpaceAnim = new YAHOO.util.Anim('pictureSpace');



}

function clickOemSetting()
{
	showProcessing();

	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showOemOption.value = true;
	document.forms[formName].locatePosition.value = "oemSaveDiv";
    document.forms[formName].submit();
}

function showOemOption(){
	document.getElementById("oemSettingDiv").style.display = "none";
	document.getElementById("oemSaveDiv").style.display = "";
	oemOptionsExpand();
}

function oemOptionsExpand(){
	var viewHeight = document.getElementById('oemView').offsetHeight;
	var optionsHeight = 250 + viewHeight;
	var spaceHeight = 250;

	oemOptionsAnim.stop();
	oemSpaceAnim.stop();
	oemOptionsAnim.attributes.height = { to: optionsHeight };
	oemOptionsAnim.animate();
	oemSpaceAnim.attributes.height = { to: spaceHeight };
	oemSpaceAnim.duration = 0.1;
	oemSpaceAnim.animate();
}

function savePictureOption(){
	if(!validateDateTimeConfig()){
		return;
	}

	var confirmMessage = '<s:text name="home.hmSettings.save.confirm" />';

	thisOperation = 'uploadPicture';
	confirmDialog.cfg.setProperty('text', confirmMessage);
	confirmDialog.show();
}

function doContinueOper()
{
	    submitAction(thisOperation);
}

function exitOemOption(){
	document.forms[formName].showOemOption.value = false;

	document.getElementById("oemSettingDiv").style.display = "";
	document.getElementById("oemSaveDiv").style.display = "none";
	oemOptionsCollapse();
}

function oemOptionsCollapse(){
	oemOptionsAnim.stop();
	oemSpaceAnim.stop();
	oemOptionsAnim.attributes.height = { to: 0 };
	oemOptionsAnim.animate();
	oemSpaceAnim.attributes.height = { to: 0 };
	oemSpaceAnim.duration = 0.1;
	oemSpaceAnim.animate();
}

var _warnDialog;
function showBreakHa() {
	if (_warnDialog == null) {
		_warnDialog = new YAHOO.widget.SimpleDialog("warnDialog", {
			width : "350px",
			fixedcenter : true,
			visible : false,
			draggable : true,
			modal : true,
			close : true,
			icon : YAHOO.widget.SimpleDialog.ICON_ALARM,
			constraintoviewport : true,
			buttons : [ {
				text : "OK",
				handler : _handleYes,
				isDefault : true
			} ]
		});
		_warnDialog.setHeader("Warning");
		_warnDialog.render(document.body);
	}
	_warnDialog.cfg.setProperty('text', '<s:text name="home.hmSettings.ha.break.confirm" />');
	_warnDialog.show();
}
function _handleYes(){
	this.hide();
	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showNetworkOption.value = true;
	document.forms[formName].locatePosition.value = "networkSaveDiv";
    document.forms[formName].submit();
	//continueExe();
	
}

function submitAction(operation)
{
    if (validate(operation))
	{
		document.forms[formName].operation.value = operation;
		try
		{
			document.forms[formName].submit();
			
			if(waitingPanel != null)
			{
				waitingPanel.show();
			}
		}
		catch(e)
		{
			if (e instanceof Error && e.name == "TypeError")
			{
				var certificateFile = document.getElementById("certificateFile");
				hm.util.reportFieldError(certificateFile, 'Please select valid file!');
			}
		}
	}
}


function validate(operation)
{
	return true;
}

function showHangMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteInfo";
	hm.util.show("noteSection");
	if(!(message == "<s:text name='message.admin.separatedb.local.success'/>"
			|| message == "<s:text name='message.admin.separatedb.success'/>")){
		setTimeout("hideMessage()", 5 * 2000);
	}else{
		setTimeout("hideMessage()", 5 * 12000);
	}
}

function hideMessage()
{
	hm.util.hide('noteSection');
}

function showErrorMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteError";
	hm.util.show("noteSection");
	setTimeout("initNoteSection()", 5 * 2000);
}

function initNoteSection()
{
	hm.util.hide('noteSection');
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}

function confirmUpdate(confirms)
{
	var titleMsg = "";
	if (confirms.length == 0)
	{
		titleMsg = '<s:text name="admin.hmOperation.continue.confirm" />';
	} else if (confirms.length == 1)
	{
		titleMsg += confirms[0]+"<br>";

		titleMsg = titleMsg+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?"
	} else if (confirms.length > 1)
	{
		titleMsg += "1. "+confirms[0]+"<br>"
		for (i = 1;i<confirms.length;i++)
		{
			titleMsg += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ (i+1) +". "+confirms[i]+"<br>"
		}

		titleMsg = titleMsg+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?"
	}

	confirmDialog.cfg.setProperty('text',titleMsg);
	confirmDialog.show();
}

function validateSettings(){
	var cn = document.getElementById("companyName");
	var pn = document.getElementById("productName");
	var hp = document.getElementById("homePage");
	var se = document.getElementById("supportEmail");
	var cr = document.getElementById("copyright");
	if ( cn.value.length == 0)
	{
        hm.util.reportFieldError(cn, '<s:text name="error.requiredField"><s:param><s:text name="geneva_11.home.oemSettings.settings.company_name" /></s:param></s:text>');
        cn.focus();
        return false;
  	}else if (cn.value.length >16)
    {
		hm.util.reportFieldError(cn, '<s:text name="error.keyValueRange"><s:param><s:text name="geneva_11.home.oemSettings.settings.company_name" /></s:param><s:param><s:text name="geneva_11.home.oemSettings.settings.company.short.length" /></s:param></s:text>');
		cn.focus();
		return false;
	}
	if ( pn.value.length == 0)
	{
        hm.util.reportFieldError(pn, '<s:text name="error.requiredField"><s:param><s:text name="geneva_11.home.oemSettings.settings.product_name" /></s:param></s:text>');
        pn.focus();
        return false;
  	}else if (pn.value.length >64)
    {
		hm.util.reportFieldError(pn, '<s:text name="error.keyValueRange"><s:param><s:text name="geneva_11.home.oemSettings.settings.product_name" /></s:param><s:param><s:text name="geneva_11.home.oemSettings.settings.company.length" /></s:param></s:text>');
		pn.focus();
		return false;
	}
	if ( hp.value.length == 0)
	{
        hm.util.reportFieldError(hp, '<s:text name="error.requiredField"><s:param><s:text name="geneva_11.home.oemSettings.settings.home_page" /></s:param></s:text>');
        hp.focus();
        return false;
  	}else if (hp.value.length >128)
    {
		hm.util.reportFieldError(hp, '<s:text name="error.keyValueRange"><s:param><s:text name="geneva_11.home.oemSettings.settings.home_page" /></s:param><s:param><s:text name="geneva_11.home.oemSettings.settings.url.length" /></s:param></s:text>');
		hp.focus();
		return false;
	}
	if ( se.value.length == 0)
	{
        hm.util.reportFieldError(se, '<s:text name="error.requiredField"><s:param><s:text name="geneva_11.home.oemSettings.settings.support_email" /></s:param></s:text>');
        se.focus();
        return false;
  	}else if (se.value.length >128)
    {
		hm.util.reportFieldError(se, '<s:text name="error.keyValueRange"><s:param><s:text name="geneva_11.home.oemSettings.settings.support_email" /></s:param><s:param><s:text name="geneva_11.home.oemSettings.settings.url.length" /></s:param></s:text>');
		se.focus();
		return false;
	}
	if ( cr.value.length == 0)
	{
        hm.util.reportFieldError(cr, '<s:text name="error.requiredField"><s:param><s:text name="geneva_11.home.oemSettings.settings.copyright" /></s:param></s:text>');
        cr.focus();
        return false;
  	}else if (cr.value.length >64)
    {
		hm.util.reportFieldError(cr, '<s:text name="error.keyValueRange"><s:param><s:text name="geneva_11.home.oemSettings.settings.copyright" /></s:param><s:param><s:text name="geneva_11.home.oemSettings.settings.company.length" /></s:param></s:text>');
		cr.focus();
		return false;
	}
	
	var productLogo = document.getElementById("leftTopLogoLocalFile");
	var urlIcon = document.getElementById("iconLocalFile");
	var background = document.getElementById("backgroundLocalFile");
	var about = document.getElementById("aboutScreenLocalFile");
	var configFooter = document.getElementById("configFooterLogoFile");
	var productLogoDiv = document.getElementById("leftTopLogoFilePath");
	var urlIconDiv = document.getElementById("iconFilePath");
	var backgroundDiv = document.getElementById("backgroundFilePath");
	var aboutDiv = document.getElementById("aboutScreenFilePath");
	var configFooterLogDiv = document.getElementById("configFooterLogoFilePath");
	if ( productLogo.value.length > 0) {
		fileExtension=productLogo.value.substring(productLogo.value.lastIndexOf('.')+1,productLogo.value.length);
		if(fileExtension.toLowerCase()!="png"){
			hm.util.reportFieldError(productLogoDiv, "<s:text name='geneva_11.error.oemsettings.image.png'></s:text>");
			return false;
		}
	}
	if ( urlIcon.value.length > 0) {
		fileExtension=urlIcon.value.substring(urlIcon.value.lastIndexOf('.')+1,urlIcon.value.length);
		if(fileExtension.toLowerCase()!="ico"){
			hm.util.reportFieldError(urlIconDiv, "<s:text name='geneva_11.error.oemsettings.image.ico'></s:text>");
			return false;
		}
	}
	if ( background.value.length > 0) {
		fileExtension=background.value.substring(background.value.lastIndexOf('.')+1,background.value.length);
		if(fileExtension.toLowerCase()!="gif"){
			hm.util.reportFieldError(backgroundDiv, "<s:text name='geneva_11.error.oemsettings.image.gif'></s:text>");
			return false;
		}
	}
	if ( about.value.length > 0) {
		fileExtension=about.value.substring(about.value.lastIndexOf('.')+1,about.value.length);
		if(fileExtension.toLowerCase()!="png"){
			hm.util.reportFieldError(aboutDiv, "<s:text name='geneva_11.error.oemsettings.image.png'></s:text>");
			return false;
		}
	}
	
	if ( configFooter.value.length > 0) {
		configFooter.value = "";
		/* fileExtension=configFooter.value.substring(configFooter.value.lastIndexOf('.')+1,configFooter.value.length);
		if(fileExtension.toLowerCase()!="png"){
			hm.util.reportFieldError(configFooterLogDiv, "<s:text name='geneva_11.error.oemsettings.image.png'></s:text>");
			return false;
		} */
	}
	
	return true;
}

function revertImage(action)
{
	document.forms[formName].showOemOption.value = false;
	document.forms[formName].operation.value=action;
	document.forms[formName].submit();
}

function saveOEMSettingsOption(){
	if (!validateSettings())
	{
		return;
	}

	var confirms = new Array();
    var index = 0;
    confirms[index] = '<s:text name="geneva_11.warn.admin.settings.update" />';

    document.forms[formName].showOemOption.value = false;
	thisOperation = 'updateSettings';
	confirmUpdate(confirms);
}

</script>
<div id="content">
	<s:form action="oemSettings" enctype="multipart/form-data" >
	<s:hidden name="showOemOption" />
	<s:hidden name="locatePosition" />
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td colspan="10">
			<div id="noteSection" style="display: none">
			<table width="700px" border="0" cellspacing="0" cellpadding="0"
				class="note">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td id="noteTD" nowrap="nowrap"></td>
				</tr>
				<tr>
					<td height="5"></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" style="border: 0px; width: 750px">
				
				<!-- picture -->
				<tr>
					<td style="padding: 10px 10px 0px 15px;">
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
						type="password">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" style="width: 100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="oemSettingDiv"><span
								class="settingCaption"><s:text
								name="geneva_11.home.oemSettings.topic" /> </span> <a href="#"
								onclick="clickOemSetting();" style="display:"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a></div>
							<div class="settingsToolBar" id="oemSaveDiv" style="display: none;">
								<span class="settingCaption"><s:text	name="geneva_11.home.oemSettings.edit.topic" /> </span> 
								<a href="#saveOptions" onclick="saveOEMSettingsOption();"> <img alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"	width="16" class="dinl"> </a>
								<a href="#exitOptions"	onclick="exitOemOption();"> <img alt="Cancel"	title="Cancel"	
									src="<s:url value="/images/cancel.png" includeParams="none"/>" width="16" class="dinl"> </a>
							</div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="oemView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr><td height="10px"></td></tr>
										<tr>
											<td class="labelT1" width="700px">
											<fieldset>
												<legend style="border: 0px;background-color: #FFFFFF;"><s:text name="geneva_11.home.oemSettings.settings.title" /></legend>
												<table border="0px">
													<tr><td height="5px"></td></tr>
													<tr><td height="20px" ><s:text name="geneva_11.home.oemSettings.settings.company_name"/></td>
														<td style="padding-left: 30px;">${companyName }</td></tr>
													<tr><td height="20px"><s:text name="geneva_11.home.oemSettings.settings.product_name"/></td>
														<td style="padding-left: 30px;">${productName }</td></tr>
													<tr><td height="20px"><s:text name="geneva_11.home.oemSettings.settings.home_page"/></td>
														<td style="padding-left: 30px;">${homePage }</td></tr>
														<!-- 
													<tr><td height="20px"><s:text name="geneva_11.home.oemSettings.settings.help_url"/></td>
														<td style="padding-left: 30px;">${helpURL }</td></tr>
														 -->
													<tr><td height="20px"><s:text name="geneva_11.home.oemSettings.settings.support_email"/></td>
														<td style="padding-left: 30px;">${supportEmail }</td></tr>
													<tr><td height="20px"><s:text name="geneva_11.home.oemSettings.settings.copyright"/></td>
														<td style="padding-left: 30px;">Copyright &copy; ${copyright }</td></tr>
													<tr><td height="5px"></td></tr>
												</table>
											</fieldset>
											</td>
										</tr>
										<tr>
											<td class="labelT1" width="100%">
											<fieldset>
												<legend style="border: 0px;background-color: #FFFFFF;"><s:text name="geneva_11.home.oemSettings.picture.title" /></legend>
												<table>
													<tr><td height="5px">
														<table>
															<tr>
																<td width="160px"><s:text name="geneva_11.home.oemSettings.picture.product.logo"/></td>
																<td><!-- <a href="<s:url value="/images/company_logo_reverse.png"/>" target="_blank">company_logo_reverse.png</a> -->company_logo_reverse.png</td>
															</tr>	
															<tr>
																<td>
																</td>
																<td style="color: gray;"><s:text name="geneva_11.info.oemsettings.topleft.image"/></td>
															</tr>														
														</table>
													</td></tr>
													<tr><td height="5px">
														<table>
															<tr>
																<td width="160px"><s:text name="geneva_11.home.oemSettings.picture.icon"/></td>
																<td><!-- <a href="<s:url value="/images/favicon.ico"/>" target="_blank">favicon.ico</a> -->favicon.ico</td>
															</tr>	
															<tr>
																<td>
																</td>
																<td style="color: gray"><s:text name="geneva_11.info.oemsettings.icon.image"/></td>
															</tr>														
														</table>
													</td></tr>
													<tr><td height="5px">
														<table>
															<tr>
																<td width="160px"><s:text name="geneva_11.home.oemSettings.picture.background"/></td>
																<td><!-- <a href="<s:url value="/images/hm/bkg.gif"/>" target="_blank">bkg.gif</a> -->bkg.gif</td>
															</tr>	
															<tr>
																<td>
																</td>
																<td style="color: gray"><s:text name="geneva_11.info.oemsettings.background.image"/></td>
															</tr>														
														</table>
													</td></tr>
													<tr><td height="5px">
														<table>
															<tr>
																<td width="160px"><s:text name="geneva_11.home.oemSettings.picture.about.screen"/></td>
																<td><!-- <a href="<s:url value="/images/company_logo.png"/>" target="_blank">company_logo.png</a> -->company_logo.png</td>
															</tr>	
															<tr>
																<td>
																</td>
																<td style="color: gray"><s:text name="geneva_11.info.oemsettings.aboutscreen.image"/></td>
															</tr>														
														</table>
													</td></tr>
													<!-- Configuration Footer Image -->
													<%-- <tr><td height="5px">
														<table>
															<tr>
																<td width="160px"><s:text name="geneva_11.home.oemSettings.picture.config.footer.logo"/></td>
																<td><!-- <a href="<s:url value="/images/company_logo.png"/>" target="_blank">company_logo.png</a> -->HM-config-footer.png</td>
															</tr>	
															<tr>
																<td>
																</td>
																<td style="color: gray"><s:text name="geneva_11.info.oemsettings.config.footer.image"/></td>
															</tr>														
														</table>
													</td></tr> --%>
												</table>
											</fieldset>
											</td>
										</tr>
										<tr>
											<td class="labelT1" width="100%">
											<fieldset>
												<legend style="border: 0px;background-color: #FFFFFF;"><s:text name="geneva_11.home.oemSettings.EULA.title" /></legend>
												<table>
													<tr><td height="5px"></td></tr>
													<tr><td>
														<s:textarea cols="140" rows="20" value='%{eulaContent}' readonly="true">
														</s:textarea>
													</td></tr>
													<tr><td height="5px"></td></tr>
												</table>
											</fieldset>
											</td>
										</tr>
										<tr>
											<td height="0px">
											<div id="pictureSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="pictureOptions" class="options" style="background-color: #FFFFFF">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
											<td class="labelT1" width="100%">
											<fieldset>
												<legend style="border: 0px;background-color: #FFFFFF;"><s:text name="geneva_11.home.oemSettings.settings.title" /></legend>
												<table style="width: 700px;">
													<tr>
														<td></td>
														<td align="right" height="18px;">
														<a style="padding-left:20px;" href="javascript:void(0);" onclick="revertImage('revertSetings')"><s:text name="geneva_11.home.oemSettings.picture.button.restore.default"/></a>
														</td>
													</tr>
													<tr><td height="20px" width="160px"><s:text name="geneva_11.home.oemSettings.settings.company_name"/><font color="red"><s:text	name="*" /> </font></td>
														<td style="width: 460px;"><s:textfield name="companyName" id="companyName" size="40"/>
														<s:text name="geneva_11.home.oemSettings.settings.company.short.length"/></td></tr>
													<tr><td height="20px"><s:text name="geneva_11.home.oemSettings.settings.product_name"/><font color="red"><s:text	name="*" /> </font></td>	
														<td ><s:textfield name="productName" id="productName" size="40"/>
														<s:text name="geneva_11.home.oemSettings.settings.company.length"/></td></tr>
													<tr><td height="20px"><s:text name="geneva_11.home.oemSettings.settings.home_page"/><font color="red"><s:text	name="*" /> </font></td>
														<td><s:textfield name="homePage" id="homePage" size="40"/>
														<s:text name="geneva_11.home.oemSettings.settings.url.length"/></td></tr>
														<!--
													<tr><td height="20px"><s:text name="geneva_11.home.oemSettings.settings.help_url"/><font color="red"><s:text	name="*" /> </font></td>
														<td><s:textfield name="helpURL" id="helpURL" size="40"/>
														<s:text name="geneva_11.home.oemSettings.settings.url.length"/></td></tr>
														-->
													<tr><td height="20px"><s:text name="geneva_11.home.oemSettings.settings.support_email"/><font color="red"><s:text	name="*" /> </font></td>
														<td><s:textfield name="supportEmail" id="supportEmail" size="40"/>
														<s:text name="geneva_11.home.oemSettings.settings.url.length"/></td></tr>
													<tr><td height="20px"><s:text name="geneva_11.home.oemSettings.settings.copyright"/><font color="red"><s:text	name="*" /> </font></td>
														<td>Copyright &copy; <s:textfield name="copyright" id="copyright" size="27"/>
														<s:text name="geneva_11.home.oemSettings.settings.company.length"/></td></tr>
													<tr><td height="5px"></td></tr>
												</table>
											</fieldset>
											</td>
										</tr>
										<tr>
											<td class="labelT1" width="100%">
											<fieldset>
												<legend style="border: 0px;background-color: #FFFFFF;"><s:text name="geneva_11.home.oemSettings.picture.title" /></legend>
												<table>
													<tr><td height="5px">
														<table>
															<tr>
																<td width="160px"><s:text name="geneva_11.home.oemSettings.picture.product.logo"/></td>
																<td>
																	<div id="leftTopLogoFilePath">
																	</div>
																	<div>
																	<s:file id="leftTopLogoLocalFile" name="leftTopLogoLocalFile" size="30"  label="top left logo file"
										 									accept="image/png" />
																	<a style="padding-left:20px;" href="javascript:void(0);" onclick="revertImage('revertLeftTopImage')"><s:text name="geneva_11.home.oemSettings.picture.button.restore.default"/></a>
																	
																	</div>
																</td>
															</tr>	
															<tr>
																<td>
																	<table>
																		<tr><td style="color: gray"><s:text name="geneva_11.home.oemSettings.picture.product.logo.size"/></td></tr>
																		<tr><td style="color: gray"><s:text name="geneva_11.home.oemSettings.picture.product.logo.type"/></td></tr>
																	</table>
																</td>
																<td style="color: gray"><s:text name="geneva_11.info.oemsettings.topleft.image"/></td>
																<td></td>
															</tr>														
														</table>
													</td></tr>
													<tr><td height="5px">
														<table>
															<tr>
																<td width="160px"><s:text name="geneva_11.home.oemSettings.picture.icon"/></td>
																<td>
																	<div id="iconFilePath">
																	</div>
																	<div>
																	<s:file id="iconLocalFile" name="iconLocalFile" size="30"  label="icon file"
										 									accept="image/ico" />
																	<a style="padding-left:20px;" href="javascript:void(0);" onclick="revertImage('revertIcoImage')"><s:text name="geneva_11.home.oemSettings.picture.button.restore.default"/></a>
																	
																	</div>
																</td>
															</tr>	
															<tr>
																<td>
																	<table>
																		<tr><td style="color: gray"><s:text name="geneva_11.home.oemSettings.picture.icon.size"/></td></tr>
																		<tr><td style="color: gray"><s:text name="geneva_11.home.oemSettings.picture.icon.type"/></td></tr>
																	</table>
																</td>
																<td style="color: gray"><s:text name="geneva_11.info.oemsettings.icon.image"/></td>
																<td></td>
															</tr>														
														</table>
													</td></tr>
													<tr><td height="5px">
														<table>
															<tr>
																<td width="160px"><s:text name="geneva_11.home.oemSettings.picture.background"/></td>
																<td>
																	<div id="backgroundFilePath">
																	</div>
																	<div>
																	<s:file id="backgroundLocalFile" name="backgroundLocalFile" size="30"  label="background image file"
										 									accept="image/gif" />
																	<a style="padding-left:20px;" href="javascript:void(0);" onclick="revertImage('revertBackgroundImage')"><s:text name="geneva_11.home.oemSettings.picture.button.restore.default"/></a>
																	
																	</div>
																</td>
															</tr>	
															<tr>
																<td>
																	<table>
																		<tr><td style="color: gray"><s:text name="geneva_11.home.oemSettings.picture.background.size"/></td></tr>
																		<tr><td style="color: gray"><s:text name="geneva_11.home.oemSettings.picture.background.type"/></td></tr>
																	</table>
																</td>
																<td style="color: gray"><s:text name="geneva_11.info.oemsettings.background.image"/></td>
																<td></td>
															</tr>														
														</table>
													</td></tr>
													<tr><td height="5px">
														<table>
															<tr>
																<td width="160px"><s:text name="geneva_11.home.oemSettings.picture.about.screen"/></td>
																<td>
																	<div id="aboutScreenFilePath">
																	</div>
																	<div>
																	<s:file id="aboutScreenLocalFile" name="aboutScreenLocalFile" size="30"  label="about screen file"
										 									accept="image/png" />
																	<a style="padding-left:20px;" href="javascript:void(0);" onclick="revertImage('revertAboutScreenImage')"><s:text name="geneva_11.home.oemSettings.picture.button.restore.default"/></a>
																	
																	</div>
																</td>
															</tr>	
															<tr>
																<td>
																	<table>
																		<tr><td style="color: gray"><s:text name="geneva_11.home.oemSettings.picture.about.screen.size"/></td></tr>
																		<tr><td style="color: gray"><s:text name="geneva_11.home.oemSettings.picture.about.screen.type"/></td></tr>
																	</table>
																</td>
																<td style="color: gray"><s:text name="geneva_11.info.oemsettings.aboutscreen.image"/></td>
																<td></td>
															</tr>														
														</table>
													</td></tr>
													<!--  Config Footer Logo -->										
													<tr style="display:none;"><td height="5px">
														<table>
															<tr>
																<td width="160px"><s:text name="geneva_11.home.oemSettings.picture.config.footer.logo"/></td>
																<td>
																	<div id="configFooterLogoFilePath">
																	</div>
																	<div>
																	<s:file id="configFooterLogoFile" name="configFooterLogoFile" size="30"  label="about config footer logo file"
										 									accept="image/png" />
																	<a style="padding-left:20px;" href="javascript:void(0);" onclick="revertImage('revertConfigFooterImage')"><s:text name="geneva_11.home.oemSettings.picture.button.restore.default"/></a>
																	
																	</div>
																</td>
															</tr>	
															<tr>
																<td>
																	<table>
																		<tr><td style="color: gray"><s:text name="geneva_11.home.oemSettings.picture.config.footer.logo.size"/></td></tr>
																		<tr><td style="color: gray"><s:text name="geneva_11.home.oemSettings.picture.config.footer.logo.type"/></td></tr>
																	</table>
																</td>
																<td style="color: gray"><s:text name="geneva_11.info.oemsettings.config.footer.image"/></td>
																<td></td>
															</tr>														
														</table>
													</td></tr>													
												</table>
											</fieldset>
											</td>
										</tr>
										<tr>
											<td class="labelT1" width="100%">
											<fieldset>
												<legend style="border: 0px;background-color: #FFFFFF;"><s:text name="geneva_11.home.oemSettings.EULA.title" /></legend>
												<table style="width: 100%">
													<tr>
														<td align="right" height="18px;" width="90%" style="padding-right: 30px;">
														<a style="padding-left:20px;" href="javascript:void(0);" onclick="revertImage('revertEULA')"><s:text name="geneva_11.home.oemSettings.picture.button.restore.default"/></a>
														</td>
													</tr>
													<tr><td>
														<s:textarea id="newEula" name="newEula" cols="140" rows="20" value='%{eulaContent}'>
														</s:textarea>
													</td></tr>
													<tr><td height="5px"></td></tr>
												</table>
											</fieldset>
											</td>
										</tr>
												
											</table>
											</td>
										</tr>
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				            
				<tr>
					<td height="10"></td>
				</tr>

			</table>
			</td>
		</tr>

	</table>
</s:form></div>
