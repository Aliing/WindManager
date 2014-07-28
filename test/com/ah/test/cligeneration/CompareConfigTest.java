package com.ah.test.cligeneration;

import java.io.File;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.config.BeConfigModuleImpl;
import com.ah.be.config.create.common.AhControlMultiApVer;
import com.ah.be.config.create.common.CompleteRunXml;
import com.ah.bo.hiveap.HiveAp;
 

public class CompareConfigTest {
	
	public void testCompareXmlWithDefaultValue() throws Exception {
        
		String versionXmlKey = "BranchRouter_200_0.0.0.0";
		String defaultXmlKey = "DefaultValue_BranchRouter_200_0.0.0.0";
		HiveAp hiveAp = new HiveAp();
		hiveAp.setDeviceType(HiveAp.Device_TYPE_BRANCH_ROUTER);
		hiveAp.setHiveApModel(HiveAp.HIVEAP_MODEL_BR200);
		hiveAp.setSoftVer("0.0.0.0");
		
		ClassLoader classLoader = CompareConfigTest.class.getClassLoader();
		
		String runPath = classLoader.getResource("com/ah/test/cligeneration/111111111111_full_run.xml").getFile();
		String newPath = classLoader.getResource("com/ah/test/cligeneration/111111111111_full_new.xml").getFile();
		String defPath = classLoader.getResource("com/ah/test/cligeneration/DefaultValue_BranchRouter_200_0.0.0.0.xml").getFile();
		
		AhControlMultiApVer.loadAllVerFile();
		Map<String, Document> versionXmlMap = AhControlMultiApVer.getVerXmlMap();
		versionXmlMap.put(versionXmlKey, versionXmlMap.get("BranchRouter_200_6.0.1.0"));	
		
		CompleteRunXml.loadAllDefaultFile("c:/");
		Map<String, Document> defValueMap = CompleteRunXml.getDefValueXmlMap();
		SAXReader reader = new SAXReader();
		Document testDocument = reader.read(defPath);
		defValueMap.put(defaultXmlKey, testDocument);
		//System.out.println(defValueMap);
				
		CompleteRunXml fill = new CompleteRunXml(hiveAp);
		//fill.fillRunXml(runPath, newPath);
		fill.fillDefaultValueForTest(runPath, newPath);
		
		BeConfigModuleImpl module = new BeConfigModuleImpl();
		String result = module.compareConfigs(runPath, newPath, hiveAp);
        System.out.println(result);
		
	}
	
	public void testCompareXml() throws Exception {
		BeConfigModuleImpl module = new BeConfigModuleImpl();
		HiveAp hiveAp = new HiveAp();
		hiveAp.setDeviceType(HiveAp.Device_TYPE_BRANCH_ROUTER);
		hiveAp.setHiveApModel(HiveAp.HIVEAP_MODEL_BR200);
		hiveAp.setSoftVer("6.0.1.0");
		String oldXmlCfgPath = "d:/temp/111111111111_full_run.xml";
		String newXmlCfgPath = "d:/temp/111111111111_full_new.xml";
		//String oldXmlCfgPath = "d:/HiveManager/tomcat/webapps/hm/schema/111111111111_full_run.xml";
		//String newXmlCfgPath = "d:/HiveManager/tomcat/webapps/hm/schema/111111111111_full_new.xml";
		String result = module.compareConfigs(oldXmlCfgPath, newXmlCfgPath, hiveAp);
        System.out.println(result);
	}
	
	public void testDom4j() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File("d:/workspace/hivemanager/webapps/schema/defaultValue/DefaultValue_BranchRouter_200_6.0.1.0.xml"));
		String node = "/configuration/mac-address-table/notification/cr[@operation='no']";
		System.out.println(document.selectNodes(node));
		Element notexist = (Element) document.selectSingleNode("/aaa/bbb");
		Element aaa = (Element) null;
		System.out.println(aaa);
	}

	public static void main(String[] args) throws Exception {
		CompareConfigTest test = new CompareConfigTest();
		test.testCompareXmlWithDefaultValue();
		//test.testCompareXml();
		//test.testDom4j();
	}

}
