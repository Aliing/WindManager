package com.ah.be.ls.action;

import com.ah.be.ls.data.OrderkeyQueryData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketOrderkeyQuery {
	
	/** 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|IP|L|Order key|HM Type|L|VHM_ID/SYSTEM_ID|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;
	 * HM IP:4bytes;Length:1byte
	 * HM Type:1byte;Length:1byte
	 */
	public static int buildOrderKeyQuery(byte[] bOut, int iOffset,OrderkeyQueryData oData)
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
		
		//HM type
		bOut[iOffset+iLength] = (byte)(oData.getHMType() & 0xff);
		iLength += CommConst.Ordery_key_HMTYPE_UNIT_LENGTH;
		
		//systemid/vhmid
		iLength += CommConst.Common_String_Length_Unit_Length;
		
		int id_length = CommTool.string2bytes(bOut, oData.getFulfillmentID(), iOffset+iLength);
		
		bOut[iOffset+iLength-CommConst.Common_String_Length_Unit_Length] = (byte)(id_length & 0xff);
		
		iLength += id_length;
		
		//key type
		iLength += CommConst.Common_String_Length_Unit_Length;
		
		int ty_length = CommTool.string2bytes(bOut, oData.getCurrentType(), iOffset+iLength);
		
		bOut[iOffset+iLength-CommConst.Common_String_Length_Unit_Length] = (byte)(ty_length & 0xff);
		
		iLength += ty_length;
		
		//ap number	
		CommTool.int2bytes(bOut, oData.getCurrentAp(), iOffset+iLength);
		iLength += CommConst.Common_INT_Length_Unit_Length;
		
		//vhm number	
		CommTool.int2bytes(bOut, oData.getCurrentVhm(), iOffset+iLength);
		iLength += CommConst.Common_INT_Length_Unit_Length;
		
		//vhm number	
		CommTool.int2bytes(bOut, oData.getCurrentCvg(), iOffset+iLength);
		iLength += CommConst.Common_INT_Length_Unit_Length;
		
		CommTool.short2bytes(bOut, 
				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF), 
				iOffset+CommConst.Type_Util_Length);
		
		return iLength;
	}

}
