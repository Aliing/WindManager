package com.ah.be.ls.action;

import com.ah.be.ls.data.CommErrInfo;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketCommErrResponse {
	
	/** 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|Error code|L|Error info|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Error code: 1byte; L:4bytes 
	 */
	
	public static int parseOederkeyResponse(byte[] bInput, int iOffset, CommErrInfo oData)
	{
		int iLength = 0;

		//type
		byte bDataType = bInput[iOffset+iLength];		
		oData.setDataType(bDataType);
		iLength += CommConst.Type_Util_Length;
		
		//data length
		int iDataLength = 0;
		iDataLength = CommTool.short2int(CommTool.bytes2short(bInput, 
                CommConst.Data_Length_Util_Length, iOffset+iLength));
		
		iLength += CommConst.Data_Length_Util_Length;
		
		//error code
		oData.setErrCode(bInput[iOffset+iLength]);
		iLength += CommConst.Common_Err_Code_Length_Unit_Length;
		
		//length for L
		int error_length = CommTool.bytes2int(bInput, CommConst.Common_INT_Length_Unit_Length, iOffset+iLength);
		iLength += CommConst.Common_INT_Length_Unit_Length;
		
		if(0 != error_length)
		{
			oData.setErrInfo(CommTool.byte2string(bInput, iOffset+iLength, error_length));
		}
		
		iLength += error_length;
		
		if(iDataLength != 
			(iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length))
		{
			return 0;
		}
		
		return iLength;
	}

}
