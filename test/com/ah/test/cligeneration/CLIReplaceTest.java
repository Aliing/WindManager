package com.ah.test.cligeneration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLIReplaceTest {

	public void test() {
		String orginalCLI = "_802.1x-mac-table suppress-interval 123  \n" +
							"interface vlan-id 200 aaa bbb  \n" +
							"_ip-nat-policy nat-policy aaa 111 222";
		String regex = "_802.1x-mac-table (expire-time|suppress-interval)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(orginalCLI);
		String newClis = matcher.replaceAll("802.1x-mac-table $1");
		
		regex = "interface vlan-id (\\d)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(orginalCLI);
		newClis = matcher.replaceAll("interface vlan $1");
		
		regex = "_ip-nat-policy nat-policy";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(orginalCLI);
		newClis = matcher.replaceAll("ip nat-policy");
		
		System.out.println(newClis);
	}

	
	public static void main(String[] args) {
		CLIReplaceTest ob = new CLIReplaceTest();
		ob.test();

	}

}
