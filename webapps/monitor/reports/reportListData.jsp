<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<report>
	<radioTrafficMetrics>
		<receiveWifi0Title>Wifi0 Received Data</receiveWifi0Title>
		<receiveWifi1Title>Wifi1 Received Data</receiveWifi1Title>
		<transmitWifi0Title>Wifi0 Transmitted Data</transmitWifi0Title>
		<transmitWifi1Title>Wifi1 Transmitted Data</transmitWifi1Title>
		<transmit>
			<wifi0totalDataFrames>
				<s:iterator value="%{rtm_trans_wifi0_totalData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0totalDataFrames>
			<wifi0beDataFrames>
				<s:iterator value="%{rtm_trans_wifi0_beData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0beDataFrames>
			<wifi0bgDataFrames>
				<s:iterator value="%{rtm_trans_wifi0_bgData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0bgDataFrames>
			<wifi0viDataFrames>
				<s:iterator value="%{rtm_trans_wifi0_viData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0viDataFrames>
			<wifi0voDataFrames>
				<s:iterator value="%{rtm_trans_wifi0_voData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0voDataFrames>
			<wifi0unicastDataFrames>
				<s:iterator value="%{rtm_trans_wifi0_unicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0unicastDataFrames>
			<wifi0multicastDataFrames>
				<s:iterator value="%{rtm_trans_wifi0_multicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0multicastDataFrames>
			<wifi0broadcastDataFrames>
				<s:iterator value="%{rtm_trans_wifi0_broadcastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0broadcastDataFrames>
			<wifi0nonBeaconMgtFrames>
				<s:iterator value="%{rtm_trans_wifi0_nonBeaconMgtData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0nonBeaconMgtFrames>
			<wifi0beaconFrames>
				<s:iterator value="%{rtm_trans_wifi0_beaconData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0beaconFrames>
			
			
			<wifi1totalDataFrames>
				<s:iterator value="%{rtm_trans_wifi1_totalData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1totalDataFrames>
			<wifi1beDataFrames>
				<s:iterator value="%{rtm_trans_wifi1_beData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1beDataFrames>
			<wifi1bgDataFrames>
				<s:iterator value="%{rtm_trans_wifi1_bgData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1bgDataFrames>
			<wifi1viDataFrames>
				<s:iterator value="%{rtm_trans_wifi1_viData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1viDataFrames>
			<wifi1voDataFrames>
				<s:iterator value="%{rtm_trans_wifi1_voData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1voDataFrames>
			<wifi1unicastDataFrames>
				<s:iterator value="%{rtm_trans_wifi1_unicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1unicastDataFrames>
			<wifi1multicastDataFrames>
				<s:iterator value="%{rtm_trans_wifi1_multicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1multicastDataFrames>
			<wifi1broadcastDataFrames>
				<s:iterator value="%{rtm_trans_wifi1_broadcastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1broadcastDataFrames>
			<wifi1nonBeaconMgtFrames>
				<s:iterator value="%{rtm_trans_wifi1_nonBeaconMgtData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1nonBeaconMgtFrames>
			<wifi1beaconFrames>
				<s:iterator value="%{rtm_trans_wifi1_beaconData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1beaconFrames>
		</transmit>
		<receive>
			<wifi0totalDataFrames>
				<s:iterator value="%{rtm_rec_wifi0_totalData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0totalDataFrames>
			<wifi0unicastDataFrames>
				<s:iterator value="%{rtm_rec_wifi0_unicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0unicastDataFrames>
			<wifi0multicastDataFrames>
				<s:iterator value="%{rtm_rec_wifi0_multicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0multicastDataFrames>
			<wifi0broadcastDataFrames>
				<s:iterator value="%{rtm_rec_wifi0_broadcastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0broadcastDataFrames>
			<wifi0mgtFrames>
				<s:iterator value="%{rtm_rec_wifi0_mgtData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0mgtFrames>
			<wifi1totalDataFrames>
				<s:iterator value="%{rtm_rec_wifi1_totalData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1totalDataFrames>
			<wifi1unicastDataFrames>
				<s:iterator value="%{rtm_rec_wifi1_unicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1unicastDataFrames>
			<wifi1multicastDataFrames>
				<s:iterator value="%{rtm_rec_wifi1_multicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1multicastDataFrames>
			<wifi1broadcastDataFrames>
				<s:iterator value="%{rtm_rec_wifi1_broadcastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1broadcastDataFrames>
			<wifi1mgtFrames>
				<s:iterator value="%{rtm_rec_wifi1_mgtData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1mgtFrames>
		</receive>
	</radioTrafficMetrics>

	<radioInterference>
		<wifi0>
			<averageTxUc>
				<s:iterator value="%{wifi0_averageTxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</averageTxUc>
			<averageRxUc>
				<s:iterator value="%{wifi0_averageRxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</averageRxUc>
			<averageInterferenceCu>
				<s:iterator value="%{wifi0_averageInterferenceCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</averageInterferenceCu>
			<shotTermTxCu>
				<s:iterator value="%{wifi0_shortTermTxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</shotTermTxCu>
			<shotTermRxCu>
				<s:iterator value="%{wifi0_shortTermRxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</shotTermRxCu>
			<shotTermInterferenceCu>
				<s:iterator value="%{wifi0_shortTermInterferenceCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</shotTermInterferenceCu>
			<snapShotTxCu>
				<s:iterator value="%{wifi0_snapShotTxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</snapShotTxCu>
			<snapShotRxCu>
				<s:iterator value="%{wifi0_snapShotRxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</snapShotRxCu>
			<snapShotInterferenceCu>
				<s:iterator value="%{wifi0_snapShotInterferenceCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</snapShotInterferenceCu>
			<averageNoiseFloor>
				<s:iterator value="%{wifi0_averageNoiseFloor}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</averageNoiseFloor>
			<shortTermNoiseFloor>
				<s:iterator value="%{wifi0_shortTermNoiseFloor}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</shortTermNoiseFloor>
			<snapShotNoiseFloor>
				<s:iterator value="%{wifi0_snapShotNoiseFloor}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</snapShotNoiseFloor>
			<crcErrorRate>
				<s:iterator value="%{wifi0_crcError}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</crcErrorRate>
		</wifi0>
		<wifi1>
			<averageTxUc>
				<s:iterator value="%{wifi1_averageTxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</averageTxUc>
			<averageRxUc>
				<s:iterator value="%{wifi1_averageRxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</averageRxUc>
			<averageInterferenceCu>
				<s:iterator value="%{wifi1_averageInterferenceCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</averageInterferenceCu>
			<shotTermTxCu>
				<s:iterator value="%{wifi1_shortTermTxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</shotTermTxCu>
			<shotTermRxCu>
				<s:iterator value="%{wifi1_shortTermRxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</shotTermRxCu>
			<shotTermInterferenceCu>
				<s:iterator value="%{wifi1_shortTermInterferenceCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</shotTermInterferenceCu>
			<snapShotTxCu>
				<s:iterator value="%{wifi1_snapShotTxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</snapShotTxCu>
			<snapShotRxCu>
				<s:iterator value="%{wifi1_snapShotRxCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</snapShotRxCu>
			<snapShotInterferenceCu>
				<s:iterator value="%{wifi1_snapShotInterferenceCu}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</snapShotInterferenceCu>
			<averageNoiseFloor>
				<s:iterator value="%{wifi1_averageNoiseFloor}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</averageNoiseFloor>
			<shortTermNoiseFloor>
				<s:iterator value="%{wifi1_shortTermNoiseFloor}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</shortTermNoiseFloor>
			<snapShotNoiseFloor>
				<s:iterator value="%{wifi1_snapShotNoiseFloor}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</snapShotNoiseFloor>
			<crcErrorRate>
				<s:iterator value="%{wifi1_crcError}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</crcErrorRate>
		</wifi1>
	</radioInterference>

	<radioChannelPowerNoise>
		<noiseTitle>Noise (dBm)</noiseTitle>
		<powerTitle>Power (dBm)</powerTitle>
		<channelWifi0Title>Wifi0 Channel</channelWifi0Title>
		<channelWifi1Title>Wifi1 Channel</channelWifi1Title>
		<wifi0Channel>
			<s:iterator value="%{rcpn_wifi0_channel}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
			</s:iterator>
		</wifi0Channel>
		<wifi1Channel>
			<s:iterator value="%{rcpn_wifi1_channel}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
			</s:iterator>
		</wifi1Channel>
		<wifi0Power>
			<s:iterator value="%{rcpn_wifi0_power}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
			</s:iterator>
		</wifi0Power>
		<wifi1Power>
			<s:iterator value="%{rcpn_wifi1_power}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
			</s:iterator>
		</wifi1Power>
		<wifi0Noise>
			<s:iterator value="%{rcpn_wifi0_noise}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
			</s:iterator>
		</wifi0Noise>
		<wifi1Noise>
			<s:iterator value="%{rcpn_wifi1_noise}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
			</s:iterator>
		</wifi1Noise>
	</radioChannelPowerNoise>
	
	<interfaceInfo>
		<wifi0>
			<txUnicast>
				<s:iterator value="%{hiveap_wifi0_trans_unicast}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txUnicast>
			<rxUnicast>
				<s:iterator value="%{hiveap_wifi0_rec_unicast}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxUnicast>
			<txBroadcast>
				<s:iterator value="%{hiveap_wifi0_trans_broadcast}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txBroadcast>
			<rxBroadcast>
				<s:iterator value="%{hiveap_wifi0_rec_broadcast}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxBroadcast>
			<txDrops>
				<s:iterator value="%{hiveap_wifi0_trans_drops}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txDrops>
			<rxDrops>
				<s:iterator value="%{hiveap_wifi0_rec_drops}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxDrops>
			<txTotalU>
				<s:iterator value="%{hiveap_wifi0_trans_totalU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txTotalU>
			<rxTotalU>
				<s:iterator value="%{hiveap_wifi0_rec_totalU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxTotalU>
			<txRetryRateU>
				<s:iterator value="%{hiveap_wifi0_trans_retryRateU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txRetryRateU>
			<rxRetryRateU>
				<s:iterator value="%{hiveap_wifi0_rec_retryRateU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxRetryRateU>
			<txAirtimeU>
				<s:iterator value="%{hiveap_wifi0_trans_airTimeU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txAirtimeU>
			<rxAirtimeU>
				<s:iterator value="%{hiveap_wifi0_rec_airTimeU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxAirtimeU>
			<crcErrorRateU>
				<s:iterator value="%{hiveap_wifi0_crcErrorRateU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</crcErrorRateU>
			<totalChannelU>
				<s:iterator value="%{hiveap_wifi0_totalChannelU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</totalChannelU>
			<interferenceU>
				<s:iterator value="%{hiveap_wifi0_InterferenceU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</interferenceU>
			<noiseFloor>
				<s:iterator value="%{hiveap_wifi0_noiseFloor}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</noiseFloor>

			<bandsteering>
				<s:iterator value="%{hiveap_wifi0_bandsteering}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</bandsteering>
			<loadbalance>
				<s:iterator value="%{hiveap_wifi0_loadbalance}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</loadbalance>
			<weaksnr>
				<s:iterator value="%{hiveap_wifi0_weaksnr}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</weaksnr>
			<safetynet>
				<s:iterator value="%{hiveap_wifi0_safetynet}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</safetynet>
			<proberequest>
				<s:iterator value="%{hiveap_wifi0_proberequest}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</proberequest>
			<authrequest>
				<s:iterator value="%{hiveap_wifi0_authrequest}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</authrequest>

			<txRateDistribution>
				<s:iterator value="hiveap_wifi0_trans_dateTimeList" status="status" id="tmpList">
					<item>
						<date><s:property/></date>
						<s:iterator value="hiveap_wifi0_trans_rate_dis.get(#tmpList)" status="status2">
						<<s:property value="value" />><s:property value="id" />
						</<s:property value="value" />>
						</s:iterator>
					</item>
				</s:iterator>
			</txRateDistribution>
			<txRateSuccDistribution>
				<s:iterator value="%{hiveap_wifi0_trans_rate_succ_dis}" status="status">
					<item>
						<date><s:property value="%{key}" /></date>
						<count><s:property value="%{value}" /></count>
						<detail><s:property value="%{toopTip}"/></detail>
					</item>
				</s:iterator>
			</txRateSuccDistribution>
			<txRateType>
				<s:iterator value="hiveap_wifi0_trans_rateTypeList" status="status" >
				<keySet>
					<typeString><s:property/></typeString>
				</keySet>
				</s:iterator>
			</txRateType>
			<rxRateDistribution>
				<s:iterator value="hiveap_wifi0_rec_dateTimeList" status="status" id="tmpList">
					<item>
						<date><s:property/></date>
						<s:iterator value="hiveap_wifi0_rec_rate_dis.get(#tmpList)" status="status2">
						<<s:property value="value" />><s:property value="id" />
						</<s:property value="value" />>
						</s:iterator>
					</item>
				</s:iterator>
			</rxRateDistribution>
			<rxRateSuccDistribution>
				<s:iterator value="%{hiveap_wifi0_rec_rate_succ_dis}" status="status">
					<item>
						<date><s:property value="%{key}" /></date>
						<count><s:property value="%{value}" /></count>
						<detail><s:property value="%{toopTip}"/></detail>
					</item>
				</s:iterator>
			</rxRateSuccDistribution>
			<rxRateType>
				<s:iterator value="hiveap_wifi0_rec_rateTypeList" status="status" >
				<keySet>
					<typeString><s:property/></typeString>
				</keySet>
				</s:iterator>
			</rxRateType>
		</wifi0>
		<wifi1>
			<txUnicast>
				<s:iterator value="%{hiveap_wifi1_trans_unicast}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txUnicast>
			<rxUnicast>
				<s:iterator value="%{hiveap_wifi1_rec_unicast}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxUnicast>
			<txBroadcast>
				<s:iterator value="%{hiveap_wifi1_trans_broadcast}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txBroadcast>
			<rxBroadcast>
				<s:iterator value="%{hiveap_wifi1_rec_broadcast}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxBroadcast>
			<txDrops>
				<s:iterator value="%{hiveap_wifi1_trans_drops}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txDrops>
			<rxDrops>
				<s:iterator value="%{hiveap_wifi1_rec_drops}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxDrops>
			<txTotalU>
				<s:iterator value="%{hiveap_wifi1_trans_totalU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txTotalU>
			<rxTotalU>
				<s:iterator value="%{hiveap_wifi1_rec_totalU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxTotalU>
			<txRetryRateU>
				<s:iterator value="%{hiveap_wifi1_trans_retryRateU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txRetryRateU>
			<rxRetryRateU>
				<s:iterator value="%{hiveap_wifi1_rec_retryRateU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxRetryRateU>
			<txAirtimeU>
				<s:iterator value="%{hiveap_wifi1_trans_airTimeU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</txAirtimeU>
			<rxAirtimeU>
				<s:iterator value="%{hiveap_wifi1_rec_airTimeU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</rxAirtimeU>
			<crcErrorRateU>
				<s:iterator value="%{hiveap_wifi1_crcErrorRateU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</crcErrorRateU>
			<totalChannelU>
				<s:iterator value="%{hiveap_wifi1_totalChannelU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</totalChannelU>
			<interferenceU>
				<s:iterator value="%{hiveap_wifi1_InterferenceU}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</interferenceU>
			<noiseFloor>
				<s:iterator value="%{hiveap_wifi1_noiseFloor}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</noiseFloor>
			<bandsteering>
				<s:iterator value="%{hiveap_wifi1_bandsteering}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</bandsteering>
			<loadbalance>
				<s:iterator value="%{hiveap_wifi1_loadbalance}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</loadbalance>
			<weaksnr>
				<s:iterator value="%{hiveap_wifi1_weaksnr}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</weaksnr>
			<safetynet>
				<s:iterator value="%{hiveap_wifi1_safetynet}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</safetynet>
			<proberequest>
				<s:iterator value="%{hiveap_wifi1_proberequest}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</proberequest>
			<authrequest>
				<s:iterator value="%{hiveap_wifi1_authrequest}" status="status">
					<item>
						<date><s:property value="%{value}" /></date>
						<count><s:property value="%{id}" /></count>
					</item>
				</s:iterator>
			</authrequest>
			<txRateDistribution>
				<s:iterator value="hiveap_wifi1_trans_dateTimeList" status="status" id="tmpList">
					<item>
						<date><s:property/></date>
						<s:iterator value="hiveap_wifi1_trans_rate_dis.get(#tmpList)" status="status2">
						<<s:property value="value" />><s:property value="id" />
						</<s:property value="value" />>
						</s:iterator>
					</item>
				</s:iterator>
			</txRateDistribution>
			<txRateSuccDistribution>
				<s:iterator value="%{hiveap_wifi1_trans_rate_succ_dis}" status="status">
					<item>
						<date><s:property value="%{key}" /></date>
						<count><s:property value="%{value}" /></count>
						<detail><s:property value="%{toopTip}"/></detail>
					</item>
				</s:iterator>
			</txRateSuccDistribution>
			<txRateType>
				<s:iterator value="hiveap_wifi1_trans_rateTypeList" status="status" >
				<keySet>
					<typeString><s:property/></typeString>
				</keySet>
				</s:iterator>
			</txRateType>
			<rxRateDistribution>
				<s:iterator value="hiveap_wifi1_rec_dateTimeList" status="status" id="tmpList">
					<item>
						<date><s:property/></date>
						<s:iterator value="hiveap_wifi1_rec_rate_dis.get(#tmpList)" status="status2">
						<<s:property value="value" />><s:property value="id" />
						</<s:property value="value" />>
						</s:iterator>
					</item>
				</s:iterator>
			</rxRateDistribution>
			<rxRateSuccDistribution>
				<s:iterator value="%{hiveap_wifi1_rec_rate_succ_dis}" status="status">
					<item>
						<date><s:property value="%{key}" /></date>
						<count><s:property value="%{value}" /></count>
						<detail><s:property value="%{toopTip}"/></detail>
					</item>
				</s:iterator>
			</rxRateSuccDistribution>
			<rxRateType>
				<s:iterator value="hiveap_wifi1_rec_rateTypeList" status="status" >
				<keySet>
					<typeString><s:property/></typeString>
				</keySet>
				</s:iterator>
			</rxRateType>
		</wifi1>
	</interfaceInfo>
	
	<radioTroubleShooting>
		<receiveWifi0Title>Wifi0 Received Data</receiveWifi0Title>
		<receiveWifi1Title>Wifi1 Received Data</receiveWifi1Title>
		<transmitWifi0Title>Wifi0 Transmitted Data</transmitWifi0Title>
		<transmitWifi1Title>Wifi1 Transmitted Data</transmitWifi1Title>
		<retryEventsTitle>Retry Threshold Crossing Events</retryEventsTitle>
		<transmit>
			<wifi0TotalRetries>
				<s:iterator value="%{rts_trans_wifi0_totalRetries}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0TotalRetries>
			<wifi0TotalFramesDropped>
				<s:iterator value="%{rts_trans_wifi0_totalFramesDropped}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0TotalFramesDropped>
			<wifi0TotalFrameErrors>
				<s:iterator value="%{rts_trans_wifi0_totalFrameErrors}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0TotalFrameErrors>
			<wifi0FEForExcessiveHWRetries>
				<s:iterator value="%{rts_trans_wifi0_feForExcessiveHWRetries}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0FEForExcessiveHWRetries>
			<wifi0RTSFailures>
				<s:iterator value="%{rts_trans_wifi0_rtsFailures}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0RTSFailures>
			
			<wifi1TotalRetries>
				<s:iterator value="%{rts_trans_wifi1_totalRetries}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1TotalRetries>
			<wifi1TotalFramesDropped>
				<s:iterator value="%{rts_trans_wifi1_totalFramesDropped}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1TotalFramesDropped>
			<wifi1TotalFrameErrors>
				<s:iterator value="%{rts_trans_wifi1_totalFrameErrors}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1TotalFrameErrors>
			<wifi1FEForExcessiveHWRetries>
				<s:iterator value="%{rts_trans_wifi1_feForExcessiveHWRetries}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1FEForExcessiveHWRetries>
			<wifi1RTSFailures>
				<s:iterator value="%{rts_trans_wifi1_rtsFailures}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1RTSFailures>
		</transmit>
		<receive>
			<wifi0TotalFrameDropped>
				<s:iterator value="%{rts_rec_wifi0_totalFrameDropped}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi0TotalFrameDropped>
			<wifi1TotalFrameDropped>
				<s:iterator value="%{rts_rec_wifi1_totalFrameDropped}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</wifi1TotalFrameDropped>
		</receive>
	</radioTroubleShooting>
	
	<ssidTrafficMetrics>
		<transmitTitle>Transmitted Data</transmitTitle>
		<receiveTitle>Received Data</receiveTitle>
		<transmit>
			<totalDataFrames>
				<s:iterator value="%{stm_trans_totalData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</totalDataFrames>
			<beDataFrames>
				<s:iterator value="%{stm_trans_beData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</beDataFrames>
			<bgDataFrames>
				<s:iterator value="%{stm_trans_bgData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</bgDataFrames>
			<viDataFrames>
				<s:iterator value="%{stm_trans_viData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</viDataFrames>
			<voDataFrames>
				<s:iterator value="%{stm_trans_voData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</voDataFrames>
			<unicastDataFrames>
				<s:iterator value="%{stm_trans_unicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</unicastDataFrames>
			<multicastDataFrames>
				<s:iterator value="%{stm_trans_multicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</multicastDataFrames>
			<broadcastDataFrames>
				<s:iterator value="%{stm_trans_broadcastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</broadcastDataFrames>
		</transmit>
		
		<receive>
			<totalDataFrames>
				<s:iterator value="%{stm_rec_totalData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</totalDataFrames>
			<unicastDataFrames>
				<s:iterator value="%{stm_rec_unicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</unicastDataFrames>
			<multicastDataFrames>
				<s:iterator value="%{stm_rec_multicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</multicastDataFrames>
			<broadcastDataFrames>
				<s:iterator value="%{stm_rec_broadcastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</broadcastDataFrames>
		</receive>
	</ssidTrafficMetrics>
	
	<ssidTroubleShooting>
		<transmitTitle>Transmitted Data</transmitTitle>
		<receiveTitle>Received Data</receiveTitle>
		<transmit>
			<errorFrames>
				<s:iterator value="%{sts_trans_totalFrameErrors}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</errorFrames>
			<droppedFrames>
				<s:iterator value="%{sts_trans_totalFramesDropped}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</droppedFrames>
		</transmit>
		<receive>
			<errorFrames>
				<s:iterator value="%{sts_rec_totalFrameErrors}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</errorFrames>
			<droppedFrames>
				<s:iterator value="%{sts_rec_totalFramesDropped}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</droppedFrames>
		</receive>
	</ssidTroubleShooting>
	
	<security>
		<rogueClientsTitle>Number of Rogue Clients</rogueClientsTitle>
		<rogueAPsTitle>Number of Rogue APs</rogueAPsTitle>
		<rogueAPs>
			<s:iterator value="%{rogueAPs}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
			</s:iterator>
		</rogueAPs>
		<rogueClients>
			<s:iterator value="%{rogueClients}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
			</s:iterator>
		</rogueClients>
	</security>
	
	<clientRadioCount>
		<clientRadioCountTitle>Client Radio Mode</clientRadioCountTitle>
		<clientCountA>
			<s:iterator value="%{radioClientCount}" status="status">
				<item>
					<date><s:property value="%{reportTimeString}" /></date>
					<count><s:property value="%{aModeCount}" /></count>
				</item>
			</s:iterator>
		</clientCountA>
		<clientCountB>
			<s:iterator value="%{radioClientCount}" status="status">
				<item>
					<date><s:property value="%{reportTimeString}" /></date>
					<count><s:property value="%{bModeCount}" /></count>
				</item>
			</s:iterator>
		</clientCountB>
		<clientCountG>
			<s:iterator value="%{radioClientCount}" status="status">
				<item>
					<date><s:property value="%{reportTimeString}" /></date>
					<count><s:property value="%{gModeCount}" /></count>
				</item>
			</s:iterator>
		</clientCountG>
		<clientCountNA>
			<s:iterator value="%{radioClientCount}" status="status">
				<item>
					<date><s:property value="%{reportTimeString}" /></date>
					<count><s:property value="%{naModeCount}" /></count>
				</item>
			</s:iterator>
		</clientCountNA>
		<clientCountNG>
			<s:iterator value="%{radioClientCount}" status="status">
				<item>
					<date><s:property value="%{reportTimeString}" /></date>
					<count><s:property value="%{ngModeCount}" /></count>
				</item>
			</s:iterator>
		</clientCountNG>
	</clientRadioCount>
	
	<meshNeighbors>
		<transmitTitle>Transmitted Data</transmitTitle>
		<receiveTitle>Received Data</receiveTitle>
		<rssiTitle>RSSI</rssiTitle>
		<transmit>
			<totalDataFrames>
				<s:iterator value="%{mesh_trans_totalData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</totalDataFrames>
			<beDataFrames>
				<s:iterator value="%{mesh_trans_beData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</beDataFrames>
			<bgDataFrames>
				<s:iterator value="%{mesh_trans_bgData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</bgDataFrames>
			<viDataFrames>
				<s:iterator value="%{mesh_trans_viData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</viDataFrames>
			<voDataFrames>
				<s:iterator value="%{mesh_trans_voData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</voDataFrames>
			<mgtFrames>
				<s:iterator value="%{mesh_trans_mgtData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</mgtFrames>
			<unicastDataFrames>
				<s:iterator value="%{mesh_trans_unicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</unicastDataFrames>
			<!-- multicastDataFrames>
				<s:iterator value="%{mesh_trans_multicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</multicastDataFrames>
			<broadcastDataFrames>
				<s:iterator value="%{mesh_trans_broadcastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</broadcastDataFrames> -->
		</transmit>
		
		<receive>
			<totalDataFrames>
				<s:iterator value="%{mesh_rec_totalData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</totalDataFrames>
			<mgtFrames>
				<s:iterator value="%{mesh_rec_mgtData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</mgtFrames>
			<unicastDataFrames>
				<s:iterator value="%{mesh_rec_unicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</unicastDataFrames>
			<multicastDataFrames>
				<s:iterator value="%{mesh_rec_multicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</multicastDataFrames>
			<broadcastDataFrames>
				<s:iterator value="%{mesh_rec_broadcastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</broadcastDataFrames>
		</receive>
		<rssiData>
			<s:iterator value="%{mesh_rssiData}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</rssiData>
	</meshNeighbors>
	
	<uniqueClientCount>
		<uniqueClientCountTitle>Unique Client Count</uniqueClientCountTitle>
		<uniqueClientData>
			<s:iterator value="%{uniqueClients}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</uniqueClientData>
	</uniqueClientCount>
	
	<clientSession>
		<clientMac><s:property value="%{reportClientMac}" /></clientMac>
		<clientIp><s:property value="%{clientIp}" /></clientIp>
		<clientHostName><s:property value="%{clientHostName}" /></clientHostName>
		<clientUserName><s:property value="%{clientUserName}" /></clientUserName>
		<clientApMac><s:property value="%{clientApMac}" /></clientApMac>
		<clientApName><s:property value="%{clientApName}" /></clientApName>
		<clientSSID><s:property value="%{clientSSID}" /></clientSSID>
		<clientVLAN><s:property value="%{clientVLAN}" /></clientVLAN>
		<clientUserProfile><s:property value="%{clientUserProfile}" /></clientUserProfile>
		<clientChannel><s:property value="%{clientChannel}" /></clientChannel>
		<clientAuthMethod><s:property value="%{clientAuthMethod}" /></clientAuthMethod>
		<clientEncryptionMethod><s:property value="%{clientEncryptionMethod}" /></clientEncryptionMethod>
		<clientPhysicalMode><s:property value="%{clientPhysicalMode}" /></clientPhysicalMode>
		<clientCWPUsed><s:property value="%{clientCWPUsed}" /></clientCWPUsed>
		<clientLinkUpTime><s:property value="%{clientLinkUpTime}" /></clientLinkUpTime>
		<clientBSSID><s:property value="%{clientBSSID}" /></clientBSSID>
		<clientSessionStartTime><s:property value="%{reportClientSessionStart}" /></clientSessionStartTime>
		<clientSessionEndTime><s:property value="%{reportClientSessionEnd}" /></clientSessionEndTime>
		<transmitTitle>Transmitted Data</transmitTitle>
		<receiveTitle>Received Data</receiveTitle>
		<dataOctetsTitle>Data Octets</dataOctetsTitle>
		<lastRateTitle>Last Rate (Kbps)</lastRateTitle>
		<rssiTitle>Signal Quality</rssiTitle>

		<transmit>
			<totalDataFrames>
				<s:iterator value="%{client_trans_totalData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</totalDataFrames>
			<beDataFrames>
				<s:iterator value="%{client_trans_beData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</beDataFrames>
			<bgDataFrames>
				<s:iterator value="%{client_trans_bgData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</bgDataFrames>
			<viDataFrames>
				<s:iterator value="%{client_trans_viData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</viDataFrames>
			<voDataFrames>
				<s:iterator value="%{client_trans_voData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</voDataFrames>
			<mgtDataFrames>
				<s:iterator value="%{client_trans_mgtData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</mgtDataFrames>
			<unicastDataFrames>
				<s:iterator value="%{client_trans_unicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</unicastDataFrames>
			<airTime>
				<s:iterator value="%{client_trans_airTime}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</airTime>
			<frameDrop>
				<s:iterator value="%{client_trans_drop}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</frameDrop>
			<rateDistribution>
				<s:iterator value="client_trans_dateTimeList" status="status" id="tmpList">
					<item>
						<date><s:property/></date>
						<s:iterator value="client_trans_rate_dis.get(#tmpList)" status="status2">
						<<s:property value="value" />><s:property value="id" />
						</<s:property value="value" />>
						</s:iterator>
					</item>
				</s:iterator>
			</rateDistribution>
			<rateSuccDistribution>
				<s:iterator value="%{client_trans_rate_succ_dis}" status="status">
				<item>
					<date><s:property value="%{key}" /></date>
					<count><s:property value="%{value}" /></count>
					<detail><s:property value="%{toopTip}"/></detail>
				</item>
				</s:iterator>
			</rateSuccDistribution>
			<rateType>
				<s:iterator value="client_trans_rateTypeList" status="status" >
				<keySet>
					<typeString><s:property/></typeString>
				</keySet>
				</s:iterator>
			</rateType>
		</transmit>
		
		<receive>
			<totalDataFrames>
				<s:iterator value="%{client_rec_totalData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</totalDataFrames>
			<mgtDataFrames>
				<s:iterator value="%{client_rec_mgtData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</mgtDataFrames>
			<unicastDataFrames>
				<s:iterator value="%{client_rec_unicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</unicastDataFrames>
			<multicastDataFrames>
				<s:iterator value="%{client_rec_multicastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</multicastDataFrames>
			<broadcastDataFrames>
				<s:iterator value="%{client_rec_broadcastData}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</broadcastDataFrames>
			<micFailures>
				<s:iterator value="%{client_rec_micfailures}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</micFailures>
			<airTime>
				<s:iterator value="%{client_rec_airTime}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</airTime>
			<frameDrop>
				<s:iterator value="%{client_rec_drop}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
				</s:iterator>
			</frameDrop>
			<rateDistribution>
				<s:iterator value="client_rec_dateTimeList" status="status" id="tmpList">
					<item>
						<date><s:property/></date>
						<s:iterator value="client_rec_rate_dis.get(#tmpList)" status="status2">
						<<s:property value="value" />><s:property value="id" />
						</<s:property value="value" />>
						</s:iterator>
					</item>
				</s:iterator>
			</rateDistribution>
			<rateSuccDistribution>
				<s:iterator value="%{client_rec_rate_succ_dis}" status="status">
				<item>
					<date><s:property value="%{key}" /></date>
					<count><s:property value="%{value}" /></count>
					<detail><s:property value="%{toopTip}" /></detail>
				</item>
				</s:iterator>
			</rateSuccDistribution>
			<rateType>
				<s:iterator value="client_rec_rateTypeList" status="status" >
				<keySet>
					<typeString><s:property/></typeString>
				</keySet>
				</s:iterator>
			</rateType>
		</receive>
		<clientScore>
			<s:iterator value="%{client_score}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</clientScore>
		<clientRadioScore>
			<s:iterator value="%{client_radio_score}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</clientRadioScore>
		<clientIpNetworkScore>
			<s:iterator value="%{client_ipnetwork_score}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</clientIpNetworkScore>
		<clientApplicationScore>
			<s:iterator value="%{client_application_score}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</clientApplicationScore>
		<bandWidth>
			<s:iterator value="%{client_bandwidth}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</bandWidth>
		<slaCount>
			<s:iterator value="%{client_slacount}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</slaCount>
		<txdataOctets>
			<s:iterator value="%{client_trans_dataOctets}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</txdataOctets>
		<rxdataOctets>
			<s:iterator value="%{client_rec_dataOctets}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</rxdataOctets>
		<txLastRate>
			<s:iterator value="%{client_trans_lastrate}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</txLastRate>
		<rxLastRate>
			<s:iterator value="%{client_rec_lastrate}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</rxLastRate>
		<clientRssi>
			<s:iterator value="%{client_rssi}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</clientRssi>
		<clientSoNR>
			<s:iterator value="%{client_signal_to_noise}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</clientSoNR>
		<lstReportClientSession>
			<s:iterator value="%{currentLstReportClientSession}" id="currentLstReportClientSession" status="status">
				<item>
					<sessionTime><s:property value="%{#currentLstReportClientSession.sessionTime}" /></sessionTime>
					<buttonColor><s:property value="%{#currentLstReportClientSession.buttonColor}" /></buttonColor>
					<buttonWidth><s:property value="%{#currentLstReportClientSession.buttonWidth}" /></buttonWidth>
					<buttonHeigth><s:property value="%{#currentLstReportClientSession.buttonHeigth}" /></buttonHeigth>
				</item>
			</s:iterator>
		</lstReportClientSession>
		<previousButton>
			<previousPage><s:property value="%{previousPage}" /></previousPage>
			<previousShowButton><s:property value="%{previousShowButton}" /></previousShowButton>
		</previousButton>
		<nextButton>
			<nextPage><s:property value="%{nextPage}" /></nextPage>
			<nextShowButton><s:property value="%{nextShowButton}" /></nextShowButton>
		</nextButton>
	</clientSession>
	<airTime>
		<airTimeTitle>Airtime (%)</airTimeTitle>
		<wifi0AirTimeTitle>Wifi0 Airtime (%)</wifi0AirTimeTitle>
		<wifi1AirTimeTitle>Wifi1 Airtime (%)</wifi1AirTimeTitle>
		<transmitAirTime>
			<s:iterator value="%{transmit_airTime}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
			</s:iterator>
		</transmitAirTime>
		<receiveAirTime>
			<s:iterator value="%{receive_airTime}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
			</s:iterator>
		</receiveAirTime>
		<transmitWifi1AirTime>
			<s:iterator value="%{wifi1_transmit_airTime}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
			</s:iterator>
		</transmitWifi1AirTime>
		<receiveWifi1AirTime>
			<s:iterator value="%{wifi1_receive_airTime}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{key}" /></count>
			</item>
			</s:iterator>
		</receiveWifi1AirTime>
	</airTime>
	<hiveApSla>
		<hiveApSlaTitle>Accumulative Client Count on <s:text name="hiveAp.tag"/> With SLA</hiveApSlaTitle>
		<badSla>
			<s:iterator value="%{lstHiveApSlaBad}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</badSla>
		<alertSla>
			<s:iterator value="%{lstHiveApSlaAlert}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</alertSla>
	</hiveApSla>
	<clientSla>
		<clientSlaTitle>SLA Events</clientSlaTitle>
		<badSla>
			<s:iterator value="%{lstClientSlaBad}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</badSla>
		<alertSla>
			<s:iterator value="%{lstClientSlaAlert}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</alertSla>
	</clientSla>
	<maxClient>
		<maxClientTitle>Max Concurrent Clients on the Network</maxClientTitle>
		<clientCount>
			<s:iterator value="%{maxClients}" status="status">
				<item>
					<date><s:property value="%{value}" /></date>
					<count><s:property value="%{id}" /></count>
				</item>
			</s:iterator>
		</clientCount>
		
	</maxClient>
	
</report>
