package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.mo.SimulateHiveAP;
import com.ah.be.communication.mo.SimulateHiveAPResult;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

@SuppressWarnings("serial")
public class BeSimulateHiveAPEvent extends BeCommunicationEvent {

	private List<SimulateHiveAP>		simulateAPList;

	private boolean						needReturnResult;

	private List<SimulateHiveAPResult>	simulateAPResultList;

	public BeSimulateHiveAPEvent() {
		msgType = BeCommunicationConstant.MESSAGETYPE_SIMULATEHIVEAPREQ;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		if (simulateAPList == null || simulateAPList.size() == 0) {
			throw new BeCommunicationEncodeException("simulateAPList field value is needed!");
		}

		try {
			int bufLength = 6;

			bufLength += 1; // return flag
			bufLength += 2; // ap element number
			for (SimulateHiveAP ap : simulateAPList) {
				bufLength += 2; // simple ap length
				bufLength += ap.getDataLength();
			}

			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_SIMULATEAPDESCRIPTION);
			buf.putInt(bufLength - 6);

			buf.put((byte) (needReturnResult ? 1 : 0));
			buf.putShort((short) simulateAPList.size());
			for (SimulateHiveAP ap : simulateAPList) {
				buf.putShort(ap.getDataLength());
				buf.putShort(ap.getSimulateNumber());

				String macAddr = ap.getMacAddress() != null ? ap.getMacAddress() : "";
				buf.put((byte) macAddr.length());
				buf.put(macAddr.getBytes());

				buf.putShort(ap.getCode());
				buf
						.putInt((ap.getIpAddress() == null || ap.getIpAddress().trim().length() == 0) ? 0
								: AhEncoder.ip2Int(ap.getIpAddress()));
				buf.putInt((ap.getNetmask() == null || ap.getNetmask().trim().length() == 0) ? 0
						: AhEncoder.ip2Int(ap.getNetmask()));
				buf.putInt((ap.getGateway() == null || ap.getGateway().trim().length() == 0) ? 0
						: AhEncoder.ip2Int(ap.getGateway()));
				buf.putInt(ap.getCountryCode());
				buf.putInt(ap.getRegionCode());
				buf.put(ap.getIpType());
				buf.put(ap.getApType());

				String wtpName = ap.getWtpName() != null ? ap.getWtpName() : "";
				buf.put((byte) wtpName.length());
				buf.put(wtpName.getBytes());

				String sn = ap.getSn() != null ? ap.getSn() : "";
				buf.put((byte) sn.length());
				buf.put(sn.getBytes());

				String softVer = ap.getSoftVer() != null ? ap.getSoftVer() : "";
				buf.put((byte) softVer.length());
				buf.put(softVer.getBytes());

				String displayVer = ap.getDisplaySoftVer() != null ? ap.getDisplaySoftVer() : "";
				buf.put((byte) displayVer.length());
				buf.put(displayVer.getBytes());

				String location = ap.getLocation() != null ? ap.getLocation() : "";
				buf.putShort((short) location.length());
				buf.put(location.getBytes());

				String productName = ap.getProductName() != null ? ap.getProductName() : "";
				buf.put((byte) productName.length());
				buf.put(productName.getBytes());

				String vhmName = ap.getVhmName() != null ? ap.getVhmName() : "";
				buf.put((byte) vhmName.length());
				buf.put(vhmName.getBytes());

				String clientInfo = ap.getClientInfo() != null ? ap.getClientInfo() : "";
				buf.putShort((short) clientInfo.length());
				buf.put(clientInfo.getBytes());

				buf.putShort(ap.getApModel());
				buf.put(ap.getTimezone());
				buf.putShort(ap.getWifi0Channel());
				buf.putShort(ap.getWifi0Power());
				buf.putShort(ap.getWifi1Channel());
				buf.putShort(ap.getWifi1Power());
			}

			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeSimulateHiveAPEvent.buildPacket() catch exception", e);
		}
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			ByteBuffer buf = ByteBuffer.wrap(data);
			while (buf.hasRemaining()) {
				short msgType = buf.getShort();
				buf.getInt();

				if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER) {
					byte macLen = buf.get();
					apMac = AhDecoder.bytes2String(buf, AhDecoder.byte2int(macLen)).toUpperCase();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR) {
					result = buf.get();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_SIMULATEAPRESULT) {
					short apNum = buf.getShort();
					simulateAPResultList = new ArrayList<SimulateHiveAPResult>(apNum);

					for (int i = 0; i < apNum; i++) {
						buf.getShort(); //length
						byte len = buf.get();
						String macAddr = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len))
								.toUpperCase();
						len = buf.get();
						String wtpName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
						short code = buf.getShort();

