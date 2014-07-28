/**
 *@filename		PacketDownLoadQuery.java
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

import com.ah.be.ls.data.PacketVersionInfoQueryData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketVersionInfoQuery {
	

	/**
	 * the download query
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|pro type|update limit|pro Version|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;pro type 1byte 
	 * 
	 * @return: int length of the data packet response.
	 * 
	 */
//	public static int buildDownloadQuery(byte[] bOut, int iOffset, PacketVersionInfoQueryData oData)
//	{
//		int iLength = 0;
//		
//		//data type
//		bOut[iOffset+iLength] = oData.getDataType();
//		iLength += CommConst.Type_Util_Length;
//		
//		//data length
//		iLength += CommConst.Data_Length_Util_Length;
//		
//		//pro type
//		bOut[iOffset+iLength] = oData.getProType();
//		iLength += CommConst.Type_Util_Length;    
//		
//		//update limit
//		bOut[iOffset+iLength] = oData.getUpdateLimited();
//		iLength += CommConst.Update_Limit_Util_Length;
//		
//		//version info
//        CommTool.innerVersion2bytes(bOut, iOffset+iLength, oData.getInnerVersion());		
//		iLength += CommConst.Inner_Ver_Util_Length;
//		
//		//set Length
//		CommTool.short2bytes(bOut,
//				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF),
//				iOffset+CommConst.Type_Util_Length);
//		     
//		return iLength;
//	}
//	

	/**
	 * the download query
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|Hm type|pro type|update limit|pro Version|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;pro type 1byte 
	 * 
	 * @return: int length of the data packet response.
	 * 
	 */
	public static int build2DownloadQuery(byte[] bOut, int iOffset, PacketVersionInfoQueryData oData)
	{
		int iLength = 0;
		
		//data type
		bOut[iOffset+iLength] = oData.getDataType();
		iLength += CommConst.Type_Util_Length;
		
		//data length
		iLength += CommConst.Data_Length_Util_Length;
		
		//hm type
		bOut[iOffset+iLength] = (byte)oData.getHmType();
		iLength += CommConst.Type_Util_Length;
		
		//pro type
		bOut[iOffset+iLength] = oData.getProType();
		iLength += CommConst.Type_Util_Length;    
		
		//update limit
		bOut[iOffset+iLength] = oData.getUpdateLimited();
		iLength += CommConst.Update_Limit_Util_Length;
		
		//version info
        CommTool.innerVersion2bytes(bOut, iOffset+iLength, oData.getInnerVersion());		
		iLength += CommConst.Inner_Ver_Util_Length;
		
		//set Length
		CommTool.short2bytes(bOut,
				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF),
				iOffset+CommConst.Type_Util_Length);
		     
		return iLength;
	}
	
}
