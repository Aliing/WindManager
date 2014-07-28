<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script src="<s:url value="/js/hm.paintbrush.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script>
var formName = 'portAccess';
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
    showProcessing();
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].normalView.value = true;
    document.forms[formName].submit();
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}
</script>

<div id="content"><s:form action="portAccess">
	<s:hidden name="product"/>
	<s:hidden name="normalView" value="true"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="new" value="New" class="button"
						id="n_menutoggle"
						<s:property value="writeDisabled" />></td>
					<td><div id="newPortTypeMenu" class="yuimenu"></div></td>
					<td><input type="button" name="new" value="Clone" class="button"
						onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="<s:text name="button.paintbrush"/>"
						id="brushTriggerBtn"
						class="button" onclick="hm.paintbrush.triggerPaintbrush('portAccess')"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="remove" value="Remove"
						class="button" onClick="submitAction('remove');"
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
			<table cellspacing="0" cellpadding="0" border="0"
				class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th align="left" nowrap><ah:sort name="name" key="config.dnsService.name" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">
							<th align="left" nowrap><ah:sort name="product" key="config.port.device.type.title" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">
							<th align="left" nowrap><ah:sort name="portType" key="config.port.type.title" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">
							<th align="left" nowrap><ah:sort name="description" key="config.dnsService.description" /></th>
						</s:if>
					</s:iterator>
					<s:if test="%{showDomain}">
					    <th align="left" nowrap><ah:sort name="owner.domainName" key="config.domain" /></th>
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
		       						<td class="list"><a href='<s:url action="portAccess"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/>
		       							<s:param name="domainId" value="%{#pageRow.owner.id}"/><s:param name="normalView" value="true"/></s:url>'><s:property value="name" /></a></td>
		    					</s:if>
		    					<s:else>
									<td class="list"><a	href='<s:url action="portAccess"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="normalView" value="true"/></s:url>'><s:property
										value="name" /></a></td>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 4}">
								<td class="list">&nbsp;<s:property value="productTypeName" /></td>
							</s:if>
							<s:if test="%{columnId == 3}">
								<td class="list">&nbsp;<s:property value="portTypeName" /></td>
							</s:if>
							<s:if test="%{columnId == 2}">
								<td class="list">&nbsp;<s:property value="description" /></td>
							</s:if>
						</s:iterator>
						<s:if test="%{showDomain}">
						    <td class="list"><s:property value="owner.domainName" /></td>
						</s:if>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>

<script>
	YAHOO.util.Event.onDOMReady(function(){
		var newMenu = new YAHOO.widget.Menu("newPortTypeMenu", { fixedcenter: false, zIndex: 999 });

		var brPara = { product: 2};
		var switchPara = {product: 3};
		newMenu.addItems([
		        	    [
		        	        { text: '<s:text name="config.port.device.type.br"/>', onclick: { fn: onMenuItemClick, obj: brPara } }
		        		],
		        		[
		        	        { text: '<s:text name="config.port.device.type.switch"/>', onclick: { fn: onMenuItemClick, obj: switchPara } }
		        		]
		        ]);

		newMenu.subscribe("beforeShow", function(){
			var x = YAHOO.util.Dom.getX('n_menutoggle');
			var y = YAHOO.util.Dom.getY('n_menutoggle');
			YAHOO.util.Dom.setX('newPortTypeMenu', x);
			YAHOO.util.Dom.setY('newPortTypeMenu', y+20);
		});

		newMenu.render();
		YAHOO.util.Event.addListener("n_menutoggle", "click", newMenu.show, null, newMenu);
	});


	function onMenuItemClick(p_sType, p_aArgs, p_oValue) {
		if(p_oValue.product){
			document.getElementById(formName + "_product").value = p_oValue.product;
		}

		submitAction('new');
	}
</script>