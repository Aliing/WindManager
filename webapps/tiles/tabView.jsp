<%@taglib prefix="s" uri="/struts-tags"%>

<!-- Sam Skin CSS for TabView -->
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/tabview/assets/skins/sam/tabview.css" includeParams="none" />" />
 
<!-- JavaScript Dependencies for Tabview: -->
<script src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" includeParams="none" />"></script>
<script src="<s:url value="/yui/element/element-min.js" includeParams="none" />"></script>
 
<!-- OPTIONAL: Connection (required for dynamic loading of data) -->
<script src="<s:url value="/yui/connection/connection-min.js" includeParams="none" />"></script>
 
<!-- Source file for TabView -->
<script src="<s:url value="/yui/tabview/tabview-min.js" includeParams="none" />"></script>

<%-- Style overrides --%>
<style>
.yui-skin-sam .yui-navset .yui-content {
	background:#FFFFFF;
}
</style>
