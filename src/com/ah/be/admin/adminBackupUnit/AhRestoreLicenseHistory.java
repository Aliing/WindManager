package com.ah.be.admin.adminBackupUnit;

import java.io.File;

import com.ah.be.admin.restoredb.AhRestoreDBTools;
import com.ah.be.admin.restoredb.RestoreAdmin;
import com.ah.be.admin.restoredb.RestoreLicenseAndActivation;
import com.ah.be.admin.restoredb.RestoreOrderKey;

public class AhRestoreLicenseHistory {
	
	public AhRestoreLicenseHistory() {
		
	}
	
	public static void main(String[] args)
	{
		AhRestoreDBTools.HM_XML_TABLE_PATH= args[0] + File.separator;
		
		// restore license history information
		RestoreLicenseAndActivation.restoreLicenseHistoryInfo();
		
		// restore activation key information
		RestoreLicenseAndActivation.restoreActivationKeyInfo();
		
		RestoreAdmin.restoreHASettings();
		
		//restore orderkey
		RestoreOrderKey.restoreOrderKey();
	}

}