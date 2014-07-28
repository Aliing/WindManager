<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<style>
#logoUploadContainer{float:left;margin: 0  0 0 10px}
#localFile{opacity:0;filter:alpha(opacity:0;);width:80px;position:absolute;}
#logoFilePathContainer{ width:150px; float:left;}
#logoFilePath{ width:100% }
</style>
<script>!window.jQuery && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>


<div id="content">

<s:if test="%{debugSwitch == false}">
	<p style="color:red">Visit reject. please contact Admin.</p>
</s:if>
<s:else>

<s:form action="schedulerDebug" id="schedulerDebugForm" name="schedulerDebugForm" method="post">
    <s:hidden name="operation" id="operation" value=""/>
</s:form>        

<textarea id="contentArea" name="contentArea" rows="6" cols="120" wrap="true">
</textarea>
<br/>  


<!-- 
<input type="button" value="AppFlowSendRequest" onclick="doExecute('appFlowSendRequest')">
<input type="button" value="AppFlowRollUp" onclick="doExecute('appFlowRollUp')">
<input type="button" value="SendDataToCopServer" onclick="doExecute('sendDataToCopServer')">
<br/>
clientMac:<input type="text" id="clientMac" name="clientMac" maxlength="20"><input type="button" value="FindClientCache" onclick="showContent('findClientCache')">
<input type="button" value="FindTop200ClientCache" onclick="showContent('findTop200ClientCache')">
<br/>
<input type="button" value="OpenLogDoubtfulClientInfo" onclick="doExecute('openLogDoubtfulClientInfo')"> 
<input type="button" value="CloseLogDoubtfulClientInfo" onclick="doExecute('closeLogDoubtfulClientInfo')"> 
<input type="button" value="MockClientInfoData" onclick="doExecute('mockClientInfoData')">
 -->      
<br/>
<input type="button" value="OpenWatchlistCleanNotification" onclick="showContent('openWatchlistCleanNotification')">
<input type="button" value="OpenWatchlistUpdateNotification" onclick="showContent('openWatchlistUpdateNotification')">

<table width="100%" border="0" cellspacing="0" cellpadding="0">

</table>
	 
<br/>
</s:else>
	
</div>

<script type="text/javascript" src="<s:url value="/js/hm.options.js" includeParams="none" />"></script>

<script language="javascript">

function getDomValue(id) {
	return document.getElementById(id).value;
}

function getDom(id) {
	return document.getElementById(id);
}

function test() {
	document.getElementById("contentArea").value = "1111111\r\n\r\n22222";
}

function doExecute(operation) {
	document.forms["schedulerDebugForm"].operation.value = operation;
	getDom("schedulerDebugForm").submit();
} 

var callbackShowContent = function(o) {
	var result = o.responseText;
	try {
		eval("var data = " + result);
		var content = "";
		if(data.message) {
			document.getElementById("contentArea").value = data.message;
		}
	} catch(e) {
		warnDialog.cfg.setProperty('text', "error.");
		warnDialog.show();
	}
	
};

var resultDoNothing = function(o) {
	warnDialog.cfg.setProperty('text', "error.");
	warnDialog.show();
};


function showContent(operation){
	//getDom("workspace.daily.recordDate").value = recordDate;
	var url = "<s:url action='schedulerDebug' includeParams='none' />?operation=" + operation;
	if (getDom("clientMac") != null) {
		url = url + "&clientMac=" + getDomValue("clientMac");
	}
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : callbackShowContent, failure : resultDoNothing, timeout: 60000}, null);	
}

</script>