						SimulateHiveAPResult result = new SimulateHiveAPResult();
						result.setMacAddress(macAddr);
						result.setWtpName(wtpName);
						result.setCode(code);

						simulateAPResultList.add(result);
					}
				} else {
					throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeGetStatisticEvent, type value = "
									+ msgType);
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeSimulateHiveAPEvent.parsePacket() catch exception", e);
		}
	}

	public List<SimulateHiveAP> getSimulateAPList() {
		return simulateAPList;
	}

	public void setSimulateAPList(List<SimulateHiveAP> simulateAPList) {
		this.simulateAPList = simulateAPList;
	}

	public void setHiveAPList(List<HiveAp> apList) {
		simulateAPList = new ArrayList<SimulateHiveAP>(apList.size());
		for (HiveAp ap : apList) {
			if (!ap.isSimulated()) {
				continue;
			}

			SimulateHiveAP simulateAP = new SimulateHiveAP();
			simulateAP.setApType((byte) ap.getHiveApType());
			simulateAP.setClientInfo(ap.getSimulateClientInfo());
			simulateAP.setCode((short) ap.getSimulateCode());
			simulateAP.setCountryCode(ap.getCountryCode());
			simulateAP.setDisplaySoftVer(ap.getDisplayVer());
			simulateAP.setGateway(ap.getGateway());
			simulateAP.setIpAddress(ap.getIpAddress());
			simulateAP.setIpType(ap.isDhcp() ? (byte) 0 : (byte) 1);
			simulateAP.setLocation(ap.getLocation());
			simulateAP.setMacAddress(ap.getMacAddress());
			simulateAP.setNetmask(ap.getNetmask());
			simulateAP.setProductName(ap.getProductName());
			simulateAP.setRegionCode(ap.getRegionCode());
			simulateAP.setSimulateNumber((short) 1);
			simulateAP.setSn(ap.getSerialNumber());
			simulateAP.setSoftVer(ap.getSoftVer());
			simulateAP.setVhmName(ap.getOwner().getDomainName());
			simulateAP.setWtpName(ap.getHostName());
			simulateAP.setApModel(ap.getHiveApModel());
			simulateAP.setTimezone((byte) (ap.getOwner().getTimeZone().getOffset(
					System.currentTimeMillis()) / 3600000 + 13));
			if (ap.getWifi0() != null) {
				simulateAP.setWifi0Channel((short) ap.getWifi0().getChannel());
				simulateAP.setWifi0Power((short) ap.getWifi0().getPower());
			}
			if (ap.getWifi1() != null) {
				simulateAP.setWifi1Channel((short) ap.getWifi1().getChannel());
				simulateAP.setWifi1Power((short) ap.getWifi1().getPower());
			}

			simulateAPList.add(simulateAP);
		}
	}

	public List<SimulateHiveAPResult> getSimulateAPResultList() {
		return simulateAPResultList;
	}

	public void setSimulateAPResultList(List<SimulateHiveAPResult> simualteAPResultList) {
		this.simulateAPResultList = simualteAPResultList;
	}

	public boolean isNeedReturnResult() {
		return needReturnResult;
	}

	public void setNeedReturnResult(boolean needReturnResult) {
		this.needReturnResult = needReturnResult;
	}
}
