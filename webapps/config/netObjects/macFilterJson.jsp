<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=7;FF=3;OtherUA=4" />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="Cache-Control" content="no-cache" />

		<!--CSS file (default YUI Sam Skin) -->
		<link rel="stylesheet" type="text/css" 
			href="<s:url value="/yui/container/assets/skins/sam/container.css" includeParams="none"/>" />
		<link type="text/css" rel="stylesheet"
			href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css" includeParams="none"/>"></link>
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/assets/skins/sam/resize.css" includeParams="none"/>"></link>
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/assets/skins/sam/layout.css" includeParams="none"/>"></link>
		<link rel="stylesheet" type="text/css"
			href="<s:url value="/yui/fonts/fonts-min.css"  includeParams="none"/>" />
		<link rel="stylesheet"
			href="<s:url value="/css/hm.css" includeParams="none"/>"
			type="text/css" />
		<!-- Dependencies -->
		<script type="text/javascript"
			src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" includeParams="none"/>"></script>
		<script type="text/javascript"
			src="<s:url value="/yui/element/element-beta-min.js" includeParams="none"/>"></script>
		<script type="text/javascript"
			src="<s:url value="/yui/datasource/datasource-min.js" includeParams="none"/>"></script>

		<!-- OPTIONAL: Connection Manager (enables XHR for DataSource) -->
		<script type="text/javascript"
			src="<s:url value="/yui/connection/connection-min.js" includeParams="none"/>"></script>

		<!-- OPTIONAL: Drag Drop (enables resizeable or reorderable columns) -->
		<script type="text/javascript"
			src="<s:url value="/yui/dragdrop/dragdrop-min.js" includeParams="none"/>"></script>

		<!-- Optional Resize Support -->
		<script type="text/javascript"
			src="<s:url value="/yui/resize/resize-min.js" includeParams="none"/>"></script>
		<!-- Source files -->
		<script type="text/javascript"
			src="<s:url value="/yui/datatable/datatable-min.js" includeParams="none"/>"></script>
		<script type="text/javascript"
			src="<s:url value="/yui/layout/layout-min.js" includeParams="none"/>"></script>

		<script type="text/javascript" 
			src="<s:url value="/js/hm.util.js" includeParams="none"/>"></script>
		
		<script type="text/javascript" 
			src="<s:url value="/yui/container/container-min.js" includeParams="none" />"></script>
			
		<script src="<s:url value="/yui/animation/animation-min.js" />"></script>

<style type="text/css">
/* custom styles */
.yui-skin-sam .yui-dt-liner {
	white-space:nowrap;
}
.yui-skin-sam .yui-dt-scrollable .yui-dt-hd, .yui-skin-sam .yui-dt-scrollable .yui-dt-bd {
	border:medium none;
}
html {
	overflow: auto;
}
.npcButton a span{
	font-weight: bold;
	cursor: pointer;
	color: #fff;
	margin: 0 10px 0 -10px;
	padding: 3px 8px 5px 18px;
    position: relative; /*To fix IE6 problem (not displaying)*/
}

.npcButton a.btCurrent{
    background: url(/hm/images/hm_v2/hm-button-bkg.png) no-repeat center right;
    text-decoration:none;
    float: left;
    cursor: pointer;
}
 
.npcButton a.btCurrent span {
    background: url(/hm/images/hm_v2/hm-button-bkg.png) no-repeat center left;
    float: left;
    cursor: pointer;
}
body {
    background-color: #f9f9f7;
}
</style>

<script>
function onLoadIpDosEvent() {
	<s:if test="%{id != null}">
		//parent.closeIFrameDialog();
	//	top.fetchConfigTemplate2Page(true);
	</s:if>
}
</script>

</head>

<body  leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="onLoadIpDosEvent();">
	<tiles:insertDefinition name="macFilterDetail" />
</body>
</html>
