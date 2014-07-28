package com.ah.test.cligeneration;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class VersionQuery {
		
	public void execute(String queryPath, final String startVersion, final String endVersion) throws Exception {
		String path = getClass().getClassLoader().getResource("").getPath();
		String projectPath = new File(path).getParentFile().getPath();
		String versionXmlPath = projectPath + File.separator + "webapps" + File.separator + "schema" + File.separator + "versionXML";
		//System.out.println(versionXmlPath);
		File file = new File(versionXmlPath);
		System.out.println("version xml path : " + file.getPath());
		System.out.println("------------------------------------------------------------------------");
		
		if (!file.isDirectory()) {
			return;
		}
		
		File[] xmlFiles = file.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				if (!name.endsWith(".xml")) {
					return false;
				}
				String version = name.substring(name.lastIndexOf("_") + 1, name.indexOf(".xml"));
				if (StringUtils.isNotBlank(startVersion)) {
					if (version.compareTo(startVersion) < 0) {
						return false;
					}
				}
				if (StringUtils.isNotBlank(endVersion)) {
					if (version.compareTo(endVersion) > 0) {
						return false;
					}
				}
				return true;
			}
		});
		
		int number = 0;
		for (File single : xmlFiles) {
			SAXReader reader = new SAXReader();
			Document document = reader.read(single);
			Element element = (Element) document.selectSingleNode(queryPath);
			if (element != null) {
				number++;
				System.out.println(number + "   " + single);
			}
			
		}
		
	}

	public static void main(String[] args) throws Exception {
		new VersionQuery().execute("/configuration/kddr", "6.1.4.0", "6.1.4.0");

	}

}
