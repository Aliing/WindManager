package com.ah.be.config.cli.parse;

import org.dom4j.Element;

public class CLIParseResult {

	private boolean matche;
	private boolean noCmd;
	private boolean noCmdManual;
	private String cli;
	private String cmd;
	private String regex;
	private String[][] group;
	private String xmlStr;
	private Element xmlNode;

	public boolean isMatche() {
		return matche;
	}

	public void setMatche(boolean matche) {
		this.matche = matche;
	}

	public boolean isNoCmd() {
		return noCmd;
	}

	public void setNoCmd(boolean noCmd) {
		this.noCmd = noCmd;
	}

	public String getCli() {
		return cli;
	}

	public void setCli(String cli) {
		this.cli = cli;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String[][] getGroup() {
		return group;
	}

	public void setGroup(String[][] group) {
		this.group = group;
	}

	public String getXmlStr() {
		return xmlStr;
	}

	public void setXmlStr(String xmlStr) {
		this.xmlStr = xmlStr;
	}

	public Element getXmlNode() {
		return xmlNode;
	}

	public void setXmlNode(Element xmlNode) {
		this.xmlNode = xmlNode;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public boolean isNoCmdManual() {
		return noCmdManual;
	}

	public void setNoCmdManual(boolean noCmdManual) {
		this.noCmdManual = noCmdManual;
	}
}
