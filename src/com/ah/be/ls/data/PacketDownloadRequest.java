package com.ah.be.ls.data;

import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketDownloadRequest {
	private byte[]	data;
	/**
	 * packet structure for download request. |New packet flag 0x00(1)|Packet Version(4)|Data
	 * Type(1)|Data Length(4)|Random Code(4)|Product Type(1)|Validate Code Length(2)|Validate
	 * Code(v)|Host Ip(4)|Hardware Target(1)|Download version(16)|download begin
	 * position(4)|download size(4)|
	 */

	private int		packetVersion	= 2;
	private byte	dataType;
	private int		dataLength;
	private int		randomCode;
	private byte	productType;
	private String	systemId;
	private short	validateCodeLength;
	private String	validateCode;
	private int		hostIp;
	private short	hardwareTargetLength;
	private String	hardwareTarget;
	private int		currentUid		= 0;
	private int		reqImageUid		= 0;
	private String	reqDownloadVersion;
	private int		reqDownloadBeginPos;
	private int		reqDownloadSize;

	public void buildData() {
		byte[] data = new byte[CommConst.BUFFER_SIZE];
		int offset = 0;

		// new packet flag
		data[offset] = 0x00;
		offset += 1;

		// packet version
		CommTool.int2bytes(data, packetVersion, offset);
		offset += 4;

		// data type
		data[offset] = dataType;
		offset += 1;

		// data offset
		offset += 4;

		// random code
		CommTool.int2bytes(data, randomCode, offset);
		offset += 4;

		// product type
		data[offset] = productType;
		offset += 1;

		// system id
		CommTool.string2bytes(data, systemId, offset);
		offset += 64;

		// validate code length
		CommTool.short2bytes(data, validateCodeLength, offset);
		offset += 2;

		if (validateCodeLength > 0) {
			// validate code
			CommTool.string2bytes(data, validateCode, offset);
			offset += validateCodeLength;
		}

		// host ip
		CommTool.int2bytes(data, hostIp, offset);
		offset += 4;

		// hardware target length
		CommTool.short2bytes(data, hardwareTargetLength, offset);
		offset += 2;

		if (hardwareTargetLength > 0) {
			// hardware target
			CommTool.string2bytes(data, hardwareTarget, offset);
			offset += hardwareTargetLength;
		}

		// current HM or AP uid
		CommTool.int2bytes(data, currentUid, offset);
		offset += 4;

		// request image uid
		CommTool.int2bytes(data, reqImageUid, offset);
		offset += 4;

		// request download version
		CommTool.string2bytes(data, reqDownloadVersion, offset);
		offset += 16;

		// request download position
		CommTool.int2bytes(data, reqDownloadBeginPos, offset);
		offset += 4;

		// request download size
		CommTool.int2bytes(data, reqDownloadSize, offset);
		offset += 4;

		dataLength = offset;
		CommTool.int2bytes(data, dataLength, 1);

		this.data = new byte[dataLength];
		System.arraycopy(data, 0, this.data, 0, dataLength);
	}

	public byte[] getData() {
		return data;
	}

	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public void setRandomCode(int randomCode) {
		this.randomCode = randomCode;
	}

	public void setProductType(byte productType) {
		this.productType = productType;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public void setValidateCode(String validateCode) {
		if (validateCode == null) {
			validateCode = "";
		}
		this.validateCode = validateCode;
		this.validateCodeLength = (short) this.validateCode.length();
	}

	public void setHostIp(int hostIp) {
		this.hostIp = hostIp;
	}

	public void setHardwareTarget(String hardwareTarget) {
		if (hardwareTarget == null) {
			hardwareTarget = "";
		}
		this.hardwareTarget = hardwareTarget;
		this.hardwareTargetLength = (short) hardwareTarget.length();
	}

	public void setCurrentUid(int currentUid) {
		this.currentUid = currentUid;
	}

	public void setReqImageUid(int reqImageUid) {
		this.reqImageUid = reqImageUid;
	}

	public void setReqDownloadVersion(String reqDownloadVersion) {
		this.reqDownloadVersion = reqDownloadVersion;
	}

	public void setReqDownloadBeginPos(int reqDownloadBeginPos) {
		this.reqDownloadBeginPos = reqDownloadBeginPos;
	}

	public int getReqDownloadBeginPos() {
		return reqDownloadBeginPos;
	}

	public void setReqDownloadSize(int reqDownloadSize) {
		this.reqDownloadSize = reqDownloadSize;
	}

	private String	strIp;	// not in packet

	public void setStrIp(String strIp) {
		this.strIp = strIp;
		byte[] ipbytes = new byte[4];
		CommTool.ip2bytes(ipbytes, 0, this.strIp);
		this.hostIp = CommTool.bytes2int(ipbytes, 4, 0);
	}

}
