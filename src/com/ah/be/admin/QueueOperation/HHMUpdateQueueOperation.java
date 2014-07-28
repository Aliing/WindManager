package com.ah.be.admin.QueueOperation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.admin.hhmoperate.HHMoperate;
import com.ah.be.admin.hhmoperate.HHMupdate;
import com.ah.be.admin.hhmoperate.UpdateInfo;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.performance.CurrentLoadCache;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;
import com.ah.bo.mgmt.QueryUtil;

public class HHMUpdateQueueOperation {
	
	private final BlockingQueue<HHMUpdateWaittingItem> qWaiting;

	private final List<HHMUpdateStatusItem> lStatus;
	
	private int numThreads   = 5;
	
	private long unit_time = HHMUpdateStatusItem.UNIT_TIME_WAITTING;
	
	public HHMUpdateQueueOperation() {
		qWaiting = new LinkedBlockingQueue<HHMUpdateWaittingItem>(100);
		lStatus = new LinkedList<HHMUpdateStatusItem>();
		numThreads = NmsUtil.getAdminThreadsNum();
	}
	
	//hhm update [put it to queue only] for GUI
	public UpdateInfo HHMUpdateInQueue(HmDomain oDomain, int iContent,HhmUpgradeVersionInfo oInfo,int iUpdateType)
	{
		 UpdateInfo oReturnInfo = new UpdateInfo();
			
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
				    	strErrMsg = "Another administrator is currently backing up the HiveManager software. " +
				    			"Please try again later if necessary.";
				    	oReturnInfo.setErrorMsg(strErrMsg);
				        break;
				    case HmDomain.DOMAIN_RESTORE_STATUS:
				    	strErrMsg = "Another administrator is currently restoring the HiveManager software. " +
		    			"Please try again later if necessary.";
		    	        oReturnInfo.setErrorMsg(strErrMsg);
				        break;
				    case HmDomain.DOMAIN_UPDATE_STATUS:
				    	strErrMsg = "Another administrator is currently updating the HiveManager software. " +
		    			"Please try again later if necessary.";
		    	        oReturnInfo.setErrorMsg(strErrMsg);
				    	break;
				}
				
