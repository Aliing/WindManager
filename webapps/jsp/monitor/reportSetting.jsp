<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<style>
#logoUploadContainer{float:left;margin: 0  0 0 10px}
#localFile{opacity:0;filter:alpha(opacity:0;);width:80px;position:absolute;cursor: pointer;}
#logoFilePathContainer{ width:350px; float:left;}
#logoFilePath{ width:100% }
</style>
<script>!window.jQuery && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
<script src="<s:url value="/js/innerhtml.js" />"></script>
<script src="<s:url value="/js/widget/tableSort.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />	
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/tableSort.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/tabview/assets/skins/sam/tabview.css" includeParams="none" />" />
<style type="text/css">
.divcontainer {
	border-top: 1px solid #666666;
	border-bottom: 1px solid #666666;
	overflow-x: auto;
	overflow-y: hidden;
	width: 560px;
	height: 30px;
}
.customdivcontainer {
	border-top: 1px solid #666666;
	border-bottom: 1px solid #666666;
	overflow-x: auto;
	overflow-y: hidden;
	width: 560px;
	height: 30px;
}
div.container {
	border-bottom: 1px solid #666666;
	overflow-x: hidden;
	overflow-y: auto;
	width: 560px;
	height: 250px;
}
table.show {
	width: 560px;
}
.thchk {
	width: 30px;
    background-color: #EEEEEE;
    vertical-align: middle;
}
.thapphead{
	width: 130px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
.thhead{
	width: 150px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
.thgrouphead{
	width: 110px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
.thcustomapphead{
	width: 160px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
.thcustomhead{
	width: 170px;
    background-color: #EEEEEE;
    color: #4F4F4F;
    text-align: left;
    vertical-align: middle;
    cursor: pointer;
}
table.appshow tr td {
	height: 20px;
}
.tdsystemchk{
	padding-left: 7px;
	width: 30px;
	vertical-align: middle;
}
.tdsystemappname{width:120px;word-break:break-all;}
.tdsystemusage{
	text-align: right;
	width: 130px;
	vertical-align: middle;
}
.tdsystemgroup{
	width: 115px;
	padding-left: 18px;
	word-break:break-all;
}
.tdcustomchk{
	padding-left: 8px;
	width: 30px;
	vertical-align: middle;
}
.tdcustomappname{width:160px;word-break:break-all;}
.tdcustomusage{
	text-align: right;
	width: 160px;
	padding-right: 5px;
}

#system_search_key{
border-radius:10px 10px 10px 10px;
color: #777777;
font-size: 12px;
border: 1px solid #DDDDDD;
height: 15px;
width: 150px;
padding: 2px 2px 2px 6px;
}
#custom_search_key{
border-radius:10px 10px 10px 10px;
color: #777777;
font-size: 12px;
border: 1px solid #DDDDDD;
height: 15px;
width: 150px;
padding: 2px 2px 2px 6px;
}
#system_search_groupkey{
border-radius:10px 10px 10px 10px;
color: #777777;
font-size: 12px;
border: 1px solid #DDDDDD;
height: 15px;
width: 150px;
padding: 2px 2px 2px 6px;
}
#searchKeyContainerDiv{width: 150px; padding-top: 2px;height:22px;}
#searchGroupKeyContainerDiv{width: 150px; padding-top: 2px;height:22px;}
#searchCustomKeyContainerDiv{width: 150px; padding-top: 2px;height:22px;}
.searchElement{display: block;float: left;}

.ul {
    width:40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UL.png) no-repeat left top;
    }
    .um {
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UM.png) repeat-x center top;
    }
    .ur {
    width:40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UR.png) no-repeat right top;
    }
    .ml {
    width:40px;
    height:100%;
    background: transparent url(images/hm_v2/popup/HM-Popup-ML.png) repeat-y 0% 50%;
    }
    .mm {
    height:100%;
    background-color: #f9f9f7;
    }
    .mr {
    width:40px;
    height:100%;
    background: transparent url(images/hm_v2/popup/HM-Popup-MR.png) repeat-y 100% 50%;
    }
    .bl {
    width: 40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LL.png) no-repeat left bottom;
    }
    .bm {
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LM.png) repeat-x center bottom;
    }

	.br {
	width: 40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LR.png) no-repeat right bottom;
    }

#newCustomAppPanelId.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}
</style>


