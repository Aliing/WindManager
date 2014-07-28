package com.ah.be.admin.restoredb;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

public class AhDBTool {
	
	static public final int		PG_COLTYPE_boolean						= 16;
	static public final int		PG_COLTYPE_bytea						= 17;
	static public final int		PG_COLTYPE_bigint						= 20;
	static public final int		PG_COLTYPE_smallint						= 21;
	static public final int		PG_COLTYPE_integer						= 23;
	static public final int		PG_COLTYPE_text							= 25;
	static public final int		PG_COLTYPE_character					= 1042;
	static public final int		PG_COLTYPE_character_var				= 1043;
	static public final int		PG_COLTYPE_timestamp					= 1114;
	static public final int		PG_COLTYPE_double						= 701;
	static public final int		PG_COLTYPE_real							= 700;
	static public final int		PG_COLTYPE_timestamp_timezone 			= 1184;
	static public final int		PG_COLTYPE_smallint_array	 			= 1005;
	static public final int		PG_COLTYPE_integer_array	 			= 1007;
	static public final int		PG_COLTYPE_varchar_array		 		= 1015;
	static public final int		PG_COLTYPE_bigint_array		 			= 1016;
	static public final int		PG_COLTYPE_text_array					= 1009;
	

	private final String	s1							= "SELECT a.attname as col_name,a.atttypid as col_type "
																+ "FROM  "
																+ "pg_catalog.pg_attribute a "
																+ "WHERE a.attnum > 0 AND NOT a.attisdropped AND a.attrelid = ( "
																+ "SELECT c.oid  "
																+ "FROM pg_catalog.pg_class c "
																+ "LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace "
																+ "WHERE c.relname ~ '^(";
	private final String	s2							= ")$' "
																+ "AND pg_catalog.pg_table_is_visible(c.oid) "
																+ ") ";

	private String[]		columnNames					= null;
	private int[]			columnTypes					= null;
	private Object[]		columnDefaultValues			= null;
	
	public static final String  AH_DOWNLOADS_HOME = "/HiveManager/downloads";
	
	/* Must be consistent with the definition in hm.util.js */
	public static final int[] DIRECTORY_CHARS = new int[] { 33, 43, 44, 45, 46, 48,
			49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69,
			70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86,
			87, 88, 89, 90, 91, 93, 95, 97, 98, 99, 100, 101, 102, 103, 104,
			105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117,
			118, 119, 120, 121, 122, 123, 125 };

	public boolean generateColumnInfo(String tabName) {

		try{
			String sql = s1 + tabName.toLowerCase() + s2;
			List<?> bos = QueryUtil.executeNativeQuery(sql);
//			AhRestoreDBTools.logRestoreMsg(tabName + " columns=" + bos.size());
	
			if(bos.size() <=0) {
				AhRestoreDBTools.logRestoreMsg("Table "+tabName+" is not exist");
				return false;
			}
			//check whether id column exists
			int columnSize = bos.size();
			for (int i = 0; i < bos.size(); i++) {
				Object[] a = (Object[]) bos.get(i);
				String colName = (String) a[0];
				if(colName.equalsIgnoreCase("id")) {
					columnSize = bos.size()-1;
					break;
				}
			}
			
			columnNames = new String[columnSize];
			columnTypes = new int[columnSize];
			columnDefaultValues = new Object[columnSize];
	
			int j = 0;
			for (int i = 0; i < bos.size(); i++) {
				Object[] a = (Object[]) bos.get(i);
				String colName = (String) a[0];
				int colType = ((BigInteger) a[1]).intValue();
				
				// AhRestoreDBTools.logRestoreMsg("f[" + i + "]=" + colName + "," + colType);
	
				if (colName.equals("id")) {
					continue;
				} else if (colName.equals("owner")) {
					columnNames[j] = colName;
					columnTypes[j] = AhConvertXMLToCSV.COLUMN_TYPE_OTHER_USE_DEFAULT;
					columnDefaultValues[j] = AhConvertXMLToCSV.OWNER_DEFAULT;
					j++;
				} else {
					columnNames[j] = colName;
					columnTypes[j] = colType;
					
					switch (colType) {
					case PG_COLTYPE_character:
					case PG_COLTYPE_character_var:
					case PG_COLTYPE_text:
						columnDefaultValues[j] = "";
						break;
					case PG_COLTYPE_timestamp:
						columnDefaultValues[j] = "1970-01-01";
						break;
					case PG_COLTYPE_double:
					case PG_COLTYPE_real:
						columnDefaultValues[j] = 0.0;
						break;
					case PG_COLTYPE_bigint:
					case PG_COLTYPE_integer:
					case PG_COLTYPE_smallint:
						columnDefaultValues[j] = 0;
						break;
					case PG_COLTYPE_boolean:
						columnDefaultValues[j] = "f";
						break;
					case PG_COLTYPE_timestamp_timezone:
					case PG_COLTYPE_smallint_array:
					case PG_COLTYPE_integer_array:
					case PG_COLTYPE_bigint_array:
					case PG_COLTYPE_varchar_array:
					case PG_COLTYPE_text_array:
					case PG_COLTYPE_bytea:
						columnDefaultValues[j] = null;
						break;
					default:
						AhRestoreDBTools.logRestoreMsg("Table "+tabName+",f[" + i + "]=" + colName + "," + colType);
						columnDefaultValues[j] = null;
						break;
					}
					j++;
				}
			}
			return true;
		}catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg("Fail to generate column info of  " + tabName + " , exception:", e);
			return false;
		}
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public int[] getColumnTypes() {
		return columnTypes;
	}

