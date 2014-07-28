package com.ah.be.config.cli.util;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ah.be.common.AhDirTools;
import com.ah.be.config.cli.brackets.DollarPlaceHolder;

public class CLIConfigFileUtil {
	
	private static String SCHEMA_PATH = AhDirTools.getCliSchemaDir();
	
	public static void setSchemaPath(String newPath){
		SCHEMA_PATH = newPath;
	}
	
	public static String getCliConfigPath(){
		return SCHEMA_PATH + "cli_config" + File.separator;
	}
	
	public static String getCLITemplatePath(){
		return SCHEMA_PATH + "versionXML" + File.separator + "schema.xml";
	}
	
	public static String getCLIConstraintsPath(){
		return getCliConfigPath() + "constraints.xml";
	}
	
	public static String getCLIGenPath(){
		return getCliConfigPath() + "cli_gen" + File.separator;
	}
	
	public static String getCLIParsePath(){
		return getCliConfigPath() + "cli_parse" + File.separator;
	}
	
	
	
//	//load CLI generate config file
//	public static Clis getCLIGenConfig(){
//		Clis genConfig = null;
//		try {
//			JAXBContext jc = JAXBContext.newInstance("com.ah.be.config.cli.xsdbean");
//			Unmarshaller unmarshaller = jc.createUnmarshaller();
//			
//			//load constraints.xml
//			InputStream inStream = new FileInputStream(getCLIConstraintsPath());
//			genConfig = (Clis) unmarshaller.unmarshal(inStream);
//			
//			//load all xml in fold cli_gen
//			File[] allXmls = getAllXmlFile(getCLIGenPath());
//			Clis subConfig;
//			for(File xml : allXmls){
//				inStream = new FileInputStream(xml);
//				subConfig = (Clis) unmarshaller.unmarshal(inStream);
//				if(subConfig.getCliGen() != null){
//					genConfig.getCliGen().addAll(subConfig.getCliGen());
//				}
//			}
//			
//			//init constraints params.
//			initConstraintsDefine(genConfig);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return genConfig;
//	}
//	
//	//load CLI parse config file
//	public static Clis getCLIParseConfig(){
//		Clis parseConfig = null;
//		try {
//			JAXBContext jc = JAXBContext.newInstance("com.ah.be.config.cli.xsdbean");
//			Unmarshaller unmarshaller = jc.createUnmarshaller();
//			
//			//load constraints.xml
//			InputStream inStream = new FileInputStream(getCLIConstraintsPath());
//			parseConfig = (Clis) unmarshaller.unmarshal(inStream);
//			
//			//load all xml in fold cli_parse
//			File[] allXmls = getAllXmlFile(getCLIParsePath());
//			Clis subConfig;
//			for(File xml : allXmls){
//				inStream = new FileInputStream(xml);
//				subConfig = (Clis) unmarshaller.unmarshal(inStream);
//				if(subConfig.getCliParse() != null){
//					parseConfig.getCliParse().addAll(subConfig.getCliParse());
//				}
//			}
//			
//			//init constraints params.
//			initConstraintsDefine(parseConfig);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return parseConfig;
//	}
	
	public static File[] getAllXmlFile(String path){
		File folder = new File(path);
		File[] filFiles = folder.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name){
				if(name.toLowerCase().endsWith(".xml")){
					return true;
				}else{
					return false;
				}
			}
		});
		return filFiles;
	}
	
	public static Document readXml(String path){
		Document destDoc = null;
		try{
			SAXReader reader = new SAXReader();
			destDoc = reader.read(new File(path));
		}catch(Exception e){
			e.printStackTrace();
		}
		return destDoc;
	}
	
	public static void writeXml(Document doc, String path, boolean isPretty) throws IOException{
		OutputFormat format = null;
		String os = System.getProperties().getProperty("os.name").toLowerCase();
		if(isPretty){
			format = new OutputFormat();
		}else{
			format = OutputFormat.createCompactFormat();
		}
		format.setIndent("\t");
		format.setNewlines(true);
		if(os.contains("win")){
			format.setLineSeparator("\r\n");
		}else{
			format.setLineSeparator("\n");
		}
		format.setTrimText(true);
		XMLWriter writer = new XMLWriter(new FileWriter(path), format);
		writer.write(doc);
		writer.close();
	}
	
	public static Map<String, String> processReferenceRelation(Map<String, String> paramMap){
		if(paramMap == null || paramMap.isEmpty()){
			return null;
		}
		Map<String, String> resultMap = new HashMap<>();
		
		Iterator<Entry<String, String>> mapItems = null;
		Entry<String, String> entryObj = null;
		List<DollarPlaceHolder> dollarList = null;
		String keyStr = null, valueStr = null;
		int count = 0;
		while(!paramMap.isEmpty()){
			if(count ++ > 10000){
				//use to prevent endless loop
				break;
			}
			mapItems = paramMap.entrySet().iterator();
			while(mapItems.hasNext()){
				entryObj = mapItems.next();
				keyStr = entryObj.getKey();
				valueStr = entryObj.getValue();
				
				//not exists format ${****}
				dollarList = DollarPlaceHolder.getInstances(valueStr);
				if(dollarList == null){
					resultMap.put(keyStr, valueStr);
					mapItems.remove();
					continue;
				}
				
				boolean allFind = true;
				for(DollarPlaceHolder holder : dollarList){
					if(!resultMap.containsKey(holder.getOriginalContent())){
						allFind = false;
						break;
					}
					holder.setContent(resultMap.get(holder.getOriginalContent()));
				}
				if(!allFind){
					//not all ${****} are ready.
					continue;
				}
				
				resultMap.put(keyStr, DollarPlaceHolder.getContent(dollarList, valueStr));
				mapItems.remove();
			}
		}
		
		return resultMap;
	}
}
