package com.ah.be.ls.action;

import com.ah.be.ls.data.OrderkeyErrData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketOrderkeyErr {
	
	/** 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|L|Order key|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;
	 * Length:1byte
	 */
	public static int buildOrderKeyErr(byte[] bOut, int iOffset,OrderkeyErrData oData)
	{
		int iLength = 0;
		
		//data type
		bOut[iOffset+iLength] = oData.getDataType();
		iLength += CommConst.Type_Util_Length;
		
		//data length
		iLength += CommConst.Data_Length_Util_Length;
		
		//length and orderkey
		iLength += CommConst.Common_String_Length_Unit_Length;
		
		int order_key_length = CommTool.string2bytes(bOut,oData.getOrderKey(),iOffset+iLength);
		
		bOut[iOffset+iLength-CommConst.Common_String_Length_Unit_Length]=(byte)(order_key_length & 0xff);
		
		iLength += order_key_length;
		
		CommTool.short2bytes(bOut, 
				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF), 
				iOffset+CommConst.Type_Util_Length);
		
		return iLength;
	}

}
