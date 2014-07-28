package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.bo.admin.CapwapClient;

/**
 * 
 *@filename		BeCapwapClientParamConfigEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-11-23 01:52:43
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCapwapClientParamConfigEvent extends BeCommunicationEvent {

	private List<CapwapClient>	capwapClientList;

	/**
	 * Construct method
	 */
	public BeCapwapClientParamConfigEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTPARAMCONFIGREQ;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		try {
			if (capwapClientList == null || capwapClientList.size() == 0) {
				throw new BeCommunicationEncodeException("capwap client list are required.");
			}

			int bufLength = 6;
			bufLength++;
			for (CapwapClient client : capwapClientList) {
				bufLength += 15;
				bufLength += (client.getPassphrase() == null ? 0 : client.getPassphrase()
						.length());
				bufLength += (client.getPrimaryCapwapIP() == null ? 0 : client.getPrimaryCapwapIP()
						.length());
				bufLength += (client.getBackupCapwapIP() == null ? 0 : client.getBackupCapwapIP()
						.length());
			}
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTDESCRIPTOR);
			buf.putInt(bufLength - 6);

			buf.put((byte) capwapClientList.size());
			for (CapwapClient client : capwapClientList) {
				int length = 0;
				
//				String primaryCapwap = client.getPrimaryCapwapIP();
//				String backupCapwap = client.getBackupCapwapIP();
////				if (primaryCapwap != null && primaryCapwap.length() > 0) {
//					try {
//						InetAddress address = InetAddress.getByName(primaryCapwap);
//						primaryCapwap = address.getHostAddress();
//					} catch (Exception e) {
//						throw new Exception("Unknown host " + primaryCapwap);
//					}
//				}
//				
//				if (backupCapwap != null && backupCapwap.length() > 0) {
//					try {
//						InetAddress address = InetAddress.getByName(backupCapwap);
//						backupCapwap = address.getHostAddress();
//					} catch (Exception e) {
//						throw new Exception("Unknown host " + backupCapwap);
//					}
//				}
//				
				length = 13;
				length += (client.getPassphrase() == null ? 0 : client.getPassphrase()
						.length());
				length += (client.getPrimaryCapwapIP() == null ? 0 : client.getPrimaryCapwapIP()
						.length());
				length += (client.getBackupCapwapIP() == null ? 0 : client.getBackupCapwapIP()
						.length());
				buf.putShort((short)length);
				buf.put(client.getServerType());
				buf.put((byte) (client.isCapwapEnable() ? 1 : 0));
				
				length = (client.getPrimaryCapwapIP() == null ? 0 : client.getPrimaryCapwapIP().length());
				buf.put((byte)length);
				if(length > 0) {
					buf.put(client.getPrimaryCapwapIP().getBytes());
				}
				length = (client.getBackupCapwapIP() == null ? 0 : client.getBackupCapwapIP().length());
				buf.put((byte)length);
				if(length > 0) {
					buf.put(client.getBackupCapwapIP().getBytes());
				}
				
				buf.putShort((short) client.getUdpPort());
				buf.putShort(client.getTimeOut());
				buf.putShort(client.getNeighborDeadInterval());
				buf.put((byte) (client.isDtlsEnable() ? 1 : 0));
				buf.put((byte) (client.getPassphrase() == null ? 0 : client.getPassphrase()
						.length()));
				if (client.getPassphrase() != null && client.getPassphrase().length() > 0) {
					buf.put(client.getPassphrase().getBytes());
				}
				buf.put((byte)client.getTransportMode());
			}

			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeCapwapClientParamConfigEvent.buildPacket() catch exception", e);
		}
	}

	public List<CapwapClient> getCapwapClientList() {
		return capwapClientList;
	}

	public void setCapwapClientList(List<CapwapClient> capwapClientList) {
		this.capwapClientList = capwapClientList;
	}

}
