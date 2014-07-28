<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script src="<s:url value="/js/hm.paintbrush.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" href="<s:url value="/css/hm_v2.css" includeParams="none"/>" type="text/css" />

<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none"/>" ></script>
<style type="text/css">
tr#awNonComplianceRow div div {
    padding: 2px 0;
}
body.yui-skin-sam {
    height: auto;
}
</style>
<script>
var formName = 'configmdm';
var thisOperation;



function submitAction(operation) {
    thisOperation = operation;
  if(operation=="cancel"|| operation=="cancelssidFull"||operation=="cancelssid"|| operation=="cancelhiveAp" || operation=="cancelportAccess"){
	  
  }else{
	  if(!validateMDM()){
		  return false;
	  }
  }

  document.forms[formName].operation.value = thisOperation;
  document.forms[formName].submit();
     
}
//Added for MDM from Dakar
function enableMDM(checked){
	
	document.getElementById("chkToggleDisplay").disabled = !checked;
	
}

function changeMdmType(type) {
	document.getElementById("airwatch.apiurl").style.display = (type==1) ? "" : "none";
	document.getElementById("airwatch.apikey").style.display = (type==1) ? "" : "none";
	document.getElementById("div.enable.symbian.os").style.display = (type==1) ? "inline" : "none";
	document.getElementById("div.enable.blackberry.os").style.display = (type==1) ? "inline" : "none";
	document.getElementById("div.enable.android.os").style.display = (type==1) ? "inline" : "none";
	document.getElementById("div.enable.windowsphone.os").style.display = (type==1) ? "inline" : "none";
	
	<s:if test="%{supportAirWatchNonCompliance}">
	if(type==1) {
		YUD.setStyle('enabledAWNonComplianceRow', 'display', '');
	} else {
		YUD.setStyle('enabledAWNonComplianceRow', 'display', 'none');
		setAWNonComplianceRowStyle(false);
	}
	</s:if>
}
function parseURL(url) {
	var a = document.createElement('a');
	a.href = url;
	if(a.host == "") {
		a.href = a.href;
	}
    return {
    	source: url,
    	protocol: a.protocol.replace(':',''),
    	host: a.hostname,
    	port: a.port,
    	query: a.search,
    	params: (function(){
    		var ret = {},
    		seg = a.search.replace(/^\?/,'').split('&'),
    		len = seg.length, i = 0, s;
    		for (;i<len;i++) {
    			if (!seg[i]) { continue; }
    			s = seg[i].split('=');
    			ret[s[0]] = s[1];
    		}
    		return ret;
    	})(),
    	file: (a.pathname.match(/\/([^\/?#]+)$/i) || [,''])[1],
    	hash: a.hash.replace('#',''),
    	path: a.pathname.replace(/^([^\/])/,'/$1'),
    	relative: (a.href.match(/tps?:\/\/[^\/]+(.+)/) || [,''])[1],
    	segments: a.pathname.replace(/^\//,'').split('/')
    	};
}
function validateMDM() {
	
	 element=document.getElementById(formName + "_dataSource_policyname");
		if(element.value.trim().length==0)
	{
			hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.name" /></s:param></s:text>');
			element.focus();
			return false;
	}
	// rootURLPath
	element = document.getElementById(formName + "_dataSource_rootURLPath");
	var value = element.value.trim();
	if(value.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.urlpath" /></s:param></s:text>');
		element.focus();
		return false;
	}
	
	if (value.trim().indexOf(' ') > -1) {
        hm.util.reportFieldError(element, '<s:text name="error.name.containsBlank"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.urlpath" /></s:param></s:text>');
        element.focus();
        return false;
	}
	
	
	if (!(value.indexOf('http') == 0
			|| value.indexOf('https') == 0)
			|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){ 
	    	hm.util.reportFieldError(element, 
		    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.urlpath"/></s:param></s:text>');
	    	element.focus();
			return false;
		
	 }
	var rootURL = parseURL(value), hostName = rootURL.host;
	if(hostName.length > 32) {
        hm.util.reportFieldError(element, 'The host name "' + hostName + '" should not exceed 32 characters.');
        element.focus();
        return false;
	}
	
	if(document.getElementById(formName + "_dataSource_mdmType").value==1){
		element = document.getElementById(formName + "_dataSource_apiURL");
		var value = element.value.trim();
		if(value.trim().length == 0) {
			hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.apiurl.airwatch" /></s:param></s:text>');
			element.focus();
			return false;
		}
		
		if (value.trim().indexOf(' ') > -1) {
	        hm.util.reportFieldError(element, '<s:text name="error.name.containsBlank"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.apiurl.airwatch" /></s:param></s:text>');
	        element.focus();
	        return false;
		}
		
		if (!(value.indexOf('http') == 0
				|| value.indexOf('https') == 0)
				|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){ 
		    	hm.util.reportFieldError(element, 
			    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.apiurl.airwatch"/></s:param></s:text>');
		    	element.focus();
				return false;
			
		 }
		rootURL = parseURL(value), hostName = rootURL.host;
	    if(hostName.length > 32) {
	        hm.util.reportFieldError(element, 'The host name "' + hostName + '" should not exceed 32 characters.');
	        element.focus();
	        return false;
	    }
		
		// api key
		element = document.getElementById(formName + "_dataSource_apiKey");
		if(element.value.trim().length == 0) {
			hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.apikey.airwatch" /></s:param></s:text>');
			element.focus();
			return false;
		}
		
		if (element.value.trim().indexOf(' ') > -1) {
	        hm.util.reportFieldError(element, '<s:text name="error.name.containsBlank"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.apikey.airwatch" /></s:param></s:text>');
	        element.focus();
	        return false;
		}
	}else{
		document.getElementById(formName + "_dataSource_apiURL").value="";
		document.getElementById(formName + "_dataSource_apiKey").value="";
		
		document.getElementById(formName + "_dataSource_enableSymbianOs").checked=false;
		document.getElementById(formName + "_dataSource_enableBlackberryOs").checked=false;
		document.getElementById(formName + "_dataSource_enableAndroidOs").checked=false;
		document.getElementById(formName + "_dataSource_enableWindowsphoneOs").checked=false;
	}
	
	
	// user name
	element = document.getElementById(formName + "_dataSource_mdmUserName");
	
	if(element.value.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.username" /></s:param></s:text>');
		element.focus();
		return false;
	}
	
	if (element.value.trim().indexOf(' ') > -1) {
        hm.util.reportFieldError(element, '<s:text name="error.name.containsBlank"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.username" /></s:param></s:text>');
        element.focus();
        return false;
	}
	
	
	var elementC;
	
	if (document.getElementById("chkToggleDisplay").checked)
	{
		element = document.getElementById("mdmUserPassword");
		elementC = document.getElementById("cfUserPassword");
	}
	else
	{
		element = document.getElementById("userPassword_text");
		elementC = document.getElementById("cfUserPassword_text");
	}
	
	// password
	if(element.value.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.password" /></s:param></s:text>');
		element.focus();
		return false;
	}
	
	// password confirmation
	if(elementC.value.trim().length == 0) {
		hm.util.reportFieldError(elementC, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.advanced.mdm.enrollment.password.confirm" /></s:param></s:text>');
		elementC.focus();
		return false;
	}
	
	if(element.value != elementC.value) {
		hm.util.reportFieldError(element, '<s:text name="config.ssid.advanced.mdm.password.mismatch" />');
		element.focus();
		return false;
	}
	
	<s:if test="%{supportAirWatchNonCompliance}">
	//AirWatch NonCompliance validate
	if(Get(formName + '_dataSource_mdmType').value == 1) {
		if(!validateAWSettings()) {
			return false;
		}
	}
	</s:if>
	
	return true;
}

function submitActionJson(operation) {

	if (operation=='cancel') {
		parent.closeIFrameDialog();
		return false;
	}
	if(!validateMDM()){
		  return false;
	  }
	var url;
	thisOpreation = operation;
	
	 if (operation == 'create') {
		 url = "<s:url action='configmdm' includeParams='none' />"+ "?jsonMode=true" +"&ignore="+new Date().getTime(); 
	 } else if (operation == 'update'){
		 var id;
		 <s:if test="%{dataSource.id != null}">
		     id = <s:property value="dataSource.id"/>; 
		 </s:if>
		 url = "<s:url action='configmdm' includeParams='none' />"+ "?jsonMode=true" +"&ignore="+new Date().getTime(); 
	 }
	 document.forms["configmdm"].operation.value = operation;
	 YAHOO.util.Connect.setForm(document.forms["configmdm"]);
	 var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveTrafficFilter, failure : failSaveTrafficFilter, timeout: 60000}, null);
	
}
var succSaveTrafficFilter = function (o) {
	try {
		eval("var details = " + o.responseText);
		if (details.ok) {
			parent.closeIFrameDialog();
			if (thisOpreation == 'create') {
				if(details.id != null && details.name != null){
					var selectUIElement = parent.selectUIElement;
					
					if(selectUIElement) {
						if (Object.prototype.toString.call(selectUIElement) == '[object Array]') {
							for (var i=0;i<selectUIElement.length;i++) {
								hm.simpleObject.addOption(selectUIElement[i], details.id, details.name, false);
							}
						} else {
							hm.simpleObject.addOption(selectUIElement, details.id, details.name, true);
						}
					}
				}
			}
		} else {
			hm.util.displayJsonErrorNote(details.msg);
			return;
		}
	}catch(e){
		
		return;
	}
}
function failSaveTrafficFilter(){

}
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="configmdm" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}
</script>

<div id="content"><s:form action="configmdm">
	<s:if test="%{jsonMode==true}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="id" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="contentShowType" />
		<s:hidden name="parentIframeOpenFlg" />
	</s:if>
	<s:if test="%{jsonMode==false}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0"  cellpadding="0">
						<tr>
							<s:if test="%{dataSource.id == null}">
								<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
									class="button"
									onClick="submitAction('create<s:property value="lstForward"/>');"
									<s:property value="writeDisabled" />></td>
							</s:if>
							<s:else>
								<td><input type="button" name="ignore"  value="<s:text name="button.update"/>"
									class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
									<s:property value="updateDisabled" />></td>
							</s:else>
							<td><input type="button" name="ignore" value="Cancel"
								class="button"
								onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:if>
	<s:else>
	<div id="vlanTitleDiv" class="topFixedTitle">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-IP_Tracking.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.title.mdm.new"/>
								</s:if>
								<s:else>
									<s:text name="config.title.mdm.edit"/>
								</s:else>
								&nbsp;
							</td>
							<td>
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<s:if test="%{!parentIframeOpenFlg}">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);"  class="btCurrent" onclick="submitActionJson('cancel')" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<td class="npcButton">
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == 'disabled'}">
									&nbsp;</td>
								</s:if>
								<s:else>
									<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('create');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
								</s:else>
							</s:if>
							<s:else>
								<s:if test="%{updateDisabled == 'disabled'}">
									&nbsp;</td>
								</s:if>
								<s:else>
									<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('update');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
								</s:else>
							</s:else>
						</tr>
					</table>
					</s:if>
					<s:else>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:if>
							<s:else>
								<s:if test="%{updateDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:else>
						</tr>
					</table>
					</s:else>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</div>
	</s:else>
	<s:if test="%{jsonMode == true}">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
	</s:if>
	<s:else>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	</s:else>

		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-left:1px;">
			<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="675px" id="configmdmEditTable">
		<tr>
			<td>
			<%-- add this password dummy to fix issue with auto complete function --%>
			<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
<%-- Mobile Device Management start --%>
		
		<tr>
			<td style="padding-left:5px;" width="100%">
				
					<table cellspacing="0" cellpadding="0" border="0" width="100%" style="padding-top:1px;">
						<tr > 
							<td class="labelT1" width="120px" style="padding-left:5px;">
								<label>
									<s:text name="config.ssid.advanced.mdm.enrollment.name" /><font color="red">*</font>
								</label></td>
							<td colspan="3"><s:textfield
								name="dataSource.policyname" size="24" maxlength="32" disabled="%{disabledName}" /> &nbsp;<s:text
								name="character.range.1.to.32" /></td>
						</tr>
						<tr> 
							<td class="labelT1" width="120px" style="padding-left:5px;">
								<label>
									<s:text name="config.ssid.advanced.mdm.enrollment.description" />
								</label></td>
							<td colspan="3"><s:textfield
								name="dataSource.description" size="48" maxlength="48" /> &nbsp;<s:text
								name="config.security.description.range" /></td>
						</tr>
						<tr>
							<td class="labelT1" width="120px" style="padding-left:5px;">
								<label>
									<s:text name="config.ssid.advanced.mdm.enrollment.type" /><font color="red">*</font>
								</label></td>
							<td colspan="2"><s:select name="dataSource.mdmType" 
								list="%{mdmTypeList}" listKey="key" listValue="value" 
								value="dataSource.mdmType" cssStyle="width: 120px;" disabled="%{!dataSource.enableMDM}" onchange="changeMdmType(this.value)"/></td>
						</tr>
						<tr>
							<td class="labelT1" width="120px" style="padding-left:5px;" ><s:text name="config.ssid.advanced.mdm.enrollment.osobj"/></td>
							<td colspan="3" style="padding-top: 5px; padding-bottom: 5px">
							<s:if test="newflag">
							<div>
							 <div style="width:140px;display:inline;padding-right:4px">
                            	<s:checkbox name="dataSource.enableAppleOs" value="true" /><s:text name="config.ssid.advanced.mdm.enrollment.appleos" />
                             </div>
                             <div style="width:140px;display:inline;padding-right:28px;">
                          		<s:checkbox name="dataSource.enableMacOs"   value="true"  /><s:text name="config.ssid.advanced.mdm.enrollment.macos" />
                             </div>
                             <div id="div.enable.symbian.os" <s:if test='dataSource.mdmType==0'>style='display:none'</s:if> <s:else>style="width:140px;display:inline;" </s:else>>
                               	<s:checkbox name="dataSource.enableSymbianOs"   value="true"/><s:text name="config.ssid.advanced.mdm.enrollment.symbianos" />
                             </div>
							</div>
							<div>
                             <div id="div.enable.windowsphone.os" <s:if test='dataSource.mdmType==0'>style='display:none'</s:if> <s:else>style="width:140px;display:inline;padding-right:10px;" </s:else>>
                               	<s:checkbox name="dataSource.enableWindowsphoneOs"   value="true"/><s:text name="config.ssid.advanced.mdm.enrollment.windowsphoneos" />
                             </div>
                             <div id="div.enable.blackberry.os" <s:if test='dataSource.mdmType==0'>style='display:none'</s:if> <s:else>style="width:140px;display:inline;padding-right:10px;" </s:else>>
								<s:checkbox name="dataSource.enableBlackberryOs" value="true" /><s:text name="config.ssid.advanced.mdm.enrollment.blackberryos" />
                             </div>
                             <div id="div.enable.android.os" <s:if test='dataSource.mdmType==0'>style='display:none'</s:if> <s:else>style="width:140px;display:inline;padding-right:10px;" </s:else>>
								<s:checkbox name="dataSource.enableAndroidOs"  value="true" /><s:text name="config.ssid.advanced.mdm.enrollment.androidos" />
                             </div>
							</div>
							</s:if>
							<s:else>
							<div>
                             <div style="width:140px;display:inline;padding-right:4px;">
                            	<s:checkbox name="dataSource.enableAppleOs"  /><s:text name="config.ssid.advanced.mdm.enrollment.appleos" />
                             </div>
                             <div style="width:140px;display:inline;padding-right:28px;">
                          		<s:checkbox name="dataSource.enableMacOs"     /><s:text name="config.ssid.advanced.mdm.enrollment.macos" />
                             </div>
                             <div id="div.enable.symbian.os" <s:if test='dataSource.mdmType==0'>style='display:none'</s:if> <s:else>style="width:140px;display:inline;" </s:else>>
                               	<s:checkbox name="dataSource.enableSymbianOs"   /><s:text name="config.ssid.advanced.mdm.enrollment.symbianos" />
                             </div>
							</div>
							<div>
                             <div id="div.enable.windowsphone.os" <s:if test='dataSource.mdmType==0'>style='display:none'</s:if> <s:else>style="width:140px;display:inline;padding-right:10px;" </s:else>>
                               	<s:checkbox name="dataSource.enableWindowsphoneOs"   /><s:text name="config.ssid.advanced.mdm.enrollment.windowsphoneos" />
                             </div>
                             <div id="div.enable.blackberry.os" <s:if test='dataSource.mdmType==0'>style='display:none'</s:if> <s:else>style="width:140px;display:inline;padding-right:10px;" </s:else>>
								<s:checkbox name="dataSource.enableBlackberryOs"  /><s:text name="config.ssid.advanced.mdm.enrollment.blackberryos" />
                             </div>
                             <div id="div.enable.android.os" <s:if test='dataSource.mdmType==0'>style='display:none'</s:if> <s:else>style="width:140px;display:inline;padding-rightx 10px;" </s:else>>
								<s:checkbox name="dataSource.enableAndroidOs"   /><s:text name="config.ssid.advanced.mdm.enrollment.androidos" />
                             </div>
							</div>
							</s:else>
                             
						   </td>
					    </tr>
						<tr>
						    <td></td>
							<td class="noteInfo" height="15px" colspan="3">
								<s:text name="config.ssid.advanced.mdm.enrollment.osobj.note"/>
							</td>
						</tr>
						<tr> 
							<td class="labelT1" width="120px" style="padding-left:5px;">
								<label>
									<s:text name="config.ssid.advanced.mdm.enrollment.urlpath" /><font color="red">*</font>
								</label></td>
							<td colspan="3"><s:textfield
								name="dataSource.rootURLPath" size="48" maxlength="256" disabled="%{!dataSource.enableMDM}"/> &nbsp;<s:text
								name="character.range.1.to.256" /></td>
						</tr>
                         <tr id="airwatch.apiurl" <s:if test='dataSource.mdmType==0'>style='display:none'</s:if> > 
                           <td class="labelT1" width="120px" style="padding-left:5px;">
                             <label>
                               <s:text name="config.ssid.advanced.mdm.enrollment.apiurl.airwatch" /><font color="red">*</font>
                             </label></td>
                           <td colspan="3"><s:textfield
                             name="dataSource.apiURL" size="48" maxlength="256" disabled="%{!dataSource.enableMDM}"/> &nbsp;<s:text
                             name="character.range.1.to.256" /></td>
                         </tr>
                         <tr id="airwatch.apikey" <s:if test='dataSource.mdmType==0'>style='display:none'</s:if>> 
                           <td class="labelT1" width="120px" style="padding-left:5px;">
                             <label>
                               <s:text name="config.ssid.advanced.mdm.enrollment.apikey.airwatch" /><font color="red">*</font>
                             </label></td>
                           <td colspan="3"><s:textfield
                             name="dataSource.apiKey" size="24" maxlength="32" disabled="%{!dataSource.enableMDM}"/> &nbsp;<s:text
                             name="character.range.1.to.32" /></td>
                         </tr>
						 <tr>
							<td class="labelT1" width="120px" style="padding-left:5px;">
								<label>
									<s:text name="config.ssid.advanced.mdm.enrollment.username" /><font color="red">*</font>
								</label>
							</td>
							<td colspan="3">
								<s:textfield name="dataSource.mdmUserName" size="24" maxlength="32" />
								<s:text name="character.range.1.to.32"/>
							</td>
						 </tr>
						 <tr>
							<td class="labelT1" width="120px" style="padding-left:5px;">
								<label>
									<s:text name="config.ssid.advanced.mdm.enrollment.password" /><font color="red">*</font>
								</label>
							</td>
							<td colspan="3">
								<s:password name="dataSource.mdmPassword"  size="24" maxlength="32"  disabled="%{!dataSource.enableMDM}" 
									  id="mdmUserPassword" showPassword="true" />
								<s:textfield name="dataSource.mdmPassword"  size="24" maxlength="32" disabled="true"
									  id="userPassword_text" cssStyle="display: none;"/>
								<s:text name="character.range.1.to.32"/>
							</td>
						 </tr>
						 <tr>
							<td class="labelT1" width="120px" style="padding-left:5px;">
								<label>
									<s:text name="config.ssid.advanced.mdm.enrollment.password.confirm" /><font color="red">*</font>
								</label>
							</td>
							<td>
								<s:password size="24"  maxlength="32"  value="%{dataSource.mdmPassword}" disabled="%{!dataSource.enableMDM}"
									  id="cfUserPassword" showPassword="true"/>
								<s:textfield size="24" maxlength="32" value="%{dataSource.mdmPassword}" disabled="true"
									 id="cfUserPassword_text"  cssStyle="display: none;"/>
								<s:checkbox id="chkToggleDisplay" name="ignore"
										value="true" disabled="%{!dataSource.enableMDM}"
										onclick="hm.util.toggleObscurePassword(this.checked,['mdmUserPassword','cfUserPassword'],['userPassword_text','cfUserPassword_text']);" />
								<s:text name="admin.user.obscurePassword" />
							</td>
						</tr>
						<s:if test="%{supportAirWatchNonCompliance}">
						<tr id="enabledAWNonComplianceRow">
						  <td class="labelT1" colspan="3" style="padding-left:1px;"><s:checkbox name="dataSource.awNonCompliance.enabledNonCompliance" id="enabledNonCompliance"/><label for="enabledNonCompliance"><s:text name="glasgow_10.config.mdm.airwath.noncompliant.desc"/></label></td>
						</tr>
						<tr id="awNonComplianceRow">
						  <td colspan="4">
						      <div style="padding-left: 20px;">
						          <div id="fe_ErrorRowMethods" style="display: none"><div class="noteError" id="textfe_ErrorRowMethods">To be changed</div></div>
						          <div><s:text name="glasgow_10.config.mdm.airwath.noncompliant.notification.method"/>&nbsp;<s:checkboxlist name="notificationMethods" list="airWatchNotificationMethods" listKey="key" listValue="value"/></div>
						          <div id="fe_ErrorRowTitle" style="display: none"><div class="noteError" id="textfe_ErrorRowTitle">To be changed</div></div>
						          <div id="titleSection"><span style="padding-right:5px;"><s:text name="glasgow_10.config.mdm.airwath.noncompliant.title"/></span><s:textfield name="dataSource.awNonCompliance.title" id="notifyTitle" maxlength="32"/>&nbsp;<s:text name="config.ipFilter.name.range"/></div>
						          <div id="fe_ErrorRowMessage" style="display: none"><div class="noteError" id="textfe_ErrorRowMessage">To be changed</div></div>
						          <div style="position: relative;" id="messageSection"><span style="float:left;padding-right:25px;"><s:text name="glasgow_10.config.mdm.airwath.noncompliant.message"/><font color="red">*</font></span><s:textarea id="notifyMessage" name="dataSource.awNonCompliance.content" cssStyle="width:400px;height:100px;resize:none;" placeholder="%{getText('glasgow_10.config.mdm.airwath.noncompliant.defualt.message.note')}" maxlength="140"></s:textarea><span id="remainningChars" style="position: absolute;font-size: 0.8em;padding-left:5px;"></span>&nbsp;(1-140 characters)</div>
						      </div>
						      <div style="padding: 2px 0 2px 15px;">
						          <s:checkbox name="dataSource.awNonCompliance.disconnectVlanChanged" id="vlanChanged"/><label for="vlanChanged"><s:text name="glasgow_10.config.mdm.airwath.noncompliant.vlan.changed"/></label>
						      </div>
						      <div id="fe_ErrorRowInterval" style="display: none;padding: 2px 0 0 20px;"><div class="noteError" id="textfe_ErrorRowInterval">To be changed</div></div>
						      <div style="padding: 2px 0 2px 20px;">
							      <s:text name="glasgow_10.config.mdm.airwath.noncompliant.pooling.desc"/>
							      <s:textfield id="pollInterval" name="dataSource.awNonCompliance.pollingInterval" onkeypress="return hm.util.keyPressPermit(event,'ten');" maxlength="3" cssStyle="width:55px;"/>&nbsp;(30-600 seconds)
						      </div>
						  </td>
						</tr>
						</s:if>
					 	</table>
					
				</td>
			</tr>
			<%-- Mobile Device Management end --%>
			</table>
			</td>
		</tr>
		</table>
		</td>
		</tr>
	</table>
</s:form></div>
<script>
<s:if test="%{supportAirWatchNonCompliance}">
var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event;
var methods = {};
function hideAWOptions() {
    Get('notificationMethods-1').style.display = "none";
	Get('notificationMethods-1').checked = false;
	//Get('notificationMethods-1').disabled = true;
    if(YUD.getNextSibling('notificationMethods-1')) YUD.getNextSibling('notificationMethods-1').style.display = "none";
    
    Get('notificationMethods-2').style.display = "none";
    Get('notificationMethods-2').checked = false;
    //Get('notificationMethods-2').disabled = true;
    if(YUD.getNextSibling('notificationMethods-2')) YUD.getNextSibling('notificationMethods-2').style.display = "none";
}
function onloadEvent() {
	hideAWOptions();
	
	YUE.on('enabledNonCompliance', 'click', function(){
		setAWNonComplianceRowStyle(this.checked);
	});
	YUE.addListener(['notificationMethods-1', 'notificationMethods-2', 'notificationMethods-3'], 'click', function(){
		hm.util.hideFieldError();
		setAWSettingsRowStyle(parseInt(this.value), this.checked);
	});
	initEvent4TextArea(Get('notifyMessage'));
	
    if(<s:property value="dataSource.mdmType"/> != 1) {
        YUD.setStyle('enabledAWNonComplianceRow', 'display', 'none');
    }
	
	setAWNonComplianceRowStyle(<s:property value="dataSource.awNonCompliance.enabledNonCompliance"/>, true);
	
	setAWSettingsRowStyle(1, <s:property value="dataSource.awNonCompliance.notifyViaPush"/>);
	setAWSettingsRowStyle(2, <s:property value="dataSource.awNonCompliance.notifyViaSMS"/>);
	setAWSettingsRowStyle(4, <s:property value="dataSource.awNonCompliance.notifyViaEmail"/>);
}
function isEmptyObj(obj) {
	for(var name in obj) {
		return false;
	}
	return true;
}
function setAWSettingsRowStyle(value, flag) {
	var id = getChkMethodIdByValue(value);
    if(flag) {
        if(id == 'notificationMethods-3') { // email
            YUD.setStyle('titleSection', 'display', '');
        }
        YUD.setStyle('messageSection', 'display', '');
        
        methods[id] = true;
    } else {
        if(methods[id]) {
            delete methods[id];
        }
        if(isEmptyObj(methods)) {
            YUD.setStyle('titleSection', 'display', 'none');
            YUD.setStyle('messageSection', 'display', 'none');
        } else {
            if(!methods['notificationMethods-3']) {
                YUD.setStyle('titleSection', 'display', 'none');
            }
        }
    }
}
function getChkMethodIdByValue(value) {
	var label;
	switch(value) {
		case 1: label = 'notificationMethods-1';break;
		case 2: label = 'notificationMethods-2';break;
		case 4: label = 'notificationMethods-3';break;
		default: label = '';
	}
	return label;
}
function setAWNonComplianceRowStyle(flag, exclueMsg){
	hm.util.hideFieldError();
	
	YUD.setStyle('awNonComplianceRow', 'display', flag ? '' : 'none');
	if(!flag) {
		resetAWNonComplianceSettings(exclueMsg);
	}
}
function resetAWNonComplianceSettings(exclueMsg) {
	Get('enabledNonCompliance').checked = false;
	
	Get('notificationMethods-1').checked = false;
	Get('notificationMethods-2').checked = false;
	Get('notificationMethods-3').checked = false;
	setAWSettingsRowStyle(1, false);
	setAWSettingsRowStyle(2, false);
	setAWSettingsRowStyle(4, false);
	
	Get('vlanChanged').checked = false;
	
	Get('notifyTitle').value = '';
	if(!exclueMsg) {
		Get('notifyMessage').value = '<s:text name="glasgow_10.config.mdm.airwath.noncompliant.defualt.message"/>';
	}
	Get('pollInterval').value = '60';
}
function validateAWSettings() {
	if(Get('enabledNonCompliance').checked) {
		var pushM = Get('notificationMethods-1').checked,
		    smsM = Get('notificationMethods-2').checked,
		    emailM = Get('notificationMethods-3').checked,
		    title = Get('notifyTitle'),
		    message = Get('notifyMessage'),
		    interval = Get('pollInterval');
		
		var selected = pushM || smsM || emailM;
		if(selected) {
			if(emailM) {
				if(title.value.length == 0) {
					hm.util.reportFieldError({id: 'ErrorRowTitle'}, '<s:text name="error.requiredField"><s:param><s:text name="glasgow_10.config.mdm.airwath.noncompliant.title" /></s:param></s:text>');
					title.focus();
					return false;
				}
			}
			if(message.value.length == 0) {
				hm.util.reportFieldError({id: 'ErrorRowMessage'}, '<s:text name="error.requiredField"><s:param><s:text name="glasgow_10.config.mdm.airwath.noncompliant.message" /></s:param></s:text>');
				message.focus();
				return false;
			} else if(message.value.length > 140) {
				hm.util.reportFieldError({id: 'ErrorRowMessage'}, '<s:text name="glasgow_10.config.mdm.airwath.noncompliant.message.exceed.limitation"></s:text>');
				message.focus();
				return false;
			}
			
			var msg = hm.util.validateIntegerRange(interval.value, 'Interval', 30, 600);
			if(msg && msg.length > 0) {
				hm.util.reportFieldError({id: 'ErrorRowInterval'}, msg);
				interval.focus();
				return false;
			}
		} else {
			// none selction any notification
			hm.util.reportFieldError({id: 'ErrorRowMethods'}, '<s:text name="glasgow_10.config.mdm.airwath.noncompliant.choose.method"></s:text>');
			return false;
		}
	}
	return true;
}
function initEvent4TextArea(textArea){
	  if(/^[0-9]+$/.test(textArea.getAttribute("maxlength"))) {
		  var keyFunc = function(e) {
			   var keycode;
			    if(window.event) {
			        keycode = e.keyCode;
			    } else if(e.which) {
			        keycode = e.which;
			    }
			  var len = parseInt(this.getAttribute("maxlength"), 10);
			  if(this.value.length <= len) {
				  Get("remainningChars").innerHTML = (len - this.value.length) + ' characters remaining';
				  return true;
			  } else {
				  Get("remainningChars").innerHTML = 'exceed the characters limitation'
				  return false;
			  }
		  }
		  textArea.onkeyup = keyFunc;
	  }
}
<s:if test="%{jsonMode}">
window.setTimeout("onloadEvent()", 100);
</s:if>
<s:else>
YUE.onDOMReady(onloadEvent);
</s:else>
</s:if>
</script>
