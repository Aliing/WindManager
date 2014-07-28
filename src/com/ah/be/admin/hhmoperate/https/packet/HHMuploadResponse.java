package com.ah.be.admin.hhmoperate.https.packet;

import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.admin.hhmoperate.https.data.HHMuploadResponseData;
import com.ah.be.ls.util.CommTool;

public class HHMuploadResponse {
	
	/**		 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Packet Type|Length|Protocol version|Send Status|Offset|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 */
     public static int buildHHMuploadResponse(byte[] bOut, int iOffset, HHMuploadResponseData oData)
     {
    	 int iLength = 0;
    	 
    	 //packet type
 		 bOut[iOffset+iLength] = oData.getPacketType();
 		 iLength += HHMConstant.Packet_Type_Util_Length;
 		
 		 //length
		 iLength += HHMConstant.Packet_Length_Util_Length;
 		
 		 //protocol version
 		 bOut[iOffset+iLength] = oData.getProtocolVersion();
 		 iLength += HHMConstant.Packet_Version_Util_Length;
 		 		
 		 //status
 		 bOut[iOffset+iLength] = oData.getSendStatus();
 		 iLength += HHMConstant.Upload_Send_Status_Util_Length;
 		
 		 //offset
 		 CommTool.long2bytes(bOut, oData.getOffset(), iOffset+iLength);
 		 iLength += HHMConstant.Upload_Offset_Util_Length;
 		 
 		 //set length
 		CommTool.short2bytes(bOut, 
				(short)((iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length) & 0xFFFF), 
				iOffset+HHMConstant.Packet_Type_Util_Length);
		
    	 
    	return iLength;
     }
     
     public static int parseHHMuploadResponse(byte[] bInput, int iOffset, HHMuploadResponseData oData)
     {
    	 int iLength = 0;
 		
 		//packet Type
 		oData.setPacketType(bInput[iLength+iOffset]);
 		iLength += HHMConstant.Packet_Type_Util_Length;
 		
 		//length
 		int iDataLength =  CommTool.short2int(CommTool.bytes2short(bInput, 
				HHMConstant.Packet_Length_Util_Length, iOffset+iLength));
		iLength += HHMConstant.Packet_Length_Util_Length;
 		
 		//protocol version
 		oData.setProtocolVersion(bInput[iLength+iOffset]);
 		iLength += HHMConstant.Packet_Version_Util_Length; 	 		
 		
 		//send status
 		oData.setSendStatus(bInput[iLength+iOffset]);
 		iLength += HHMConstant.Upload_Send_Status_Util_Length;
 		
 		//offset
 		oData.setOffset(CommTool.bytes2long(bInput, HHMConstant.Upload_Offset_Util_Length, iOffset+iLength));
 		iLength += HHMConstant.Upload_Offset_Util_Length;	
 		
 		if(iDataLength !=  (iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length))
		{
			return 0;
		}
 		
 		return iLength;
     }
    
}
