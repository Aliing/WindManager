<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/assets/skins/sam/layout.css"  includeParams="none"/>?v=<s:property value="verParam" />"></link>
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/assets/skins/sam/resize.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>

<style>
<!--
#devices, #filters{
	background-color: #fff;
	height: 100%;
}
#devices_content, #filters_content{
	overflow: auto;
}
#layoutDiv div.box_title{
	background-color: #EEE;
	padding: 2px;
	font-weight: bold;
	color: #4F4F4F;
	height: 25px;
	line-height: 23px;
}
.yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd-nohd{
	border-color: #DEDCD5;
}
-->
</style>

<!-- Source files -->
<script type="text/javascript" src="<s:url value="/yui/resize/resize-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script type="text/javascript" src="<s:url value="/yui/layout/layout-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script type="text/javascript">
<!--
var monitorPane = (function(){
	var allData = null;
	var ht = 0, ot = 0;
	var layout = null;
	var initializeMonitorPane = function(height){
		ht = height;
		createLayout();
		createTree();
		return allData;
	};

	var createLayout = function(){
	   	layout = new YAHOO.widget.Layout("layoutDiv",{
			units: [
					{ position: 'top', height: 240, maxHeight: 320, minHeight: 50, resize: true, body: 'devices', gutter: '0 0 5px 0' },
					{ position: 'center', body: 'filters', gutter: '0 0 1px 0' }
			]
		});
    	function resizeSection(){
    	   	var devices_title_size = YAHOO.util.Dom.getRegion(YAHOO.util.Dom.getPreviousSibling("devices_content"));
    	   	var filters_title_size = YAHOO.util.Dom.getRegion(YAHOO.util.Dom.getPreviousSibling("filters_content"));
   			YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('devices_content'),'height',(this.getSizes().top.h - devices_title_size.height - 6) + 'px');
   			YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('filters_content'),'height',(this.getSizes().center.h - filters_title_size.height - 6) + 'px');
   		}

	   	function initLayout(){
	   		var lss = YAHOO.util.Dom.getY("lss");
	   		var lse = YAHOO.util.Dom.getY("lse");
	   		var layoutDivY = YAHOO.util.Dom.getY("layoutDiv");
	   		var vpHeight = YAHOO.util.Dom.getViewportHeight();
	   		ot = layoutDivY + (lse - lss);
	   		var height = ht ? ht : vpHeight - ot;
			layout.set("height", (height-2));
			layout.set("width", 200);
			layout.getUnitByPosition("top").set("height", height*3/5);
	   	}
		 //On resize
		layout.on('resize', resizeSection, layout, true);
		layout.on('render', initLayout, layout, true);
		layout.render();
	};

   	var resizeLayout = function(height){
   		var vpHeight = YAHOO.util.Dom.getViewportHeight();
   		var h = (height > 0) ? height : (vpHeight - ot);
   		h = (h > 0) ? h : 0;
		layout.set("height", (h-2));
		layout.getUnitByPosition("top").set("height", h*3/5);
   	};

   	var resizeWidth = function(width){
   		layout.set("width", width);
   		layout.resize();
   	};
	
	var createTree = function(){
		//eval("var data = " + "<s:property value="topyGroupJson"/>");
		var data2 =<s:property escape="false" value="filterGroupJson"/>;
		var data3 =<s:property escape="false" value="filterGroupSecondJson"/>;
		//hm.da.createTree("<s:property value="dataSource.currentDashMapTreeId"/>", "da_div_group_topy_tree", data);
		hm.da.createTree("<s:property value="dataSource.currentDashMapFilterId" escapeHtml="false"/>", "da_div_group_filter_tree", data2);
		hm.da.createTree("<s:property value="dataSource.currentDashMapFilterSecondId" escapeHtml="false"/>", "da_div_group_filter_userpro_tree", data3);
		allData = [data2, data3];
	};

	return {
		init: initializeMonitorPane,
		resizeWidth: resizeWidth,
		resizeLayout: resizeLayout
	};
})();
//-->
</script>
<div id="layoutDiv">
	<div id="devices">
		<div class="box_title"><s:text name="hm.dashboard.tree.filter.group" /></div>
		<div id="devices_content">
			<div id="da_div_group_topy_tree"></div>
			<div id="da_div_group_filter_tree"></div>
		</div>
	</div>
	<div id="filters">
		<div class="box_title"><s:text name="hm.dashboard.tree.filter.title" /></div>
		<div id="filters_content">
			<div id="da_div_group_filter_userpro_tree"></div>
		</div>
	</div>
</div>