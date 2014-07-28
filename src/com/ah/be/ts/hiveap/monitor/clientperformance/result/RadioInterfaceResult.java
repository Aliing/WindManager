package com.ah.be.ts.hiveap.monitor.clientperformance.result;

import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;

public class RadioInterfaceResult extends AbstractClientPerfResult {
    
    private String interfaceName;
    private int channelWidth;
    private int channelNum;
    private int txPower;
    private short txAggRate;
    
    // new fields from Dakar
    private boolean enableWMM;
    private boolean enableAPPUD;
    private boolean enableShortGuardInterval;
    // Access mode or dual mode
    private byte mode;
    private long bgscan;
    private long txFrameRate;
    private long txBytesRate;
    private long rxFrameRate;
    private long rxBytesRate;
    private short rxAggRate;
    private short subFrameRetryRates;
    private short retryRates;
    private long txDropCounter;
    private short channelTxUtil;
    private short channelRxUtil;
    private short interferenceUtil;
    private short totalUtil;
    private long errorCounterCRC;
    private long errorCounterPHY;
    private long radar;
    
    
    /**
     * Field for diff the result whether the result is new from Dakar. 
     */
    private boolean isDakarVersion;

    public RadioInterfaceResult(ByteBuffer buffer) {
        super(buffer);
        
        int startPosition = buffer.position();
        
        int infoLength = AhDecoder.byte2int(buffer.get());
        this.interfaceName = AhDecoder.bytes2String(buffer, infoLength).toUpperCase();
        this.channelWidth = AhDecoder.short2int(buffer.getShort());
        this.channelNum = AhDecoder.short2int(buffer.getShort());
        this.txPower = buffer.getInt();
        this.txAggRate = AhDecoder.byte2short(buffer.get());
        
        int endPosition = buffer.position();
        
        int realLength = endPosition - startPosition;
        if(realLength < length) {
            this.enableWMM = buffer.get() == 1 ? true : false;
            this.enableAPPUD = buffer.get() == 1 ? true : false;
            this.enableShortGuardInterval = buffer.get() == 1 ? true : false;
            this.mode = buffer.get();
            this.bgscan = AhDecoder.int2long(buffer.getInt());
            this.txFrameRate = AhDecoder.int2long(buffer.getInt());
            this.txBytesRate = AhDecoder.int2long(buffer.getInt());
            this.rxFrameRate = AhDecoder.int2long(buffer.getInt());
            this.rxBytesRate = AhDecoder.int2long(buffer.getInt());
            this.rxAggRate = buffer.get();
            this.subFrameRetryRates =buffer.get();
            this.retryRates = buffer.get();
            this.txDropCounter = AhDecoder.int2long(buffer.getInt());
            this.channelTxUtil = buffer.get();
            this.channelRxUtil = buffer.get();
            this.interferenceUtil = buffer.get();
            this.totalUtil = buffer.get();
            
            //FIXME
            this.errorCounterCRC = AhDecoder.int2long(buffer.getInt());
            this.errorCounterPHY = AhDecoder.int2long(buffer.getInt());
            
            this.radar = AhDecoder.int2long(buffer.getInt());
            
            isDakarVersion = true;
            
            endPosition = buffer.position();
            realLength = endPosition - startPosition;
            if(realLength < length) {
                // for new fields
                buffer.position(buffer.position() + (length - realLength));
            }
        }
    
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("Radio Interface Info: [")
                .append("Interface Name: " + this.interfaceName)
                .append(", Channel Width: " + this.channelWidth + "MHz")
                .append(", Channel Number: " + this.channelNum)
                .append(", Tx Power: " + this.txPower + " dBm")
                .append(", Tx Aggregation Rate: " + this.txAggRate + PERCENTAGE_MARK);
        if(isDakarVersion) {
            //builder.append(", WMM: " + (this.enableWMM ? STR_ENABLE : STR_DISABLE));
            builder.append(", AMPDU Frame Aggregation: " + (this.enableAPPUD ? STR_ENABLE : STR_DISABLE))
                    .append(", Short Guard Interval: " + (this.enableShortGuardInterval ? STR_ENABLE : STR_DISABLE))
                    .append(", Radio Mode: " + getModeStr(this.mode))
                    .append(", Bgscan Interval: " + this.bgscan + " min")
                    .append(", Tx Frame Rate: " + this.txFrameRate + "pbs")
                    .append(", Tx Bytes Rate: " + this.txBytesRate * 8 + "bps")
                    .append(", Rx Frame Rate: " + this.rxFrameRate + "pbs")
                    .append(", Rx Bytes Rate: " + this.rxBytesRate * 8 + "bps")
                    .append(", Rx Aggregation Rate: " + this.rxAggRate + PERCENTAGE_MARK)
                    .append(", Subframe Retry Rates: " + this.subFrameRetryRates + PERCENTAGE_MARK)
                    .append(", Retry Rates: " + this.retryRates + PERCENTAGE_MARK)
                    .append(", Tx Drop Counter: " + this.txDropCounter)
                    .append(", Channel Tx Utilization: " + this.channelTxUtil + PERCENTAGE_MARK)
                    .append(", Channel Rx Utilization: " + this.channelRxUtil + PERCENTAGE_MARK)
                    .append(", Interference Utilization: " + this.interferenceUtil + PERCENTAGE_MARK)
                    .append(", Total Utilization: " + this.totalUtil + PERCENTAGE_MARK)
                    .append(", CRC Error Counter: " + this.errorCounterCRC)
                    .append(", PHY Error Counter: " + this.errorCounterPHY)
                    .append(", Radar: " + this.radar);
        }
        builder.append("]");
        return builder.toString();
    }
    
}
