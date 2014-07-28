package com.ah.be.ts.hiveap.monitor.clientperformance.result;

import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;

public class ClientResult extends AbstractClientPerfResult {

    private String macAddress;
    private long upThroughput;
    private long downThroughput;
    private long txRate;
    private long rxRate;
    private int rssi;
    private short noiseFloor;
    
    // new fields from Dakar
    private short clientType;
    private long channel;
    private short clientHealthScore;
    private long maxPYHRate;
    
    /**
     * Field for diff the result whether the result is new from Dakar. 
     */
    private boolean isDakarVersion;
    
    /**
     * To adjust whether only container the macdress field
     */
    private boolean macOnly;

    public ClientResult(ByteBuffer buffer) {
        super(buffer);
        
        int startPosition = buffer.position();
        
        this.macAddress = AhDecoder.bytes2hex(buffer, 6).toUpperCase();
        macOnly = length == 6;
        if(!macOnly) {
            this.upThroughput = AhDecoder.int2long(buffer.getInt());
            this.downThroughput = AhDecoder.int2long(buffer.getInt());
            this.txRate = AhDecoder.int2long(buffer.getInt());
            this.rxRate = AhDecoder.int2long(buffer.getInt());
            this.rssi = buffer.getShort();
            this.noiseFloor = buffer.getShort();
            
            int endPosition = buffer.position();
            
            int realLength = endPosition - startPosition;
            if(realLength < length) {
                this.clientType = AhDecoder.byte2short(buffer.get());
                this.channel = AhDecoder.int2long(buffer.getInt());
                this.clientHealthScore = AhDecoder.byte2short(buffer.get());
                this.maxPYHRate = AhDecoder.int2long(buffer.getInt());
                
                isDakarVersion = true;
            }
            
        }
    
    }

    @Override
    public String getDescription() {
        if(macOnly) {
            return EMPTY_STR;
        }
        StringBuilder builder = new StringBuilder("Client Info: [");
        builder.append("Uplink Throughtput: " + convertDataRate2Str(this.upThroughput))
                .append(", Downlink Throughtput: " + convertDataRate2Str(this.downThroughput))
                .append(", Tx Rate: " + convertDataRate2Str(this.txRate))
                .append(", Rx Rate: " + convertDataRate2Str(this.rxRate))
                .append(", RSSI: " + this.rssi + " dBm")
                .append(", Noise floor: " + this.noiseFloor + " dBm");
        if(isDakarVersion) {
            builder.append(", Client Type:" + this.clientType)
                    .append(", Channel: " + this.channel)
                    .append(", Client Health Score: " + this.clientHealthScore);
                    //.append(", Max PHY rate: " + this.maxPYHRate);
        }
        builder.append("]");
        return builder.toString();
    }

    /*------------------ Getter/Setter ------------------*/
    public String getMacAddress() {
        return macAddress;
    }

    public boolean isMacOnly() {
        return macOnly;
    }
    
}
