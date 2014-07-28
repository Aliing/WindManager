package com.ah.be.ls.action;

import java.util.List;

import com.ah.be.hiveap.ClientMacInfo;
import com.ah.be.ls.data.PacketAPClientInfoData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketAPClientInfo {

	/**
	 *  
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|Activation Key|System ID|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Optional counts|optional Data
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 
	 * optional data is
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |MAC-Prefix|counts|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;Activation Code:4bytes
	 * Activation Key:41bytes;System ID:39bytes
	 * 
	 * optional counts:2bytes
	 * MAc-prefix:6bytes
	 * counts: 4bytes
	 *
	 */
	public static int buildApClientInfo(byte[] bOut, int iOffset, PacketAPClientInfoData oData)
	{
		int iLength = 0;
		
		//type
		bOut[iOffset+iLength] = oData.getDataType();
		iLength += CommConst.Type_Util_Length;
		
		//Data length
		iLength += CommConst.Data_Length_Util_Length;
		
		//actkey
		CommTool.string2bytes(bOut, oData.getActKey(), iOffset+iLength);
		iLength += CommConst.Act_key_Util_Length;		
		
		//systemid
		CommTool.string2bytes(bOut, oData.getSystemId(), iOffset+iLength);
		iLength += CommConst.System_ID_Util_Length;
		
		//count
		List<ClientMacInfo> oMacInfoList = oData.getApClientList();
		if(null == oMacInfoList)
		{
			oData.setClientTypecount(0);
		}else
		{
			oData.setClientTypecount(oMacInfoList.size());
		}
		
		CommTool.short2bytes(bOut, (short)oData.getClientTypecount(), iOffset+iLength);
		iLength += CommConst.Ap_Client_Type_Count_Util_Length;
		
		//mac client list
		if(0 != oData.getClientTypecount())
		{
			for (ClientMacInfo macInfo : oMacInfoList) {
				iLength += buildMacInfo(bOut, iLength + iOffset, macInfo);
			}
		}
		
		//data length
		CommTool.short2bytes(bOut, 
				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF), 
				iOffset+CommConst.Type_Util_Length);
		
		return iLength;
	}
	
	private static int buildMacInfo(byte[] bOut, int iOffset, ClientMacInfo oData)
	{
		int iLength = 0;
		
		//Mac prefix
		CommTool.string2bytes(bOut, oData.getClientMac(), iOffset+iLength);
		iLength += CommConst.Ap_Client_Mac_Prefix_Util_Length;
		
		//counts
		CommTool.int2bytes(bOut, oData.getClientCount(), iOffset+iLength);
		iLength += CommConst.Ap_Client_Count_Util_Length;
		
		return iLength;
	}

}