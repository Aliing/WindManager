<%@taglib prefix="s" uri="/struts-tags"%>
<script>

<s:if test="%{dataSource.id == null}">
var operation='create2';
</s:if>
<s:else>
var operation='update2<s:property value="lstForward"/>';
</s:else>
</script>
<div>
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<s:if test="%{dataSource.id == null}">
				<td><input type="button" name="ignore"
					value="<s:text name="button.create"/>" class="button"
					onClick="submitAction('create2');"
					<s:property value="writeDisabled" />></td>
			</s:if>
			<s:else>
				<td><input type="button" name="ignore"
					value="<s:text name="button.update"/>" class="button"
					onClick="submitAction('update2');"
					<s:property value="writeDisabled" />></td>
			</s:else>
			<td><input type="button" name="ignore" value="Cancel"
				class="button" onClick="submitAction('cancel');"></td>
		</tr>
	</table>
</div>