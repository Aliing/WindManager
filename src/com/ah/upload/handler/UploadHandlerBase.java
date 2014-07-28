package com.ah.upload.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import com.ah.upload.UploadHandler;
import com.ah.util.HmContextListener;
import com.ah.util.Tracer;
import com.ah.util.fileupload.FileUploadUtil;

public abstract class UploadHandlerBase implements UploadHandler {

	private static final Tracer log = new Tracer(UploadHandlerBase.class.getSimpleName());


	// ----------------------------------------------------- Instance Variables


	/* File cache size. File less than 'FILE_CACHE_SIZE' will be written into memory, otherwise directly written into disk */
	protected int fileCacheSize = 1024 * 1024;

	/* Temp file directory used to store files whose sizes are larger than the 'FILE_CACHE_SIZE' */
	protected String tempFileDir = "tmp";

	/* Maximum file size in byte */
	protected long maxFileSize = 50 * 1000 * 1000;

	/**
	 * The file path in which the file being uploaded will be written.
	 */
	protected String filePath;

	/**
	 * The name of the file that is converted by the file being uploaded.
	 */
	protected String filename;

	/**
	 * The node id of HiveAP which is uploading file onto HM.
	 */
	protected String hiveApNodeId;


	// ------------------------------------------------------------- Properties


	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getHiveApNodeId() {
		return hiveApNodeId;
	}

	public void setHiveApNodeId(String hiveApNodeId) {
		this.hiveApNodeId = hiveApNodeId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}


	// --------------------------------------------------------- Public Methods


	@Override
	public void execute(HttpServletRequest request) throws IOException, FileUploadException {
		ServletRequestContext requestContext = new ServletRequestContext(request);

		if (!ServletFileUpload.isMultipartContent(requestContext)) {
			log.warning("execute", "HM can just support file upload in POST method using the content type as multipart/form-data according to RFC2388. Remote Client: " + request.getRemoteAddr() + "; Method: " + request.getMethod() + "; Content Type: " + request.getContentType());
			throw new FileUploadException("HM can just support file upload in POST method using the content type as multipart/form-data");
		}

		String tempFilePath = HmContextListener.context.getRealPath("/" + tempFileDir);
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(fileCacheSize);
		factory.setRepository(new File(tempFilePath));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(maxFileSize);

		// Parse request.
		List<?> fileItems = upload.parseRequest(request);

		for (Object obj : fileItems) {
			FileItem fileItem = (FileItem) obj;

			if (fileItem.isFormField()) {
				String name = fileItem.getFieldName();
				String value = fileItem.getString();
				request.setAttribute(name, value);
			} else {
				if (log.getLogger().isDebugEnabled()) {
					log.debug("execute", "Field Name: " + fileItem.getFieldName() + "; File Name: " + fileItem.getName() + "; File Size: " + fileItem.getSize());
				}

				if (filename == null || filename.isEmpty()) {
					filename = fileItem.getName();
				}

				if (filename.length() == 0) {
					log.error("execute", "Incorrect filename: " + fileItem.getName() + "; Field Name: " + fileItem.getFieldName() + "; File Size: " + fileItem.getSize());
					throw new FileUploadException("Filename " + fileItem.getName() + " is invalid");
				}
				
				if (FileUploadUtil.containsNotAcceptableExtension(filename)) {
				    log.error("execute", "Unsupported file type for the file : " + fileItem.getName());
				    throw new FileUploadException("File " + fileItem.getName() + " is not allowed to upload");
				}
				
				if (FileUploadUtil.isPathTraversal(filename)) {
				    log.error("execute", "Unsupported file name : " + fileItem.getName());
				    throw new FileUploadException("File " + fileItem.getName() + " is not allowed to upload");
				}

				File file = new File(filePath, filename);
				OutputStream out = new FileOutputStream(file);
				InputStream in = fileItem.getInputStream();
				byte[] buffer = new byte[1024];
				int length;

				if (log.getLogger().isDebugEnabled()) {
					log.debug("execute", "File Type: " + getFileType() + "; File: " + file.getPath());
				}

				try {
					while((length = in.read(buffer)) > 0) {
						out.write(buffer, 0, length);
					}
				} finally {
					try {
						out.close();
					} catch (IOException e) {
						log.error("execute", "Output IO Close Error", e);
					}

					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							log.error("execute", "Input IO Close Error", e);
						}
					}
				}

				log.info("execute", "File was successfully uploaded into " + file.getPath());
			}
		}
	}


	// ------------------------------------------------------ Protected Methods


	/**
	 * @return the type of file being uploaded.
	 */
	protected abstract int getFileType();

}