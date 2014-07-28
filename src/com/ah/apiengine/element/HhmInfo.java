package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class HhmInfo extends AbstractElement {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(HhmInfo.class.getSimpleName());

	/* Version */
	private String version;

	/* Build Time */
	private int buildTime;

	/* Model Number */
	private String modelNum;

	/* System UP Time */
	private int systemUpTime;

	/* Maximum number of accepted HiveAPs */
	private int maxApNum;

	/* Maximum number of VHMs */
	private short maxVhmNum;

	/* HA Status */
	private byte haStatus;

	public HhmInfo() {
		super();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getBuildTime() {
		return buildTime;
	}

	public void setBuildTime(int buildTime) {
		this.buildTime = buildTime;
	}

	public String getModelNum() {
		return modelNum;
	}

	public void setModelNum(String modelNum) {
		this.modelNum = modelNum;
	}

	public int getSystemUpTime() {
		return systemUpTime;
	}

	public void setSystemUpTime(int systemUpTime) {
		this.systemUpTime = systemUpTime;
	}

	public int getMaxApNum() {
		return maxApNum;
	}

	public void setMaxApNum(int maxApNum) {
		this.maxApNum = maxApNum;
	}

	public short getMaxVhmNum() {
		return maxVhmNum;
	}

	public void setMaxVhmNum(short maxVhmNum) {
		this.maxVhmNum = maxVhmNum;
	}

	public byte getHaStatus() {
		return haStatus;
	}

	public void setHaStatus(byte haStatus) {
		this.haStatus = haStatus;
	}

	@Override
	public short getElemType() {
		return HHM_INFO;
	}

	@Override
	public String getElemName() {
		return "HHM Information";
	}

	/*-
	 * API Engine/Client HHM Information Format.
	 *
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * | Version Length|                  Version ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                           Build Time                          |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |    Model Number Length    |          Model Number ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         System UP Time                        |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |               Maximum Number of accepted HiveAPs              |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |     Maximum Number of VHMs    |   HA Status   |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     * Length > 18
     *
     * HA Status:
     *
     * 0 - UNKNOWN
     * 1 - STAND ALONG
     * 2 - MASTER
     * 3 - SLAVE
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

			/* Version */
			log.debug("encode", "Version: " + version);
			byte[] toBytes = version.getBytes("iso-8859-1");
			int verLen = toBytes.length;
			bb.put((byte) verLen);
			bb.put(toBytes);

			/* Build Time */
			log.debug("encode", "Build Time: " + buildTime);
			bb.putInt(buildTime);

			/* Model Number */
			log.debug("encode", "Model Number: " + modelNum);
			toBytes = modelNum.getBytes("iso-8859-1");
			int modelNumLen = toBytes.length;
			bb.putShort((short) modelNumLen);
			bb.put(toBytes);

			/* System UP Time */
			log.debug("encode", "System UP Time: " + systemUpTime);
			bb.putInt(systemUpTime);

			/* Maximum number of accepted HiveAPs */
			log.debug("encode", "Maximum number of accepted HiveAPs: " + maxApNum);
			bb.putInt(maxApNum);

			/* Maximum number of VHMs */
			log.debug("encode", "Maximum number of VHMs: " + maxVhmNum);
			bb.putShort(maxVhmNum);

			/* Status */
			log.debug("encode", "HA Status: " + haStatus);
			bb.put(haStatus);

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
	 * API Engine/Client HHM Information Format.
	 *
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * | Version Length|                  Version ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                           Build Time                          |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |    Model Number Length    |          Model Number ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         System UP Time                        |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |               Maximum Number of accepted HiveAPs              |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |     Maximum Number of VHMs    |   HA Status   |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     * Length > 18
	 */
	@Override
	public int decode(ByteBuffer bb, int len) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.debug("decode", "Start Position: " + startPos);

			/* Version */
			byte verLen = bb.get();
			version = AhDecoder.bytes2String(bb, verLen);
			log.debug("decode", "Version[len/value]: " + verLen + "/" + version);

			/* Build Time */
			buildTime = bb.getInt();
			log.debug("decode", "Build Time: " + buildTime);

			/* Model Number */
			short modelNumLen = bb.getShort();
			modelNum = AhDecoder.bytes2String(bb, modelNumLen);
			log.debug("decode", "Model Number[len/value]: " + modelNumLen + "/" + modelNum);

			/* System UP Time */
			systemUpTime = bb.getInt();
			log.debug("decode", "System UP Time: " + systemUpTime);

			/* Number of HiveAPs */
			maxApNum = bb.getInt();
			log.debug("decode", "Maximum number of accepted HiveAPs: " + maxApNum);

			/* Number of VHMs */
			maxVhmNum = bb.getShort();
			log.debug("decode", "Maximum number of VHMs: " + maxVhmNum);

			/* HA Status */
			haStatus = bb.get();
			log.debug("decode", "HA Status: " + haStatus);

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