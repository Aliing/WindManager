package com.ah.be.admin.restoredb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;

public class AhConvertXMLToCSV {
	
//	private static String DEFAULT_CSV_FILE_NAME			= "d:/temp/dbdata.csv";
	
	private static String DEFAULT_CSV_FILE_NAME			= System.getenv("CATALINA_HOME") + "/dbxmlfile/dbdata.csv";
	
	public final static	GetColumnValueInterface OWNER_DEFAULT = new GetColumnValueInterface(){
		public String getValue(String columnName,String value,AhRestoreGetXML xmlParser,int row) {
			long lID=AhRestoreCommons.convertLong(value);
			
			HmDomain oDomain = AhRestoreNewMapTools.getHmDomain(lID);
			
			if(null == oDomain)
			{
				return "";
			}
			
			return String.valueOf(oDomain.getId());
		}
	};
	
	/**
	 * type of column
	 */
	public final static int		COLUMN_TYPE_STRING 				= 0X0001;
	public final static int		COLUMN_TYPE_AUTO_INCREASE		= 0X0002;
	public final static int		COLUMN_TYPE_OTHER				= 0X0004;
	public final static int		COLUMN_TYPE_STRING_USE_DEFAULT	= 0X0100;
	public final static int		COLUMN_TYPE_OTHER_USE_DEFAULT	= 0X0200;
	
	/**
	 * increase number
	 */
	private long					autoIncrease				= 1;
	
	/**
	 * column array and column type array and default column value
	 */
	private	String[]				column_Array;
	private int[]					column_Type;
	private Object[]				column_Default;
	
	/**
	 * check row valid interface
	 */
	private CheckRowValidInterface	checkInterface;
	
	public AhConvertXMLToCSV()
	{
		DEFAULT_CSV_FILE_NAME = AhRestoreDBTools.HM_XML_TABLE_PATH+"/dbdata.csv";
		File file = new File(DEFAULT_CSV_FILE_NAME);
		try {
			DEFAULT_CSV_FILE_NAME = file.getCanonicalPath();
			DEFAULT_CSV_FILE_NAME = DEFAULT_CSV_FILE_NAME.replace('\\', '/');
		} catch (IOException e) {
			DEFAULT_CSV_FILE_NAME = file.getAbsolutePath();
		}
	}
	
	/**
	 * set check row valid interface
	 *
	 * @param checkInterface -
	 */
	public void setCheckInterface(CheckRowValidInterface checkInterface) {
		this.checkInterface = checkInterface;
	}

	/**
	 * set column array and column type array and default column value when value is null
	 *
	 * @param column -
	 * @param type -
	 * @param valueDefault -
	 */
	public void setColumnInfo(String[] column,int[] type,Object[] valueDefault){
		column_Array = column;
		column_Type = type;
		column_Default = valueDefault;
		
		if(column_Type != null) {
			for(int i = 0; i < column_Type.length; i++) {
				switch(column_Type[i]) {
				case AhDBTool.PG_COLTYPE_boolean:
				case AhDBTool.PG_COLTYPE_integer:
				case AhDBTool.PG_COLTYPE_bigint:
				case AhDBTool.PG_COLTYPE_smallint:
				case AhDBTool.PG_COLTYPE_timestamp:
				case AhDBTool.PG_COLTYPE_double:
				case AhDBTool.PG_COLTYPE_real:
					column_Type[i] = COLUMN_TYPE_OTHER;
					break;
				case AhDBTool.PG_COLTYPE_character:
				case AhDBTool.PG_COLTYPE_character_var:
				case AhDBTool.PG_COLTYPE_text:
				case AhDBTool.PG_COLTYPE_bigint_array:
				case AhDBTool.PG_COLTYPE_integer_array:
				case AhDBTool.PG_COLTYPE_text_array:
				case AhDBTool.PG_COLTYPE_bytea:
					column_Type[i] = COLUMN_TYPE_STRING;
					break;
				case COLUMN_TYPE_STRING:
				case COLUMN_TYPE_AUTO_INCREASE:
				case COLUMN_TYPE_OTHER:
				case COLUMN_TYPE_STRING_USE_DEFAULT:
				case COLUMN_TYPE_OTHER_USE_DEFAULT:
					break;
				default:
					column_Type[i] = COLUMN_TYPE_STRING;
					break;
				}
			}
		}
	}
	
