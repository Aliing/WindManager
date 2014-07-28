<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'tvClass';
var thisOperation;

function submitAction(operation) {
	thisOperation = operation;
	if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
    	hm.util.checkAndConfirmClone();
    } else {
        doContinueOper();
    }	
}
function doContinueOper() {
	document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}


var filterFormName = 'tvClassFilter';
YAHOO.util.Event.onDOMReady(init);
var filterOverlay = null;
function init() {
// create filter overlay
	var div = document.getElementById('filterPanel');
	filterOverlay = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	filterOverlay.render(document.body);
	div.style.display = "";

}

function openFilterOverlay(){
	initialValues();
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', true);
	}
}

function hideOverlay(){
	if(null != filterOverlay){
		filterOverlay.cfg.setProperty('visible', false);
	}
}

function submitFilterAction(operation) {
	document.forms[filterFormName].operation.value = operation;
   	document.forms[filterFormName].submit();
}

function initialValues(){
	document.getElementById(filterFormName+"_filterClassName").value = '';
	document.getElementById(filterFormName+"_filterSubject").value = '';
	document.getElementById(filterFormName+"_filterTeacher").value = '';
}

</script>

<div id="content"><s:form action="tvClass">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="new" value="New" class="button"
						onClick="submitAction('new');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Clone"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="remove" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="import" value="Import"
						class="button" onClick="submitAction('import');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="filter" value="Filter"
						class="button" onClick="openFilterOverlay();"
						<s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th align="left" nowrap><ah:sort name="className"
								key="config.tv.className" /></th>
						</s:if>
						<s:elseif test="%{columnId == 2}">
							<th align="left" nowrap><ah:sort name="subject"
								key="config.tv.subject" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 3}">
							<th align="left" nowrap><ah:sort name="teacherId"
								key="config.tv.teacher" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 4}">
							<th align="left" nowrap><ah:sort name="rosterType"
								key="config.tv.rosterType" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 5}">
							<th align="left" nowrap><ah:sort name="description"
								key="config.hp.description" /></th>
						</s:elseif>
					</s:iterator>
					<s:if test="%{showDomain}">
						<th><ah:sort name="owner.domainName" key="config.domain" /></th>
   					</s:if>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status" id="pageRow">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
						<s:if test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
							<td class="listCheck"><input type="checkbox" disabled /></td>
   						</s:if>
   						<s:else>
							<td class="listCheck"><ah:checkItem /></td>
   						</s:else>
   						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
		   						<s:if test="%{showDomain}">
									<td class="list"><a
										href='<s:url value="tvClass.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="className" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="tvClass.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="className" /></a></td>
								</s:else>
							</s:if>
							<s:elseif test="%{columnId == 2}">
								<td class="list"><s:property value="subject" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 3}">
								<td class="list"><s:property value="teacherId" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 4}">
								<td class="list"><s:property value="rosterTypeString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 5}">
								<td class="list"><s:property value="description" />&nbsp;</td>
							</s:elseif>
						</s:iterator>
						<s:if test="%{showDomain}">
							<td class="list"><s:property value="%{owner.domainName}" /></td>
   						</s:if>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>

<div id="filterPanel" style="display: none;">
	<div class="hd">
		Filter Classes By
	</div>
	<div class="bd">
		<s:form action="tvClass" id="tvClassFilter"
			name="tvClassFilter">
			<s:hidden name="operation" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td style="padding: 6px 5px 5px 5px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1">
												<s:text name="config.tv.className" />
											</td>
											<td>
												<s:textfield name="filterClassName" maxlength="32"
													size="30" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:text name="config.tv.subject" />
											</td>
											<td>
												<s:textfield name="filterSubject" maxlength="128"
													size="30" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:text name="config.tv.teacher" />
											</td>
											<td>
												<s:select name="filterTeacher" list="%{filterTeacherList}" listKey="key"
													 listValue="value" headerKey="" headerValue="" cssStyle="width: 175px;" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td style="padding-top: 8px;" colspan="2">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<input type="button" name="ignore" value="Search" id="search"
										class="button" onClick="submitFilterAction('search');">
								</td>
								<td>
									<input type="button" name="ignore" value="Cancel"
										class="button" onClick="hideOverlay();">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>
</div>

