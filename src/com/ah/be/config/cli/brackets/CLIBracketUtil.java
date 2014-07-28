package com.ah.be.config.cli.brackets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;



public class CLIBracketUtil {
	
	public static <T extends BracketObj> T getFirstBracket(Class<T> bracketClass, String cmdStr) {
		List<T> resList = getBracketList(bracketClass, cmdStr, true);
		if(resList != null && !resList.isEmpty()){
			return resList.get(0);
		}else{
			return null;
		}
	}
	
	public static <T extends BracketObj> List<T> getBracketList(Class<T> bracketClass, String cmdStr, boolean firstOnly) {
		if(StringUtils.isEmpty(cmdStr)){
			return null;
		}
		
		List<T> resList = new ArrayList<>();
		char[] cmdArg = cmdStr.toCharArray();
		T resultItem = null;
		char cliChar;
		for(int i=0; i<cmdArg.length; i++){
			cliChar = cmdArg[i];
			
			//if open bracket create object;
			if(isOpenBracket(cliChar) && resultItem == null && 
					(i == 0 || !isPlaceHolderSign(cmdArg[i-1]) ) ){
				try{
					resultItem = bracketClass.newInstance();
				}catch(Exception e){
					e.printStackTrace();
					break;
				}
				resultItem.setBracketType(cliChar);
				resultItem.setFullText(cmdStr);
				resultItem.setOpenIndex(i);
			}
			
			//count bracket close index.
			if(resultItem != null){
				if(isOpenBracket(cliChar)){
					resultItem.countAdd(cliChar);
				}else if(isCloseBracket(cliChar)){
					resultItem.countSubtract(cliChar, i);
				}
				if(resultItem.isValide()){
					resList.add(resultItem);
					resultItem = null;
					if(firstOnly){
						return resList;
					}
				}
			}
		}
		return resList;
	}
	
	public static List<BracketObj> loadQuotGroups(String cliStr, char quotChar){
		if(cliStr == null){
			return null;
		}
		
		List<BracketObj> resGroups = new ArrayList<BracketObj>();
		char[] cliArg = cliStr.toCharArray();
		
		BracketObj itemObj = null;
		char cliChar;
		for(int i=0; i<cliArg.length; i++){
			cliChar = cliArg[i];
			if(cliChar != quotChar){
				continue;
			}
			if(itemObj == null){
				itemObj = new BracketObj(quotChar);
				itemObj.setFullText(cliStr);
				itemObj.setOpenIndex(i);
			}else{
				itemObj.setCloseIndex(i);
			}
			
			if(itemObj.isValide()){
				resGroups.add(itemObj);
				itemObj = null;
			}
		}
		return resGroups;
	}
	
	public static List<BracketObj> loadParamGroups(String cmdStr){
		List<BracketObj> resGroups = new ArrayList<BracketObj>();
		char[] cmdArg = cmdStr.toCharArray();
		
		BracketObj itemObj;
		char cliChar;
		for(int i=0; i<cmdArg.length; i++){
			cliChar = cmdArg[i];
			
			//if open bracket create object;
			if(isOpenBracket(cliChar)){
				itemObj = new BracketObj(cliChar);
				itemObj.setFullText(cmdStr);
				itemObj.setOpenIndex(i);
				resGroups.add(itemObj);
			}
			
			//count bracket close index.
			if(isOpenBracket(cliChar) || isCloseBracket(cliChar)){
				for(BracketObj item : resGroups){
					if(isOpenBracket(cliChar)){
						item.countAdd(cliChar);
					}else if(isCloseBracket(cliChar)){
						item.countSubtract(cliChar, i);
					}
				}
			}
		}
		
//		//set parent
//		for(BracketObj parentTemp : resGroups){
//			for(BracketObj item : resGroups){
//				item.setParent(parentTemp);
//			}
//		}
		
		return resGroups;
	}
	
	public static String[][] getCLIParamsArgs(String paramStr){
		if(StringUtils.isEmpty(paramStr)){
			return null;
		}
		
		String[] splitArg = paramStr.split("\\|");
		String[][] keyValueArg = new String[splitArg.length][2];
		int index;
		String keyValue;
		for (int i = 0; i < splitArg.length; i++) {
			keyValue = splitArg[i];
			index = keyValue.indexOf(":");
			if(index > 0){
				keyValueArg[i][0] = keyValue.substring(0, index);
				keyValueArg[i][1] = keyValue.substring(index + 1);
			}else{
				keyValueArg[i][0] = keyValue;
				keyValueArg[i][1] = keyValue;
			}
		}
		
		return keyValueArg;
	}
	
	private static boolean isOpenBracket(char c){
		return c=='(' || c=='[' || c=='{' || c=='<';
	}
	private static boolean isCloseBracket(char c){
		return c==')' || c==']' || c=='}' || c=='>';
	}
	
	private static boolean isPlaceHolderSign(char c){
		return c=='$' || c=='#';
	}
	
	public static int[] getMacOuiGroupIds(String cmd){
		if(StringUtils.isEmpty(cmd)){
			return null;
		}
		
		cmd = "[" + cmd + "]";
		BracketCLIGenObj firstbracketObj = CLIBracketUtil.getFirstBracket(BracketCLIGenObj.class, cmd);
		firstbracketObj.init();
		
		int index = -1;
		List<Integer> resIndexs = new ArrayList<>();
		getMacOuiGroupIds(firstbracketObj, index, resIndexs);
		
		int[] resArgs = new int[resIndexs.size()];
		for(int i=0; i<resIndexs.size(); i++){
			resArgs[i] = resIndexs.get(i);
		}
		return resArgs;
	}
	
	private static int getMacOuiGroupIds(BracketCLIGenObj bObj, int index, List<Integer> resList){
		if(bObj == null){
			return index;
		}
		
		index++;
		if(bObj.getBracketType() == BracketObj.ANGLE_BRACKET && 
				("mac_addr".equals(bObj.getContent()) || "oui".equals(bObj.getContent())) ){
			resList.add(index);
		}
		
		if(bObj.getChilds() != null){
			for(BracketCLIGenObj cldObj : bObj.getChilds()){
				index = getMacOuiGroupIds(cldObj, index, resList);
			}
		}
		
		return index;
	}
}
