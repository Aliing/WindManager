package com.ah.be.admin.restoredb;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;

public class AhRestoreHHMDomainData {
	
	public static void main(String[] args)
	{
		try
		{
			if(args.length != 3)
			{
				//add log
				BeLogTools.debug(HmLogConst.M_RESTORE, "the parameters of AhRestoreHHMDomainData are error! ");
				
				return;
			}
			
			AhRestoreDBData oRestoreData = new AhRestoreDBData();
			
			Long lDomainId = Long.parseLong(args[0]);
			
			String strPath = args[1];
			String strtOldName = args[2];
			
			oRestoreData.restoreHHMDomainData(lDomainId, strPath, strtOldName);			
		}
		catch(Exception ex)
		{
			//add log
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());

		}
	}

}