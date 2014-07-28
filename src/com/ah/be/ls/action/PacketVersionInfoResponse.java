/**
 *@filename		PacketDownLoadResponse.java
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

import java.util.ArrayList;
import java.util.List;

import com.ah.be.ls.data.PacketVersionInfoResponseData;
import com.ah.be.ls.data.VersionInfoData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class PacketVersionInfoResponse {
	
	
	/**
	 * the valid response
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Data Type|Data Length|count|Optional Data|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * Data Type:1byte;Data Length:2bytes;pro type 1byte
	 * 
	 * Optional Data, format
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |product type|File size|version length|version|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * @return: int length of the data packet response.
	 * 
	 */
	public static int parseDownloadResponse(byte[] bInput, int iOffset, PacketVersionInfoResponseData oData)
	{
        int iLength = 0;
		
		//data type
		byte bDataType = bInput[iOffset+iLength];
		if( bDataType !=  (byte)(CommConst.Download_Query_Data_Type & 0xFF))
		{
			//add error log
			
			return 0;
		}
		
		oData.setDataType(bDataType);
		iLength += CommConst.Type_Util_Length;
		
		//data length
		int iDataLength = CommTool.short2int(CommTool.bytes2short(bInput,
				                    CommConst.Data_Length_Util_Length, iOffset+iLength));		
		iLength += CommConst.Data_Length_Util_Length;
		
		//count
		oData.setCount((int)bInput[iLength+iOffset]);
		iLength += CommConst.Version_Count_Util_Length;	
		
		if(0 != oData.getCount())
		{
			//version info
			List<VersionInfoData> oInfoList = new ArrayList<VersionInfoData>();
			for(int i=0; i<oData.getCount(); ++i)
			{
				VersionInfoData oVersionData = new VersionInfoData();
				
				iLength += parseVersionInfo(bInput,iOffset+iLength,oVersionData);
				
				oInfoList.add(oVersionData);
			}
			
			oData.setVersionList(oInfoList);
		}
		
		//length is not equal
		if(iDataLength != 
			(iLength-CommConst.Type_Util_Length-CommConst.Data_Length_Util_Length))
		{
			return 0;
		}
		
		return iLength;
	}
	
	private static int parseVersionInfo(byte[] bInput, int iOffset,VersionInfoData oInfoData)
	{
		int iLength = 0;
		
		//pro type
		oInfoData.setProType(bInput[iOffset+iLength]);
		iLength += CommConst.Type_Util_Length;
		
		//File size
		oInfoData.setFileSize(CommTool.bytes2long(bInput, CommConst.File_Size_Util_Length, iOffset+iLength));
		iLength += CommConst.File_Size_Util_Length;
		
		//version length
		int iDataLength = CommTool.bytes2int(bInput, CommConst.Info_Length_Util_Length, iOffset+iLength);
		iLength += CommConst.Info_Length_Util_Length;
		
		//version content
		oInfoData.setVersion(CommTool.byte2string(bInput, iOffset+iLength, iDataLength));
		iLength += iDataLength;
		
		return iLength;
	}

}