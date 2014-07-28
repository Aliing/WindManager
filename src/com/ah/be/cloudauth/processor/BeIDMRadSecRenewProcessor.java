package com.ah.be.cloudauth.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.cloudauth.ICloudAuthCertMgmt;
import com.ah.be.cloudauth.result.HmCloudAuthCertResult;
import com.ah.be.cloudauth.result.UpdateCAStatus;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeRadSecCertCreationResultEvent;
import com.ah.be.communication.event.BeRadSecCertRevokenEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;

public class BeIDMRadSecRenewProcessor implements IBeProcessor{
    
    private static final String LOG_PREFIX = "<BE Thread> IDM Certification Renew Processor - ";

    private final BlockingQueue<BeBaseEvent> eventQueue;

    private static final int eventQueueSize = 10000;
    
    private volatile boolean running;

    public BeIDMRadSecRenewProcessor() {
        eventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
    }
    
    @Override
    public void startTask() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MgrUtil.setTimerName(this.getClass().getSimpleName());
                BeLogTools.info(HmLogConst.M_TRACER, LOG_PREFIX + "event processor is running...");
                
                running = true;
                
                while (running) {
                    try {
                        BeBaseEvent event = eventQueue.take();
                        if(null == event) {
                            continue;
                        }
                        if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
                            BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;
                            // put interested communication event in queue
                            if(communicationEvent.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
                                BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
                                switch (resultEvent.getResultType()) {
                                case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_CERT_CREATION:
                                    BeRadSecCertCreationResultEvent radsecResult = (BeRadSecCertCreationResultEvent) communicationEvent;
                                    if (radsecResult.getSequenceNum() == 0) {
                                        // only handle the auto request from device
                                        ICloudAuthCertMgmt<HmCloudAuthCertResult> mgmt = new HmCloudAuthCertMgmtImpl();
                                        final HiveAp device = radsecResult.getAp();
                                        HmCloudAuthCertResult result = mgmt
                                                .renewCertificationByRequest(device, radsecResult);
                                        if (result.getStatus() == UpdateCAStatus.SUCCESS) {
                                            String certCli = getRadSecSaveCertCLI(device);
                                            
                                            // send CLI command
                                            BeCliEvent cliRequest = new BeCliEvent();
                                            cliRequest.setAp(device);
                                            cliRequest.setClis(new String[] { certCli });
                                            cliRequest.setSequenceNum(HmBeCommunicationUtil
                                                    .getSequenceNumber());
                                            cliRequest.buildPacket();
                                            
                                            BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(cliRequest);
                                            if(BeTopoModuleUtil.isCliExeSuccess(response)) {
                                                BeLogTools.info(HmLogConst.M_TRACER, LOG_PREFIX + "event processor is running...");
                                            } else {
                                                String msg = BeTopoModuleUtil.parseCliRequestResult(response);
                                                BeLogTools.error(HmLogConst.M_TRACER, LOG_PREFIX + "event processor: send save radsec cert fail. "+msg);
                                            }
                                            
                                        } else {
                                            if (result.getStatus() == UpdateCAStatus.IDM_SERVER_ERR
                                                    && result.getResultMessage().contains("[1009]")) {
                                                // the device is in the IDM black list,
                                                BeRadSecCertRevokenEvent revokenEvent = new BeRadSecCertRevokenEvent();
                                                revokenEvent.setAp(device);
                                                revokenEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
                                                try {
                                                    revokenEvent.buildPacket();
                                                    HmBeCommunicationUtil.sendRequest(revokenEvent);
                                                } catch (Exception e) {
                                                    BeLogTools.error(HmLogConst.M_TRACER, LOG_PREFIX + "event processor: send cert revoken fail. ", e);   
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        BeLogTools.error(HmLogConst.M_TRACER, LOG_PREFIX + "event processor occurs error...",e);
                    }
                }
                BeLogTools.info(HmLogConst.M_TRACER, LOG_PREFIX + "event processor is break...");
            }
        },"IDMCertRenewThread");
        thread.start();
    }

    @Override
    public void addEvent(BeBaseEvent event) {
        try {
            eventQueue.offer(event);
        } catch (Exception e) {
            BeLogTools.error(HmLogConst.M_TRACER, LOG_PREFIX + "processor add new event error");
        }
    }

    @Override
    public boolean shutdown() {
        BeLogTools.info(HmLogConst.M_TRACER, LOG_PREFIX + "processor is shutdown");

        running = false;
        
        eventQueue.clear();
        BeBaseEvent stopThreadEvent = new BeBaseEvent();
        eventQueue.offer(stopThreadEvent);
        return true;
    }

    private String getRadSecSaveCertCLI(final HiveAp device) {
        if(null == device) {
            return "";
        }
        String hiveApMac = device.getMacAddress();
        String domainName = device.getOwner().getDomainName();
        String host = NmsUtil.getRunningCapwapServer(device);
        String userName = NmsUtil.getHMScpUser();
        String password = NmsUtil.getHMScpPsd();
        String fileName = HmCloudAuthCertMgmtImpl.CERTIFICATE_NAME;
        String certCli = "";
        
        if (device.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
            certCli = AhCliFactory.downloadCloudAuthCaCert(domainName, hiveApMac, host, fileName,
                    userName, password);
        } else {
            String proxy = device.getProxyName();
            int proxyPort = device.getProxyPort();
            String proxyLoginUser = device.getProxyUsername();
            String proxyLoginPwd = device.getProxyPassword();
            certCli = AhCliFactory.downloadCloudAuthCaCertViaHttp(domainName, hiveApMac, host,
                    fileName, userName, password, proxy, proxyPort,
                    proxyLoginUser, proxyLoginPwd);
        }
        return certCli;
    }

}
