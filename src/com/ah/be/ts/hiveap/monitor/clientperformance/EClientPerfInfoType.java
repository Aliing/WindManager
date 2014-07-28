package com.ah.be.ts.hiveap.monitor.clientperformance;


public enum EClientPerfInfoType {

    UNKNOW(0), 
    CLIENT(1), RADIO_INTERFACE(2), ETH_INTERFACE(3), SYSTEM(4), CLIENT_ALARM(5), RADIO_INTERFACE_ALARM(6),
    ETH_INTERFACE_ALARM(7), QOS_ALARM(8), INTERFACE_FE_ALARM(9), ACSP_NEIGHBOR(10);
    
    private int value;
    private EClientPerfInfoType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

    public static EClientPerfInfoType getEnumByValue(int value) {
        String name = "UNKNOW";
        for (EClientPerfInfoType type : EClientPerfInfoType.values()) {
            if(type.value == value) {
                name = type.toString();
                break;
            }
        }
        return EClientPerfInfoType.valueOf(name);
    }

}
