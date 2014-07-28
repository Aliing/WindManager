<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<link rel="stylesheet" type="text/css" href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<div id="content">
	<s:form name="hmServices">
		<s:hidden name="operation"></s:hidden>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0" align="right" width="100%">
						<tr>
							<td class="dialogPanelTitle" width="100%" align="left">
								<s:text name="glasgow_16.home.hmSettings.openDNS.title.deviceid.create" />
							</td>
							<td align="right" style="padding-left:10px;" width="80px" nowrap>
								<a href="javascript:void(0);" class="btCurrent" onclick="javascript: hideCreateOpenDNSDevicePanel();" title="close">
									<img class="dinl" width="16px" height="16px" src="images/cancel.png" style="border:none;"/>
								</a>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td align="center" style="padding-top: 2px; padding-bottom: 5px; width:100%;">
					<table border="0" cellspacing="0" cellpadding="0" align="center" style="margin-top: 15px;">
						<tr align="left">
							<td align="left">
								<label>
									<s:text name="glasgow_16.home.hmSettings.openDNS.title.deviceidlabel" /><font color="red"><s:text name="*"/></font>
								</label>&nbsp;						
							</td>
							<td>
								<s:textfield id="openDNSDeviceLabel" name="openDNSDeviceLabel"  onkeypress="return hm.util.keyPressPermit(event,'specialname');" size="40" maxlength="50"/>
								<s:text name="admin.management.updateOpenDNSServer.deviceLabel.range" />									
							</td>
						</tr>
<!-- 						<tr>
							<td align="center" style="padding-top: 10px; padding-bottom:10px; width:100%;" colspan="2">
								<input style="width:100px;" class="button" id="getDeviceID" type="button" value="Get Device ID" onclick="getDeviceID();" name="getDeviceID" />
							</td>
						</tr> -->
						<tr>
							<td colspan="2" style="height:10px"></td>
						</tr>							
						<tr align="left">
							<td align="left">
								<label>
									<s:text name="glasgow_16.home.hmSettings.openDNS.title.deviceidkey" />
								</label>&nbsp;												
							</td>
							<td>
								<s:textarea id="openDNSDeviceKey" name="openDNSDeviceKey" rows="2" cols="38" disabled="true" />		
							</td>
						</tr>
						<tr>
							<td style="height:10px" colspan=""/>
						</tr>
						<tr align="left">
							<td align="left">
								<label>
									<s:text name="glasgow_16.home.hmSettings.openDNS.title.deviceidid" />
								</label>&nbsp;												
							</td>
							<td>
								<s:textfield id="openDNSDeviceId" name="openDNSDeviceId" size="40" disabled="true" />		
							</td>
						</tr>												
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2" style="height:10px"></td>
			</tr>	
			<tr>
				<td colspan="2">
					<div id="note_createDeviceId" style="display: none">
						<table cellspacing="0" cellpadding="0" border="0"
							width="100%">
							<tr>
								<td height="10"></td>
							</tr>
							<tr align="center">
								<td class="noteInfo" id="info_createDeviceId"></td>
							</tr>
							<tr align="center">
								<td class="noteError" id="error_createDeviceId"></td>
							</tr>
						</table>
					</div>														
				</td>
			</tr>				
			<tr>
				<td align="center" style="padding-top: 2px; padding-bottom: 5px; width:100%;">
					<table border="0" cellspacing="0" cellpadding="0" align="center" style="margin-top: 15px;">
						<tr>
							<td>
								<input style="width:150px;" id="createDeviceId" class="button" type="button" value="Get/Create Device ID" onclick="getDeviceId();" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>

<script>
function getDeviceId() {
	$("#openDNSDeviceKey").attr("value","");
	$("#openDNSDeviceId").attr("value", "");
	
	if(!validateDeviceLabel()){
		return;
	}
	var openDNSDeviceLabel = document.getElementById("openDNSDeviceLabel").value;
	var url = "<s:url action='hmServices' includeParams='none' />?operation=createOpenDNSDeviceId&openDNSDeviceLabel="+openDNSDeviceLabel+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succCreateOpenDNSDevice, failure : failCreateOpenDNSDevice, timeout: 60000}, null);
	$("#createDeviceId").attr("value", "On Processing...");
	$("#createDeviceId").attr("disabled", true);
}

var succCreateOpenDNSDevice = function (o) {
	try {
		$("#createDeviceId").attr("value", "Get/Create Device ID");
		$("#createDeviceId").attr("disabled", false);
		eval("var details = " + o.responseText);
		if (details.isSuccess == true) {
			$("#info_createDeviceId").html(details.msg.valueOf());
			$("#error_createDeviceId").html("");
			$("#openDNSDeviceKey").attr("value", details.deviceKey.valueOf());
			$("#openDNSDeviceId").attr("value", details.deviceId.valueOf());
			if(details.isCreated){
				top.updateOpenDNSOptions(details.id.valueOf(), details.deviceLabel.valueOf());
			}			 
		} else {
			$("#error_createDeviceId").html(details.msg.valueOf());
			$("#info_createDeviceId").html("");
			$("#openDNSDeviceKey").attr("value","");
			$("#openDNSDeviceId").attr("value", "");
		}
		
		$("#note_createDeviceId").css("display","");
	    noteTimer = setTimeout("hideOpenDNSCreateDeviceMsg()", 10 * 1000);  // 5 seconds
	}catch(e){
		return;
	}
}

function hideOpenDNSCreateDeviceMsg(){
	$("#info_createDeviceId").html("");
	$("#error_createDeviceId").html("");
	$("#note_createDeviceId").css("display","none");
}

var failCreateOpenDNSDevice = function(o){
	 $("#createDeviceId").attr("value", "OK");
	 $("#createDeviceId").attr("disabled", false);
	 $("#error_createDeviceId").html("Network Error, Please Try again!");
	 $("#info_createDeviceId").html("");
	 $("#note_createDeviceId").css("display","");
	 noteTimer = setTimeout("hideOpenDNSCreateDeviceMsg()", 10 * 1000);  // 5 seconds
}

function validateDeviceLabel(){
	var inputElement = document.getElementById("openDNSDeviceLabel");
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="glasgow_16.home.hmSettings.openDNS.title.deviceidlabel" /></s:param></s:text>');
		return false;
	}else if(inputElement.value.length > 50){
    	hm.util.reportFieldError(accountID, '<s:text name="error.keyValueRange"><s:param><s:text name="glasgow_16.home.hmSettings.openDNS.title.deviceidlabel" /></s:param><s:param><s:text name="admin.management.updateOpenDNSServer.deviceLabel.range" /></s:param></s:text>');
    	inputElement.focus();
		return false;
    }
	
	return true;
}

</script>
