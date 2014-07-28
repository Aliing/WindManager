package com.ah.upload.handler.hiveap;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileUploadException;
import java.io.IOException;

import com.ah.be.common.AhDirTools;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.upload.handler.UploadHandlerBase;


public class NetdumpUploadHandler extends UploadHandlerBase {

	public void execute(HttpServletRequest request) throws IOException, FileUploadException{
		hiveApNodeId = request.getParameter(REQ_PARAM_AP_NODE_ID);
		//get vhm name according to AP NodeId, use vhm name as sub path
		SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(hiveApNodeId);	
		String subPath = new String();
		if(null != simpleHiveAp) {
			subPath = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId()).getDomainName();
		}
		//String subPath = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId()).getDomainName();
		filePath = AhDirTools.getNetdumpUploadDir() + subPath;
		AhDirTools.checkDir(filePath);
		super.execute(request);
	}
	public int getFileType() {
		return FILE_TYPE_AP_NETDUMP;
	}
	
}