package com.ah.be.ls.data2;

import java.io.IOException;
import java.io.OutputStream;

import org.dom4j.Document;

public class ApUsageStatRequest implements FileTxObject {
	private Document doc;

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public void write(OutputStream out) throws IOException {
		out.write(doc.asXML().getBytes());
		out.flush();
		out.close();
	}

}
