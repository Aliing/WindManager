package com.ah.be.ls.processor2;

import java.nio.ByteBuffer;
import java.util.List;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.ls.data2.ErrorResponse;
import com.ah.be.ls.data2.Header;
import com.ah.be.ls.data2.ResponseTxObjectSample;
import com.ah.be.ls.data2.TxObject;
import com.ah.be.ls.util.CommConst;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;
import com.ah.util.http.HttpCommunication;

public class TxProcessor {

//	private static final Log log = LogFactory.getLog("commonlog.TxProcessor");
                  private static final Tracer log = new Tracer(TxProcessor.class.getSimpleName());
	private TxObject requestObj;
	private TxObject responseObj;

	public TxObject getResponseTxObject() {
		return responseObj;
	}

	public void setRequestTxObject(TxObject obj) {
		this.requestObj = obj;
	}

	public byte[] buildPacket() {
		if (requestObj == null) {
			return new byte[0];
		}
		ByteBuffer buffer = requestObj.pack();
		byte[] outBytes = new byte[buffer.limit()];
		buffer.get(outBytes);
		return outBytes;
	}

	public void parsePacket(byte packetType, byte[] input) {
		ByteBuffer buf = ByteBuffer.wrap(input);
		switch (packetType) {
		case CommConst.PacketType_Error_Response:
			responseObj = new ErrorResponse();
			break;
		case CommConst.PacketType_Sample_Response:
			responseObj = new ResponseTxObjectSample();
			break;
		default:
			break;
		}
		if (responseObj != null) {
			responseObj.unpack(buf);
		}
	}

	private byte packetType;

	public byte getPacketType() {
		return packetType;
	}

	public void setPacketType(byte packetType) {
		this.packetType = packetType;
	}

	public void run() throws Exception {
		byte[] sendData = buildPacket();

		Header sendHeader = new Header();
		sendHeader.setPacketType(getPacketType());
		sendHeader.setProtocolVersion(CommConst.Protocol_Version);
		sendHeader.setLength(sendData.length);
		sendHeader.setSecretFlag(true);

		byte[] sendBytes = PacketUtil.join(sendHeader, sendData);

		HttpCommunication hc = new HttpCommunication(getLsMessageServerURL());
		initHttpProxySetting(hc);

		byte[] recvBytes = hc.sendAndReceive(sendBytes);

		if (recvBytes.length > 0) {
			if (recvBytes.length == 1) {
				byte a = recvBytes[0];
				log.warn("TxProcessor,result="  + a + ", no this packet type:" + this.packetType);
				throw new Exception("TxProcessor,no this transaction");
			} else {
				Header recvHeader = new Header();
				byte[] recvData = PacketUtil.split(ByteBuffer.wrap(recvBytes), recvHeader);
				if ((recvHeader.getType() != sendHeader.getType() + 1)
						&& (recvHeader.getType() != CommConst.PacketType_Error_Response)) {
					log.error("TxProcessor,response header type mismatch! require "
							+ (sendHeader.getType() + 1) + " but " + recvHeader.getType() + "!");
					throw new Exception("TxProcessor,response error");
				}
				parsePacket(recvHeader.getType(), recvData);
			}
		} else {
			throw new Exception("TxProcessor,no response");
		}
	}

	private void initHttpProxySetting(HttpCommunication hc) {
		if (proxySetting != null) {
			hc.setEnableProxyFlag(proxySetting.isEnableProxy());
			hc.setProxyHost(proxySetting.getProxyHost());
			hc.setProxyPort(proxySetting.getProxyPort());
			hc.setProxyUsername(proxySetting.getProxyUsername());
			hc.setProxyPassword(proxySetting.getProxyPassword());
		} else {
			HMServicesSettings setting = null;
			List<HMServicesSettings> bos = QueryUtil.executeQuery(HMServicesSettings.class, null,
					null, BoMgmt.getDomainMgmt().getHomeDomain().getId());
			if (!bos.isEmpty()) {
				setting = bos.get(0);
			}

			boolean enableProxyFlag = false;
			if (setting != null) {
				enableProxyFlag = setting.isEnableProxy();
			}

			if (enableProxyFlag) {
				hc.setEnableProxyFlag(true);
				hc.setProxyHost(setting.getProxyServer());
				hc.setProxyPort(setting.getProxyPort());
				hc.setProxyUsername(setting.getProxyUserName());
				hc.setProxyPassword(setting.getProxyPassword());
			}
		}
	}

	private static String getLsMessageServerURL() {
		String lsHost = HmBeActivationUtil.getLicenseServerInfo().getLserverUrl();
		return "https://" + lsHost + "/messageserver";
	}

	private ProxySetting proxySetting;

	public ProxySetting getProxySetting() {
		return proxySetting;
	}

	public void setProxySetting(ProxySetting proxySetting) {
		this.proxySetting = proxySetting;
	}

}