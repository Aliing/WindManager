package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.mgmt.QueryUtil;

public class RestoreIDMCustomer {

    public static final String tableName = "cloud_auth_customer";

    public static boolean restoreCustomers() {

        try {
            long start = System.currentTimeMillis();

            List<CloudAuthCustomer> allCustomers = getAllCustomers();

            if (null == allCustomers || allCustomers.isEmpty()) {
                AhRestoreDBTools.logRestoreMsg("IDM Customer is null or empty.");
            } else {
                AhRestoreDBTools.logRestoreMsg("Restore IDM Customer size:" + allCustomers.size());
                QueryUtil.restoreBulkCreateBos(allCustomers);
            }
            long end = System.currentTimeMillis();
            AhRestoreDBTools.logRestoreMsg("Restore IDM Customer completely. Count:"
                    + (null == allCustomers ? "0" : allCustomers.size()) + ", cost:" + (end - start) + " ms.");
        } catch (Exception e) {
            AhRestoreDBTools.logRestoreMsg("Restore IDM Customer error.", e);
            return false;
        }
        return true;

    }

    private static List<CloudAuthCustomer> getAllCustomers() throws AhRestoreException,
            AhRestoreColNotExistException {
        AhRestoreGetXML xmlParser = new AhRestoreGetXML();

        /**
         * Check validation of cloud_auth_customer.xml
         */
        boolean restoreRet = xmlParser.readXMLFile(tableName);
        if (!restoreRet) {
            return null;
        }

        /**
         * No one row data stored in cloud_auth_customer table is allowed
         */
        int rowCount = xmlParser.getRowCount();
        boolean isColPresent;
        String colName;

        List<CloudAuthCustomer> customers = new ArrayList<CloudAuthCustomer>();
        CloudAuthCustomer customer;

        for (int i = 0; i < rowCount; i++) {
            customer = new CloudAuthCustomer();

            /**
             * Set id
             */
            colName = "id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            if (!isColPresent) {
            	BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'cloud_auth_customer' data be lost, cause: 'id' column is not exist.");
                /**
                 * The id column must be exist in the table.
                 */
                continue;
            }

            /**
             * Set userName
             */
            colName = "userName";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String userName = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (!isIllegalString(userName)) {
                customer.setUserName(userName);
            }

            /**
             * Set password
             */
            colName = "password";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String password = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (!isIllegalString(password)) {
                customer.setPassword(password);
            }

            /**
             * Set customerId
             */
            colName = "customerId";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String customerId = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (!isIllegalString(customerId)) {
                customer.setCustomerId(customerId);
            }
            
            // [HMOL Only] ::start::
            /**
             * Set tried
             */
            colName = "tried";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String tried = isColPresent ? xmlParser.getColVal(i, colName) : "false";
            customer.setTried(AhRestoreCommons.convertStringToBoolean(tried));
            
            /**
             * Set idmanagerId
             */
            colName = "idmanagerId";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String idmanagerId = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (!isIllegalString(idmanagerId)) {
                customer.setIdmanagerId(idmanagerId);
            }
            
            /**
             * Set trialSettingsText
             */
            colName = "trialSettingsText";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String trialSettingsText = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (!isIllegalString(trialSettingsText)) {
                customer.setTrialSettingsText(trialSettingsText);
            }
            // [HMOL Only] ::end::
            
            // [HM On-Premise Only] ::start::
            /**
             * Set tried
             */
            colName = "usingProxy";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String usingProxy = isColPresent ? xmlParser.getColVal(i, colName) : "false";
            customer.setUsingProxy(AhRestoreCommons.convertStringToBoolean(usingProxy));
         // [HM On-Premise Only] ::start::
            
            /**
             * Set owner
             */
            colName = "owner";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
                    colName)) : 1;

            if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
            	BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'cloud_auth_customer' data be lost, cause: 'owner' column is not available.");
                continue;
            }

            customer.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

            customers.add(customer);
        }
        return customers;
    }
    
    private static boolean isIllegalString(String str) {
        return StringUtils.isBlank(str) 
                || str.trim().equalsIgnoreCase("null")
                || str.trim().toLowerCase().startsWith("_null");
    }
}
