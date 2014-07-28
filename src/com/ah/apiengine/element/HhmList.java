package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class HhmList extends AbstractElement {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(HhmList.class.getSimpleName());

	public HhmList() {
		super();
	}

	private Collection<HhmUpgradeVersionInfo> hhms;

	public Collection<HhmUpgradeVersionInfo> getHhms() {
		return hhms;
	}

	public void setHhms(Collection<HhmUpgradeVersionInfo> hhms) {
		this.hhms = hhms;
	}

	@Override
	public short getElemType() {
		return HHM_LIST;
	}

	@Override
	public String getElemName() {
		return "HHM List";
	}

	/*-
	 * API Engine/Client HHM List Format.
	 *
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |         Number of HHMs        |        HHM1 IP Address
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *           HHM1 IP Address       |  HHM1 Ver Len | HHM Version ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |         Number of VHMs        |      Remaining AP Number
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *        Remaining AP Number      |         User Name Len         |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         User Name ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |        Password Length        |          Password ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         HHM[2..N] ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     * Length > 2
	 */
	@Override
	public int encode(ByteBuffer bb) throws EncodeException {
		try {
			// Element Header
			int elemHeaderLen = encodeElementHeader(bb, 0);
			log.debug("encode", "Element Header Length: " + elemHeaderLen);

			// Start Position
			int startPos = bb.position();
			log.debug("encode", "Start Position: " + startPos);

			/* Number of HHMs*/
			int hhmNum = hhms.size();
			log.debug("encode", "Number of HHMs: " + hhmNum);
			bb.putShort((short) hhmNum);

			for (HhmUpgradeVersionInfo hhm : hhms) {
				/* HHM IP */
				String ip = hhm.getIpAddress();
				log.debug("encode", "HHM IP: " + ip);
				//int i_ip = new Long(AhEncoder.ip2Long(ip)).intValue();
				int i_ip = 0;
				bb.putInt(i_ip);

				/* HHM Version */
				String version = hhm.getHmVersion();
				log.debug("encode", "HHM Version: " + version);
				byte[] toBytes = version.getBytes("iso-8859-1");
				int verLen = toBytes.length;
				bb.put((byte) verLen);
				bb.put(toBytes);

				/* Number of VHMs  */
				int vhmNum = hhm.getLeftVhmCount();
				log.debug("encode", "Number of VHMs: " + vhmNum);
				bb.putShort((short) vhmNum);

				/* Remaining AP Number  */
				int remainApNum = hhm.getLeftApCount();
				log.debug("encode", "Remaining AP Number: " + remainApNum);
				bb.putInt(remainApNum);

				/* HHM Super User Name */
				String userName = hhm.getUserName();
				log.debug("encode", "HHM User Name: " + userName);
				toBytes = userName.getBytes("iso-8859-1");
				int userNameLen = toBytes.length;
				bb.putShort((short) userNameLen);
				bb.put(toBytes);

				/* HHM Super User Password */
				String password = hhm.getPassword();
				log.debug("encode", "HHM Password: " + password);
				toBytes = password.getBytes("iso-8859-1");
				int pwdLen = toBytes.length;
				bb.putShort((short) pwdLen);
				bb.put(toBytes);

				/* HHM host-name or IP */
				toBytes = ip.getBytes("iso-8859-1");
				int len = toBytes.length;
				bb.putShort((short) len);
				bb.put(toBytes);
			}

			// End Position
			int endPos = bb.position();
			log.debug("encode", "End Position: " + endPos);

			// Element Length
			int elemBodyLen = endPos - startPos;
			log.debug("encode", "Element Length: " + elemBodyLen);

			// Fill pending element length.
			fillPendingElementLength(bb, startPos, elemBodyLen);

			return elemHeaderLen + elemBodyLen;
		} catch (Exception e) {
			throw new EncodeException("Encoding '" + getElemName() + "' Error.", e);
		}
	}

	/*-
	 * API Engine/Client HHM List Format.
	 *
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |         Number of HHMs        |        HHM1 IP Address
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *           HHM1 IP Address       |  HHM1 Ver Len | HHM Version ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |         Number of VHMs        |      Remaining AP Number
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *        Remaining AP Number      |         User Name Len         |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         User Name ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |        Password Length        |          Password ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         HHM[2..N] ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     * Length > 2
	 */
	@Override
	public int decode(ByteBuffer bb, int len) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.debug("decode", "Start Position: " + startPos);

			/* Number of HHMs */
			short hhmNum = bb.getShort();
			hhms = new ArrayList<HhmUpgradeVersionInfo>(hhmNum);

			for (int i = 0; i < hhmNum; i++) {
				HhmUpgradeVersionInfo hhmInfo = new HhmUpgradeVersionInfo();

				/* HHM IP */
				bb.getInt();
				// int ipv4 = bb.getInt();
				// String ipAddr = AhDecoder.long2Ip(ipv4);
				// log.debug("decode", "HHM IP[int/string]: " + ipv4 + "/" + ipAddr);
				// hhmInfo.setIpAddress(ipAddr);

				/* HHM Version */
				byte verLen = bb.get();
				String version = AhDecoder.bytes2String(bb, verLen);
				log.debug("decode", "HHM Version[len/value]: " + verLen + "/" + version);
				hhmInfo.setHmVersion(version);

				/* Number of VHMs */
				short vhmNum = bb.getShort();
				log.debug("decode", "Number of VHMs: " + vhmNum);
				hhmInfo.setLeftVhmCount(vhmNum);

				/* Remaining AP Number */
				int remainApNum = bb.getInt();
				log.debug("decode", "Remaining AP Number: " + remainApNum);
				hhmInfo.setLeftApCount(remainApNum);

				/* HHM Super User Name */
 				short userNameLen = bb.getShort();
				String userName = AhDecoder.bytes2String(bb, userNameLen);
				log.debug("decode", "HHM User Name[len/value]: " + userNameLen + "/" + userName);
				hhmInfo.setUserName(userName);

				/* HHM Super User Password */
 				short pwdLen = bb.getShort();
				String password = AhDecoder.bytes2String(bb, pwdLen);
				log.debug("decode", "HHM User Password[len/value]: " + pwdLen + "/" + password);
				hhmInfo.setPassword(password);

				short xlen = -1;
				/* HHM host-name or IP */
				xlen = bb.getShort();
				String ip = AhDecoder.bytes2String(bb, xlen);
				hhmInfo.setIpAddress(ip);

				hhms.add(hhmInfo);
			}

			// End Position
			int endPos = bb.position();
			log.debug("decode", "End Position: " + endPos);

			return endPos - startPos;
		} catch (BufferUnderflowException e) {
			throw new DecodeException("Incorrect element length '" + len + "' for " + getElemName(), e);
		} catch (Exception e) {
			throw new DecodeException("Decoding '" + getElemName() + "' Error.", e);
		}
	}

}