<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script type="text/javascript">
var formName = 'spectralAnalysis';  
var thisOperation;
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="spectralAnalysis" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> ');
	document.writeln('</td>');
}

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		beforeSubmitAction(document.forms[formName]);
		document.forms[formName].submit();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
	    thisOperation = operation;
	    if (operation == 'start') {
	        confirmDialog.cfg.setProperty('text', "<html><body>Performing spectrum analysis on the selected device will affect performance.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
			confirmDialog.show();
	    } else {
	        doContinueOper();
	    }  
    } 
}

function submitActionSec(operation, currentApId) {
	if (operation == 'view') { // No form submit for this operation
		window.location.href = 'spectralAnalysis.action?operation=view&id='+currentApId;
		return;
	} else if (operation == 'interference') {
		window.location.href = 'spectralAnalysis.action?operation=interference&currentApId='+currentApId;
		return;
	} 
	document.forms[formName].currentApId.value=currentApId;
	submitAction(operation);
}

function doContinueOper() {
    showProcessing();
    document.forms[formName].operation.value = thisOperation;
    beforeSubmitAction(document.forms[formName]);
    document.forms[formName].submit();
}

function validate(operation) {
	if (operation != 'start') {
		return true;
	}
	if (!checkAP()){
		return false;
	}
	if (!checkInterface()){
		return false;
	}
	if (!checkChannelWifi0()){
		return false;
	}
	if (!checkChannelWifi1()){
		return false;
	}
	if (!checkInterval()){
		return false;
	}
	return true;
}

function checkAP(){
	if (Get(formName + "_runAP").value==-1){
		hm.util.reportFieldError(Get(formName + "_runAP"), '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.snp.hiveap"/></s:param></s:text>');
       	Get(formName + "_runAP").focus();
       	return false;
	}
	return true;
}

function checkInterface() {
	if (Get(formName + "_runInterface").value==-1){
		hm.util.reportFieldError(Get(formName + "_runInterface"), '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.snp.interface"/></s:param></s:text>');
       	Get(formName + "_runInterface").focus();
       	return false;
	}
	return true;
}

