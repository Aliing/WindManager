<%@ taglib prefix="s" uri="/struts-tags"%>

<style>
.statusBg {
	background-image: url(<s:url value="/images/hm/nav-silver-bkg.gif" includeParams="none"/>);
	background-repeat: repeat-x;
}

.statusLabel {
	background-image: url(<s:url value="/images/hm/nav-silver-bkg.gif" includeParams="none"/>);
	background-repeat: repeat-x;
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 11px;
	font-style: normal;
	font-weight: bold;
	color: #002D56;
	padding-left: 5px;
}

.statusNumber {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 11px;
	font-style: normal;
	font-weight: normal;
	padding: 0 3px 0 3px;
	background-color: white;
	color: #000000;
	text-align: center;
	border: 1px solid #cccccc;
}

td.statusNumber a {
	color: #000;
	text-decoration: none;
}

td.statusNumber a:link {
	color: #000;
	text-decoration: none;
}

td.statusNumber a:hover {
	color: #000;
	text-decoration: underline;
}

td.statusNumber a:active {
	color: #000;
	text-decoration: none;
}
</style>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td height="1" id="lss"></td>
	</tr>
	<%-- if the license is invalid, not show the section --%>
	<s:if test="%{showHiveApInfo}">
	<s:if test="%{showStatusNewDevicesInfo}">
	<tr>
		<td class="statusBg">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="5"><img
					src="<s:url value="/images/hm/nav-silver-border-left.png" includeParams="none"/>"
					width="5" height="25" alt="" class="dblk"></td>
				<td class="statusLabel" width="100%"><s:text name="geneva_08.device.list.newdevice.title" /></td>
				<td>
				<table width="100%" height="16" border="0" cellspacing="0"
					cellpadding="0">
					<tr>
						<td class="statusNumber newHiveAP"><a id="sc_nh"
							href="<s:url action="hiveAp" includeParams="none"><s:param name="operation" value="%{'view'}" /><s:param name="hmListType" value="%{'managedHiveAps'}" /><s:param name="isNew" value="%{'true'}" /></s:url>"><s:property
							value="%{newHiveAPCount}" /></a></td>
					</tr>
				</table>
				</td>
				<td width="5"><img
					src="<s:url value="/images/hm/nav-silver-border.gif" includeParams="none"/>"
					width="5" height="25" alt="" class="dblk"></td>
			</tr>
		</table>
		</td>
	</tr>
	</s:if>
	<s:if test="%{showStatusRogueClientsInfo}">
	<tr>
		<td class="statusBg">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="5"><img
					src="<s:url value="/images/hm/nav-silver-border-left.png" includeParams="none"/>"
					width="5" height="25" alt="" class="dblk"></td>
				<td class="statusLabel" width="100%">Rogue Clients</td>
				<td>
				<table width="100%" height="16" border="0" cellspacing="0"
					cellpadding="0">
					<tr>
						<td class="statusNumber"><a id="sc_rc"
							href="<s:url action="idp" includeParams="none"><s:param name="listType" value="%{'rogueClient'}" /></s:url>"><s:property
							value="%{rogueClientCount}" /></a></td>
					</tr>
				</table>
				</td>
				<td width="5"><img
					src="<s:url value="/images/hm/nav-silver-border.gif" includeParams="none"/>"
					width="5" height="25" alt="" class="dblk"></td>
			</tr>
		</table>
		</td>
	</tr>
	</s:if>
	<s:if test="%{showStatusRogueApsInfo}">
	<tr>
		<td class="statusBg">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="5"><img
					src="<s:url value="/images/hm/nav-silver-border-left.png" includeParams="none"/>"
					width="5" height="25" alt="" class="dblk"></td>
				<td class="statusLabel" width="100%" nowrap>Rogue APs</td>
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
				</td>
				<td width="5"><img
					src="<s:url value="/images/hm/nav-silver-border.gif" includeParams="none"/>"
					width="5" height="25" alt="" class="dblk"></td>
			</tr>
		</table>
		</td>
	</tr>
	</s:if>
	<s:if test="%{showStatusAlarmsInfo}">
	<tr>
		<td class="statusBg">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="5"><img
					src="<s:url value="/images/hm/nav-silver-border-left.png" includeParams="none"/>"
					width="5" height="25" alt="" class="dblk"></td>
				<td class="statusLabel" width="100%" nowrap>Alarms</td>
				<td>
				<table width="100%" height="16" border="0" cellspacing="0"
					cellpadding="0">
					<tr>
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
				<td width="5"><img
					src="<s:url value="/images/hm/nav-silver-border.gif" includeParams="none"/>"
					width="5" height="25" alt="" class="dblk"></td>
			</tr>
		</table>
		</td>
	</tr>
	</s:if>
	</s:if>
	<tr>
		<td height="1" id="lse"></td>
	</tr>
</table>
