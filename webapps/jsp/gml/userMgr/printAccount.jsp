<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'temporaryAccount';

function onLoadPage() {
	window.print();
}
</script>
<div>
<s:form action="temporaryAccount" enctype="multipart/form-data"
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
				<table cellspacing="0" cellpadding="0" border="1" 
						width="360px" style="word-break:break-all">
					<s:iterator id="fields" value="%{templateFields}" status="status">
						<tr>
							<td style="padding: 1px 0 1px 4px" align="left" width="30%">
								<s:property value="%{label}"/>
							</td>
							<td style="padding: 1px 0 1px 4px" align="left">
								<s:property value="%{value}"/>&nbsp;
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