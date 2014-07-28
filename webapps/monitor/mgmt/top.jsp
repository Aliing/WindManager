<%@taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><s:text name="hiveAp.tag" /> Mgmt</title>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/struts/ext/resources/css/ext-all.css"/>" />

<%--
<link rel="stylesheet" type="text/css"
	href="<s:url value="/struts/ext/resources/css/xtheme-gray.css"/>" />
  --%>
  
<script type="text/javascript"
	src="<s:url value="/struts/ext/adapter/ext/ext-base.js"/>"></script>

<script type="text/javascript"
	src="<s:url value="/struts/ext/ext-all.js"/>"></script>
<style type="text/css">
html, body {
	font:normal 12px verdana;
	margin:0;
	padding:0;
	border:0 none;
	overflow:hidden;
	height:100%;
}
.top_nav_bg {
	background-image: url(../images/hm/header-blue-bkg.gif);
	background-repeat: repeat-x;
}
.settings {
	background-image:url(<s:url value="/struts/ext/examples/shared/icons/fam/folder_wrench.png"/>);
}
.nav {
	background-image:url(<s:url value="/struts/ext/examples/shared/icons/fam/folder_go.png"/>);
}

#menu-panel .x-panel {
	margin-bottom:3px;
	margin-right:0;
}
#menu-panel .x-panel-body {
	border:0 none;
}
#menu-panel .x-panel-body li {
	margin:3px;	
}
#menu-panel .x-panel-body li img {
	width:16px;
	height:16px;
	vertical-align:middle;
	margin-right:2px;
	margin-bottom:2px;
}
#menu-panel .x-panel-body li a {
	text-decoration:none;
	color:#3764A0;
}
#menu-panel .x-plain-body {
//	background-color:#cad9ec;
	background-color:#E1E1E1;
    padding:5px 0 0 5px;
}
#menu-panel .x-panel-body li a:hover {
	text-decoration:underline;
	color:#15428b;
}

.x-panel-trans {
	background:transparent;
}

.x-layout-split-west {
	cursor:move;
//    background-color:#cad9ec;
	background-color:#E1E1E1;
}
.x-panel-header-text {
	color:#3764A0;
}
.ip-address {
    background-image:url(<s:url value="/struts/ext/examples/tasks/images/icon-show-all.gif"/>) !important;
}
.mac-address {
    background-image:url(<s:url value="/struts/ext/examples/tasks/images/icon-all.gif"/>) !important;
}
.net-services {
    background-image:url(<s:url value="/struts/ext/examples/tasks/images/icon-active.gif"/>) !important;
}
.ethernet-profiles {
    background-image:url(<s:url value="/struts/ext/examples/tasks/images/icon-show-complete.gif"/>) !important;
}
.radio-profiles {
    background-image:url(<s:url value="/struts/ext/examples/tasks/images/icon-by-date.gif"/>) !important;
}
.markers-classifiers {
    background-image:url(<s:url value="/struts/ext/examples/tasks/images/edit.gif"/>) !important;
}
.classifier-maps {
    background-image:url(<s:url value="/struts/ext/examples/tasks/images/delete.gif"/>) !important;
}
.marker-maps {
    background-image:url(<s:url value="/struts/ext/examples/tasks/images/icon-by-category.gif"/>) !important;
}
.rate-control {
    background-image:url(<s:url value="/struts/ext/examples/tasks/images/icon-no-group.gif"/>) !important;
}
</style>
<script type="text/javascript">
Ext.BLANK_IMAGE_URL = '<s:url value="/struts/ext/resources/images/default/s.gif"/>';

Ext.onReady(layout);
    
