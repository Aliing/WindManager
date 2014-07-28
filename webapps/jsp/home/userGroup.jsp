<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<tiles:insertDefinition name="tabView" />
<%-- Style overrides --%>
<style>
.yui-skin-sam .yui-navset .yui-content {
	background:#edf5ff;
}
</style>

<script>
var formName = 'userGroups';
var isInHomeDomain;
function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	if(operation == '<%=Navigation.L2_FEATURE_ADMIN_GROUPS%>' || operation == 'cancel<s:property value="lstForward"/>')
	{
		if (<s:property value="%{showAttribute}" />)
    	{
    		document.getElementById("attribute").value = 0;
    	}
		
		return true;
	}

	var inputElement = document.getElementById("groupname");
    if (inputElement.value.length == 0) {
    	userGroupTabs.set('activeIndex', 0);
        hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="admin.usergroup.name" /></s:param></s:text>');
        inputElement.focus();
        return false;
    }
    if (inputElement.value.indexOf(' ') > -1) {
    	userGroupTabs.set('activeIndex', 0);
        hm.util.reportFieldError(inputElement, '<s:text name="error.name.containsBlank"><s:param><s:text name="admin.usergroup.name" /></s:param></s:text>');
        inputElement.focus();
        return false;
	}
	
	if (<s:property value="%{showAttribute}" />)
	{
		var attribute = document.getElementById("attribute");
		if (attribute.value.length == 0) {
			userGroupTabs.set('activeIndex', 0);
	        hm.util.reportFieldError(attribute, '<s:text name="error.requiredField"><s:param><s:text
							name="admin.usergroup.attribute" /></s:param></s:text>');
	        attribute.focus();
	        return false;
	    }
	    var message = hm.util.validateIntegerRange(attribute.value, '<s:text name="admin.usergroup.attribute" />',10,65535);
	    if (message != null) {
	    	userGroupTabs.set('activeIndex', 0);
	        hm.util.reportFieldError(attribute, message);
	        attribute.focus();
	        return false;
	    }
	}
	return true;
}
var image_plus, image_minus;
var userGroupTabs;
function onLoadPage() {
	image_plus = hm.util.loadImage("<s:url value="/images/expand_plus_line.gif" includeParams="none"/>");
	image_minus = hm.util.loadImage("<s:url value="/images/expand_minus_line.gif" includeParams="none"/>");
	userGroupTabs = new YAHOO.widget.TabView("userGroupTabs", {activeIndex:0});
	isInHomeDomain = <s:property value="%{isInHomeDomain}"/>;
}

var featureTree = new Array();
<s:iterator id="permission" value="featurePermissions" status="status">
	featureTree[featureTree.length] = {id: "<s:property value="key" />", ns: <s:property value="nextSibling" />, cc: <s:property value="childCount" />, p: <s:property value="parent" />, e: false};
