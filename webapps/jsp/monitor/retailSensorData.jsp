<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script type="text/javascript">
	function changeTotalClients(type) {
		if (type == 1) {
			$("#minuteClients").show();
			$("#minuteBandWidths").show();
			$("#hourClients").hide();
			$("#hourBandWidths").hide();
			$("#dayClients").hide();
			$("#dayBandWidths").hide();
			$("#refreshTip").html('<s:text name="presence.retail.analytics.monitor.minute.refresh.tip" />');
		} else if (type == 2) {
			$("#minuteClients").hide();
			$("#minuteBandWidths").hide();
			$("#hourClients").show();
			$("#hourBandWidths").show();
			$("#dayClients").hide();
			$("#dayBandWidths").hide();
			$("#refreshTip").html('<s:text name="presence.retail.analytics.monitor.hour.refresh.tip" />');
		} else {
			$("#minuteClients").hide();
			$("#minuteBandWidths").hide();
			$("#hourClients").hide();
			$("#hourBandWidths").hide();
			$("#dayClients").show();
			$("#dayBandWidths").show();
			$("#refreshTip").html('<s:text name="presence.retail.analytics.monitor.day.refresh.tip" />');
		}
	}

	function operateDeviceStyle(type, index) {
		var obj;
		var showImg;
		var hideImg;
		if (type == 1) {
			obj = document.getElementById("minuteDeviceName_" + index);
			showImg = document.getElementById("minuteShowImg_" + index);
			hideImg = document.getElementById("minuteHideImg_" + index);

		} else if (type == 2) {
			obj = document.getElementById("hourDeviceName_" + index);
			showImg = document.getElementById("hourShowImg_" + index);
			hideImg = document.getElementById("hourHideImg_" + index);
		} else if (type == 3) {
			obj = document.getElementById("dayDeviceName_" + index);
			showImg = document.getElementById("dayShowImg_" + index);
			hideImg = document.getElementById("dayHideImg_" + index);
		}
		if (null == obj) {
			return;
		}
		var cssStyle = obj.style.display;
		if (cssStyle == "none") {
			obj.style.display = "";
			showImg.style.display = "none";
			hideImg.style.display = "";
		} else {
			obj.style.display = "none";
			showImg.style.display = "";
			hideImg.style.display = "none";
		}
	}
</script>
<style>
b.fontStyle {
	font-size: 15px;
}

table.tableView {
	width: 850px
}

table.tableView tr td.imgStyle {
	text-align: left;
	width: 2px;
	cursor: pointer;
}

table.tableView tr td.list2 {
	padding: 2px 5px 2px 0px;
	height: 20px;
	text-align: left;
	width: 180px;
}

