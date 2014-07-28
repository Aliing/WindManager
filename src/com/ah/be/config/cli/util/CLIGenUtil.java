package com.ah.be.config.cli.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ah.be.config.cli.brackets.BracketCLIGenObj;
import com.ah.be.config.cli.brackets.BracketObj;
import com.ah.be.config.cli.brackets.CLIBracketUtil;
import com.ah.util.ahdes.AhCliSec;

public class CLIGenUtil {
	
	public static final String CLI_SPLIT									= " ";
	public static final String FINAL_ELEMENT								= "AH-DELTA-ASSISTANT";
	
	public static final String ATTR_NAME									= "name";
	public static final String ATTR_CLI_NAME								= "CLIName";
	public static final String ATTR_VALUE									= "value";
	public static final String ATTR_OPERATION								= "operation";
	public static final String ATTR_QUOTEPROHIBITED							= "quoteProhibited";
	public static final String ATTR_ENCRYPTED								= "encrypted";
	public static final String ATTR_NULL_VALUE_VALID						= "nullValueValid";
	
	public static final String ATTR_KEYWORD									= "keyWord";
	public static final String ATTR_EXIST									= "exist";
	public static final String ATTR_NOT_EXIST								= "notExist";
	public static final String ATTR_NOCMD									= "noCmd";
	
	public static final String OPERATION_YES 								= "yes";
	public static final String OPERATION_NO							 		= "no";
	public static final String OPERATION_YES_WITH_VALUE		 				= "yesWithValue";
	public static final String OPERATION_NO_WITH_VALUE 						= "noWithValue";
	public static final String OPERATION_YES_WITH_SHOW 						= "yesWithShow";
	public static final String OPERATION_NO_WITH_SHOW 						= "noWithHidden";

	/** CLI generate start ************************************************************************************/
	
