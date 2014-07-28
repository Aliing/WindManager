package com.ah.be.admin.restoredb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.bo.mgmt.QueryUtil;

public class CheckAfterRestore {
	String logStr = "";
	static String TMP_PATH = System.getenv("CATALINA_HOME") + File.separator + "xmltmp" + File.separator;

	public static void main(String[] args){

		CheckAfterRestore c = new CheckAfterRestore();
		c.check(args[0], args[1]);
	}

	/**
	 * compare row count of table with row count of dbxml
	 * @param filePath temp file path
	 * @param logPath log path
	 * @return void
	 * @author fhu
	 * @date Dec 21, 2011 4:15:42 PM
	 */
	private void check(String filePath,String logPath){
		Calendar cl = Calendar.getInstance();

		logStr += "\r\n" + cl.getTime() + "-------------------check start-------------------\r\n\r\n";

		this.compareRowCount(this.getFileNameList(filePath));

		logStr += "\r\n" + cl.getTime() + "-------------------check end---------------------\r\n\r\n";

		this.writeLog(logPath);

	}

	/**
	 * write log
	 * @param logPath
	 * @return void
	 * @author fhu
	 * @date Dec 21, 2011 4:22:52 PM
	 */
	private void writeLog(String logPath){
		FileOutputStream out = null;
		File log = new File(logPath);
		if(!log.exists()){
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			out = new FileOutputStream(log);
			out.write(logStr.getBytes());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get fileName list
	 * @param dirPath
	 * @return List<String>
	 * @author fhu
	 * @date Dec 20, 2011 1:59:31 PM
	 */
	private List<String> getFileNameList(String dirPath){
		List<String> list = new ArrayList<String>();
		File xmlFileRoot = new File(dirPath);
		if(xmlFileRoot.isDirectory()){
			for(File dbDir : xmlFileRoot.listFiles()){
				String fileName = dbDir.getName();
				if(fileName.indexOf(".xml") >= 0){
					//wipe off suffix
					String tmpName = fileName.substring(0,fileName.lastIndexOf("."));
					String tableStr = this.getIgnoreTableList();
					if(tableStr.indexOf(tmpName + ",") < 0 && !("").equalsIgnoreCase(tmpName)){
						//if name not include "_",it is tableName and add to list
						if(tmpName.lastIndexOf("_") < 0){
							list.add(tmpName);
						}else{
							String checkValue = tmpName.substring(tmpName.lastIndexOf("_")+1,tmpName.length());
							//if after "_" is not number,it is tableName and add to list
							if(!checkValue.matches("[0-9]+")){
								list.add(tmpName);
							}
						}
					}
				}
			}
		}
		return list;
	}

	private String getIgnoreTableList(){
		String strLine;
		FileReader fr = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			fr = new FileReader(System.getenv("CATALINA_HOME") + File.separator + "webapps"
					+ File.separator + "hm" + File.separator + "resources" + File.separator + "ignore-tables.txt");
			br = new BufferedReader(fr);

			while ((strLine = br.readLine()) != null) {
				strLine = strLine.trim();
				sb.append(strLine);
				sb.append(",");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(null != br)
					br.close();
				if(null != fr)
					fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return sb.toString();
	}
	
	/**
	 * compare row count and print message that number not equal to log
	 * @param xmlNameList
	 * @return void
	 * @author fhu
	 * @date Dec 20, 2011 2:25:59 PM
	 */
	private void compareRowCount(List<String> xmlNameList){
		int errorCount = 0;
		for(String name : xmlNameList){
			if("hive_ap".equals(name))
				name = "convert_hive_ap";
			if("sub_network_resource".equals(name))
				name = "convert_sub_network_resource";
			if("client_device_info".equals(name))
				name = "convert_client_device_info";
			int xmlCount = this.getAllXmlRowCount(name);
			int dbCount = 0;
			try{
				if("convert_hive_ap".equals(name))
					name = "hive_ap";
				if("convert_sub_network_resource".equals(name))
					name = "sub_network_resource";
				if("convert_client_device_info".equals(name))
					name = "client_device_info";
				dbCount = this.getDBRowCount(name);
			}catch(Exception e){
				errorCount++ ;
				logStr += "  [ERROR] " + errorCount + " db read failed! tableName:" + name +
						"; xmlRowCount:" + xmlCount + "; dbRowCount:" + dbCount + "\r\n";
				//System.out.println("can not find this table.table name is : " + name);
				continue;
			}

			if(xmlCount != dbCount){
				errorCount++ ;
				logStr += "  [ERROR] " + errorCount + " compare failed! tableName:" + name +
						"; xmlRowCount:" + xmlCount + "; dbRowCount:" + dbCount + "\r\n";
			}
		}

		if(errorCount == 0)
			logStr += " compare success ";
	}

	/**
	 * get row count of xml file
	 * @param xmlName
	 * @return Integer
	 * @author fhu
	 * @date Dec 20, 2011 2:30:27 PM
	 */
	private Integer getAllXmlRowCount(String xmlName){
		String xmlPath = TMP_PATH + xmlName + ".xml";
		int rowCount = 0;
		rowCount += this.getOneXmlRowCount(xmlPath);
		rowCount += this.getExtendFileRowCount(xmlName);

		return rowCount;
	}

	/**
	 * @param baseName
	 * @return Integer
	 * @author fhu
	 * @date Dec 21, 2011 2:32:38 PM
	 */
	private Integer getExtendFileRowCount(String baseName){
		int fileNum = 1;
		boolean isFile = true;
		while(isFile){
			String newFile = TMP_PATH + baseName + "_" + fileNum + ".xml";
			File file = new File(newFile);
			if(!file.exists()){
				isFile = false;
			}else{
				fileNum++;
			}
		}
		if(fileNum > 1){
			return (fileNum-2)*5000 + this.getOneXmlRowCount(TMP_PATH + baseName + "_" + --fileNum + ".xml");
		}else{
			return 0;
		}
	}

	private Integer getOneXmlRowCount(String xmlPath){
		int rowCount = 0;
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File(xmlPath));
			Element root = document.getRootElement();
			rowCount = root.elements().size();
		} catch (DocumentException e) {
			logStr += "  [ERROR] can't find file,file path is : " + xmlPath + "\r\n";
			e.printStackTrace();
		}

		return rowCount;
	}

	/**
	 * get table row count
	 * @param tableName
	 * @return Integer
	 * @author fhu
	 * @date Dec 20, 2011 3:12:16 PM
	 */
	private Integer getDBRowCount(String tableName){
		String sql = "select count(*) from " + tableName;
        List<?> rsList = QueryUtil.executeNativeQuery(sql);

        if(rsList.isEmpty()){
        	return 0;
        }

        return Integer.parseInt(rsList.get(0).toString());
	}
}
