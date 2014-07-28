package com.ah.be.ts.hiveap.monitor.clientperformance.result;

import java.nio.ByteBuffer;

import com.ah.bo.hiveap.AhInterface;
import com.ah.util.coder.AhDecoder;

public abstract class AbstractClientPerfResult {
    
    /**
     * The length of per client performance result 
     */
    protected int length;
    
    /**
     * The Flag for adjust whether the optional fields exist. 
     */
    protected boolean containsOptionals;

    /**
     * The start position of the data (exclude the data size).
     */
    private int startPosition;
    
    /**
     * Disable the default constructor
     */
    private AbstractClientPerfResult() {
    }
    
    /**
     * Initialize the constructor by {@link ByteBuffer}. (The length of result is set.)
     * @param buffer
     */
    public AbstractClientPerfResult(ByteBuffer buffer){
        this();
        length = AhDecoder.short2int(buffer.getShort());
        // get start position
        startPosition = buffer.position();
    }
    
    /**
     * Collect the information from the client performance result 
     * 
     * @return String
     */
    public abstract String getDescription();
    
    /*--------------------------- Constant Fields ---------------------------*/
    protected static final String STR_DISABLE = "Disable";
    protected static final String STR_ENABLE = "Enable";
    protected static final String STR_ON = "On";
    protected static final String STR_OFF = "Off";
    protected static final String STR_YES = "Yes";
    protected static final String STR_NO = "No";
    
    protected static final String PERCENTAGE_MARK = "%";
    protected static final String KB_MARK = "KB";
    
    protected static final String EMPTY_STR = "";
    
    /*--------------------------- Common Methods ---------------------------*/
    protected String convertDataRate2Str(long value) {
        String desc;
        final int KBPS = 1000, MBPS = KBPS * 1000, GBPS = MBPS * 1000;
        if(value < KBPS) {
            desc = value + " kbps";
        } else if (value < MBPS) {
            desc = value/KBPS + " mbps";
        } else if (value < GBPS) {
            desc = value/MBPS + " gbps";
        } else {
            desc = value/GBPS + "gbps";
        }
        return desc;
    }
    
    protected String getModeStr(byte mode) {
        String modeStr = ""; 
        switch (mode) {
        case AhInterface.OPERATION_MODE_ACCESS:
            modeStr = "Access";
            break;
        case AhInterface.OPERATION_MODE_BACKHAUL:
            modeStr = "Backhaul";
            break;
        case AhInterface.OPERATION_MODE_BRIDGE:
            modeStr = "Bridge";
            break;
        case AhInterface.OPERATION_MODE_DUAL:
            modeStr = "Dual";
            break;

        default:
            break;
        }
        return modeStr;
    }
    
    /*--------------------------- Getters/Setters ---------------------------*/
    public int getLength() {
        return length;
    }

    public int getStartPosition() {
        return startPosition;
    }
    
}
