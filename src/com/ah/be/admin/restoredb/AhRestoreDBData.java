package com.ah.be.admin.restoredb;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.admin.adminOperateImpl.BeUploadCfgTools;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.os.FileManager;
import com.ah.be.parameter.BeParaModuleDefImpl;
import com.ah.be.performance.db.TablePartitionProcessor;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

public class AhRestoreDBData {
	
	public class RestoreInfo{
		
		private long lDomainId;
		private String strRestorePath;
		
		public long getDomainId()
		{
			return lDomainId;
		}
		
		public void setDomainId(long lId)
		{
			lDomainId = lId;
		}
		
		public String getPath()
		{
			return strRestorePath;
		}
		
		public void setPath(String strPath)
		{
			strRestorePath = strPath;
		}
	}
	
	private final BlockingQueue<RestoreInfo> DomainInfoQueue = new LinkedBlockingQueue<RestoreInfo>(1000);
//	private static final int THREADS_NUM = 5;
	
	public AhRestoreDBData(){}
	
	public void restoreDomainData(long lDomainId, String StrOldDomainName)
	{
		String strPath = BeAdminCentOSTools.ahBackupdir	+ File.separatorChar + StrOldDomainName + File.separatorChar;
		
		restoreDomain(lDomainId, strPath, "");
	}
	
	public void restoreHHMDomainData(long lDomainId, String strPath, String strOldName)
	{
		restoreDomain(lDomainId,strPath, strOldName);
	}
	
	private void restoreDomain(long lDomainId, String strPath, String strOldName)
	{
		try
		{
			//find domain
            HmDomain oDomain = QueryUtil.findBoById(HmDomain.class, lDomainId);
			
			if (null == oDomain)
			{
				BeUploadCfgTools.finishRunningFlag();
				
				BeLogTools.showshellLog(BeLogTools.INFO, "restore process is finished");
				
				return;
			}
			
			//cleanLogFile();
			
			//insert default data
			BeParaModuleDefImpl oDefImpl = new BeParaModuleDefImpl();
			
			oDefImpl.constructDefaultProfile();
			
			//set the static var domain info
			//AhRestoreDBTools.HM_RESTORE_DOMAIN = oDomain;
			
			AhRestoreDBTools.HM_XML_TABLE_PATH = strPath; 
		    long lstart = System.currentTimeMillis();
			
			RestoreAdmin.initDomainmap(oDomain, strOldName);
			
			RestoreAdmin.restoreDomainExt(oDomain);
			RestoreAdmin.initDefaultOrderKey(oDomain);
			
			//restore domain data
			AhRestoreDBTools oDBRestore = new AhRestoreDBTools();
			
			AhRestoreDBTools.logRestoreMsg("restore the data for domain: "+oDomain.getDomainName());
			BeLogTools.showshellLog(BeLogTools.INFO, "##################################################");
			BeLogTools.showshellLog(BeLogTools.INFO, "###");
			BeLogTools.showshellLog(BeLogTools.INFO, "### restore the data for domain: "+oDomain.getDomainName());
			BeLogTools.showshellLog(BeLogTools.INFO, "###");
			BeLogTools.showshellLog(BeLogTools.INFO, "##################################################");
			
			oDBRestore.restoreNewFramework(false);
			
			AhRestoreNewMapTools.resetCache();
			
			AhDBTool.changeDomainName(oDomain.getDomainName());
			
			BeUploadCfgTools.finishRunningFlag();		
			
			BeLogTools.showshellLog(BeLogTools.INFO, "restore process is finished");
			BeLogTools.showshellLog(BeLogTools.INFO, "Please press <Enter> key to main menu");
			long lend = System.currentTimeMillis();
			AhRestoreDBTools.logRestoreMsg("The whole restore procress take time is: "+ (lend-lstart)/1000+" sec.");
			
			//cleanLogFile();
		}
		catch(Exception ex)
		{
			BeUploadCfgTools.finishRunningFlag();
			
			BeLogTools.showshellLog(BeLogTools.INFO, "restore process is finished");
			BeLogTools.showshellLog(BeLogTools.INFO, "Please press <Enter> key to main menu");
			//cleanLogFile();
			
			BeLogTools.debug(HmLogConst.M_RESTORE, ex.getMessage());			
		}
	}
	
