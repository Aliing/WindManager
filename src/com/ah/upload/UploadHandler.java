package com.ah.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;

public interface UploadHandler {


	/* Upload File Types */


	int FILE_TYPE_AP_TECH			= 1;

	int FILE_TYPE_AP_KERNEL_DUMP	= 2;

	int FILE_TYPE_AP_PACKET_CAPTURE = 3;
	
	int FILE_TYPE_AP_DATA_COLLECTION = 4;
	
	int FILE_TYPE_AP_VPN_REPORT		= 5;
	
	int FILE_TYPE_AP_APPLICATION_REPORT = 6;
	
	int FILE_TYPE_AP_NETDUMP = 7;


	/* Upload Request Parameters */
	

	// Which is used for figuring out what's kind of file being uploaded.
	String REQ_PARAM_FILE_TYPE = "FileType";

	// Which is used for figuring out the name of the file that is converted by the file being uploaded.
	String REQ_PARAM_FILE_NAME = "Filename";

	// Which is used for figuring out the HiveAP that is uploading file onto HM.
	String REQ_PARAM_AP_NODE_ID = "ApNodeId";


	/**
	 * Executes a file upload request.
	 *
     * @param request Request we are processing
     * @throws IOException if an input/output error occurs.
	 * @throws FileUploadException if any error occurs during file uploading.
	 */
	void execute(HttpServletRequest request) throws IOException, FileUploadException;

}