package com.ah.be.config.cli.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.ah.be.config.cli.brackets.BracketObj;
import com.ah.be.config.cli.brackets.CLIBracketUtil;
import com.ah.be.config.cli.xsdbean.ConstraintType;

public class ConstraintCheckUtil {
	
	private static final String VERSION_REGEX = CmdRegexUtil.START + "(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)" + CmdRegexUtil.END;
	private static final Pattern PT_VERSION = Pattern.compile(VERSION_REGEX);
	private static final Pattern PT_TRUE = Pattern.compile(CmdRegexUtil.START + "(?:true|!\\s*false)" + CmdRegexUtil.END, Pattern.CASE_INSENSITIVE);
	private static final Pattern PT_FALSE = Pattern.compile(CmdRegexUtil.START + "(?:false|!\\s*true)" + CmdRegexUtil.END, Pattern.CASE_INSENSITIVE);
	
	private static final String EXP_REGEX = "(!=|==|<=|>=|>|<|=)";
//	private static final String EXP_REGEX = CmdRegexUtil.START + "(\\S*)\\s*(!=|==|<=|>=|>|<|=)?\\s*(\\S+)" + CmdRegexUtil.END;
//	private static final Pattern PT_EXP = Pattern.compile(EXP_REGEX);
	
	private static final Pattern PT_NUM = Pattern.compile("\\d+");
	
	private static final String PLATFORM_HOLDER = ":p";
	private static final String TYPE_HOLDER = ":t";
	private static final String VERSION_HOLDER = ":v";
	
	public static boolean isMatch(ConstraintType paramObj, ConstraintType expressionObj){
		if(expressionObj == null){
			return true;
		}
		
		boolean blnPlatform, blnType, blnVersion;
		String platformStr, typeStr, versionStr;
		
		if(paramObj == null){
			platformStr = typeStr = versionStr = "";
		}else{
			platformStr = paramObj.getPlatform();
			typeStr = paramObj.getType();
			versionStr = paramObj.getVersion();
		}
		
		if(!StringUtils.isEmpty(expressionObj.getExpression())){
			String expressionStr = expressionObj.getExpression();
			expressionStr = expressionStr.replace(PLATFORM_HOLDER, platformStr);
			expressionStr = expressionStr.replace(TYPE_HOLDER, typeStr);
			expressionStr = expressionStr.replace(VERSION_HOLDER, versionStr);
			return checkConstraint(expressionStr);
		}else{
			blnPlatform = checkConstraint(expressionObj.getPlatform(), platformStr);
			blnType = checkConstraint(expressionObj.getType(), typeStr);
			blnVersion = checkConstraint(expressionObj.getVersion(), versionStr);
			return blnPlatform && blnType && blnVersion;
		}
	}
	
	public static String getExpression(ConstraintType expressionObj){
		if(expressionObj == null){
			return null;
		}
		
		List<String> contentList = new ArrayList<String>();
		//expression
		if(!StringUtils.isEmpty(expressionObj.getExpression())){
			contentList.add(expressionObj.getExpression());
		}
		//platform
		if(!StringUtils.isEmpty(expressionObj.getPlatform())){
			contentList.add(getExpression(expressionObj.getPlatform(), PLATFORM_HOLDER));
		}
		//device type
		if(!StringUtils.isEmpty(expressionObj.getType())){
			contentList.add(getExpression(expressionObj.getType(), TYPE_HOLDER));
		}
		//version
		if(!StringUtils.isEmpty(expressionObj.getVersion())){
			contentList.add(getExpression(expressionObj.getVersion(), VERSION_HOLDER));
		}

		if(contentList.isEmpty()){
			return null;
		}else if(contentList.size() == 1){
			return contentList.get(0);
		}else {
			StringBuilder sbRes = new StringBuilder();
			String expressionStr = null;
			for(int i=0; i<contentList.size(); i++){
				if(i > 0){
					sbRes.append(" && ");
				}
				expressionStr = "("+contentList.get(i)+")";
				sbRes.append(expressionStr);
			}
			return sbRes.toString();
		}
	}
	
	public static boolean checkConstraint(String expression, String... paramStr){
		if(StringUtils.isEmpty(expression)){
			return true;
		}
		
		BracketObj bracketObj = CLIBracketUtil.getFirstBracket(BracketObj.class, expression);
		String bracketContent;
		boolean match;
		while(bracketObj != null){
			bracketContent = bracketObj.getContent();
			match = checkConstraint(bracketContent, paramStr);
			expression = expression.substring(0, bracketObj.getOpenIndex()) 
							+ match 
							+ expression.substring(bracketObj.getCloseIndex() + 1);
			
			bracketObj = CLIBracketUtil.getFirstBracket(BracketObj.class, expression);
		}
		
		String[] versionArg;
		if(expression.contains("&&")){
			versionArg = expression.split("&&");
			for(String exp : versionArg){
				if(!checkConstraint(exp, paramStr)){
					return false;
				}
			}
			return true;
		}else if(expression.contains("||")){
			versionArg = expression.split("\\|\\|");
			for(String exp : versionArg){
				if(checkConstraint(exp, paramStr)){
					return true;
				}
			}
			return false;
		}else{
			return constraintMatch(expression, paramStr);
		}
	}