<script>
var formName = 'reportSetting';
var maxAppCount = <s:property value="watchlistLimitation"/>;
//#EEEEEE #fff
function submitAction(operation) {
	if (validate(operation)) {
			
		if (operation == 'update')
		{
			showProcessing();
		}

		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) 
{
	if (operation != 'update')
	{
		return true;
	}
	
	var maxPerfRecord = document.getElementById("maxPerfRecord");
	var maxClientHistory = document.getElementById("maxClientHistory");
	if (maxPerfRecord!=null && maxPerfRecord!=undefined) {
		if (maxPerfRecord.value.length == 0) 
		{
	        hm.util.reportFieldError(maxPerfRecord, '<s:text name="error.requiredField"><s:param><s:text name="admin.logSet.maxPerfRecord" /></s:param></s:text>');
	        maxPerfRecord.focus();
	        return false;
	    }
	    else if (!isValidMaxPerfRecords(maxPerfRecord.value)) 
	    {
	    	<s:if test="%{hMOnline == false}">
				hm.util.reportFieldError(maxPerfRecord, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.logSet.maxPerfRecord" /></s:param><s:param><s:text name="admin.logSet.maxPerfRecordRange" /></s:param></s:text>');
			</s:if>
			<s:else>
				hm.util.reportFieldError(maxPerfRecord, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.logSet.maxPerfRecord" /></s:param><s:param><s:text name="admin.logSet.maxPerfRecordRange.online" /></s:param></s:text>');
			</s:else>
			maxPerfRecord.focus();
			return false;
		}
	}
	if (maxClientHistory!=null && maxClientHistory!=undefined) {
		if (maxClientHistory.value.length == 0) 
		{
	        hm.util.reportFieldError(maxClientHistory, '<s:text name="error.requiredField"><s:param><s:text name="admin.logSet.maxHistoryClientRecord" /></s:param></s:text>');
	        maxClientHistory.focus();
	        return false;
	    }
	    else if (!isValidMaxHistoryClientRecords(maxClientHistory.value)) 
	    {
	    	<s:if test="%{hMOnline == false}">
	    		hm.util.reportFieldError(maxClientHistory, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.logSet.maxHistoryClientRecord" /></s:param><s:param><s:text name="admin.logSet.maxHistoryClientRecordRange" /></s:param></s:text>');
			</s:if>
			<s:else>
				hm.util.reportFieldError(maxClientHistory, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.logSet.maxHistoryClientRecord" /></s:param><s:param><s:text name="admin.logSet.maxHistoryClientRecordRange.online" /></s:param></s:text>');
			</s:else>
	    	maxClientHistory.focus();
			return false;
		} 
	}
	<s:if test="%{hMOnline}">
	if (!validateValue("intervalTablePartPer",'<s:text name="reports.reportsetting.interval.performance.table"/>', 1, 7)) {
		return false;
	}
	if (!validateValue("maxTimeTablePerSave",'<s:text name="reports.reportsetting.maxtime.performance.table.datasave"/>', 1, 30)) {
		return false;
	}
	if (!validateValue("intervalTablePartCli",'<s:text name="reports.reportsetting.interval.client.table"/>', 8, 72)) {
		return false;
	}
	if (!validateValue("maxTimeTableCliSave",'<s:text name="reports.reportsetting.maxtime.client.table.datasave"/>', 1, 30)) {
		return false;
	}
	</s:if>
	if (!validateValue("maxOriginalCount",'<s:text name="admin.logSet.reportSummary.originalValue"/>', 24, 48)) {
		return false;
	}
	if (!validateValue("maxHourValue",'<s:text name="admin.logSet.reportSummary.hourValue"/>', 2, 7)) {
		return false;
	}
	if (!validateValue("maxDayValue",'<s:text name="admin.logSet.reportSummary.dayValue"/>', 5, 8)) {
		return false;
	}
	if (!validateValue("maxWeekValue",'<s:text name="admin.logSet.reportSummary.weekValue"/>', 12, 24)) {
		return false;
	}
	if (!validateValue("maxSupportAp",'<s:text name="admin.logSet.reportSummary.maxApValue"/>', 10, 999)) {
		return false;
	}
	if (!validateValue("slaPeriod",'<s:text name="admin.logSet.reportSummary.slaPeriod"/>', 1, 10)) {
		return false;
	}
	if (!validateValue("clientPeriod",'<s:text name="admin.logSet.reportSummary.clientPeriod"/>', 5, 30)) {
		return false;
	}
	if (!validateValue("reportIntervalMinute",'<s:text name="admin.logSet.reportSummary.reportIntervalMinute"/>', 1, 30)) {
		return false;
	}
	if (!validateValue("reportMaxApCount",'<s:text name="admin.logSet.app.setting.lasthour.vhm"/>', 1, 100)) {
		return false;
	}
	if (!validateValue("reportMaxApForSystem",'<s:text name="admin.logSet.app.setting.lasthour.hm"/>', 1, 200)) {
		return false;
	}
	if (!validateValue("reportDbHourly",'<s:text name="admin.logSet.app.setting.usage.hourly"/>', 1000000, 100000000)) {
		return false;
	}
	if (!validateValue("reportDbDaily",'<s:text name="admin.logSet.app.setting.usage.daily"/>', 5000000, 100000000)) {
		return false;
	}
	if (!validateValue("reportDbWeekly",'<s:text name="admin.logSet.app.setting.usage.weekly"/>', 1000000, 100000000)) {
		return false;
	}
	if (!validateApplication()) {
		return false;
	}
	
	return true;
}

function validateApplication() {
	var selectedAppIds = "";
	var selectedCustomAppIds = "";
	var list = getDom('rightAppTable').getElementsByTagName('tr');
	if (list.length > maxAppCount) {
		var rightAppDiv = getDom('messageError');
		hm.util.reportFieldError(rightAppDiv, 'Maximum number of applications allowed is ' + maxAppCount);
		//rightAppDiv.focus();
		getDom('addAppButton').focus();
    	return false;
	}
	for(var i = 0; i < list.length; i++) {
		var appType = list[i].getAttribute("appType");
		if(appType){
			if(0 == appType){
				selectedAppIds = selectedAppIds + list[i].getAttribute("appId") + ",";
			}else{
				selectedCustomAppIds = selectedCustomAppIds + list[i].getAttribute("appId") + ",";
			}
		}
	}
	if (selectedAppIds.length > 0) {
		selectedAppIds = selectedAppIds.substring(0, selectedAppIds.length - 1);
	}
	if (selectedCustomAppIds.length > 0) {
		selectedCustomAppIds = selectedCustomAppIds.substring(0, selectedCustomAppIds.length - 1);
	}
	document.getElementById("selectedAppIds").value = selectedAppIds;
	document.getElementById("selectedCustomAppIds").value = selectedCustomAppIds;
	return true;
}

function validateValue(pid, label, min, max) {
    var inputElement = document.getElementById(pid);
    if (inputElement == null) {
    	return true;
    }
    if (inputElement.value.length == 0) {
          hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>'+ label+ '</s:param></s:text>');
          inputElement.focus();
          return false;
    }
    var message = hm.util.validateIntegerRange(inputElement.value, label, min, max);
    if (message != null) {
          hm.util.reportFieldError(inputElement, message);
          inputElement.focus();
          return false;
    }
    return true;
}

function isValidMaxPerfRecords(value)
{
	var intValue = value.valueOf();
	<s:if test="%{hMOnline == false}">
		if ( value >=20000 && intValue <= 500000 )
		{
			return true;
		}
	</s:if>
	<s:else>
		if ( value >=20000 && intValue <= 2000000 )
		{
			return true;
		}
	</s:else>
	
	return false;
}

function isValidMaxHistoryClientRecords(value)
{
	var intValue = value.valueOf();
	<s:if test="%{hMOnline == false}">
		if ( value >=500000 && intValue <= 3000000 )
		{
			return true;
		}
	</s:if>
	<s:else>
		if ( value >=500000 && intValue <= 10000000 )
		{
			return true;
		}
	</s:else>
	
	return false;
}
function GetPos(obj,oType) 
{   
    var pos=obj["offset"+oType]; 
    var objParent=obj.offsetParent; 
     
    while(objParent.tagName.toUpperCase()!="BODY") 
    {   
        pos+=objParent["offset"+oType]; 
        objParent=objParent.offsetParent;   
    }   
    return pos; 
} 
function adjustPosition(e)
{
	 var event=$.event.fix(e); 
	 document.getElementById("localFile").style.left=GetPos(event.target,"Left")+"px"; 
	 document.getElementById("localFile").style.top=GetPos(event.target,"Top")+"px";     
}

function showLogoPath()
{
	$("#logoFilePath").val($("#localFile").val());
}
function uploadImage()
{
	var localFile = document.getElementById("localFile"),
    logoFilePathContainer=document.getElementById("logoFilePathContainer");
	if ( localFile.value.length == 0) {
	     hm.util.reportFieldError(logoFilePathContainer, '<s:text name="error.requiredField"><s:param><s:text name="admin.updateSoftware.filePath" /></s:param></s:text>');
	     return false;
	 	}
	else{
		var fileExtension=localFile.value.substring(localFile.value.lastIndexOf('.')+1,localFile.value.length);
		if(fileExtension.toLowerCase()!="png")
			{
			hm.util.reportFieldError(logoFilePathContainer, '<s:text name="error.report.logo.image.format"></s:text>');
	      return false;
			}
	}
	localFile.form.operation.value="uploadLogo";
	localFile.form.submit();
}

function deleteImage()
{
	var handleYes = function() {
	    this.hide();
		document.forms["upload"].operation.value="deleteLogo";
		document.forms["upload"].submit();
	};

	var handleNo = function() {
	    this.hide();
	};
	var confirmDialog =
	     new YAHOO.widget.SimpleDialog("confirmDialog",
	              { width: "350px",
	                fixedcenter: true,
	                visible: false,
	                draggable: true,
	                modal:true,
	                close: true,
	                text: "<html><body>This operation will remove the current logo image.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>",
	                icon: YAHOO.widget.SimpleDialog.ICON_WARN,
	                constraintoviewport: true,
	                buttons: [ { text:"Yes", handler:handleYes, isDefault:true },
	                           { text:"&nbsp;No&nbsp;", handler:handleNo } ]
	              } );
	     confirmDialog.setHeader("Confirm");
	     confirmDialog.render(document.body);
	     confirmDialog.show();

}

function resizeImage()
{
	var image=$("#logoImage")[0],
		 preWidth=100,
		 preHeight=37;
	if(image)
	{
		 if(image.width>0 && image.height>0){
			    if(image.width/image.height>= preWidth/preWidth){
				     if(image.width>preWidth){  
				    	 image.height=(image.height*preWidth)/image.width;
				    	 image.width=preWidth;
				     }
				     image.alt=image.width+"*"+image.height;
			     }
			    else{
				     if(image.height>preHeight){  
				    	 image.width=(image.width*preHeight)/image.height;     
				    	 image.height=preHeight;
				     }
				     image.alt=image.width+"*"+image.height;
			     }
		 }
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}
function onLoadPage(){
	resizeImage();
	createWaitingPanel();
	//addContent();
	initAppAutoSearch();
	initGroupAutoSearch();
	initCustomAutoSearch();
}
function addContent(){
$("#leftSystemAppTable").html(getDom("appContent").innerHTML);
$("#appContent").html("");
}

var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"260px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("Preparing resources...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

</script>

<div id="content">
	<s:form action="reportSetting">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<input type="button" name="update" value="Update" class="button"
									onClick="submitAction('update');"
									<s:property value="writeDisabled" />>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td style="padding-right: 10px">
					<table class="editBox" style="border-bottom:0px; border-radius: 10px 10px 0 0; " cellspacing="0" cellpadding="0" border="0" width="100%">
						<s:if test="%{isInHomeDomain}">
						<tr><td><fieldset><legend><s:text name="admin.logSet.device.title" /></legend><table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td class="labelT1" width="40%">
								<label>
									<s:text name="admin.logSet.maxPerfRecord" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="maxPerfRecord" name="maxPerfRecord"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="7" />
								<s:if test="%{hMOnline==false}">
									<s:text name="admin.logSet.maxPerfRecordRange" />
								</s:if>
								<s:else>
									<s:text name="admin.logSet.maxPerfRecordRange.online" />
								</s:else>
								
							</td>
						</tr>
						<tr>
							<td class="labelT1">
								<label>
									<s:text name="admin.logSet.maxHistoryClientRecord" /><font color="red"><s:text name="*" /> </font>
								</label>
							</td>
							<td>
								<s:textfield id="maxClientHistory" name="maxClientHistory"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="8" />
								<s:if test="%{hMOnline==false}">
									<s:text name="admin.logSet.maxHistoryClientRecordRange" />
								</s:if>
								<s:else>
									<s:text name="admin.logSet.maxHistoryClientRecordRange.online" />
								</s:else>
							</td>
						</tr>
						
						<s:if test="%{hMOnline==false}">
						<tr>
							<td class="labelT1" width="40%">
								<s:text name="admin.logSet.interface.pollPeriod" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:select name="interfacePollInterval"
									list="%{pollIntervalList}" listKey="key" listValue="value"
									cssStyle="width: 135px;" />
							</td>
						</tr>
						</s:if>
						<s:if test="%{hMOnline==false}">
						<tr>
							<td class="labelT1">
								<s:text name="admin.logSet.statistic.start.minute" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:select name="statsStartMinute"
									list="%{statsStartMinuteList}" listKey="key" listValue="value"
									cssStyle="width: 135px;" />
							</td>
						</tr>
						</s:if>
						<s:if test="%{hMOnline}">
							<tr style="display:none;">
								<td class="labelT1" width="40%">
									<label>
										<s:text name="reports.reportsetting.interval.performance.table" /><font color="red"><s:text name="*" /> </font>
									</label>
								</td>
								<td>
									<s:textfield id="intervalTablePartPer" name="intervalTablePartPer"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										maxlength="8" />
										<s:text name="reports.reportsetting.interval.performance.table.valueRange" />
								</td>
							</tr>
							<tr>
								<td class="labelT1">
									<label>
										<s:text name="reports.reportsetting.maxtime.performance.table.datasave" /><font color="red"><s:text name="*" /> </font>
									</label>
								</td>
								<td>
									<s:textfield id="maxTimeTablePerSave" name="maxTimeTablePerSave"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										maxlength="8" />
										<s:text name="reports.reportsetting.maxtime.performance.table.datasave.valueRange" />
								</td>
							</tr>
							<tr style="display:none;">
								<td class="labelT1">
									<label>
										<s:text name="reports.reportsetting.interval.client.table" /><font color="red"><s:text name="*" /> </font>
									</label>
								</td>
								<td>
									<s:textfield id="intervalTablePartCli" name="intervalTablePartCli"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										maxlength="8" />
									<s:text name="reports.reportsetting.interval.client.table.valueRange" />
								</td>
							</tr>
							<tr>
								<td class="labelT1">
									<label>
										<s:text name="reports.reportsetting.maxtime.client.table.datasave" /><font color="red"><s:text name="*" /> </font>
									</label>
								</td>
								<td>
									<s:textfield id="maxTimeTableCliSave" name="maxTimeTableCliSave"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										maxlength="8" />
										<s:text name="reports.reportsetting.maxtime.client.table.datasave.valueRange" />
								</td>
							</tr>
						</s:if>
						</table></fieldset></td></tr>
						<tr><td height="8px"></td></tr>
						<tr><td style="display: <s:property value="reportSummarySettingDisplay"/>">
						<fieldset><legend><s:text name="admin.logSet.networkreport.title" /></legend><table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr style="display: <s:property value="reportSummarySettingDisplay"/>">
							<td class="labelT1" width="40%">
								<s:text name="admin.logSet.reportSummary.originalValue" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="maxOriginalCount" name="maxOriginalCount"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="2" />
								<s:text name="admin.logSet.reportSummary.originalValueRange" />
							</td>
						</tr>
						<tr style="display: <s:property value="reportSummarySettingDisplay"/>">
							<td class="labelT1">
								<s:text name="admin.logSet.reportSummary.hourValue" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="maxHourValue" name="maxHourValue"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="2" />
								<s:text name="admin.logSet.reportSummary.hourValueRange" />
							</td>
						</tr>
						<tr style="display: <s:property value="reportSummarySettingDisplay"/>">
							<td class="labelT1">
								<s:text name="admin.logSet.reportSummary.dayValue" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="maxDayValue" name="maxDayValue"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="2" />
								<s:text name="admin.logSet.reportSummary.dayValueRange" />
							</td>
						</tr>
						<tr style="display: <s:property value="reportSummarySettingDisplay"/>">
							<td class="labelT1">
								<s:text name="admin.logSet.reportSummary.weekValue" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="maxWeekValue" name="maxWeekValue"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="2" />
								<s:text name="admin.logSet.reportSummary.weekValueRange" />
							</td>
						</tr>
						<tr style="display: none;">
							<td class="labelT1">
								<s:text name="admin.logSet.reportSummary.maxApValue" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="maxSupportAp" name="maxSupportAp"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="3" />
								<s:text name="admin.logSet.reportSummary.maxApValueRange" />
							</td>
						</tr>
						<tr style="display: <s:property value="reportSummarySettingDisplay"/>">
							<td class="labelT1">
								<s:text name="admin.logSet.reportSummary.slaPeriod" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="slaPeriod" name="slaPeriod"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="2" />
								<s:text name="admin.logSet.reportSummary.slaPeriodRange" />
							</td>
						</tr>
						<tr style="display: <s:property value="reportSummarySettingDisplay"/>">
							<td class="labelT1">
								<s:text name="admin.logSet.reportSummary.clientPeriod" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="clientPeriod" name="clientPeriod"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="2" />
								<s:text name="admin.logSet.reportSummary.clientPeriodRange" />
							</td>
						</tr>
						<tr style="display: <s:property value="reportSummarySettingDisplay"/>">
							<td class="noteInfo" colspan="2" style="padding-left: 8px">
								<s:text name="admin.logSet.reportSummary.note" />
							</td>
						</tr>
						</table></fieldset></td></tr>
						
						<!--application report setting start-->
						<tr>
							<td height="10"></td>
						</tr>
						<tr><td><fieldset><legend><s:text name="admin.logSet.app.setting.title" /></legend><table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td class="labelT1" colspan="2">
								<s:text name="admin.logSet.app.setting.lasthour.title" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" colspan="2" style="padding-left: 40px; vertical-align: middle;">
								<s:text name="admin.logSet.app.setting.lasthour.minute" />
							    <select name="reportIntervalMinute" id="reportIntervalMinute" style="width:125px;">
							         <option value="1">1</option>
							         <option value="2">2</option>
							         <option value="3">3</option>
							         <option value="4">4</option>
							         <option value="5">5</option>
							         <option value="6">6</option>
							         <option value="10">10</option>
							         <option value="12">12</option>
							         <option value="15">15</option>
							         <option value="20">20</option>
							         <option value="30">30</option>
							    </select> 
							   <s:text name="admin.logSet.app.setting.lasthour.minute.range" /><font color="red"><s:text name="*" /> </font>
							</td>
							<script type="text/javascript">
								document.getElementById('reportIntervalMinute').value='<s:property value="reportIntervalMinute"/>';
                			</script>	
						</tr>
						<tr>
							<td class="labelT1" width="50%" style="padding-left: 40px">
								<s:text name="admin.logSet.app.setting.lasthour.vhm" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="reportMaxApCount" name="reportMaxApCount"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="3" />
								   <s:text name="admin.logSet.app.setting.lasthour.vhm.range" />
							</td>
						</tr>
						
						<tr>
							<td class="labelT1" width="50%" style="padding-left: 40px">
								<s:text name="admin.logSet.app.setting.lasthour.hm" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="reportMaxApForSystem" name="reportMaxApForSystem"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="3" />
								   <s:text name="admin.logSet.app.setting.lasthour.hm.range" />
								
							</td>
						</tr>
						</table>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td class="labelT1" colspan="2">
								<s:text name="admin.logSet.app.setting.usage.title" />
							</td>
						</tr>
						<tr>
							<td class="noteInfo" colspan="2" style="padding-left: 40px">
								<s:text name="admin.logSet.app.setting.usage.note" />
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="30%" style="padding-left: 40px">
								<s:text name="admin.logSet.app.setting.usage.hourly" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="reportDbHourly" name="reportDbHourly"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="9" />
								   <s:text name="admin.logSet.app.setting.usage.hourly.range" />
								
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="30%" style="padding-left: 40px">
								<s:text name="admin.logSet.app.setting.usage.daily" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="reportDbDaily" name="reportDbDaily"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="9" />
								   <s:text name="admin.logSet.app.setting.usage.daily.range" />
								
							</td>
						</tr>
						<tr>
							<td class="labelT1" width="30%" style="padding-left: 40px">
								<s:text name="admin.logSet.app.setting.usage.weekly" /><font color="red"><s:text name="*" /> </font>
							</td>
							<td>
								<s:textfield id="reportDbWeekly" name="reportDbWeekly"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"
									maxlength="9" />
								   <s:text name="admin.logSet.app.setting.usage.weekly.range" />
								
							</td>
						</tr>
						</table></fieldset></td></tr>
						<!--application report setting end-->
						</s:if> <!--is home domain condition end-->
						
						<tiles:insertDefinition name="applicationConfigure" />
						
						
					</table>
				</td>
			</tr>
		</table>
	</s:form>
	<s:form action="reportSetting" enctype="multipart/form-data" name="upload" id="upload" method="POST">
		<s:hidden name="operation"/>
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
		<td style="padding-right: 10px">
		<table class="editBox"  style="border-top:0px; border-radius: 0 0 10px 10px; padding-top: 0; " cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr><td height="5px"></td></tr>
			<tr><td>
				  	<fieldset>
						<legend><s:text name="admin.logSet.pdflogo.title" /></legend>
						<table cellspacing="0" cellpadding="0" border="0" width="100%" >
							 <tr>
								 <td style="padding: 8px 0 5px 0; vertical-align: top;" width="25%">
									 <s:if test="%{logoExsit}">
									 	<input type="image"  id="logoImage" src="<s:property value="previousFilePath" />" onclick="return false;" style="border:2px solid #eee">
									 </s:if>
									 <s:else>
									 		<font color="red"><s:text name="admin.logSet.pdflogo.unset" /></font>	
									 </s:else>
								 </td>
								 <td>
								 <div id="logoFilePathContainer">
								 <input type="text" id="logoFilePath"  onKeyDown="return false"/>
								 </div>
								 <div id="logoUploadContainer">
								 	<input type="button"  value="Select..." onmouseover='adjustPosition(event)' class="button" <s:property value="writeDisabled" />/>
								 	<s:file id="localFile" name="localFile" size="1"  label="logo file"
									 accept="image/png" onchange="showLogoPath()"/>
								 	<input type="button"  value="Upload" onclick="uploadImage()" class="button" <s:property value="writeDisabled" />/>
								 	<s:if test="%{logoExsit}">
								 	<input type="button"  value="Delete" onclick="deleteImage()" class="button" <s:property value="writeDisabled" />/>
								 	</s:if>
								 	<s:else>
								 	<input type="button"  value="Delete" onclick="deleteImage()" class="button"  disabled="true"/>
								 	</s:else>
<%-- 								 	<span><s:text name="reports.reportsetting.export.logo.valueRange" /></span> --%>
									
								 </div>
								 
								 </td>
					 		</tr>
						 </table>
						 </fieldset>
				</td></tr>
			<tr>
				<td height="10"></td>
			</tr>
		</table>
		</td>
		</tr>
		</table>
	</s:form>
</div>

<script language="javascript">
function initAppAutoSearch(){
	// init DataSource
	var appArray = new Array();
	var index = 0;
	<s:iterator value="allAppNames" id="appName">
	appArray[index++] = "<s:property value='appName'/>";
	</s:iterator>
	
	var appDataSource = new YAHOO.util.LocalDataSource(appArray);
	 // Optional to define fields for single-dimensional array
    appDataSource.responseSchema = {fields : "name"};
	
    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("system_search_key", "searchKeyContainer", appDataSource);
    oAC.prehighlightClassName = "yui-ac-prehighlight";
    oAC.useShadow = true;
    oAC.typeAhead = true;
    oAC.autoHighlight = false;
    oAC.maxResultsDisplayed = 10;
    
    oAC.itemSelectEvent.subscribe(setSelectedVal);
    return {
        oDS: appDataSource,
        oAC: oAC
    };
}

function initGroupAutoSearch(){
	// init DataSource
	var groupArray = new Array();
	var index = 0;
	<s:iterator value="allGroupNames" id="groupName">
	groupArray[index++] = "<s:property value='groupName'/>";
	</s:iterator>
	
	var groupDataSource = new YAHOO.util.LocalDataSource(groupArray);
	 // Optional to define fields for single-dimensional array
    groupDataSource.responseSchema = {fields : "name"};
	
    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("system_search_groupkey", "searchGroupKeyContainer", groupDataSource);
    oAC.prehighlightClassName = "yui-ac-prehighlight";
    oAC.useShadow = true;
    oAC.typeAhead = true;
    oAC.autoHighlight = false;
    oAC.maxResultsDisplayed = 10;
    
    oAC.itemSelectEvent.subscribe(setSelectedVal);
    return {
        oDS: groupDataSource,
        oAC: oAC
    };
}
function initCustomAutoSearch(){
	// init DataSource
	var appArray = new Array();
	var index = 0;
	var tempstr;
	<s:iterator value="allCustomNames" id="customName">
	tempstr = "<s:property value='%{customName}' escape='false'/>";
	tempstr = tempstr.replace(/\\\\/g, "\\");
	tempstr = tempstr.replace(/&amp;/g,"&");
	appArray[index++] = tempstr;
	</s:iterator>
	
	var appDataSource = new YAHOO.util.LocalDataSource(appArray);
	 // Optional to define fields for single-dimensional array
    appDataSource.responseSchema = {fields : "name"};
	
    // Instantiate the AutoComplete
     var oAC = new YAHOO.widget.AutoComplete("custom_search_key", "searchCustomKeyContainer", appDataSource);
    oAC.prehighlightClassName = "yui-ac-prehighlight";
    oAC.useShadow = true;
    oAC.typeAhead = true;
    oAC.autoHighlight = false;
    oAC.maxResultsDisplayed = 10;
    
    oAC.itemSelectEvent.subscribe(setSelectedCustomVal);
    return {
        oDS: appDataSource,
        oAC: oAC
    };
}
function setSelectedVal(sType, aArgs){
	var oData = aArgs[2]; // object literal of selected item's result data
	var showName = oData[0];
	appService.filterApps(showName,'leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');
}
function setSelectedCustomVal(sType, aArgs){
	var oData = aArgs[2]; // object literal of selected item's result data
	var showName = oData[0];
	appService.filterApps(showName,'leftCustomAppTable','','currentAvailableCustomNum','currentSelectCustomNum','totalCustomAppNum');
}
function changeService(service){
	if (service == 'application') {
			getDom("application_radio").checked = true;
			getDom("group_radio").checked = false;
			getDom("groupSearchTd").style.display = "none";
			getDom("appSearchTd").style.display = "";
			getDom("system_search_key").value = "";
	} else {
		getDom("application_radio").checked = false;
		getDom("group_radio").checked = true;
		getDom("groupSearchTd").style.display = "";
		getDom("appSearchTd").style.display = "none";
		getDom("system_search_groupkey").value = "";
	}
}

function checkAllSystemItem(checkAll) {
	var rightTable = getDom("leftSystemAppTable");
	var trArray = rightTable.getElementsByTagName('tr');
	for (var i = 0; i < trArray.length; i++) {
		if(trArray[i].style.display == ""){
			var cb = trArray[i].getElementsByTagName("input")[0];
			if (cb.checked != checkAll.checked) {
				cb.checked = checkAll.checked;
			}
		}
	}
}

function checkAllCustomItem(checkAll) {
	var rightTable = getDom("leftCustomAppTable");
	var trArray = rightTable.getElementsByTagName('tr');
	for (var i = 0; i < trArray.length; i++) {
		if(trArray[i].style.display == ""){
			var cb = trArray[i].getElementsByTagName("input")[0];
			if (cb.checked != checkAll.checked) {
				cb.checked = checkAll.checked;
			}
		}
	}
}

function checkAllAppItem(checkAll) {
	var rightTable = getDom("rightAppTable");
	var trArray = rightTable.getElementsByTagName('tr');
	for (var i = 0; i < trArray.length; i++) {
		var cb = trArray[i].getElementsByTagName("input")[0];
		if (cb.checked != checkAll.checked) {
			cb.checked = checkAll.checked;
		}
	}
} 

var appService = appService || {};

function getDomValue(id) {
	return document.getElementById(id).value;
}

function getDom(id) {
	return document.getElementById(id);
}

String.prototype.startWith = function(str){
	var result = false;
	try{
		var reg=new RegExp("^"+str);
		result = reg.test(this);
	}catch(e){
		result = contains(this,str);
	}
	return result;       
}

function contains(string,substr){
	 string = string.toLowerCase();
	 substr = substr.toLowerCase();
	 string = string.replace(/&amp;/g,"&");
	 var startChar = substr.substring(0, 1);
	 var strLen = substr.length;
	 for (var j = 0; j < string.length - strLen + 1; j++) {
	  if (string.charAt(j) == startChar)
	  {
	   if (string.substring(j, j + strLen) == substr)
	   {
	    return true;
	   }
	  }
	 }
	 return false;
}


appService.hiddenApp = function(currentObj) {
	currentObj.style.display = "none";
}

appService.showApp = function(currentObj) {
	currentObj.style.display = "";
}

 appService.showAllApp = function(leftTable,currentAvailableNum,selectedAppNum,totalAppNum) {
	var trs = getDom(leftTable).getElementsByTagName('tr');
	for (var i = 0; i < trs.length; i++) {
		trs[i].style.display = "";
	}
	var currentSelectNum = parseInt(getDomValue(selectedAppNum));
	var totalAppNum = parseInt(getDomValue(totalAppNum));
	this.changeAvailableNum(currentAvailableNum, totalAppNum - currentSelectNum);
}

appService.changeCurrentSelectNum = function(hiddenSelectedNumId,number) {
	var currentValue = getDom("currentSelectAllNum").innerHTML;
	var currentSelectNum = parseInt(currentValue);
	currentSelectNum = currentSelectNum + number;
	getDom("currentSelectAllNum").innerHTML = currentSelectNum;
	var hiddenSelectedNum = getDomValue(hiddenSelectedNumId);
	getDom(hiddenSelectedNumId).value = parseInt(hiddenSelectedNum) + number;
}

appService.changeAvailableNum = function(currentAvailableNum,number) {
	getDom(currentAvailableNum).innerHTML = number;
}

appService.changeDeltaAvailableNum = function(currentAvailableNumId,number) {
	var currentValue = getDom(currentAvailableNumId).innerHTML;
	var currentAvailableNum = parseInt(currentValue);
	currentAvailableNum = currentAvailableNum + number;
	getDom(currentAvailableNumId).innerHTML = currentAvailableNum;
}

appService.addApp = function() {
	var list;
	var leftTable;
	var inputElements;
	var div = getDom("systemDiv");
	var trArray = new Array();
	var hiddenSelectedNumId;
	var currentAvailableNumId;
	if(div.className == 'yui-hidden'){
		leftTable = getDom("leftCustomAppTable");
		list = leftTable.getElementsByTagName('tr');
		inputElements = document.getElementsByName('customAppIds');
		hiddenSelectedNumId = "currentSelectCustomNum";
		currentAvailableNumId = "currentAvailableCustomNum";
	}else{
		leftTable = getDom("leftSystemAppTable");
		list = leftTable.getElementsByTagName('tr');
		inputElements = document.getElementsByName('systemAppIds');
		hiddenSelectedNumId = "currentSelectSystemNum";
		currentAvailableNumId = "currentAvailableSystemNum";
	}
	
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
			if (cb.checked) {
				trArray.push(list[i]);
			}
		}
	}
	if(trArray.length == 0){
		return false;
	}
	var rightTable = getDom("rightAppTable");
	var rightTableTr = rightTable.getElementsByTagName('tr');
	
	if((trArray.length + rightTableTr.length) > 7){
		var rightAppDiv = getDom('messageError');
		hm.util.reportFieldError(rightAppDiv, 'Maximum number of applications allowed is ' + maxAppCount);
		return false;
	}
	if(div.className == 'yui-hidden'){
		for (var i = 0; i < trArray.length; i++) {
			trArray[i].getElementsByTagName("td")[0].className = "tdsystemchk";
			trArray[i].getElementsByTagName("input")[0].checked = false;
			trArray[i].getElementsByTagName("td")[1].className = "tdsystemappname";
			trArray[i].getElementsByTagName("td")[2].className = "tdsystemusage";
			trArray[i].getElementsByTagName("td")[3].className = "tdsystemusage";
			var tdgroup = document.createElement("td");
			tdgroup.className = "tdsystemgroup";
			tdgroup.appendChild(document.createTextNode("Custom"));
			trArray[i].appendChild(tdgroup);
			rightTable.appendChild(trArray[i]);
		}
	}else{
		for (var i = 0; i < trArray.length; i++) {
			trArray[i].getElementsByTagName("input")[0].checked = false;
			rightTable.appendChild(trArray[i]);
		}
	}
	this.changeCurrentSelectNum(hiddenSelectedNumId,trArray.length);
	this.changeDeltaAvailableNum(currentAvailableNumId,0 - trArray.length);
}

appService.removeApp = function() {
	var systemLeftTable = getDom("leftSystemAppTable");
	var customLeftTable = getDom("leftCustomAppTable");
	var rightTable = getDom("rightAppTable");
	var list = rightTable.getElementsByTagName('tr');
	var systemTrArray = new Array();
	var customTrArray = new Array();
	var n = list.length;
	
	for(var i = 0; i < n; i++) {
		var tr = list[i];
		var tdchk = list[i].getElementsByTagName("input")[0];
		if(tdchk.checked){
			if (tr.getAttribute("appType") == 0) {
				systemTrArray.push(list[i]);
			}else{
				customTrArray.push(list[i]);
			}
		}
	}
	if(systemTrArray.length > 0 && customTrArray.length > 0){
		for (var i = 0; i < systemTrArray.length; i++) {
			systemTrArray[i].getElementsByTagName("input")[0].checked = false;
			systemLeftTable.appendChild(systemTrArray[i]);
		}
		for (var i = 0; i < customTrArray.length; i++) {
			customTrArray[i].getElementsByTagName("td")[0].className = "tdcustomchk";
			customTrArray[i].getElementsByTagName("input")[0].checked = false;
			customTrArray[i].getElementsByTagName("td")[1].className = "tdcustomappname";
			customTrArray[i].getElementsByTagName("td")[2].className = "tdcustomusage";
			customTrArray[i].getElementsByTagName("td")[3].className = "tdcustomusage";
			var tdgroup = customTrArray[i].getElementsByTagName("td")[4];
			customTrArray[i].removeChild(tdgroup);
		    customLeftTable.appendChild(customTrArray[i]);
		}
		this.changeCurrentSelectNum("currentSelectSystemNum",0 - systemTrArray.length);
		this.changeDeltaAvailableNum("currentAvailableSystemNum",systemTrArray.length);
		this.changeCurrentSelectNum("currentSelectCustomNum",0 - customTrArray.length);
		this.changeDeltaAvailableNum("currentAvailableCustomNum",customTrArray.length);
	}else if(systemTrArray.length > 0 && customTrArray.length == 0){
		for (var i = 0; i < systemTrArray.length; i++) {
			systemTrArray[i].getElementsByTagName("input")[0].checked = false;
		    systemLeftTable.appendChild(systemTrArray[i]);
		}
		getDom("leftSystemAppTable").style.display = "";
		getDom("systemDiv").className = "";
		getDom("customDiv").className = "yui-hidden";
		getDom("customTab").className = "";
		getDom("systemTab").className = "selected";
		this.changeCurrentSelectNum("currentSelectSystemNum",0 - systemTrArray.length);
		this.changeDeltaAvailableNum("currentAvailableSystemNum",systemTrArray.length);
	}else if(systemTrArray.length == 0 && customTrArray.length > 0){
		for (var i = 0; i < customTrArray.length; i++) {
			customTrArray[i].getElementsByTagName("td")[0].className = "tdcustomchk";
			customTrArray[i].getElementsByTagName("input")[0].checked = false;
			customTrArray[i].getElementsByTagName("td")[1].className = "tdcustomappname";
			customTrArray[i].getElementsByTagName("td")[2].className = "tdcustomusage";
			customTrArray[i].getElementsByTagName("td")[3].className = "tdcustomusage";
			var tdgroup = customTrArray[i].getElementsByTagName("td")[4];
			customTrArray[i].removeChild(tdgroup);
		    customLeftTable.appendChild(customTrArray[i]);
		}
		getDom("leftSystemAppTable").style.display = "none";
		getDom("systemDiv").className = "yui-hidden";
		getDom("customDiv").className = "";
		getDom("systemTab").className = "";
		getDom("customTab").className = "selected";
		this.changeCurrentSelectNum("currentSelectCustomNum",0 - customTrArray.length);
		this.changeDeltaAvailableNum("currentAvailableCustomNum",customTrArray.length);
	}else{
		return false;
	}
	
}

appService.filterApps = function(value,leftTable,radioCheck,currentAvailableNum,selectedAppNum,totalAppNum) {
	var isAppFilter;
	var keywords = value;
	if (keywords == null || keywords.trim() == "") {
		this.showAllApp(leftTable,currentAvailableNum,selectedAppNum,totalAppNum);
		if(waitingPanel != null){
			waitingPanel.hide();
		}
		return false;
	}
	var num = 0;
	keywords = keywords.toUpperCase();
	var tableObj = getDom(leftTable);
	var trs = tableObj.getElementsByTagName('tr');
	if(radioCheck == ""){
		for (var i = 0; i < trs.length; i++) {
			appInfo = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
			if (appInfo.startWith(keywords)) {
				this.showApp(trs[i])
				num++;
			} else {
				this.hiddenApp(trs[i]);
			}
		}
	}else{
		isAppFilter = getDom(radioCheck).checked;
		for (var i = 0; i < trs.length; i++) {
			if (isAppFilter) { //compare app name
				appInfo = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
			} else { //compare app group name
				appInfo = trs[i].getElementsByTagName('td')[4].innerHTML.trim().toUpperCase();
			}
			
			if (appInfo.startWith(keywords)) {
				this.showApp(trs[i])
				num++;
			} else {
				this.hiddenApp(trs[i]);
			}
		}
	}
	
	this.changeAvailableNum(currentAvailableNum,num);
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

appService.searchApps = function(leftTable,radioCheck,currentAvailableNum,selectedAppNum,totalAppNum) {
	var isAppFilter;
	var keywords;
	if(radioCheck == ""){
		keywords = getDomValue("custom_search_key");
	}else{
		isAppFilter = getDom(radioCheck).checked;
		if (isAppFilter) { 
			keywords = getDomValue("system_search_key");
		}else{
			keywords = getDomValue("system_search_groupkey");
		}
	}
	if (keywords == null || keywords.trim() == "") {
		this.showAllApp(leftTable,currentAvailableNum,selectedAppNum,totalAppNum);
		if(waitingPanel != null){
			waitingPanel.hide();
		}
		return false;
	}
	var num = 0;
	keywords = keywords.toUpperCase();
	var tableObj = getDom(leftTable);
	var trs = tableObj.getElementsByTagName('tr');
	if(radioCheck == ""){
		for (var i = 0; i < trs.length; i++) {
			appInfo = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
			if (appInfo.startWith(keywords)) {
				this.showApp(trs[i])
				num++;
			} else {
				this.hiddenApp(trs[i]);
			}
		}
	}else{
		for (var i = 0; i < trs.length; i++) {
			if (isAppFilter) { //compare app name
				appInfo = trs[i].getElementsByTagName('a')[0].innerHTML.trim().toUpperCase();
			} else { //compare app group name
				appInfo = trs[i].getElementsByTagName('td')[4].innerHTML.trim().toUpperCase();
			}
			
			if (appInfo.startWith(keywords)) {
				this.showApp(trs[i])
				num++;
			} else {
				this.hiddenApp(trs[i]);
			}
		}
	}
	
	this.changeAvailableNum(currentAvailableNum,num);
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

appService.enterKeywords = function(event,leftTable,radioCheck,currentAvailableNum,selectedAppNum,totalAppNum){
	var keycode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	if(keycode == 13){
		if(waitingPanel != null){
			waitingPanel.show();
		}
		this.searchApps(leftTable,radioCheck,currentAvailableNum,selectedAppNum,totalAppNum);
		return false;
	}	
}

function changeTab(tab){
	if("systemTab" == tab){
		getDom("leftSystemAppTable").style.display = "";
		$("#customTab").removeClass("selected");
		$("#systemTab").addClass("selected");
		$("#systemDiv").removeClass("yui-hidden");
		$("#customDiv").addClass("yui-hidden");
		$("#systemTdId").show();
		$("#customTdId").hide();
	}else{
		getDom("leftSystemAppTable").style.display = "none";
		$("#systemTab").removeClass("selected");
		$("#customTab").addClass("selected");
		$("#customDiv").removeClass("yui-hidden");
		$("#systemDiv").addClass("yui-hidden");
		$("#systemTdId").hide();
		$("#customTdId").show();
	}
}

appService.changeNumber = function(number) {
	var currentAvailableCustomNum = getDom("currentAvailableCustomNum").innerHTML;
	var currentAvailableNum = parseInt(currentAvailableCustomNum);
	var totalShowCustomAppNum = getDom("totalShowCustomAppNum").innerHTML;
	var currentTotalNum = parseInt(totalShowCustomAppNum);
	
	getDom("currentAvailableCustomNum").innerHTML = currentAvailableNum + number;
	getDom("totalShowCustomAppNum").innerHTML = currentTotalNum + number;
	getDom("totalCustomAppNum").innerHTML = currentTotalNum + number;
	getDom("totalCustomAppNum").value = currentTotalNum + number;
}

var newCustomAppPanel = null;
function preparePanels4SelectService() {
	var div = document.getElementById('newCustomAppPanelId');
	newCustomAppPanel = new YAHOO.widget.Panel(div, {
		width:"700px",
		underlay: "none",
		fixedcenter:"contained",
		visible:false,
		draggable:false,
		close:false,
		modal:true,
		constraintoviewport:true,
		zIndex:3
		});
	newCustomAppPanel.render(document.body);
	div.style.display = "";
}

function fetchSelectServiceNewDlg(value) {
	var url = "<s:url action='customApp' includeParams='none' />?operation=new&selectAdd=true" + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchServiceNewDlg, failure : resultDoNothing, timeout: 60000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var succFetchServiceNewDlg = function(o) {
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	set_innerHTML("newCustomAppPanelContentId",
			o.responseText);
	YAHOO.util.Event.onContentReady("newCustomAppPanelContentId", showSelectServicePanel, this);
}

var resultDoNothing = function(){}

function showSelectServicePanel(){
	if(null != newCustomAppPanel){
		newCustomAppPanel.cfg.setProperty('visible', true);
		newCustomAppPanel.center();
	}
}

function hideSelectServicePanel(){
	if(null != newCustomAppPanel){
		set_innerHTML("newCustomAppPanelContentId", "");
		newCustomAppPanel.cfg.setProperty('visible', false);
	}
}

YAHOO.util.Event.onContentReady("newCustomAppPanelId", function() {
	preparePanels4SelectService();
}, this);

function addCustomApp(){
	fetchSelectServiceNewDlg("");
}

function editCustomApp(id){
	fetchSelectServiceNewDlg(id);
}
</script>
