/**
 *@filename		PacketUserRegInfo.java
 *@version
 *@author		Fiona
 *@createtime	2011-4-9 PM 05:33:06
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.ls.action;

import com.ah.be.license.BeLicenseModule;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;
import com.ah.bo.admin.UserRegInfoForLs;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class PacketUserRegInfo
{
	/** 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Length|VHM_ID/SYSTEM_ID|Order key|
	 * company|addressLine1|addressLine2|country|postalCode|name|telephone|email
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Length:2bytes;
	 */
	public static int buildUserRegInfo(byte[] bOut, int iOffset,UserRegInfoForLs oData)
	{
		int iLength = 0;
		
		// data length
		iLength += CommConst.Data_Length_Util_Length;
		
		String hmId = "";
		if (oData.getOwner().isHomeDomain()) {
			hmId = BeLicenseModule.HIVEMANAGER_SYSTEM_ID;
		} else {
			hmId = oData.getOwner().getVhmID();
		}
		
		// systemid/vhmid
		iLength = setUserInfoOneByOne(iLength, hmId, bOut, iOffset);
		
		// company
		iLength = setUserInfoOneByOne(iLength, oData.getCompany(), bOut, iOffset);
		
		// address 1
		iLength = setUserInfoOneByOne(iLength, oData.getAddressLine1(), bOut, iOffset);
		
		// address 2
		iLength = setUserInfoOneByOne(iLength, oData.getAddressLine2(), bOut, iOffset);
		
		// country
		iLength = setUserInfoOneByOne(iLength, oData.getCountry(), bOut, iOffset);
		
		// zip code
		iLength = setUserInfoOneByOne(iLength, oData.getPostalCode(), bOut, iOffset);
		
		// user name
		iLength = setUserInfoOneByOne(iLength, oData.getName(), bOut, iOffset);
		
		// phone
		iLength = setUserInfoOneByOne(iLength, oData.getTelephone(), bOut, iOffset);
		
		// email
		iLength = setUserInfoOneByOne(iLength, oData.getEmail(), bOut, iOffset);
		
		CommTool.short2bytes(bOut, 
				(short)((iLength-CommConst.Data_Length_Util_Length) & 0xFFFF), iOffset);
		
		return iLength;
	}
	
	private static int setUserInfoOneByOne(int iLength, String strValue, byte[] bOut, int iOffset) {
		iLength += CommConst.Common_String_Length_Unit_Length;
		
		int str_length = CommTool.string2bytes(bOut,strValue,iOffset+iLength);
		
		bOut[iOffset+iLength-CommConst.Common_String_Length_Unit_Length]=(byte)(str_length & 0xff);
		
		iLength += str_length;
		
		return iLength;
	}
}
