package com.ah.util.notificationmsg.message;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.ApplicationProfile;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.ApplicationUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;

public class HmWatchlistCleanMsg extends AhNotificationMessage {
	
	//private static final String NOTIFICATION_MSG = "The meaning of watchlist has been changed, do you want to clear and reset it?";

	@Override
	public int initPriority() {
	    return CLEAN_WATCHLIST_PRIORITY;
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
        flag = isNeedClearWatchlist(userContext);
        return flag;
    }

    private boolean isNeedClearWatchlist(HmUser userContext) {
    	boolean flag = false;
    	if (userContext == null || userContext.getDomain() == null) {
    		return false;
    	}
        HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
        if(!hmDomain.getDomainName().equals(userContext.getDomain().getDomainName())) {
        	return false;
        }
        try {
        	HMServicesSettings serviceSetting = QueryUtil.findBoByAttribute(
            		HMServicesSettings.class, "owner.id", userContext.getDomain().getId());
            if (serviceSetting == null || !serviceSetting.isNotifyCleanWatchList()) {
            	
//            	int appSize = 0;
//            	ApplicationProfile profile = QueryUtil.findBoByAttribute(ApplicationProfile.class, "owner.id", userContext.getDomain().getId(), new ConfigLazyQueryBo());
//            	if (profile != null) {
//            		appSize = profile.getApplicationList().size();
//            	}
//            	if (appSize > ApplicationUtil.getWatchlistLimitation()) {
//            		flag = true;
//            	}
            	flag = true;
            }
        } catch (Exception e) {
            log.error("[HmWatchlistCleanMsg] Error when get the msg display flag", e);
        }
   
        return flag;
    }

    @Override
    public void build(HmUser userContext) {
        this.contents = MgrUtil.getUserMessage("notification.message.watchlist.clean");
    	//this.contents = NOTIFICATION_MSG;
        // buttons
        List<AhNotificationMsgButton> buttons = generateButtons();
        this.actionButtons.addAll(buttons);
        // close icon
        if(null != userContext && userContext.getId() > 0L) {
            this.closeButton = new AhNotificationMsgButton(
                    MgrUtil.getUserMessage("notification.message.osversions.close.tips"),
                    "closeNotifyWatchlistClean()");
        }
    }

    @Override
    public boolean refresh(HmUser userContext, Object action) {
        return init(userContext);
    }

	private List<AhNotificationMsgButton> generateButtons() {
		// get the buttons description by user context. For VA user, it should not display the 'Donot Show' buttons.
		List<AhNotificationMsgButton> buttons = new ArrayList<AhNotificationMsgButton>();
		buttons.add(new AhNotificationMsgButton("", "watchlistClean()"));
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
                	serviceSetting.setNotifyCleanWatchList(true);
                	QueryUtil.createBo(serviceSetting);
                }
                else {
                	serviceSetting.setNotifyCleanWatchList(true);
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
