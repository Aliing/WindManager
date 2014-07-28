<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div id="deviceWifiClientModeSettings">
	<s:if test="%{dataSource.deviceInfo.sptWifiClientMode}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.wifiClientMode.label" />','wifiClientModeSettings');</script></td>
		</tr>
		<tr>
			<td>
			  <div id="wifiClientModeSettings"  style="display: <s:property value="%{dataSource.wifiClientModeDisplayStyle}"/>">
			  		<table cellspacing="0" cellpadding="0" border="0">
			  			<tr id="wifiWanSetting">
			  				<td>
			  				   <fieldset><legend><s:text name="hiveAp.wifiClientMode.wansetting" /></legend>
			  				   	   <span><s:text name="hiveAp.wifiClientMode.choosewan" /></span>
				  				   <s:radio
											name="dataSource.wifiClientWan"
											list="#{'0':'wifi0','1':'wifi1'}" listKey="key"
											listValue="value" onchange="wifiClientWanChanged(this.value)" />
							   </fieldset>
			  				</td>
			  			</tr>
			  			<tr><td height="5px"></td></tr>
			  			<tr>
			  			  <td>
			  			    <fieldset><legend><s:text name="hiveAp.wifiClientMode.preferredssids" /></legend>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="4px"></td>
										</tr>
                                                          <tr>
                                                              <s:push value="%{preferredSsidOptions}">
                                                                  <td style="padding-left: 10px;"><tiles:insertDefinition
                                                                      name="optionsTransfer" /></td>
                                                              </s:push>
                                                          </tr>
									</table>
			  			 	</fieldset>
			  			  </td>
			  			</tr>
			  		</table>
			  </div>
			</td>
		</tr>
   </table>
   </s:if>
</div>