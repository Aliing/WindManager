<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
var formName = 'tvScheduleMap';  
function onLoadPage() {

}

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
    if (operation == 'addPeriod') {
    	if(Get(formName + "_addSection").value.length==0){
    		hm.util.reportFieldError(Get(formName + "_addSection"), '<s:text name="error.requiredField"><s:param><s:text name="config.tv.schedulemap.section" /></s:param></s:text>');
	        Get(formName + "_addSection").focus();
	        return false;
    	} else {
    		var message = hm.util.validateIntegerRange(Get(formName + "_addSection").value, '<s:text name="config.tv.schedulemap.section" />',1,8);
	      	if (message != null) {
	            hm.util.reportFieldError(Get(formName + "_addSection"), message);
	           	Get(formName + "_addSection").focus();
	            return false;
	      	}
    	}
    	if (Get(formName + "_addSHour").value>Get(formName + "_addEHour").value) {
    	    hm.util.reportFieldError(Get(formName + "_addSHour"), '<s:text name="error.notLargerThan"><s:param><s:text name="config.tv.startTime" /></s:param><s:param><s:text name="config.tv.endTime" /></s:param></s:text>');
	        Get(formName + "_addSHour").focus();
	        return false;
    	} else {
    		if (Get(formName + "_addSHour").value==Get(formName + "_addEHour").value){
	    		if (Get(formName + "_addSMin").value>=Get(formName + "_addEMin").value){
		    		hm.util.reportFieldError(Get(formName + "_addSMin"), '<s:text name="error.notLargerThan"><s:param><s:text name="config.tv.startTime" /></s:param><s:param><s:text name="config.tv.endTime" /></s:param></s:text>');
			        Get(formName + "_addSMin").focus();
			        return false;
	    		}
	    	}
    	}
    }
    
    if (operation == 'addWeek') {
    	var symbolItem = Get(formName + "_addSymbol");
    	if(symbolItem.value.trim().length==0){
    		hm.util.reportFieldError(symbolItem, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.schedulemap.symbol" /></s:param></s:text>');
	        symbolItem.focus();
	        return false;
    	} else if (symbolItem.value.trim().length==1) {
    		if (symbolItem.value.trim()<'A' || symbolItem.value.trim()>'z' || (symbolItem.value.trim()>'Z' && symbolItem.value.trim()<'a')) {
    		    hm.util.reportFieldError(symbolItem, '<s:text name="error.formatInvalid"><s:param><s:text name="config.tv.schedulemap.symbol" /></s:param></s:text>');
		        symbolItem.focus();
		        return false;
    		}
    	} else {
    	    if (symbolItem.value.trim().charAt(0)!='A' && symbolItem.value.trim().charAt(0)!='a') {
    		    hm.util.reportFieldError(symbolItem, '<s:text name="error.formatInvalid"><s:param><s:text name="config.tv.schedulemap.symbol" /></s:param></s:text>');
		        symbolItem.focus();
		        return false;
    		} else {
    			if (symbolItem.value.trim().charAt(1)<'A' || symbolItem.value.trim().charAt(1)>'z' || (symbolItem.value.trim().charAt(1)>'Z' && symbolItem.value.trim().charAt(1)<'a')) {
					hm.util.reportFieldError(symbolItem, '<s:text name="error.formatInvalid"><s:param><s:text name="config.tv.schedulemap.symbol" /></s:param></s:text>');
		        	symbolItem.focus();
		        	return false;
				}
    		}
    	}

    	if (Get(formName + "_addMon").checked==false && Get(formName + "_addTue").checked==false &&
    		Get(formName + "_addWed").checked==false && Get(formName + "_addThu").checked==false &&
    		Get(formName + "_addFri").checked==false && Get(formName + "_addSat").checked==false &&
    		Get(formName + "_addSun").checked==false){
    		
    		hm.util.reportFieldError(Get(formName + "_addSun"), '<s:text name="error.requiredField"><s:param><s:text name="config.tv.weekDay" /></s:param></s:text>');
	        Get(formName + "_addSun").focus();
	        return false;
    	}
    }

    if (operation == 'removePeriod' || operation == 'removePeriodNone') {
    	var table = document.getElementById("checkAll");
		var cbs = document.getElementsByName('scheduleIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param>period</s:param></s:text>');
			return false;
		}
	}
	
	if (operation == 'removeWeek' || operation == 'removeWeekNone') {
    	var table = document.getElementById("checkAll2");
		var cbs = document.getElementsByName('weekIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param>week</s:param></s:text>');
			return false;
		}
	}
	
    if(operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>') {
    	var macIds = document.getElementsByName('scheduleIndices');
    	var table = document.getElementById("checkAll");
		if(macIds.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>period</s:param></s:text>');
       		table.focus();
       		return false;
		}
		
		var macIds = document.getElementsByName('weekIndices');
    	var table = document.getElementById("checkAll2");
		if(macIds.length == 0) {
			hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>week</s:param></s:text>');
       		table.focus();
       		return false;
		}
    }

	return true;
}


