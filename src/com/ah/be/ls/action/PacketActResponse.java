/**
 *@filename		PacketActResponse.java
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


import com.ah.be.ls.data.PacketInvalidActResponseData;
import com.ah.be.ls.data.PacketValidActResponseData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketActResponse {
	
	/**
	
    //parse the packet

	/**
	 * the valid response
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|Activation Code|new period|Retry Times|Interval|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;Activation Code:4bytes
	 * new period:2bytes pro type:1bytep; pro Version Length:1byte
	 * 
	 * @return: int length of the data packet response.
	 * 
	 */
	
	public static int parseValidActResponse(byte[] bInput, int iOffset, PacketValidActResponseData oData)
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
		
		//activation code
		oData.setActCode(CommTool.bytes2int(bInput, CommConst.Act_Code_Util_Length, iOffset+iLength));
		iLength += CommConst.Act_Code_Util_Length;
		
		//new period
		oData.setPerid(CommTool.bytes2short(bInput, CommConst.Period_Util_Length, iOffset+iLength));
		iLength += CommConst.Period_Util_Length;
		
		//retry tmes
		oData.setRetryTimes(bInput[iOffset+iLength]);
		iLength += CommConst.Retry_Times_Util_Length;
		
		//interval	
		oData.setInterval(CommTool.bytes2int(bInput, CommConst.Retry_Interval_Util_Length, iOffset+iLength));
		iLength += CommConst.Retry_Interval_Util_Length;		
		
		//length is not equal
		if(iDataLength != 
			(iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length))
		{
			return 0;
		}		
		
		return iLength;
	}
	
	/**
	 * the Invalid response
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|pro type|operation|desc length|descrption
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;pro type:1byte;
	 * operation:1byte
	 * 
	 * @return: int length of the data packet response.
	 * 
	 */
	public static int parseInvalidActResponse(byte[] bInput, int iOffset, PacketInvalidActResponseData oData)
	{
		int iLength = 0;
		
		byte bDataType = bInput[iOffset+iLength];
		if( bDataType !=  (byte)(CommConst.Act_Response_Deny_Data_Type & 0xFF))
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
		
		//protype
		oData.setProType(bInput[iOffset+iLength]);
		iLength += CommConst.Type_Util_Length;
		
		//operation
		oData.setOperation(bInput[iOffset+iLength]);
		iLength += CommConst.Operate_Util_Length;
		
		//desc Length
		int iDescLength = CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += CommConst.Description_Legth_Util_Length;
		
		//description
		oData.setDesc(CommTool.byte2string(bInput, iOffset+iLength, iDescLength));
		
		iLength += iDescLength;
		
		//length is not equal
		if(iDataLength != 
			(iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length))
		{
			return 0;
		}
		
		return iLength;
	}
	
}
