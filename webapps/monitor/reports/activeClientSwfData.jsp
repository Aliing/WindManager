<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/xml");
%>
<?xml version="1.0" encoding="utf-8"?>
<report>
	<activeClientSwf>
		<transmitTitle>Transmitted Data</transmitTitle>
		<receiveTitle>Received Data</receiveTitle>
		<dataOctetsTitle>Data Octets</dataOctetsTitle>
		<lastRateTitle>Last Rate (Kbps)</lastRateTitle>
		<rssiTitle>Signal Quality</rssiTitle>
		<slaTitle>Bandwidth Sentinel Conformance</slaTitle>

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
					<detail><s:property value="%{toopTip}"/></detail>
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
		<clientSla>
			<s:iterator value="%{client_sla}" status="status">
			<item>
				<date><s:property value="%{value}" /></date>
				<count><s:property value="%{id}" /></count>
			</item>
			</s:iterator>
		</clientSla>
	</activeClientSwf>
</report>
