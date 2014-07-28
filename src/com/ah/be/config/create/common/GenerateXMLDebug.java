package com.ah.be.config.create.common;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.ah.be.config.create.GenerateXML;
import com.ah.util.Tracer;

/**
 * @author zhang
 * @version 2009-5-4 16:34:02
 */

public class GenerateXMLDebug {
	
	private static final Tracer log = new Tracer(GenerateXMLDebug.class
			.getSimpleName());
	
	public static final short NULL = -1;
	public static final short CONFIG_ELEMENT = 0;
	public static final short SET_VALUE= 1;
	public static final short SET_NAME= 2;
	public static final short SET_OPERATION= 3;
	
	private final List<DebugMsg> debugList = new LinkedList<DebugMsg>();
	private DebugMsg lastMsg;
	
	public void debug(String parentXPath, String currentElement, short type, String guiProfile, String pName){
		lastMsg = new DebugMsg(parentXPath, currentElement, type, guiProfile, pName);
		debugList.add(lastMsg);
	}
	
	public DebugMsg getLastMsg(){
		return this.lastMsg;
	}
	
	public String getLastErrorMsg(Document document, String errorKey){
		try{
			if(lastMsg == null){
				return null;
			}
			Node node = document.selectSingleNode(lastMsg.getParentXPath());
			if(node instanceof Element){
				
				/** */
				Element element = (Element)node;
				Element lastEle = element.element(lastMsg.getCurrentElement());
				if(lastEle == null){
					lastEle = element.addElement(lastMsg.getCurrentElement());
				}
				lastEle.addAttribute(GenerateXML.ATTRIBUTE_NAME_DEBUG, "yes");
				
				
				while(!lastEle.getParent().isRootElement()){
					lastEle = lastEle.getParent();
				}
				Element newEle = lastEle.createCopy();
				Element rootEle = document.getRootElement();
				rootEle.clearContent();
				rootEle.add(newEle);
				
				StringBuffer cliBuffer = new StringBuffer();
				StringBuffer allCliCmd = new StringBuffer();
				GenerateXML.errorCLIMap.remove(errorKey);
				GenerateXML.treeWalkElement(rootEle, cliBuffer, allCliCmd, errorKey);
				String errorCLI = GenerateXML.errorCLIMap.get(errorKey);
				
				if(lastMsg.getType() == CONFIG_ELEMENT){
					return "CLI: \""+errorCLI + "\" error, whether config attribute \""+lastMsg.getCurrentElement()+"\".";
				}else if(lastMsg.getType() == SET_VALUE || lastMsg.getType() == SET_NAME){
					return "CLI: \""+errorCLI + " [error_value]\" error, set value error.";
				}else{
					return "CLI: \"[no] "+errorCLI + "\" error, enable|disable";
				}
			}else{
				return null;
			}
		}catch(Exception e){
			log.error("getLastErrorMsg", e.getMessage(), e);
			return null;
		}
	}
	
	public class DebugMsg{
		private final String parentXPath;
		private final String currentElement;
		private final short type;
		private final String guiProfile;
		private final String pName;
		
		public DebugMsg(String parentXPath, String currentElement, short type, String guiProfile, String pName){
			this.parentXPath = parentXPath;
			this.currentElement = currentElement;
			this.type = type;
			this.guiProfile = guiProfile;
			this.pName = pName;
		}
		
		public String getParentXPath(){
			return this.parentXPath;
		}
		
		public String getCurrentElement(){
			return this.currentElement;
		}
		
		public short getType(){
			return this.type;
		}
		
		public String getGuiProfile(){
			return this.guiProfile;
		}
		
		public String getPName(){
			return this.pName;
		}
	}

}