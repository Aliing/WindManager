package com.ah.util.notificationmsg.message;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.app.DebugUtil;
import com.ah.be.fault.BeFaultConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;

public class HmCriticalAlarmWarningMsg extends AhNotificationMessage {

	@Override
	public int initPriority() {
		return CRITICAL_CLEAR_MSG_PRIORITY;
	}

	@Override
	public boolean isDisplayFlagOn(HmUser userContext) {
		if (null == userContext) {
			return false;
		}
		long domainId = QueryUtil.getDependentDomainFilter(userContext);
		Object obj = MgrUtil.getSessionAttribute(domainId + ":"
				+ SessionKeys.CLOSE_CRITICAL_ALARM_MSG);
		if (null != obj && (boolean) obj) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isNeedBuild(HmUser userContext) {
		if (null == userContext) {
			return false;
		}
		int reminderDays = LogSettings.DEFAULT_ALARM_REMINDER_DAYS;
		List<?> list = QueryUtil.executeQuery("select alarmReminderDays from "
				+ LogSettings.class.getSimpleName(), 1);
		if (null == list || list.isEmpty()) {
			DebugUtil
					.faultDebugWarn("RemoveEventAlarmTimerTask: no log settings in db!");
		} else {
			reminderDays = (Integer) list.get(0);
		}
		this.contents = MgrUtil.getUserMessage(
				"glasgow_19.critical.alarm.warningMsg",
				String.valueOf(reminderDays));
		long domainId = QueryUtil.getDependentDomainFilter(userContext);
		String where = "trap_time < :s1 and severity = :s2 and owner.id= :s3";
		Object[] values = new Object[] {
				AhDateTimeUtil.getDateAfter2(-reminderDays),
				(short) BeFaultConst.ALERT_SERVERITY_CRITICAL, domainId };
		long rowCount = QueryUtil.findRowCount(AhAlarm.class, new FilterParams(
				where, values));
		return rowCount > 0;
	}

	@Override
	public void build(HmUser userContext) {
		this.contents = "Warning: " + this.contents;
		// buttons
		List<AhNotificationMsgButton> buttons = getActionButtonsDescByUserContext(userContext);
		this.actionButtons.addAll(buttons);
		// close icon
		if (null != userContext && userContext.getId() > 0L) {
			this.closeButton = new AhNotificationMsgButton(
					"Hide this message.", "closeCriticalAlarmMsg()");
		}
	}

	private List<AhNotificationMsgButton> getActionButtonsDescByUserContext(
			HmUser userContext) {
		List<AhNotificationMsgButton> buttons = new ArrayList<AhNotificationMsgButton>();
		buttons.add(new AhNotificationMsgButton("Clear Now",
				"clearCriticalAlarm(true)"));
		return buttons;
	}

	@Override
	public boolean refresh(HmUser userContext, Object action) {
		if (null == userContext) {
			return false;
		}
		boolean flag = false;
		boolean refresh = false;
		long domainId = QueryUtil.getDependentDomainFilter(userContext);
		Long domianKey = (Long) MgrUtil
				.getSessionAttribute(SessionKeys.DOMAIN_SESSION_KEY);
		if (null == domianKey) {
			flag = true;
		} else if (domianKey != domainId) {
			flag = true;
		}
		if (flag) {
			refresh = init(userContext);
			MgrUtil.setSessionAttribute(SessionKeys.DOMAIN_SESSION_KEY,
					domainId);
		}
		return refresh;
	}

	@Override
	public boolean disableDisplay(HmUser userContext) {
		return true;
	}

}