</s:iterator>
function toggleFeature(index) {
	var img = document.getElementById('i' + index);
	img.blur();
	var expanded = featureTree[index].e;
	for (var i = 0; i < featureTree[index].cc; i++) {
		if (expanded) {
			hm.util.hide('fr' + (index + i + 1));
		} else {
			hm.util.show('fr' + (index + i + 1));
		}
	}
	if (expanded) {
		img.src = image_plus.src;
	} else {
		img.src = image_minus.src;
	}

	featureTree[index].e = !expanded;

	var visibleDepth = getVisibleDepth(featureTree);
	setColspans(featureTree, visibleDepth);
}
function getVisibleDepth(tree) {
	var visibleDepth = 1;
	var i = 0;
	do {
		var node = tree[i];
		if (node.e) {
			if (visibleDepth < 2) {
				visibleDepth = 2;
			}
			var ci = i + 1;
			do {
				var child = tree[ci];
				if (child.cc > 0) {
					if (visibleDepth < 3) {
						visibleDepth = 3;
					}
					var gci = ci + 1;
					do {
						var gchild = tree[gci];
						if (gchild.cc > 0) {
							if (visibleDepth < 4) {
								visibleDepth = 4;
							}
							var ggci = gci + 1;
							do {
								var ggchild = tree[ggci];
								if (ggchild.cc > 0 && visibleDepth < 5) {
									visibleDepth = 5;
								}
								ggci = ggchild.ns;
							} while (ggci > 0)
						}
						gci = gchild.ns;
					} while (gci > 0)
				}
				ci = child.ns;
			} while (ci > 0)
		}
		i = node.ns;
	} while (i > 0)
	return visibleDepth;
}
function setColspans(tree, depth) {
	var i = 0;
	do {
		var node = tree[i];
		if (node.e) {
			var ci = i + 1;
			do {
				var child = tree[ci];
				setTdColspan('td' + ci, depth - 1);
				if (child.cc > 0) {
					var gci = ci + 1;
					do {
						var gchild = tree[gci];
						setTdColspan('td' + gci, depth - 2);
						if (gchild.cc > 0) {
							var ggci = gci + 1;
							do {
								var ggchild = tree[ggci];
								setTdColspan('td' + ggci, depth - 3);
								if (ggchild.cc > 0) {
									var gggci = ggci + 1;
									do {
										var gggchild = tree[gggci];
										gggci = gggchild.ns;
									} while(gggci > 0)
								}
								ggci = ggchild.ns;
							} while (ggci > 0)
						}
						gci = gchild.ns;
					} while (gci > 0)
				}
				ci = child.ns;
			} while (ci > 0)
		}
		i = node.ns;
	} while (i > 0)
}
function setTdColspan(id, colspan) {
	var td = document.getElementById(id);
	td.colSpan = colspan;
}
function toggleFeatureRead(readCb, index) {
	toggleRead(featureTree, readCb, index);
	if(readCb.id == 'r'+mapFeatureId || readCb.id == 'r'+mapHierarchyId){
		//disable or enable map instance checkbox
		if(readCb.checked){
			updateMapCheckBoxs('r', false);
		}else{
			updateMapCheckBoxs('r', true);
			updateMapCheckBoxs('w', true);
		}
	}
	var parent = featureTree[index].p;
	if (parent < 0 || readCb.checked) {
		return;
	}
	if (allChildNodesMatch(featureTree, parent, 'r', false)) {
		toggleCb(featureTree[parent].id, 'r', false);
	}
	if (allChildNodesMatch(featureTree, parent, 'w', false)) {
		toggleCb(featureTree[parent].id, 'w', false);
	}
}
function toggleFeatureWrite(writeCb, index) {
	toggleWrite(featureTree, writeCb, index);
	if(writeCb.id == 'w'+mapFeatureId || writeCb.id == 'w'+mapHierarchyId){
		//disable or enable map instance checkbox
		if(writeCb.checked){
			updateMapCheckBoxs('r', false);
			updateMapCheckBoxs('w', false);
		}else{
			updateMapCheckBoxs('w', true);
		}
	}
	var parent = featureTree[index].p;
	if (parent < 0) {
		return;
	}
	if (allChildNodesMatch(featureTree, parent, 'w', writeCb.checked)) {
		toggleCb(featureTree[parent].id, 'w', writeCb.checked);
	} else if (!writeCb.checked) {
		toggleCb(featureTree[parent].id, 'w', false);
	}
}

var mapFeatureId = "<s:property value="mapFeatureKey" />";
var mapHierarchyId = "<s:property value="mapHierarchyKey" />";
var mapTree = new Array();
<s:iterator id="permission" value="mapPermissions" status="status">
	mapTree[mapTree.length] = {id: <s:property value="id" />, ns: <s:property value="nextSibling" />, cc: <s:property value="childCount" />, p: <s:property value="parent" />};
