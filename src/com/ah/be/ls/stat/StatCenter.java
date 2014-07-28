package com.ah.be.ls.stat;

import java.io.File;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

import com.ah.be.ls.ClientSenderCenter;
import com.ah.util.Tracer;

public class StatCenter {
//	private static final Log log = LogFactory.getLog("commonlog.StatCenter");
                  private static final Tracer log = new Tracer(StatCenter.class.getSimpleName());
	public static void reportApUsageStat() {
		try {
			log.info("StatCenter","Start data mining statistic.");
			long start = System.currentTimeMillis();
			Document doc = StatManager.getInstance().stat();
			if (null == doc) {
				log.info("StatCenter","data mining collect no xml document.");
				return;
			}
			if (log.getLogger().isDebugEnabled()) {
				String shortName = DateFormatUtils.format(
						Calendar.getInstance(), "yyyy-MM-dd HH-mm-ss")
						+ ".xml";
				String fullName = FileUtils.getTempDirectoryPath()
						+ File.separatorChar + "dm" + File.separatorChar
						+ shortName;
				FileUtils.writeStringToFile(new File(fullName), doc.asXML(),
						"UTF-8");
				log.debug("StatCenter","data mining data logged to file: " + fullName);
			}
			ClientSenderCenter.sendApUsageStat(doc);
			long end = System.currentTimeMillis();
			log.info("StatCenter","report data mining statistic total cost: "
					+ (end - start) + "ms.");
		} catch (Exception e) {
			log.error("StatCenter","reportApUsageStat exception", e);
		}
	}
	/*- unused codes, commented by mfjin
	 public static boolean report_statis() {
	 try {
	 // init env
	 String strUpload = CommConst.Upload_Dir;

	 File oUpload = new File(strUpload);

	 if (oUpload.exists()) {
	 if (oUpload.isDirectory()) {

	 FileManager.getInstance().deleteDirectory(strUpload);
	 } else {
	 oUpload.delete();
	 }
	 }

	 String strTmp = CommConst.Upload_Dir + "/tmp";

	 File oTmp = new File(strTmp);

	 oTmp.mkdirs();

	 // get the data
	 StatManager.getInstance().stat(strTmp);

	 // tar a file
	 String shCmd = "tarstat.sh";
	 String[] strCmds = { "sh",
	 BeAdminCentOSTools.ahShellRoot + "/" + shCmd,
	 CommConst.Upload_Static_Name };

	 if (!BeOperateHMCentOSImpl.isRslt_0(strCmds)) {
	 BeLogTools.commonLog(BeLogTools.ERROR, "tar faile failed!");
	 return false;
	 }

	 // upload the file
	 return ClientSenderCenter.uploadStaFile();
	 } catch (Exception ex) {
	 // add log
	 BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage());
	 return false;
	 }
	 }
	 */

}