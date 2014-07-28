<%@ taglib prefix="s" uri="/struts-tags"%>

<style>
.alarmsLabel {
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 12px;
	color: #666666;
	font-weight: bold;
	line-height: 16px;
	padding-right:3px;
}
td.alarms {
	padding: 2px 3px 2px 3px;
	border-top: 1px solid #cccccc;
	border-bottom: 1px solid #cccccc;
	border-right: 1px solid #cccccc;
}
td.alarms a        { color: #000; text-decoration: none;}
td.alarms a:link   { color: #000; text-decoration: none;}
td.alarms a:hover  { color: #000; text-decoration: underline;}
td.alarms a:active { color: #000; text-decoration: none;}
</style>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td height="1" id="lss"></td>
	</tr>
	<tr>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td style="padding-bottom:2px;">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="alarmsLabel" nowrap><s:text name="geneva_08.device.list.newdevice.title" /></td>
						<td width="100%"></td>
						<td class="alarms" style="border-left: 1px solid #cccccc;"><a
							id="sc_nh"
							href="<s:url action="hiveAp" includeParams="none"><s:param name="operation" value="%{'view'}" /><s:param name="hmListType" value="%{'newHiveAps'}" /></s:url>"><s:property
							value="%{systemStatus.newHiveAPCount}" /></a></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td style="padding-bottom:2px;">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="alarmsLabel" nowrap>Rogue Clients</td>
						<td width="100%"></td>
						<td class="alarms" style="border-left: 1px solid #cccccc;"><a
							id="sc_rc"
							href="<s:url action="idp" includeParams="none"><s:param name="listType" value="%{'rogueClient'}" /></s:url>"><s:property
							value="%{systemStatus.rogueClientCount}" /></a></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td style="padding-bottom:2px;">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="alarmsLabel" nowrap>Rogue APs</td>
						<td>
						<table width="100%" height="16" border="0" cellspacing="0"
							cellpadding="0">
							<tr>
								<td class="statusNumber innet"><a id="ac_innet"
									title="<s:text name="idp.category.label.inNet"/>"
									href="<s:url action="idp" includeParams="none"><s:param name="operation" value="%{'switch'}" /><s:param name="listType" value="%{'rogueAps'}" /><s:param name="category" value="%{'innet'}" /></s:url>"><s:property
									value="%{innetRogueCount}" /></a></td>
								<td class="statusNumber onmap"><a id="ac_onmap"
									title="<s:text name="idp.category.label.onMap"/>"
									href="<s:url action="idp" includeParams="none"><s:param name="operation" value="%{'switch'}" /><s:param name="listType" value="%{'rogueAps'}" /><s:param name="category" value="%{'onmap'}" /></s:url>"><s:property
									value="%{onmapRogueCount}" /></a></td>
								<td class="statusNumber strong"><a id="ac_strong"
									title="<s:text name="idp.category.label.strong"/>"
									href="<s:url action="idp" includeParams="none"><s:param name="operation" value="%{'switch'}" /><s:param name="listType" value="%{'rogueAps'}" /><s:param name="category" value="%{'strong'}" /></s:url>"><s:property
									value="%{strongRogueCount}" /></a></td>
								<td class="statusNumber weak"><a id="ac_weak"
									title="<s:text name="idp.category.label.weak"/>"
									href="<s:url action="idp" includeParams="none"><s:param name="operation" value="%{'switch'}" /><s:param name="listType" value="%{'rogueAps'}" /><s:param name="category" value="%{'weak'}" /></s:url>"><s:property
									value="%{weakRogueCount}" /></a></td>
							</tr>
						</table>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="alarmsLabel" nowrap>Alarms</td>
						<td width="100%"></td>
						<td class="statusNumber severity5"><a id="ac_cr"
							title="<s:text name="alarm.severity.critical"/>"
							href="<s:url action="alarms" includeParams="none"><s:param name="operation" value="%{'search'}" /><s:param name="severity" value="%{@com.ah.bo.monitor.AhAlarm@AH_SEVERITY_CRITICAL}" /></s:url>"><s:property
							value="%{criticalAlarmCount}" /></a></td>
						<td class="statusNumber severity4"><a id="ac_ma"
							title="<s:text name="alarm.severity.major"/>"
							href="<s:url action="alarms" includeParams="none"><s:param name="operation" value="%{'search'}" /><s:param name="severity" value="%{@com.ah.bo.monitor.AhAlarm@AH_SEVERITY_MAJOR}" /></s:url>"><s:property
							value="%{majorAlarmCount}" /></a></td>
						<td class="statusNumber severity3"><a id="ac_mi"
							title="<s:text name="alarm.severity.minor"/>"
							href="<s:url action="alarms" includeParams="none"><s:param name="operation" value="%{'search'}" /><s:param name="severity" value="%{@com.ah.bo.monitor.AhAlarm@AH_SEVERITY_MINOR}" /></s:url>"><s:property
							value="%{minorAlarmCount}" /></a></td>
						<td class="statusNumber severity1"><a id="ac_cl"
							title="<s:text name="alarm.severity.cleared"/>"
							href="<s:url action="alarms" includeParams="none"><s:param name="operation" value="%{'search'}" /><s:param name="severity" value="%{@com.ah.bo.monitor.AhAlarm@AH_SEVERITY_UNDETERMINED}" /></s:url>"><s:property
							value="%{clearedAlarmCount}" /></a></td>
			</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td height="1" id="lse"></td>
	</tr>
</table>
