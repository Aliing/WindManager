<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=7;FF=3;OtherUA=4" />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="Cache-Control" content="no-cache" />
<style>
.yui-skin-sam {
	white-space:nowrap;
	border:medium none;
}

.a0{
	float:left;
	margin-left:0px;
	width:102px;
	height:15px;
	border:1px solid #5B94DF;
}
.a1{
	float:left;
	height:13px;width:0px;font-size:12px;
	border-left:1px solid  #FFFFFF;
	border-top:1px solid  #FFFFFF;
	border-bottom:1px solid  #FFFFFF;
	background-Color:#8cd92b;
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#66f900, endColorstr=#81ACE7);
}
a.actionType{
	padding: 2px 2px 2px 0;
	text-decoration: none;
}
a.actionType:hover {
	color: #CC3300;
	text-decoration: underline;
}

.currentState{
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;
	font-style: italic;
}
</style>
		<script src="<s:url value="/js/hm.util.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

		<!-- CSS -->
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/container/assets/skins/sam/container.css" includeParams="none"/>?v=<s:property value="verParam" />" />
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/menu/assets/skins/sam/menu.css" includeParams="none"/>?v=<s:property value="verParam" /> " />
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/fonts/fonts-min.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/button/assets/skins/sam/button.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/autocomplete/assets/skins/sam/autocomplete.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/css/hm.css" includeParams="none"/>?v=<s:property value="verParam" />" />
		<!-- JS -->
		<script
			src="<s:url value="/yui/utilities/utilities.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
		<script
			src="<s:url value="/yui/container/container-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
		<script
			src="<s:url value="/yui/menu/menu-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
		<script
			src="<s:url value="/yui/button/button-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
		<script
			src="<s:url value="/yui/datasource/datasource-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
		<script
			src="<s:url value="/yui/autocomplete/autocomplete-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script>
var formName = 'downloadSoft';
var interval = 1;        // seconds
var timeoutId;

function onLoadEvent() {
	<s:if test="%{isDownloading}">
		refreshList();
	</s:if>
}

function onUnloadEvent() {
	hm.util.onUnloadValidation();
}

function requestDownload(imgId) {
	var url = "<s:url action="downloadSoft" includeParams='none'/>" + "?operation=download&imageId="+imgId+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function refreshList() {
	var closePannel = document.getElementById("autoClosePanel");
	if (closePannel.style.display == "none") {
		closePannel.style.display = "";
	}
	var url = "<s:url action="downloadSoft" includeParams='none'/>" + "?operation=refresh&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

var detailsSuccess = function(o) {
	eval("var data = " + o.responseText);
	if ("" != data.error) {
		hm.util.reportFieldError(document.getElementById("noteErrorTd"), data.error);
	}
	var table = document.getElementById("result");
	var totalValue = data.v;
	if (totalValue) {
		for(var i=0; i<totalValue.length; i++){
			var rowData = totalValue[i];
			if(i+1 < table.rows.length){
				table.deleteRow(i+1);
			}
			var row = table.insertRow(i+1);
			for(var j=0; j<7; j++){
				var cell = row.insertCell(j);
				cell.className = 'list';
				var value = "";
				switch(j) {
					case 0:
						value = rowData.fileVersion;
						break;
					case 1:
						value = rowData.fileUid;
						break;
					case 2:
						value = rowData.fileSize;
						break;
					case 3:
						value = rowData.status;
						break;
					case 4:
						value = rowData.rate;
						break;
					case 5:
						value = rowData.time;
						break;
					case 6:
						value = rowData.action;
						break;
					default:
						break;
				}
				cell.innerHTML = value + "&nbsp;";
			}
		}
		if (data.flag) {
			window.clearTimeout(timeoutId);
			if (document.getElementById("autoClose").checked) {
				window.setTimeout("top.getNewDescription()", 3 * 1000);
				top.document.getElementById("downloadFilePanel").style.display = "none";
				top.downloadFilePanel = null;
			}
		} else if ("" == data.error) {
			timeoutId = window.setTimeout("refreshList()", interval * 1000);
		}
	}
};

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};
</script>
</head>
<body class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0" onload="onLoadEvent();"
	marginheight="0" onunload="onUnloadEvent();" >
<div id="content">
<s:form action="downloadSoft">
	<s:hidden name="operation" />
		<table border="0" cellspacing="0" cellpadding="0" width="600px">
			<tr>
				<td style="padding:10px 10px 0 10px">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="noteError">
							<label id="noteErrorTd"><s:property value="%{errorMes}" /></label>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="padding:10px 10px 0 10px">
					<fieldset>
						<legend>
							<s:text name="license.server.available.software.version"/>
						</legend>
						<div>
							<table cellspacing="0" cellpadding="0" border="0" id="result">
								<tr>
									<th align="left">
										<s:text name="license.server.file.version.title" />
									</th>
									<th align="left">
										<s:text name="license.server.file.uid.title" />
									</th>
									<th align="left">
										<s:text name="admin.certificates.fileSize" />
									</th>
									<th align="left">
										<s:text name="license.server.file.status.title" />
									</th>
									<th align="left">
										<s:text name="license.server.file.rate.title" />
									</th>
									<th align="left">
										<s:text name="license.server.file.time.title" />
									</th>
									<th align="left">
										<s:text name="license.server.file.action.title" />
									</th>
								</tr>
								<s:iterator value="%{imageList}" status="status">
									<tr class="list">
										<td class="list"><s:property value="%{imageVersion}" />&nbsp;</td>
										<td class="list"><s:property value="%{imageUid}" />&nbsp;</td>
										<td class="list"><s:property value="%{fileSizeByByte}" />&nbsp;</td>
										<td class="list"><s:property value="%{statusString}" escape="false" />&nbsp;</td>
										<td class="list"><s:property value="%{rateString}" escape="false" />&nbsp;</td>
										<td class="list"><s:property value="%{timeString}" />&nbsp;</td>
										<td class="list"><s:property value="%{actionTypeString}" escape="false" />&nbsp;</td>
									</tr>
								</s:iterator>
								<s:if test="%{imageList.size() == 0}">
									<s:generator separator="," val="%{' '}" count="3">
										<s:iterator>
											<tr>
												<td class="list" colspan="6">&nbsp;</td>
											</tr>
										</s:iterator>
									</s:generator>
								</s:if>	
							</table>
						</div>
					</fieldset>
				</td>
			</tr>
			<tr style="display:none" id="autoClosePanel">
				<td style="padding:5px 20px 0 10px" align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="headCheck"><s:checkbox id="autoClose" name="autoClose" /></td>
							<td style="padding-left: 2px"><s:text name="license.server.file.checkbox.title" /></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="5"></td>
			</tr>
		</table>
	</s:form>
</div>
</body>
</html>
