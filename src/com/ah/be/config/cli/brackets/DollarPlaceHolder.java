package com.ah.be.config.cli.brackets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DollarPlaceHolder implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final Pattern P_DOLLAR = Pattern.compile("\\$\\{.*\\}");
	private static final char SYMBOL = '$';
	private static final char START_CHAR = '{';
	private static final char END_CHAR = '}';

	private int dollarIndex;
	
	private int endIndex;
	
	private String originalContent;
	
	private String content;
	
	public static List<DollarPlaceHolder> getInstances(String paramStr){
		if(StringUtils.isEmpty(paramStr)){
			return null;
		}
		Matcher matcher = P_DOLLAR.matcher(paramStr);
		if(!matcher.find()){
			return null;
		}
		
		List<DollarPlaceHolder> resList = new ArrayList<DollarPlaceHolder>();
		char[] charArg = paramStr.toCharArray();
		int tagIndex = -1, openIndex = -1, closeIndex = -1;
		char c;
		for(int i=0; i<charArg.length; i++){
			c = charArg[i];
			if(c == SYMBOL){
				tagIndex = i;
			}else if(c == START_CHAR){
				openIndex = i;
			}else if(c == END_CHAR){
				closeIndex = i;
			}
			
			if(tagIndex + 1 == openIndex && closeIndex > openIndex){
				DollarPlaceHolder dObj = new DollarPlaceHolder();
				dObj.setDollarIndex(tagIndex);
				dObj.setEndIndex(closeIndex);
				dObj.setOriginalContent(new String(Arrays.copyOfRange(charArg, openIndex + 1, closeIndex)));
				resList.add(dObj);
				tagIndex = openIndex = closeIndex = -1;
			}
		}
		return resList;
	}
	
	public static String getContent(List<DollarPlaceHolder> allHolders, String originalContent){
		if(allHolders == null || allHolders.isEmpty() || StringUtils.isEmpty(originalContent)){
			return originalContent;
		}
		
		StringBuilder sb = new StringBuilder();
		int startIndex = 0, maxIndex = originalContent.length() - 1;
		for(DollarPlaceHolder holder : allHolders){
			if(startIndex >= maxIndex){
				break;
			}
			sb.append(originalContent.substring(startIndex, holder.getDollarIndex()));
			sb.append(holder.getContent());
			startIndex = holder.getEndIndex() + 1;
		}
		if(startIndex <= maxIndex){
			sb.append(originalContent.substring(startIndex));
		}
		
		return sb.toString();
	}

	public int getDollarIndex() {
		return dollarIndex;
	}

	public void setDollarIndex(int dollarIndex) {
		this.dollarIndex = dollarIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public String getOriginalContent() {
		return originalContent;
	}

	public void setOriginalContent(String originalContent) {
		this.originalContent = originalContent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString(){
		return ReflectionToStringBuilder.toString(this);
	}
}