				return oReturnInfo;
			}
			
			if(null == HHMoperate.updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_UPDATE_STATUS))
			{
				oReturnInfo.setResult(false);
				String strErrmsg = "An error occurred while changing the status of VHM";
				oReturnInfo.setErrorMsg(strErrmsg);
				HHMoperate.updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_DEFAULT_STATUS);	
				return oReturnInfo;
			}
			
			//add to the queue
			addQueueItem(oDomain,iContent,oInfo,iUpdateType);
			
			oReturnInfo.setResult(true);
			return oReturnInfo;
	}
	
	//deal update
	public void dealwithUpdate()
	{
		for(int i=0; i<numThreads; i++)
		{
			updateThreads  dealwithThread = new updateThreads();
			dealwithThread.setName("dealwith-update-queue"+i);
			dealwithThread.start();
		}
	}
	
	//shutdown the update thread
	public void shutdown()
	{
		HHMUpdateWaittingItem oItem = new HHMUpdateWaittingItem();
		oItem.setShutDownFlag(true);

		qWaiting.clear();
		for(int i=0; i< numThreads; i++)
		{
			qWaiting.offer(oItem);
		}		
	}
	
	//get the status for update
	public HHMUpdateStatusItem getUpdateStatus(long lId)
	{
		HHMUpdateStatusItem oItem = null;
		
		synchronized(lStatus)
		{
			for(Iterator<HHMUpdateStatusItem> it=lStatus.iterator();it.hasNext();)
			{
				oItem = it.next();
				
				if(oItem.getDomainId() == lId)
				{
					if(oItem.getStatus() == HHMUpdateStatusItem.UPDATE_FINISHED)
					{
						it.remove();
						CurrentLoadCache.getInstance().decreaseNumberOfUpgradeRequest();
					}
					
					break;
				}
				
				oItem = null;
			}
		}
		
		return oItem;
	}
	
	//public cancel the update
	public boolean cancelHHMupdateInQueue(HmDomain oDomain)
	{
		boolean bReturn = false;
		HHMUpdateWaittingItem oWaitItem = new HHMUpdateWaittingItem();
		oWaitItem.setDomain(oDomain);
		
		synchronized(lStatus)
		{
			for(Iterator<HHMUpdateStatusItem> it=lStatus.iterator();it.hasNext();)
			{
				HHMUpdateStatusItem oItem = it.next();
				
				if(oItem.getDomainId() == oDomain.getId() && oItem.getStatus() == HHMUpdateStatusItem.UPDATE_WAITTING)
				{
					it.remove();
					
					bReturn = qWaiting.remove(oWaitItem);
					
					CurrentLoadCache.getInstance().decreaseNumberOfUpgradeRequest();
				}
			}
		}
		
		return bReturn;
	}
	
	private boolean addQueueItem(HmDomain oDomain, int  iContent, HhmUpgradeVersionInfo oInfo, int iUpdateType)
	{
		HHMUpdateStatusItem oItem  = new HHMUpdateStatusItem();
		oItem.setDomainId(oDomain.getId());
		oItem.setStatus(HHMUpdateStatusItem.UPDATE_WAITTING);
		oItem.setUpdateStatus(HHMUpdateStatusItem.Update_Status_No_operation);
		oItem.setBeforeCount(getQueueItemSize());
		oItem.setWaittingTime(((oItem.getBeforeCount()/(numThreads+1))+2)*unit_time);
		
		HHMUpdateWaittingItem oWaitItem = new HHMUpdateWaittingItem();
		oWaitItem.setDomain(oDomain);
		oWaitItem.setContent(iContent);
		oWaitItem.setUpgradeVersionInfo(oInfo);
		oWaitItem.setUpdateType(iUpdateType);
		oWaitItem.setShutDownFlag(false);
		
		synchronized(lStatus)
		{
			lStatus.add(oItem);

	    	if(!qWaiting.offer(oWaitItem))
	    	{
	    		//add some log
	    		 DebugUtil.adminDebugError("HHMUpdateQueueOperation.addQueueItem failed,the itme domain is: "+oDomain.getDomainName());
	    		 
	    		 lStatus.remove(oItem);
	    		return false;
	    	}
		}	
		
		CurrentLoadCache.getInstance().increaseNumberOfUpgradeRequest();
    	
    	return true;
	}
	
	private int getQueueItemSize()
	{
		return qWaiting.size();
	}
	
	private HHMUpdateStatusItem changeItemValue(long lDomainid)
	{
		HHMUpdateStatusItem oReturnItem = null;
		
		 synchronized(lStatus)
		 {
			 for (HHMUpdateStatusItem oItem : lStatus) {
				 if (oItem.getDomainId() == lDomainid) {
					 oItem.setStatus(HHMUpdateStatusItem.UPDATE_RUNNING);
					 oItem.setBeforeCount(0);
					 oItem.setWaittingTime(unit_time);
					 oReturnItem = oItem;
				 } else {
					 if (oItem.getStatus() == HHMUpdateStatusItem.UPDATE_WAITTING) {
						 if (oItem.getBeforeCount() > 0) {
							 oItem.setBeforeCount(oItem.getBeforeCount() - 1);
							 oItem.setWaittingTime(((oItem.getBeforeCount() / (numThreads + 1)) + 2) * unit_time);
						 }
					 }
				 }
			 }
		 }
		 
		 return oReturnItem;
	}
	
	private void changeItemToFinished(long lDomainid,UpdateInfo oReturnInfo)
	{
		synchronized(lStatus)
		{
			for (HHMUpdateStatusItem oItem : lStatus) {
				if (oItem.getDomainId() == lDomainid) {
					oItem.setStatus(HHMUpdateStatusItem.UPDATE_FINISHED);
					oItem.setBeforeCount(0);
					oItem.setWaittingTime(0);
					oItem.setReturnInfo(oReturnInfo);
				}
			}
		}
	}
	
	/**
	 * update operation threads
	 */
	class updateThreads extends Thread
	{
		@Override
		public void run()
		{
			while(true)
			{
				try
				{
					HHMUpdateWaittingItem oItem = qWaiting.take();
					UpdateInfo oReturnInfo;
					
					if(oItem.getShutDownFlag())
					{
						//add some log
						DebugUtil.adminDebugInfo("Thread : "+ getName()+" is finished");
						break;
					}
					
					CurrentLoadCache.getInstance().increaseNumberOfUpgradeRunning();
					
					//change value
					HHMUpdateStatusItem oStatus = changeItemValue(oItem.getDomain().getId());
					
					long lbegin = System.currentTimeMillis();
					
					if(null == oStatus)
					{
						//set error
						oReturnInfo = new UpdateInfo();
						
						oReturnInfo.setResult(false);
						oReturnInfo.setErrorMsg("Could not change Status");
					}
					else
					{
						//do update
						if(oItem.getUpdateType() == HHMUpdateWaittingItem.FLAG_UPDATE_HHM2HHM)
						{
							oReturnInfo = HHMupdate.updateHHM(oItem.getDomain(), oItem.getContent(), oItem.getUpgradeVersionInfo(),oStatus);	
						}
						else
						{
							try {
								List<HmUser> allUser = QueryUtil.executeQuery(HmUser.class, null, null, oItem.getDomain().getId());
								HmUserGroup configGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName", HmUserGroup.CONFIG,
										oItem.getDomain().getId());
								
								for (HmUser singleUser : allUser) {
									singleUser.setUserGroup(configGroup);
								}
								
								QueryUtil.bulkUpdateBos(allUser);
								
								oReturnInfo = HHMupdate.updateHHM(oItem.getDomain(), oItem.getContent(), oItem.getUpgradeVersionInfo(),oStatus);
								
								allUser = QueryUtil.executeQuery(HmUser.class, null, null, oItem.getDomain().getId());
								HmUserGroup plannerGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName", HmUserGroup.PLANNING,
										oItem.getDomain().getId());
								
								for (HmUser singleUser : allUser) {
									singleUser.setUserGroup(plannerGroup);
								}
								
								QueryUtil.bulkUpdateBos(allUser);
								
								//HHMoperate.updateConfirm(oItem.getDomain());
								HHMoperate.UpdateConfirm_2(oItem.getDomain(),HHMoperate.HHM_UPDATE);
								
							} catch (Exception e) {
								DebugUtil.commonDebugError("updatePlanner2HM", e);
								oReturnInfo = new UpdateInfo();
								oReturnInfo.setResult(false);
								oReturnInfo.setErrorMsg("Cannot upgrade planner account to normal HiveManager account.");
							}
						}
					}	
					
                    long lend = System.currentTimeMillis();
					
					long ltmp = (lend-lbegin)/1000;
					
					unit_time = (ltmp > HHMUpdateStatusItem.UNIT_TIME_WAITTING ) ? ltmp : HHMUpdateStatusItem.UNIT_TIME_WAITTING;
					
					//update domain status
					HHMoperate.updateDomainStatus(oItem.getDomain().getId(),HmDomain.DOMAIN_DEFAULT_STATUS);	
					
					//change finished
					changeItemToFinished(oItem.getDomain().getId(),oReturnInfo);
					
					CurrentLoadCache.getInstance().decreaseNumberOfUpgradeRunning();
				}
				catch(Exception ex)
				{
					//add log
				}
			}
		}		
	}
	
}