.tip_label {
	font-style: italic;
	color: #999;
}
</style>
<div id="sensorDataTabs" class="yui-navset" style="height: 100%">
	<table style="border-bottom: 2px solid #CCCCCC; width: 100%">
		<tr>
			<td><div style="width: 80px">
					<s:text name="presence.retail.analytics.monitor.range"></s:text>
				</div></td>
			<td>
				<div style="width: 270px">
					<ul class="yui-nav" style="border: 0px;">
						<li class="selected"><a href="#dataTb1"
							onclick="changeTotalClients('1')"><em id="minuteTimeRange"
								title="<s:property value="%{minuteTimeRange}"/>"><s:text
										name="presence.retail.analytics.monitorTab.lastMinute"></s:text>
							</em></a></li>
						<li><a href="#dataTb2" onclick="changeTotalClients('2')"><em
								id="hourTimeRange"
								title="<s:property value="%{hourTimeRange}"/>"> <s:text
										name="presence.retail.analytics.monitorTab.lastHour"></s:text>
							</em></a></li>
						<li><a href="#dataTb3" onclick="changeTotalClients('3')"><em
								id="dayTimeRange" title="<s:property value="%{dayTimeRange}"/>">
									<s:text name="presence.retail.analytics.monitorTab.lastDay"></s:text>
							</em></a></li>
					</ul>
				</div>
			</td>
			<td class="tip_label"><div style="width: 170px" id="refreshTip">
					<s:text name="presence.retail.analytics.monitor.minute.refresh.tip" />
				</div></td>
			<td style="width: 16%"></td>
			<td>
				<div style="width: 290px">
					<s:text name="presence.retail.analytics.monitor.status"></s:text>
					<s:if test="%{connectEuclid}">
						<img class="dinl" hspace="2"
							src="<s:url value="/images/HM-capwap-up.png"/>" title="Connected">
					</s:if>
					<s:else>
						<img class="dinl" hspace="2"
							src="<s:url value="/images/HM-capwap-down.png"/>"
							title="Disconnected">
					</s:else>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="4"></td>
			<td><s:text name="presence.retail.analytics.monitor.postTime"></s:text>
				<span id="lastPostTimeValue"><s:property
						value="%{lastPostTime}" /></span></td>
		</tr>
		<tr>
			<td id="minuteClients"><b class="fontStyle"
				id="minuteClientsValue"><s:property value="%{minuteClients}" /></b></td>
			<td id="hourClients" style="display: none"><b class="fontStyle"
				id="hourClientsValue"><s:property value="%{hourClients}" /></b></td>
			<td id="dayClients" style="display: none"><b class="fontStyle"
				id="dayClientsValue"><s:property value="%{dayClients}" /></b></td>
			<td style="width: 200px"><s:text
					name="presence.retail.analytics.monitor.client"></s:text></td>
			<td colspan="2"></td>
			<td><s:text name="presence.retail.analytics.monitor.euclidId"></s:text>
				<span id="euclidId"><s:property value="%{euclidId}" /></span></td>
		</tr>
		<tr>
			<td id="minuteBandWidths"><b class="fontStyle"
				id="minutetBandWidthsValue"><s:property
						value="%{minuteBandWidths}" /></b></td>
			<td id="hourBandWidths" style="display: none"><b
				class="fontStyle" id="hourBandWidthsValue"><s:property
						value="%{hourBandWidths}" /></b></td>
			<td id="dayBandWidths" style="display: none"><b
				class="fontStyle" id="dayBandWidthsValue"><s:property
						value="%{dayBandWidths}" /></b></td>
			<td><s:text name="presence.retail.analytics.monitor.bandwidth"></s:text></td>
			<td colspan="2"></td>
			<td><a href="javascript:;;" onclick="top.showDecalPanel();"><s:text name="presence.retail.analytics.decal.link"></s:text></a></td>
		</tr>
	</table>
	<s:if test="%{storeErrorMsg==null}">
		<div class="yui-content" style="border: 0px;">
			<div id="#dataTb1">
				<table class="tableView">
					<s:iterator value="%{minuteSensorData}" status="minuteStoreStatus"
						var="minuteStore">
						<tr>
							<td class="imgStyle"
								onclick="operateDeviceStyle('1','<s:property value="%{#minuteStoreStatus.index}"/>')"><img
								id="minuteShowImg_<s:property value="%{#minuteStoreStatus.index}"/>"
								class="expandImg" style="display: inline" alt="Show Option"
								src="<s:url value="/images/expand_plus.gif" />"> <img
								id="minuteHideImg_<s:property value="%{#minuteStoreStatus.index}"/>"
								class="expandImg" style="display: none" alt="Hide Option"
								src="<s:url value="/images/expand_minus.gif" />"></td>
							<td class="list2" style="cursor: pointer"
								onclick="operateDeviceStyle('1','<s:property value="%{#minuteStoreStatus.index}"/>')"><font
								color="#3399FF"><s:property value="storeName" /> (<s:property
										value="deviceCount" /> <s:if test="%{deviceCount<=1}">
										<s:text name="presence.retail.analytics.monitor.oneDevice" />)
									</s:if> <s:else>
										<s:text name="presence.retail.analytics.monitor.devices" />)
									</s:else></font></td>
							<td class="list2"><s:text
									name="presence.retail.analytics.monitor.postObjects"></s:text>
								(<span
								id="minuteObjectCount<s:property value="%{#minuteStoreStatus.index}"/>"><s:property
										value="objectCount" /></span>)</td>
							<td class="list2"><s:text
									name="presence.retail.analytics.monitor.detectedClients"></s:text>
								(<span
								id="minuteClientMacCount<s:property value="%{#minuteStoreStatus.index}"/>"><s:property
										value="clientMacCount" /></span>)</td>
							<td><s:text
									name="presence.retail.analytics.monitor.bandwidthUsage"></s:text>
								(<span
								id="minuteBandWidthCount<s:property value="%{#minuteStoreStatus.index}"/>"><s:property
										value="bandWidthCount" /></span>)</td>
						</tr>
						<tr
							id="minuteDeviceName_<s:property value="%{#minuteStoreStatus.index}"/>"
							style="display: none">
							<td colspan="5">
								<table style="width: 100%"
									id="minuteJsonDataStr<s:property value="%{#minuteStoreStatus.index}"/>">
									<s:iterator value="%{sensorDataList}">
										<tr>
											<td style="width: 2%"><s:if test="%{connectStatus}">
													<img class="dinl" hspace="2"
														src="<s:url value="/images/HM-capwap-up.png"/>"
														title="Connected">
												</s:if> <s:else>
													<img class="dinl" hspace="2"
														src="<s:url value="/images/HM-capwap-down.png"/>"
														title="Disconnected">
												</s:else></td>
											<td style="width: 27%"><a href="javascript:;;"
												onclick="parent.showTrackPanel('<s:property value="macAddress" />','<s:property value="%{#minuteStore.storeName}"/> ','<s:property value="hostName" />');"><s:property
														value="hostName" /></a></td>
											<td style="width: 23%"><s:property value="objects" /></td>
											<td style="width: 25%"><s:property value="clientMacs" /></td>
											<td><s:property value="convertBandWidth" /></td>
										</tr>
									</s:iterator>
								</table>
							</td>
						</tr>
					</s:iterator>
				</table>
			</div>
			<div id="#dataTb2">
				<table class="tableView">
					<s:iterator value="%{hourSensorData}" status="hourStoreStatus"
						var="hourStore">
						<tr>
							<td class="imgStyle"
								onclick="operateDeviceStyle('2','<s:property value="%{#hourStoreStatus.index}"/>')"><img
								id="hourShowImg_<s:property value="%{#hourStoreStatus.index}"/>"
								class="expandImg" style="display: inline" alt="Show Option"
								src="<s:url value="/images/expand_plus.gif" />"> <img
								id="hourHideImg_<s:property value="%{#hourStoreStatus.index}"/>"
								class="expandImg" style="display: none" alt="Hide Option"
								src="<s:url value="/images/expand_minus.gif" />"></td>
							<td class="list2" style="cursor: pointer;"
								onclick="operateDeviceStyle('2','<s:property value="%{#hourStoreStatus.index}"/>')"><font
								color="#3399FF"><s:property value="storeName" /> (<s:property
										value="deviceCount" /> <s:if test="%{deviceCount<=1}">
										<s:text name="presence.retail.analytics.monitor.oneDevice" />)
									</s:if> <s:else>
										<s:text name="presence.retail.analytics.monitor.devices" />)
									</s:else></font></td>
							<td class="list2"><s:text
									name="presence.retail.analytics.monitor.postObjects"></s:text>
								(<span
								id="hourObjectCount<s:property value="%{#hourStoreStatus.index}"/>"><s:property
										value="objectCount" /></span>)</td>
							<td class="list2"><s:text
									name="presence.retail.analytics.monitor.detectedClients"></s:text>
								(<span
								id="hourClientMacCount<s:property value="%{#hourStoreStatus.index}"/>"><s:property
										value="clientMacCount" /></span>)</td>
							<td><s:text
									name="presence.retail.analytics.monitor.bandwidthUsage"></s:text>
								(<span
								id="hourBandWidthCount<s:property value="%{#hourStoreStatus.index}"/>"><s:property
										value="bandWidthCount" /></span>)</td>
						</tr>
						<tr
							id="hourDeviceName_<s:property value="%{#hourStoreStatus.index}"/>"
							style="display: none">
							<td colspan="5">
								<table style="width: 100%"
									id="hourJsonDataStr<s:property value="%{#hourStoreStatus.index}"/>">
									<s:iterator value="%{sensorDataList}">
										<tr>
											<td style="width: 2%"><s:if test="%{connectStatus}">
													<img class="dinl" hspace="2"
														src="<s:url value="/images/HM-capwap-up.png"/>"
														title="Connected">
												</s:if> <s:else>
													<img class="dinl" hspace="2"
														src="<s:url value="/images/HM-capwap-down.png"/>"
														title="Disconnected">
												</s:else></td>
											<td style="width: 27%"><a href="javascript:;;"
												onclick="parent.showTrackPanel('<s:property value="macAddress" />','<s:property value="%{#hourStore.storeName}"/> ','<s:property value="hostName" />');"><s:property
														value="hostName" /></a></td>
											<td style="width: 23%"><s:property value="objects" /></td>
											<td style="width: 25%"><s:property value="clientMacs" /></td>
											<td><s:property value="convertBandWidth" /></td>
										</tr>
									</s:iterator>
								</table>
							</td>
						</tr>
					</s:iterator>
				</table>
			</div>
			<div id="#dataTb3">
				<table class="tableView">
					<s:iterator value="%{daySensorData}" status="dayStoreStatus"
						var="dayStore">
						<tr>
							<td class="imgStyle"
								onclick="operateDeviceStyle('3','<s:property value="%{#dayStoreStatus.index}"/>')"><img
								id="dayShowImg_<s:property value="%{#dayStoreStatus.index}"/>"
								class="expandImg" style="display: inline" alt="Show Option"
								src="<s:url value="/images/expand_plus.gif" />"> <img
								id="dayHideImg_<s:property value="%{#dayStoreStatus.index}"/>"
								class="expandImg" style="display: none" alt="Hide Option"
								src="<s:url value="/images/expand_minus.gif" />"></td>
							<td class="list2" style="cursor: pointer;"
								onclick="operateDeviceStyle('3','<s:property value="%{#dayStoreStatus.index}"/>')"><font
								color="#3399FF"><s:property value="storeName" /> (<s:property
										value="deviceCount" /> <s:if test="%{deviceCount<=1}">
										<s:text name="presence.retail.analytics.monitor.oneDevice" />)
									</s:if> <s:else>
										<s:text name="presence.retail.analytics.monitor.devices" />)
									</s:else></font></td>
							<td class="list2"><s:text
									name="presence.retail.analytics.monitor.postObjects"></s:text>
								(<span
								id="dayObjectCount<s:property value="%{#dayStoreStatus.index}"/>"><s:property
										value="objectCount" /></span>)</td>
							<td class="list2"><s:text
									name="presence.retail.analytics.monitor.detectedClients"></s:text>
								(<span
								id="dayClientMacCount<s:property value="%{#dayStoreStatus.index}"/>"><s:property
										value="clientMacCount" /></span>)</td>
							<td><s:text
									name="presence.retail.analytics.monitor.bandwidthUsage"></s:text>
								(<span
								id="dayBandWidthCount<s:property value="%{#dayStoreStatus.index}"/>"><s:property
										value="bandWidthCount" /></span>)</td>
						</tr>
						<tr
							id="dayDeviceName_<s:property value="%{#dayStoreStatus.index}"/>"
							style="display: none">
							<td colspan="5">
								<table style="width: 100%"
									id="dayJsonDataStr<s:property value="%{#dayStoreStatus.index}"/>">
									<s:iterator value="%{sensorDataList}">
										<tr>
											<td style="width: 2%"><s:if test="%{connectStatus}">
													<img class="dinl" hspace="2"
														src="<s:url value="/images/HM-capwap-up.png"/>"
														title="Connected">
												</s:if> <s:else>
													<img class="dinl" hspace="2"
														src="<s:url value="/images/HM-capwap-down.png"/>"
														title="Disconnected">
												</s:else></td>
											<td style="width: 27%"><a href="javascript:;;"
												onclick="parent.showTrackPanel('<s:property value="macAddress" />','<s:property value="%{#dayStore.storeName}"/>','<s:property value="hostName" />');"><s:property
														value="hostName" /></a></td>
											<td style="width: 23%"><s:property value="objects" /></td>
											<td style="width: 25%"><s:property value="clientMacs" /></td>
											<td><s:property value="convertBandWidth" /></td>
										</tr>
									</s:iterator>
								</table>
							</td>
						</tr>
					</s:iterator>
				</table>
			</div>
		</div>
	</s:if>
	<s:else>
		<div style="padding-top: 5px">
			<s:property value="%{storeErrorMsg}" />
			<input type="button" value="<s:text name="common.button.refresh"/>"
				class="button" onClick="reOpenMonitorPage();">
		</div>
		<div class="yui-content" style="display: none"></div>
	</s:else>
</div>
<tiles:insertDefinition name="tabView" />
<script type="text/javascript">
	function reOpenMonitorPage() {
		location.href = "<s:url value='retailAnalytics.action' includeParams='none' />?operation=retailSensorData&ignore="
				+ new Date().getTime()
	}
	var storeSize = '<s:property value="%{minuteSensorData.size}"/>';
	var tabView2 = new YAHOO.widget.TabView("sensorDataTabs");
	//flush Sensor data
	function getJsonSensorData() {
		var url = "<s:url action='retailAnalytics' includeParams='none'/>"
				+ "?operation=getJsonSensorData&ignore=" + new Date().getTime();
		ajaxRequest(null, url, getJsonDataDone);
	}
	function getJsonDataDone(o) {
		eval("var result = " + o.responseText);
		fillJsonData("lastPostTimeValue", result.lastPostTime);

		fillJsonData("minuteClientsValue", result.minuteClients);
		fillJsonData("hourClientsValue", result.hourClients);
		fillJsonData("dayClientsValue", result.dayClients);

		fillJsonData("minutetBandWidthsValue", result.minuteBandWidths);
		fillJsonData("hourBandWidthsValue", result.hourBandWidths);
		fillJsonData("dayBandWidthsValue", result.dayBandWidths);

		document.getElementById("minuteTimeRange").setAttribute("title",
				result.minuteTimeRange);
		document.getElementById("hourTimeRange").setAttribute("title",
				result.hourTimeRange);
		document.getElementById("dayTimeRange").setAttribute("title",
				result.dayTimeRange);

		for ( var i = 0; i < storeSize; i++) {
			fillJsonData("minuteObjectCount" + i, result['minuteObjectCount'
					+ i]);
			fillJsonData("minuteClientMacCount" + i,
					result['minuteClientMacCount' + i]);
			fillJsonData("minuteBandWidthCount" + i,
					result['minuteBandWidthCount' + i]);
			fillJsonData("minuteJsonDataStr" + i, result['minuteJsonDataStr'
					+ i]);

			fillJsonData("hourObjectCount" + i, result['hourObjectCount' + i]);
			fillJsonData("hourClientMacCount" + i, result['hourClientMacCount'
					+ i]);
			fillJsonData("hourBandWidthCount" + i, result['hourBandWidthCount'
					+ i]);
			fillJsonData("hourJsonDataStr" + i, result['hourJsonDataStr' + i]);

			fillJsonData("dayObjectCount" + i, result['dayObjectCount' + i]);
			fillJsonData("dayClientMacCount" + i, result['dayClientMacCount'
					+ i]);
			fillJsonData("dayBandWidthCount" + i, result['dayBandWidthCount'
					+ i]);
			fillJsonData("dayJsonDataStr" + i, result['dayJsonDataStr' + i]);

		}
	}
	function fillJsonData(id, json) {
		$("#" + id).html(json);
	}
	setInterval(getJsonSensorData, 10000);// 10 seconds

	parent.waitingPanel.hide();
</script>
