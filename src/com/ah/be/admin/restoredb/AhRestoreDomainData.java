package com.ah.be.admin.restoredb;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;

public class AhRestoreDomainData {
	
	public AhRestoreDomainData() {
		
	}
	
	public static void main(String[] args)
	{
		try
		{

			if(args.length != 2)
			{
				//add log
				BeLogTools.debug(HmLogConst.M_RESTORE, "the parameters of AhRestoreDomainData are error! ");
				
				return;
			}
			
			AhRestoreDBData oRestoreData = new AhRestoreDBData();
			
			Long lDomainId = Long.parseLong(args[0]);
			
			String strOldDomainName = args[1];
			
			oRestoreData.restoreDomainData(lDomainId, strOldDomainName);			
		}
		catch(Exception ex)
		{
			//add log
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
	}

}