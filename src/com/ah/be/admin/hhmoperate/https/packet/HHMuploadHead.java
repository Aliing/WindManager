package com.ah.be.admin.hhmoperate.https.packet;

import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.admin.hhmoperate.https.data.HHMuploadHeadData;
import com.ah.be.ls.util.CommTool;

public class HHMuploadHead {
	
	/*
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|Type|Head Length|version|HHM string|L|dir|L|filename|status|offset|data L|data|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- 
	 * 
	 * Type:1b Head L:2b version:1b HHM_string 12b L:1b status:1b offset:8b data L:4b
	 */
	
	//build the packet
	public static int buildHHMuploadHead(byte[] bOut, int iOffset, HHMuploadHeadData oheadData)
	{
		int iLength = 0;
		
		//type
		bOut[iOffset+iLength] = oheadData.getPackType();
		iLength += HHMConstant.Packet_Type_Util_Length;
		
		//head length
		iLength += HHMConstant.Packet_Length_Util_Length;
		
		//version 
		bOut[iOffset+iLength] = oheadData.getProtocolVersion();
		iLength += HHMConstant.Packet_Version_Util_Length;
		
		//HHM string
		CommTool.string2bytes(bOut, oheadData.getHHMFlag(), iOffset+iLength);
		iLength += HHMConstant.HHM_Fix_Flag_Length;
		
		//length for dir
		iLength += HHMConstant.Packet_File_Path_Length_Util_Length;
		
		//dir
		int iDirLength = CommTool.string2bytes(bOut, oheadData.getFilePath(), iOffset+iLength);
		bOut[iOffset+iLength-HHMConstant.Packet_File_Path_Length_Util_Length] = (byte)(iDirLength & 0xFF);
		
		iLength += iDirLength;
		
		//length for name
		iLength += HHMConstant.Packet_File_Path_Length_Util_Length;
		
		//file name
		int iFileNameLength = CommTool.string2bytes(bOut, oheadData.getFileName(), iOffset+iLength);
		bOut[iOffset+iLength-HHMConstant.Packet_File_Path_Length_Util_Length] = (byte)(iFileNameLength & 0xFF);
		
		iLength += iFileNameLength;
		
		//status
		bOut[iOffset+iLength] = oheadData.getSendStatus();
		iLength += HHMConstant.Upload_Send_Status_Util_Length;
		
		//offset
		CommTool.long2bytes(bOut, oheadData.getOffset(), iOffset+iLength);
		iLength += HHMConstant.Upload_Offset_Util_Length;
		
		//data length.
		CommTool.int2bytes(bOut, oheadData.getDataLength(), iOffset+iLength);
		iLength += HHMConstant.Upload_Data_Size_Util_Length;
		
		//set the lenth for head
		CommTool.short2bytes(bOut, 
				(short)((iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length) & 0xFFFF), 
				iOffset+HHMConstant.Packet_Type_Util_Length);
		
		return iLength;
		
	}
	
	//parse the packet
	public static int parseHHMuploadHead(byte[] bInput, int iOffset, HHMuploadHeadData oheadData)
	{
		int iLength = 0;
		
		//type
		oheadData.setPackType(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Type_Util_Length;
		
		//head length
		int iDataLength =  CommTool.short2int(CommTool.bytes2short(bInput, 
				HHMConstant.Packet_Length_Util_Length, iOffset+iLength));
		iLength += HHMConstant.Packet_Length_Util_Length;
		
		//version
		oheadData.setProtocolVersion(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Version_Util_Length;
		
		//HHM flag
		oheadData.setHHMFlag(CommTool.byte2string(bInput, iOffset+iLength, HHMConstant.HHM_Fix_Flag_Length));
		iLength += HHMConstant.HHM_Fix_Flag_Length;
		
		if(!HHMConstant.HHM_Fix_Flag.equals(oheadData.getHHMFlag()))
		{
			return 0;
		}
		
		//length for dir
		int iDirLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Packet_File_Path_Length_Util_Length;

	    //dir
	    oheadData.setFilePath(CommTool.byte2string(bInput, iOffset+iLength, iDirLength));
	    iLength += iDirLength;
	    
	    //legth for name
	    int iNameLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Packet_File_Path_Length_Util_Length;
	    
	    oheadData.setFileName(CommTool.byte2string(bInput, iOffset+iLength, iNameLength));
	    iLength += iNameLength;
	    
	    //status
	    oheadData.setSendStatus(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Upload_Send_Status_Util_Length;
	    
	    //offset
	    oheadData.setOffset(CommTool.bytes2long(bInput, HHMConstant.Upload_Offset_Util_Length, iOffset+iLength));
	    iLength += HHMConstant.Upload_Offset_Util_Length;
	    
	    //Data legth
	    oheadData.setDataLength(CommTool.bytes2int(bInput, HHMConstant.Upload_Data_Size_Util_Length, iOffset+iLength));
		iLength += HHMConstant.Upload_Data_Size_Util_Length;
		
		if(iDataLength !=  (iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length))
		{
			return 0;
		}
	    
		return iLength;
	}
	

}
