package com.ah.util.notificationmsg.message;

import java.util.List;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.tca.TCAUtils;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;

/**
 * 
 * @author xxu
 *
 */
public class HmTCADiskUsageWarningMSG extends AhNotificationMessage{

	public final static Integer DISK_USAGE_AHALARM_CODE=10101;
	
	
	@Override
	public int initPriority() {
		return TCAALARM_WARNING_MSG_PRIORITY;
	}

	@Override
	public boolean isDisplayFlagOn(HmUser userContext) {
		if(HmDomain.HOME_DOMAIN.equals(userContext.getOwner().getDomainName())){
			return true;
		}
		return false;
	}

	@Override
	public boolean isNeedBuild(HmUser userContext) {
		boolean flag=false;
		flag=isDiskFull();
		return flag;
	}
	
	private String getHighThreshold(){
		if(highThreshold==null){
		if(TCAUtils.getDiskFullAlarm()!=null){
			return TCAUtils.getDiskFullAlarm().getHighThresholdPercentStr();
		}else{
			return "90%";
		}
		}
		else {
			return highThreshold;
		}
		
	}
	
	private String highThreshold;
	
	public void setHighThreshold(String highThreshold) {
		this.highThreshold = highThreshold;
	}
	
	private String praseMSG(String msg){
		int index=msg.lastIndexOf("%");
		String result=msg.substring(index-3, index+1);
		//in case of disk usage is 100%.
		if(!result.startsWith("1"))
		{
			result=msg.substring(index-2, index+1);
		}
		if(result.indexOf("%")>0 ){
			return result;
		}else{
			return null;
		}
	}
	
	private int getBiggestPercent(){
		int result=-1;
		FilterParams filterParams = new FilterParams("code", DISK_USAGE_AHALARM_CODE);
		List<AhAlarm> alarmLists = QueryUtil.executeQuery(AhAlarm.class, null, filterParams, 10);
		if(alarmLists==null || alarmLists.isEmpty()){
			return -1;
		}

		for(AhAlarm alarm:alarmLists){
			if(alarm!=null && alarm.getSeverity()==AhAlarm.AH_SEVERITY_CRITICAL){
				String range=praseMSG(alarm.getTrapDescString());
				int bigRange=Integer.parseInt(range.substring(0, range.length()-1));
				if(bigRange>result){
					result=bigRange;
				}
			}
		}
		return result;
	}

	private boolean isDiskFull(){
		boolean result=false;
		FilterParams filterParams = new FilterParams("code", DISK_USAGE_AHALARM_CODE);
		List<AhAlarm> alarmLists = QueryUtil.executeQuery(AhAlarm.class, null, filterParams, 10);
		if(alarmLists==null || alarmLists.isEmpty()){
			return false;
		}

		for(AhAlarm alarm:alarmLists){
			if(alarm!=null && alarm.getSeverity()==AhAlarm.AH_SEVERITY_CRITICAL){
				//setHighThreshold(praseMSG(alarm.getTrapDescString()));
				result=true;
			}
		}
//		AhAlarm alarm = alarmLists.get(0);
//		if(alarm!=null && alarm.getSeverity()==AhAlarm.AH_SEVERITY_CRITICAL){
//			setHighThreshold(praseMSG(alarm.getTrapDescString()));
//			return true;
//		}
		if(result){
			int high=getBiggestPercent(); 
			if(high>=0 && high<=100){
				setHighThreshold(high+"%");
			}
		}
		return result;
	}

	@Override
	public void build(HmUser userContext) {
		 this.contents = MgrUtil.getUserMessage("notification.message.diskfull.contents",getHighThreshold());
	        this.actionButtons.add(new AhNotificationMsgButton("Enter Now", "gotoOnLineHelpDiskFullURL()"));

	        // close icon
	        if(null != userContext && userContext.getId() > 0L) {
	            this.closeButton = new AhNotificationMsgButton("Hide this message.", "hideTCAMessageInSession()");
	        }
		
	}
	
	@Override
	 public String getBtnNameValue() {
		return "Show Me How";
	}
	
	@Override
	public String getGoBtnWidth() {
		return "110px";
	}

/*-	@Override
    public String getMsgStyle() {
        return "max-width: 475px;";
    }
*/	
	@Override
	public String getItemStyle() {
		return "color: #FE1000; ";
	}
	
	@Override
	public boolean refresh(HmUser userContext, Object action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disableDisplay(HmUser userContext) {
		// TODO Auto-generated method stub
		return true;
	}

}
