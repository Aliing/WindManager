package com.ah.be.admin.QueueOperation;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import com.ah.be.admin.hhmoperate.HHMoperate;
import com.ah.be.admin.hhmoperate.HHMrestore;
import com.ah.be.admin.hhmoperate.RestoreInfo;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.performance.CurrentLoadCache;
import com.ah.be.sync.VhmUserSync;
import com.ah.bo.admin.HmDomain;

public class RestoreQueueOperation {

	private final BlockingQueue<RestoreWaittingItem> qWaiting;
	
	private final List<RestoreStatusItem> lStatus;
	
	private int numThreads = 5;
	
	private long unit_time = RestoreStatusItem.UNIT_TIME_WAITTING;
	
	public RestoreQueueOperation() {
		qWaiting = new LinkedBlockingQueue<RestoreWaittingItem>(100);
		lStatus = new LinkedList<RestoreStatusItem>();
		numThreads = NmsUtil.getAdminThreadsNum();
	}
	
	public RestoreInfo restoreDomainDataInQueue(HmDomain oDomain, String strPath, String strFileName)
	{
        RestoreInfo oReturnInfo = new RestoreInfo();
		
	    //get status and charge	
        int iStatus = HHMoperate.getDomainStatus(oDomain.getId());
		
		if(HmDomain.DOMAIN_BACKUP_STATUS == iStatus 
				|| HmDomain.DOMAIN_RESTORE_STATUS == iStatus
				|| HmDomain.DOMAIN_UPDATE_STATUS == iStatus)
		{
			oReturnInfo.setResult(false);
			String strErrMsg;
			
			switch(iStatus)
			{			
			    case HmDomain.DOMAIN_BACKUP_STATUS:
			    	strErrMsg = "Another administrator is currently backing up the " +
			    	NmsUtil.getOEMCustomer().getNmsName() + " software. " +
			    			"Please try again later if necessary.";
			    	oReturnInfo.setErrorMsg(strErrMsg);
			        break;
			    case HmDomain.DOMAIN_RESTORE_STATUS:
			    	strErrMsg = "Another administrator is currently restoring the " +
			    	NmsUtil.getOEMCustomer().getNmsName() + " software. " +
	    			"Please try again later if necessary.";
	    	        oReturnInfo.setErrorMsg(strErrMsg);
			        break;
			    case HmDomain.DOMAIN_UPDATE_STATUS:
			    	strErrMsg = "Another administrator is currently updating the " +
			    	NmsUtil.getOEMCustomer().getNmsName() + " software. " +
	    			"Please try again later if necessary.";
	    	        oReturnInfo.setErrorMsg(strErrMsg);
			    	break;
			}
			
			return oReturnInfo;
		}
		
		//set restore status
		if(null == HHMoperate.updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_RESTORE_STATUS))
		{
			oReturnInfo.setResult(false);
			String strErrmsg = "An error occurred while changing the status of VHM";
			oReturnInfo.setErrorMsg(strErrmsg);
			HHMoperate.updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_DEFAULT_STATUS);	
			return oReturnInfo;
		}
		
		//add to queue
		addQueueItem(oDomain, strPath, strFileName);
		
		oReturnInfo.setResult(true);
		return oReturnInfo;
	}
	
	//restore running function deal with restore
	public void dealrestore()
	{
		for(int i=0; i<numThreads; i++)
		{
			RestoreThreads dealwithThread = new RestoreThreads();
			dealwithThread.setName("dealwith-restore-queue"+i);
			dealwithThread.start();
		}
	}
	
	//shut down the while
	public void shutdown()
	{
		//add shutdown item
		RestoreWaittingItem oItem = new  RestoreWaittingItem();
		oItem.setShutDownFlag(true);

		qWaiting.clear();
		for(int i=0; i< numThreads; i++)
		{
			qWaiting.offer(oItem);
		}		
	}
	
	public RestoreStatusItem getRestoreStatus(long lId)
	{
		RestoreStatusItem oItem = null;
		
		synchronized(lStatus)
		{
			for(Iterator<RestoreStatusItem> it=lStatus.iterator(); it.hasNext();)
			{
				oItem = it.next();
				
				if(oItem.getDomainId() == lId)
				{
					if(oItem.getStatus() == RestoreStatusItem.RESTORE_FINISHED)
					{
						//delete the item from list
						it.remove();
						CurrentLoadCache.getInstance().decreaseNumberOfRestoreRequest();
					}
					
					break;
				}
				
				oItem = null;
			}
		}
		
		return oItem;
	}
	
	public boolean cancelRestoreInQueue(HmDomain oDomain)
	{
		boolean bReturn = false;
		
		RestoreWaittingItem oWaittingItem = new RestoreWaittingItem();
		oWaittingItem.setDomain(oDomain);
		
		synchronized(lStatus)
		{
			for(Iterator<RestoreStatusItem> it=lStatus.iterator(); it.hasNext();)
			{
				RestoreStatusItem oItem = it.next();
				
				if(oItem.getDomainId() == oDomain.getId() && oItem.getStatus() == RestoreStatusItem.RESTORE_WAITTING)
				{
					it.remove();
					
					bReturn = qWaiting.remove(oWaittingItem);
					
					CurrentLoadCache.getInstance().decreaseNumberOfRestoreRequest();
					break;
				}
			}
		}
		
		return bReturn;
	}
	
	private  boolean addQueueItem(HmDomain oDomain, String strPath, String strFileName)
	{
		RestoreStatusItem oStatusItem = new RestoreStatusItem();
		oStatusItem.setDomainId(oDomain.getId());
		oStatusItem.setStatus(RestoreStatusItem.RESTORE_WAITTING);
		oStatusItem.setBeforeCount(getQueueItemSize());
		oStatusItem.setWaittingTime(((oStatusItem.getBeforeCount()/(numThreads+1))+2)*unit_time);
		
		RestoreWaittingItem oWaittingItem = new RestoreWaittingItem();
		oWaittingItem.setDomain(oDomain);
		oWaittingItem.setFileName(strFileName);
		oWaittingItem.setFilePath(strPath);
		oWaittingItem.setShutDownFlag(false);
		
		synchronized(lStatus)
		{
			lStatus.add(oStatusItem);
			
			if(!qWaiting.offer(oWaittingItem))
	    	{
	    		//add some log
	    		 DebugUtil.adminDebugError("RestoreQueueOperation.addItem failed,the itme domain is: "+oDomain.getDomainName());
	    		 
	    		 lStatus.remove(oStatusItem);
	    		return false;
	    	}
		}
		
		CurrentLoadCache.getInstance().increaseNumberOfRestoreRequest();
		
		return true;
	}
	
	private int getQueueItemSize()
	{
		return qWaiting.size();
	}
	
	//change the status for status list
	private  void changeItemValue(long lDomainid,int n)
	{
	    synchronized(lStatus)
		{
			for (RestoreStatusItem oItem : lStatus) {
				if (oItem.getDomainId() == lDomainid) {
					oItem.setStatus(RestoreStatusItem.RESTORE_RUNNING);
					oItem.setBeforeCount(0);
					oItem.setWaittingTime((n + 1) * RestoreStatusItem.UNIT_TIME_WAITTING);
				} else {
					if (oItem.getStatus() == RestoreStatusItem.RESTORE_WAITTING) {
						if (oItem.getBeforeCount() > 0) {
							oItem.setBeforeCount(oItem.getBeforeCount() - 1);
							oItem.setWaittingTime(((oItem.getBeforeCount() / (numThreads + 1)) + 2) * unit_time);
						}
					}
				}
			}
		}
	}	
	
	private void changeItemToFinished(long lDomainid, RestoreInfo oInfo)
	{
		synchronized(lStatus)
		{
			for (RestoreStatusItem oItem : lStatus) {
				if (oItem.getDomainId() == lDomainid) {
					oItem.setStatus(RestoreStatusItem.RESTORE_FINISHED);
					oItem.setBeforeCount(0);
					oItem.setWaittingTime(0);
					oItem.setRestoreInfo(oInfo);
				}
			}
		}
	}
	
	class RestoreThreads extends Thread
	{
		@Override
		public void run()
		{
			while(true)
			{
				try
				{
					RestoreWaittingItem oItem = qWaiting.take();
					
					if(oItem.getShutDownFlag())
					{
						//add finished log
						DebugUtil.adminDebugInfo("Thread : "+ getName()+" is finished");
						break;
					}
					
					CurrentLoadCache.getInstance().increaseNumberOfRestoreRunning();
					
					File oFile = new File(oItem.getFilePath()+"/"+oItem.getFileName());
					
					int icount = (int)oFile.length()/(3*1024*1024);
					
					//change status value
					changeItemValue(oItem.getDomain().getId(),icount);
					
					long lbegin = System.currentTimeMillis();
					
					RestoreInfo oReturnInfo = new RestoreInfo();
					//do restore
					String retMessage = HHMrestore.restoreData(oItem.getFilePath(), oItem.getFileName(), oItem.getDomain());
					if(!retMessage.equals(""))
					{
						oReturnInfo.setResult(false);
//						String strErrmsg = "An error occurred while restore db data";
						oReturnInfo.setErrorMsg(retMessage);
					}
					else
					{
						oReturnInfo.setResult(true);
						
						try
						{
							if(null != oItem.getDomain())
							{
								HHMoperate.changeAfterRestoreDomain(oItem.getDomain().getId(),oItem.getDomain().getDomainName());
							}
						}
						catch(Exception ex)
						{
							DebugUtil.adminDebugError("changeAfterRestoreDomain ", ex);
							oReturnInfo.setResult(false);
							oReturnInfo.setErrorMsg(ex.getMessage());
						}
					}				
					
                    long lend = System.currentTimeMillis();
					
					long ltmp = (lend-lbegin)/1000;
					
					unit_time = (ltmp > RestoreStatusItem.UNIT_TIME_WAITTING ) ? ltmp : RestoreStatusItem.UNIT_TIME_WAITTING;
					
					//set default  status
					HHMoperate.updateDomainStatus(oItem.getDomain().getId(),HmDomain.DOMAIN_DEFAULT_STATUS);	
					
				    //changed finished status to item
					changeItemToFinished(oItem.getDomain().getId(),oReturnInfo);
					
					CurrentLoadCache.getInstance().decreaseNumberOfRestoreRunning();
					
					if (NmsUtil.isHostedHMApplication()) {
						VhmUserSync.syncVhmUserAfterRestoreVhm(oItem.getDomain().getDomainName());
					}
				}
				catch(Exception ex)
				{
					//add error log
					DebugUtil.adminDebugError("RestoreThreads failed ", ex);
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		File otest = new File("/root/yum-updatesd-0.9-2.el5.noarch.rpm");
		
		System.out.println(otest.length()/(1024*1024));
	}

}