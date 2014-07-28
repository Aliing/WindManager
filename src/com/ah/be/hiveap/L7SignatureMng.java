package com.ah.be.hiveap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.os.FileManager;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.LSevenSignatures;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;

public class L7SignatureMng {
	//private static String l7Path = "/HiveManager/downloads/home/signature";
	private static String l7Path = AhDirTools.getL7SignatureDir(HmDomain.HOME_DOMAIN);
	public static final String FLAG_FILE = l7Path + ".new";
	public static final String FLAG_UPDATE = "/HiveManager/l7_signatures/.update";
	
	private void unzipTar(String filename) throws Exception{
		String strCmd = "tar -xzvf " + l7Path + filename + " -C " + l7Path + " *.meta";
		BeAdminCentOSTools.exeSysCmd(strCmd);
	}
	
	public LSevenSignatures l7SaveOne(String filename) throws Exception{
		File file = new File(l7Path + filename);
		if(!file.exists()){
			BeLogTools.error(HmLogConst.M_COMMON, MgrUtil.getUserMessage("error.l7.signature.file.not.exists"));
			throw new HmException(String.format("target file: '%s' not existed.", filename), "error.l7.signature.file.not.exists");
		}
		
		unzipTar(filename);
		
		String metaName = getMetaFileName();
		
		LSevenSignatures ls = new LSevenSignatures();
		File metaFile = new File(l7Path + metaName);
		if (!metaFile.exists()) {
			throw new HmException(String.format(
					"meta file '%s' in target file '%s' not existed", metaName,
					filename), "error.l7.signature.meta.not.exists");
		}
		FileInputStream in = new FileInputStream(metaFile);
		try {
			Properties p = new Properties();
			p.load(in);

			String ahVer = p.getProperty("ah_ver");
			String dateReleased = p.getProperty("date_released");
			String vendorVer = p.getProperty("vendor_ver");
			String vendorId = p.getProperty("vendor_id");
			String type = p.getProperty("type");
			String platformId = p.getProperty("platform_id");

			if (null == ahVer/*
							 * || null == dateReleased || null == vendorVer ||
							 * null == vendorId
							 */|| null == type || null == platformId) {
				BeLogTools.error(HmLogConst.M_COMMON, MgrUtil
						.getUserMessage("error.l7.signature.meta.not.exists"));

				throw new HmException(String.format(
						"meta file '%s' in target file '%s' format invalid.",
						metaName, filename),
						"error.l7.signature.meta.invalid.format");
			}

			if (!ahVer.trim().matches("(\\d+)(\\.(\\d+)){2}")) {
				throw new HmException(
						String.format(
								"meta file '%s' in target file '%s' ah_ver field format invalid.",
								metaName, filename),
						"error.l7.signature.meta.invalid.format");
			}

			if (!"full".equalsIgnoreCase(type.trim())
					&& !"patch".equalsIgnoreCase(type.trim()))
				throw new HmException(String.format(
						"meta file '%s' in target file '%s' format invalid.",
						metaName, filename),
						"error.l7.signature.meta.invalid.format");

			ls.setAhVersion(ahVer.trim());
			ls.setDateReleased(dateReleased);
			ls.setVendorVersion(vendorVer);
			ls.setVendorId(vendorId);
			short packageT = 0;
			if ("full".equalsIgnoreCase(type.trim()))
				packageT = LSevenSignatures.PACKAGE_TYPE_FULL;
			else
				packageT = LSevenSignatures.PACKAGE_TYPE_PATCH;
			ls.setPackageType(packageT);
			ls.setPlatformId((short) Integer.parseInt(platformId.trim()));
			ls.setFileName(filename);
			HmDomain domain = BoMgmt.getDomainMgmt().getHomeDomain();
			ls.setOwner(domain);
		}
		finally{
			IOUtils.closeQuietly(in);
		}
		saveModel(ls, filename);
		
		//delete meta file after insert
		metaFile.delete();
		
		return ls;
	}
	
