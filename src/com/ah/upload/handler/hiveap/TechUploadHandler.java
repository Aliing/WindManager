package com.ah.upload.handler.hiveap;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;

import com.ah.be.common.AhDirTools;
import com.ah.upload.UploadHandler;
import com.ah.upload.handler.UploadHandlerBase;

public class TechUploadHandler extends UploadHandlerBase {


	// --------------------------------------------------------- Public Methods


	@Override
	public void execute(HttpServletRequest request) throws IOException, FileUploadException {
		filename = request.getParameter(REQ_PARAM_FILE_NAME);

		if (filename != null) {
			filename = filename.trim();

			if (!filename.isEmpty()) {
				filePath = AhDirTools.getTechDir();

				super.execute(request);
			} else {
				throw new FileUploadException("The filename '" + filename + "' provided was invalid");
			}
		} else {
			throw new FileUploadException("The parameter of '" + UploadHandler.REQ_PARAM_FILE_NAME + "' must be required");
		}
	}

	@Override
	public int getFileType() {
		return FILE_TYPE_AP_TECH;
	}

}