	public static String generateCLI(String cmdStr, Object[] params, Object... defParams){
		if(cmdStr == null){
			return null;
		}
		
		//generate cli
		cmdStr = "[" + cmdStr + "]";
		BracketCLIGenObj bracketObj = CLIBracketUtil.getFirstBracket(BracketCLIGenObj.class, cmdStr);
		bracketObj.init();
		bracketObj.fillParams(CLIGenUtil.getCLIParamsQueue(params), false);
		if(defParams != null && defParams.length > 0){
			bracketObj.fillParams(CLIGenUtil.getCLIParamsQueue(defParams), true);
		}
		
		String cliRes = null;
		try{
			cliRes = bracketObj.getCLI();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(cliRes != null){
			cliRes = CLIGenUtil.mergeCLIBlanks(cliRes.trim());
		}
		
		return cliRes;
	}
	
	public static Queue<String> getCLIParamsQueue(Object[] paramArgs){
		if(paramArgs == null){
			return null;
		}
		
		Queue<String> paramQueue = new LinkedList<String>();
		for(Object obj : paramArgs){
			paramQueue.add(obj == null ? null : obj.toString());
		}
		
		return paramQueue;
	}
	
	public static String mergeCLIBlanks(String cli){
		if(cli == null || "".equals(cli)){
			return cli;
		}
		List<BracketObj> quotGroups = CLIBracketUtil.loadQuotGroups(cli, '"');
		if(quotGroups == null || quotGroups.isEmpty()){
			return cli.replaceAll(" {2,}", " ");
		}
		
		StringBuffer resBuff = new StringBuffer();
		int startIndex = 0;
		for(BracketObj item : quotGroups){
			resBuff.append(cli.substring(startIndex, item.getOpenIndex() + 1).replaceAll(" {2,}", " "));
			resBuff.append(item.getContent());
			startIndex = item.getCloseIndex();
		}
		resBuff.append(cli.substring(startIndex).replaceAll(" {2,}", " "));
		
		return resBuff.toString();
	}
	
	/** CLI generate end **************************************************************************************************/
	
	public static String getClassPath(Class<?> classObj){
		return classObj.getResource("").getPath();
	}
	
	public static String removeQuoteTwoSide(String string){
		if(string == null){
			return string;
		}
		if(string.startsWith("\"")){
			string = string.substring(1);
		}
		if(string.endsWith("\"")){
			string = string.substring(0, string.length()-1);
		}
		return string;
	}
	
	public static String getNoOperation(String yesOpt){
		if(yesOpt == null){
			return yesOpt;
		}else if(OPERATION_YES.equals(yesOpt)){
			return OPERATION_NO;
		}else if(OPERATION_YES_WITH_VALUE.equals(yesOpt)){
			return OPERATION_NO_WITH_VALUE;
		}else if(OPERATION_YES_WITH_SHOW.equals(yesOpt)){
			return OPERATION_NO_WITH_SHOW;
		}else{
			return yesOpt;
		}
	}
	
	public static boolean isExistsCLISpecialChar(String value){
		if(value == null){
			return false;
		}
		return value.contains(" ") || value.contains("?") || value.contains("\"") || value.contains("\\");
	}
	
	public static String getElementPath(Element ele) {
		Element element = ele;
		String elePath = "";
		while (true) {
			elePath = "/" + getCurrentNodePath(element) + elePath;
			if (element.getParent() == null) {
				break;
			}
			element = element.getParent();
		}
		return elePath;
	}
	
	//conver String to dom4j Element Object.
	public static Element converStringToXml(String content){
		try{
			if(StringUtils.isEmpty(content)){
				return null;
			}
			System.out.println("Content length: "+content.length());
			Document document = DocumentHelper.parseText(content);
			return document.getRootElement();
		}catch(Exception e){
			System.out.println(content);
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getCurrentNodePath(Element ele) {
		String nodePath = ele.getName();
		String attrName = ele.attributeValue(ATTR_NAME);
		if (attrName != null) {
			nodePath += "[@" + ATTR_NAME + "='"+attrName+"']";
		}
		return nodePath;
	}
	
	public static void generatePathElementMap(Element element, Map<String, Element> xmlMap){
		xmlMap.put(getElementPath(element), element);
		
		Iterator<?> chldElements = element.elementIterator();
		while(chldElements.hasNext()){
			generatePathElementMap((Element)chldElements.next(), xmlMap);
		}
	}
	
	public static String arrayToString(Object[] args){
		if(args == null){
			return null;
		}
		StringBuffer resBuffer = new StringBuffer("{");
		for(int i=0; i<args.length; i++){
			if(i > 0){
				resBuffer.append(", ");
			}
			resBuffer.append(String.valueOf(args[i]));
		}
		resBuffer.append("}");
		return resBuffer.toString();
	}
	
	public static String[] stringToArray(String argStr){
		if(StringUtils.isEmpty(argStr)){
			return null;
		}
		argStr = argStr.trim();
		if(argStr.startsWith("{") && argStr.endsWith("}")){
			argStr = argStr.substring(1, argStr.length()-1);
		}
		
		String[] resArgs = argStr.split(",");
		String strItem;
		for(int i=0; i<resArgs.length; i++){
			strItem = resArgs[i];
			strItem = strItem.trim();
			if(strItem.equalsIgnoreCase("null")){
				strItem = null;
			}else if((strItem.startsWith("\"") && strItem.endsWith("\"")) || 
					(strItem.startsWith("'") && strItem.endsWith("'"))){
				strItem = strItem.substring(1, strItem.length()-1);
			}
			resArgs[i] = strItem;
		}
		
		return resArgs;
	}
	
	//judge whether no cli
	public static boolean isNoCLI(String cli){
		return cli != null &&
				cli.trim().startsWith("no ");
	}
	
	public static String getNormalCLI(String cli){
		if(isNoCLI(cli)){
			return cli.trim().substring(3).trim();
		}else{
			return cli.trim();
		}
	}
	
	//password in show running config is encrypt, need decrypt and then encrypt.
	public static String decryptPassword(String decryptionPass){
		if(StringUtils.isEmpty(decryptionPass)){
			return decryptionPass;
		}
		
		String clearText = AhCliSec.ah_is_encrypted_pwd(decryptionPass) ? AhCliSec.ah_decrypt(decryptionPass) : decryptionPass;
		return AhCliSec.ah_encrypt(clearText);
	}
}
