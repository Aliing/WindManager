package com.ah.upload.handler.hiveap;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;

import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.bo.performance.AhReport;
import com.ah.upload.UploadHandler;
import com.ah.upload.handler.UploadHandlerBase;

public class VpnReportUploadHandler extends UploadHandlerBase {
	
	public static String REQ_PARAM_REPORT_PERIOD = "Period";

	// --------------------------------------------------------- Public Methods


	@Override
	public void execute(HttpServletRequest request) throws IOException, FileUploadException {
		hiveApNodeId = request.getParameter(REQ_PARAM_AP_NODE_ID);
		String period = request.getParameter(REQ_PARAM_REPORT_PERIOD);
		
		if (hiveApNodeId != null && period != null) {
			hiveApNodeId = hiveApNodeId.trim();
			String subDir = (period.trim().equals(String.valueOf(AhReport.REPORT_PERIOD_VPN_ONEHOUR)) ? "high" : "low");
			
			if (!hiveApNodeId.isEmpty() && hiveApNodeId.length() == 12) {
				filePath = AhDirTools.getInterfaceReportUploadDir(hiveApNodeId + File.separator + subDir);
				filename = "vpn_report.tgz";
				super.execute(request);
			} else {
				throw new FileUploadException("The "+NmsUtil.getOEMCustomer().getAccessPonitName()+" node id '" + hiveApNodeId + "' provided was invalid");
			}
		} else {
			throw new FileUploadException("The parameter of '"
					+ UploadHandler.REQ_PARAM_AP_NODE_ID + "' and '"
					+ REQ_PARAM_REPORT_PERIOD + "' must be required");
		}
	}

	@Override
	public int getFileType() {
		return FILE_TYPE_AP_VPN_REPORT;
	}

}