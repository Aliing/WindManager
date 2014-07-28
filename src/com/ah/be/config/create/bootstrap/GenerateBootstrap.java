package com.ah.be.config.create.bootstrap;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.dom4j.DocumentException;

import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.GenerateXML;
import com.ah.be.config.create.common.AhControlMultiApVer;
import com.ah.be.config.create.common.postprocess.BootstrapCLIPostProcess;
import com.ah.be.config.create.common.postprocess.CLIPostProcess;
import com.ah.be.config.event.AhBootstrapGeneratedEvent;
import com.ah.bo.hiveap.HiveAp;
import com.ah.xml.be.config.Configuration;
import com.ah.xml.be.config.ObjectFactory;

public class GenerateBootstrap {

	private final AhBootstrapGeneratedEvent genBootstrapEvent;
	private final HiveAp hiveAp;

	public GenerateBootstrap(AhBootstrapGeneratedEvent genBootstrapEvent) throws CreateXMLException, DocumentException {
		this.genBootstrapEvent = genBootstrapEvent;
		this.hiveAp = genBootstrapEvent.getHiveAp();
		
		/** check HiveAp current version */
		StringBuffer errorMsg = new StringBuffer();
		if(!AhControlMultiApVer.checkHiveApVersion(hiveAp, errorMsg)){
			throw new CreateXMLException(errorMsg.toString());
		}
	}

	public void generateBootstrapXmlConfig(String bootstrapXmlCfgPath)
			throws Exception {
		BootstrapImpl bootstrapImpl = new BootstrapImpl(genBootstrapEvent);
		CreateBootstrapTree bootstrapTree = new CreateBootstrapTree(
				bootstrapImpl);
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
		Marshaller marshaller = context.createMarshaller();
		JAXBElement<Configuration> element = new JAXBElement<Configuration>(
				new QName("", GenerateXML.ROOT_ELEMENT_NAME),
				Configuration.class, bootstrapTree.getBootStrapConfig());
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		FileOutputStream output = null;

		try {
			output = new FileOutputStream(bootstrapXmlCfgPath);
			marshaller.marshal(element, output);
		} finally {
			if (output != null) {
				output.close();
			}
		}
		
		//XML post process
		CLIPostProcess cliPostProcess = new BootstrapCLIPostProcess(hiveAp, bootstrapXmlCfgPath);
		cliPostProcess.init();
		cliPostProcess.process();
		
//		/** modify xml with ap version file */
//		AhControlMultiApVer apVer = new AhControlMultiApVer(hiveAp, hiveAp.getSoftVer(), bootstrapXmlCfgPath);
//		Document cfgDoc = apVer.generate();
//		
////		//auto fill element <AH-DELTA-ASSISTANT operation="yes"/>
////		Document cfgDoc = CLICommonFunc.readXml(bootstrapXmlCfgPath);
//		cfgDoc = FillAssistantElementAuto.getInstance().fillElement(cfgDoc);
//		// xml element sort
//		cfgDoc = XmlOrderTool.getInstance().orderXml(cfgDoc);
//		//write xml
//		CLICommonFunc.writeXml(cfgDoc, bootstrapXmlCfgPath, true);
	}

	public static void generateBootstrapScript(String bootstrapClis,
			String bootstrapCfgPath) throws IOException {
		FileOutputStream output = null;

		try {
			output = new FileOutputStream(bootstrapCfgPath);
			output.write(bootstrapClis.getBytes());
			output.flush();
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

}