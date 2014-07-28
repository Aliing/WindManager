package com.ah.util.notificationmsg.message;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;

public class HmDisableL7Msg extends AhNotificationMessage {
		
	@Override
	public int initPriority() {
	    return L7_DSIABLE_PRIORITY;
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
        flag = isNeedBuildL7DisableMsg(userContext);
        return flag;
    }

    private boolean isNeedBuildL7DisableMsg(HmUser userContext) {
    	boolean flag = false;
    	if (userContext == null || userContext.getDomain() == null) {
    		return false;
    	}
        HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
        if(!hmDomain.getDomainName().equals(userContext.getDomain().getDomainName())) {
        	return false;
        }
        if (ReportCacheMgmt.getInstance().isEnableSystemL7Switch()) {
        	return false;
        }

        try {
        	HMServicesSettings serviceSetting = QueryUtil.findBoByAttribute(
            		HMServicesSettings.class, "owner.id", userContext.getDomain().getId());
            if (serviceSetting == null || !serviceSetting.isNotifyDisableL7()) {
            	flag = true;
            }
        } catch (Exception e) {
            log.error("[HmDisableL7Msg] Error when get the msg display flag", e);
        }
   
        return flag;
    }

    @Override
    public void build(HmUser userContext) {
        this.contents = MgrUtil.getUserMessage("notification.message.L7.disable");
        // buttons
        List<AhNotificationMsgButton> buttons = generateButtons();
        this.actionButtons.addAll(buttons);
        // close icon
        if(null != userContext && userContext.getId() > 0L) {
            this.closeButton = new AhNotificationMsgButton(
                    MgrUtil.getUserMessage("notification.message.osversions.close.tips"),
                    "closeNotifyL7DisableMsg()");
        }
    }

    @Override
    public boolean refresh(HmUser userContext, Object action) {
        return init(userContext);
    }

	private List<AhNotificationMsgButton> generateButtons() {
		// get the buttons description by user context. For VA user, it should not display the 'Donot Show' buttons.
		List<AhNotificationMsgButton> buttons = new ArrayList<AhNotificationMsgButton>();
		buttons.add(new AhNotificationMsgButton("Close", "closeNotifyL7DisableMsg()"));
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
                	serviceSetting.setNotifyDisableL7(true);
                	QueryUtil.createBo(serviceSetting);
                }
                else {
                	serviceSetting.setNotifyDisableL7(true);
                	QueryUtil.updateBo(serviceSetting);
                }
                flag = true;
            } catch (Exception e) {
                log.error("disableDisplay()", "Error when get the msg display status", e);
            }
        }
        return flag;
    }
    
    public String getBtnNameValue() {
        return btnNameValue = "Close";
    }
    
    @Override
    public String getMsgStyle() {
        return "padding-left: 45px;";
    }
}
