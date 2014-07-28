package com.ah.mdm.core.profile.utils;

import java.io.ByteArrayOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public abstract class Payload implements IProtocolContants {

	protected abstract void fillPayloadContent(Element parentNode);

	public StringBuffer toPlist() {
		try {
			Document document = DocumentHelper.createDocument();
			document.addDocType(PLIST, DOCTYPE_PUBLIC_ID, DOCTYPE_SYSTEM_ID);
			Element root = document.addElement(PLIST).addAttribute(VERSION_STR,
					VERSION_VALUE);
			Element rootDict = root.addElement(DICT);
			fillPayloadContent(rootDict);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter output = new XMLWriter(stream, format);
			output.write(document);
			return new StringBuffer(stream.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new StringBuffer();
	}
}
