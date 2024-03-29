<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=7;FF=3;OtherUA=4" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />

<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/hm_v2.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<style>

body {
	background-color: #f9f9f7;
	margin: 0px;
}

img.dinl {
	display: inline;
	border: 0;
}
img.dialogTitleImg {
	padding: 0;
	vertical-align: middle; 
	align: center;
	border:none;
	}
input, td, textarea, th {
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 12px;
}	
ul.dark{
	margin-bottom: 0;
	margin-top: 0;
	padding-bottom: 0;
	padding-top: 0
}
ul.dark li {
	margin-bottom: 0;
    margin-top: 0;
}
ul.dark li a.current span {
    float: left;
    font-size: 12px;
    font-weight: bold;
    margin: 0 10px 0 -10px;
    padding: 1px 8px 5px 18px;
    position: relative;
}

</style>
<script src="<s:url value="/js/hm.util.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script type="text/javascript"
	src="<s:url value="/yui/utilities/utilities.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script>
var formName = 'scheduler';

function submitAction(operation) {
    document.forms[formName].operation.value = operation;
    document.forms[formName].submit();
}

function createScheduler() {
	var selected = hm.util.getSelectedCheckItems("selectedSchedulers");
	if(selected.length >= 8) {
		hm.util.reportFieldError(document.getElementById("notes"), 
		'<s:text name="error.config.networkPolicy.scheduler.notcreate" />');
		return ;
	}
	
 	parent.closeIFrameDialog();
 	var selectedSchedulers = hm.util.getSelectedCheckItems("selectedSchedulers");
 	if (selectedSchedulers == hm.util._LIST_SELECTION_NOITEM || selectedSchedulers == hm.util._LIST_SELECTION_NOSELECTION) {
 		selectedSchedulers = null;
 	}
 	parent.createSchedulerFromSSID(document.forms[formName].bindTarget.value,
 			document.forms[formName].ssidId.value,selectedSchedulers);
}

function clickOK() {
	var selected = hm.util.getSelectedCheckItems("selectedSchedulers");
	if(selected == hm.util._LIST_SELECTION_NOITEM) {
		hm.util.reportFieldError(document.getElementById("notes"), 
    		'<s:text name="error.config.networkPolicy.ssid.scheduler.noItem" />');
		return ;
	//} else if(selected == hm.util._LIST_SELECTION_NOSELECTION) {
	//	hm.util.reportFieldError(document.getElementById("notes"), 
	//		'<s:text name="error.config.networkPolicy.ssid.scheduler.noSelection" />');
	//	return ;
	} else if(selected.length > 8) {
		hm.util.reportFieldError(document.getElementById("notes"), 
		'<s:text name="error.config.networkPolicy.scheduler.selectedMax" />');
		return ;
	}
	
	var url = '<s:url action="scheduler" includeParams="none" />' 
		+ '?ignore='+new Date().getTime();
	document.forms[formName].operation.value = 'setSchedulerToSSID';
	YAHOO.util.Connect.setForm(document.forms[formName]);
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:afterChangeSSID}, null);
}

var afterChangeSSID = function(o) {
	eval("var details = " + o.responseText);
	
	if(!details.ok) {
		hm.util.reportFieldError(document.getElementById("notes"), details.msg);
		return ;
	}
	
	parent.closeIFrameDialog();
	parent.fetchConfigTemplate2Page(true);
}

function editScheduler(schedulerId, event) {
	// close this dialog
	parent.closeIFrameDialog();
	
 	var selectedSchedulers = hm.util.getSelectedCheckItems("selectedSchedulers");
 	if (selectedSchedulers == hm.util._LIST_SELECTION_NOITEM || selectedSchedulers == hm.util._LIST_SELECTION_NOSELECTION) {
 		selectedSchedulers = null;
 	}
	// expand the subdrawer
	parent.editSchedulerInSubdrawer(schedulerId, <s:property value="bindTarget"/>,<s:property value="ssidId"/>,selectedSchedulers);
	// stop bubble!
	hm.util.stopBubble(event);
	// init the callback function
	parent.networkPolicyCallbackFn = function() {
		parent.openSchedulerListDialog(<s:property value="bindTarget"/>,
				<s:property value="ssidId"/>);
	}
}

function prepareSchedulerListBtPerrmit(){
	<s:if test="%{savePermit==false}">
		document.getElementById("btCreateScheduler").style.visibility="hidden";
		document.getElementById("btSelectScheduler").style.visibility="hidden";
	</s:if>
	<s:else>
		document.getElementById("btCreateScheduler").style.visibility="visible";
		document.getElementById("btSelectScheduler").style.visibility="visible";
	</s:else>
}
window.setTimeout("prepareSchedulerListBtPerrmit()", 100);

</script>
</head>
<div id="content">
<s:form action="scheduler">
<s:hidden name="operation" />
<s:hidden name="bindTarget" />
<s:hidden name="ssidId" />
<table width="100%" height="220px" border="0" cellspacing="0" cellpadding="0">
	<!-- header -->
	<tr>
		<td align="left" width="100%">
		<img  src="/hm/images/hm_v2/profile/HM-icon-schedules-big.png" width="40px" height="40px" title="<s:text name="config.scheduler.dialog.title"/>" class="dialogTitleImg">
		<span class="npcHead1" style="padding-left:10px;"><s:text name="config.scheduler.dialog.title" /></span>
		</td>
		<td width="20px"><a href="javascript:parent.closeIFrameDialog();">
			<img src="<s:url value="/images/cancel.png" />"
				width="16" height="16" alt="Cancel" title="Cancel" class="dinl" /></a></td>
	</tr>
	<tr>
		<td colspan="2" height="5px"></td>
	</tr>
	<!-- list -->
	<tr>
		<td colspan="2">
		<table>
			<tr>
				<td><span id="notes"></span></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
		<ah:checkList name="selectedSchedulers" list="availableSchedulers" listKey="id" listValue="value" value="selectedSchedulers"
			 height="120px" width="100%" multiple="true" editEvent="editScheduler"/>
		</td>
	</tr>
	<!-- buttons -->
	<tr>
		<td colspan="2" align="center" height="100%"  style="vertical-align:bottom;">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					 <td>
						<ul class="dark">
							<s:if test="%{writeDisabled == 'disabled'}">
								<li ><a href="javascript:void(0);" title="<s:text name="config.networkpolicy.button.ok" />" class="current"><span><s:text name="config.networkpolicy.button.ok" /></span></a></li>
								<li ><span>&nbsp;</span></li>
								<li><a href="javascript:void(0);" title="<s:text name="config.networkpolicy.button.new"/>" class="current"><span><s:text name="config.networkpolicy.button.new" /></span></a></li>
							</s:if>
							<s:else>
								<li ><a href="javascript: clickOK();" title="<s:text name="config.networkpolicy.button.ok" />" class="current" id="btSelectScheduler" style="visibility:hidden;" ><span><s:text name="config.networkpolicy.button.ok" /></span></a></li>
								<li ><span>&nbsp;</span></li>
								<li><a href="javascript: createScheduler();" title="<s:text name="config.networkpolicy.button.new"/>" class="current" id="btCreateScheduler" style="visibility:hidden;"><span><s:text name="config.networkpolicy.button.new" /></span></a></li>
							</s:else>
						</ul>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</s:form>
</div>
</html>