</s:iterator>
function toggleMapRead(readCb, index) {
	toggleRead(mapTree, readCb, index);
	<%-- check map instance not refect to map feature
	if(readCb.checked){
		// Make sure user has read access to map feature
		var mapFeatureCb = document.getElementById('r' + mapFeatureId);
		if(mapFeatureCb && !mapFeatureCb.checked){
			clickCheckBox(mapFeatureCb);
		}
	}
	--%>
}
var triggerFromMap = false;
function toggleMapWrite(writeCb, index) {
	toggleWrite(mapTree, writeCb, index);
	<%-- check map instance not refect to map feature
	if(writeCb.checked){
		// Make sure user has write access to map feature
		var mapFeatureCb = document.getElementById('w' + mapFeatureId);
		if(mapFeatureCb && !mapFeatureCb.checked){
			triggerFromMap = true;
			clickCheckBox(mapFeatureCb);
		}
	}
	--%>
}
function toggleRead(tree, readCb, index) {
	toggleChildCbs(tree, index, 'r', readCb.checked);
	if (readCb.checked) {
		// Make sure user has read access to all parent nodes 
		toggleParentCbs(tree, index, 'r', readCb.checked);
	} else {
		toggleCb(tree[index].id, 'w', readCb.checked);
		toggleChildCbs(tree, index, 'w', readCb.checked);
	}
}
function toggleWrite(tree, writeCb, index) {
	toggleChildCbs(tree, index, 'w', writeCb.checked);
	if (writeCb.checked) {
		toggleCb(tree[index].id, 'r', writeCb.checked);
		toggleChildCbs(tree, index, 'r', writeCb.checked);
		// Make sure user has read access to all parent nodes 
		toggleParentCbs(tree, index, 'r', writeCb.checked);
	}
}
function toggleChildCbs(tree, index, type, value) {
	for (var i = 0; i < tree[index].cc; i++) {
		toggleCb(tree[index + i + 1].id, type, value);
	}
}
function toggleParentCbs(tree, index, type, value) {
	var parent = tree[index].p;
	while (parent != -1) {
		toggleCb(tree[parent].id, type, value);
		parent = tree[parent].p;
	}
}
function toggleCb(id, type, value) {
	var childCb = document.getElementById(type + id);
	
	if(childCb.value=='<%=Navigation.L2_FEATURE_LICENSEMGR%>'
		|| childCb.value=='<%=Navigation.L2_FEATURE_USER_PASSWORD_MODIFY%>') {
		return;
	}
	
	if (childCb.checked != value) {
		childCb.checked = value;
	}
}
function allChildNodesMatch(tree, index, type, value) {
	for (var i = 0; i < tree[index].cc; i++) {
		var childCb = document.getElementById(type + tree[index + i + 1].id);
		if (childCb.checked != value) {
			return false;
		}
	}
	return true;
}
function clickCheckBox(cb){
	if(document.createEvent){ //DOM
		var evt = document.createEvent("MouseEvents");
		evt.initEvent("click", true, true);
		cb.dispatchEvent(evt);
	}else{
		cb.click();
	}
}
function updateMapCheckBoxs(type, isDisable){
	for(var index in mapTree){
		var el = document.getElementById(type + mapTree[index].id);
		if(!el){continue;}
		if(isDisable){
			el.checked = false;
			el.disabled = true;
		}else{
			el.disabled = false;
			el.checked = true;
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
       document.writeln('<td class="crumb" nowrap><a href="<s:url action="userGroups" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
       <s:if test="%{dataSource.id == null}">
           document.writeln('New </td>');
       </s:if>
       <s:else>
       	   	<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="displayName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="displayName" />\'</td>');
			</s:else>
       </s:else>
    </s:else>
    /*
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="userGroups" includeParams="none"/>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{operation == 'new'}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		document.writeln('Edit \'<s:property value="dataSource.groupName" />\'</td>');
	</s:else> 
	*/
}
</script>

<div id="content"><s:form action="userGroups">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore"
							value="<s:text name="button.create"/>" class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled4HHM" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore"
							value="<s:text name="button.update"/>" class="button"
							onClick="submitAction('update');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_ADMIN_GROUPS%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:else>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<div id="userGroupTabs" class="yui-navset" style="width:600px;">
			<ul class="yui-nav">
				<li class="selected"><a href="#tab1"><em>Features</em></a></li>
				<s:if test="%{showMapTab}"><li><a href="#tab2"><em>Maps</em></a></li></s:if>
			</ul>
			<div class="yui-content">
			<div>
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td class="labelT1" width="60" style="padding-left: 4px;"><s:text
						name="admin.usergroup.name" /><font color="red"><s:text
						name="*" /></font></td>
					<td width="400"><s:textfield id="groupname"
						name="dataSource.groupName" value="%{displayName}"
						disabled="%{disabledName}"
						onkeypress="return hm.util.keyPressPermit(event,'password');"
						maxlength="%{groupNameLength}" size="28" /> <s:text
						name="admin.usergroup.nameRange" /></td>
					<td>&nbsp;</td>
				</tr>
				<s:if test="%{showAttribute}">
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td class="labelT1" width="60" style="padding-left: 4px;"><s:text
								name="admin.usergroup.attribute" /><font color="red"><s:text
								name="*" /></font></td>
							<td width="400"><s:textfield id="attribute"
								name="dataSource.groupAttribute"
								onkeypress="return hm.util.keyPressPermit(event,'ten');"
								maxlength="5" size="8" 
								disabled="%{disabledGroupAttribute}" /><s:if test="%{!disabledGroupAttribute}"> <s:text
								name="admin.usergroup.attributeRange" /></s:if></td>
							<td>&nbsp;</td>
						</tr>
				</s:if>
			</table>
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<th align="center" style="padding-left: 4px;">Read</th>
					<th align="center">Write</th>
					<th>&nbsp;</th>
					<th align="left" style="padding-left: 3px;" colspan="5">Feature</th>
				</tr>
				<s:iterator id="permission" value="featurePermissions"
					status="status">
					<tiles:insertDefinition name="rowClass" />
					<s:if test="%{level == 0}">
						<s:set name="display" value="%{''}" />
					</s:if>
					<s:else>
						<s:set name="display" value="%{'none'}" />
					</s:else>
					<tr class="<s:property value="%{#rowClass}"/>"
						id="fr<s:property value="%{#status.index}" />"
						style="display:<s:property value="%{#display}"/>">
						<s:if test="%{readAccess}">
							<s:set name="readChecked" value="%{'checked'}" />
						</s:if>
						<s:else>
							<s:set name="readChecked" value="%{''}" />
						</s:else>
						<s:if test="%{writeAccess}">
							<s:set name="writeChecked" value="%{'checked'}" />
						</s:if>
						<s:else>
							<s:set name="writeChecked" value="%{''}" />
						</s:else>
						<s:if test="%{key != 'userPasswordModify' && key != 'licenseMgr' && key != 'dateTimeConfig'}">
							<td class="listCheck" align="center"><input type="checkbox"
								name="readFeatureIds" id="r<s:property value="key" />"
								onClick="toggleFeatureRead(this, <s:property value="%{#status.index}" />);"
								value="<s:property value="key" />"
								<s:property value="%{#readChecked}" /> /></td>
							<td class="listCheck" align="center"><input type="checkbox"
								name="writeFeatureIds" id="w<s:property value="key" />"
								onClick="toggleFeatureWrite(this, <s:property value="%{#status.index}" />);"
								value="<s:property value="key" />"
								<s:property value="%{#writeChecked}" /> /></td>						
						</s:if>
						<s:else>
							<td class="listCheck" align="center"><input type="checkbox"
								name="readFeatureIds" id="r<s:property value="key" />"
								onClick="toggleFeatureRead(this, <s:property value="%{#status.index}" />);"
								value="<s:property value="key" />"
								disabled="disabled"
								<s:property value="%{#readChecked}" /> /></td>
							<td class="listCheck" align="center"><input type="checkbox"
								name="writeFeatureIds" id="w<s:property value="key" />"
								onClick="toggleFeatureWrite(this, <s:property value="%{#status.index}" />);"
								value="<s:property value="key" />"
								disabled="disabled"
								<s:property value="%{#writeChecked}" /> /></td>
						</s:else>
						<s:if test="%{level == 0}">
							<td class="list" style="padding: 2px 0px 0px 5px;"><a
								href="javascript: toggleFeature(<s:property value="%{#status.index}" />)"
								style="cursor:default;"><img
								src="<s:url value="/images/expand_plus_line.gif" includeParams="none"/>"
								style="border:0;" alt="" width="16" height="11"
								id="i<s:property value="%{#status.index}" />" /></a></td>
							<td class="listCheck" style="padding: 2px 5px 2px 3px;"
								id="td<s:property value="%{#status.index}" />" colspan="5"
								width="100%"><s:property value="nodeName" /></td>
						</s:if>
						<s:else>
							<td class="listCheck">&nbsp;</td>
							<s:iterator id="td" value="%{#permission.treeIndentation}"
								status="tdStatus">
								<s:if test="%{!#tdStatus.first}">
									<s:if test="%{#td}">
										<td class="listCheck"><img
											src="<s:url value="/images/menutree/treenode_grid_v.gif" includeParams="none"/>"
											alt="" width="18" height="20" class="dblk" /></td>
									</s:if>
									<s:else>
										<td class="listCheck">&nbsp;</td>
									</s:else>
								</s:if>
							</s:iterator>
							<td class="listCheck"><img
								src="<s:url value="%{#permission.treeImage}" includeParams="none"/>"
								alt="" width="18" height="20" class="dblk" /></td>
							<td class="listCheck" style="padding: 2px 5px 2px 3px;"
								width="100%" id="td<s:property value="%{#status.index}" />"
								colspan="<s:property value="%{5 - #permission.level}" />"><s:property
								value="nodeName" /></td>
						</s:else>
					</tr>
				</s:iterator>
			</table>
			</div>
			<s:if test="%{showMapTab}">
			<div>
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<th align="center">Read</th>
					<th align="center">Write</th>
					<th>&nbsp;</th>
					<th align="left" style="padding-left: 5px;"
						colspan="<s:property value="%{mapsDepth + 1}" />">Map Name</th>
				</tr>
				<s:iterator id="permission" value="mapPermissions" status="status">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
						<s:if test="%{readAccess}">
							<s:set name="readChecked" value="%{'checked'}" />
						</s:if>
						<s:else>
							<s:set name="readChecked" value="%{''}" />
						</s:else>
						<s:if test="%{writeAccess}">
							<s:set name="writeChecked" value="%{'checked'}" />
						</s:if>
						<s:else>
							<s:set name="writeChecked" value="%{''}" />
						</s:else>
						<td class="listCheck" align="center"><input type="checkbox"
							name="readMapIds" id="r<s:property value="id" />"
							onClick="toggleMapRead(this, <s:property value="%{#status.index}" />);"
							value="<s:property value="id" />" <s:property value="%{mapInstanceReadDisabled}" />
							<s:property value="%{#readChecked}" /> /></td>
						<td class="listCheck" align="center"><input type="checkbox"
							name="writeMapIds" id="w<s:property value="id" />"
							onClick="toggleMapWrite(this, <s:property value="%{#status.index}" />);"
							value="<s:property value="id" />" <s:property value="%{mapInstanceWriteDisabled}" />
							<s:property value="%{#writeChecked}" /> /></td>
						<td class="listCheck"><img
							src="<s:url value="/images/spacer.gif" includeParams="none"/>"
							width="3" height="1" alt="" class="dblk" /></td>
						<s:iterator id="td" value="%{#permission.treeIndentation}"
							status="status">
							<s:if test="%{#td}">
								<td class="listCheck"><img
									src="<s:url value="/images/menutree/treenode_grid_v.gif" includeParams="none"/>"
									alt="" width="18" height="20" class="dblk" /></td>
							</s:if>
							<s:else>
								<td class="listCheck">&nbsp;</td>
							</s:else>
						</s:iterator>
						<td class="listCheck"><img
							src="<s:url value="%{#permission.treeImage}" includeParams="none"/>"
							alt="" width="18" height="20" class="dblk" /></td>
						<td class="listCheck" style="padding: 2px 5px 2px 3px;"
							width="100%"
							colspan="<s:property value="%{mapsDepth - #permission.level}" />"><s:property
							value="nodeName" /></td>
					</tr>
				</s:iterator>
			</table>
			</div>
			</s:if>
			</div>
			</div>
			</td>
		</tr>
	</table>
</s:form></div>
