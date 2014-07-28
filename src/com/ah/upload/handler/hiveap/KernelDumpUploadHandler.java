package com.ah.upload.handler.hiveap;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;

import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.upload.UploadHandler;
import com.ah.upload.handler.UploadHandlerBase;

public class KernelDumpUploadHandler extends UploadHandlerBase {


	// --------------------------------------------------------- Public Methods


	@Override
	public void execute(HttpServletRequest request) throws IOException, FileUploadException {
		hiveApNodeId = request.getParameter(REQ_PARAM_AP_NODE_ID);

		if (hiveApNodeId != null) {
			hiveApNodeId = hiveApNodeId.trim();

			if (!hiveApNodeId.isEmpty() && hiveApNodeId.length() == 12) {
				filePath = AhDirTools.getKernelDumpDir(hiveApNodeId);

				super.execute(request);
			} else {
				throw new FileUploadException("The "+NmsUtil.getOEMCustomer().getAccessPonitName()+" node id '" + hiveApNodeId + "' provided was invalid");
			}
		} else {
			throw new FileUploadException("The parameter of '" + UploadHandler.REQ_PARAM_AP_NODE_ID + "' must be required");
		}
	}

	@Override
	public int getFileType() {
		return FILE_TYPE_AP_KERNEL_DUMP;
	}

}