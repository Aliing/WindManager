package com.ah.be.config.cli.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


public class CmdRegexUtil {
	
	public static final String START = "^\\s*";
	public static final String END = "\\s*$";
	public static final String CLI_SPLIT = " +";
	
	public static final String number = "-?\\d+";
	public static final String string = "\\S+|\".+\"";
	public static final String ethx = "eth[0-4]";
	public static final String aggx = "agg" + number;
	public static final String redx = "red0";
	public static final String wifix = "wifi[0-1]";
	public static final String mgtx = "mgt0";
	public static final String mgtx_y = "mgt0\\.1?\\d(?<!1[7-9]|0\\.0)";
	public static final String wifix_y = "wifi[0-1]\\.1?\\d(?<!1[7-9]|\\.0)";
	public static final String ethx_y = "eth1/[1-5]?\\d(?<!5[3-9]|1/0)";
	public static final String usbnetx = "usbnet0";
	public static final String tunnelx = "tunnel" + number;
	public static final String vlanx = "vlan" + number;
	public static final String oui = "[0-9a-fA-F]{6}" + "|" + 
									"[0-9a-fA-F]{3}(?:-|:|\\.)[0-9a-fA-F]{3}" + "|" + 
									"[0-9a-fA-F]{2}(?:-|:|\\.)[0-9a-fA-F]{2}(?:-|:|\\.)[0-9a-fA-F]{2}";
	public static final String mac_addr = "[0-9a-fA-F]{12}" + "|" + 
										"[0-9a-fA-F]{6}(?:-|:|\\.)[0-9a-fA-F]{6}" + "|" +
										"[0-9a-fA-F]{4}(?:-|:|\\.)[0-9a-fA-F]{4}(?:-|:|\\.)[0-9a-fA-F]{4}" + "|" +
										"[0-9a-fA-F]{2}(?:-|:|\\.)[0-9a-fA-F]{2}(?:-|:|\\.)[0-9a-fA-F]{2}(?:-|:|\\.)[0-9a-fA-F]{2}(?:-|:|\\.)[0-9a-fA-F]{2}(?:-|:|\\.)[0-9a-fA-F]{2}";
//	public static final String ip_addr = "(?:[1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(?:\\.(?:\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
	public static final String ip_addr = "[1-2]?\\d?(?<!2[6-9])\\d(?<!25[6-9])(?:\\.[1-2]?\\d?(?<!2[6-9])\\d(?<!25[6-9])){3}";
	public static final String string_64 = "\\S{1,64}|\".{1,64}\"";
//	public static final String mask = "(?:254|252|248|240|224|192|128|0)\\.0\\.0\\.0" + "|" +
//									"(?:255\\.(?:254|252|248|240|224|192|128|0)\\.0\\.0)" + "|" +
//									"(?:255\\.255\\.(?:254|252|248|240|224|192|128|0)\\.0)" + "|" +
//									"(?:255\\.255\\.255\\.(?:254|252|248|240|224|192|128|0))";
	public static final String mask = ip_addr;
	public static final String mask_num = "[1-3]?\\d(?<!3[3-9])";
	public static final String ip_addr_mask = "(?:"+ip_addr+")" +"/" + mask_num;
	public static final String date = "(?:\\d{4}-)?\\d{2}-\\d{2}";								//not sure
	public static final String time = "(?:[0-1][0-9]|[2][0-3]):[0-5][0-9](?::[0-5][0-9])?";
	public static final String url = "(?:(?:https|http|ftp|rtsp|mms)?://)?" 
			+ "(?:(?:[0-9a-z_!~*\'().&=+$%-]+: )?[0-9a-z_!~*\'().&=+$%-]+@)?" //fpt user@
			+ "(?:(?:[0-9]{1,3}.){3}[0-9]{1,3}" // URL- 199.194.52.184 
			+ "|" // allow IP address and domain name 
			+ "(?:[0-9a-z_!~*\'()-]+.)*" // domain www.
			+ "(?:[0-9a-z][0-9a-z-]{0,61})?[0-9a-z]." // second domain name
			+ "[a-z]{2,6})" // first level domain- .com or .museum 
			+ "(?::[0-9]{1,4})?" // port : 80
			+ "(?:(?:/?)|" // a slash isn't required if there is no file name 
			+ "(?:/[0-9a-z_!~*\'().;?:@&=+$,%#-]+)+/?)";
	public static final String port = "[0-9]{1,4}";
	public static final String hex = "[0-9a-fA-F]+";
	public static final String channel_g3 = "\\d{2}-\\d{2}-\\d{2}";	
	public static final String channel_g4 = "\\d{2}-\\d{2}-\\d{2}-\\d{2}";
	public static final String location = "\\S+";											//not sure
	
