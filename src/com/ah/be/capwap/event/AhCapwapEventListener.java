/**
 *@filename		AhCapwapEventListener.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap.event;

// java import
import java.io.Serializable;

// aerohive import
import com.ah.be.capwap.event.request.server.*;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public interface AhCapwapEventListener extends Serializable {

	/* Capwap Client Response Notifications. */
	void wtpEventControlResp(AhWtpEventControlRequest request);

	void idpQueryResp(AhIdpQueryRequest request);

	void l3RoamConfigResp(AhLayer3RoamingConfigRequest request);

	void fileDownloadResp(AhFileDownloadRequest request);

	/* WTP Event Notifications. */
	void idpReport(AhIdpReportEvent event);

	void apTypeChange(AhApTypeChangeEvent event);

	void fileDownloadFinish(AhFileDownloadFinishEvent event);

	void fileDownloadProgress(AhFileDownloadProgressEvent event);

}