	public void restoreFullData()
	{
		try
		{ 
			//delete all data(that function will be implement in shell script)
			
			//insert default data
			BeParaModuleDefImpl oDefImpl = new BeParaModuleDefImpl();
			
			oDefImpl.constructDefaultProfile();
			
			//if data is 2.0 3.0 or 3.1
			File oXmlDir = new File(BeAdminCentOSTools.ahBackupdir);

			if(!oXmlDir.isDirectory())
			{
				BeUploadCfgTools.finishRunningFlag();
				
				BeLogTools.showshellLog(BeLogTools.INFO, "restore process is finished");
				BeLogTools.showshellLog(BeLogTools.INFO, "Please press <Enter> key to main menu");
				
				//cleanLogFile();
				
				return;
			}

			if( !oXmlDir.exists() )
			{
				BeUploadCfgTools.finishRunningFlag();
				
				BeLogTools.showshellLog(BeLogTools.INFO, "restore process is finished");
				BeLogTools.showshellLog(BeLogTools.INFO, "Please press <Enter> key to main menu");
				
				//cleanLogFile();
				
				return;
			}
			
			//cleanLogFile();

			String strVersionFlag = BeAdminCentOSTools.ahBackupdir + File.separator + AhRestoreDBTools.HM_VERSION_FLAG;

			File oFileFlag = new  File(strVersionFlag);

			if(oFileFlag.exists())
			{
				AhRestoreDBTools.logRestoreMsg("restore new framework begin");	
				long lstart = System.currentTimeMillis();				
                
				String strVersionFile = BeAdminCentOSTools.ahBackupdir + File.separator + "hivemanager.ver";
				
				File oVersionFile = new  File(strVersionFile);
				
				if(oVersionFile.exists())
				{
					String strNewStructFlag = BeAdminCentOSTools.ahBackupdir+File.separator+AhRestoreDBTools.HM_NEW_BACKUP_STRUCT_FLAG;
					
					File oStructFlag = new File(strNewStructFlag);
					if(!oStructFlag.exists())
					{
						//restore 3.*
						restore31Data();
					}
					else
					{
						//restore 3.5 & later
						restore35Data();
					}				
				}
				else
				{
					//restore 3.0 data
					restore30Data();
				}		
				
				AhRestoreNewMapTools.resetCache();
				AhRestoreNewMapTools.cleanHmDomainCache();
				
				BeUploadCfgTools.finishRunningFlag();
				
				BeLogTools.showshellLog(BeLogTools.INFO, "restore process is finished");
				BeLogTools.showshellLog(BeLogTools.INFO, "Please press <Enter> key to main menu");
				long lend = System.currentTimeMillis();
				AhRestoreDBTools.logRestoreMsg("The whole restore procress take time is: "+ (lend-lstart)/1000+" sec.");
				
				//cleanLogFile();
				fixResolveConfigure();

				return;
			}

			AhRestoreDBTools.logRestoreMsg("restore old framework begin");			
			
			//restore 2.0 data
			restore20Data();	
			
			BeUploadCfgTools.finishRunningFlag();
			
			BeLogTools.showshellLog(BeLogTools.INFO, "restore process is finished");
			BeLogTools.showshellLog(BeLogTools.INFO, "Please press <Enter> key to main menu");
			
			//cleanLogFile();
		}
		catch(Exception ex)
		{
			BeLogTools.debug(HmLogConst.M_RESTORE, ex.getMessage());
			
			BeUploadCfgTools.finishRunningFlag();
			
            BeLogTools.showshellLog(BeLogTools.INFO, "restore process is finished");
            BeLogTools.showshellLog(BeLogTools.INFO, "Please press <Enter> key to main menu");
			
			//cleanLogFile();
		}
	}
	
