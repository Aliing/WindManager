package com.ah.upload.handler;

import com.ah.upload.UploadHandler;
import com.ah.upload.handler.hiveap.AppReportUploadHandler;
import com.ah.upload.handler.hiveap.DataCollectionUploadHandler;
import com.ah.upload.handler.hiveap.KernelDumpUploadHandler;
import com.ah.upload.handler.hiveap.NetdumpUploadHandler;
import com.ah.upload.handler.hiveap.PacketCaptureUploadHandler;
import com.ah.upload.handler.hiveap.TechUploadHandler;
import com.ah.upload.handler.hiveap.VpnReportUploadHandler;
import com.ah.util.Tracer;

public class UploadHandlers {

	private static final Tracer log = new Tracer(UploadHandlers.class.getSimpleName());


	// --------------------------------------------------------- Public Methods


	/**
	 * Returns a specific upload handler based on the file type given.
	 *
	 * @param fileType type of file uploaded.
	 * @return a file upload handler used to upload specified kind of file.
	 */
	public static UploadHandler getHandler(int fileType) {
		UploadHandler uploadHandler;

		switch (fileType) {
			case UploadHandler.FILE_TYPE_AP_TECH:
				uploadHandler = new TechUploadHandler();
				break;
			case UploadHandler.FILE_TYPE_AP_KERNEL_DUMP:
				uploadHandler = new KernelDumpUploadHandler();
				break;
			case UploadHandler.FILE_TYPE_AP_PACKET_CAPTURE:
				uploadHandler = new PacketCaptureUploadHandler();
				break;
			case UploadHandler.FILE_TYPE_AP_DATA_COLLECTION:
				uploadHandler = new DataCollectionUploadHandler();
				break;
			case UploadHandler.FILE_TYPE_AP_VPN_REPORT:
				uploadHandler = new VpnReportUploadHandler();
				break;
			case UploadHandler.FILE_TYPE_AP_APPLICATION_REPORT:
				uploadHandler = new AppReportUploadHandler();
				break;
			case UploadHandler.FILE_TYPE_AP_NETDUMP:
				uploadHandler = new NetdumpUploadHandler();
				break;				
			default:
				log.warning("getHandler", "Could not find out a matched upload handler based on the provided file type " + fileType);
				uploadHandler = null;
				break;
		}

		return uploadHandler;
	}

}