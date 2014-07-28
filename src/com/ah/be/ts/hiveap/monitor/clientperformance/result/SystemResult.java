package com.ah.be.ts.hiveap.monitor.clientperformance.result;

import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;

/**
 * <pre>
 * Name                     Size(in octet)      Description
 * --------------------------------------------------------
 * Total CPU utilization        1   
 * User CPU utilization         1   
 * System CPU utilization       1   
 * Total memory                 4                   Bytes
 * Used memory                  4   
 * Total free memory            4   
 * Kernel free memory           4
 * --------------------------------------------------------
 * </pre>
 */
public class SystemResult extends AbstractClientPerfResult {

    private short totalCPUUtil;
    private short userCPUUtil;
    private short sysCPUUtil;
    private long totalMem;
    private long usedMem;
    private long totalFreeMem;
    private long kernelFreeMem;
    
    public SystemResult(ByteBuffer buffer) {
        super(buffer);
        
        this.totalCPUUtil = buffer.get();
        this.userCPUUtil = buffer.get();
        this.sysCPUUtil = buffer.get();
        this.totalMem = AhDecoder.int2long(buffer.getInt());
        this.usedMem = AhDecoder.int2long(buffer.getInt());
        this.totalFreeMem = AhDecoder.int2long(buffer.getInt());
        this.kernelFreeMem = AhDecoder.int2long(buffer.getInt());
        
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("System Info: [");
        builder.append("Total CPU utilization: " + this.totalCPUUtil + PERCENTAGE_MARK)
                .append(", User CPU utilization: " + this.userCPUUtil + PERCENTAGE_MARK)
                .append(", System CPU utilization: " + this.sysCPUUtil + PERCENTAGE_MARK)
                .append(", Total memory: " + this.totalMem + KB_MARK)
                .append(", Used memory: " + this.usedMem + KB_MARK)
                .append(", Total free memory: " + this.totalFreeMem + KB_MARK)
                .append(", Kernel free memory: " + this.kernelFreeMem + KB_MARK);
        builder.append("]");
        return builder.toString();
    }

}
