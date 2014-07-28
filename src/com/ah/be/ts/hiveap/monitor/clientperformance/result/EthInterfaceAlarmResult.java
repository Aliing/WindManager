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
 * (optional)Current tx errors value    1   
 * (optional)Tx Alarm threshold     1   
 * (optional)Rx errors value            1   
 * (optional)Rx Alarm threshold     1
 * -------------------------------------------------------- 
 * </pre>
 */
public class EthInterfaceAlarmResult extends AbstractClientPerfResult {
    
    private String interfaceName;
    private byte alarmFlag;
    // optional fields
    private byte txErrorsValue;
    private byte txAlarmThreshold;
    private byte rxErrorsValue;
    private byte rxAlarmThreshold;

    public EthInterfaceAlarmResult(ByteBuffer buffer) {
        super(buffer);

        int nameLength = AhDecoder.byte2int(buffer.get());
        this.interfaceName = AhDecoder.bytes2String(buffer, nameLength);
        this.alarmFlag = buffer.get();
        if(nameLength + 2 < length) {
            this.txErrorsValue = buffer.get();
            this.txAlarmThreshold = buffer.get();
            this.rxErrorsValue = buffer.get();
            this.rxAlarmThreshold = buffer.get();
            
            containsOptionals = true;
        }
        
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("Eth Interface Alarm Info: [");
        builder.append("Interface Name: " + this.interfaceName)
                .append(", Alarm flag: " + this.alarmFlag);
        if(containsOptionals) {
            builder.append(", Tx errors value: " + this.txErrorsValue + PERCENTAGE_MARK)
                    .append(", Tx Alarm threshold: " + this.txAlarmThreshold + PERCENTAGE_MARK)
                    .append(", Rx errors value: " + this.rxErrorsValue + PERCENTAGE_MARK)
                    .append(", Rx Alarm threshold: " + this.rxAlarmThreshold + PERCENTAGE_MARK);
        }
        builder.append("]");
        return null;
    }

}
