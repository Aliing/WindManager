package com.ah.util.userprofile.attr;

import com.ah.util.MgrUtil;

public interface UserProfileType {
    enum UserProfileOnSSID implements UserProfileType {
        DEFAULT {
            @Override
            public String getTypeName() {
                return MgrUtil.getMessageString("config.v2.select.user.profile.popup.tab.default");
            }
        },
        SELFREG {
            @Override
            public String getTypeName() {
                return MgrUtil.getMessageString("config.configTemplate.wizard.step4.subtitle1");
            }
        },
        AUTHENTICATION {
            @Override
            public String getTypeName() {
                return MgrUtil.getMessageString("config.v2.select.user.profile.popup.tab.auth");
            }
        },
        GUEST {
            @Override
            public String getTypeName() {
                return MgrUtil.getMessageString("glasgow_10.config.v2.select.user.profile.popup.tab.guest");
            }
        };

        public abstract String getTypeName();
    }
    enum UserProfileOnPort implements UserProfileType{
        DEFAULT {
            @Override
            public String getTypeName() {
                return MgrUtil.getMessageString("config.v2.select.user.profile.popup.tab.default");
            }
        },
        SELFREG {
            @Override
            public String getTypeName() {
                return MgrUtil.getMessageString("config.configTemplate.wizard.step4.subtitle1");
            }
        },
        GUEST {
            @Override
            public String getTypeName() {
                return MgrUtil.getMessageString("glasgow_10.config.v2.select.user.profile.popup.tab.guest");
            }
        },
        AUTHOK {
            @Override
            public String getTypeName() {
                return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authok");
            }
        },
        AUTHOK_VOICE {
            @Override
            public String getTypeName() {
                return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authok")
                        + " (Voice)";
            }
        },
        AUTHOK_DATA {
            @Override
            public String getTypeName() {
                return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authok")
                        + " (Data)";
            }
        },
        AUTHFAIL {
            @Override
            public String getTypeName() {
                return MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authfail");
            }
        };

        public abstract String getTypeName();
    }
    String getTypeName();
}
