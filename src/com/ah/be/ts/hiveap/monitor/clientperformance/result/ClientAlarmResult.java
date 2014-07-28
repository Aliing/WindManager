package com.ah.be.ts.hiveap.monitor.clientperformance.result;

import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;

/**
 * <pre>
 * Name                             Size(in octet)      Description
 * ----------------------------------------------------------------
 * Mac address                      6   
 * Alarm flag                       1   
 * (optional)Times of client power save
 *      :current counter value      1   
 * (optional)Times of client power save
 *      :counter alarm threshold    1
 * ----------------------------------------------------------------
 * </pre>
 */
public class ClientAlarmResult extends AbstractClientPerfResult {

    private static final int MAX_SIZE_OF_FILEDS = 9;
    
    private String macAddress;
    private boolean alarmFlag;
    
    // optional fields
    private byte currentCounterValue;
    private byte counterAlarmThreshold;
    
    public ClientAlarmResult(ByteBuffer buffer) {
        super(buffer);
        
        this.macAddress = AhDecoder.bytes2hex(buffer, 6).toUpperCase();
        this.alarmFlag = buffer.get() == 1 ? true : false;
        
        if(length == MAX_SIZE_OF_FILEDS) {
            this.currentCounterValue = buffer.get();
            this.counterAlarmThreshold = buffer.get();
            
            containsOptionals = true;
        }
        
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("Client Alarm Info: [");
        builder.append("Mac address: " + this.macAddress)
                .append(", Alarm flag: " + (this.alarmFlag ? STR_ON : STR_OFF));
        if(containsOptionals) {
            builder.append(", Times of client power save (currentcounter value): "
                    + this.currentCounterValue)
                    .append(", Times of client power save (counter alarm threshold): "
                            + this.counterAlarmThreshold);
        }
        builder.append("]");
        return builder.toString();
    }
    
}
