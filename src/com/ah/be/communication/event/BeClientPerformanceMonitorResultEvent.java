package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.ts.hiveap.monitor.clientperformance.ClientPerfResultFactory;
import com.ah.be.ts.hiveap.monitor.clientperformance.EClientPerfInfoType;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.AbstractClientPerfResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.ClientResult;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeClientPerformanceMonitorResultEvent extends BeCapwapClientResultEvent {

    private long sequenceNumber4TroubleShoot;
    private String clientMac;
    private List<AbstractClientPerfResult> clientResults = new ArrayList<AbstractClientPerfResult>();

    public BeClientPerformanceMonitorResultEvent() {
        super();
        resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTPERFORMANCE;
    }

    @Override
    protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
        try {
            super.parsePacket(data);

            ByteBuffer buf = ByteBuffer.wrap(resultData);

            sequenceNumber4TroubleShoot = AhDecoder.int2long(buf.getInt());
            short totalNum = buf.getShort();
            for (int i = 0; i < totalNum; i++) {
                byte type = buf.get();
                
                AbstractClientPerfResult clientResult = ClientPerfResultFactory.createResult(buf, type);
                if(null != clientResult) {
                    clientResults.add(clientResult);
                    if(type == EClientPerfInfoType.CLIENT.getValue()) {
                        clientMac = ((ClientResult)clientResult).getMacAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeCommunicationDecodeException(
                    "BeClientPerformanceResultEvent.parsePacket() catch exception", e);
        }
    }
    
    public List<AbstractClientPerfResult> getClientResults() {
        return clientResults;
    }

    public void setClientResults(List<AbstractClientPerfResult> clientResults) {
        this.clientResults = clientResults;
    }

    public long getSequenceNumber4TroubleShoot() {
        return sequenceNumber4TroubleShoot;
    }

    public void setSequenceNumber4TroubleShoot(long sequenceNumber4TroubleShoot) {
        this.sequenceNumber4TroubleShoot = sequenceNumber4TroubleShoot;
    }

    public String getClientMac() {
        return clientMac;
    }

    public void setClientMac(String clientMac) {
        this.clientMac = clientMac;
    }

}
