package com.ah.be.admin.hhmoperate.https.packet;

import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.ls.util.CommTool;

public class HHMrevertPacket {
	/*
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *|Type|Head Length|version|HHM string|L|Domain Name|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Type:1b Head L:2b version:1b HHM_string 12b L:1b 
	 */

	//build revert version
	public static int buildRevertPacket(byte[] bOut, int iOffset, String strDomainName)
	{
		int iLength = 0;
		
		//type
		bOut[iLength+iOffset] = HHMConstant.Packet_Type_HHM_Revert;
		iLength += HHMConstant.Packet_Type_Util_Length;
		
		//packet length
		iLength += HHMConstant.Packet_Length_Util_Length;
		
		//version
		bOut[iOffset+iLength] = HHMConstant.Packet_protocol_version;
		iLength += HHMConstant.Packet_Version_Util_Length;
		
		//HHM string
		CommTool.string2bytes(bOut, HHMConstant.HHM_Fix_Flag, iOffset+iLength);
		iLength += HHMConstant.HHM_Fix_Flag_Length;
		
		//length for dir
		iLength += HHMConstant.Packet_DomainName_Length_Util_Length;
		
		//dir
		int iDomainLength = CommTool.string2bytes(bOut, strDomainName, iOffset+iLength);
		bOut[iOffset+iLength-HHMConstant.Packet_DomainName_Length_Util_Length] = (byte)(iDomainLength & 0xFF);
		
		iLength += iDomainLength;
		
		//set the lenth for head
		CommTool.short2bytes(bOut, 
				(short)((iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length) & 0xFFFF), 
				iOffset+HHMConstant.Packet_Type_Util_Length);
		
		return iLength;
	}
	
	public static String parseRevertPacket(byte[] bInput, int iOffset)
	{
		String strDomainName = "";
		
		int iLength = 0;
		
		//type
		iLength += HHMConstant.Packet_Type_Util_Length;
		
		//data length
		int iDataLength = CommTool.short2int(CommTool.bytes2short(bInput, 
				HHMConstant.Packet_Length_Util_Length, iOffset+iLength));		
		iLength += HHMConstant.Packet_Length_Util_Length;
		
		//version
		iLength += HHMConstant.Packet_Version_Util_Length;
		
		//HHM flag
		String strHHMFlag = CommTool.byte2string(bInput, iOffset+iLength, HHMConstant.HHM_Fix_Flag_Length);
		iLength += HHMConstant.HHM_Fix_Flag_Length;
		
		if(!HHMConstant.HHM_Fix_Flag.equals(strHHMFlag))
		{
			return "";
		}
		
		int iDomainNameLength = (int) CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_DomainName_Length_Util_Length;
		
		strDomainName = CommTool.byte2string(bInput, iOffset+iLength, iDomainNameLength);
		iLength += iDomainNameLength;
		
		if(iDataLength !=  (iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length))
		{
			return "";
		}	
		
		
		return strDomainName;
	}
}
