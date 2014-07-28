package com.ah.be.cloudauth;

import com.ah.be.communication.event.BeRadSecCertCreationResultEvent;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;

public interface ICloudAuthCertMgmt<T> {
    
    /**
     * Update the certification for the device which is set as a RadSec proxy. 
     * 
     * @author Yunzhi Lin
     * - Time: Mar 29, 2012 4:17:01 PM
     * @param device {@link HiveAp}, the device which is set as a RadSec proxy
     * @return The update result which should contains: success/failure, message (external error message) and the path of certification 
     */
    T updateCertification(HiveAp device);
    
    /**
     * Force update the certification whatever the file is exist in the device. 
     * @author Yunzhi Lin
     * - Time: Mar 29, 2012 4:21:39 PM
     * @param device {@link HiveAp}, the device which is set as a RadSec proxy
     * @return The update result which should contains: success/failure, message (external error message) and the path of certification
     */
    T forceUpdateCertification(HiveAp device);
    
    /**
     * Device send request to automatic renew the certification request to HiveManager,
     * HiveManager will pass the request to IDM Certification Server.
     * 
     * @author Yunzhi Lin
     * - Time: Aug 2, 2012 4:14:12 PM
     * @param device {@link HiveAp}, the device which is set as a RadSec proxy
     * @param response The renew certification cert request from device.
     * @return The update result which should contains: success/failure, message (external error message) and the path of certification
     */
    T renewCertificationByRequest(HiveAp device, BeRadSecCertCreationResultEvent response);
    
    /**
     * Check whether is the customer ID exist by current domain
     * <ul>
     * <li>For HMOL, the customer ID mapping with per vHM</li>
     * <li>For On-Premise HM, the customer ID should be global</li>
     * </ul>
     * 
     * @author Yunzhi Lin
     * - Time: Aug 6, 2012 2:13:48 PM
     * @param domainId domain ID
     * @return <code>True</code> or <code>False</code>
     */
    @Deprecated
    boolean isCustomerIdExist(Long domainId);
    
    /**
     * Get the IDM RadSec configuration according by the flag under IDM Manager Settings.
     * <ul>
     * <li>For HMOL, per vHM can choose to use standard/beta IDM service</li>
     * <li>For On-Premise HM, administrator can choose to use standard/beta IDM service</li>
     * </ul>
     * 
     * @author Yunzhi Lin - Time: Aug 12, 2012 1:14:57 PM
     * @return {@link IDMConfig} or <b>NULL</b>
     */
    IDMConfig getRadSecConfig(Long domainId);
    
    /**
     * Retrieve Customer ID from Portal for the HMOL
     * 
     * @author Yunzhi Lin
     * - Time: Aug 15, 2012 2:53:46 PM
     * @param domainId
     */
    @Deprecated
    void retrieveCustomerIdFromPortal(Long domainId);
    
    /**
     * Check whether is the ID Manager available for current domain
     * <ul>
     * <li>For HMOL, the ID Manager mapping with per vHM</li>
     * <li>For On-Premise HM, the ID Manager should be global</li>
     * </ul>
     * 
     * @author Yunzhi Lin
     * - Time: May 11, 2013 9:21:03 PM
     * @param domainId domain ID
     * @return <code>True</code> or <code>False</code>
     */
    boolean isIDManagerEnabled(Long domainId);
    
    /**
     * <b>[For HMOL Only]</b><br>
     * Check whether is allowed current user to request a ID Manager trial.<br>
     * 
     * 
     * @author Yunzhi Lin
     * - Time: May 12, 2013 3:16:01 PM
     * @param domainId
     * @return
     */
    boolean isAllowedTrial(Long domainId);
    
    /**
     * <b>[For HMOL Only]</b><br>
     * Refresh the status from Portal by the email address, update the customer ID and the idmanager ID.<br>
     * It will be called when navigate to "Configuration" tab first time for the session and recalled when new/edit the SSID page.<br>
     * And the response should be stored into session and update the database if necessary.
     * Note: not support for the switch domain situation because it cannot identify the email address.
     * 
     * @author Yunzhi Lin
     * - Time: May 12, 2013 10:24:10 AM
     * @param domainId
     * @param user {@link HmUser}
     * @return <code>True</code> or <code>False</code>
     */
    boolean refreshIDManagerStatus(Long domainId, HmUser user);
}
