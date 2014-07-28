package com.ah.be.admin.adminBackupUnit;

import com.ah.be.log.BeLogTools;

public class AhBackupFullData {

	public AhBackupFullData() {

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

			int iContent = Integer.parseInt(args[1]);

			//AhBackupTool oBackupTool = new AhBackupTool();
			//oBackupTool.backupFullDatabase(strXmlPath, iContent);
			AhBackupNewTool oBackupNewTool = new AhBackupNewTool();
			oBackupNewTool.backupWholeData(strXmlPath, iContent);
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