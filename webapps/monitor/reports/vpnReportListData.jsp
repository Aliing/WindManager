<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<report>
	<vpnAvailability>
		<vTitle><s:property value="%{vTitle}" /></vTitle>
		<lbUptime>Uptime</lbUptime>
		<vUptime><s:property value="%{vUptime}" /></vUptime>
		<lbDowntime>Downtime</lbDowntime>
		<vDowntime><s:property value="%{vDowntime}" /></vDowntime>
		<lbDowntimeNumber>Number of Downtimes</lbDowntimeNumber>
		<vDowntimeNumber><s:property value="%{vDowntimeNumber}" /></vDowntimeNumber>
		<lbAvailableTunnel>Available Tunnels</lbAvailableTunnel>
		<avaliable>
			<s:iterator value="%{vpn_availability}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
			</s:iterator>
		</avaliable>
		<uptime>
			<s:iterator value="%{vpn_uptime}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
			</s:iterator>
		</uptime>
	</vpnAvailability>
	<vpnThroughput>
		<vTitle><s:property value="%{vTitle}" /></vTitle>
		<lbAput>Average Throughput</lbAput>
		<vAputIn><s:property value="%{vAputIn}" /></vAputIn>
		<vAputOut><s:property value="%{vAputOut}" /></vAputOut>
		<lbLput>Lowest Throughput</lbLput>
		<vLputIn><s:property value="%{vLputIn}" /></vLputIn>
		<vLputOut><s:property value="%{vLputOut}" /></vLputOut>
		<lbHput>Highest Throughput</lbHput>
		<vHputIn><s:property value="%{vHputIn}" /></vHputIn>
		<vHputOut><s:property value="%{vHputOut}" /></vHputOut>
		<throughputIn>
			<s:iterator value="%{vpn_throughput_in}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
			</s:iterator>
		</throughputIn>
		<throughputOut>
			<s:iterator value="%{vpn_throughput_out}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
			</s:iterator>
		</throughputOut>
	</vpnThroughput>
	<vpnLatency>
		<vTitle><s:property value="%{vTitle}" /></vTitle>
		<lbALatency>Average Latency</lbALatency>
		<vALatency><s:property value="%{vALatency}" /></vALatency>
		<lbSLatency>Smallest Latency</lbSLatency>
		<vSLatency><s:property value="%{vSLatency}" /></vSLatency>
		<lbHLatency>Largest Latency</lbHLatency>
		<vHLatency><s:property value="%{vHLatency}" /></vHLatency>
		<latency>
			<s:iterator value="%{vpn_latency}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
			</s:iterator>
		</latency>
	</vpnLatency>
</report>
