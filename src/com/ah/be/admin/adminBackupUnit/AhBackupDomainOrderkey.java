package com.ah.be.admin.adminBackupUnit;

import com.ah.be.log.BeLogTools;

public class AhBackupDomainOrderkey {
	
	public AhBackupDomainOrderkey() {
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			if(2 != args.length )
			{
				BeLogTools.restoreLog(BeLogTools.ERROR, "the parameters of AhBackupFullData are error! ");
				
				System.exit(1);
				
				return;
			}		
			
			String strXmlPath = args[0];
			
			String strDomainName = args[1];
			
			AhBackupTool oBackupTool = new AhBackupTool();
			oBackupTool.backupdomainOrderInfo(strXmlPath, strDomainName);
		}
		catch(Exception ex)
		{
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