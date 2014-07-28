<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'vhmManagement';

function onLoadPage() {
	createOperationMenu();
}

var switchPermission = <s:property value="showLogoutMenu" />;

var operationMenu;
function createOperationMenu()
{
	operationMenu = new YAHOO.widget.Menu('operation_menu', { fixedcenter: false });
	var operationItems = [
			 [
		        { text: '<s:text name="admin.vhmMgr.switchVHM"/>',disabled: !switchPermission}
			 ]
	];
	
	operationMenu.addItems(operationItems);
	operationMenu.subscribe('click', operationMenuItemClick);
	operationMenu.subscribe("beforeShow", function(){
		var x = YAHOO.util.Dom.getX('operationMenuBtn');
		var y = YAHOO.util.Dom.getY('operationMenuBtn');
		YAHOO.util.Dom.setX('operation_menu', x);
		YAHOO.util.Dom.setY('operation_menu', y+20);
	});
	operationMenu.render(document.body);
}

function operationMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	if(menuItem.cfg.getProperty("disabled") == true){
		return;
	}
	var text = menuItem.cfg.getProperty("text");
	
	if (text == '<s:text name="admin.vhmMgr.switchVHM"/>') 
	{
		switchDomainFromMgr();
	} 
}

var urls = ["<s:property value="%{selectedL1Feature.action}"/>", "<s:property value="%{selectedL2FeatureKey}"/>"];
function switchDomainFromMgr(domainName) {
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length != 1){
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return false;
	}

	var url = "<s:url action="switchDomain" includeParams="none" />?operation=switchDomain&switchDomainID=" + selectedIds[0] + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : switchResult }, null);
}
function switchResult(o) {
	eval("var result = " + o.responseText);
	if (result.success) {
		var td = window.document.getElementById('login_name');
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(result.dn));
		var redirect_url;
		if(urls[0].length == 0 || urls[1].length == 0 ){
			redirect_url = "<s:url value="index.jsp" includeParams="none"/>";
		}else{
			redirect_url = "<s:url action='" + urls[0] + "' includeParams="none" />" + "?operation=" + urls[1];
		}
		window.location.href = redirect_url;
	}else{
		if(result.msg && warnDialog != null){
			warnDialog.cfg.setProperty('text', result.msg);
			warnDialog.show();
		}
	}
}

function showOperationMenu()
{
	operationMenu.show();
}

var thisOperation;
function submitAction(operation) {
    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDeleteVhm();
    } else if (operation == 'clone') {
        hm.util.checkAndConfirmClone();
    } else {
        doContinueOper();
    }   
}
function doContinueOper() {
    showProcessing();
    if (thisOperation=='remove') {
    	document.forms[formName].resetDeviceFlag.value = Get('ck_resetDeviceFlag').checked;
    }
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content">
	<s:form action="vhmManagement">
	<s:hidden name="resetDeviceFlag"/>
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
								<input type="button" name="new" value="New" class="button"
									onClick="submitAction('new');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="ignore" value="Clone" class="button"
									onClick="submitAction('clone');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="remove" value="Remove" class="button"
									onClick="submitAction('remove');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="ignore" value="Operation..."
									class="button" id="operationMenuBtn"
									onclick="showOperationMenu();"
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
				<td>
					<table cellspacing="0" cellpadding="0" border="0" class="view">
						<tr>
							<th class="check">
								<input type="checkbox" id="checkAll"
									onClick="hm.util.toggleCheckAll(this);">
							</th>
							<s:iterator value="%{selectedColumns}">
								<s:if test="%{columnId == 1}">
									<th>
										<ah:sort name="domainName" key="admin.vhmMgr.vhm" />
									</th>
								</s:if>
								<s:if test="%{columnId == 2}">
									<th>
										<ah:sort name="runStatus" key="admin.vhmMgr.runStatus" />
									</th>
								</s:if>
								<s:if test="%{columnId == 4}">
									<th>
										<ah:sort name="supportGM" key="admin.vhmMgr.userMgr" />
									</th>
								</s:if>
								<s:if test="%{columnId == 3}">
									<th>
										<ah:sort name="maxApNum" key="admin.vhmMgr.maxAPNum" />
									</th>
								</s:if>
								<s:if test="%{columnId == 6}">
									<th>
										<ah:sort name="vhmID" key="admin.vhmMgr.vhmID" />
									</th>
								</s:if>
								<s:if test="%{columnId == 7}">
									<th>
										<ah:sort name="maxSimuAp" key="admin.vhmMgr.maxSimulatedAPNum" />
									</th>
								</s:if>
								<s:if test="%{columnId == 8}">
									<th>
										<ah:sort name="maxSimuClient" key="admin.vhmMgr.maxSimulatedClientNum" />
									</th>
								</s:if>
								<s:if test="%{columnId == 5}">
									<th>
										<s:text name="admin.vhmMgr.description" />
									</th>
								</s:if>
							</s:iterator>
						</tr>
						<s:if test="%{page.size() == 0}">
							<ah:emptyList />
						</s:if>
						<tiles:insertDefinition name="selectAll" />
						<s:iterator value="page" status="status" id="pageRow">
							<tiles:insertDefinition name="rowClass" />
							<tr class="<s:property value="%{#rowClass}"/>">
								<td class="listCheck">
									<ah:checkItem />
								</td>
								<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 1}">
										<td class="list">
											<a
												href='<s:url action="vhmManagement"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
													value="domainName" /> </a>
										</td>
									</s:if>
									<s:if test="%{columnId == 2}">
										<td class="list">
											<s:property value="runStatusShow" />
										</td>
									</s:if>
									<s:if test="%{columnId == 4}">
										<td class="list">
											<s:property value="GMCapability" />
										</td>
									</s:if>
									<s:if test="%{columnId == 3}">
										<td class="list">
											<s:property value="maxApNum" />
										</td>
									</s:if>
									<s:if test="%{columnId == 6}">
										<td class="list">
											<s:property value="vhmID" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 7}">
										<td class="list">
											<s:property value="maxSimuAp" />
										</td>
									</s:if>
									<s:if test="%{columnId == 8}">
										<td class="list">
											<s:property value="maxSimuClient" />
										</td>
									</s:if>
									<s:if test="%{columnId == 5}">
										<td class="list">
											<s:property value="comment" />
											&nbsp;
										</td>
									</s:if>
								</s:iterator>
							</tr>
						</s:iterator>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
