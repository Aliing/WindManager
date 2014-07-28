package com.ah.be.ts.hiveap.monitor.clientperformance.result;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.util.coder.AhDecoder;

/**
 * Name                     Size(in octet)      Description
 * --------------------------------------------------------
 * Mac address              6   
 * Alarm flag               1   
 * (optional)Current Cls rate valud 1*8                 short array
 * (optional)Current Cls threshold  1*8 
 * (optional)Current  Plc rate value    1*8 
 * (optional)Current  Plc threshold 1*8 
 * (optional)Current sch rate value 1*8 
 * (optional)Current sch threshold  1*8
 * -------------------------------------------------------- 
 */
public class QoSAlarmResult extends AbstractClientPerfResult {

    private String macAddress;
    private boolean alarmFlag;
    // optional fields
    private List<Byte> currentClsValue = new ArrayList<Byte>();
    private List<Byte> currentClsThreshold = new ArrayList<Byte>();
    private List<Byte> currentPlcValue = new ArrayList<Byte>();
    private List<Byte> currentPlcThreshold = new ArrayList<Byte>();
    private List<Byte> currentSchValue = new ArrayList<Byte>();
    private List<Byte> currentSchThreshold = new ArrayList<Byte>();
    
    private static final int QOS_LEVEL_COUNT = 8;
    
    public QoSAlarmResult(ByteBuffer buffer) {
        super(buffer);
        
        this.macAddress = AhDecoder.bytes2hex(buffer, 6).toUpperCase();
        this.alarmFlag = buffer.get() == 1 ? true : false;
        
        if(length > 7) {
            for (int i = 0; i < QOS_LEVEL_COUNT; i++) {
                // CLS
                this.currentClsValue.add(buffer.get());
                this.currentClsThreshold.add(buffer.get());
                // PLC
                this.currentPlcValue.add(buffer.get());
                this.currentPlcThreshold.add(buffer.get());
                // SCH
                this.currentSchValue.add(buffer.get());
                this.currentSchThreshold.add(buffer.get());
            }
            
            containsOptionals = true;
        }
        
        
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("Oos Alarm Info: [");
        builder.append("Mac address: " + this.macAddress)
                .append(", Alarm flag: " + (this.alarmFlag ? STR_ON : STR_OFF));
        if (containsOptionals) {
            builder.append(", Current Cls Rate/Threshold: " + converToArrayValues(this.currentClsValue, this.currentClsThreshold))
            .append(", Current Plc Rate/Threshold: " + converToArrayValues(this.currentPlcValue, this.currentPlcThreshold))
            .append(", Current Sch Rate/Threshold: " + converToArrayValues(this.currentSchValue, this.currentSchThreshold));
        }
        builder.append("]");
        return builder.toString();
    }

    private String converToArrayValues(List<Byte> values, List<Byte> thresholds) {
        String result = "";
        if (!values.isEmpty() && !thresholds.isEmpty()) {
            if(values.size() != thresholds.size()) {
                return result;
            }
            result = "{";
            for (int i = 0; i < values.size(); i++) {
                String pairValue = i + ":" + values.get(i) + PERCENTAGE_MARK+"/"+thresholds.get(i) + PERCENTAGE_MARK;
                if (i == 0) {
                    result += pairValue;
                } else {
                    result += ", " + pairValue;
                }
            }
            result += "}";
        }
        return result;
    }
}
