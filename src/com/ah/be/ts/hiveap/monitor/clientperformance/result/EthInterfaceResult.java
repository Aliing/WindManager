package com.ah.be.ts.hiveap.monitor.clientperformance.result;

import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;

/**
 * <pre>
 * Name                     Size(in octet)      Description
 * --------------------------------------------------------
 * Interface name length    1   
 * Interface name           Vary    
 * Eth link speed           2   
 * Eth link duplex mode     1   
 * Tx frames rate           4   
 * Rx frames rate           4   
 * Tx bytes rate            4   
 * Rx bytes rate            4
 * --------------------------------------------------------
 * </pre>
 */
public class EthInterfaceResult extends AbstractClientPerfResult {

    private String interfaceName;
    private short ethLinkSpeed;
    private byte ethLinkDuplexMode;
    private long txFramesRate;
    private long rxFramesRate;
    private long txBytesRate;
    private long rxBytesRate;
    
    public EthInterfaceResult(ByteBuffer buffer) {
        super(buffer);

        int nameLength = AhDecoder.byte2int(buffer.get());
        this.interfaceName = AhDecoder.bytes2String(buffer, nameLength);
        this.ethLinkSpeed = buffer.getShort();
        this.ethLinkDuplexMode = buffer.get();
        this.txFramesRate = AhDecoder.int2long(buffer.getInt());
        this.rxFramesRate = AhDecoder.int2long(buffer.getInt());
        this.txBytesRate = AhDecoder.int2long(buffer.getInt());
        this.rxBytesRate = AhDecoder.int2long(buffer.getInt());
        
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("Eth interface Info: [");
        builder.append("Interface Name: " + this.interfaceName)
                .append(", Eth link speed: " + this.ethLinkSpeed)
                .append(", Eth link duplex mode: " + this.ethLinkDuplexMode)
                .append(", Tx frames rate: " + this.txFramesRate)
                .append(", Rx frames rate: " + this.rxFramesRate)
                .append(", Tx bytes rate: " + this.txBytesRate)
                .append(", Rx bytes rate: " + this.rxBytesRate);
        builder.append("]");
        return builder.toString();
    }

}
