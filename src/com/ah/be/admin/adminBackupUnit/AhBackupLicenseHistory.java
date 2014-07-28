package com.ah.be.admin.adminBackupUnit;

import com.ah.be.log.BeLogTools;

public class AhBackupLicenseHistory {
	
	public AhBackupLicenseHistory() {

	}
	
	public static void main(String[] args)
	{
		if(1 != args.length)
		{
			BeLogTools.restoreLog(BeLogTools.ERROR, "the parameters of AhBackupLicenseHistory are error! ");
			
			return;
		}
		
		AhBackupTool oBackupTool = new AhBackupTool();		
		
		String strXmlPath = args[0];
		
		oBackupTool.backupLicenseHistory(strXmlPath);
		oBackupTool.backupActivationkey(strXmlPath);
		oBackupTool.backupHaSetting(strXmlPath);
		oBackupTool.backupOrderkeyInfo(strXmlPath);
		
	}

}