function clickMonFri(value){
	if (value) {
		Get(formName + "_addMon").checked=true;
		Get(formName + "_addTue").checked=true;
		Get(formName + "_addWed").checked=true;
		Get(formName + "_addThu").checked=true;
		Get(formName + "_addFri").checked=true;
	} else {
		Get(formName + "_addMon").checked=false;
		Get(formName + "_addTue").checked=false;
		Get(formName + "_addWed").checked=false;
		Get(formName + "_addThu").checked=false;
		Get(formName + "_addFri").checked=false;
	}
}

function clickMonFriStatus(cid,value){
	if (value) {
		Get("arrayWeekMon_" + cid).checked=true;
		Get("arrayWeekTue_" + cid).checked=true;
		Get("arrayWeekWed_" + cid).checked=true;
		Get("arrayWeekThu_" + cid).checked=true;
		Get("arrayWeekFri_" + cid).checked=true;
	} else {
		Get("arrayWeekMon_" + cid).checked=false;
		Get("arrayWeekTue_" + cid).checked=false;
		Get("arrayWeekWed_" + cid).checked=false;
		Get("arrayWeekThu_" + cid).checked=false;
		Get("arrayWeekFri_" + cid).checked=false;
	}
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');

}

function hideCreateSection() {
	hm.util.hide('createButton');
	hm.util.show('newButton');
	hm.util.hide('createSection');
}

function showCreateSection2() {
	hm.util.hide('newButton2');
	hm.util.show('createButton2');
	hm.util.show('createSection2');

}

function hideCreateSection2() {
	hm.util.hide('createButton2');
	hm.util.show('newButton2');
	hm.util.hide('createSection2');
}

