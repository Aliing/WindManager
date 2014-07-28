/**
 *@filename	AhBackupDatabase.java
 *@version
 *@author		Xiaolanbao
 *@createtime	2007-10-16 09:22:00
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */

package com.ah.be.admin.adminBackupUnit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.ah.be.log.BeLogTools;
import com.ah.bo.admin.AhScheduleBackupData;

/**
 * @author Xiaolanbao
 * @version V2.2.0.0
 */
public class AhBackupDatabase {

	private static final String ahAlarmTable = "AH_ALARM";

	private static final String ahEventTable = "AH_EVENT";

	public static void main(String[] args) {
		// String strPath="./tmp";
		//
		// System.out.println("backup begin......");
		//
		// AhBackupDatabase oTmp = new AhBackupDatabase();
		//
		// oTmp.backupDatabase(strPath);
		//
		// System.out.println("backup end......");

		try
		{
			if(args.length != 2)
			{
				//add log

				return;
			}

			AhBackupDatabase oTmp = new AhBackupDatabase();

			int iContent = Integer.parseInt(args[1]);

			oTmp.backupDatabase(args[0], iContent);
		}
		catch(Exception ex)
		{
			//add log
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
	}

	public void backupDatabase(String strXmlPath, int iContent) {
		Statement st = null;
		ResultSet rs = null;
		Connection con = null;
		try {
			if (!(strXmlPath.substring(strXmlPath.length() - 1).equals(
					File.separator) || strXmlPath.substring(
					strXmlPath.length() - 1).equals("/"))) {
				strXmlPath = strXmlPath + File.separatorChar;
			}

//			Class.forName("org.postgresql.Driver").newInstance();
//
//			String strUrl = "jdbc:postgresql://localhost:5432/hm";
//
//			String strUsr = "hivemanager";
//
//			String strPsd = "aerohive";
//
//			Connection con = DriverManager
//					.getConnection(strUrl, strUsr, strPsd);
			AhBackupNewTool oTool = new AhBackupNewTool();

			con = oTool.initCon();


			st = con.createStatement();

			String strSql = "select tablename from pg_tables where "
					+ "tableowner='hivemanager'";

			rs = st.executeQuery(strSql);

			if (iContent == AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP) {
				while (rs.next()) {
					createTableFile(rs.getString(1), strXmlPath, con);
				}
			} else {
				while (rs.next()) {
					if (rs.getString(1).equalsIgnoreCase(
							AhBackupDatabase.ahAlarmTable)
							|| rs.getString(1).equalsIgnoreCase(
									AhBackupDatabase.ahEventTable)) {
						continue;
					}

					createTableFile(rs.getString(1), strXmlPath, con);
				}
			}
		}
		catch (Exception ex)
		{
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}finally {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				BeLogTools.restoreLog(BeLogTools.ERROR, e1.getMessage());
			}
			try {
				st.close();
			} catch (SQLException e) {
				BeLogTools.restoreLog(BeLogTools.ERROR, e.getMessage());
			}
			try {
				con.close();
			} catch (SQLException e) {
				BeLogTools.restoreLog(BeLogTools.ERROR, e.getMessage());
			}
		}
	}

	private void createTableFile(String strTableName, String strPath,
			Connection conTable) {
		XMLWriter output = null;
		try {
			Statement stTable = conTable.createStatement();

			String strSql = "select count(*) from " + strTableName;

			ResultSet rsTable = stTable.executeQuery(strSql);

			rsTable.next();

			int iRowCount = rsTable.getInt(1);

			int intFileCount = 1;

			for (int i = 0; i < iRowCount || i == 0; i = i + 5000) {
				strSql = "select * from " + strTableName + " limit " + 5000
						+ " offset " + i;

				rsTable = stTable.executeQuery(strSql);

				// OutputFormat format = OutputFormat.createPrettyPrint();
				//
				// format.setEncoding("UTF-8");

				Document document = DocumentHelper.createDocument();

				Element table = document.addElement("table").addAttribute(
						"schema", strTableName);

				File file;

				if (i == 0) {
					file = new File(strPath + strTableName.toLowerCase() + ".xml");
				} else {
					file = new File(strPath + strTableName.toLowerCase() + "_"
							+ intFileCount++ + ".xml");
				}

				// FileWriter fw = new FileWriter(file);
				//
				// XMLWriter output = new XMLWriter(fw, format);

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
							newStr = rsTable.getString(icol);//replaceAll("&",
//									"&amp;");
//
//							newStr = newStr.replaceAll("<", "&lt;");
//
//							newStr = newStr.replaceAll("\"", "&quot;");
						}

						row.addElement("field").addAttribute("name",
								rsmd.getColumnName(icol)).addAttribute("value",
								newStr);
					}
				}

				output.write(document);
			}
		}
		catch (Exception ex)
		{
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}finally{
			try {
				output.close();
			} catch (IOException e) {
				BeLogTools.restoreLog(BeLogTools.ERROR, e.getMessage());
			}
		}
	}

}