	public static final Map<String, String> regexMap = new HashMap<String, String>();
	public static final Map<String, Pattern> regexPatternMap = new HashMap<String, Pattern>();
	static{
		regexMap.put("number", number);
		regexMap.put("string", string);
		regexMap.put("oui", oui);
		regexMap.put("mac_addr", mac_addr);
		regexMap.put("ip_addr", ip_addr);
		regexMap.put("mask", mask);
		regexMap.put("ethx", ethx);
		regexMap.put("date/time", "(?:"+date+")" +"/" + "(?:"+time+")");
		regexMap.put("time", time);
		regexMap.put("date", date);
		regexMap.put("url", url);
		regexMap.put("wifix", wifix);
		regexMap.put("mgtx", mgtx);
		regexMap.put("mgtx.y", mgtx_y);
		regexMap.put("wifix.y", wifix_y);
		regexMap.put("ip_addr/netmask", ip_addr_mask);
		regexMap.put("ip_addr/mask", ip_addr_mask);
		regexMap.put("hex", hex);
		regexMap.put("redx", redx);
		regexMap.put("aggx", aggx);
		regexMap.put("channel_g3", channel_g3);
		regexMap.put("channel_g4", channel_g4);
		regexMap.put("netmask", mask);
		regexMap.put("port", port);
		regexMap.put("location", location);
		regexMap.put("ethx/y", ethx_y);
		regexMap.put("vlanx", vlanx);
		regexMap.put("usbnetx", usbnetx);
		regexMap.put("tunnelx", tunnelx);
		regexMap.put("string_64", string_64);
		
		for(Entry<String, String> entryObj : regexMap.entrySet()){
			regexPatternMap.put(entryObj.getKey(), Pattern.compile(entryObj.getValue()));
		}
	}
	
	public static final String TYPE_STRING 		= "string";
	private static Pattern groupKeyPattern;
	
	public static String generate(String cmd){
		String resultCmd = cmd;
		
		resultCmd = resultCmd.trim();
		
		resultCmd = resultCmd.replaceAll(" {2,}", " ");
		
		resultCmd = resultCmd.replace(" [", "[");
		
		resultCmd = resultCmd.replace(" ]", "]");
		
		resultCmd = resultCmd.replace("[", "(");
		
		resultCmd = resultCmd.replace("]", ")?");
		
		resultCmd = resultCmd.replace("{", "(");
		
		resultCmd = resultCmd.replace("}", ")");
		
		resultCmd = resultCmd.replace(" ", CLI_SPLIT);
		
		resultCmd = cmdTypeReplace(resultCmd);
		
		resultCmd = START + resultCmd + END;
		
		resultCmd = resultCmd.replace(START+"(?i)(no)? +", START+"(?i)(no +)? *");
		
		return resultCmd;
	}
	
	//replace <> content in CMD definition
	private static String cmdTypeReplace(String cmdStr){
		int startIndex = -1, endIndex = -1;
		List<String> strList = new ArrayList<String>();
		startIndex = cmdStr.indexOf("<");
		endIndex = cmdStr.indexOf(">");
		while(startIndex >= 0){
			strList.add(cmdStr.substring(0, startIndex));
			if(endIndex < cmdStr.length()){
				strList.add(cmdStr.substring(startIndex, endIndex+1));
			}else{
				strList.add(cmdStr.substring(startIndex));
				break;
			}
			
			cmdStr = cmdStr.substring(endIndex+1);
			startIndex = cmdStr.indexOf("<");
			endIndex = cmdStr.indexOf(">");
		}
		strList.add(cmdStr);
		
		String strItem;
		StringBuffer resBuf = new StringBuffer();
		for(int i=0; i<strList.size(); i++){
			strItem = strList.get(i);
			if(strItem.startsWith("<") && strItem.endsWith(">")){
				strItem = strItem.substring(1, strItem.length()-1);
				strItem = getRegexByType(strItem);
				strItem = "(" + strItem + ")";
			}else{
				//Ignore case 
				strItem = "(?i)"+strItem+"(?-i)";
			}
			resBuf.append(strItem);
		}
		
		return resBuf.toString();
	}
	