function toggleCheckAllRules(cb) {
	var cbs = document.getElementsByName('scheduleIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function toggleCheckAllRules2(cb) {
	var cbs = document.getElementsByName('weekIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
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
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="tvScheduleMap" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> </td>');
	</s:else>
}
</script>
<div id="content"><s:form action="tvScheduleMap">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward != null && lstForward != ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:if>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="720px">
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr style="display:<s:property value="%{showMaxPeriodNote}"/>">
							<td class="noteInfo" style="padding-left: 30px" colspan="2">
								<s:text name="config.tv.classPeriod" /></td>
						</tr>
						<tr>
							<td colspan="2" style="padding:4px 4px 4px 4px;" valign="top">
							<div> <fieldset><legend><s:text name="config.tv.schedulemap.period.title" /></legend>
								<table cellspacing="0" cellpadding="0" border="0" class="embedded" width="100%">
									<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
										<td colspan="4" style="padding: 4px 0 2px 0">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="New"
													class="button" onClick="showCreateSection();"
													<s:property value="applyUpdateDisabled" />></td>
												<td><input type="button" name="ignore" value="Remove"
													class="button" <s:property value="updateDisabled" />
													onClick="submitAction('removePeriod');"></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
										<td colspan="4" style="padding: 4px 0 2px 0">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
													class="button" <s:property value="applyUpdateDisabled" /> onClick="submitAction('addPeriod');"></td>
												<td><input type="button" name="ignore" value="Remove"
													class="button" <s:property value="updateDisabled" />
													onClick="submitAction('removePeriodNone');"></td>
												<td><input type="button" name="ignore" value="Cancel"
													class="button" onClick="hideCreateSection();"></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
										<td colspan="4" style="padding: 4px 4px 4px 2px">
											<div><fieldset>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1"><s:text name="config.tv.schedulemap.section"></s:text></td>
													<td><s:textfield size="30" name="addSection" maxlength="1" />
														<s:text name="config.tv.schedulemap.section.range"></s:text></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text name="config.tv.startTime"></s:text></td>
													<td style="padding-top: 4px"> <s:select name="addSHour" list="%{lstHour}" listKey="key"
															listValue="value" cssStyle="width: 50px;" />&nbsp;:
														<s:select name="addSMin" list="%{lstMin}" listKey="key"
															listValue="value" cssStyle="width: 50px;" /> </td>	
												</tr>
												<tr>
													<td class="labelT1"><s:text name="config.tv.endTime"></s:text></td>
													<td> <s:select name="addEHour" list="%{lstHour}" listKey="key"
															listValue="value" cssStyle="width: 50px;" />&nbsp;:
														<s:select name="addEMin" list="%{lstMin}" listKey="key"
															listValue="value" cssStyle="width: 50px;" /> </td>
												</tr>
											</table>
											</fieldset>
											</div>
										</td>
									</tr>
									<tr id="headerSection">
										<th align="left" style="padding-left: 0;" width="10px"><input
											type="checkbox" id="checkAll"
											onClick="toggleCheckAllRules(this);"></th> 
										<th align="left" width="80px"><s:text
											name="config.tv.schedulemap.section" /></th>
										<th align="left" width="140px" ><s:text
											name="config.tv.startTime" /></th>
										<th align="left" width="140px"><s:text
											name="config.tv.endTime" /></th>
										
									</tr>
									<s:iterator value="%{dataSource.lstPeriod}" status="status" id="periodStatus">
										<tr>
											<td class="listCheck"><s:checkbox name="scheduleIndices"
												fieldValue="%{#status.index}" /></td>
											<td class="list"><s:property value="section"/></td>
											
											
											<td class="list">
													<s:select name="arraySHour" list="%{lstHour}" listKey="key"
															listValue="value" value="%{#periodStatus.sHour}" 
															id="arraySHour_%{#status.index}"
															cssStyle="width: 50px;" />&nbsp;:
													<s:select name="arraySMin" list="%{lstMin}" listKey="key"
															listValue="value" value="%{#periodStatus.sMin}" 
															id="arraySMin_%{#status.index}"
															cssStyle="width: 50px;" />
											</td>
											<td class="list">
													<s:select name="arrayEHour" list="%{lstHour}" listKey="key"
															listValue="value" value="%{#periodStatus.eHour}" 
															id="arrayEHour_%{#status.index}"
															cssStyle="width: 50px;" />&nbsp;:
													<s:select name="arrayEMin" list="%{lstMin}" listKey="key"
															listValue="value" value="%{#periodStatus.eMin}" 
															id="arrayEMin_%{#status.index}"
															cssStyle="width: 50px;" />
											</td>
											
										</tr>
									</s:iterator>
									<s:if test="%{gridCount > 0}">
										<s:generator separator="," val="%{' '}" count="%{gridCount}">
											<s:iterator>
												<tr>
													<td class="list" colspan="4">&nbsp;</td>
												</tr>
											</s:iterator>
										</s:generator>
									</s:if>
								</table>
								</fieldset>
								</div>
							</td>
						</tr>
						<tr>
							<td colspan="2" style="padding:4px 4px 4px 4px;" valign="top">
								<div> <fieldset><legend><s:text name="config.tv.schedulemap.week.title" /></legend>
								<table cellspacing="0" cellpadding="0" border="0" class="embedded" width="100%">
								 	<tr style="display:<s:property value="%{hideNewButton2}"/>" id="newButton2">
										<td colspan="5" style="padding: 4px 0 2px 0">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="New"
													class="button" onClick="showCreateSection2();"
													<s:property value="applyUpdateDisabled2" />></td>
												<td><input type="button" name="ignore" value="Remove"
													class="button" <s:property value="updateDisabled" />
													onClick="submitAction('removeWeek');"></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem2}"/>" id="createButton2">
										<td colspan="5" style="padding: 4px 0 2px 0">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
													class="button" <s:property value="applyUpdateDisabled2" /> onClick="submitAction('addWeek');"></td>
												<td><input type="button" name="ignore" value="Remove"
													class="button" <s:property value="updateDisabled" />
													onClick="submitAction('removeWeekNone');"></td>
												<td><input type="button" name="ignore" value="Cancel"
													class="button" onClick="hideCreateSection2();"></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr style="display:<s:property value="%{hideCreateItem2}"/>" id="createSection2">
										<td colspan="5" style="padding: 4px 4px 4px 2px">
											<div><fieldset>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1"><s:text name="config.tv.schedulemap.symbol"></s:text></td>
													<td colspan="8" style="padding-top: 2px"><s:textfield size="30" name="addSymbol" maxlength="2" />
														<s:text name="config.tv.schedulemap.symbol.range"></s:text></td>
												</tr>
												<tr>
													<td class="labelT1" width="120px"><s:text name="config.tv.weekDay"></s:text></td>
													<td width="80px"><s:checkbox name="addSun"/><s:text name="config.tv.weekday.1"/></td>
													<td width="80px"><s:checkbox name="addMon"/><s:text name="config.tv.weekday.2"/></td>
													<td width="80px"><s:checkbox name="addTue"/><s:text name="config.tv.weekday.3"/></td>
													<td width="80px"><s:checkbox name="addWed"/><s:text name="config.tv.weekday.4"/></td>
													<td width="80px"><s:checkbox name="addThu"/><s:text name="config.tv.weekday.5"/></td>
													<td width="80px"><s:checkbox name="addFri"/><s:text name="config.tv.weekday.6"/></td>
													<td width="80px"><s:checkbox name="addSat"/><s:text name="config.tv.weekday.7"/></td>
													<td width="130px"><s:checkbox name="addMonFri" onclick="clickMonFri(this.checked);"/><s:text name="config.tv.weekday.8"/></td>
												</tr>
											</table>
											</fieldset>
											</div>
										</td>
									</tr>
									<tr id="headerSection">
										<th align="left" style="padding-left: 0;" width="10px"><input
											type="checkbox" id="checkAll2"
											onClick="toggleCheckAllRules2(this);"></th> 
										<th align="left" width="60px"><s:text
											name="config.tv.schedulemap.symbol" /></th>
										<th align="left" colspan="3"><s:text
											name="config.tv.weekDay" /></th>
										
									</tr>
									<s:iterator value="%{dataSource.lstWeek}" status="status" id="weekStatus">
										<tr>
											<td class="listCheck"><s:checkbox name="weekIndices"
												fieldValue="%{#status.index}" /></td>
											<td class="list" width="60px"><s:property value="symbol"/></td>
											<td colspan="3">
												<table border="0" cellspacing="0" cellpadding="0" width="100%">
													<tr>
														<td class="list">		
															<s:checkbox name="arrayWeekSun" value="%{#weekStatus.cSun}" 
																fieldValue="%{symbol}"	id="arrayWeekSun_%{#status.index}" />
																<s:text name="config.tv.weekday.1"/></td>
														<td class="list">
															<s:checkbox name="arrayWeekMon" value="%{#weekStatus.cMon}" 
																fieldValue="%{symbol}"	id="arrayWeekMon_%{#status.index}" />
																<s:text name="config.tv.weekday.2"/></td>
														<td class="list">		
															<s:checkbox name="arrayWeekTue" value="%{#weekStatus.cTue}" 
																fieldValue="%{symbol}"	id="arrayWeekTue_%{#status.index}" />
																<s:text name="config.tv.weekday.3"/></td>
														<td class="list">		
															<s:checkbox name="arrayWeekWed" value="%{#weekStatus.cWed}" 
																fieldValue="%{symbol}"	id="arrayWeekWed_%{#status.index}" />
																<s:text name="config.tv.weekday.4"/></td>
														<td class="list">		
															<s:checkbox name="arrayWeekThu" value="%{#weekStatus.cThu}" 
																fieldValue="%{symbol}"	id="arrayWeekThu_%{#status.index}" />
																<s:text name="config.tv.weekday.5"/></td>
														<td class="list">		
															<s:checkbox name="arrayWeekFri" value="%{#weekStatus.cFri}" 
																fieldValue="%{symbol}"	id="arrayWeekFri_%{#status.index}" />
																<s:text name="config.tv.weekday.6"/></td>
														<td class="list">		
															<s:checkbox name="arrayWeekSat" value="%{#weekStatus.cSat}" 
																fieldValue="%{symbol}" id="arrayWeekMon_%{#status.index}" />
																<s:text name="config.tv.weekday.7"/></td>
														<td class="list">		
															<s:checkbox name="arrayWeekMonFri" value="%{#weekStatus.cMonFri}" 
																	id="arrayWeekMonFri_%{#status.index}" onclick="clickMonFriStatus(%{#status.index},this.checked);"/>
																<s:text name="config.tv.weekday.8"/></td>
													<tr>
												</table>
											</td>
											
										</tr>
									</s:iterator>
									<s:if test="%{gridWeekCount > 0}">
										<s:generator separator="," val="%{' '}" count="%{gridWeekCount}">
											<s:iterator>
												<tr>
													<td class="list" colspan="5">&nbsp;</td>
												</tr>
											</s:iterator>
										</s:generator>
									</s:if>
								</table>
								</fieldset>
								</div>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
