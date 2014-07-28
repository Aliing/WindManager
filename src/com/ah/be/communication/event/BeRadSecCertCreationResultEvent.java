package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;


public class BeRadSecCertCreationResultEvent extends BeCapwapClientResultEvent {

    private static final long serialVersionUID = 1L;
    
    private boolean needCreate;
    
    private boolean exist;
    
    private boolean createError;
    
    private byte[] csrContent;

    public BeRadSecCertCreationResultEvent() {
        super();
        resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_CERT_CREATION;
    }

    @Override
    protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
        try {
            super.parsePacket(data);

            ByteBuffer buf = ByteBuffer.wrap(resultData);
            byte respCode = buf.get();
            // get the response code: 
            //  0 imply need to get new certification from CA server;
            //  1 imply the certification is already exist.
            //  2 imply create the CSR error.
            switch (respCode) {
            case 0:
                needCreate = true;
                break;
            case 1:
                exist = true;
                break;
            case 2:
                createError = true;
                break;

            default:
                break;
            }
            if(needCreate) {
                int length = buf.getInt();
                if(length > 0) {
                    csrContent = new byte[length];
                    buf.get(csrContent, 0, length);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new BeCommunicationDecodeException(
                    "BeRadSecCertCreationResultEvent.parsePacket() catch exception", e);
        }
    }

    public boolean isNeedCreate() {
        return needCreate;
    }

    public boolean isExist() {
        return exist;
    }

    public boolean isCreateError() {
        return createError;
    }

    public byte[] getCsrContent() {
        return csrContent;
    }

    public void setCsrContent(byte[] csrContent) {
        this.csrContent = csrContent;
    }
    
}
