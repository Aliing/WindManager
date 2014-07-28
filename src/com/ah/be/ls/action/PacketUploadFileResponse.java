package com.ah.be.ls.action;

import com.ah.be.ls.data.UploadFileInfoResponseData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;


public class PacketUploadFileResponse {
	//parse packet
	/**
	 * 
	 * 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Packet Type|Protocol version|File Type|Send Status|Offset|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;
	 * permit code:1byte
	 * 
	 * @param bOut
	 * @param ioffset
	 * @return length of packet
	 */
	public static int parseUploadFileResponse(byte[] bInput, int iOffset, UploadFileInfoResponseData oResponseInfo)
	{
		int iLength = 0;
		
		//packet Type
		oResponseInfo.setPacketType(bInput[iLength+iOffset]);
		iLength += CommConst.Type_Util_Length;
		
		//protocol version
		oResponseInfo.setProtocolVersion(bInput[iLength+iOffset]);
		iLength += CommConst.Protocol_Version_Util_Length;
		
		//data type
		oResponseInfo.setType(bInput[iLength+iOffset]);
		iLength += CommConst.Type_Util_Length;
		
		//send status
		oResponseInfo.setSendStatus(bInput[iLength+iOffset]);
		iLength += CommConst.Upload_Send_Status_Util_Length;
		
		//offset
		oResponseInfo.setOffset(CommTool.bytes2long(bInput, CommConst.Upload_Offset_Util_Length, iOffset+iLength));
		iLength += CommConst.Upload_Offset_Util_Length;		
		
		return iLength;
	}

}