	/**
	 * convert XML file to CSV file and import into database, use default destination CSV file name
	 * @param xmlFileName name of xml file
	 * @param tblName 	name of table
	 * @return the number of records,0 if xml file is not exist
	 * @throws Exception -
	 */
	public int importXMLToDB(String xmlFileName,String tblName) throws Exception {
		return importXMLToDB(xmlFileName, tblName, DEFAULT_CSV_FILE_NAME);
	}

	/**
	 * convert XML file to CSV file and import into database
	 * @param xmlFileName name of xml file
	 * @param tblName 	name of table
	 * @param fileName	destination CSV file name
	 * @return the number of records,0 if xml file is not exist
	 * @throws Exception -
	 */
	public int importXMLToDB(String xmlFileName,String tblName,String fileName) throws Exception {
		int count = convertXMLToCSV(xmlFileName, fileName);
		if(count <= 0)
			return count;
		
		StringBuilder buf = new StringBuilder();
		buf.append("copy ").append(tblName).append("(");
		for(int i = 0; i < column_Array.length; i++) {
			if(0 != i)
				buf.append(",");
			buf.append("\"").append(column_Array[i]).append("\"");
		}
		buf.append(") from stdin with csv");
		QueryUtil.executeCopy(buf.toString(),fileName);
		
		return count;
	}
	
	/**
	 * convert XML file to CSV file, use default destination file name
	 * @param xmlFileName name of xml file
	 * @return the number of records,0 if xml file is not exist
	 * @throws Exception -
	 */
	public int convertXMLToCSV(String xmlFileName) throws Exception{
		return convertXMLToCSV(xmlFileName, DEFAULT_CSV_FILE_NAME);
	}

	/**
	 * convert XML file to CSV file
	 * @param xmlFileName name of xml file
	 * @param fileName destination CSV file name
	 * @return the number of records,0 if xml file is not exist
	 * @throws Exception -
	 */
	public int convertXMLToCSV(String xmlFileName,String fileName) throws Exception{
		if(null == column_Array || null == column_Type || null == column_Default)
			throw new Exception("Column info is empty");
		if(column_Array.length != column_Type.length || column_Array.length != column_Default.length)
			throw new Exception("Column info is wrong");
//		try {
			AhRestoreGetXML xmlParser = new AhRestoreGetXML();

			/**
			 * Check validation of hm_historyclientsession.xml
			 */
			if (!xmlParser.readXMLOneFile(xmlFileName)) {
				return 0;
			}
			
			FileWriter writer = new FileWriter(new File(fileName));
			int rowCount = xmlParser.getRowCount();
			int invalidCount = 0;
			for(int i = 0; i < rowCount; i++) {
				//check valid or row
				if(null != checkInterface) {
					if(!checkInterface.isValid(xmlParser, i)) {
						invalidCount ++;
						continue;
					}
				}
				StringBuffer buffer = new StringBuffer();
				for(int j = 0; j < column_Array.length; j++) {
					String value;
					try {
						value = xmlParser.getColVal(i, column_Array[j]);
						if(null != value)
							value = value.trim();
					} catch (Exception e) {
						//it only exception when there is no this column
						value = null;
					}
					if(j != 0)
						buffer.append(",");
					switch(column_Type[j]) {
					case COLUMN_TYPE_AUTO_INCREASE:
						buffer.append(autoIncrease++);
						break;
					case COLUMN_TYPE_STRING:
						if(null != value &&!"NULL".equalsIgnoreCase(value)) {
							if(value.contains("\""))
								value = value.replace("\"", "\"\"");
							buffer.append("\"").append(value).append("\"");
						} else {
							getDefaultValue(buffer, column_Default[j], column_Array[j],value, true,xmlParser, i);
						}
						break;
					case COLUMN_TYPE_STRING_USE_DEFAULT:
						getDefaultValue(buffer, column_Default[j], column_Array[j],value, true,xmlParser, i);
						break;
					case COLUMN_TYPE_OTHER_USE_DEFAULT:
						getDefaultValue(buffer, column_Default[j], column_Array[j],value, false,xmlParser, i);
						break;
					default:
						if(null != value && !"NULL".equalsIgnoreCase(value))
							buffer.append(value);
						else {
							getDefaultValue(buffer, column_Default[j], column_Array[j],value, false,xmlParser, i);
						}
						break;
					}
				}
				buffer.append("\n");
				writer.write(new String(buffer.toString().getBytes("UTF8")));
			}
			writer.close();
			return rowCount - invalidCount;
//		} catch (IOException e) {
//			throw e;
//		}
	}
	
