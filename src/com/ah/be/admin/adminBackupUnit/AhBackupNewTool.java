package com.ah.be.admin.adminBackupUnit;

import static com.ah.be.log.HmLogConst.M_RESTORE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.ah.be.admin.restoredb.AhRestoreDBTools;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.AhScheduleBackupData;
//import java.io.IOException;


public class AhBackupNewTool {

	private static final String ahDomain = "HM_DOMAIN";
	private static final String ahHomeDomain = "home";
	private static final String ahFeaturePermission = "HM_FEATURE_PERMISSION";
	private static final String hm_Update_version_info = "hm_updatesoftwareinfo";
	

	//db value
	private static final String db_driver         = "org.postgresql.Driver";
	private static        String db_url            = "jdbc:postgresql://localhost:5432/hm";
	//private static final String db_url            = "dbc:postgresql://10.155.20.227:5432/hm";
	private static  String db_usr            = "hivemanager";
	private static  String db_psd            = "aerohive";
	private static final long   RESTORE_FULL_DATA = -1;

	//private values
    private BlockingQueue<BackupTableDTO2>		eventQueue  = new LinkedBlockingQueue<BackupTableDTO2>();
	private BackupProcessorThread[] 	threadArray = null;
	private int			BAKCUP_THREAD_NUM = 2;
	
	//log current backup vhm name
	private String backupVHMName;

	public AhBackupNewTool(){}
	public static void main(String[] args)
	{
		String[] strTmp = AhBackupNewTool.getDBUserAndPsd("/root/workspace/pro_nms_head/webapps/WEB-INF/classes/resources/hmConfig.properties");
		System.out.println(strTmp[0]);
		System.out.println(strTmp[1]);
	}

	public Connection initCon()
	{
		String strCfgFile = System.getenv("HM_ROOT")+"/WEB-INF/classes/hibernate.cfg.xml";
		db_url = AhBackupNewTool.getUrlFromFile(strCfgFile);
		String strProfile = System.getenv("HM_ROOT")+"/WEB-INF/classes/resources/hmConfig.properties";
		String[] strUserPsd = AhBackupNewTool.getDBUserAndPsd(strProfile);
		db_usr = strUserPsd[0];
		db_psd = strUserPsd[1];
		Connection con = null;

		try {
			Class.forName(db_driver).newInstance();
			Properties pros = new Properties();
			pros.setProperty("user", db_usr);
			pros.setProperty("password", db_psd);
			pros.setProperty("tcpKeepAlive", "true");
		    //con = DriverManager
			//	.getConnection(db_url, db_usr, db_psd);
			
			con = DriverManager
			.getConnection(db_url, pros);
			
		} catch (Exception e1) {
			BeLogTools.error(HmLogConst.M_RESTORE, e1.getMessage(),e1);
		}

		return con;
	}

	public static boolean isValidCoon(Connection con)
	{
		try
		{
			Statement stTable = con.createStatement();
			stTable.close();
			return true;
		}
		catch(Exception ex)
		{
			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
			return false;
		}
	}
	
	private void freeCon(Connection con)
	{
		try {
			if(null != con)
				con.close();
		} catch (Exception ex) {
			BeLogTools.error(HmLogConst.M_RESTORE,ex);
		}
	}

