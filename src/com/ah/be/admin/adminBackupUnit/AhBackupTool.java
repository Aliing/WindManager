package com.ah.be.admin.adminBackupUnit;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;
//import java.sql.DriverManager;

public class AhBackupTool {

	private static final String ahAlarmTable = "AH_ALARM";

	private static final String ahEventTable = "AH_EVENT";

	private static final String ahAssociation = "HM_ASSOCIATION";

	private static final String ahCurrentClient = "AH_CLIENTSESSION";

	private static final String ahHistoryClient = "AH_CLIENTSESSION_HISTORY";

	private static final String ahNeighbor = "HM_NEIGHBOR";

	private static final String ahRadioAttribute = "HM_RADIOATTRIBUTE";

	private static final String ahRadioStats = "HM_RADIOSTATS";

	private static final String ahVifStats = "HM_VIFSTATS";

	private static final String ahXif = "HM_XIF";

	private static final String ahDomain = "HM_DOMAIN";

	private static final String ahLicenseHistory = "LICENSE_HISTORY_INFO";

	private static final String ahHomeDomain = "home";

	private static final String ahGlobalDomain = "global";

	private static final String AP_UPDATE_RESULT = "hive_ap_update_result";

	private static final String AP_UPDATE_RESULT_CLI = "hive_ap_update_result_item";

	private static final String AP_IDP = "IDP";

	private static final String ahHaSetting = "HA_SETTINGS";

	private static final String ahActkey = "ACTIVATION_KEY_INFO";

	private static final String hm_Update_version_info = "hm_updatesoftwareinfo";

	//for order key info
	private static final String hmOrderKeyInfo  = "DOMAIN_ORDER_KEY_INFO";
	private static final String HmOrderKeyHistory = "order_history_info";
	private static final String HMReginfo="USER_REG_INFO_FOR_LS";
	//private static final String HMOrderKeyApInfo  = "order_ap_info";
	private static final String ACM_ENTITLE_KEY_HISTORY_INFO = "acm_entitle_key_history_info";

	private BlockingQueue<BackupTableDTO>		eventQueue  = new LinkedBlockingQueue<BackupTableDTO>();

	private BackupProcessorThread[] 	threadArray = null;

	private int			BAKCUP_THREAD_NUM = 2;

	public AhBackupTool() {
	}

	public static void main(String[] args) {
		AhBackupTool tool = new AhBackupTool();
		tool.backupFullDatabase("D:\\dbback", 0);
	}

	public void backupFullDatabase(String strXmlPath, int iContent) {
		if (null == strXmlPath) {
			// add the debug log
			BeLogTools
					.restoreLog(BeLogTools.ERROR, "the parameter is error,Domainid or file path is null");

			return;
		}

		if (!(strXmlPath.substring(strXmlPath.length() - 1).equals(
				File.separator) || strXmlPath
				.substring(strXmlPath.length() - 1).equals("/"))) {
			strXmlPath = strXmlPath + File.separatorChar;
		}

		BeLogTools
		.restoreLog(BeLogTools.DEBUG, "Begin to backup full database");

		List<HmDomain> listData = QueryUtil.executeQuery(HmDomain.class, null, null);

		if (listData.isEmpty()) {
			return;
		}

		// store xml root path
		for (HmDomain hoDomain : listData) {
			if (AhBackupTool.ahHomeDomain.equalsIgnoreCase(hoDomain
					.getDomainName())) {
				storeBriefDomainInfo(strXmlPath, hoDomain);
			}
		}

		for (HmDomain hoDomain : listData) {
			if (AhBackupTool.ahGlobalDomain.equalsIgnoreCase(hoDomain
					.getDomainName())) {
				continue;
			}

			backupCertainDomain(strXmlPath, hoDomain, iContent);
		}

		threadArray = new BackupProcessorThread[BAKCUP_THREAD_NUM];
		for(int i = 0; i < BAKCUP_THREAD_NUM; i++) {
			threadArray[i] = new BackupProcessorThread();
			threadArray[i].setName("Backup:"+i);
			threadArray[i].start();
		}
	}

