package com.ah.be.config.create.common.postprocess;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import com.ah.be.app.AhAppContainer;
import com.ah.be.common.AhDirTools;
import com.ah.be.config.AhConfigParsedException;
import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.be.config.cli.generate.CompleteCfgGen;
import com.ah.be.config.cli.merge.XmlMergeCache;
import com.ah.be.config.cli.parse.CLIParseFactory;
import com.ah.be.config.cli.xsdbean.ConstraintType;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.common.AhControlMultiApVer;
import com.ah.be.config.create.common.FillAssistantElementAuto;
import com.ah.be.config.create.common.XmlOrderTool;
import com.ah.be.config.create.source.impl.ConfigureProfileFunction;
import com.ah.be.os.BeNoPermissionException;
import com.ah.be.os.FileManager;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.Tracer;

public abstract class AbstractCLIPostProcess implements CLIPostProcess {

	private static final Tracer log = new Tracer(AbstractCLIPostProcess.class.getSimpleName());
	
	protected ConfigureProfileFunction profileFunction;
	protected HiveAp hiveAp;
	protected String sourceXmlPath;
	
	protected Document document;
	protected List<String> cliList;
	
	protected PostProcessType[] processOrder;
	
	private static final String IGNORE_CLI_SUFFIX = "_ignore.config";
	private static final String RESULT_XML_SUFFIX = "_result.xml";
	private static final String CLI_SOURCE_SUFFIX = "_clis.config";
	
	@Override
	public void process(){
		if(processOrder == null){
			return;
		}
		
		boolean processResult = true;
		for(PostProcessType processType : processOrder){
			if(processType == PostProcessType.CLIGenNew){
				processResult = this.CLIGenerateNew();
			}else if(processType == PostProcessType.CLIParseNew){
				processResult = this.cliParseNew();
			}else if(processType == PostProcessType.CLIParseOld){
				processResult = this.cliParseOld();
			}else if(processType == PostProcessType.DeltaAssistant){
				processResult = this.addDeltaAssistant();
			}else if(processType == PostProcessType.VersionXmlFilter){
				processResult = this.filterByXml();
			}else if(processType == PostProcessType.XmlOrder){
				processResult = this.xmlOrder();
			}else if(processType == PostProcessType.Write && !StringUtils.isEmpty(sourceXmlPath) ){
				processResult = this.writeResult(sourceXmlPath);
			}
			
			if(!processResult){
				break;
			}
		}
	}

	@Override
	public Document getResult() {
		return this.document;
	}

	@Override
	public boolean writeResult(String xmlPath) {
		try {
			CLICommonFunc.writeXml(document, xmlPath, true);
			return true;
		} catch (IOException e) {
			log.error("writeResult", e);
			return false;
		}
	}

	@Override
	public boolean filterByXml(){
		AhControlMultiApVer filterTool;
		try {
			filterTool = new AhControlMultiApVer(hiveAp, this.document);
			this.document = filterTool.generate();
			return true;
		} catch (CreateXMLException | DocumentException | IOException e) {
			log.error("filterByXml()", e);
			return false;
		}
	}
	
	@Override
	public boolean CLIGenerateNew(){
		CompleteCfgGen allCLIGen = new CompleteCfgGen(this.profileFunction);
		try {
			cliList = allCLIGen.generateAllCLIs();
		} catch (CLIGenerateException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean cliParseNew() {
		XmlMergeCache xmlCache = new XmlMergeCache(this.document);
		xmlCache.init();
		ConstraintType type = new ConstraintType();
		type.setPlatform(String.valueOf(hiveAp.getHiveApModel()));
		type.setVersion(hiveAp.getSoftVer());
		type.setType(String.valueOf(hiveAp.getDeviceType()));
		
		CLIParseFactory parseFactory = CLIParseFactory.getInstance();
		List<String> cannotParseClis = parseFactory.parseCli(cliList.toArray(new String[cliList.size()]), 
				xmlCache, type);
		cliList.clear();
		cliList.addAll(cannotParseClis);
		this.document = xmlCache.getDocument();
		
		return true;
	}
	
	public static String getIgnoreCLIContent(HiveAp hiveAp){
		String ignorePath = AhDirTools.getTempXmlConfigDir(hiveAp.getOwner().getDomainName()) + File.separator 
				+ hiveAp.getMacAddress() + IGNORE_CLI_SUFFIX;
		
		File file = new File(ignorePath);
		if(!file.exists()){
			return null;
		}else if(System.currentTimeMillis() - file.lastModified() > 2000){
			return null;
		}
		
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			log.error("getIgnoreCLIContent", e);
			return null;
		}
	}

	@Override
	public boolean cliParseOld() {
		if(cliList.isEmpty()){
			// no need parse
			return true;
		}
		
		String tempDirPath = AhDirTools.getTempXmlConfigDir(hiveAp.getOwner().getDomainName());
		String cliSourcePath = tempDirPath + File.separator + hiveAp.getMacAddress() + CLI_SOURCE_SUFFIX;
		String xmlResultPath = tempDirPath + File.separator + hiveAp.getMacAddress() + RESULT_XML_SUFFIX;
		String cliIgnorePath = tempDirPath + File.separator + hiveAp.getMacAddress() + IGNORE_CLI_SUFFIX;
		Document resDoc = null;
		String[] ignoreClis = null;
		
		StringBuilder sBuilder = new StringBuilder();
		for(String cli : cliList){
			if(sBuilder.length() > 0){
				sBuilder.append("\n");
			}
			sBuilder.append(cli);
		}
		cliList.clear();
		
		try {
			FileManager.getInstance().createFile(sBuilder.toString(), cliSourcePath);
			AhAppContainer.getBeConfigModule().parseCli(hiveAp, ConfigType.AP_FULL, cliSourcePath, xmlResultPath, cliIgnorePath);
		} catch (IOException | IllegalArgumentException e) {
			log.error("cliParseOld()", e);
			return false;
		} catch(AhConfigParsedException e){
			log.error("cliParseOld()", e);
		}
		
		try {
			resDoc = CLICommonFunc.readXml(xmlResultPath);
			ignoreClis = FileManager.getInstance().readFile(cliIgnorePath);
		} catch (IllegalArgumentException | IOException
				| BeNoPermissionException | DocumentException e) {
			log.error("cliParseOld()", e);
		}
		
		if(resDoc != null){
			XmlMergeCache xmlCache = new XmlMergeCache(this.document);
			xmlCache.init();
			xmlCache.mergeElement(resDoc.getRootElement());
			this.document = xmlCache.getDocument();
		}
		
		if(ignoreClis != null && ignoreClis.length > 0){
			cliList.addAll(Arrays.asList(ignoreClis));
		}
		
		return true;
	}

	@Override
	public boolean xmlOrder() {
		this.document = XmlOrderTool.getInstance().orderXml(this.document);
		return true;
	}

	@Override
	public boolean addDeltaAssistant() {
		this.document = FillAssistantElementAuto.getInstance().fillElement(this.document);
		return true;
	}

	@Override
	public boolean fillXmlDefaultValue() {
		// TODO Auto-generated method stub
		return false;
	}
}
