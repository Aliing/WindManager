package com.ah.be.ls.action;

import java.util.List;

import com.ah.be.hiveap.HiveApVersionInfo;
import com.ah.be.ls.data.PacketApInfoData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketApInfo {
	
	/**
	 * 
	 * 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|Activation Key|HM IP|System ID|System ID|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Product Type|pro Version Length|Content|Port ap count|Mesh ap count|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |version count|Optional Data
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |ap count|version length|version
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;Activation Code:4bytes
	 * Activation Key:41bytes;HM IP:4bytes;System ID:39bytes
	 * Product Type:1byte;pro Version Length:1byte
	 * 
	 * ap count:4bytes, version count:1byte
	 */
	public static int buildApInfo(byte[] bOut, int iOffset, PacketApInfoData oData)
	{		
		int iLength = 0;
		
		//data type
		bOut[iOffset+iLength] = oData.getDataType();
		iLength += CommConst.Type_Util_Length;
		
		//data length
		iLength += CommConst.Data_Length_Util_Length;		
		
		//activation key
		CommTool.string2bytes(bOut, oData.getActKey(), iOffset+iLength);
		iLength += CommConst.Act_key_Util_Length;		
		
		//HM IP
		CommTool.ip2bytes(bOut, iOffset+iLength, oData.getHMIP());
		iLength += CommConst.IP_Util_Length;	
		
		//System _id	
		CommTool.string2bytes(bOut, oData.getSystemId(), iOffset+iLength);
		iLength += CommConst.System_ID_Util_Length;
		
		//Hm info	
		if ( CommConst.Product_Type_1U_HM == oData.getProType() || CommConst.Product_Type_2U_HM == oData.getProType() )
		{
			iLength += buildHMInfo(bOut, iOffset+iLength, oData);
		}
		else
		{
			//add unknown
			bOut[iOffset] = CommConst.Product_Type_UNKNOW;
			iLength += CommConst.Type_Util_Length;
		}
		
		//port ap count
		CommTool.int2bytes(bOut, oData.getPortApcount(), iOffset+iLength);
		iLength += CommConst.Ap_Count_Util_Length;		
		
		//mesh ap count
		CommTool.int2bytes(bOut, oData.getMeshApcount(), iOffset+iLength);
		iLength += CommConst.Ap_Count_Util_Length;
		
		//ap version
		List<HiveApVersionInfo> oInfoList = oData.getApVersionList();
		if(null == oInfoList)
		{
			oData.setApVersionCount(0);
		}
		else
		{
			oData.setApVersionCount(oInfoList.size());
		}
		
		//version count
		bOut[iOffset+iLength] = (byte)(oData.getApVersionCount() & 0xFF);
		iLength += CommConst.Version_Count_Util_Length;
		
		//version list
		if(0 != oData.getApVersionCount())
		{
			for (HiveApVersionInfo info : oInfoList) {
				iLength += buildVersion(bOut, iLength + iOffset, info);
			}
		}
		
		//set length
		CommTool.short2bytes(bOut, 
				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF), 
				iOffset+CommConst.Type_Util_Length);
		
		return iLength;
	}
	
	
	private static int buildHMInfo(byte[] bOut, int iOffset, PacketApInfoData oData)
	{
		int iLength = 0;
		
		bOut[iOffset] = oData.getProType();
		iLength += CommConst.Type_Util_Length;
		int iTmp = iLength;
		
		iLength += CommConst.Info_Length_Util_Length;		
		
		//build view content
		iLength += CommTool.string2bytes(bOut, oData.getViewVersion(),iOffset+iLength);
		
		//set length
		bOut[iOffset+iTmp] = 
			(byte)((iLength-CommConst.Type_Util_Length-CommConst.Info_Length_Util_Length) & 0xFF);
		
		return iLength;
	}	

	
	private static int buildVersion(byte[] bOut, int iOffset, HiveApVersionInfo oVersion)
	{
		int iLength = 0;
		
		CommTool.int2bytes(bOut, oVersion.getHiveapCount(), iOffset+iLength);
		iLength += CommConst.Ap_Count_Util_Length;		
		
		//length		
		iLength += CommConst.Info_Length_Util_Length;
		
		//version content
		int iVersionLength = CommTool.string2bytes(bOut,oVersion.getHiveapVersion(),iOffset+iLength);
		
		bOut[iOffset+CommConst.Ap_Count_Util_Length] = (byte)(iVersionLength & 0xFF);
		
		iLength += iVersionLength;
		
		return iLength;
	}

}