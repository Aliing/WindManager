package com.ah.upload.handler.hiveap;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileUploadException;
import java.io.IOException;

import com.ah.be.common.AhDirTools;
import com.ah.upload.handler.UploadHandlerBase;


public class AppReportUploadHandler extends UploadHandlerBase {

	public void execute(HttpServletRequest request) throws IOException, FileUploadException{
		filePath = AhDirTools.getApplicationReportUploadDir();
		super.execute(request);
		//need update here
		
	}
	public int getFileType() {
		return FILE_TYPE_AP_APPLICATION_REPORT;
	}
	
}