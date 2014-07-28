package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.GuestAnalyticsInfo;
import com.ah.bo.mgmt.QueryUtil;

public class RestoreGuestAnalytics {
    public static final String tableName = "guest_analytics_info";

    public static boolean restoreGuestAnalytics() {

        try {
            long start = System.currentTimeMillis();

            List<GuestAnalyticsInfo> infos = getGuestAnalyticsInfo();

            if (null == infos || infos.isEmpty()) {
                AhRestoreDBTools.logRestoreMsg("GuestAnalytics is null or empty.");
            } else {
                AhRestoreDBTools.logRestoreMsg("Restore GuestAnalytics size:" + infos.size());
                QueryUtil.restoreBulkCreateBos(infos);
            }
            long end = System.currentTimeMillis();
            AhRestoreDBTools.logRestoreMsg("Restore GuestAnalytics completely. Count:"
                    + (null == infos ? "0" : infos.size()) + ", cost:" + (end - start) + " ms.");
        } catch (Exception e) {
            AhRestoreDBTools.logRestoreMsg("Restore IDM Customer error.", e);
            return false;
        }
        return true;

    }

    private static List<GuestAnalyticsInfo> getGuestAnalyticsInfo() throws AhRestoreException, AhRestoreColNotExistException {
        AhRestoreGetXML xmlParser = new AhRestoreGetXML();

        /**
         * Check validation of guest_analytics_info.xml
         */
        boolean restoreRet = xmlParser.readXMLFile(tableName);
        if (!restoreRet) {
            return null;
        }

        /**
         * No one row data stored in guest_analytics_info table is allowed
         */
        int rowCount = xmlParser.getRowCount();
        boolean isColPresent;
        String colName;

        List<GuestAnalyticsInfo> infos = new ArrayList<GuestAnalyticsInfo>();
        GuestAnalyticsInfo info;

        for (int i = 0; i < rowCount; i++) {
            info = new GuestAnalyticsInfo();

            /**
             * Set id
             */
            colName = "id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            if (!isColPresent) {
                BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'guest_analytics_info' data be lost, cause: 'id' column is not exist.");
                /**
                 * The id column must be exist in the table.
                 */
                continue;
            }

            /**
             * Set apiKey
             */
            colName = "apiKey";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String apiKey = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (!isIllegalString(apiKey)) {
                info.setApiKey(apiKey);
            }
            
            /**
             * Set apiNonce
             */
            colName = "apiNonce";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String apiNonce = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (!isIllegalString(apiNonce)) {
                info.setApiNonce(apiNonce);
            }
            
            /**
             * Set enabled
             */
            colName = "enabled";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String tried = isColPresent ? xmlParser.getColVal(i, colName) : "false";
            info.setEnabled(AhRestoreCommons.convertStringToBoolean(tried));
            
            
            /**
             * Set owner
             */
            colName = "owner";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
                    colName)) : 1;

            if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
                BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'guest_analytics_info' data be lost, cause: 'owner' column is not available.");
                continue;
            }

            info.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

            infos.add(info);
        }
        return infos;
    }
    
    private static boolean isIllegalString(String str) {
        return StringUtils.isBlank(str) 
                || str.trim().equalsIgnoreCase("null")
                || str.trim().toLowerCase().startsWith("_null");
    }
}
