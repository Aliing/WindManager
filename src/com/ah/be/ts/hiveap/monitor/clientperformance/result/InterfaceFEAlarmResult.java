package com.ah.be.ts.hiveap.monitor.clientperformance.result;

import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;

/**
 * <pre>
 * Name                     Size(in octet)      Description
 * --------------------------------------------------------
 * Interface name length    1   
 * Interface name           Vary    
 * Alarm flag               1   
 * (optional)Current rate value     1   
 * (optional)Threshold              1
 * --------------------------------------------------------
 * </pre> 
 */
public class InterfaceFEAlarmResult extends AbstractClientPerfResult {

    private String interfaceName;
    private byte alarmFlag;
    // optional fields
    private byte currentRate;
    private byte threshold;
    
    public InterfaceFEAlarmResult(ByteBuffer buffer) {
        super(buffer);

        int nameLength = AhDecoder.byte2int(buffer.get());
        this.interfaceName = AhDecoder.bytes2String(buffer, nameLength);
        this.alarmFlag = buffer.get();
        if(nameLength + 2 < length) {
            this.currentRate = buffer.get();
            this.threshold = buffer.get();
            
            containsOptionals = true;
        }
        
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("Interface FE Alarm Info: [");
        builder.append("Interface name: " + this.interfaceName)
                .append(", Alarm flag: " + this.alarmFlag);
        if(containsOptionals) {
            builder.append(", Current rate value: " + this.currentRate + PERCENTAGE_MARK)
                    .append(", Threshold: " + this.threshold + PERCENTAGE_MARK);
        }
        builder.append("]");
        return builder.toString();
    }

}