	private static boolean constraintMatch(String expression, String... paramStr){
		expression = expression.trim();
		if(PT_TRUE.matcher(expression).matches()){
			return true;
		}else if(PT_FALSE.matcher(expression).matches()){
			return false;
		}
		
		if(expression.startsWith("!")){
			expression = "!(" + expression.substring(1) + ")";
			return checkConstraint(expression, paramStr);
		}
		
		String sourceStr = null;
		String expressionStr = null;
		String signStr = null;
		
		String[] expArgs = expression.split(EXP_REGEX);
		if(expArgs != null && expArgs.length == 2){
			sourceStr = expArgs[0];
			expressionStr = expArgs[1];
			signStr = expression.substring(sourceStr.length(), expression.length() - expressionStr.length());
		}
		sourceStr = paramStr.length > 0 ? paramStr[0] : sourceStr;
		signStr = StringUtils.isEmpty(signStr) ? "=" : signStr;
		if(StringUtils.isEmpty(sourceStr) || StringUtils.isEmpty(expressionStr) || StringUtils.isEmpty(signStr)){
			return false;
		}
		sourceStr = sourceStr.trim();
		signStr = signStr.trim();
		expressionStr = expressionStr.trim();
		
		int compareRes = compareExpression(sourceStr, expressionStr);
		if(signStr == null || signStr.equals("=") || signStr.equals("==")){
			return compareRes == 0;
		}else if(signStr.equals("!=")){
			return compareRes != 0;
		}else if(signStr.equals(">")){
			return compareRes > 0;
		}else if(signStr.equals("<")){
			return compareRes < 0;
		}else if(signStr.equals(">=")){
			return compareRes >= 0;
		}else if(signStr.equals("<=")){
			return compareRes <= 0;
		}else {
			return false;
		}
	}
	
	private static int compareExpression(String sourceStr, String expressStr){
		//number compare
		if(isNum(sourceStr) && isNum(expressStr)){
			return Integer.parseInt(sourceStr) - Integer.parseInt(expressStr);
		}
		
		//version compare
		int[] sourceArg = getVersionArgs(sourceStr);
		int[] expressArg = getVersionArgs(expressStr);
		if(sourceArg != null && expressArg != null){
			for(int i=0; i<sourceArg.length; i++){
				if(sourceArg[i] != expressArg[i]){
					return sourceArg[i] - expressArg[i];
				}
			}
			return 0;
		}
		
		//expression compare
		Pattern patternOrg = Pattern.compile(expressStr);
		Matcher matcher = patternOrg.matcher(sourceStr);
		if(matcher.matches()){
			return 0;
		}else{
			return sourceStr.compareTo(expressStr);
		}
	}
	
	private static boolean isNum(String paramStr){
		return PT_NUM.matcher(paramStr).matches();
	}
	
	private static int[] getVersionArgs(String paramStr){
		Matcher matcher = PT_VERSION.matcher(paramStr);
		if(matcher.matches()){
			return new int[]{Integer.parseInt(matcher.group(1)), 
					Integer.parseInt(matcher.group(2)),
					Integer.parseInt(matcher.group(3)),
					Integer.parseInt(matcher.group(4))
			};
		}
		return null;
	}
	
	private static String getExpression(String expressionStr, String placeHolder){
		BracketObj bracketObj = CLIBracketUtil.getFirstBracket(BracketObj.class, expressionStr);
		StringBuilder sbExp = new StringBuilder();
		while(bracketObj != null){
			sbExp.append(expressionStr.substring(0, bracketObj.getOpenIndex() + 1) );
			sbExp.append(getExpression(bracketObj.getContent(), placeHolder));
			sbExp.append(expressionStr.substring(bracketObj.getCloseIndex(), bracketObj.getCloseIndex() + 1) );
			
			if(bracketObj.getCloseIndex() + 1 <= expressionStr.length()){
				expressionStr = expressionStr.substring(bracketObj.getCloseIndex() + 1);
				bracketObj = CLIBracketUtil.getFirstBracket(BracketObj.class, expressionStr);
			}
		}
		if(expressionStr.length() > 0){
			sbExp.append(expressionStr);
		}
		expressionStr = sbExp.toString();
		
		String[] versionArg;
		if(expressionStr.contains("&&")){
			versionArg = expressionStr.split("&&");
			return expressionArrayToString(versionArg, placeHolder, "&&");
		}else if(expressionStr.contains("||")){
			versionArg = expressionStr.split("\\|\\|");
			return expressionArrayToString(versionArg, placeHolder, "||");
		}
		
		String[] expArgs = expressionStr.split(EXP_REGEX);
		if(expArgs.length < 2){
			return placeHolder + " = " + expressionStr;
		}else if(StringUtils.isEmpty(expArgs[0].trim())){
			return placeHolder + expressionStr;
		}else{
			return expressionStr;
		}
	}
	
	private static String expressionArrayToString(String[] arg, String placeHolder, String signStr){
		if(arg == null || arg.length == 0){
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		for(String exp : arg){
			exp = getExpression(exp, placeHolder);
			if(sb.length() == 0){
				sb.append(exp);
				continue;
			}
			
			if(!" ".equals(sb.substring(sb.length()-1))){
				sb.append(" ");
			}
			sb.append(signStr);
			if(!exp.startsWith(" ")){
				sb.append(" ");
			}
			sb.append(exp);
		}
		return sb.toString();
	}
	
	public static void main(String[] args){
//		ConstraintType paramObj = new ConstraintType();
//		paramObj.setPlatform("14");
//		paramObj.setType("1");
//		paramObj.setVersion("6.1.6.0");
		
		ConstraintType expressionObj = new ConstraintType();
		expressionObj.setExpression("(:p  && :v >= 6.1.2.0) || (:p = 10|11 && :v >= 6.1.6.0)");
		expressionObj.setPlatform("5|6|7");
		expressionObj.setVersion(">=6.1.1.3");
		
//		System.out.println(isMatch(paramObj, expressionObj));
		
		String resStr = getExpression(expressionObj);
		System.out.println(resStr);
		
//		System.out.println(">12121".split(EXP_REGEX)[0]);
	}
}
