package com.ah.be.ts.hiveap.monitor.clientperformance.result;

import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;

/**
 * <pre>
 * Name                         Size(in octet)      Description
 * ------------------------------------------------------------
 * Interface name length        1   
 * Interface name               Vary    
 * Alarm flag                   1   
 * (optional)Wifi radio tx drop count
 *      :current tx_drops rate  1   
 * (optional)Wifi radio tx drop count
 *      :alarm threshold        1
 * ------------------------------------------------------------ 
 * </pre>
 */
public class RadioInterfaceAlarmResult extends AbstractClientPerfResult {
    
    private static final int MAX_SIZE_OF_FILEDS = 4;

    private String interfaceName;
    private byte alarmFlag;
    
    // optional fields
    private int currentTxDropsRate;
    private int alarmThreshold;
    
    public RadioInterfaceAlarmResult(ByteBuffer buffer) {
        super(buffer);

        int nameLength = AhDecoder.byte2int(buffer.get());
        this.interfaceName = AhDecoder.bytes2String(buffer, nameLength);
        this.alarmFlag = buffer.get();
        
        if(length == MAX_SIZE_OF_FILEDS + nameLength) {
            this.currentTxDropsRate = buffer.get();
            this.alarmThreshold = buffer.get();
            
            containsOptionals = true;
        }
        
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("Radio Interface Alarm Info: [");
        builder.append("Interface Name: " + this.interfaceName)
                .append(", Alarm flag: " + this.alarmFlag);
        if (containsOptionals) {
            builder.append(", Wifi radio tx drop count-current tx drops rate: " + this.currentTxDropsRate + PERCENTAGE_MARK)
                    .append(", Wifi radio tx drop count-alarm threshold: " + this.alarmThreshold + PERCENTAGE_MARK);
        }
        builder.append("]");
        return builder.toString();
    }

}
