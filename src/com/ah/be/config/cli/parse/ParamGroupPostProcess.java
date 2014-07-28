package com.ah.be.config.cli.parse;

import org.apache.commons.lang.StringUtils;

import edu.emory.mathcs.backport.java.util.Arrays;

public class ParamGroupPostProcess {

	public static String[][] processInterfacePort(String[][] paramGroup){
		if(paramGroup == null || paramGroup.length == 0){
			return paramGroup;
		}
		
		String portStr = paramGroup[1][1];
		if(StringUtils.isEmpty(portStr)){
			return paramGroup;
		}
		
		String[][] resGroup = (String[][])Arrays.copyOf(paramGroup, paramGroup.length + 1);
		String portType = portStr.substring(0, 3);
		String protIndex = portStr.substring(3);
		resGroup[1][1] = protIndex;
		resGroup[resGroup.length-1] = new String[]{"port", portType};
		
		return resGroup;
	}
}
