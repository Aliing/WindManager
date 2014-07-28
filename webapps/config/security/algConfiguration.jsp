<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.util.EnumConstUtil"%>
<s:if test="%{jsonMode}">
<link rel="stylesheet" href="<s:url value="/css/hm_v2.css" includeParams="none"/>" type="text/css" />
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none"/>" ></script>
</s:if>

<script>
var formName = 'algConfiguration';
function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_configName").disabled) {
		document.getElementById(formName + "_dataSource_configName").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		<s:if test="%{jsonMode}">
		 	if ('cancel' + '<s:property value="lstForward"/>' == operation) {
				parent.closeIFrameDialog();	
			} else{
				saveAlgConfiguration(operation);
			}
		</s:if>
		<s:else>
			document.forms[formName].operation.value = operation;
		    document.forms[formName].submit();
		</s:else>
		
	}
}

function saveAlgConfiguration(operation) {
	var url;
	 if (operation == 'create<s:property value="lstForward"/>') {
		 url = "<s:url action='algConfiguration' includeParams='none' />"+ "?jsonMode=true" +"&ignore="+new Date().getTime(); 
	 } else if (operation == 'update<s:property value="lstForward"/>'){
		 var id;
		 <s:if test="%{dataSource.id != null}">
		     id = <s:property value="dataSource.id"/>; 
		 </s:if>
		 url = "<s:url action='algConfiguration' includeParams='none' />"+ "?jsonMode=true"+ "&id="+id +"&ignore="+new Date().getTime(); 
	 }
	 document.forms["algConfiguration"].operation.value = operation;
	 YAHOO.util.Connect.setForm(document.forms["algConfiguration"]);
	 var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveAlgConfiguration, failure : failSaveAlgConfiguration, timeout: 60000}, null);
}

var succSaveAlgConfiguration = function (o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			parent.closeIFrameDialog();
			if(details.id != null && details.name != null){	
				if (details.parentDomID) {
					var parentIpPolicySelect = parent.document.getElementById(details.parentDomID);
					if(parentIpPolicySelect != null) {
						hm.util.insertSelectValue(details.id,details.name,parentIpPolicySelect,false,true);	
					}
				}
			}
		}
	}catch(e){
		alert("error")
		return;
	}
}

var failSaveAlgConfiguration = function(o){
	
}

function validate(operation) {
	if (operation == 'cancel' + '<s:property value="lstForward"/>') {
		initValue();
		return true;
	}
	if (!validateAlgName()) {
		return false;
	}
	var timeTitle;
	var durationTitle;
	for (var i=0;i<3;i++) {
        var timeout_value=document.getElementById("timeout_" + i);
        var duration_value=document.getElementById("duration_" + i);
        switch(i) {
        	case 0:
        		timeTitle = "FTP's Inactive Data Timeout";
        		durationTitle = "FTP's Max Session Duration";
        		break;
        	case 1:
        		timeTitle = "SIP's Inactive Data Timeout";
        		durationTitle = "SIP's Max Session Duration";
        		break;
        	case 2:
        		timeTitle = "TFTP's Inactive Data Timeout";
        		durationTitle = "TFTP's Max Session Duration";
        		break;
        	default:
        		break;
        }
    
        if (timeout_value.value.length == 0) {
            hm.util.reportFieldError(timeout_value, '<s:text name="error.requiredField"><s:param>'+timeTitle+'</s:param></s:text>');
            timeout_value.focus();
            return false;
      	}
      	var message = hm.util.validateIntegerRange(timeout_value.value, timeTitle,1,1800);
	    if (message != null) {
	        hm.util.reportFieldError(timeout_value, message);
	        timeout_value.focus();
	        return false;
	    }
        if (duration_value.value.length == 0) {
            hm.util.reportFieldError(duration_value, '<s:text name="error.requiredField"><s:param>'+durationTitle+'</s:param></s:text>');
            duration_value.focus();
            return false;
      	}
      	message = hm.util.validateIntegerRange(duration_value.value, durationTitle,1,7200);
	    if (message != null) {
	        hm.util.reportFieldError(duration_value, message);
	        duration_value.focus();
	        return false;
	    }
    } 
	return true;
}

function validateAlgName() {
    var inputElement = document.getElementById(formName + "_dataSource_configName");
    var message = hm.util.validateName(inputElement.value, '<s:text name="config.alg.configuration.name" />');
	if (message != null) {
   		hm.util.reportFieldError(inputElement, message);
       	inputElement.focus();
       	return false;
   	}
    return true;
}

