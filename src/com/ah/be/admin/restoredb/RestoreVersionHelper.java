package com.ah.be.admin.restoredb;

import java.io.File;

import com.ah.be.common.NmsUtil;

public class RestoreVersionHelper {

    private static final String VERSION_FILE_PATH = AhRestoreDBTools.HM_XML_TABLE_PATH
            + File.separatorChar + ".." + File.separatorChar + "hivemanager.ver";

    public static boolean isRestoreBefore(String version) {
        return NmsUtil.compareSoftwareVersion(NmsUtil.getHiveOSVersion(NmsUtil
                .getVersionInfo(VERSION_FILE_PATH)), version) < 0;
    }

    public static boolean isRestoreEquals(String version) {
        return NmsUtil.compareSoftwareVersion(NmsUtil.getHiveOSVersion(NmsUtil
                .getVersionInfo(VERSION_FILE_PATH)), version) == 0;
    }

    public static boolean isRestoreAfter(String version) {
        return NmsUtil.compareSoftwareVersion(NmsUtil.getHiveOSVersion(NmsUtil
                .getVersionInfo(VERSION_FILE_PATH)), version) > 0;
    }

    public static boolean isRestoreBetween(String minVersion, String maxVersion) {
        return NmsUtil.compareSoftwareVersion(NmsUtil.getHiveOSVersion(NmsUtil
                .getVersionInfo(VERSION_FILE_PATH)), minVersion) > 0
                && 
                NmsUtil.compareSoftwareVersion(NmsUtil.getHiveOSVersion(NmsUtil
                        .getVersionInfo(VERSION_FILE_PATH)), maxVersion) < 0;
    }
}
