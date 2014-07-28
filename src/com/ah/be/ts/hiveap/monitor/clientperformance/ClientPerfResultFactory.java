package com.ah.be.ts.hiveap.monitor.clientperformance;

import java.nio.ByteBuffer;

import com.ah.be.ts.hiveap.monitor.clientperformance.result.ACSPNeignborResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.AbstractClientPerfResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.ClientAlarmResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.ClientResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.EthInterfaceAlarmResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.EthInterfaceResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.InterfaceFEAlarmResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.QoSAlarmResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.RadioInterfaceAlarmResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.RadioInterfaceResult;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.SystemResult;

public class ClientPerfResultFactory {
    public static AbstractClientPerfResult createResult(ByteBuffer buffer, int type) {
        AbstractClientPerfResult result = null;
        // get the enum object
        EClientPerfInfoType resultType = EClientPerfInfoType.getEnumByValue(type);
        switch (resultType) {
        case CLIENT:
            result = new ClientResult(buffer);
            break;
        case CLIENT_ALARM:
            result = new ClientAlarmResult(buffer);
            break;
        case RADIO_INTERFACE:
            result = new RadioInterfaceResult(buffer);
            break;
        case ACSP_NEIGHBOR:
            result = new ACSPNeignborResult(buffer);
            break;
        case RADIO_INTERFACE_ALARM:
            result = new RadioInterfaceAlarmResult(buffer);
            break;
        case ETH_INTERFACE:
            result = new EthInterfaceResult(buffer);
            break;
        case ETH_INTERFACE_ALARM:
            result = new EthInterfaceAlarmResult(buffer);
            break;
        case SYSTEM:
            result = new SystemResult(buffer);
            break;
        case INTERFACE_FE_ALARM:
            result = new InterfaceFEAlarmResult(buffer);
            break;
        case QOS_ALARM:
            result = new QoSAlarmResult(buffer);
            break;

        default:
            break;
        }
        
        // skip the reserve bytes
        if(null != result) {
            
            // get end position
            int endPosition = buffer.position();
            
            int realLength = endPosition - result.getStartPosition();
            final int length = result.getLength();
            if(realLength < length) {
                // for new fields
                buffer.position(buffer.position() + (length - realLength));
            }
        }
        
        return result;
    }
}
