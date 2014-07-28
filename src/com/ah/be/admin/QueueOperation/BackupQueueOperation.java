package com.ah.be.admin.QueueOperation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Iterator;

import com.ah.be.admin.hhmoperate.BackupInfo;
import com.ah.be.admin.hhmoperate.HHMbackup;
import com.ah.be.admin.hhmoperate.HHMoperate;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.performance.CurrentLoadCache;
import com.ah.bo.admin.HmDomain;

public class BackupQueueOperation {

	private final BlockingQueue<BackupWaittingItem> qWaiting;
	
	private final List<BackupStatusItem> lStatus;
	
	private int numThreads = 5;
	
	private long unit_time = BackupStatusItem.UNIT_TIME_WAITTING;
	
	public BackupQueueOperation() {
		qWaiting = new LinkedBlockingQueue<BackupWaittingItem>(100);
		lStatus = new LinkedList<BackupStatusItem>();
	    numThreads = NmsUtil.getAdminThreadsNum();
	}
	
	//backup domain [put it to queue only] for GUI
	public BackupInfo backupDomainDataInQueue(HmDomain oDomain, int iContent)
	{
        BackupInfo oReturnInfo = new BackupInfo(); 
		
		//get status & charge status
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
		
		//set backup status
		if(null == HHMoperate.updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_BACKUP_STATUS))
		{
			oReturnInfo.setResult(false);
			String strErrmsg = "An error occurred while changing the status of VHM";
			oReturnInfo.setErrorMsg(strErrmsg);
			return oReturnInfo;
		}
		
		//add to queue		
		addQueueItem(oDomain,iContent);		
		
