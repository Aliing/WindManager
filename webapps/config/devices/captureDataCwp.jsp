<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div id="captureDataCwpSettings">
<script type="text/javascript">
function enabledOverrideCaptureDataCheckBox(value){
	if (value) {
		document.getElementById("global_captureDataCwp").style.display= "";
	} else {
		document.getElementById("global_captureDataCwp").style.display= "none";
	}
}
</script>

	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="geneva_09.hiveap.captureDataCwp.settings" />','captureDataCwpDiv');</script></td>
		</tr>
		<tr>
			<td>
				<table id="captureDataCwpDiv" style="padding-left: 15px;display: <s:property value="%{dataSource.captureDataCwpDivStyle}"/>" cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td>
							<s:checkbox onclick="enabledOverrideCaptureDataCheckBox(this.checked);"
								name="dataSource.overrideCaptureDataByCWP"
								style="margin-bottom:7px;" /> <s:text
								name="geneva_09.hiveap.captureDataCwp.settings.override"></s:text>
						</td>
					</tr>
					<tr>
					<td id="global_captureDataCwp" style="padding-left: 22px;display: <s:property value="%{enabledOverrideCaptureDataMode}"/>">
						<table width="100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
											<table width="100%" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td style="padding-left: 22px;">
														<table width="100%" cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1">
																	<s:checkbox
																		name="dataSource.enableCaptureDataByCWP"
																		style="margin-bottom:7px;" /> <s:text
																		name="geneva_09.hiveap.captureDataCwp.capture.data"></s:text>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
									</div>
								</td>
							</tr>
						</table>
					</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