	public void backupDomainDatabase(Long lDomainId, String strXmlPath,
			int iContent) {
		if (null == lDomainId && null == strXmlPath) {
			// add the debug log
			BeLogTools
					.restoreLog(BeLogTools.ERROR, "the parameter is error,Domainid or file path is null");

			return;
		}

		try {
			HmDomain oDomain = QueryUtil.findBoById(HmDomain.class, lDomainId);

			if (null == oDomain) {
				return;
			}

			if (!(strXmlPath.substring(strXmlPath.length() - 1).equals(
					File.separator) || strXmlPath.substring(
					strXmlPath.length() - 1).equals("/"))) {
				strXmlPath = strXmlPath + File.separatorChar;
			}
			// store strxml root path
			storeBriefDomainInfo(strXmlPath, oDomain);

			backupCertainDomain(strXmlPath, oDomain, iContent);

			threadArray = new BackupProcessorThread[BAKCUP_THREAD_NUM];
			for(int i = 0; i < BAKCUP_THREAD_NUM; i++) {
				threadArray[i] = new BackupProcessorThread();
				threadArray[i].setName("Backup:"+i);
				threadArray[i].start();
			}

		} catch (Exception ex) {
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
	}

	public void backupLicenseHistory(String strPath)
	{
		backup1table(strPath, AhBackupTool.ahLicenseHistory.toLowerCase());
	}

	public void backupHaSetting(String strPath)
	{
		backup1table(strPath,AhBackupTool.ahHaSetting.toLowerCase());
	}

	public void backupActivationkey(String strPath)
	{
		backup1table(strPath,AhBackupTool.ahActkey.toLowerCase());
	}

	public void backupOrderkeyInfo(String strPath)
	{
		backup1table(strPath, AhBackupTool.hmOrderKeyInfo.toLowerCase());
		backup1table(strPath, AhBackupTool.HmOrderKeyHistory.toLowerCase());
		backup1table(strPath, AhBackupTool.HMReginfo.toLowerCase());
		//backup1table(strPath, AhBackupTool.HMOrderKeyApInfo.toLowerCase());
		backup1table(strPath, AhBackupTool.ACM_ENTITLE_KEY_HISTORY_INFO.toLowerCase());
	}

	public void backupdomainOrderInfo(String strPath, String strDomainName)
	{
		backupDomainTable(strPath, strDomainName, AhBackupTool.hmOrderKeyInfo.toLowerCase());
		backupDomainTable(strPath, strDomainName, AhBackupTool.HmOrderKeyHistory.toLowerCase());
		backupDomainTable(strPath, strDomainName, AhBackupTool.HMReginfo.toLowerCase());
		backupDomainTable(strPath, strDomainName, AhBackupTool.ACM_ENTITLE_KEY_HISTORY_INFO.toLowerCase());
	}

	private void backupDomainTable(String strPath, String strDomainName, String strTableName)
	{
        Connection con = null;

		XMLWriter output = null;

		ResultSet rsTable = null;

		Statement stTable = null;

		try {
			if (!(strPath.substring(strPath.length() - 1)
					.equals(File.separator) || strPath.substring(
					strPath.length() - 1).equals("/"))) {
				strPath = strPath + File.separatorChar;
			}

//			Class.forName("org.postgresql.Driver").newInstance();
//
//			String strUrl = "jdbc:postgresql://localhost:5432/hm";
//
//			String strUsr = "hivemanager";
//
//			String strPsd = "aerohive";

			AhBackupNewTool oTool = new AhBackupNewTool();

			con = oTool.initCon();

			try {
				stTable = con.createStatement();

				String strSql;
				int intFileCount = 0;
				int intRecordNum;

				while(true)
				{
					intRecordNum = 0;

					strSql = "select * from " + strTableName+" where domainname='"+strDomainName+"'" + " limit " + 5000
							+ " offset " + intFileCount*5000;
					
					if(! AhBackupNewTool.isValidCoon(con))
					{
						con = oTool.initCon();
						stTable = con.createStatement();
					}

					rsTable = stTable.executeQuery(strSql);

					Document document = DocumentHelper.createDocument();

					Element table = document.addElement("table").addAttribute(
							"schema", strTableName);

					ResultSetMetaData rsmd = rsTable.getMetaData();

					int iCount = rsmd.getColumnCount();

					while (rsTable.next()) {
						intRecordNum++;
						Element row = table.addElement("row");

						for (int icol = 1; icol <= iCount; icol++) {
							String newStr;

							if (rsTable.getString(icol) == null) {
								newStr = "NULL";
							} else if("null".equalsIgnoreCase(rsTable.getString(icol))){
								newStr = "_" + rsTable.getString(icol) + "_";
							}else {
								newStr = rsTable.getString(icol);
							}

							if(1 == intRecordNum) {
								row.addElement("field").addAttribute("name",
									rsmd.getColumnName(icol)).addAttribute("value",
									newStr);
							}else {
								row.addElement("field").addAttribute("value",
										newStr);
							}
						}
					}

					if(intRecordNum <= 0 && 0 != intFileCount)
						break;

					File file;

					if (intFileCount == 0) {
						file = new File(strPath + strTableName.toLowerCase()
								+ ".xml");

					} else {
						file = new File(strPath + strTableName.toLowerCase()
								+ "_" + intFileCount + ".xml");
					}

					intFileCount++;

					output = new XMLWriter(new FileOutputStream(file));

					output.write(document);

					output.close();

					if(5000 > intRecordNum)
						break;
				}

				rsTable.close();

				stTable.close();
			} catch (Exception ex) {
				BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
			}
		} catch (Exception ex) {
			// add the debug msg
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
		finally
		{
		    if(null != con)
		    {
		    	try
		    	{
		    	  con.close();
		    	}
		    	catch(Exception conex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, conex.getMessage());
		    	}
		    }

		    if(null != output)
		    {
		    	try
		    	{
		    		output.close();
		    	}
		    	catch(Exception outex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, outex.getMessage());
		    	}
		    }

		    if(null != rsTable)
		    {
		    	try
		    	{
		    		rsTable.close();
		    	}
		    	catch(Exception rsex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, rsex.getMessage());
		    	}
		    }

		    if(null != stTable)
		    {
		    	try
		    	{
		    		stTable.close();
		    	}
		    	catch(Exception stex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, stex.getMessage());
		    	}
		    }
		}
	}

	private void backup1table(String strPath,String strTableName) {
		Connection con = null;

		XMLWriter output = null;

		ResultSet rsTable = null;

		Statement stTable = null;

		try {
			if (!(strPath.substring(strPath.length() - 1)
					.equals(File.separator) || strPath.substring(
					strPath.length() - 1).equals("/"))) {
				strPath = strPath + File.separatorChar;
			}

//			Class.forName("org.postgresql.Driver").newInstance();
//
//			String strUrl = "jdbc:postgresql://localhost:5432/hm";
//
//			String strUsr = "hivemanager";
//
//			String strPsd = "aerohive";

//			con = DriverManager
//					.getConnection(strUrl, strUsr, strPsd);
			AhBackupNewTool oTool = new AhBackupNewTool();

			con = oTool.initCon();


			//String strTableName = AhBackupTool.ahLicenseHistory.toLowerCase();

			try {
				stTable = con.createStatement();

//				String strSql = "select count(*) from " + strTableName;
//
//				rsTable = stTable.executeQuery(strSql);
//
//				rsTable = stTable.executeQuery(strSql);
//
//				rsTable.next();
//
//				int iRowCount = rsTable.getInt(1);
//
//				rsTable.close();

				//int intFileCount = 1;
				String strSql;
				int intFileCount = 0;
				int intRecordNum;

				//for (int i = 0; i < iRowCount || i == 0; i = i + 5000) {
				while(true)
				{
					intRecordNum = 0;

					strSql = "select * from " + strTableName + " limit " + 5000
							+ " offset " + intFileCount*5000;
					if(! AhBackupNewTool.isValidCoon(con))
					{
						con = oTool.initCon();
						stTable = con.createStatement();
					}			
					
					rsTable = stTable.executeQuery(strSql);

					Document document = DocumentHelper.createDocument();

					Element table = document.addElement("table").addAttribute(
							"schema", strTableName);

					ResultSetMetaData rsmd = rsTable.getMetaData();

					int iCount = rsmd.getColumnCount();

					while (rsTable.next()) {
						intRecordNum++;
						Element row = table.addElement("row");

						for (int icol = 1; icol <= iCount; icol++) {
							String newStr;

							if (rsTable.getString(icol) == null) {
								newStr = "NULL";
							} else if("null".equalsIgnoreCase(rsTable.getString(icol))){
								newStr = "_" + rsTable.getString(icol) + "_";
							}else {
								newStr = rsTable.getString(icol);
							}

							if(1 == intRecordNum) {
								row.addElement("field").addAttribute("name",
									rsmd.getColumnName(icol)).addAttribute("value",
									newStr);
							}else {
								row.addElement("field").addAttribute("value",
										newStr);
							}
						}
					}

					if(intRecordNum <= 0 && 0 != intFileCount)
						break;

					File file;

					if (intFileCount == 0) {
						file = new File(strPath + strTableName.toLowerCase()
								+ ".xml");

					} else {
						file = new File(strPath + strTableName.toLowerCase()
								+ "_" + intFileCount + ".xml");
					}

					intFileCount++;

					output = new XMLWriter(new FileOutputStream(file));

					output.write(document);

					output.close();

					if(5000 > intRecordNum)
						break;
				}

				rsTable.close();

				stTable.close();
			} catch (Exception ex) {
				BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
			}
		} catch (Exception ex) {
			// add the debug msg
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
		finally
		{
		    if(null != con)
		    {
		    	try
		    	{
		    	  con.close();
		    	}
		    	catch(Exception conex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, conex.getMessage());
		    	}
		    }

		    if(null != output)
		    {
		    	try
		    	{
		    		output.close();
		    	}
		    	catch(Exception outex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, outex.getMessage());
		    	}
		    }

		    if(null != rsTable)
		    {
		    	try
		    	{
		    		rsTable.close();
		    	}
		    	catch(Exception rsex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, rsex.getMessage());
		    	}
		    }

		    if(null != stTable)
		    {
		    	try
		    	{
		    		stTable.close();
		    	}
		    	catch(Exception stex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, stex.getMessage());
		    	}
		    }
		}
	}

	private void backupCertainDomain(String strXmlPath, HmDomain oDomain,
			int iContent) {
		Connection con = null;

		Statement st = null;

		try {
			// create domain dir with domain name
			String strDomain = strXmlPath + oDomain.getDomainName();

			File dDomain = new File(strDomain);

			dDomain.mkdirs();
			// restore the data to domain dir
//			Class.forName("org.postgresql.Driver").newInstance();
//
//			String strUrl = "jdbc:postgresql://localhost:5432/hm";
//
//			String strUsr = "hivemanager";
//
//			String strPsd = "aerohive";
//
//			con = DriverManager
//					.getConnection(strUrl, strUsr, strPsd);
			AhBackupNewTool oTool = new AhBackupNewTool();

			con = oTool.initCon();


			st = con.createStatement();

			String strSql = "select tablename from pg_tables where "
					+ "tableowner='hivemanager' order by tablename";

			ResultSet rs = st.executeQuery(strSql);

			while (rs.next()) {
				BackupTableDTO dto = new BackupTableDTO();
				String strTableName = rs.getString(1);
				dto.setTableName(strTableName);
				dto.setFilePath(strDomain);
				dto.setDomain(oDomain);
				dto.setIContext(iContent);
				eventQueue.add(dto);
			}

//			if (iContent == AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP) {
//				while (rs.next()) {
//					String strTableName = rs.getString(1);
//
//					if (strTableName
//							.equalsIgnoreCase(AhBackupTool.ahLicenseHistory)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahCurrentClient)
//							|| strTableName.equalsIgnoreCase(AP_UPDATE_RESULT)
//							|| strTableName.equalsIgnoreCase(AP_UPDATE_RESULT_CLI)) {
//						continue;
//					}
//
//					if (strTableName
//							.equalsIgnoreCase(AhBackupTool.ahAlarmTable)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahEventTable)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahAssociation)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahHistoryClient)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahNeighbor)
//							|| strTableName.equalsIgnoreCase(AhBackupTool.AP_IDP)
//							//|| strTableName
//							//		.equalsIgnoreCase(AhBackupTool.ahRadioAttribute)
//							//|| strTableName
//							//		.equalsIgnoreCase(AhBackupTool.ahRadioStats)
//							//|| strTableName
//							//		.equalsIgnoreCase(AhBackupTool.ahVifStats)
//							//|| strTableName
//							//		.equalsIgnoreCase(AhBackupTool.ahXif)
//							) {
//
//						storeSpecialTable(strTableName,strDomain, oDomain, con);
//
//						continue;
//					}
//
//					createTableFile(strTableName, strDomain, oDomain, con);
//				}
//			} else {
//				while (rs.next()) {
//					String strTableName = rs.getString(1);
//
//					if (strTableName
//							.equalsIgnoreCase(AhBackupTool.ahAlarmTable)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahEventTable)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahAssociation)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahCurrentClient)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahHistoryClient)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahNeighbor)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahRadioAttribute)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahRadioStats)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahVifStats)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahXif)
//							|| strTableName
//									.equalsIgnoreCase(AhBackupTool.ahLicenseHistory)
//							|| strTableName.equalsIgnoreCase(AP_UPDATE_RESULT)
//							|| strTableName.equalsIgnoreCase(AP_UPDATE_RESULT_CLI)
//							|| strTableName.equalsIgnoreCase(AhBackupTool.AP_IDP)) {
//
//						continue;
//					}
//
//					createTableFile(strTableName, strDomain, oDomain, con);
//				}
//			}

			st.close();

			con.close();
		} catch (Exception ex) {
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
		finally
		{
		    if(null != con)
		    {
		    	try
		    	{
		    	  con.close();
		    	}
		    	catch(Exception conex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, conex.getMessage());
		    	}
		    }

		    if(null != st)
		    {
		    	try
		    	{
		    		st.close();
		    	}
		    	catch(Exception stex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, stex.getMessage());
		    	}
		    }
		}
	}

	private void createTableFile(String strTableName, String strPath,
			HmDomain oDomain, Connection conTable) {
		if (null == oDomain || null == oDomain.getId()) {
			// add log
			BeLogTools.restoreLog(BeLogTools.ERROR, "The Domain or id of Domain is null");

			return;
		}

		if (!(strPath.substring(strPath.length() - 1).equals(File.separator) || strPath
				.substring(strPath.length() - 1).equals("/"))) {
			strPath = strPath + File.separatorChar;
		}

		if (AhBackupTool.ahDomain.equalsIgnoreCase(strTableName))
		{
			if(	AhBackupTool.ahHomeDomain.equalsIgnoreCase(oDomain
				.getDomainName())) {

		    storeDomainTable(strTableName, strPath, oDomain, conTable);

		    return;

			}

			storeCertainDomainTable(strTableName, strPath, oDomain, conTable);

			return;
		}

		if (AhBackupTool.hm_Update_version_info.equalsIgnoreCase(strTableName))
		{
		    if(AhBackupTool.ahHomeDomain.equalsIgnoreCase(oDomain
				.getDomainName())) {

		    storeDomainTable(strTableName, strPath, oDomain, conTable);

		    }

			return;
		}

		storeCommonTable(strTableName, strPath, oDomain, conTable);
	}

	private void storeBriefDomainInfo(String strXmlPath, HmDomain oDomain) {
		String strDomainTable = AhBackupTool.ahDomain.toLowerCase();

		Document document = DocumentHelper.createDocument();

		Element table = document.addElement("table").addAttribute("schema",
				strDomainTable);

		File fDomain = new File(strXmlPath + strDomainTable.toLowerCase()
				+ ".xml");

		XMLWriter output =null;

		try {
			output = new XMLWriter(new FileOutputStream(fDomain));

			Element row = table.addElement("row");

			String strDomainId = oDomain.getId().toString();

			String strDomainName = oDomain.getDomainName();

			row.addElement("field").addAttribute("name", "id").addAttribute(
					"value", strDomainId);

			row.addElement("field").addAttribute("name", "domainname")
					.addAttribute("value", strDomainName);

			output.write(document);

			output.close();
		} catch (Exception ex) {
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
		finally
		{

		    if(null != output)
		    {
		    	try
		    	{
		    		output.close();
		    	}
		    	catch(Exception outex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, outex.getMessage());
		    	}
		    }
		}
	}

	private void storeDomainTable(String strTableName, String strPath,
			HmDomain oDomain, Connection conTable) {
		String strSql = "select * from " + strTableName;

		XMLWriter output = null;

		ResultSet rsTable = null;

		Statement stTable = null;

		try {
			stTable = conTable.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

			rsTable = stTable.executeQuery(strSql);

			Document document = DocumentHelper.createDocument();

			Element table = document.addElement("table").addAttribute("schema",
					strTableName);

			File file = new File(strPath + strTableName.toLowerCase() + ".xml");

			output = new XMLWriter(new FileOutputStream(file));

			ResultSetMetaData rsmd = rsTable.getMetaData();

			int iCount = rsmd.getColumnCount();

			while (rsTable.next()) {
				Element row = table.addElement("row");

				for (int icol = 1; icol <= iCount; icol++) {
					String newStr;

					if (rsTable.getString(icol) == null) {
						newStr = "NULL";
					} else if("null".equalsIgnoreCase(rsTable.getString(icol))){
						newStr = "_" + rsTable.getString(icol) + "_";
					}else {
						newStr = rsTable.getString(icol);
					}

					row.addElement("field").addAttribute("name",
							rsmd.getColumnName(icol)).addAttribute("value",
							newStr);
				}
			}

			output.write(document);

			output.close();

			rsTable.close();

			stTable.close();
		} catch (Exception ex) {
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
		finally
		{
		    if(null != output)
		    {
		    	try
		    	{
		    		output.close();
		    	}
		    	catch(Exception outex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, outex.getMessage());
		    	}
		    }

		    if(null != rsTable)
		    {
		    	try
		    	{
		    		rsTable.close();
		    	}
		    	catch(Exception rsex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, rsex.getMessage());
		    	}
		    }

		    if(null != stTable)
		    {
		    	try
		    	{
		    		stTable.close();
		    	}
		    	catch(Exception stex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, stex.getMessage());
		    	}
		    }
		}
	}

	private void storeCertainDomainTable(String strTableName, String strPath,
			HmDomain oDomain, Connection conTable)
	{
        String strSql = "select * from " + strTableName +" where domainname="+ "'"+oDomain.getDomainName()+"'";

		XMLWriter output = null;

		ResultSet rsTable = null;

		Statement stTable = null;

		try {
			stTable = conTable.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

			rsTable = stTable.executeQuery(strSql);

			Document document = DocumentHelper.createDocument();

			Element table = document.addElement("table").addAttribute("schema",
					strTableName);

			File file = new File(strPath + strTableName.toLowerCase() + ".xml");

			output = new XMLWriter(new FileOutputStream(file));

			ResultSetMetaData rsmd = rsTable.getMetaData();

			int iCount = rsmd.getColumnCount();

			while (rsTable.next()) {
				Element row = table.addElement("row");

				for (int icol = 1; icol <= iCount; icol++) {
					String newStr;

					if (rsTable.getString(icol) == null) {
						newStr = "NULL";
					} else if("null".equalsIgnoreCase(rsTable.getString(icol))){
						newStr = "_" + rsTable.getString(icol) + "_";
					}else {
						newStr = rsTable.getString(icol);
					}

					row.addElement("field").addAttribute("name",
							rsmd.getColumnName(icol)).addAttribute("value",
							newStr);
				}
			}

			output.write(document);

			output.close();

			rsTable.close();

			stTable.close();
		} catch (Exception ex) {
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
		finally
		{
		    if(null != output)
		    {
		    	try
		    	{
		    		output.close();
		    	}
		    	catch(Exception outex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, outex.getMessage());
		    	}
		    }

		    if(null != rsTable)
		    {
		    	try
		    	{
		    		rsTable.close();
		    	}
		    	catch(Exception rsex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, rsex.getMessage());
		    	}
		    }

		    if(null != stTable)
		    {
		    	try
		    	{
		    		stTable.close();
		    	}
		    	catch(Exception stex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, stex.getMessage());
		    	}
		    }
		}
	}

	private void storeCommonTable(String strTableName, String strPath,
			HmDomain oDomain, Connection conTable) {
        XMLWriter output = null;

		ResultSet rsTable = null;

		Statement stTable = null;

		try {
			stTable = conTable.createStatement();

			boolean bOwnerFlag = false;

			String strSql = "select * from " + strTableName + " limit 1";

			rsTable = stTable.executeQuery(strSql);

			rsTable.next();

			ResultSetMetaData rsmd = rsTable.getMetaData();

			int iColumnCount = rsmd.getColumnCount();

			for (int i = 1; i <= iColumnCount; ++i) {
				if (rsmd.getColumnName(i).equalsIgnoreCase("owner")) {
					bOwnerFlag = true;

					break;
				}
			}

			rsTable.close();

//			if (!bOwnerFlag) {
//				strSql = "select count(*) from " + strTableName;
//			} else {
//				strSql = "select count(*) from " + strTableName
//						+ ", hm_domain where " + strTableName
//						+ ".owner=hm_domain.id and (" + strTableName
//						+ ".owner=" + oDomain.getId().toString()
//						+ " or hm_domain.domainname='global')";
//			}


//			rsTable = stTable.executeQuery(strSql);

//			rsTable.next();

//			int iRowCount = rsTable.getInt(1);

//			rsTable.close();

			int intFileCount = 0;
			int intRecordNum;

			strSql="select id from hm_domain where domainname='global'";
			rsTable = stTable.executeQuery(strSql);
			long  global_id = 0;

			if(rsTable.next())
			    global_id = rsTable.getLong(1);

			String strId = String.valueOf(global_id);

			while(true)
			{

		    intRecordNum = 0;
			//for (int i = 0; i < iRowCount || i == 0; i = i + 5000) {

//				if (!bOwnerFlag) {
//					strSql = "select * from " + strTableName + " limit " + 5000
//							+ " offset " + i;
//				} else {
//					strSql = "select " + strTableName + ".* from "
//							+ strTableName + ", hm_domain where "
//							+ strTableName + ".owner=hm_domain.id and ("
//							+ strTableName + ".owner="
//							+ oDomain.getId().toString()
//							+ " or hm_domain.domainname='global')" + " limit "
//							+ 5000 + " offset " + i;
//				}

				if (!bOwnerFlag) {
					strSql = "select * from " + strTableName + " limit " + 5000
							+ " offset " + intFileCount*5000;
				} else {
					strSql = "select * from "+ strTableName + " where owner="
							+ oDomain.getId().toString()+" or owner="+strId+ " limit "
							+ 5000 + " offset " + intFileCount*5000;
				}

				rsTable = stTable.executeQuery(strSql);

				Document document = DocumentHelper.createDocument();

				Element table = document.addElement("table").addAttribute(
						"schema", strTableName);

				rsmd = rsTable.getMetaData();

				int iCount = rsmd.getColumnCount();

				while (rsTable.next()) {
					intRecordNum++;
					Element row = table.addElement("row");

					for (int icol = 1; icol <= iCount; icol++) {
						String newStr;

						if (rsTable.getString(icol) == null) {
							newStr = "NULL";
						} else if("null".equalsIgnoreCase(rsTable.getString(icol))){
							newStr = "_" + rsTable.getString(icol) + "_";
						}else {
							newStr = rsTable.getString(icol);
						}

//						row.addElement("field").addAttribute("name",
//								rsmd.getColumnName(icol)).addAttribute("value",
//								newStr);
						if(1 == intRecordNum) {
							row.addElement("field").addAttribute("name",
								rsmd.getColumnName(icol)).addAttribute("value",
								newStr);
						}else {
							row.addElement("field").addAttribute("value",
									newStr);
						}
					}
				}

				if(intRecordNum <= 0 && 0 != intFileCount)
					break;

				File file;

				if (intFileCount == 0) {
					file = new File(strPath + strTableName.toLowerCase()
							+ ".xml");

				} else {
					file = new File(strPath + strTableName.toLowerCase() + "_"
							+ intFileCount + ".xml");
				}

				intFileCount++;

				output = new XMLWriter(new FileOutputStream(file));

				output.write(document);

				output.close();

				if(5000 > intRecordNum)
					break;
			}

			rsTable.close();

			stTable.close();
		} catch (Exception ex) {
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
		finally
		{
		    if(null != output)
		    {
		    	try
		    	{
		    		output.close();
		    	}
		    	catch(Exception outex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, outex.getMessage());
		    	}
		    }

		    if(null != rsTable)
		    {
		    	try
		    	{
		    		rsTable.close();
		    	}
		    	catch(Exception rsex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, rsex.getMessage());
		    	}
		    }

		    if(null != stTable)
		    {
		    	try
		    	{
		    		stTable.close();
		    	}
		    	catch(Exception stex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, stex.getMessage());
		    	}
		    }
		}
	}

	private void storeSpecialTable(String strTableName, String strPath,
			HmDomain oDomain, Connection conTable) {
        XMLWriter output = null;

		ResultSet rsTable = null;

		Statement stTable = null;

		try {
			if (!(strPath.substring(strPath.length() - 1).equals(File.separator) || strPath
					.substring(strPath.length() - 1).equals("/"))) {
				strPath = strPath + File.separatorChar;
			}


			stTable = conTable.createStatement();


//			String strSql = "select count(*) from " + strTableName
//					+ ", hm_domain where " + strTableName
//					+ ".owner=hm_domain.id and (" + strTableName + ".owner="
//					+ oDomain.getId().toString()
//					+ " or hm_domain.domainname='global')";
//
//			begin = System.currentTimeMillis();
//
//			rsTable = stTable.executeQuery(strSql);
//
//			dbtime += System.currentTimeMillis() - begin;
//
//			rsTable.next();
//
//			int iRowCount = rsTable.getInt(1);
//
//			rsTable.close();

			String strSql = "select id from " + strTableName +" where owner=" + oDomain.getId().toString()
					+ " order by id asc limit 1";

			rsTable = stTable.executeQuery(strSql);

			long  id = 0;
			if(rsTable.next())
				id = rsTable.getLong(1)-1;
//			else
//				return;

			rsTable.close();

			int intFileCount = 0;
			String strId = String.valueOf(id);
			int intRecordNum;

			while(true) {
				intRecordNum = 0;
//				strSql = "select * from " + strTableName
//						+ " where owner=" + oDomain.getId().toString()
//						+ " and id>" + strId +" order by id asc "+ " limit " + 5000;

				strSql = "select * from " + strTableName
				+ " where id > "+strId+ " and owner=" + oDomain.getId().toString()
				+ " order by id asc "+ " limit " + 5000;

				rsTable = stTable.executeQuery(strSql);

				Document document = DocumentHelper.createDocument();

				Element table = document.addElement("table").addAttribute(
						"schema", strTableName);

				ResultSetMetaData rsmd = rsTable.getMetaData();

				int iCount = rsmd.getColumnCount();

				while (rsTable.next()) {
					intRecordNum++;
					Element row = table.addElement("row");

					for (int icol = 1; icol <= iCount; icol++) {
						String newStr;

						if (rsTable.getString(icol) == null) {
							newStr = "NULL";
						} else if("null".equalsIgnoreCase(rsTable.getString(icol))){
							newStr = "_" + rsTable.getString(icol) + "_";
						}else {
							newStr = rsTable.getString(icol);
						}

						if ("id".equalsIgnoreCase(rsmd.getColumnName(icol))) {
							strId = newStr;
						}

						if(1 == intRecordNum) {
							row.addElement("field").addAttribute("name",
								rsmd.getColumnName(icol)).addAttribute("value",
								newStr);
						}else {
							row.addElement("field").addAttribute("value",
									newStr);
						}
					}
				}

				if(intRecordNum <= 0 && 0 != intFileCount)
					break;

				File file;

				if (intFileCount == 0) {
					file = new File(strPath + strTableName.toLowerCase()
							+ ".xml");

				} else {
					file = new File(strPath + strTableName.toLowerCase() + "_"
							+ intFileCount + ".xml");
				}
				intFileCount++;

				output = new XMLWriter(new FileOutputStream(file));

				output.write(document);

				output.close();

				if(5000 > intRecordNum)
					break;
			}

			rsTable.close();

			stTable.close();
		} catch (Exception ex) {
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
		finally
		{
		    if(null != output)
		    {
		    	try
		    	{
		    		output.close();
		    	}
		    	catch(Exception outex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, outex.getMessage());
		    	}
		    }

		    if(null != rsTable)
		    {
		    	try
		    	{
		    		rsTable.close();
		    	}
		    	catch(Exception rsex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, rsex.getMessage());
		    	}
		    }

		    if(null != stTable)
		    {
		    	try
		    	{
		    		stTable.close();
		    	}
		    	catch(Exception stex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, stex.getMessage());
		    	}
		    }
		}
	}

	/**
	 * process for client trap and client event
	 *
	 */
	class BackupProcessorThread extends Thread {
		public void run() {
			BeLogTools.debug(HmLogConst.M_RESTORE, "Bakup process thread begin to run");

			Connection con = null;
			try {
//				Class.forName("org.postgresql.Driver").newInstance();
//
//				String strUrl = "jdbc:postgresql://localhost:5432/hm";
//
//				String strUsr = "hivemanager";
//
//				String strPsd = "aerohive";
//
//				con = DriverManager
//						.getConnection(strUrl, strUsr, strPsd);
				AhBackupNewTool oTool = new AhBackupNewTool();

				con = oTool.initCon();

			} catch (Exception e1) {
				BeLogTools.restoreLog(BeLogTools.ERROR, "Can not get connection");
			}

			while (true) {
				try {
					BackupTableDTO dto = eventQueue.poll();
					if(dto == null)
						break;
					if (dto.getIContext() == AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP) {
						String strTableName = dto.getTableName();
						if (strTableName
								.equalsIgnoreCase(AhBackupTool.ahLicenseHistory)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahCurrentClient)
								|| strTableName
										.equalsIgnoreCase(AP_UPDATE_RESULT)
								|| strTableName
										.equalsIgnoreCase(AP_UPDATE_RESULT_CLI)
								|| strTableName.equalsIgnoreCase(ahActkey)
								|| strTableName.equalsIgnoreCase(ahHaSetting)
								|| strTableName.equalsIgnoreCase(hmOrderKeyInfo)
								|| strTableName.equalsIgnoreCase(HmOrderKeyHistory)
								|| strTableName.equalsIgnoreCase(ACM_ENTITLE_KEY_HISTORY_INFO)
								//|| strTableName.equalsIgnoreCase(HMOrderKeyApInfo)
								) {
							continue;
						}

						if (strTableName
								.equalsIgnoreCase(AhBackupTool.ahAlarmTable)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahEventTable)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahAssociation)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahHistoryClient)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahNeighbor)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.AP_IDP)
						// || strTableName
						// .equalsIgnoreCase(AhBackupTool.ahRadioAttribute)
						// || strTableName
						// .equalsIgnoreCase(AhBackupTool.ahRadioStats)
						// || strTableName
						// .equalsIgnoreCase(AhBackupTool.ahVifStats)
						// || strTableName
						// .equalsIgnoreCase(AhBackupTool.ahXif)
						) {

							storeSpecialTable(strTableName, dto.getFilePath(),
									dto.getDomain(), con);

							continue;
						}

						createTableFile(strTableName, dto.getFilePath(), dto
								.getDomain(), con);
					} else {
						String strTableName = dto.getTableName();

						if (strTableName
								.equalsIgnoreCase(AhBackupTool.ahAlarmTable)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahEventTable)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahAssociation)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahCurrentClient)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahHistoryClient)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahNeighbor)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahRadioAttribute)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahRadioStats)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahVifStats)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahXif)
								|| strTableName
										.equalsIgnoreCase("HM_ACSPNEIGHBOR")
								|| strTableName
										.equalsIgnoreCase("HM_BANDWIDTHSENTINEL_HISTORY")
								|| strTableName
										.equalsIgnoreCase("HM_INTERFERENCESTATS")
								|| strTableName
										.equalsIgnoreCase("HM_LATESTACSPNEIGHBOR")
								|| strTableName
										.equalsIgnoreCase("HM_LATESTINTERFERENCESTATS")
								|| strTableName
										.equalsIgnoreCase("HM_LATESTNEIGHBOR")
								|| strTableName
										.equalsIgnoreCase("HM_LATESTRADIOATTRIBUTE")
								|| strTableName
										.equalsIgnoreCase("HM_LATESTXIF")
								|| strTableName
										.equalsIgnoreCase("HM_PCIDATA")
								|| strTableName
										.equalsIgnoreCase("HM_VPNSTATUS")
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.ahLicenseHistory)
								|| strTableName
										.equalsIgnoreCase(AP_UPDATE_RESULT)
								|| strTableName
										.equalsIgnoreCase(AP_UPDATE_RESULT_CLI)
								|| strTableName
										.equalsIgnoreCase(AhBackupTool.AP_IDP)
								|| strTableName.equalsIgnoreCase(ahActkey)
								|| strTableName.equalsIgnoreCase(ahHaSetting)
								|| strTableName.equalsIgnoreCase(hmOrderKeyInfo)
								|| strTableName.equalsIgnoreCase(HmOrderKeyHistory)
								|| strTableName.equalsIgnoreCase(ACM_ENTITLE_KEY_HISTORY_INFO)
								//|| strTableName.equalsIgnoreCase(HMOrderKeyApInfo)
								) {

							continue;
						}

						createTableFile(strTableName, dto.getFilePath(), dto
								.getDomain(), con);
					}
				} catch (Exception e) {
					BeLogTools.restoreLog(BeLogTools.ERROR, e.getMessage());
				}
			}
			try {
				if(null != con)
					con.close();
			} catch (SQLException e) {
			}
			BeLogTools.debug(HmLogConst.M_RESTORE, "Bakup process thread end");
		}
	}
}

class BackupTableDTO
{
	private String tableName;
	private String filePath;
	private HmDomain domain;
	int		iContext;
	public int getIContext() {
		return iContext;
	}
	public void setIContext(int context) {
		iContext = context;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public HmDomain getDomain() {
		return domain;
	}
	public void setDomain(HmDomain domain) {
		this.domain = domain;
	}

}