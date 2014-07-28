/*
 * @author:Lanbaoxiao
 * @description: the class is using for hiveos kernel dump
 */

package com.ah.be.admin.adminOperateImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.os.FileManager;

public class AhHiveAPKernelDump {

//	private static String KERNEL_DUMP_ROOT      = "/HiveAP/kernel_dump";
	
	private static int    MAX_DUMP_LOGS         = 20; 
	
	public static String TAR_NAME       = "device_crash_logs.tar.gz";
	
	
    public static String get_dump_location(String strMac)
	{
    	if(null == strMac)
    	{
    		return null;
    	}

	//	String strHome = KERNEL_DUMP_ROOT+File.separator+strMac;
		String strHome = AhDirTools.getKernelDumpDir(strMac);
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
		
		rotate_dump_location(strMac);
		
		return strHome;
	}
    
    private static boolean rotate_dump_location(String strMac)
    {
    	if(null == strMac)
    	{
    		return false;
    	}
    	
	//	String strHome = KERNEL_DUMP_ROOT+File.separator+strMac;
		String strHome = AhDirTools.getKernelDumpDir(strMac);
		File oFile = new File(strHome);
    	
    	if(!oFile.exists() || !oFile.isDirectory())
    	{
    		return false;
    	}
    	
    	
        File[] listFile = oFile.listFiles();	
		
		while(MAX_DUMP_LOGS-1 < listFile.length)
		{
			File fTmp = listFile[0];
		
			for(int i=1; i<listFile.length; ++i)
			{
				if(fTmp.lastModified() > listFile[i].lastModified())
				{
					fTmp = listFile[i];
				}
			}
			
			fTmp.delete();
			
			listFile = oFile.listFiles();			
		}
		
    	return true;
    }
    
//    public static void del_dump_location(List<String> strMacList)
//    {
//    	if(null == strMacList || 0 == strMacList.size())
//    	{
//    		return ;
//    	}
//    	
//    	for(int i=0; i<strMacList.size(); ++i)
//    	{
//    		String strDir = KERNEL_DUMP_ROOT+File.separator+strMacList.get(i);
//    		
//    		File ofile = new File(strDir);
//    		
//    		try
//    		{
//    			if(ofile.exists() && ofile.isDirectory())
//    			{
//    				//ofile.delete();only delete empty dir
//    				FileManager.getInstance().deleteDirectory(strDir);
//    			}
//    		}
//    		catch(Exception ex)
//    		{
//    			DebugUtil.adminDebugError("delete the kernel dump dir error", ex);
//    		}
//    	}
//    	
//    }
    
    public static  String zip_dump_localtion(List<String> strMacList)
    {
    	String strZipFile=""; 
    	
    	if(null == strMacList || 0 == strMacList.size())
    	{
    		return strZipFile;
    	}    	 	
    	
    	FileWriter fwGrubFile = null;
    	
    	BufferedWriter bwGrubFile = null;
    	
    	try
    	{   		
    		String strContent = "";

			for (String strMac : strMacList) {
			//	File dirMac = new File(KERNEL_DUMP_ROOT +File.separator+strMacList.get(i));
				File dirMac = new File(AhDirTools.getKernelDumpDir(strMac));

				if (dirMac.exists() && dirMac.isDirectory()) {
					strContent = strContent.concat(strMac);
					strContent = strContent.concat("\n");
				}
			}
        	
        	if("".equalsIgnoreCase(strContent))
        	{
        		return strZipFile;
        	}
        	
        //	String strTmp = KERNEL_DUMP_ROOT +File.separator+"tmp";
			String strTmp = AhDirTools.getKernelDumpDir() + "tmp";
        	
        	File oFile = new File(strTmp);
        	
        	if(oFile.exists() && oFile.isDirectory())
        	{
        		//oFile.delete();
                FileManager.getInstance().deleteDirectory(strTmp);
        	}
        	
        	oFile.mkdirs();   
        	
        	String strFileList = strTmp +File.separator+"zip_file_list.txt";
        	
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
    	}finally
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
    	
    	//create the file name for get tar files    	
    	strZipFile=TAR_NAME;
    	
    	String strErrMsg = "error_tar_kernel_dump";
    	
        File oFile = new File(BeAdminCentOSTools.ahShellRoot+"/tarHiveosKernelDump.sh");
    	
    	if(!oFile.exists() || !oFile.isFile())
    	{
    		return "";
    	}
    	
    	String[] strCmdss={"sh",BeAdminCentOSTools.ahShellRoot+"/tarHiveosKernelDump.sh",strZipFile};
    	
    	String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmdss, strErrMsg);
    	
    	if (strReturnMsg.equalsIgnoreCase(strErrMsg)) 
    	{
    		return "";
    	}
    	
    	return strZipFile;
    }
    
//    public static String get_zip_file_location(String strZipFileName)
//    {
//    	if(null == strZipFileName || "".equalsIgnoreCase(strZipFileName))
//    	{
//    		return "";
//    	}
//    	
//    	return KERNEL_DUMP_ROOT+File.separator+ZIP_DOWNLOAD_DIR+File.separator+strZipFileName;
//    }
//    
//    public static boolean del_zip_file(String strZipFileName)
//    {
//    	if(null == strZipFileName)
//    	{
//    		return false;
//    	}
//    	
//    	String strZipFile = KERNEL_DUMP_ROOT+File.separator+ZIP_DOWNLOAD_DIR+File.separator+strZipFileName;
//    	
//    	File oFile = new File(strZipFile);
//    	
//    	if(!oFile.exists() || !oFile.isFile() )
//    	{
//    		return true;
//    	}
//    	
//    	return oFile.delete(); 	
//    }
	
	public static void main(String[] args)
	{
		
	}

}