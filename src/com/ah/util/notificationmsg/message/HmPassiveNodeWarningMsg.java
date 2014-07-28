/**
 *@filename		HmPassiveNodeWarningMsg.java
 *@version
 *@author		Fiona
 *@createtime	Feb 8, 2012 4:36:55 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.util.notificationmsg.message;

import com.ah.bo.admin.HmUser;
import com.ah.ha.HAUtil;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class HmPassiveNodeWarningMsg extends AhNotificationMessage {

	@Override
	public void build(HmUser userContext) {
		this.contents = "Warning: " +this.contents;
		// buttons
        this.actionButtons.add(new AhNotificationMsgButton("Go to Active", "gotoActiveNodeFromPassive()"));
	}

	@Override
	public boolean disableDisplay(HmUser userContext) {
		return false;
	}

	@Override
	public int initPriority() {
		return PASSIVE_NODE_WARING_MSG_PRIORITY;
	}

	@Override
	public boolean isDisplayFlagOn(HmUser userContext) {
		return HAUtil.isSlave();
	}
	
/*-    @Override
    public String getMsgStyle() {
        return "max-width: 460px;";
    }
*/    
	@Override
	public boolean isNeedBuild(HmUser userContext) {
		this.contents = MgrUtil.getUserMessage("warn.admin.ha.supper.admin.in.passive.node");
		return true;
	}

	@Override
	public boolean refresh(HmUser userContext, Object action) {
		return init(userContext);
	}

}
