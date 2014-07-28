package com.ah.be.admin.hhmoperate.https.packet;

import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.admin.hhmoperate.https.data.HHMupdatePacketData;
import com.ah.be.ls.util.CommTool;
import com.ah.bo.admin.HmDomain;

public class HHMupdatePacket {


	/*
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|Type|Head Length|version|HHM string|L|dir|L|filename|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|L|Version|L|domain name|Max AP|GM flag|L|vhmid|L|owneruser|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- 
	 *|Max sim ap|Max sim ap client|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- 
	 * Type:1b Head L:2b version:1b HHM_string 12b L:1b Max AP:4b
	 * GM flag: 1,true 2, false;Vhmid L:1b;
	 */
	
	//build the update packet
	public static int buildupdatePacket(byte[] bOut, int iOffset, HHMupdatePacketData oData)
	{
		int iLength = 0;
		
		//type
		bOut[iLength+iOffset] = oData.getPackType();
		iLength += HHMConstant.Packet_Type_Util_Length;
		
		//packet length
		iLength += HHMConstant.Packet_Length_Util_Length;
		
		//version
		bOut[iOffset+iLength] = oData.getProtocolVersion();
		iLength += HHMConstant.Packet_Version_Util_Length;
		
		//HHM string
		CommTool.string2bytes(bOut, oData.getHHMFlag(), iOffset+iLength);
		iLength += HHMConstant.HHM_Fix_Flag_Length;
		
		//length for dir
		iLength += HHMConstant.Packet_File_Path_Length_Util_Length;
		
		//dir
		int iDirLength = CommTool.string2bytes(bOut, oData.getFilePath(), iOffset+iLength);
		bOut[iOffset+iLength-HHMConstant.Packet_File_Path_Length_Util_Length] = (byte)(iDirLength & 0xFF);
		
		iLength += iDirLength;
		
		//length for name
		iLength += HHMConstant.Packet_File_Path_Length_Util_Length;
		
		//file name
		int iFileNameLength = CommTool.string2bytes(bOut, oData.getFileName(), iOffset+iLength);
		bOut[iOffset+iLength-HHMConstant.Packet_File_Path_Length_Util_Length] = (byte)(iFileNameLength & 0xFF);
		
		iLength += iFileNameLength;
		
		//length for version
		iLength += HHMConstant.Packet_Version_Length_Util_Length;
		
		//version
		int iVersionLength = CommTool.string2bytes(bOut, oData.getHHMVersion(), iOffset+iLength);
		bOut[iOffset+iLength-HHMConstant.Packet_Version_Length_Util_Length] = (byte)(iVersionLength & 0xFF);
		
		iLength += iVersionLength;
		
		//length domain name
		iLength += HHMConstant.Packet_DomainName_Length_Util_Length;
		
		//domainname
		int iDomainNameLength = CommTool.string2bytes(bOut, oData.getHHMDomain().getDomainName(), iOffset+iLength);
		bOut[iOffset+iLength-HHMConstant.Packet_DomainName_Length_Util_Length] = (byte)(iDomainNameLength & 0xFF);
		
		iLength += iDomainNameLength;
		
		//maxap
		CommTool.int2bytes(bOut, oData.getHHMDomain().getMaxApNum(), iOffset+iLength);
		iLength += HHMConstant.Packet_MaxAp_Util_Length;
		
		//gm flag
		if(oData.getHHMDomain().isSupportGM())
		{
			bOut[iOffset+iLength] = HHMConstant.Gm_Flag_True;
		}
		else
		{
			bOut[iOffset+iLength] = HHMConstant.Gm_Flag_False;
		}
		iLength += HHMConstant.Packet_Gmflag_util_Length;
		
		//length for vhmid
		iLength += HHMConstant.Packet_DomainID_Util_Length;
		
		if(null == oData.getHHMDomain().getVhmID() || "".equals(oData.getHHMDomain().getVhmID()))
		{
			bOut[iOffset+iLength-HHMConstant.Packet_DomainID_Util_Length] = 0;
		}
		else
		{
			int iVhmIdLength = CommTool.string2bytes(bOut, oData.getHHMDomain().getVhmID(), iOffset+iLength);
			bOut[iOffset+iLength-HHMConstant.Packet_DomainID_Util_Length] = (byte)(iVhmIdLength & 0xFF);
			iLength += iVhmIdLength;
		}
		
		//length for owneruser
		iLength += HHMConstant.Packet_Owneruser_Util_Length;
		
//		HmUser ownerUser = QueryUtil.findBoById(HmDomain.class, oData.getHHMDomain().getId(),
//				new QueryBo4Domain()).getOwnerUser();
		
		String partnerId = oData.getHHMDomain().getPartnerId();

		if (null == partnerId || "".equalsIgnoreCase(partnerId)) {
			bOut[iOffset + iLength - HHMConstant.Packet_Owneruser_Util_Length] = 0;
		} else {
			int iOwnerUserLegth = CommTool.string2bytes(bOut, partnerId, iOffset
					+ iLength);
			bOut[iOffset + iLength - HHMConstant.Packet_Owneruser_Util_Length] = (byte) (iOwnerUserLegth & 0xFF);
			iLength += iOwnerUserLegth;
		}
		
		//max sim ap
		CommTool.int2bytes(bOut, oData.getHHMDomain().getMaxSimuAp(), iOffset+iLength);
		iLength += HHMConstant.Packet_MaxAp_Util_Length;
		
		//max sim ap client
		CommTool.int2bytes(bOut, oData.getHHMDomain().getMaxSimuClient(), iOffset+iLength);
		iLength += HHMConstant.Packet_MaxAp_Util_Length;
		
		//set the lenth for head
		CommTool.short2bytes(bOut, 
				(short)((iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length) & 0xFFFF), 
				iOffset+HHMConstant.Packet_Type_Util_Length);
		
		return iLength;
	}	
	
