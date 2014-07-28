<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div>
<s:if test="%{dataSource.deviceInfo.sptEthernetMore_24}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.switchSettings.switchQos.settings.lable" />','switchQosSettingDiv');</script></td>
		</tr>
		<tr>
			<td style="padding: 0px 0px 0px 15px;">
				<div id="switchQosSettingDiv" style="display: <s:property value="%{dataSource.switchQosSettingDivStyle}"/>">
					<table cellspacing="0" cellpadding="0" border="0" >
						<tr id="switchQosSettingDetailDiv">
							<td style="padding: 5px 0px 0px 20px;"><s:checkbox
								name="dataSource.enableSwitchQosSettings"
								value="%{dataSource.enableSwitchQosSettings}"></s:checkbox>
								<span><s:text
								name="config.switchSettings.switchQos.settings.enable" /></span>
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
</s:if>
</div>