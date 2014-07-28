package com.ah.be.cloudauth.result;

import org.apache.commons.lang.StringUtils;

import com.ah.util.MgrUtil;

public enum UpdateCAStatus {
    NOUPDATE(0),
    SUCCESS(1),
    INIT_EVENT_ERR(2),
    SENT_EVENT_ERR(3),
    RESPONSE_EVENTE_ERR(4),
    CSR_CREATE_ERR(5),
    CSR_DECODE_ERR(6),
    CSR_VERIFY_ERR(7),
    CSR_FORMAT_ERR(8),
    CSR_STATUS_ERR(9),
    HTTPS_POST_CLOUDAUTH_ERR(10),
    HTTPS_CERT_NOTEXIST_ERR(11),
    HTTPS_CERT_MISMATCH_ERR(12),
    HTTPS_CERT_CONTENT_ERR(13),
    HTTPS_CERT_FILE_IO_ERR(14),
    JSON_INIT_ERR(19),
    JSON_PARSE_ERR(20),
    CUSTOMER_ID_EMPTY_ERR(21),
    CUSTOMER_ID_UPDATE_ERR(22),
    ID_MANAGER_NOT_AVAILABLE(23),
    DEVICE_NULL_ERR(30),
    REST_INIT_ERR(40),
    CONFIG_LOAD_ERR(50),
    IDM_SERVER_ERR(999),
    UNKNOW(-1) {
        @Override
        public String getMessage() {
            return "Unknow.";
        }
    };

    private int code;

    private UpdateCAStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        final String msg = MgrUtil.getUserMessage("cloudauth.update.result." + this.getCode());
        return StringUtils.isBlank(msg) ? "Unknow." : msg;
    }

}
