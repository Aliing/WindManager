<%@taglib prefix="s" uri="/struts-tags"%>

<s:if test="%{jsonMode && vpnGateWayDlg}">
<div  class="topFixedTitle">
<table width="100%" border="0" cellspacing="0" cellpadding="0" >
	<tr>
		<td style="padding:10px 10px 10px 10px">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="100%">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-cvg.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<td class="dialogPanelTitle"><s:text name="config.vpnservice.config.vpngateway.title"/></td>
					</tr>
				</table>
				</td>
				<td align="right" width="120px">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="margin-right: 20px;" title="Cancel" onclick="submitAction('cancelDlg');"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Cancel</span></a></td>
						<!-- <td width="25px">&nbsp;</td> -->
						<td class="npcButton">
							<s:if test="%{writeDisabled == 'disabled'}">
								<a href="javascript:void(0);" class="btCurrent"  style="float: right;" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
							</s:if>
							<s:else>
								<a href="javascript:void(0);" class="btCurrent"  style="float: right;" onclick="submitAction('update2');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
							</s:else>
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
</s:if>

	