package com.ah.be.ls.action;

import com.ah.be.ls.data.PacketTrapData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketTrap {
	/**
	 * 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|Activation Key|HM IP|System ID|Trap Type|severity|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |time|Reason|Desc Length|Desciption|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 
	 */
	public static int buildTrap(byte[] bOut, int iOffset, PacketTrapData oData)
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
		CommTool.ip2bytes(bOut, iOffset+iLength, oData.getHmIP());
		iLength += CommConst.IP_Util_Length;
		
		//system id
		CommTool.string2bytes(bOut, oData.getSystemId(), iOffset+iLength);
		iLength += CommConst.System_ID_Util_Length;
		
		//trap type
		bOut[iOffset+iLength] = oData.getTrapType();
		iLength += CommConst.Type_Util_Length;
		
		//severity
		bOut[iOffset+iLength] = oData.getSeverity();
		iLength += CommConst.Trap_Severity_Util_Length;
		
		//time
		CommTool.long2bytes(bOut, oData.getTime(), iOffset+iLength);
		iLength += CommConst.Trap_Time_Util_Length;
		
		//Reason
		CommTool.short2bytes(bOut, oData.getReason(), iOffset+iLength) ;
		iLength += CommConst.Trap_Reason_Util_Length;
		
		//desc length
		iLength += CommConst.Description_Legth_Util_Length;
		
		//description
		int iDescLength = CommTool.string2bytes(bOut, oData.getDescription(), iOffset+iLength);
		
		bOut[iOffset+iLength-CommConst.Description_Legth_Util_Length] = (byte)(iDescLength & 0xff);
		
        iLength += iDescLength;
	    
		//set length
		CommTool.short2bytes(bOut,
				(short)((iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length) & 0xFFFF),
				iOffset+CommConst.Type_Util_Length);
        
        return iLength;
	}
	

}
