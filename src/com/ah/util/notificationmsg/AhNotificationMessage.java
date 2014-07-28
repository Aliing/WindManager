// $Id: AhNotificationMessage.java,v 1.11.44.1 2014/03/14 09:26:43 huihe Exp $
package com.ah.util.notificationmsg;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.util.Tracer;

public abstract class AhNotificationMessage implements AhNotificationMsgConstant{
    protected static final Tracer log = new Tracer(AhNotificationMessage.class.getSimpleName());

    protected int               priority;
    protected int               type;
    protected String            contents;
    protected boolean          enableDisplay;
    /** The interval field is used for refreshing message by thread */
    protected long              refreshInterval;
    
    protected AhNotificationMsgButton closeButton;
    protected List<AhNotificationMsgButton> actionButtons = new ArrayList<AhNotificationMsgButton>();
    
    protected String            msgStyle;
    protected String            btnGroupStyle;
    protected String            btnNameValue;
    protected String            goBtnWidth;
    protected String			itemStyle;
    protected HmUser 			userContext;

	public boolean init(HmUser userContext) {
		this.userContext = userContext;
        // check the priority value
        this.priority = initPriority();
        if(this.priority <= 0) {
            return false;
        }
        actionButtons.clear();
        
        try {
            // check Enable/Disable display flag
            enableDisplay = isDisplayFlagOn(userContext);
            debug("The flag is: "
                    + (enableDisplay ? "true - [EnableDisplay], continue proceed building msg"
                            : "false - [DisableDisplay], stop build msg") + "- ["+this.priority+"].");
            if (enableDisplay) {
                enableDisplay = isNeedBuild(userContext);
                debug("The flag is: "
                        + (enableDisplay ? "true - [Need2BuildMsg], continue proceed building msg"
                                : "false - [NoNeed2BuildMsg], stop build msg") + "- ["+this.priority+"].");
                if (enableDisplay) {
                    build(userContext);
                    enableDisplay = validateMsg();
                }
            }
        } catch (Exception e) {
            log.error("Error when initialize the notification message", e);
            enableDisplay = false;
        }
        
        return enableDisplay;
    }
	/** Initial the priority of message. The value should be unique. <br>(Defined in {@link AhNotificationMsgConstant}) */
    public abstract int        initPriority();
    /** Check the message display flag in database or session. */
	public abstract boolean	isDisplayFlagOn(HmUser userContext);
	/** Check if it is necessary to build the notification message. */
	public abstract boolean	isNeedBuild(HmUser userContext);
	/** Build the message: the message content, message actions({@link AhNotificationMsgButton}) are required.*/
	public abstract void		build(HmUser userContext);
	/** Refresh the message.*/
	public abstract boolean	refresh(HmUser userContext, Object action);
	/** Disable the message in the widget.*/
	public abstract boolean	disableDisplay(HmUser userContext);
	
    private boolean validateMsg() {
        if (this.priority > 0 && StringUtils.isNotBlank(this.contents)
                && !actionButtons.isEmpty()) {
            debug("Build the msg = ["+this.toString()+"] successfully.");
            return true;
        } else {
            log.warn("validateMsg()", 
                    "Unable to add the msg to pool cause the msg = ["+this.toString()+"] is invalid format.");
            return false;
        }
    }
    /*------------Common Method-------------*/
    protected void debug(String debugMsg) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String methodName = stackTraceElement.getMethodName();
//        String className = stackTraceElement.getClassName();
//        String fileName = stackTraceElement.getFileName();
//        int lineNumber = stackTraceElement.getLineNumber();
             
        log.debug(methodName, debugMsg);
//        System.out.println(this.getClass().getSimpleName() + "." + methodName + "()@[lineNum="
//                + lineNumber + "]" + " >> [Message] " + debugMsg);
    }
    
    protected boolean isDomainUser(HmUser userContext) {
        if (null != userContext && null != userContext.getDomain()) {
            HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
            if (hmDomain.getDomainName().equals(userContext.getDomain().getDomainName())) {
                return true;
            }
        }
        return false;
    }
    /*------------Override Method-------------*/
	@Override
	public String toString() {
        return new ToStringBuilder(this)
                .append("priority", priority)
                .append("contens", contents)
                .append("Buttons", actionButtons).toString();
	}
	
	/*---------- Getter/Setter --------------*/
	public int getPriority() {
		return priority;
	}
    protected void setPriority(int priority) {
		this.priority = priority;
	}
	protected int getType() {
		return type;
	}
	protected void setType(int type) {
		this.type = type;
	}
	public String getContents() {
		return contents;
	}
	protected void setContents(String contents) {
		this.contents = contents;
	}
    public List<AhNotificationMsgButton> getActionButtons() {
        return actionButtons;
    }
    protected void setActionButtons(List<AhNotificationMsgButton> actionButtons) {
        this.actionButtons = actionButtons;
    }
    protected long getRefreshInterval() {
		return refreshInterval;
	}
	protected void setRefreshInterval(long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
    public boolean isEnableDisplay() {
        return enableDisplay;
    }
    protected void setEnableDisplay(boolean enableDisplay) {
        this.enableDisplay = enableDisplay;
    }
    public AhNotificationMsgButton getCloseButton() {
        return closeButton;
    }
    protected void setCloseButton(AhNotificationMsgButton closeButton) {
        this.closeButton = closeButton;
    }
    public String getMsgStyle() {
        return msgStyle;
    }
    public void setMsgStyle(String msgStyle) {
        this.msgStyle = msgStyle;
    }
    public String getBtnGroupStyle() {
        if(null == this.closeButton) {
            return "padding-left:10px;";
        }
        return "";
    }
	public String getItemStyle() {
		return itemStyle;
	}
	public void setItemStyle(String itemStyle) {
		this.itemStyle = itemStyle;
	}
    public String getGoBtnWidth() {
        return goBtnWidth;
    }
    public void setGoBtnWidth(String goBtnWidth) {
        this.goBtnWidth = goBtnWidth;
    }
    public String getBtnNameValue() {
        return btnNameValue == null ? "Go" : btnNameValue;
    }
    public void setBtnNameValue(String btnNameValue) {
        this.btnNameValue = btnNameValue;
    }
    
	/**
	 * get domain object
	 *
	 * @return -
	 */
	public HmDomain getDomain() {
		return userContext.getSwitchDomain() == null ? userContext.getDomain()
				: userContext.getSwitchDomain();
	}
}
