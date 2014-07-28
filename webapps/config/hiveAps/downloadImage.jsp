<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page pageEncoding="UTF-8"%>

<style type="text/css">
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
<script>
var formName = 'downloadImage';
var interval = 1;        // seconds
var timeoutId;

function onUnloadPage(){
	if (null != timeoutId)
		window.clearTimeout(timeoutId);
}

var detailsSuccess = function(o) {
	eval("var data = " + o.responseText);
	if ("" != data.error) {
		hm.util.reportFieldError(document.getElementById("noteError"), data.error);
	}
	var note = document.getElementById("noteInfo");
	var detail = document.getElementById("fileView");
	if ("" != data.error) {
		note.style.display="";
		detail.style.display="none";
	} else {
		note.style.display="none";
		detail.style.display="";
		var table = document.getElementById("result");
		var totalValue = data.v;
		for(var i=0; i<totalValue.length; i++){
			var rowData = totalValue[i];
			if(i+1 < table.rows.length){
				table.deleteRow(i+1);
			}
			var row = table.insertRow(i+1);
			for(var j=0; j<6; j++){
				var cell = row.insertCell(j);
				cell.className = 'list';
				var value = "";
				switch(j) {
					case 0:
						value = rowData.fileVersion;
						break;
					case 1:
						value = rowData.fileType;
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
					default:
						break;
				}
				cell.innerHTML = value + "&nbsp;";
			}
		}
		if (data.flag) {
			window.clearTimeout(timeoutId);
		} else {
			timeoutId = window.setTimeout("requestDownload()", interval * 1000);
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

function getUpdatesFromLS() {
	var hardwareId = document.forms[formName].hwMode.value;
	var url = "<s:url action='downloadImage' includeParams='none'/>" + "?operation=download&hwMode="+hardwareId+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function requestDownload() {
	var url = "<s:url action='downloadImage' includeParams='none'/>" + "?operation=refresh&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="downloadImage">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td height="4"></td>
			</tr>		
		</table>
		<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="680px">
			<tr>
				<td class="noteError" style="padding:10px 10px 0 10px">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td id="noteError" colspan="7">
							<label></label>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr id="noteInfo">
				<td style="padding:10px 10px 0 10px">
					<div>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr style="display:<s:property value="%{updateDisplay}"/>">
								<td class="labelT1"><s:text name="license.server.available.software.update.hiveos" /></td>
							</tr>
							<tr style="display:<s:property value="%{downloadDisplay}"/>">
								<td class="labelT1" width="90"><s:text name="monitor.hiveAp.model"/></td>
								<td><s:select name="hwMode" list="%{hardwareList}" cssStyle="width: 200px" /></td>
								<td style="padding-left:5px">
									<input type="button" name="ignore" value="Download" class="button"
									onclick="getUpdatesFromLS();" <s:property value="writeDisabled" /> /></td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
			<tr style="display:none" id="fileView">
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
										<s:text name="monitor.hiveAp.model" />
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
	</s:form>
</div>
