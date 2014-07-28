/**
 *@filename		PacketNewVersionFlagQuery.java
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


import com.ah.be.ls.data.PacketNewVersionFlagQueryData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketNewVersionFlagQuery {
	
	/**
	 * 
	 * 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|Activation Key|System ID|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Product Type|pro Version Length|Content|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;Activation Code:4bytes
	 * Activation Key:41bytes;HM IP:4bytes;System ID:39bytes
	 * Product Type:1byte;pro Version Length:1byte
	 * 
	 * ap count:4bytes, version count:1byte
	 */
	
//	public static int buildNewVersionFlagQuery(byte[] bOut, int iOffset, PacketNewVersionFlagQueryData oData)
//	{
//        int iLength = 0;
//		
//		//data type
//		bOut[iOffset+iLength] = oData.getDataType();
//		iLength += CommConst.Type_Util_Length;
//		
//		//data length
//		iLength += CommConst.Data_Length_Util_Length;		
//		
//		
//		//activation key
//		CommTool.string2bytes(bOut, oData.getActKey(), iOffset+iLength);
//		iLength += CommConst.Act_key_Util_Length;		
//		
//		//System _id	
//		CommTool.string2bytes(bOut, oData.getSystemId(), iOffset+iLength);
//		iLength += CommConst.System_ID_Util_Length;
//		
//		//Hm info	
//		if ( CommConst.Product_Type_1U_HM == oData.getProType() || CommConst.Product_Type_2U_HM == oData.getProType() )
//		{
//			iLength += buildHMInfo(bOut, iOffset+iLength, oData);
//		}
//		else
//		{
//			//add unkonw
//			bOut[iOffset] = CommConst.Product_Type_UNKNOW;
//			iLength += CommConst.Type_Util_Length;
//		}		
//		
//		//set length
//		CommTool.short2bytes(bOut, 
//				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF), 
//				iOffset+CommConst.Type_Util_Length);
//		
//		return iLength;
//	}
//	
	
	private static int buildHMInfo(byte[] bOut, int iOffset, PacketNewVersionFlagQueryData oData)
	{
		int iLength = 0;
		
		bOut[iOffset] = oData.getProType();
		iLength += CommConst.Type_Util_Length;
		int iTmp = iLength;
		
		iLength += CommConst.Info_Length_Util_Length;		
		
		//build version content
		iLength +=  buildHmVersion(bOut, iOffset+iLength, oData);
		
		//set length
		bOut[iOffset+iTmp] = 
			(byte)((iLength-CommConst.Type_Util_Length-CommConst.Info_Length_Util_Length) & 0xFF);
		
		return iLength;
	}
	
	private static int buildHmVersion(byte[] bOut, int iOffset, PacketNewVersionFlagQueryData oData)
	{		
		int iLength = 0;
		
		//update limit
		bOut[iOffset] = oData.getUpdateLimited();		
		iLength += CommConst.Update_Limit_Util_Length;		
		
		CommTool.innerVersion2bytes(bOut, iOffset+iLength, oData.getInnerVersion());
		
		iLength += CommConst.Inner_Ver_Util_Length;
		
		return iLength;
	}	
	
	/**
	 * 
	 * 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|Activation Key|System ID|HM Type|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Product Type|pro Version Length|Content|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;Activation Code:4bytes
	 * Activation Key:41bytes;HM IP:4bytes;System ID:39bytes HM Type:1byte
	 * Product Type:1byte;pro Version Length:1byte
	 * 
	 * ap count:4bytes, version count:1byte
	 */
//	public static int build2NewVersionFlagQuery(byte[] bOut, int iOffset, PacketNewVersionFlagQueryData oData)
//	{
//        int iLength = 0;
//		
//		//data type
//		bOut[iOffset+iLength] = oData.getDataType();
//		iLength += CommConst.Type_Util_Length;
//		
//		//data length
//		iLength += CommConst.Data_Length_Util_Length;		
//		
//		
//		//activation key
//		CommTool.string2bytes(bOut, oData.getActKey(), iOffset+iLength);
//		iLength += CommConst.Act_key_Util_Length;		
//		
//		//System _id	
//		CommTool.string2bytes(bOut, oData.getSystemId(), iOffset+iLength);
//		iLength += CommConst.System_ID_Util_Length;
//		
//		//hm type
//		bOut[iOffset+iLength] = (byte)oData.getHmType();
//		iLength += CommConst.Type_Util_Length;
//		
//		//Hm info	
//		if ( CommConst.Product_Type_1U_HM == oData.getProType() || CommConst.Product_Type_2U_HM == oData.getProType() )
//		{
//			iLength += buildHMInfo(bOut, iOffset+iLength, oData);
//		}
//		else
//		{
//			//add unkonw
//			bOut[iOffset] = CommConst.Product_Type_UNKNOW;
//			iLength += CommConst.Type_Util_Length;
//		}		
//		
//		//set length
//		CommTool.short2bytes(bOut, 
//				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF), 
//				iOffset+CommConst.Type_Util_Length);
//		
//		return iLength;
//	}
//	
	/**
	 * 
	 * 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|L|Order Key|ystem ID|HM Type|Uid|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Product Type|pro Version Length|Content|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;Activation Code:4bytes
	 * Activation Key:41bytes;HM IP:4bytes;System ID:39bytes HM Type:1byte
	 * Product Type:1byte;pro Version Length:1byte
	 * 
	 * ap count:4bytes, version count:1byte
	 */
	
	public static int build3NewVersionFlagQuery(byte[] bOut, int iOffset, PacketNewVersionFlagQueryData oData)
	{
        int iLength = 0;
		
		//data type
		bOut[iOffset+iLength] = oData.getDataType();
		iLength += CommConst.Type_Util_Length;
		
		//data length
		iLength += CommConst.Data_Length_Util_Length;		
		
		
		//orderkey length and orderkey
		iLength += CommConst.Common_String_Length_Unit_Length;
		
		int order_key_length = 0;
	    if(oData.isNeedOrderkey())
	    {
	    	order_key_length = CommTool.string2bytes(bOut, oData.getOrderkey(), iOffset+iLength);
	    }
	    
	    bOut[iOffset+iLength-CommConst.Common_String_Length_Unit_Length]=(byte)(order_key_length & 0xff);
	    iLength += order_key_length;
		
		//System _id	
		CommTool.string2bytes(bOut, oData.getSystemId(), iOffset+iLength);
		iLength += CommConst.System_ID_Util_Length;
		
		//hm type
		bOut[iOffset+iLength] = (byte)oData.getHmType();
		iLength += CommConst.Type_Util_Length;
		
		//uid
		CommTool.int2bytes(bOut, oData.getUid(), iOffset+iLength);
	    iLength += CommConst.Common_INT_Length_Unit_Length;
		
		//Hm info	
		if ( CommConst.Product_Type_1U_HM == oData.getProType() || CommConst.Product_Type_2U_HM == oData.getProType() )
		{
			iLength += buildHMInfo(bOut, iOffset+iLength, oData);
		}
		else
		{
			//add unkonw
			bOut[iOffset] = CommConst.Product_Type_UNKNOW;
			iLength += CommConst.Type_Util_Length;
		}		
		
		//set length
		CommTool.short2bytes(bOut, 
				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF), 
				iOffset+CommConst.Type_Util_Length);
		
		return iLength;
	}
}
