<%@taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
<script>
function pushIt() {
	top.pushEvent(<s:property value="%{eventCount}"/>);
}
</script>
</head>
<body onload="pushIt()">
<s:form name="f1" action="push">
	<input type="hidden" name="operation" value="alarms">
	<input type="hidden" name="hostName">
</s:form>
</body>
</html>
