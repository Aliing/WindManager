package com.ah.be.fault;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.SendMailUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.monitor.AhAlarm;
import com.ah.util.MgrUtil;

public class TrapToMailProcessThread extends Thread implements QueryBo {

	private BlockingQueue<Object>	    eq		= null;
	private BeFaultModule				parent	= null;

	public TrapToMailProcessThread(BeFaultModule arg_Module) {
		parent = arg_Module;
	}

	@Override
	public void run() {
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
				"<BE Thread> Trap mail processor is running...");

		eq = parent.getTrapToMailQueue();
		if (eq == null) {
			DebugUtil.faultDebugWarn(" TrapMail thread :: trap queue is empty");
		}

		while (true) {
			try {
				Object obj = eq.take();
				DebugUtil.faultDebugInfo("processing an e-mail send request, remain size="
						+ eq.size());
				if (obj instanceof BeBaseEvent) {
					BeBaseEvent event = (BeBaseEvent) obj;
					DebugUtil
							.faultDebugInfo("processing an e-mail send request, remain size="
									+ eq.size());

				// shutdown thread. 2008-7-28. Jun & Yang
				if (event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
					BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
							"<BE Thread> Trap mail processor is shutdown. " + eq.size()
									+ " traps lost.");

					break;
				}

				HmDomain domain = getHmDomain((BeTrapEvent) event);
				MailNotification bo = getMailConfig(domain);
				if (bo == null) {
					DebugUtil.faultDebugInfo("TrapMail: no mail notification params!");
					continue;
				} else if (!bo.getSendMailFlag()) {
					DebugUtil.faultDebugInfo("TrapMail: forbid to send mail!");
					continue;
				}

				// check mail to list
				if (bo.getMailTo() == null || bo.getMailTo().length() == 0) {
					DebugUtil.faultDebugInfo("don't need send mail, no mail destination address!");
					continue;
				}

				String[] mailContent = getMailContent((BeTrapEvent) event, bo);

				if (mailContent != null && mailContent.length == 2) {
					SendMailUtil mailUtil = new SendMailUtil(bo);
					mailUtil.setSubject(mailContent[1]);
					mailUtil.setText(mailContent[0]);
					try {
						DebugUtil.faultDebugInfo("send mail [" + bo.getMailFrom() + " -> "
								+ bo.getMailTo() + "] start...");
						mailUtil.startSend();
						DebugUtil.faultDebugInfo("send mail end!");
					} catch (Exception e) {
						DebugUtil.faultDebugError("send mail failed! catch Exception", e);
					}
				 }
				} else if (obj instanceof AhAlarm) {
					AhAlarm alarm = (AhAlarm) obj;
					if(!sendAlarmEail(alarm)){
						continue;
					}
				}
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_TRACER, "TrapToMailProcessThread:run() catch Exception: " + e);
			} catch (Error e) {
				BeLogTools.error(HmLogConst.M_TRACER, "TrapToMailProcessThread:run() catch Error: " + e);
			}
		}
	}

	private boolean sendAlarmEail(AhAlarm alarm) throws Exception {
		 HmDomain domain = getAlarmDomain(alarm);
		 MailNotification bo = getMailConfig(domain);
			if (bo == null) {
				DebugUtil
						.faultDebugInfo("TrapMail: no mail notification params!");
				return false;
			} else if (!bo.getSendMailFlag()) {
				DebugUtil
						.faultDebugInfo("TrapMail: forbid to send mail!");
				return false;
			}
			// check mail to list
			if (bo.getMailTo() == null || bo.getMailTo().length() == 0) {
				DebugUtil
						.faultDebugInfo("don't need send mail, no mail destination address!");
				return false;
			}
			String[] mailContent = getAlarmMailContent(alarm,bo);
			if (mailContent != null && mailContent.length == 2) {
				SendMailUtil mailUtil = new SendMailUtil(bo);
				mailUtil.setSubject(mailContent[1]);
				mailUtil.setText(mailContent[0]);
				try {
					DebugUtil.faultDebugInfo("send mail ["
							+ bo.getMailFrom() + " -> "
							+ bo.getMailTo() + "] start...");
					mailUtil.startSend();
					DebugUtil.faultDebugInfo("send mail end!");
				} catch (Exception e) {
					DebugUtil.faultDebugError(
							"send mail failed! catch Exception", e);
					return false;
				}
			}
			return true;
	}

	private HmDomain getAlarmDomain(AhAlarm alarm) {
		if (alarm.getAlarmType() == BeFaultConst.ALARM_TYPY_HM) {
			return BoMgmt.getDomainMgmt().getHomeDomain();
		}
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(
				alarm.getApId());
		if (ap == null) {
			DebugUtil.faultDebugInfo("Not find AP info in cache: "
					+ alarm.getApId());
			return null;
		}
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(
				ap.getDomainId());
		return owner;
	}
	private String[] getAlarmMailContent(AhAlarm alarm, MailNotification bo)
			throws Exception {
		String[] mail = null;
		String description = alarm.getTrapDesc() != null ? alarm
				.getTrapDesc() : "nothing";
		String subject ="";
		int severity = alarm.getSeverity();
		//HM Alarm
		if(alarm.getAlarmType()==BeFaultConst.ALARM_TYPY_HM){
			if (!alarmObjectSeverityIsChoose(bo.getTca(), severity)) {
				DebugUtil
						.faultDebugInfo("don't need send mail, object="
								+ alarm.getObjectName() + ", severity="
								+ severity);
				return null;
			}
			//HM Alarm about eculid server
			if(alarm.getAlarmSubType()==BeFaultConst.ALARM_HM_EUCLID_SERVER){
				 if(severity== BeFaultConst.ALERT_SERVERITY_CLEAR){
					 subject=MgrUtil.getUserMessage("config.presence.euclidserver.clearAlarm");
				 }else{
					 subject=MgrUtil.getUserMessage("config.presence.euclidserver.criticalAlarm");
				 }
				 mail = new String[2];
				    mail[0] = description
							+ "\n\nAlert\n"
							+ "\nAlert Type:\t\t"
							+ alarm.getObjectName()
							+ "\nAlert Detail(Partition):\t\t"
							+ alarm.getTag3()
							+ "\nSeverity:\t\t\t"
							+ BeFaultConst.TRAP_ALERT_SEVERITY_TYPEX[alarm
									.getSeverity() - 1] + "\nMessage:\t\t"
							+ description + ".";

					mail[1] = subject;	
			}else{
				return null;
			}
		}
		return mail;
	}
	private HmDomain getHmDomain(BeTrapEvent trapEvent) {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(trapEvent.getApMac());
		if(trapEvent.getTrapType()==BeTrapEvent.TYPE_TCA_ALARM){
			return BoMgmt.getDomainMgmt().getHomeDomain();
		}
		if (ap == null) {
			DebugUtil.faultDebugInfo("Not find AP info in cache: " + trapEvent.getApMac());
			return null;
		}
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
		return owner;
	}

	private MailNotification getMailConfig(HmDomain domain) {
		return HmBeAdminUtil.getCacheMailNotification(domain.getDomainName());
	}

	private boolean alarmObjectSeverityIsChoose(byte object, int severity) {
		if ((object >> (severity - 1)) % 2 == 1) {
			return true;
		} else {
			return false;
		}
	}

	private String[] getMailContent(BeTrapEvent event, MailNotification bo) throws Exception {
		String[] mail = null;
		if(event.getTrapType() == BeTrapEvent.TYPE_TCA_ALARM){
			
			int eventSeverity = event.getSeverity();
			if (!alarmObjectSeverityIsChoose(bo.getTca(), eventSeverity)) {
				DebugUtil.faultDebugInfo("don't need send mail, object="
						+ event.getObjectName() + ", severity=" + eventSeverity);
				return null;
			}
			mail = new String[2];
			String description = event.getDescribe() != null ? event.getDescribe() : "nothing";
			String subject = event.getDescribe();
			String apName = event.getApName();

			mail[0] = subject + "\n\nAlert\n" + "\nAlert Type:\t\t" + event.getObjectName()+ "\nAlert Detail(Partition):\t\t" + event.getAlarmTag3()
					+ "\nSeverity:\t\t\t"
					+ BeFaultConst.TRAP_ALERT_SEVERITY_TYPEX[event.getSeverity() - 1]
					+ "\nMessage:\t\t" + description + ".";

			mail[1] = subject;
			
		}
		else if (event.getTrapType() == BeTrapEvent.TYPE_FAILURE
				|| event.getTrapType() == BeTrapEvent.TYPE_CAPWAP_EVENT
				|| event.getTrapType() == BeTrapEvent.TYPE_CAPWAP_DELAY
				|| event.getTrapType() == BeTrapEvent.TYPE_TIMEBOMBWARNING
				|| event.getTrapType() == BeTrapEvent.TYPE_SECURITY_ALARM
				|| event.getTrapType() == BeTrapEvent.TYPE_AD_ALARM) {
			int eventSeverity = event.getSeverity();
			if (event.getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[8])) {
				if (!alarmObjectSeverityIsChoose(bo.getHdRadio(), eventSeverity)) {
					DebugUtil.faultDebugInfo("don't need send mail, object="
							+ event.getObjectName() + ", severity=" + eventSeverity);
					return null;
				}
			} else if (event.getObjectName().equalsIgnoreCase(
					BeFaultConst.TRAP_SEND_MAIL_TYPEX[9])) {
				if (!alarmObjectSeverityIsChoose(bo.getCapWap(), eventSeverity)) {
					DebugUtil.faultDebugInfo("don't need send mail, object="
							+ event.getObjectName() + ", severity=" + eventSeverity);
					return null;
				}
			} else if (event.getObjectName().equalsIgnoreCase(
					BeFaultConst.TRAP_SEND_MAIL_TYPEX[10])) {
				if (!alarmObjectSeverityIsChoose(bo.getConfig(), eventSeverity)) {
					DebugUtil.faultDebugInfo("don't need send mail, object="
							+ event.getObjectName() + ", severity=" + eventSeverity);
					return null;
				}
			} else if (event.getObjectName().equalsIgnoreCase(
					BeFaultConst.TRAP_SEND_MAIL_TYPEX[12])) {
				if (!alarmObjectSeverityIsChoose(bo.getTimeBomb(), eventSeverity)) {
					DebugUtil.faultDebugInfo("don't need send mail, object="
							+ event.getObjectName() + ", severity=" + eventSeverity);
					return null;
				}
			} else if (event.getTrapType() == BeTrapEvent.TYPE_SECURITY_ALARM) {
				if (!alarmObjectSeverityIsChoose(bo.getSecurity(), eventSeverity)) {
					return null;
				}
			} else if (event.getTrapType() == BeTrapEvent.TYPE_AD_ALARM) {
				if (!alarmObjectSeverityIsChoose(bo.getAd(), eventSeverity)) {
					return null;
				}
			}			
			else {
					DebugUtil.faultDebugInfo("don't need send mail, Unknow object: "
							+ event.getObjectName());
					return null;
			}

		
			mail = new String[2];
			String description = event.getDescribe() != null ? event.getDescribe() : "nothing";
			String subject = "";
			String apName = event.getApName();
			if (event.getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[9])) {
				if (eventSeverity == BeFaultConst.ALERT_SERVERITY_CLEAR) {
					subject = "HiveAP:" + apName
							+ " connected. The CAPWAP client reconnected because "
							+ changeFirstLetterToLowercase(description);
				} else {
					subject = NmsUtil.getOEMCustomer().getAccessPonitName()+" " + apName + ": " + description;
				}
			} else {
				subject = NmsUtil.getOEMCustomer().getAccessPonitName()+" " + apName + ": " + description;
			}

			mail[0] = subject + "\n\nAlert\n" + "\nAlert Type:\t\t" + event.getObjectName()
					+ "\nSeverity:\t\t\t"
					+ BeFaultConst.TRAP_ALERT_SEVERITY_TYPEX[eventSeverity - 1]
					+ "\n"+NmsUtil.getOEMCustomer().getAccessPonitName()+" ID:\t\t" + event.getApMac() + "\n"+NmsUtil.getOEMCustomer().getAccessPonitName()+" Name:\t"
					+ apName + "\nTime:\t\t\t" + event.getAhTimeDisplay2()
					+ "\nMessage:\t\t" + description + ".";

			mail[1] = subject;
		} else if (event.getTrapType() == BeTrapEvent.TYPE_CONNECTCHANGE
				|| event.getTrapType() == BeTrapEvent.TYPE_STATECHANGE
				|| event.getTrapType() == BeTrapEvent.TYPE_THRESHOLDCROSSING
				|| event.getTrapType() == BeTrapEvent.TYPE_POE
				|| event.getTrapType() == BeTrapEvent.TYPE_CHANNELPOWERCHANGE
				|| event.getTrapType() == BeTrapEvent.TYPE_AIRSCREENREPORT
				|| event.getTrapType() == BeTrapEvent.TYPE_VPN
				|| event.getTrapType() == BeTrapEvent.TYPE_INTERFACECLIENTTRAP
				|| event.getTrapType() == BeTrapEvent.TYPE_PSE_ERROR
				|| event.getTrapType() == BeTrapEvent.TYPE_POWER_MODE_CHANGE
				|| event.getTrapType() == BeTrapEvent.TYPE_CWP_INFO) {

			if ((event.isHDCPUEvent() && bo.isHdCpu())
					|| (event.isHDMemoryEvent() && bo.isHdMemory())
					|| (event.isAuthEvent() && bo.isAuth())
					|| (event.isInterfaceEvent() && bo.isInterfaceValue())
					|| (event.isL2DOSEvent() && bo.isL2Dos())
					|| (event.isScreenEvent() && bo.isScreen())
					|| (event.isVPNEvent() && bo.isVpn())
					|| (event.isAirScreenEvent() && bo.isAirScreen())
					|| (event.isClientMonitorEvent() && bo.isClientMonitor())
					//|| (event.isClientRegisterEvent() && bo.isClientRegister())
					) {

				mail = new String[2];
				String description = event.getDescribe() == null ? "nothing" : event
						.getDescribe();
				String subject = "";
				String apName = event.getApName();
				subject = NmsUtil.getOEMCustomer().getAccessPonitName()+" " + apName + ": " + description;

				mail[0] = subject + "\n\nEvent\n" + "\nEvent Type:\t\t" + event.getObjectName()
						+ "\nTrap Type:\t\t" + event.getTrapTypeString() + "\n"+NmsUtil.getOEMCustomer().getAccessPonitName()+" ID:\t\t"
						+ event.getApMac() + "\n"+NmsUtil.getOEMCustomer().getAccessPonitName()+" Name:\t\t" + apName
						+ "\nTime:\t\t\t" + event.getAhTimeDisplay2() + "\nMessage:\t\t"
						+ description + ".";

				mail[1] = subject;
			}
		} else if(event.getTrapType() == BeTrapEvent.TYPE_ALARM) {
			mail = new String[2];
			String description = "Alarm Info";
			String subject = "";
			String apName = event.getApName();
			subject = NmsUtil.getOEMCustomer().getAccessPonitName()+" " + apName + ": " + description;
			StringBuffer mailContent = new StringBuffer();
			mailContent.append(subject);
			List<AhAlarm> alarmList = event.getAlarmList();

			for(int i = 0; i < alarmList.size(); i++) {
				AhAlarm alarm = alarmList.get(i);
				if((alarm.getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[18]) 
						&& alarmObjectSeverityIsChoose(bo.getClient(), alarm.getSeverity())) 
					|| (alarm.getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[20]) 
						&& alarmObjectSeverityIsChoose(bo.getSystem(), alarm.getSeverity())) ){
					mailContent.append("\n\nAlarm " + Integer.valueOf(i + 1) + "\n");
					mailContent.append("\nAlarm Type:\t\t");
					mailContent.append(alarm.getObjectName());
					mailContent.append("\nSub Alarm Type:\t\t");
					mailContent.append(alarm.getAlarmSubType());
					mailContent.append("\nSeverity:\t\t");
					mailContent.append(BeFaultConst.TRAP_ALERT_SEVERITY_TYPEX[alarm.getSeverity()-1]);
					mailContent.append("\nTime:\t\t");
					mailContent.append(alarm.getTrapTimeString());
					mailContent.append("\nDescription:\t\t");
					mailContent.append(alarm.getTrapDesc());
				}
			}
			mail[0] = mailContent.toString();
			mail[1] = subject;
		}
		else {
			DebugUtil.faultDebugInfo("don't need send mail, trap type=" + event.getTrapType());
		}

		return mail;
	}

	private String changeFirstLetterToLowercase(String description) {
		if(description == null || description.length() < 2)return description;
		char a = description.charAt(0);
		char b = description.charAt(1);

		if (a >= 'A' && a <= 'Z') {
			if (b >= 'A' && b <= 'Z') {
				return description;
			} else {
				return String.valueOf((char) (a + ('a' - 'A'))) + description.substring(1);
			}
		} else {
			return description;
		}
	}

	public void stopThread() {
		// stop thread
		BeBaseEvent shutdownEvent = new AhShutdownEvent();
		eq.add(shutdownEvent);
	}

	public Collection<HmBo> load(HmBo bo) {
		if (null == bo) {
			return null;
		}
		if (bo instanceof MailNotification) {
			MailNotification mail = (MailNotification) bo;
			if (mail.getOwner() != null) {
				mail.getOwner().getId();
			}
		}
		return null;
	}
}