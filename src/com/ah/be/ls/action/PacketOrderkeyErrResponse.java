package com.ah.be.ls.action;

import com.ah.be.ls.data.OrderkeyErrInfo;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketOrderkeyErrResponse {

	/** 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|Response Flag|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte
	 * Data Length:2bytes; Response Flag:1bytes
	 * 
	 */
	
	public static int parseOederkeyErrResponse(byte[] bInput, int iOffset, OrderkeyErrInfo orderkeyErrInfo)
	{
		int iLength = 0;
		byte bDataType = bInput[iOffset+iLength];
		orderkeyErrInfo.setDataType(bDataType);
		
		if(bDataType != CommConst.Order_Key_Err_Response_Data_Type)
		{
			return 0;
		}
		
		iLength += CommConst.Type_Util_Length;
		//data length
		int iDataLength = CommTool.short2int(CommTool.bytes2short(bInput, 
                CommConst.Data_Length_Util_Length, iOffset+iLength));
		
		iLength += CommConst.Data_Length_Util_Length;
		
		if(bInput[iOffset+iLength] == 1){
			orderkeyErrInfo.setResponseFlag(true);
		} else {
			orderkeyErrInfo.setResponseFlag(false);
		}

		iLength += 1;
		
		//length is not equal
		if(iDataLength != (iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length))
		{
			return 0;
		}	
		
		return iLength;
	}
	
}