<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<summary>
	<totalAps><s:property value="%{totalAps}" /></totalAps>
	<autoAps><s:property value="%{autoAps}" /></autoAps>
	<manuallyAps><s:property value="%{manuallyAps}" /></manuallyAps>
	<managedAps><s:property value="%{managedAps}" /></managedAps>
	<friendlyAps><s:property value="%{friendlyAPs}" /></friendlyAps>
	<rogueAps><s:property value="%{rogueAps}" /></rogueAps>
	<alarmAps><s:property value="%{alarmAps}" /></alarmAps>
	<outofdataAps><s:property value="%{outofdataAps}" /></outofdataAps>
	
	<activeClients><s:property value="%{activeClients}" /></activeClients>
	<rogueClients><s:property value="%{rogueClients}" /></rogueClients>
	
	<slaApLastHour><s:property value="%{slaApTitle}" /></slaApLastHour>
	<slaClientLastHour><s:property value="%{slaClientTitle}" /></slaClientLastHour>

	<minPicUrl>../../images/expand_minus.gif</minPicUrl>
	<maxPicUrl>../../images/expand_plus.gif</maxPicUrl>
	<alarms>
		<item>
			<severity>Critical</severity>
			<count><s:property value="%{criticalAlarmCount}" /></count>
		</item>
		<item>
			<severity>Major</severity>
			<count><s:property value="%{majorAlarmCount}" /></count>
		</item>
		<item>
			<severity>Minor</severity>
			<count><s:property value="%{minorAlarmCount}" /></count>
		</item>
		<item>
			<severity>Cleared</severity>
			<count><s:property value="%{clearedAlarmCount}" /></count>
		</item>
	</alarms>
	<clientVendor>
		<s:iterator value="%{lstClientVendorCount}" status="status">
		<item>
			<severity><s:property value="%{value}" /></severity>
			<count><s:property value="%{id}" /></count>
		</item>
		</s:iterator>
	</clientVendor>
	<compliance>
		<item>
			<severity>Weak</severity>
			<count><s:property value="%{poorAp}" /></count>
		</item>
		<item>
			<severity>Acceptable</severity>
			<count><s:property value="%{goodAp}" /></count>
		</item>
		<item>
			<severity>Strong</severity>
			<count><s:property value="%{excellentAp}" /></count>
		</item>
	</compliance>
	<clients>
		<item>
			<wlanType>11a</wlanType>
			<count><s:property value="%{count11a}" /></count>
		</item>
		<item>
			<wlanType>11b</wlanType>
			<count><s:property value="%{count11b}" /></count>
		</item>
		<item>
			<wlanType>11g</wlanType>
			<count><s:property value="%{count11g}" /></count>
		</item>
		<item>
			<wlanType>11na</wlanType>
			<count><s:property value="%{count11na}" /></count>
		</item>
		<item>
			<wlanType>11ng</wlanType>
			<count><s:property value="%{count11ng}" /></count>
		</item>
	</clients>
	<users>
		<s:iterator value="%{users}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</users>
	<hiveapUptime>
		<s:iterator value="%{apUptime}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</hiveapUptime>
	<roamings>
		<s:iterator value="%{roamings}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</roamings>
	<bindWidthRate>
		<s:iterator value="%{bindWidthRate}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</bindWidthRate>
	<mostTraffic>
		<s:iterator value="%{apMostData}" status="status">
		<item>
			<apName><s:property value="%{name}" /></apName>
			<totalFrames><s:property value="%{totalData}" /></totalFrames>
			<txFrames><s:property value="%{txdata}" /></txFrames>
			<rxFrames><s:property value="%{rxdata}" /></rxFrames>
		</item>
		</s:iterator>
	</mostTraffic>
	
	<mostClient>
		<s:iterator value="%{apNameClientCount}" status="status">
		<item>
			<apName><s:property value="%{value}" /></apName>
			<clientCount><s:property value="%{key}" /></clientCount>
		</item>
		</s:iterator>
	</mostClient>
	
	<mostInterference>
		<s:iterator value="%{lstInterferenceCrcError}" status="status">
		<item>
			<apName><s:property value="%{name}" /></apName>
			<wifi0Frames><s:property value="%{txdata}" /></wifi0Frames>
			<wifi1Frames><s:property value="%{rxdata}" /></wifi1Frames>
		</item>
		</s:iterator>
	</mostInterference>
	
	<longestTimeClient>
		<s:iterator value="%{clientLongestTime}" status="status">
		<item>
			<clientMac><s:property value="%{key}" /></clientMac>
			<date><s:property value="%{value}" /></date>
		</item>
		</s:iterator>
	</longestTimeClient>
	
	<clientAirtime>
		<s:iterator value="%{lstClientAirtime}" status="status">
		<item>
			<clientMac><s:property value="%{name}" /></clientMac>
			<totalFrames><s:property value="%{totalData}" /></totalFrames>
			<txFrames><s:property value="%{txdata}" /></txFrames>
			<rxFrames><s:property value="%{rxdata}" /></rxFrames>
		</item>
		</s:iterator>
	</clientAirtime>
	
	<failureClient>
		<s:iterator value="%{lstClietnFailures}" status="status">
		<item>
			<clientMac><s:property value="%{value}" /></clientMac>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</failureClient>
	
	<clientUserProfile>
		<s:iterator value="%{lstClientUserProfile}" status="status">
		<item>
			<userProfileId><s:property value="%{value}" /></userProfileId>
			<count><s:property value="%{id}" /></count>
		</item>
		</s:iterator>
	</clientUserProfile>
	
	<slaApBad>
		<s:iterator value="%{lstSlaApBad}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaApBad>
	
	<slaApAction>
		<s:iterator value="%{lstSlaApAction}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaApAction>
	
	<slaApClear>
		<s:iterator value="%{lstSlaApClear}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaApClear>
	
	<slaClientBad>
		<s:iterator value="%{lstSlaClientBad}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaClientBad>
	
	<slaClientAction>
		<s:iterator value="%{lstSlaClientAction}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaClientAction>
	
	<slaClientClear>
		<s:iterator value="%{lstSlaClientClear}" status="status">
		<item>
			<date><s:property value="%{value}" /></date>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</slaClientClear>
	
	<clientMostSla1>
		<s:iterator value="%{lstSlaClientMost1}" status="status11">
		<item>
			<clientMac><s:property value="%{clientMac}" /></clientMac>
			<count><s:property value="%{status}" /></count>
		</item>
		</s:iterator>
	</clientMostSla1>
	
	<hiveApMostSla1>
		<s:iterator value="%{lstSlaApMost1}" status="status">
		<item>
			<apName><s:property value="%{value}" /></apName>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</hiveApMostSla1>
	<clientMostSla2>
		<s:iterator value="%{lstSlaClientMost2}" status="status11">
		<item>
			<clientMac><s:property value="%{clientMac}" /></clientMac>
			<count><s:property value="%{status}" /></count>
		</item>
		</s:iterator>
	</clientMostSla2>
	
	<hiveApMostSla2>
		<s:iterator value="%{lstSlaApMost2}" status="status">
		<item>
			<apName><s:property value="%{value}" /></apName>
			<count><s:property value="%{key}" /></count>
		</item>
		</s:iterator>
	</hiveApMostSla2>

</summary>
