/**
 *@filename		client.java
 *@version
 *@author		xiaolanbao
 *@createtime	2009-4-7 09:37:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.ls;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.ls.data2.Header;
import com.ah.be.ls.processor.DataProcessor;
import com.ah.be.ls.processor2.PacketUtil;
import com.ah.be.ls.util.CommConst;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;
import com.ah.util.http.HttpCommunication;

public class client {

	private static final Tracer log = new Tracer(client.class.getSimpleName());
                  
	public static boolean sPackProcesser(DataProcessor oprocesser) {
		try {
			byte[] bData = new byte[CommConst.BUFFER_SIZE - CommConst.Packet_Head_Size];
			Arrays.fill(bData, (byte) 0);

			int iDataLength = oprocesser.do_build_packet(bData);

			if (iDataLength <= 0) {
				log.error("sPackProcesser","build packet is error");
				return false;
			}

			// packet head data
			Header sendHeader = new Header();
			sendHeader.setPacketType(oprocesser.get_packet_type());
			sendHeader.setProtocolVersion(CommConst.Protocol_Version);
			sendHeader.setLength(iDataLength);
			sendHeader.setSecretFlag(true);

			byte[] content = new byte[iDataLength];
			System.arraycopy(bData, 0, content, 0, iDataLength);

			// join header and content
			byte[] sendBytes = PacketUtil.join(sendHeader, content);

			HttpCommunication hc = new HttpCommunication(getLsMessageServerURL());

			initProxySetting(hc);

			// send and receive
			byte[] recvBytes = hc.sendAndReceive(sendBytes);

			if (oprocesser.is_need_response()) {
				if (recvBytes == null || recvBytes.length == 0) {
					log.error("sPackProcesser","no receive content!");
					return false;
				}

				if (CommConst.Error_Response_Flag == recvBytes[0]) {
					// error response add log
					log.error("sPackProcesser","receive error response");
					return false;
				}

				Header recvHeader = new Header();
				byte[] recvContent = PacketUtil.split(ByteBuffer.wrap(recvBytes), recvHeader);
				if (recvContent.length <= 0) {
					log.error("sPackProcesser","parse packet header error");
					return false;
				}

				if (recvHeader.getType() != sendHeader.getType() + 1) {
					// add log
					log.error("sPackProcesser","response packet type not right");
					return false;
				}

				if (oprocesser.do_parse_packet(recvContent) <= 0) {
					// add log
					log.error("sPackProcesser","parse packet error");
					return false;
				}
			}

			return true;
		} catch (Exception e) {
            log.error("sPackProcesser", e);
			return false;
		}
	}

	private static void initProxySetting(HttpCommunication hc) {
		// look proxy settings
		HMServicesSettings setting = null;
		List<HMServicesSettings> bos = QueryUtil.executeQuery(HMServicesSettings.class, null, null,
				BoMgmt.getDomainMgmt().getHomeDomain().getId());
		if (bos.size() == 1) {
			setting = bos.get(0);
		}

		boolean enableProxyFlag = false;
		if (setting != null) {
			enableProxyFlag = setting.isEnableProxy();
		}

		if (enableProxyFlag) {
			hc.setEnableProxyFlag(enableProxyFlag);
			hc.setProxyHost(setting.getProxyServer());
			hc.setProxyPort(setting.getProxyPort());

			if (setting.getProxyUserName() != null && !setting.getProxyUserName().equals("")) {
				hc.setProxyUsername(setting.getProxyUserName());
				hc.setProxyPassword(setting.getProxyPassword());
			}
		}
	}

	private static String getLsMessageServerURL() {
		String lsHost = HmBeActivationUtil.getLicenseServerInfo().getLserverUrl();
		return "https://" + lsHost + "/messageserver";
	}

}