function initValue() {
	for (var i=0;i<3;i++) {
        var timeout_value=document.getElementById("timeout_" + i);
        var duration_value=document.getElementById("duration_" + i);
        timeout_value.value=i == 1?"60":"30";
        duration_value.value=i == 1?"720":"60";
    } 
}

function enableItems(checked, index,value) {
	if (value == 'HTTP') {
		return;
	}
	var qosClass = document.getElementById("qosClass_" + index);
	qosClass.disabled = !checked;
	if(!checked) {
		qosClass.value=index == 1?<%=EnumConstUtil.QOS_CLASS_VOICE%> : <%=EnumConstUtil.QOS_CLASS_BACKGROUND%>;
	}
	if (index != 3) {
		var timeout = document.getElementById("timeout_" + index);
		var duration = document.getElementById("duration_" + index);
		timeout.disabled = !checked;
		duration.disabled = !checked;
		if(!checked) {
			timeout.value=index == 1?"60":"30";
	        duration.value=index == 1?"720":"60";
		}
	}
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="algConfiguration" includeParams="none" />"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedAlgName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedAlgName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>	
}

</script>

<div id="content"><s:form action="algConfiguration">
	<s:if test="%{jsonMode == true}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID" />
		<div class="topFixedTitle">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-ALG_services.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.alg.configuration.title"/>
								</s:if> <s:else>
									<s:text name="config.alg.configuration.title.edit"/>
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
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitAction('cancel<s:property value="lstForward"/>');" title="Cancel"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Cancel</span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton">
								<s:if test="'' == writeDisabled">
									<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a>
								</s:if>
								</td>
							</s:if>
							<s:else>
								<td class="npcButton">
								<s:if test="%{'' == updateDisabled}">
									<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
								</s:if>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</div>
	</s:if>
	<s:else>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
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
			<td style="padding-top: 5px;">
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="620">
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td class="labelT1" width="100"><label><s:text
									name="config.alg.configuration.name" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield name="dataSource.configName"
									onkeypress="return hm.util.keyPressPermit(event,'name');"
									size="24" maxlength="%{algNameLength}" disabled="%{disabledName}"/>&nbsp;<s:text
									name="config.ssid.ssidName_range" /></td>
							</tr>
							<tr>
								<td class="labelT1"><label><s:text
									name="config.localUser.description" /></label></td>
								<td><s:textfield size="48"
									name="dataSource.description" maxlength="%{commentLength}" />&nbsp;<s:text
									name="config.ssid.description_range" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>	
					<td style="padding:0 4px 6px 8px">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td colspan="4">

						</tr>
						<tr>
							<th align="center"><s:text
								name="config.alg.configuration.enable" /></th>
							<th align="center"><s:text
								name="config.alg.configuration.gateway" /></th>
							<th align="center"><s:text name="config.alg.configuration.class" /></th>
							<th align="center"><s:text
								name="config.alg.configuration.timeout" /></th>
							<th align="center"><s:text
								name="config.alg.configuration.duration" /></th>
						</tr>
						<s:iterator id="items"
							value="%{dataSource.items.values()}" status="status">
							<tr class="list">
								<td class="listCheck" align="center"><s:checkbox onclick="enableItems(this.checked,%{#status.index},'%{value}')"
									name="ifEnable" fieldValue="%{#status.index}" /></td>
								<td class="list" align="center"><s:property value="%{value}" /></td>
								<s:if test="%{value != 'HTTP'}">
								<td class="list" align="center"><s:select name="qosClass" id="qosClass_%{#status.index}"
									value="%{qosClass}" list="%{enumClass}" listKey="key" disabled="%{!ifEnable}"
									listValue="value"/></td>
								<s:if test="%{value != 'DNS'}">
									<td class="list" align="center"><s:textfield name="timeout"
										value="%{timeout}" maxlength="4" size="10"
										id="timeout_%{#status.index}" disabled="%{!ifEnable}" 
										onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
									</td>
									<td class="list" align="center"><s:textfield name="duration"
										value="%{duration}" maxlength="4" size="10"
										id="duration_%{#status.index}" disabled="%{!ifEnable}"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
									</td>
								</s:if>
								</s:if>
							</tr>
						</s:iterator>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
