<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div>
	<s:if test="%{dataSource.deviceInfo.sptEthernetMore_24}">
	<script>
		$('#portGroupSection').portsConfig();
	</script>
	<div id="switchPortView">
		<tiles:insertDefinition name="portInterface" />
		<tiles:insertDefinition name="portInterfaceScript" />
	</div>
	</s:if>
</div>