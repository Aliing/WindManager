package com.ah.be.ls.action;

import com.ah.be.ls.data.QueryLicenseInfo;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;
import com.ah.bo.admin.UserRegInfoForLs;

public class PacketOrderkeyResponse {

	/** 
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |AP Numbers|vHM Numbers|user manager flag|L|user manager ls|LS type|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * ls type:Eval |valid days|user reg info|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Ls type:perm |L|subscription end date|L|support end date|user reg info|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 
	 * Data Type:1byte;Data Length:2bytes
	 * AP numbers:4bytes; vhm numbers:4bytes;user manager flag:1byte 0:true;1:false;
	 * num guests:4bytes; ls type:1byte; valid days:1byte
	 * 
	 */
	private static final int User_Manager_Flag_True      = 0;
	//private static final int User_Manager_Flag_False     = 1;
	
	public static int parseOederkeyResponse(byte[] bInput, int iOffset, QueryLicenseInfo oData)
	{
		int iLength = 0;
		byte bDataType = bInput[iOffset+iLength];
		
		if(bDataType != CommConst.Order_key_Response_Data_type)
		{
			return 0;
		}
		
		oData.setDataType(bDataType);
		iLength += CommConst.Type_Util_Length;
		//data length
		int iDataLength = CommTool.short2int(CommTool.bytes2short(bInput, 
                CommConst.Data_Length_Util_Length, iOffset+iLength));
		
		iLength += CommConst.Data_Length_Util_Length;
		
		//ap nums
		oData.setNumberOfAp(CommTool.bytes2int(bInput, CommConst.Common_INT_Length_Unit_Length, iOffset+iLength));
		iLength += CommConst.Common_INT_Length_Unit_Length;
		
		//vhm numbers
		oData.setNumberOfVhm(CommTool.bytes2int(bInput, CommConst.Common_INT_Length_Unit_Length, iOffset+iLength));
		iLength += CommConst.Common_INT_Length_Unit_Length;
		
		//user manager flag
		byte bFlag = bInput[iOffset+iLength];
		if(bFlag == User_Manager_Flag_True)
		{
			oData.setUserManagerLicenseExistFlag(true);
		}
		else
		{
			oData.setUserManagerLicenseExistFlag(false);
		}
		
		iLength += CommConst.User_Manager_Flag_Unit_length;
		
		int user_manager_length = CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += CommConst.Common_String_Length_Unit_Length;
		
		if(user_manager_length != 0)
		{
			oData.setManageLicense(CommTool.byte2string(bInput, iOffset+iLength, user_manager_length));
			iLength += user_manager_length;
		}
				
		//type
		byte bType = bInput[iOffset+iLength];
		oData.setLicenseType(bType);
		iLength += CommConst.Common_Type_Unit_Length;
		
		//permanent or renew
		if(bType != QueryLicenseInfo.License_Type_Evaluation)
		{
		    //subscription end date
			int start_time_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			oData.setSubEndDate(CommTool.byte2string(bInput, iOffset+iLength, start_time_length));
			iLength += start_time_length;
			
		    //support end date
			int end_time_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			oData.setSupportEndDate(CommTool.byte2string(bInput, iOffset+iLength, end_time_length));
			iLength += end_time_length;
		}
		else
		{
			//evail
			oData.setNumberOfEvalValidDays(CommTool.bytes2int(bInput, CommConst.Common_INT_Length_Unit_Length, iOffset+iLength));
			iLength += CommConst.Common_INT_Length_Unit_Length;
		}
		
		//cvg numbers
		oData.setNumberOfCvg(CommTool.bytes2int(bInput, CommConst.Common_INT_Length_Unit_Length, iOffset+iLength));
		iLength += CommConst.Common_INT_Length_Unit_Length;
		
		//cvg subscription end date
		if (bType != QueryLicenseInfo.License_Type_Evaluation) {
			//subscription end date
			int cvg_sub_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			oData.setCvgSubEndDate(CommTool.byte2string(bInput, iOffset+iLength, cvg_sub_length));
			iLength += cvg_sub_length;
		}
		
		// contain user information
		if (iDataLength > (iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length)) {
			UserRegInfoForLs userInfo = new UserRegInfoForLs();
			
			// company
			int company_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			userInfo.setCompany(CommTool.byte2string(bInput, iOffset+iLength, company_length));
			iLength += company_length;
			
			// country
			int country_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			userInfo.setCountry(CommTool.byte2string(bInput, iOffset+iLength, country_length));
			iLength += country_length;
			
			// address1
			int address1_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			userInfo.setAddressLine1(CommTool.byte2string(bInput, iOffset+iLength, address1_length));
			iLength += address1_length;
			
			// address2
			int address2_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			userInfo.setAddressLine2(CommTool.byte2string(bInput, iOffset+iLength, address2_length));
			iLength += address2_length;
			
			// zip code
			int zip_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			userInfo.setPostalCode(CommTool.byte2string(bInput, iOffset+iLength, zip_length));
			iLength += zip_length;
			
			// user name
			int user_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			userInfo.setName(CommTool.byte2string(bInput, iOffset+iLength, user_length));
			iLength += user_length;
			
			// email
			int email_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			userInfo.setEmail(CommTool.byte2string(bInput, iOffset+iLength, email_length));
			iLength += email_length;
			
			// phone
			int phone_length = CommTool.byte2short(bInput[iOffset+iLength]);
			iLength += CommConst.Common_String_Length_Unit_Length;
			userInfo.setTelephone(CommTool.byte2string(bInput, iOffset+iLength, phone_length));
			iLength += phone_length;
			
			oData.setUserRegInfo(userInfo);
		}
		
		//length is not equal
		if(iDataLength != (iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length))
		{
			return 0;
		}	
		
		return iLength;
	}
	
}