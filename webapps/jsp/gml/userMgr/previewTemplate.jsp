<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'printTemplate';

</script>
<div>
<s:form action="printTemplate" enctype="multipart/form-data"
		method="POST">
	<s:hidden name="operation" />
	<div>
		<s:property value="headerHTML" escape="false"/>
	</div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="15"></td>
		</tr>
		<tr>
			<td>
				<table cellspacing="0" cellpadding="0" border="1" width="360px">
					<s:iterator id="fields" value="%{fields}" status="status">
						<tr>
							<td style="padding: 2px 0 2px 4px" align="left" width="30%">
								<s:property value="%{label}"/>
							</td>
							<td style="padding: 2px 0 2px 4px" align="left">
								<s:property value="%{label}"/>_Test
							</td>
						</tr>
					</s:iterator>
				</table>
			</td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
	</table>
	<div>
		<s:property value="footerHTML" escape="false"/>
	</div>

</s:form>
</div>