function layout() {
	Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

    var netObjectsActions = new Ext.Panel({
    	frame:true,
    	title: 'Network Objects',
    	collapsible:true,
    	contentEl:'net-objects',
    	titleCollapse: true
    });

    var qosActions = new Ext.Panel({
    	frame:true,
    	title: 'QoS Policies',
    	collapsible:true,
    	contentEl:'qos-policies',
    	titleCollapse: true
    });
    
    var menuPanel = new Ext.Panel({
    	id:'menu-panel',
    	region:'west',
    	split:true,
    	collapsible: true,
    	collapseMode: 'mini',
    	width:200,
    	minWidth: 160,
    	maxWidth: 170,
    	border: false,
    	baseCls:'x-plain',
    	items: [netObjectsActions, qosActions]
    });
    
	var viewport = new Ext.Viewport({
            layout:'border',
//	    	style:'background-color: #cad9ec;',
	    	style:'background-color: #E1E1E1;',	    	
            items:[
                new Ext.BoxComponent({ // raw
                    region:'north',                    
                    el: 'north',
                    height:55,
                    margins: '0 0 0 0'
                }), menuPanel, {
                    region:'east',
                    id:'west-panel',
                    title:'Configuration',
                    titleCollapse: true,
                    split:true,
                    width: 200,
                    minSize: 160,
                    maxSize: 300,
                    collapsible: true,
                    collapseMode: 'mini',
                    margins:'5 0 0 0',
                    layout:'accordion',
                    layoutConfig:{
                        animate:true
                    },
                    items: [{
                        contentEl: 'west',
                        title:'Network Objects',
                        border:false,
                        iconCls:'nav'
                    },{
                        title:'Settings',
                        html:'<p>Settings here.</p>',
                        border:false,
                        iconCls:'settings'
                    }]
                },
                new Ext.TabPanel({
                    region:'center',
                    deferredRender:false,
                    activeTab:0,
                    margins:'5 0 0 0',
                    items:[{
                        contentEl:'center1',
                        title: 'General',
                        closable:false,
                        autoScroll:true
                    },{
                        contentEl:'center2',
                        title: 'Advanced',
                        autoScroll:true
                    }]
                })
             ]
	});
}
</script>
</head>
<body>
<div id="west">
<p>...</p>
</div>
<div id="north">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="top_nav_bg">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><img src="<s:url value="/images/company_logo.png"/>"
					width="128" height="55" alt="" class="dblk"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
<div id="center1">... general info ...
<div id="container"></div>
</div>
<div id="center2">... advanced info ...</div>
<ul id="net-objects" class="x-hidden">
	<li><img
		src="<s:url value="/struts/ext/resources/images/default/s.gif"/>"
		class="ip-address" /> <a id="view-all" href="#">IP Addresses</a></li>
	<li><img
		src="<s:url value="/struts/ext/resources/images/default/s.gif"/>"
		class="mac-address" /> <a id="view-active" href="#">MAC
	Adress/OUIs</a></li>
	<li><img
		src="<s:url value="/struts/ext/resources/images/default/s.gif"/>"
		class="net-services" /> <a id="view-complete" href="#">Network
	Services</a></li>
	<li><img
		src="<s:url value="/struts/ext/resources/images/default/s.gif"/>"
		class="ethernet-profiles" /> <a id="view-complete" href="#">Ethernet
	Profiles</a></li>
	<li><img
		src="<s:url value="/struts/ext/resources/images/default/s.gif"/>"
		class="radio-profiles" /> <a id="view-complete" href="#">Radio
	Profiles</a></li>
</ul>
<ul id="qos-policies" class="x-hidden">
	<li><img
		src="<s:url value="/struts/ext/resources/images/default/s.gif"/>"
		class="markers-classifiers" /> <a id="view-all" href="#">Classifiers
	and Markers</a></li>
	<li><img
		src="<s:url value="/struts/ext/resources/images/default/s.gif"/>"
		class="classifier-maps" /> <a id="view-active" href="#">Classifier
	Maps</a></li>
	<li><img
		src="<s:url value="/struts/ext/resources/images/default/s.gif"/>"
		class="marker-maps" /> <a id="view-complete" href="#">Marker Maps</a></li>
	<li><img
		src="<s:url value="/struts/ext/resources/images/default/s.gif"/>"
		class="rate-control" /> <a id="view-complete" href="#">Rate
	Control and Queuing</a></li>
</ul>
</body>
</html>
