<%@ taglib prefix="s" uri="/struts-tags"%>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr id="portsOptionalTr">
		<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.configTemplate.optionalSettingsTitle" />','portsOptionalSettings');</script></td>
	</tr>
	<tr>
		<td>
		<div id="portsOptionalSettings" style="display: none;">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td height="10"></td>
				</tr>
				<tr>
				    <td style="padding-left: 15px;">
					    <s:checkbox id="enabledClientReport" name="dataSource.enabledClientReport"/>
					    <label for="enabledClientReport"><s:text name="config.port.access.clientReport.note" /></label>
				    </td>
				</tr>
			</table>
		</div>
		</td>
	</tr>
</table>