	private int l7SaveAll() throws Exception{
		int count = 0;
		File fileP = new File(l7Path);
		File[] files = fileP.listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".tar.gz")) {
				l7SaveOne(file.getName());
				count++;
			}
		}
		return count;
	}
	
	private void saveModel(LSevenSignatures ls, String filename) throws Exception{
		deleteModel(filename,false);
		QueryUtil.createBo(ls);
	}
	
	public void deleteModel(String filename, boolean delFile) throws Exception{
		QueryUtil.removeBos(LSevenSignatures.class, new FilterParams("filename", filename));
		
		if(delFile){
			File file = new File(l7Path + filename);
			if(file.exists())
				file.delete();
		}
	}
	
	private String getMetaFileName(){
		/*File fileP = new File(l7Path);
		for(File file : fileP.listFiles()){
			if(file.isFile() && file.getName().indexOf("meta") > 0){
				return file.getName();
			}
		}*/
		return "signatures.meta";
	}
	
	private List<LSevenSignatures> signatures;
	
	private List<LSevenSignatures> querySignatures(){
		List<LSevenSignatures> signatures = QueryUtil.executeQuery(LSevenSignatures.class, null, null);
		return signatures;
	}
	
	public String findLatestSupportedVersion(String currentVer,
			short deviceModel) {
		if (StringUtils.isEmpty(currentVer)) {
			return currentVer;
		}
		List<LSevenSignatures> signatures = querySignatures();
		List<LSevenSignatures> matches = new ArrayList<>();
		for (LSevenSignatures signature : signatures) {
			if (signature.isMatchDevicePlatform(deviceModel)
					&& signature.isMatchDeviceSignatureVersion(currentVer)) {
				matches.add(signature);
			}
		}
		// add current version into list to compare
		LSevenSignatures current = new LSevenSignatures();
		current.setAhVersion(currentVer);
		matches.add(current);
		Collections.sort(matches, new Comparator<LSevenSignatures>() {
			@Override
			public int compare(LSevenSignatures o1, LSevenSignatures o2) {
				String ver1 = o1.getAhVersion();
				String ver2 = o2.getAhVersion();
				String[] values1 = ver1.split("\\.");
				String[] values2 = ver2.split("\\.");
				int v11 = Integer.parseInt(values1[0]);
				int v12 = Integer.parseInt(values1[1]);
				int v13 = Integer.parseInt(values1[2]);
				int v21 = Integer.parseInt(values2[0]);
				int v22 = Integer.parseInt(values2[1]);
				int v23 = Integer.parseInt(values2[2]);
				return v21 != v11 ? (v21 - v11) : (v22 != v12 ? (v22 - v12)
						: (v23 - v13));
			}
		});
		return matches.get(0).getAhVersion();
	}
	
	public boolean isLatestSupportedVersion(String currentVer, short deviceModel){
		if (StringUtils.isEmpty(currentVer)) {
			return false;
		}
		return currentVer.equals(findLatestSupportedVersion(currentVer, deviceModel));
	}
	
	/**
	 * Get L7 signature file name for specified version number and device model
	 * 
	 * @param version
	 * @param deviceModel
	 * @return
	 */
	public String findSignatureFileNameByVersion(String version,
			short deviceModel) {
		if (StringUtils.isEmpty(version)) {
			return null;
		}
		String fileName = null;
		if (null == signatures) {
			signatures = querySignatures();
		}
		for (LSevenSignatures signature : signatures) {
			if (version.equals(signature.getAhVersion())
					&& signature.isMatchDevicePlatform(deviceModel)) {
				fileName = signature.getFileName();
				break;
			}
		}
		return fileName;
	}
	
	/**
	 * Find L7 Signature record by signature file name.
	 * 
	 * @param fileName
	 * @return
	 */
	public LSevenSignatures findSignatureByFileName(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		return QueryUtil.findBoByAttribute(LSevenSignatures.class, "fileName",
				fileName);
	}
	
	/**
	 * Find L7 Signature records by signature version.
	 * 
	 * @param version
	 * @return
	 */
	public List<LSevenSignatures> findSignaturesByVersion(String version) {
		if (StringUtils.isEmpty(version)) {
			return null;
		}
		return QueryUtil.executeQuery(LSevenSignatures.class, null,
				new FilterParams("ahVersion", version));
	}

	/**
	 * Check whether there's new L7 signature file in signature directory.
	 * 
	 * @return
	 */
	private boolean needSyncSignatureInfo() {
		return FileManager.getInstance().existsFile(FLAG_FILE);
	}

	private void syncSignatureInfo() throws Exception {
		int count = QueryUtil.bulkRemoveBos(LSevenSignatures.class, null);
		BeLogTools.info(HmLogConst.M_COMMON, String.format(
				"total %s old signature records removed from database.", count));
		int newCount = l7SaveAll();
		BeLogTools.info(HmLogConst.M_COMMON, String.format(
				"total %s new signature records created into database.",
				newCount));
	}

	/**
	 * Clean the flag file which indicates new signature file added into
	 * signature directory.
	 * 
	 * @throws Exception
	 */
	private void cleanSyncSingatureFlag() throws Exception {
		boolean isExist = FileManager.getInstance().existsFile(FLAG_FILE);
		if (isExist) {
			FileManager.getInstance().deletefile(FLAG_FILE);
			BeLogTools.info(HmLogConst.M_COMMON,
					"remove signature flag file successfully.");
		} else {
			BeLogTools.info(HmLogConst.M_COMMON,
					"no signature flag file need to be removed.");
		}
	}

	/**
	 * Call to synchronize L7 signature meta information into database, only do
	 * synchronize when there's a flag file in signature directory.
	 */
	public void callSyncSignatureInfo() {
		try {
			if (needSyncSignatureInfo()) {
				BeLogTools
						.info(HmLogConst.M_COMMON,
								"signature flag file existed, prepare to sync meta information.");
				syncSignatureInfo();
				cleanSyncSingatureFlag();
			} else {
				BeLogTools
						.info(HmLogConst.M_COMMON,
								"no signature flag file existed, do not touch current data.");
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_COMMON,
					"call synchronize L7 signature meta information error.", e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
					HmSystemLog.FEATURE_CONFIGURATION,
					MgrUtil.getMessageString("error.l7.signature.meta.parse"));
		}
	}

	public static void main(String[] args) throws IOException{
		
	}
}
