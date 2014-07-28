<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script type="text/javascript">
var formName = "multicastMonitor";

function onLoadPage(){
	var hostName = "<s:property value="%{stringForTitle}"/>";
	
	if(hostName){
		top.updateMulticastMonitorTitle(hostName);
	}
	
	//save this reference on the parent
	top.multicastMonitorIFrameWindow = window;
}

function requestAction(action){
	if(validate(action)){
		
		if(null != top.waitingPanel){
			top.waitingPanel.show();
		}
		
		var url = "<s:url action="mapNodes" includeParams="none"></s:url>" + "?ignore="+ + new Date().getTime();
		document.forms[formName].operation.value = action;
		ajaxRequest(formName, url, requestActionProcess);
	}
}

var requestActionProcess = function(o){
	if(null != top.waitingPanel){
		top.waitingPanel.hide();
	}
		
	eval("var result = " + o.responseText);
	
	var cliDiv = document.getElementById("multicastMonitorResult");
	if(result.e){ 
		//cliDiv.innerHTML = result.e;
		cliDiv.innerHTML = "<pre>" + result.e.replace(/\n/g,"<br>") + "</pre>";
	}
	if (result.v) {
		cliDiv.innerHTML = "<pre>" + result.v.replace(/\n/g,"<br>") + "</pre>";
	}
}


function cancel() {
	top.closeMulticastMonitorPanel();
}

function validate(action) {
	return true;
}

</script>

<div><s:form action="mapNodes" 
			id="multicastMonitor" name="multicastMonitor" 
			method="POST">
<s:hidden name="operation" />
<s:hidden name="hiveApId" />
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td class="labelT1" width="90"><label><s:text
									name="topology.menu.diagnostics.showmulticastmonitor.interface" /><font color="red"><s:text name="*"/></font></label></td>
		<td>
			<s:select name="wifiInterfaceId" list="%{availableWifiInterface}" listKey="id" listValue="value"
				cssStyle="width: 150px;" />
		</td>
		<td>
			<input id="show" type="button" name="ignore" value="Show"
						class="button" onClick="requestAction('showMulticastMonitor');">
		</td>
	</tr>
	<tr>
		   <td style="padding:0px 4px 0px 4px;" colspan="3">
		     <table cellspacing="0" cellpadding="0" border="0" width="100%">
		        <tr>
		         <td class="sepLine" colspan="3"><img
				   src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
				</tr>
			 </table>
			</td>
		</tr>
	<tr>
		<td colspan="3">
			<table border="0" cellspacing="0" cellpadding="0" width="490px">
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td id = "multicastMonitorResult"></td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</s:form></div>