	private void getDefaultValue(StringBuffer buffer,Object obj,String columnName,String value,boolean isStr,AhRestoreGetXML xmlParser,int row) {
		if(null != obj) {
			if(obj instanceof GetColumnValueInterface) {
				String str = ((GetColumnValueInterface)obj).getValue(columnName,value,xmlParser,row);
				if(null != str) {
					if(isStr) {
						if(str.contains("\""))
							str = str.replace("\"", "\"\"");
						buffer.append("\"").append(str).append("\"");
					}
					else
						buffer.append(str);
				}
			}
			else
				if(isStr) {
					if(obj.toString().contains("\""))
						obj = obj.toString().replace("\"", "\"\"");
					buffer.append("\"").append(obj).append("\"");
				}
				else
					buffer.append(obj);
		}
	}
	
	/**
	 * convert all XML file to CSV file and import into database, use default destination CSV file name
	 *
	 * @param baseXmlFileName name of base xml file
	 * @param tblName 	name of table
	 */
	public void importAllXMLToDB(String baseXmlFileName,String tblName) {
		importAllXMLToDB(baseXmlFileName, tblName, DEFAULT_CSV_FILE_NAME);
	}

	/**
	 * convert XML file to CSV file and import into database
	 *
	 * @param baseXmlFileName name of base xml file
	 * @param tblName 	name of table
	 * @param fileName	destination CSV file name
	 */
	public void importAllXMLToDB(String baseXmlFileName,String tblName,String fileName) {
		try{
			long startTime = System.currentTimeMillis();
			int index = 0;
			int count = 0;
			int ret;
			AhRestoreDBTools.logRestoreMsg("Restore " + tblName + " start...");
			while (true) {
				String xmlFile = baseXmlFileName;
				if (index > 0) {
					xmlFile = baseXmlFileName + "_" + index;
				}
				index++;
				try {
					ret = importXMLToDB(xmlFile, tblName,fileName);
					if(ret <= 0)
						break;
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("Fail to restore " + tblName + "table of file" + xmlFile + ",  exception:", e);
					ret = 0;
				}
				count += ret;
			}
			AhRestoreDBTools.logRestoreMsg("Restore " + tblName + " end, records=" + count
					+ ", cost time(ms)=" + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore " + tblName + " table exception:", e);
		}
	}

	public static void main(String[] args) {
		try {
//			AhConvertXMLToCSV csv = new AhConvertXMLToCSV();
//			csv.importAllXMLToDB("ah_event", "ah_event");
			QueryUtil.executeQuery(HmDomain.class, null, null);
			for(int i = 0; i < 1; i++) {
				AhDBRestoreTool.restoreTable("test_array");
			}
			System.out.println("test");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}