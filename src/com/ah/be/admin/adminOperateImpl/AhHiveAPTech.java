package com.ah.be.admin.adminOperateImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.AhDirTools;

public class AhHiveAPTech {

//	private static String HIVEAP_TECH_ROOT      = "/HiveAP/tech_dump";

	private static String TAR_NAME              = "device-tech-logs.tar.gz";
	
	public static final String FINAL_TAR_NAME        = "device_support_logs.tar.gz";
	
	public static final String REBOOT_HISTORY_FILE_NAME       = "reboot_history.csv";
	public static final String REBOOT_HISTORY_TAR_NAME       = "device_diagnosis_log.tar.gz";
	public static final String REBOOT_HISTORY_HOME         = "/HiveAP/reboot_dump";
	
	private static String DOWNLOAD_HOME         = "/HiveAP/support";

	/*-
	public static String get_tech_location()
	{
	//	String strHome = HIVEAP_TECH_ROOT;
		String strHome = AhDirTools.getTechDir();

		File oFile = new File(strHome);

		if(!oFile.exists() || !oFile.isDirectory())
		{
			try
			{
			    oFile.mkdirs();
			}
			catch(Exception ex)
			{
				//add log
				DebugUtil.adminDebugError("init the dires for hiveos kernel failed", ex);

			    return null;
			}
		}

	//	strHome = strHome+File.separator;

		return strHome;
	}*/

//	public static String get_zip_file_location(String strZipFileName)
//    {
//    	if(null == strZipFileName || "".equalsIgnoreCase(strZipFileName))
//    	{
//    		return "";
//    	}
//
//    	return HIVEAP_TECH_ROOT+File.separator+ZIP_DOWNLOAD_DIR+File.separator+strZipFileName;
//    }
	
	public static synchronized String zip_tech_dump_files(Map<String,String> hmap, List<String> strMacList)
	{
		zip_tech_files(hmap);
		
		AhHiveAPKernelDump.zip_dump_localtion(strMacList);
		
		String[] strCmds = {"sh",BeAdminCentOSTools.ahShellRoot+"/tarHiveosSupport.sh",AhHiveAPKernelDump.TAR_NAME,AhHiveAPTech.TAR_NAME,REBOOT_HISTORY_TAR_NAME,AhHiveAPTech.FINAL_TAR_NAME};
		
		if(!BeOperateHMCentOSImpl.isRslt_0(strCmds))
		{
			return "";
		}
		
		return DOWNLOAD_HOME+"/"+ FINAL_TAR_NAME;
	}

	private static String zip_tech_files(Map<String,String> hmap)
	{
		String strZipFile="";

		if(null == hmap || 0 == hmap.size())
    	{
    		return strZipFile;
    	}

        FileWriter fwGrubFile = null;

    	BufferedWriter bwGrubFile = null;

    	try
    	{
    		String strContent = "";

    	//	Iterator<String> it = hmap.keySet().iterator();

			for (String strKey : hmap.keySet()) {
				strContent = strContent.concat(strKey);
				strContent = strContent.concat("\r\n");

				String strDesc = hmap.get(strKey);

				strContent = strContent.concat(strDesc);
				strContent = strContent.concat("\r\n");
			}

    		if("".equalsIgnoreCase(strContent))
        	{
        		return strZipFile;
        	}

		//	String strFileList =  HIVEAP_TECH_ROOT+File.separator+"readme.txt";
			String strFileList = AhDirTools.getTechDir() + "readme.txt";

        	File oZipList = new File(strFileList);

        	fwGrubFile = null;

        	bwGrubFile = null;

            oZipList.createNewFile();

    		fwGrubFile = new FileWriter(oZipList);

    		bwGrubFile = new BufferedWriter(fwGrubFile);

        	bwGrubFile.write(strContent, 0, strContent.length());

    		bwGrubFile.close();

    		fwGrubFile.close();
    	}
    	catch(Exception ex)
    	{
            DebugUtil.adminDebugError("create zip file list", ex);

    		return "";
    	}
    	finally
    	{
    		if(null != bwGrubFile)
    		{
    			try
    			{
    				bwGrubFile.close();
    			}
    			catch(Exception bwex)
    			{
    				DebugUtil.adminDebugError("create zip file list", bwex);
    			}
    		}

    		if(null != fwGrubFile)
    		{
    			try
    			{
    				fwGrubFile.close();
    			}
    			catch(Exception fwex)
    			{
    				DebugUtil.adminDebugError("create zip file list", fwex);
    			}
    		}
    	}

     	strZipFile=TAR_NAME;

    	String strErrMsg = "error_tar_tech";

    	File oFile = new File(BeAdminCentOSTools.ahShellRoot+"/tarHiveosTech.sh");

    	if(!oFile.exists() || !oFile.isFile())
    	{
    		return "";
    	}

    	String[] strCmdss={"sh",BeAdminCentOSTools.ahShellRoot+"/tarHiveosTech.sh",strZipFile};

    	String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmdss, strErrMsg);

    	if (strReturnMsg.equalsIgnoreCase(strErrMsg))
    	{
    		return "";
    	}

    	return strZipFile;
	}

}