function checkChannelWifi0(){
	if (Get(formName + "_runInterface").value==2) {
		return true;
	}
	var attriValue = Get(formName + "_runChannelWifi0");
	if (attriValue.value.length == 0) {
        hm.util.reportFieldError(attriValue, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
        attriValue.focus();
        return false;
    } else {
	    var attrInThis = new Array();
		var attributes = attriValue.value.split(",");
		for (var i = 0; i < attributes.length; i++)
		{
			var str_attribute = attributes[i];
			if("" == str_attribute)
			{
				hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        	attriValue.focus();
	        	return false;
			}
	
			if(!isNaN(str_attribute))
			{
				// it is a number;
				var message = hm.util.validateIntegerRange(str_attribute, '<s:text name="hm.tool.snp.channel.wifi0"/>',1,13);
	      		if (message != null) {
	            	hm.util.reportFieldError(attriValue, message);
	           		attriValue.focus();
	            	return false;
	      		}
	      		for(var j = 0; j < attrInThis.length; j++)
	      		{
				if (attrInThis[j] == str_attribute)
				{
					hm.util.reportFieldError(attriValue, '<s:text name="error.sameObjectExists"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        		attriValue.focus();
	        		return false;
				}
				}
				attrInThis.push(str_attribute);
			}
			else
			{
				// it is a range;
				var str_range = str_attribute.split("-");
				if (str_range.length != 2)
				{
					hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        		attriValue.focus();
	        		return false;
				}
				else
				{
					if(!isNaN(str_range[0]) && !isNaN(str_range[1]))
					{
						var message1 = hm.util.validateIntegerRange(str_range[0], '<s:text name="hm.tool.snp.channel.wifi0"/>',1,13);
						var message2 = hm.util.validateIntegerRange(str_range[1], '<s:text name="hm.tool.snp.channel.wifi0"/>',1,13);
	      				if (message1 != null) {
	            			hm.util.reportFieldError(attriValue, message1);
	           				attriValue.focus();
	            			return false;
	      				}
	      				if (message2 != null) {
	            			hm.util.reportFieldError(attriValue, message2);
	           				attriValue.focus();
	            			return false;
	      				}
						if (parseInt(str_range[0]) >= parseInt(str_range[1]))
						{
							hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        				attriValue.focus();
	        				return false;	
						}
						for(var j = 0; j < attrInThis.length; j++)
	      				{
							if (attrInThis[j] == str_range)
							{
								hm.util.reportFieldError(attriValue, '<s:text name="error.sameObjectExists"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        					attriValue.focus();
	        					return false;
							}
						}
						attrInThis.push(str_range);			
					}
					else
					{
						hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi0"/></s:param></s:text>');
	        			attriValue.focus();
	        			return false;
					}
				}
			}
		}
    } 
	return true;
}
function checkChannelWifi1(){
	if (Get(formName + "_runInterface").value==1) {
		return true;
	}
	var attriValue = Get(formName + "_runChannelWifi1");
	if (attriValue.value.length == 0) {
        hm.util.reportFieldError(attriValue, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
        attriValue.focus();
        return false;
    } else {
	    var attrInThis = new Array();
		var attributes = attriValue.value.split(",");
		for (var i = 0; i < attributes.length; i++)
		{
			var str_attribute = attributes[i];
			if("" == str_attribute)
			{
				hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        	attriValue.focus();
	        	return false;
			}
	
			if(!isNaN(str_attribute))
			{
				// it is a number;
				var message = hm.util.validateIntegerRange(str_attribute, '<s:text name="hm.tool.snp.channel.wifi1"/>',36,165);
	      		if (message != null) {
	            	hm.util.reportFieldError(attriValue, message);
	           		attriValue.focus();
	            	return false;
	      		}
	      		for(var j = 0; j < attrInThis.length; j++)
	      		{
				if (attrInThis[j] == str_attribute)
				{
					hm.util.reportFieldError(attriValue, '<s:text name="error.sameObjectExists"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        		attriValue.focus();
	        		return false;
				}
				}
				attrInThis.push(str_attribute);
			}
			else
			{
				// it is a range;
				var str_range = str_attribute.split("-");
				if (str_range.length != 2)
				{
					hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        		attriValue.focus();
	        		return false;
				}
				else
				{
					if(!isNaN(str_range[0]) && !isNaN(str_range[1]))
					{
						var message1 = hm.util.validateIntegerRange(str_range[0], '<s:text name="hm.tool.snp.channel.wifi1"/>',36,165);
						var message2 = hm.util.validateIntegerRange(str_range[1], '<s:text name="hm.tool.snp.channel.wifi1"/>',36,165);
	      				if (message1 != null) {
	            			hm.util.reportFieldError(attriValue, message1);
	           				attriValue.focus();
	            			return false;
	      				}
	      				if (message2 != null) {
	            			hm.util.reportFieldError(attriValue, message2);
	           				attriValue.focus();
	            			return false;
	      				}
						if (parseInt(str_range[0]) >= parseInt(str_range[1]))
						{
							hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        				attriValue.focus();
	        				return false;	
						}
						for(var j = 0; j < attrInThis.length; j++)
	      				{
							if (attrInThis[j] == str_range)
							{
								hm.util.reportFieldError(attriValue, '<s:text name="error.sameObjectExists"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        					attriValue.focus();
	        					return false;
							}
						}
						attrInThis.push(str_range);			
					}
					else
					{
						hm.util.reportFieldError(attriValue, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.tool.snp.channel.wifi1"/></s:param></s:text>');
	        			attriValue.focus();
	        			return false;
					}
				}
			}
		}
    } 
	return true;
}
function checkInterval(){
	var vInterval = Get(formName + "_runInterval");
    if (vInterval.value.length == 0) {
          hm.util.reportFieldError(vInterval, '<s:text name="error.requiredField"><s:param><s:text name="hm.tool.snp.interval"/></s:param></s:text>');
          vInterval.focus();
          return false;
      }
      var message = hm.util.validateIntegerRange(vInterval.value, '<s:text name="hm.tool.snp.interval" />', 1, 30);
      if (message != null) {
          hm.util.reportFieldError(vInterval, message);
          vInterval.focus();
          return false;
      }
	return true;
}

function changeInterfaceType(value){
	if (value==1) {
		Get("wifi0Channel").style.display="";
		Get("wifi1Channel").style.display="none";
	} else if (value==2){
		Get("wifi0Channel").style.display="none";
		Get("wifi1Channel").style.display="";
	} else if (value==3){
		Get("wifi0Channel").style.display="";
		Get("wifi1Channel").style.display="";
	} else {
		Get("wifi0Channel").style.display="none";
		Get("wifi1Channel").style.display="none";
	}
	Get(formName + "_runChannelWifi0").value="";
	Get(formName + "_runChannelWifi1").value="";
}

function changeHiveAp(value){
	var url = '<s:url action="spectralAnalysis" includeParams="none"></s:url>' + "?operation=fetchApInterface&runAP="+value + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : detailsSuccessIf, timeout: 120000 }, null);

}

var detailsSuccessIf = function(o) {
	eval("var details = " + o.responseText);
	var value = details.v;
	// 1: wifi0
	// 2: wifi1
	// 3: both
	// 0: none
	var tinf = document.getElementById(formName + "_runInterface");
	if (value==1){
		tinf.length=0;
		tinf.length=1;
		tinf.options[0].text="2.4 GHz (11n/b/g)";
		tinf.options[0].value=1;
		tinf.value=1;
		changeInterfaceType(1);
	} else if (value==2) {
		tinf.length=0;
		tinf.length=1;
		tinf.options[0].text="5 GHz (11n/a)";
		tinf.options[0].value=2;
		tinf.value=2;
		changeInterfaceType(2);
	} else if (value==3) {
		tinf.length=0;
		tinf.length=2;
		tinf.options[0].text="2.4 GHz (11n/b/g)";
		tinf.options[0].value=1;
		tinf.options[1].text="5 GHz (11n/a)";
		tinf.options[1].value=2;
		tinf.value=1;
		changeInterfaceType(1);
	} else {
		tinf.length=0;
		tinf.length=1;
		tinf.options[0].text="None available";
		tinf.options[0].value=-1;
		tinf.value=-1;
		changeInterfaceType(-1);
    }
};

</script>
<div id="content"><s:form action="spectralAnalysis">
<s:hidden name="currentApId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="4px"></td>
		</tr>
		<tr id="configPanel">
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="750px">
				<tr>
					<td style="padding: 4px 4px 4px 4px;" class="noteInfo"><s:property value="noteInfo"/></td>
				</tr>
				<s:if test="%{listRunAPs.size>0}">
				<tr>
					<td style="padding: 4px 4px 4px 4px;">
						<fieldset><legend><s:text name="hm.tool.snp.runpanel.title"/></legend>
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<th class="list" align="left" width="120px"><s:text name="hm.tool.snp.title.apName" /></th>
									<th class="list" align="left" width="120px"><s:text name="config.ipAddress.location" /></th>
									<th>&nbsp;</th>
									<th>&nbsp;</th>
								</tr>
								<s:iterator value="%{listRunAPs}" status="status">
								<tr>
									<td class="list" width="120px"><s:property value="%{hostname}" /></td>
									<td class="list" width="120px"><s:property value="%{mapName}" />&nbsp;</td>
									<td class="list" width="120px"><a href='#1' onclick="submitActionSec('view', '<s:property value="%{id}"/>');">
										<s:text name="hm.tool.snp.runpanel.view"/></a></td>
									<s:if test="%{writeDisabled==''}">
										<td class="list"><a href='#1' onclick="submitActionSec('stop','<s:property value="%{id}"/>');">
											<s:text name="hm.tool.snp.runpanel.stop"/></a></td>
									</s:if>
									<s:else>
										<td class="list"><s:text name="hm.tool.snp.runpanel.stop"/></td>
									</s:else>
								</tr>
								</s:iterator>
							</table>
						</fieldset>
					</td>
				</tr>
				</s:if>
				<tr>
					<td style="padding: 4px 4px 4px 4px;">
						<fieldset><legend><s:text name="hm.tool.snp.newpanel.title"/></legend>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="labelT1" width="150px"><s:text name="hm.tool.snp.hiveap"></s:text></td>
									<td><s:select name="runAP"
											list="%{hiveAPLst}" listKey="id"
											onchange="changeHiveAp(this.options[this.selectedIndex].value);"
											listValue="value" cssStyle="width: 175px;" />&nbsp;
										<input type="button" id="startButton" name="start" value="Start"
										class="button"
										onClick="submitAction('start');"
										<s:property value="runWriteDisabled" />>
									</td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="hm.tool.snp.interface"></s:text></td>
									<td><s:select name="runInterface"
											list="%{interfaceLst}" listKey="key"
											onchange="changeInterfaceType(this.options[this.selectedIndex].value);"
											listValue="value" cssStyle="width: 175px;" /></td>
								</tr>
								<tr id="wifi0Channel" style="display: <s:property value="%{showWifi0Channel}"/>">
									<td class="labelT1"><s:text name="hm.tool.snp.channel.wifi0"></s:text></td>
									<td><s:textfield name="runChannelWifi0" size="28" maxlength="256"
									onkeypress="return hm.util.keyPressPermit(event,'attribute');"/>
									&nbsp;<s:text name="hm.tool.snp.channel.wifi0.range"/></td>
								</tr>
								<tr id="wifi1Channel" style="display: <s:property value="%{showWifi1Channel}"/>">
									<td class="labelT1"><s:text name="hm.tool.snp.channel.wifi1"></s:text></td>
									<td><s:textfield name="runChannelWifi1" size="28"  maxlength="256"
									onkeypress="return hm.util.keyPressPermit(event,'attribute');"/>
									&nbsp;<s:text name="hm.tool.snp.channel.wifi1.range"/></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="hm.tool.snp.interval"></s:text></td>
									<td><s:textfield name="runInterval" size="28"
										onkeypress="return hm.util.keyPressPermit(event,'ten');"
										maxlength="2" />&nbsp;<s:text
										name="hm.tool.snp.interval.range" /></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="hm.tool.snp.runTime"></s:text></td>
									<td><s:select name="runTime"
											list="%{timeLst}" listKey="id"
											listValue="value" cssStyle="width: 175px;" /></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				
			</table>
			</td>
		</tr>
	</table>
</s:form></div>

