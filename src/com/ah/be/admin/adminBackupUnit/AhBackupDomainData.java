package com.ah.be.admin.adminBackupUnit;

import com.ah.be.log.BeLogTools;

public class AhBackupDomainData {
	
	public AhBackupDomainData(){}
	
	public static void main(String[] args)
	{
		try
		{
			if(args.length != 3)
			{
				//add log
				BeLogTools.restoreLog(BeLogTools.ERROR, "the parameters of AhBackupDomainData are error! ");
				
				System.exit(1);
				
				return;
			}			
			
			Long lDomainId = Long.parseLong(args[0]);
			
			String strXmlPath = args[1];
			
			int iContent = Integer.parseInt(args[2]);
			
			//AhBackupTool oBackupTool = new AhBackupTool();			
			//oBackupTool.backupDomainDatabase(lDomainId, strXmlPath, iContent);
			
			AhBackupNewTool oBackupNewTool = new AhBackupNewTool();
			oBackupNewTool.backupvHMData(lDomainId, strXmlPath, iContent);
		}
		catch(Exception ex)
		{
			//add log
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
			
			System.exit(1);
		}
		catch(Error er)
		{
            BeLogTools.restoreLog(BeLogTools.ERROR, er.getMessage());
			
			System.exit(1);
		}
	}

}