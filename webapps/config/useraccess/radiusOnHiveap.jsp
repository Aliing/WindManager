<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.useraccess.RadiusOnHiveap"%>
<%@page import="com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap"%>
<tiles:insertDefinition name="tabView" />

<script src="<s:url value="/js/hm.options.js" />"></script>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/yui/treeview/treeview-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/hm.widget.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/treeview/assets/treeview.css" includeParams="none"/>" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/treeview/assets/css/folders/treeview.css" includeParams="none"/>" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/hm.widget.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<style>
<!--
fieldset{
	padding: 0 10px 10px 10px;
	margin: 0 4px 6px 4px;
}
.servericon { display:block; height: 22px; padding-left: 20px; background: transparent url(<s:url value="/yui/treeview/assets/server.PNG" includeParams="none"/>) 0 0px no-repeat; }
-->
</style>
<script><!--
var dbTypeTabs = null;
var formName = 'radiusOnHiveAp';
var TYPE_LEAP = <%=RadiusOnHiveap.RADIUS_AUTH_TYPE_LEAP%>;
var TYPE_GROUP = <%=RadiusOnHiveap.RADIUS_SERVER_MAP_BY_GROUPATTRI%>;
var TYPE_USER = <%=RadiusOnHiveap.RADIUS_SERVER_MAP_BY_USERATTRI%>;
var FOLDER_RADIUS = "radiusSettings";
var FOLDER_DATABASE = "databaseAccess";
var FOLDER_NAS = "nasSettings";
var mapType = <s:property value="%{dataSource.mapByGroupOrUser}"/>;
var localEnabled;
var ACTIVE_TREE = <%=ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY%>;  // 1
var OPEN_TREE = <%=ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP%>;           // 2
var OPENDIR_TREE = <%=ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY%>;   // 3

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
	waitingPanel.setHeader("Retrieving Information...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

YAHOO.util.Event.addListener(window, "load", function() {
		createWaitingPanel();
});

function onLoadPage() {
	localEnabled = <s:property value="%{local}"/>;
	if (!document.getElementById(formName + "_dataSource_radiusName").disabled) {
		document.getElementById(formName + "_dataSource_radiusName").focus();
	}
	if (YAHOO.env.ua.ie) {
		document.getElementById('authTitle').width="175px";
	}
	dbTypeTabs = new YAHOO.widget.TabView("dbTypeTabs");
	document.getElementById("dbTypeTabs").style.display = "";
	var tabId = <s:property value="%{tabId}"/>;
	<s:if test="%{local}">
	// add tab 'Local Database'
		var tabIndex = 0;  // if no other tabs are checked, insert local as first item
		var tabs = dbTypeTabs.get('tabs');
		if (tabs.length == 2) {
			// if 'External Database' and 'Library SIP Server' already checked, insert local to the middle
			tabIndex = 1;
		} else if (tabs.length == 1) {
			// if only 'External Database' already checked, insert local as the second one
			var tabLabel = tabs[0].get('label');
			if (tabLabel == '<s:text name="config.radiusOnHiveAp.external.db" />') {
				tabIndex = 1;
			}
		}
		var tabContent = document.getElementById("tab2");
		dbTypeTabs.addTab( new YAHOO.widget.Tab({
		    label: '<s:text name="config.radiusOnHiveAp.local" />',
		    contentEl: tabContent
		}), tabIndex);
	</s:if>
	<s:if test="%{fullMode}">
		<s:if test="%{externalDb && (active || open || openDirect) || dataSource.librarySipCheck}">
		// add tab 'External Database' and 'Library SIP Server'
				<s:if test="%{externalDb && (active || open || openDirect)}">
				// add tab 'External Database', always show at the first place
					var labelStr = '<s:text name="config.radiusOnHiveAp.external.db" />';
					var treeType;
					if (<s:property value="%{active}"/>) {
						//labelStr = '<s:text name="config.radiusOnHiveAp.active" />';
						treeType = ACTIVE_TREE;
					} else if (<s:property value="%{open}"/>) {
						//labelStr = '<s:text name="config.radiusOnHiveAp.open" />';
						treeType = OPEN_TREE;
					} else if (<s:property value="%{openDirect}"/>) {
						//labelStr = '<s:text name="config.radiusOnHiveAp.open.directory" />';
						treeType = OPENDIR_TREE;
					}
					var tabContent = document.getElementById("tab1");
					dbTypeTabs.addTab( new YAHOO.widget.Tab({
					    label: labelStr,
					    contentEl: tabContent
					}), 0);
				</s:if>
				<s:if test="%{dataSource.librarySipCheck}">
				// add tab 'Library SIP Server'
					var tabIndex = 0; // if no other tabs are checked, insert Library as first item
					var tabs = dbTypeTabs.get('tabs');
					if (tabs.length == 2) {
						// if 'External Database' and 'Local Database' already checked, insert Library as last one
						tabIndex = 2;
					} else if (tabs.length == 1) {
						// if 'External Database' or 'Local Database' already checked, insert Library as last one
						tabIndex = 1;
					}
					var tabContent = document.getElementById("tab3");
					dbTypeTabs.addTab( new YAHOO.widget.Tab({
					    label: '<s:text name="config.radiusOnHiveAp.radius.library.server" />',
					    contentEl: tabContent
					}), tabIndex);
				</s:if>
		</s:if>
    	<s:if test="%{jsonMode}">
    	if(top.isIFrameDialogOpen()) {
        	top.changeIFrameDialog(910, 750);
    	}
		</s:if>
	</s:if>
	var tabLen = dbTypeTabs.get('tabs').length;
	//debug("tabLen=" + tabLen + " tabId=" + tabId);
	if (tabLen >= tabId) {
		dbTypeTabs.set('activeIndex', tabId);
	} else {
		dbTypeTabs.set('activeIndex', 0);
	}
	<s:if test="%{fullMode && externalDb && (active || open || openDirect)}">
	// must do after set active tab, otherwise IE will show set focus error
	initLdapTree(treeType);
	// add autocomplete UI
	initAutoCompleteRadiusComboBox();
	</s:if>
}

/**
 * special char escape
 *
 * optType: 1, escape special. 											e.g. # -> &#35;, \+ -> &#43;
 *			2, convert html code back to char for submit to server. 	e.g. &#35; -> #, &#43; -> \\+
 *			3, convert html code back to char for UI show. 				e.g. &#35; -> #, &#43; -> +
 *
 */
function replaceSpecialChar(str, optType) {
	//debug('before replace, str: [' + str + ']');
	if (optType == 1) {// currently this branch not used
		str = str.replace(/#/g, "&#35;");// must put as the first one
		str = str.replace(/\\\\/g, "&#92;"); // must do before replace " to &#034
		str = str.replace(/\\"/g, "&#34;");
		//str = str.replace(/\\/g, "&#92;"); // must do before replace " to &#034
		//str = str.replace(/\"/g, "&#34;");
		str = str.replace(/\'/g, "&#39;");
		str = str.replace(/</g, "&#60;");
		str = str.replace(/>/g, "&#62;");
		str = str.replace(/\+/g, "&#43;");
		//str = str.replace(/=/g, "&#61;");
		//str = str.replace(/,/g, "&#44;");
	} else if (optType == 2) {
		str = str.replace(/\&#92;/g, "\\\\");
		str = str.replace(/\&#34;/g, "\\\"");
		str = str.replace(/\&#39;/g, "'");
		str = str.replace(/\&#60;/g, "\\\<");
		str = str.replace(/\&#62;/g, "\\\>");
		str = str.replace(/\&#43;/g, "\\\+");
		str = str.replace(/\&#59;/g, "\\\;");
		str = str.replace(/\&#61;/g, "\\=");
		str = str.replace(/\&#44;/g, ",");
		str = str.replace(/\&#35;/g, "\\#"); // must put as the last one
	} else if (optType == 3) {
		str = str.replace(/\&#92;/g, "\\");
		str = str.replace(/\&#34;/g, "\"");
		str = str.replace(/\&#39;/g, "'");
		str = str.replace(/\&#60;/g, "<");
		str = str.replace(/\&#62;/g, ">");
		str = str.replace(/\&#43;/g, "+");
		str = str.replace(/\&#59;/g, ";");
		str = str.replace(/\&#61;/g, "=");
		str = str.replace(/\&#44;/g, ",");
		str = str.replace(/\&#35;/g, "#"); // must put as the last one
		
	}
	//debug('              after: [' + str + ']');
	return str;
}

var treeDir
var treeLdap
var treeOpenDir
var resetDirNodes = '', resetLdapNodes = '', resetOpenDirNodes = ''
var resetDirFlag = false, resetLdapFlag = false, resetOpenDirFlag = false
function initLdapTree(treeType) {
	var expandedNodes='', tree;
	if (treeType == ACTIVE_TREE) {
		showTree(true, false, false);
		if(treeDir != null) { setNodeFocus(treeDir); showNodeRefreshButton(true); return };
		if(resetDirFlag) {
			expandedNodes = resetDirNodes;
		} else {
			expandedNodes = '<s:property value="expandedDirNodes"/>';
		}
		if(expandedNodes == '') {showNodeRefreshButton(false); return};
		tree = new YAHOO.widget.TreeView("treeDivDir");
		treeDir = tree;
	} else if (treeType == OPEN_TREE) {
		showTree(false, true, false);
		if(treeLdap != null) { setNodeFocus(treeLdap); showNodeRefreshButton(true); return };
		if(resetLdapFlag) {
			expandedNodes = resetLdapNodes;
		} else {
			expandedNodes = '<s:property value="expandedLdapNodes"/>';
		}
		if(expandedNodes == '') {showNodeRefreshButton(false); return};
		tree = new YAHOO.widget.TreeView("treeDivLdap");
		treeLdap = tree;
	} else if (treeType == OPENDIR_TREE) {
		showTree(false, false, true);
		if(treeOpenDir != null) { setNodeFocus(treeOpenDir); showNodeRefreshButton(true); return };
		if(resetOpenDirFlag) {
			expandedNodes = resetOpenDirNodes;
		} else {
			expandedNodes = '<s:property value="expandedOpenDirNodes"/>';
		}
		if(expandedNodes == '') {showNodeRefreshButton(false); return};
		tree = new YAHOO.widget.TreeView("treeDivOpenDir");
		treeOpenDir = tree
	}
	if (expandedNodes != '') {
		expandedNodes = '[' + expandedNodes + ']'
	}
	//alert(expandedNodes.replace(/\&quot;/g,'"').replace(/\&amp;/g,'&'));
	
	initTree(tree, expandedNodes, treeType);
	showNodeRefreshButton(true); 
}

function showNodeRefreshButton(isShow) {
	//document.getElementById("nodeRefresh").style.display = isShow?"":"none";
}

function showTree(dir, ldap, openDir) {
	document.getElementById("treeDivDir").style.display = dir?"":"none";
	document.getElementById("treeDivLdap").style.display = ldap?"":"none";
	document.getElementById("treeDivOpenDir").style.display = openDir?"":"none";
}

function initTree(tree, expandedNodes, treeType) {
    //turn dynamic loading on for entire tree:
    var iconMode = 1;
    tree.setDynamicLoad(loadNodeData, iconMode);
	//add labelClick listener for tree
	tree.subscribe("labelClick", onLabelClick);
	tree.subscribe("expand", expandClick);
	tree.subscribe("collapse", collapseClick);
	
	var nodeList = eval(expandedNodes.replace(/\&quot;/g,'"').replace(/\&amp;/g,'&'));
	buildTree(tree.getRoot(), nodeList, true, treeType);
	tree.draw();
	
	// set default focus (previously selected node)
	setNodeFocus(tree);
}

function setNodeFocus(tree) {
	<s:if test="%{actionErrors.size > 0 || actionMessages.size > 0}">
		return;
	</s:if>
	if (tree == null) {
		var serverType = getServerType();
		if (serverType == ACTIVE_TREE) {
			tree = treeDir;
		} else if (serverType == OPEN_TREE) {
			tree = treeLdap;
		} else {
			tree = treeOpenDir;
		}
	}
	var dn = document.getElementById(formName + "_previousSelectedNodeDn").value;
	var node = tree.getNodeByProperty('dn', dn);
	if (node != null) {
		node.focus();
	}
}

function buildTree(parentNode, nodeList, isInit, treeType){
	var oData = null;
	for(i=0; i<nodeList.length; i++){
		oData = nodeList[i];
		if(oData.label == undefined || oData.label == '' ){ continue; }
		oData.label = replaceSpecialChar(oData.label, 3);
		//oData.dn = replaceSpecialChar(oData.dn, 2);
		//debug('buildTree==> after replace, oData.label = [' + oData.label + '], oData.dn = [' + oData.dn + ']');
		if(isInit) {
			if(oData.parentId != -1){
				eval("var node" + oData.nodeId + "= new YAHOO.widget.TextNode(oData, eval(node" + oData.parentId  + "), false);");
			}else{
				eval("var node" + oData.nodeId + "= new YAHOO.widget.TextNode(oData, parentNode, false);");
				var tmpNode = eval("node" + oData.nodeId);
				tmpNode.labelStyle = "servericon";
				//tmpNode.hasIcon = false;
			}
		} else {
			var node = new YAHOO.widget.TextNode(oData, parentNode, false);
		}
	}
	if(isInit) {
		initExpandtree(treeType);
	}
}

function initExpandtree(treeType){
	if (treeType == ACTIVE_TREE) {
		<s:iterator id="expandDn" value="%{dataSource.expandDnsDir}" status="status">
			expand(treeDir, '<s:property value="expandDn" />');
		</s:iterator>
	} else if (treeType == OPEN_TREE) {
		<s:iterator id="expandDn" value="%{dataSource.expandDnsLdap}" status="status">
			expand(treeLdap, '<s:property value="expandDn" />');
		</s:iterator>
	} else if (treeType == OPENDIR_TREE) {
		<s:iterator id="expandDn" value="%{dataSource.expandDnsOpenDir}" status="status">
			expand(treeOpenDir, '<s:property value="expandDn" />');
		</s:iterator>
	}
}

function expand(tree, dn) {
	var  expandParent = function (node) {
		var parent = node.parent;
	    if (parent) {
	    	if(!parent.expanded) {
	        	expandParent(parent);
	        }
	        parent.dynamicLoadComplete = true;
	        parent.expand();
	    } 
	};
	var node = tree.getNodeByProperty('dn', dn);
	if (node != null) {
    	/*if(!node.parent.expanded) {
			expandParent(node);
    	}*/
        //node.dynamicLoadComplete = true;
		node.expand();
	}
}

var resetTreeSuccess = function(o){
	eval("var ret = " + o.responseText);
	//alert('ret.expandedNodes=' + ret.expandedNodes);
	var serverType = o.argument.serverType;
	if (serverType == ACTIVE_TREE) {
		resetDirNodes = ret.expandedNodes;
		resetDirFlag = true;
		treeDir = null;
	} else if (serverType == OPEN_TREE) {
		resetLdapNodes = ret.expandedNodes;
		resetLdapFlag = true;
		treeLdap = null;
	} else {
		resetOpenDirNodes = ret.expandedNodes;
		resetOpenDirFlag = true;
		treeOpenDir = null;
	}
	removeDnHidden('', serverType, true);
	initLdapTree(serverType);
}

function resetTree(domainId) {
	var serverType = getServerType()
    var call =
    {
		success: resetTreeSuccess,
		failure: handleFailure,
		argument: {"serverType": serverType}
    };
	var sUrl = '<s:url action="radiusOnHiveAp" includeParams='none' />' + '?operation=resetTree&selectedDomainId=' + domainId + '&serverType=' + serverType +'&ignore=' + new Date().getTime();
	//AJAX GET
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, call);
}

function reInitTree(node) {
	// if current node is not server node, return
	if(!node.parent.isRoot()) return;
	node.tree.destroy;
	var serverType = getServerType();
	if (serverType == ACTIVE_TREE) {
		treeDir = null;
	} else if (serverType == OPEN_TREE) {
		treeLdap = null;
	} else {
		treeOpenDir = null;
	}
	initLdapTree(serverType);
}

function addDnHidden(dn, serverType) {
	var div = document.getElementById("expandDnsDiv");
	var hidden = document.createElement("input");
	hidden.setAttribute("type", "hidden");
	var dnName;
	if (serverType == ACTIVE_TREE) {
		dnName = "dataSource.expandDnsDir";
	} else if (serverType == OPEN_TREE) {
		dnName = "dataSource.expandDnsLdap";
	} else {
		dnName = "dataSource.expandDnsOpenDir";
	}
	hidden.setAttribute("name", dnName);
	hidden.setAttribute("value", dn);
	div.appendChild(hidden);
}

function removeDnHidden(dn, serverType, removeAll) {
	var div = document.getElementById("expandDnsDiv");
	var dnName;
	if (serverType == ACTIVE_TREE) {
		dnName = "dataSource.expandDnsDir";
	} else if (serverType == OPEN_TREE) {
		dnName = "dataSource.expandDnsLdap";
	} else {
		dnName = "dataSource.expandDnsOpenDir";
	}
	var hiddenDns = document.getElementsByName(dnName);
	if (hiddenDns != null && hiddenDns.length > 0) {
		for ( i = 0; i < hiddenDns.length; i++) {
			if (removeAll) {
				div.removeChild(hiddenDns[i]);
			} else {
				if (hiddenDns[i].value == dn) {
					div.removeChild(hiddenDns[i]);
				}
			}
		}
	}
}

function getServerType() {
	var serverType;
	if (document.getElementById(formName + "_dataSource_externalDbType2").checked) {
		serverType = ACTIVE_TREE;
	} else if (document.getElementById(formName + "_dataSource_externalDbType3").checked) {
		serverType = OPEN_TREE;
	} else {
		serverType = OPENDIR_TREE;
	}
	return serverType;
}

function getServerTree() {
	var tree;
	if (document.getElementById(formName + "_dataSource_externalDbType2").checked) {
		tree = treeDir;
	} else if (document.getElementById(formName + "_dataSource_externalDbType3").checked) {
		tree = treeLdap;
	} else {
		tree = treeOpenDir;
	}
	return tree;
}

function getSelectedDomainId() {
	var domainId;
	if (document.getElementById(formName + "_dataSource_externalDbType2").checked) {
		domainId = document.getElementById('domainForTreeDir').value;
	} else if (document.getElementById(formName + "_dataSource_externalDbType3").checked) {
		domainId = document.getElementById('domainForTreeLdap').value;
	} else {
		domainId = document.getElementById('domainForTreeOpenDir').value;
	}
	return domainId;
}

function expandClick(node){
 	if(node.dynamicLoadComplete) {
 		var serverType = getServerType()
		addDnHidden(node.data.dn, serverType);
 	}
}

function collapseClick(node){
 	if(node.dynamicLoadComplete) {
 		var serverType = getServerType()
		removeDnHidden(node.data.dn, serverType);
 	}
}

var loadNodeSuccess = function(o){
	waitingPanel.hide();
	eval("var nodeList = " + o.responseText);
	var serverType = getServerType();
	var thisNode = o.argument.node;
	if(nodeList[0].resCode == 0) {
		buildTree(thisNode, nodeList, false);
		addDnHidden(thisNode.data.dn, serverType);
		o.argument.fnLoadComplete();
	} else {
		showFoldingContext(FOLDER_DATABASE);
		dbTypeTabs.set('activeIndex', 0);
		if(nodeList[0].resCode == -2) {
			// OU expanding has more than 1000 items, show note.
			hm.util.reportFieldError(document.getElementById('forNoteText'), nodeList[0].msg)
		} else {
   			hm.util.reportFieldError(document.getElementById('groupTable'), nodeList[0].msg)
		}
		o.argument.fnLoadComplete();
		
		// once failed in loading children, dynamic load event for current node cann't be fired again
		// so we need to reinitial the node.
		//reInitTree(o.argument.node);
   		thisNode.tree.removeChildren(thisNode);
	}
}

var handleLoadFailure = function(o) {
	waitingPanel.hide();
	o.argument.fnLoadComplete();  
	alert("failed in getting child nodes.");
	var thisNode = o.argument.node;
	//reInitTree(thisNode);
	thisNode.tree.removeChildren(thisNode);
}

function loadNodeData(node, fnLoadComplete){
	/*var selectedAp = validateListSelection("apServer");
	if(selectedAp < 0){
		return;
	}*/
	var selectedAp = "";
	if (!document.getElementById(formName + "_dataSource_externalDbType2").checked) {
		var el = document.getElementById("apMacHidden");
		if (el.value == "" || el.value == -1) {
			hm.util.reportFieldError(document.getElementById("apServerDiv"), '<s:text name="info.config.select.hiveAP.radius.server" />');
			reInitTree(node);
			return;
		} else {
			selectedAp = el.value;
		}
	}
    var callback =
	{
		success: loadNodeSuccess,
		failure: handleLoadFailure,
		argument: {"node": node,
					"fnLoadComplete": fnLoadComplete}
		        
		//timeout -- if more than 7 seconds go by, we'll abort
		//the transaction and assume there are no children:
		//timeout: 7000
	};
	var serverType = getServerType();
	var selectedDomainId = getSelectedDomainId();
	var dn = node.data.dn;
	//debug("expand node:" + dn);
	var sUrl = '<s:url action="radiusOnHiveAp" includeParams='none' />' + '?operation=loadNodeData&serverId=' + node.data.serverId + '&dn=' + encodeURIComponent(dn) + '&serverType=' + serverType + '&selectedDomainId=' + selectedDomainId + '&selectedAp=' + selectedAp + '&ignore=' + new Date().getTime();
	//AJAX GET
	waitingPanel.show();
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, callback);
}
/*
function refreshNodeSuccess(o) {
	eval("var result = " + o.responseText);
	var tree = getServerTree();
	if (tree != null) {
		var node = tree.getNodeByProperty('dn', o.argument.dn);
		if(result.hasNode != null && result.hasNode != '') {
			if (node != null) {
				tree.removeChildren(node);
				if (result.removeNodeSelf) {
					var nodeForFocus = node.previousSibling;
					if (nodeForFocus == null) {
						nodeForFocus = node.nextSibling;
					}
					if (nodeForFocus == null) {
						nodeForFocus = node.parent;
					}
					tree.removeNode(node, true);
					if (nodeForFocus != null) {
						nodeForFocus.focus();
					}
				} else {
					node.focus();
				}
			}
		} else {
			showFoldingContext(FOLDER_DATABASE);
			dbTypeTabs.set('activeIndex', 0);
	   		hm.util.reportFieldError(document.getElementById('groupTable'), result.msg)
			node.focus();
		}
	}
}

function refreshNode() {
	var selectedAp = "";
	var el = document.getElementById("apMacHidden");
	if (el.value == "" || el.value == -1) {
		hm.util.reportFieldError(document.getElementById("apServerDiv"), '<s:text name="info.config.select.hiveAP.radius.server" />');
		return;
	} else {
		selectedAp = el.value;
	}
	var serverId = document.forms[formName].lastNodeServerId.value;
	if (serverId == null || serverId == "") {
		showFoldingContext(FOLDER_DATABASE);
		dbTypeTabs.set('activeIndex', 0);
		hm.util.reportFieldError(document.getElementById('groupTable'), '<s:text name="info.config.no.selected.treeNode"></s:text>');
    	setNodeFocus();
       	return false;
	}
	var dn = document.forms[formName].lastNodeDn.value;
	var serverType = getServerType()
    var callback =
	{
		success: refreshNodeSuccess,
		failure: handleFailure,
		argument: {"dn" : dn}
	};
	var selectedDomainId = getSelectedDomainId();
	var dn = replaceSpecialChar(dn, 2);
	var sUrl = '<s:url action="radiusOnHiveAp" includeParams='none' />' + '?operation=refreshNode&serverId=' + serverId + '&dn=' + dn + '&serverType=' + serverType + '&selectedDomainId=' + selectedDomainId + '&selectedAp=' + selectedAp + '&ignore=' + new Date().getTime();
    //AJAX POST
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, callback);
}*/

function onLabelClick(node){
	document.forms[formName].lastNodeServerId.value = node.data.serverId;
	document.forms[formName].lastNodeDn.value = node.data.dn;
	document.forms[formName].previousSelectedNodeDn.value = node.data.dn;
}

function submitAddRoleMap() {
	//var globalCatalogChk = document.getElementById(formName + "_dataSource_globalCatalog").checked;
	var userGroupForGlobalCatalog = document.getElementById(formName + "_dataSource_userGroupForGlobalCatalog").value;
	if (/*globalCatalogChk &&*/ userGroupForGlobalCatalog != "") {
		// if is global catalog and user group for it is inputed
		submitAction('addRoleMap');
	} else {
		getNodeAttribute(/*globalCatalogChk*/);
	}
}

var getAttrSuccess = function(o){
	eval("var nodeAttr = " + o.responseText);
	document.forms[formName].lastNodeAttrValue.value = nodeAttr.groupAttributeValue;
	document.forms[formName].lastNodeObjectClassOfGroup.value = nodeAttr.objectClassOfGroup;
	beforeSubmitAction(document.forms[formName]);
	submitAction('addRoleMap');
}

function getNodeAttribute(/*globalCatalogChk*/){
	/*var selectedAp = validateListSelection("apServer");
	if(selectedAp < 0){
		return;
	}*/
	var selectedAp = "";
	if (!document.getElementById(formName + "_dataSource_externalDbType2").checked) {
		var el = document.getElementById("apMacHidden");
		if (el.value == "" || el.value == -1) {
			hm.util.reportFieldError(document.getElementById("apServerDiv"), '<s:text name="info.config.select.hiveAP.radius.server" />');
			return;
		} else {
			selectedAp = el.value;
		}
	}
	var groupAttribute = document.getElementById(formName + "_dataSource_groupAttribute");
	if(!checkNameValidate(groupAttribute, '<s:text name="config.radiusOnHiveAp.database.group" />', FOLDER_DATABASE, groupAttribute, 0)) {
		return false;
	}
	var treeDiv = document.getElementById('addRoleMapArea');
	var addRoleBtn = document.getElementById('addRoleBtn');
	if (treeDir == null && treeLdap == null && treeOpenDir == null) {
		showFoldingContext(FOLDER_DATABASE);
		dbTypeTabs.set('activeIndex', 0);
		var msg;
		//if (globalCatalogChk) { //fix bug 17026
		if (document.getElementById(formName + "_dataSource_externalDbType2").checked) {
			msg = '<s:text name="info.config.no.serverTree.AndNoUserGroupForGlobalCatalog"></s:text>';
		} else {
			msg = '<s:text name="info.config.no.serverTree"></s:text>'
		}
		hm.util.reportFieldError(treeDiv, msg);
    	//setNodeFocus();
    	addRoleBtn.focus();
       	return false;
	}
	var serverId = document.forms[formName].lastNodeServerId.value;
	if (serverId == null || serverId == "") {
		showFoldingContext(FOLDER_DATABASE);
		dbTypeTabs.set('activeIndex', 0);
		var msg;
		//if (globalCatalogChk) { //fix bug 17026
		if (document.getElementById(formName + "_dataSource_externalDbType2").checked) {
			msg = '<s:text name="info.config.no.selected.treeNode.AndNoUserGroupForGlobalCatalog"></s:text>';
		} else {
			msg = '<s:text name="info.config.no.selected.treeNode"></s:text>'
		}
		hm.util.reportFieldError(treeDiv, msg);
    	//setNodeFocus();
    	addRoleBtn.focus();
       	return false;
	}
	var dn = document.forms[formName].lastNodeDn.value;
    var call =
    {
		success: getAttrSuccess,
		failure: handleFailure
    };
	var serverType = getServerType();
	var selectedDomainId = getSelectedDomainId();
	//debug("get node attribute:" + dn);
	var sUrl = '<s:url action="radiusOnHiveAp" includeParams='none' />' + '?operation=getNodeAttr&serverId=' + serverId + '&dn=' + encodeURIComponent(dn) + '&groupAttributeName=' + groupAttribute.value + '&serverType=' + serverType + '&selectedDomainId=' + selectedDomainId + '&selectedAp=' + selectedAp + '&ignore=' + new Date().getTime();
    //AJAX GET
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, call);
}

var handleFailure = function(o) {
	alert("failed.");
}

var getAttrIdSuccess = function(o){
	eval("var attrId = " + o.responseText);
	o.argument.attributeId.value = attrId.attributeId;
}

function queryAttributeId(userProfileId, attributeId) {
    var call =
    {
		success: getAttrIdSuccess,
		failure: handleFailure,
		argument: {"attributeId": attributeId}
    };
	var sUrl = '<s:url action="radiusOnHiveAp" includeParams='none' />' + '?operation=getUserProfileAttr&userProfileId=' + userProfileId + '&ignore=' + new Date().getTime();
	//AJAX GET
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, call);
}

function changeAttributeIdDir(index) {
	var userProfile = document.getElementById("userProfileDir_" + index);
	var attributeId = document.getElementById("attIdsDir_" + index);
	queryAttributeId(userProfile.value, attributeId);
}

function changeAttributeIdLdap(index) {
	var userProfile = document.getElementById("userProfileLdap_" + index);
	var attributeId = document.getElementById("attIdsLdap_" + index);
	queryAttributeId(userProfile.value, attributeId);
}

function changeAttributeIdOpenDir(index) {
	var userProfile = document.getElementById("userProfileOpenDir_" + index);
	var attributeId = document.getElementById("attIdsOpenDir_" + index);
	queryAttributeId(userProfile.value, attributeId);
}

function validateListSelection(elId){
	var el = document.getElementById(elId);
	if (el.options.length == 0) {//blank list
		hm.util.reportFieldError(el, '<s:text name="info.config.no.hiveAP.radius.server" />');
		return -1;
	} else {
		var index = el.selectedIndex;
		if (index < 0) {//no selection
			hm.util.reportFieldError(el, '<s:text name="info.config.select.hiveAP.radius.server" />');
			return -1;
		}
		var value = el.options[index].value;
		if (value == undefined || value <= 0) {//invalid selection
			hm.util.reportFieldError(el, '<s:text name="info.config.select.valid.hiveAP.radius.server" />');
			return -1;
		}
		return value;
	}
}

var tab1Element;
var tab2Element;
var tab3Element;
function operatorTab1(labelStr, checked) {
	// add tab
	if (checked) {
		document.getElementById("dbTypeTabs").style.display = "";
		var createFlag = true;
		if (null == dbTypeTabs) {
			dbTypeTabs = new YAHOO.widget.TabView("dbTypeTabs");
		} else {
			var tabs = dbTypeTabs.get('tabs');
			if (tabs.length > 0) {
				var tabLabel = tabs[0].get('label');
				/*if (tabLabel == '<s:text name="config.radiusOnHiveAp.active" />'
					|| tabLabel == '<s:text name="config.radiusOnHiveAp.open" />'
					|| tabLabel == '<s:text name="config.radiusOnHiveAp.open.directory" />') {*/
				if (tabLabel == '<s:text name="config.radiusOnHiveAp.external.db" />') {
					createFlag = false;
					tabs[0].set('label', labelStr);
					dbTypeTabs.set('activeIndex', 0);
				}
			}
		}
		if (createFlag) {
			if (null != tab1Element) {
				tab1Element.set('label', labelStr);
				dbTypeTabs.addTab(tab1Element, 0);
			} else {
				var tabContent = document.getElementById("tab1");
				dbTypeTabs.addTab( new YAHOO.widget.Tab({
				    label: labelStr,
				    contentEl: tabContent
				}), 0);
			}
			dbTypeTabs.set('activeIndex', 0);
		}
	// remove tab
	} else {
		tab1Element = dbTypeTabs.getTab(0);
		dbTypeTabs.removeTab(tab1Element);
		if (dbTypeTabs.get('tabs').length == 0) {
			document.getElementById("dbTypeTabs").style.display = "none";
		}
	}
}

function operatorTab2(checked) {
	var tabIndex = 0;
	if (null == dbTypeTabs) {
		dbTypeTabs = new YAHOO.widget.TabView("dbTypeTabs");
	}

	// add tab
	if (checked) {
		/*if (dbTypeTabs.get('tabs').length > 0) {
			tabIndex = 1;
		}*/
		var tabs = dbTypeTabs.get('tabs');
		if (tabs.length == 2) {
			tabIndex = 1;
		} else if (tabs.length == 1) {
			var tabLabel = tabs[0].get('label');
			if (tabLabel == '<s:text name="config.radiusOnHiveAp.external.db" />') {
				tabIndex = 1;
			}
		}
		document.getElementById("dbTypeTabs").style.display = "";
		
		if (null != tab2Element) {
			dbTypeTabs.addTab(tab2Element, tabIndex);
		} else {
			var tabContent = document.getElementById("tab2");
			dbTypeTabs.addTab( new YAHOO.widget.Tab({
			    label: '<s:text name="config.radiusOnHiveAp.local" />',
			    contentEl: tabContent
			}), tabIndex);
		}
		
		dbTypeTabs.set('activeIndex', tabIndex);
	// remove tab
	} else {
		/*if (dbTypeTabs.get('tabs').length > 1) {
			tabIndex = 1;
		}*/
		var tabs = dbTypeTabs.get('tabs');
		if (tabs.length == 3) {
			tabIndex = 1;
		} else if (tabs.length == 2) {
			var tabLabel = tabs[0].get('label');
			if (tabLabel == '<s:text name="config.radiusOnHiveAp.external.db" />') {
				tabIndex = 1;
			}
		}
		tab2Element = dbTypeTabs.getTab(tabIndex);
		dbTypeTabs.removeTab(tab2Element);
		if (dbTypeTabs.get('tabs').length == 0) {
			document.getElementById("dbTypeTabs").style.display = "none";
		}
	}
}
function operatorTab3(checked) {
	var tabIndex = 0;
	if (null == dbTypeTabs) {
		dbTypeTabs = new YAHOO.widget.TabView("dbTypeTabs");
	}

	// add tab
	if (checked) {
		/*if (dbTypeTabs.get('tabs').length > 0) {
			tabIndex = 1;
		}*/
		var tabs = dbTypeTabs.get('tabs');
		if (tabs.length == 2) {
			tabIndex = 2;
		} else if (tabs.length == 1) {
			tabIndex = 1;
		}
		document.getElementById("dbTypeTabs").style.display = "";
		
		if (null != tab3Element) {
			dbTypeTabs.addTab(tab3Element, tabIndex);
		} else {
			var tabContent = document.getElementById("tab3");
			dbTypeTabs.addTab( new YAHOO.widget.Tab({
			    label: '<s:text name="config.radiusOnHiveAp.radius.library.server" />',
			    contentEl: tabContent
			}), tabIndex);
		}
		
		dbTypeTabs.set('activeIndex', tabIndex);
	// remove tab
	} else {
		/*if (dbTypeTabs.get('tabs').length > 1) {
			tabIndex = 1;
		}*/
		var tabs = dbTypeTabs.get('tabs');
		if (tabs.length == 3) {
			tabIndex = 2;
		} else if (tabs.length == 2) {
			tabIndex = 1;
		}
		tab3Element = dbTypeTabs.getTab(tabIndex);
		dbTypeTabs.removeTab(tab3Element);
		if (dbTypeTabs.get('tabs').length == 0) {
			document.getElementById("dbTypeTabs").style.display = "none";
		}
	}
}

function showFoldingContext(contextId){
	showHideContent(contextId,"");
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
	    document.forms[formName].operation.value = operation;
	    <s:if test="%{fullMode}">
	    //if ((document.getElementById(formName + "_dataSource_externalDbType2").checked || document.getElementById(formName + "_dataSource_externalDbType3").checked || document.getElementById(formName + "_dataSource_externalDbType6").checked) 
	    //	&& document.getElementById(formName + "_dataSource_mapEnable").checked && mapType == TYPE_GROUP) {
		//	hm.options.selectAllOptions('selectLocalGroup');
		//}
		Get(formName + "_dataSource_databaseAccessStyle").value = Get(FOLDER_DATABASE).style.display;
		</s:if>
		if (document.forms[formName].local.checked) {
			hm.options.selectAllOptions('selectLocalUserGroup');
		}
		
		//save style values
		Get(formName + "_dataSource_radiusSettingsStyle").value = Get(FOLDER_RADIUS).style.display;
		if(Get(FOLDER_NAS)) {
		    Get(formName + "_dataSource_nasSettingsStyle").value = Get(FOLDER_NAS).style.display;
		}
	    document.forms[formName].submit();
	}
}

var sipTab;
var localTab;
function validate(operation) {
	if('<%=Navigation.L2_FEATURE_RADIUS_SERVER_HIVEAP%>' == operation || operation == 'cancel<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_serverPort").value = 0;
		<s:if test="%{fullMode}">
		if(document.getElementById(formName + "_dataSource_cacheTime"))
			document.getElementById(formName + "_dataSource_cacheTime").value = 0;
		if(document.getElementById(formName + "_dataSource_localInterval"))
			document.getElementById(formName + "_dataSource_localInterval").value = 0;
		if(document.getElementById(formName + "_dataSource_remoteInterval"))
			document.getElementById(formName + "_dataSource_remoteInterval").value = 0;
		if(document.getElementById(formName + "_dataSource_retryInterval"))
			document.getElementById(formName + "_dataSource_retryInterval").value = 0;
		if(document.getElementById(formName + "_dataSource_sipPort"))
			document.getElementById(formName + "_dataSource_sipPort").value = 0;
		</s:if>
		return true;
	}
	
	if (null != dbTypeTabs) {
		//sipTab = dbTypeTabs.get('tabs').length > 1 ? 1 : 0;

		var tabs = dbTypeTabs.get('tabs');
		if (tabs.length == 3) {
			localTab = 1;
			sipTab = 2;
		} else if (tabs.length == 2) {
			if(document.forms[formName].externalDb.checked) {
				if(document.forms[formName].local.checked) {
					localTab = 1;
					//sipTab = null;
				} else {
					sipTab = 1;
				}
			} else {
				localTab = 0;
				sipTab = 1;
			}
		} else if (tabs.length == 1) {
			if(!document.forms[formName].externalDb.checked) {
				if(document.forms[formName].local.checked) {
					localTab = 0;
					//sipTab = null;
				} else {
					sipTab = 0;
				}
			}
		}
	}
	if(operation == 'newIpAddress' || operation == 'newActiveDirectory' || operation == 'newOpenLdap' || operation == 'newOpenDir'
		|| operation == 'editIpAddress' || operation == 'editActiveDirectory' || operation == 'editOpenLdap' || operation == 'editOpenDir'
		|| operation == 'newFile3' || operation == 'newFile1' || operation == 'newFile2'|| operation == 'newUserGroup' || operation == 'newLocalUserGroup'
		|| operation == 'editUserGroup' || operation == 'editLocalUserGroup'|| operation == 'newLibrarySip' || operation == 'editLibrarySip'
		|| operation == 'newIpAddressSip'|| operation == 'editIpAddressSip' || operation == 'newUserProfile'|| operation == 'editUserProfile') {
		var serverEl = document.getElementById(formName + "_dataSource_serverEnable");
		var port = document.getElementById(formName + "_dataSource_serverPort");
    	if(serverEl.checked && port.value.length > 0 && !checkInputRange(port, '<s:text name="config.radiusOnHiveAp.port" />', 1,65535,FOLDER_RADIUS, null)) {
    		return false;
    	}
    	<s:if test="%{fullMode}">
    	if(document.forms[formName].externalDb.checked) {
    		if(document.getElementById(formName + "_dataSource_cacheEnable").checked) {
    			var time = document.getElementById(formName + "_dataSource_cacheTime");
    			if(time.value.length > 0 && !checkInputRange(time, '<s:text name="config.radiusOnHiveAp.time" />', 3600,2592000,FOLDER_DATABASE, 0)) {
    				return false;
    			}
    		}
    		var retry = document.getElementById(formName + "_dataSource_retryInterval");
   			if(retry.value.length > 0 && !checkInputRange(retry, '<s:text name="config.radiusOnHiveAp.database.ldap" />', 60,200000000,FOLDER_DATABASE, 0)) {
   				return false;
   			}
    		var local = document.getElementById(formName + "_dataSource_localInterval");
   			if(local.value.length > 0 && !checkInputRange(local, '<s:text name="config.radiusOnHiveAp.database.local" />', 30,3600,FOLDER_DATABASE, 0)) {
   				return false;
   			}
    		var remote = document.getElementById(formName + "_dataSource_remoteInterval");
   			if(remote.value.length > 0 && !checkInputRange(remote, '<s:text name="config.radiusOnHiveAp.database.remote" />', 10,3600,FOLDER_DATABASE, 0)) {
   				return false;
   			}
    	}
    	if (document.getElementById(formName + "_dataSource_librarySipCheck").checked) {
			var sipPort = document.getElementById(formName + "_dataSource_sipPort");
   			if(sipPort.value.length > 0 && !checkInputRange(sipPort, '<s:text name="config.radiusOnHiveAp.port" />', 1,65535,FOLDER_DATABASE, sipTab)) {
   				return false;
   			}
		}
    	</s:if>
    	if(operation == "editLocalUserGroup"){
    		var value = hm.util.validateOptionTransferSelection("selectLocalUserGroup");
    		if(value < 0){
    			return false
    		}else{
    			document.forms[formName].localUserGroupId.value = value;
    		}
    	}
    	if(operation == "editActiveDirectory"){
    		var value = hm.util.validateListSelection(formName + "_activeDir");
    		if(value < 0){
    			return false
    		}else{
    			document.forms[formName].activeDir.value = value;
    		}
    	}
    	if(operation == "editOpenLdap"){
    		var value = hm.util.validateListSelection(formName + "_openLdap");
    		if(value < 0){
    			return false
    		}else{
    			document.forms[formName].openLdap.value = value;
    		}
    	}
    	if(operation == "editOpenDir"){
    		var value = hm.util.validateListSelection(formName + "_openDirectory");
    		if(value < 0){
    			return false
    		}else{
    			document.forms[formName].openDirectory.value = value;
    		}
    	}
    	if(operation == "editIpAddress"){
    		var value = hm.util.validateListSelection("myIpSelect");
    		if(value < 0){
    			return false
    		}else{
    			document.forms[formName].ipAddress.value = value;
    		}
    	}
    	if(operation == "editLibrarySip"){
    		var value = hm.util.validateListSelection("librarySipId");
    		if(value < 0){
    			return false
    		}
    	}
    	if(operation == "editIpAddressSip"){
    		var value = hm.util.validateListSelection("sipServerSelect");
    		if(value < 0){
    			return false
    		}else{
    			document.forms[formName].sipServerId.value = value;
    		}
    	}
    	if(operation == "editUserProfile"){
    		var value = hm.util.validateListSelection(formName + "_userProfile");
    		if(value < 0){
    			return false
    		}
    	}
    	<s:if test="%{jsonMode}">
    	if(operation == "newLocalUserGroup" || operation == "editLocalUserGroup"){
    		top.changeIFrameDialog(880, 500);
    	} else if(operation == "newIpAddress" || operation == "editIpAddress" ||
    			operation == "newIpAddressSip" || operation == "editIpAddressSip"){
    		top.changeIFrameDialog(950, 450);
    	}
		</s:if>
		return true;
	}

	var feChild = document.getElementById("checkAll");
	if (operation == 'addIpAddress') {
		var rowcount = document.getElementsByName('descriptions');
		if(rowcount.length == 128) {
			hm.util.reportFieldError(feChild, '<s:text name="error.entryLimit"><s:param><s:text name="config.radiusOnHiveAp.radiusNas" /></s:param><s:param value="128" /></s:text>');
        	//feChild.focus();
        	return false;
		}
		var ipnames = document.getElementById("myIpSelect");
		var ipValue = document.forms[formName].inputIpValue;
		if ("" == ipValue.value) {
	        hm.util.reportFieldError(ipValue, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.radiusOnHiveAp.ipName" /></s:param></s:text>');
	        ipValue.focus();
			return false;
		}
		if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
			if (!hm.util.validateIpAddress(ipValue.value)) {
				var message = hm.util.validateName(ipValue.value, '<s:text name="config.radiusOnHiveAp.ipName" />');
		    	if (message != null) {
		    		hm.util.reportFieldError(ipValue, message);
		        	ipValue.focus();
		        	return false;
		    	}
			}
			document.forms[formName].ipAddress.value = -1;
		} else {
			document.forms[formName].ipAddress.value = ipnames.options[ipnames.selectedIndex].value;
		}		
		
		var password;
		var confirm;
		if (document.getElementById("chkShareKeyDisplay").checked) {
			password = document.getElementById("sharekey");
			confirm = document.getElementById("sharekeyConf");
		} else {
			password = document.getElementById("sharekey_text");
			confirm = document.getElementById("sharekeyConf_text");
		}
		if(!checkPassword(password, confirm, '<s:text name="config.radiusAssign.secret" />', '<s:text name="config.radiusAssign.confirmSecret" />', null, FOLDER_NAS, null)) {
        	return false;
    	}
	}
	if (operation == 'removeIpAddress' || operation == 'removeIpAddressNone') {
		var cbs = document.getElementsByName('ipIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(feChild, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(feChild, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.radiusOnHiveAp.radius.nas" /></s:param></s:text>');
			return false;
		}
	}

	var active = document.getElementById("checkAllActive");
	if (operation == 'addActive') {
		var activePriorities = document.getElementsByName('activePriorities');
		if(activePriorities.length == 4) {
			hm.util.reportFieldError(active, '<s:text name="error.entryLimit"><s:param><s:text name="config.radiusOnHiveAp.active" /></s:param><s:param value="4" /></s:text>');
        	//active.focus();
        	return false;
		}

		var activeDir = document.forms[formName].activeDir;
		if (activeDir.options[0].value == -1) {
            hm.util.reportFieldError(active, '<s:text name="info.emptyList" />');
            activeDir.focus();
			return false;
		}

    	var activePriority = document.forms[formName].activePriority;
    	for(var i = 0; i < activePriorities.length; i ++) {
			if(activePriority.value == activePriorities[i].value) {
				hm.util.reportFieldError(active, '<s:text name="error.sameObjectExists"><s:param><s:text name="config.radiusAssign.priority" /></s:param></s:text>');
				activePriority.focus();
                return false;
			}
		}
	}
	if (operation == 'removeActive' || operation == 'removeActiveNone') {
		var cbs = document.getElementsByName('activeIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(active, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(active, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.radiusOnHiveAp.active" /></s:param></s:text>');
			return false;
		}
	}
	
	var openDir = document.getElementById("checkAllOpenDir");
	if (operation == 'addOpenDir') {
		var openDirPriorities = document.getElementsByName('openDirPriorities');
		if(openDirPriorities.length == 4) {
			dbTypeTabs.set('activeIndex', 0);
			hm.util.reportFieldError(openDir, '<s:text name="error.entryLimit"><s:param><s:text name="config.radiusOnHiveAp.open.directory" /></s:param><s:param value="4" /></s:text>');
        	//active.focus();
        	return false;
		}

		var openDirectory = document.forms[formName].openDirectory;
		if (openDirectory.options[0].value == -1) {
            hm.util.reportFieldError(openDir, '<s:text name="info.emptyList" />');
            openDirectory.focus();
			return false;
		}

    	var openDirPriority = document.forms[formName].openDirPriority;
    	for(var i = 0; i < openDirPriorities.length; i ++) {
			if(openDirPriority.value == openDirPriorities[i].value) {
				hm.util.reportFieldError(openDir, '<s:text name="error.sameObjectExists"><s:param><s:text name="config.radiusAssign.priority" /></s:param></s:text>');
				openDirPriority.focus();
                return false;
			}
		}
	}
	if (operation == 'removeOpenDir' || operation == 'removeOpenDirNone') {
		var cbs = document.getElementsByName('openDirIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(openDir, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(openDir, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.radiusOnHiveAp.open.directory" /></s:param></s:text>');
			return false;
		}
	}

	var ldap = document.getElementById("checkAllLdap");
	if (operation == 'addLdap') {
		var ldapPriorities = document.getElementsByName('ldapPriorities');
		if(ldapPriorities.length == 4) {
			hm.util.reportFieldError(ldap, '<s:text name="error.entryLimit"><s:param><s:text name="config.radiusOnHiveAp.open" /></s:param><s:param value="4" /></s:text>');
        	//ldap.focus();
        	return false;
		}

		var openLdap = document.forms[formName].openLdap;
		if (openLdap.options[0].value == -1) {
            hm.util.reportFieldError(ldap, '<s:text name="info.emptyList" />');
            openLdap.focus();
			return false;
		}

    	var ldapPriority = document.forms[formName].ldapPriority;
    	for(var i = 0; i < ldapPriorities.length; i ++) {
			if(ldapPriority.value == ldapPriorities[i].value) {
				hm.util.reportFieldError(ldap, '<s:text name="error.sameObjectExists"><s:param><s:text name="config.radiusAssign.priority" /></s:param></s:text>');
                ldapPriority.focus();
                return false;
			}
		}
	}
	if (operation == 'removeLdap' || operation == 'removeLdapNone') {
		var cbs = document.getElementsByName('ldapIndices');
		if (cbs.length == 0) {
			hm.util.reportFieldError(ldap, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(ldap, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.radiusOnHiveAp.open" /></s:param></s:text>');
			return false;
		}
	}
	
   	var roleMap;
   	var roleMapChks;
   	if(document.forms[formName].externalDb.checked) {
   		if ((document.getElementById(formName + "_dataSource_externalDbType2").checked)) {
   			roleMapChks = document.getElementsByName('roleMapDirIndices');
   			roleMap = document.getElementById("checkAllRoleMapDir");
   		} else if (document.getElementById(formName + "_dataSource_externalDbType3").checked) {
   			roleMapChks = document.getElementsByName('roleMapLdapIndices');
   			roleMap = document.getElementById("checkAllRoleMapLdap");
   		} else if (document.getElementById(formName + "_dataSource_externalDbType6").checked) {
   			roleMapChks = document.getElementsByName('roleMapOpenDirIndices');
   			roleMap = document.getElementById("checkAllRoleMapOpen");
   		}
   		if (operation == 'addRoleMap') {
   	 		var attrValue = document.forms[formName].lastNodeAttrValue.value;
   	 		var objectClassValue = document.forms[formName].lastNodeObjectClassOfGroup.value;
   	 		//var globalCatalogChk = document.getElementById(formName + "_dataSource_globalCatalog").checked;
   	 		var userGroupForGlobalCatalog = document.getElementById(formName + "_dataSource_userGroupForGlobalCatalog").value;
   	 		
   	 		if ((objectClassValue != null && objectClassValue != "") 
   	 				|| (attrValue != null && attrValue != "")
   					|| (/*globalCatalogChk && --> fix bug 17026*/ userGroupForGlobalCatalog != "")) {
   	 			
   	 		} else {
   	   			showFoldingContext(FOLDER_DATABASE);
   	   			dbTypeTabs.set('activeIndex', 0);
   				var treeDiv = document.getElementById('addRoleMapArea');
   				var groupAttribute = document.getElementById(formName + "_dataSource_groupAttribute").value;
   				var msg;
   				//if (globalCatalogChk) { //fix bug 17026
   				if (document.getElementById(formName + "_dataSource_externalDbType2").checked) {
   					msg = '<s:text name="info.config.nodeAttrNotFound.AndNoUserGroupForGlobalCatalog"><s:param>' + groupAttribute + '</s:param></s:text>';
   				} else {
   					msg = '<s:text name="info.config.nodeAttrNotFound"><s:param>' + groupAttribute + '</s:param></s:text>';	
   				}
   				hm.util.reportFieldError(treeDiv, msg);
   		        //roleMap.focus();
   	    		setNodeFocus();
   	  	        return false;
   	   	    }
   		}
   		if (operation == 'removeRoleMap' || operation == 'removeRoleMapNone') {
   			if (roleMapChks.length == 0) {
   				hm.util.reportFieldError(roleMap, '<s:text name="info.emptyList"></s:text>');
   				return false;
   			}
   			if (!hm.util.hasCheckedBoxes(roleMapChks)) {
   	            hm.util.reportFieldError(roleMap, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.radiusOnHiveAp.database.custom.roleMap" /></s:param></s:text>');
   				return false;
   			}
   		}
   	}

	var name = document.getElementById(formName + "_dataSource_radiusName");
	if ((operation == 'create' + '<s:property value="lstForward"/>' || operation == 'create') &&
		!checkNameValidate(name, '<s:text name="config.radiusOnHiveAp.radiusName" />', null, name, null)) {
        	return false;
    }

	if (operation == 'create'+'<s:property value="lstForward"/>' || operation == 'update'+'<s:property value="lstForward"/>'
			|| operation == 'create' || operation == 'update') {
		<s:if test="%{fullMode}">
			if(!document.forms[formName].local.checked && !document.forms[formName].externalDb.checked && !document.getElementById(formName + "_dataSource_librarySipCheck").checked) {
	    		showFoldingContext(FOLDER_DATABASE);
	    		//hm.util.reportFieldError(document.forms[formName].local, '<s:text name="error.radius.accessType" />');
	    		hm.util.reportFieldError(document.forms[formName].externalDb, '<s:text name="error.radius.accessType" />');
	        	document.forms[formName].externalDb.focus();
	        	return false;
	    	}
		</s:if>
		<s:else>
			if(!document.forms[formName].local.checked) {
	    		hm.util.reportFieldError(document.forms[formName].local, '<s:text name="error.radius.accessType" />');
	        	document.forms[formName].local.focus();
	        	return false;
	    	}
		</s:else>
    	
    	if (localEnabled && document.getElementById('selectLocalUserGroup').length == 0) {
    		showFoldingContext(FOLDER_DATABASE);
    		dbTypeTabs.set('activeIndex', localTab);
    		hm.util.reportFieldError(document.getElementById('userTable'), '<s:text name="error.pleaseSelectItem"><s:param>local user group</s:param></s:text>');
           	return false;
    	}
    	    	
    	var caCertFile = document.getElementById(formName + "_dataSource_caCertFile");
    	var authType = document.getElementById(formName + "_dataSource_authType");
    	if(authType.value != TYPE_LEAP && caCertFile.value == "") {
        	if (caCertFile.value == "") {
        		showFoldingContext(FOLDER_RADIUS);
                hm.util.reportFieldError(caCertFile, '<s:text name="info.emptyList" />');
                caCertFile.focus();
    			return false;
        	}
        	var keyPassword;
    		var confPassword;
    		if (document.getElementById("chkPasswordDisplay").checked) {
    			keyPassword = document.getElementById("keyPassword");
    			confPassword = document.getElementById("confPassword");
    		} else {
    			keyPassword = document.getElementById("keyPassword_text");
    			confPassword = document.getElementById("confPassword_text");
    		}
        	if(keyPassword.valdbTypeTabs.setue.length > 0 || confPassword.value.length > 0) {
        		if(!checkPassword(keyPassword, confPassword, '<s:text name="config.radiusOnHiveAp.keyPass" />', '<s:text name="config.radiusOnHiveAp.passConf" />', null, FOLDER_RADIUS, null)) {
            		return false;
        		}
        	}
    	}

    	var serverEl = document.getElementById(formName + "_dataSource_serverEnable");
		var port = document.getElementById(formName + "_dataSource_serverPort");
    	if(serverEl.checked && !checkIfInput(port, '<s:text name="config.radiusOnHiveAp.port" />', FOLDER_RADIUS, port, null) || !checkInputRange(port, '<s:text name="config.radiusOnHiveAp.port" />', 1,65535, FOLDER_RADIUS, null)) {
    		return false;
    	}

		<s:if test="%{fullMode}">
    	if(document.forms[formName].externalDb.checked) {
    		if(document.getElementById(formName + "_dataSource_cacheEnable").checked) {
    			var time = document.getElementById(formName + "_dataSource_cacheTime");
    			if(!checkIfInput(time, '<s:text name="config.radiusOnHiveAp.time" />', FOLDER_DATABASE, time, 0) || !checkInputRange(time, '<s:text name="config.radiusOnHiveAp.time" />', 3600,2592000, FOLDER_DATABASE, 0)) {
    				return false;
    			}
    		}
    		var retry = document.getElementById(formName + "_dataSource_retryInterval");
   			if(!checkIfInput(retry, '<s:text name="config.radiusOnHiveAp.database.ldap" />', FOLDER_DATABASE, retry, 0) || !checkInputRange(retry, '<s:text name="config.radiusOnHiveAp.database.ldap" />', 60,200000000,FOLDER_DATABASE, 0)) {
   				return false;
   			}
    		var local = document.getElementById(formName + "_dataSource_localInterval");
   			if(!checkIfInput(local, '<s:text name="config.radiusOnHiveAp.database.local" />', FOLDER_DATABASE, local, 0) || !checkInputRange(local, '<s:text name="config.radiusOnHiveAp.database.local" />', 30,3600,FOLDER_DATABASE, 0)) {
   				return false;
   			}
    		var remote = document.getElementById(formName + "_dataSource_remoteInterval");
   			if(!checkIfInput(remote, '<s:text name="config.radiusOnHiveAp.database.remote" />', FOLDER_DATABASE, remote, 0) || !checkInputRange(remote, '<s:text name="config.radiusOnHiveAp.database.remote" />', 10,3600,FOLDER_DATABASE, 0)) {
   				return false;
   			}
    		if(document.getElementById(formName + "_dataSource_mapEnable").checked) {
    			if (mapType == TYPE_GROUP) {
    				var groupAttribute = document.getElementById(formName + "_dataSource_groupAttribute");
		   			if(!checkNameValidate(groupAttribute, '<s:text name="config.radiusOnHiveAp.database.group" />', FOLDER_DATABASE, groupAttribute, 0)) {
		   				return false;
		   			}
		   			if (roleMapChks.length == 0) {
		   				showFoldingContext(FOLDER_DATABASE);
		   				dbTypeTabs.set('activeIndex', 0);
						hm.util.reportFieldError(roleMap, '<s:text name="error.pleaseAddItems"></s:text>');
				       	roleMap.focus();
		   	           	return false;
		   	    	}
		   	    	
		   	    	var localGroup = document.getElementById('selectLocalUserGroup');
		   	    	if (localEnabled && localGroup.length + roleMapChks.length > 512) {
						showFoldingContext(FOLDER_DATABASE);
						dbTypeTabs.set('activeIndex', 0);
				   		hm.util.reportFieldError(document.getElementById('groupTable'), '<s:text name="error.entryLimit"><s:param>local user group</s:param><s:param>512</s:param></s:text>');
				        return false;
		   	    	}
    			} else {
    				var reauthTime = document.getElementById(formName + "_dataSource_reauthTime");
		   			var userProfileId = document.getElementById(formName + "_dataSource_userProfileId");
		   			var vlanId = document.getElementById(formName + "_dataSource_vlanId");
		   			if (reauthTime.value.length == 0 && userProfileId.value.length == 0 && vlanId.value.length == 0) {
		   				showFoldingContext(FOLDER_DATABASE);
		   				dbTypeTabs.set('activeIndex', 0);
		       			hm.util.reportFieldError(userProfileId, '<s:text name="error.requiredField"><s:param><s:text name="config.radiusOnHiveAp.mapName" /></s:param></s:text>');
		       			userProfileId.focus();
		       			return false;
		   			}
		   			if (userProfileId.value.length > 0 && !checkNameValidate(userProfileId, '<s:text name="config.localUserGroup.profileId" />', FOLDER_DATABASE, userProfileId, 0)) {
		   				return false;
		   			}
		   			if (vlanId.value.length > 0 && !checkNameValidate(vlanId, '<s:text name="config.localUserGroup.vlanId" />', FOLDER_DATABASE, vlanId, 0)) {
		   				return false;
		   			}
		   			if (reauthTime.value.length > 0 && !checkNameValidate(reauthTime, '<s:text name="config.localUserGroup.reauthTime" />', FOLDER_DATABASE, reauthTime, 0)) {
		   				return false;
		   			}
    			}
    		}
    		if(document.getElementById(formName + "_dataSource_externalDbType2").checked) {
    			var activePriorities = document.getElementsByName('activePriorities');
    			if(!checkActiveOrLdap(activePriorities, active)) {
		        	return false;
				}
    		}
    		if(document.getElementById(formName + "_dataSource_externalDbType3").checked) {
    			var ldapPriorities = document.getElementsByName('ldapPriorities');
				if(!checkActiveOrLdap(ldapPriorities, ldap)) {
		        	return false;
				}
    		}
    		if(document.getElementById(formName + "_dataSource_externalDbType6").checked) {
    			var ldapPriorities = document.getElementsByName('openDirPriorities');
				if(!checkActiveOrLdap(ldapPriorities, openDir)) {
		        	return false;
				}
    		}
    	}
    	// check library sip values
    	if (document.getElementById(formName + "_dataSource_librarySipCheck").checked) {
    		// primary sip server
			if (!checkLibrarySipServer()) {
				return false;
			}
			
			// server port	
			var sipPort = document.getElementById(formName + "_dataSource_sipPort");
   			if(!checkIfInput(sipPort, '<s:text name="config.radiusOnHiveAp.radius.library.sip.port" />', FOLDER_DATABASE, sipPort, sipTab) || !checkInputRange(sipPort, '<s:text name="config.radiusOnHiveAp.radius.library.sip.port" />', 1,65535,FOLDER_DATABASE, sipTab)) {
   				return false;
   			}
   			
   			if(document.getElementById(formName + "_dataSource_loginEnable").checked){
	   			// login user
	   			var loginUser = document.getElementById(formName + "_dataSource_loginUser");
	   			if(!checkNameValidate(loginUser, '<s:text name="config.radiusOnHiveAp.radius.library.login.name" />', FOLDER_DATABASE, loginUser, sipTab)) {
	   				return false;
	   			}
   			
	   			// login password
	   			var password;
				var confirm;
				if (document.getElementById("chkSipPwdDisplay").checked) {
					password = document.getElementById("sipLoginPwd");
					confirm = document.getElementById("confirmSipPwd");
				} else {
					password = document.getElementById("sipLoginPwd_text");
					confirm = document.getElementById("confirmSipPwd_text");
				}
				if(!checkPassword(password, confirm, '<s:text name="config.radiusOnHiveAp.radius.library.login.pwd" />', '<s:text name="config.radiusOnHiveAp.passConf" />', null, FOLDER_DATABASE, sipTab)) {
		        	return false;
		    	}
   			}
	    	
	    	// sip policy
	    	var librarySip = document.getElementById("librarySipId");
			if (librarySip.value <= 0) {
				showFoldingContext(FOLDER_DATABASE);
				dbTypeTabs.set('activeIndex', sipTab);
	            hm.util.reportFieldError(librarySip, '<s:text name="error.pleaseSelect"><s:param><s:text name="config.radiusOnHiveAp.radius.library.sip.policy" /></s:param></s:text>'); 
	            librarySip.focus(); 
				return false;
			}
	    	
	    	// institution id
   			var institutionId = document.getElementById(formName + "_dataSource_institutionId");
   			if(!checkNameValidate(institutionId, '<s:text name="config.radiusOnHiveAp.radius.library.institution" />', FOLDER_DATABASE, institutionId, sipTab)) {
   				return false;
   			}
   			
   			// separator
   			var separator = document.getElementById(formName + "_dataSource_separator");
   			if(!checkNameValidate(separator, '<s:text name="config.radiusOnHiveAp.radius.library.separator" />', FOLDER_DATABASE, separator, sipTab)) {
   				return false;
   			}
		}
    	</s:if>

		// check nas share key
		var secrets = document.getElementsByName('sharedSecrets');
		if(secrets) {
			for(var i = 0; i < secrets.length-1; i += 2) {
				var activeObj;
				if (secrets[i].style.display == "") {
					activeObj = secrets[i];
				} else {
					activeObj = secrets[i+1];
				}
				if (!checkIfInput(activeObj, '<s:text name="config.radiusAssign.secret" />', FOLDER_NAS, feChild, null)) {
					return false;
				} else {
					var message = hm.util.validatePassword(activeObj.value, '<s:text name="config.radiusAssign.secret" />');
				   	if (message != null) {
				   		showFoldingContext(FOLDER_NAS);
				   		hm.util.reportFieldError(feChild, message);
				       	activeObj.focus();
				       	return false;
				   	}
				}
			}
		}
	}
	return true;
}

function checkLibrarySipServer() {
	var ipnames = document.getElementById("sipServerSelect");
	var ipValue = document.forms[formName].inputSipServer;
	if ("" == ipValue.value) {
		showFoldingContext(FOLDER_DATABASE);
		dbTypeTabs.set('activeIndex', sipTab);
        hm.util.reportFieldError(Get("errorDisplaySip"), '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.radiusOnHiveAp.radius.library.server.title" /></s:param></s:text>');
        ipValue.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(ipnames, ipValue)) {
		if (!hm.util.validateIpAddress(ipValue.value)) {
			var message = hm.util.validateName(ipValue.value, '<s:text name="config.radiusOnHiveAp.radius.library.server.title" />');
	    	if (message != null) {
	    		showFoldingContext(FOLDER_DATABASE);
				dbTypeTabs.set('activeIndex', sipTab);
	    		hm.util.reportFieldError(ipValue, message);
	        	ipValue.focus();
	        	return false;
	    	}
		}
		document.forms[formName].sipServerId.value = -1;
	} else {
		document.forms[formName].sipServerId.value = ipnames.options[ipnames.selectedIndex].value;
	}
	return true;
}

function checkActiveOrLdap(inputElement, table) {
	if(inputElement.length == 0) {
		showFoldingContext(FOLDER_DATABASE);
		dbTypeTabs.set('activeIndex', 0);
		hm.util.reportFieldError(table, '<s:text name="error.pleaseAddItems"></s:text>');
       	table.focus();
       	return false;
	}
	for(var i = 0; i < inputElement.length - 1; i ++) {
   		var bool = false;
   		for(var j = i + 1; j < inputElement.length; j ++) {
			if(inputElement[j].value == inputElement[i].value) {
				showFoldingContext(FOLDER_DATABASE);
				dbTypeTabs.set('activeIndex', 0);
				hm.util.reportFieldError(table, '<s:text name="error.sameObjectExists"><s:param><s:text name="config.radiusAssign.priority" /></s:param></s:text>');
                table.focus();
                bool = true;
                break;
			}
		}
		if(bool) {
			return false;
		}
	}
	return true;
}

function checkIfInput(inputElement, title, contextId, focusInput, tabId)
{
	if (inputElement.value.length == 0) {
		if(contextId != null){
        	showFoldingContext(contextId);
        }
        if (null != tabId) {
        	dbTypeTabs.set('activeIndex', tabId);
        }
        hm.util.reportFieldError(focusInput, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        inputElement.focus();
        return false;
    }
    return true;
}

function checkInputRange(inputElement, title, min, max, contextId, tabId)
{
	var message = hm.util.validateIntegerRange(inputElement.value, title,min,max);
    if (message != null) {
        if(contextId != null){
        	showFoldingContext(contextId);
        }
        if (null != tabId) {
        	dbTypeTabs.set('activeIndex', tabId);
        }
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
    return true;
}

function checkPassword(password, confirm, passtitle, confirmtitle, focusInput, contextId, tabId)
{
	var message = hm.util.validatePassword(password.value, passtitle);
   	if (message != null) {
   		if(contextId != null){
        	showFoldingContext(contextId);
        }
        if (null != tabId) {
        	dbTypeTabs.set('activeIndex', tabId);
        }
   		if (focusInput == null) {
   			hm.util.reportFieldError(password, message);
   		} else {
   			hm.util.reportFieldError(focusInput, message);
   		}
       	password.focus();
       	return false;
   	}
   	var message = hm.util.validatePassword(confirm.value, confirmtitle);
   	if (message != null) {
   		if(contextId != null){
        	showFoldingContext(contextId);
        }
        if (null != tabId) {
        	dbTypeTabs.set('activeIndex', tabId);
        }
   		if (focusInput == null) {
   			hm.util.reportFieldError(confirm, message);
   		} else {
   			hm.util.reportFieldError(focusInput, message);
   		}
       	confirm.focus();
       	return false;
   	}
   	if (focusInput == null) {
   		focusInput = confirm;
   	}
	if (password.value != confirm.value) {
		if(contextId != null){
        	showFoldingContext(contextId);
        }
        if (null != tabId) {
        	dbTypeTabs.set('activeIndex', tabId);
        }
        hm.util.reportFieldError(focusInput, '<s:text name="error.notEqual"><s:param>'+confirmtitle+'</s:param><s:param>'+passtitle+'</s:param></s:text>');
        confirm.focus();
        return false;
    }
    return true;
}

function checkNameValidate(name, title, contextId, errorInput, tabId)
{
	var message = hm.util.validateName(name.value, title);
   	if (message != null) {
        if(contextId != null){
        	showFoldingContext(contextId);
        }
        if (null != tabId) {
        	dbTypeTabs.set('activeIndex', tabId);
        }
   		hm.util.reportFieldError(errorInput, message);
       	name.focus();
       	return false;
   	}
    return true;
}

function enableCache(checked) {
	document.getElementById(formName + "_dataSource_cacheTime").disabled =!checked;
}

function enableMap(checked) {
	var map = document.getElementById("hideMap");
	map.style.display = checked ? "" : "none";
	if(checked) {
		if(document.getElementById(formName + "_dataSource_externalDbType2").checked) {
			setActiveMap();
		} else if(document.getElementById(formName + "_dataSource_externalDbType3").checked) {
			setOpenMap();
		} else {
			setOpenDirMap();
		}
	} else {
		setMapBlank();
	}
}

function showActive(checked) {
	//operatorTab1('<s:text name="config.radiusOnHiveAp.active" />', checked);
	if(checked) {
		/*document.getElementById(formName + "_dataSource_externalDbType3").checked = false;
		document.getElementById(formName + "_dataSource_externalDbType6").checked = false;*/
		document.getElementById("hideOpen").style.display = "none";
		document.getElementById("hideOpenDir").style.display = "none";
		document.getElementById("hideOpenRoleMap").style.display = "none";
		document.getElementById("hideOpenDirRoleMap").style.display = "none";
		document.getElementById("hideDomainsForTreeLdap").style.display = "none";
		document.getElementById("hideDomainsForTreeOpenDir").style.display = "none";
		
		if(document.getElementsByName('activePriorities').length == 0) {
			showCreateSection('newActiveButton', 'createActiveButton', 'createActiveSection');
		}
		var hideAc = document.getElementById("hideActive");
		var hideCa = document.getElementById("hideCache");
		var hideRo = document.getElementById("hideActiveRoleMap");
		var hideDl = document.getElementById("hideDomainsForTreeDir");
		hideCa.style.display = checked ? "" : "none";
		hideAc.style.display = checked ? "" : "none";
		hideRo.style.display = checked ? "" : "none";
		hideDl.style.display = checked ? "" : "none";
		initLdapTree(ACTIVE_TREE);
		// add autocomplete UI
		initAutoCompleteRadiusComboBox();

		if(document.getElementById(formName + "_dataSource_mapEnable").checked) {
			setActiveMap();
			enableGroup(document.getElementById(formName + "_radioGroupOrUsergroup").checked);
		} else {
			setMapBlank();
		}
		
		// note for LDAP tree
		if(document.getElementById(formName + "_dataSource_globalCatalog").checked) {
			controlTipForLdapTree(true);
		} else {
			controlTipForLdapTree(false);
		}
		// hide HiveAP list for ldap tree retrieving
		document.getElementById("hiveApListForCommu").style.display = "none";
	}
}

function showOpenDir(checked) {
	//operatorTab1('<s:text name="config.radiusOnHiveAp.open.directory" />', checked);
	if(checked) {
		/*document.getElementById(formName + "_dataSource_externalDbType2").checked = false;
		document.getElementById(formName + "_dataSource_externalDbType3").checked = false;*/
		document.getElementById("hideActive").style.display = "none";
		document.getElementById("hideOpen").style.display = "none";
		document.getElementById("hideActiveRoleMap").style.display = "none";
		document.getElementById("hideOpenRoleMap").style.display = "none";
		document.getElementById("hideDomainsForTreeDir").style.display = "none";
		document.getElementById("hideDomainsForTreeLdap").style.display = "none";
		
		if(document.getElementsByName('openDirPriorities').length == 0) {
			showCreateSection('newOpenDirButton', 'createOpenDirButton', 'createOpenDirSection');
		}
		var hideAc = document.getElementById("hideOpenDir");
		var hideCa = document.getElementById("hideCache");
		var hideRo = document.getElementById("hideOpenDirRoleMap");
		var hideDl = document.getElementById("hideDomainsForTreeOpenDir");
		hideCa.style.display = checked ? "" : "none";
		hideAc.style.display = checked ? "" : "none";
		hideRo.style.display = checked ? "" : "none";
		hideDl.style.display = checked ? "" : "none";
		initLdapTree(OPENDIR_TREE);
		// add autocomplete UI
		initAutoCompleteRadiusComboBox();

		if(document.getElementById(formName + "_dataSource_mapEnable").checked) {
			setOpenDirMap();
			enableGroup(document.getElementById(formName + "_radioGroupOrUsergroup").checked);
		} else {
			setMapBlank();
		}
		
		// note for LDAP tree
		controlTipForLdapTree(false);
		// show HiveAP list for ldap tree retrieving
		document.getElementById("hiveApListForCommu").style.display = "";
	}
}

function showOpen(checked) {
	//operatorTab1('<s:text name="config.radiusOnHiveAp.open" />', checked);
	if(checked) {
		/*document.getElementById(formName + "_dataSource_externalDbType2").checked = false;
		document.getElementById(formName + "_dataSource_externalDbType6").checked = false;*/
		document.getElementById("hideActive").style.display = "none";
		document.getElementById("hideOpenDir").style.display = "none";
		document.getElementById("hideActiveRoleMap").style.display = "none";
		document.getElementById("hideOpenDirRoleMap").style.display = "none";
		document.getElementById("hideDomainsForTreeDir").style.display = "none";
		document.getElementById("hideDomainsForTreeOpenDir").style.display = "none";
		
		if(document.getElementsByName('ldapPriorities').length == 0) {
			showCreateSection('newLdapButton', 'createLdapButton', 'createLdapSection');
		}
		var hideOp = document.getElementById("hideOpen");
		var hideCa = document.getElementById("hideCache");
		var hideRo = document.getElementById("hideOpenRoleMap");
		var hideDl = document.getElementById("hideDomainsForTreeLdap");
		hideCa.style.display = checked ? "" : "none";
		hideOp.style.display = checked ? "" : "none";
		hideRo.style.display = checked ? "" : "none";
		hideDl.style.display = checked ? "" : "none";
		initLdapTree(OPEN_TREE);
		// add autocomplete UI
		initAutoCompleteRadiusComboBox();

		if(document.getElementById(formName + "_dataSource_mapEnable").checked) {
			setOpenMap();
			enableGroup(document.getElementById(formName + "_radioGroupOrUsergroup").checked);
		} else {
			setMapBlank();
		}
		
		// note for LDAP tree
		controlTipForLdapTree(false);
		// show HiveAP list for ldap tree retrieving
		document.getElementById("hiveApListForCommu").style.display = "";
	}
}

function showExternalDb(checked, externalDbType) {
	operatorTab1('<s:text name="config.radiusOnHiveAp.external.db" />', checked);
	var externalDbTypesEl = document.getElementById("externalDbTypes");
	if (externalDbTypesEl != null) {
		externalDbTypesEl.style.display = checked ? "" : "none";
	}
	if (checked) {
		if (<%=RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE%> == externalDbType) {
			// AD
			showActive(true);
		} else if (<%=RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN%> == externalDbType) {
			// LDAP
			showOpen(true)
		} else if (<%=RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT%> == externalDbType) {
			// OPEN DIR
			showOpenDir(true);
		}
	}
}

//TODO
/*function localDatabaseSelect(checked) {
	document.getElementById("localUserTr").style.display =  checked? "" : "none";
	localEnabled = checked;
}*/

function showLocalDatabase(checked) {
	operatorTab2(checked);
	var localUserTr = document.getElementById("localUserTr");
	if (null != localUserTr) {
		localUserTr.style.display = checked ? "" : "none";
	}
	localEnabled = checked;
}

function showLibrarySip(checked) {
	operatorTab3(checked);
	var sipContent = document.getElementById("sipContent");
	if (null != sipContent) {
		sipContent.style.display = checked ? "" : "none";
	}
}

function useEDirectory(checked) {
	var accPol = document.getElementById(formName + "_dataSource_accPolicy");
	accPol.disabled = !checked;
	if (!checked) {
		accPol.checked = false;
	}
}

function showCreateSection(newButton, createButton, createSection) {
	hm.util.hide(newButton);
	hm.util.show(createButton);
	hm.util.show(createSection);
}

function hideCreateSection(createButton, newButton, createSection) {
	hm.util.hide(createButton);
	hm.util.show(newButton);
	hm.util.hide(createSection);
}

function setActiveMap() {
	document.getElementById(formName + "_dataSource_reauthTime").value ="msRADIUSServiceType";
	document.getElementById(formName + "_dataSource_userProfileId").value ="msRADIUSCallbackNumber";
	document.getElementById(formName + "_dataSource_vlanId").value ="msRASSavedCallbackNumber";
	document.getElementById(formName + "_dataSource_groupAttribute").value ="memberOf";
}

function setOpenMap() {
	document.getElementById(formName + "_dataSource_reauthTime").value ="radiusServiceType";
	document.getElementById(formName + "_dataSource_userProfileId").value ="radiusCallbackNumber";
	document.getElementById(formName + "_dataSource_vlanId").value ="radiusCallbackID";
	document.getElementById(formName + "_dataSource_groupAttribute").value ="radiusGroupName";
}

function setOpenDirMap() {
	document.getElementById(formName + "_dataSource_reauthTime").value ="radiusServiceType";
	document.getElementById(formName + "_dataSource_userProfileId").value ="radiusCallbackNumber";
	document.getElementById(formName + "_dataSource_vlanId").value ="radiusCallbackID";
	document.getElementById(formName + "_dataSource_groupAttribute").value ="apple-group-realname";
}

function setMapBlank() {
	document.getElementById(formName + "_dataSource_reauthTime").value ="";
	document.getElementById(formName + "_dataSource_userProfileId").value ="";
	document.getElementById(formName + "_dataSource_vlanId").value ="";
	document.getElementById(formName + "_dataSource_groupAttribute").value ="";
}

function enableGroup(checked) {
	document.getElementById("hideLocalUserGroup").style.display = checked ? "" : "none";
	document.getElementById("hideMapUserInfor").style.display = checked ? "none" : "";
	mapType = checked ? TYPE_GROUP : TYPE_USER;
}

function showLoginUserAndPwd(checked) {
	document.getElementById("showSiplogin").style.display = checked ? "" : "none";
	if(!checked){
		document.getElementById(formName + "_dataSource_loginUser").value ="";
		document.getElementById("sipLoginPwd").value ="";
		document.getElementById("sipLoginPwd_text").value ="";
		document.getElementById("confirmSipPwd").value ="";
		document.getElementById("confirmSipPwd_text").value ="";
	}
}

function showLoginUserAndPwd(checked) {
	document.getElementById("showSiplogin").style.display = checked ? "" : "none";
	if(!checked){
		document.getElementById(formName + "_dataSource_loginUser").value ="";
		document.getElementById("sipLoginPwd").value ="";
		document.getElementById("sipLoginPwd_text").value ="";
		document.getElementById("confirmSipPwd").value ="";
		document.getElementById("confirmSipPwd_text").value ="";
	}
}

function enableFiles(type) {
	var checked = TYPE_LEAP == type;	
    document.getElementById("tlsCheck").style.display = checked ? "none" : "";
    <s:if test="%{fullMode}">
    var peap = document.getElementById(formName + "_dataSource_peapCheckInDb");
    var ttls = document.getElementById(formName + "_dataSource_ttlsCheckInDb");
    </s:if>
	if (checked) {
		document.getElementById(formName + "_dataSource_caCertFile").value = "";
		document.getElementById(formName + "_dataSource_keyFile").value = "";
		document.getElementById(formName + "_dataSource_serverFile").value = "";
		document.getElementById("keyPassword").value = "";
    	document.getElementById("confPassword").value = "";
    	document.getElementById("keyPassword_text").value = "";
    	document.getElementById("confPassword_text").value = "";
    	document.getElementById(formName + "_dataSource_cnEnable").checked = false;
		document.getElementById(formName + "_dataSource_dbEnable").checked = false;
		<s:if test="%{fullMode}">
		peap.checked = false;
		ttls.checked = false;
		</s:if>
	} else {
		<s:if test="%{fullMode}">
		if (<%=RadiusOnHiveap.RADIUS_AUTH_TYPE_ALL%> == type || <%=RadiusOnHiveap.RADIUS_AUTH_TYPE_PEAP%> == type) {
			peap.disabled = false;
		} else {
			peap.disabled = true;
			peap.checked = false;
		}
		if (<%=RadiusOnHiveap.RADIUS_AUTH_TYPE_ALL%> == type || <%=RadiusOnHiveap.RADIUS_AUTH_TYPE_TTLS%> == type) {
			ttls.disabled = false;
		} else {
			ttls.disabled = true;
			ttls.checked = false;
		}
		</s:if>
	}
	modifyTheAuthDefaultOptions(type);
}

function modifyTheAuthDefaultOptions(type){
	var selectedText = Get(formName + "_dataSource_authType").options[type].text;
	var authTypeDefaultOptions = selectedText.split("\/");
	var authDefaultEle = Get(formName + "_dataSource_authTypeDefault");
	var selectedIndex = -1;
	authDefaultEle.options.length = 0;
	
	for(var i = 0;i < authTypeDefaultOptions.length; i++){
		var item = null;
		if("PEAP" == authTypeDefaultOptions[i]){
			item = new Option(authTypeDefaultOptions[i],<%=RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_PEAP%>);
			selectedIndex = i;
		}else if("TTLS" == authTypeDefaultOptions[i]){
			item = new Option(authTypeDefaultOptions[i],<%=RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_TTLS%>);
		}else if("TLS" == authTypeDefaultOptions[i]){
			item = new Option(authTypeDefaultOptions[i],<%=RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_TLS%>);
		}else if("LEAP" == authTypeDefaultOptions[i]){
			item = new Option(authTypeDefaultOptions[i],<%=RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_LEAP%>);
		}else if("MD5" == authTypeDefaultOptions[i]){
			item = new Option(authTypeDefaultOptions[i],<%=RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_MD5%>);
		}
		authDefaultEle.options.add(item);
	}
	if(selectedIndex != -1 && selectedIndex < authDefaultEle.options.length){
		authDefaultEle.options[selectedIndex].selected = true;
	}
}

function toggleCheckAllRules(cb, index) {
	var cbs = document.getElementsByName(index);
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function changeServerState(checked){
	var el = document.getElementById(formName + "_dataSource_serverPort");
	if(el){el.disabled = !checked}
}

function onGlobalCatalogClick(checked) {
	//controlTipForLdapTree(checked);
}

function controlTipForLdapTree(showWithGc) {
	//fix bug 17026
	/*document.getElementById("userGroupForGlobalCatalog").style.display =  showWithGc? "" : "none";
	document.getElementById("ouTipWithGlobalCatalog").style.display =  showWithGc? "" : "none";
	document.getElementById("ouTipNoGlobalCatalog").style.display =  showWithGc? "none" : "";*/
}

function initAutoCompleteRadiusComboBox() {
	// init DataSource
	var dataStr = "<s:property value='apListString'/>";
	//alert('dataStr=' + dataStr.replace(/\&quot;/g,'"'));
	var dataSource = eval('[' + dataStr.replace(/\&quot;/g,'"') + ']');
	
	var hiveAPDataSource = new YAHOO.util.LocalDataSource(dataSource);
	hiveAPDataSource.responseSchema = {fields :["name" , "id"]};
	
	// AutoComplete BomboBox primary RADIUS server constructor
	var acComboBox = autoCompelteComboBox('apServerName', 'apContainer', 'apComboBox', hiveAPDataSource, dataSource.length, "apMacHidden");
	acComboBox.oAC.resultTypeList = false;
	
	//////// Start: custom event for primary RADIUS server ///////////
	var initHiveApName = "<s:property value='%{dataSource.apHostName}'/>";
	if(initHiveApName.trim() != '') {
		acComboBox.oAC.textboxFocusEvent.subscribe(function(){
				if(acComboBox.oAC.getInputEl().value.trim() == initHiveApName){
					//acComboBox.oAC.getInputEl().focus();
					setTimeout(function() {// For IE
						acComboBox.oAC.sendQuery(initHiveApName);
					}, 0);
				} else {
					//onChangePrimaryHiveAP();
				}
		});
	}
	//////// End: custom event for primary RADIUS server ///////////
}

function save(operation) {
	if (validate(operation)) {
		url = "<s:url action='radiusOnHiveAp' includeParams='none' />" + "?jsonMode=true" 
				+ "&ignore=" + new Date().getTime(); 
		if (operation == 'create') {
			// 
		} else if (operation == 'update') {
			url = url + "&id="+'<s:property value="dataSource.id" />';
		}
		document.forms[formName].operation.value = operation;
		if (document.forms[formName].local.checked) {
			hm.options.selectAllOptions('selectLocalUserGroup');
		}
		YAHOO.util.Connect.setForm(document.forms["radiusOnHiveAp"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSave, failure : failSave, timeout: 60000}, null);
	}
}

var succSave = function(o) {
	try {
		eval("var details = " + o.responseText);
		if (details.resultStatus == false) {
			hm.util.displayJsonErrorNote(details.errMsg);
			return;
		} else {
			var parentSelectEl = parent.document.getElementById(details.parentDomID);
			if(parentSelectEl != null) {
				if(details.newObjId != null && details.newObjId != ''){
					dynamicAddSelect(parentSelectEl, details.newObjName, details.newObjId);
				}
			}
		}
		parent.closeIFrameDialog();
	}catch(e){
		// do nothing now
	}
}

var failSave = function(o) {
	// do nothing now
}

<s:if test="%{!jsonMode}">
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="radiusOnHiveAp" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}
</s:if>

var debug = function(msg) {
    if(window.console && console.debug) {
        if(typeof msg == 'string' || typeof msg == 'number' || typeof msg == 'boolean') {
            console.debug(msg);
        } else {
            console.dir(msg);
        }
    }
};

--></script>
<div id="content"><s:form action="radiusOnHiveAp">
	<s:if test="%{jsonMode == true}">
	<s:hidden name="operation" />
	<s:hidden name="jsonMode" />
	<s:hidden name="parentDomID" />
	<s:hidden name="contentShowType" />
	<div id="titleDiv" style="margin-bottom:15px;" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-HiveAP_AAA_Server_Settings.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
							<td class="dialogPanelTitle">
							     <s:if test="dataSource.server4Router">
							     <s:text name="config.radius.router.dialog.new.title"/>
							     </s:if>
							     <s:else>
							     <s:text name="config.radius.dialog.new.title"/>
							     </s:else>
							</td>
							</s:if>
							<s:else>
							<td class="dialogPanelTitle">
							     <s:if test="dataSource.server4Router">
							     <s:text name="config.radius.router.dialog.edit.title"/>
							     </s:if>
							     <s:else>
							     <s:text name="config.radius.dialog.edit.title"/>
							     </s:else>
							</td>
							</s:else>
							<td style="padding-left: 10px">
								<a href="javascript:void(0);"  onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk" />
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" style="float: right;" onclick="save('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" style="float: right;" onclick="save('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
	</div>
	</s:if>
	<s:hidden name="localUserGroupId" />
	<s:hidden name="dataSource.radiusSettingsStyle"></s:hidden>
	<s:hidden name="dataSource.databaseAccessStyle"></s:hidden>
	<s:hidden name="dataSource.nasSettingsStyle"></s:hidden>
	<s:hidden name="ipAddress" />
	<s:hidden name="sipServerId" />
	<s:hidden name="lastNodeServerId" />
	<s:hidden name="lastNodeDn" />
	<s:hidden name="lastNodeAttrValue" />
	<s:hidden name="lastNodeObjectClassOfGroup" />
	<s:hidden name="previousSelectedNodeDn" value="%{lastNodeDn}" />
	<div id="expandDnsDiv">
		<s:iterator id="expandDn" value="%{dataSource.expandDnsDir}" status="status">
			<s:hidden name="dataSource.expandDnsDir" value="%{expandDn}" />
		</s:iterator>
		<s:iterator id="expandDn" value="%{dataSource.expandDnsLdap}" status="status">
			<s:hidden name="dataSource.expandDnsLdap" value="%{expandDn}" />
		</s:iterator>
		<s:iterator id="expandDn" value="%{dataSource.expandDnsOpenDir}" status="status">
			<s:hidden name="dataSource.expandDnsOpenDir" value="%{expandDn}" />
		</s:iterator>
	</div>
	<s:if test="%{jsonMode == true}">
		<div style="margin:0 auto; width:100%;">
		<s:if test="%{contentShownInDlg == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
		</s:if>
		<s:else>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		</s:else>
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
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button" onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_RADIUS_SERVER_HIVEAP%>');">
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
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<table style="padding: 0 4px 2px;" cellspacing="0" cellpadding="0" border="0" width="790px">
			</s:if>
			<s:else>
				<table class="editBox" style="padding: 0 4px 2px;" cellspacing="0" cellpadding="0" border="0" width="790px">
			</s:else>
				<tr><td height="4px">
					<%-- add this password dummy to fix issue with auto complete function --%>
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
				</td></tr>
				<tr>
					<td><!-- definition -->
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" width="195px"><label><s:text
												name="config.radiusOnHiveAp.radiusName" /><font color="red"><s:text name="*"/></font></label></td>
											<td><s:textfield size="24" name="dataSource.radiusName"
												maxlength="%{radiusNameLength}" disabled="%{disabledName}"
												onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
												name="config.ssid.ssidName_range" /></td>
										</tr>
										<tr>
											<td class="labelT1"><label><s:text
												name="config.radiusOnHiveAp.description" /></label>&nbsp;&nbsp;<a class="marginBtn" 
												href="javascript:showInfoDialog('<s:text name="info.config.hiveap.radius.server.tip" />')">?</a></td>
											<td><s:textfield size="48" name="dataSource.description"
												maxlength="%{commentLength}" />&nbsp;<s:text
												name="config.ssid.description_range" /></td>
										</tr>
									</table>
								</td>
							</tr>
							<!-- HiveAP Candidates TODO
							<tr>
								<td>
									<table cellspacing="0" cellpadding="0" border="0">
									</table>
								</td>
							</tr>
							 -->
							<!-- Database Access -->
							<tr>
								<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusOnHiveAp.radius.daSetting" />','databaseAccess');</script>&nbsp;&nbsp;<a class="marginBtn" 
										href="javascript:showInfoDialog('<s:text name="info.config.hiveap.radius.db.tip" />')">?</a></td>
							</tr>
							<tr>
								<td style="padding-left: 5px;">
									<div id="databaseAccess" style="display: <s:property value="%{dataSource.databaseAccessStyle}"/>">
									<table cellspacing="0" cellpadding="0" border="0" width="750px">
										<tr>
											<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<s:if test="%{fullMode}">
														<td><s:checkbox name="externalDb"
															onclick="showExternalDb(this.checked, %{dataSource.externalDbType});" /></td>
														<td class="labelT1" style="padding-left: 0"><label><s:text
															name="config.radiusOnHiveAp.external.db" /></label></td>
														</s:if>
														<td style="padding:2px 2px 2px 6px"><s:checkbox name="local"
															value="%{local}" onclick="showLocalDatabase(this.checked)" /></td>
														<td class="labelT1" style="padding-left: 0"><label><s:text 
															name="config.radiusOnHiveAp.local" /></label></td>
														<s:if test="%{fullMode}">
														<td><s:checkbox name="dataSource.librarySipCheck"
															onclick="showLibrarySip(this.checked);" /></td>
														<td class="labelT1" style="padding-left: 0"><label><s:text
															name="config.radiusOnHiveAp.radius.library.server" /></label></td>
														</s:if>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td style="padding-left: 5px;">
												<div id="dbTypeTabs" class="yui-navset" style="display:none">           
												</div>
												<s:if test="%{fullMode}">
										        <div id="tab1">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr id="externalDbTypes" style="display:<s:property value="%{externalDb?'':'none'}" />" >
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td style="padding:2px 2px 2px 5px"><s:radio label="Gender" name="dataSource.externalDbType" 
																			value="%{dataSource.externalDbType}" list="%{externalDbType1}" onchange="showExternalDb(true, this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
																		<td style="padding:2px 2px 2px 5px"><s:radio label="Gender" name="dataSource.externalDbType" 
																			value="%{dataSource.externalDbType}" list="%{externalDbType2}" onchange="showExternalDb(true, this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
																		<td style="padding:2px 2px 2px 5px"><s:radio label="Gender" name="dataSource.externalDbType" 
																			value="%{dataSource.externalDbType}" list="%{externalDbType3}" onchange="showExternalDb(true, this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr id="hideActive" style="display:<s:property value="%{active?'':'none'}" />">
															<td style="padding-left: 5px;">
																<table cellspacing="0" cellpadding="0" border="0" class="embedded">
																	<tr>
																		<td colspan="3">
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td style="padding:2px 2px 4px 0;"><s:checkbox name="dataSource.globalCatalog" onclick="onGlobalCatalogClick(this.checked);"/></td>
																					<td class="labelT1" width="200px" style="padding-left: 0"><s:text 
																						name="config.radiusOnHiveAp.active.global.catalog" />&nbsp;&nbsp;<a class="marginBtn" 
																						href="javascript:showInfoDialog('<s:text name="info.config.hiveap.radius.globalcatalog.tip" />')">?</a></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr style="display:<s:property value="%{hideNewActiveButton}"/>" id="newActiveButton">
																		<td colspan="3" style="padding: 2px 0 2px 0;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
																					class="button" onClick="showCreateSection('newActiveButton',
																					'createActiveButton', 'createActiveSection');"></td>
																				<td><input type="button" name="ignore" value="Remove"
																					class="button" <s:property value="writeDisabled" />
																					onClick="submitAction('removeActive');"></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr style="display:<s:property value="%{hideCreateActiveItem}"/>" id="createActiveButton">
																		<td colspan="3" style="padding: 2px 0 2px 0;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
																					class="button" onClick="submitAction('addActive');"></td>
																				<td><input type="button" name="ignore" value="Remove"
																					class="button" <s:property value="writeDisabled" />
																					onClick="submitAction('removeActiveNone');"></td>
																				<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																					class="button" onClick="hideCreateSection('createActiveButton', 'newActiveButton', 'createActiveSection');"></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr id="headerActiveSection">
																		<th align="left" style="padding-left: 0;" width="10px"><input
																			type="checkbox" id="checkAllActive"
																			onClick="toggleCheckAllRules(this,'activeIndices');"></th>
																		<th align="left" width="335px"><s:text
																			name="config.radiusOnHiveAp.active" /></th>
																		<th align="left"><s:text
																			name="config.radiusAssign.priority" /></th>
																	</tr>
																	<tr style="display:<s:property value="%{hideCreateActiveItem}"/>" id="createActiveSection">
																		<td class="listHead">&nbsp;</td>
																		<td class="listHead" valign="top">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><s:select name="activeDir" cssStyle="width: 250px;"
																					list="%{availableDirectory}" listKey="id" listValue="value" /></td>
																				<td valign="top" style="padding-left:3px;">
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																						width="16" height="16" alt="New" title="New" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('newActiveDirectory')"><img class="dinl"
																						src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																					</s:else>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																						width="16" height="16" alt="Modify" title="Modify" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('editActiveDirectory')"><img class="dinl"
																						src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																					</s:else>
																				</td>
																			</tr>
																		</table>
																		</td>
																		<td class="listHead" valign="top"><s:select name="activePriority" cssStyle="width: 92px;"
																			list="%{enumPriority}" listKey="key" listValue="value" /></td>
																	</tr>
																	<s:iterator value="%{dataSource.directory}" status="status">
																		<tr>
																			<td class="listCheck"><s:checkbox name="activeIndices"
																				fieldValue="%{#status.index}" /></td>
																			<td class="list"><s:property value="directoryOrLdap.name" /></td>
																			<td class="list"><s:select name="activePriorities" cssStyle="width: 92px;"
																				value="%{serverPriority}" list="%{enumPriority}" listKey="key"
																				listValue="value" /></td>
																		</tr>
																	</s:iterator>
																	<s:if test="%{gridActiveCount > 0}">
																		<s:generator separator="," val="%{' '}" count="%{gridActiveCount}">
																			<s:iterator>
																				<tr>
																					<td class="list" colspan="3">&nbsp;</td>
																				</tr>
																			</s:iterator>
																		</s:generator>
																	</s:if>
																	<tr>
																		<td class="list" colspan="6">&nbsp;</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr id="hideOpenDir" style="display:<s:property value="%{openDirect?'':'none'}" />">
															<td style="padding-left: 5px;">
																<table cellspacing="0" cellpadding="0" border="0" class="embedded">
																	<tr style="display:<s:property value="%{hideNewOpenDirButton}"/>" id="newOpenDirButton">
																		<td colspan="3" style="padding: 2px 0 2px 0;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
																					class="button" onClick="showCreateSection('newOpenDirButton',
																					'createOpenDirButton', 'createOpenDirSection');"></td>
																				<td><input type="button" name="ignore" value="Remove"
																					class="button" <s:property value="writeDisabled" />
																					onClick="submitAction('removeOpenDir');"></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr style="display:<s:property value="%{hideCreateOpenDirItem}"/>" id="createOpenDirButton">
																		<td colspan="3" style="padding: 2px 0 2px 0;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
																					class="button" onClick="submitAction('addOpenDir');"></td>
																				<td><input type="button" name="ignore" value="Remove"
																					class="button" <s:property value="writeDisabled" />
																					onClick="submitAction('removeOpenDirNone');"></td>
																				<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																					class="button" onClick="hideCreateSection('createOpenDirButton', 'newOpenDirButton', 'createOpenDirSection');"></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr id="headerOpenDirSection">
																		<th align="left" style="padding-left: 0;" width="10px"><input
																			type="checkbox" id="checkAllOpenDir"
																			onClick="toggleCheckAllRules(this,'openDirIndices');"></th>
																		<th align="left" width="335px"><s:text
																			name="config.radiusOnHiveAp.open.directory" /></th>
																		<th align="left"><s:text
																			name="config.radiusAssign.priority" /></th>
																	</tr>
																	<tr style="display:<s:property value="%{hideCreateOpenDirItem}"/>" id="createOpenDirSection">
																		<td class="listHead">&nbsp;</td>
																		<td class="listHead" valign="top">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><s:select name="openDirectory" cssStyle="width: 250px;"
																					list="%{availableOpenDirectory}" listKey="id" listValue="value" /></td>
																				<td valign="top" style="padding-left:3px;">
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																						width="16" height="16" alt="New" title="New" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('newOpenDir')"><img class="dinl"
																						src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																					</s:else>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																						width="16" height="16" alt="Modify" title="Modify" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('editOpenDir')"><img class="dinl"
																						src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																					</s:else>
																				</td>
																			</tr>
																		</table>
																		</td>
																		<td class="listHead" valign="top"><s:select name="openDirPriority" cssStyle="width: 92px;"
																			list="%{enumPriority}" listKey="key" listValue="value" /></td>
																	</tr>
																	<s:iterator value="%{dataSource.openDir}" status="status">
																		<tr>
																			<td class="listCheck"><s:checkbox name="openDirIndices"
																				fieldValue="%{#status.index}" /></td>
																			<td class="list"><s:property value="directoryOrLdap.name" /></td>
																			<td class="list"><s:select name="openDirPriorities" cssStyle="width: 92px;"
																				value="%{serverPriority}" list="%{enumPriority}" listKey="key"
																				listValue="value" /></td>
																		</tr>
																	</s:iterator>
																	<s:if test="%{gridOpenDirCount > 0}">
																		<s:generator separator="," val="%{' '}" count="%{gridOpenDirCount}">
																			<s:iterator>
																				<tr>
																					<td class="list" colspan="3">&nbsp;</td>
																				</tr>
																			</s:iterator>
																		</s:generator>
																	</s:if>
																	<tr>
																		<td class="list" colspan="6">&nbsp;</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr id="hideOpen" style="display:<s:property value="%{open?'':'none'}" />">
															<td style="padding-left: 5px;">
																<table cellspacing="0" cellpadding="0" border="0" class="embedded">
																	<tr>
																		<td colspan="3">
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td style="padding:2px 2px 4px 0;"><s:checkbox name="dataSource.useEdirect"
																						onclick="useEDirectory(this.checked);" /></td>
																					<td class="labelT1" width="155px" style="padding-left: 0"><s:text name="config.radiusOnHiveAp.ldap.edirectory" /></td>
																					<td><s:checkbox name="dataSource.accPolicy"
																						disabled="!dataSource.useEdirect" /></td>
																					<td class="labelT1" style="padding-left: 0"><s:text name="config.radiusOnHiveAp.ldap.accPolicyCheck" /></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr style="display:<s:property value="%{hideNewLdapButton}"/>" id="newLdapButton">
																		<td colspan="3" style="padding-bottom: 2px;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
																					class="button" onClick="showCreateSection('newLdapButton',
																					'createLdapButton', 'createLdapSection');"></td>
																				<td><input type="button" name="ignore" value="Remove"
																					class="button" <s:property value="writeDisabled" />
																					onClick="submitAction('removeLdap');"></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr style="display:<s:property value="%{hideCreateLdapItem}"/>" id="createLdapButton">
																		<td colspan="3" style="padding-bottom: 2px;">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
																					class="button" onClick="submitAction('addLdap');"></td>
																				<td><input type="button" name="ignore" value="Remove"
																					class="button" <s:property value="writeDisabled" />
																					onClick="submitAction('removeLdapNone');"></td>
																				<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																					class="button" onClick="hideCreateSection('createLdapButton', 'newLdapButton', 'createLdapSection');"></td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr id="headerLdapSection">
																		<th align="left" style="padding-left: 0;" width="10px"><input
																			type="checkbox" id="checkAllLdap"
																			onClick="toggleCheckAllRules(this,'ldapIndices');"></th>
																		<th align="left" width="335px"><s:text
																			name="config.radiusOnHiveAp.open" /></th>
																		<th align="left"><s:text
																			name="config.radiusAssign.priority" /></th>
																	</tr>
																	<tr style="display:<s:property value="%{hideCreateLdapItem}"/>" id="createLdapSection">
																		<td class="listHead">&nbsp;</td>
																		<td class="listHead" valign="top">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><s:select name="openLdap" cssStyle="width: 250px;"
																					list="%{availableLdap}" listKey="id" listValue="value" /></td>
																				<td valign="top" style="padding-left:3px;">
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																						width="16" height="16" alt="New" title="New" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('newOpenLdap')"><img class="dinl"
																						src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																					</s:else>
																					<s:if test="%{writeDisabled == 'disabled'}">
																						<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																						width="16" height="16" alt="Modify" title="Modify" />
																					</s:if>
																					<s:else>
																						<a class="marginBtn" href="javascript:submitAction('editOpenLdap')"><img class="dinl"
																						src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																					</s:else>
																				</td>
																			</tr>
																		</table>
																		</td>
																		<td class="listHead" valign="top"><s:select name="ldapPriority" cssStyle="width: 92px;"
																			list="%{enumPriority}" listKey="key" listValue="value" /></td>
																	</tr>
																	<s:iterator value="%{dataSource.ldap}" status="status">
																		<tr>
																			<td class="listCheck"><s:checkbox name="ldapIndices"
																				fieldValue="%{#status.index}" /></td>
																			<td class="list"><s:property value="directoryOrLdap.name" /></td>
																			<td class="list"><s:select name="ldapPriorities" cssStyle="width: 92px;"
																				value="%{serverPriority}" list="%{enumPriority}" listKey="key"
																				listValue="value" /></td>
																		</tr>
																	</s:iterator>
																	<s:if test="%{gridLdapCount > 0}">
																		<s:generator separator="," val="%{' '}" count="%{gridLdapCount}">
																			<s:iterator>
																				<tr>
																					<td class="list" colspan="3">&nbsp;</td>
																				</tr>
																			</s:iterator>
																		</s:generator>
																	</s:if>
																	<tr>
																		<td class="list" colspan="6">&nbsp;</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr style="display:<s:property value="%{hideCache}"/>" id="hideCache">
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr><td height="5px" /></tr>
																	<tr>
																		<td>
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td style="padding:2px 2px 2px 6px;"><s:checkbox name="dataSource.cacheEnable"
																						onclick="enableCache(this.checked);" /></td>
																					<td width="242px" class="labelT1" style="padding-left: 0"><label><s:text
																						name="config.radiusOnHiveAp.cache" /></label></td>
																					<td style="padding-right: 5px;" class="labelT1"><s:text
																						name="config.radiusOnHiveAp.time" /><font color="red">
																						<s:text name="*"/></font></td>
																					<td><s:textfield size="12" name="dataSource.cacheTime" disabled="%{timeDis}" maxlength="7"
																						onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																						<s:text name="config.radiusOnHiveAp.timeRange" /></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td>
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td class="labelT1" width="175px"><s:text name="config.radiusOnHiveAp.database.ldap" /><font color="red"><s:text name="*"/></font></td>
																					<td><s:textfield size="12" name="dataSource.retryInterval" maxlength="9"
																						onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																						<s:text name="config.radiusOnHiveAp.database.ldap.range" /></td>
																				</tr>
																				<tr>
																					<td class="labelT1"><s:text name="config.radiusOnHiveAp.database.local" /><font color="red"><s:text name="*"/></font></td>
																					<td><s:textfield size="12" name="dataSource.localInterval" maxlength="4"
																						onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																						<s:text name="config.radiusOnHiveAp.database.local.range" /></td>
																				</tr>
																				<tr>
																					<td class="labelT1"><s:text name="config.radiusOnHiveAp.database.remote" /><font color="red"><s:text name="*"/></font></td>
																					<td><s:textfield size="12" name="dataSource.remoteInterval" maxlength="4"
																						onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																						<s:text name="config.radiusOnHiveAp.database.remote.range" /></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td>
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td style="padding:2px 2px 0 6px;"><s:checkbox name="dataSource.mapEnable"
																						onclick="enableMap(this.checked);" /></td>
																					<td class="labelT1" style="padding-left: 0"><label><s:text
																						name="config.radiusOnHiveAp.map" /></label></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr style="display:<s:property value="%{hideMap}"/>" id="hideMap">
																		<td style="padding:5px 0px 5px 6px">
																		<div>
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr><td height="4px" /></tr>
																				<tr>
																					<td>
																						<table cellspacing="0" cellpadding="0" border="0">
																							<tr>
																								<td colspan="4" width="300px"><s:radio label="Gender" name="radioGroupOrUser" list="#{'group':'Manually map LDAP user groups to user profiles'}"
																									onclick="enableGroup(true);" value="%{radioGroupOrUser}" /></td>
																								<td colspan="4" width="450px"><s:radio label="Gender" name="radioGroupOrUser" list="#{'user':'Automatically map LDAP user groups to user profiles by matching attributes'}"
																									onclick="enableGroup(false);" value="%{radioGroupOrUser}" /></td>
																							</tr>
																						</table>
																					</td>													
																				</tr>
																				<tr><td height="5px" /></tr>
																				<tr style="display: <s:property value="%{hideMapUserInfor}"/>" id="hideMapUserInfor">
																					<td>
																						<table cellspacing="0" cellpadding="0" border="0">
																							<tr>
																								<td class="labelT1" width="154px"><s:text name="config.localUserGroup.profileId" /></td>
																								<td><s:textfield size="40" name="dataSource.userProfileId" maxlength="32"
																									onkeypress="return hm.util.keyPressPermit(event,'name');" />
																									<s:text name="config.radiusOnHiveAp.nameRange0" /></td>
																							</tr>
																							<tr>
																								<td class="labelT1"><s:text name="config.localUserGroup.vlanId" /></td>
																								<td><s:textfield size="40" name="dataSource.vlanId" maxlength="32"
																									onkeypress="return hm.util.keyPressPermit(event,'name');" />
																									<s:text name="config.radiusOnHiveAp.nameRange0" /></td>
																							</tr>
																							<tr>
																								<td class="labelT1"><s:text name="config.localUserGroup.reauthTime" /></td>
																								<td><s:textfield size="40" name="dataSource.reauthTime" maxlength="32"
																									onkeypress="return hm.util.keyPressPermit(event,'name');" />
																									<s:text name="config.radiusOnHiveAp.nameRange0" /></td>
																							</tr>
																						</table>
																					</td>														
																				</tr>
																				<tr style="display: <s:property value="%{hideLocalUserGroup}"/>" id="hideLocalUserGroup">
																					<td colspan="4">
																						<table border="0" cellspacing="0" cellpadding="0">					
																							<tr>
																								<td class="labelT1" width="155px"><s:text name="config.radiusOnHiveAp.database.group" /></td>
																								<td><s:textfield size="40" name="dataSource.groupAttribute" maxlength="32"
																									onkeypress="return hm.util.keyPressPermit(event,'name');" />
																									<s:text name="config.radiusOnHiveAp.nameRange0" /></td>
																							</tr>
																							<tr>
																								<td colspan="2" style="padding:8px 0px 5px 8px;">
																									<table cellspacing="0" cellpadding="0" border="0">
																										<tr>
																											<td height="5">
																												<table cellspacing="0" cellpadding="0" border="0">
																													<tr><td><label id="groupTable"></label></td></tr>
																												</table>
																											</td>
																										</tr>
																										<tr>
																											<td style="padding:5px 0px 0px 0px">
																											<div>
																												<table border="0" cellspacing="0" cellpadding="0">
																													<tr id="hiveApListForCommu" style="display:<s:property value="%{active?'none':''}" />">
																														<td valign="top" colspan="5">
																															<table>
																																<tr>
																																	<td><s:text name="config.radiusOnHiveAp.database.custom.roleMap.commu.AP" /></td>
																																	<td><div style="z-index: 90; width:223px" id="apServerDiv"><s:textfield 
																																		id="apServerName" name="dataSource.apHostName" maxlength="18" size="25" /><a 
																																		id="apComboBox" tabindex="-1" href="javascript:void(0)" class="acDropdown"></a><div 
																																		id="apContainer"></div><s:hidden 
																																		id="apMacHidden" name="dataSource.apMac"></s:hidden></div><br></td>
																																	<td></td>
																																</tr>
																															</table>
																														</td>
																													</tr>
																													<tr>
																														<td colspan="5" valign="top">
																															<table><tr>
																																<td>
																																	<div id="hideDomainsForTreeDir" style="display:<s:property value="%{active?'':'none'}" />">
																																	<s:text name="config.radiusOnHiveAp.workName" /><select 
																																	name="dataSource.domainForTreeDir" id="domainForTreeDir" style="width: 200px" onchange="resetTree(this.options[this.selectedIndex].value);" >
																																		<s:if test="%{dataSource.domainsForTreeDir.size() == 0}"><option value="-1"><s:text name="config.optionsTransfer.none" /></option></s:if>
																																		<s:else>
																																			<s:set name="optGrp" value=""/>
																																			<s:iterator value="%{dataSource.domainsForTreeDir}" status="status">
																																				<s:if test="%{optGroupLabel != #optGrp}"><s:if test="%{#optGrp != ''}"></optgroup></s:if><optgroup
																																				 label="<s:property value='optGroupLabel'/>" ></s:if>
																																					<s:if test="%{domainId == dataSource.domainForTreeDir}"><option value="<s:property value='domainId'/>" selected="selected"></s:if>
																																					<s:else><option value="<s:property value='domainId'/>"></s:else>
																																						<s:if test="%{domain != ''}"><s:property value="domain"/></s:if><s:else
																																						><s:property value="basedN"/></s:else></option>
																																				<s:set name="optGrp" value="optGroupLabel"/>
																																			</s:iterator>
																																		</s:else>
																																	</select>
																																	</div>
																																	<div id="hideDomainsForTreeLdap" style="display:<s:property value="%{open?'':'none'}" />">
																																	<s:text name="config.radiusOnHiveAp.workName" /><select 
																																	name="dataSource.domainForTreeLdap" id="domainForTreeLdap" style="width: 200px" onchange="resetTree(this.options[this.selectedIndex].value);" >
																																		<s:if test="%{dataSource.domainsForTreeLdap.size() == 0}"><option value="-1"><s:text name="config.optionsTransfer.none" /></option></s:if>
																																		<s:else>
																																			<s:set name="optGrp" value=""/>
																																			<s:iterator value="%{dataSource.domainsForTreeLdap}" status="status">
																																				<s:if test="%{optGroupLabel != #optGrp}"><s:if test="%{#optGrp != ''}"></optgroup></s:if><optgroup
																																				 label="<s:property value='optGroupLabel'/>" ></s:if>
																																					<s:if test="%{domainId == dataSource.domainForTreeLdap}"><option value="<s:property value='domainId'/>" selected="selected"></s:if>
																																					<s:else><option value="<s:property value='domainId'/>"></s:else>
																																						<s:if test="%{domain != ''}"><s:property value="domain"/></s:if><s:else
																																						><s:property value="basedN"/></s:else></option>
																																				<s:set name="optGrp" value="optGroupLabel"/>
																																			</s:iterator>
																																		</s:else>
																																	</select>
																																	</div>
																																	<div id="hideDomainsForTreeOpenDir" style="display:<s:property value="%{openDirect?'':'none'}" />">
																																	<s:text name="config.radiusOnHiveAp.workName" /><select 
																																	name="dataSource.domainForTreeOpenDir" id="domainForTreeOpenDir" style="width: 200px" onchange="resetTree(this.options[this.selectedIndex].value);" >
																																		<s:if test="%{dataSource.domainsForTreeOpenDir.size() == 0}"><option value="-1"><s:text name="config.optionsTransfer.none" /></option></s:if>
																																		<s:else>
																																			<s:set name="optGrp" value=""/>
																																			<s:iterator value="%{dataSource.domainsForTreeOpenDir}" status="status">
																																				<s:if test="%{optGroupLabel != #optGrp}"><s:if test="%{#optGrp != ''}"></optgroup></s:if><optgroup
																																				 label="<s:property value='optGroupLabel'/>" ></s:if>
																																					<s:if test="%{domainId == dataSource.domainForTreeOpenDir}"><option value="<s:property value='domainId'/>" selected="selected"></s:if>
																																					<s:else><option value="<s:property value='domainId'/>"></s:else>
																																						<s:if test="%{domain != ''}"><s:property value="domain"/></s:if><s:else
																																						><s:property value="basedN"/></s:else></option>
																																				<s:set name="optGrp" value="optGroupLabel"/>
																																			</s:iterator>
																																		</s:else>
																																	</select>
																																	</div>
																																</td>
																																<td>
																																	<%--<div id="nodeRefresh" style="display:none; align:right"><input type="button" 
																																		name="ignore" style="width:100px" value="node refresh" <s:property value="writeDisabled" />
																																		class="button" onClick="refreshNode();"></div>--%>
																																</td>
																																<td></td>
																																</tr>
																															</table>
																														</td>
																													</tr>
																													<tr>
																														<td valign="top" colspan="5">
																															<table>
																																<tr>
																																	<td class="noteInfo"><br>
																																	<div id="ouTipWithGlobalCatalog"
																																		style="display:block"><s:text
																																		name="config.radiusOnHiveAp.database.custom.map.userProfile.globalcatalog" /></div>
																																	<!--<div id="ouTipNoGlobalCatalog"
																																		style="display:<s:property value="%{dataSource.globalCatalog?'none':''}" />"><s:text
																																		name="config.radiusOnHiveAp.database.custom.map.userProfile" /></div>
																																	--></td>
																																</tr>
																															</table>
																														</td>
																													</tr>
																													<tr>
																														<td width="2px" style="background-color: #FFFFFF"></td>
																														<td width="350px" valign="top"><div id="treeDivDir" style="overflow:scroll;width:350px;height:350px;border:1px solid #808080"></div><div 
																															id="treeDivLdap" style="overflow:scroll;width:350px;height:350px;border:1px solid #808080"></div><div 
																															id="treeDivOpenDir" style="overflow:scroll;width:350px;height:350px;border:1px solid #808080"></div></td>
																														<td width="2px" style="background-color: #FFFFFF"></td>
																														<td width="5px" ></td>
																														<td width="310px" >
																															<table cellspacing="0" cellpadding="0" border="0">
																																<tr>
																																	<td valign="top" height="175px">
																																		<table cellspacing="0" cellpadding="0" border="0">
																																			<tr><td/><td class="listHead" id="userGroupForGlobalCatalog" style="display:block">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<s:text 
																																				 name="config.radiusOnHiveAp.database.group.globalcatalog" /><br/><br/>&nbsp;&nbsp;<font size="3" color="#808080" ><strong><s:text 
																																				 name="config.radiusOnHiveAp.database.group.map.or" /></strong></font>&nbsp;&nbsp;&nbsp;&nbsp;<s:textfield
																																				 onkeypress="return hm.util.keyPressPermit(event,'ssid');"
																																				 maxlength="32" size="25" name="dataSource.userGroupForGlobalCatalog" /></td></tr>
																																			<tr>
																																				<td><br/></td>
																																			</tr>
																																			<tr id="fe_forNoteText" style="display: none">
																																				<td/>
																																				<td class="noteError" id="textfe_forNoteText">To be changed</td>
																																			</tr>
																																			<tr style="display: none">
																																				<td ><s:textfield id="forNoteText" name="forNoteText"/></td>
																																			</tr>
																																		</table>
																																	</td>
																																</tr>
																																<tr>
																																	<td valign="bottom" height="175px">
																																		<table cellspacing="0" cellpadding="0" border="0">
																																			<tr><td colspan="2" align="center"><br/><br/><font size="3" color="#808080"><strong><s:text name="config.radiusOnHiveAp.database.group.mapsto.userprofile" /></strong></font><br/><br/></td></tr>
																																			<tr><td/><td class="listHead">&nbsp;&nbsp;<s:text name="config.radiusOnHiveAp.database.custom.userProfileForTraffic" /><br/></td></tr>
																																			<tr>
																																				<td/>
																																				<td class="listHead" valign="top">
																																					<table border="0" cellspacing="0" cellpadding="0">
																																						<tr>
																																							<td><s:select name="userProfile" cssStyle="width: 250px;"
																																								list="%{availableUserProfile}" listKey="id" listValue="value" /></td>
																																							<td valign="top" style="padding-left:3px;">
																																								<s:if test="%{writeDisabled == 'disabled'}">
																																									<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																																									width="16" height="16" alt="New" title="New" />
																																								</s:if>
																																								<s:else>
																																									<a class="marginBtn" href="javascript:submitAction('newUserProfile')"><img class="dinl"
																																									src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																																								</s:else>
																																								<s:if test="%{writeDisabled == 'disabled'}">
																																									<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																																									width="16" height="16" alt="Modify" title="Modify" />
																																								</s:if>
																																								<s:else>
																																									<a class="marginBtn" href="javascript:submitAction('editUserProfile')"><img class="dinl"
																																									src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																																								</s:else>
																																							</td>
																																						</tr>
																																					</table>
																																				</td>
																																			</tr>
																																			<tr><td colspan="2"><br/></td></tr>
																																			<tr>
																																				<td/>
																																				<td>
																																					<table border="0" cellspacing="0" cellpadding="0" id="addRoleMapArea">
																																						<tr>
																																							<td><input type="button" id="addRoleBtn" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
																																								class="button" onClick="submitAddRoleMap();"></td>
																																							<td><input type="button" name="ignore" value="Remove"
																																								class="button" <s:property value="writeDisabled" />
																																								onClick="submitAction('removeRoleMapNone');"></td>
																																						</tr>
																																					</table><br/>
																																				</td>
																																			</tr>
																																		</table>
																																	</td>
																																</tr>
																															</table>
																														</td>													
																													</tr>
																													<tr>
																														<td colspan="5">
																															<table border="0" cellspacing="0" cellpadding="0">
																															<tr><td class="noteInfo"><br/><s:text name="config.radiusOnHiveAp.database.custom.userProfileNote" /><br/></td></tr>
																															<tr id="hideActiveRoleMap" style="display:<s:property value="%{active?'':'none'}" />">
																																<td>
																																	<table border="0" cellspacing="0" cellpadding="0">
																																		<tr id="headerSection">
																																			<th align="left" style="padding-left: 0;" width="10"><input
																																				type="checkbox" id="checkAllRoleMapDir"
																																				onClick="toggleCheckAllRules(this, 'roleMapDirIndices');"></th>
																																			<th align="left" width="50px"><s:text
																																				name="config.radiusOnHiveAp.database.group.id" /></th>
																																			<th align="left" width="450px"><s:text
																																				name="config.radiusOnHiveAp.database.group.ldapServerOu" /></th>
																																			<th align="left" width="150px"><s:text
																																				name="config.radiusOnHiveAp.database.group.userProfile" /></th>
																																			<th align="left" width="80px"><s:text
																																				name="config.radiusOnHiveAp.database.group.attrId" /></th>
																																		</tr>
																																		<tr>
																																		<td valign="top" colspan="5">
																																			<table cellspacing="0" cellpadding="0" border="0" class="embedded" id="roleMapDirTable">
																																				<s:iterator value="%{dataSource.directoryOu}" status="status">
																																					<tr>
																																						<td class="listCheck"><s:checkbox name="roleMapDirIndices"
																																							fieldValue="%{#status.index}" /></td>
																																						<td class="list" width="50px"><s:property value="rowId" 
																																							/><s:hidden name="orderingDir" value="%{#status.index}" /></td>
																																						<td class="list" width="450px"><s:property value="groupAttributeValue" /></td>
																																						<td class="list" width="150px"><s:select id="userProfileDir_%{#status.index}" name="userProfilesDir" cssStyle="width: 120px;"
																																							list="%{availableUserProfile}" listKey="id" listValue="value" value="userProfileId" 
																																							onchange="changeAttributeIdDir(%{#status.index})"/></td>
																																						<td class="list" width="60px"><s:textfield id="attIdsDir_%{#status.index}" name="attributeIdsDir" 
																																							value="%{userProfileAttribute}" readonly="true" size="5"/></td>
																																					</tr>
																																				</s:iterator>
																																				<s:if test="%{gridDirOuCount > 0}">
																																					<s:generator separator="," val="%{' '}" count="%{gridDirOuCount}">
																																						<s:iterator>
																																							<tr>
																																								<td class="list" width="760px">&nbsp;</td>
																																							</tr>
																																						</s:iterator>
																																					</s:generator>
																																				</s:if>
																																			</table>
																																		</td>
																																		</tr>
																																	</table>
																																</td>
																																<s:if test="%{dataSource.directoryOu.size() > 1}">
																																	<td valign="top" style="padding: 0px 0px 0 10px;">
																																		<table cellspacing="0" cellpadding="0" border="0">
																																			<tr><td height="50px">&nbsp;</td></tr>
																																			<tr>
																																				<td><input type="button" class="moveRow" value="Up"
																																					onclick="hm.util.moveRowsUp('roleMapDirTable');" /></td>
																																			</tr>
																																			<tr>
																																				<td><input type="button" class="moveRow" value="Down"
																																					onclick="hm.util.moveRowsDown('roleMapDirTable');" /></td>
																																			</tr>
																																			<s:if test="%{dataSource.directoryOu.size() > 15}">
																																			<s:generator separator="," val="%{' '}" count="%{dataSource.directoryOu.size()-2}">
																																				<s:iterator>
																																					<tr>
																																						<td>&nbsp;</td>
																																					</tr>
																																				</s:iterator>
																																			</s:generator>
																																				<tr>
																																					<td><input type="button" class="moveRow" value="Up"
																																						onclick="hm.util.moveRowsUp('roleMapDirTable');" /></td>
																																				</tr>
																																				<tr>
																																					<td><input type="button" class="moveRow" value="Down"
																																						onclick="hm.util.moveRowsDown('roleMapDirTable');" /></td>
																																				</tr>
																																			</s:if>
																																		</table>
																																	</td>
																																</s:if>
																															</tr>
																															<tr id="hideOpenRoleMap" style="display:<s:property value="%{open?'':'none'}" />">
																																<td>
																																	<table border="0" cellspacing="0" cellpadding="0">
																																		<tr id="headerSection">
																																			<th align="left" style="padding-left: 0;" width="10"><input
																																				type="checkbox" id="checkAllRoleMapLdap"
																																				onClick="toggleCheckAllRules(this, 'roleMapLdapIndices');"></th>
																																			<th align="left" width="50px"><s:text
																																				name="config.radiusOnHiveAp.database.group.id" /></th>
																																			<th align="left" width="450px"><s:text
																																				name="config.radiusOnHiveAp.database.group.ldapServerOu" /></th>
																																			<th align="left" width="150px"><s:text
																																				name="config.radiusOnHiveAp.database.group.userProfile" /></th>
																																			<th align="left" width="80px"><s:text
																																				name="config.radiusOnHiveAp.database.group.attrId" /></th>
																																		</tr>
																																		<tr>
																																		<td valign="top" colspan="5">
																																			<table cellspacing="0" cellpadding="0" border="0" class="embedded" id="roleMapLdapTable">
																																				<s:iterator value="%{dataSource.ldapOu}" status="status">
																																					<tr>
																																						<td class="listCheck"><s:checkbox name="roleMapLdapIndices"
																																							fieldValue="%{#status.index}" /></td>
																																						<td class="list" width="50px">
																																							<s:property value="rowId" /><s:hidden name="orderingLdap" value="%{#status.index}" /></td>
																																						<td class="list" width="450px">
																																							<s:property value="groupAttributeValue" /></td>
																																						<td class="list" width="150px"><s:select id="userProfileLdap_%{#status.index}" name="userProfilesLdap" cssStyle="width: 120px;"
																																							list="%{availableUserProfile}" listKey="id" listValue="value" value="userProfileId" 
																																							onchange="changeAttributeIdLdap(%{#status.index})"/></td>																																									
																																						<td class="list" width="60px"><s:textfield id="attIdsLdap_%{#status.index}" name="attributeIdsLdap" 
																																							value="%{userProfileAttribute}" readonly="true" size="5"/></td>
																																					</tr>
																																				</s:iterator>
																																				<s:if test="%{gridLdapOuCount > 0}">
																																					<s:generator separator="," val="%{' '}" count="%{gridLdapOuCount}">
																																						<s:iterator>
																																							<tr>
																																								<td class="list" width="760px">&nbsp;</td>
																																							</tr>
																																						</s:iterator>
																																					</s:generator>
																																				</s:if>	
																																			</table>
																																		</td>
																																		</tr>
																																	</table>
																																</td>
																																<s:if test="%{dataSource.ldapOu.size() > 1}">
																																	<td valign="top" style="padding: 0px 0px 0 10px;">
																																		<table cellspacing="0" cellpadding="0" border="0">
																																			<tr><td height="50px">&nbsp;</td></tr>
																																			<tr>
																																				<td><input type="button" class="moveRow" value="Up"
																																					onclick="hm.util.moveRowsUp('roleMapLdapTable');" /></td>
																																			</tr>
																																			<tr>
																																				<td><input type="button" class="moveRow" value="Down"
																																					onclick="hm.util.moveRowsDown('roleMapLdapTable');" /></td>
																																			</tr>
																																			<s:if test="%{dataSource.ldapOu.size() > 15}">
																																			<s:generator separator="," val="%{' '}" count="%{dataSource.ldapOu.size()-2}">
																																				<s:iterator>
																																					<tr>
																																						<td>&nbsp;</td>
																																					</tr>
																																				</s:iterator>
																																			</s:generator>
																																				<tr>
																																					<td><input type="button" class="moveRow" value="Up"
																																						onclick="hm.util.moveRowsUp('roleMapLdapTable');" /></td>
																																				</tr>
																																				<tr>
																																					<td><input type="button" class="moveRow" value="Down"
																																						onclick="hm.util.moveRowsDown('roleMapLdapTable');" /></td>
																																				</tr>
																																			</s:if>
																																		</table>
																																	</td>
																																</s:if>
																															</tr>
																															<tr id="hideOpenDirRoleMap" style="display:<s:property value="%{openDirect?'':'none'}" />">
																																<td>
																																	<table border="0" cellspacing="0" cellpadding="0">
																																		<tr id="headerSection">
																																			<th align="left" style="padding-left: 0;" width="10"><input
																																				type="checkbox" id="checkAllRoleMapOpen"
																																				onClick="toggleCheckAllRules(this, 'roleMapOpenDirIndices');"></th>
																																			<th align="left" width="50px"><s:text
																																				name="config.radiusOnHiveAp.database.group.id" /></th>
																																			<th align="left" width="450px"><s:text
																																				name="config.radiusOnHiveAp.database.group.ldapServerOu" /></th>
																																			<th align="left" width="150px"><s:text
																																				name="config.radiusOnHiveAp.database.group.userProfile" /></th>
																																			<th align="left" width="80px"><s:text
																																				name="config.radiusOnHiveAp.database.group.attrId" /></th>
																																		</tr>
																																		<tr>
																																		<td valign="top" colspan="5">
																																			<table cellspacing="0" cellpadding="0" border="0" class="embedded" id="roleMapOpenDirTable">
																																				<s:iterator value="%{dataSource.openDirOu}" status="status">
																																					<tr>
																																						<td class="listCheck"><s:checkbox name="roleMapOpenDirIndices"
																																							fieldValue="%{#status.index}" /></td>
																																						<td class="list" width="50px">
																																							<s:property value="rowId" /><s:hidden name="orderingOpenDir" value="%{#status.index}" /></td>
																																						<td class="list" width="450px">
																																							<s:property value="groupAttributeValue" /></td>
																																						<td class="list" width="150px"><s:select id="userProfileOpenDir_%{#status.index}" name="userProfilesOpenDir" cssStyle="width: 120px;"
																																							list="%{availableUserProfile}" listKey="id" listValue="value" value="userProfileId" 
																																							onchange="changeAttributeIdOpenDir(%{#status.index})"/></td>																																								
																																						<td class="list" width="60px"><s:textfield id="attIdsOpenDir_%{#status.index}" name="attributeIdsOpenDir" 
																																							value="%{userProfileAttribute}" readonly="true" size="5"/></td>
																																					</tr>
																																				</s:iterator>
																																				<s:if test="%{gridOpenDirOuCount > 0}">
																																					<s:generator separator="," val="%{' '}" count="%{gridOpenDirOuCount}">
																																						<s:iterator>
																																							<tr>
																																								<td class="list" width="760px">&nbsp;</td>
																																							</tr>
																																						</s:iterator>
																																					</s:generator>
																																				</s:if>	
																																			</table>
																																		</td>
																																		</tr>
																																	</table>
																																</td>
																																<s:if test="%{dataSource.openDirOu.size() > 1}">
																																	<td valign="top" style="padding: 0px 0px 0 10px;">
																																		<table cellspacing="0" cellpadding="0" border="0">
																																			<tr><td height="50px">&nbsp;</td></tr>
																																			<tr>
																																				<td><input type="button" class="moveRow" value="Up"
																																					onclick="hm.util.moveRowsUp('roleMapOpenDirTable');" /></td>
																																			</tr>
																																			<tr>
																																				<td><input type="button" class="moveRow" value="Down"
																																					onclick="hm.util.moveRowsDown('roleMapOpenDirTable');" /></td>
																																			</tr>
																																			<s:if test="%{dataSource.openDirOu.size() > 15}">
																																			<s:generator separator="," val="%{' '}" count="%{dataSource.openDirOu.size()-2}">
																																				<s:iterator>
																																					<tr>
																																						<td>&nbsp;</td>
																																					</tr>
																																				</s:iterator>
																																			</s:generator>
																																				<tr>
																																					<td><input type="button" class="moveRow" value="Up"
																																						onclick="hm.util.moveRowsUp('roleMapOpenDirTable');" /></td>
																																				</tr>
																																				<tr>
																																					<td><input type="button" class="moveRow" value="Down"
																																						onclick="hm.util.moveRowsDown('roleMapOpenDirTable');" /></td>
																																				</tr>
																																			</s:if>
																																		</table>
																																	</td>
																																</s:if>
																															</tr>
																															</table>
																														</td>
																													</tr>																						
																												</table>
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
																			</div>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
										        </div>
										        <!-- Library SIP Settings -->
										        <div id="tab3">
										        	<table border="0" cellspacing="0" cellpadding="0">
										        		<tr id="sipContent" style="display:<s:property value="%{dataSource.librarySipCheck?'':'none'}" />">
										        			<td>
										        				<table border="0" cellspacing="0" cellpadding="0">
										        					<tr>
																		<td class="labelT1" width="160"><label><s:text name="config.radiusOnHiveAp.radius.library.server.title" /><font color="red"><s:text name="*"/></font></label></td>
																		<td>
																			<ah:createOrSelect divId="errorDisplaySip" swidth="193px"
																				list="availableSipServers" typeString="IpAddressSip"
																				selectIdName="sipServerSelect" inputValueName="inputSipServer" />
																		</td>
																	</tr>
																	<tr>
																		<td class="labelT1" width="160"><label><s:text name="config.radiusOnHiveAp.radius.library.sip.port" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:textfield size="6" name="dataSource.sipPort" maxlength="5"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																			<s:text name="config.radiusAssign.portRange" /></td>
																	</tr>
																	<tr>
																		<td colspan="2">
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td class="labelT1" width="160">
																						<label><s:text name="config.radiusOnHiveAp.radius.library.sip.policy" /></label><font color="red"><s:text name="*"/></font></td>
																					<td><s:select name="librarySipId" cssStyle="width: 272px;" id="librarySipId"
																						list="%{librarySipPolicies}" listKey="id" listValue="value" />
																					</td>
																					<td>
																						<s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																							src="<s:url value="/images/new_disable.png" />"
																							width="16" height="16" alt="New" title="New" />
																						</s:if>
																						<s:else>
																							<a class="marginBtn" href="javascript:submitAction('newLibrarySip')"><img class="dinl"
																							src="<s:url value="/images/new.png" />"
																							width="16" height="16" alt="New" title="New" /></a>
																						</s:else>
																					</td>
																					<td>
																						<s:if test="%{writeDisabled == 'disabled'}">
																							<img class="dinl marginBtn"
																							src="<s:url value="/images/modify_disable.png" />"
																							width="16" height="16" alt="Modify" title="Modify" />
																						</s:if>
																						<s:else>
																							<a class="marginBtn" href="javascript:submitAction('editLibrarySip')"><img class="dinl"
																							src="<s:url value="/images/modify.png" />"
																							width="16" height="16" alt="Modify" title="Modify" /></a>
																						</s:else>
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.radius.library.institution" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:textfield size="48" name="dataSource.institutionId" maxlength="%{commentLength}"
																			onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
																			name="config.radiusOnHiveAp.passRange" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.radius.library.separator" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:textfield size="6" name="dataSource.separator" maxlength="1" 
																			onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
																			name="config.radiusOnHiveAp.radius.library.separator.range" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1" colspan="2">
																			<s:checkbox name="dataSource.loginEnable" onclick="showLoginUserAndPwd(this.checked);"/>
																			<s:text name="config.radiusOnHiveAp.radius.library.login.enable"/></td>
																	</tr>
																	<tr id="showSiplogin" style="display: <s:property value="%{dataSource.loginEnable?'':'none'}"/>">
																		<td colspan="2"><table cellspacing="0" cellpadding="0" border="0" width="100%" style="padding-left: 30px">
																			<tr>
																				<td class="labelT1" width="130"><label><s:text name="config.radiusOnHiveAp.radius.library.login.name" /><font color="red"><s:text name="*"/><font></label></td>
																				<td><s:textfield size="32" name="dataSource.loginUser" maxlength="%{radiusNameLength}" 
																					onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
																					name="config.radiusOnHiveAp.nameRange1" /></td>
																			</tr>
																			<tr>
																				<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.radius.library.login.pwd" /><font color="red"><s:text name="*"/><font></label></td>
																				<td colspan="2"><s:password id="sipLoginPwd" name="dataSource.loginPwd" onkeypress="return hm.util.keyPressPermit(event,'password');" 
																	               	maxlength="%{radiusNameLength}" showPassword="true" size="32" /><s:textfield id="sipLoginPwd_text" name="dataSource.loginPwd" maxlength="%{radiusNameLength}" cssStyle="display:none"
																					onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true" size="32" />
																					<s:text name="config.radiusOnHiveAp.nameRange1" /></td>
																			</tr>
																			<tr>
																				<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.passConf" /><font color="red"><s:text name="*"/><font></label></td>
																				<td colspan="2"><s:password id="confirmSipPwd" onkeypress="return hm.util.keyPressPermit(event,'password');" 
																	                maxlength="%{radiusNameLength}" showPassword="true" value="%{dataSource.loginPwd}" size="32" /><s:textfield id="confirmSipPwd_text" maxlength="%{radiusNameLength}" cssStyle="display:none"
																					onkeypress="return hm.util.keyPressPermit(event,'password');" size="32" />
																					<s:text name="config.radiusOnHiveAp.nameRange1" /></td>
																			</tr>
																			<tr>
																				<td>&nbsp;</td>
																				<td colspan="2"><s:checkbox id="chkSipPwdDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword
																	                (this.checked,['sipLoginPwd','confirmSipPwd'],['sipLoginPwd_text','confirmSipPwd_text']);" />
																	                <s:text name="config.radiusAssign.secret.obscure" /></td>
																			</tr>
																		</table></td>
																	</tr>
										        				</table>
										        			</td>
										        		</tr>
										        	</table>
												</div>
												</s:if>
										        <!-- Local Database -->
										        <div id="tab2">
										        	<table border="0" cellspacing="0" cellpadding="0">
														<tr id="localUserTr" style="display: <s:property value="%{localUserStyle}"/>">
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td height="2">
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr><td><label id="userTable"></label></td></tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<s:push value="%{localUserGroupOptions}">
																			<td colspan="3" style="padding-left: 45px"><tiles:insertDefinition
																				name="optionsTransfer"/></td>
																		</s:push>
																	</tr>
																	<tr>
																		<td height="5"></td>
																	</tr>
																	<%--<tr>
																		<td style="padding-left:50px;">
																			<s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn" src="<s:url value="/images/new_disable.png" />"
																				width="16" height="16" alt="New" title="New" />
																			</s:if>
																			<s:else>
																				<a class="marginBtn" href="javascript:submitAction('newLocalUserGroup')"><img class="dinl"
																				src="<s:url value="/images/new.png" />" width="16" height="16" alt="New" title="New" /></a>
																			</s:else>
																			<s:if test="%{writeDisabled == 'disabled'}">
																				<img class="dinl marginBtn" src="<s:url value="/images/modify_disable.png" />"
																				width="16" height="16" alt="Modify" title="Modify" />
																			</s:if>
																			<s:else>
																				<a class="marginBtn" href="javascript:submitAction('editLocalUserGroup')"><img class="dinl"
																				src="<s:url value="/images/modify.png" />" width="16" height="16" alt="Modify" title="Modify" /></a>
																			</s:else>
																		</td>
																	</tr>--%>
																</table>
															</td>
														</tr>
										        	</table>
												</div>
											</td>
										</tr>
										<tr><td height="10px"></td></tr>
									</table>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr><td height="5px"></td></tr>
				<tr>
					<td><!-- optional settings -->
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
									<fieldset style="width:750px;"><legend><s:text name="config.radiusOnHiveAp.optional" /></legend>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr><td height="10px"></td></tr>
										<!-- RADIUS Settings -->
										<tr>
											<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusOnHiveAp.radius.setting" />','radiusSettings');</script></td>
										</tr>
										<tr>
											<td style="padding-left: 5px;">
												<div id="radiusSettings" style="display:<s:property value="%{dataSource.radiusSettingsStyle}"/>">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1" width="175px" id="authTitle"><s:text name="config.radiusOnHiveAp.authType" /></td>
																		<td><s:select name="dataSource.authType" cssStyle="width: 303px;"
																			list="%{authType}" value="dataSource.authType" listKey="key"
																			onchange="enableFiles(this.options[this.selectedIndex].value);"
																			listValue="value" /></td>
																	</tr>
																</table>
															</td>
															
														</tr>
														<tr>
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1" width="175px" id="defaultAuthTitle"><s:text name="config.radiusOnHiveAp.authType.default" /></td>
																		<td><s:select name="dataSource.authTypeDefault" cssStyle="width: 303px;"
																			list="%{authTypeDefault}" value="dataSource.authTypeDefault" listKey="key"
																			listValue="value" /></td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr id="tlsCheck" style="display:<s:property value="%{showCertFile}"/>">
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td class="labelT1" width="175px"><s:text name="config.radiusOnHiveAp.caCert" /><font color="red"><s:text name="*"/></font></td>
																		<td><s:select name="dataSource.caCertFile" cssStyle="width: 303px;"
																			list="%{availableCaFile}" value="dataSource.caCertFile" />
																			<input type="button" value="Import" <s:property value="writeDisabled" />
																			class="button short" onClick="submitAction('newFile1');"></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><s:text name="config.radiusOnHiveAp.certFile" /><font color="red"><s:text name="*"/></font></td>
																		<td><s:select name="dataSource.serverFile" cssStyle="width: 303px;"
																			list="%{availableCaFile}" value="dataSource.serverFile" />
																			<input type="button" value="Import" <s:property value="writeDisabled" />
																			class="button short" onClick="submitAction('newFile2');"></td>
																	</tr>
																	<tr>
																		<td class="labelT1">&nbsp;</td>
																		<td><s:checkbox name="dataSource.checkCA"/><label><s:text
																			name="config.radiusOnHiveAp.verify.server.cert.file" /></label></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><s:text name="config.radiusOnHiveAp.keyFile" /><font color="red"><s:text name="*"/></font></td>
																		<td><s:select name="dataSource.keyFile" cssStyle="width: 303px;"
																			list="%{availableCaFile}" value="dataSource.keyFile" />
																			<input type="button" value="Import" <s:property value="writeDisabled" />
																			class="button short" onClick="submitAction('newFile3');"></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><s:text name="config.radiusOnHiveAp.keyPass" /></td>
																		<td><s:password id="keyPassword" name="dataSource.keyPassword" cssStyle="width: 300px;"
															                maxlength="%{passLength}" showPassword="true"
															                onkeypress="return hm.util.keyPressPermit(event,'password');" />
															                <s:textfield id="keyPassword_text" name="dataSource.keyPassword" maxlength="%{passLength}" cssStyle="width: 300px;display:none"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true" />
															                <s:text name="config.radiusOnHiveAp.desRange" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><s:text name="config.radiusOnHiveAp.passConf" /></td>
																		<td><s:password cssStyle="width: 300px;" id="confPassword" maxlength="%{passLength}" showPassword="true"
															                onkeypress="return hm.util.keyPressPermit(event,'password');" value="%{dataSource.keyPassword}" />
															                <s:textfield id="confPassword_text" maxlength="%{passLength}" cssStyle="width: 300px;display:none"
																			onkeypress="return hm.util.keyPressPermit(event,'password');" />
															                <s:text name="config.radiusOnHiveAp.desRange" /></td>
																	</tr>
																	<tr>
																		<td>&nbsp;</td>
																		<td>
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td><s:checkbox id="chkPasswordDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword(this.checked,['keyPassword','confPassword'],['keyPassword_text','confPassword_text']);"
																						 disabled="%{writeDisable4Struts}" /></td>
																					<td><s:text name="admin.user.obscurePassword" /></td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td colspan="2">
																			<table cellspacing="0" cellpadding="0" border="0">
																				<tr>
																					<td>
																						<table cellspacing="0" cellpadding="0" border="0">
																							<tr>
																								<td style="padding:2px 2px 2px 6px"><s:checkbox name="dataSource.cnEnable"
																									disabled="%{fileDis}" /></td>
																								<td class="labelT1" style="padding-left: 0"><label><s:text
																									name="config.radiusOnHiveAp.general.checkCN" /></label></td>
																							</tr>
																						</table>
																					</td>
																				</tr>
																				<tr><td height="2px"></td></tr>
																				<tr>
																					<td>
																					<fieldset>
																					<legend><s:text name="config.radiusOnHiveAp.general.checkDB" /></legend>
																					<table cellspacing="0" cellpadding="0" border="0">
																						<tr>
																							<td style="padding:2px 2px 0 0"><s:checkbox name="dataSource.dbEnable" /></td>
																							<td class="labelT1" style="padding-left: 0"><s:text name="config.radiusOnHiveAp.general.checkDB.tls"/></td>
																							<s:if test="%{fullMode}">
																							<td style="padding:2px 2px 0 16px">
																								<s:checkbox name="dataSource.peapCheckInDb" disabled="%{disablePeapCheck}" /></td>
																							<td class="labelT1" style="padding-left: 0"><s:text name="config.radiusOnHiveAp.general.checkDB.peap"/></td>
																							<td style="padding:2px 2px 0 16px">
																								<s:checkbox name="dataSource.ttlsCheckInDb" disabled="%{disableTtlsCheck}" /></td>
																							<td class="labelT1" style="padding-left: 0"><s:text name="config.radiusOnHiveAp.general.checkDB.ttls"/></td>
																							</s:if>
																						</tr>
																					</table>
																					</fieldset>
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td style="padding:2px 2px 0 6px"><s:checkbox name="dataSource.serverEnable" onclick="changeServerState(this.checked);" /></td>
																		<td class="labelT1" style="padding-left: 0" width="155px"><label><s:text name="config.radiusOnHiveAp.server" /></label></td>
																		<td width="80px"><label><s:text name="config.radiusOnHiveAp.port" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:textfield size="12" name="dataSource.serverPort" maxlength="5"
																			onkeypress="return hm.util.keyPressPermit(event,'ten');" disabled="%{dataSource.serverEnable==false}" />&nbsp;<s:text
																			name="config.radiusOnHiveAp.portRange" /></td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</div>
											</td>
										</tr>
										<tr><td height="10px"></td></tr>
										<!-- NAS Settings -->
										<tr>
											<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusOnHiveAp.radius.nasSetting" />','nasSettings');</script></td>
										</tr>
										<tr>
											<td style="padding-left: 10px;">
												<div id="nasSettings" style="display: <s:property value="%{dataSource.nasSettingsStyle}"/>">
													<table cellspacing="0" cellpadding="0" border="0">
														<tr><td height="10px"></td></tr>
														<tr>
															<td valign="top">
															<table cellspacing="0" cellpadding="0" border="0" class="embedded" id="radiusTable" width="100%">
																<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
																	<td colspan="6" style="padding-bottom: 2px;">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
																				class="button" onClick="showCreateSection('newButton',
																							'createButton', 'createSection');"></td>
																			<td><input type="button" name="ignore" value="Remove"
																				class="button" <s:property value="writeDisabled" />
																				onClick="submitAction('removeIpAddress');"></td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
																	<td colspan="6" style="padding-bottom: 2px;">
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
																				class="button" onClick="submitAction('addIpAddress');"></td>
																			<td><input type="button" name="ignore" value="Remove"
																				class="button"
																				onClick="submitAction('removeIpAddressNone');"></td>
																			<td><input type="button" name="ignore" value="Cancel"
																				class="button" onClick="hideCreateSection('createButton', 'newButton', 'createSection');"></td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
																	<td colspan="4" style="padding-left: 5px;">
																		<fieldset style="background-color: #edf5ff">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr style="padding-top: 5px;">
																				<td class="labelT1" width="150"><label><s:text
																					name="config.radiusOnHiveAp.ipName" /><font color="red"><s:text name="*"/></font></label></td>
																				<td>
																					<ah:createOrSelect divId="errorDisplay" swidth="193px"
																						list="availableIpAddress" typeString="IpAddress"
																						selectIdName="myIpSelect"
																						inputValueName="inputIpValue" />
																				</td>
																			</tr>
																			<tr>
																				<td class="labelT1"><label><s:text
																					name="config.radiusAssign.secret" /><font color="red"><s:text name="*"/></font></label></td>
																				<td><s:password size="32" id="sharekey" name="sharekey" onkeypress="return hm.util.keyPressPermit(event,'password');"
																	                maxlength="31" showPassword="true" value="%{sharekey}" /><s:textfield id="sharekey_text" name="sharekey" maxlength="31" size="32" cssStyle="display:none"
																					onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true" />
																					<s:text name="config.radiusOnHiveAp.shareRange" /></td>
																			</tr>
																			<tr>
																				<td class="labelT1"><label><s:text
																					name="config.radiusAssign.confirmSecret" /><font color="red"><s:text name="*"/></font></label></td>
																				<td><s:password size="32" id="sharekeyConf" onkeypress="return hm.util.keyPressPermit(event,'password');"
														                			maxlength="31" showPassword="true" value="%{sharekey}" /><s:textfield id="sharekeyConf_text" maxlength="31" size="32" cssStyle="display:none" 
														                			disabled="true" onkeypress="return hm.util.keyPressPermit(event,'password');" />
																					<s:text name="config.radiusOnHiveAp.shareRange" /></td>
																			</tr>
																			<tr>
																				<td>&nbsp;</td>
																				<td><s:checkbox id="chkShareKeyDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword
														                			(this.checked,['sharekey','sharekeyConf'],['sharekey_text','sharekeyConf_text']);" />
														                			<s:text name="config.radiusAssign.secret.obscure" /></td>
																			</tr>
																			<tr>
																				<td class="labelT1"><label><s:text name="config.radiusAssign.description" /></label></td>
																				<td><s:textfield size="38" name="description" maxlength="64" value="%{description}"/>
																					<s:text name="config.radiusOnHiveAp.desRange" /></td>
																			</tr>
																		</table>
																		</fieldset>
																	</td>
																</tr>
																<tr id="headerSection">
																	<th align="left" style="padding-left: 0;" width="10"><input
																		type="checkbox" id="checkAll"
																		onClick="toggleCheckAllRules(this, 'ipIndices');"></th>
																	<th align="left" width="200px"><s:text
																		name="config.radiusOnHiveAp.ipName" /></th>
																	<th align="left" width="150px"><s:text
																		name="config.radiusAssign.secret" /></th>
																	<th align="left" width="200px"><s:text
																		name="config.radiusAssign.description" /></th>
																</tr>
																<s:iterator value="%{dataSource.ipOrNames}" status="status">
																	<tr>
																		<td class="listCheck"><s:checkbox name="ipIndices"
																			fieldValue="%{#status.index}" /></td>
																		<td class="list">
																			<s:property value="ipAddress.addressName" />
																		</td>
																		<td class="list" valign="middle">
																			<s:password id="shareKey_%{#status.index}" name="sharedSecrets" onkeypress="return hm.util.keyPressPermit(event,'password');" 
																               	maxlength="31" showPassword="true" value="%{sharedKey}" size="24" /><s:textfield id="shareKey_%{#status.index}_text" name="sharedSecrets" maxlength="31" cssStyle="display:none"
																				onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true" size="24" /><br><s:checkbox id="chkPasswordDisplay_%{#status.index}" name="ignore" value="true" onclick="hm.util.toggleObscurePassword
																                (this.checked,['shareKey_%{#status.index}'],['shareKey_%{#status.index}_text']);" />
																                <s:text name="config.radiusAssign.secret.obscure" />
																		</td>
																		<td class="list"><s:textfield size="30" name="descriptions"
																			value="%{description}" maxlength="64" /></td>
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
															</td>
														</tr>
													</table>
												</div>
											</td>
										</tr>
									</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				</table>
			</td>
		</tr>
	</table>
	<s:if test="%{jsonMode == true}">
		</div>
	</s:if>
</s:form></div>
