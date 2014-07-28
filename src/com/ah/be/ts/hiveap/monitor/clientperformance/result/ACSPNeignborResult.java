package com.ah.be.ts.hiveap.monitor.clientperformance.result;

import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;

/**
 * <pre>
 * Name                                 Size(in octet)      Description
 * --------------------------------------------------------------------
 * ACSP interface name length           1
 * ACSP interface name                  Vary
 * ACSP neighbor mac address            6   
 * ACSP neighbor mode                   1   
 * ACSP neighbor ssid/hive length       1   
 * ACSP neighbor ssid/hive name         Vary    
 * ACSP neighbor channel                4   
 * ACSP neighbor rssi                   4   
 * ACSP neighbor aerohive ap            1                   Yes/no
 * ACSP neighbor total channel utility  1   
 * ACSP neighbor crc error rate         1   
 * ACSP neighbor station count          1   
 * ACSP neighbor channel string length  1   
 * ACSP neighbor channel string         Vary
 * --------------------------------------------------------------------
 * </pre>
 */
public class ACSPNeignborResult extends AbstractClientPerfResult {

    private String interfName;
    private String macAddress;
    private byte mode;
    private String ssidName;
    private long channel;
    private int rssi;
    private boolean isAerohiveAp;
    
    // optional fields
    private byte totalChannelUtil;
    private byte errorCRCRate;
    private byte stationCount;
    private String channelName;
    
    public ACSPNeignborResult(ByteBuffer buffer) {
        super(buffer);

        int dataLength = 0;
        
        int nameLength = AhDecoder.byte2int(buffer.get());
        dataLength += nameLength;
        this.interfName = AhDecoder.bytes2String(buffer, nameLength);
        this.macAddress = AhDecoder.bytes2hex(buffer, 6).toUpperCase();
        this.mode = buffer.get();
        nameLength = AhDecoder.byte2int(buffer.get());
        dataLength += nameLength;
        this.ssidName = AhDecoder.bytes2String(buffer, nameLength);
        this.channel = AhDecoder.int2long(buffer.getInt());
        this.rssi = buffer.getInt();
        this.isAerohiveAp = buffer.get() == 1 ? true : false;
        
        if(dataLength+18 < length) {
            this.totalChannelUtil = buffer.get();
            this.errorCRCRate = buffer.get();
            this.stationCount = buffer.get();
            nameLength = AhDecoder.byte2int(buffer.get());
            this.channelName = AhDecoder.bytes2String(buffer, nameLength);
            
            containsOptionals = true;
        }
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("ACSP Neighbor Info: [");
        builder.append("Interface Name: " + this.interfName)
                .append(", Mac Address: " + this.macAddress)
                .append(", Mode: " + getModeStr(this.mode))
                .append(", SSID/Hive Name: " + this.ssidName)
                .append(", Channel: " + this.channel)
                .append(", RSSI: " + this.rssi + " dBm")
                .append(", Aerohive AP: " + (this.isAerohiveAp ? STR_YES : STR_NO));
        if(containsOptionals) {
            builder.append(", Total Channel Utility: " + this.totalChannelUtil + "%")
                    .append(", CRC Error Rate: " + this.errorCRCRate + "%")
                    .append(", Station Count: " + this.stationCount)
                    .append(", Channel Width: " + this.channelName + "MHz");
        }
        builder.append("]");
        return builder.toString();
    }

}
