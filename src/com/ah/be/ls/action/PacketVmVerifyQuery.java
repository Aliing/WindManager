package com.ah.be.ls.action;

import com.ah.be.ls.data.VmVerifyInfo;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;


public class PacketVmVerifyQuery {
	/** 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|IP|L|Order key|L|VHM_ID/SYSTEM_ID|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;
	 * HM IP:4bytes;Length:1byte
	 */
	public static int buildOrderKeyQuery(byte[] bOut, int iOffset,VmVerifyInfo oData)
	{
        int iLength = 0;
		
		//data type
		bOut[iOffset+iLength] = oData.getDataType();
		iLength += CommConst.Type_Util_Length;
		
		//data length
		iLength += CommConst.Data_Length_Util_Length;
		
		//IP
		CommTool.ip2bytes(bOut, iOffset+iLength, oData.getHmIp());
		iLength += CommConst.IP_Util_Length;
		
		//length and orderkey
		iLength += CommConst.Common_String_Length_Unit_Length;
		
		int order_key_length = CommTool.string2bytes(bOut,oData.getOrderKey(),iOffset+iLength);
		
		bOut[iOffset+iLength-CommConst.Common_String_Length_Unit_Length]=(byte)(order_key_length & 0xff);
		
		iLength += order_key_length;
		
		
		//systemid/vhmid
		iLength += CommConst.Common_String_Length_Unit_Length;
		
		int id_length = CommTool.string2bytes(bOut, oData.getSystemID(), iOffset+iLength);
		
		bOut[iOffset+iLength-CommConst.Common_String_Length_Unit_Length] = (byte)(id_length & 0xff);
		
		iLength += id_length;
		
		CommTool.short2bytes(bOut, 
				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF), 
				iOffset+CommConst.Type_Util_Length);
		
		return iLength;
	}

}
