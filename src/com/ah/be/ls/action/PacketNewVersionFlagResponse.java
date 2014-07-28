package com.ah.be.ls.action;

import com.ah.be.ls.data.PacketNewVersionFlagResponseData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketNewVersionFlagResponse {
	
	/**
	 * the valid response
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|new ver Flag|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;Activation Code:4bytes
	 * new period:2bytes pro type:1bytep; pro Version Length:1byte
	 * 
	 * @return: int length of the data packet response.
	 * 
	 */
	public static int parseNewVersionFlagtResponse(byte[] bInput, int iOffset, PacketNewVersionFlagResponseData oData)
	{
		int iLength = 0;
		
		//data type
		byte bDataType = bInput[iOffset+iLength];
		if( bDataType !=  (byte)(CommConst.Act_Response_Data_Type & 0xFF))
		{
			//add error log
			
			return 0;
		}
		
		oData.setDataType(bDataType);
		iLength += CommConst.Type_Util_Length;
		
		//data length
		int iDataLength = 0;
		iDataLength = CommTool.short2int(CommTool.bytes2short(bInput, 
				                    CommConst.Data_Length_Util_Length, iOffset+iLength));
		
		iLength += CommConst.Data_Length_Util_Length;	
		
		//flag
		oData.setNewVersionFlag(bInput[iOffset+iLength]);
		iLength += CommConst.New_Version_Flag_Util_Length;
		
		//length is not equal
		if(iDataLength != 
			(iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length))
		{
			return 0;
		}		
		
		return iLength;
	}	
}