	private void restore35Data()
	{
		//restore the domain table
		AhRestoreDBTools.HM_XML_TABLE_PATH = BeAdminCentOSTools.ahBackupdir	+ File.separatorChar+"home/";
		RestoreAdmin.restoreDomain();
		//RestoreAdmin.restoreUpdateSoftwareInfo();
		AhRestoreDBImpl.restoreAdminOnlyForHome();	
		
		//AhRestoreDBTools.HM_XML_TABLE_PATH = BeAdminCentOSTools.ahBackupdir	+ File.separatorChar + hmDomain.getDomainName()+ File.separatorChar;
		//init default orderkey
		Iterator<Map.Entry<Long, HmDomain>> it = AhRestoreNewMapTools.getHmDomainMap().entrySet().iterator();

		while(it.hasNext())
		{
			Map.Entry<Long, HmDomain> entry = it.next();
			HmDomain hmDomain = entry.getValue();
			RestoreAdmin.initDefaultOrderKey(hmDomain);
 		}
		
		AhRestoreDBTools.isNewFrameData = true;
		
		//table partition maintain
		TablePartitionProcessor processor = new  TablePartitionProcessor();
		processor.tablePartitionManage();
		
		//restore domain data
		AhRestoreDBTools oDBRestore = new AhRestoreDBTools();
		
		oDBRestore.restoreNewFramework(false);	
	}

	/*-
	private void restoreMthreads()
	{
		try
		{
			//1.restore the domain table first
			AhRestoreDBTools.HM_XML_TABLE_PATH = BeAdminCentOSTools.ahBackupdir	+ File.separatorChar+"home/";
			RestoreAdmin.restoreDomain();
			RestoreAdmin.restoreUpdateSoftwareInfo();
			
			//2.get the domain list
            List<HmDomain> listData = QueryUtil.executeQuery(HmDomain.class, null, null);
			
			if(listData.isEmpty())
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "domain is null");
				
				return;
			}
			
			//3.add the domain to the queue
			for (HmDomain hmDomain : listData) {
				if ("global".equalsIgnoreCase(hmDomain.getDomainName())) {
					continue;
				}

				RestoreInfo oInfo = new RestoreInfo();
				oInfo.setDomainId(hmDomain.getId());
				oInfo.setPath(BeAdminCentOSTools.ahBackupdir + File.separatorChar + hmDomain.getDomainName() + File.separatorChar);

				//add queue
				DomainInfoQueue.add(oInfo);
			}
			
			//4.run muti-threads do the restore for domain
			for(int i=0; i < THREADS_NUM; ++i )
			{
				restoreDomainThread restoreThread = new restoreDomainThread();
				restoreThread.start();
			}
		}
		catch(Exception ex)
		{
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
	}*/

	private void restore31Data()
	{
	   try
	   {
		    //restore the domain table
			AhRestoreDBTools.HM_XML_TABLE_PATH = BeAdminCentOSTools.ahBackupdir	+ File.separatorChar+"home/";
			RestoreAdmin.restoreDomain();
			//RestoreAdmin.restoreUpdateSoftwareInfo();
			AhRestoreDBImpl.restoreAdminOnlyForHome();
			
			//find the domain list
			List<HmDomain> listData = QueryUtil.executeQuery(HmDomain.class, null, null);
			
			if(listData.isEmpty())
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "domain is null");
				
				return;
			}
			
