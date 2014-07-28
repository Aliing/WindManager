<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<s:if test="%{hMOnline}">
	<tiles:insertDefinition name="deviceInventoryListOnline" />
</s:if>
<s:else>
	<tiles:insertDefinition name="deviceInventoryListOnPremise" />
</s:else>