	public static String getRegexByType(String typeStr){
		if(typeStr == null || "".equals(typeStr)){
			return typeStr;
		}
		if(regexMap.containsKey(typeStr)){
			return regexMap.get(typeStr);
		}
		if(typeStr.contains("|")){
			String[] strArgs = typeStr.split("\\|");
			StringBuffer strBuf = new StringBuffer();
			String regexStr = null;
			for(int i=0; i<strArgs.length; i++){
				if(regexMap.containsKey(strArgs[i])){
					regexStr = regexMap.get(strArgs[i]);
				}else{
					regexStr = strArgs[i];
				}
				if(strBuf.length() > 0){
					strBuf.append("|");
				}
				strBuf.append("(?:"+regexStr+")");
			}
			return strBuf.toString();
		}
		return typeStr;
	}
	
	public static String getCmdGroupKey(String cmdStr, boolean... withOutNo){
		if(StringUtils.isEmpty(cmdStr)){
			return cmdStr;
		}
		
		cmdStr = cmdStr.trim();
		if(withOutNo.length > 0 && withOutNo[0] && cmdStr.startsWith("no ")){
			cmdStr = cmdStr.substring(3).trim();
		}
		
		
		int index = cmdStr.indexOf(" ");
		int endIndex = index > 0 ? index : cmdStr.length();
		return cmdStr.substring(0, endIndex);
	}
	
	public static String getCmdGroupKeyWithoutNo(String cmdStr){
		if(StringUtils.isEmpty(cmdStr)){
			return cmdStr;
		}
		
		if(groupKeyPattern == null){
			String regex = "^\\s*(no|\\[\\s*no\\s*\\])?\\s*(\\S+).*$";
			groupKeyPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		}
		
		Matcher match = groupKeyPattern.matcher(cmdStr);
		if(match.matches()){
			return match.group(2);
		}else{
			return "";
		}
	}
	
	public static String translateRegex(String string){
		return string
				.replace("\\", "\\\\")
				.replace(".", "\\.")
				.replace("$", "\\$")
				.replace("^", "\\^")
				.replace("{", "\\{")
				.replace("}", "\\}")
				.replace("[", "\\[")
				.replace("]", "\\]")
				.replace("(", "\\(")
				.replace(")", "\\)")
				.replace("|", "\\|")
				.replace("*", "\\*")
				.replace("+", "\\+")
				.replace("?", "\\?");
	}
	
	public static boolean cliTypeCheck(String type, String value){
		Matcher matcher = null;
		Pattern pattern = regexPatternMap.get(type);
		if(pattern != null){
			matcher = pattern.matcher(value);
		}
		
		if(matcher != null){
			return matcher.matches();
		}else{
			return false;
		}
	}
	
	public static String formatMacOui(String macOui){
		if(StringUtils.isEmpty(macOui)){
			return macOui;
		}
		
		macOui = macOui.replaceAll("[^0-9a-fA-F]", "");
		char[] charArgs = macOui.toCharArray();
		
		StringBuffer resBuff = new StringBuffer();
		for(int i=0; i<charArgs.length; i++){
			if(i>0 && i%2 == 0){
				resBuff.append("-");
			}
			resBuff.append(charArgs[i]);
		}
		return resBuff.toString().toLowerCase();
	}
	
	public static void main(String[] args){
//		String cliStr = "no  user-profile   zhang attribute 10";
		System.out.println(formatMacOui("1122338899ffDD"));
	}

}
