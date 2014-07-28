package com.ah.upload.handler.hiveap;

import com.ah.be.common.AhDirTools;
import com.ah.upload.handler.UploadHandlerBase;

public class PacketCaptureUploadHandler extends UploadHandlerBase {


	// ----------------------------------------------------------- Constructors


	public PacketCaptureUploadHandler() {
		filePath = AhDirTools.getDumpDir();
	}


	// --------------------------------------------------------- Public Methods


	@Override
	public int getFileType() {
		return FILE_TYPE_AP_PACKET_CAPTURE;
	}

}