		oReturnInfo.setResult(true);
		return oReturnInfo;
	}
	
    //backend running function deal with backup
	public void dealwithBackup()
	{
		for(int i=0; i<numThreads; i++)
		{
			BackupThreads dealwithThread = new BackupThreads();
			dealwithThread.setName("dealwith-backup-queue"+i);
			dealwithThread.start();
		}
	}
	
	//shut down the while
	public void shutdown()
	{
		//add shutdown item
		BackupWaittingItem oItem = new  BackupWaittingItem();
		oItem.setShutDownFlag(true);
		
		qWaiting.clear();
		for(int i=0; i< numThreads; i++)
		{
			qWaiting.offer(oItem);
		}		
	}
	
	//check the backup status info
	public BackupStatusItem getBackupStatus(long lId)
	{
		BackupStatusItem oItem = null;
		
		synchronized(lStatus)
		{
			for(Iterator<BackupStatusItem> it=lStatus.iterator(); it.hasNext();)
			{
				oItem = it.next();
				
				if(oItem.getDomainId() == lId)
				{
					if(oItem.getStatus() == BackupStatusItem.BACKUP_FINISHED)
					{
						//delete the item from list
						it.remove();
						CurrentLoadCache.getInstance().decreaseNumberOfBackupRequest();
					}
					
					break;
				}
				
				oItem = null;
			}
		}
		
		return oItem;
	}
	
	//cancel this operation
	public boolean cancelBackupInQueue(HmDomain oDomain)
	{
		boolean bReturn = false;
		
		BackupWaittingItem oWaitItem = new BackupWaittingItem();
		oWaitItem.setDomain(oDomain);
		
		synchronized(lStatus)
		{
			for(Iterator<BackupStatusItem> it=lStatus.iterator(); it.hasNext();)
			{
				BackupStatusItem oItem = it.next();
				
				if(oItem.getDomainId() == oDomain.getId() && oItem.getStatus() == BackupStatusItem.BACKUP_WAITTING)
				{
					it.remove();
					
					bReturn = qWaiting.remove(oWaitItem);
					
					CurrentLoadCache.getInstance().decreaseNumberOfBackupRequest();
					
					break;
				}
			}
		}
		
		return bReturn;
	}
	
	
	//change the backup status info	
	private  void changeItemValue(long lDomainid)
	{
	    synchronized(lStatus)
		{
			for (BackupStatusItem oItem : lStatus) {
				if (oItem.getDomainId() == lDomainid) {
					oItem.setStatus(BackupStatusItem.BACKUP_RUNNING);
					oItem.setBeforeCount(0);
					oItem.setWaittingTime(unit_time);
				} else {
					if (oItem.getStatus() == BackupStatusItem.BACKUP_WAITTING) {
						if (oItem.getBeforeCount() > 0) {
							oItem.setBeforeCount(oItem.getBeforeCount() - 1);
							oItem.setWaittingTime(((oItem.getBeforeCount() / (numThreads + 1)) + 2) * unit_time);
						}
					}
				}
			}
		}
	}	
	
	private void changeItemToFinished(long lDomainid, BackupInfo oInfo)
	{
		synchronized(lStatus)
		{
			for (BackupStatusItem oItem : lStatus) {
				if (oItem.getDomainId() == lDomainid) {
					oItem.setStatus(BackupStatusItem.BACKUP_FINISHED);
					oItem.setBeforeCount(0);
					oItem.setWaittingTime(0);
					oItem.setBackupInfo(oInfo);
				}
			}
		}
	}	

	private  boolean addQueueItem(HmDomain oDomain, int iContent)
	{
		BackupStatusItem oItem = new BackupStatusItem();
		oItem.setDomainId(oDomain.getId());
		oItem.setStatus(BackupStatusItem.BACKUP_WAITTING);
		oItem.setBeforeCount(getQueueItemSize());
		oItem.setWaittingTime(((oItem.getBeforeCount()/(numThreads+1))+2)*unit_time);
		
		BackupWaittingItem oWaitItem = new BackupWaittingItem();
		oWaitItem.setDomain(oDomain);
		oWaitItem.setContent(iContent);
		oWaitItem.setShutDownFlag(false);
		
		//add status list
		synchronized(lStatus)
		{
			lStatus.add(oItem);

	    	if(!qWaiting.offer(oWaitItem))
	    	{
	    		//add some log
	    		 DebugUtil.adminDebugError("BackupQueueOperation.addItem failed,the itme domain is: "+oDomain.getDomainName());
	    		 
	    		 lStatus.remove(oItem);
	    		return false;
	    	}
		}	
		
		CurrentLoadCache.getInstance().increaseNumberOfBackupRequest();
    	
    	return true;
	}	
	
	
	private int getQueueItemSize()
	{
		return qWaiting.size();
	}
	
	/*
	 * backup operation threads
	 */
	class BackupThreads extends Thread
	{
		@Override
		public void run()
		{
			while(true)
			{
				try
				{
					BackupWaittingItem oItem = qWaiting.take();
					
					if(oItem.getShutDownFlag())
					{
						//add some log
						DebugUtil.adminDebugInfo("Thread : "+ getName()+" is finished");
						break;
					}
					
					CurrentLoadCache.getInstance().increaseNumberOfBackupRunning();
					
					//change status
					changeItemValue(oItem.getDomain().getId());
					
					long lbegin = System.currentTimeMillis();
					
					//do the backup 
					BackupInfo oReturnInfo = HHMbackup.backupHHM(oItem.getDomain(), oItem.getContent(), false);	
					
					long lend = System.currentTimeMillis();
					
					long ltmp = (lend-lbegin)/1000;
					
					unit_time = (ltmp > BackupStatusItem.UNIT_TIME_WAITTING ) ? ltmp : BackupStatusItem.UNIT_TIME_WAITTING;
									
					//change status
					HHMoperate.updateDomainStatus(oItem.getDomain().getId(),HmDomain.DOMAIN_DEFAULT_STATUS);
					
					//change finished status to item
					changeItemToFinished(oItem.getDomain().getId(),oReturnInfo);
					
					CurrentLoadCache.getInstance().decreaseNumberOfBackupRunning();
				}
				catch(Exception ex)
				{
					//add log
					DebugUtil.adminDebugError("BackupThreads failed "+ ex.getMessage());
				}
			}
		}
	}	
	
	public static void main(String[] args)
	{
		System.out.println(4/5);
	}

}