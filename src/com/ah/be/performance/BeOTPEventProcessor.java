package com.ah.be.performance;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeOTPStatusEvent;
import com.ah.be.communication.event.BeOTPStatusResultEvent;
import com.ah.be.config.hiveap.provision.ProvisionProcessor;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.OneTimePassword;
import com.ah.util.MgrUtil;

public class BeOTPEventProcessor {
	private final BlockingQueue<BeBaseEvent> eventQueue;

	private static final int eventQueueSize = 10000;
	
	private boolean isContinue;
	
	private final OneTimePasswordThread oneTimePasswordThread;
	
	public BeOTPEventProcessor(){
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
		isContinue = true;
		oneTimePasswordThread = new OneTimePasswordThread();
		oneTimePasswordThread.setName("oneTimePasswordThread");
		oneTimePasswordThread.start();
	}
	
	public void addEvent(BeBaseEvent event) {
		try {
			eventQueue.offer(event);
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeOTPEventProcessor.addEvent(): Exception while add event to queue", e);
		}
	}
	
	public void startTask() {
		// nothing else to do at current time
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> One-Time Password Processor is running...");
	}
	
	public boolean shutdown() {
		isContinue = false;
		eventQueue.clear();
		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.offer(stopThreadEvent);
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> One-Time Password Processor is shutdown");
		
		return true;
	}
	
	public class OneTimePasswordThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setThreadName(oneTimePasswordThread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> One-Time Password Process Thread is running...");
			
			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = eventQueue.take();
					if (null == event)
						continue;

					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;
						if (communicationEvent.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
							BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
							if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_OTP) {
								BeOTPStatusResultEvent otpStatusResultEvent = (BeOTPStatusResultEvent)resultEvent;
								handleOTPStatusResultEvent(otpStatusResultEvent);
							}
							
						}
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeDeviceRealTimeProcessor.DeviceRealTimeThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn(
							"BeDeviceRealTimeProcessor.DeviceRealTimeThread.run() Error in processor thread", e);
				}
			}
		}
	}
	
	private void handleOTPStatusResultEvent(BeOTPStatusResultEvent resultEvent){
		try {
			String apMac = resultEvent.getApMac();
			SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp == null) {
				throw new BeCommunicationDecodeException("Invalid apMac: (" + apMac
						+ "), Can't find corresponding data in cache.");
			}
			HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());
			String password = resultEvent.getPassword().trim();
			
			//if response password is null send check error to br.
			if(password == null || "".equals(password)){
				//when password is null,send check error message to br
				BeTopoModuleUtil.sendOtpEventQuery(resultEvent.getAp(), BeOTPStatusEvent.OTP_MODE_WRONG,resultEvent.getPassword());
				
				//set device manageStatus to new.
				QueryUtil.updateBos(HiveAp.class, 
						"manageStatus = :s1", 
						"upper(macAddress) = :s2", 
						new Object[]{HiveAp.STATUS_NEW, apMac.toUpperCase()});
				return;
			}
			
			//judge OTP mapping whether exists.
			List<OneTimePassword> macList = QueryUtil.executeQuery(OneTimePassword.class, null,
					new FilterParams("upper(macAddress) = :s1",
							new Object[] {apMac.toUpperCase()}));
			if(macList != null && !macList.isEmpty()){
				OneTimePassword macOtp = macList.get(0);
				if(macOtp.getOneTimePassword().equals(password)){
					//send sucess message
					BeTopoModuleUtil.sendOtpEventQuery(resultEvent.getAp(), BeOTPStatusEvent.OTP_MODE_CORRECT,resultEvent.getPassword());
					//do auto Provision
					ProvisionProcessor.doAutoProvision(apMac);
					return;
				}else{
					BeTopoModuleUtil.sendOtpEventQuery(resultEvent.getAp(), BeOTPStatusEvent.OTP_MODE_WRONG,resultEvent.getPassword());
					//set device manageStatus to new.
					QueryUtil.updateBos(HiveAp.class, 
							"manageStatus = :s1", 
							"upper(macAddress) = :s2", 
							new Object[]{HiveAp.STATUS_NEW, apMac.toUpperCase()});
					return;
				}
			}
			
			
			List<OneTimePassword> passList = QueryUtil.executeQuery(OneTimePassword.class, null,
					new FilterParams("oneTimePassword = :s1 AND owner.id = :s2",
							new Object[] {password, owner.getId()}));
			
			//if response password not exists in Hm database send check error to br.
			if(passList == null || passList.isEmpty()){
				BeTopoModuleUtil.sendOtpEventQuery(resultEvent.getAp(), BeOTPStatusEvent.OTP_MODE_WRONG,resultEvent.getPassword());
				//set device manageStatus to new.
				QueryUtil.updateBos(HiveAp.class, 
						"manageStatus = :s1", 
						"upper(macAddress) = :s2", 
						new Object[]{HiveAp.STATUS_NEW, apMac.toUpperCase()});
				return;
			}
			
			OneTimePassword otpResult = passList.get(0);
			
			//create mapping between otp and macaddress.
			if(otpResult.getMacAddress() == null || "".equals(otpResult.getMacAddress())){
				//build new mapping between macAddress and oneTimePassword
				QueryUtil.updateBos(OneTimePassword.class, 
						"macAddress = :s1,dateTimeZone = :s2,dateActivateStamp = :s3,deviceModel = :s4", 
						"id = :s5", 
						new Object[]{apMac.toUpperCase(), owner.getTimeZoneString(), System.currentTimeMillis(),
									simpleHiveAp.getHiveApModel(), otpResult.getId()});
				//send sucess message
				BeTopoModuleUtil.sendOtpEventQuery(resultEvent.getAp(), BeOTPStatusEvent.OTP_MODE_CORRECT,resultEvent.getPassword());
				//do auto Provision
				ProvisionProcessor.doAutoProvision(apMac);
				return;
			}
			
			if(otpResult.getMacAddress().equalsIgnoreCase(apMac)){
				//send sucess message
				BeTopoModuleUtil.sendOtpEventQuery(resultEvent.getAp(), BeOTPStatusEvent.OTP_MODE_CORRECT,resultEvent.getPassword());
				//do auto Provision
				ProvisionProcessor.doAutoProvision(apMac);
			}else{
				//send failed message
				BeTopoModuleUtil.sendOtpEventQuery(resultEvent.getAp(), BeOTPStatusEvent.OTP_MODE_WRONG,resultEvent.getPassword());
				//set device manageStatus to new.
				QueryUtil.updateBos(HiveAp.class, 
						"manageStatus = :s1", 
						"upper(macAddress) = :s2", 
						new Object[]{HiveAp.STATUS_NEW, apMac.toUpperCase()});
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeOTPEventProcessor.handleOTPStatusResultEvent(): catch exception.", e);
		}
	}
	
	
}
