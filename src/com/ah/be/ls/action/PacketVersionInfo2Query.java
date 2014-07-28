package com.ah.be.ls.action;

import com.ah.be.ls.data.PacketVersion2QueryData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;


public class PacketVersionInfo2Query {
	
	/**
	 * the download query
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|L|systemid|L|order key|uid|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |HM Type|pro type|update limit |pro Version|
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;pro type 1byte 
	 * L:1byte UId:4byte
	 * 
	 * @return: int length of the data packet response.
	 * 
	 */
	
	public static int buildInfoToDownload_2(byte[] bOut, int iOffset, PacketVersion2QueryData oData)
	{
		int iLength = 0;
		
		//data type
		bOut[iOffset+iLength] = oData.getDataType();
		iLength += CommConst.Type_Util_Length;
		
		//data length
		iLength += CommConst.Data_Length_Util_Length;
		
		//length of systemid and systemid
		iLength += CommConst.Common_String_Length_Unit_Length;
		
		int system_id_length = CommTool.string2bytes(bOut,oData.getSystemId(),iOffset+iLength);
		bOut[iOffset+iLength-CommConst.Common_String_Length_Unit_Length]=(byte)(system_id_length & 0xff);
	    iLength += system_id_length;
	    
	    //length of orderkey and orderkey
	    iLength += CommConst.Common_String_Length_Unit_Length;
	    
	    int order_key_length = 0;
	    if(oData.IsOrderkey())
	    {
	    	order_key_length = CommTool.string2bytes(bOut, oData.getOrderkey(), iOffset+iLength);
	    }
	    
	    bOut[iOffset+iLength-CommConst.Common_String_Length_Unit_Length]=(byte)(order_key_length & 0xff);
	    iLength += order_key_length;
	    
	    //uid
	    CommTool.int2bytes(bOut, oData.getUid(), iOffset+iLength);
	    iLength += CommConst.Common_INT_Length_Unit_Length;
	    
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
