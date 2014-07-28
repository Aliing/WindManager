package com.ah.ui.actions.monitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang.StringUtils;
import org.apache.fop.svg.PDFTranscoder;
import org.json.JSONObject;

import com.ah.be.admin.util.EmailElement;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.performance.AhNewReport;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class ReportExportAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	
	private static final Tracer	log	= new Tracer(ReportExportAction.class.getSimpleName());
	
	public static final String DEFAULT_EXPORTED_CHART_NAME = "exportedChart";
	public static final String fileDirPathCurrent = "/tmp/currentchart";
	public static final String FILE_TO_BE_EXPORT_IF_ERROR = "chart.err";
	
	public static final String EXPORTED_OBJECT_TYPE_JPEG = "image/jpeg";
	public static final String EXPORTED_OBJECT_TYPE_PNG = "image/png";
	public static final String EXPORTED_OBJECT_TYPE_PDF = "application/pdf";
	public static final String EXPORTED_OBJECT_TYPE_SVG = "image/svg+xml";
	public static final String EXPORTED_OBJECT_TYPE_EMAIL_PDF = "email/pdf";

	public String execute() throws Exception {
		try {
			if ("emailConfig".equals(operation)) {
				SimpleEmailObject emailObj = prepareEmailInformation();
				if (emailObj != null) {
					this.emailAddress = emailObj.getToEmail();
				}
				return "chartEmailConfig";
			} else {
				if (EXPORTED_OBJECT_TYPE_EMAIL_PDF.equals(this.getType())) {
					File tmpFileDir = new File(fileDirPathCurrent + File.separator
							+ this.getDomain().getDomainName() + File.separator + "email");
					if (!tmpFileDir.exists()) {
						tmpFileDir.mkdirs();
					}
				}
				
				Reader inreader = null;
		        TranscoderInput input = null;
		        ByteArrayOutputStream ostream = null;
		        TranscoderOutput output = null;
		        
		        if (EXPORTED_OBJECT_TYPE_JPEG.equals(this.getType())
		        		|| EXPORTED_OBJECT_TYPE_PNG.equals(this.getType())
		        		|| EXPORTED_OBJECT_TYPE_PDF.equals(this.getType())
		        		|| EXPORTED_OBJECT_TYPE_SVG.equals(this.getType())) {
		        	inreader = new StringReader(this.getSvg());
			        input = new TranscoderInput(inreader);
	
			        ostream = new ByteArrayOutputStream();
			        output = new TranscoderOutput(ostream);
		        }
				
		        if (EXPORTED_OBJECT_TYPE_EMAIL_PDF.equals(this.getType())) {
					String domainName = "";
					String toEmail = "";
					String subject = "Exported report: " + this.getFilename();
					SimpleEmailObject emailObj = prepareEmailInformation();
					if (emailObj != null) {
						domainName = emailObj.getDomainName();
						toEmail = emailObj.getToEmail();
					}
					
					if (!StringUtils.isBlank(this.emailAddress)) {
						toEmail = this.emailAddress;
					}
					
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					
					if (StringUtils.isBlank(domainName)
							|| StringUtils.isBlank(toEmail)) {
						jsonObject.put("resultStatus", false);
						if (StringUtils.isBlank(toEmail)) {
							jsonObject.put("errInfo", "Please input Email Delivery Address for this report.");
						}
						return "json";
					} 
					
					PDFTranscoder t = new PDFTranscoder();
			        input = new TranscoderInput(new StringReader(this.getSvg()));
		
			        OutputStream ostreamEmail = new FileOutputStream(this.getEmailInputPath());
			        TranscoderOutput outputEmail = new TranscoderOutput(ostreamEmail);
		
			        t.transcode(input, outputEmail);
			        ostreamEmail.flush();
			        ostreamEmail.close();
			        
			        try {
						String mailFileName;
						mailFileName = "Exported report: "  + this.getFilename() + this.getFileExtName();
						String filePath = this.getEmailInputPath();
						
						File tmpFile = new File(filePath);
						if (tmpFile.exists()) {
							EmailElement email = new EmailElement();
							email.setDomainName(domainName);
							email.setToEmail(toEmail);
							email.setSubject(subject);
							email.setMailContent(mailFileName);
							List<String> fileList = new ArrayList<String>();
							fileList.add(filePath);
							email.setDetachedFileList(fileList);
							email.setMustBeSent(true);
							
							HmBeAdminUtil.sendEmail(email);
						}
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						return "json";
					}
					
					return "json";
				} else if (EXPORTED_OBJECT_TYPE_JPEG.equals(this.getType())) {
			        JPEGTranscoder t = new JPEGTranscoder();
			        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
			        t.transcode(input, output);
				} else if (EXPORTED_OBJECT_TYPE_PDF.equals(this.getType())) {
					PDFTranscoder t = new PDFTranscoder();
			        t.transcode(input, output);
				} else if (EXPORTED_OBJECT_TYPE_SVG.equals(this.getType())) {
					ostream.write(this.getSvg().getBytes());
				} else {
					PNGTranscoder t = new PNGTranscoder();
			        t.transcode(input, output);
				}
				
				inputStream = new ByteArrayInputStream(ostream.toByteArray());
		        if (ostream != null) ostream.close();
		        if (inreader != null) inreader.close();
		        
				return "download";
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			e.printStackTrace();
			log.error("ReportExportAction: Failed to generate exported charts.", e);
			return "error";
		}
	}
	
	/**
	 * what kind type of image to be exported: jpeg/png/pdf/svg
	 */
	private String type;
	
	/**
	 * hold the content of svg
	 */
	private String svg;
	
	/**
	 * indicate what is the filename to be used
	 */
	private String filename;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String getFilename() {
		if (StringUtils.isBlank(filename)) {
			filename = DEFAULT_EXPORTED_CHART_NAME;
		}
		filename = filename.replaceAll("/", " ");
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getEmailInputPath() {
		return fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + "email" + File.separator + this.getFilename() + this.getFileExtName();
	}
	
	private InputStream inputStream;
	public InputStream getInputStream() throws Exception {
		return inputStream;
	}
	
	public String getLocalFileName() {
		return this.getFilename() + this.getFileExtName();
	}
	
	public String getErrorInputPath() {
		return fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + FILE_TO_BE_EXPORT_IF_ERROR;
	}
	public String getErrorFileName() {
		return FILE_TO_BE_EXPORT_IF_ERROR;
	}
	public InputStream getErrorInputStream() throws Exception {
		return new ByteArrayInputStream("Errors occured while generating exported chart.".getBytes());
	}
	
	public String getFileExtName() {
		if (EXPORTED_OBJECT_TYPE_JPEG.equals(type)) {
			return ".jpg";
		} else if (EXPORTED_OBJECT_TYPE_PNG.equals(type)) {
			return ".png";
		} else if (EXPORTED_OBJECT_TYPE_PDF.equals(type)) {
			return".pdf";
		} else if (EXPORTED_OBJECT_TYPE_SVG.equals(type)) {
			return ".svg";
		} else if (EXPORTED_OBJECT_TYPE_EMAIL_PDF.equals(type)) {
			return ".pdf";
		}
		
		return "";
	}

	private String emaildb;
	
	public String getEmaildb() {
		return emaildb;
	}

	public void setEmaildb(String emaildb) {
		this.emaildb = emaildb;
	}
	
	private String emailAddress;

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	private String chartId;

	public String getChartId() {
		return chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}
	
	class SimpleEmailObject {
		private String domainName;
		private String toEmail;
		public String getDomainName() {
			return domainName;
		}
		public void setDomainName(String domainName) {
			this.domainName = domainName;
		}
		public String getToEmail() {
			return toEmail;
		}
		public void setToEmail(String toEmail) {
			this.toEmail = toEmail;
		}
	}
	
	private SimpleEmailObject prepareEmailInformation() {
		SimpleEmailObject result = null;
		
		if (StringUtils.isBlank(this.emaildb)) {
			if (EMAIL_FOR_TYPE_NEWREPORT.equals(this.emailMark)) {
				AhNewReport aReport = (AhNewReport)MgrUtil.getSessionAttribute(AhNewReport.class.getSimpleName() + "Source");
				if (aReport != null) {
					result = new SimpleEmailObject();
					result.setDomainName(aReport.getOwner().getDomainName());
					result.setToEmail(aReport.getEmailAddress());
				}
			} else if (EMAIL_FOR_TYPE_DASHBOARD.equals(this.emailMark)) {
				AhDashboard aReport = (AhDashboard)MgrUtil.getSessionAttribute(AhDashboard.class.getSimpleName() + "Source");
				if (aReport != null) {
					result = new SimpleEmailObject();
					result.setDomainName(aReport.getOwner().getDomainName());
					result.setToEmail(aReport.getReEmailAddress());
				}
			}
		}
		
		return result;
	}
	
	private static final String EMAIL_FOR_TYPE_DASHBOARD = "da";
	private static final String EMAIL_FOR_TYPE_NEWREPORT = "rp";
	private String emailMark = EMAIL_FOR_TYPE_NEWREPORT;

	public String getEmailMark() {
		return emailMark;
	}

	public void setEmailMark(String emailMark) {
		this.emailMark = emailMark;
	}
}
