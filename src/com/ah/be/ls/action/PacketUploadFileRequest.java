package com.ah.be.ls.action;

import com.ah.be.ls.data.UploadFileInfoRequestData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketUploadFileRequest {
	
	/**
	 * 
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Packet Type|Protocol version|Is Act Key|Act Key|SystemId|File Type|
	 * |Send Status|Offset|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Length|Data
	 * 
	 * Packet Type:1byte Protocol version:1byte 
	 * Is Act Key:1byte Act key:41bytes
	 * SystemId:39bytes
	 * File Type:1byte Send Status:1byte offset:8byte
	 * DataLength:4 byte
	 * fix is: 
	 * @param bOut
	 * @param ioffset
	 * @return length of packet
	 */
	public static int buildUploadFileReq(byte[] bOut, int iOffset, UploadFileInfoRequestData oFileInfo)
	{
		int iLength = 0;		
		
		//packet type
		bOut[iOffset+iLength] = oFileInfo.getPacketType();
		iLength += CommConst.Type_Util_Length;
		
		//protocol version
		bOut[iOffset+iLength] = oFileInfo.getProtocolVersion();
		iLength += CommConst.Protocol_Version_Util_Length;
		
		//is Need Act
		if(oFileInfo.getNeedActKeyFlag())
		{
			bOut[iOffset+iLength] = CommConst.Need_Act_Key_Packet_Type;
		}
		else
		{
			bOut[iOffset+iLength] = CommConst.Not_Need_Act_Key_Packet_Type;
		}
		iLength += CommConst.Type_Util_Length;
		
		//Act key
		CommTool.string2bytes(bOut, oFileInfo.getActKey(), iOffset+iLength);
		iLength += CommConst.Act_key_Util_Length;
		
		//system id
		CommTool.string2bytes(bOut, oFileInfo.getSystemId(), iOffset+iLength);
		iLength += CommConst.System_ID_Util_Length;
		
		//file type
		bOut[iOffset+iLength] = oFileInfo.getType();
		iLength += CommConst.Type_Util_Length;
		
		//send status
		bOut[iOffset+iLength] = oFileInfo.getSendStatus();
		iLength += CommConst.Upload_Send_Status_Util_Length;
		
		//offset
		CommTool.long2bytes(bOut, oFileInfo.getOffset(), iOffset+iLength);
		iLength += CommConst.Upload_Offset_Util_Length;
		
		//data length
		CommTool.int2bytes(bOut, oFileInfo.getDataLength(), iOffset+iLength);
		iLength += CommConst.Upload_Data_Size_Util_Length;
		
		iLength += oFileInfo.getDataLength();
		
		return iLength;
	}

}