	private void storeBriefDomainInfo(String strXmlPath,long lDomainId, String strDomainName)
	{
		String strDomainTable = AhBackupNewTool.ahDomain.toLowerCase();

		File fDomain = new File(strXmlPath + File.separator+strDomainTable+ ".xml");

		Document document = DocumentHelper.createDocument();

		Element table = document.addElement("table").addAttribute("schema",
				strDomainTable);

       XMLWriter output =null;

		try {
			output = new XMLWriter(new FileOutputStream(fDomain));

			Element row = table.addElement("row");

			String strDomainId = Long.toString(lDomainId);

			row.addElement("field").addAttribute("name", "id").addAttribute(
					"value", strDomainId);

			row.addElement("field").addAttribute("name", "domainname")
					.addAttribute("value", strDomainName);

			output.write(document);

			output.close();
		} catch (Exception ex) {
			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage());
		}
		finally
		{
			free(output, null,null);
		}
	}


	private void initTables(String strXmlPath, long lId, int iContent)
	{
        Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		con = initCon();

		if(null == con)
		{
			return ;
		}

		try
		{
			String strSql = "";
			st = con.createStatement();

			if(lId == RESTORE_FULL_DATA)
			{
				strSql = "/*NO LOAD BALANCE*/select id, domainname from hm_domain where domainname='home'";
			}
			else
			{
				strSql = "/*NO LOAD BALANCE*/select id, domainname from hm_domain where id="+lId;
			}
			long lDomainId=lId;
			String strDomainName = "home";
			rs = st.executeQuery(strSql);
			while(rs.next())
			{
				lDomainId = rs.getLong(1);
				strDomainName = rs.getString(2);
			}

			storeBriefDomainInfo(strXmlPath,lDomainId,strDomainName);

			//new struct flag
			try
			{
				String struct_flag=strXmlPath+AhRestoreDBTools.HM_NEW_BACKUP_STRUCT_FLAG;
				File file_flag = new File(struct_flag);
				file_flag.createNewFile();
			}
			catch(Exception fex)
			{
				BeLogTools.error(HmLogConst.M_RESTORE, fex);
			}

			// create domain dir with domain name
			String strDomain = strXmlPath + strDomainName;
			File dDomain = new File(strDomain);
			dDomain.mkdirs();

			strSql = "/*NO LOAD BALANCE*/select tablename from pg_tables where "
				+ "tableowner='hivemanager' and schemaname='public' " +
				"and tablename !~* '^[a-z0-9_].*_[0-9]{4,10}_[0-9]{4,10}' order by tablename";
			
			if (! AhBackupNewTool.isValidCoon(con))
			{
				con = initCon();
			}
			rs = st.executeQuery(strSql);

			while (rs.next()) {
				BackupTableDTO2 dto = new BackupTableDTO2();
				String strTableName = rs.getString(1);
				dto.setTableName(strTableName);
				dto.setFilePath(strDomain);
				dto.setDomainId(lDomainId);
				dto.setDomainName(strDomainName);
				dto.setIContext(iContent);
				eventQueue.add(dto);
			}
			
			backupVHMName = strDomainName;
		}
		catch(Exception ex)
		{
			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage());
		}
		finally
		{
			free(null, rs, st);
			freeCon(con);
		}

	}


