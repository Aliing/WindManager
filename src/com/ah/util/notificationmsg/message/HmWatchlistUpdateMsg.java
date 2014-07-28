package com.ah.util.notificationmsg.message;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.ApplicationProfile;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.ApplicationUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;

public class HmWatchlistUpdateMsg extends AhNotificationMessage {
	
	//private static final String NOTIFICATION_MSG = "6.1r3 devices supports seven applications within a watchlist. please limit the number of applications to seven.";
	
	@Override
	public int initPriority() {
	    return UPDATE_WATCHLIST_PRIORITY;
	}
	
	@Override
    public boolean isDisplayFlagOn(HmUser userContext) {
        // get the flag in database/session to check if it's necessary to display this warning message.
        boolean flag = false;
        if (null != userContext && null != userContext.getDomain()) {
            HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
            if(hmDomain.getDomainName().equals(userContext.getDomain().getDomainName())) {
               flag = true;
            }
        }
        return flag;
    }

	@Override
    public boolean isNeedBuild(HmUser userContext) {
        boolean flag = false;
        flag = isNeedUpdateWatchlist(userContext);
        return flag;
    }

    private boolean isNeedUpdateWatchlist(HmUser userContext) {
    	boolean flag = false;
    	if (userContext == null || userContext.getDomain() == null) {
    		return false;
    	}
        HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
        if(!hmDomain.getDomainName().equals(userContext.getDomain().getDomainName())) {
        	return false;
        }
        int appSize = 0;
    	ApplicationProfile profile = QueryUtil.findBoByAttribute(ApplicationProfile.class, "owner.id", userContext.getDomain().getId(), new ConfigLazyQueryBo());
    	//List<ApplicationProfile> list = QueryUtil.executeQuery(ApplicationProfile.class, null, new FilterParams("owner.id = :s1", new Object[] {userContext.getDomain().getId()}));
    	if (profile != null) {
    		appSize = profile.getApplicationList().size();
    	}
    	if (appSize <= ApplicationUtil.getWatchlistLimitation()) {
    		return false;
    	}
    	
        try {
        	HMServicesSettings serviceSetting = QueryUtil.findBoByAttribute(
            		HMServicesSettings.class, "owner.id", userContext.getDomain().getId());
            if (serviceSetting == null || !serviceSetting.isNotifyUpdateWatchList()) {
            	List<SimpleHiveAp> apList = CacheMgmt.getInstance().getAllApList(userContext.getDomain().getId());
            	if (apList != null) {
            		for (SimpleHiveAp hiveAp : apList) {
            			if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.2.0") >= 0){ //is exist 6.1r3 or later devices
            				flag = true;
            				break;
            			}
            		}
            	}
            	
            	
            }
        } catch (Exception e) {
            log.error("[HmWatchlistUpdateMsg] Error when get the msg display flag", e);
        }
   
        return flag;
    }

    @Override
    public void build(HmUser userContext) {
        this.contents = MgrUtil.getUserMessage("notification.message.watchlist.update");
    	//this.contents = NOTIFICATION_MSG;
        // buttons
        List<AhNotificationMsgButton> buttons = generateButtons();
        this.actionButtons.addAll(buttons);
        // close icon
        if(null != userContext && userContext.getId() > 0L) {
            this.closeButton = new AhNotificationMsgButton(
                    MgrUtil.getUserMessage("notification.message.osversions.close.tips"),
                    "closeNotifyWatchlistUpdate()");
        }
    }

    @Override
    public boolean refresh(HmUser userContext, Object action) {
        return init(userContext);
    }

	private List<AhNotificationMsgButton> generateButtons() {
		// get the buttons description by user context. For VA user, it should not display the 'Donot Show' buttons.
		List<AhNotificationMsgButton> buttons = new ArrayList<AhNotificationMsgButton>();
		buttons.add(new AhNotificationMsgButton("", "watchlistUpdate()"));
		return buttons;
	}
	
    @Override
    public boolean disableDisplay(HmUser userContext) {
        boolean flag = false;
        if (null != userContext && null != userContext.getDomain()) {
            try {
            	HMServicesSettings serviceSetting = QueryUtil.findBoByAttribute(
                		HMServicesSettings.class, "owner.id", userContext.getDomain().getId());
                if (serviceSetting == null) {
                	serviceSetting = new HMServicesSettings();
                	serviceSetting.setOwner(userContext.getDomain());
                	serviceSetting.setNotifyUpdateWatchList(true);
                	QueryUtil.createBo(serviceSetting);
                }
                else {
                	serviceSetting.setNotifyUpdateWatchList(true);
                	QueryUtil.updateBo(serviceSetting);
                }
                flag = true;
            } catch (Exception e) {
                log.error("disableDisplay()", "Error when get the msg display status", e);
            }
        }
        return flag;
    }
    
    @Override
    public String getMsgStyle() {
        return "padding-left: 45px;";
    }
}