			for(HmDomain hmDomain : listData)
			{
				if("global".equalsIgnoreCase(hmDomain.getDomainName()))
				{
					continue;
				}
				
				//set the static domain info
				//AhRestoreDBTools.HM_RESTORE_DOMAIN = hmDomain;
				
				AhRestoreDBTools.HM_XML_TABLE_PATH = BeAdminCentOSTools.ahBackupdir	+ File.separatorChar + hmDomain.getDomainName()+ File.separatorChar;
				
				AhRestoreDBTools.isNewFrameData = true;
				
				BeLogTools.debug(HmLogConst.M_RESTORE, "restore the domain name is: "+hmDomain.getDomainName());
				BeLogTools.showshellLog(BeLogTools.INFO, "##################################################");
				BeLogTools.showshellLog(BeLogTools.INFO, "###");
				BeLogTools.showshellLog(BeLogTools.INFO, "### restore the data for domain: "+hmDomain.getDomainName());
				BeLogTools.showshellLog(BeLogTools.INFO, "###");
				BeLogTools.showshellLog(BeLogTools.INFO, "##################################################");
				//restore domain data
				//RestoreAdmin.restoreDomainExt(hmDomain);
				RestoreAdmin.initDefaultOrderKey(hmDomain);
				
				AhRestoreDBTools oDBRestore = new AhRestoreDBTools();
				
				oDBRestore.restoreNewFramework(false);	
				
				AhRestoreNewMapTools.resetCache();
				
				AhDBTool.changeDomainName(hmDomain.getDomainName());
			}			
	   }
	   catch(Exception ex)
	   {
		   BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
	   }
	}

	private void restore20Data()
	{
		//set domain to home & dir to ./xmldbfile 		
		List<HmDomain> listData = QueryUtil.executeQuery(HmDomain.class, null, new FilterParams("domainName","home"));
		
		if(listData.isEmpty())
		{
			return;
		}

		//AhRestoreDBTools.HM_RESTORE_DOMAIN = (HmDomain) listData.get(0);
		AhRestoreDBTools.HM_XML_TABLE_PATH = BeAdminCentOSTools.ahBackupdir	+ File.separatorChar;
		
		RestoreAdmin.initDomainmap(listData.get(0),"home");
		//restore data
		AhRestoreDBTools oDBRestore = new AhRestoreDBTools();
		
		AhRestoreDBTools.isNewFrameData = false;
		
		oDBRestore.restoreOldFramework();
		
		AhRestoreNewMapTools.resetCache();
	}

	private void restore30Data()
	{
		//set domain to home & dir to ./xmldbfile
        List<HmDomain> listData = QueryUtil.executeQuery(HmDomain.class, null, new FilterParams("domainName","home"));
		
		if(listData.isEmpty())
		{
			return;
		}

		//AhRestoreDBTools.HM_RESTORE_DOMAIN = (HmDomain) listData.get(0);
		AhRestoreDBTools.HM_XML_TABLE_PATH = BeAdminCentOSTools.ahBackupdir	+ File.separatorChar;
		RestoreAdmin.initDomainmap(listData.get(0), "home");
		AhRestoreDBTools.isNewFrameData = true;
		//restore data
		AhRestoreDBTools oDBRestore = new AhRestoreDBTools();
		
		oDBRestore.restoreNewFramework(true);	
		
		AhRestoreNewMapTools.resetCache();
	}
	
	public static  void cleanLogFile()
	{
		String strCmd="sh " + BeAdminCentOSTools.ahShellRoot + "/cleanShowshellLog.sh " ;
		
		try
		{		
		   Runtime.getRuntime().exec(strCmd);
		}
		catch(Exception ex)
		{
			//add debug
			 BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
			
		}
	}
	
	/**
	 * Fix format error in resolve.conf, add this API in 3.4r3.
	 */
	private void fixResolveConfigure()
	{
		try {
			String resolveFile = "/etc/resolv.conf";
			String[] content = FileManager.getInstance().readFile(resolveFile);
			List<String> newContent = new ArrayList<String>();
			for (String line : content) {
				if (line.indexOf("nameserver") >= 0 && line.trim().length() == "nameserver".length()) {
					continue;
				}
				newContent.add(line);
			}
			
			FileManager.getInstance().writeFile(resolveFile, newContent.toArray(new String[newContent.size()]), false);
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR, e);
		}
	}
	
	public class restoreDomainThread extends Thread
	{
		public void run()
		{
			while(true)
			{
				RestoreInfo oInfo = DomainInfoQueue.poll();
				
				if(null == oInfo )
				{
					break;
				}
				
				//do the restore
				
				String[] strCmdss={"sh",BeAdminCentOSTools.ahShellRoot+ "/restoreHHMThread.sh",
			        		String.valueOf(oInfo.getDomainId()), oInfo.getPath()};			        
			        
				if(!BeOperateHMCentOSImpl.isRslt_0(strCmdss))
				{
					//add log
					 BeLogTools.restoreLog(BeLogTools.ERROR,"HHMrestore::restoreData, " +
							"run restoreHHMThread.sh have some error");
				}
			}
		}
	}

}