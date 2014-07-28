package com.ah.be.ls.data;

import org.apache.log4j.Logger;

import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketDownloadResponse {
	private static Logger	log				= Logger.getLogger("PacketDownloadResponse");

	private byte[]			data;
	/**
	 * packet structure for download request. |New packet flag (1)|Packet Version (4)|Data
	 * Type(1)|Data Length(4)|Random Code(4)|Product Type(1)|File name Length(2)|File Name(v)|File
	 * Checksum(32)|Thread Count(1)|download length(4)|
	 */

	private boolean			newPacketFlag	= false;
	private int				packetVersion	= 1;
	private byte			dataType;
	private int				dataLength;
	private int				randomCode;
	private byte			productType;
	private short			fileNameLength;
	private String			fileName;
	private int				fileSize;
	private String			fileChecksum;
	private int				imageUid;
	// device can download the latest version image
	private String          imageVersion;
	private byte			threadCount;
	private int				downloadLength;

	private short			reasonLength;
	private String			reason;

	public PacketDownloadResponse() {
	}

	public PacketDownloadResponse(byte[] data) {
		this.data = data;
	}

	public void parseData() throws Exception {
		int offset = 0;

		if (data[offset] == 0x00) {
			newPacketFlag = true;
			offset += 1;
		} else {
			newPacketFlag = false;
		}

		if (newPacketFlag) {
			packetVersion = CommTool.bytes2int(data, 4, offset);
			offset += 4;
		}

		// data type
		dataType = data[offset];
		offset += 1;
		if (dataType != CommConst.Data_Type_File_Query_Success_Response
				&& dataType != CommConst.Data_Type_File_Download_Success_Response
				&& dataType != CommConst.Data_Type_File_Query_Deny_Response
				&& dataType != CommConst.Data_Type_File_Download_Deny_Response) {
			log.error("data type error! [" + dataType + "]");
			throw new Exception("data type error! [" + dataType + "]");
		}

		// data length
		dataLength = CommTool.bytes2int(data, 4, offset);
		offset += 4;

		// random code
		randomCode = CommTool.bytes2int(data, 4, offset);
		offset += 4;

		// product type
		productType = data[offset];
		offset += 1;

		if (dataType == CommConst.Data_Type_File_Query_Deny_Response
				|| dataType == CommConst.Data_Type_File_Download_Deny_Response) {

			// reason length
			reasonLength = CommTool.bytes2short(data, 2, offset);
			offset += 2;

			// reason
			reason = CommTool.byte2string(data, offset, reasonLength);
			offset += reasonLength;

			log.warn("result is fail and reason is " + reason);
			return;
		}

		// file name length
		fileNameLength = CommTool.bytes2short(data, 2, offset);
		offset += 2;

		// file name
		fileName = CommTool.byte2string(data, offset, fileNameLength);
		offset += fileNameLength;

		// file size
		fileSize = CommTool.bytes2int(data, 4, offset);
		offset += 4;

		// file checksum
		fileChecksum = CommTool.byte2string(data, offset, 32);
		offset += 32;

		if (packetVersion >= 2) {
			imageUid = CommTool.bytes2int(data, 4, offset);
			offset += 4;
		}
		
		// image version length
		short versionLength = CommTool.bytes2short(data, 2, offset);
		offset += 2;

		// image version
		imageVersion = CommTool.byte2string(data, offset, versionLength);
		offset += versionLength;

		// thread count
		threadCount = data[offset];
		offset += 1;

		// download length
		downloadLength = CommTool.bytes2int(data, 4, offset);
		offset += 4;
	}

	public boolean isNewPacketFlag() {
		return newPacketFlag;
	}

	public int getPacketVersion() {
		return packetVersion;
	}

	public byte getDataType() {
		return dataType;
	}

	public int getDataLength() {
		return dataLength;
	}

	public int getRandomCode() {
		return randomCode;
	}

	public byte getProductType() {
		return productType;
	}

	public short getFileNameLength() {
		return fileNameLength;
	}

	public String getFileName() {
		return fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public String getFileChecksum() {
		return fileChecksum;
	}

	public int getImageUid() {
		return imageUid;
	}

	public byte getThreadCount() {
		return threadCount;
	}

	public int getDownloadLength() {
		return downloadLength;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getReason() {
		return reason;
	}
	
	public String getImageVersion() {
		return imageVersion;
	}

}
