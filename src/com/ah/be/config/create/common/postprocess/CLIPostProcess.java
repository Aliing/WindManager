package com.ah.be.config.create.common.postprocess;

import org.dom4j.Document;

public interface CLIPostProcess {
	
	public enum PostProcessType {
		Write, VersionXmlFilter, CLIGenNew, CLIParseNew,
		CLIParseOld, XmlOrder, DeltaAssistant
	}
	
	public static final PostProcessType[] PostConfigApFull = {PostProcessType.VersionXmlFilter,
		PostProcessType.CLIGenNew, PostProcessType.CLIParseNew, PostProcessType.CLIParseOld,
		PostProcessType.DeltaAssistant, PostProcessType.XmlOrder, PostProcessType.Write};
	
	public static final PostProcessType[] PostConfigUserFull = {PostProcessType.VersionXmlFilter,
		PostProcessType.DeltaAssistant, PostProcessType.XmlOrder, PostProcessType.Write};
	
	public static final PostProcessType[] PostCLIParse = {PostProcessType.VersionXmlFilter,
		PostProcessType.CLIParseNew, PostProcessType.DeltaAssistant, PostProcessType.XmlOrder};
	
	public static final PostProcessType[] PostCLIBootstrap = {PostProcessType.VersionXmlFilter,
		PostProcessType.DeltaAssistant, PostProcessType.XmlOrder, PostProcessType.Write};
	
	void init();

	void process();
	
	Document getResult();
	
	boolean writeResult(String xmlPath);
	
	boolean filterByXml();
	
	boolean CLIGenerateNew();
	
	boolean cliParseNew();
	
	boolean cliParseOld();
	
	boolean xmlOrder();
	
	boolean addDeltaAssistant();
	
	boolean fillXmlDefaultValue();
}
