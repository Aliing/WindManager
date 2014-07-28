package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class UpdateDNS extends AbstractElement {

	private static final long					serialVersionUID	= 1L;

	private static final Tracer					log					= new Tracer(UpdateDNS.class
																			.getSimpleName());

	/* DNS Update Info */
	private Collection<HMUpdateSoftwareInfo>	dnsUpdateInfos;

	public UpdateDNS() {
		super();
	}

	public Collection<HMUpdateSoftwareInfo> getDnsUpdateInfos() {
		return dnsUpdateInfos;
	}

	public void setDnsUpdateInfos(Collection<HMUpdateSoftwareInfo> dnsUpdateInfos) {
		this.dnsUpdateInfos = dnsUpdateInfos;
	}

	@Override
	public short getElemType() {
		return UPDATE_DNS;
	}

	@Override
	public String getElemName() {
		return "Update DNS";
	}

	/*-
	 * API Engine/Client Update DNS Format.
	 *
	 * 0                   1                   2                   3
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |         Number of VHMs        |       VHM1 Name Length        |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                         VHM1 Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                    Migrated HHM IP Address                    |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                          VHM[2..N] ...
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

			/* Number of VHMs */
			int vhmNum = dnsUpdateInfos.size();
			log.debug("encode", "Number of VHMs: " + vhmNum);
			bb.putShort((short) vhmNum);

			for (HMUpdateSoftwareInfo dnsUpdateInfo : dnsUpdateInfos) {
				/* VHM Name */
				String vhmName = dnsUpdateInfo.getDomainName();
				log.debug("encode", "VHM Name: " + vhmName);
				byte[] toBytes = vhmName.getBytes("iso-8859-1");
				int vhmNameLen = toBytes.length;
				bb.putShort((short) vhmNameLen);
				bb.put(toBytes);

				/* Migrated HHM IP */
				String ip = dnsUpdateInfo.getIpAddress();
				// log.debug("encode", "Migrated HHM IP: " + ip);
				// int i_ipv4 = new Long(AhEncoder.ip2Long(ip)).intValue();
				// bb.putInt(i_ipv4);
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
	 * API Engine/Client Update DNS Format.
	 *
	 * 0                   1                   2                   3
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |         Number of VHMs        |       VHM1 Name Length        |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                         VHM1 Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                    Migrated HHM IP Address                    |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                          VHM[2..N] ...
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

			/* Number of VHMs */
			short vhmNum = bb.getShort();

			dnsUpdateInfos = new ArrayList<HMUpdateSoftwareInfo>(vhmNum);

			for (int i = 0; i < vhmNum; i++) {
				HMUpdateSoftwareInfo dnsUpdateInfo = new HMUpdateSoftwareInfo();

				/* VHM Name */
				short vhmNameLen = bb.getShort();
				String vhmName = AhDecoder.bytes2String(bb, vhmNameLen);
				log.debug("decode", "VHM Name[len/value]: " + vhmNameLen + "/" + vhmName);
				dnsUpdateInfo.setDomainName(vhmName);

				/* Migrated HHM IP */
				// int ipv4 = bb.getInt();
				// String ipAddr = AhDecoder.long2Ip(ipv4);
				// log.debug("decode", "Migrated HHM IP[int/string]: " + ipv4 + "/" + ipAddr);
				// dnsUpdateInfo.setIpAddress(ipAddr);
				short lenx = bb.getShort();
				String ipAddr = AhDecoder.bytes2String(bb, lenx);
				dnsUpdateInfo.setIpAddress(ipAddr);

				dnsUpdateInfos.add(dnsUpdateInfo);
			}

			// End Position
			int endPos = bb.position();
			log.debug("decode", "End Position: " + endPos);

			return endPos - startPos;
		} catch (BufferUnderflowException e) {
			throw new DecodeException(
					"Incorrect element length '" + len + "' for " + getElemName(), e);
		} catch (Exception e) {
			throw new DecodeException("Decoding '" + getElemName() + "' Error.", e);
		}
	}

}