	public Object[] getColumnDefaultValues() {
		return columnDefaultValues;
	}

	public void setColumnInfoByName(String colName,int type,Object defaultValue){
		if(null == columnNames) {
			AhRestoreDBTools.logRestoreMsg("column info is not exist");
		}
		int i;
		for(i = 0; i < columnNames.length; i++) {
			if(columnNames[i].equalsIgnoreCase(colName))
				break;
		}
		if(i < columnNames.length) {
			columnTypes[i] = type;
			columnDefaultValues[i] = defaultValue;
		}
		else {
			AhRestoreDBTools.logRestoreMsg("column " + colName + " is not exist");
		}
	}
	
	public static void changeDomainName(String strDomainName)
	{
		boolean bFlag = false;		
		
		String strNewName = strDomainName;
		
		//get the last name
		for(int i=0; i<strNewName.length(); ++i)
		{
			char c = strNewName.charAt(i);		
			
			if(!isValidChar((int)c))
			{
				strNewName = strNewName.replace(c, '_');
				bFlag = true;
			}			
		}	
		
		//if need change the values
		if(!bFlag)
		{
			return;
		}
		
		//change name from db hmdomain,hm_updatesoftwareinfo
		HmDomain bo = QueryUtil.findBoByAttribute(HmDomain.class, "domainName",
				strDomainName);
		
		if(null == bo)
		{
			return;
		}
		
		//update bo
		bo.setDomainName(strNewName);
		try
		{
		    QueryUtil.updateBo(bo);
		}
		catch(Exception ex)
		{
			//add log
			AhRestoreDBTools.logRestoreMsg(ex.getMessage());
			return;
		}
		
		String where = "domainName = :s1";
		Object[] values = new Object[1]; 
		values[0] = strNewName;
		
		List<HMUpdateSoftwareInfo> infoList = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
				null, new FilterParams(where,values));
		
		if(!infoList.isEmpty())
		{
			for (HMUpdateSoftwareInfo item : infoList) {
				item.setDomainName(strNewName);
				try {
					QueryUtil.updateBo(item);
				}
				catch (Exception ex) {
					//add log
					AhRestoreDBTools.logRestoreMsg(ex.getMessage());
				}
			}
		}
		
		//change name from dir,domains & downloads
		String strDownloadDomain = AhDBTool.AH_DOWNLOADS_HOME+"/"+strDomainName;
		String strNewDownloadDomain = AhDBTool.AH_DOWNLOADS_HOME+"/"+strNewName;
		
		File fDownloadDomain = new File(strDownloadDomain);
		File fNewDownloadDomain = new File(strNewDownloadDomain);
		
		if(fDownloadDomain.exists() && fDownloadDomain.isDirectory())
		{
			fDownloadDomain.renameTo(fNewDownloadDomain);
		}
		
		String strMapDoamin = BeTopoModuleUtil.FILE_ROOT+"/"+strDomainName;
		String strNewMapDomain = BeTopoModuleUtil.FILE_ROOT+"/"+strNewName;
		
		File fMapDomain = new File(strMapDoamin);
		File fNewMapDomain = new File(strNewMapDomain);
		
		if(fMapDomain.exists() && fMapDomain.isDirectory())
		{
			fMapDomain.renameTo(fNewMapDomain);
		}
	}
	
	public static boolean isValidChar(int iAskii)
	{
		boolean bReturn = false;
		
		for(int i=0; i<AhDBTool.DIRECTORY_CHARS.length; ++i)
		{
			if(iAskii == AhDBTool.DIRECTORY_CHARS[i])
			{
				bReturn = true;
				break;
			}
		}
		
		return bReturn;
	}

}