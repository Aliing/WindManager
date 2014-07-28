package com.ah.be.config.cli.testTools;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.ah.be.config.cli.util.CLIConfigFileLoader;
import com.ah.be.config.cli.util.CLIConfigFileUtil;
import com.ah.be.config.cli.xsdbean.Clis;

public class PrintConstraintXml {

	public static void main(String[] args) throws Exception{
		CLIConfigFileUtil.setSchemaPath("webapps"+File.separator+"schema"+File.separator);
		Clis clisObj = CLIConfigFileLoader.getInstance().getCLIConfig();

		JAXBContext jc = JAXBContext.newInstance("com.ah.be.config.cli.xsdbean");
		Marshaller marshaller = jc.createMarshaller();
		
		//load constraints.xml
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(clisObj, System.out);
	}
}
