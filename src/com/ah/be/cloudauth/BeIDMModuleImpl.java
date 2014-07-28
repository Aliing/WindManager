/**
 * 
 */
package com.ah.be.cloudauth;

import com.ah.be.app.BaseModule;
import com.ah.be.cloudauth.processor.BeIDMRadSecRenewProcessor;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeRadSecCertCreationResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;

/**
 * @author Yunzhi Lin
 *
 */
public class BeIDMModuleImpl extends BaseModule implements BeIDMModule {
    
    private BeIDMRadSecRenewProcessor processor;
    
    public BeIDMModuleImpl() {
        setModuleId(ModuleID_IDM);
        setModuleName("BeIDMModule");
    }
    
    @Override
    public boolean run() {
        processor = new BeIDMRadSecRenewProcessor();
        processor.startTask();
        return super.run();
    }
    
    @Override
    public boolean shutdown() {
        boolean success = true;
        if(null != processor) {
            success &= processor.shutdown();
        }
        return success;
    }
    
    @Override
    public void eventDispatched(BeBaseEvent arg_Event) {
        if (arg_Event.isShutdownRequestEvent()) {
            shutdown();
            return;
        }
        // communication event
        if (arg_Event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
            BeCommunicationEvent communicationEvent = (BeCommunicationEvent) arg_Event;
            // put interested communication event in queue
            if(communicationEvent.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
                BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
                switch (resultEvent.getResultType()) {
                case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_CERT_CREATION:
                    BeRadSecCertCreationResultEvent radsecResult = (BeRadSecCertCreationResultEvent) communicationEvent;
                    if(radsecResult.getSequenceNum() == 0) {
                        processor.addEvent(radsecResult);
                    }
                }
            }
        }
    }
}
