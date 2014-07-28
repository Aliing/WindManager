package com.ah.be.admin.adminOperateImpl;

import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.license.AeroLicenseTimer;
import com.ah.be.log.BeLogTools;
import com.ah.bo.admin.AcmEntitleKeyHistoryInfo;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.admin.UserRegInfoForLs;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class AhCleanLicenseHistory {
	
	private static final Tracer log = new Tracer(AhCleanLicenseHistory.class.getSimpleName());
	
	public AhCleanLicenseHistory(){}
	
	public static void main(String[] args)
	{
		try
		{
			log.warn("remove all license information begin");
			// license information
		    QueryUtil.bulkRemoveBos(LicenseHistoryInfo.class, null, null, null);
		    
		    // order key information
		    QueryUtil.removeBos(OrderHistoryInfo.class, null, null, null);
		    QueryUtil.bulkRemoveBos(DomainOrderKeyInfo.class, null, null, null);
		    QueryUtil.bulkRemoveBos(UserRegInfoForLs.class, null, null, null);
		    QueryUtil.bulkRemoveBos(AcmEntitleKeyHistoryInfo.class, null, null, null);
		    
		    // remove the values from memory
		    HmBeLicenseUtil.VHM_ORDERKEY_INFO.clear();
		    HmBeLicenseUtil.LICENSE_TIMER_OBJ.stopAllLicenseTimer();
		    HmBeLicenseUtil.LICENSE_TIMER_OBJ = new AeroLicenseTimer();
		    log.warn("remove all license information end");
		}
		catch(Exception ex)
		{
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
	}

}
