<%@taglib prefix="s" uri="/struts-tags"%>

<s:if test="%{#status.even}">
	<s:if test="%{selected}">
		<s:set name="rowClass" value="%{'evenSelected'}" />
	</s:if>
	<s:else>
		<s:set name="rowClass" value="%{'even'}" />
	</s:else>
</s:if>
<s:else>
	<s:if test="%{selected}">
		<s:set name="rowClass" value="%{'oddSelected'}" />
	</s:if>
	<s:else>
		<s:set name="rowClass" value="%{'odd'}" />
	</s:else>
</s:else>
