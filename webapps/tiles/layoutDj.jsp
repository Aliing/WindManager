<%--
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

  --%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<tiles:importAttribute name="leftWidth" scope="request" />

<script type="text/javascript">
function onLoadNotes() {
}
function onLoadPage() {
}
function onLoadEvent() {
//	alert("dojo version: " + dojo.version);
	onLoadNotes();
	onLoadPage();
//	@import "/struts/dojoroot/dijit/themes/tundra/tundra.css";
//	@import "/struts/dojoroot/dijit/themes/soria/soria.css";
}
function onUnloadNotes() {
}
function onUnloadPage() {
}
function onUnloadEvent() {
	onUnloadNotes();
	onUnloadPage();
}
</script>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Cache-Control" content="no-cache" />
<title><s:property value="%{selectedL2Feature.description}" /></title>
<script type="text/javascript"
	src="/struts/dojoroot/dojo/dojo.js"
	djConfig="parseOnLoad: true, isDebug: false">
</script>
<style type="text/css">
	@import "/struts/dojoroot/dojo/resources/dojo.css";
	@import "/struts/dojoroot/dijit/themes/nihilo/nihilo.css";
</style>
<style>
html, body {
	overflow: hidden;
}

/* Tree overrides */
#mapTree .dijitTreeContent {
    color: #003366;
    padding-bottom: 1px;  /* should be on IE only */
}

</style>
<script type="text/javascript">
	dojo.require("dojo.parser");
	dojo.require("dijit.layout.AccordionContainer");
	dojo.require("dijit.layout.LayoutContainer");
	dojo.require("dijit.layout.SplitContainer");
	dojo.require("dijit.layout.ContentPane");
	dojo.require("dojo.data.ItemFileReadStore");
	dojo.require("dijit.Tree");
</script>
<script src="<s:url value="/js/hm.util.js" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/fonts/fonts-min.css"/>" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/menu/assets/skins/sam/menu.css"/>" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/container/assets/skins/sam/container.css"/>" />
<link rel="shortcut icon" href="<s:url value="/images/favicon.ico"/>"
	type="image/x-icon" />
<link rel="stylesheet" href="<s:url value="/css/hm.css"/>"
	type="text/css" />
</head>
<body class="body_bg skin_hm yui-skin-sam tundra soria nihilo" onload="onLoadEvent()"
	onunload="onUnloadEvent()" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0">
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; padding: 0; margin: 0; border: 0;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top"><tiles:insertAttribute name="topPane" /></div>
	<div dojoType="dijit.layout.SplitContainer" orientation="horizontal" layoutAlign="client"
	                                            sizerWidth="7" activeSizing="true">
		<div dojoType="dijit.layout.AccordionContainer" style="background-color: #FFFFFF;"
                                                 sizeShare="1" sizeMin="${leftWidth}">
		<div dojoType="dijit.layout.AccordionPane" title="Map Hierarchy">
        <div dojoType="dijit.layout.ContentPane" >
			<script type="text/javascript" charset="utf-8">
				var data = {data: {  label: 'name',
						identifier: 'name',
						items: [
							{ name:'US', type:'category'},
							{ name:'Europe', type: 'category'},
							{ name:'Australia', type: 'category'}
						]
				}};
				var mapStore = new dojo.data.ItemFileReadStore(data);
			</script>
			<div dojoType="dijit.Tree" id="mapTree" store="mapStore" label="World">
				<script type="dojo/method" event="onClick" args="item">
                	//alert("Selected: " + mapStore.getLabel(item) + ".");
            	</script>
            </div>
		</div>
		</div>

        <div dojoType="dijit.layout.AccordionPane" title="Alerts">...</div>

		</div>

        <div dojoType="dijit.layout.ContentPane" title="Preview" layoutAlign="client"
             	sizeShare="9" style="padding-left: 4px; padding-top: 4px;">
        	<tiles:insertAttribute name="body" />
        </div>
	</div>
</div>
</body>
</html>
