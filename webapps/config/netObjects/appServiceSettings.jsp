<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<div id="content">
	<s:form name="appService">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0" align="right" width="100%">
						<tr>
							<td class="dialogPanelTitle" width="100%" align="left">
								<s:text name="geneva_26.config.system.application.settings" />
							</td>
							<td align="right" style="padding-left:10px;" width="80px" nowrap><a href="javascript:void(0);" class="btCurrent"
							onclick="javascript: hideSelectServicePanel();" title="close"><img class="dinl" width="16px" height="16px" src="images/cancel.png" style="border:none;"/></a></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td align="center" style="padding-top: 2px; padding-bottom: 5px; width:100%;">
					<table border="0" cellspacing="0" cellpadding="0" align="center" style="margin-top: 15px;">
						<tr>
							<td align="left" colspan="2">
								<label><s:text name="geneva_26.config.system.application.settings.servicename" /></label>&nbsp;<s:property value="names"/>
							</td>
						</tr>
						<tr>
							<td align="left" style="padding-top:10px;">
								<label><s:text
										name="config.ns.idleTimeout" /><font color="red"><s:text name="*"/></font></label>&nbsp;
							</td>
							<td style="padding-top:10px;">
								<s:hidden name="operation"></s:hidden>
								<s:hidden name="ids"></s:hidden>
								<s:textfield maxlength="5" id="timeout" name="timeout"
								onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp;
							</td>
						</tr>
						<tr>
							<td colspan="2" style="padding-top:10px;">
								<s:text name="config.ns.idleTimeoutRange" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td align="center" style="padding-top: 2px; padding-bottom: 5px; width:100%;">
					<table border="0" cellspacing="0" cellpadding="0" align="center" style="margin-top: 15px;">
						<tr>
							<td>
								<input  class="button" id="selectedService" type="button" value="OK" onclick="doSubmitNewService()" name="select service" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</s:form>
</div>

<script>

function doSubmitNewService() {
	if(!validateIdleTimeout()){
		return;
	}
	var url= "<s:url action='appService' includeParams='none' />"+ "?ignore="+new Date().getTime();
	 document.forms["appService"].operation.value = "create";
	 YAHOO.util.Connect.setForm(document.forms["appService"]);
	 var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveAppService, failure : failSaveAppService, timeout: 60000}, null);
}

var succSaveAppService = function (o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			hideSelectServicePanel();
			return;
		}
	}catch(e){
		return;
	}
}

var failSaveAppService = function(o){
	
}

function validateIdleTimeout(){
	var inputElement = document.getElementById("timeout");
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ns.idleTimeout" /></s:param></s:text>');
		return false;
	}
	var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.ns.idleTimeout" />',
												<s:property value="0" />,
												<s:property value="65535" />);
	if (message != null) {
		hm.util.reportFieldError(inputElement, message);
		return false;
	}
	return true;
}

/* function loadPage() {
		document.getElementById("timeout").focus();
}

window.setTimeout("loadPage()", 500); */
</script>