	public static int parseupdatePacket(byte[] bInput, int iOffset, HHMupdatePacketData oData)
	{
		byte bVersion = bInput[iOffset+HHMConstant.Packet_Type_Util_Length+HHMConstant.Packet_Length_Util_Length];
		
		switch(bVersion)
		{
		  case 1:
		  {
			  return parse1updatePacket(bInput,iOffset,oData);
			  
		  }
		  case 2:
		  {
			  return parseu2pdatePacket(bInput,iOffset,oData);
		  }
		  case 3:
		  {
			  return parse3updatePacket(bInput,iOffset,oData);
		  }
		  case 4:
		  {
			  return parse4updatePacket(bInput,iOffset,oData);
		  }
		  default:
		  {
			  return parse4updatePacket(bInput,iOffset,oData);
		  }
		}
	}
	
	//parse the update 
	/*
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|Type|Head Length|version|HHM string|L|dir|L|filename|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|L|Version|L|domain name|Max AP|GM flag|L|vhmid|L|owneruser|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- 
	 *|Max sim ap|Max sim ap client|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- 
	 * Type:1b Head L:2b version:1b HHM_string 12b L:1b Max AP:4b
	 * GM flag: 1,true 2, false;Vhmid L:1b;
	 */
	public static int parse4updatePacket(byte[] bInput, int iOffset, HHMupdatePacketData oData)
	{
		int iLength = 0;
		
		//type
		oData.setPackType(bInput[iLength+iOffset]);
		iLength += HHMConstant.Packet_Type_Util_Length;
		
		if(oData.getPackType() != HHMConstant.Packet_Type_HHM_Update)
		{
			return 0;
		}
		
		//data length
		int iDataLength = CommTool.short2int(CommTool.bytes2short(bInput, 
				HHMConstant.Packet_Length_Util_Length, iOffset+iLength));		
		iLength += HHMConstant.Packet_Length_Util_Length;
		
		//version
		oData.setProtocolVersion(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Version_Util_Length;
		
		//HHM string
		oData.setHHMFlag(CommTool.byte2string(bInput, iOffset+iLength, HHMConstant.HHM_Fix_Flag_Length));
		iLength += HHMConstant.HHM_Fix_Flag_Length;
		
		if(!HHMConstant.HHM_Fix_Flag.equals(oData.getHHMFlag()))
		{
			return 0;
		}
		
		//length for dir
		int iDirLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Packet_File_Path_Length_Util_Length;

	    //dir
	    oData.setFilePath(CommTool.byte2string(bInput, iOffset+iLength, iDirLength));
	    iLength += iDirLength;
	    
	    //legth for name
	    int iNameLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Packet_File_Path_Length_Util_Length;
	    
	    oData.setFileName(CommTool.byte2string(bInput, iOffset+iLength, iNameLength));
	    iLength += iNameLength;

		
		//version
		int iVersionLength = (int) CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Version_Length_Util_Length;
		
		oData.setHHMVersion(CommTool.byte2string(bInput, iOffset+iLength, iVersionLength));
		iLength += iVersionLength;
		
		//domain name
		HmDomain oDomain = new HmDomain();
		
		int iDomainNameLength = (int) CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_DomainName_Length_Util_Length;
		
		oDomain.setDomainName(CommTool.byte2string(bInput, iOffset+iLength, iDomainNameLength));
		iLength += iDomainNameLength;
		
		//max ap
		oDomain.setMaxApNum(CommTool.bytes2int(bInput, HHMConstant.Packet_MaxAp_Util_Length, iOffset+iLength));
		iLength += HHMConstant.Packet_MaxAp_Util_Length;
		
		//gm flag
		if(HHMConstant.Gm_Flag_True == bInput[iOffset+iLength])
		{
			oDomain.setSupportGM(true);
		}
		else
		{
			oDomain.setSupportGM(false);
		}
		
		iLength += HHMConstant.Packet_Gmflag_util_Length;
		
		//vhmid
		int iVhmidLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_DomainID_Util_Length;
		
		if( 0 != iVhmidLength)
		{
			oDomain.setVhmID(CommTool.byte2string(bInput, iOffset+iLength, iVhmidLength));
			iLength += iVhmidLength;
		}
		
		//owner user;
		int iOwneruserLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Owneruser_Util_Length;
		
		if( 0 != iOwneruserLength)
		{
//			HmUser ownerUser = new HmUser();
//			ownerUser.setUserName(CommTool.byte2string(bInput, iOffset+iLength, iOwneruserLength));
			oDomain.setPartnerId(CommTool.byte2string(bInput, iOffset+iLength, iOwneruserLength));
			iLength += iOwneruserLength;
		}
		
		//max sim ap
		oDomain.setMaxSimuAp(CommTool.bytes2int(bInput, HHMConstant.Packet_MaxAp_Util_Length, iOffset+iLength));
		iLength += HHMConstant.Packet_MaxAp_Util_Length;
		
		//max sim ap client
		oDomain.setMaxSimuClient(CommTool.bytes2int(bInput, HHMConstant.Packet_MaxAp_Util_Length, iOffset+iLength));
		iLength += HHMConstant.Packet_MaxAp_Util_Length;
		
		if(iDataLength !=  (iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length))
		{
			return 0;
		}			
		
		oData.setHHMDomain(oDomain);
		
		return iLength;
	}
	
	/*
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|Type|Head Length|version|HHM string|L|dir|L|filename|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|L|Version|L|domain name|Max AP|GM flag|L|vhmid|L|owneruser|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- 
	 * Type:1b Head L:2b version:1b HHM_string 12b L:1b Max AP:4b
	 * GM flag: 1,true 2, false;Vhmid L:1b;
	 */
	public static int parse3updatePacket(byte[] bInput, int iOffset, HHMupdatePacketData oData)
	{
		int iLength = 0;
		
		//type
		oData.setPackType(bInput[iLength+iOffset]);
		iLength += HHMConstant.Packet_Type_Util_Length;
		
		if(oData.getPackType() != HHMConstant.Packet_Type_HHM_Update)
		{
			return 0;
		}
		
		//data length
		int iDataLength = CommTool.short2int(CommTool.bytes2short(bInput, 
				HHMConstant.Packet_Length_Util_Length, iOffset+iLength));		
		iLength += HHMConstant.Packet_Length_Util_Length;
		
		//version
		oData.setProtocolVersion(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Version_Util_Length;
		
		//HHM string
		oData.setHHMFlag(CommTool.byte2string(bInput, iOffset+iLength, HHMConstant.HHM_Fix_Flag_Length));
		iLength += HHMConstant.HHM_Fix_Flag_Length;
		
		if(!HHMConstant.HHM_Fix_Flag.equals(oData.getHHMFlag()))
		{
			return 0;
		}
		
		//length for dir
		int iDirLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Packet_File_Path_Length_Util_Length;

	    //dir
	    oData.setFilePath(CommTool.byte2string(bInput, iOffset+iLength, iDirLength));
	    iLength += iDirLength;
	    
	    //legth for name
	    int iNameLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Packet_File_Path_Length_Util_Length;
	    
	    oData.setFileName(CommTool.byte2string(bInput, iOffset+iLength, iNameLength));
	    iLength += iNameLength;

		
		//version
		int iVersionLength = (int) CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Version_Length_Util_Length;
		
		oData.setHHMVersion(CommTool.byte2string(bInput, iOffset+iLength, iVersionLength));
		iLength += iVersionLength;
		
		//domain name
		HmDomain oDomain = new HmDomain();
		
		int iDomainNameLength = (int) CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_DomainName_Length_Util_Length;
		
		oDomain.setDomainName(CommTool.byte2string(bInput, iOffset+iLength, iDomainNameLength));
		iLength += iDomainNameLength;
		
		//max ap
		oDomain.setMaxApNum(CommTool.bytes2int(bInput, HHMConstant.Packet_MaxAp_Util_Length, iOffset+iLength));
		iLength += HHMConstant.Packet_MaxAp_Util_Length;
		
		//gm flag
		if(HHMConstant.Gm_Flag_True == bInput[iOffset+iLength])
		{
			oDomain.setSupportGM(true);
		}
		else
		{
			oDomain.setSupportGM(false);
		}
		
		iLength += HHMConstant.Packet_Gmflag_util_Length;
		
		//vhmid
		int iVhmidLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_DomainID_Util_Length;
		
		if( 0 != iVhmidLength)
		{
			oDomain.setVhmID(CommTool.byte2string(bInput, iOffset+iLength, iVhmidLength));
			iLength += iVhmidLength;
		}
		
		//owner user;
		int iOwneruserLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Owneruser_Util_Length;
		
		if( 0 != iOwneruserLength)
		{
//			HmUser ownerUser = new HmUser();
//			ownerUser.setUserName(CommTool.byte2string(bInput, iOffset+iLength, iOwneruserLength));
			oDomain.setPartnerId(CommTool.byte2string(bInput, iOffset+iLength, iOwneruserLength));
			iLength += iOwneruserLength;
		}
		
		if(iDataLength !=  (iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length))
		{
			return 0;
		}			
		
		oData.setHHMDomain(oDomain);
		
		return iLength;
	}
	
	//parse the update 
	/*
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|Type|Head Length|version|HHM string|L|dir|L|filename|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|L|Version|L|domain name|Max AP|GM flag|L|vhmid|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- 
	 * Type:1b Head L:2b version:1b HHM_string 12b L:1b Max AP:4b
	 * GM flag: 1,true 2, false;Vhmid L:1b;
	 */
	public static int parseu2pdatePacket(byte[] bInput, int iOffset, HHMupdatePacketData oData)
	{
		int iLength = 0;
		
		//type
		oData.setPackType(bInput[iLength+iOffset]);
		iLength += HHMConstant.Packet_Type_Util_Length;
		
		if(oData.getPackType() != HHMConstant.Packet_Type_HHM_Update)
		{
			return 0;
		}
		
		//data length
		int iDataLength = CommTool.short2int(CommTool.bytes2short(bInput, 
				HHMConstant.Packet_Length_Util_Length, iOffset+iLength));		
		iLength += HHMConstant.Packet_Length_Util_Length;
		
		//version
		oData.setProtocolVersion(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Version_Util_Length;
		
		//HHM string
		oData.setHHMFlag(CommTool.byte2string(bInput, iOffset+iLength, HHMConstant.HHM_Fix_Flag_Length));
		iLength += HHMConstant.HHM_Fix_Flag_Length;
		
		if(!HHMConstant.HHM_Fix_Flag.equals(oData.getHHMFlag()))
		{
			return 0;
		}
		
		//length for dir
		int iDirLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Packet_File_Path_Length_Util_Length;

	    //dir
	    oData.setFilePath(CommTool.byte2string(bInput, iOffset+iLength, iDirLength));
	    iLength += iDirLength;
	    
	    //legth for name
	    int iNameLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Packet_File_Path_Length_Util_Length;
	    
	    oData.setFileName(CommTool.byte2string(bInput, iOffset+iLength, iNameLength));
	    iLength += iNameLength;

		
		//version
		int iVersionLength = (int) CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Version_Length_Util_Length;
		
		oData.setHHMVersion(CommTool.byte2string(bInput, iOffset+iLength, iVersionLength));
		iLength += iVersionLength;
		
		//domain name
		HmDomain oDomain = new HmDomain();
		
		int iDomainNameLength = (int) CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_DomainName_Length_Util_Length;
		
		oDomain.setDomainName(CommTool.byte2string(bInput, iOffset+iLength, iDomainNameLength));
		iLength += iDomainNameLength;
		
		//max ap
		oDomain.setMaxApNum(CommTool.bytes2int(bInput, HHMConstant.Packet_MaxAp_Util_Length, iOffset+iLength));
		iLength += HHMConstant.Packet_MaxAp_Util_Length;
		
		//gm flag
		if(HHMConstant.Gm_Flag_True == bInput[iOffset+iLength])
		{
			oDomain.setSupportGM(true);
		}
		else
		{
			oDomain.setSupportGM(false);
		}
		
		iLength += HHMConstant.Packet_Gmflag_util_Length;
		
		//vhmid
		int iVhmidLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_DomainID_Util_Length;
		
		if( 0 != iVhmidLength)
		{
			oDomain.setVhmID(CommTool.byte2string(bInput, iOffset+iLength, iVhmidLength));
			iLength += iVhmidLength;
		}
		
		if(iDataLength !=  (iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length))
		{
			return 0;
		}			
		
		oData.setHHMDomain(oDomain);
		
		return iLength;
	}
	
	/*
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|Type|Head Length|version|HHM string|L|dir|L|filename|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	 *|L|Version|L|domain name|Max AP|GM flag|
	 *+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- 
	 * Type:1b Head L:2b version:1b HHM_string 12b L:1b Max AP:4b
	 * GM flag: 1,true 2, false;
	 */
	//parse the update 
	public static int parse1updatePacket(byte[] bInput, int iOffset, HHMupdatePacketData oData)
	{
		int iLength = 0;
		
		//type
		oData.setPackType(bInput[iLength+iOffset]);
		iLength += HHMConstant.Packet_Type_Util_Length;
		
		if(oData.getPackType() != HHMConstant.Packet_Type_HHM_Update)
		{
			return 0;
		}
		
		//data length
		int iDataLength = CommTool.short2int(CommTool.bytes2short(bInput, 
				HHMConstant.Packet_Length_Util_Length, iOffset+iLength));		
		iLength += HHMConstant.Packet_Length_Util_Length;
		
		//version
		oData.setProtocolVersion(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Version_Util_Length;
		
		//HHM string
		oData.setHHMFlag(CommTool.byte2string(bInput, iOffset+iLength, HHMConstant.HHM_Fix_Flag_Length));
		iLength += HHMConstant.HHM_Fix_Flag_Length;
		
		if(!HHMConstant.HHM_Fix_Flag.equals(oData.getHHMFlag()))
		{
			return 0;
		}
		
		//length for dir
		int iDirLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Packet_File_Path_Length_Util_Length;

	    //dir
	    oData.setFilePath(CommTool.byte2string(bInput, iOffset+iLength, iDirLength));
	    iLength += iDirLength;
	    
	    //legth for name
	    int iNameLength = (int)CommTool.byte2short(bInput[iOffset+iLength]);
	    iLength += HHMConstant.Packet_File_Path_Length_Util_Length;
	    
	    oData.setFileName(CommTool.byte2string(bInput, iOffset+iLength, iNameLength));
	    iLength += iNameLength;

		
		//version
		int iVersionLength = (int) CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_Version_Length_Util_Length;
		
		oData.setHHMVersion(CommTool.byte2string(bInput, iOffset+iLength, iVersionLength));
		iLength += iVersionLength;
		
		//domain name
		HmDomain oDomain = new HmDomain();
		
		int iDomainNameLength = (int) CommTool.byte2short(bInput[iOffset+iLength]);
		iLength += HHMConstant.Packet_DomainName_Length_Util_Length;
		
		oDomain.setDomainName(CommTool.byte2string(bInput, iOffset+iLength, iDomainNameLength));
		iLength += iDomainNameLength;
		
		//max ap
		oDomain.setMaxApNum(CommTool.bytes2int(bInput, HHMConstant.Packet_MaxAp_Util_Length, iOffset+iLength));
		iLength += HHMConstant.Packet_MaxAp_Util_Length;
		
		//gm flag
		if(HHMConstant.Gm_Flag_True == bInput[iOffset+iLength])
		{
			oDomain.setSupportGM(true);
		}
		else
		{
			oDomain.setSupportGM(false);
		}
		
		iLength += HHMConstant.Packet_Gmflag_util_Length;
		
		if(iDataLength !=  (iLength-HHMConstant.Packet_Type_Util_Length-HHMConstant.Packet_Length_Util_Length))
		{
			return 0;
		}		
		
		oData.setHHMDomain(oDomain);
		
		return iLength;
	}
}