//	private int initDoc(ResultSet rsTable, Document document, String strTableName)
//	{
//		int intRecordNum = 0;
//
//		try
//		{
//			Element table = document.addElement("table").addAttribute(
//					"schema", strTableName);
//			ResultSetMetaData rsmd = rsTable.getMetaData();
//			int iCount = rsmd.getColumnCount();
//
//			while (rsTable.next())
//			{
//			    intRecordNum++;
//
//				Element row = table.addElement("row");
//
//				for (int icol = 1; icol <= iCount; icol++)
//				{
//					String newStr;
//
//					if (rsTable.getString(icol) == null) {
//						newStr = "NULL";
//					}else if("null".equalsIgnoreCase(rsTable.getString(icol))){
//						newStr = "_" + rsTable.getString(icol) + "_";
//					}else {
//						newStr = rsTable.getString(icol);
//					}
//
//
//					if(1 == intRecordNum) {
//						row.addElement("field").addAttribute("name",
//							rsmd.getColumnName(icol)).addAttribute("value",
//							newStr);
//					}else {
//						row.addElement("field").addAttribute("value",
//								newStr);
//					}
//				}
//			}
//		}
//		catch(Exception ex)
//		{
//			 BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage());
//			 return 0;
//		}
//
//		return intRecordNum;
//	}

	private void  free(XMLWriter output, ResultSet rsTable, Statement stTable)
	{
		if(null != output)
	    {
	    	try
	    	{
	    		output.close();
	    	}
	    	catch(Exception outex)
	    	{
	    	  BeLogTools.error(HmLogConst.M_RESTORE, outex.getMessage());
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
	    		BeLogTools.error(HmLogConst.M_RESTORE, rsex.getMessage());
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
	    		BeLogTools.error(HmLogConst.M_RESTORE, stex.getMessage(),stex);
	    	}
	    }
	}

	private boolean backupTable3File(String strTableName, String strPath, Connection conTable, String strSql, String orderBy, String owner)
	{
        XMLWriter output = null;
		ResultSet rsTable = null;
		Statement stTable = null;
		//Statement stLock  = null;
		//String strLock = "lock "+strTableName+" in exclusive mode";
		String[] primaryOrderByCols = null;
		List<Object> tempPrimaryOrderValue = new ArrayList<Object>();
		try
		{
			if (! AhBackupNewTool.isValidCoon(conTable))
			{
			    conTable = initCon();				
			}
			conTable.setAutoCommit(false);
			stTable = conTable.createStatement();
			//don't lock table
			//stTable.execute(strLock);
            //Thread.sleep(2*60*1000);
			if(null != orderBy){
				primaryOrderByCols = orderBy.split(",");
			}
			int intRecordNum = 0;
			int intFileCount = 0;
			String whereConditions = null;
			while(true)
			{
				intRecordNum = 0;
				whereConditions = null;
				if(tempPrimaryOrderValue != null && tempPrimaryOrderValue.size() > 0){
			    	 for(int i= 0 ; i < tempPrimaryOrderValue.size(); i++){
			    		 if(null == whereConditions){
			    				whereConditions = primaryOrderByCols[i] + ">'" + tempPrimaryOrderValue.get(i) + "'";
				    	 }else{
				    		 String otherCons = null;
				    		 for(int j=0; j<i; j++){
				    			 if(null == otherCons){
				    				 otherCons = primaryOrderByCols[j] + "='" + tempPrimaryOrderValue.get(j) + "'";
				    			 }else{
				    				 otherCons += " and " + primaryOrderByCols[j] + "='"+tempPrimaryOrderValue.get(j)+"'";
				    			 }
				    		 }
				    		 whereConditions += " or " + "(" + otherCons + " and " + primaryOrderByCols[i] + ">'" + tempPrimaryOrderValue.get(i) + "')";
				    	 }
			    	 }
			     }
				
				String strSqls = strSql;
				if(intFileCount == 0){
					if(null != owner){
						strSqls = strSqls+ " where " +owner+ " order by " +orderBy+ " limit 5000";
					}else{
						strSqls = strSqls+ " order by " +orderBy+ " limit 5000";
					}
				}else{
					if(null != owner){
						strSqls = strSqls+ " where " +whereConditions+ " and " +owner+ " order by " +orderBy+ " limit 5000";
					}else{
						strSqls = strSqls+ " where " +whereConditions+ " order by " +orderBy+ " limit 5000";
					}
				}
				
				if (! AhBackupNewTool.isValidCoon(conTable))
				{
					conTable = initCon();
					conTable.setAutoCommit(false);
					stTable = conTable.createStatement();
				}
				stTable.setFetchSize(2500);
				rsTable = stTable.executeQuery(strSqls);

				Document document = DocumentHelper.createDocument();
//				int intRecordNum = initDoc(rsTable, document, strTableName);
				
				try
				{
					Element table = document.addElement("table").addAttribute(
							"schema", strTableName);
					ResultSetMetaData rsmd = rsTable.getMetaData();
					int iCount = rsmd.getColumnCount();
					
					while (rsTable.next())
					{
						List<Object> searchLastItem = new ArrayList<Object>();
					    intRecordNum++;

						Element row = table.addElement("row");

						for (int icol = 1; icol <= iCount; icol++)
						{
							String newStr;

							if (rsTable.getString(icol) == null) {
								newStr = "NULL";
							}else if("null".equalsIgnoreCase(rsTable.getString(icol))){
								newStr = "_" + rsTable.getString(icol) + "_";
							}else {
								newStr = rsTable.getString(icol);
							}
							
							if(rsTable.isLast()){
								for(int m=0; m<primaryOrderByCols.length; m++){
									if(primaryOrderByCols[m].equalsIgnoreCase(rsmd.getColumnName(icol))){
											searchLastItem.add(newStr);
									}
								}
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
						tempPrimaryOrderValue = searchLastItem;
					}
				}
				catch(Exception ex)
				{
					 BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage());
				}

				if(intRecordNum <= 0)
					break;

				File file;

				if (intFileCount == 0) {
					file = new File(strPath +File.separator+ strTableName.toLowerCase()
							+ ".xml");

				} else {
					file = new File(strPath +File.separator+ strTableName.toLowerCase()
							+ "_" + intFileCount + ".xml");
				}

				output = new XMLWriter(new FileOutputStream(file));

				output.write(document);

				output.close();

				intFileCount++;

				if(5000 > intRecordNum)
					break;
			}
			rsTable.close();
			stTable.close();
			return true;
		}
		catch(Exception ex)
		{
			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
			return false;
		}
		finally
		{
			try
			{
				conTable.commit();
			}
			catch(Exception ex)
			{
				BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
			}
			free(output, rsTable, stTable);
		}
	}
//	private boolean backupTable2File(String strTableName, String strPath, Connection conTable, String strSql)
//	{
//        XMLWriter output = null;
//		ResultSet rsTable = null;
//		Statement stTable = null;
//		//Statement stLock  = null;
//		//String strLock = "lock "+strTableName+" in exclusive mode";
//
//		try
//		{
//			if (! AhBackupNewTool.isValidCoon(conTable))
//			{
//			    conTable = initCon();				
//			}
//			conTable.setAutoCommit(false);
//			stTable = conTable.createStatement();
//			//don't lock table
//			//stTable.execute(strLock);
//            //Thread.sleep(2*60*1000);
//			int intFileCount = 0;
//			while(true)
//			{
//				String strSqls = strSql + intFileCount*5000;
//				
//				if (! AhBackupNewTool.isValidCoon(conTable))
//				{
//					conTable = initCon();
//					conTable.setAutoCommit(false);
//					stTable = conTable.createStatement();
//				}
//				rsTable = stTable.executeQuery(strSqls);
//
//				Document document = DocumentHelper.createDocument();
//				int intRecordNum = initDoc(rsTable, document, strTableName);
//
//				if(intRecordNum <= 0)
//					break;
//
//				File file;
//
//				if (intFileCount == 0) {
//					file = new File(strPath +File.separator+ strTableName.toLowerCase()
//							+ ".xml");
//
//				} else {
//					file = new File(strPath +File.separator+ strTableName.toLowerCase()
//							+ "_" + intFileCount + ".xml");
//				}
//
//				output = new XMLWriter(new FileOutputStream(file));
//
//				output.write(document);
//
//				output.close();
//
//				intFileCount++;
//
//				if(5000 > intRecordNum)
//					break;
//			}
//			rsTable.close();
//			stTable.close();
//			return true;
//		}
//		catch(Exception ex)
//		{
//			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
//			return false;
//		}
//		finally
//		{
//			try
//			{
//				conTable.commit();
//			}
//			catch(Exception ex)
//			{
//				BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
//			}
//			free(output, rsTable, stTable);
//		}
//	}

	private boolean isIncludeOwner(String strTableName, Connection conTable)
	{
		boolean bOwnerFlag = false;

        ResultSet rsTable = null;
		Statement stTable = null;

		try
		{
			if (! AhBackupNewTool.isValidCoon(conTable))
			{
				conTable = initCon();				
			}
			
			stTable = conTable.createStatement();

			String strSql = "/*NO LOAD BALANCE*/select * from " + strTableName + " limit 1";

			
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
			stTable.close();

			return bOwnerFlag;
		}
		catch(Exception ex)
		{
			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
			return false;
		}
		finally
		{
			free(null, rsTable, stTable);
		}
	}

	private String getGlobalDomainID(Connection conTable)
	{
		ResultSet rsTable = null;
		Statement stTable = null;

		String strSql="/*NO LOAD BALANCE*/select id from hm_domain where domainname='global'";

		String strId = "0";
		try
		{
			long  global_id = 0;
			if (! AhBackupNewTool.isValidCoon(conTable))
			{
				conTable = initCon();				
			}
			stTable = conTable.createStatement();
			rsTable = stTable.executeQuery(strSql);

			if(rsTable.next())
			    global_id = rsTable.getLong(1);

			strId = String.valueOf(global_id);
			return strId;
		}
		catch(Exception ex)
		{
			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
			return strId;
		}
		finally
		{
			free(null, rsTable, stTable);
		}
	}

	private boolean backupTablewithID(String strTableName, String strPath,
			Connection conTable, String strDomainName, long lDomainId)
	{
        XMLWriter output = null;
		ResultSet rsTable = null;
		Statement stTable = null;

		try
		{
			if (!(strPath.substring(strPath.length() - 1).equals(File.separator) || strPath
					.substring(strPath.length() - 1).equals("/"))) {
				strPath = strPath + File.separatorChar;
			}

			String strSql = "";
			long  id = 0;
			int intFileCount = 0;
			String strId = String.valueOf(id);
			int intRecordNum = 0;
			
			while(true) {

				intRecordNum = 0;

				if(AhBackupNewTool.ahHomeDomain.equalsIgnoreCase(strDomainName))
				{
					if(intFileCount == 0){
						strSql = "/*NO LOAD BALANCE*/select * from " + strTableName + " order by id asc "+ " limit " + 5000;
					}else{
						strSql = "/*NO LOAD BALANCE*/select * from " + strTableName + " where id > "+strId+ " order by id asc "+ " limit " + 5000;
					}
				}
				else
				{
					if(intFileCount == 0){
						strSql = "/*NO LOAD BALANCE*/select * from " + strTableName + " where owner=" + lDomainId + " order by id asc "+ " limit " + 5000;
					}else{
						strSql = "/*NO LOAD BALANCE*/select * from " + strTableName + " where id > "+strId+ " and owner=" + lDomainId + " order by id asc "+ " limit " + 5000;
					}
					
				}

				if (! AhBackupNewTool.isValidCoon(conTable))
				{
					conTable = initCon();
					stTable = conTable.createStatement();
				}else{
					stTable = conTable.createStatement();
				}
				stTable.setFetchSize(2500);
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

				if(intRecordNum <= 0 )
					break;

				File file;

				if (intFileCount == 0) {
					file = new File(strPath + File.separator+strTableName.toLowerCase()
							+ ".xml");

				} else {
					file = new File(strPath + File.separator+strTableName.toLowerCase() + "_"
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

			return true;
		} catch (Exception ex) {
			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
			return false;
		}
		finally
		{
			free(output, rsTable, stTable);
		}
	}

	private boolean backupCommTalbe(String strTableName, String strPath, Connection conTable, String strOrderby)
	{
//		String strSql = "";
//		if(null == strOrderby)
//        {
//			 strSql = "select * from " + strTableName + " limit " + 5000 + " offset ";
//        }else{
//        	strSql = "select * from " + strTableName + " order by " + strOrderby + " limit " + 5000 + " offset ";
//        }

//		return backupTable2File(strTableName, strPath, conTable, strSql);
		String strSql = "/*NO LOAD BALANCE*/select * from " + strTableName;
		return backupTable3File(strTableName, strPath, conTable, strSql, strOrderby, null);
	}

	private boolean backupDomainTable(String strTableName, String strPath, Connection conTable, long lId,String orderby)
	{
		String strSql = "";

		if(!isIncludeOwner(strTableName, conTable))
		{
//			if(null == orderby)
//				strSql = "select * from " + strTableName + " limit " + 5000 + " offset ";
//			else
//				strSql = "select * from " + strTableName + " order by " + orderby + " limit " + 5000 + " offset ";
			strSql = "/*NO LOAD BALANCE*/select * from " + strTableName;
			return  backupTable3File(strTableName, strPath, conTable, strSql, orderby, null);
		}
		else
		{
//			if(null == orderby)
//				strSql = "select * from "+ strTableName + " where owner="
//			          + lId+" or owner="+getGlobalDomainID(conTable)+ " limit "+ 5000 + " offset ";
//			else
//				strSql = "select * from "+ strTableName + " where owner="
//				          + lId+" or owner="+getGlobalDomainID(conTable) + " order by " + orderby + " limit "+ 5000 + " offset ";
			strSql = "/*NO LOAD BALANCE*/select * from "+ strTableName;
			return  backupTable3File(strTableName, strPath, conTable, strSql, orderby,"(owner="+ lId+" or owner="+getGlobalDomainID(conTable)+")");
		}

//		return  backupTable2File(strTableName, strPath, conTable, strSql, orderby);
	}

	private boolean backupFuncPermTable(String strTableName,String strPath, Connection conTable, String strDomainName, long lId)
	{
		ResultSet rsTable = null;
		String strOrderby = null;
		XMLWriter output = null;
		Statement stTable = null;
		String[] primaryOrderByCols = null;
		List<Object> tempPrimaryOrderValue = new ArrayList<Object>();
	     try {
	    	 if (! AhBackupNewTool.isValidCoon(conTable))
			{
				conTable = initCon();				
			}
			rsTable = conTable.getMetaData().getPrimaryKeys(null, null, "hm_feature_permission");
			 while (rsTable.next())
			 {
				String str = rsTable.getString("COLUMN_NAME");
				if(null == strOrderby)
				{
					strOrderby = str;
				}
				else
				{
					strOrderby += ","+str;
				}
			 }
		} catch (SQLException e) {
			BeLogTools.error(HmLogConst.M_RESTORE, e.getMessage(),e);
		}
	     
	    primaryOrderByCols = strOrderby.split(",");
	    String strSql = "";

		if(AhBackupNewTool.ahHomeDomain.equalsIgnoreCase(strDomainName))
		{
			strSql = "/*NO LOAD BALANCE*/select hm_feature_permission.* " +
					"from hm_feature_permission, hm_user_group " +
					"where  hm_feature_permission.hm_user_group_id=hm_user_group.id " +
					"and hm_user_group.defaultflag=false ";
		}
		else
		{
			strSql = "/*NO LOAD BALANCE*/select hm_feature_permission.* " +
					"from hm_feature_permission, hm_user_group " +
					"where  hm_feature_permission.hm_user_group_id=hm_user_group.id " +
					"and hm_user_group.defaultflag=false and owner = "+lId;
		}
	    
	    try
		{
			if (! AhBackupNewTool.isValidCoon(conTable))
			{
			    conTable = initCon();				
			}
			conTable.setAutoCommit(false);
			int intRecordNum = 0;
			int intFileCount = 0;
			String whereConditions = null;
			while(true)
			{
				intRecordNum = 0;
				whereConditions = null;
				if(tempPrimaryOrderValue != null && tempPrimaryOrderValue.size() > 0){
			    	 for(int i= 0 ; i < tempPrimaryOrderValue.size(); i++){
			    		 if(null == whereConditions){
			    				whereConditions = "hm_feature_permission." + primaryOrderByCols[i] + ">'" + tempPrimaryOrderValue.get(i) + "'";
				    	 }else{
				    		 String otherCons = null;
				    		 for(int j=0; j<i; j++){
				    			 if(null == otherCons){
				    				 otherCons = "hm_feature_permission." + primaryOrderByCols[j] + "='" + tempPrimaryOrderValue.get(j) + "'";
				    			 }else{
				    				 otherCons += " and " + "hm_feature_permission." + primaryOrderByCols[j] + "='"+tempPrimaryOrderValue.get(j)+"'";
				    			 }
				    		 }
				    		 whereConditions += " or " + "(" + otherCons + " and " + "hm_feature_permission." + primaryOrderByCols[i] + ">'" + tempPrimaryOrderValue.get(i) + "')";
				    	 }
			    	 }
			     }
				
				String strSqls = strSql;
				if(intFileCount == 0){
						strSqls = strSqls+ " order by " +strOrderby+ " limit 5000";
				}else{
						strSqls = strSqls+ " and " +whereConditions+ " order by " +strOrderby+ " limit 5000";
				}
				
				if (! AhBackupNewTool.isValidCoon(conTable))
				{
					conTable = initCon();
					conTable.setAutoCommit(false);
					stTable = conTable.createStatement();
				}else{
					stTable = conTable.createStatement();
				}
				stTable.setFetchSize(2500);
				rsTable = stTable.executeQuery(strSqls);

				Document document = DocumentHelper.createDocument();
//				int intRecordNum = initDoc(rsTable, document, strTableName);
				
				try
				{
					Element table = document.addElement("table").addAttribute(
							"schema", strTableName);
					ResultSetMetaData rsmd = rsTable.getMetaData();
					int iCount = rsmd.getColumnCount();
					
					while (rsTable.next())
					{
						List<Object> searchLastItem = new ArrayList<Object>();
					    intRecordNum++;

						Element row = table.addElement("row");

						for (int icol = 1; icol <= iCount; icol++)
						{
							String newStr;

							if (rsTable.getString(icol) == null) {
								newStr = "NULL";
							}else if("null".equalsIgnoreCase(rsTable.getString(icol))){
								newStr = "_" + rsTable.getString(icol) + "_";
							}else {
								newStr = rsTable.getString(icol);
							}
							
							if(rsTable.isLast()){
								for(int m=0; m<primaryOrderByCols.length; m++){
									if(primaryOrderByCols[m].equalsIgnoreCase(rsmd.getColumnName(icol))){
											searchLastItem.add(newStr);
									}
								}
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
						tempPrimaryOrderValue = searchLastItem;
					}
				}
				catch(Exception ex)
				{
					 BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage());
				}

				if(intRecordNum <= 0)
					break;

				File file;

				if (intFileCount == 0) {
					file = new File(strPath +File.separator+ strTableName.toLowerCase()
							+ ".xml");

				} else {
					file = new File(strPath +File.separator+ strTableName.toLowerCase()
							+ "_" + intFileCount + ".xml");
				}

				output = new XMLWriter(new FileOutputStream(file));

				output.write(document);

				output.close();

				intFileCount++;

				if(5000 > intRecordNum)
					break;
			}
			rsTable.close();
			stTable.close();
			return true;
		}
		catch(Exception ex)
		{
			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
			return false;
		}
		finally
		{
			try
			{
				conTable.commit();
			}
			catch(Exception ex)
			{
				BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
			}
			free(output, rsTable, stTable);
		}
		
//		String strSql = "";
//
//		if(AhBackupNewTool.ahHomeDomain.equalsIgnoreCase(strDomainName))
//		{
//			strSql = "select hm_feature_permission.* " +
//					"from hm_feature_permission, hm_user_group " +
//					"where  hm_feature_permission.hm_user_group_id=hm_user_group.id " +
//					"and hm_user_group.defaultflag=false limit "+ 5000 + " offset ";
//		}
//		else
//		{
//			strSql = "select hm_feature_permission.* " +
//					"from hm_feature_permission, hm_user_group " +
//					"where  hm_feature_permission.hm_user_group_id=hm_user_group.id " +
//					"and hm_user_group.defaultflag=false and owner = "+lId+
//					" limit "+ 5000 + " offset ";
//		}

//		return backupTable2File(strTableName, strPath, conTable, strSql);
	}

	private boolean backupTable(String strTableName, String strPath,
			                       Connection conTable, String strDomainName, long lDomainId)
	{
		ResultSet rsTable = null;
		String strOrderby = null;
	     try {
	    	 if (! AhBackupNewTool.isValidCoon(conTable))
			{
				conTable = initCon();				
			}
			rsTable = conTable.getMetaData().getPrimaryKeys(null, null, strTableName);
			 while (rsTable.next())
			 {
				String str = rsTable.getString("COLUMN_NAME");
				if(null == strOrderby)
				{
					strOrderby = str;
				}
				else
				{
					strOrderby += ","+str;
				}
			 }
		} catch (SQLException e) {
			BeLogTools.error(HmLogConst.M_RESTORE, e.getMessage(),e);
		}
	     //for filter have no primary key temporary table
	     if(null == strOrderby){
		    	return false;
		    }

		if(	AhBackupNewTool.ahHomeDomain.equalsIgnoreCase(strDomainName))
		{
			//home - whole backup

			if (AhBackupNewTool.ahDomain.equalsIgnoreCase(strTableName))
			{
//				String strSql = "";
//				 if(null == strOrderby)
//                 {
//					 strSql = "select * from " + strTableName + " limit 5000 offset ";
//                 }else{
//                	 strSql = "select * from " + strTableName +" order by "+strOrderby+ " limit 5000 offset ";
//                 }
//				return backupTable2File(strTableName, strPath, conTable, strSql);
				String strSql = "/*NO LOAD BALANCE*/select * from " + strTableName;
				return backupTable3File(strTableName, strPath, conTable, strSql, strOrderby, null);
			}

			return backupCommTalbe(strTableName, strPath, conTable,strOrderby);
		}
		else
		{
			//domain - certain backup
			if (AhBackupNewTool.ahDomain.equalsIgnoreCase(strTableName))
			{
//				String strSql = "";
//				 if(null == strOrderby)
//                {
//					strSql = "select * from " + strTableName +" where domainname="+ "'"+strDomainName+"'"+ " limit 5000 offset ";
//                }else{
//                	strSql = "select * from " + strTableName +" where domainname="+ "'"+strDomainName+"'"+" order by "+strOrderby + " limit 5000 offset ";
//                }
//				return backupTable2File(strTableName, strPath, conTable, strSql);
				String strSql = "/*NO LOAD BALANCE*/select * from " + strTableName;
				return backupTable3File(strTableName, strPath, conTable, strSql, strOrderby, "domainname="+ "'"+strDomainName+"'");
			}

			if (AhBackupNewTool.hm_Update_version_info.equalsIgnoreCase(strTableName))
			{
				return true;
			}

			return backupDomainTable(strTableName, strPath, conTable, lDomainId,strOrderby);
		}
	}
	/*/
	 * @description get url from hivernate.cfg.xml
	 * @input file
	 * @outpu usr
	 */
	public static String getUrlFromFile(String sFile)
	{
		String strPrefix="jdbc:postgresql:";
		String strReturn="jdbc:postgresql://localhost:5432/hm";

		try
		{
			File fcfg = new File(sFile);
			if(fcfg.isDirectory() || !fcfg.exists())
			{
				//file is not exist
				BeLogTools.error(HmLogConst.M_RESTORE, sFile+" is not exist.");

				return 	strReturn;
			}

			FileReader frcfg = new FileReader(fcfg);
			BufferedReader brcfg = new BufferedReader(frcfg);

			String strTmp;

			while ((strTmp = brcfg.readLine()) != null)
			{
				if(strTmp.trim().indexOf(strPrefix) != -1)
				{
					strReturn = strTmp.trim();
					if(strReturn.indexOf(">") != -1)
					{
						strReturn = strReturn.substring(strReturn.indexOf(">")+1);
						if(strReturn.indexOf("<") != -1)
						{
							strReturn = strReturn.substring(0, strReturn.indexOf("<"));
							strReturn = strReturn.trim();
						}
					}
					break;
				}
			}

			brcfg.close();
			frcfg.close();

		}catch(Exception ex)
		{
			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(), ex);
		}


		return strReturn;
	}

	/*/
	 * @description get username & password from hmconfig.properties
	 * @input file
	 * @outpu string[] 0:user 1:password
	 */
	public static String[] getDBUserAndPsd(String sFile)
	{
		String[] strProperty = new String[2];
	    strProperty[0] = "hivemanager";
	    strProperty[1] = "aerohive";
	    String strUserPrefix = "hm.connection.username";
	    String strPsdPrefix  = "hm.connection.password";

	    try
	    {
	    	File fcfg = new File(sFile);
			if(fcfg.isDirectory() || !fcfg.exists())
			{
				//file is not exist
				BeLogTools.error(HmLogConst.M_RESTORE, sFile+" is not exist.");

				return 	strProperty;
			}

			FileReader frcfg = new FileReader(fcfg);
			BufferedReader brcfg = new BufferedReader(frcfg);

			String strTmp;

			while ((strTmp = brcfg.readLine()) != null)
			{
				if(strTmp.trim().indexOf(strUserPrefix) != -1)
				{
					strTmp = strTmp.trim();
					strTmp = strTmp.substring(strTmp.indexOf("=")+1);
		            strProperty[0] = strTmp;
		            continue;
				}

				if(strTmp.trim().indexOf(strPsdPrefix) != -1)
				{
					strTmp = strTmp.trim();
					strTmp = strTmp.substring(strTmp.indexOf("=")+1);
		            strProperty[1] = strTmp;
		            continue;
				}
			}

			brcfg.close();
			frcfg.close();
	    }
	    catch(Exception ex)
	    {
	    	BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(),ex);
	    }

	    return strProperty;
	}

	public void backupWholeData(String strXmlPath, int iContent)
	{
		if (null == strXmlPath) {
			// add the debug log
			BeLogTools.error(HmLogConst.M_RESTORE, "the parameter is error,Domainid or file path is null");
			return;
		}

		if (!(strXmlPath.substring(strXmlPath.length() - 1).equals(
				File.separator) || strXmlPath
				.substring(strXmlPath.length() - 1).equals("/"))) {
			strXmlPath = strXmlPath + File.separatorChar;
		}

		BeLogTools.debug(HmLogConst.M_RESTORE, "Begin to backup full database");

		initTables(strXmlPath, RESTORE_FULL_DATA,iContent);
		
		BeLogTools.debug(HmLogConst.M_RESTORE, "The VHM '"+backupVHMName+"' begin to backup...");
		
		threadArray = new BackupProcessorThread[BAKCUP_THREAD_NUM];
		for(int i = 0; i < BAKCUP_THREAD_NUM; i++) {
			threadArray[i] = new BackupProcessorThread();
			threadArray[i].setName("Backup:"+i);
			threadArray[i].start();
		}
	}

	public void backupvHMData(Long lDomainId, String strXmlPath,
			int iContent)
	{
		if (null == lDomainId && null == strXmlPath) {
			// add the debug log
			BeLogTools.error(M_RESTORE, "the parameter is error,Domainid or file path is null");

			return;
		}

		try
		{
			if (!(strXmlPath.substring(strXmlPath.length() - 1).equals(
					File.separator) || strXmlPath.substring(
					strXmlPath.length() - 1).equals("/"))) {
				strXmlPath = strXmlPath + File.separatorChar;
			}
			// store strxml root path

			initTables(strXmlPath, lDomainId,iContent);
			
			BeLogTools.debug(HmLogConst.M_RESTORE, "The VHM '"+backupVHMName+"' begin to backup...");
			
			threadArray = new BackupProcessorThread[BAKCUP_THREAD_NUM];
			for(int i = 0; i < BAKCUP_THREAD_NUM; i++) {
				threadArray[i] = new BackupProcessorThread();
				threadArray[i].setName("Backup:"+i);
				threadArray[i].start();
			}

		} catch (Exception ex) {
			BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(), ex);
		}
	}

	class BackupProcessorThread extends Thread {

		public void run()
		{
			BeLogTools.debug(HmLogConst.M_RESTORE, "Bakup process thread begin to run");

			Connection con = null;
			con = initCon();

			if(null == con)
			{
				BeLogTools.error(HmLogConst.M_RESTORE, "Can not get connection");
			    return;
			}

			while(true)
			{
				boolean noNeedBackupTable = false;
				boolean fullModelBackupTable = false;
				boolean noFullModelNoNeedBackupTable = false;
				BackupTableDTO2 dto = eventQueue.poll();
				if(dto == null)
					break;
				String strTableName = dto.getTableName();
				try
				{
					if(! AhBackupNewTool.isValidCoon(con))
	                {
	                	con = initCon();
	                }
				}
				catch(Exception ex)
				{
					BeLogTools.error(HmLogConst.M_RESTORE, ex.getMessage(), ex);
				}
                
                
				//filter no need backup table
                for(String sTable : BackupFilterTable.NO_NEED_BACKUP_FILTER_TABLE){
                	if(sTable.equalsIgnoreCase(strTableName)){
                		noNeedBackupTable = true;
                		break;
                	}
                }
				if (noNeedBackupTable) {
					continue;
				}

				if(strTableName.equalsIgnoreCase(AhBackupNewTool.ahFeaturePermission))
				{
					backupFuncPermTable(strTableName, dto.getFilePath(),con, dto.getDomainName(),dto.getDomainId());
					continue;
				}

				if (dto.getIContext() == AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP)
				{
					for(String sTable : BackupFilterTable.FULL_BACKUP_MODEL_BACKUP_TABLE){
		                	if(sTable.equalsIgnoreCase(strTableName)){
		                		fullModelBackupTable = true;
		                		break;
		                	}
		            }
					if (fullModelBackupTable)
				    {
						backupTablewithID(strTableName, dto.getFilePath(),con, dto.getDomainName(), dto.getDomainId());
						continue;
				    }
				}
				else
				{
					for(String sTable : BackupFilterTable.NO_FULL_BACKUP_MODEL_FILTER_TABLE){
	                	if(sTable.equalsIgnoreCase(strTableName)){
	                		noFullModelNoNeedBackupTable = true;
	                		break;
	                	}
					}
					if (noFullModelNoNeedBackupTable)
					{
						continue;
					}
				}

				backupTable(strTableName, dto.getFilePath(),con, dto.getDomainName(), dto.getDomainId());
			}

			BeLogTools.debug(HmLogConst.M_RESTORE, "Finish backup database");
			freeCon(con);
		}
	}


}


class BackupTableDTO2
{
	private String tableName;
	private String filePath;
	private long domain_id;
	private String domain_name;
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
	public long getDomainId() {
		return domain_id;
	}
	public void setDomainId(long lid) {
		this.domain_id = lid;
	}

	public String getDomainName()
	{
		return domain_name;
	}

	public void setDomainName(String strName)
	{
		this.domain_name = strName;
	}
}
