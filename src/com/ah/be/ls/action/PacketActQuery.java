/**
 *@filename		PacketActQuery.java
 *@version
 *@author		xiaolanbao
 *@createtime	2009-4-7 09:37:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.ls.action;

import com.ah.be.ls.data.PacketActQueryData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketActQuery {
	
	/**
	 * 	
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|Activation Code|Activation Key|HM IP|System ID|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ 
	 * Data Type:1byte;Data Length:2bytes;Activation Code:4bytes
	 * Activation Key:41bytes;HM IP:4bytes;System ID:39bytes	
	 *	 	 
	 * data type=1 for act query
	 * @param bOut
	 * @param ioffset
	 * @return length of packet
	 */
	public static int buildActQuery(byte[] bOut, int iOffset, PacketActQueryData oData)
	{		
		int iLength = 0;
		
		//data type
		bOut[iOffset+iLength] = oData.getDataType();
		iLength += CommConst.Type_Util_Length;
		
		//data length
		iLength += CommConst.Data_Length_Util_Length;
		
		//activation code
		CommTool.int2bytes(bOut, oData.getActCode(), iOffset+iLength);
		iLength += CommConst.Act_Code_Util_Length;
		
		//activation key
		CommTool.string2bytes(bOut, oData.getActKey(), iOffset+iLength);
		iLength += CommConst.Act_key_Util_Length;		
		
		//HMIP
		CommTool.ip2bytes(bOut, iOffset+iLength, oData.getHMIP());
		iLength += CommConst.IP_Util_Length;	
		
		//System _id	
		CommTool.string2bytes(bOut, oData.getSystemId(), iOffset+iLength);
		iLength += CommConst.System_ID_Util_Length;		
		
		//set length
		CommTool.short2bytes(bOut, 
				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF), 
				iOffset+CommConst.Type_Util_Length);
		
		return iLength;
	}	
}
