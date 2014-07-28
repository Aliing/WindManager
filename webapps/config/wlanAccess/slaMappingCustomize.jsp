<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<tiles:insertDefinition name="tabView" />
<style>
<!--
html {
	overflow: auto;
}
-->
</style>
<script type="text/javascript">
<!--

var formName = "slaMappingCustomize";
var tabView = null;

function onLoadPage(){
	if(null == tabView){
		tabView = new YAHOO.widget.TabView('customizeTabs');  
	}
}

function validate(operation){
	var els = document.getElementsByTagName("input");
	for(var i=0; i<els.length; i++){
		if(els[i].getAttribute("type") != "text"){
			continue;
		}
		var inputElement = els[i];
		var message = hm.util.validateIntegerRange(inputElement.value,"input",1,100);
	    if (message != null) {
		    var name = inputElement.getAttribute("name");
		    if(name.indexOf('high')>-1){
			    tabView.set('activeIndex', 0);
			}else if(name.indexOf('medium')>-1){
				 tabView.set('activeIndex', 1);
			}else if(name.indexOf('low')>-1){
				 tabView.set('activeIndex', 2);
			}
	        hm.util.reportFieldError(inputElement, message);
	        inputElement.focus();
	        return false;
	    }
	}
   	return true;
}
function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}
//-->
</script>
<div><s:form action="slaMappingCustomize" id="slaMappingCustomize" name="slaMappingCustomize">
	<s:hidden name="operation" />
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td class="buttons">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><input type="button" name="ignore" value="Update"
							class="button" 
							onClick="submitAction('update');"></td>
						<td><input type="button" name="ignore" value="Reset"
							class="button" 
							onClick="submitAction('reset');"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="5px"></td>
		</tr>
		<tr>
			<td>
				<tiles:insertDefinition name="notes" />
			</td>
		</tr>
		<tr>
			<td colspan="10">
				<div id="noteDiv" style="display:none">
				<table width="450px" border="0" cellspacing="0" cellpadding="0"
					class="note">
					<tr>
						<td height="5px"></td>
					</tr>
					<tr>
						<td id="noteId"></td>
					</tr>
					<tr>
						<td height="5px"></td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="10">
			<div id="customizeTabs" class="yui-navset">
			    <ul class="yui-nav">
			        <li class="selected"><a href="#tab1"><em><s:text name="config.radioProfile.sla.throughput.high.simple" /></em></a></li>
			        <li><a href="#tab2"><em><s:text name="config.radioProfile.sla.throughput.medium.simple" /></em></a></li>
			        <li><a href="#tab3"><em><s:text name="config.radioProfile.sla.throughput.low.simple" /></em></a></li>
			    </ul>            
			    <div class="yui-content">
			        <div id="tab1">
			        	<table border="0" cellspacing="0" cellpadding="0" width="100%">
			        		<tr>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.mode" /></th>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.rate" /></th>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.success" /></th>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.usage" /></th>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11a" /></td>
			        			<td class="list"><s:select name="high_11a_rate_1" list="%{_11aRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="high_11a_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="high_11a_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="high_11a_rate_2" list="%{_11aRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="high_11a_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="high_11a_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11b" /></td>
			        			<td class="list"><s:select name="high_11b_rate_1" list="%{_11bRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="high_11b_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="high_11b_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="high_11b_rate_2" list="%{_11bRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="high_11b_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="high_11b_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11g" /></td>
			        			<td class="list"><s:select name="high_11g_rate_1" list="%{_11gRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="high_11g_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="high_11g_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="high_11g_rate_2" list="%{_11gRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="high_11g_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="high_11g_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11n" /></td>
			        			<td class="list"><s:select name="high_11n_rate_1" list="%{_11nRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="high_11n_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="high_11n_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="high_11n_rate_2" list="%{_11nRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="high_11n_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="high_11n_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11ac" /></td>
			        			<td class="list"><s:select name="high_11ac_rate_1" list="%{_11acRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="high_11ac_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="high_11ac_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>			        		
			        		<tr>
			        			<td class="list"><s:select name="high_11ac_rate_2" list="%{_11acRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="high_11ac_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="high_11ac_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>			        		
			        	</table>
			        </div>
			        <div id="tab2">
			        	<table border="0" cellspacing="0" cellpadding="0" width="100%">
			        		<tr>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.mode" /></th>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.rate" /></th>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.success" /></th>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.usage" /></th>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11a" /></td>
			        			<td class="list"><s:select name="medium_11a_rate_1" list="%{_11aRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="medium_11a_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="medium_11a_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="medium_11a_rate_2" list="%{_11aRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="medium_11a_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="medium_11a_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11b" /></td>
			        			<td class="list"><s:select name="medium_11b_rate_1" list="%{_11bRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="medium_11b_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="medium_11b_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="medium_11b_rate_2" list="%{_11bRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="medium_11b_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="medium_11b_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11g" /></td>
			        			<td class="list"><s:select name="medium_11g_rate_1" list="%{_11gRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="medium_11g_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="medium_11g_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="medium_11g_rate_2" list="%{_11gRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="medium_11g_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="medium_11g_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11n" /></td>
			        			<td class="list"><s:select name="medium_11n_rate_1" list="%{_11nRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="medium_11n_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="medium_11n_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="medium_11n_rate_2" list="%{_11nRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="medium_11n_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="medium_11n_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11ac" /></td>
			        			<td class="list"><s:select name="medium_11ac_rate_1" list="%{_11acRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="medium_11ac_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="medium_11ac_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="medium_11ac_rate_2" list="%{_11acRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="medium_11ac_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="medium_11ac_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>	
			        	</table>
			        </div>
			        <div id="tab3">
			        	<table border="0" cellspacing="0" cellpadding="0" width="100%">
			        		<tr>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.mode" /></th>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.rate" /></th>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.success" /></th>
			        			<th align="left"><s:text name="config.radioProfile.sla.customize.table.usage" /></th>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11a" /></td>
			        			<td class="list"><s:select name="low_11a_rate_1" list="%{_11aRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="low_11a_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="low_11a_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="low_11a_rate_2" list="%{_11aRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="low_11a_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="low_11a_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11b" /></td>
			        			<td class="list"><s:select name="low_11b_rate_1" list="%{_11bRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="low_11b_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="low_11b_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="low_11b_rate_2" list="%{_11bRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="low_11b_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="low_11b_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11g" /></td>
			        			<td class="list"><s:select name="low_11g_rate_1" list="%{_11gRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="low_11g_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="low_11g_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="low_11g_rate_2" list="%{_11gRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="low_11g_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="low_11g_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11n" /></td>
			        			<td class="list"><s:select name="low_11n_rate_1" list="%{_11nRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="low_11n_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="low_11n_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="low_11n_rate_2" list="%{_11nRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="low_11n_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="low_11n_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list labelH1" rowspan="2"><s:text name="config.radioProfile.sla.customize.table.mode.11ac" /></td>
			        			<td class="list"><s:select name="low_11ac_rate_1" list="%{_11acRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="low_11ac_success_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="low_11ac_usage_1" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>
			        		<tr>
			        			<td class="list"><s:select name="low_11ac_rate_2" list="%{_11acRates}" listKey="key" listValue="value" cssStyle="width:80px" /></td>
			        			<td class="list"><s:textfield name="low_11ac_success_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        			<td class="list"><s:textfield name="low_11ac_usage_2" size="10" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');" /></td>
			        		</tr>			        		
			        	</table>
			        </div>
			    </div>
			</div>
			</td>
		</tr>
	</table>
</s:form></div>