package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;

public class BeRadSecCertCreationEvent extends BeCapwapClientEvent {

    private static final long serialVersionUID = 1L;
    
    public static final byte UPDATE_NOT_OVERRIDE = 0;
    public static final byte UPDATE_OVERRIDE = 1;
    
    private byte createFlag = UPDATE_NOT_OVERRIDE;

    public BeRadSecCertCreationEvent() {
        super();
        queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_CERT_CREATION;
    }
    
    /**
     * build event data to packet message
     * 
     * @return BeCommunicationMessageData
     * @throws BeCommunicationEncodeException -
     */
    public byte[] buildPacket() throws BeCommunicationEncodeException {
        if (apMac == null) {
            throw new BeCommunicationEncodeException("ApMac is a necessary field!");
        }

        if (sequenceNum <= 0) {
            throw new BeCommunicationEncodeException("sequenceNum is a necessary field!");
        }

        try {
            /**
             * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
             * query's length = 6 + 11 + 1
             */
            int apIdentifierLen = 7 + apMac.length();
            int queryLen = 18;
            int bufLength = apIdentifierLen + queryLen;
            ByteBuffer buf = ByteBuffer.allocate(bufLength);
            // set value
            buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
            buf.putInt(apIdentifierLen - 6);
            buf.put((byte) apMac.length());
            buf.put(apMac.getBytes());
            buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
            buf.putInt(11 + 1); // 2+4+1+4+1
            buf.putShort(queryType);
            buf.putInt(sequenceNum);
            buf.put(flag);
            buf.putInt(1); // data length
            buf.put(createFlag);
            setPacket(buf.array());
            return buf.array();
        } catch (Exception e) {
            throw new BeCommunicationEncodeException(
                    "BeRadSecCertCreation.buildPacket() catch exception", e);
        }
    }

    /**
     * parse packet message to event data
     * 
     * @param data -
     * @throws BeCommunicationDecodeException -
     */
    protected void parsePacket(byte[] data)
            throws BeCommunicationDecodeException {
        super.parsePacket(data);
    }

    public byte getCreateFlag() {
        return createFlag;
    }

    public void setCreateFlag(byte createFlag) {
        this.createFlag = createFlag;
    }

}
